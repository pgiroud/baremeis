/**
 * 
 */
package ch.ge.afc.baremeis.service;

/**
 * @author <a href="mailto:patrick.giroud@etat.ge.ch">Patrick Giroud</a>
 *
 */
public enum GroupeTarifaire {
	
	A('A',"Barème Personne célibataire"),
	SALAIRE_UNIQUE('B',"Barème Salaire unique (marié)"),
	DOUBLE_SALAIRE('C',"Barème Double salaire"),
	GAIN_ACCESSOIRE('D',"Barème Gain accessoire"),
	FRONTALIER('G',"Barème Frontalier (GD; GI)"),
	ACTIVITE_COMPLEMENTAIRE('V',"Canton VD: Barème \"Activité complémentaire\""),
	BIZARRE_TESSIN('E',"Bizarre tessin"),
	BIZARRE_TESSIN2('F',"Bizarre tessin 2"),
	CONJOINT_FONC_INTERNATIONAL('I',"Conjoint de fonctionnaire internationale");
	
	
	public static GroupeTarifaire getParCode(char code) {
		for (GroupeTarifaire groupe : values()) {
			if (groupe.code == code) return groupe;
		}
		return null;
	}
	
	private final char code;
	private final String libelle;
	
	
	private GroupeTarifaire(char code, String libelle) {
		this.code = code;
		this.libelle = libelle;
	}


	public char getCode() {
		return code;
	}


	public String getLibelle() {
		return libelle;
	}
	
}
