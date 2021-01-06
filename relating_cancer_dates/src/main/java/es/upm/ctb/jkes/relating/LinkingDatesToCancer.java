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
	ArrayList<String> events;
	
	public LinkingDatesToCancer() {
		sentencesId =  new ArrayList <Integer>();
		events = new ArrayList<String> ();
	}
	
	public void loadDataBase() {
		try {
			databaseController = new DataBaseController();
			//System.out.println("Data Base OK");	
		}
		catch(Exception e) {
			System.out.println("Error Accessing the Patient database, check connetion permission");	
		}
		
		//load events from database
		this.getEvents();
		
	}
	
	
	
	public void getEvents() {
		events =databaseController.getEvents();
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
															  
		ArrayList<Link> linkList = new ArrayList<Link>();
		
		for (Integer sentenceId: sentencesId) {
			ArrayList<Annotation> sentenceAnnotations= databaseController.getSentenceAnnotations(sentenceId);
			
			ArrayList<Annotation> annotationsWithAncetors=checkAncestors(sentenceAnnotations, pipeLine1);
			ArrayList<Link> linksTemp=  createdLinks(annotationsWithAncetors);
			linkList.addAll(linksTemp);
		}
		
		databaseController.saveLinks(linkList);
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
	
	public ArrayList<Link> createdLinks(ArrayList<Annotation> annotationsWithAncetors) {
		ArrayList<Link> linkList = new ArrayList<Link>();
		
		for (Annotation ann: annotationsWithAncetors) {
			String entity= ann.getEntity();
			if (entity.equals("cancer_entity")) {
				String entityValue=ann.getEntityValue();
				
				Link link=searchDateAsSon(annotationsWithAncetors, ann );
				if (link.equals(null)== false) {
					linkList.add(link);
				}
				else {
					link=checkSameParent(annotationsWithAncetors, ann);
					
					if (link.equals(null)== false) {
						linkList.add(link);
					}
					else {
						link = checkSamePredicate(annotationsWithAncetors, ann );
						if (link.equals(null)== false) {
							linkList.add(link);
						}
					}
				}
				
				
				
			}
			
		}
		return linkList;
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
					link.setPatient(ann.getPatientId());
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
		
	}//end method
	
	public Link checkSameParent(ArrayList<Annotation> annotationsWithAncetors, Annotation cancerAnn ){
		Link link= new Link();
		String cancerAncestor=cancerAnn.getAncestor();		
		boolean linked=false;
		
		for (Annotation ann: annotationsWithAncetors) {
			String entity= ann.getEntity();
			if (entity.equals("date")) {
				String ancestor = ann.getAncestor();
				if (ancestor.contentEquals(cancerAncestor)) {
					
					link.setCancerEntity(cancerAncestor);
					link.setDate(ann.getEntityValue());
					link.setNormalized(ann.getNormalized());
					link.setSentenceId(ann.getSenteceId());
					link.setSentence(ann.getSentence());
					link.setPatient(ann.getPatientId());
					linked=true;
					
				}
			}
		}//for
		
		if (linked== true) {
			return link;
		}
		else {
			return null;
		}
	}//end method
	
	public Link checkSamePredicate(ArrayList<Annotation> annotationsWithAncetors, Annotation cancerAnn ){
		Link link= new Link();
		String cancerAncestor=cancerAnn.getAncestor();		
		boolean linked=false;
		
		for (Annotation ann: annotationsWithAncetors) {
			String entity= ann.getEntity();
			if (entity.equals("date")) {
				
				String sentence = cancerAnn.getSentence();
				int cancerEntityIndex= sentence.indexOf(cancerAnn.getEntityValue());
				int dateIndex=sentence.indexOf(ann.getEntityValue());
				boolean related = true;
				
				if(cancerEntityIndex < dateIndex) {
					related = searhForEvent( dateIndex, cancerAnn);
				}
				else {
					related = searhForEvent( cancerEntityIndex, cancerAnn);
				}
				
				if (related == true) {
					link.setCancerEntity(cancerAncestor);
					link.setDate(ann.getEntityValue());
					link.setNormalized(ann.getNormalized());
					link.setSentenceId(ann.getSenteceId());
					link.setSentence(ann.getSentence());
					link.setPatient(ann.getPatientId());
					linked=true;
					
				}
				
				
					
				
			}
		}//for
		
		if (linked== true) {
			return link;
		}
		else {
			return null;
		}
	}//end method
	
	public boolean searhForEvent(int limitIndex, Annotation cancerAnn) {
		String sentence= cancerAnn.getSentence();
		boolean related = true;
		for (String event:events) {
			int eventIndex = sentence.indexOf(event);	
			if (eventIndex >= 1 && eventIndex < limitIndex ) {				
					related =false;
				
			}
			
		}
		return related;
	}
	
}

