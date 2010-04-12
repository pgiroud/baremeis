/**
 * 
 */
package ch.ge.afc.baremeis.service.dao.fichierge;

import java.math.BigDecimal;

/**
 * @author <a href="mailto:patrick.giroud@etat.ge.ch">Patrick Giroud</a>
 *
 */
class LigneEnregistrement {
	private BigDecimal mntMinAnnuel;
	private BigDecimal mntMaxAnnuel;
	private BigDecimal mntMinMensu;
	private BigDecimal mntMaxMensu;
	private BigDecimal mntMinHoraire;
	private BigDecimal mntMaxHoraire;
	private BigDecimal[] taux;
	public BigDecimal getMntMinAnnuel() {
		return mntMinAnnuel;
	}
	public void setMntMinAnnuel(BigDecimal mntMinAnnuel) {
		this.mntMinAnnuel = mntMinAnnuel;
	}
	public BigDecimal getMntMaxAnnuel() {
		return mntMaxAnnuel;
	}
	public void setMntMaxAnnuel(BigDecimal mntMaxAnnuel) {
		this.mntMaxAnnuel = mntMaxAnnuel;
	}
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
	public BigDecimal getMntMinHoraire() {
		return mntMinHoraire;
	}
	public void setMntMinHoraire(BigDecimal mntMinHoraire) {
		this.mntMinHoraire = mntMinHoraire;
	}
	public BigDecimal getMntMaxHoraire() {
		return mntMaxHoraire;
	}
	public void setMntMaxHoraire(BigDecimal mntMaxHoraire) {
		this.mntMaxHoraire = mntMaxHoraire;
	}
	public BigDecimal[] getTaux() {
		return taux;
	}
	public void setTaux(BigDecimal[] taux) {
		this.taux = taux;
	}
	
	
}
