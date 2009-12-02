/**
 * 
 */
package ch.ge.afc.baremeis.service;

import java.util.Set;

import ch.ge.afc.baremeis.service.dao.BaremeImpotSourceDao;
import ch.ge.afc.baremeis.service.dao.fichierfederal.CodeTarifaire;
import ch.ge.afc.baremeis.service.dao.fichierge.CodeTarifaireGE;
import ch.ge.afc.calcul.bareme.Bareme;

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
	
	private void validerCodeBareme(String code) {
		
	}
	
	public Bareme obtenirBaremeMensuel(int annee, String codeCanton, String code){
		if (annee < 2010 && "GE".equals(codeCanton.toUpperCase())) {
			return daoge.obtenirBaremeMensuel(annee, codeCanton, CodeTarifaireGE.getParCode(code));
		} else {
			return dao.obtenirBaremeMensuel(annee, codeCanton, new CodeTarifaire(code));
		}
	}
}
