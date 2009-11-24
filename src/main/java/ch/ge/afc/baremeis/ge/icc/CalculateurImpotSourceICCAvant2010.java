/**
 * 
 */
package ch.ge.afc.baremeis.ge.icc;

import java.math.BigDecimal;
import java.text.NumberFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.ge.afc.calcul.assurancessociales.SituationAVS;
import ch.ge.afc.calcul.impot.FournisseurAssietteCommunale;
import ch.ge.afc.calcul.impot.cantonal.ge.pp.avant2010.FournisseurAssiettePeriodiqueGE;
import ch.ge.afc.calcul.impot.cantonal.ge.pp.avant2010.ProducteurRabaisImpot;
import ch.ge.afc.calcul.impot.cantonal.ge.pp.avant2010.SituationFamilialeGE;
import ch.ge.afc.calcul.impot.taxation.pp.FournisseurAssiettePeriodique;
import ch.ge.afc.calcul.impot.taxation.pp.RecepteurImpotSomme;
import ch.ge.afc.calcul.impot.taxation.pp.SituationFamiliale;
import ch.ge.afc.calcul.impot.taxation.pp.ge.deduction.rabais.FournisseurMontantRabaisImpot;
import ch.ge.afc.baremeis.CalculateurImpotSource;

/**
 * @author <a href="mailto:patrick.giroud@etat.ge.ch">Patrick Giroud</a>
 *
 */
public class CalculateurImpotSourceICCAvant2010 extends
		CalculateurImpotSourceICC implements CalculateurImpotSource {

    /**************************************************/
    /****************** Attributs *********************/
    /**************************************************/

	final Logger logger = LoggerFactory.getLogger(CalculateurImpotSourceICCAvant2010.class);
	private ProducteurRabaisImpot producteurRabais;
	private final FournisseurMontantRabaisImpot fournisseurRabais;
	
    /**************************************************/
    /*************** Constructeurs ********************/
    /**************************************************/

	/**
	 * @param annee
	 */
	public CalculateurImpotSourceICCAvant2010(int annee) {
		super(annee);
		fournisseurRabais = new FournisseurMontantRabaisImpot() {

			@Override
			public BigDecimal getMontantRenteAVSPercu() {
				return null;
			}

			@Override
			public BigDecimal getRevenuBrutTotaux() {
				return null;
			}

			@Override
			public BigDecimal getRevenuPourMontantAdditionnelRenteAVS() {
				return null;
			}

			@Override
			public SituationAVS getSituationAVS() {
				return null;
			}

			@Override
			public boolean hasDoubleActivite() {
				return false;
			}
			
		};
	}

	/**************************************************/
    /************ Accesseurs / Mutateurs **************/
    /**************************************************/

	public void setProducteurRabaisImpot(ProducteurRabaisImpot producteur) {
		this.producteurRabais = producteur;
	}
	
	/**************************************************/
    /******************* Méthodes *********************/
    /**************************************************/

	protected BigDecimal calculImpot(SituationFamiliale situation, BigDecimal revenuImposable) {
		SituationFamilialeGE situationGE = (SituationFamilialeGE)situation;
		FournisseurAssiettePeriodique fournisseur = this.creerAssiettes(this.getAnnee(), revenuImposable,situationGE);
		
		RecepteurImpotSomme recepteur = new RecepteurImpotSomme();
		getProducteurImpot().produireImpot(situation, fournisseur, recepteur);
		NumberFormat format = NumberFormat.getNumberInstance();
		format.setGroupingUsed(true);
		format.setMaximumFractionDigits(2);
		format.setMinimumFractionDigits(2);
		logger.debug("ICC : impôt   " + format.format(recepteur.getValeur()));
		return recepteur.getValeur();
	}
	
	private FournisseurAssiettePeriodique creerAssiettes(final int periodeFiscale, final BigDecimal montantImposable, final SituationFamilialeGE situation) {
		FournisseurAssiettePeriodiqueGE assietteFournisseur = new FournisseurAssiettePeriodiqueGE() {
			@Override
			public BigDecimal getMontantDeterminantRabais() {
				return producteurRabais.produireMontantDeterminantRabais(situation, fournisseurRabais);
			}
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
