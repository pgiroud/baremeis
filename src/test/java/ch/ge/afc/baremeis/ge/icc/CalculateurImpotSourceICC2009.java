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

import ch.ge.afc.baremeis.FournisseurCalculateurIS;
import ch.ge.afc.calcul.impot.cantonal.ge.pp.avant2010.ConstructeurSituationFamilialeGE;
import ch.ge.afc.calcul.impot.cantonal.ge.pp.avant2010.SituationFamilialeGE;
import ch.ge.afc.baremeis.CalculateurImpotSource;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/beansis.xml")
public class CalculateurImpotSourceICC2009 {

	final Logger logger = LoggerFactory.getLogger(CalculateurImpotSourceICC2009.class);

	@Resource(name = "fournisseurCalculateurISGeneve")
	private FournisseurCalculateurIS fournisseur;
	private ConstructeurSituationFamilialeGE constructeurSituation = new ConstructeurSituationFamilialeGE();
	private CalculateurImpotSource calculateur;

	@Before
	public void setUp() throws Exception {
		calculateur = fournisseur.getCalculateurImpotSourceICC(2009);
	}

	private boolean presqueEgal(BigDecimal montantAttendu, BigDecimal montantCalcule) {
		BigDecimal tolerance = new BigDecimal("0.13");
		return 0 < tolerance.compareTo(montantAttendu.subtract(montantCalcule).abs());
	}
	
	private void test(SituationFamilialeGE situation, String codeBareme, String revenuBrut, String montantAttendu) {
		assertTrue("Barème " + codeBareme + " avec " + revenuBrut+ " francs de revenu brut", presqueEgal(new BigDecimal(montantAttendu),calculateur.calcul(situation, new BigDecimal(revenuBrut))));
	}
	
	@Test
	public void testA0() {
		// Barème A0
		SituationFamilialeGE situation = constructeurSituation.creerCelibataireSansCharge();
		test(situation,"A0","200100","40175.59");
		test(situation,"A0","300000","67038.90");
	}
	
	@Test
	public void testB0() {
		// Barème B0
		SituationFamilialeGE situation = constructeurSituation.creerCoupleSansDoubleActiviteSansCharge();
		test(situation,"B0","120000","14746.74");
		test(situation,"B0","134700","18142.16");
	}

	@Test
	public void testB1() {
		// Barème B1
		SituationFamilialeGE situation = constructeurSituation.creerCoupleSansDoubleActiviteAvecEnfant(1, false, false);
		test(situation,"B1","131100","15661.96");
		test(situation,"B1","200100","32816.36");
	}

	@Test
	public void testB2() {
		// Barème B2
		SituationFamilialeGE situation = constructeurSituation.creerCoupleSansDoubleActiviteAvecEnfant(1, true, false);
		test(situation,"B2","132900","15811.01");
		test(situation,"B2","254400","46891.07");
		test(situation,"B2","400800","87617.60");
		test(situation,"B2","124600","13880.10");
	}
	
	@Test
	public void testB8() {
		// Barème B8
		SituationFamilialeGE situation = constructeurSituation.creerCoupleSansDoubleActiviteAvecEnfant(3, true, false);
		test(situation,"B8","150000","16353.10");
	}
	
	@Test
	public void testB9() {
		// Barème B9
		SituationFamilialeGE situation = constructeurSituation.creerCoupleSansDoubleActiviteAvecEnfant(3, true, true);
		test(situation,"B9","117300","8423.58");
	}
	
	@Test
	public void testB10() {
		// Barème B10
		SituationFamilialeGE situation = constructeurSituation.creerCoupleSansDoubleActiviteAvecEnfant(4, false, false);
		test(situation,"B10","565200","129991.45");
	}
	
	@Test
	public void testB13() {
		// Barème B13
		logger.debug("*************** B13 ******************");
		SituationFamilialeGE situation = constructeurSituation.creerCoupleSansDoubleActiviteAvecEnfant(5, false, false);
		test(situation,"B13","348000","65486.81");
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
		test(situation,"B15","200100","24820.03");
	}

	@Test
	public void testI0() {
		// Barème I0
		logger.debug("*************** I0 ******************");
		SituationFamilialeGE situation = constructeurSituation.creerCoupleDontUnFonctionnaireInternational(0, false);
		test(situation,"I0","146700","26847.32");
		test(situation,"I0","315600","71616.83");
	}
	
	@Test
	public void testI1() {
		// Barème I1
		logger.debug("*************** I1 ******************");
		SituationFamilialeGE situation = constructeurSituation.creerCoupleDontUnFonctionnaireInternational(1, false);
		test(situation,"I1","45900","3374.18");
		test(situation,"I1","105300","16147.17");
		test(situation,"I1","69900","8245.77");
		test(situation,"I1","120300","19620.45");
		test(situation,"I1","221700","45269.98");
		test(situation,"I1","302400","67120.88");
		test(situation,"I1","580800","147080.90");
		test(situation,"I1","59100","5968.18");
	}
	

	@Test
	public void testI2() {
		// Barème I2
		logger.debug("*************** I2 ******************");
		SituationFamilialeGE situation = constructeurSituation.creerCoupleDontUnFonctionnaireInternational(1, true);
		test(situation,"I2","511200","126420.36");
		test(situation,"I2","357600","82264.72");
		
	}
	
	@Test
	public void testI4() {
		// Barème I4
		logger.debug("*************** I4 ******************");
		SituationFamilialeGE situation = constructeurSituation.creerCoupleDontUnFonctionnaireInternational(2, true);
		test(situation,"I4","71100","7359.90");		
	}

	@Test
	public void testI5() {
		// Barème I5
		logger.debug("*************** I5 ******************");
		SituationFamilialeGE situation = constructeurSituation.creerCoupleDontUnFonctionnaireInternational(3, false);
		test(situation,"I5","242400","48984.82");		
	}

	@Test
	public void testI6() {
		// Barème I6
		logger.debug("*************** I6 ******************");
		SituationFamilialeGE situation = constructeurSituation.creerCoupleDontUnFonctionnaireInternational(3, true);
		test(situation,"I6","295200","63029.03");		
		test(situation,"I6","95100","11768.73");		
	}

	@Test
	public void testI7() {
		// Barème I7
		logger.debug("*************** I7 ******************");
		SituationFamilialeGE situation = constructeurSituation.creerCoupleDontUnFonctionnaireInternational(4, false);
		test(situation,"I7","427200","99599.63");		
	}

	@Test
	public void testI8() {
		// Barème I8
		logger.debug("*************** I8 ******************");
		SituationFamilialeGE situation = constructeurSituation.creerCoupleDontUnFonctionnaireInternational(4, true);
		test(situation,"I8","145500","22717.42");		
		test(situation,"I8","226500","43499.39");		
	}
	
}
