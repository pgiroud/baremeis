/**
 * 
 */
package ch.ge.afc.baremeis.service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import org.impotch.util.StringUtil;
import org.impotch.util.TypeArrondi;

import org.impotch.bareme.BaremeParTranche;
import ch.ge.afc.baremeis.service.dao.BaremeImpotSourceDao;
import ch.ge.afc.baremeis.service.dao.fichierfederal.CodeTarifaire;
import ch.ge.afc.baremeis.service.dao.fichierge.CodeTarifaireGE;

/**
 * @author <a href="mailto:patrick.giroud@etat.ge.ch">Patrick Giroud</a>
 *
 */
public class ServiceBaremeImpotSourceImpl implements ServiceBaremeImpotSource {
	
	private final BaremeImpotSourceDao dao;
	private BaremeImpotSourceDao daoge;
	private BaremeImpotSourceDao daofr;

	public ServiceBaremeImpotSourceImpl(BaremeImpotSourceDao dao) {
		this.dao = dao;
	}

	public void setDaoge(BaremeImpotSourceDao daoge) {
		this.daoge = daoge;
	}

	public void setDaofr(BaremeImpotSourceDao daofr) {
		this.daofr = daofr;
	}

	public Set<ICodeTarifaire> rechercherCodeTarifaire(int annee, String codeCanton) {
		verifierCodeCanton(codeCanton);
		if ("GE".equals(codeCanton.toUpperCase()) && annee < 2014 && null != daoge) {
			return daoge.rechercherCodesTarifaires(annee, codeCanton);
		} else {
			if ("FR".equals(codeCanton.toUpperCase()) && null != daofr) {
				Set<ICodeTarifaire> codes = daofr.rechercherCodesTarifaires(annee, codeCanton);
				if (null != codes) return codes;
			}
			return dao.rechercherCodesTarifaires(annee, codeCanton);
		}
	}

	private void verifierCodeCanton(String codeCanton) {
		if (!StringUtil.hasText(codeCanton)) throw new RuntimeException("Le code canton doît au moins contenir du texte !!");
		codeCanton = codeCanton.trim();
		if (2 != codeCanton.length()) throw new RuntimeException("Le code canton doît être composé de 2 caractères !!");
		if (Canton.getParCode(codeCanton).isEmpty()) throw new RuntimeException("Le code canton '" + codeCanton + "'n'est pas un code de canton suisse !!");
	}

	@Override
	public BaremeParTranche obtenirBaremeAnnuel(int annee, String codeCanton, String code) {
		return obtenirBaremeMensuel(annee,codeCanton,code).homothetie(BigDecimal.valueOf(12), TypeArrondi.UNITE_LA_PLUS_PROCHE);
	}

	
	public BaremeParTranche obtenirBaremeMensuel(int annee, String codeCanton, String code){
		if ("GE".equals(codeCanton.toUpperCase()) && annee < 2014 && null != daoge) {
			if (annee < 2010) return daoge.obtenirBaremeMensuel(annee, codeCanton, CodeTarifaireGE.getParCode(code));
			else return daoge.obtenirBaremeMensuel(annee, codeCanton, new CodeTarifaire(code));
		} else {
			if ("FR".equals(codeCanton.toUpperCase()) && null != daofr) {
				BaremeParTranche bareme = daofr.obtenirBaremeMensuel(annee, codeCanton, new CodeTarifaire(code));
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
