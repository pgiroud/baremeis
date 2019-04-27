package ch.ge.afc.baremeis.service.dao.fichierfederal;


import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import static org.assertj.core.api.Assertions.fail;

public class LecteurFichierFederalTest {

    final Logger logger = LoggerFactory.getLogger(LecteurFichierFederalTest.class);


    @BeforeEach
    public void setUp() throws Exception {
    }

    @Test
    public void testSimpleLecture() {
        LecteurFichierTexteStructureFederale lecteur = new LecteurFichierTexteStructureFederale();
        lecteur.setCharsetName("ISO-8859-1");
        lecteur.setFichier(new ClassPathResource("2009/tar09fr.txt"));
        PasAction callback = new PasAction();
        try {
            lecteur.lire(callback);
        } catch (IOException ioe) {
            fail("Exception de lecture : " + ioe.getLocalizedMessage());
        }
    }

    @Test
    public void testLectureDiversCodeTarifaire() {
        LecteurFichierTexteStructureFederale lecteur = new LecteurFichierTexteStructureFederale();
        lecteur.setCharsetName("ISO-8859-1");
        lecteur.setFichier(new ClassPathResource("2009/tar09gr.txt"));

        final Map<String, Set<String>> codesParCanton = new HashMap<String, Set<String>>();
        EnregistrementCallback callback = new PasAction() {
            @Override
            public void lectureEnregistrement(EnregistrementBareme enreg) {
                String codeCanton = enreg.getCodeCanton();
                if (!codesParCanton.containsKey(codeCanton)) {
                    codesParCanton.put(codeCanton, new HashSet<String>());
                }
                Set<String> codes = codesParCanton.get(enreg.getCodeCanton());
                codes.add(enreg.getCodeTarifaire().getCode());
            }
        };

        ClassPathResource resource = new ClassPathResource("2009/tar09gr.txt");
        try {
            for (String filename : resource.getFile().getParentFile().list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.length() > 3 && "tar".equals(name.substring(0, 3));
                }

            })) {
                logger.info("Parsing de " + filename);
                lecteur.setFichier(new ClassPathResource("2009/" + filename));
                lecteur.lire(callback);
            }
        } catch (IOException ioe) {
            fail("Exception de lecture : " + ioe.getLocalizedMessage());
        }
    }

    @Test
    public void lireFichierZip() {
        LecteurFichierTexteStructureFederale lecteur = new LecteurFichierTexteStructureFederale();
        lecteur.setCharsetName("ISO-8859-1");
        lecteur.setFichier(new ClassPathResource("2010/tar10vd.zip"));
        EnregistrementCallback callback = new PasAction();
        try {
            lecteur.lire(callback);
        } catch (IOException e) {
            fail("Exception de lecture : " + e.getLocalizedMessage());
        }
    }


    @Test
    public void lireFichierXz() {
        LecteurFichierTexteStructureFederale lecteur = new LecteurFichierTexteStructureFederale();
        lecteur.setCharsetName("ISO-8859-1");
        lecteur.setFichier(new ClassPathResource("2010/tar10vd.xz"));
        EnregistrementCallback callback = new PasAction();
        try {
            lecteur.lire(callback);
        } catch (IOException e) {
            fail("Exception de lecture : " + e.getLocalizedMessage());
        }
    }


    private class PasAction implements EnregistrementCallback {
        @Override
        public void lectureEnregistrement(EnregistrementBareme enreg) {
        }

        @Override
        public void lectureEnregistrementFinal(String codeCanton,
                                               int nbreEnregistrement) {
        }

        @Override
        public void lectureEnregistrementInitial(String codeCanton,
                                                 Date dateCreation) {
        }

    }
}
