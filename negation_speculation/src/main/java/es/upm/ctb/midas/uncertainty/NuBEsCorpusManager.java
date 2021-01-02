package es.upm.ctb.midas.uncertainty;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;



import edu.stanford.nlp.*;

public class NuBEsCorpusManager {
	 HashMap<String, String> cuesMap;
	 HashMap<String, ArrayList<String>> sentencesMap;
	 HashMap<String,Integer> resultMap;
	 ArrayList <SentenceText> uncertainSentencesList;//Guarda el numero de uncertainty cues por sentencia
	 ArrayList <String> cuesList;
	 ArrayList <String> sentencesList;
	
	 int numTotalSentencesIASIS;
	 int numTotalSentencesNubes;
	 int withUncertainty;
	
	
	 
	 public NuBEsCorpusManager() {
		resultMap = new HashMap<String, Integer>(); 
	  
	    uncertainSentencesList = new  ArrayList <SentenceText> ();	    
	    numTotalSentencesIASIS=0;
	    numTotalSentencesNubes=0;
	    withUncertainty=0;
	    sentencesList = new ArrayList <String> ();
	    
	   
	   
	 }
	 
	 public void loadDictionary(String path) {
		 Z_CuesLoaderDict dict= new Z_CuesLoaderDict();
		 cuesList = dict.loadCues(path);
		 cuesMap = dict.loadCuesMap(path);	 
		
	 }
	 
	 
	 
	 public void loadNubesCorpus(String directory) {
		 NubesCorpusLoader nubes = new NubesCorpusLoader();
		 HashMap<String, String> mapFiles = nubes.loadDirectory(directory);
		 
		 sentencesMap = nubes.loadSentences(mapFiles);
		 numTotalSentencesNubes = nubes.getTotalSentences();		 
		 sentencesList = nubes.loadSentences1(mapFiles);		 
		 
		 
	 }
	 public  ArrayList <String> getIAsisSentences(String table) {
		 java.sql.Connection conn;
		 MysqlUncertainty  mySqlDB= new MysqlUncertainty ();
		 mySqlDB.getConnection();	
		 ArrayList <String> list1 =  mySqlDB.getSentences(table); //"diagnostic_dx"
		 numTotalSentencesIASIS = list1.size();
		 return list1;
	 }
	 /**
	  * Count in IASIS
	  * @param table
	  */
	 public void countByTermIASIS(String table) {
		 ArrayList <String> list1 = this.getIAsisSentences(table);
		 
		 System.out.print("Counting ...");
		 for (Map.Entry<String,String> entry : cuesMap.entrySet()) {
			    String key = entry.getKey();			   
			    String  term= entry.getValue();	
			    
			    System.out.println("Counting ... search key " +  key);
			    int result = countIntoSentences(term, list1);	
			   
			    resultMap.put(key, result);
			    
		 }//for
	 
	 }
	 
	 public void countByTermNubes() {
		 System.out.print("Counting ...");
		 for (Map.Entry<String,String> entry : cuesMap.entrySet()) {
			    String key = entry.getKey();			   
			    String  term= entry.getValue();	
			    
			    int result = countByFile(key);			    
			    resultMap.put(key, result);
			    
		 }//for
	 
	 }
	 
	 public int countByFile(String term1) {
		 System.out.println("Counting ... search key " +  term1);
		 int counter=0;
		 for (Map.Entry<String, ArrayList<String>> entry :  sentencesMap.entrySet()) {
			    String key = entry.getKey();
			    ArrayList <String> sentences1 = entry.getValue();
			    //System.out.println("list size 1 " +  sentences1.size());
			    int temp = countIntoSentences(term1, sentences1);
			    counter= counter + temp;
			    		    		   
			    
		 }//for
		 return counter;
	 }
	 
