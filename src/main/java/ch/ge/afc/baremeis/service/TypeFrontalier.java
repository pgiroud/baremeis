/**
 * 
 */
package ch.ge.afc.baremeis.service;

/**
 * @author <a href="mailto:patrick.giroud@etat.ge.ch">Patrick Giroud</a>
 *
 */
public enum TypeFrontalier {
	
	FRONTALIER_ALLEMAGNE("GD"),
	FRONTALIER_ITALIE("GI");
	
	public static TypeFrontalier getParCode(String code) {
		for(TypeFrontalier type : values()) {
			if (type.code.equals(code)) return type;
		}
		return null;
	}
	
	private final String code;
	
	private TypeFrontalier(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
	
	
}
