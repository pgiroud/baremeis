/**
 * 
 */
package ch.ge.afc.baremeis.ge.icc;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.ge.afc.calcul.ReglePeriodique;
import ch.ge.afc.calcul.impot.FournisseurAssietteCommunale;
import ch.ge.afc.calcul.impot.Souverainete;
import ch.ge.afc.baremeis.ge.ICalculCotisationSociale;
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
public class CalculateurImpotSourceICC extends ReglePeriodique implements
		CalculateurImpotSource {

    /**************************************************/
    /****************** Attributs *********************/
    /**************************************************/

	final Logger logger = LoggerFactory.getLogger(CalculateurImpotSourceICC.class);
	
	// Cotisations diverses
	private List<ICalculCotisationSociale> reglesCotisations;
	private ICalculCotisationSociale regleAANP;
	private BigDecimal taux2emePilier;
	
	// Frais professionnels
	private BigDecimal tauxFraisProfessionnel;
	private BigDecimal plancherFraisprofessionnel;
	private BigDecimal plafondFraisProfessionnel;
	
	private BigDecimal tauxFraisMedicaux;
	private BigDecimal plafondFraisMedicaux;
	
	private BigDecimal cotisationAssuranceMaladieParEpoux;
	private BigDecimal cotisationAssuranceMaladieEnfant;
	
	List<DeductionSociale> deducSociales = new ArrayList<DeductionSociale>();
	
	private ProducteurImpot producteurImpot;

	
	
    /**************************************************/
    /*************** Constructeurs ********************/
    /**************************************************/

	/**
	 * @param annee
	 */
	public CalculateurImpotSourceICC(int annee) {
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
	 * @param producteurImpot the producteurImpot to set
	 */
	public void setProducteurImpot(ProducteurImpot producteurImpot) {
		this.producteurImpot = producteurImpot;
	}

	/**
	 * @param tauxFraisMedicaux the tauxFraisMedicaux to set
	 */
	public void setTauxFraisMedicaux(BigDecimal tauxFraisMedicaux) {
		this.tauxFraisMedicaux = tauxFraisMedicaux;
	}

	/**
	 * @param plafondFraisMedicaux the plafondFraisMedicaux to set
	 */
	public void setPlafondFraisMedicaux(BigDecimal plafondFraisMedicaux) {
		this.plafondFraisMedicaux = plafondFraisMedicaux;
	}

	/**
	 * @param cotisationAssuranceMaladieParEpoux the cotisationAssuranceMaladieParEpoux to set
	 */
	public void setCotisationAssuranceMaladieParEpoux(
			BigDecimal cotisationAssuranceMaladieParEpoux) {
		this.cotisationAssuranceMaladieParEpoux = cotisationAssuranceMaladieParEpoux;
	}

	/**
	 * @param cotisationAssuranceMaladieEnfant the cotisationAssuranceMaladieEnfant to set
	 */
	public void setCotisationAssuranceMaladieEnfant(
			BigDecimal cotisationAssuranceMaladieEnfant) {
		this.cotisationAssuranceMaladieEnfant = cotisationAssuranceMaladieEnfant;
	}

	
	/**
	 * @param regleAANP the regleAANP to set
	 */
	public void setRegleAANP(ICalculCotisationSociale regleAANP) {
		this.regleAANP = regleAANP;
	}

	public void ajouterDeductionSociale(DeductionSociale deduction) {
		deducSociales.add(deduction);
	}
	
	protected ProducteurImpot getProducteurImpot() {
		return producteurImpot;
	}
	
	/**************************************************/
    /******************* Méthodes *********************/
    /**************************************************/

	protected BigDecimal estimerMontantAssuranceMaladie(SituationFamiliale situation) {
		BigDecimal cotisation = cotisationAssuranceMaladieParEpoux;
		if (situation.isCouple()) {
			cotisation = cotisation.add(cotisationAssuranceMaladieParEpoux);
		}
		for (EnfantACharge enfant : situation.getEnfants()) {
			cotisation = cotisation.add(enfant.isDemiPart(Souverainete.CANTONALE) ? cotisationAssuranceMaladieEnfant.divide(new BigDecimal(2), 0, BigDecimal.ROUND_HALF_UP) : cotisationAssuranceMaladieEnfant);
		}
		return cotisation;
	}
	
	
	/* (non-Javadoc)
	 * @see ch.ge.afc.calcul.impot.taxation.pp.source.CalculateurImpotSource#calcul(ch.ge.afc.calcul.impot.taxation.pp.SituationFamiliale, java.math.BigDecimal)
	 */
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
		BigDecimal montant2emePilier = TypeArrondi.CINQ_CTS.arrondirMontant(revenuBrut.multiply(taux2emePilier));
		logger.debug("ICC : cot. 2ème pilier   " + format.format(montant2emePilier));
		
		BigDecimal revenuNet = revenuBrut.subtract(cotisations)
				.subtract(montant2emePilier);
		
		BigDecimal deductionFraisProfessionnel = TypeArrondi.CINQ_CTS.arrondirMontant(revenuNet.multiply(tauxFraisProfessionnel))
										.min(plafondFraisProfessionnel).max(plancherFraisprofessionnel);
		logger.debug("ICC : déduc. frais prof.   " + format.format(deductionFraisProfessionnel));
		
		BigDecimal fraisMedicauxGE = plafondFraisMedicaux.min(TypeArrondi.CINQ_CTS.arrondirMontant(revenuBrut.multiply(tauxFraisMedicaux)));
		logger.debug("ICC : déduc. frais médic.   " + format.format(fraisMedicauxGE));

		BigDecimal assuranceMaladie = estimerMontantAssuranceMaladie(situation);
		logger.debug("ICC : déduc. ass. maladie " + format.format(assuranceMaladie));

		BigDecimal assuranceAccidentNonProfessionnel = regleAANP.calcul(revenuBrut);
		logger.debug("ICC : déduc. AANP   " + format.format(assuranceAccidentNonProfessionnel));
		
		
		// Déductions sociales après 2010
		BigDecimal deducSociale = BigDecimal.ZERO;
		for (DeductionSociale deduc : deducSociales) {
			deducSociale = deducSociale.add(deduc.getMontantDeduction(situation));
		}
		
		
		BigDecimal revenuImposable = revenuNet.subtract(deductionFraisProfessionnel).subtract(fraisMedicauxGE)
										.subtract(assuranceMaladie).subtract(assuranceAccidentNonProfessionnel)
										.subtract(deducSociale);
		logger.debug("ICC : rev. imposable   " + format.format(revenuImposable));
		return calculImpot(situation,revenuImposable);
	}

	protected BigDecimal calculImpot(SituationFamiliale situation, BigDecimal revenuImposable) {
		FournisseurAssiettePeriodique fournisseur = this.creerAssiettes(this.getAnnee(), revenuImposable);
		
		RecepteurImpotSomme recepteur = new RecepteurImpotSomme();
		getProducteurImpot().produireImpot(situation, fournisseur, recepteur);
		NumberFormat format = NumberFormat.getNumberInstance();
		format.setGroupingUsed(true);
		format.setMaximumFractionDigits(2);
		format.setMinimumFractionDigits(2);
		logger.debug("ICC : impôt   " + format.format(recepteur.getValeur()));
		return recepteur.getValeur();
	}
	
	
	private FournisseurAssiettePeriodique creerAssiettes(final int periodeFiscale, final BigDecimal montantImposable) {
		FournisseurAssiettePeriodique fournisseur = new FournisseurAssiettePeriodique() {
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
		return fournisseur;
	}
}
