/**
 * 
 */
package ch.ge.afc.baremeis.service;

import java.math.BigDecimal;
import java.util.Set;

import org.impotch.bareme.BaremeParTranche;
import static org.impotch.util.TypeArrondi.UNITE_LA_PLUS_PROCHE;

/**
 * @author <a href="mailto:patrick.giroud@etat.ge.ch">Patrick Giroud</a>
 *
 */
public interface ServiceBaremeImpotSource {

	Set<ICodeTarifaire> rechercherCodeTarifaire(int annee, String codeCanton);

	BaremeParTranche obtenirBaremeMensuel(int annee, String codeCanton, String code);

	default BaremeParTranche obtenirBaremeAnnuel(int annee, String codeCanton, String code) {
		return obtenirBaremeMensuel(annee,codeCanton,code).homothetie(BigDecimal.valueOf(12), UNITE_LA_PLUS_PROCHE);
	}

	Set<BaremeDisponible> baremeDisponible();
}