	 public int countIntoSentences(String term, ArrayList <String> sentences) {
		 int i = 0;//cuenta las ocurrencias de term, en cada archivo (ArrayList) 
		 
		 for (String sentence: sentences) {
			
			 //Aplicar regex
			 String regex = "(?i)\\b" + term +"\\b";
			 Pattern p = Pattern.compile(regex);
			 Matcher m = p.matcher(sentence);			
			 int countBySentence = 0;		
			 
			 while (m.find()) {
				 i++;				
				 countBySentence ++;
				//System.out.println("\n" +  m.group() + "  --> " + sentence);				
			 }//end while
			 
			 if (countBySentence > 0) {
				 int numTokens = NubesCorpusLoader.countSentenceTokens(sentence);				
				 withUncertainty ++;
				 
				 SentenceText sent = new SentenceText();
				 sent.setCues(term);
				 sent.setCuesNumber(countBySentence);
				 sent.setTokensNumber(numTokens);
				 sent.setSentence(sentence);
				 uncertainSentencesList.add(sent);
				 
			 }
			 
			 
		 }//for		 
		 return i;		 
	 }//end
	 
	 public void printResults() {
		 System.out.println("\n ****************** Printing Results *************** \n");
		 for (Map.Entry<String, Integer> entry :  resultMap.entrySet()) {
			    String key = entry.getKey();
			    Integer x= entry.getValue();
			    System.out.println("key= " +  key + "\t cant= " + x);
			   
			    		   
			    
		 }//for
	
	 }//end
	 
	 
	 public void printOrderResults() {
		 final Map<String, Integer> sortedByCount = resultMap.entrySet()
	             .stream()
	             .sorted(Map.Entry.comparingByValue())
	             .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
		 
		 System.out.println("\n ****************** Printing Ordered Results *************** \n");
		 for (Map.Entry<String, Integer> entry :  sortedByCount.entrySet()) {
			    String key = entry.getKey();
			    Integer x= entry.getValue();
			    System.out.println("key= " +  key + "\t cant= " + x);
				   
			    
		 }//for
	 }
	 
	  
	 
	 /**
	  * Cuenta la longitud en tokens de las sentencias donde está uncertainty
	  */
	 
	 public void countTokensLenght() {
		 System.out.println("\n **********  Calculando Longitud en tokens ********** \n");
		 HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
		 int masDe200 = 0;
		 
		 for (int i=2; i<=200; i++) {
			 int tokensNumber = 0;
			
			 for (int j=0; j<uncertainSentencesList.size(); j++) {
				 SentenceText unc=  uncertainSentencesList.get(j);
				 
				 int tokens = unc.getTokensNumber();
				 
				 if (tokens == i) {
					 tokensNumber ++;
				 }
				 
				 if (tokens > 200) {
					 masDe200++;
				 }
				 
				 
			 }//end for
			 
			 map.put(i, tokensNumber);
			System.out.println("Contanto ... " +  i +  " tokens \t " + tokensNumber);
			 
		 }//end for
		 
		 //se ponen los mayores de 200 tokens
		 map.put(201, masDe200);
		 printSentencesLengt(map);
	 }
	 
