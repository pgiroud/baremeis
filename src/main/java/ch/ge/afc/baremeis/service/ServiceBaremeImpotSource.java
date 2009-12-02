/**
 * 
 */
package ch.ge.afc.baremeis.service;

import java.util.Set;

import ch.ge.afc.calcul.bareme.Bareme;

/**
 * @author <a href="mailto:patrick.giroud@etat.ge.ch">Patrick Giroud</a>
 *
 */
public interface ServiceBaremeImpotSource {

	Set<ICodeTarifaire> rechercherBareme(int annee, String codeCanton);
	
	Bareme obtenirBaremeMensuel(int annee, String codeCanton, String code);
	
}
