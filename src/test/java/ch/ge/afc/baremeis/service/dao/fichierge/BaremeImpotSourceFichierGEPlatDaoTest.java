package ch.ge.afc.baremeis.service.dao.fichierge;


import java.math.BigDecimal;
import java.util.Set;

import org.impotch.bareme.Bareme;
import ch.ge.afc.baremeis.service.ICodeTarifaire;
import ch.ge.afc.baremeis.service.dao.BaremeImpotSourceDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BaremeImpotSourceFichierGEPlatDaoTest {

	@BeforeEach
	public void setUp() throws Exception {
	}

	
	@Test
	public void baremes() {
		BaremeImpotSourceDao dao = new BaremeImpotSourceFichierGEPlatDao();
		Set<ICodeTarifaire> codes = dao.rechercherCodesTarifaires(2009,"GE");
		assertThat(10 < codes.size()).isTrue();
	}
	
	@Test
	public void baremeB5() {
		BaremeImpotSourceDao dao = new BaremeImpotSourceFichierGEPlatDao();
		Bareme bareme = dao.obtenirBaremeMensuel(2009, "GE", CodeTarifaireGE.B5);
		BigDecimal impot = bareme.calcul(new BigDecimal("10000"));
		assertThat(impot).isEqualTo(new BigDecimal("1020.00"));
	}
	
	
}
