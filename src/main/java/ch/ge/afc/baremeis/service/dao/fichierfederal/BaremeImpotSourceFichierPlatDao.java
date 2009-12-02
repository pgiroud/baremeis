/**
 * 
 */
package ch.ge.afc.baremeis.service.dao.fichierfederal;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.EmptyResultDataAccessException;

import ch.ge.afc.baremeis.service.ICodeTarifaire;
import ch.ge.afc.baremeis.service.dao.BaremeImpotSourceDao;
import ch.ge.afc.calcul.bareme.Bareme;
import ch.ge.afc.calcul.bareme.BaremeTauxEffectifConstantParTranche;
import ch.ge.afc.util.TypeArrondi;

/**
 * @author <a href="mailto:patrick.giroud@etat.ge.ch">Patrick Giroud</a>
 *
 */
public class BaremeImpotSourceFichierPlatDao implements BaremeImpotSourceDao {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private String getNomFichier(int annee, int anneeBareme, String codeCanton) {
		StringBuilder builder = new StringBuilder();
		builder.append(annee).append("/tar").append(String.valueOf(anneeBareme).substring(2))
		.append(codeCanton).append(".txt");
		return builder.toString();
	}
	
	private LecteurFichierTexteStructureFederale creerLecteur(int annee, int anneeBareme, String codeCanton) {
		// On recherche d'abord le fichier
		String codeCantonMinuscule = codeCanton.toLowerCase();
		String nomResource = getNomFichier(annee,anneeBareme,codeCantonMinuscule);
		Resource fichier = new ClassPathResource(nomResource);
		if (!fichier.exists()) {
			String message = "Pas de fichier " + nomResource + " pour l'année " + annee + " et le canton '" +  codeCanton + "'";
			EmptyResultDataAccessException exception = new EmptyResultDataAccessException(message,1);
			logger.info(message, exception);
			throw exception;
		}
		
		LecteurFichierTexteStructureFederale lecteur = new LecteurFichierTexteStructureFederale();
		lecteur.setCharsetName("ISO-8859-1");
		lecteur.setFichier(fichier);
		return lecteur;
	}
	
	private LecteurFichierTexteStructureFederale creerLecteur(int annee, String codeCanton) {
		LecteurFichierTexteStructureFederale lecteur = null;
		try {
			lecteur = this.creerLecteur(annee, annee, codeCanton);
 		} catch (EmptyResultDataAccessException ex) {
 			// Dans le cas où le fichier n'existe pas pour l'année, on teste avec l'année précédente
 			lecteur = this.creerLecteur(annee, annee -1, codeCanton);
 		}
 		return lecteur;
	}
	
	@Override
	public Set<ICodeTarifaire> rechercherBareme(int annee, String codeCanton) {
		LecteurFichierTexteStructureFederale lecteur = this.creerLecteur(annee, codeCanton);
		final Set<ICodeTarifaire> codes = new TreeSet<ICodeTarifaire>();
		EnregistrementCallback callback = new EnregistrementCallback() {
			@Override
			public void lectureEnregistrement(EnregistrementBareme enreg) {
				codes.add(enreg.getCodeTarifaire());
			}
			@Override
			public void lectureEnregistrementFinal(String codeCanton,
					int nbreEnregistrement) {
				logger.debug("Fin de lecture du fichier cantonal '" + codeCanton + "' Nbre enreg. " + nbreEnregistrement);
			}
			@Override
			public void lectureEnregistrementInitial(String codeCanton,
					Date dateCreation) {
				logger.debug("Début de lecture du fichier cantonal '" + codeCanton + "' crée le " + dateCreation);
			}
			
		};
		try {
			lecteur.lire(callback);
		} catch (IOException ioe) {
			String message = "Exception de lecture I/O dans le fichier " + codeCanton;
			logger.error(message);
			throw new DataAccessResourceFailureException("message",ioe);
		}
		return codes;
	}

	public List<EnregistrementBareme> rechercherTranches(int annee,
			String codeCanton, final ICodeTarifaire code) {
		LecteurFichierTexteStructureFederale lecteur = this.creerLecteur(annee, codeCanton);
		final List<EnregistrementBareme> liste = new LinkedList<EnregistrementBareme>();
		EnregistrementCallback callback = new EnregistrementCallback() {
			@Override
			public void lectureEnregistrement(EnregistrementBareme enreg) {
				if (code.equals(enreg.getCodeTarifaire())) {
					liste.add(enreg);
				}
			}
			@Override
			public void lectureEnregistrementFinal(String codeCanton,
					int nbreEnregistrement) {
				logger.debug("Fin de lecture du fichier cantonal '" + codeCanton + "' Nbre enreg. " + nbreEnregistrement);
			}
			@Override
			public void lectureEnregistrementInitial(String codeCanton,
					Date dateCreation) {
				logger.debug("Début de lecture du fichier cantonal '" + codeCanton + "' crée le " + dateCreation);
			}
		};
		try {
			lecteur.lire(callback);
		} catch (IOException ioe) {
			String message = "Exception de lecture I/O dans le fichier " + codeCanton;
			logger.error(message);
			throw new DataAccessResourceFailureException("message",ioe);
		}
		Collections.sort(liste,new Comparator<EnregistrementBareme>(){
			@Override
			public int compare(EnregistrementBareme o1, EnregistrementBareme o2) {
				return o1.getRevenuImposable().compareTo(o2.getRevenuImposable());
			}
		});
		return liste;
	}

	@Override
	public Bareme obtenirBaremeMensuel(int annee, String codeCanton, ICodeTarifaire code){
		List<EnregistrementBareme> enreg = rechercherTranches(annee, codeCanton, code);
		// Construction du barème
		BaremeTauxEffectifConstantParTranche bareme = new BaremeTauxEffectifConstantParTranche();
		bareme.setTypeArrondi(TypeArrondi.CINQ_CTS);
		BigDecimal revenu = BigDecimal.ZERO;
		BigDecimal tauxPrecedent = enreg.get(0).getTaux();
		for (EnregistrementBareme enr : enreg) {
			if (0 != tauxPrecedent.compareTo(enr.getTaux())) {
				bareme.ajouterTranche(revenu, tauxPrecedent);
			}
			revenu = revenu.add(enr.getEchelonTarifaire());
			tauxPrecedent = enr.getTaux();
		}
		bareme.ajouterDerniereTranche(tauxPrecedent);
		return bareme;
	}


}
