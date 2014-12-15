/**
 * 
 */
package ch.ge.afc.baremeis.service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import org.impotch.bareme.BaremeTauxEffectifConstantParTranche;
import ch.ge.afc.baremeis.service.dao.BaremeImpotSourceDao;
import ch.ge.afc.baremeis.service.dao.fichierfederal.CodeTarifaire;
import ch.ge.afc.baremeis.service.dao.fichierge.CodeTarifaireGE;
import org.impotch.util.TypeArrondi;

/**
 * @author <a href="mailto:patrick.giroud@etat.ge.ch">Patrick Giroud</a>
 *
 */
public class ServiceBaremeImpotSourceImpl implements ServiceBaremeImpotSource {
	
	private BaremeImpotSourceDao dao;
	private BaremeImpotSourceDao daoge;
	private BaremeImpotSourceDao daofr;
	
	public void setDao(BaremeImpotSourceDao dao) {
		this.dao = dao;
	}

	public void setDaoge(BaremeImpotSourceDao daoge) {
		this.daoge = daoge;
	}

	public void setDaofr(BaremeImpotSourceDao daofr) {
		this.daofr = daofr;
	}

	public Set<ICodeTarifaire> rechercherBareme(int annee, String codeCanton) {
		if ("GE".equals(codeCanton.toUpperCase()) && annee < 2014) {
			return daoge.rechercherBareme(annee, codeCanton);
		} else {
			if ("FR".equals(codeCanton.toUpperCase())) {
				Set<ICodeTarifaire> codes = daofr.rechercherBareme(annee, codeCanton);
				if (null != codes) return codes;
			}
			return dao.rechercherBareme(annee, codeCanton);
		}
	}

	@Override
	public BaremeTauxEffectifConstantParTranche obtenirBaremeAnnuel(int annee, String codeCanton, String code) {
		return obtenirBaremeMensuel(annee,codeCanton,code).homothetie(BigDecimal.valueOf(12), TypeArrondi.FRANC);
	}

	
	public BaremeTauxEffectifConstantParTranche obtenirBaremeMensuel(int annee, String codeCanton, String code){
		if ("GE".equals(codeCanton.toUpperCase()) && annee < 2014) {
			if (annee < 2010) return daoge.obtenirBaremeMensuel(annee, codeCanton, CodeTarifaireGE.getParCode(code));
			else return daoge.obtenirBaremeMensuel(annee, codeCanton, new CodeTarifaire(code));
		} else {
			if ("FR".equals(codeCanton.toUpperCase())) {
				BaremeTauxEffectifConstantParTranche bareme = daofr.obtenirBaremeMensuel(annee, codeCanton, new CodeTarifaire(code));
				if (null != bareme) return bareme;
			}
			return dao.obtenirBaremeMensuel(annee, codeCanton, new CodeTarifaire(code));
		}
	}
	
	@Override
	public Set<BaremeDisponible> baremeDisponible() {
		Set<BaremeDisponible> baremes = new HashSet<BaremeDisponible>();
		baremes.addAll(daoge.baremeDisponible());
		baremes.addAll(daofr.baremeDisponible());
		baremes.addAll(dao.baremeDisponible());
		return baremes;
	}
	
}
