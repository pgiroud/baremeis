/**
 * 
 */
package ch.ge.afc.baremeis.service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.impotch.bareme.BaremeTauxEffectifConstantParTranche;
import org.impotch.util.TypeArrondi;

/**
 * @author <a href="mailto:patrick.giroud@etat.ge.ch">Patrick Giroud</a>
 *
 */
public class ServiceBaremeImpotSourceCache implements ServiceBaremeImpotSource {

	private ConcurrentMap<CleCache,Set<ICodeTarifaire>> codeTarifaireParCantonAnnee = new ConcurrentHashMap<CleCache,Set<ICodeTarifaire>>(); 
	private Set<BaremeDisponible> listeBaremeDisponible;
	private Object synchroBaremeDispo = new Object();
	
	// Injection de dépendance
	private ServiceBaremeImpotSource cible;
	
	public void setCible(ServiceBaremeImpotSource cible) {
		this.cible = cible;
	}

	private String choisirCode(String codeSaisi, Set<ICodeTarifaire> codes, boolean isExactPrioritaire) {
		// On choisit en priorité le code qui correspond exactement
		if (isExactPrioritaire) {
			for (ICodeTarifaire code : codes) {
				if (code.getCode().trim().equalsIgnoreCase(codeSaisi)) return code.getCode().trim();
			}
		}
		// Ensuite, on choisit en priorité un code avec part écclésiastique
		for (ICodeTarifaire code : codes) {
			if (code.getCode().trim().endsWith("+")) return code.getCode().trim();
		}
		// Sinon, on choisit le dissident
		for (ICodeTarifaire code : codes) {
			if (code.getCode().trim().endsWith("d")) return code.getCode().trim();
		}
		if (!isExactPrioritaire) {
			for (ICodeTarifaire code : codes) {
				if (code.getCode().trim().equalsIgnoreCase(codeSaisi)) return code.getCode().trim();
			}
		}
		// Sinon, le premier qui vient
		return codes.iterator().next().getCode();
	}
	
	/* (non-Javadoc)
	 * @see ch.ge.afc.baremeis.service.ServiceBaremeImpotSource#obtenirBaremeMensuel(int, java.lang.String, java.lang.String)
	 */
	@Override
	public BaremeTauxEffectifConstantParTranche obtenirBaremeMensuel(int annee, String codeCanton, String code) {
		boolean complet = true;
		String codeComplet = code;
		if (1 == code.length()) {
			complet = false;
			codeComplet =  code + "0";
		}
		Set<ICodeTarifaire> codes = rechercherBareme(annee,codeCanton);
		Set<ICodeTarifaire> codesFiltres = new HashSet<ICodeTarifaire>();
		for (ICodeTarifaire codeTarif : codes) {
			if (codeTarif.getCode().toUpperCase().startsWith(codeComplet.toUpperCase())) {
				codesFiltres.add(codeTarif);
			}
		}
		if (codesFiltres.isEmpty()) return null;
		String codeChoisi = null;
		if (1 == codesFiltres.size()) codeChoisi = codesFiltres.iterator().next().getCode();
		else codeChoisi = choisirCode(codeComplet,codesFiltres,complet);
		return cible.obtenirBaremeMensuel(annee, codeCanton,codeChoisi);
	}

	@Override
	public BaremeTauxEffectifConstantParTranche obtenirBaremeAnnuel(int annee, String codeCanton, String code) {
		return obtenirBaremeMensuel(annee,codeCanton,code).homothetie(BigDecimal.valueOf(12), TypeArrondi.FRANC);
	}

	/* (non-Javadoc)
	 * @see ch.ge.afc.baremeis.service.ServiceBaremeImpotSource#rechercherBareme(int, java.lang.String)
	 */
	@Override
	public Set<ICodeTarifaire> rechercherBareme(int annee, String codeCanton) {
		CleCache cle = new CleCache(annee, codeCanton);
		if (!codeTarifaireParCantonAnnee.containsKey(cle)) {
			codeTarifaireParCantonAnnee.putIfAbsent(cle, cible.rechercherBareme(annee, codeCanton));
		}
		return codeTarifaireParCantonAnnee.get(cle);
	}

	@Override
	public Set<BaremeDisponible> baremeDisponible() {
		synchronized (synchroBaremeDispo) {
			if (null == listeBaremeDisponible) {
				listeBaremeDisponible = cible.baremeDisponible();
			}
		}
		return listeBaremeDisponible; 
	}

	private class CleCache {
		
		private final int annee;
		private final String codeCanton;
		
		public CleCache(int annee, String codeCanton) {
			this.annee = annee;
			this.codeCanton = codeCanton.toLowerCase();
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof CleCache)) return false;
			CleCache cle = (CleCache)obj;
			return this.annee == cle.annee && this.codeCanton.equals(cle.codeCanton);
		}

		@Override
		public int hashCode() {
			int result = 17;
			result = 37 * result + annee;
			result = 37 * result + codeCanton.hashCode();
			return result;
		}
		
		
	}
}
