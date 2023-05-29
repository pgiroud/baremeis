/**
 * 
 */
package ch.ge.afc.baremeis.service;

import java.util.Set;

import org.impotch.bareme.BaremeParTranche;

/**
 * @author <a href="mailto:patrick.giroud@etat.ge.ch">Patrick Giroud</a>
 *
 */
public interface ServiceBaremeImpotSource {

	Set<ICodeTarifaire> rechercherCodeTarifaire(int annee, String codeCanton);

	BaremeParTranche obtenirBaremeMensuel(int annee, String codeCanton, String code);

	BaremeParTranche obtenirBaremeAnnuel(int annee, String codeCanton, String code);

	Set<BaremeDisponible> baremeDisponible();
}
