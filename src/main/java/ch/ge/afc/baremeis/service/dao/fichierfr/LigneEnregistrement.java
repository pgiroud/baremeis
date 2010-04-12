/**
 * 
 */
package ch.ge.afc.baremeis.service.dao.fichierfr;

import java.math.BigDecimal;

import ch.ge.afc.baremeis.service.GroupeTarifaire;

/**
 * @author <a href="mailto:patrick.giroud@etat.ge.ch">Patrick Giroud</a>
 *
 */
class LigneEnregistrement {
	private GroupeTarifaire groupe;
	private BigDecimal mntMinMensu;
	private BigDecimal mntMaxMensu;
	private BigDecimal[] taux;
	
	public BigDecimal getMntMinMensu() {
		return mntMinMensu;
	}
	
	public void setMntMinMensu(BigDecimal mntMinMensu) {
		this.mntMinMensu = mntMinMensu;
	}
	
	public BigDecimal getMntMaxMensu() {
		return mntMaxMensu;
	}
	
	public void setMntMaxMensu(BigDecimal mntMaxMensu) {
		this.mntMaxMensu = mntMaxMensu;
	}
	
	public BigDecimal[] getTaux() {
		return taux;
	}
	
	public void setTaux(BigDecimal[] taux) {
		this.taux = taux;
	}

	public GroupeTarifaire getGroupe() {
		return groupe;
	}

	public void setGroupe(GroupeTarifaire groupe) {
		this.groupe = groupe;
	}

	
}
