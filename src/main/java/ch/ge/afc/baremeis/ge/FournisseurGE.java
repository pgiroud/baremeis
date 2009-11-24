/**
 * 
 */
package ch.ge.afc.baremeis.ge;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import ch.ge.afc.baremeis.CalculateurImpotSource;
import ch.ge.afc.baremeis.FournisseurCalculateurIS;
import ch.ge.afc.baremeis.ge.icc.CalculateurImpotSourceICC;
import ch.ge.afc.baremeis.ge.icc.CalculateurImpotSourceICCAvant2010;
import ch.ge.afc.baremeis.ge.ifd.CalculateurImpotSourceIFD;
import ch.ge.afc.calcul.assurancessociales.FournisseurRegleCalculAssuranceSociale;
import ch.ge.afc.calcul.assurancessociales.ge.CalculCotisationsSocialesSalarieGE;
import ch.ge.afc.calcul.impot.ProducteurImpotDerivePourcent;
import ch.ge.afc.calcul.impot.cantonal.ge.pp.FournisseurRegleImpotCantonalGE;
import ch.ge.afc.calcul.impot.federal.FournisseurRegleImpotFederal;
import ch.ge.afc.calcul.impot.taxation.pp.DeductionSociale;
import ch.ge.afc.calcul.impot.taxation.pp.ProducteurImpot;
import ch.ge.afc.util.BigDecimalUtil;
import ch.ge.afc.util.ExplicationDetailleTexteBuilder;
import ch.ge.afc.util.IExplicationDetailleeBuilder;
import ch.ge.afc.util.TypeArrondi;

/**
 * @author <a href="mailto:patrick.giroud@etat.ge.ch">Patrick Giroud</a>
 *
 */
public class FournisseurGE implements FournisseurCalculateurIS {

	private FournisseurRegleCalculAssuranceSociale fournisseurRegleCalculCotisationAssuranceSociale;
	private FournisseurRegleImpotCantonalGE fournisseurRegleImpotCantonal;
	private FournisseurRegleImpotFederal fournisseurRegleImpotFederal;
	private ConcurrentMap<Integer, ProducteurImpot> producteurImpotSourceRevenu = new ConcurrentHashMap<Integer, ProducteurImpot>();
	private ConcurrentMap<Integer,CalculateurImpotSource> calculateursIS = new ConcurrentHashMap<Integer,CalculateurImpotSource>();
	private ConcurrentMap<Integer,CalculateurImpotSource> calculateursISIFD = new ConcurrentHashMap<Integer,CalculateurImpotSource>();
	private ConcurrentMap<Integer,CalculateurImpotSource> calculateursISICC = new ConcurrentHashMap<Integer,CalculateurImpotSource>();
	
	public void setFournisseurRegleCalculCotisationAssuranceSociale(
			FournisseurRegleCalculAssuranceSociale fournisseurRegleCalculCotisationAssuranceSociale) {
		this.fournisseurRegleCalculCotisationAssuranceSociale = fournisseurRegleCalculCotisationAssuranceSociale;
	}

	public void setFournisseurRegleImpotCantonal(FournisseurRegleImpotCantonalGE fournisseur) {
		fournisseurRegleImpotCantonal = fournisseur;
	}
	
	/**
	 * @param fournisseurRegleImpotFederal the fournisseurRegleImpotFederal to set
	 */
	public void setFournisseurRegleImpotFederal(
			FournisseurRegleImpotFederal fournisseurRegleImpotFederal) {
		this.fournisseurRegleImpotFederal = fournisseurRegleImpotFederal;
	}

	private IExplicationDetailleeBuilder getNewExplicationBuilder() {
		return new ExplicationDetailleTexteBuilder();
	}


	private ProducteurImpot construireProducteurImpotRevenuSource(int annee) {
		ProducteurImpot producteur = fournisseurRegleImpotCantonal.construireProducteurImpotsCantonauxRevenu(annee);
		ProducteurImpotDerivePourcent prodCOR = null;
		if (annee < 2009) {
			prodCOR = new ProducteurImpotDerivePourcent("COR","45 %","COM");
		} else {
			prodCOR = new ProducteurImpotDerivePourcent("COR","44.5 %","COM");
		}
		IExplicationDetailleeBuilder explication = getNewExplicationBuilder();
		explication.ajouter("Cts additionnels communaux :");
		explication.ajouter("impôt de base revenu ({0,number,#,##0.00}) * {1,number,percent}");
		explication.ajouter("{2,number,#,##0.00}");
		prodCOR.setExplicationDetailleePattern(explication.getTexte());
		producteur.ajouteProducteurDerive(prodCOR);
		return producteur;
	}
	
	public ProducteurImpot getProducteurImpotSourceRevenu(int annee) {
		if (!producteurImpotSourceRevenu.containsKey(annee)) {
			producteurImpotSourceRevenu.putIfAbsent(annee, this.construireProducteurImpotRevenuSource(annee));
		}
		return producteurImpotSourceRevenu.get(annee);
	}
	
