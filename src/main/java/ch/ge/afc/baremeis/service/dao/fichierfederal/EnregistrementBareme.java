/**
 *
 */
package ch.ge.afc.baremeis.service.dao.fichierfederal;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

import ch.ge.afc.baremeis.service.Sexe;
import ch.ge.afc.baremeis.service.dao.CodeTarifaire;

/**
 * @author <a href="mailto:patrick.giroud@etat.ge.ch">Patrick Giroud</a>
 */
public class EnregistrementBareme {

    // TODO PGI A transformer en record
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EnregistrementBareme that = (EnregistrementBareme) o;
        return nbreEnfant == that.nbreEnfant && genre == that.genre && Objects.equals(codeCanton, that.codeCanton) && Objects.equals(codeTarifaire, that.codeTarifaire) && Objects.equals(dateInitialeValidite, that.dateInitialeValidite) && Objects.equals(revenuImposable, that.revenuImposable) && Objects.equals(echelonTarifaire, that.echelonTarifaire) && sexe == that.sexe && Objects.equals(montantImpot, that.montantImpot) && Objects.equals(taux, that.taux);
    }

    @Override
    public int hashCode() {
        return Objects.hash(genre, codeCanton, codeTarifaire, dateInitialeValidite, revenuImposable, echelonTarifaire, sexe, nbreEnfant, montantImpot, taux);
    }
}
