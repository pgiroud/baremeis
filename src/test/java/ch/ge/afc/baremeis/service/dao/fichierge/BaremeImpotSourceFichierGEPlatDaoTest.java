package ch.ge.afc.baremeis.service.dao.fichierge;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import ch.ge.afc.bareme.Bareme;
import ch.ge.afc.baremeis.service.ICodeTarifaire;
import ch.ge.afc.baremeis.service.dao.BaremeImpotSourceDao;

public class BaremeImpotSourceFichierGEPlatDaoTest {

	@Before
	public void setUp() throws Exception {
	}

	
	@Test
	public void baremes() {
		BaremeImpotSourceDao dao = new BaremeImpotSourceFichierGEPlatDao();
		Set<ICodeTarifaire> codes = dao.rechercherBareme(2009,"GE");
		assertTrue("Nbre code > 10", 10 < codes.size());
	}
	
	@Test
	public void baremeB5() {
		BaremeImpotSourceDao dao = new BaremeImpotSourceFichierGEPlatDao();
		Bareme bareme = dao.obtenirBaremeMensuel(2009, "GE", CodeTarifaireGE.B5);
		BigDecimal impot = bareme.calcul(new BigDecimal("10000"));
		assertEquals("Impôt barème B5 Ge 2009 pour 10'000.-- de revenu mensuel",new BigDecimal("1020.00"),impot);
	}
	
	
}
