/**
 * 
 */
package ch.ge.afc.extraction;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
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

	private static final String[] codeTousCantons = new String[]{"ag","ai","ar",
		"be","bl","bs","fr","ge","gl","gr","ju","lu","ne","nw","ow","sg","sh","so",
		"sz","tg","ti","ur","vd","vs","zg","zh"};
	
	
	private Map<String,List<String>> mapCode = new TreeMap<String,List<String>>();
	private ServiceBaremeImpotSource service;
	private String[] codeCanton = codeTousCantons;
	
	public ExtractionTypeBareme() {
		
	}
	
	public void setService(ServiceBaremeImpotSource service) {
		this.service = service;
	}

	public void restreindreCanton(String premierCanton, String... cantonSuivant) {
		String[] codeCantons = new String[cantonSuivant.length + 1];
		codeCantons[0] = premierCanton;
		System.arraycopy(cantonSuivant, 0, codeCantons, 0, cantonSuivant.length);
		codeCanton = codeCantons;
	}
	
	public void extraire() {
		mapCode.clear();
		for (String codeCan : codeCanton) {
			Set<ICodeTarifaire> codes = service.rechercherCodeTarifaire(2009, codeCan);
			for (ICodeTarifaire code : codes) {
				String codeStr = code.getCode();
				if (!mapCode.containsKey(codeStr)) {
					mapCode.put(codeStr, new ArrayList<String>());
				}
				mapCode.get(codeStr).add(codeCan);
			}
		}
	}
	
	public void ecrireConsole() {
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
	
	public void ecrireEnteteTousCantons(BufferedWriter writer) throws IOException {
		for (String code : codeCanton) {
			writer.write(";");
			writer.write(code);
		}
		writer.newLine();
	}
	
	public void ecrire(BufferedWriter writer) throws IOException {
		ecrireEnteteTousCantons(writer);
		for (String codeTarif : mapCode.keySet()) {
			writer.write(codeTarif);
			List<String> cantons = mapCode.get(codeTarif);
			for (String codeTous : codeCanton) {
				writer.write(";");
				for (String code : cantons) {
					if (code.equals(codeTous)) writer.write("X");
				}
			}
			writer.newLine();
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		ApplicationContext context = new ClassPathXmlApplicationContext(
		        new String[] {"beansis.xml"});
		ServiceBaremeImpotSource service = (ServiceBaremeImpotSource)context.getBean("serviceBareme");

		ExtractionTypeBareme extraction = new ExtractionTypeBareme();
		extraction.setService(service);
		extraction.extraire();
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("ExtractionTypeBaremeIS.csv"),"Windows-1252"));
		extraction.ecrire(writer);
		writer.close();
	}

}
