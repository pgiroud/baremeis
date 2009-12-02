/**
 * 
 */
package ch.ge.afc.baremeis.service.dao.fichierfederal;


/**
 * @author <a href="mailto:patrick.giroud@etat.ge.ch">Patrick Giroud</a>
 *
 */
public enum GenreTransaction {

	NOUVELLE_ANNONCE("01"),
	MODIFICATION("02"),
	RADIE("03");
	
	public static GenreTransaction getParCode(String code) {
		for (GenreTransaction genre : values()) {
			if (genre.code.equals(code)) return genre;
		}
		return null;
	}
	
	private final String code;
	
	private GenreTransaction(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
	
	
}
