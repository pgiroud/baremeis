package ch.ge.afc.baremeis.service.dao.ifdch;

import java.util.stream.IntStream;

public class DescriptionBareme {
    private final DescriptionIntervalle desInter;
    private final char type;
    private final int nbChargeMin;
    private final int nbChargeMax;


    public static DescriptionBareme description(String ligne) {
        DescriptionIntervalle inter = new DescriptionIntervalle(ligne);
        String[] tokens = ligne.trim().split(";");
        int length = tokens.length;
        String dernier = tokens[length-1];
        char c = dernier.charAt(0);
        int nbChargeMax = dernier.length() > 1 ? Integer.valueOf(dernier.substring(1)): 0;
        String troisieme = tokens[2];
        int nbChargeMin = troisieme.length() > 1 ? Integer.valueOf(troisieme.substring(1)): 0;
        return new DescriptionBareme(inter,c,nbChargeMin,nbChargeMax);
    }

    private DescriptionBareme(DescriptionIntervalle desInter, char type, int nbChargeMin, int nbChargeMax) {
        this.desInter = desInter;
        this.type = type;
        this.nbChargeMin = nbChargeMin;
        this.nbChargeMax = nbChargeMax;
    }

    public DescriptionIntervalle getDesInter() {
        return desInter;
    }

    public char getType() {
        return type;
    }

    public int getNbChargeMin() {
        return nbChargeMin;
    }

    public int getNbChargeMax() {
        return nbChargeMax;
    }
}
