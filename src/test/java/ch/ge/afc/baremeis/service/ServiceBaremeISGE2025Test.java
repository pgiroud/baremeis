package ch.ge.afc.baremeis.service;

import org.impotch.bareme.Bareme;
import org.impotch.bareme.BaremeParTranche;
import org.impotch.util.BigDecimalUtil;
import org.impotch.util.TypeArrondi;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static ch.ge.afc.baremeis.service.ContexteTest.CTX_TST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

public class ServiceBaremeISGE2025Test {

    private static final int ANNEE = 2025;
    private static final String GE = "ge";

    private BigDecimal obtenirImpot(int revenu, String taux) {
        return TypeArrondi.CINQ_CENTIEMES_LES_PLUS_PROCHES.arrondirMontant(new BigDecimal(revenu).multiply(BigDecimalUtil.parseTaux(taux)));
    }

    @Test
    public void premiereTrancheA0_tres_grande_valeur_negative() {
        BaremeParTranche bareme = null;
        try {
            bareme = CTX_TST.getService().obtenirBaremeAnnuel(ANNEE, GE, "A0");
        } catch (Exception ex) {
            fail("Les barèmes "+ ANNEE + " ne sont pas dans le classpath !!");
        }
        BigDecimal tresGrandeValeurNegative = new BigDecimal("-100000000000000000000000000000000");
        assertThat(bareme.calcul(tresGrandeValeurNegative)).isEqualTo("0.00");
    }


    @Test
    public void codeTarifaire() {
        Set<ICodeTarifaire> codes = CTX_TST.getService().rechercherCodeTarifaire(ANNEE,GE);
        assertThat(codes).hasSize(52);
    }

    @Test
    public void baremeE() {
        // Barème pour les revenus imposés dans le cadre de la procédure de
        // décompte simplifiée
        Bareme bareme = CTX_TST.getService().obtenirBaremeMensuel(ANNEE,GE,"E0");
        // Il s'agit d'un barème à 5 %
        assertThat(bareme.calcul(100)).isEqualByComparingTo("5");
        assertThat(bareme.calcul(1_000)).isEqualByComparingTo("50");
        assertThat(bareme.calcul(10_000)).isEqualByComparingTo("500");
        assertThat(bareme.calcul(100_000)).isEqualByComparingTo("5000");
    }

    @Test
    public void baremeG() {
        // Barème pour les revenus acquis en compensation qui sont versés aux
        // personnes soumises à l'imposition à la source par une personne autre
        // que l'employeur
        // TODO PGI Plus de 600 tranches, à creuser
        Bareme bareme = CTX_TST.getService().obtenirBaremeMensuel(ANNEE,GE,"G9");
        assertThat(bareme).isNotNull();
    }

    @Test
    public void baremeQ() {
        // Barème pour les frontaliers allemands qui remplissent les conditions du
        // barème G
        // Une seule tranche à 4.5 %
        Bareme bareme = CTX_TST.getService().obtenirBaremeMensuel(ANNEE,GE,"Q9");
        assertThat(bareme.calcul(100)).isEqualByComparingTo("4.5");
        assertThat(bareme.calcul(1_000)).isEqualByComparingTo("45");
        assertThat(bareme.calcul(10_000)).isEqualByComparingTo("450");
        assertThat(bareme.calcul(100_000)).isEqualByComparingTo("4500");
    }

    @Test
    @Disabled
    public void baremeW() {
        // Bizarre, il y a 2 barèmes W9G à 4.5 % et W9N à 10 % !!
        Bareme bareme = CTX_TST.getService().obtenirBaremeMensuel(ANNEE,GE,"W9G");
        assertThat(bareme.calcul(10_000)).isEqualByComparingTo("1000");
    }
}
