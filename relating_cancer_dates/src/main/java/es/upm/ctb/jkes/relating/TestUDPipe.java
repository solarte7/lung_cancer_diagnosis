package es.upm.ctb.jkes.relating;
import java.util.ArrayList;
import java.util.Arrays;

import cz.cuni.mff.ufal.udpipe.*;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.*;
import java.util.*;

public class TestUDPipe {
	Pipeline pipeline;
	Model model;
	public TestUDPipe() {
		String path="/datos-oswaldo/univalleRedes/spanish-ancora-ud-2.1-20180111.udpipe";
		String input="horizontal";
		String output="matxin";
		model = Model.load(path);
		System.out.println("\n Model Loaded \n");
		pipeline = new Pipeline(model, input, Pipeline.getDEFAULT(), Pipeline.getDEFAULT(), output);
	}
	public String process(String sent1, String date1) {		
		//ProcessingError error = new ProcessingError() ;
		String proc = pipeline.process(sent1);
		
		System.out.println(proc);
		
		String a[] = proc.split("\n");
		
		for (int i=0; i<a.length;i++) {
			//System.out.println(i + "-> " + a[i] );
		}
		getScope(sent1, proc, date1);
		
	 return proc;
		
	}
	
	public void getScope(String sentence, String xmlTree, String date1) {
		TreeManager tree =new TreeManager();
		tree.getAncestor(xmlTree, sentence, date1);
	}
	
	 public static void main(String[] args) {
	   TestUDPipe t= new TestUDPipe();
	   String sent = "Paciente diagnosticado con cancer de pulmón el 15-08-2017 tras inicar estudios en enero de 2017 .";
	   t.process(sent, "cancer");
	 }
	  
	
	
}
