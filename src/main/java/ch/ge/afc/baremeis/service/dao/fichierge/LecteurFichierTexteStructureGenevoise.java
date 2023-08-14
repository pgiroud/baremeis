/**
 * 
 */
package ch.ge.afc.baremeis.service.dao.fichierge;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




/**
 * @author <a href="mailto:patrick.giroud@etat.ge.ch">Patrick Giroud</a>
 *
 */
class LecteurFichierTexteStructureGenevoise {

	public static Optional<LecteurFichierTexteStructureGenevoise> unLecteurDepuisClasspath(String fichierAvecCheminComplet, String charsetName) {
		LecteurFichierTexteStructureGenevoise lecteur = new LecteurFichierTexteStructureGenevoise(fichierAvecCheminComplet,charsetName);
		return lecteur.exist() ? Optional.of(lecteur) : Optional.empty();
	}

	/**************************************************/
    /****************** Attributs *********************/
    /**************************************************/

	final Logger logger = LoggerFactory.getLogger(LecteurFichierTexteStructureGenevoise.class);
	private final String fichierDansClasspathAvecCheminComplet;
	private final String charsetName;
	
    /**************************************************/
    /**************** Constructeurs *******************/
    /**************************************************/

	private LecteurFichierTexteStructureGenevoise(String fichierAvecCheminComplet, String charset) {
		super();
		this.fichierDansClasspathAvecCheminComplet = fichierAvecCheminComplet;
		this.charsetName = charset;	}
	

	
	/**************************************************/
    /******************* Méthodes *********************/
    /**************************************************/

	private ClassLoader getClassLoader() {
		ClassLoader cl = null;
		try {
			cl = Thread.currentThread().getContextClassLoader();
		}
		catch (Throwable ex) {}
		if (cl == null) {
			cl = LecteurFichierTexteStructureGenevoise.class.getClassLoader();
			if (cl == null) {
				try {
					cl = ClassLoader.getSystemClassLoader();
				}
				catch (Throwable ex) {
				}
			}
		}
		return cl;
	}

	boolean exist() {
		try {
			ClassLoader cl = getClassLoader();
			InputStream is = (cl != null ? cl.getResourceAsStream(fichierDansClasspathAvecCheminComplet) : ClassLoader.getSystemResourceAsStream(fichierDansClasspathAvecCheminComplet));
			return null != is && new BufferedReader(new InputStreamReader(is,charsetName)).ready();
		} catch (IOException ioe) {
			logger.debug("Pas de lecture possible dans 'classpath:" + fichierDansClasspathAvecCheminComplet + "'",ioe);
		}
		return false;
	}
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
	
	private BigDecimal taux(String taux) {
		return new BigDecimal("0." + taux);
	}
	
	private void traiterLigne(EnregistrementGECallback callback, String ligne, int numeroLigne) throws ParseException {
		LigneEnregistrement enreg = new LigneEnregistrement();
		enreg.setMntMinAnnuel(new BigDecimal(sousChaine(ligne,3,9)));
		enreg.setMntMaxAnnuel(new BigDecimal(sousChaine(ligne,10,16)));
		enreg.setMntMinMensu(montantAvecCentime(ligne,17,23));
		enreg.setMntMaxMensu(new BigDecimal(sousChaine(ligne,24,30)));
		enreg.setMntMinHoraire(montantAvecCentime(ligne,31,37));
		enreg.setMntMaxHoraire(montantAvecCentime(ligne,38,44));
		int offset = 45;
		BigDecimal[] taux = new BigDecimal[(ligne.length()-offset) / 4];
		int curseur = offset;
		for (int pos = 0; pos < taux.length; pos++) {
			taux[pos] = taux(sousChaine(ligne,curseur,curseur+3));
			curseur += 4;
		}
		enreg.setTaux(taux);
		callback.traiterLigne(enreg);
	}	
	
	
	public void lire(EnregistrementGECallback callback) throws IOException {
		ClassLoader cl = getClassLoader();
		InputStream is = (cl != null ? cl.getResourceAsStream(fichierDansClasspathAvecCheminComplet) : ClassLoader.getSystemResourceAsStream(fichierDansClasspathAvecCheminComplet));
		BufferedReader reader = new BufferedReader(new InputStreamReader(is,charsetName));
		int numLigne = 1;
		String ligne = reader.readLine(); 
		while (null != ligne) {
			try {
				traiterLigne(callback, ligne,numLigne);
			} catch (ParseException pe) {
				throw new RuntimeException("Erreur de lecture dans la ressource 'classpath:" + fichierDansClasspathAvecCheminComplet + "' à la ligne " + numLigne,pe);
			} catch (NumberFormatException nfe) {
				throw new RuntimeException("Erreur de lecture dans la ressource 'classpath:" + fichierDansClasspathAvecCheminComplet + "' à la ligne " + numLigne,nfe);
			}
			ligne = reader.readLine();
			numLigne++;
		}
		reader.close();
	}
}
