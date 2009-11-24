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
public class CalculateurImpotSourceIFD2008 {

	final Logger logger = LoggerFactory.getLogger(CalculateurImpotSourceIFD2008.class);

	@Resource(name = "fournisseurCalculateurISGeneve")
	private FournisseurCalculateurIS fournisseur;
	private ConstructeurSituationFamilialeGE constructeurSituation = new ConstructeurSituationFamilialeGE();
	private CalculateurImpotSource calculateur;

	
	@Before
	public void setUp() throws Exception {
		calculateur = fournisseur.getCalculateurImpotSourceIFD(2008);
	}

	
	private boolean egal(BigDecimal montantAttendu, BigDecimal montantCalcule) {
		boolean egal = 0 == montantAttendu.compareTo(montantCalcule);
		if (!egal) logger.debug("Montant attendu " + montantAttendu + ", montant calculé" + montantCalcule);
		return egal;
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
		test(calculateur,situation,"A0","200100","10077.35");
		test(calculateur,situation,"A0","300000","21864.95");
	}
	
	@Test
	public void testB0() {
		// Barème B0
		SituationFamilialeGE situation = constructeurSituation.creerCoupleSansDoubleActiviteSansCharge();
		test(calculateur,situation,"B0","100000","1032.00");
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
	public void testB10() {
		// Barème B10
		SituationFamilialeGE situation = constructeurSituation.creerCoupleSansDoubleActiviteAvecEnfant(4, false, false);
		test(calculateur,situation,"B10","565200","47575.00");
	}
	

}
