package es.upm.ctb.jkes.relating;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cz.cuni.mff.ufal.udpipe.Model;
import cz.cuni.mff.ufal.udpipe.Pipeline;
import cz.cuni.mff.ufal.udpipe.ProcessingError;

public class LinkingDatesToCancer {
	cz.cuni.mff.ufal.udpipe.Model udPipeModel ;
	Pipeline pipeline;
	DataBaseController databaseController;
	ArrayList<Integer> sentencesId;
	
	public LinkingDatesToCancer() {
		sentencesId =  new ArrayList <Integer>();
	}
	
	public void loadDataBase() {
		try {
			databaseController = new DataBaseController();
			System.out.println("Data Base OK");	
		}
		catch(Exception e) {
			System.out.println("Error Accessing the Patient database, check connetion permission");	
		}
		
	}
	public void loadUDPipe() {
			ClassLoader classLoader = getClass().getClassLoader();
			udPipeModel= null;
			try {
				String path = classLoader.getResource("spanish-ancora-ud-2.1-20180111.udpipe").getPath();
				System.out.println("Udpipe Path: " + path);				
				udPipeModel = Model.load(path);			
				System.out.println("UDpipe model loaded");
			}catch(Exception e) {
				System.out.println("UDpipe exception " + e.getMessage());
				udPipeModel= null;
				System.out.println("*** Error: Accesing udpipe ... Check path. ****\n");
				
			}
			
						
	}
	
	public Pipeline createUdPipeline() {
		String input="horizontal";
		String output="matxin";	
		Pipeline pipeline = new Pipeline(udPipeModel, input, Pipeline.getDEFAULT(), Pipeline.getDEFAULT(), output);
		return pipeline;
	}
	
	
	
	public void linking() {
		sentencesId.clear();
		sentencesId = databaseController.getSentencesId();
		
		Pipeline pipeLine1= this.createUdPipeline();
															  
		
		
		for (Integer sentenceId: sentencesId) {
			ArrayList<Annotation> sentenceAnnotations= databaseController.getSentenceAnnotations(sentenceId);
			
			ArrayList<Annotation> annotationsWithAncetors=checkAncestors(sentenceAnnotations, pipeLine1);
			//createLinks();
		}
	}
	
	public void printAnnotations(ArrayList<Annotation> sentenceAnnotations1) {
		for (Annotation ann: sentenceAnnotations1) {
			System.out.println("sentence: " + ann.getSentence());
			System.out.println("entity: " + ann.getEntity());
			System.out.println("entity value: " + ann.getEntityValue());
			System.out.println("\n \n");
			
		}
	}
	
	public ArrayList<Annotation> checkAncestors(ArrayList<Annotation> sentenceAnnotations1,  Pipeline pipeline) {
		TreeManager tree = new TreeManager();
		ArrayList<Annotation> annotationsWithAncestor= new ArrayList<Annotation>();
		
		for (Annotation annotation: sentenceAnnotations1) {
			String entity= annotation.getEntity();
			String ancestor="";
			
			if (entity.equals("cancer_entity") || entity.equals("date")) {
				String sentence =annotation.getSentence();
				String entityValue= annotation.getEntityValue();
				String xml = pipeline.process(sentence);
				
				try {
					ancestor = tree.getAncestor(xml, sentence, entityValue);
					annotation.setAncestor(ancestor);
				}
				catch(Exception e) {
					
				}
			}//end if
			annotationsWithAncestor.add(annotation);
			System.out.println("sentence: " + annotation.getSentence());
			System.out.println("entity: " + annotation.getEntity());
			System.out.println("entity value: " + annotation.getEntityValue());
			System.out.println("Ancestos: " + annotation.getAncestor());
			System.out.println("\n **************\n");
		}
		
		return annotationsWithAncestor;
	}
	
	public void createdLinks(ArrayList<Annotation> annotationsWithAncetors) {
		for (Annotation ann: annotationsWithAncetors) {
			String entity= ann.getEntity();
			if (entity.equals("cancer_entity")) {
				String entityValue=ann.getEntityValue();
				
				Link link=searchDateAsSon(annotationsWithAncetors, ann );
				
			}
			
		}
	}
	
	public Link searchDateAsSon(ArrayList<Annotation> annotationsWithAncetors, Annotation cancerAnn ){
		Link link= new Link();
		boolean linked=false;
		for (Annotation ann: annotationsWithAncetors) {
			String entity= ann.getEntity();
			if (entity.equals("date")) {
				String ancestor = ann.getAncestor();							
				String cancerEntityValue=cancerAnn.getEntityValue();
				
				if (cancerEntityValue.contains(ancestor)) {					
					link.setCancerEntity(cancerEntityValue);
					link.setDate(ann.getEntityValue());
					link.setNormalized(ann.getNormalized());
					link.setSentenceId(ann.getSenteceId());
					link.setSentence(ann.getSentence());
					linked=true;
				}
			}
			
		}
		if (linked== true) {
			return link;
		}
		else {
			return null;
		}
		
	}
	
	
}

