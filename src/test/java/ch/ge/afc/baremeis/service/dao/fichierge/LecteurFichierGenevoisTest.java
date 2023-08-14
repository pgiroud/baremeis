package ch.ge.afc.baremeis.service.dao.fichierge;

import java.io.IOException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Fail.fail;
import static ch.ge.afc.baremeis.service.dao.fichierge.LecteurFichierTexteStructureGenevoise.unLecteurDepuisClasspath;

public class LecteurFichierGenevoisTest {

	@Test
	public void testSimpleLecture() {
		LecteurFichierTexteStructureGenevoise lecteur =
			unLecteurDepuisClasspath("2009/ge/bar_is2009_ascii.txt","ISO-8859-1").orElseThrow();

		PasAction callback = new PasAction();
		try {
			lecteur.lire(callback);
		} catch (IOException ioe) {
			fail("Exception de lecture : " + ioe.getLocalizedMessage());
		}
	}
	
	private class PasAction implements EnregistrementGECallback {

		@Override
		public void traiterLigne(LigneEnregistrement ligne) {
			
		}
		
	}
}
