/**
 * 
 */
package ch.ge.afc.baremeis.service;

/**
 * @author <a href="mailto:patrick.giroud@etat.ge.ch">Patrick Giroud</a>
 *
 */
public enum Sexe {
	MASCULIN('m'),
	FEMININ('w');
	
	public static Sexe getParCode(char code) {
		for (Sexe sexe : values()) {
			if (sexe.code == code) return sexe;
		}
		return null;
	}
	
	private final char code;
	
	private Sexe(char code) {
		this.code = code;
	}

	public char getCode() {
		return code;
	}
	
	
}
