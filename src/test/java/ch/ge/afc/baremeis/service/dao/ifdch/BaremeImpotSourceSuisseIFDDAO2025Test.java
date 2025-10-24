package ch.ge.afc.baremeis.service.dao.ifdch;

import ch.ge.afc.baremeis.service.dao.CodeTarifaire;
import org.impotch.bareme.Bareme;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BaremeImpotSourceSuisseIFDDAO2025Test {

    @Test
    public void baremeA0() {
        BaremeImpotSourceSuisseIFDDAO dao = new BaremeImpotSourceSuisseIFDDAO();
        Bareme bareme = dao.obtenirBaremeMensuel(2025, new CodeTarifaire("A0"));
        assertThat(bareme.calcul(200)).isEqualTo("0.00");
        assertThat(bareme.calcul(2425)).isEqualTo("0.00");
        assertThat(bareme.calcul(2475)).isEqualTo("27.70");
    }


}
