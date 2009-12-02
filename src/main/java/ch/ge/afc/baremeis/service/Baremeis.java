/**
 * 
 */
package ch.ge.afc.baremeis.service;

import java.util.Date;


/**
 * @author <a href="mailto:patrick.giroud@etat.ge.ch">Patrick Giroud</a>
 *
 */
public class Baremeis {
	
	private String codeCanton;
	private GroupeTarifaire groupe;
	private int nbreEnfantSuivantIndicCanton;
	private boolean avecPartImpotEcclesiastique;
	private TypeFrontalier typeFrontalier;
	private Date dateInitialeValidite;
	private Sexe sexe;
	
	public String getCodeCanton() {
		return codeCanton;
	}
	public void setCodeCanton(String codeCanton) {
		this.codeCanton = codeCanton;
	}
	public GroupeTarifaire getGroupe() {
		return groupe;
	}
	public void setGroupe(GroupeTarifaire groupe) {
		this.groupe = groupe;
	}
	public int getNbreEnfantSuivantIndicCanton() {
		return nbreEnfantSuivantIndicCanton;
	}
	public void setNbreEnfantSuivantIndicCanton(int nbreEnfantSuivantIndicCanton) {
		this.nbreEnfantSuivantIndicCanton = nbreEnfantSuivantIndicCanton;
	}
	public boolean isAvecPartImpotEcclesiastique() {
		return avecPartImpotEcclesiastique;
	}
	public void setAvecPartImpotEcclesiastique(boolean avecPartImpotEcclesiastique) {
		this.avecPartImpotEcclesiastique = avecPartImpotEcclesiastique;
	}
	public TypeFrontalier getTypeFrontalier() {
		return typeFrontalier;
	}
	public void setTypeFrontalier(TypeFrontalier typeFrontalier) {
		this.typeFrontalier = typeFrontalier;
	}
	public Date getDateInitialeValidite() {
		return dateInitialeValidite;
	}
	public void setDateInitialeValidite(Date dateInitialeValidite) {
		this.dateInitialeValidite = dateInitialeValidite;
	}
	public Sexe getSexe() {
		return sexe;
	}
	public void setSexe(Sexe sexe) {
		this.sexe = sexe;
	}
	
	
}
