package ch.ge.afc.baremeis.service;

import org.impotch.bareme.*;
import org.impotch.util.BigDecimalUtil;
import org.impotch.util.TypeArrondi;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import javax.annotation.Resource;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

@SpringJUnitConfig(locations = {"/beansis.xml"})
public class ServiceBaremeISGE2019Test {

    @Resource(name = "serviceBareme")
    private ServiceBaremeImpotSource service;

    private BigDecimal obtenirImpot(int revenu, String taux) {
        return TypeArrondi.CINQ_CENTIEMES_LES_PLUS_PROCHES.arrondirMontant(new BigDecimal(revenu).multiply(BigDecimalUtil.parseTaux(taux)));
    }

    @Test
    public void premiereTrancheA0() {
        BaremeParTranche bareme = null;
        try {
            bareme = service.obtenirBaremeAnnuel(2019, "ge", "A0");
        } catch (Exception ex) {
            fail("Les barèmes 2019 ne sont pas dans le classpath !!");
        }
        BigDecimal tresGrandeValmeurNegative = new BigDecimal("-100000000000000000000000000000000");
        assertThat(bareme.calcul(tresGrandeValmeurNegative)).isEqualTo("0.00");
        assertThat(bareme.calcul(BigDecimal.ZERO)).isEqualTo("0.00");
        assertThat(bareme.calcul(BigDecimal.valueOf(27600))).isEqualTo("0.00");
        assertThat(bareme.calcul(BigDecimal.valueOf(27601))).isGreaterThan(BigDecimal.ZERO);
    }

    @Test
    public void derniereTrancheA0() {
        BaremeParTranche bareme = null;
        try {
            bareme = service.obtenirBaremeAnnuel(2019, "ge", "A0");
        } catch (Exception ex) {
            fail("Les barèmes 2019 ne sont pas dans le classpath !!");
        }

        // Avant dernière tranche : fin incluse = 83150×12 = 997800 Taux = 37.02 %
        assertThat(bareme.calcul(BigDecimal.valueOf(997_800))).isEqualTo(new BigDecimal("369385.55"));
        // Dernière tranche : début exclus = 83150×12 = 997800 Taux = 37.03 %
        // Superbe effet de seuil : 1 franc de plus sur l'assiette, 100 franc de plus sur l'impôt !
        assertThat(bareme.calcul(BigDecimal.valueOf(997_801))).isEqualTo(new BigDecimal("369485.70"));
        assertThat(bareme.calcul(BigDecimal.valueOf(1_000_000))).isEqualTo(new BigDecimal("370300.00"));

//
//        TrancheBareme tranche = bareme.obtenirTranches().get(bareme.obtenirTranches().size()-1);
//        Intervalle intervalle = tranche.getIntervalle();
//        assertThat(intervalle.getDebut()).isEqualByComparingTo(BigDecimal.valueOf(83150*12));
//        assertThat(intervalle.isFinPlusInfini()).isTrue();
//        assertThat(tranche.getTauxOuMontant()).isEqualByComparingTo(new BigDecimal("0.3703"));
    }


}
