/**
 * 
 */
package ch.ge.afc.baremeis.ge.icc;


import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ch.ge.afc.baremeis.CalculateurImpotSource;
import ch.ge.afc.baremeis.FournisseurCalculateurIS;
import ch.ge.afc.calcul.impot.cantonal.ge.pp.ConstructeurSituationFamiliale;
import ch.ge.afc.calcul.impot.taxation.pp.SituationFamiliale;


/**
 * @author <a href="mailto:patrick.giroud@etat.ge.ch">Patrick Giroud</a>
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/beansis.xml")
public class CalculateurImpotSourceICC2010Test {

	final Logger logger = LoggerFactory.getLogger(CalculateurImpotSourceICC2010Test.class);

	@Resource(name = "fournisseurCalculateurISGeneve")
	private FournisseurCalculateurIS fournisseur;
	private ConstructeurSituationFamiliale constructeurSituation = new ConstructeurSituationFamiliale();
	private CalculateurImpotSource calculateur;

	@Before
	public void setUp() throws Exception {
		calculateur = fournisseur.getCalculateurImpotSourceICC(2010);
	}

	private boolean presqueEgal(BigDecimal montantAttendu, BigDecimal montantCalcule) {
		BigDecimal tolerance = new BigDecimal("0.05");
		return 0 < tolerance.compareTo(montantAttendu.subtract(montantCalcule).abs());
	}
	
	private void test(SituationFamiliale situation, String codeBareme, String revenuBrut, String montantAttendu) {
	    BigDecimal montantCalcule = calculateur.calcul(situation, new BigDecimal(revenuBrut));
		assertTrue("Barème " + codeBareme + " avec " + revenuBrut+ " francs de revenu brut attendu " + montantAttendu + ", calculé " + montantCalcule, presqueEgal(new BigDecimal(montantAttendu),montantCalcule));
	}
	
	@Test
	public void testA() {
		// Barème A
		SituationFamiliale situation = constructeurSituation.creerCelibataireSansCharge();
		test(situation,"A","300000","66253.50");
	}
	
	@Test
	public void testB0() {
		// Barème B0
		SituationFamiliale situation = constructeurSituation.creerCoupleSansCharge();
		test(situation,"B0","120000","11826.25");
		test(situation,"B0","134700","15264.65");
	}
	
	@Test
	public void testB1() {
		// Barème B1
		SituationFamiliale situation = constructeurSituation.creerCoupleAvecEnfant(1);
		test(situation,"B1","131100","11726.15"); 
		test(situation,"B1","200100","28109.50"); 
	}

	@Test
	public void testB2() {
		// Barème B2
		SituationFamiliale situation = constructeurSituation.creerCoupleAvecEnfant(2);
		test(situation,"B2","132900","9535.55"); 
		test(situation,"B2","254400","38601.41");
		test(situation,"B2","400800","75143.00");
	}
	
	@Test
	public void testB3() {
		// Barème B3
		SituationFamiliale situation = constructeurSituation.creerCoupleAvecEnfant(3);
		test(situation,"B3","117300","4019.15");
	}
	
	@Test
	public void testB5() {
		// Barème B5
		SituationFamiliale situation = constructeurSituation.creerCoupleAvecEnfant(5);
		test(situation,"B5","565200","109719.05");
	}
	
}
