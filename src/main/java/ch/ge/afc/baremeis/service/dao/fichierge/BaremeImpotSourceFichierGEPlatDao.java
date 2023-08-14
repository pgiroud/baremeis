/**
 *
 */
package ch.ge.afc.baremeis.service.dao.fichierge;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.impotch.bareme.BaremeParTranche;
import org.impotch.bareme.ConstructeurBareme;
import org.impotch.util.BigDecimalUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import static ch.ge.afc.baremeis.service.dao.fichierge.CodeTarifaireGE.*;

import ch.ge.afc.baremeis.service.BaremeDisponible;
import ch.ge.afc.baremeis.service.BaremeDisponibleImpl;
import ch.ge.afc.baremeis.service.ICodeTarifaire;
import ch.ge.afc.baremeis.service.dao.BaremeImpotSourceDao;
import ch.ge.afc.baremeis.service.dao.fichierfederal.CodeTarifaire;
import org.impotch.util.TypeArrondi;

import static ch.ge.afc.baremeis.service.dao.fichierge.LecteurFichierTexteStructureGenevoise.unLecteurDepuisClasspath;
import static org.impotch.bareme.ConstructeurBareme.unBaremeATauxEffectif;
/**
 * @author <a href="mailto:patrick.giroud@etat.ge.ch">Patrick Giroud</a>
 */
public class BaremeImpotSourceFichierGEPlatDao implements BaremeImpotSourceDao {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final List<CodeTarifaireGE> ORDRE_CODE_AVANT_2010 = Arrays.asList(A0, B0, B1, B2, B3, B4, B5, B6, B7, B8, B9, B10, B11, B12, B13, B14, B15, I0, I1, I2, I3, I4, I5, I6, I7, I8);
    private static final List<String> ORDRE_CODE = Arrays.asList("A0", "B0", "B1", "B2", "B3", "B4", "B5");


    @Override
    public Set<BaremeDisponible> baremeDisponible() {
        return IntStream.iterate(2000, n -> n < 2100, n -> n+1)
                .filter(n -> creerLecteur(n).isPresent())
                .mapToObj(n -> new BaremeDisponibleImpl(n, "ge"))
                .collect(Collectors.toUnmodifiableSet());
    }

    private String getNomFichier(int annee) {
        return new StringBuilder()
            .append(annee)
            .append("/ge/bar_is")
            .append(annee)
            .append("_ascii.txt")
                .toString();
    }

    private Optional<LecteurFichierTexteStructureGenevoise> creerLecteur(int annee) {
        return  unLecteurDepuisClasspath(getNomFichier(annee),"ISO-8859-1");
    }


    private int getOrdre(CodeTarifaireGE code) {
        int index = ORDRE_CODE_AVANT_2010.indexOf(code);
        if (index < 0) throw new IllegalArgumentException("Le code tarifaire '" + code + "' est inconnu !!");
        return index;
    }

    private int getOrdre(String code) {
        String debutCode = code.substring(0, 2);
        int index = ORDRE_CODE.indexOf(debutCode);
        if (index < 0) throw new IllegalArgumentException("Le code tarifaire '" + code + "' est inconnu !!");
        return index;
    }


    public Set<ICodeTarifaire> rechercherCodesTarifaires(int annee, String codeCanton) {
        if ("ge".equals(codeCanton.toLowerCase())) {
            Set<ICodeTarifaire> set = new TreeSet<ICodeTarifaire>();
            if (annee < 2010) {
                CodeTarifaireGE[] valeurs = CodeTarifaireGE.values();
                for (CodeTarifaireGE code : valeurs) {
                    set.add(code);
                }
            } else {
                for (final String code : ORDRE_CODE) {
                    set.add(new CodeTarifaire(code));
                }
            }
            return set;
        } else {
            throw new IllegalArgumentException("Ce dao ne s'applique que pour les fichiers genevois !!");
        }
    }

    private List<EnregistrementBaremeGE> rechercherTranches(final int annee, final ICodeTarifaire code) {
        LecteurFichierTexteStructureGenevoise lecteur = this.creerLecteur(annee).orElseThrow();
        final List<EnregistrementBaremeGE> liste = new LinkedList<EnregistrementBaremeGE>();
        EnregistrementGECallback callback = new EnregistrementGECallback() {
            @Override
            public void traiterLigne(LigneEnregistrement ligne) {
                EnregistrementBaremeGE enreg = new EnregistrementBaremeGE();
                enreg.setMntMinAnnuel(ligne.getMntMinAnnuel());
                enreg.setMntMaxAnnuel(ligne.getMntMaxAnnuel());
                // Attention, les barèmes GE commencent avec 5 cts de décalage
                enreg.setMntMinMensu(ligne.getMntMinMensu().subtract(new BigDecimal("0.05")));
                enreg.setMntMaxMensu(ligne.getMntMaxMensu());
                enreg.setMntMinHoraire(ligne.getMntMinHoraire());
                enreg.setMntMaxHoraire(ligne.getMntMaxHoraire());
                BigDecimal[] taux = ligne.getTaux();
                int index;
                if (annee < 2010) index = getOrdre((CodeTarifaireGE) code);
                else index = getOrdre(code.getCode());
                enreg.setTaux(taux[index]);
                liste.add(enreg);
            }
        };
        try {
            lecteur.lire(callback);
        } catch (IOException ioe) {
            String message = "Exception de lecture I/O dans le fichier";
            logger.error(message);
            throw new RuntimeException("message", ioe);
        }
        Collections.sort(liste, new Comparator<EnregistrementBaremeGE>() {
            @Override
            public int compare(EnregistrementBaremeGE o1, EnregistrementBaremeGE o2) {
                return o1.getMntMaxAnnuel().compareTo(o2.getMntMaxAnnuel());
            }
        });
        return liste;
    }

    @Override
    public BaremeParTranche obtenirBaremeMensuel(int annee, String codeCanton,
                                                 ICodeTarifaire code) {
        if (annee < 2010 && !(code instanceof CodeTarifaireGE))
            throw new IllegalArgumentException("Le code tarifaire doit être un code genevois !!");
        List<EnregistrementBaremeGE> enreg = rechercherTranches(annee, code);
        // Construction du barème
        ConstructeurBareme cons = unBaremeATauxEffectif().typeArrondiSurChaqueTranche(TypeArrondi.CINQ_CENTIEMES_LES_PLUS_PROCHES);
        if (1 < enreg.size()) {
            // 1ère tranche
            EnregistrementBaremeGE premierEnregistrement = enreg.get(0);
            cons.premiereTranche(premierEnregistrement.getMntMaxMensu(),premierEnregistrement.getTaux());
            BigDecimal maxDerniereTranche = BigDecimal.valueOf(9_999_999);
            enreg.stream()
                    .filter(enr -> {
                        return BigDecimalUtil.isStrictementPositif(enr.getMntMinAnnuel())
                                //;
                                // on ne veut pas la première tranche
                                &&
                                BigDecimalUtil.isStrictementPositif(maxDerniereTranche.subtract(enr.getMntMaxAnnuel())); // on ne veut pas la dernière tranche
                    })
                    .forEach(enr -> cons.tranche(enr.getMntMinMensu(),enr.getMntMaxMensu(),enr.getTaux()));

        }
        EnregistrementBaremeGE dernier = enreg.get(enreg.size()-1);
        cons.derniereTranche(dernier.getMntMinMensu(),dernier.getTaux());
        return cons.construire();
    }


}
