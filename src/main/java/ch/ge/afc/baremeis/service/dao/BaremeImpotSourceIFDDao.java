package ch.ge.afc.baremeis.service.dao;

import ch.ge.afc.baremeis.service.BaremeDisponible;
import ch.ge.afc.baremeis.service.ICodeTarifaire;
import org.impotch.bareme.BaremeParTranche;

import java.util.Set;

public interface BaremeImpotSourceIFDDao {
    Set<ICodeTarifaire> rechercherCodesTarifaires(int annee);
    BaremeParTranche obtenirBaremeMensuel(int annee, ICodeTarifaire code);
    Set<BaremeDisponible> baremeDisponible();
}
