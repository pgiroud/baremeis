/**
 * 
 */
package ch.ge.afc.baremeis.service.dao.fichierfr;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.impotch.bareme.ConstructeurBareme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.EmptyResultDataAccessException;

import org.impotch.bareme.BaremeTauxEffectifConstantParTranche;
import ch.ge.afc.baremeis.service.BaremeDisponible;
import ch.ge.afc.baremeis.service.BaremeDisponibleImpl;
import ch.ge.afc.baremeis.service.ICodeTarifaire;
import ch.ge.afc.baremeis.service.dao.BaremeImpotSourceDao;
import org.impotch.util.TypeArrondi;

/**
 * @author <a href="mailto:patrick.giroud@etat.ge.ch">Patrick Giroud</a>
 * 
 */
public class BaremeImpotSourceFichierFRPlatDao implements BaremeImpotSourceDao {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.ge.afc.baremeis.service.dao.BaremeImpotSourceDao#obtenirBaremeMensuel
	 * (int, java.lang.String, ch.ge.afc.baremeis.service.ICodeTarifaire)
	 */
	@Override
	public BaremeTauxEffectifConstantParTranche obtenirBaremeMensuel(int annee,
			String codeCanton, ICodeTarifaire code) {
		try {
			List<EnregistrementBaremeFR> enreg = rechercherTranches(annee, code);
			// Construction du barème
			ConstructeurBareme cons = new ConstructeurBareme();
			cons.typeArrondiSurChaqueTranche(TypeArrondi.CINQ_CTS);
			enreg.stream().forEach(enr -> cons.tranche(enr.getMntMaxMensu(),enr.getTaux()));
			cons.derniereTranche(enreg.get(enreg.size()-1).getTaux());
			return cons.construireBaremeTauxEffectifConstantParTranche();

//			BaremeTauxEffectifConstantParTranche bareme = new BaremeTauxEffectifConstantParTranche();
//			bareme.setTypeArrondiSurChaqueTranche(TypeArrondi.CINQ_CTS);
//			BigDecimal montantPrecedent = BigDecimal.ZERO;
//			BigDecimal tauxPrecedent = BigDecimal.ZERO;
//			for (EnregistrementBaremeFR enr : enreg) {
//				if (0 != tauxPrecedent.compareTo(enr.getTaux())) {
//					bareme.ajouterTranche(montantPrecedent, tauxPrecedent);
//				}
//				montantPrecedent = enr.getMntMaxMensu();
//				tauxPrecedent = enr.getTaux();
//			}
//			bareme.ajouterDerniereTranche(tauxPrecedent);
//			return bareme;
		} catch (EmptyResultDataAccessException ed) {
			return null;
		}
	}

	private int getOrdre(ICodeTarifaire code) {
		return Integer.parseInt(code.getCode().substring(1, 2)) + 1;
	}

	private LecteurFichierTexteStructureFribourgeoise creerLecteur(int annee) {
		// On recherche d'abord le fichier
		String nomResource = getNomFichier(annee);
		Resource fichier = new ClassPathResource(nomResource);
		if (!fichier.exists()) {
			String message = "Pas de fichier " + nomResource + " pour l'année "
					+ annee + ".";
			EmptyResultDataAccessException exception = new EmptyResultDataAccessException(
					message, 1);
			logger.warn(message, exception);
			throw exception;
		}

		LecteurFichierTexteStructureFribourgeoise lecteur = new LecteurFichierTexteStructureFribourgeoise();
		lecteur.setCharsetName("ISO-8859-1");
		lecteur.setFichier(fichier);
		return lecteur;
	}

	private List<EnregistrementBaremeFR> rechercherTranches(int annee,
			final ICodeTarifaire code) {
		LecteurFichierTexteStructureFribourgeoise lecteur = this
				.creerLecteur(annee);
		final List<EnregistrementBaremeFR> liste = new LinkedList<EnregistrementBaremeFR>();
		EnregistrementFRCallback callback = new EnregistrementFRCallback() {
			@Override
			public void traiterLigne(LigneEnregistrement ligne) {
				EnregistrementBaremeFR enreg = new EnregistrementBaremeFR();
				enreg.setMntMinMensu(ligne.getMntMinMensu());
				enreg.setMntMaxMensu(ligne.getMntMaxMensu());
				BigDecimal[] taux = ligne.getTaux();
				int index = getOrdre(code);
				enreg.setTaux(taux[index]);
				liste.add(enreg);
			}
		};
		try {
			lecteur.lire(callback);
		} catch (IOException ioe) {
			String message = "Exception de lecture I/O dans le fichier";
			logger.error(message);
			throw new DataAccessResourceFailureException("message", ioe);
		}
		Collections.sort(liste, new Comparator<EnregistrementBaremeFR>() {
			@Override
			public int compare(EnregistrementBaremeFR o1,
					EnregistrementBaremeFR o2) {
				return o1.getMntMaxMensu().compareTo(o2.getMntMaxMensu());
			}
		});
		return liste;
	}

	private String getNomFichier(int annee) {
		StringBuilder builder = new StringBuilder();
		int anneeDansSiecle = annee % 100;
		String anneeStr = (anneeDansSiecle < 10) ? "0" + anneeDansSiecle : ""
				+ anneeDansSiecle;
		builder.append(annee).append("/fr/Bareme").append(anneeStr).append(
				"fr.txt");
		return builder.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.ge.afc.baremeis.service.dao.BaremeImpotSourceDao#rechercherBareme(int,
	 * java.lang.String)
	 */
	@Override
	public Set<ICodeTarifaire> rechercherBareme(int annee, String codeCanton) {
		// TODO PGI À revoir pour lancer l'exception !
		try {
			this.creerLecteur(annee);
		} catch (EmptyResultDataAccessException ed) {
			return null;
		}
		Set<ICodeTarifaire> codes = new TreeSet<ICodeTarifaire>();
		for (int i = 0; i < 6; i++) {
			codes.add(new CodeTarifaireFR("A", i));
			codes.add(new CodeTarifaireFR("B", i));
			codes.add(new CodeTarifaireFR("C", i));
		}
		return codes;
	}

	@Override
	public Set<BaremeDisponible> baremeDisponible() {
		Set<BaremeDisponible> baremes = new HashSet<BaremeDisponible>();
		for (int annee = 2000; annee < 2100; annee++) {
			Resource resource = new ClassPathResource(getNomFichier(annee));
			if (resource.exists()) {
				baremes.add(new BaremeDisponibleImpl(annee, "fr"));
			}
		}
		return baremes;
	}

}
