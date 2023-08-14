/**
 *
 */
package ch.ge.afc.baremeis.service.dao.fichierfederal;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import ch.ge.afc.baremeis.service.Canton;
import org.impotch.bareme.BaremeParTranche;
import org.impotch.bareme.ConstructeurBareme;
import org.impotch.util.BigDecimalUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.impotch.util.TypeArrondi;

import ch.ge.afc.baremeis.service.BaremeDisponible;
import ch.ge.afc.baremeis.service.BaremeDisponibleImpl;
import ch.ge.afc.baremeis.service.ICodeTarifaire;
import ch.ge.afc.baremeis.service.dao.BaremeImpotSourceDao;

import static org.impotch.bareme.ConstructeurBareme.unBaremeATauxEffectif;
import static ch.ge.afc.baremeis.service.dao.fichierfederal.LecteurFichierTexteStructureFederale.unLecteurDepuisClasspath;
/**
 * @author <a href="mailto:patrick.giroud@etat.ge.ch">Patrick Giroud</a>
 */
public class BaremeImpotSourceFichierPlatDao implements BaremeImpotSourceDao {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private String getNomFichier(int annee, int anneeBareme, String codeCanton, String extension) {
        StringBuilder builder = new StringBuilder();
        builder.append(annee).append("/tar").append(String.valueOf(anneeBareme).substring(2))
                .append(codeCanton).append(".").append(extension);
        return builder.toString();
    }

    private Optional<LecteurFichierTexteStructureFederale> creerLecteurTxt(int annee, int anneeBareme, String codeCantonMinuscule) {
        return unLecteurDepuisClasspath(getNomFichier(annee, anneeBareme, codeCantonMinuscule, "txt"),"ISO-8859-1");
    }

    private Optional<LecteurFichierTexteStructureFederale> creerLecteurXz(int annee, int anneeBareme, String codeCantonMinuscule) {
        return unLecteurDepuisClasspath(getNomFichier(annee, anneeBareme, codeCantonMinuscule, "xz"),"ISO-8859-1");
    }

    private Optional<LecteurFichierTexteStructureFederale> creerLecteurZip(int annee, int anneeBareme, String codeCantonMinuscule) {
        return unLecteurDepuisClasspath(getNomFichier(annee, anneeBareme, codeCantonMinuscule, "zip"),"ISO-8859-1");
    }



    private Optional<LecteurFichierTexteStructureFederale> creerLecteur(int annee, String codeCanton) {
        // On recherche d'abord le fichier
        String codeCantonMinuscule = codeCanton.toLowerCase();
         for (int anneeBareme = annee; anneeBareme > 2000; anneeBareme--) {
            Optional<LecteurFichierTexteStructureFederale> lecteur =
                    creerLecteurTxt(annee, anneeBareme, codeCantonMinuscule);
            if (lecteur.isPresent()) {
                return lecteur;
            } else {
                lecteur = creerLecteurXz(annee, anneeBareme, codeCantonMinuscule);
                if (lecteur.isPresent()) {
                    return lecteur;
                }
                else {
                    lecteur = creerLecteurZip(annee, anneeBareme, codeCantonMinuscule);
                    if (lecteur.isPresent()) {
                        return lecteur;
                    } else {
                        String message = "Pas de fichier fédéral pour l'année " + annee + " et le canton '" + codeCanton + "'";
                        RuntimeException exception = new RuntimeException(message);
                        logger.info(message, exception);
                        return Optional.empty();
                    }
                }
            }
        }
         return null;
     }

