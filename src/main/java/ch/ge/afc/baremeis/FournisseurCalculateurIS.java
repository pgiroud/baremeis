/**
 * 
 */
package ch.ge.afc.baremeis;


/**
 * @author <a href="mailto:patrick.giroud@etat.ge.ch">Patrick Giroud</a>
 *
 */
public interface FournisseurCalculateurIS {
	
	CalculateurImpotSource getCalculateurImpotSource(int annee);
	CalculateurImpotSource getCalculateurImpotSourceIFD(int annee);
	CalculateurImpotSource getCalculateurImpotSourceICC(int annee);
	

}
