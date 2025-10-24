package ch.ge.afc.baremeis.service.dao.ifdch;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TrancheBareme {
    private final int borneInf;
    private final int borneSup;
    private final BigDecimal[] valeurs;

    public static TrancheBareme tranche(String ligne) {
        int nbPointVirgule = ligne.length() - ligne.replace(";", "").length();
        int nbNombres = nbPointVirgule + 1;
        int nbValeursBareme = nbNombres - 2; // les 2 premiers sont les bornes de l’intervalle
        String[] mots = ligne.trim().split(";");
        // Ici, on ne peut obtenir que 2 mots !
        int inf = Integer.parseInt(mots[0]);
        int sup = Integer.parseInt(mots[1]);
        List<String> liste = Arrays.asList(Arrays.copyOfRange(mots, 2, mots.length));

        List<BigDecimal> listeBg = liste.stream().filter(TrancheBareme::estNonVide)
                .map(TrancheBareme::bg).toList();
        BigDecimal[] bgs = listeBg.toArray(BigDecimal[]::new);
        BigDecimal[] toutesLesValeurs = new BigDecimal[nbValeursBareme];
        Arrays.fill(toutesLesValeurs, new BigDecimal("0.00"));
        System.arraycopy(bgs, 0, toutesLesValeurs, 0, bgs.length);
        return new TrancheBareme(inf,sup, toutesLesValeurs);
    }

    private static boolean estNonVide(String mot) {
        return !(null == mot || "".equals(mot.trim()));
    }

    private static BigDecimal bg(String mot) {
        return (mot.startsWith("-")) ? BigDecimal.ZERO : new BigDecimal(mot);
    }

    public TrancheBareme(int borneInf, int borneSup, BigDecimal[] valeurs) {
        this.borneInf = borneInf;
        this.borneSup = borneSup;
        this.valeurs = valeurs;
    }

    public int inf() {
        return borneInf;
    }

    public int sup() {
        return borneSup;
    }

    public BigDecimal valeur(int index) {
        return valeurs[index];
    }
}
