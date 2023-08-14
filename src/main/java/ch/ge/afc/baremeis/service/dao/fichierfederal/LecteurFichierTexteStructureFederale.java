/**
 *
 */
package ch.ge.afc.baremeis.service.dao.fichierfederal;

import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Optional;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;
import org.impotch.util.TypeArrondi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



import ch.ge.afc.baremeis.service.Sexe;


/**
 * Attention, non thread safe !!
 *
 * @author <a href="mailto:patrick.giroud@etat.ge.ch">Patrick Giroud</a>
 */
public class LecteurFichierTexteStructureFederale {

    public static Optional<LecteurFichierTexteStructureFederale> unLecteurDepuisClasspath(String fichierAvecCheminComplet, String charsetName) {
        LecteurFichierTexteStructureFederale lecteur = new LecteurFichierTexteStructureFederale(fichierAvecCheminComplet,charsetName);
        return lecteur.exist() ? Optional.of(lecteur) : Optional.empty();
    }


    /**************************************************/
    /****************** Attributs *********************/
    /**************************************************/
    private static BigDecimal UN_CENTIME = new BigDecimal("0.01");

    final Logger logger = LoggerFactory.getLogger(LecteurFichierTexteStructureFederale.class);
    private final String fichierDansClasspathAvecCheminComplet;
    private final String charsetName;
    private DateFormat dateFmt;

    /**************************************************/
    /**************** Constructeurs *******************/
    /**************************************************/

