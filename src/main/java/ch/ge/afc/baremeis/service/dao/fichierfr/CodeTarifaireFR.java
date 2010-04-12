/**
 * 
 */
package ch.ge.afc.baremeis.service.dao.fichierfr;

import ch.ge.afc.baremeis.service.GroupeTarifaire;
import ch.ge.afc.baremeis.service.ICodeTarifaire;

/**
 * @author <a href="mailto:patrick.giroud@etat.ge.ch">Patrick Giroud</a>
 *
 */
class CodeTarifaireFR implements ICodeTarifaire, Comparable<CodeTarifaireFR> {

	private final int nbreEnfant;
	private final String code;
	private final GroupeTarifaire groupe;
	
	public CodeTarifaireFR(String code, int nombreEnfant) {
		this.code = code;
		this.nbreEnfant = nombreEnfant;
		if ("A".equals(code)) {
			groupe = GroupeTarifaire.A;
		} else if ("B".equals(code)) {
			groupe = GroupeTarifaire.SALAIRE_UNIQUE;
		} else if ("C".equals(code)) {
			groupe = GroupeTarifaire.DOUBLE_SALAIRE;
		} else {
			groupe = null;
			throw new IllegalArgumentException("Le code d'un barème à la source fribourgeois doit être A, B ou C et non pas '" + code +"'");
		}
	}
	
	@Override
	public String getCode() {
		return code + nbreEnfant;
	}

	@Override
	public GroupeTarifaire getGroupe() {
		return groupe;
	}

	@Override
	public int compareTo(CodeTarifaireFR o) {
		return this.getCode().compareTo(o.getCode());
	}

	
}
