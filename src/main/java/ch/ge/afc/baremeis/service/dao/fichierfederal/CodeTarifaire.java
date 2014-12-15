/**
 * 
 */
package ch.ge.afc.baremeis.service.dao.fichierfederal;

import ch.ge.afc.baremeis.service.GroupeTarifaire;
import ch.ge.afc.baremeis.service.ICodeTarifaire;
import ch.ge.afc.baremeis.service.TypeFrontalier;

/**
 * @author <a href="mailto:patrick.giroud@etat.ge.ch">Patrick Giroud</a>
 *
 */
public class CodeTarifaire implements Comparable<CodeTarifaire>, ICodeTarifaire {
	
	private final GroupeTarifaire groupe;
	private final int nbreEnfantSuivantIndicCanton;
	private final Boolean avecPartImpotEcclesiastique;
	private final TypeFrontalier typeFrontalier;

	public CodeTarifaire(String code) {
		StringBuilder codeB = new StringBuilder(code);
		int nbCarToDix = 10 - codeB.length();
		for (int i = 0; i < nbCarToDix ; i++) {
			codeB.append(' ');
		}
		String codeLong = codeB.toString();
		groupe = GroupeTarifaire.getParCode(codeLong.charAt(0));
		nbreEnfantSuivantIndicCanton = Integer.valueOf(codeLong.substring(1,2));
		char codeImpotEcclesiastique = codeLong.charAt(2);
		if (' ' != codeImpotEcclesiastique) avecPartImpotEcclesiastique = Boolean.valueOf('+' == codeImpotEcclesiastique
			|| 'Y' == codeImpotEcclesiastique);
		else avecPartImpotEcclesiastique = null;
		typeFrontalier = TypeFrontalier.getParCode(codeLong.substring(3));
	}
	
	
	public GroupeTarifaire getGroupe() {
		return groupe;
	}
	public int getNbreEnfantSuivantIndicCanton() {
		return nbreEnfantSuivantIndicCanton;
	}
	public Boolean isAvecPartImpotEcclesiastique() {
		return avecPartImpotEcclesiastique;
	}
	public TypeFrontalier getTypeFrontalier() {
		return typeFrontalier;
	}
	
	public String getCode() {
		StringBuilder builder = new StringBuilder();
		builder.append(groupe.getCode());
		builder.append(getNbreEnfantSuivantIndicCanton());
		if (null == avecPartImpotEcclesiastique) builder.append(' ');
		else if (avecPartImpotEcclesiastique) builder.append('+');
		else builder.append('d');
		if (null != typeFrontalier) builder.append(typeFrontalier.getCode());
		return builder.toString().trim();
	}


	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CodeTarifaire)) return false;
		CodeTarifaire code = (CodeTarifaire)obj;
		if (this.groupe != code.groupe) return false;
		if (this.nbreEnfantSuivantIndicCanton != code.nbreEnfantSuivantIndicCanton) return false;
		if (null == avecPartImpotEcclesiastique) return null == code.avecPartImpotEcclesiastique;
		else if (!avecPartImpotEcclesiastique.equals(code.avecPartImpotEcclesiastique)) return false;;
		if (null == typeFrontalier) return null == code.typeFrontalier;
		else if (!typeFrontalier.equals(code.typeFrontalier)) return false;
		return true;
	}


	@Override
	public int hashCode() {
		return getCode().hashCode();
	}


	@Override
	public String toString() {
		return getCode();
	}


	@Override
	public int compareTo(CodeTarifaire o) {
		return this.getCode().compareTo(o.getCode());
	}
	
	
}
