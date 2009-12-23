/**
 * 
 */
package ch.ge.afc.baremeis.service;

import ch.ge.afc.util.HashCodeBuilder;



/**
 * @author <a href="mailto:patrick.giroud@etat.ge.ch">Patrick Giroud</a>
 *
 */
public class BaremeDisponibleImpl implements BaremeDisponible {
	
	private String codeCanton;
	private int annee;
	
	public BaremeDisponibleImpl(int annee, String codeCanton) {
		this.annee = annee;
		this.codeCanton = codeCanton.toLowerCase();
	}
	
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

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof BaremeDisponibleImpl)) return false;
		BaremeDisponibleImpl bardispo = (BaremeDisponibleImpl)obj;
		return this.annee == bardispo.annee && this.codeCanton.equals(bardispo.codeCanton);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().add(annee).add(codeCanton).hash();
	}
	
	
}
