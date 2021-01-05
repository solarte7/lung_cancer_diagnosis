package es.upm.ctb.midas.dx.calculator;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.stanford.nlp.process.Tokenizer;

public class Disambiguator {
	
	ArrayList<String> speculations;
	ArrayList<String> subject;
	ArrayList<String> negations;
	ArrayList<String> other;
	ArrayList<String> speculationPhrases;
	ArrayList<String> cancerExpresions;
	ArrayList<String> notesDx;
	
	String filePath ="";
	
	int contadorNegation=0;
	int contadorSpeculation =0;
	int contadorSubject=0;
	

	
	public Disambiguator(String path) {
		filePath = path;
	}
	
	public Disambiguator() {
		loadDictionaries();		
	}
	
	/**
	 * Load dictionaries 
	 */
	public void loadDictionaries() {
		TextFileLoader textFile = new TextFileLoader();
				
		try {
			speculations = textFile.getSpeculations();
			negations = textFile.getNegations();
			subject = textFile.getSubject();
			other = textFile.getOther();
			speculationPhrases = textFile.getPhrases();
			cancerExpresions = textFile.getCancerExpresions();
			notesDx = textFile.getNotesDx();
			
			//System.out.println("Dictionaries load sucessfully");
			
		}
		catch(Exception e) {
			System.out.println("Errorr loading filter file...");
		}
		
	}//end method

	public void loadFiltersFile() {		
		TextFileLoader textFile = new TextFileLoader();
		textFile.readFilters(filePath);	
		try {
			speculations = textFile.getSpeculations();
			negations = textFile.getNegations();
			subject = textFile.getSubject();
			other = textFile.getOther();
			speculationPhrases = textFile.getPhrases();
			
		}
		catch(Exception e) {
			System.out.println("Errorr loading filter file...");
		}
		
		System.out.println("Filters loaded Successfully spe: " + speculations.size());
	}//end method
	
	/**
	 * Filter annotations
	 * @param list1
	 * @return
	 */
	public ArrayList <DxAnnotation> filterAnnotations(ArrayList <DxAnnotation> list1) {
		
		ArrayList <DxAnnotation> disambiguatedList = new ArrayList <DxAnnotation>();
		
		System.out.println("List 1: " +list1.size());
		
		for(int i=0; i < list1.size(); i++) {			
			DxAnnotation annotation = list1.get(i);			
			
			//it is used to verify the annotation status (true=filter   false=no filter) 
			boolean filter = false;
			
			String sentence = annotation.getSentence();
			sentence = sentence.toLowerCase();
			sentence= cleanAcents(sentence);
			System.out.println("\n \n \nSentencia: " + sentence + "\n");
			
			
			if ( filterSpeculations(sentence) == true ) {	
				System.out.println("Filtering  speculations ok1" +  "\n");
				filter = true;		
				contadorSpeculation ++;
			}//end if
			
					
			if (filter == false) {
				
				if ( filterSubject(sentence) == true ) {
					System.out.println("Filtering Subject ok2" +  "\n");	
					filter = true;
					//contadorNegation ++;
			    }	
		   }//end if
			
			
			if (filter == false) {
				
				if ( filterNegations(sentence) == true) {
					System.out.println("Filtering negations ok3" +  "\n");
					filter = true;
					//contadorSubject ++;
				}
			}//end if
			
			if (filter == false) {
				if(filterPhrases(sentence) ==true) {
					filter = true;
					
				}
			}
			
			if (filter == false) {
				if(filterOtherCancer(sentence) == true) {
					filter = true;					
				}
			}
			
           
			// if annotation is not filtered then, it is added to new list.
			if (filter == false) {
				disambiguatedList.add(annotation);
			}
			
		}//end for		
		
	return disambiguatedList;		
		
  }//end method
	
	
		
