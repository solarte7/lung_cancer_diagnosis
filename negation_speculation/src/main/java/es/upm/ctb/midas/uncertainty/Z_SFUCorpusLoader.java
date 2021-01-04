package es.upm.ctb.midas.uncertainty;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.stanford.nlp.util.CoreMap;


public class Z_SFUCorpusLoader {
	int totalSentences;
	int sentenceId;
	int totalFiles;
	ArrayList<String> sentences;
	ArrayList<SentenceText> sentenceObjectList;
	ArrayList <Integer> tokensNumber; 
	ArrayList <Integer> negatedSentences; 
	ArrayList <String> negatedSentences1; 
	HashMap<String, Integer> cues;
	ArrayList<Z_SFU_Object> cuesObject; 
	
	public  Z_SFUCorpusLoader() {
		totalSentences=0;
		sentenceId =0;
		totalFiles=0;
		
		sentences = new ArrayList<String> ();
		sentenceObjectList = new  ArrayList<SentenceText> ();
		tokensNumber= new ArrayList<Integer> ();
		negatedSentences = new ArrayList<Integer> ();
		negatedSentences1 = new ArrayList<String> ();
		cues = new HashMap<String, Integer>();
		cuesObject = new ArrayList<Z_SFU_Object>();
	}
	
	public HashMap<String, String> loadDirectory(String path) {
		System.out.println("\n");
		//String path = "/datos-oswaldo/PhD/git-external/NUBes/NUBes";		
		HashMap<String, String> map1 = new HashMap<String, String>();
		
		for (int i=1; i<=10; i++) { 
			File carpeta = new File (path);
			System.out.println("\n");
			
			for (final File file : carpeta.listFiles()) {
			       	String name= file.getName();		        	
			        String extension = name.substring(name.lastIndexOf("."));
			        
			        if(extension.equals(".xml")) {
			        		map1.put(name, file.getAbsolutePath());
			        		System.out.println(name);
			        		totalFiles= totalFiles +1;
			        
			        }
			 }//for
			
		}//for
		
		System.out.println("map size: " + map1.size());
		return map1;
		
	  }//end
	
	  public HashMap<String, ArrayList<String>> loadSentences(HashMap<String, String> mapFiles, String pathDirectory) {
		  HashMap<String, ArrayList<String>> sentencesMap = new HashMap<String, ArrayList<String>>();
		  int i=0;
		  for (Map.Entry<String,String> entry : mapFiles.entrySet()) {
			    String key = entry.getKey();			   
			    String  filePath = entry.getValue();
			    
			   
			    String fileName = pathDirectory + "/"+ key ;
			    System.out.println("Processing " + fileName);
			    
			    ArrayList<String> list = loadFile2(filePath);
			    sentencesMap.put(fileName, list);
			    totalSentences = totalSentences + list.size();
			    
			    
			}
		  
		    //System.out.println("******************Total sentencias*********************** ");
		    //System.out.println("Total: " + totalSentences);		    
		    //System.out.println("***************************************** ");
		  
		 return sentencesMap;
	  }
	 
	  
	  
	  //load file xml
	  public ArrayList <String> loadFile2(String fileName) {
		  File archivo = null;
	      FileReader fr = null;
	      BufferedReader br = null;
	      ArrayList <String> sentences = new  ArrayList  <String>();
	      

	      try {
	         // Apertura del fichero y creacion de BufferedReader para poder
	         // hacer una lectura comoda (disponer del metodo readLine()).
	         archivo = new File (fileName);
	         fr = new FileReader (archivo);
	         br = new BufferedReader(fr);

	         // Lectura del fichero
	         String linea;
	         String sent="";
	         int i=0;
	         while((linea=br.readLine())!=null) {
	            
	         if (linea.contains("<sentence")) {
	        	 //System.out.println(linea);
	        	 i++;
	        	 sent=  i + "==> ";
	        	 if (linea.contains("<sentence complex=")) {
	        		 sent= "NEG " + i + "==> " ;
	        	 }
	        	 
	         }
	        	         
	         if (linea.contains("</senten")) {
	        	 //System.out.println(linea);
	        	 sentences.add(sent);
	        	 System.out.println(sent);
	        	 sent="";
	        	 
	        	 
	         }
	         
	         if (linea.contains("wd=")) {
	        	 String a[]= linea.split("wd=");
	        	// System.out.println(linea);
	        	 String temp= a[1].replaceAll("\"", "");
	        	 temp = temp.replace("/","");
	        	 temp = temp.replaceAll(">","");
	        	 temp = temp.trim();
	        	 sent = sent + " " + temp;
	         }
	       }
	            
	      }
	      catch(Exception e){
	         e.printStackTrace();
	      }finally{
	         // En el finally cerramos el fichero, para asegurarnos
	         // que se cierra tanto si todo va bien como si salta 
	         // una excepcion.
	         try{                    
	            if( null != fr ){   
	               fr.close();     
	            }                  
	         }catch (Exception e2){ 
	            e2.printStackTrace();
	         }
	      }
	      
	      return sentences;
	 }
	  