	public CalculateurImpotSource getCalculateurImpotSource(int annee) {
		if (!calculateursIS.containsKey(annee)) {
			calculateursIS.putIfAbsent(annee, construireCalculateurIS(annee));
		}
		return calculateursIS.get(annee);
	}

	public CalculateurImpotSource getCalculateurImpotSourceIFD(int annee) {
		if (!calculateursISIFD.containsKey(annee)) {
			calculateursISIFD.putIfAbsent(annee, this.construireCalculateurISIFD(annee));
		}
		return calculateursISIFD.get(annee);
	}
	
	protected CalculCotisationsSocialesSalarieGE getCalculateurCotiSociale(int annee) {
		return fournisseurRegleCalculCotisationAssuranceSociale.getCalculateurCotisationsSocialesSalarieGE(annee);
	}

	protected List<ICalculCotisationSociale> construireRegleCotisationSociale(int annee) {
		final CalculCotisationsSocialesSalarieGE calculateurCotiSociale = getCalculateurCotiSociale(annee);
		List<ICalculCotisationSociale> cotisations = new ArrayList<ICalculCotisationSociale>();
		cotisations.add(new ICalculCotisationSociale() {
			@Override
			public String getNomCotisation() {
				return "Cotisations AVS/AI/APG";
			}
			@Override
			public BigDecimal calcul(BigDecimal revenuBrut) {
				return calculateurCotiSociale.calculPartSalarieeCotisationAvsAiApg(revenuBrut);
			}
		});
		cotisations.add(new ICalculCotisationSociale() {
			@Override
			public String getNomCotisation() {
				return "Cotisations AC";
			}
			@Override
			public BigDecimal calcul(BigDecimal revenuBrut) {
				return calculateurCotiSociale.calculPartSalarieeCotisationAssuranceChomage(revenuBrut);
			}
		});
		cotisations.add(new ICalculCotisationSociale() {
			@Override
			public String getNomCotisation() {
				return "Primes AANP";
			}
			@Override
			public BigDecimal calcul(BigDecimal revenuBrut) {
				return calculateurCotiSociale.calculPartSalarieeCotisationAssuranceAccidentNonProfessionnel(BigDecimalUtil.parseTaux("1.4 %"), revenuBrut);
			}
		});
		// Uniquement à partir de 2006, on introduit les cotisations à l'assurance maternité
		if (annee >= 2006) {
			cotisations.add(new ICalculCotisationSociale() {
				@Override
				public String getNomCotisation() {
					return "Cotisations maternité adoption";
				}
				@Override
				public BigDecimal calcul(BigDecimal revenuBrut) {
					return calculateurCotiSociale.calculPartSalarieeAssuranceMaterniteAdoption(revenuBrut);
				}
			});
		}
		return cotisations;
	}
	
	protected CalculateurImpotSourceIFD construireCalculateurISIFD(int annee) {
		CalculateurImpotSourceIFD calculateur = new CalculateurImpotSourceIFD(annee);
		calculateur.setRegleCalculCotisations(construireRegleCotisationSociale(annee));
		if (annee < 2005) {
			calculateur.setTaux2emePilier(BigDecimalUtil.parseTaux("4.5 %"));
		} else {
			calculateur.setTaux2emePilier(BigDecimalUtil.parseTaux("5.5 %"));
		}
		
		// valable dès 2001
		calculateur.setTauxFraisProfessionnel(BigDecimalUtil.parseTaux("3 %"));
		calculateur.setPlancherFraisprofessionnel(new BigDecimal("1900"));
		calculateur.setPlafondFraisProfessionnel(new BigDecimal("3800"));
		
		if (annee < 2008) {
			// 700 francs frais de déplacement
			// 1500 francs demi-déduction repas extérieur
			calculateur.setFraisProfessionnelDeplacementRepas(new BigDecimal("2200"));
		} else {
			// 700 francs frais de déplacement
			// 1600 francs demi-déduction repas extérieur
			calculateur.setFraisProfessionnelDeplacementRepas(new BigDecimal("2300"));
		}
		
		// deductions primes ass et int capitaux epargne valable dès 2001
		calculateur.setTauxPrimeAssIntCapitEpargneCelibataire(BigDecimalUtil.parseTaux("3 %"));
		calculateur.setTauxPrimeAssIntCapitEpargneCouple(BigDecimalUtil.parseTaux("5 %"));
		calculateur.setPrimeAssIntCapitEpargneParEnfant(new BigDecimal("700"));
		if (annee > 2005) {
			calculateur.setPlafondPrimeAssIntCapitEpargneCelibataire(new BigDecimal("1700"));
			calculateur.setPlafondPrimeAssIntCapitEpargneCouple(new BigDecimal("3300"));
		} else {
			calculateur.setPlafondPrimeAssIntCapitEpargneCelibataire(new BigDecimal("1500"));
			calculateur.setPlafondPrimeAssIntCapitEpargneCouple(new BigDecimal("3100"));
		}
		
		calculateur.ajouterDeductionSociale(fournisseurRegleImpotFederal.getRegleDeductionSocialeEnfant(annee));
		if (annee > 2007) calculateur.ajouterDeductionSociale(fournisseurRegleImpotFederal.getRegleDeductionSocialeConjoint(annee));
		
		calculateur.setProducteurImpot(getProducteurImpotIFD(annee));
		return calculateur;
	}

