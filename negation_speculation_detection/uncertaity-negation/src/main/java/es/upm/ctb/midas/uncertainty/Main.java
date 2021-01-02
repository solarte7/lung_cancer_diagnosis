package es.upm.ctb.midas.uncertainty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.cuni.mff.ufal.udpipe.Model;
import cz.cuni.mff.ufal.udpipe.Pipeline;
import edu.stanford.nlp.util.CoreMap;

public class Main {
	SpanishTokenizer tokenizer;
	Annotator annotator1;
	
	//Solo para nubes corpus
	ArrayList<String> nubesCorpus;
	ArrayList<SentenceText>  sentenceObjects;
	
	
	public Main() {
		tokenizer =  new SpanishTokenizer();
		annotator1 = new Annotator();
		
	}
	
	
	public void processSentence(String text) {
		List<CoreMap> sentences = tokenizer.tokenizeDocument(text.toLowerCase());
		annotator1.process(sentences, "path");		 
		annotator1.printScopes();		
		System.out.println("\n ============  Finished ok============ \n");
	}
	
	public void loadTestCorpus(String directory) {
		  
		  NubesCorpusLoader loader = new NubesCorpusLoader();		  
		  System.out.println("\n************************************");
		  HashMap<String, String> map = loader.loadDirectory(directory);
		  System.out.println("map size: " + map.size());
		  System.out.println("\n************************************");
		  
		  nubesCorpus = loader.loadSentences1(map);
		  sentenceObjects = loader.getSentenceObjects();
	}
	
	public void processDocument(String doc) {
		 List<CoreMap> sentences = tokenizer.tokenizeDocument(doc);
		 annotator1.process(sentences, "path");		 
		 annotator1.printScopes();
		 
		ArrayList<Scope>  scopeList = annotator1.getScopeList();
		saveScopes(scopeList, "uncertain");
	}
	
	public void processAllDocuments(String path) {
				
		this.loadTestCorpus(path);		
		 
		System.out.println("\n  Nubes Corpus Loaded: -> " + nubesCorpus.size() + " sentences");
		System.out.println("\n Procesando Nubes Corpus ...");
		System.out.println ("\n ===============  Iniciando anotacion ============ \n");
		int i=0;
		
		
		for (SentenceText line: sentenceObjects) {
			   String textLine = line.getSentence();
			   List<CoreMap> sentences = tokenizer.tokenizeDocument(textLine);
			   
			   for(CoreMap sentence: sentences) {
				   List<CoreMap> sent= tokenizer.tokenizeDocument(sentence.toString());
				   annotator1.process(sent, line.getFilePath());
				   annotator1.printScopes();
			   }//end for
			   
			   i++;
			   System.out.println("Processing sentences " + i + "/" + sentenceObjects.size());
			   
		    	    
		}//for
		
		ArrayList<Scope>  scopeList = annotator1.getScopeList();
		saveScopes(scopeList, "uncertain");
		
	}//end method
	public void saveScopes(ArrayList<Scope>  scopeList, String annotator) {
		System.out.println("Saving Scopes .... ");
		MysqlUncertainty  mySqlDB= new MysqlUncertainty ();
		mySqlDB.saveScopes(scopeList, annotator);
	}
	
	
	
	public static void main (String args[]) {			
		Main main = new Main();	
		String text1 = "Posible cancer de pulmón."; 
		main.processSentence(text1);
	}

}
