/**
 * 
 */
package ch.ge.afc.baremeis.service;



/**
 * @author <a href="mailto:patrick.giroud@etat.ge.ch">Patrick Giroud</a>
 *
 */
public class BaremeDisponibleImpl implements BaremeDisponible {
	
	private String codeCanton;
	private int annee;
	
	public String getCodeCanton() {
		return codeCanton;
	}
	public void setCodeCanton(String codeCanton) {
		this.codeCanton = codeCanton;
	}
	public int getAnnee() {
		return annee;
	}
	public void setAnnee(int annee) {
		this.annee = annee;
	}
	
	
}
