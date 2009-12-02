package ch.ge.afc.baremeis;

import java.math.BigDecimal;

import javax.annotation.Resource;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ch.ge.afc.calcul.bareme.Bareme;
import ch.ge.afc.calcul.impot.cantonal.ge.pp.ConstructeurSituationFamiliale;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/beansis.xml")
public class ConstructeurBaremeISGE2010Test {

	final Logger logger = LoggerFactory.getLogger(ConstructeurBaremeISGE2010Test.class);

	@Resource(name = "fournisseurCalculateurISGeneve")
	private FournisseurCalculateurIS fournisseur;
	private ConstructeurSituationFamiliale constructeurSituation = new ConstructeurSituationFamiliale();
	private ConstruteurBaremeImpotSource constructeur;
	
	
	@Before
	public void init() {
		constructeur = new ConstruteurBaremeImpotSource();
		constructeur.setCalculateur(fournisseur.getCalculateurImpotSource(2010));
		constructeur.largeur(600).jusqua(235800)
			.largeur(1200).jusqua(580200);
	}
	
	private void test(Bareme bareme, int revenu, String montantAttendu) {
		assertEquals("Montant pour " + revenu,new BigDecimal(montantAttendu),bareme.calcul(new BigDecimal(revenu)));
	}
	
	@Test
	public void celibataire() {
		Bareme bareme = constructeur.construireBareme(constructeurSituation.creerCelibataireSansCharge());
		test(bareme,0,"0.00");
		assertEquals("Montant pour 25200",new BigDecimal("0.00"),bareme.calcul(new BigDecimal(25200)));
		assertEquals("Montant pour 25800",new BigDecimal("25.80"),bareme.calcul(new BigDecimal(25800)));
		assertEquals("Montant pour 26000",new BigDecimal("28.60"),bareme.calcul(new BigDecimal(26000)));
		assertEquals("Montant pour 26400",new BigDecimal("29.05"),bareme.calcul(new BigDecimal(26400)));
	}
}
