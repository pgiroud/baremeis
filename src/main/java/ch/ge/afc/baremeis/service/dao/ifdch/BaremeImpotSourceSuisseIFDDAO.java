package ch.ge.afc.baremeis.service.dao.ifdch;

import ch.ge.afc.baremeis.service.BaremeDisponible;
import ch.ge.afc.baremeis.service.ICodeTarifaire;
import ch.ge.afc.baremeis.service.dao.BaremeImpotSourceIFDDao;
import ch.ge.afc.baremeis.service.dao.CodeTarifaire;
import org.impotch.bareme.BaremeParTranche;
import org.impotch.bareme.ConstructeurBareme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static ch.ge.afc.baremeis.service.dao.ifdch.LecteurFichierTexteIFD.unLecteurDepuisClasspath;

public class BaremeImpotSourceSuisseIFDDAO implements BaremeImpotSourceIFDDao {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    private String getNomFichier(String typeBareme, int annee, String extension) {
        StringBuilder builder = new StringBuilder("ch/");
        builder.append(annee).append("/").append(typeBareme)
                .append(".").append(extension);
        return builder.toString();
    }


    private Optional<LecteurFichierTexteIFD> creerLecteur(String extension, String typeBareme, int annee) {
        return unLecteurDepuisClasspath(getNomFichier(typeBareme, annee, extension));
    }

    private Optional<LecteurFichierTexteIFD> creerLecteur(String typeBareme, int annee) {
        Stream<String> extensions = Stream.of("txt", "xz", "zip");
        Optional<LecteurFichierTexteIFD> lecteur = extensions.map(ex -> creerLecteur(ex, typeBareme, annee)).filter(Optional::isPresent)
                .map(Optional::get).findFirst();
        if (lecteur.isPresent()) {
            String nomFichier = lecteur.get().getFichierDansClasspathAvecCheminComplet();
            logger.atDebug().setMessage("Le fichier {} a été trouvé dans le classpath.").addArgument(nomFichier).log();
            return lecteur;
        } else {
            logger.atInfo().setMessage("Pas de fichier comprenant uniquement l’IFD pour l'année {}")
                    .addArgument(annee).log();
            return Optional.empty();
        }
    }

    @Override
    public Set<ICodeTarifaire> rechercherCodesTarifaires(int annee) {
        CodeTarifaireCallback callback = new CodeTarifaireCallback();
        Stream<String> typeBareme = Stream.of("A", "B", "C","H");
        typeBareme.map(tb -> this.creerLecteur(tb, annee)).filter(Optional::isPresent)
                .map(Optional::get).forEach(l -> {
                    try {
                        l.lire(callback);
                    } catch (IOException e) {
                        String message = "Exception de lecture I/O dans le fichier ";
                        logger.error(message);
                        throw new RuntimeException("message", e);
                    }
                });
        return callback.getCodes();
    }


    private boolean fermeAGauche(long finTranche) {
        return finTranche % 10 != 0;
    }

    @Override
    public BaremeParTranche obtenirBaremeMensuel(int annee, ICodeTarifaire code) {
        TrancheBaremeCallback cb
                = new TrancheBaremeCallback(Integer.parseInt(code.getCode().substring(1).trim()));
        this.creerLecteur(code.getCode().substring(0,1), annee)
                .ifPresent(l -> {
                    try {
                        l.lire(cb);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
        List<EnregistrementBareme> liste = cb.getListe();
        boolean fermeAGauche = fermeAGauche(liste.get(0).getA());
        long borneSupDerniereTranche = liste.get(liste.size()-1).getA();

        ConstructeurBareme cons = fermeAGauche ?
                ConstructeurBareme.unBareme().fermeAGauche() :
                ConstructeurBareme.unBareme(); // Par défaut, c’est ferme à droite
        if (1 < liste.size()) {
            // 1ère tranche
            EnregistrementBareme premierEnregistrement = liste.getFirst();
            cons.premiereTranche(
                    BigDecimal.valueOf(fermeAGauche ? premierEnregistrement.getA()+1 : premierEnregistrement.getA()),
                    premierEnregistrement.getTaux());
            liste.stream()
                    .filter(enr -> {
                        return enr.getDe() > 1 // on ne veut pas la première tranche
                                &&
                                enr.getA() < borneSupDerniereTranche; // on ne veut pas la dernière tranche
                    })
                    .forEach(enr -> cons.tranche(
                            BigDecimal.valueOf(fermeAGauche ? enr.getDe() : enr.getDe() -1),
                            BigDecimal.valueOf(fermeAGauche ? enr.getA()+1 : enr.getA()),
                            enr.getTaux()));

        }
        EnregistrementBareme dernier = liste.get(liste.size()-1);
        cons.derniereTranche(BigDecimal.valueOf(fermeAGauche ? dernier.getDe() : dernier.getDe() -1),dernier.getTaux());
        return cons.construire();
    }

    @Override
    public Set<BaremeDisponible> baremeDisponible() {
        return Set.of();
    }


    private static class CodeTarifaireCallback implements EnregistrementCallback {

        private final Set<ICodeTarifaire> codes = new TreeSet<ICodeTarifaire>();

        public Set<ICodeTarifaire> getCodes() {
            return Collections.unmodifiableSet(codes);
        }

        @Override
        public void lireTrancheEtValeur(TrancheBareme tr) {

        }

        @Override
        public void lireDefinitionBareme(DescriptionBareme bar) {
            final String type = String.valueOf(bar.getType());
            codes.addAll(IntStream.rangeClosed(bar.getNbChargeMin(),bar.getNbChargeMax()).mapToObj(n -> type + n)
                    .map(CodeTarifaire::new).collect(Collectors.toUnmodifiableSet()));
        }
    }

    private static class TrancheBaremeCallback implements EnregistrementCallback {

        private final List<EnregistrementBareme> enregistrementBaremeListe = new LinkedList<>();
        private final int nbCharge;
        private int index;

        public TrancheBaremeCallback(int nbCharge) {
            this.nbCharge = nbCharge;
        }

        public List<EnregistrementBareme> getListe() {
            return Collections.unmodifiableList(enregistrementBaremeListe);
        }

        @Override
        public void lireTrancheEtValeur(TrancheBareme tr) {
            enregistrementBaremeListe.add(new EnregistrementBareme(tr.inf(),tr.sup(),tr.valeur(index)));
        }

        @Override
        public void lireDefinitionBareme(DescriptionBareme bar) {
            if (bar.getNbChargeMin() <= nbCharge && nbCharge <= bar.getNbChargeMax()) {
                index = nbCharge - bar.getNbChargeMin();
            } else {
                throw new RuntimeException("Il n’y a pas de barème avec " + nbCharge + " charges !");
            }
        }
    }

    private static class EnregistrementBareme {
        private final long de;
        private final long a;
        private final BigDecimal taux;

        public EnregistrementBareme(long de, long a, BigDecimal taux) {
            this.de = de;
            this.a = a;
            this.taux = taux;
        }

        public long getDe() {
            return de;
        }

        public long getA() {
            return a;
        }

        public BigDecimal getTaux() {
            return taux;
        }
    }
}
