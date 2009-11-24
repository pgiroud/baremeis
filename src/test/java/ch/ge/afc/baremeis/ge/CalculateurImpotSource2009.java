/**
 * 
 */
package ch.ge.afc.baremeis.ge;


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

/**
 * @author <a href="mailto:patrick.giroud@etat.ge.ch">Patrick Giroud</a>
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/beansis.xml")
public class CalculateurImpotSource2009 {

	final Logger logger = LoggerFactory.getLogger(CalculateurImpotSource2009.class);

	@Resource(name = "fournisseurCalculateurISGeneve")
	private FournisseurCalculateurIS fournisseur;
	private ConstructeurSituationFamilialeGE constructeurSituation = new ConstructeurSituationFamilialeGE();
	private CalculateurImpotSource calculateur;
	
	
	@Before
	public void init() {
		calculateur = fournisseur.getCalculateurImpotSource(2009);
	}
	
	private boolean presqueEgal(BigDecimal montantAttendu, BigDecimal montantCalcule) {
		BigDecimal tolerance = new BigDecimal("0.15");
		return 0 < tolerance.compareTo(montantAttendu.subtract(montantCalcule).abs());
	}
	
	private void test(CalculateurImpotSource calculateur, SituationFamilialeGE situation, String codeBareme, String revenuBrut, String montantAttendu) {
		assertTrue("Barème " + codeBareme + " avec " + revenuBrut+ " francs de revenu brut", presqueEgal(new BigDecimal(montantAttendu),calculateur.calcul(situation, new BigDecimal(revenuBrut))));
	}
	
	@Test
	public void testA0() {
		// Barème A0
		SituationFamilialeGE situation = constructeurSituation.creerCelibataireSansCharge();
		test(calculateur,situation,"A0","100000","17264.22");
		test(calculateur,situation,"A0","300000","88903.85");
	}
	
	@Test
	public void testB0() {
		// Barème B0
		SituationFamilialeGE situation = constructeurSituation.creerCoupleSansDoubleActiviteSansCharge();
		test(calculateur,situation,"B0","120000","16526.74");
		test(calculateur,situation,"B0","134700","20643.16");
	}
	
	@Test
	public void testB1() {
		// Barème B1
		SituationFamilialeGE situation = constructeurSituation.creerCoupleSansDoubleActiviteAvecEnfant(1, false, false);
		test(calculateur,situation,"B1","131100","17581.96");
		test(calculateur,situation,"B1","200100","40598.36");
	}

	@Test
	public void testB2() {
		// Barème B2
		SituationFamilialeGE situation = constructeurSituation.creerCoupleSansDoubleActiviteAvecEnfant(1, true, false);
		test(calculateur,situation,"B2","100000","9293.85");
		test(calculateur,situation,"B2","254400","60978.07");
		test(calculateur,situation,"B2","400800","118734.60");
	}
	
	@Test
	public void testB8() {
		// Barème B8
		SituationFamilialeGE situation = constructeurSituation.creerCoupleSansDoubleActiviteAvecEnfant(3, true, false);
		test(calculateur,situation,"B8","150000","18452.1");
	}
	
	@Test
	public void testB9() {
		// Barème B9
		SituationFamilialeGE situation = constructeurSituation.creerCoupleSansDoubleActiviteAvecEnfant(3, true, true);
		test(calculateur,situation,"B9","117300","9228.58");
	}
	
	@Test
	public void testB10() {
		// Barème B10
		SituationFamilialeGE situation = constructeurSituation.creerCoupleSansDoubleActiviteAvecEnfant(4, false, false);
		test(calculateur,situation,"B10","565200","177566.45");
	}
	
	@Test
	public void testB13() {
		// Barème B13
		logger.debug("*************** B13 ******************");
		SituationFamilialeGE situation = constructeurSituation.creerCoupleSansDoubleActiviteAvecEnfant(5, false, false);
		test(calculateur,situation,"B13","348000","86918.81");
	}
	
	@Test
	public void testB14() {
		// Barème B14
		logger.debug("*************** B14 ******************");
		SituationFamilialeGE situation = constructeurSituation.creerCoupleSansDoubleActiviteAvecEnfant(5, true, false);
		test(calculateur,situation,"B14","86700","40");
	}

	@Test
	public void testB15() {
		// Barème B15
		logger.debug("*************** B15 ******************");
		SituationFamilialeGE situation = constructeurSituation.creerCoupleSansDoubleActiviteAvecEnfant(5, true, true);
		test(calculateur,situation,"B15","200100","29207.03");
	}

	@Test
	public void testI0() {
		// Barème I0
		logger.debug("*************** I0 ******************");
		SituationFamilialeGE situation = constructeurSituation.creerCoupleDontUnFonctionnaireInternational(0, false);
		test(calculateur,situation,"I0","146700","30071.32");
		test(calculateur,situation,"I0","315600","93711.83");
	}
	
	@Test
	public void testI1() {
		// Barème I1
		logger.debug("*************** I1 ******************");
		SituationFamilialeGE situation = constructeurSituation.creerCoupleDontUnFonctionnaireInternational(1, false);
		test(calculateur,situation,"I1","45900","3374.18");
		test(calculateur,situation,"I1","105300","17083.17");
		test(calculateur,situation,"I1","69900","8416.77");
		test(calculateur,situation,"I1","120300","21070.45");
		test(calculateur,situation,"I1","221700","55560.98");
		test(calculateur,situation,"I1","302400","86797.88");
		test(calculateur,situation,"I1","580800","199114.9");
		test(calculateur,situation,"I1","59100","6050.18");
	}
	
	@Test
	public void testI2() {
		// Barème I2
		logger.debug("*************** I2 ******************");
		SituationFamilialeGE situation = constructeurSituation.creerCoupleDontUnFonctionnaireInternational(1, true);
		test(calculateur,situation,"I2","511200","170368.36");
		test(calculateur,situation,"I2","357600","108350.72");
		
	}
	
	@Test
	public void testI4() {
		// Barème I4
		logger.debug("*************** I4 ******************");
		SituationFamilialeGE situation = constructeurSituation.creerCoupleDontUnFonctionnaireInternational(2, true);
		test(calculateur,situation,"I4","71100","7472.9");		
	}

	@Test
	public void testI5() {
		// Barème I5
		logger.debug("*************** I5 ******************");
		SituationFamilialeGE situation = constructeurSituation.creerCoupleDontUnFonctionnaireInternational(3, false);
		test(calculateur,situation,"I5","242400","59912.82");		
	}

	@Test
	public void testI6() {
		// Barème I6
		logger.debug("*************** I6 ******************");
		SituationFamilialeGE situation = constructeurSituation.creerCoupleDontUnFonctionnaireInternational(3, true);
		test(calculateur,situation,"I6","295200","80093.03");		
		test(calculateur,situation,"I6","95100","12050.73");		
	}

	@Test
	public void testI7() {
		// Barème I7
		logger.debug("*************** I7 ******************");
		SituationFamilialeGE situation = constructeurSituation.creerCoupleDontUnFonctionnaireInternational(4, false);
		test(calculateur,situation,"I7","427200","131132.63");		
	}

	@Test
	public void testI8() {
		// Barème I8
		logger.debug("*************** I8 ******************");
		SituationFamilialeGE situation = constructeurSituation.creerCoupleDontUnFonctionnaireInternational(4, true);
		test(calculateur,situation,"I8","145500","24257.42");		
		test(calculateur,situation,"I8","226500","51697.39");		
	}

}
