/**
 * 
 */
package ch.ge.afc.baremeis.service.dao;

import java.util.Set;

import ch.ge.afc.baremeis.service.ICodeTarifaire;
import ch.ge.afc.calcul.bareme.Bareme;

/**
 * @author <a href="mailto:patrick.giroud@etat.ge.ch">Patrick Giroud</a>
 *
 */
public interface BaremeImpotSourceDao {
	Set<ICodeTarifaire> rechercherBareme(int annee, String codeCanton);
	Bareme obtenirBaremeMensuel(int annee, String codeCanton, ICodeTarifaire code); 
}
