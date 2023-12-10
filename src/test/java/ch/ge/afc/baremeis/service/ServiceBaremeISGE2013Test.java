package ch.ge.afc.baremeis.service;

import org.impotch.bareme.BaremeParTranche;
import org.impotch.bareme.Intervalle;
import org.impotch.bareme.TrancheBareme;
import org.junit.jupiter.api.Test;

import static ch.ge.afc.baremeis.service.ContexteTest.CTX_TST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

public class ServiceBaremeISGE2013Test {

    @Test
    public void premiereTrancheB1() {
        BaremeParTranche bareme = null;
        try {
            bareme = CTX_TST.getService().obtenirBaremeAnnuel(2013, "ge", "B1");
        } catch (Exception ex) {
            fail("Les barèmes genevois 2013 ne sont pas dans le classpath !!");
        }
        TrancheBareme tranche = ((BaremeParTranche)bareme).obtenirTranches().get(0);
        Intervalle inter = tranche.getIntervalle();
        assertThat(inter.getFin()).isEqualByComparingTo("68400");
    }
}
