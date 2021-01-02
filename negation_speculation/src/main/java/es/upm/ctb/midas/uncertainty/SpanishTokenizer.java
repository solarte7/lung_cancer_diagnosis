package es.upm.ctb.midas.uncertainty;

import java.util.Properties;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.pipeline.CoreNLPProtos.Sentence;
import edu.stanford.nlp.util.*;
import edu.stanford.nlp.util.TypesafeMap.Key;
import java.util.*;

import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.ie.util.*;
import edu.stanford.nlp.semgraph.*;
import edu.stanford.nlp.trees.*;

public class SpanishTokenizer {
	Properties props;
	StanfordCoreNLP pipeline;
	
	public SpanishTokenizer(){
		props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos");        
        props.setProperty("tokenize.language", "es");
        props.setProperty("pos.model", "edu/stanford/nlp/models/pos-tagger/spanish/spanish-distsim.tagger");        
       //props.setProperty("parse.model", "/edu/stanford/nlp/models/srparser/spanishSR.ser.gz");       
        props.setProperty("pos.maxlen", "300");
        
        //Create a pipeLine
        pipeline = new StanfordCoreNLP(props);
		
	}
	
	/*
	 * Tokeniza un documento y retorna el conjunto de sentencias.
	 */
	public List<CoreMap> tokenizeDocument(String text) {	   
        Annotation annotation = new Annotation(text);
        pipeline.annotate(annotation);
        List<CoreMap> sentences = annotation.get(SentencesAnnotation.class);       
        return sentences;
	}//end
	
	public void tokenizeDocumentTest(String text) {
		 Annotation annotation = new Annotation(text);
	        pipeline.annotate(annotation);
	        List<CoreMap> sentences = annotation.get(SentencesAnnotation.class);
	       
	        
	        for(CoreMap sentence: sentences) {
	        	 System.out.println("\n Size: "  + sentence.toString().length());
	        	 System.out.println("sentence: " + sentence.toString());
	        	System.out.println("TOKENS: " + sentence.get(TokensAnnotation.class).size());
	        	       
	        	 
	        	 for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
	                 String word = token.get(TextAnnotation.class);
	                 token.beginPosition();
	                //System.out.println("Token: " +  word );
	                System.out.println(word + " -> " + token.get(PartOfSpeechAnnotation.class) +
	                		"\t" + token.beginPosition() + ":" + token.endPosition());
	                
	             }
	          
	            
	        }//for  
		
		
	}
	public static void main(String args []) {
		SpanishTokenizer spa = new SpanishTokenizer();
		String text1 = "diagnostiado de Carcinoma microcitico pulmon derecho con afectacion ganglionar mediastinica.";
		spa.tokenizeDocumentTest(text1.toLowerCase());
		//spa.tokenizeDocument(text1.toLowerCase());
	}

}//end class

