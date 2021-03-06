package ch.ge.afc.baremeis.service.dao.fichierge;


import static org.assertj.core.api.Fail.fail;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;


public class LecteurFichierGenevoisTest {

	@Test
	public void testSimpleLecture() {
		LecteurFichierTexteStructureGenevoise lecteur = new LecteurFichierTexteStructureGenevoise();
		lecteur.setCharsetName("ISO-8859-1");
		lecteur.setFichier(new ClassPathResource("2009/ge/bar_is2009_ascii.txt"));
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
