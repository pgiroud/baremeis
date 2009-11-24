/**
 * 
 */
package ch.ge.afc.baremeis.ge;

import java.math.BigDecimal;

import ch.ge.afc.calcul.ReglePeriodique;
import ch.ge.afc.calcul.impot.taxation.pp.SituationFamiliale;
import ch.ge.afc.baremeis.CalculateurImpotSource;

/**
 * @author <a href="mailto:patrick.giroud@etat.ge.ch">Patrick Giroud</a>
 *
 */
public class CalculateurImpotSourceICCetIFD extends ReglePeriodique implements
		CalculateurImpotSource {

	private CalculateurImpotSource calculateurIFD;
	private CalculateurImpotSource calculateurICC;
	
	
	public CalculateurImpotSourceICCetIFD(int annee) {
		super(annee);
	}

	/**
	 * @param calculateurIFD the calculateurIFD to set
	 */
	public void setCalculateurIFD(CalculateurImpotSource calculateurIFD) {
		this.calculateurIFD = calculateurIFD;
	}

	/**
	 * @param calculateurICC the calculateurICC to set
	 */
	public void setCalculateurICC(CalculateurImpotSource calculateurICC) {
		this.calculateurICC = calculateurICC;
	}


	
	protected CalculateurImpotSource getCalculateurIFD() {
		return calculateurIFD;
	}

	protected CalculateurImpotSource getCalculateurICC() {
		return calculateurICC;
	}

	protected BigDecimal calculIFD(SituationFamiliale situation, BigDecimal revenuBrut) {
		return getCalculateurIFD().calcul(situation, revenuBrut);
	}
	
	protected BigDecimal calculICC(SituationFamiliale situation, BigDecimal revenuBrut) {
		return getCalculateurICC().calcul(situation, revenuBrut);
	}
	
	/* (non-Javadoc)
	 * @see ch.ge.afc.calcul.impot.taxation.pp.source.CalculateurImpotSource#calcul(ch.ge.afc.calcul.impot.taxation.pp.SituationFamiliale, java.math.BigDecimal)
	 */
	@Override
	public BigDecimal calcul(SituationFamiliale situation, BigDecimal revenuBrut) {
		BigDecimal impotICC = this.calculICC(situation, revenuBrut);
		BigDecimal impotIFD = this.calculIFD(situation, revenuBrut);
		return impotICC.add(impotIFD);
	}

}