    @Override
    public Set<ICodeTarifaire> rechercherCodesTarifaires(int annee, String codeCanton) {
        if (Canton.getParCode(codeCanton).isEmpty()) throw new RuntimeException("Le code '" + codeCanton + "' n'est pas un code de canton suisse !!");
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
            Optional<LecteurFichierTexteStructureFederale> lecteur = this.creerLecteur(annee, codeCanton);
            if (lecteur.isPresent()) {
                lecteur.get().lire(callback);
            }
        } catch (IOException ioe) {
            String message = "Exception de lecture I/O dans le fichier " + codeCanton;
            logger.error(message);
            throw new RuntimeException("message", ioe);
        }
        return codes;
    }

    public List<EnregistrementBareme> rechercherTranches(int annee,
                                                         String codeCanton, final ICodeTarifaire code) {
         final List<EnregistrementBareme> liste = new LinkedList<>();
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
            Optional<LecteurFichierTexteStructureFederale> lecteur = this.creerLecteur(annee, codeCanton);
            if (lecteur.isPresent()) {
                lecteur.get().lire(callback);
            }
        }
        catch(IOException ioe) {
            String message = "Exception de lecture I/O dans le fichier " + codeCanton;
            logger.error(message);
            throw new RuntimeException("message", ioe);
        }
        Collections.sort(liste, new Comparator<EnregistrementBareme>() {
            @Override
            public int compare(EnregistrementBareme o1, EnregistrementBareme o2) {
                return o1.getRevenuImposable().compareTo(o2.getRevenuImposable());
            }
        });
        return liste;
    }

    @Override
    public BaremeParTranche obtenirBaremeMensuel(int annee, String codeCanton, ICodeTarifaire code) {
        List<EnregistrementBareme> enreg = rechercherTranches(annee, codeCanton, code);
        // Construction du barème
        ConstructeurBareme cons = unBaremeATauxEffectif()
            .typeArrondiSurChaqueTranche(TypeArrondi.CINQ_CENTIEMES_LES_PLUS_PROCHES);
        if (1 < enreg.size()) {
            // 1ère tranche
            EnregistrementBareme premierEnregistrement = enreg.get(0);
            cons.premiereTranche(premierEnregistrement.getMontantImposableMax(),premierEnregistrement.getTaux());
            BigDecimal longueurDerniereTranche = BigDecimal.valueOf(9_999_999);
            enreg.stream()
                    .filter(enr -> {
                        return BigDecimalUtil.isStrictementPositif(enr.getRevenuImposable())
                                && BigDecimalUtil.isStrictementPositif(enr.getRevenuImposable().subtract(BigDecimal.ONE))
                                // on ne veut pas la première tranche
                                &&
                                BigDecimalUtil.isStrictementPositif(longueurDerniereTranche.subtract(enr.getEchelonTarifaire())); // on ne veut pas la dernière tranche
                    })
                    .forEach(enr -> cons.tranche(enr.getRevenuImposable(),enr.getMontantImposableMax(),enr.getTaux()));

        }
        EnregistrementBareme dernier = enreg.get(enreg.size()-1);
        cons.derniereTranche(dernier.getRevenuImposable(),dernier.getTaux());
        return cons.construire();
    }

    private Set<BaremeDisponible> baremeDisponible(String codeCanton) {
        return IntStream.iterate(2000, n -> n < 2100, n -> n+1)
                .filter(n -> creerLecteur(n,codeCanton).isPresent())
                .mapToObj(n -> new BaremeDisponibleImpl(n, codeCanton))
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public Set<BaremeDisponible> baremeDisponible() {
        Set<BaremeDisponible> dispo = baremeDisponible("ge");
        dispo.addAll(baremeDisponible("fr"));
        dispo.addAll(baremeDisponible("ag"));
        dispo.addAll(baremeDisponible("gr"));
        dispo.addAll(baremeDisponible("vd"));

        return dispo;
    }
//    @Override
//    public Set<BaremeDisponible> baremeDisponible() {
//        Set<BaremeDisponible> baremes = new HashSet<>();
//        for (int annee = 2001; annee < 2100; annee++) {
//            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
//            try {
//                Resource[] resources = resolver.getResources("classpath*:" + annee + "/tar*");
//                for (Resource resource : resources) {
//                    String codeCanton = resource.getFilename().substring(5, 7);
//                    baremes.add(new BaremeDisponibleImpl(annee, codeCanton));
//                }
//            } catch (IOException ioe) {
//                // TODO PGI propager une RuntimeException
//                logger.error("Problème lors de la recherche de barèmes fédéraux pour " + annee, ioe);
//            }
//        }
//        return baremes;
//    }


}
