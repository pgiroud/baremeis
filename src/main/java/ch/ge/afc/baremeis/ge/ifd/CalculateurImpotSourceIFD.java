/**
 * 
 */
package ch.ge.afc.baremeis.ge.ifd;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.ge.afc.baremeis.ge.ICalculCotisationSociale;
import ch.ge.afc.calcul.ReglePeriodique;
import ch.ge.afc.calcul.impot.FournisseurAssietteCommunale;
import ch.ge.afc.calcul.impot.Souverainete;
import ch.ge.afc.calcul.impot.taxation.pp.DeductionSociale;
import ch.ge.afc.calcul.impot.taxation.pp.EnfantACharge;
import ch.ge.afc.calcul.impot.taxation.pp.FournisseurAssiettePeriodique;
import ch.ge.afc.calcul.impot.taxation.pp.ProducteurImpot;
import ch.ge.afc.calcul.impot.taxation.pp.RecepteurImpotSomme;
import ch.ge.afc.calcul.impot.taxation.pp.SituationFamiliale;
import ch.ge.afc.baremeis.CalculateurImpotSource;
import ch.ge.afc.util.TypeArrondi;

/**
 * @author <a href="mailto:patrick.giroud@etat.ge.ch">Patrick Giroud</a>
 *
 */
public class CalculateurImpotSourceIFD extends ReglePeriodique implements
		CalculateurImpotSource {

    /**************************************************/
    /****************** Attributs *********************/
    /**************************************************/

	final Logger logger = LoggerFactory.getLogger(CalculateurImpotSourceIFD.class);
	
	// Cotisations diverses
	private List<ICalculCotisationSociale> reglesCotisations;
	private BigDecimal taux2emePilier;
	
	// Frais professionnels
	private BigDecimal tauxFraisProfessionnel;
	private BigDecimal plancherFraisprofessionnel;
	private BigDecimal plafondFraisProfessionnel;
	private BigDecimal fraisProfessionnelDeplacementRepas;

	private BigDecimal tauxPrimeAssIntCapitEpargneCelibataire;
	private BigDecimal plafondPrimeAssIntCapitEpargneCelibataire;
	private BigDecimal tauxPrimeAssIntCapitEpargneCouple;
	private BigDecimal plafondPrimeAssIntCapitEpargneCouple;
	private BigDecimal primeAssIntCapitEpargneParEnfant;

	List<DeductionSociale> deducSociales = new ArrayList<DeductionSociale>();
	
	private ProducteurImpot producteurImpot;
	
	
    /**************************************************/
    /*************** Constructeurs ********************/
    /**************************************************/

	public CalculateurImpotSourceIFD(int annee) {
		super(annee);
	}

    /**************************************************/
    /************* Accesseurs / Mutateurs *************/
    /**************************************************/

	public void setRegleCalculCotisations(List<ICalculCotisationSociale> regles) {
		this.reglesCotisations = regles;
	}
	
	/**
	 * @param taux2emePilierIFD the taux2emePilierIFD to set
	 */
	public void setTaux2emePilier(BigDecimal taux2emePilierIFD) {
		this.taux2emePilier = taux2emePilierIFD;
	}

	
	
	
    /**
	 * @param tauxFraisProfessionnel the tauxFraisProfessionnel to set
	 */
	public void setTauxFraisProfessionnel(BigDecimal tauxFraisProfessionnel) {
		this.tauxFraisProfessionnel = tauxFraisProfessionnel;
	}

	/**
	 * @param plancherFraisprofessionnel the plancherFraisprofessionnelIFD to set
	 */
	public void setPlancherFraisprofessionnel(
			BigDecimal plancherFraisprofessionnel) {
		this.plancherFraisprofessionnel = plancherFraisprofessionnel;
	}

	/**
	 * @param plafondFraisProfessionnel the plafondFraisProfessionnel to set
	 */
	public void setPlafondFraisProfessionnel(
			BigDecimal plafondFraisProfessionnel) {
		this.plafondFraisProfessionnel = plafondFraisProfessionnel;
	}

	/**
	 * Spécifie la déduction combinée pour les frais de déplacement et les repas
	 * pris à l'extérieur.
	 * 
	 * @param fraisProfessionnelDeplacementRepas the fraisProfessionnelDeplacementRepasIFD to set
	 */
	public void setFraisProfessionnelDeplacementRepas(
			BigDecimal fraisProfessionnelDeplacementRepas) {
		this.fraisProfessionnelDeplacementRepas = fraisProfessionnelDeplacementRepas;
	}

	
	
	
	/**
	 * @param tauxPrimeAssIntCapitEpargneCelibataire the tauxPrimeAssIntCapitEpargneCelibataire to set
	 */
	public void setTauxPrimeAssIntCapitEpargneCelibataire(
			BigDecimal tauxPrimeAssIntCapitEpargneCelibataire) {
		this.tauxPrimeAssIntCapitEpargneCelibataire = tauxPrimeAssIntCapitEpargneCelibataire;
	}

	/**
	 * @param plafondPrimeAssIntCapitEpargneCelibataire the plafondPrimeAssIntCapitEpargneCelibataire to set
	 */
	public void setPlafondPrimeAssIntCapitEpargneCelibataire(
			BigDecimal plafondPrimeAssIntCapitEpargneCelibataire) {
		this.plafondPrimeAssIntCapitEpargneCelibataire = plafondPrimeAssIntCapitEpargneCelibataire;
	}

	/**
	 * @param tauxPrimeAssIntCapitEpargneCouple the tauxPrimeAssIntCapitEpargneCouple to set
	 */
	public void setTauxPrimeAssIntCapitEpargneCouple(
			BigDecimal tauxPrimeAssIntCapitEpargneCouple) {
		this.tauxPrimeAssIntCapitEpargneCouple = tauxPrimeAssIntCapitEpargneCouple;
	}

	/**
	 * @param plafondPrimeAssIntCapitEpargneCouple the plafondPrimeAssIntCapitEpargneCouple to set
	 */
	public void setPlafondPrimeAssIntCapitEpargneCouple(
			BigDecimal plafondPrimeAssIntCapitEpargneCouple) {
		this.plafondPrimeAssIntCapitEpargneCouple = plafondPrimeAssIntCapitEpargneCouple;
	}

	/**
	 * @param primeAssIntCapitEpargneParEnfant the primeAssIntCapitEpargneParEnfant to set
	 */
	public void setPrimeAssIntCapitEpargneParEnfant(
			BigDecimal primeAssIntCapitEpargneParEnfant) {
		this.primeAssIntCapitEpargneParEnfant = primeAssIntCapitEpargneParEnfant;
	}

	/**
	 * @param producteurImpot the producteurImpot to set
	 */
	public void setProducteurImpot(ProducteurImpot producteurImpot) {
		this.producteurImpot = producteurImpot;
	}

	/**************************************************/
    /******************* Méthodes *********************/
    /**************************************************/

	public void ajouterDeductionSociale(DeductionSociale deduction) {
		deducSociales.add(deduction);
	}
	
	
	protected BigDecimal estimerMontantAssuranceMaladieEtIntCapitEpargne(SituationFamiliale situation, BigDecimal revenuBrut) {
		BigDecimal estimation = BigDecimal.ZERO;
		if (situation.isCouple()) {
			estimation = TypeArrondi.CINQ_CTS.arrondirMontant(revenuBrut.multiply(tauxPrimeAssIntCapitEpargneCouple)).min(plafondPrimeAssIntCapitEpargneCouple);
		} else {
			estimation = TypeArrondi.CINQ_CTS.arrondirMontant(revenuBrut.multiply(tauxPrimeAssIntCapitEpargneCelibataire)).min(plafondPrimeAssIntCapitEpargneCelibataire);
		}
		for (EnfantACharge enfant : situation.getEnfants()) {
			estimation = estimation.add(enfant.isDemiPart(Souverainete.FEDERALE) ? primeAssIntCapitEpargneParEnfant.divide(new BigDecimal(2), 0, BigDecimal.ROUND_HALF_UP) : primeAssIntCapitEpargneParEnfant);
		}
		return estimation;
	}
	
	@Override
	public BigDecimal calcul(SituationFamiliale situation, BigDecimal revenuBrut) {
		NumberFormat format = NumberFormat.getNumberInstance();
		format.setGroupingUsed(true);
		format.setMaximumFractionDigits(2);
		format.setMinimumFractionDigits(2);

		BigDecimal cotisations = BigDecimal.ZERO;
		for (ICalculCotisationSociale regle : this.reglesCotisations) {
			cotisations = cotisations.add(regle.calcul(revenuBrut));
		}
		BigDecimal assuranceMaladieEtIntCapitEpargne = this.estimerMontantAssuranceMaladieEtIntCapitEpargne(situation, revenuBrut);
		logger.debug("IFD : ass. mal. et int. capit. " + format.format(assuranceMaladieEtIntCapitEpargne));

		BigDecimal montant2emePilier = TypeArrondi.CINQ_CTS.arrondirMontant(revenuBrut.multiply(taux2emePilier));
		
		BigDecimal deductionFraisProfessionnel = TypeArrondi.CINQ_CTS.arrondirMontant(revenuBrut.multiply(tauxFraisProfessionnel))
		.min(plafondFraisProfessionnel).max(plancherFraisprofessionnel).add(fraisProfessionnelDeplacementRepas);
		
		BigDecimal deducSociale = BigDecimal.ZERO;
		for (DeductionSociale deduc : deducSociales) {
			deducSociale = deducSociale.add(deduc.getMontantDeduction(situation));
		}
		BigDecimal revenuImposable = revenuBrut.subtract(cotisations)
										.subtract(assuranceMaladieEtIntCapitEpargne)
										.subtract(montant2emePilier)
										.subtract(deductionFraisProfessionnel)
										.subtract(deducSociale);
		logger.debug("IFD : rev.imposable   " + format.format(revenuImposable));
		
		FournisseurAssiettePeriodique fournisseur = this.creerAssiettes(this.getAnnee(), revenuImposable);
		RecepteurImpotSomme recepteur = new RecepteurImpotSomme();
		
		producteurImpot.produireImpot(situation, fournisseur, recepteur);
		logger.debug("IFD : impôt   " + format.format(recepteur.getValeur()));
		return recepteur.getValeur();
	}
	
	protected FournisseurAssiettePeriodique creerAssiettes(final int periodeFiscale, final BigDecimal montantImposable) {
		FournisseurAssiettePeriodique assietteFournisseur = new FournisseurAssiettePeriodique() {

			
			@Override
			public int getNombreJourPourAnnualisation() {
				return 360;
			}

			@Override
			public int getPeriodeFiscale() {
				return periodeFiscale;
			}

			@Override
			public BigDecimal getMontantDeterminant() {
				return montantImposable;
			}

			@Override
			public BigDecimal getMontantImposable() {
				return montantImposable;
			}

			@Override
			public FournisseurAssietteCommunale getFournisseurAssietteCommunale() {
				return null;
			}
		};
		return assietteFournisseur;
	}

}
