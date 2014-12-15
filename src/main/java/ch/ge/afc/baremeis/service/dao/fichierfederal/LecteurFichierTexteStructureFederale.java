/**
 * 
 */
package ch.ge.afc.baremeis.service.dao.fichierfederal;

import java.io.*;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.dao.TypeMismatchDataAccessException;


import ch.ge.afc.baremeis.service.Sexe;


/**
 * Attention, non thread safe !!
 * @author <a href="mailto:patrick.giroud@etat.ge.ch">Patrick Giroud</a>
 *
 */
public class LecteurFichierTexteStructureFederale {

	/**************************************************/
    /****************** Attributs *********************/
    /**************************************************/

	final Logger logger = LoggerFactory.getLogger(LecteurFichierTexteStructureFederale.class);
	private Resource fichier;
	private String charsetName;
	private DateFormat dateFmt;
	
    /**************************************************/
    /**************** Constructeurs *******************/
    /**************************************************/

	public LecteurFichierTexteStructureFederale() {
		super();
		dateFmt = new SimpleDateFormat("yyyyMMdd");
		dateFmt.setTimeZone(TimeZone.getTimeZone("Europe/Zurich"));
	}
	
	/**************************************************/
    /******* Accesseurs / Mutateurs *******************/
    /**************************************************/
	
	public void setFichier(Resource fichier) {
		this.fichier = fichier;
	}
	
	public void setCharsetName(String charsetName) {
		this.charsetName = charsetName;
	}
	
    /**
	 * @return the dateFmt
	 */
	protected DateFormat getDateFmt() {
		return dateFmt;
	}

	/**************************************************/
    /******************* Méthodes *********************/
    /**************************************************/

	private String sousChaine(String chaine, int posDebut, int posFin) {
		return chaine.substring(posDebut - 1, posFin);
	}
	
	private BigDecimal montantAvecCentime(String chaine, int posDebut, int posFin) {
		String montant = sousChaine(chaine,posDebut,posFin);
		if (montant.length() < 3) {
			logger.error("Erreur lors du parsing d'un montant avec centimes '" + montant + "'");
			throw new IllegalArgumentException("Impossible de traiter un montant si petit '" + montant + "' !!");
		}
		return new BigDecimal(montant.substring(0, montant.length() -2) + "." + montant.substring(montant.length() -2));
	}
	
	private Date date(String chaine, int posDebut, int posFin) throws ParseException {
		String date = sousChaine(chaine,posDebut,posFin);
		return dateFmt.parse(date);
	}
	
	private int entier(String chaine, int posDebut, int posFin) {
		String entier = sousChaine(chaine,posDebut,posFin).trim();
		return Integer.valueOf(entier);
	}
	
	private void traiterLigneEnregInitial(EnregistrementCallback callback, String ligne, int numeroLigne) throws ParseException {
		String codeCanton = sousChaine(ligne,3,4);
		Date dateCreation = date(ligne,20,27);
		callback.lectureEnregistrementInitial(codeCanton, dateCreation);
	}
	
	private void traiterLigneEnregFinal(EnregistrementCallback callback, String ligne, int numeroLigne) {
		String codeCanton = sousChaine(ligne,18,19);
		int nbreEnregistrement = entier(ligne,20,27);
		callback.lectureEnregistrementFinal(codeCanton, nbreEnregistrement);
	}
	
	private void traiterCodeTarifaire(String codeTarifaire, EnregistrementBareme enreg) {
		enreg.setCodeTarifaire(new CodeTarifaire(codeTarifaire));
	}
	
	private BigDecimal tauxEnPourcent(String taux) {
		return new BigDecimal(taux.substring(0, 1) + "." + taux.substring(1,5));
	}


