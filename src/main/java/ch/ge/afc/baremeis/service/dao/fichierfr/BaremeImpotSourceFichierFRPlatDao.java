/**
 *
 */
package ch.ge.afc.baremeis.service.dao.fichierfr;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.impotch.bareme.BaremeParTranche;
import org.impotch.bareme.ConstructeurBareme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.ge.afc.baremeis.service.BaremeDisponible;
import ch.ge.afc.baremeis.service.BaremeDisponibleImpl;
import ch.ge.afc.baremeis.service.ICodeTarifaire;
import ch.ge.afc.baremeis.service.dao.BaremeImpotSourceDao;
import org.impotch.util.TypeArrondi;

import static ch.ge.afc.baremeis.service.dao.fichierfr.LecteurFichierTexteStructureFribourgeoise.unLecteurDepuisClasspath;
import static org.impotch.bareme.ConstructeurBareme.unBaremeATauxEffectifSansOptimisationDesQueNonNul;
import static org.impotch.util.TypeArrondi.VINGTIEME_LE_PLUS_PROCHE;

/**
 * @author <a href="mailto:patrick.giroud@etat.ge.ch">Patrick Giroud</a>
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
    public BaremeParTranche obtenirBaremeMensuel(int annee,
                                                 String codeCanton, ICodeTarifaire code) {
            List<EnregistrementBaremeFR> enreg = rechercherTranches(annee, code);
            if (null == enreg) return null;
            // Construction du barème
            ConstructeurBareme cons = unBaremeATauxEffectifSansOptimisationDesQueNonNul().typeArrondiSurChaqueTranche(VINGTIEME_LE_PLUS_PROCHE);
            enreg.stream().forEach(enr -> cons.tranche(enr.getMntMinMensu().subtract(BigDecimal.ONE), enr.getMntMaxMensu(), enr.getTaux()));
            EnregistrementBaremeFR dernier = enreg.get(enreg.size() - 1);
            cons.derniereTranche(dernier.getMntMinMensu(), dernier.getTaux());
            return cons.construire();
    }

    private int getOrdre(ICodeTarifaire code) {
        return Integer.parseInt(code.getCode().substring(1, 2)) + 1;
    }

    private Optional<LecteurFichierTexteStructureFribourgeoise> creerLecteur(int annee) {
        // On recherche d'abord le fichier
        String nomResource = getNomFichier(annee);
        return unLecteurDepuisClasspath(nomResource,"ISO-8859-1");
    }

    private List<EnregistrementBaremeFR> rechercherTranches(int annee,
                                                            final ICodeTarifaire code) {
        Optional<LecteurFichierTexteStructureFribourgeoise> lecteurO = this
                .creerLecteur(annee);
        if (!lecteurO.isPresent()) {
            return null;
        }
        final List<EnregistrementBaremeFR> liste = new LinkedList<EnregistrementBaremeFR>();
        EnregistrementFRCallback callback = new EnregistrementFRCallback() {
            @Override
            public void traiterLigne(LigneEnregistrement ligne) {
                if (code.getCode().charAt(0) == ligne.getGroupe().getCode()) {
                    EnregistrementBaremeFR enreg = new EnregistrementBaremeFR();
                    enreg.setMntMinMensu(ligne.getMntMinMensu());
                    enreg.setMntMaxMensu(ligne.getMntMaxMensu());
                    BigDecimal[] taux = ligne.getTaux();
                    int index = getOrdre(code);
                    enreg.setTaux(taux[index]);
                    liste.add(enreg);
                }
            }
        };
        try {
            lecteurO.get().lire(callback);
        } catch (IOException ioe) {
            String message = "Exception de lecture I/O dans le fichier";
            logger.error(message);
            throw new RuntimeException("message", ioe);
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

    /**
     * Les codes tarifaires sont composés
     * de A0, A1, ..., A5, B0, B1, ... B5, C0, C1, ..., C5
     * @return ces 18 codes tarifaires
     */
    private Set<ICodeTarifaire> getCodes() {
        Set<ICodeTarifaire> codes = new TreeSet<ICodeTarifaire>();
        for (int i = 0; i < 6; i++) {
            codes.add(new CodeTarifaireFR("A", i));
            codes.add(new CodeTarifaireFR("B", i));
            codes.add(new CodeTarifaireFR("C", i));
        }
        return codes;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * ch.ge.afc.baremeis.service.dao.BaremeImpotSourceDao#rechercherBareme(int,
     * java.lang.String)
     */
    @Override
    public Set<ICodeTarifaire> rechercherCodesTarifaires(int annee, String codeCanton) {
        return this.creerLecteur(annee).isPresent() ? getCodes() : null;
    }

    @Override
    public Set<BaremeDisponible> baremeDisponible() {
        return IntStream.iterate(2000, n -> n < 2100, n -> n+1)
                .filter(n -> creerLecteur(n).isPresent())
                .mapToObj(n -> new BaremeDisponibleImpl(n, "fr"))
                .collect(Collectors.toUnmodifiableSet());
    }

}
