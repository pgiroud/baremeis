/**
 * 
 */
package ch.ge.afc.baremeis.service.dao.fichierfr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Optional;
import java.util.stream.Stream;

import org.impotch.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.ge.afc.baremeis.service.GroupeTarifaire;

/**
 * @author <a href="mailto:patrick.giroud@etat.ge.ch">Patrick Giroud</a>
 *
 */
class LecteurFichierTexteStructureFribourgeoise {

	public static Optional<LecteurFichierTexteStructureFribourgeoise> unLecteurDepuisClasspath(String fichierAvecCheminComplet, String charsetName) {
		LecteurFichierTexteStructureFribourgeoise lecteur = new LecteurFichierTexteStructureFribourgeoise(fichierAvecCheminComplet,charsetName);
		return lecteur.exist() ? Optional.of(lecteur) : Optional.empty();
	}

	/**************************************************/
    /****************** Attributs *********************/
    /**************************************************/

	final Logger logger = LoggerFactory.getLogger(LecteurFichierTexteStructureFribourgeoise.class);
	private final String fichierDansClasspathAvecCheminComplet;
	private final String charsetName;
	
    /**************************************************/
    /**************** Constructeurs *******************/
    /**************************************************/

	private LecteurFichierTexteStructureFribourgeoise(String fichierAvecCheminComplet, String charset) {
		super();
		this.fichierDansClasspathAvecCheminComplet = fichierAvecCheminComplet;
		this.charsetName = charset;
	}
	
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
			cl = LecteurFichierTexteStructureFribourgeoise.class.getClassLoader();
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


	private String sousChaine(String chaine, int posDebut, int posFin) {
		return chaine.substring(posDebut - 1, posFin);
	}

	private GroupeTarifaire lireGroupe(String code) {
		if ("TS 1".equals(code)) {
			return GroupeTarifaire.A;
		} else if ("TS 2".equals(code)) {
			return GroupeTarifaire.SALAIRE_UNIQUE;
		} else if ("TS 3".equals(code)) {
			return GroupeTarifaire.DOUBLE_SALAIRE;
		}
		throw new IllegalArgumentException("Le groupe tarifaire ne peut pas être égal à '" + code+ "' !!");
	}
	
	private BigDecimal taux(String taux) {
		if (!StringUtil.hasText(taux)) return null;
		return new BigDecimal("0." + taux);
	}
	
	private void traiterLigne(EnregistrementFRCallback callback, String ligne, int numeroLigne) throws ParseException {
		LigneEnregistrement enreg = new LigneEnregistrement();
		enreg.setGroupe(lireGroupe(sousChaine(ligne,1,4)));
		enreg.setMntMinMensu(new BigDecimal(sousChaine(ligne,5,9)));
		enreg.setMntMaxMensu(new BigDecimal(sousChaine(ligne,10,14)));
		int offset = 14;
		BigDecimal[] taux = new BigDecimal[(ligne.length()-offset) / 4];
		int curseur = offset;
		for (int pos = 0; pos < taux.length; pos++) {
			taux[pos] = taux(sousChaine(ligne,curseur+1,curseur+4));
			curseur += 4;
		}
		enreg.setTaux(taux);
		callback.traiterLigne(enreg);
	}	
	
	private boolean exist() {
		try {
			ClassLoader cl = getClassLoader();
			InputStream is = (cl != null ? cl.getResourceAsStream(fichierDansClasspathAvecCheminComplet) : ClassLoader.getSystemResourceAsStream(fichierDansClasspathAvecCheminComplet));
			return null != is && new BufferedReader(new InputStreamReader(is,charsetName)).ready();
		} catch (IOException ioe) {
			logger.debug("Pas de lecture possible dans 'classpath:" + fichierDansClasspathAvecCheminComplet + "'",ioe);
		}
		return false;
	}


	public void lire(EnregistrementFRCallback callback) throws IOException {
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
