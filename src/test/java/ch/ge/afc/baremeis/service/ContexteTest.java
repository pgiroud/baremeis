package ch.ge.afc.baremeis.service;

import ch.ge.afc.baremeis.service.dao.fichierfederal.BaremeImpotSourceFichierPlatDao;
import ch.ge.afc.baremeis.service.dao.fichierfr.BaremeImpotSourceFichierFRPlatDao;
import ch.ge.afc.baremeis.service.dao.fichierge.BaremeImpotSourceFichierGEPlatDao;

public enum ContexteTest {
    CTX_TST;

    private final ServiceBaremeImpotSource service;

    ContexteTest() {
        service = construireService();
    }

    private ServiceBaremeImpotSource construireService() {
        BaremeImpotSourceFichierPlatDao dao = new BaremeImpotSourceFichierPlatDao();
        ServiceBaremeImpotSourceImpl serviceBaremeSansCache = new ServiceBaremeImpotSourceImpl(dao);
        serviceBaremeSansCache.setDaofr(new BaremeImpotSourceFichierFRPlatDao());
        serviceBaremeSansCache.setDaoge(new BaremeImpotSourceFichierGEPlatDao());

        ServiceBaremeImpotSource service = new ServiceBaremeImpotSourceCache(serviceBaremeSansCache);
        return service;
    }

    public ServiceBaremeImpotSource getService() {
        return service;
    }
}
