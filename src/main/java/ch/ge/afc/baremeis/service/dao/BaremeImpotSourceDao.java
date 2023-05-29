/**
 * 
 */
package ch.ge.afc.baremeis.service.dao;

import java.util.Set;


import ch.ge.afc.baremeis.service.BaremeDisponible;
import ch.ge.afc.baremeis.service.ICodeTarifaire;
import org.impotch.bareme.BaremeParTranche;

/**
 * @author <a href="mailto:patrick.giroud@etat.ge.ch">Patrick Giroud</a>
 *
 */
public interface BaremeImpotSourceDao {
	Set<ICodeTarifaire> rechercherCodesTarifaires(int annee, String codeCanton);
	BaremeParTranche obtenirBaremeMensuel(int annee, String codeCanton, ICodeTarifaire code);
	Set<BaremeDisponible> baremeDisponible();
}
