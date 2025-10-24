/**
 * 
 */
package ch.ge.afc.baremeis.service;

import static ch.ge.afc.baremeis.service.TypeFrontalier.*;

/**
 * @author <a href="mailto:patrick.giroud@etat.ge.ch">Patrick Giroud</a>
 *
 */
public enum GroupeTarifaire {


	A('A',"Barème pour les personnes célibataires",true,NON_SPECIFIQUE),
	SALAIRE_UNIQUE('B',"Barème pour les couples mariés vivant en ménage commun dont un seul conjoint exerce une activité lucrative",true,NON_SPECIFIQUE),
	DOUBLE_SALAIRE('C',"Barème pour les couples mariés à deux revenus",true,NON_SPECIFIQUE),
	GAIN_ACCESSOIRE('D',"Barème pour les activités lucratives accessoires et les revenus acquis en compensation",true,NON_SPECIFIQUE),
	DECOMPTE_SIMPLIFIE('E',"Barème pour les revenus imposés dans le cadre de la procédure de décompte simplifiée",false,NON_SPECIFIQUE),
	FRONT_ITAL('F',"Barème pour les frontaliers italiens dont le conjoint exerce une activité lucrative en dehors de la Suisse",true,DOMICILE_ITALIE),
	FRONTALIER('G',"Barème Frontalier (GD; GI)",true,DOMICILE_ALLEMAGNE),
	G('G',"Barème pour les revenus acquis en compensation qui sont versés aux " +
			"personnes soumises à l'imposition à la source par une personne autre " +
			"que l'employeur",false,NON_SPECIFIQUE),
	SEUL_AVEC_CHARGE('H',"Barème pour les personnes vivant seules et faisant ménage commun avec des enfants ou des personnes nécessiteuses dont ils assument l’essentiel de l’entretien",true,NON_SPECIFIQUE),
	CONJOINT_FONC_INTERNATIONAL('I',"Conjoint de fonctionnaire internationale",true,NON_SPECIFIQUE),
	L('L',"Barème pour les frontaliers allemands qui remplissent les conditions du barème A",true,DOMICILE_ALLEMAGNE),
	M('M',"Barème pour les frontaliers allemands qui remplissent les conditions du barème B",true,DOMICILE_ALLEMAGNE),
	N('N',"Barème pour les frontaliers allemands qui remplissent les conditions du barème C",true,DOMICILE_ALLEMAGNE),
	O('O',"Barème pour les frontaliers allemands qui remplissent les conditions du barème D",true,DOMICILE_ALLEMAGNE),
	P('P',"Barème pour les frontaliers allemands qui remplissent les conditions du barème H",true,DOMICILE_ALLEMAGNE),
	Q('Q',"Barème pour les frontaliers allemands qui remplissent les conditions du barème G",false,DOMICILE_ALLEMAGNE),
	R('R',"frontaliers imposés selon l’art. 3, par. 1, de l’accord du 23 dé-\n" +
			"cembre 2020 entre la Confédération suisse et la République italienne relatif à\n" +
			"l’imposition des travailleurs frontaliers (accord CH-IT sur l’imposition des\n" +
			"frontaliers)2 qui remplissent les conditions pour l’octroi du barème A",true,DOMICILE_ITALIE),
	S('S',"rontaliers imposés selon l’art. 3, par. 1, de l’accord CH-IT sur\n" +
			"l’imposition des frontaliers qui remplissent les conditions pour l’octroi du ba-\n" +
			"rème B",true,DOMICILE_ITALIE),
	T('T',"frontaliers imposés selon l’art. 3, par. 1, de l’accord CH-IT sur\n" +
			"l’imposition des frontaliers qui remplissent les conditions pour l’octroi du ba-\n" +
			"rème C",true,DOMICILE_ITALIE),
	U('U',"rontaliers imposés selon l’art. 3, par. 1, de l’accord CH-IT sur\n" +
			"l’imposition des frontaliers qui remplissent les conditions pour l’octroi du ba-\n" +
			"rème H",true,DOMICILE_ITALIE),
	V('V',"Barème pour les frontaliers italiens imposés selon l’art. 3, par. 1, de\n" +
			"l’accord entre la Suisse et l'Italie sur l’imposition des frontaliers qui\n" +
			"remplissent les conditions du barème G",false,DOMICILE_ITALIE),
	ACTIVITE_COMPLEMENTAIRE('V',"Canton VD: Barème \"Activité complémentaire\"",false,NON_SPECIFIQUE),
	W('W',"Barème pour les rentes provenant de la prévoyance professionnelle",false,NON_SPECIFIQUE),
	Y('Y',"Prestation en capital versée à une personne célibataire",false,NON_SPECIFIQUE),
	Z('Z',"Prestation en capital versée à une personne mariée",false,NON_SPECIFIQUE);

	
	public static GroupeTarifaire getParCode(char code) {
		for (GroupeTarifaire groupe : values()) {
			if (groupe.code == code) return groupe;
		}
		return null;
	}
	
	private final char code;
	private final String libelle;
	private final boolean nbEnfantsSignificatifs;
	private final TypeFrontalier typeFrontalier;
	
	
	private GroupeTarifaire(char code, String libelle, boolean nbEnfantsSignificatifs) {
		this.code = code;
		this.libelle = libelle;
		this.nbEnfantsSignificatifs = nbEnfantsSignificatifs;
		this.typeFrontalier = NON_SPECIFIQUE;
	}

	private GroupeTarifaire(char code, String libelle, boolean nbEnfantsSignificatifs, TypeFrontalier typeFrontalier) {
		this.code = code;
		this.libelle = libelle;
		this.nbEnfantsSignificatifs = nbEnfantsSignificatifs;
		this.typeFrontalier = typeFrontalier;
	}

	public boolean nbEnfantsSignificatifs() {
		return this.nbEnfantsSignificatifs;
	}

	public char getCode() {
		return code;
	}


	public String getLibelle() {
		return libelle;
	}

	public TypeFrontalier getTypeFrontalier() {
		return typeFrontalier;
	}
}