	/**
	 * Filtra una sentencia de dx
	 * @param sentence
	 * @return
	 */
	
public String  filterSentence(String  sentence) {
			String resp="";					
			
			//it is used to verify the annotation status (true=filter   false=no filter) 
			boolean filter = false;					
			
			sentence = sentence.toLowerCase();	
			sentence= cleanAcents(sentence);
			//System.out.println("\n \n \nSentencia: " + sentence + "\n");
			
			
			if ( filterSpeculations(sentence) == true ) {					
				filter = true;					
				
			}//end if
			
					
			if (filter == false) {				
				if ( filterSubject(sentence) == true ) {					
					filter = true;				
					
			    }	
		   }//end if
			
			
			if (filter == false) {				
				if ( filterNegations(sentence) == true) {					
					filter = true;			
					
				}
			}//end if
			
			if (filter == false) {
				if(filterPhrases(sentence) == true) {
					filter = true;					
				}
			}
			
			
			if (filter == false) {
				if(filterOtherCancer(sentence) == true) {
					filter = true;	
					
				}
			}
			
			
			if (filter == true) { // if filter == true,  is ambiguos, a
				resp="yes";
			}
			else {
				resp="no";	
			}
			
			return resp;
	
  }//end method
	

/**
 * Filter speculations in sentnces
 * @param sentence
 * @return
 */
	public boolean filterSpeculations(String sentence) {
		boolean isSpeculative=false;
		
		int param1 = 21;//used to verify the number of sentence tokens
		
		StringTokenizer tokenizator = new StringTokenizer(sentence, " ");
		//System.out.println(" Num Tokens " + tokenizator.countTokens());
		int numTokens = tokenizator.countTokens();
		
		if (numTokens  >= 3 ){
			while (tokenizator.hasMoreTokens() && isSpeculative == false) {			
				String token = tokenizator.nextToken();
				
								
				for(int i=0; i < speculations.size(); i++) {
					String speculativeWord = speculations.get(i);
				
				    if(token.contains(speculativeWord)) {
						isSpeculative=true;
						i = speculations.size();
												
						if (numTokens >= param1) {							
							isSpeculative = verifySpeculationPosition(sentence, speculativeWord);		
							
							//System.out.println("verificando mayor tokens");
							
						}//if
						
					}//end if
				
				}// end for
								
			}//while
		}//end if
		
		else {
		
			isSpeculative=true;
		}
		
		
		return isSpeculative;
		
   }//end method
	
	/*
	 * Verify if speculative word is after diagnostic concept
	 * 
	 */
	
	public boolean verifySpeculationPosition(String sentence1, String speculative){
				boolean isSpeculative= true;
				
				int speculativePosition=sentence1.indexOf(speculative);
				int cancerPosition= -1;
				
				for (int i= 0; i< cancerExpresions.size(); i++) {
					String expression = cancerExpresions.get(i);
					cancerPosition= sentence1.indexOf(expression);
					
					if(cancerPosition >= 0) {
						//System.out.println("cancer position " + cancerPosition);
						//System.out.println("speculative position " + speculativePosition + " " + speculative + "\n");
						
						i = cancerExpresions.size();
						
						if (speculativePosition < cancerPosition ) {
							isSpeculative = true;							
						}
						else {
							isSpeculative = false;							
						}
					}
				}//end for
				
		return isSpeculative;
		
	}//end
	
	/**
	 * Fliter negations
	 */
	
	public boolean filterNegations(String sentence) {
		boolean isNegated=false;
		StringTokenizer tokenizator = new StringTokenizer(sentence, " ");
		
		while (tokenizator.hasMoreTokens()) {			
			String token = tokenizator.nextToken();
			token=cleanToken(token.trim());
			
			for(int i=0; i < negations.size(); i++) {
				if(token.contains(negations.get(i))) {
					isNegated=true;
					i=negations.size();
				}
			}
			
		}
		return isNegated;
		
	}
	
	public boolean filterSubject(String sentence) {
		boolean isFamily=false;

		StringTokenizer tokenizator = new StringTokenizer(sentence, " ");
		
		while (tokenizator.hasMoreTokens()) {			
			String token = tokenizator.nextToken();
			token=cleanToken(token.trim());
			
			for(int i=0; i < subject.size(); i++) {
				if(token.equalsIgnoreCase(subject.get(i))) {
					isFamily=true;
					i=subject.size();
					//System.out.println(token);
				}
			}//for
		
			
	    }//while
	return isFamily;

  }//method
	
	public boolean filterPhrases(String sentence) {
		boolean resp= false;
		int numPatterns=0;
	

		for(int i=0; i < speculationPhrases.size(); i++) {
			String regex = speculationPhrases.get(i);		
			

	    	Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(sentence);
			
			while (m.find()) {				
				//System.out.println("i: " + numPatterns +  "	Pattern Found:  " + m.group() );								
					numPatterns ++;
		
			}//while
			
		}//end for
		
		//System.out.println("Patterns " + numPatterns);
		
		if (numPatterns > 0) {
			resp = true;
		}
		else {
			resp=false;
		}
		
		//System.out.println("Patterns " + numPatterns +  " Filtrar: " + resp);
		
		return resp;
		
	}//end method
	
	

