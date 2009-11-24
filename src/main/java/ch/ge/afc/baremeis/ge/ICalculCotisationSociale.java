/**
 * 
 */
package ch.ge.afc.baremeis.ge;

import java.math.BigDecimal;

/**
 * @author <a href="mailto:patrick.giroud@etat.ge.ch">Patrick Giroud</a>
 *
 */
public interface ICalculCotisationSociale {
	String getNomCotisation();
	BigDecimal calcul(BigDecimal revenuBrut);

}
