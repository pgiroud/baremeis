package ch.ge.afc.baremeis.service;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

import org.impotch.util.BigDecimalUtil;
import org.impotch.util.TypeArrondi;
import org.impotch.bareme.BaremeParTranche;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;
import static ch.ge.afc.baremeis.service.ContexteTest.CTX_TST;

public class ServiceBaremeISGE2022Test {


    private final int annee = 2022;

    private BigDecimal obtenirImpot(int revenu, String taux) {
        return TypeArrondi.CINQ_CENTIEMES_LES_PLUS_PROCHES.arrondirMontant(new BigDecimal(revenu).multiply(BigDecimalUtil.parseTaux(taux)));
    }

    @Test
    public void premiereTrancheA0() {
        BaremeParTranche bareme = null;
        try {
            bareme = CTX_TST.getService().obtenirBaremeAnnuel(annee, "ge", "A0");
        } catch (Exception ex) {
            fail("Les barèmes "+ annee + " ne sont pas dans le classpath !!");
        }
        BigDecimal tresGrandeValeurNegative = new BigDecimal("-100000000000000000000000000000000");
        assertThat(bareme.calcul(tresGrandeValeurNegative)).isEqualTo("0.00");
        assertThat(bareme.calcul(BigDecimal.ZERO)).isEqualTo("0.00");
        assertThat(bareme.calcul(BigDecimal.valueOf(27600))).isEqualTo("0.00");
        assertThat(bareme.calcul(BigDecimal.valueOf(27601))).isGreaterThan(BigDecimal.ZERO);

//        TrancheBareme tranche = bareme.obtenirTranches().get(0);
//        Intervalle intervalle = tranche.getIntervalle();
//        assertThat(intervalle.isDebutMoinsInfini()).isTrue();
//        assertThat(intervalle.getFin()).isEqualByComparingTo(BigDecimal.valueOf(27600));
//        assertThat(tranche.getTauxOuMontant()).isEqualByComparingTo(BigDecimal.ZERO);
    }

}