	  public void analyzeFileMap(HashMap<String, ArrayList<String>> sentencesMap) {
		  
		  
		  for (Map.Entry<String, ArrayList<String>> entry : sentencesMap.entrySet()) {
			    String fileName = entry.getKey();			   
			    ArrayList<String>  sentencesList = entry.getValue();
			    for(String sentence: sentencesList) {
			    	String sent[]= sentence.split("==>");			    	
			    				    	
			    	if (sent[0].contains("NEG")) {
			    		int tokens =analyzeSentenceLength(sent[1]);
				    	tokensNumber.add(tokens);
				    	negatedSentences1.add(sent[1]);
			    	}
			    	
			    }
		  }
		  //print
		  
		  
	  }
	  
	  
	  public int analyzeSentenceLength(String sentence1) {
		  StringTokenizer tokenizator = new StringTokenizer(sentence1.trim(), " ");
		  //System.out.println("sentence: " + sentence1 + "  tokens: " + tokenizator.countTokens());
		  return tokenizator.countTokens();
	  }
	  
	  public void printLength() {
		  System.out.println ("\nPrinting tokens number\n");
		  for (Integer t: tokensNumber) {
			  System.out.println (t);
		  }
	  }
	  
	  public void printNegatedSentences() {
		  System.out.println ("\n ************* Negated Sentences ************************");
		  for (String sent: negatedSentences1) {
			  System.out.println (sent);
		  }
		  System.out.println ("\n ************* Negated Sentences ************************");
	  }
	  
	  public void countDistribution() {
		  System.out.println ("\n Caltulating distribution \n");
		  HashMap<Integer, Integer> freqMap = new HashMap<Integer, Integer>();
		  for (int i=1; i<=200;i++) {
			  int count=0;
			  for (Integer t: tokensNumber) {
				  if (t==i) {
					  count++;
				  }
			  }
			  
			  freqMap.put(i,count);
		  }
		  
		  //Printing
		  System.out.println("\nPrinting frequencies");
		  for (Map.Entry<Integer, Integer> entry : freqMap.entrySet()) {
			   int x = entry.getKey();			   
			   int freq =  entry.getValue();
			   System.out.println( freq);
		  }
		  
	  }
	  
	  
	  public int getTotalSentences() {
		  return this.totalSentences;
	  }
	  
	  public ArrayList<SentenceText> getSentenceObjects(){
		  return sentenceObjectList;
	  }
	  
	  public ArrayList<String> init() {
		 String dir ="/home/kdd/Descargas/corpus_SFU/SFU/";
		  
		  ArrayList<String> paths = new ArrayList<String>();
		

		  paths.add(dir+"coches");
		  paths.add(dir+"hoteles");
		  paths.add(dir+"lavadoras");
		  paths.add(dir+"libros");
		  paths.add(dir+"moviles");
		  paths.add(dir+"musica");
		  paths.add(dir+"ordenadores");
		  paths.add(dir+"peliculas");
		  
		 return paths;
		  
	  }
	  public void printTotales() {
		  System.out.println("Files: " + totalFiles);
		  System.out.println("Sentences: " + totalSentences);	
		  System.out.println("Sentences Neg: " + tokensNumber.size());
		  
	  }
	  
	  public ArrayList<String>  loadDictionary() {		 
			 DictionaryLoader loader = new DictionaryLoader();
			 ArrayList<String> dict = loader.loadDictionary();
			 ArrayList<String> dictionary = configureNegationLists(dict);
			 return dictionary;
			 
		}//end Method
	  
	  public ArrayList<String> configureNegationLists(ArrayList<String> list){
	        ArrayList<String> dict= new ArrayList();
	        
	        //System.out.print("\n \n \n\n \n  ********************************************************\n \n \n \n");
	        
	        for (int i=0; i<list.size();i++){
	            String   line =  list.get(i);
	            String line_words[]= line.split("\t");
	           
	            //System.out.println ("Line: " + i + " " + line);
	            
	            if(line_words[1].equals("[pre-negation]")){
	            	dict.add(line_words[0].toLowerCase());            
	            }
	            
	            if(line_words[1].equals("[post-negation]")){
	            	dict.add(line_words[0].toLowerCase());            
	            }
	           
	            
	            if(line_words[1].equals("[uncertainty]")){
	            	dict.add(line_words[0].toLowerCase());            
	            }
	            
	            if(line_words[1].equals("[post-uncertainty]")){
	            	dict.add(line_words[0].toLowerCase());            
	            }
	                                       
	          
	           
	        }//end for
	        
	        return dict;
	     
	        
	 }//End Method
	  
