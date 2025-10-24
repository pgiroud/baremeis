package ch.ge.afc.baremeis.service;

import org.impotch.bareme.BaremeParTranche;
import org.impotch.util.BigDecimalUtil;
import org.impotch.util.TypeArrondi;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static ch.ge.afc.baremeis.service.ContexteTest.CTX_TST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

public class ServiceBaremeISVD2025Test {

    private static final int ANNEE = 2025;
    private static final String CODE_CANTON = "vd";

    private BigDecimal obtenirImpot(int revenu, String taux) {
        return TypeArrondi.CINQ_CENTIEMES_LES_PLUS_PROCHES.arrondirMontant(new BigDecimal(revenu).multiply(BigDecimalUtil.parseTaux(taux)));
    }

    @Test
    public void premiereTrancheA0_tres_grande_valeur_negative() {
        BaremeParTranche bareme = null;
        try {
            bareme = CTX_TST.getService().obtenirBaremeAnnuel(ANNEE, CODE_CANTON, "A0");
        } catch (Exception ex) {
            fail("Les barèmes "+ ANNEE + " ne sont pas dans le classpath !!");
        }
        BigDecimal tresGrandeValeurNegative = new BigDecimal("-100000000000000000000000000000000");
        assertThat(bareme.calcul(tresGrandeValeurNegative)).isEqualTo("0.00");
    }
}
