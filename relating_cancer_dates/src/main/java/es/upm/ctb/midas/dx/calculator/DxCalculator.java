package es.upm.ctb.midas.dx.calculator;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.plaf.metal.MetalIconFactory.FolderIcon16;



public class DxCalculator {
	
	HashMap<Integer, TextDocument> documentsMap = new HashMap<Integer, TextDocument>();
	static String filtros= "/home/kdd/ctb/validacion/Diagnostico/filtrosDx.txt";
	static String errors= "/home/kdd/ctb/validacion/Diagnostico/errorMas30.txt";
	//static String errors= "/home/kdd/ctb/validacion/Diagnostico/errorTmp.txt";
	
	HashMap <Integer, Integer > ehrErrorMas30List;	
	Disambiguator disambiguator;	
	HashMap <String, DxAnnotation > diagnosisDateMap;
	ArrayList<String> allEhrs;
	
	
	public DxCalculator() {
		disambiguator = new Disambiguator();
		diagnosisDateMap = new HashMap<String, DxAnnotation>();
		allEhrs= new ArrayList<String>();
	}
	
	
	public void loadDocumentsMap() {
    	System.out.println("Loading  Database Connection ...");
    	//DaoDocument dataBaseObject = new DaoDocument();
    	
    	//documentsMap = dataBaseObject.loadDocumentsMap();
    	//dataBaseObject.closeConnection();
    	
    	System.out.println("Data Base ok...");
    }
	
	
	public  HashMap<Integer, TextDocument> getDocumentsMap() {
    	return this.documentsMap;
    }
	
	public void loadAllEhr() {
		DaoPatient dao = new DaoPatient();
		allEhrs = dao.getAllEhr();
	}
	
	
	public void loadEhrError() {		
		TextFileLoader textFile = new TextFileLoader();	
		textFile.readErrorMas30(errors);
		ehrErrorMas30List = textFile.getErrorMas30();		
		System.out.println("EHR Load successfully " + ehrErrorMas30List.size());
	}
	
	/**
	 * 
	 */
	public void configureDisambiguator() {
		disambiguator.loadDictionaries();
	}//end
	
	
	public void disambiguateAnnotations() {
		DaoPatient dao = new DaoPatient();
		//for (HashMap.Entry<Integer, Integer> entry : ehrErrorMas30List.entrySet()) {
		
		for (String ehr: allEhrs) {
	         
			//Integer ehr = entry.getKey();
			System.out.println("EHR =" + ehr);
			
			//Consultamos las anotaciones para cada ehr
			ArrayList <DxAnnotation> annotations = dao.getDxAnnotations(ehr.toString());
			System.out.println("\n \n ============================================================" );
			System.out.println("Disambiguando anotaciones de =  " + ehr  );
			
			
			//filtramos las anotaciones			
			ArrayList <DxAnnotation> disambiguatedAnnotations = disambiguator.filterAnnotations(annotations);			
			
				
			
			System.out.println("============================================================ \n \n" );
			
			//Si la lista disambiguada es mayor a 10
			if (disambiguatedAnnotations.size() >=3){
				
				System.out.println("Size 2: " +   disambiguatedAnnotations.size()); 
				System.out.println("Salvando Disambiguated " + disambiguatedAnnotations.size() + " de =  " + ehr  );
				saveAnnotations(disambiguatedAnnotations);
			}
			else {
				System.out.println("Salvando ambiguated" + annotations.size() + " de =  " + ehr  );
				System.out.println("Size 1: " +  annotations.size()); 	
				
				saveAnnotations(annotations); 
				
			}
			
	     }//end for
	}//end method
	
	
	/**
	 * Calculate the diagnsosis Date
	 * @param disambiguatedList
	 */
	public void saveAnnotations(ArrayList <DxAnnotation> disambiguatedList) {
		DaoPatient dao = new DaoPatient();
				
		for (DxAnnotation annotation: disambiguatedList) {
			dao.saveDisambiguatedAnnotation(annotation);
			
		}
	}
	
	public void extractDiagnosisDateNew() {
		DaoPatient dao = new DaoPatient();
		 for (String ehr: allEhrs) {   
				//Integer ehr = entry.getKey();
				System.out.println("EHR = " + ehr.toString());
				
				//1. Consultamos anotaciones que estén  en las notas de juicio clinico
				ArrayList <DxAnnotation> annotations1 = dao.getClinicalJudgmentlAnnotations(ehr.toString());
				System.out.println("size trial: " + annotations1.size());
		 }
	}
	/*
	 * Extract the date
	 */
	public void extractDate() {
		DaoPatient dao = new DaoPatient();
		
		//for (HashMap.Entry<Integer, Integer> entry : ehrErrorMas30List.entrySet()) {
	      for (String ehr: allEhrs) {   
			//Integer ehr = entry.getKey();
			System.out.println("EHR = " + ehr.toString());			
			ArrayList <DxAnnotation> annotations21 = dao.getEhrAnnotations(ehr.toString());
			try {
				diagnosisDateMap.put(ehr.toString(), annotations21.get(0));
			}catch(Exception e) {
				System.out.println("EHR = " +  ehr + " (Error)");		
			}
		}//for
	}//end method
	
	
	/**
	 * Verifica si hay una anotacion en juicio clinica y escoge la de minima fecha
	 * @param annotations
	 * @return
	 */
	public DxAnnotation calculateDate(ArrayList <DxAnnotation> annotations) {
		Date date1= null;
		ArrayList <DxAnnotation> result  = new ArrayList <DxAnnotation>();
		
		if (annotations.size()>0) {
			
			result.add(annotations.get(0));
			date1= annotations.get(0).getDateNew();
			
		}
		/*
		if (annotations.size()>1) {
			Date date2 = annotations.get(1).getDateNew();
			System.out.println("Date2 trial " + date2);
			
			int distanceDays=(int) ((date1.getTime()- date2.getTime())/86400000);
			
			if(Math.abs(distanceDays) >= 360) {
				
				result.add(annotations.get(1));
			}
		}*/
		
		if (result.size() > 0) {
			return result.get(0);
		}
		else {
			return null;
		}
	
 }//method
	
