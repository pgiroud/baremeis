package ch.ge.afc.baremeis.service;


import java.math.BigDecimal;

import javax.annotation.Resource;

import org.impotch.bareme.Bareme;
import org.impotch.util.BigDecimalUtil;
import org.impotch.util.TypeArrondi;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

@SpringJUnitConfig(locations = {"/beansis.xml"})
public class ServiceBaremeImpotSourceTest {

    @Resource(name = "serviceBareme")
    private ServiceBaremeImpotSource service;

    private BigDecimal obtenirImpot(int revenu, String taux) {
        return TypeArrondi.CINQ_CTS.arrondirMontant(new BigDecimal(revenu).multiply(BigDecimalUtil.parseTaux(taux)));
    }

    private void test(Bareme bareme, int revenu, String taux) {
        BigDecimal impotAttendu = obtenirImpot(revenu, taux);
        BigDecimal impotCalcule = bareme.calcul(new BigDecimal(revenu));
        assertThat(impotCalcule).isEqualTo(impotAttendu);
    }

    @Test
    public void testFribourg() {
        // Barème B1+
        Bareme bareme = service.obtenirBaremeMensuel(2009, "fr", "B1+");
        test(bareme, 15000, "17.28 %");
        test(bareme, 15010, "17.33 %");
        bareme = service.obtenirBaremeMensuel(2009, "fr", "B1");
        test(bareme, 15000, "17.28 %");
        test(bareme, 15010, "17.33 %");
    }

    @Test
    public void testGeneve() {
        try {
            service.obtenirBaremeMensuel(2010, "ge", "B2");
            fail("Les barèmes 2010 ne sont pas dans le classpath !!");
        } catch (Exception ex) {

        }
    }

    @Test
    public void testPrioriteBareme() {
        Bareme baremeB = service.obtenirBaremeMensuel(2009, "ag", "B");
        Bareme baremeB0plus = service.obtenirBaremeMensuel(2009, "ag", "B0+");
        assertThat(baremeB.equals(baremeB0plus)).isTrue();
        Bareme baremeB0 = service.obtenirBaremeMensuel(2009, "ag", "B0");
        assertThat(baremeB0.equals(baremeB0plus)).isFalse();
    }

    @Test
    public void testArgovie2015() {
        Bareme baremeH2 = service.obtenirBaremeMensuel(2015, "ag", "H2");
        assertThat(baremeH2).isNotNull();
    }
}
