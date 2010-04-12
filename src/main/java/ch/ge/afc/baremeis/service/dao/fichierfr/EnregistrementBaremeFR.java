/**
 * 
 */
package ch.ge.afc.baremeis.service.dao.fichierfr;

import java.math.BigDecimal;

/**
 * @author <a href="mailto:patrick.giroud@etat.ge.ch">Patrick Giroud</a>
 *
 */
class EnregistrementBaremeFR {
	private BigDecimal mntMinMensu;
	private BigDecimal mntMaxMensu;
	private BigDecimal taux;
	
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
	public BigDecimal getTaux() {
		return taux;
	}
	public void setTaux(BigDecimal taux) {
		this.taux = taux;
	}
	
	
}
