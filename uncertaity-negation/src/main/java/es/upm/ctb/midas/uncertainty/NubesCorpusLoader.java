package es.upm.ctb.midas.uncertainty;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class NubesCorpusLoader {
	int totalSentences;
	int sentenceId;
	ArrayList<String> sentences;
	ArrayList<SentenceText> sentenceObjectList;
	
	public NubesCorpusLoader() {
		totalSentences=0;
		sentenceId =0;
		
		sentences = new ArrayList<String> ();
		sentenceObjectList = new  ArrayList<SentenceText> ();
	}
	
	public HashMap<String, String> loadDirectory(String path) {
		System.out.println("\n");
		//String path = "/datos-oswaldo/PhD/git-external/NUBes/NUBes";		
		HashMap<String, String> map1 = new HashMap<String, String>();
		
		for (int i=1; i<=10; i++) { 
			File carpeta = null;
			if (i<=9) {
				carpeta = new File (path + "/"+"SAMPLE-00" + i);
				//carpeta = new File (path );
			}
			else {
				carpeta = new File (path + "/"+"SAMPLE-0" + i);
				//carpeta = new File (path );
			}
			
			System.out.println("\n " + carpeta);
			
			for (final File file : carpeta.listFiles()) {
			       	String name= file.getName();		        	
			        String extension = name.substring(name.lastIndexOf("."));
			        System.out.println("File " + file);
			        
			        if(extension.equals(".txt")) {
			        		map1.put(name, file.getAbsolutePath());
			        		System.out.println(name);
			        
			        }
			 }//for
			
		}//for
		
		System.out.println("map size: " + map1.size());
		return map1;
		
	  }//end
	
	  public HashMap<String, ArrayList<String>> loadSentences(HashMap<String, String> mapFiles) {
		  HashMap<String, ArrayList<String>> sentencesMap = new HashMap<String, ArrayList<String>>();
		  int i=0;
		  for (Map.Entry<String,String> entry : mapFiles.entrySet()) {
			    String key = entry.getKey();			   
			    String  filePath = entry.getValue();
			    System.out.println("File " +  filePath );
			    ArrayList<String> list = loadTextFile(filePath);
			   
			    String fileName = key + "_" + i;
			    sentencesMap.put(fileName, list);
			    totalSentences = totalSentences + list.size();
			    
			}
		  System.out.println("total sentences: " + totalSentences);
		  
		 return sentencesMap;
	  }
	  
	  public ArrayList<String> loadSentences1(HashMap<String, String> mapFiles) {
		  HashMap<String, ArrayList<String>> sentencesMap = new HashMap<String, ArrayList<String>>();
		 
		  for (Map.Entry<String,String> entry : mapFiles.entrySet()) {
			   
			    String key = entry.getKey();			   
			    String  filePath = entry.getValue();
			    System.out.println("File " +  filePath );
			    ArrayList<String> list = loadTextFile(filePath);
			    sentences.addAll(list);
			    			    
			    totalSentences = totalSentences + list.size();
			    
			    
			    
			}
		  
		 return sentences;
	  }
	  
	  public ArrayList<String> loadTextFile(String path) {
		  ArrayList<String> list1 = new  ArrayList<String>();
		 // System.out.println("\n===============================================================");
		  System.out.println("Path: " + path);
			try {
				BufferedReader bf = new BufferedReader(new FileReader(path));
				
				String line="";				
				
				
				try {
					while ((line = bf.readLine())!=null) {
						list1.add(line);
						System.out.println("Line " + line);
						
						sentenceId ++;
						SentenceText obj= new SentenceText();
						obj.setId(sentenceId);
						obj.setSentence(line);
						obj.setFilePath(path);
						sentenceObjectList.add(obj);
						
					 }//while
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			  //System.out.println("\n===============================================================\n");	
		
		System.out.println("\n Sentences Loaded Succesful \n");	
		
		return list1;
		  
	  }//end
	  
	  static public int countSentenceTokens(String sentence1) {
		  StringTokenizer tokenizator = new StringTokenizer(sentence1, " ");
		  //System.out.println(" Num Tokens " + tokenizator.countTokens());
		  return tokenizator.countTokens();
		  
	  }
	  
	  public int getTotalSentences() {
		  return this.totalSentences;
	  }
	  
	  public ArrayList<SentenceText> getSentenceObjects(){
		  return sentenceObjectList;
	  }

	  public static void main(String a[]) {
		  String directory = "/home/kdd/Descargas/NUBes";		
		  NubesCorpusLoader loader = new NubesCorpusLoader();
		  
		  System.out.println("\n************************************");
		  HashMap<String, String> map = loader.loadDirectory(directory);
		  System.out.println("map size: " + map.size());
		  System.out.println("\n************************************");
		  
		  loader.loadSentences(map);
		  //loader.loadDirectory(file1);
	  }
  
}