	public void printDiagnosisDate() {
		DaoPatient dao = new DaoPatient();
		System.out.println("\n \n \n ********** Imprimiendo   Fechas ***************** \n" ); 
		for (HashMap.Entry<String, DxAnnotation> entry : diagnosisDateMap.entrySet()) {
	         
			String ehr = entry.getKey();
			DxAnnotation d= entry.getValue();
			System.out.println("Ehr: " +  ehr +  "\t Date: " +  d);
			dao.saveDiagnosisDate(ehr, d);
		}
	}

	
	/**
	 * Calculate the diagnsosis Date
	 * @param disambiguatedList
	 */
	public void saveTNMAnnotations(ArrayList <DxAnnotation> disambiguatedList) {
		DaoPatient dao = new DaoPatient();
				
		for (DxAnnotation annotation: disambiguatedList) {
			dao.saveDisambiguatedAnnotationsTNM(annotation);
			
		}
	}
	
	
	public void extractDateTNM() {
		DaoPatient dao = new DaoPatient();
		//leer ehr
		//para cada ehr traer annotaciones  que sean de juicio clinico ordenadas por fechas
		//seleccionar una
		//traaer anotaciones que sean de reportes de servicio de oncologia
		//consultar stage, seleccionar una
		//escoger la minima.getClinicalTrialAnnotations

		//for (HashMap.Entry<Integer, Integer> entry : ehrErrorMas30List.entrySet()) {
		
	        for (String ehr: allEhrs) {
	        	try {
	        	
					//Integer ehr = entry.getKey();
					System.out.println("EHR = " + ehr.toString());
					
					//1. Consultamos anotaciones que estén  TNM y juicio clinico
					ArrayList <DxAnnotation> annotations1 = dao.getJudgtment_TNM_Annotations(ehr.toString());
					System.out.println("size TNM Judment: " + annotations1.size());
					
					System.out.println("Entry 1");
					if (annotations1.size() > 0) {
						DxAnnotation selected1= calculateDate(annotations1);
						if (selected1.equals(null) == false) {
							System.out.println(" **** Date1 TNM " + selected1.getDateNew());
							diagnosisDateMap.put(ehr.toString(), selected1);
						}
					}
			
			
					
					//2.Traemos anotaciones de Juicio clinico
					else{
						System.out.println("Entry 2");
						//ArrayList <DxAnnotation> annotations2 = dao.getEhrAnnotations(ehr.toString());
						ArrayList <DxAnnotation> annotations2 = dao.getClinicalJudgmentlAnnotations(ehr.toString());
						if (annotations2.size()>0) {
							DxAnnotation selected2 = calculateDate(annotations2);
							
							if (selected2.equals(null) == false) {
								System.out.println(" **** Date2 Juicio " + selected2);
								diagnosisDateMap.put(ehr.toString(), selected2);
							}//if
							
						}//if
						else {
							ArrayList <DxAnnotation> annotations3 = dao.getEhrAnnotations(ehr.toString());
							System.out.println("Entry 3 All ");
							
							if (annotations3.size()>0) {
								DxAnnotation selected3 = calculateDate(annotations3);
								System.out.println("Entry 4");
								if (selected3.equals(null) == false) {
									System.out.println(" **** Date3 All " + selected3);
									try {
										diagnosisDateMap.put(ehr.toString(), selected3);
									}
									catch(Exception e){
										//diagnosisDateMap.put(ehr.toString(), selected3);
									}
								}//if
								System.out.println("Entry 5");
						     }
					    }//else
				
					}//else	
	        	}
	        	catch(Exception e) {
	        		System.out.println("Error ehr " +  ehr);
	        	}
			
		}//for
	        
	}//end method
	
	public void printContadores() {
		int spe = this.disambiguator.getContadorSpeculation();
		int neg = this.disambiguator.getContadorNegation();
		int sub = this.disambiguator.getContadorSubject();
		System.out.println("Speculation: " + spe);
		System.out.println("Negacion: " + neg);
		System.out.println("sub: " + sub);
		
	}
	
	/// ========================================================================
	
	//=====================  MAIN  //==================================
	public static void main(String a[]) {
		DxCalculator dx= new DxCalculator();
		//dx.loadDocumentsMap();
		
		dx.loadAllEhr();
		dx.configureDisambiguator();
		
		//dx.disambiguateAnnotations();//limpia las anotaiones y las guarda en la bd
		
		//===========================================================================
		
		dx.extractDate();
		
		dx.printDiagnosisDate();
		dx.printContadores();
		System.out.println("\n =========== End Extracting and saving ===============\n ");
		
		
		
	}

}
