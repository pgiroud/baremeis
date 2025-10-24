package ch.ge.afc.baremeis.service.dao.ifdch;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;
import static ch.ge.afc.baremeis.service.dao.ifdch.LecteurFichierTexteIFD.unLecteurDepuisClasspath;

public class LecteurFichierTexteIFDTest {

    @Test
    public void lireDefinitionBareme() {
        List<DescriptionBareme> descriptions = new ArrayList<>();
        EnregistrementCallback callback = new EnregistrementCallback() {
            @Override
            public void lireTrancheEtValeur(TrancheBareme tr) {
                // Ne fait rien
            }

            @Override
            public void lireDefinitionBareme(DescriptionBareme bar) {
                descriptions.add(bar);
            }
        };
        LecteurFichierTexteIFD lecteur = unLecteurDepuisClasspath("ch/2014/H.xz").orElseThrow();
        try {
            lecteur.lire(callback);
        } catch (IOException ioe) {
            fail("Exception de lecture : " + ioe.getLocalizedMessage());
        }
        assertThat(descriptions).hasSize(1); // Il n’y a qu’une ligne contenant la description de 10 barèmes au plus
    }

    @Test
    public void lireNombreDefiniDeTranchesBareme() {
        List<TrancheBareme> descriptions = new ArrayList<>();
        EnregistrementCallback callback = new LectureTranche(100,descriptions);
        LecteurFichierTexteIFD lecteur = unLecteurDepuisClasspath("ch/2014/B.xz").orElseThrow();
        try {
            lecteur.lire(callback);
        } catch (IOException ioe) {
            fail("Exception de lecture : " + ioe.getLocalizedMessage());
        }
        assertThat(descriptions).hasSize(100);
    }


    public static class LectureTranche implements EnregistrementCallback {
        private final int nbTrancheALire;
        private final List<TrancheBareme> tranches;
        private int compteur = 0;

        public LectureTranche(int nbTrancheALire, List<TrancheBareme> descriptions) {
            this.nbTrancheALire = nbTrancheALire;
            this.tranches = descriptions;
        }

        @Override
        public void lireTrancheEtValeur(TrancheBareme tr) {
            if (compteur < nbTrancheALire) {
                tranches.add(tr);
            }
            compteur++;
        }

        @Override
        public void lireDefinitionBareme(DescriptionBareme bar) {

        }
    }
}
