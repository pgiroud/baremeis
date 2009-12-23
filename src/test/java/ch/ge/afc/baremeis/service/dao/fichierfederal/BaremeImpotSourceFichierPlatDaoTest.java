package ch.ge.afc.baremeis.service.dao.fichierfederal;


import java.math.BigDecimal;
import java.util.Set;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.EmptyResultDataAccessException;

import ch.ge.afc.bareme.Bareme;
import ch.ge.afc.baremeis.service.ICodeTarifaire;
import ch.ge.afc.baremeis.service.dao.BaremeImpotSourceDao;

public class BaremeImpotSourceFichierPlatDaoTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test(expected=EmptyResultDataAccessException.class)
	public void baremeCantonBizarre() {
		BaremeImpotSourceDao dao = new BaremeImpotSourceFichierPlatDao();
		dao.rechercherBareme(2009,"lulu");
	}
	
	@Test
	public void baremeVaudois() {
		BaremeImpotSourceDao dao = new BaremeImpotSourceFichierPlatDao();
		Set<ICodeTarifaire> codes = dao.rechercherBareme(2009,"VD");
		assertTrue("Nbre code > 10", 10 < codes.size());
		// Barème revenu accessoire à 10 %
		Bareme bareme = dao.obtenirBaremeMensuel(2009, "VD", new CodeTarifaire("D0+"));
		BigDecimal impot = bareme.calcul(new BigDecimal("100000"));
		assertEquals("Impôt barème D Vaud pour 100'000.-- ",new BigDecimal("10000.00"),impot);
	}
	
	
	
	@Test
	public void trancheFribourgeoise() {
		BaremeImpotSourceDao dao = new BaremeImpotSourceFichierPlatDao();
		CodeTarifaire code = new CodeTarifaire("B2+");
		Bareme bareme = dao.obtenirBaremeMensuel(2009, "FR", code);
		BigDecimal impot = bareme.calcul(new BigDecimal(10000));
		assertEquals("Montant impôt mensuel pour 10'000 francs de revenu, barème B2",new BigDecimal("1057.00"),impot);
		
	}
	
//	@Test
//	public void trancheNeuchateloise() {
//		
//		BaremeImpotSourceDao dao = new BaremeImpotSourceFichierPlatDao();
//		Set<ICodeTarifaire> codes = dao.rechercherBareme(2009, "NE");
//		codes.remove(new CodeTarifaire("D0"));
//		for (ICodeTarifaire code : codes) {
//			dao.obtenirBaremeMensuel(2009, "NE", code);
//		}
//	}
}
