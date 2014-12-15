/**
 *
 */
package ch.ge.afc.baremeis.service.dao.fichierfederal;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.EmptyResultDataAccessException;

import org.impotch.bareme.BaremeTauxEffectifConstantParTranche;
import org.impotch.util.TypeArrondi;

import ch.ge.afc.baremeis.service.BaremeDisponible;
import ch.ge.afc.baremeis.service.BaremeDisponibleImpl;
import ch.ge.afc.baremeis.service.ICodeTarifaire;
import ch.ge.afc.baremeis.service.dao.BaremeImpotSourceDao;

/**
 * @author <a href="mailto:patrick.giroud@etat.ge.ch">Patrick Giroud</a>
 */
public class BaremeImpotSourceFichierPlatDao implements BaremeImpotSourceDao {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private String getNomFichier(int annee, int anneeBareme, String codeCanton, String extension) {
        StringBuilder builder = new StringBuilder();
        builder.append(annee).append("/tar").append(String.valueOf(anneeBareme).substring(2))
                .append(codeCanton).append(".").append(extension);
        return builder.toString();
    }

    private LecteurFichierTexteStructureFederale creerLecteur(int annee, String codeCanton) {
        // On recherche d'abord le fichier
        String codeCantonMinuscule = codeCanton.toLowerCase();
        Resource fichierFederal = null;
        for (int anneeBareme = annee; anneeBareme > 2000; anneeBareme--) {
            String nomResource = getNomFichier(annee, anneeBareme, codeCantonMinuscule, "txt");
            Resource fichier = new ClassPathResource(nomResource);
            if (fichier.exists()) {
                fichierFederal = fichier;
                break;
            } else {
                nomResource = getNomFichier(annee, anneeBareme, codeCantonMinuscule, "xz");
                fichier = new ClassPathResource(nomResource);
                if (fichier.exists()) {
                    fichierFederal = fichier;
                    break;
                }
                else {
                    nomResource = getNomFichier(annee, anneeBareme, codeCantonMinuscule, "zip");
                    fichier = new ClassPathResource(nomResource);
                    if (fichier.exists()) {
                        fichierFederal = fichier;
                        break;
                    }
                }
            }
        }
        if (null == fichierFederal) {
            String message = "Pas de fichier fédéral pour l'année " + annee + " et le canton '" + codeCanton + "'";
            EmptyResultDataAccessException exception = new EmptyResultDataAccessException(message, 1);
            logger.info(message, exception);
            throw exception;
        }

        LecteurFichierTexteStructureFederale lecteur = new LecteurFichierTexteStructureFederale();
        lecteur.setCharsetName("ISO-8859-1");
        lecteur.setFichier(fichierFederal);
        return lecteur;
    }

    @Override
    public Set<ICodeTarifaire> rechercherBareme(int annee, String codeCanton) {
        LecteurFichierTexteStructureFederale lecteur = this.creerLecteur(annee, codeCanton);
        final Set<ICodeTarifaire> codes = new TreeSet<ICodeTarifaire>();
        EnregistrementCallback callback = new EnregistrementCallback() {
            @Override
            public void lectureEnregistrement(EnregistrementBareme enreg) {
                codes.add(enreg.getCodeTarifaire());
            }

            @Override
            public void lectureEnregistrementFinal(String codeCanton,
                                                   int nbreEnregistrement) {
                logger.debug("Fin de lecture du fichier cantonal '" + codeCanton + "' Nbre enreg. " + nbreEnregistrement);
            }

            @Override
            public void lectureEnregistrementInitial(String codeCanton,
                                                     Date dateCreation) {
                logger.debug("Début de lecture du fichier cantonal '" + codeCanton + "' crée le " + dateCreation);
            }

        };
        try {
            lecteur.lire(callback);
        } catch (IOException ioe) {
            String message = "Exception de lecture I/O dans le fichier " + codeCanton;
            logger.error(message);
            throw new DataAccessResourceFailureException("message", ioe);
        }
        return codes;
    }

    public List<EnregistrementBareme> rechercherTranches(int annee,
                                                         String codeCanton, final ICodeTarifaire code) {
        LecteurFichierTexteStructureFederale lecteur = this.creerLecteur(annee, codeCanton);
        final List<EnregistrementBareme> liste = new LinkedList<EnregistrementBareme>();
        EnregistrementCallback callback = new EnregistrementCallback() {
            @Override
            public void lectureEnregistrement(EnregistrementBareme enreg) {
                if (code.equals(enreg.getCodeTarifaire())) {
                    liste.add(enreg);
                }
            }

            @Override
            public void lectureEnregistrementFinal(String codeCanton,
                                                   int nbreEnregistrement) {
                logger.debug("Fin de lecture du fichier cantonal '" + codeCanton + "' Nbre enreg. " + nbreEnregistrement);
            }

            @Override
            public void lectureEnregistrementInitial(String codeCanton,
                                                     Date dateCreation) {
                logger.debug("Début de lecture du fichier cantonal '" + codeCanton + "' crée le " + dateCreation);
            }
        };
        try {
            lecteur.lire(callback);
        } catch (IOException ioe) {
            String message = "Exception de lecture I/O dans le fichier " + codeCanton;
            logger.error(message);
            throw new DataAccessResourceFailureException("message", ioe);
        }
        Collections.sort(liste, new Comparator<EnregistrementBareme>() {
            @Override
            public int compare(EnregistrementBareme o1, EnregistrementBareme o2) {
                return o1.getRevenuImposable().compareTo(o2.getRevenuImposable());
            }
        });
        return liste;
    }

    @Override
    public BaremeTauxEffectifConstantParTranche obtenirBaremeMensuel(int annee, String codeCanton, ICodeTarifaire code) {
        List<EnregistrementBareme> enreg = rechercherTranches(annee, codeCanton, code);
        // Construction du barème
        BaremeTauxEffectifConstantParTranche bareme = new BaremeTauxEffectifConstantParTranche();
        bareme.setTypeArrondiSurChaqueTranche(TypeArrondi.CINQ_CTS);
        BigDecimal revenu = BigDecimal.ZERO;
        BigDecimal tauxPrecedent = enreg.get(0).getTaux();
        for (EnregistrementBareme enr : enreg) {
            if (0 != tauxPrecedent.compareTo(enr.getTaux())) {
                bareme.ajouterTranche(revenu, tauxPrecedent);
            }
            revenu = revenu.add(enr.getEchelonTarifaire());
            tauxPrecedent = enr.getTaux();
        }
        bareme.ajouterDerniereTranche(tauxPrecedent);
        return bareme;
    }

    @Override
    public Set<BaremeDisponible> baremeDisponible() {
        Set<BaremeDisponible> baremes = new HashSet<BaremeDisponible>();
        for (int annee = 2001; annee < 2100; annee++) {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            try {
                Resource[] resources = resolver.getResources("classpath*:" + annee + "/tar*");
                for (Resource resource : resources) {
                    String codeCanton = resource.getFilename().substring(5, 7);
                    baremes.add(new BaremeDisponibleImpl(annee, codeCanton));
                }
            } catch (IOException ioe) {
                // TODO PGI propager une RuntimeException
                logger.error("Problème lors de la recherche de barèmes fédéraux pour " + annee, ioe);
            }
        }
        return baremes;
    }


}
