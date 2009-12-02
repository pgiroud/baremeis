/**
 * 
 */
package ch.ge.afc.baremeis.service.dao.fichierge;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.EmptyResultDataAccessException;

import static ch.ge.afc.baremeis.service.dao.fichierge.CodeTarifaireGE.*;

import ch.ge.afc.baremeis.service.ICodeTarifaire;
import ch.ge.afc.baremeis.service.dao.BaremeImpotSourceDao;
import ch.ge.afc.calcul.bareme.Bareme;
import ch.ge.afc.calcul.bareme.BaremeTauxEffectifConstantParTranche;
import ch.ge.afc.util.TypeArrondi;


/**
 * @author <a href="mailto:patrick.giroud@etat.ge.ch">Patrick Giroud</a>
 *
 */
public class BaremeImpotSourceFichierGEPlatDao implements BaremeImpotSourceDao {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private String getNomFichier(int annee) {
		StringBuilder builder = new StringBuilder();
		builder.append(annee).append("/bar_is").append(annee).append("_ascii.txt");
		return builder.toString();
	}
	
	private LecteurFichierTexteStructureGenevoise creerLecteur(int annee) {
		// On recherche d'abord le fichier
		String nomResource = getNomFichier(annee);
		Resource fichier = new ClassPathResource(nomResource);
		if (!fichier.exists()) {
			String message = "Pas de fichier " + nomResource + " pour l'année " + annee + ".";
			EmptyResultDataAccessException exception = new EmptyResultDataAccessException(message,1);
			logger.warn(message, exception);
			throw exception;
		}
		
		LecteurFichierTexteStructureGenevoise lecteur = new LecteurFichierTexteStructureGenevoise();
		lecteur.setCharsetName("ISO-8859-1");
		lecteur.setFichier(fichier);
		return lecteur;
	}
	
	
	private int getOrdre(CodeTarifaireGE code) {
		if (A0.equals(code)) return 0;
		else if (B0.equals(code)) return 1;
		else if (B1.equals(code)) return 2;
		else if (B2.equals(code)) return 3;
		else if (B3.equals(code)) return 4;
		else if (B4.equals(code)) return 5;
		else if (B5.equals(code)) return 6;
		else if (B6.equals(code)) return 7;
		else if (B7.equals(code)) return 8;
		else if (B8.equals(code)) return 9;
		else if (B9.equals(code)) return 10;
		else if (B10.equals(code)) return 11;
		else if (B11.equals(code)) return 12;
		else if (B12.equals(code)) return 13;
		else if (B13.equals(code)) return 14;
		else if (B14.equals(code)) return 15;
		else if (B15.equals(code)) return 16;
		else if (I0.equals(code)) return 17;
		else if (I1.equals(code)) return 18;
		else if (I2.equals(code)) return 19;
		else if (I3.equals(code)) return 20;
		else if (I4.equals(code)) return 21;
		else if (I5.equals(code)) return 22;
		else if (I6.equals(code)) return 23;
		else if (I7.equals(code)) return 24;
		else if (I8.equals(code)) return 25;
		else throw new IllegalArgumentException("Le code tarifaire '" + code + "' est inconnu !!");
	}
	
	public Set<ICodeTarifaire> rechercherBareme(int annee, String codeCanton) {
		if ("ge".equals(codeCanton.toLowerCase())) {
			CodeTarifaireGE[] valeurs = CodeTarifaireGE.values();
			Set<ICodeTarifaire> set = new HashSet<ICodeTarifaire>(valeurs.length);
			for (CodeTarifaireGE code : valeurs) {
				set.add(code);
			}
			return set;
		} else {
			throw new IllegalArgumentException("Ce dao ne s'applique que pour les fichiers genevois !!");
		}
	}

	public List<EnregistrementBaremeGE> rechercherTranches(int annee, final ICodeTarifaire code) {
		LecteurFichierTexteStructureGenevoise lecteur = this.creerLecteur(annee);
		final List<EnregistrementBaremeGE> liste = new LinkedList<EnregistrementBaremeGE>();
		EnregistrementGECallback callback = new EnregistrementGECallback() {
			@Override
			public void traiterLigne(LigneEnregistrement ligne) {
				EnregistrementBaremeGE enreg = new EnregistrementBaremeGE();
				enreg.setMntMinAnnuel(ligne.getMntMinAnnuel());
				enreg.setMntMaxAnnuel(ligne.getMntMaxAnnuel());
				enreg.setMntMinMensu(ligne.getMntMinMensu());
				enreg.setMntMaxMensu(ligne.getMntMaxMensu());
				enreg.setMntMinHoraire(ligne.getMntMinHoraire());
				enreg.setMntMaxHoraire(ligne.getMntMaxHoraire());
				BigDecimal[] taux = ligne.getTaux();
				int index = getOrdre((CodeTarifaireGE)code);
				enreg.setTaux(taux[index]);
				liste.add(enreg);
			}
		};
		try {
			lecteur.lire(callback);
		} catch (IOException ioe) {
			String message = "Exception de lecture I/O dans le fichier";
			logger.error(message);
			throw new DataAccessResourceFailureException("message",ioe);
		}
		Collections.sort(liste,new Comparator<EnregistrementBaremeGE>(){
			@Override
			public int compare(EnregistrementBaremeGE o1, EnregistrementBaremeGE o2) {
				return o1.getMntMaxAnnuel().compareTo(o2.getMntMaxAnnuel());
			}
		});
		return liste;
	}

	@Override
	public Bareme obtenirBaremeMensuel(int annee, String codeCanton,
			ICodeTarifaire code) {
		if (!(code instanceof CodeTarifaireGE)) throw new IllegalArgumentException("Le code tarifaire doit être un code genevois !!");
		List<EnregistrementBaremeGE> enreg = rechercherTranches(annee, code);
		// Construction du barème
		BaremeTauxEffectifConstantParTranche bareme = new BaremeTauxEffectifConstantParTranche();
		bareme.setTypeArrondi(TypeArrondi.CINQ_CTS);
		BigDecimal montantPrecedent = BigDecimal.ZERO;
		BigDecimal tauxPrecedent = BigDecimal.ZERO;
		for (EnregistrementBaremeGE enr : enreg) {
			if (0 != tauxPrecedent.compareTo(enr.getTaux())) {
				bareme.ajouterTranche(montantPrecedent, tauxPrecedent);
			}
			montantPrecedent = enr.getMntMaxMensu();
			tauxPrecedent = enr.getTaux();
		}
		bareme.ajouterDerniereTranche(tauxPrecedent);
		return bareme;
	}

	
	
}
