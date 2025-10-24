package ch.ge.afc.baremeis.service;

import org.impotch.bareme.BaremeParTranche;
import org.impotch.util.BigDecimalUtil;
import org.impotch.util.TypeArrondi;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static ch.ge.afc.baremeis.service.ContexteTest.CTX_TST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

public class ServiceBaremeISGE2024Test {

    private static final int ANNEE = 2024;

    private BigDecimal obtenirImpot(int revenu, String taux) {
        return TypeArrondi.CINQ_CENTIEMES_LES_PLUS_PROCHES.arrondirMontant(new BigDecimal(revenu).multiply(BigDecimalUtil.parseTaux(taux)));
    }

    @Test
    public void premiereTrancheA0_tres_grande_valeur_negative() {
        BaremeParTranche bareme = null;
        try {
            bareme = CTX_TST.getService().obtenirBaremeAnnuel(ANNEE, "ge", "A0");
        } catch (Exception ex) {
            fail("Les barèmes "+ ANNEE + " ne sont pas dans le classpath !!");
        }
        BigDecimal tresGrandeValeurNegative = new BigDecimal("-100000000000000000000000000000000");
        assertThat(bareme.calcul(tresGrandeValeurNegative)).isEqualTo("0.00");
    }

    @Test
    public void premiereTrancheA0_zero() {
        BaremeParTranche bareme = null;
        try {
            bareme = CTX_TST.getService().obtenirBaremeAnnuel(ANNEE, "ge", "A0");
        } catch (Exception ex) {
            fail("Les barèmes "+ ANNEE + " ne sont pas dans le classpath !!");
        }
        assertThat(bareme.calcul(BigDecimal.ZERO)).isEqualTo("0.00");
    }

    @Test
    public void premiereTrancheA0_juste_avant_saut_prochaine_tranche() {
        BaremeParTranche bareme = null;
        try {
            bareme = CTX_TST.getService().obtenirBaremeAnnuel(ANNEE, "ge", "A0");
        } catch (Exception ex) {
            fail("Les barèmes "+ ANNEE + " ne sont pas dans le classpath !!");
        }
        assertThat(bareme.calcul(BigDecimal.valueOf(28799))).isEqualTo("0.00");
    }

    @Test
    public void premiereTrancheA0_deja_sur_prochaine_tranche() {
        BaremeParTranche bareme = null;
        try {
            bareme = CTX_TST.getService().obtenirBaremeAnnuel(ANNEE, "ge", "A0");
        } catch (Exception ex) {
            fail("Les barèmes "+ ANNEE + " ne sont pas dans le classpath !!");
        }
        assertThat(bareme.calcul(BigDecimal.valueOf(28800))).isGreaterThan(BigDecimal.ZERO);
    }

    @Test
    public void derniereTrancheA0() {
        BaremeParTranche bareme = null;
        try {
            bareme = CTX_TST.getService().obtenirBaremeAnnuel(ANNEE, "ge", "A0");
        } catch (Exception ex) {
            fail("Les barèmes "+ ANNEE + " ne sont pas dans le classpath !!");
        }
        assertThat(bareme.calcul(BigDecimal.valueOf(98_106_600))).isEqualByComparingTo(new BigDecimal("41430417.20"));
    }



}