	  public void search() {
		  ArrayList<String> dict = this.loadDictionary();
		  int i=1;
		  for (String sent:negatedSentences1) {
			  
			  for(String cue: dict) {
				  findPreNegationsRegex(sent, cue);
			  }
			  i++;
		  }
		  
		  countEachCue(dict);
	  }
	  
	  public void countEachCue(ArrayList<String> dict) {
		  for(String cue: dict) {
			  int count=0;
			  for (Z_SFU_Object ob: cuesObject) {
				    String cueDetected = ob.getCue();
				    if (cue.equals(cueDetected)) {
				    	count ++;
				    	if(ob.getFreq() > 1) {
				    		System.out.println("Contiguous " + ob.getFreq());
				    	}
				    }
			  }
			  cues.put(cue, count);
			  
		  }
	  }
	  
	  public void printCuesResult() {
		  System.out.println("========== Cues Freq===============");
		  //for (Z_SFU_Object ob: cuesObject) {
			//    System.out.println(ob.getCue() +  "\t " + ob.getFreq());
		  //}
		  
		  for (Map.Entry<String, Integer> entry : cues.entrySet()) {
			  if(entry.getValue() > 0) {
				  System.out.println(entry.getKey() +  "\t" + entry.getValue());
			  }
			  
		  }
	  }
	  
	  public  ArrayList<Integer> findPreNegationsRegex(String sent, String negationPhrase) {
		    
			ArrayList<Integer> result = new ArrayList<Integer>();		   
			/*
			 * Algunos términos como no, sin, descartar son ambiguos y pueden 
			 *  indicar   doble negación lo que se puede conseguir en no-negación
			 * Ej: no se puede descartar ....
			 */
			
			
			    
	    	 String sentenceText = sent.toString().toLowerCase();		  			    
			 String pattern = "\\b" + negationPhrase + "\\b";
			 
			 if(negationPhrase.equals("no") ){
				pattern = "\\b" + negationPhrase + "\\b|\\bni\\b";
				//System.out.println("Pattern ----->" + pattern);
			 }
			 
			 
			 
			 Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
			 Matcher m = p.matcher(sentenceText);				
			 								
			  while(m.find()) {	
					result.add(m.start());
					
						
						/*System.out.println("Sentence: " + sentenceText);								            
					    System.out.println("Negated Phrase start (dentro de frase:) => " + m.start() 
							                                 + "\t => " +  negationPhrase
							                                 + "\t  => \t Length: (" +  negationPhrase.length() 
							                                 + ")" + "\t sentence.begin => " +  1  +   "\n ");*/
					   
			  }//end while
			  
			  if(result.size() > 0) {
				  Z_SFU_Object ob =new Z_SFU_Object();
				  ob.setCue(negationPhrase);
				  ob.setFreq(result.size());
				  cuesObject.add(ob);
				  
				  if (result.size() > 1){
					  System.out.println("xxx " + negationPhrase + " " + result.size() +  " --> " +  sent);
				  }
				  
			  }
						
			return result;

}//end method   
	  
	 public void printInFile() {
		 String fileOut="Number;Tokens" +"\n";
		 int masDe200 = 0;
		 int i=1;
		 for(int token:tokensNumber) {
			 fileOut = fileOut + i + ";" + token + "\n";
		 }
		 
		 String ruta = "/home/kdd/ctb/paper_new/fileSFU.csv";
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


	  public static void main(String a[]) {
		  String directory = "/home/kdd/Descargas/corpus_SFU/SFU/coches";
		 
		  Z_SFUCorpusLoader loader= new Z_SFUCorpusLoader();
		  ArrayList<String> paths= loader.init();
		  for (String path: paths) {
			  
			  HashMap<String, String> map = loader.loadDirectory(directory);
			  System.out.println("map size: " + map.size());
			 
			  System.out.println("\n************************************");
			 HashMap<String, ArrayList<String>>  sentences = loader.loadSentences(map, directory);
			 loader.analyzeFileMap(sentences);
			  System.out.println("\n************************************");
		  }
		  
		 
		 //loader.printNegatedSentences();
		 loader.countDistribution();
		 loader.printTotales();
		 loader.search();
		 loader.printCuesResult();
		 loader.printInFile();
		  
	  }
  
}


