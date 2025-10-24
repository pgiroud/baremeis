package ch.ge.afc.baremeis.service.dao.fichierfederal;


import java.math.BigDecimal;
import java.util.Set;

import ch.ge.afc.baremeis.service.dao.CodeTarifaire;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.impotch.bareme.Bareme;
import ch.ge.afc.baremeis.service.ICodeTarifaire;
import ch.ge.afc.baremeis.service.dao.BaremeImpotSourceDao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class BaremeImpotSourceFichierPlatDaoTest {

    @BeforeEach
    public void setUp() throws Exception {
    }

    @Test
    public void baremeCantonBizarre() {
        BaremeImpotSourceDao dao = new BaremeImpotSourceFichierPlatDao();
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> dao.rechercherCodesTarifaires(2009, "lulu"));
    }

    @Test
    public void baremeVaudois() {
        BaremeImpotSourceDao dao = new BaremeImpotSourceFichierPlatDao();
        Set<ICodeTarifaire> codes = dao.rechercherCodesTarifaires(2009, "VD");
        assertThat(10 < codes.size()).isTrue();
        // Barème revenu accessoire à 10 %
        Bareme bareme = dao.obtenirBaremeMensuel(2009, "VD", new CodeTarifaire("D0+"));
        BigDecimal impot = bareme.calcul(new BigDecimal("100000"));
        assertThat(impot).isEqualTo(new BigDecimal("10000.00"));
    }


    @Test
    public void trancheFribourgeoise() {
        BaremeImpotSourceDao dao = new BaremeImpotSourceFichierPlatDao();
        CodeTarifaire code = new CodeTarifaire("B2+");
        Bareme bareme = dao.obtenirBaremeMensuel(2009, "FR", code);
        BigDecimal impot = bareme.calcul(new BigDecimal(10000));
        assertThat(impot).isEqualTo(new BigDecimal("1057.00"));
    }


    @Test
    public void fribourg2013() {
        BaremeImpotSourceDao dao = new BaremeImpotSourceFichierPlatDao();
        CodeTarifaire code = new CodeTarifaire("B2+");
        Bareme bareme = dao.obtenirBaremeMensuel(2013, "fr", code);
        BigDecimal impot = bareme.calcul(new BigDecimal(10000));
        assertThat(impot).isEqualTo(new BigDecimal("855.00"));

    }
}
