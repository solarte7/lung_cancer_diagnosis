package es.upm.ctb.jkes.relating;
import java.util.HashMap;

import es.upm.ctb.jkes.relating.databases.*;

public class Main {
	LinkingDatesToCancer linker;
	ChoosingCancerDiagnosis diagnosisExtractor;
	HashMap<String, String> datesMap;
	HashMap<String, String> diagnosisMap;
	public void Main() {
		
	}
	public void init() {
		linker = new LinkingDatesToCancer();
		linker.loadUDPipe();
		linker.loadDataBase();
		diagnosisExtractor = new ChoosingCancerDiagnosis();
	}
	public void process() {
		linker.linking();
		datesMap = diagnosisExtractor.getDiagnosisDate();
		diagnosisMap = diagnosisExtractor.getDiagnosis();
	}
	
	
	
	public static void main(String a[]) {
		Main main =new Main();
		main.init();
		main.process();
		
	}
}
