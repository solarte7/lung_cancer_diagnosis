package es.upm.ctb.jkes.relating;

import java.util.ArrayList;
import java.util.HashMap;

public class ChoosingCancerDiagnosis {
	DataBaseController databaseController;
	ArrayList<String> patients;
	ArrayList<String> priorityNotes;
	ArrayList<String> priorityUMLS;
	
	public ChoosingCancerDiagnosis  () {
		databaseController = new DataBaseController();
		patients = databaseController.getPatients();
		priorityNotes=  databaseController.getNotesByPriority();
		priorityUMLS =databaseController.getPriorityUMLS();
	}
	
	public HashMap<String, String>  getDiagnosisDate() {
		HashMap<String, String> map1 = new HashMap<String, String>();
		for (String patient: patients) {
			String date="";
			date= databaseController.getDiagnosisDate(priorityNotes, patient);
			if (date.length()<1){
				date= databaseController.getDiagnosisDate(patient);
			}
			map1.put(patient, date);
			
		}//for
		return map1;
	}
	
	
	public HashMap<String, String>  getDiagnosis() {
		HashMap<String, String> map1 = new HashMap<String, String>();
		for (String patient: patients) {
			String dx= databaseController.getDiagnosis(priorityUMLS, patient);
			map1.put(patient, dx);
			
		}//for
		return map1;
	}
}
