package ch.ge.afc.baremeis.service.dao.ifdch;

import org.impotch.bareme.Intervalle;
import static ch.ge.afc.baremeis.service.dao.ifdch.DescriptionIntervalle.DescriptionBorne.borne;

public class DescriptionIntervalle {

    private DescriptionBorne inf;
    private DescriptionBorne sup;

    public DescriptionIntervalle(String descriptionBareme) {
        String[] tokens = descriptionBareme.split(" ");
        for (String str : tokens) {
            if (str.toLowerCase().contains("inf")) {
                inf = borne(str);
            } else if (str.toLowerCase().contains("sup")) {
                sup = borne(str);
            }
        }
    }


    public static class DescriptionBorne {
        private final int increment;
        private final boolean ouvert;

        public static DescriptionBorne borne(String str) {
            return new DescriptionBorne(increment(str),ouvert(str));
        }

        private static int increment(String str) {
            if (str.contains("+1")) return -1;
            else if (str.contains("-1")) return 1;
            return 0;
        }

        private static boolean ouvert(String str) {
            return str.toLowerCase().contains("ouvert");
        }

        public DescriptionBorne(int increment, boolean ouvert) {
            this.increment = increment;
            this.ouvert = ouvert;
        }
    }
}
