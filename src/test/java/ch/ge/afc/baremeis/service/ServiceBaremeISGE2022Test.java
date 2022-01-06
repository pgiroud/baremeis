package ch.ge.afc.baremeis.service;

import org.impotch.bareme.BaremeTauxEffectifConstantParTranche;
import org.impotch.bareme.Intervalle;
import org.impotch.bareme.TrancheBareme;
import org.impotch.util.BigDecimalUtil;
import org.impotch.util.TypeArrondi;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import javax.annotation.Resource;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

@SpringJUnitConfig(locations = {"/beansis.xml"})
public class ServiceBaremeISGE2022Test {
    @Resource(name = "serviceBareme")
    private ServiceBaremeImpotSource service;

    private final int annee = 2022;

    private BigDecimal obtenirImpot(int revenu, String taux) {
        return TypeArrondi.CINQ_CTS.arrondirMontant(new BigDecimal(revenu).multiply(BigDecimalUtil.parseTaux(taux)));
    }

    @Test
    public void premiereTrancheA0() {
        BaremeTauxEffectifConstantParTranche bareme = null;
        try {
            bareme = service.obtenirBaremeAnnuel(annee, "ge", "A0");
        } catch (Exception ex) {
            fail("Les barèmes "+ annee + " ne sont pas dans le classpath !!");
        }
        TrancheBareme tranche = bareme.obtenirTranches().get(0);
        Intervalle intervalle = tranche.getIntervalle();
        assertThat(intervalle.isDebutMoinsInfini()).isTrue();
        assertThat(intervalle.getFin()).isEqualByComparingTo(BigDecimal.valueOf(27600));
        assertThat(tranche.getTauxOuMontant()).isEqualByComparingTo(BigDecimal.ZERO);
    }

}
