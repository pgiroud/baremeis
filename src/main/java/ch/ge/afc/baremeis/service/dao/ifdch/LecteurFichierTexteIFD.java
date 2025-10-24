package ch.ge.afc.baremeis.service.dao.ifdch;

import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.text.ParseException;
import java.util.Enumeration;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class LecteurFichierTexteIFD {

    public static Optional<LecteurFichierTexteIFD> unLecteurDepuisClasspath(String fichierAvecCheminComplet) {
        LecteurFichierTexteIFD lecteur = new LecteurFichierTexteIFD(fichierAvecCheminComplet,"UTF-8");
        return lecteur.exist() ? Optional.of(lecteur) : Optional.empty();
    }

    final static Logger logger = LoggerFactory.getLogger(LecteurFichierTexteIFD.class);


    private final String fichierDansClasspathAvecCheminComplet;
    private final String charsetName;

    public LecteurFichierTexteIFD(String fichierDansClasspathAvecCheminComplet, String charsetName) {
        this.fichierDansClasspathAvecCheminComplet = fichierDansClasspathAvecCheminComplet;
        this.charsetName = charsetName;
    }

    public String getFichierDansClasspathAvecCheminComplet() {
        return fichierDansClasspathAvecCheminComplet;
    }

    private ClassLoader getClassLoader() {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        }
        catch (Throwable ex) {}
        if (cl == null) {
            cl = LecteurFichierTexteIFD.class.getClassLoader();
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


    private void traiterLigneDefinitionBareme(EnregistrementCallback callback, String ligne, int numeroLigne) throws ParseException {
        callback.lireDefinitionBareme(DescriptionBareme.description(ligne));
    }

    private void traiterLigneTranche(EnregistrementCallback callback, String ligne, int numeroLigne) throws ParseException {
        callback.lireTrancheEtValeur(TrancheBareme.tranche(ligne));
    }

    private void traiterLigne(EnregistrementCallback callback, String ligne, int numeroLigne) throws ParseException {
        if (ligne.startsWith("#")) return;
        if (ligne.startsWith("de") || ligne.startsWith("De")) traiterLigneDefinitionBareme(callback,ligne,numeroLigne);
        else traiterLigneTranche(callback,ligne,numeroLigne);
    }


    public void lire(EnregistrementCallback callback) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(getInputStream(), charsetName));
        int numLigne = 1;
        String ligne = reader.readLine();
        while (null != ligne) {
            try {
                // logger.debug();
                traiterLigne(callback, ligne, numLigne);
            } catch (ParseException | NumberFormatException ex) {
                throw new RuntimeException("Erreur de lecture dans la ressource 'classpath:" + fichierDansClasspathAvecCheminComplet + "' à la ligne " + numLigne,ex);
            }
            ligne = reader.readLine();
            numLigne++;
        }
        reader.close();
    }
}
