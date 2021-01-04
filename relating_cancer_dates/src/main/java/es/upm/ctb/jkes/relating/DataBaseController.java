package es.upm.ctb.jkes.relating;
import java.util.ArrayList;

import es.upm.ctb.jkes.relating.databases.*;

public class DataBaseController {
	DaoDiagnosis dao;
	public DataBaseController() {
		dao= new DaoDiagnosis();
	}
	
	public ArrayList<Integer> getSentencesId() {
		ArrayList<Integer> list1 =dao.getSentences();
		return list1;
	}
	
	public ArrayList<Annotation> getSentenceAnnotations(int sentenceId){
		return dao.getSentenceAnnotations(sentenceId);
	}
	
}
