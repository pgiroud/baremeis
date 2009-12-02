/**
 * 
 */
package ch.ge.afc.extraction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ch.ge.afc.baremeis.service.ICodeTarifaire;
import ch.ge.afc.baremeis.service.ServiceBaremeImpotSource;

/**
 * @author <a href="mailto:patrick.giroud@etat.ge.ch">Patrick Giroud</a>
 *
 */
public class ExtractionTypeBareme {

	private static final String[] codeCanton = new String[]{"ag","ai","ar",
		"be","bl","bs","fr","ge","gl","gr","ju","lu","ne","nw","ow","sg","sh","so",
		"sz","tg","ti","ur","vd","vs","zg","zh"};
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext(
		        new String[] {"beansis.xml"});
		ServiceBaremeImpotSource service = (ServiceBaremeImpotSource)context.getBean("serviceBareme");
		Map<String,List<String>> mapCode = new TreeMap<String,List<String>>();
		
		for (String codeCan : codeCanton) {
			Set<ICodeTarifaire> codes = service.rechercherBareme(2009, codeCan);
			for (ICodeTarifaire code : codes) {
				String codeStr = code.getCode();
				if (!mapCode.containsKey(codeStr)) {
					mapCode.put(codeStr, new ArrayList<String>());
				}
				mapCode.get(codeStr).add(codeCan);
			}
		}
		
		for (String codeTarif : mapCode.keySet()) {
			StringBuilder builder = new StringBuilder("Code '"); 
			builder.append(codeTarif).append("', canton ");
			List<String> cantons = mapCode.get(codeTarif);
			boolean premier = true;
			for (String canton : cantons) {
				if (premier) {
					premier = false;
				} else {
					builder.append(", ");
				}
				builder.append(canton);
			}
			System.out.println(builder.toString());
		}
		
	}

}
