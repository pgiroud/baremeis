/**
 * 
 */
package ch.ge.afc.baremeis;

import java.math.BigDecimal;

import ch.ge.afc.calcul.impot.taxation.pp.SituationFamiliale;

/**
 * @author <a href="mailto:patrick.giroud@etat.ge.ch">Patrick Giroud</a>
 *
 */
public interface CalculateurImpotSource {
	BigDecimal calcul(SituationFamiliale situation, BigDecimal revenuBrut);
}
