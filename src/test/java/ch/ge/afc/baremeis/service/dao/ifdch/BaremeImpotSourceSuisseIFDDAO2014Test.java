package ch.ge.afc.baremeis.service.dao.ifdch;

import ch.ge.afc.baremeis.service.ICodeTarifaire;
import ch.ge.afc.baremeis.service.dao.CodeTarifaire;
import org.impotch.bareme.Bareme;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class BaremeImpotSourceSuisseIFDDAO2014Test {

    @Test
    public void codesTarifaires() {
        BaremeImpotSourceSuisseIFDDAO dao = new BaremeImpotSourceSuisseIFDDAO();
        Set<ICodeTarifaire> ens = dao.rechercherCodesTarifaires(2014);
        assertThat(ens).hasSizeGreaterThan(39);
    }

    @Test
    public void baremeA0() {
        BaremeImpotSourceSuisseIFDDAO dao = new BaremeImpotSourceSuisseIFDDAO();
        Bareme bareme = dao.obtenirBaremeMensuel(2014, new CodeTarifaire("A0"));
        assertThat(bareme.calcul(200)).isEqualTo("0.00");
        assertThat(bareme.calcul(2325)).isEqualTo("0.00");
        assertThat(bareme.calcul(2375)).isEqualTo("26.95");
    }

    @Test
    public void baremeC5() {
        BaremeImpotSourceSuisseIFDDAO dao = new BaremeImpotSourceSuisseIFDDAO();
        Bareme bareme = dao.obtenirBaremeMensuel(2014, new CodeTarifaire("C5"));
        assertThat(bareme.calcul(200)).isEqualTo("0.00");
        assertThat(bareme.calcul(8375)).isEqualTo("0.00");
        assertThat(bareme.calcul(8400)).isEqualTo("0.00");
        assertThat(bareme.calcul(8401)).isEqualTo("11.75");
        assertThat(bareme.calcul(8425)).isEqualTo("11.75");
        assertThat(bareme.calcul(99951)).isEqualTo("117574.85");
        assertThat(bareme.calcul(100000)).isEqualTo("117574.85");
    }
}
