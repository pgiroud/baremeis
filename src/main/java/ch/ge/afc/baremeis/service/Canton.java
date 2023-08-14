package ch.ge.afc.baremeis.service;

import java.util.Optional;

public enum Canton {
    AR("Appenzell Rhodes-Extérieures","ar"),
    AI("Appenzell Rhodes-Intérieures","ai"),
	AG("Argovie","ag"),
	BL("Bâle-Campagne","bl"),
	BS("Bâle-Ville","bs"),
	BE("Berne","be"),
	FR("Fribourg","fr"),
	GE("Genève","ge"),
	GL("Glaris","gl"),
	GR("Grisons","gr"),
	JU("Jura","ju"),
	LU("Lucerne","lu"),
	NE("Neuchâtel","ne"),
	NW("Nidwald","nw"),
	OW("Obwald","ow"),
	SG("Saint-Gall","sg"),
	SH("Schaffhouse","sh"),
	SZ("Schwytz","sz"),
	SO("Soleure","so"),
	TI("Tessin","ti"),
	TG("Thurgovie","tg"),
	UR("Uri","ur"),
	VS("Valais","vs"),
	VD("Vaud","vd"),
	ZG("Zoug","zg"),
	ZH("Zurich","zh");

    public static Optional<Canton> getParCode(String code) {
        for (Canton canton : values()) {
            if (code.equalsIgnoreCase(canton.codeEnMinuscule)) return Optional.of(canton);
        }
        return Optional.empty();
    }

	private final String nomEnFrancais;
	private final String codeEnMinuscule;


	private Canton(String nomEnFrancais, String codeEnMinuscule) {
		this.nomEnFrancais = nomEnFrancais;
		this.codeEnMinuscule = codeEnMinuscule;
	}
}