    private LecteurFichierTexteStructureFederale(String fichierAvecCheminComplet, String charset) {
        super();
        dateFmt = new SimpleDateFormat("yyyyMMdd");
        dateFmt.setTimeZone(TimeZone.getTimeZone("Europe/Zurich"));
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
            cl = LecteurFichierTexteStructureFederale.class.getClassLoader();
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

    boolean exist() {
        try {
            ClassLoader cl = getClassLoader();
            InputStream is = (cl != null ? cl.getResourceAsStream(fichierDansClasspathAvecCheminComplet) : ClassLoader.getSystemResourceAsStream(fichierDansClasspathAvecCheminComplet));
            return null != is && new BufferedReader(new InputStreamReader(is,charsetName)).ready();
        } catch (IOException ioe) {
            logger.debug("Pas de lecture possible dans 'classpath:" + fichierDansClasspathAvecCheminComplet + "'",ioe);
        }
        return false;
    }

    private String sousChaine(String chaine, int posDebut, int posFin) {
        return chaine.substring(posDebut - 1, posFin);
    }

    private BigDecimal montantAvecCentime(String chaine, int posDebut, int posFin) {
        String montant = sousChaine(chaine, posDebut, posFin);
        if (montant.length() < 3) {
            logger.error("Erreur lors du parsing d'un montant avec centimes '" + montant + "'");
            throw new IllegalArgumentException("Impossible de traiter un montant si petit '" + montant + "' !!");
        }
        return new BigDecimal(montant.substring(0, montant.length() - 2) + "." + montant.substring(montant.length() - 2));
    }

    private BigDecimal montantSansCentime(String chaine, int posDebut, int posFin) {
        String montant = sousChaine(chaine, posDebut, posFin);
        if (montant.length() < 3) {
            logger.error("Erreur lors du parsing d'un montant avec centimes '" + montant + "'");
            throw new IllegalArgumentException("Impossible de traiter un montant si petit '" + montant + "' !!");
        }
        return new BigDecimal(montant.substring(0, montant.length() - 2) + ".00");
    }

    private Date date(String chaine, int posDebut, int posFin) throws ParseException {
        String date = sousChaine(chaine, posDebut, posFin);
        return dateFmt.parse(date);
    }

    private int entier(String chaine, int posDebut, int posFin) {
        String entier = sousChaine(chaine, posDebut, posFin).trim();
        return Integer.valueOf(entier);
    }

    private void traiterLigneEnregInitial(EnregistrementCallback callback, String ligne, int numeroLigne) throws ParseException {
        String codeCanton = sousChaine(ligne, 3, 4);
        Date dateCreation = date(ligne, 20, 27);
        callback.lectureEnregistrementInitial(codeCanton, dateCreation);
    }

    private void traiterLigneEnregFinal(EnregistrementCallback callback, String ligne, int numeroLigne) {
        String codeCanton = sousChaine(ligne, 18, 19);
        int nbreEnregistrement = entier(ligne, 20, 27);
        callback.lectureEnregistrementFinal(codeCanton, nbreEnregistrement);
    }

    private void traiterCodeTarifaire(String codeTarifaire, EnregistrementBareme enreg) {
        enreg.setCodeTarifaire(new CodeTarifaire(codeTarifaire));
    }

    private BigDecimal tauxEnPourcent(String taux) {
        return new BigDecimal(taux.substring(0, 1) + "." + taux.substring(1, 5));
    }


    /**
     * barèmes progressifs de l’impôt à la source
     * pour le revenu d’une activité lucrative salariée
     *
     * @param callback
     * @param ligne
     * @param numeroLigne
     * @throws ParseException
     */
    private void traiterLigneSalarie(EnregistrementCallback callback, String ligne, int numeroLigne) throws ParseException {
        EnregistrementBareme enreg = new EnregistrementBareme();
        enreg.setGenre(GenreTransaction.getParCode(sousChaine(ligne, 3, 4)));
        enreg.setCodeCanton(sousChaine(ligne, 5, 6));
        String codeTarifaire = sousChaine(ligne, 7, 16);
        traiterCodeTarifaire(codeTarifaire, enreg);
        enreg.setDateInitialeValidite(date(ligne, 17, 24));
        BigDecimal revenuImposable = montantAvecCentime(ligne, 25, 33);
        // Attention, certain commence les tranches avec 5 cts de plus et d'autres avec 1 franc de plus
        // Il aurait été beaucoup plus malin de mettre les montants en fin de tranche !!
        revenuImposable = TypeArrondi.UNITE_INF.arrondirMontant(revenuImposable.subtract(UN_CENTIME));
        enreg.setRevenuImposable(revenuImposable);
        enreg.setEchelonTarifaire(montantAvecCentime(ligne, 34, 42));
        enreg.setSexe(Sexe.getParCode(ligne.charAt(44)));
        enreg.setNbreEnfant(entier(ligne, 44, 45));
        enreg.setMontantImpot(montantAvecCentime(ligne, 46, 54));
        enreg.setTaux(tauxEnPourcent(sousChaine(ligne, 55, 59)));
        callback.lectureEnregistrement(enreg);
    }

    /**
     * barèmes pour administrateurs d’après
     * l’art. 93 LIFD et pour participations de collaborateur d’après l’art. 97a LIFD
     *
     * @param callback
     * @param ligne
     * @param numeroLigne
     * @throws ParseException
     */
    private void traiterLigneAdministrateur(EnregistrementCallback callback, String ligne, int numeroLigne) throws ParseException {
    }

    /**
     * @param callback
     * @param ligne
     * @param numeroLigne
     * @throws ParseException
     */
    private void traiterLigneCommissionPerception(EnregistrementCallback callback, String ligne, int numeroLigne) throws ParseException {
    }

    private void traiterLigne(EnregistrementCallback callback, String ligne, int numeroLigne) throws ParseException {
        // Lecture du genre d'enregistrement
        String genreTransaction = ligne.substring(0, 2);
        if ("06".equals(genreTransaction)) {
            traiterLigneSalarie(callback, ligne, numeroLigne);
        } else if ("11".equals(genreTransaction)) {
            logger.debug("Le fichier 'classpath:" + fichierDansClasspathAvecCheminComplet + "' comprend une ligne administrateur ! Ce genre de ligne n'est pas prévu.");
            traiterLigneAdministrateur(callback, ligne, numeroLigne);
        } else if ("12".equals(genreTransaction)) {
            logger.debug("Le fichier 'classpath:" + fichierDansClasspathAvecCheminComplet + "' comprend une ligne commission de perception ! Ce genre de ligne n'est pas prévu.");
            traiterLigneCommissionPerception(callback, ligne, numeroLigne);
        } else if ("00".equals(genreTransaction)) {
            traiterLigneEnregInitial(callback, ligne, numeroLigne);
        } else if ("99".equals(genreTransaction)) {
            traiterLigneEnregFinal(callback, ligne, numeroLigne);
        }
    }

    private InputStream getInputStream() throws IOException {
        ClassLoader cl = getClassLoader();
        URL resource = cl.getResource(fichierDansClasspathAvecCheminComplet);
        InputStream stream = (cl != null ? cl.getResourceAsStream(fichierDansClasspathAvecCheminComplet) : ClassLoader.getSystemResourceAsStream(fichierDansClasspathAvecCheminComplet));

        if (fichierDansClasspathAvecCheminComplet.endsWith("zip")) {
            ZipFile fichierArchive = new ZipFile(resource.getFile());
            String nomFichier = fichierDansClasspathAvecCheminComplet.replace("zip", "txt");
            for (Enumeration<? extends ZipEntry> entrees = fichierArchive.entries(); entrees.hasMoreElements(); ) {
                ZipEntry entree = entrees.nextElement();
                if (nomFichier.endsWith(entree.getName())) {
                    return fichierArchive.getInputStream(entree);
                }
            }
            throw new RuntimeException("Il n'existe pas de fichier " + nomFichier + " dans l'archive 'classpath:" + fichierDansClasspathAvecCheminComplet + "'");
        } else if (fichierDansClasspathAvecCheminComplet.endsWith("xz") || fichierDansClasspathAvecCheminComplet.endsWith("XZ")) {
            return new XZCompressorInputStream(new BufferedInputStream(stream));
        }
        return stream;
    }


    public void lire(EnregistrementCallback callback) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(getInputStream(), charsetName));
        int numLigne = 1;
        String ligne = reader.readLine();
        while (null != ligne) {
            try {
                traiterLigne(callback, ligne, numLigne);
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
