/**
 *
 */
package ch.ge.afc.baremeis.service.dao.fichierfederal;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import ch.ge.afc.baremeis.service.Canton;
import org.impotch.bareme.BaremeParTranche;
import org.impotch.bareme.ConstructeurBareme;
import org.impotch.util.BigDecimalUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.ge.afc.baremeis.service.BaremeDisponible;
import ch.ge.afc.baremeis.service.BaremeDisponibleImpl;
import ch.ge.afc.baremeis.service.ICodeTarifaire;
import ch.ge.afc.baremeis.service.dao.BaremeImpotSourceDao;

import static org.impotch.util.TypeArrondi.DIZAINE_LA_PLUS_PROCHE;
import static org.impotch.util.TypeArrondi.VINGTIEME_LE_PLUS_PROCHE;
import static org.impotch.bareme.ConstructeurBareme.unBaremeATauxEffectifSansOptimisationDesQueNonNul;
import static ch.ge.afc.baremeis.service.dao.fichierfederal.LecteurFichierTexteStructureFederale.unLecteurDepuisClasspath;
/**
 * @author <a href="mailto:patrick.giroud@etat.ge.ch">Patrick Giroud</a>
 */
public class BaremeImpotSourceFichierPlatDao implements BaremeImpotSourceDao {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final static String PREFIX_SALARIE = "tar"; // Certainement tar comme tarif !
    private final static String PREFIX_AUTRES_REVENUS = "vsl"; //


    private String getNomFichier(String prefixe, int annee, int anneeBareme, String codeCanton, String extension) {
        StringBuilder builder = new StringBuilder();
        builder.append(annee).append("/").append(prefixe).append(String.valueOf(anneeBareme).substring(2))
                .append(codeCanton).append(".").append(extension);
        return builder.toString();
    }

    private Optional<LecteurFichierTexteStructureFederale> creerLecteurTxt(String prefixe, int annee, int anneeBareme, String codeCantonMinuscule) {
        return unLecteurDepuisClasspath(getNomFichier(prefixe, annee, anneeBareme, codeCantonMinuscule, "txt"),"ISO-8859-1");
    }

    private Optional<LecteurFichierTexteStructureFederale> creerLecteurXz(String prefixe, int annee, int anneeBareme, String codeCantonMinuscule) {
        return unLecteurDepuisClasspath(getNomFichier(prefixe, annee, anneeBareme, codeCantonMinuscule, "xz"),"ISO-8859-1");
    }

    private Optional<LecteurFichierTexteStructureFederale> creerLecteurZip(String prefixe, int annee, int anneeBareme, String codeCantonMinuscule) {
        return unLecteurDepuisClasspath(getNomFichier(prefixe, annee, anneeBareme, codeCantonMinuscule, "zip"),"ISO-8859-1");
    }

    private Optional<LecteurFichierTexteStructureFederale> creerLecteurSalaire(int annee, String codeCanton) {
        return creerLecteur(PREFIX_SALARIE, annee,codeCanton);
    }

    private Optional<LecteurFichierTexteStructureFederale> creerLecteurAutresRevenus(int annee, String codeCanton) {
        return creerLecteur(PREFIX_AUTRES_REVENUS, annee,codeCanton);
    }

