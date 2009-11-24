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
import ch.ge.afc.calcul.impot.cantonal.ge.pp.avant2010.ConstructeurSituationFamilialeGE;
import ch.ge.afc.calcul.impot.cantonal.ge.pp.avant2010.SituationFamilialeGE;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/beansis.xml")
public class CalculateurImpotSourceICC2008 {

	final Logger logger = LoggerFactory.getLogger(CalculateurImpotSourceICC2008.class);

	@Resource(name = "fournisseurCalculateurISGeneve")
	private FournisseurCalculateurIS fournisseur;
	private ConstructeurSituationFamilialeGE constructeurSituation = new ConstructeurSituationFamilialeGE();
	private CalculateurImpotSource calculateur;

	@Before
	public void setUp() throws Exception {
		calculateur = fournisseur.getCalculateurImpotSourceICC(2008);
	}

	private boolean presqueEgal(BigDecimal montantAttendu, BigDecimal montantCalcule) {
		BigDecimal tolerance = new BigDecimal("0.20");
		boolean presqueEgal = 0 < tolerance.compareTo(montantAttendu.subtract(montantCalcule).abs());
		if (!presqueEgal) logger.info("Montant attendu " + montantAttendu + ", montant calculé " + montantCalcule);
		return presqueEgal;
	}
	
	private void test(SituationFamilialeGE situation, String codeBareme, String revenuBrut, String montantAttendu) {
		assertTrue("Barème " + codeBareme + " avec " + revenuBrut+ " francs de revenu brut", presqueEgal(new BigDecimal(montantAttendu),calculateur.calcul(situation, new BigDecimal(revenuBrut))));
	}

	
	@Test
	public void testA0() {
		// Barème A0
		SituationFamilialeGE situation = constructeurSituation.creerCelibataireSansCharge();
		test(situation,"A0","200100","40615.34");
		test(situation,"A0","300000","67640.13");
	}
	
	@Test
	public void testB0() {
		// Barème B0
		SituationFamilialeGE situation = constructeurSituation.creerCoupleSansDoubleActiviteSansCharge();
		test(situation,"B0","100000","10798.88");
		test(situation,"B0","134700","18613.21");
	}
	
	@Test
	public void testB1() {
		// Barème B1
		SituationFamilialeGE situation = constructeurSituation.creerCoupleSansDoubleActiviteAvecEnfant(1, false, false);
		test(situation,"B1","131100","16204.63");
		test(situation,"B1","200100","33487.60");
	}

	@Test
	public void testB2() {
		// Barème B2
		SituationFamilialeGE situation = constructeurSituation.creerCoupleSansDoubleActiviteAvecEnfant(1, true, false);
		test(situation,"B2","132900","16373.25");
		test(situation,"B2","254400","47668.60");
		test(situation,"B2","400800","88622.05");
	}
	
	@Test
	public void testB8() {
		// Barème B8
		SituationFamilialeGE situation = constructeurSituation.creerCoupleSansDoubleActiviteAvecEnfant(3, true, false);
		test(situation,"B8","554400","129814.11");
	}
	
	@Test
	public void testB9() {
		// Barème B9
		SituationFamilialeGE situation = constructeurSituation.creerCoupleSansDoubleActiviteAvecEnfant(3, true, true);
		test(situation,"B9","117300","9136.17");
	}
	
	@Test
	public void testB10() {
		// Barème B10
		SituationFamilialeGE situation = constructeurSituation.creerCoupleSansDoubleActiviteAvecEnfant(4, false, false);
		test(situation,"B10","565200","131452.09");
	}
	
	@Test
	public void testB13() {
		// Barème B13
		logger.debug("*************** B13 ******************");
		SituationFamilialeGE situation = constructeurSituation.creerCoupleSansDoubleActiviteAvecEnfant(5, false, false);
		test(situation,"B13","348000","66736.51");
	}
	
	@Test
	public void testB14() {
		// Barème B14
		logger.debug("*************** B14 ******************");
		SituationFamilialeGE situation = constructeurSituation.creerCoupleSansDoubleActiviteAvecEnfant(5, true, false);
		test(situation,"B14","86700","0");
	}

	@Test
	public void testB15() {
		// Barème B15
		logger.debug("*************** B15 ******************");
		SituationFamilialeGE situation = constructeurSituation.creerCoupleSansDoubleActiviteAvecEnfant(5, true, true);
		test(situation,"B15","200100","25865.39");
	}

	@Test
	public void testI0() {
		// Barème I0
		logger.debug("*************** I0 ******************");
		SituationFamilialeGE situation = constructeurSituation.creerCoupleDontUnFonctionnaireInternational(0, false);
		test(situation,"I0","146700","27186.28");
		test(situation,"I0","315600","72227.28");
	}
	
	@Test
	public void testI1() {
		// Barème I1
		logger.debug("*************** I1 ******************");
		SituationFamilialeGE situation = constructeurSituation.creerCoupleDontUnFonctionnaireInternational(1, false);
		test(situation,"I1","45900","3591.63");
		test(situation,"I1","59100","6201.50");
		test(situation,"I1","69900","8516.89");
		test(situation,"I1","105300","16464.84");
		test(situation,"I1","120300","19959.27");
		test(situation,"I1","221700","45769.21");
		test(situation,"I1","302400","67750.26");
		test(situation,"I1","580800","148096.11");
	}
	
	@Test
	public void testI2() {
		// Barème I2
		logger.debug("*************** I2 ******************");
		SituationFamilialeGE situation = constructeurSituation.creerCoupleDontUnFonctionnaireInternational(1, true);
		test(situation,"I2","357600","82995.38");
		test(situation,"I2","511200","127365.31");
		
	}
	
	@Test
	public void testI4() {
		// Barème I4
		logger.debug("*************** I4 ******************");
		SituationFamilialeGE situation = constructeurSituation.creerCoupleDontUnFonctionnaireInternational(2, true);
		test(situation,"I4","71100","7690.30");		
	}

	@Test
	public void testI5() {
		// Barème I5
		logger.debug("*************** I5 ******************");
		SituationFamilialeGE situation = constructeurSituation.creerCoupleDontUnFonctionnaireInternational(3, false);
		test(situation,"I5","242400","49600.95");		
	}

	@Test
	public void testI6() {
		// Barème I6
		logger.debug("*************** I6 ******************");
		SituationFamilialeGE situation = constructeurSituation.creerCoupleDontUnFonctionnaireInternational(3, true);
		test(situation,"I6","95100","12172.65");		
		test(situation,"I6","295200","63746.97");		
	}

	@Test
	public void testI7() {
		// Barème I7
		logger.debug("*************** I7 ******************");
		SituationFamilialeGE situation = constructeurSituation.creerCoupleDontUnFonctionnaireInternational(4, false);
		test(situation,"I7","427200","100541.66");		
	}

	@Test
	public void testI8() {
		// Barème I8
		logger.debug("*************** I8 ******************");
		SituationFamilialeGE situation = constructeurSituation.creerCoupleDontUnFonctionnaireInternational(4, true);
		test(situation,"I8","145500","23236.92");		
		test(situation,"I8","226500","44149.05");		
	}
	
}
