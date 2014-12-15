/**
 * 
 */
package ch.ge.afc.baremeis.service.dao.fichierge;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.EmptyResultDataAccessException;

import static ch.ge.afc.baremeis.service.dao.fichierge.CodeTarifaireGE.*;

import ch.ge.afc.baremeis.service.BaremeDisponible;
import ch.ge.afc.baremeis.service.BaremeDisponibleImpl;
import ch.ge.afc.baremeis.service.ICodeTarifaire;
import ch.ge.afc.baremeis.service.dao.BaremeImpotSourceDao;
import ch.ge.afc.baremeis.service.dao.fichierfederal.CodeTarifaire;
import org.impotch.bareme.BaremeTauxEffectifConstantParTranche;
import org.impotch.util.TypeArrondi;


/**
 * @author <a href="mailto:patrick.giroud@etat.ge.ch">Patrick Giroud</a>
 *
 */
public class BaremeImpotSourceFichierGEPlatDao implements BaremeImpotSourceDao {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private static final List<CodeTarifaireGE> ORDRE_CODE_AVANT_2010 = Arrays.asList(A0,B0,B1,B2,B3,B4,B5,B6,B7,B8,B9,B10,B11,B12,B13,B14,B15,I0,I1,I2,I3,I4,I5,I6,I7,I8);
	private static final List<String> ORDRE_CODE = Arrays.asList("A0","B0","B1","B2","B3","B4","B5");
	
	
	@Override
	public Set<BaremeDisponible> baremeDisponible() {
		Set<BaremeDisponible> baremes = new HashSet<BaremeDisponible>();
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		for (int annee = 2000; annee < 2100; annee++) {
			try {
				Resource[] resources = resolver.getResources("classpath*:" + annee + "/ge/*.txt");
				if (resources.length > 0) baremes.add(new BaremeDisponibleImpl(annee,"ge"));
			} catch (IOException ioe) {
				logger.error("Problème lors de la recherche de barèmes cantonaux GE pour " + annee, ioe);
				throw new DataAccessResourceFailureException("Problème lors de la recherche de fichier genevois pour l'année " + annee);
			}
		}
		return baremes;
	}

	private LecteurFichierTexteStructureGenevoise creerLecteur(int annee) {
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		Resource fichier = null;
		try {
			Resource[] resources = resolver.getResources("classpath*:" + annee + "/ge/*.txt");
			if (resources.length > 0) fichier = resources[0];
			else throw new DataAccessResourceFailureException("Il n'existe pas de fichier genevois pour l'année " + annee);
		} catch (IOException ioe) {
			logger.error("Problème lors de la recherche de barèmes cantonaux GE pour " + annee, ioe);
			throw new DataAccessResourceFailureException("Problème lors de la recherche de fichier genevois pour l'année " + annee);
		}
		if (!fichier.exists()) {
			String message = "Pas de fichier genevois pour l'année " + annee + ".";
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
		int index = ORDRE_CODE_AVANT_2010.indexOf(code);
		if (index < 0) throw new IllegalArgumentException("Le code tarifaire '" + code + "' est inconnu !!");
		return index;
	}
	
	private int getOrdre(String code) {
		String debutCode = code.substring(0,2);
		int index = ORDRE_CODE.indexOf(debutCode);
		if (index < 0) throw new IllegalArgumentException("Le code tarifaire '" + code + "' est inconnu !!");
		return index;
	}
	
	
	
	
	public Set<ICodeTarifaire> rechercherBareme(int annee, String codeCanton) {
		if ("ge".equals(codeCanton.toLowerCase())) {
			Set<ICodeTarifaire> set = new TreeSet<ICodeTarifaire>();
			if (annee < 2010) {
				CodeTarifaireGE[] valeurs = CodeTarifaireGE.values();
				for (CodeTarifaireGE code : valeurs) {
					set.add(code);
				}
			} else {
				for (final String code : ORDRE_CODE) {
					set.add(new CodeTarifaire(code));
				}
			}
			return set;
		} else {
			throw new IllegalArgumentException("Ce dao ne s'applique que pour les fichiers genevois !!");
		}
	}

	private List<EnregistrementBaremeGE> rechercherTranches(final int annee, final ICodeTarifaire code) {
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
				int index;
				if (annee < 2010) index = getOrdre((CodeTarifaireGE)code);
				else index = getOrdre(code.getCode());
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
	public BaremeTauxEffectifConstantParTranche obtenirBaremeMensuel(int annee, String codeCanton,
			ICodeTarifaire code) {
		if (annee < 2010 && !(code instanceof CodeTarifaireGE)) throw new IllegalArgumentException("Le code tarifaire doit être un code genevois !!");
		List<EnregistrementBaremeGE> enreg = rechercherTranches(annee, code);
		// Construction du barème
		BaremeTauxEffectifConstantParTranche bareme = new BaremeTauxEffectifConstantParTranche();
		bareme.setTypeArrondiSurChaqueTranche(TypeArrondi.CINQ_CTS);
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