	 /**
	  * Save in file
	  */
	 public void printInFile() {
		
			 
			String fileOut="Number;Tokens" +"\n";
			 int masDe200 = 0;
			 
							
				 for (int j=0; j<uncertainSentencesList.size(); j++) {
					 SentenceText unc=  uncertainSentencesList.get(j);
					 
					 int tokens = unc.getTokensNumber();
					 int index= j+1;
					 fileOut = fileOut + j + ";" + tokens + "\n";
					 if (unc.getTokensNumber()<=7) {
						 System.out.println("Short -->" + unc.getSentence());
					 }
					 
				 }//end for
				 
				
				 String ruta = "/home/kdd/ctb/paper_new/file.csv";
			     File archivo = new File(ruta);
			     System.out.println("\n ********** GuardandoFile ********** \n");
			        
			        FileWriter fw;
					try {
						fw = new FileWriter(archivo);
						BufferedWriter bw = new BufferedWriter(fw);
			            bw.write(fileOut);
			            bw.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		            
			            
			 
	 }
	 
	 /**
	  * Imprime la longitud en tokens
	  * @param map1
	  */
	 public void printSentencesLengt(HashMap<Integer, Integer> map1) {
		 System.out.println("\n **********  Imprimiendo Longitud en tokens ********** \n");
		 for (Map.Entry<Integer, Integer> entry : map1.entrySet()) {
			    int key = entry.getKey();			   
			    int  value = entry.getValue();
			    //System.out.println(key + "\t" + value);
			    System.out.println(value);
		 }
	 }
	 
	 public void countCountiguosCues() {
		 int cont=0;
		 
		 for (SentenceText sent: uncertainSentencesList) {
			 int cuesNum = sent.getCuesNumber();
			 if(cuesNum > 1) {
				 cont ++;
			 }
		 }
		 
		 System.out.println("Sentences contiguos cues: " +  cont);
		 System.out.println("Total: " + uncertainSentencesList.size());
	 }
	 
	 public void printSentencesIndicators(String name) {
		 if (name.equals("IASIS")) {
			 System.out.println("Total sentences IASIS: " +  numTotalSentencesIASIS);
			 System.out.println("With Ucertainty: " +  withUncertainty);
			 System.out.println("WithOut Ucertainty: " +  (numTotalSentencesIASIS - withUncertainty));
		 }
		 else {
			 System.out.println("Total sentences Nubes: " +  numTotalSentencesNubes);
			 System.out.println("With Ucertainty: " +  withUncertainty);
			 System.out.println("WithOut Ucertainty: " +  (numTotalSentencesNubes - withUncertainty));
		 }
		
		 
		
	 }
	 
	 public void printCuesIndicator() {
		 int count1 =0;
		 int count2 = 0;
		 int count3 =0;
		 int count4 =0;
		 int count5 =0;
		 
		 String  tokens1 ="";
		 String  tokens2 ="";
		 String  tokens3 ="";
		 String  tokens4 ="";
		 String  tokens5 ="";
		
		 System.out.println("\n Número de Uncertain Cues por sentencia");
		 
		 for (int i=0; i< uncertainSentencesList.size(); i++) {
			 SentenceText sentence= uncertainSentencesList.get(i);
			 
			 int cuesNumber = sentence.getCuesNumber();
			 int tokens= sentence.getTokensNumber();
			
			 if (cuesNumber==1) {
				 count1 ++;
				 tokens1 = tokens1 + ", " +  tokens;
				 
			 }
			 
			 if (cuesNumber==2) {
				 count2 ++;
				 tokens2 = tokens2 + ", " +  tokens;
			 }
			 
			 
			 if (cuesNumber==3) {
				 count3 ++;
				 tokens3 = tokens3 + ", " +  tokens;
			 }
			 if (cuesNumber==4) {
				 count4 ++;
				 tokens4 = tokens4 + ", " +  tokens;
			 }
			 
			 if (cuesNumber>=5) {
				 count5 ++;
				 tokens5 = tokens5 + ", " +  tokens;
			 }
			 
		 }//for
		 
		 System.out.println("1 Cue: " + count1);
		 System.out.println("2 Cue: " + count2);
		 System.out.println("3 Cue: " + count3);
		 System.out.println("4 Cue: " + count4);
		 System.out.println("Mas de 4  Cue: " + count5);
		 
		 System.out.println("\n \n 1 Cue: " + tokens2);
		 System.out.println(" 2 Cue: " + tokens2);
		 System.out.println(" 3 Cue: " + tokens3);
		 System.out.println(" 4 Cue: " + tokens4);
		 
	 }
	
	public static void main(String args[]) {
		//String dir= "/home/kdd/Descargas/corpus_SFU/iula";
		String dir = "/datos-oswaldo/PhD/datasets/NUBES/NUBes";	
		String path= "/home/kdd/negation_spanish.txt";
		//String path = "/datos-oswaldo/PhD/git-external/NUBes/NUBes";	
		NuBEsCorpusManager m =new NuBEsCorpusManager();
		m.loadDictionary(path);		
		m.loadNubesCorpus(dir);	
		
		//=========================================================================//
		m.countByTermNubes();
		//main.processDocument(text1);
		//m.processAllDocuments(path);
		
		//m.countByTermIASIS("umls");
		
		//m.printResults();
		m.printOrderResults();
		m.countTokensLenght();
		m.printInFile();
		//m.countCountiguosCues();
		//m.printSentencesIndicators("IASIS"); //Nubes  IASIS
		//m.printCuesIndicator();
		
	}

}
