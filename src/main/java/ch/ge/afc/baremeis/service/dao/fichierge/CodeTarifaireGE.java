/**
 * 
 */
package ch.ge.afc.baremeis.service.dao.fichierge;

import ch.ge.afc.baremeis.service.GroupeTarifaire;
import ch.ge.afc.baremeis.service.ICodeTarifaire;

/**
 * @author <a href="mailto:patrick.giroud@etat.ge.ch">Patrick Giroud</a>
 *
 */
public enum CodeTarifaireGE implements ICodeTarifaire {
	
	A0("A0",GroupeTarifaire.A),
	B0("B0",GroupeTarifaire.SALAIRE_UNIQUE),
	B1("B1",GroupeTarifaire.SALAIRE_UNIQUE,1),
	B2("B2",GroupeTarifaire.SALAIRE_UNIQUE,1,TypeFraisGarde.DEMI_FRAIS),
	B3("B3",GroupeTarifaire.SALAIRE_UNIQUE,1,TypeFraisGarde.FRAIS_ENTIER),
	B4("B4",GroupeTarifaire.SALAIRE_UNIQUE,2),
	B5("B5",GroupeTarifaire.SALAIRE_UNIQUE,2,TypeFraisGarde.DEMI_FRAIS),
	B6("B6",GroupeTarifaire.SALAIRE_UNIQUE,2,TypeFraisGarde.FRAIS_ENTIER),
	B7("B7",GroupeTarifaire.SALAIRE_UNIQUE,3),
	B8("B8",GroupeTarifaire.SALAIRE_UNIQUE,3,TypeFraisGarde.DEMI_FRAIS),
	B9("B9",GroupeTarifaire.SALAIRE_UNIQUE,3,TypeFraisGarde.FRAIS_ENTIER),
	B10("B10",GroupeTarifaire.SALAIRE_UNIQUE,4),
	B11("B11",GroupeTarifaire.SALAIRE_UNIQUE,4,TypeFraisGarde.DEMI_FRAIS),
	B12("B12",GroupeTarifaire.SALAIRE_UNIQUE,4,TypeFraisGarde.FRAIS_ENTIER),
	B13("B13",GroupeTarifaire.SALAIRE_UNIQUE,5),
	B14("B14",GroupeTarifaire.SALAIRE_UNIQUE,5,TypeFraisGarde.DEMI_FRAIS),
	B15("B15",GroupeTarifaire.SALAIRE_UNIQUE,5,TypeFraisGarde.FRAIS_ENTIER),
	I0("I0",GroupeTarifaire.CONJOINT_FONC_INTERNATIONAL),
	I1("I1",GroupeTarifaire.CONJOINT_FONC_INTERNATIONAL,1),
	I2("I2",GroupeTarifaire.CONJOINT_FONC_INTERNATIONAL,1,TypeFraisGarde.DEMI_FRAIS),
	I3("I3",GroupeTarifaire.CONJOINT_FONC_INTERNATIONAL,2),
	I4("I4",GroupeTarifaire.CONJOINT_FONC_INTERNATIONAL,2,TypeFraisGarde.DEMI_FRAIS),
	I5("I5",GroupeTarifaire.CONJOINT_FONC_INTERNATIONAL,3),
	I6("I6",GroupeTarifaire.CONJOINT_FONC_INTERNATIONAL,3,TypeFraisGarde.DEMI_FRAIS),
	I7("I7",GroupeTarifaire.CONJOINT_FONC_INTERNATIONAL,4),
	I8("I8",GroupeTarifaire.CONJOINT_FONC_INTERNATIONAL,4,TypeFraisGarde.DEMI_FRAIS)
	;

	public static CodeTarifaireGE getParCode(String code) {
		for (CodeTarifaireGE codeTar : values()) {
			if (codeTar.getCode().equalsIgnoreCase(code)) {
				return codeTar;
			}
		}
		return null;
	}
	
	private final String code;
	private final GroupeTarifaire groupe;
	private final int nbreEnfant;
	private final TypeFraisGarde typeFraisGarde;
	
	private CodeTarifaireGE(String code, GroupeTarifaire groupe) {
		this(code,groupe,0,null);
	}
	
	private CodeTarifaireGE(String code, GroupeTarifaire groupe, int nbreCharge) {
		this(code,groupe,nbreCharge,null);
	}
	
	private CodeTarifaireGE(String code, GroupeTarifaire groupe, int nbreEnfant, TypeFraisGarde typeFraisGarde) {
		this.code = code;
		this.groupe = groupe;
		this.nbreEnfant = nbreEnfant;
		this.typeFraisGarde = typeFraisGarde;
	}
	
	@Override
	public String getCode() {
		return code;
	}

	@Override
	public GroupeTarifaire getGroupe() {
		return groupe;
	}

	public int getNbreEnfant() {
		return nbreEnfant;
	}

	public TypeFraisGarde getTypeFraisGarde() {
		return typeFraisGarde;
	}
	
	
	
	
	
}
