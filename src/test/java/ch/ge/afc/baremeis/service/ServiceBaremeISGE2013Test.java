package ch.ge.afc.baremeis.service;

import org.impotch.bareme.BaremeParTranche;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

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
        BigDecimal abscisse = BigDecimal.ONE;
        BigDecimal ordonnee = BigDecimal.ZERO;
        while (0 == ordonnee.compareTo(BigDecimal.ZERO)) {
            abscisse = abscisse.add(BigDecimal.valueOf(100));
            ordonnee = bareme.calcul(abscisse);
        }

        assertThat(abscisse).asString().isEqualTo("68401");
    }
}
