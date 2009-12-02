/**
 * 
 */
package ch.ge.afc.baremeis.ge.ifd;


import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Set;

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
import ch.ge.afc.calcul.impot.taxation.pp.EnfantACharge;
import ch.ge.afc.calcul.impot.taxation.pp.PersonneACharge;
import ch.ge.afc.calcul.impot.taxation.pp.SituationFamiliale;

/**
 * @author <a href="mailto:patrick.giroud@etat.ge.ch">Patrick Giroud</a>
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/beansis.xml")
public class CalculateurImpotSourceIFD2009Test {

	final Logger logger = LoggerFactory.getLogger(CalculateurImpotSourceIFD2009Test.class);

	@Resource(name = "fournisseurCalculateurISGeneve")
	private FournisseurCalculateurIS fournisseur;
	private ConstructeurSituationFamilialeGE constructeurSituation = new ConstructeurSituationFamilialeGE();
	private CalculateurImpotSource calculateur;

	
	@Before
	public void setUp() throws Exception {
		calculateur = fournisseur.getCalculateurImpotSourceIFD(2009);
	}

	
	private boolean egal(BigDecimal montantAttendu, BigDecimal montantCalcule) {
		return 0 == montantAttendu.compareTo(montantCalcule);
	}
	
	private void test(CalculateurImpotSource calculateur, SituationFamilialeGE situation, String codeBareme, String revenuBrut, String montantAttendu) {
		assertTrue("Barème " + codeBareme + " avec " + revenuBrut+ " francs de revenu brut", egal(new BigDecimal(montantAttendu),calculateur.calcul(construireSituationIFD(situation), new BigDecimal(revenuBrut))));
	}
	
	private SituationFamiliale construireSituationIFD(final SituationFamilialeGE situationGE) {
		return new SituationFamiliale() {

			/* (non-Javadoc)
			 * @see ch.ge.afc.calcul.impot.taxation.pp.SituationFamiliale#getEnfants()
			 */
			@Override
			public Set<EnfantACharge> getEnfants() {
				return situationGE.getEnfants();
			}

			/* (non-Javadoc)
			 * @see ch.ge.afc.calcul.impot.taxation.pp.SituationFamiliale#getPersonnesNecessiteuses()
			 */
			@Override
			public Set<PersonneACharge> getPersonnesNecessiteuses() {
				return situationGE.getPersonnesNecessiteuses();
			}

			/* (non-Javadoc)
			 * @see ch.ge.afc.calcul.impot.taxation.pp.SituationFamiliale#isCouple()
			 */
			@Override
			public boolean isCouple() {
				return situationGE.isCouple() || situationGE.isConjointFonctionnaireInternational();
			}
			
		};
	}	

	@Test
	public void testA0() {
		// Barème A0
		SituationFamilialeGE situation = constructeurSituation.creerCelibataireSansCharge();
		test(calculateur,situation,"A0","100000","1770.15");
		test(calculateur,situation,"A0","300000","21864.95");
	}
	
	@Test
	public void testB0() {
		// Barème B0
		SituationFamilialeGE situation = constructeurSituation.creerCoupleSansDoubleActiviteSansCharge();
		test(calculateur,situation,"B0","120000","1780.00");
		test(calculateur,situation,"B0","134700","2501.00");
	}
	
	@Test
	public void testB1() {
		// Barème B1
		SituationFamilialeGE situation = constructeurSituation.creerCoupleSansDoubleActiviteAvecEnfant(1, false, false);
		test(calculateur,situation,"B1","131100","1920.00");
		test(calculateur,situation,"B1","200100","7782.00");
	}

	@Test
	public void testB2() {
		// Barème B2
		SituationFamilialeGE situation = constructeurSituation.creerCoupleSansDoubleActiviteAvecEnfant(1, true, false);
		test(calculateur,situation,"B2","132900","2000.00");
		test(calculateur,situation,"B2","254400","14087.00");
		test(calculateur,situation,"B2","400800","31117.00");
	}
	
	@Test
	public void testB8() {
		// Barème B8
		SituationFamilialeGE situation = constructeurSituation.creerCoupleSansDoubleActiviteAvecEnfant(3, true, false);
		test(calculateur,situation,"B8","150000","2099.00");
	}
	
	@Test
	public void testB9() {
		// Barème B9
		SituationFamilialeGE situation = constructeurSituation.creerCoupleSansDoubleActiviteAvecEnfant(3, true, true);
		test(calculateur,situation,"B9","117300","805.00");
	}
	
	@Test
	public void testB10() {
		// Barème B10
		SituationFamilialeGE situation = constructeurSituation.creerCoupleSansDoubleActiviteAvecEnfant(4, false, false);
		test(calculateur,situation,"B10","565200","47575.00");
	}
	
	@Test
	public void testB13() {
		// Barème B13
		logger.debug("*************** B13 ******************");
		SituationFamilialeGE situation = constructeurSituation.creerCoupleSansDoubleActiviteAvecEnfant(5, false, false);
		test(calculateur,situation,"B13","348000","21432.00");
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
		test(calculateur,situation,"B15","200100","4387.00");
	}

	@Test
	public void testI0() {
		// Barème I0
		logger.debug("*************** I0 ******************");
		SituationFamilialeGE situation = constructeurSituation.creerCoupleDontUnFonctionnaireInternational(0, false);
		test(calculateur,situation,"I0","146700","3224.00");
		test(calculateur,situation,"I0","315600","22095.00");
	}
	
	@Test
	public void testI1() {
		// Barème I1
		logger.debug("*************** I1 ******************");
		SituationFamilialeGE situation = constructeurSituation.creerCoupleDontUnFonctionnaireInternational(1, false);
		test(calculateur,situation,"I1","45900","0.00");
		test(calculateur,situation,"I1","105300","936.00");
		test(calculateur,situation,"I1","69900","171.00");
		test(calculateur,situation,"I1","120300","1450.00");
		test(calculateur,situation,"I1","221700","10291.00");
		test(calculateur,situation,"I1","302400","19677.00");
		test(calculateur,situation,"I1","580800","52034.00");
		test(calculateur,situation,"I1","59100","82.00");
	}
	
	@Test
	public void testI2() {
		// Barème I2
		logger.debug("*************** I2 ******************");
		SituationFamilialeGE situation = constructeurSituation.creerCoupleDontUnFonctionnaireInternational(1, true);
		test(calculateur,situation,"I2","511200","43948.00");
		test(calculateur,situation,"I2","357600","26086.00");
		
	}
	
	@Test
	public void testI4() {
		// Barème I4
		logger.debug("*************** I4 ******************");
		SituationFamilialeGE situation = constructeurSituation.creerCoupleDontUnFonctionnaireInternational(2, true);
		test(calculateur,situation,"I4","71100","113.00");		
	}

	@Test
	public void testI5() {
		// Barème I5
		logger.debug("*************** I5 ******************");
		SituationFamilialeGE situation = constructeurSituation.creerCoupleDontUnFonctionnaireInternational(3, false);
		test(calculateur,situation,"I5","242400","10928.00");		
	}

	@Test
	public void testI6() {
		// Barème I6
		logger.debug("*************** I6 ******************");
		SituationFamilialeGE situation = constructeurSituation.creerCoupleDontUnFonctionnaireInternational(3, true);
		test(calculateur,situation,"I6","295200","17064.00");		
		test(calculateur,situation,"I6","95100","282.00");		
	}

	@Test
	public void testI7() {
		// Barème I7
		logger.debug("*************** I7 ******************");
		SituationFamilialeGE situation = constructeurSituation.creerCoupleDontUnFonctionnaireInternational(4, false);
		test(calculateur,situation,"I7","427200","31533.00");		
	}

	@Test
	public void testI8() {
		// Barème I8
		logger.debug("*************** I8 ******************");
		SituationFamilialeGE situation = constructeurSituation.creerCoupleDontUnFonctionnaireInternational(4, true);
		test(calculateur,situation,"I8","145500","1540.00");		
		test(calculateur,situation,"I8","226500","8198.00");		
	}


}
