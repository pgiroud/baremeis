package ch.ge.afc.baremeis.service;


import java.math.BigDecimal;

import javax.annotation.Resource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ch.ge.afc.bareme.Bareme;
import ch.ge.afc.util.BigDecimalUtil;
import ch.ge.afc.util.TypeArrondi;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/beansis.xml")
public class ServiceBaremeImpotSourceTest {

	@Resource(name = "serviceBareme")
	private ServiceBaremeImpotSource service;
	
	private BigDecimal obtenirImpot(int revenu, String taux) {
		return TypeArrondi.CINQ_CTS.arrondirMontant(new BigDecimal(revenu).multiply(BigDecimalUtil.parseTaux(taux)));
	}
	
	private void test(Bareme bareme, int revenu, String taux) {
		BigDecimal impotAttendu = obtenirImpot(revenu,taux);
		BigDecimal impotCalcule = bareme.calcul(new BigDecimal(revenu));
		assertEquals("Pour " + revenu + " francs",impotAttendu,impotCalcule);
		
	}
	
	@Test
	public void testFribourg() {
		// Barème B1+
		Bareme bareme = service.obtenirBaremeMensuel(2009, "fr", "B1+");
		test(bareme,15000,"17.28 %");
		test(bareme,15010,"17.33 %");
		bareme = service.obtenirBaremeMensuel(2009, "fr", "B1");
		test(bareme,15000,"17.28 %");
		test(bareme,15010,"17.33 %");
	}
	
	@Test
	public void testGeneve() {
		Bareme bareme = service.obtenirBaremeMensuel(2010, "ge", "B2+");
		test(bareme,9000,"4.80 %");
		bareme = service.obtenirBaremeMensuel(2010, "ge", "B2");
		test(bareme,9000,"4.80 %");
	}
	
	@Test
	public void testPrioriteBareme() {
		Bareme baremeB = service.obtenirBaremeMensuel(2009, "ag", "B");
		Bareme baremeB0plus = service.obtenirBaremeMensuel(2009, "ag", "B0+");
		assertTrue(baremeB.equals(baremeB0plus));
		Bareme baremeB0 = service.obtenirBaremeMensuel(2009, "ag", "B0");
		assertFalse(baremeB0.equals(baremeB0plus));
	}
}
