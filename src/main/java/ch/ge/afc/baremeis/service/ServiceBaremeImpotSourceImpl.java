/**
 * 
 */
package ch.ge.afc.baremeis.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.ge.afc.bareme.Bareme;
import ch.ge.afc.baremeis.service.dao.BaremeImpotSourceDao;
import ch.ge.afc.baremeis.service.dao.fichierfederal.CodeTarifaire;
import ch.ge.afc.baremeis.service.dao.fichierge.CodeTarifaireGE;

/**
 * @author <a href="mailto:patrick.giroud@etat.ge.ch">Patrick Giroud</a>
 *
 */
public class ServiceBaremeImpotSourceImpl implements ServiceBaremeImpotSource {
	
	private BaremeImpotSourceDao dao;
	private BaremeImpotSourceDao daoge;
	
	public void setDao(BaremeImpotSourceDao dao) {
		this.dao = dao;
	}

	public void setDaoge(BaremeImpotSourceDao daoge) {
		this.daoge = daoge;
	}

	public Set<ICodeTarifaire> rechercherBareme(int annee, String codeCanton) {
		if (annee < 2010 && "GE".equals(codeCanton.toUpperCase())) {
			return daoge.rechercherBareme(annee, codeCanton);
		} else {
			return dao.rechercherBareme(annee, codeCanton);
		}
	}
	
	public Bareme obtenirBaremeMensuel(int annee, String codeCanton, String code){
		if (annee < 2010 && "GE".equals(codeCanton.toUpperCase())) {
			return daoge.obtenirBaremeMensuel(annee, codeCanton, CodeTarifaireGE.getParCode(code));
		} else {
			return dao.obtenirBaremeMensuel(annee, codeCanton, new CodeTarifaire(code));
		}
	}
	
	@Override
	public Set<BaremeDisponible> baremeDisponible() {
		// TODO PGI en dur pour l'instant
		Set<BaremeDisponible> baremes = new HashSet<BaremeDisponible>();
		baremes.addAll(daoge.baremeDisponible());
		baremes.addAll(dao.baremeDisponible());
//		baremes.add(new BaremeDisponibleImpl(2009,"fr"));
//		baremes.add(new BaremeDisponibleImpl(2010,"ge"));
		return baremes;
	}
	
}