	public boolean filterOtherCancer(String sentence) {
		boolean isOther=false;
				
		int indexOtherCancer= -1;
		int indexMetastasis1 = -1;
		int indexMetastasis2 = -1;
		int indexMetastasis3 = -1;
		int indexMetastasis4 = -1;
		int indexMetastasis5 = -1;
		int indexMetastasis6 = -1;
		int indexMetastasis7 = -1;
		int indexMetastasis8 = -1;
		
		
		StringTokenizer tokenizator = new StringTokenizer(sentence, " ");
		
		while (tokenizator.hasMoreTokens()) {			
			String token = tokenizator.nextToken();
			token=cleanToken(token.trim());
			//System.out.println (token + " " );
			
			for(int i=0; i < other.size(); i++) {
			
				if(token.equals(other.get(i))) {
					 i=other.size();					 
					 isOther=true;					 
					 indexOtherCancer= sentence.indexOf(token);	 
				
					 indexMetastasis1 = sentence.indexOf("pulmon");	
					 indexMetastasis2 = sentence.indexOf("pulmonar");
					 indexMetastasis3 = sentence.indexOf("metastasis");
					 indexMetastasis4 = sentence.indexOf("mtx");
					 indexMetastasis5 = sentence.indexOf("mts");
					 indexMetastasis6 = sentence.indexOf("metastasica");
					 indexMetastasis7 = sentence.indexOf("afectacion");					
					 indexMetastasis8 = sentence.indexOf("afect");
					
				}
				
			}//for
				
		}//while
		
		if (isOther == true) {
		
			if ( indexMetastasis1 < indexOtherCancer && indexMetastasis1 >=1)  { 
				isOther=false;
			}
			
			if ( indexMetastasis2 < indexOtherCancer && indexMetastasis2 >=1)  {
				isOther=false;
			}
			
			if ( indexMetastasis3 < indexOtherCancer && indexMetastasis3 >=1)  { 
				isOther=false;
			}
			
			if ( indexMetastasis4 < indexOtherCancer && indexMetastasis4 >=1)  {
				isOther=false;
			}
			
			if ( indexMetastasis5 < indexOtherCancer && indexMetastasis5 >=1)  { 
				isOther=false;
			}
			

			if ( indexMetastasis6 < indexOtherCancer && indexMetastasis6 >=1)  { //&& (indexMetastasis1 >=1)
				isOther=false;
			}
			
			if ( indexMetastasis7 < indexOtherCancer && indexMetastasis7 >=1)  { //&& (indexMetastasis1 >=1)
				isOther=false;
			}
			
			if ( indexMetastasis8 < indexOtherCancer && indexMetastasis8 >=1)  { //&& (indexMetastasis1 >=1)
				isOther=false;
			}
			
			//System.out.println("Index 1: " +  indexMetastasis1);
			//System.out.println("Index 2: " +  indexMetastasis2);
			//System.out.println("Index c: " +  indexCancer);
	
		
	    }//end if
		
		//System.out.println("Other  Filtrar: " + isOther);
				
		return  isOther;
		
   }//end method
	
	/**
	 * 
	 * @return
	 */
	
	public String cleanAcents(String text) {
		 
        text = Normalizer.normalize(text, Normalizer.Form.NFD);
        text = text.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return text;

	}
	public String cleanToken(String token) {
		token= token.replace("(", "");
		token= token.replace(")", "");
		token= token.replace("-", "");
		token= token.replace("*", "");
		token= token.replace("?", "");
		token= token.replace(":", "");
		token= token.replace(";", "");
		token= token.replace(",", "");
		token= token.replace(".", "");
		token= token.replace("%", "");
		return token;
	}
	
	//contadores
	public int getContadorNegation() {
		return contadorNegation;
	}

	

	public int getContadorSpeculation() {
		return contadorSpeculation;
	}

	

	public int getContadorSubject() {
		return contadorSubject;
	}

	
	
	public static void main(String args[]) {
		String filtros= "/home/kdd/ctb/validacion/Diagnostico/filtrosDx.txt";
		Disambiguator d= new Disambiguator();
		//d.filterSentence("de todos mods");
		
		//d.filterOtherCancer(s1_Filtrar);
		//System.out.println("\n ");
		//d.filterOtherCancer(s2_NoFiltrar);
		//String sentence="Enfisema Pulmonar en seguimiento en Neumologia desde hace 6 años.";
		String sentence="(sugestiva) de malig";
		System.out.println ("Is ambiguos: " + d.filterSentence(sentence));
		//System.out.println ("Is ambiguos: " + d.filterTNMSentence(sentence));
	}
	
}