/**
 * 
 */
package ch.ge.afc.baremeis.service.dao.fichierfederal;

import java.util.Date;

/**
 * @author <a href="mailto:patrick.giroud@etat.ge.ch">Patrick Giroud</a>
 *
 */
public interface EnregistrementCallback {
	void lectureEnregistrementInitial(String codeCanton, Date dateCreation);
	void lectureEnregistrement(EnregistrementBareme enreg);
	void lectureEnregistrementFinal(String codeCanton, int nbreEnregistrement);
}
