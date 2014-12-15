/**
 * 
 */
package ch.ge.afc.baremeis.service;

import java.util.Set;

import org.impotch.bareme.BaremeTauxEffectifConstantParTranche;

/**
 * @author <a href="mailto:patrick.giroud@etat.ge.ch">Patrick Giroud</a>
 *
 */
public interface ServiceBaremeImpotSource {

	Set<ICodeTarifaire> rechercherBareme(int annee, String codeCanton);
	
	BaremeTauxEffectifConstantParTranche obtenirBaremeMensuel(int annee, String codeCanton, String code);
	
	BaremeTauxEffectifConstantParTranche obtenirBaremeAnnuel(int annee, String codeCanton, String code);

	Set<BaremeDisponible> baremeDisponible();
}
