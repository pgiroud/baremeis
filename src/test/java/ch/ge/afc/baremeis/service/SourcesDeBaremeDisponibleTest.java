package ch.ge.afc.baremeis.service;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static ch.ge.afc.baremeis.service.ContexteTest.CTX_TST;
import static org.assertj.core.api.Assertions.assertThat;

public class SourcesDeBaremeDisponibleTest {

    @Test
    public void baremeDisponible() {
        Set<BaremeDisponible> baremes = CTX_TST.getService().baremeDisponible();
        assertThat(baremes).hasSizeGreaterThan(30);
    }


}
