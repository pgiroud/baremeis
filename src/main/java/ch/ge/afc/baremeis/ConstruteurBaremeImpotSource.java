/**
 * 
 */
package ch.ge.afc.baremeis;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.ge.afc.calcul.bareme.BaremeTauxEffectifConstantParTranche;
import ch.ge.afc.calcul.impot.taxation.pp.SituationFamiliale;
import ch.ge.afc.util.TypeArrondi;

/**
 * @author <a href="mailto:patrick.giroud@etat.ge.ch">Patrick Giroud</a>
 *
 */
public class ConstruteurBaremeImpotSource {

	private CalculateurImpotSource calculateur;
	private int largeurCourante = -1;
	private List<PasDiscretisation> pas = new ArrayList<PasDiscretisation>();
	private boolean listeTriee = true;
	
	public void setCalculateur(CalculateurImpotSource calculateur) {
		this.calculateur = calculateur;
	}

	public ConstruteurBaremeImpotSource largeur(int largeur) {
		largeurCourante = largeur;
		return this;
	}
	
	public ConstruteurBaremeImpotSource jusqua(int montant) {
		pas.add(new PasDiscretisation(largeurCourante,montant));
		listeTriee = false;
		return this;
	}
	
	protected BigDecimal calculerTaux(SituationFamiliale situation, BigDecimal assiette) {
		BigDecimal montantImpot = calculateur.calcul(situation, assiette);
		BigDecimal taux = montantImpot.divide(assiette, 4,BigDecimal.ROUND_HALF_UP);
		return taux;
	}
	
	public BaremeTauxEffectifConstantParTranche construireBareme(SituationFamiliale situation) {
		BaremeTauxEffectifConstantParTranche bareme = new BaremeTauxEffectifConstantParTranche();
		if (!listeTriee) {
			Collections.sort(pas);
			listeTriee = true;
		}
		int maxPrecedent = 0;
		BigDecimal taux = BigDecimal.ZERO;
		for (PasDiscretisation pad : pas) {
			for (int abscisse = maxPrecedent + pad.largeur; abscisse <= pad.montantSup; abscisse += pad.largeur) {
				bareme.ajouterTranche(abscisse, taux);
				taux = calculerTaux(situation, new BigDecimal(abscisse));
			}
			maxPrecedent = pad.montantSup;
		}
		bareme.ajouterDerniereTranche(taux);
		bareme.setTypeArrondi(TypeArrondi.CINQ_CTS);
		return bareme;
	}
	
	
	private class PasDiscretisation implements Comparable<PasDiscretisation> {
		private final int largeur;
		private final int montantSup;
		
		public PasDiscretisation(int largeur, int montantSup) {
			this.largeur = largeur;
			this.montantSup = montantSup;
		}

		@Override
		public int compareTo(PasDiscretisation o) {
			if (this.montantSup < o.montantSup) return -1;
			if (this.montantSup > o.montantSup) return 1;
			return 0;
		}
		
		
	}
	
}
