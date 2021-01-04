package es.upm.ctb.jkes.relating;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;



import java.sql.Connection;




public class CSVLoader {
	int cont;
	HashMap<Integer, Integer> ehrMap;
	HashMap<Integer, String> sentencesMap;
	HashMap<Integer, String> datesMap;
	HashMap<Integer, String>  cancerMap;
	HashMap<Integer, String>  eventsMap;
	HashMap<Integer, String>  normalizedMap;
	
	public CSVLoader() {	
		datesMap = new HashMap<Integer, String>();
		cancerMap = new HashMap<Integer, String>();
		eventsMap = new HashMap<Integer, String>();
		sentencesMap = new HashMap<Integer, String>();
		ehrMap = new HashMap<Integer, Integer>();
		normalizedMap = new HashMap<Integer, String>();
	}
	
	
	public HashMap<Integer, String> getDatesMap() {
		return datesMap;
	}


	


	public HashMap<Integer, String> getCancerMap() {
		return cancerMap;
	}


	


	public HashMap<Integer, String> getEventsMap() {
		return eventsMap;
	}


	public HashMap<Integer, Integer> getEhrMap() {
		return ehrMap;
	}

	

	public HashMap<Integer, String> getSentencesMap() {
		return sentencesMap;
	}

	
	
	public int getCont() {
		return this.cont;
	}
	
	public  CSVParser openCSVFile() {
		char delimiter =';';
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("annotations.csv").getFile());
		
		try {
			BufferedReader reader= new BufferedReader(new FileReader(file));
        
			CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
			    .withDelimiter(delimiter).withRecordSeparator('\n').withFirstRecordAsHeader()
			);

	       System.out.println("CSV File was open -> " + file );	     
	       return csvParser;
	   }
		catch(Exception e){
			System.out.println("Error to open CSV: " + e.getMessage());
			return null;
		}
		
		
			
}//end method
	
	
	
  
	
	
	//Obtiene una fecha en el formato deseado
	public Date parseDate(String d1) {
		
		if(d1.equals ("") || d1.equals(null)) {
			System.out.println("\n \n ***************************************  ");
			System.out.println("Error al obtener la fecha ");
			System.out.println("\n \n *******************************************\n");
			cont ++;
			return new Date();
			
		}
		else {
			try {
				
				 SimpleDateFormat inSDF = new SimpleDateFormat("dd/mm/yy");
				 SimpleDateFormat outSDF = new SimpleDateFormat("yyyy-mm-dd");
				 Date date1 = inSDF.parse(d1);
				 
				
				 String d2= outSDF.format(date1);			
				 System.out.println("d2 " + d2);
				 
				 Date date2= (Date)outSDF.parse(d2);
				 
				
				return date2;
				
			} catch (ParseException ex) {				
				System.out.println("Error getting Date: ");
				Date date2= new Date();
				System.out.println("\n \n ***************************************  ");
				System.out.println("Error " + ex.toString() );
				System.out.println("\n \n *******************************************\n");
				cont++;
				return date2;
				
				
				
			} 
		}
			
		
			
			 
		
	}
	
	
	public String cleanText(String textInput) {
		String textOutput="";
		textOutput = textInput;
		//textOutput= this.eliminateHTMLLabels(textInput);
		//textOutput= this.eliminateNonPrintCharacter(textOutput);
		//textOutput= this.eliminateSignedBy(textOutput);
		return textOutput;
	}
	
	public String getDocumetID() {
		return "";
	}
	
	public ArrayList <Annotation> processCsv(CSVParser parser) {
		   ArrayList <Annotation> annotationList = new ArrayList<Annotation>();
		   int i=0;
		   try {
				
				for (CSVRecord row :parser) {
					i++;
					int id = Integer.parseInt(row.get("id"));	
					int ehr =Integer.parseInt(row.get("ehr"));	
					int docID =Integer.parseInt(row.get("document_id"));	
					String docType =row.get("document_type");	
					String category =row.get("category");	
					int sentenceId =Integer.parseInt(row.get("sentence_id"));	
					String sentence =row.get("sentence");	
					String entity =row.get("entity");	
					String entityValue =row.get("entity_value");	
					String normalized =row.get("normalized");	
					String negated = row.get("negated");
					String speculated = row.get("speculated");
					
					Annotation ann= new Annotation();
					ann.setId(id);
					ann.setEhr(ehr);
					ann.setDocumentId(docID);
					ann.setDocumentType(docType);
					ann.setCategory(category);
					ann.setSenteceId(sentenceId);
					ann.setSentence(sentence);
					ann.setEntity(entity);
					ann.setEntityValue(entityValue);
					ann.setNormalized(normalized);
					ann.setNegated(negated);
					ann.setSpeculated(speculated);
					
					
					
					//Solo se cargan anotaciones libres de negación y especulacion
					if(negated.trim().length()<1 &&  speculated.trim().length()<1) {
						annotationList.add(ann);
						ehrMap.put(ehr, ehr);
						sentencesMap.put(id, sentence);
						
						if(entity.equals("cancer_entity")){
							cancerMap.put(id, entityValue);
							normalizedMap.put(id, normalized);
						}
						
						
						if(entity.equals("date")){
							
							datesMap.put(id, entityValue);
							normalizedMap.put(id, normalized);
						
						
					    }
						
						

						if(entity.equals("event")){							
								eventsMap.put(id, entityValue);
								normalizedMap.put(id, normalized);
						
						}
							
					}//end if
					
					
					//System.out.println ("id: " + id);
									
				}
		   }catch(Exception e) {
			   System.out.print("Error " + e.getMessage());
			}
		
		   
		   return annotationList;
	}//end method
	
	

   

   public HashMap<Integer, Integer> getEhr (CSVParser parser) {
		HashMap map = new HashMap<Integer, Integer> ();
		   int i=0;
		   try {
				
				for (CSVRecord row :parser) {
					i++;
					
					int ehr =Integer.parseInt(row.get("ehr"));	
					map.put(ehr, ehr);
					
					
					
					
									
				}
		   }catch(Exception e) {
			   System.out.print("Error " + e.getMessage());
			}
		   
		   
		   System.out.println("EHR: " + i);
		   
		   return map;
		   
	   }


   
   public HashMap<Integer, Integer> getSentences (CSVParser parser) {
		HashMap map = new HashMap<Integer, Integer> ();
		   int i=0;
		   try {
				
				for (CSVRecord row :parser) {
					i++;					
					int sentenceId =Integer.parseInt(row.get("sentence_id"));	
					map.put(sentenceId, sentenceId);
					
									
				}
		   }catch(Exception e) {
			   System.out.print("Error " + e.getMessage());
			}
		   
		   
		   //System.out.println("Documents loaded: " + i);
		   
		   return map;
		   
	   }



	
}//end class


	




///




	
	

	