    private Optional<LecteurFichierTexteStructureFederale> creerLecteur(String prefixe, int annee, String codeCanton) {
        // On recherche d'abord le fichier
        String codeCantonMinuscule = codeCanton.toLowerCase();
         for (int anneeBareme = annee; anneeBareme > 1999; anneeBareme--) {
            Optional<LecteurFichierTexteStructureFederale> lecteur =
                    creerLecteurTxt(prefixe, annee, anneeBareme, codeCantonMinuscule);
            if (lecteur.isPresent()) {
                String nomFichier = getNomFichier(prefixe,annee, anneeBareme, codeCantonMinuscule, "txt");
                logger.atDebug().setMessage("Le fichier {} a été trouvé dans le classpath.").addArgument(nomFichier).log();
                return lecteur;
            } else {
                lecteur = creerLecteurXz(prefixe, annee, anneeBareme, codeCantonMinuscule);
                if (lecteur.isPresent()) {
                    String nomFichier = getNomFichier(prefixe,annee, anneeBareme, codeCantonMinuscule, "xz");
                    logger.atDebug().setMessage("Le fichier {} a été trouvé dans le classpath.").addArgument(nomFichier).log();
                    return lecteur;
                }
                else {
                    lecteur = creerLecteurZip(prefixe, annee, anneeBareme, codeCantonMinuscule);
                    if (lecteur.isPresent()) {
                        String nomFichier = getNomFichier(prefixe,annee, anneeBareme, codeCantonMinuscule, "zip");
                        logger.atDebug().setMessage("Le fichier {} a été trouvé dans le classpath.").addArgument(nomFichier).log();
                        return lecteur;
                    } else {
                        String message = "Pas de fichier fédéral pour l'année " + annee + " et le canton '" + codeCanton + "'";
                        logger.atInfo().setMessage("Pas de fichier fédéral pour l'année {} et le canton '{}'")
                                .addArgument(annee).addArgument(codeCantonMinuscule).log();
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
            Optional<LecteurFichierTexteStructureFederale> lecteur = this.creerLecteurSalaire(annee, codeCanton);
            if (lecteur.isPresent()) {
                lecteur.get().lire(callback);
            }
            lecteur = this.creerLecteurAutresRevenus(annee,codeCanton);
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
            Optional<LecteurFichierTexteStructureFederale> lecteur = this.creerLecteurSalaire(annee, codeCanton);
            if (lecteur.isPresent()) {
                lecteur.get().lire(callback);
            }
            if (liste.isEmpty()) {
                lecteur = this.creerLecteurAutresRevenus(annee, codeCanton);
                if (lecteur.isPresent()) {
                    lecteur.get().lire(callback);
                }
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

    private static BigDecimal a10francsPres(BigDecimal bg) {
        return DIZAINE_LA_PLUS_PROCHE.arrondir(bg);
    }

    private BaremeParTranche obtenirBaremeMensuelGEavant2024(int annee, ICodeTarifaire code) {
        // Avant 2024, les barèmes genevois étaient fermés à droite et non pas à gauche comme dans les autres cantons
        List<EnregistrementBareme> enreg = rechercherTranches(annee, "ge", code);
        // Construction du barème
        ConstructeurBareme cons = unBaremeATauxEffectifSansOptimisationDesQueNonNul()
                .typeArrondiSurChaqueTranche(VINGTIEME_LE_PLUS_PROCHE);
        if (1 < enreg.size()) {
            // 1ère tranche
            EnregistrementBareme premierEnregistrement = enreg.get(0);
            cons.premiereTranche(a10francsPres(premierEnregistrement.getMontantImposableMax()),premierEnregistrement.getTaux());
            BigDecimal longueurDerniereTranche = BigDecimal.valueOf(9_999_999);
            enreg.stream()
                    .filter(enr -> {
                        return BigDecimalUtil.isStrictementPositif(enr.getRevenuImposable())
                                && BigDecimalUtil.isStrictementPositif(enr.getRevenuImposable().subtract(BigDecimal.ONE))
                                // on ne veut pas la première tranche
                                &&
                                BigDecimalUtil.isStrictementPositif(longueurDerniereTranche.subtract(enr.getEchelonTarifaire())); // on ne veut pas la dernière tranche
                    })
                    .forEach(enr -> cons.tranche(a10francsPres(enr.getRevenuImposable()),a10francsPres(enr.getMontantImposableMax()),enr.getTaux()));

        }
        EnregistrementBareme dernier = enreg.get(enreg.size()-1);
        cons.derniereTranche(a10francsPres(dernier.getRevenuImposable()),dernier.getTaux());
        return cons.construire();
    }

    private boolean fermeAGauche(EnregistrementBareme enreg) {
        BigDecimal montantDebutTranche = enreg.getRevenuImposable();
        return 0 == DIZAINE_LA_PLUS_PROCHE.arrondir(montantDebutTranche).compareTo(montantDebutTranche);
    }

    @Override
    public BaremeParTranche obtenirBaremeMensuel(int annee, String codeCanton, ICodeTarifaire code) {
//        if ("ge".equalsIgnoreCase(codeCanton) && 2024 > annee) {
//            return obtenirBaremeMensuelGEavant2024(annee,code);
//        } else {
//            // À partir de 2024, Genève fait comme les autres !!
            List<EnregistrementBareme> enreg = rechercherTranches(annee, codeCanton, code);
        EnregistrementBareme premierEnregistrement = enreg.get(0);
        EnregistrementBareme deuxiemeEnregistrement = enreg.size() > 1 ? enreg.get(1) : null;
        EnregistrementBareme dernierEnregistrement = enreg.get(enreg.size()-1);
            // Construction du barème
        final ConstructeurBareme cons = unBaremeATauxEffectifSansOptimisationDesQueNonNul()
                .typeArrondiSurChaqueTranche(VINGTIEME_LE_PLUS_PROCHE);


        if (deuxiemeEnregistrement != null && fermeAGauche(deuxiemeEnregistrement)) cons.fermeAGauche();
        if (1 < enreg.size()) {
                // 1ère tranche
                cons.premiereTranche(a10francsPres(premierEnregistrement.getMontantImposableMax()),premierEnregistrement.getTaux());
                enreg.stream()
                        .filter(enr -> { return !premierEnregistrement.equals(enr) && !dernierEnregistrement.equals(enr); })
                        .forEach(enr -> cons.tranche(a10francsPres(enr.getRevenuImposable()),a10francsPres(enr.getMontantImposableMax()),enr.getTaux()));
            }
            cons.derniereTranche(a10francsPres(dernierEnregistrement.getRevenuImposable()),dernierEnregistrement.getTaux());
            return cons.construire();
    }

    private boolean fichierExistant(int annee, String codeCanton) {
        return creerLecteurSalaire(annee,codeCanton).isPresent();
    }

    private Set<BaremeDisponible> baremeDisponible(String codeCanton) {
        int anneeProchaine = Calendar.getInstance().get(Calendar.YEAR)+1;
        return IntStream.iterate(2000, n -> n <= anneeProchaine, n -> n+1)
                .filter(annee -> fichierExistant(annee,codeCanton))
                .mapToObj(n -> new BaremeDisponibleImpl(n, codeCanton))
                .collect(Collectors.toUnmodifiableSet());
    }

    private void ajouter(Set<BaremeDisponible> sourcesBareme, String codeCanton) {
        sourcesBareme.addAll(baremeDisponible(codeCanton));
    }

    @Override
    public Set<BaremeDisponible> baremeDisponible() {
        return Stream.of("ag", "ai", "ar", "be", "bl", "bs", "fr", "ge", "gl", "gr", "ju", "lu", "ne",
                "nw", "ow", "sg", "sh", "so", "sz", "tg", "ti", "ur", "vd", "vs", "zg", "zh").flatMap(code -> baremeDisponible(code).stream())
                .collect(Collectors.toUnmodifiableSet());
    }

}
