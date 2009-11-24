/**
 * 
 */
package ch.ge.afc.baremeis.ge;

import java.math.BigDecimal;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.ge.afc.calcul.impot.cantonal.ge.pp.avant2010.SituationFamilialeGE;
import ch.ge.afc.calcul.impot.taxation.pp.EnfantACharge;
import ch.ge.afc.calcul.impot.taxation.pp.PersonneACharge;
import ch.ge.afc.calcul.impot.taxation.pp.SituationFamiliale;
import ch.ge.afc.baremeis.CalculateurImpotSource;

/**
 * @author <a href="mailto:patrick.giroud@etat.ge.ch">Patrick Giroud</a>
 *
 */
public class CalculateurImpotSourceAvant2010 extends
		CalculateurImpotSourceICCetIFD implements CalculateurImpotSource {
	final Logger logger = LoggerFactory.getLogger(CalculateurImpotSourceAvant2010.class);
	
	
	public CalculateurImpotSourceAvant2010(int annee) {
		super(annee);
	}

	@Override
	public BigDecimal calcul(SituationFamiliale situation, BigDecimal revenuBrut) {
		try {
			SituationFamilialeGE situationGE = (SituationFamilialeGE)situation;
			BigDecimal impotICC = this.calculICC(situationGE, revenuBrut);
			BigDecimal impotIFD = this.calculIFD(construireSituationIFD(situationGE), revenuBrut);
			return impotICC.add(impotIFD);
		} catch (ClassCastException ccex) {
			logger.error("Pour l'année " + this.getAnnee() + ", la situation familiale doit précisé si le contribuable est conjoint d'un fonctionnaire international et ");
			throw ccex;
		}
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

}
