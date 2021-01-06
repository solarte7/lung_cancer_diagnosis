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
	
	public ArrayList <String> getEvents() {
		return dao.getEvents();
	}
	
	public void saveLinks(ArrayList<Link> links) {
		dao.saveLinks(links);
	}
	
	public ArrayList <String>  getPatients() {
		return dao.getPatients();
	}
	
	public ArrayList<String> getNotesByPriority(){
		return dao.getPriorityNotes();
	}
	
	public ArrayList<String> getPriorityUMLS(){
		return dao.getPriorityUMLS();
	}
	
	public String  getDiagnosisDate (ArrayList<String> priorityNotes, String patient) {
		return dao.getDianosisDate(priorityNotes, patient);
	}
	
	public String  getDiagnosisDate (String patient) {
		return dao.getDianosisDate(patient);
	}
	
	public String getDiagnosis(ArrayList<String> priorityUMLS, String patient) {
		return dao.getDiagnosis(priorityUMLS, patient);
	}
	
}
