/**
 * 
 */
package ch.ge.afc.baremeis.service.dao.fichierfr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.dao.TypeMismatchDataAccessException;
import org.springframework.util.StringUtils;

import ch.ge.afc.baremeis.service.GroupeTarifaire;

/**
 * @author <a href="mailto:patrick.giroud@etat.ge.ch">Patrick Giroud</a>
 *
 */
class LecteurFichierTexteStructureFribourgeoise {
	/**************************************************/
    /****************** Attributs *********************/
    /**************************************************/

	final Logger logger = LoggerFactory.getLogger(LecteurFichierTexteStructureFribourgeoise.class);
	private Resource fichier;
	private String charsetName;
	
    /**************************************************/
    /**************** Constructeurs *******************/
    /**************************************************/

	public LecteurFichierTexteStructureFribourgeoise() {
		super();
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
	
	/**************************************************/
    /******************* Méthodes *********************/
    /**************************************************/

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
		if (!StringUtils.hasText(taux)) return null;
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
	
	
	public void lire(EnregistrementFRCallback callback) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(fichier.getInputStream(),charsetName));
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
