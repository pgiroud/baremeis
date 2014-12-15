/**
 * 
 */
package ch.ge.afc.baremeis.service;

/**
 * @author <a href="mailto:patrick.giroud@etat.ge.ch">Patrick Giroud</a>
 *
 */
public enum GroupeTarifaire {


	A('A',"Barème pour les personnes célibataires"),
	SALAIRE_UNIQUE('B',"Barème pour les couples mariés vivant en ménage commun dont un seul conjoint exerce une activité lucrative"),
	DOUBLE_SALAIRE('C',"Barème pour les couples mariés à deux revenus"),
	GAIN_ACCESSOIRE('D',"Barème pour les activités lucratives accessoires et les revenus acquis en compensation"),
	DECOMPTE_SIMPLIFIE('E',"Barème pour les revenus imposés dans le cadre de la procédure de décompte simplifiée"),
	FRONT_ITAL('F',"Barème pour les frontaliers italiens dont le conjoint exerce une activité lucrative en dehors de la Suisse"),
	FRONTALIER('G',"Barème Frontalier (GD; GI)"),
	SEUL_AVEC_CHARGE('H',"Barème pour les personnes vivant seules et faisant ménage commun avec des enfants ou des personnes nécessiteuses dont ils assument l’essentiel de l’entretien"),
	L('L',"Barème pour les frontaliers allemands qui remplissent les conditions du barème A"),
	M('M',"Barème pour les frontaliers allemands qui remplissent les conditions du barème B"),
	N('N',"Barème pour les frontaliers allemands qui remplissent les conditions du barème C"),
	O('O',"Barème pour les frontaliers allemands qui remplissent les conditions du barème D"),
	P('P',"Barème pour les frontaliers allemands qui remplissent les conditions du barème H"),
	ACTIVITE_COMPLEMENTAIRE('V',"Canton VD: Barème \"Activité complémentaire\""),
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