	protected CalculateurImpotSourceICC construireCalculateurISICC(int annee) {
		CalculateurImpotSourceICC calculateur = null;
		if (annee < 2010) {
			CalculateurImpotSourceICCAvant2010 calculateurAvant2010 = new CalculateurImpotSourceICCAvant2010(annee);
			calculateurAvant2010.setProducteurRabaisImpot(fournisseurRegleImpotCantonal.getProducteurRabaisImpot(annee));
			calculateur = calculateurAvant2010;
		} else {
			calculateur = new CalculateurImpotSourceICC(annee);
		}
		
		List<ICalculCotisationSociale> cotis = this.construireRegleCotisationSociale(annee);
		// Attention, à l'ICC il convient de distinguer l'AANP des autres cotisations !
		for (Iterator<ICalculCotisationSociale> iter = cotis.iterator(); iter.hasNext();) {
			ICalculCotisationSociale regle = iter.next();
			if ("Primes AANP".equals(regle.getNomCotisation())) {
				calculateur.setRegleAANP(regle);
				iter.remove();
			}
		}
		// En 2010, le taux assurance maternité n'est pas correct : il est resté celui de 2009
		if (2010 == annee) {
			for (Iterator<ICalculCotisationSociale> iter = cotis.iterator(); iter.hasNext();) {
				ICalculCotisationSociale regle = iter.next();
				if ("Cotisations maternité adoption".equals(regle.getNomCotisation())) {
					iter.remove();
				}
			}
			// Création de la cotisation avec taux de 2009
			ICalculCotisationSociale cotiAMat = new ICalculCotisationSociale() {

				@Override
				public BigDecimal calcul(BigDecimal revenuBrut) {
					return TypeArrondi.CINQ_CTS.arrondirMontant(revenuBrut.multiply(new BigDecimal("0.0002")));
				}

				@Override
				public String getNomCotisation() {return "Cotisations maternité adoption";}
				
			};
			cotis.add(cotiAMat);
		}
		calculateur.setRegleCalculCotisations(cotis);
		calculateur.setTaux2emePilier(BigDecimalUtil.parseTaux("5 %"));
		
		calculateur.setTauxFraisProfessionnel(BigDecimalUtil.parseTaux("3 %"));
		
		// Valable dès 2005
		calculateur.setPlancherFraisprofessionnel(new BigDecimal("600"));
		if (annee < 2009) {
			calculateur.setPlafondFraisProfessionnel(new BigDecimal("1600"));
		} else {
			calculateur.setPlafondFraisProfessionnel(new BigDecimal("1700"));
		}
		
		calculateur.setTauxFraisMedicaux(BigDecimalUtil.parseTaux("2 %"));
		calculateur.setPlafondFraisMedicaux(new BigDecimal(1200));
		
		// valable dès 2007
		if (annee < 2010) {
			calculateur.setCotisationAssuranceMaladieParEpoux(new BigDecimal("3500"));
			calculateur.setCotisationAssuranceMaladieEnfant(new BigDecimal("1000"));
		} else {
			calculateur.setCotisationAssuranceMaladieParEpoux(new BigDecimal("5225"));
			calculateur.setCotisationAssuranceMaladieEnfant(new BigDecimal("1219"));
		}
			
		DeductionSociale regleDeductionCharge = fournisseurRegleImpotCantonal.getRegleDeductionSocialeCharge(annee);
		if (null != regleDeductionCharge) calculateur.ajouterDeductionSociale(regleDeductionCharge);
		
		
		calculateur.setProducteurImpot(getProducteurImpotSourceRevenu(annee));
		return calculateur;
	}
	
	public CalculateurImpotSource getCalculateurImpotSourceICC(int annee) {
		if (!calculateursISICC.containsKey(annee)) {
			calculateursISICC.putIfAbsent(annee, this.construireCalculateurISICC(annee));
		}
		return calculateursISICC.get(annee);
	}
	
	protected CalculateurImpotSource construireCalculateurIS(int annee) {
		CalculateurImpotSourceICCetIFD calculateur = null;
		if (annee < 2010) {
			calculateur = new CalculateurImpotSourceAvant2010(annee);
		} else {
			calculateur = new CalculateurImpotSourceICCetIFD(annee);
		}
		calculateur.setCalculateurICC(getCalculateurImpotSourceICC(annee));
		calculateur.setCalculateurIFD(getCalculateurImpotSourceIFD(annee));
		return calculateur;
	}

	private ProducteurImpot getProducteurImpotIFD(int annee) {
		return fournisseurRegleImpotFederal.getProducteurImpotsFederauxPP(annee);
	}
	

}
