package es.upm.ctb.midas.uncertainty;

import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.semgraph.*;
import edu.stanford.nlp.util.*;

import java.util.*;

public class PrintParser {
	Properties props;
	StanfordCoreNLP pipeline;

  public PrintParser() {
	  props = new Properties();
      props.setProperty("annotators", "tokenize, ssplit, pos, lemma,parse");        
      props.setProperty("tokenize.language", "es");
      props.setProperty("pos.model", "edu/stanford/nlp/models/pos-tagger/spanish/spanish-distsim.tagger");        
     //props.setProperty("parse.model", "/edu/stanford/nlp/models/srparser/spanishSR.ser.gz");       
      props.setProperty("pos.maxlen", "300");
      
      //Create a pipeLine
      pipeline = new StanfordCoreNLP(props);
  }
  
  public void print(String text) {
	  Annotation annotation = new Annotation(text);
      pipeline.annotate(annotation);
      List<CoreMap> sentences = annotation.get(SentencesAnnotation.class);
      Tree constituencyParse = null;
      for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
          constituencyParse = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
          System.out.println(constituencyParse);
          SemanticGraph dependencyParse =
              sentence.get(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class);
          System.out.println(dependencyParse.toList());
          
        }
      System.out.println("\n****************************************\n");
     
     
      List<Tree> leaves = constituencyParse.getLeaves();
      List<Tree> childs = constituencyParse.getChildrenAsList();
      // Print words and Pos Tags
      for (Tree leaf : leaves) { 
          Tree parent = leaf.parent(constituencyParse);
          System.out.println(leaf.label().value() + "\t " + parent.label().value() + " ");
        
      }
      System.out.println("\n****************************************\n");
     
     
        
  }
  public static void main(String[] args) {
	  PrintParser parser = new PrintParser();
	  String text = "Sin clínica organotópica asociada se inicia exploracion física adonida.";
	  parser.print(text);
	
  }

}
