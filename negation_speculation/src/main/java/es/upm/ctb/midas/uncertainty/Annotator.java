
package es.upm.ctb.midas.uncertainty;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.cuni.mff.ufal.udpipe.Model;
import cz.cuni.mff.ufal.udpipe.Pipeline;
import edu.stanford.nlp.coref.data.Document;
import edu.stanford.nlp.coref.data.DocumentPreprocessor;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.SentenceUtils;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.CoreNLPProtos.Sentence;
import edu.stanford.nlp.util.CoreMap;

public class Annotator {
	
     private ArrayList<Scope> scopeList;//to save scope     
     private CuesDetector cuesDetection;
     private ScopeResolution scopeResolution;     
	
	 final int PRE_NEGATION = 1;
	 final int POST_NEGATION = 2;	 
	 final int SHORT_SENTENCE_HEURISTIC= 6; //Number of tokens in a sentence. This value was obtained from Exploratory Data Analysis (First Quartile)
	 final int LARGE_SENTENCE_HEURISTIC= 70; //Number of tokens in a sentence. This value was obtained from Exploratory Data Analysis (Second Quartile)
	
	 
	 
	 //Initialize the DetectorAnnotator
	 public Annotator() {   
		  ArrayList<String> dictionary = loadDictionary();
		  scopeList = new ArrayList<Scope>();
		  
		  cuesDetection = new CuesDetector(dictionary, PRE_NEGATION, POST_NEGATION, SHORT_SENTENCE_HEURISTIC);
		  scopeResolution = new ScopeResolution(dictionary, PRE_NEGATION, POST_NEGATION, SHORT_SENTENCE_HEURISTIC, LARGE_SENTENCE_HEURISTIC);
		 
		   
		   //cuesDetection.printNegatedPhrases();
		   //scopeResolution.printTerminationTerms();
				 
	 }//end Method
	 
	 /**
	  * This method detecs  the uncertainty
	  * @param sentences
	  */
	 public void process(List<CoreMap> sentences, String filePath) {
		 
		 for(CoreMap sentence: sentences) {		 			 
			 try {
				   //========== Buscando  téminos pre-negados y calculando scope ===================
					//System.out.println("------- Sentence " + sentence.toString() + " ---------");
					HashMap<Integer,CuePosition> pre_NegationsMap = cuesDetection.getPreNegation(sentence);	
					
					//System.out.println("\n  Buscando pre-negados -> size():  " + pre_NegationsMap.size());
									
					if(pre_NegationsMap.size() > 0) {							
						//ArrayList <NegationScope> pre_ScopeList = getNegationScope(pre_NegationsMap, sentence, PRE_NEGATION, filePath);
						ArrayList <Scope> pre_ScopeList = scopeResolution.getNegationScope(pre_NegationsMap, sentence, PRE_NEGATION, filePath);					
						addNegationsToIndices(pre_ScopeList);
								
					}//if
					
					
				   //============= Buscando  téminos post-negados en la sentencia ====================
					
					if(sentence.get(TokensAnnotation.class).size() < SHORT_SENTENCE_HEURISTIC && pre_NegationsMap.size() > 0) {
						//Si la sentencia es corta y se encontraron terminos pre-negados, no se busca post-negados. Se pasa a la siguiente sentencia.
						//iterator.moveToNext();		
						//System.out.println("No se procesa post-negated, se pasa a la siguiente sentencia.");
						
					}
					else {
						
						HashMap<Integer,CuePosition> post_NegationsMap = cuesDetection.getPostNegation(sentence);
						//System.out.println("\n \n \n ============================================================================= \n");
						//System.out.println("\n Buscando post-negados -> size(): " + post_NegationsMap.size());
						
							 
						if (post_NegationsMap.size() > 0) {
									ArrayList <Scope> post_ScopeList = scopeResolution.getNegationScope(post_NegationsMap, sentence, POST_NEGATION, filePath);
									addNegationsToIndices(post_ScopeList);
								
						}
						
						
						// New code for detecting morphological Negation.
						HashMap<Integer,CuePosition> morphologicalMap = cuesDetection.getMorphologicalNegation (sentence);
						if (morphologicalMap.size() > 0) {
							ArrayList <Scope> morphoScopeList = scopeResolution.getMorphologicalScope(morphologicalMap, sentence);
							addNegationsToIndices (morphoScopeList);
						}
					
				
				    }
			
				
				
			}catch(Exception e) {
				System.out.println("\n\n  Error al procesar el anotador de Negación:  " +  e.getMessage() + "\n \n \n");
				System.out.println("\n\n  Sentencia: " +  sentence.toString()  +  "\n ");
				e.printStackTrace();
			}
		 }// end for
		 
		 //printScopes();
		
	 }//end method
	 /**
	  * add scopes to list
	  * @param scopeList
	  * @param 
	  */
	 public void addNegationsToIndices(ArrayList <Scope> list1) {
		   if (list1.size() >0) {
			   scopeList.addAll(list1);	
		   }
			
	 }//end method
	 
		
  
  public ArrayList<Scope> getScopeList() {
	  return scopeList;
  }
  
  
	 
	 //==================== METODOS LLAMADOS POR EL CONSTRUCTOR AL INICIO ==========================
	//================================================================================================ 
	//================================================================================================ 
	 public ArrayList<String>  loadDictionary() {		 
		 DictionaryLoader loader = new DictionaryLoader();
		 ArrayList<String> dict = loader.loadDictionary();
		 return dict;
		 
	}//end Method
	 
		 
	 
	    
	    
	    /**
		  * Print scopes
		  */
		   
		  public void printScopes() {
			  System.out.println("\n \n *********************  Results ******************************\n ");
			  for(Scope scope: scopeList) {
				  String sentence =scope.getSentence();
				  String cue =scope.getPhrase();
				  int begin = scope.getBegin();
				  int end = scope.getEnd();
				  String type= scope.getType();
				  
				  System.out.println ("\nCue:" + cue + "\t[" + type + "]" );		 	
				  //System.out.println ("Sentence: " + sentence);
				  //System.out.println ("Sentence lenght: " + sentence.length());		 
				  System.out.println ("Begin: " + begin +  "\t End: " + end);
				  try {
					  System.out.println ("Scope: " + sentence.substring(begin, end) + "\n");
				  }
				  catch (Exception e) {
					// TODO: handle exception
				}
			  }
		  }//end
	   
   /**
   * Metodo de uso temporal, solo se debe llamar desde la GUI (Solo de prueba)
   * @return
   */
	public void cleanScopeList() {
			  scopeList.clear();
	}
	    
	public static void main(String args[]) {
	    	Annotator ann = new Annotator();
	    	
	}
	    
	 
}//end class
