package ch.ge.afc.baremeis.service;


import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.impotch.util.BigDecimalUtil;
import org.impotch.util.TypeArrondi;
import org.impotch.bareme.BaremeParTranche;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

import static ch.ge.afc.baremeis.service.ContexteTest.CTX_TST;


public class ServiceBaremeISGE2019Test {

    private static final int ANNEE = 2019;

    private BigDecimal obtenirImpot(int revenu, String taux) {
        return TypeArrondi.CINQ_CENTIEMES_LES_PLUS_PROCHES.arrondirMontant(new BigDecimal(revenu).multiply(BigDecimalUtil.parseTaux(taux)));
    }

    @Test
    public void premiereTrancheA0_tres_grande_valeur_negative() {
        BaremeParTranche bareme = null;

        try {
            bareme = CTX_TST.getService().obtenirBaremeAnnuel(ANNEE, "ge", "A0");
        } catch (Exception ex) {
            fail("Les barèmes 2019 ne sont pas dans le classpath !!");
        }
        BigDecimal tresGrandeValmeurNegative = new BigDecimal("-100000000000000000000000000000000");
        assertThat(bareme.calcul(tresGrandeValmeurNegative)).isEqualTo("0.00");
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
        assertThat(bareme.calcul(BigDecimal.valueOf(27600))).isEqualTo("0.00");
    }

    @Test
    public void premiereTrancheA0_deja_sur_prochaine_tranche() {
        BaremeParTranche bareme = null;
        try {
            bareme = CTX_TST.getService().obtenirBaremeAnnuel(ANNEE, "ge", "A0");
        } catch (Exception ex) {
            fail("Les barèmes "+ ANNEE + " ne sont pas dans le classpath !!");
        }
        assertThat(bareme.calcul(BigDecimal.valueOf(27601))).isGreaterThan(BigDecimal.ZERO);
    }




    @Test
    public void justeAvantDerniereTrancheA0() {
        BaremeParTranche bareme = null;
        try {
            bareme = CTX_TST.getService().obtenirBaremeAnnuel(2019, "ge", "A0");
        } catch (Exception ex) {
            fail("Les barèmes 2019 ne sont pas dans le classpath !!");
        }

        // Avant dernière tranche : fin incluse = 83150×12 = 997800 Taux = 37.02 %
        assertThat(bareme.calcul(BigDecimal.valueOf(997_800))).isEqualTo(new BigDecimal("369385.55"));
    }

    @Test
    public void derniereTrancheA0_debutTranche() {
        BaremeParTranche bareme = null;
        try {
            bareme = CTX_TST.getService().obtenirBaremeAnnuel(2019, "ge", "A0");
        } catch (Exception ex) {
            fail("Les barèmes 2019 ne sont pas dans le classpath !!");
        }

        // Dernière tranche : début exclus = 83150×12 = 997800 Taux = 37.03 %
        // Superbe effet de seuil : 1 franc de plus sur l'assiette, 100 franc de plus sur l'impôt !
        assertThat(bareme.calcul(BigDecimal.valueOf(997_801))).isEqualTo(new BigDecimal("369485.70"));
    }


    @Test
    public void derniereTrancheA0_grosMontant() {
        BaremeParTranche bareme = null;
        try {
            bareme = CTX_TST.getService().obtenirBaremeAnnuel(2019, "ge", "A0");
        } catch (Exception ex) {
            fail("Les barèmes 2019 ne sont pas dans le classpath !!");
        }
        assertThat(bareme.calcul(BigDecimal.valueOf(1_000_000))).isEqualTo(new BigDecimal("370300.00"));
    }

}
