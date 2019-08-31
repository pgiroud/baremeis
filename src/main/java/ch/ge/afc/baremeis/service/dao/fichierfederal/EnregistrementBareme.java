/**
 *
 */
package ch.ge.afc.baremeis.service.dao.fichierfederal;

import java.math.BigDecimal;
import java.util.Date;

import ch.ge.afc.baremeis.service.Sexe;
import org.impotch.util.BigDecimalUtil;

/**
 * @author <a href="mailto:patrick.giroud@etat.ge.ch">Patrick Giroud</a>
 */
public class EnregistrementBareme {

    public static BigDecimal TROU_DEBUT_TRANCHE = new BigDecimal("0.05");

    private GenreTransaction genre;
    private String codeCanton;
    private CodeTarifaire codeTarifaire;
    private Date dateInitialeValidite;
    private BigDecimal revenuImposable;
    private BigDecimal echelonTarifaire;
    private Sexe sexe;
    private int nbreEnfant;
    private BigDecimal montantImpot;
    private BigDecimal taux;

    public GenreTransaction getGenre() {
        return genre;
    }

    public void setGenre(GenreTransaction genre) {
        this.genre = genre;
    }

    public String getCodeCanton() {
        return codeCanton;
    }

    public void setCodeCanton(String codeCanton) {
        this.codeCanton = codeCanton;
    }

    public Date getDateInitialeValidite() {
        return dateInitialeValidite;
    }

    public void setDateInitialeValidite(Date dateInitialeValidite) {
        this.dateInitialeValidite = dateInitialeValidite;
    }

    public BigDecimal getRevenuImposable() {
        return revenuImposable;
    }

    public void setRevenuImposable(BigDecimal revenuImposable) {
        this.revenuImposable = revenuImposable;
    }

    public BigDecimal getEchelonTarifaire() {
        return echelonTarifaire;
    }

    public void setEchelonTarifaire(BigDecimal echelonTarifaire) {
        this.echelonTarifaire = echelonTarifaire;
    }

    public Sexe getSexe() {
        return sexe;
    }

    public void setSexe(Sexe sexe) {
        this.sexe = sexe;
    }

    public int getNbreEnfant() {
        return nbreEnfant;
    }

    public void setNbreEnfant(int nbreEnfant) {
        this.nbreEnfant = nbreEnfant;
    }

    public BigDecimal getMontantImpot() {
        return montantImpot;
    }

    public void setMontantImpot(BigDecimal montantImpot) {
        this.montantImpot = montantImpot;
    }

    public void setTaux(BigDecimal taux) {
        this.taux = taux;
    }

    public CodeTarifaire getCodeTarifaire() {
        return codeTarifaire;
    }

    public void setCodeTarifaire(CodeTarifaire codeTarifaire) {
        this.codeTarifaire = codeTarifaire;
    }


    public BigDecimal getMontantImposableMax() {
        return revenuImposable.add(echelonTarifaire);
    }

    public BigDecimal getTaux() {
        return taux;
    }

}