	/**
	 * barèmes progressifs de l’impôt à la source
	 * pour le revenu d’une activité lucrative salariée
	 * @param callback
	 * @param ligne
	 * @param numeroLigne
	 * @throws ParseException
	 */
	private void traiterLigneSalarie(EnregistrementCallback callback, String ligne, int numeroLigne) throws ParseException {
		EnregistrementBareme enreg = new EnregistrementBareme();
		enreg.setGenre(GenreTransaction.getParCode(sousChaine(ligne,3,4)));
		enreg.setCodeCanton(sousChaine(ligne,5,6));
		String codeTarifaire = sousChaine(ligne,7,16);
		traiterCodeTarifaire(codeTarifaire,enreg);
		enreg.setDateInitialeValidite(date(ligne,17,24));
		enreg.setRevenuImposable(montantAvecCentime(ligne,25,33));
		enreg.setEchelonTarifaire(montantAvecCentime(ligne,34,42));
		enreg.setSexe(Sexe.getParCode(ligne.charAt(44)));
		enreg.setNbreEnfant(entier(ligne,44,45));
		enreg.setMontantImpot(montantAvecCentime(ligne,46,54));
		enreg.setTaux(tauxEnPourcent(sousChaine(ligne,55,59)));
		callback.lectureEnregistrement(enreg);
	}

	/**
	 * barèmes pour administrateurs d’après
	 * l’art. 93 LIFD et pour participations de collaborateur d’après l’art. 97a LIFD
	 * @param callback
	 * @param ligne
	 * @param numeroLigne
	 * @throws ParseException
	 */
	private void traiterLigneAdministrateur(EnregistrementCallback callback, String ligne, int numeroLigne) throws ParseException {
	}

	/**
	 *
	 * @param callback
	 * @param ligne
	 * @param numeroLigne
	 * @throws ParseException
	 */
	private void traiterLigneCommissionPerception(EnregistrementCallback callback, String ligne, int numeroLigne) throws ParseException {
	}

	private void traiterLigne(EnregistrementCallback callback, String ligne, int numeroLigne) throws ParseException {
		// Lecture du genre d'enregistrement
		String genreTransaction = ligne.substring(0, 2);
		if ("06".equals(genreTransaction)) {
			traiterLigneSalarie(callback, ligne, numeroLigne);
		} else if ("11".equals(genreTransaction)) {
			logger.debug("Le fichier " + fichier.getFilename() + " comprend une ligne administrateur ! Ce genre de ligne n'est pas prévu.");
		 	traiterLigneAdministrateur(callback, ligne, numeroLigne);
		} else if ("12".equals(genreTransaction)) {
			logger.debug("Le fichier " + fichier.getFilename() + " comprend une ligne commission de perception ! Ce genre de ligne n'est pas prévu.");
			traiterLigneCommissionPerception(callback, ligne, numeroLigne);
		} else if ("00".equals(genreTransaction)) {
			traiterLigneEnregInitial(callback,ligne,numeroLigne);
		} else if ("99".equals(genreTransaction)) {
			traiterLigneEnregFinal(callback,ligne,numeroLigne);
		}
	}
	
	private InputStream getInputStream() throws IOException {
		InputStream stream = null;
		if (fichier.getFilename().endsWith("zip")) {
			ZipFile fichierArchive = new ZipFile(fichier.getFile());
			String nomFichier = fichier.getFilename().replace("zip", "txt");
			for (Enumeration<? extends ZipEntry> entrees = fichierArchive.entries(); entrees.hasMoreElements();) {
				ZipEntry entree = entrees.nextElement();
				if (nomFichier.equals(entree.getName())) {
					stream = fichierArchive.getInputStream(entree);
				}
			}
			if (null == stream) throw new TypeMismatchDataAccessException("Il n'existe pas de fichier " + nomFichier + " dans l'archive " + fichier.getFilename());
		} else if (fichier.getFilename().endsWith("xz") || fichier.getFilename().endsWith("XZ")) {
            BufferedInputStream in = new BufferedInputStream(fichier.getInputStream());
            stream = new XZCompressorInputStream(in);
        } else {
			stream = fichier.getInputStream();
		}
		return stream;
	}
	
	
	public void lire(EnregistrementCallback callback) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(getInputStream(),charsetName));
		int numLigne = 1;
		String ligne = reader.readLine(); 
		while (null != ligne) {
			try {
				traiterLigne(callback, ligne,numLigne);
			} catch (ParseException pe) {
				throw new TypeMismatchDataAccessException("Erreur de lecture dans la ressource " + fichier.getFilename() + " à la ligne " + numLigne,pe);
			} catch (NumberFormatException nfe) {
				throw new TypeMismatchDataAccessException("Erreur de lecture dans la ressource " + fichier.getFilename() + " à la ligne " + numLigne,nfe);
			}
			ligne = reader.readLine();
			numLigne++;
		}
		reader.close();
	}
}
