package es.upm.ctb.midas.uncertainty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.util.CoreMap;

public class CuesDetector {
	 ArrayList<String> preNegationPhrases;	 
	 ArrayList<String> postNegationPhrases;	
	 ArrayList<String> doublePreNegationPhrases;
	 ArrayList<String> doublePostNegationPhrases;
	 ArrayList<String> preUncertaintyPhrases; 
	 ArrayList<String> postUncertaintyPhrases; 
	 ArrayList<String> morphologicalPhrases; 
	 
	 int PRE_NEGATION;
	 int POST_NEGATION;	 
	 int MORPHOLOGICAL_NEGATION;
	 int SHORT_SENTENCE_HEURISTIC; 
	
		
	public CuesDetector(ArrayList<String> dictionary, int pre, int post, int shortSent) {
		PRE_NEGATION = pre;
		POST_NEGATION = post;		
		SHORT_SENTENCE_HEURISTIC = shortSent;
		MORPHOLOGICAL_NEGATION = 0;
		
		setUpNegationLists();
		configureNegationLists(dictionary);
		//====  new code =========
		preNegationPhrases.addAll(preUncertaintyPhrases);   
		ArrayList<String> tmp1= new ArrayList<String>();
		tmp1.addAll(preNegationPhrases);

		preNegationPhrases.clear();	preNegationPhrases.clear();			
		MyStringSorter sorter1 = new MyStringSorter();
		preNegationPhrases = sorter1.sortRules(tmp1);
		
		// ==== Post phrases ========
		//adding post uncertainty
		postNegationPhrases.addAll(postUncertaintyPhrases);
		ArrayList<String> tmp2= new ArrayList<String>();
		tmp2.addAll(postNegationPhrases);
		
		postNegationPhrases.clear(); postNegationPhrases.clear();	
		MyStringSorter sorter2 = new MyStringSorter();
		postNegationPhrases = sorter2.sortRules(tmp2);	
		
	}
	/**
	 * Detect PreNgation
	 * @param sentence
	 * @return
	 */
	 public HashMap<Integer,CuePosition>  getPreNegation(CoreMap sentence ){ 
		  
	 	    int counter_prenegation_trigger=0; 	 	   
	 	    int limit_heuristic =0;
	 	    
	 	    if(sentence.get(TokensAnnotation.class).size() < SHORT_SENTENCE_HEURISTIC) {
	 	    	limit_heuristic = 2;
	 	    }
	 	    else {
	 	    	limit_heuristic = 7;
	 	    }
	 	    
	 	    
	         HashMap<Integer,CuePosition> myHashMap=new HashMap<Integer,CuePosition>();
	        
	         //Buscando frases pre-negadas
	         for(int i=0; i< preNegationPhrases.size() ; i++){            
	 	            String preNegPhrase = preNegationPhrases.get(i);            
	 	            
	 	            ArrayList<Integer> negationList =  findPreNegationsRegex(sentence, preNegPhrase);
	 	            int numberOfNegations =negationList.size();
	 	           //System.out.println("Cues detection..."  + numberOfNegations);
	 	            //Hay un solo elemento negato en la sentencia
	 	            if (numberOfNegations == 1){
	 	                	int negationBegin = negationList.get(0);	 	                	
	 	                	
	 	                // // se quiere guardar solo un elemento negado en la misma posición de la sentencia
	 		                if(myHashMap.get(negationBegin) == null){
	 		                    CuePosition negPosition= new CuePosition();
	 		                    
	 		                    //negPosition.setBegin( (negationBegin + sentence.getBegin() ));
	 		                    negPosition.setBegin( (negationBegin + 1 ));
	 		                    //negPosition.setEnd( (negationBegin + sentence.getBegin() +  preNegPhrase.length()) ); 
	 		                    negPosition.setEnd( (negationBegin + 1 +  preNegPhrase.length()) );               
	 		                    
	 		                    negPosition.setNegationType(PRE_NEGATION);
	 		                    negPosition.setIsContinuos(false);
	 		                    negPosition.setNegationPhrase(preNegPhrase);		                   
	 		                    
	 		                    myHashMap.put(negationBegin, negPosition);	 		                    
	 		                    counter_prenegation_trigger ++;	 		                    
	 		                    //temporal
	 		                    //configureMetric(sentence, numberOfNegations, 0, false);
	 		                    
	 		                }//if
	 		                
	 		                      
	 	            }//if
	             
	 	            //Hay más de un término negado de este tipo Ej: No hta, no FGR, no ALK
	 	            if(numberOfNegations > 1){
	 	            	
	 		            	//ArrayList <Integer> continuosList = buildContinuosList(negationList);
	 		            	Collections.sort(negationList);
	 		            	//Collections.sort(continuosList);
	 		            
	 		            			            	
	 		                for(int z=0; z < numberOfNegations; z++) {
	 		                	 //System.out.println(" *** NEgation List: " + numberOfNegations   + " z= " + z + " *** \n");
	 			                    int  negationBegin =  negationList.get(z); 	                    
	 			                   
	 			                    // se quiere guardar solo un elemento negado en la misma posición de la sentencia
	 			                    if(myHashMap.get(negationBegin) == null){                        
	 				                        CuePosition negPosition = new CuePosition();
	 				                       
	 				                        //negPosition.setBegin(negationBegin + sentence.getBegin());
	 				                        //negPosition.setEnd( negationBegin + sentence.getBegin() +  preNegPhrase.length() ); 
	 				                        negPosition.setBegin(negationBegin);
	 				                        negPosition.setEnd( negationBegin + preNegPhrase.length() + 1); 
	 				                       
	 				                        negPosition.setNegationType(PRE_NEGATION);
	 				                        
	 				                        negPosition.setNegationPhrase(preNegPhrase);
	 				                        try {
		 				                        if(z < (numberOfNegations - 1)  ){	 				                        	
		 				                        	int next = negationList.get(z + 1);
		 				                        	//next = next + sentence.getBegin();
		 				                        	next = next +  1;	 				                        	
		 				                        	negPosition.setNextPosition(next);
		 				                            negPosition.setIsContinuos(true);		 				                            
		 				                            		 				                           
		 				                        }//if
	 				                        }catch(Exception e) {
		 				                    	 
		 				                    }
	 				                        
	 				                        
	 				                        //Se usa para ver si es el último término negado dentro de la secuencia.
	 				                        if (z == (numberOfNegations - 1)) {
	 				                        	negPosition.setIsContinuos(false);
	 				                        	
	 				                        	if(preNegPhrase.contentEquals("sin")) {
	 				                        		 negPosition.setEnd(negationBegin + preNegPhrase.length());
	 				                        	}
	 				                        }
	 				                        
	 				                        negPosition.setContiguosCuesNumber(negationList.size());	 				                       
	 				                        myHashMap.put(negationBegin, negPosition);
	 				                        
	 				                        //temporal se puede deshabilitar
	 					                    //configureMetric(sentence, numberOfNegations, 0, true);
	 				                        
	 			                        }//if
	 			                
	 	                        }//for
	 		                
	 		                
	 		                counter_prenegation_trigger ++;
	 		                                    
	 	            }//end if numberOfNegations > 1
	             
	                //se limita a que haya hasta n tipos de palabras pre_negadas en una sentencia.
	 	            
	 		       if(counter_prenegation_trigger >= limit_heuristic){
	 	                i = preNegationPhrases.size();
	 	           }
	 	       
	 	            
	         }// end for
	         
	      return myHashMap;
	         
	   }//end method
	 
	 
	 /**
	 * 
	 * @param sentence
	 * @return
	 */
	  
	  public HashMap<Integer,CuePosition>  getPostNegation(CoreMap sentence){
		    //System.out.println("*** Counting POS Negations");
	        int counter_postnegation_trigger=0;  	       
	        int limit_heuristic =0;
	        
	        /*Número máximo de diferentes frases de negación  usadas en una sentencia.
	         * Ejemplo: Fiebre: NO, dolor: No nauseas: no . [Solo una frase  => :no]
	         *          
	         */
		    
		    if(sentence.get(TokensAnnotation.class).size() < SHORT_SENTENCE_HEURISTIC) {
		    	limit_heuristic = 2;
		    }
		    else {
		    	limit_heuristic = 5;
		    }
		    
		    
	        HashMap<Integer,CuePosition> myHashMap=new HashMap<Integer,CuePosition>(); 
	        
	        //Buscando frases post-negadas
	        for(int i=0; i< postNegationPhrases.size() ; i++){            
		            String postNegPhrase = postNegationPhrases.get(i);            
		            
		            ArrayList<Integer> negationList =  findPostNegationsRegex(sentence, postNegPhrase);
		            int numberOfNegations =negationList.size();
	            
		            //Hay un solo elemento negado en la sentencia
		            if (numberOfNegations == 1){
		                	int negationEnd = negationList.get(0); 
		                	
		                	//int negationBegin = (sentence.getBegin() +  negationEnd)  -   postNegPhrase.length() ;
		                	int negationBegin = (1 +  negationEnd)  -   postNegPhrase.length() ;
		                	
		                // // se quiere guardar solo un elemento negado en la misma posición de la sentencia
			                if(myHashMap.get(negationEnd) == null){
			                    CuePosition negPosition= new CuePosition();
			                  
			                    negPosition.setBegin(negationBegin);
			                    //negPosition.setEnd(sentence.getBegin() + negationEnd);
			                    negPosition.setEnd( 1  + negationEnd);
			                   
			                    
			                    negPosition.setNegationType(POST_NEGATION);
			                    negPosition.setIsContinuos(false);
			                    negPosition.setNegationPhrase(postNegPhrase);		                    
			                    
			                    myHashMap.put(negationEnd, negPosition);
			                    counter_postnegation_trigger ++;
			                    
			                    //temporal, se puede deshabilitar.
			                    //configureMetric(sentence,  0, numberOfNegations,  false);
			                    
			                    
			                }//if
		             }//if
	            
		            //Hay más de un término negado de este tipo Ej: No hta, no FGR, no ALK
		            if(numberOfNegations > 1){
			            	//ArrayList <Integer> continuosList = buildContinuosList(negationList);
			            	Collections.sort(negationList);
			            	//Collections.sort(continuosList);
			            	
			            	
			                for(int z=0; z< negationList.size(); z++) {
			                	
			                	
			                    int  negationEnd =  negationList.get(z); 	      
			                    //int negationBegin = sentence.getBegin() +  (negationEnd -  postNegPhrase.length()) ;
			                    int negationBegin = 1  +  (negationEnd -  postNegPhrase.length()) ;
			                	
			                    
			                    // se quiere guardar solo un elemento negado en la misma posición de la sentencia
			                    if(myHashMap.get(negationEnd) == null){                        
				                        CuePosition negPosition = new CuePosition();
				                       
				                        negPosition.setBegin(negationBegin);
				                        //negPosition.setEnd(sentence.getBegin() + negationEnd);
					                    negPosition.setEnd(1 + negationEnd);
				                        
				                        negPosition.setNegationType(POST_NEGATION);			                        
				                        negPosition.setNegationPhrase(postNegPhrase);
				                        negPosition.setIsContinuos(true);
				                        
				                        //Si es el primer termino de la serie, el anterior seria el begin de la sentencia
				                        if(z == 0)  {
				                        	//int lastPosition = sentence.getBegin();	
				                        	int lastPosition = 1;	
				                        	negPosition.setLastPostion(lastPosition);
				                            
				                        }//if
				                        
				                        //se asigna al anterior término de la serie de términos negados.
				                        if(z > 0) {
				                        	int lastPosition =  negationList.get(z-1); 		                        			                        	
				                        	negPosition.setLastPostion(lastPosition);
				                        }
				                      		
				                        negPosition.setContiguosCuesNumber(negationList.size());				                        
				                        myHashMap.put(negationEnd, negPosition);
				                        
				                        //temporal se puede deshabilitar
					                    //configureMetric(sentence,  0, numberOfNegations,  true);
			                     }//if
			                    
			              
		                   }//for
			                
			           counter_postnegation_trigger ++;
			            
		            }//if
	            
	            
	               
		        //se limita a que haya hasta 4 tipos de palabras post-negadas en una sentencia.
		        if(counter_postnegation_trigger >= limit_heuristic){
	                i = postNegationPhrases.size();
	            }
	          
		        
	        }// end for
		   
	       return myHashMap;
	        
	  }//end method
	  
	  /**
	   * Busca si una frase pre-negada se encuentra en la sentencia usando expresiones regulares
	   * @param sent
	   * @param negationPhrase
	   * @return
	   */
	  
	  public  ArrayList<Integer> findPreNegationsRegex(CoreMap sent, String negationPhrase) {
		 		    
	  			ArrayList<Integer> result = new ArrayList<Integer>();		   
				/*
				 * Algunos términos como no, sin, descartar son ambiguos y pueden 
				 *  indicar   doble negación lo que se puede conseguir en no-negación
				 * Ej: no se puede descartar ....
				 */
	  			if (negationPhrase.equals("no") || negationPhrase.equals("sin") || negationPhrase.equals("descartar") ||  negationPhrase.equals("descartarse") || negationPhrase.equals("se descarta") || negationPhrase.equals("negatividad") ||  negationPhrase.equals("puede")){
					  int doubleNegPosition = findDoublePreNegation(sent, negationPhrase);
					  //System.out.println("\n Descartando double negation postion: " + doubleNegPosition );
					  
					  //si hay ambiguedad se retorna vacio y la sentencia se procesará despés como post-negados.
					  if(doubleNegPosition > 0) {
						  return result;				    	        	
				      }				  
					  
	  			}//end if
	  			
	  			    
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
				  
							
				return result;

	  }//end method   
	  
	   /**
	    * 
	    * @param sent
	    */
	  public int containsNoCue(String sent) {
		  String sentenceText = sent.toString();		  			    
		  String pattern = "\\bno\\b";
		  

		  Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
		  Matcher m = p.matcher(sentenceText);
		  int cont=0;		  								
		  while(m.find()) {									
					cont++;
		  }
		  return cont;
		  
	  }
	  
	  /**
	   *  Busca si hay alguna expresión que indica doble-negación y retorna su posicion 
	   * @param sent
	   * @param negationPhrase
	   * @return
	   */
	  public int findDoublePreNegation(CoreMap sent, String negationPhrase) {
		  
		  int position = 0;
				  
			        //String sentenceText = sent.getCoveredText();
			        String sentenceText = sent.toString();
			  	
			  		for (int i=0; i<doublePreNegationPhrases.size(); i++) {
					  
						   String contraNegation = doublePreNegationPhrases.get(i);						  
						   String pattern = "\\b" + contraNegation + "\\b";						  
						   Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
						   Matcher m = p.matcher(sentenceText);
						   
						   while(m.find()) {
								
							    int temp = m.start();
								//position = temp + sent.getBegin();
								position = temp + 1;								
								//System.out.println("\n Términos contra-negado encontrado en pos: " +  position + "\n ");
								
								i=doublePreNegationPhrases.size();						
								
								//temporal							
								//addDoubleNegtionMetrics(sent,  m.group() +"[" + position + "-" + (position+ m.end()) + "]");
					                              
						   }//end while
				   }//end for
			  		
			  	  /*el siguiente caso es especial del no
			  	   * Para expresiones como Dolor: no.
			  	   * En este caso queremos que se comporte como post-negado-
			  	   */
			  				  	  
			  		sentenceText = sentenceText.toLowerCase();
			  		
			  	    if(sentenceText.contains(":") && sentenceText.contains("no")) {
			  				  	
			  				    int x = sentenceText.indexOf(":");
			  				    int right = (sentenceText.length() - x);
			  				    int left = (x - 0);
			  				    
			  				  //System.out.println(" Encontrado :No => " + left + " " +  right);
			  				    
			  				  	if( left < right) {			  				  		
				  				  	    //System.out.println("\n Double negation ambiguedad [:no] ==>  se comportara como pre-negado\n ");
				  				    	position=0;
				  				 }
			  				    
			  				  	 else {
			  				  		  position = 1;
			  				  	  }
			  		 
			  	   }
			  		  
			
		  return position;
	  }//end method.
	  
	  /**
	   * Busca si una frase post-negada está en la sentencia
	   * @param sentence
	   * @param negationPhrase
	   * @return
	   */
	  public  ArrayList<Integer> findPostNegationsRegex(CoreMap sentence, String negationPhrase) {
		  //System.out.println("Buscando palabras negadas usando Regex: "  + negationPhrase);
		    
		    ArrayList<Integer> result = new ArrayList<Integer>();   
	  	
		    /*se usa porque estas expresiones de negación también pueden aparacer en pre-negados,
			 * entonces para evitar que las anotaciones se solapen, se verifique que no lo esten.
			 */
			if (negationPhrase.equals("negativo") || negationPhrase.equals("negativa") || negationPhrase.equals("negativos") || negationPhrase.equals("negativas")){
				
				int doubleNegPosition = findDoublePostNegation(sentence, negationPhrase);
				
				if (doubleNegPosition > 0) {
					return result;
				}
				
			}
												                
			//System.out.println(pattern);
			//String sentenceText = sentence.getCoveredText();	
			String sentenceText = sentence.toString();
			String pattern = "\\b" + negationPhrase + "\\b";
			Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(sentenceText);
						
			while(m.find()) {		
					result.add(m.end()); 
				    /*System.out.println("Negated Phrase End (dentro de frase:) => " + m.end() 
				                                 + "\t => " +  negationPhrase
				                                 + "\t  => \t Length: (" +  negationPhrase.length() 
				                                 + ")" + "\t sentence.begin => "  +   "\n ");*/
				                                
			}//end while
				                
	        return result;
	   
	  }//end method
	       
	  
	  

	   /**
	   * Se usa para buscar expresiones que indican doble negación en Post negados.
	  */
	    
	  public int findDoublePostNegation(CoreMap sent, String negationPhrase) {
		  
		  int position = 0;
				  
		  //String sentenceText = sent.getCoveredText();
		  String sentenceText = sent.toString();
			  	
		  for (int i=0; i < doublePostNegationPhrases.size(); i++) {
					  
				String contraNegation = doublePostNegationPhrases.get(i);
						  
				String pattern = "\\b" + contraNegation + "\\b";
						  
				Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
				Matcher m = p.matcher(sentenceText);
						   
				while(m.find()) {
								
					int temp = m.start();
					//position = temp + sent.getBegin();		
					position = temp + 1;		
					i = doublePostNegationPhrases.size();	                              
					                              
				}//end while
				
		    }//end for
			  		
			
		  return position;
		  
	  }//end method.
	  
	  ///////// ======================================================================= ///////////
	  
	  /*
	   * Methods For morphological Negation.
	   * 
	   */
	  
	  
	  public HashMap<Integer,CuePosition>  getMorphologicalNegation(CoreMap sentence){
		    //System.out.println("*** Counting POS Negations");
	        int counter_postnegation_trigger=0;  	       
	        int limit_heuristic =0;
	        
	        /*Número máximo de diferentes frases de negación  usadas en una sentencia.
	         * Ejemplo: Fiebre: NO, dolor: No nauseas: no . [Solo una frase  => :no]
	         *          
	         */
		    
		    if(sentence.get(TokensAnnotation.class).size() < SHORT_SENTENCE_HEURISTIC) {
		    	limit_heuristic = 2;
		    }
		    else {
		    	limit_heuristic = 5;
		    }
		    
		    
	        HashMap<Integer,CuePosition> myHashMap=new HashMap<Integer,CuePosition>(); 
	        
	        //Buscando frases post-negadas
	        for(int i=0; i< morphologicalPhrases.size() ; i++){            
		            String negPhrase = morphologicalPhrases.get(i);            
		            
		            ArrayList<Integer> negationList =  findMorphologicalNegationRegex(sentence, negPhrase);
		            int numberOfNegations =negationList.size();
	            
		            //Hay un solo elemento negado en la sentencia
		            if (numberOfNegations == 1){
		                	int negationEnd = negationList.get(0); 
		                	
		                	//int negationBegin = (sentence.getBegin() +  negationEnd)  -   postNegPhrase.length() ;
		                	int negationBegin = (1 +  negationEnd)  -   negPhrase.length() ;
		                	
		                // // se quiere guardar solo un elemento negado en la misma posición de la sentencia
			                if(myHashMap.get(negationEnd) == null){
			                    CuePosition negPosition= new CuePosition();
			                  
			                    negPosition.setBegin(negationBegin);
			                    //negPosition.setEnd(sentence.getBegin() + negationEnd);
			                    negPosition.setEnd( 1  + negationEnd);
			                   
			                    
			                    negPosition.setNegationType(MORPHOLOGICAL_NEGATION);
			                    negPosition.setIsContinuos(false);
			                    negPosition.setNegationPhrase(negPhrase);
			                    negPosition.setMorphological(true);
			                    
			                    myHashMap.put(negationEnd, negPosition);
			                    counter_postnegation_trigger ++;
			                    
			                    //temporal, se puede deshabilitar.
			                    //configureMetric(sentence,  0, numberOfNegations,  false);
			                    
			                    
			                }//if
		             }//if
	            
		            //Hay más de un término negado de este tipo Ej: No hta, no FGR, no ALK
		            if(numberOfNegations > 1){
			            	//ArrayList <Integer> continuosList = buildContinuosList(negationList);
			            	Collections.sort(negationList);
			            	//Collections.sort(continuosList);
			            	
			            	
			                for(int z=0; z< negationList.size(); z++) {
			                	
			                	
			                    int  negationEnd =  negationList.get(z); 	      
			                    //int negationBegin = sentence.getBegin() +  (negationEnd -  postNegPhrase.length()) ;
			                    int negationBegin = 1  +  (negationEnd -  negPhrase.length()) ;
			                	
			                    
			                    // se quiere guardar solo un elemento negado en la misma posición de la sentencia
			                    if(myHashMap.get(negationEnd) == null){                        
				                        CuePosition negPosition = new CuePosition();
				                       
				                        negPosition.setBegin(negationBegin);
				                        //negPosition.setEnd(sentence.getBegin() + negationEnd);
					                    negPosition.setEnd(1 + negationEnd);
				                        
				                        negPosition.setNegationType(MORPHOLOGICAL_NEGATION);			                        
				                        negPosition.setNegationPhrase(negPhrase);
				                        negPosition.setIsContinuos(true);
				                        negPosition.setMorphological(true);
				                        
				                        //Si es el primer termino de la serie, el anterior seria el begin de la sentencia
				                        if(z == 0)  {
				                        	//int lastPosition = sentence.getBegin();	
				                        	int lastPosition = 1;	
				                        	negPosition.setLastPostion(lastPosition);
				                            
				                        }//if
				                        
				                        //se asigna al anterior término de la serie de términos negados.
				                        if(z > 0) {
				                        	int lastPosition =  negationList.get(z-1); 		                        			                        	
				                        	negPosition.setLastPostion(lastPosition);
				                        }
				                      		
				                        negPosition.setContiguosCuesNumber(negationList.size());				                        
				                        myHashMap.put(negationEnd, negPosition);
				                        
				                        //temporal se puede deshabilitar
					                    //configureMetric(sentence,  0, numberOfNegations,  true);
			                     }//if
			                    
			              
		                   }//for
			                
			           counter_postnegation_trigger ++;
			            
		            }//if
	            
	            
	               
		        //se limita a que haya hasta 4 tipos de palabras post-negadas en una sentencia.
		        if(counter_postnegation_trigger >= limit_heuristic){
	                i = morphologicalPhrases.size();
	            }
	          
		        
	        }// end for
		   
	       return myHashMap;
	        
	  }//end
	  
	  public  ArrayList<Integer> findMorphologicalNegationRegex(CoreMap sentence, String negationPhrase) {
		  //System.out.println("Buscando palabras negadas usando Regex: "  + negationPhrase);
		    
		    ArrayList<Integer> result = new ArrayList<Integer>();   
	  										                
			//System.out.println(pattern);
			//String sentenceText = sentence.getCoveredText();	
			String sentenceText = sentence.toString();
			String pattern = "\\b" + negationPhrase + "\\b";
			Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(sentenceText);
						
			while(m.find()) {		
					result.add(m.end()); 
				    /*System.out.println("Negated Phrase End (dentro de frase:) => " + m.end() 
				                                 + "\t => " +  negationPhrase
				                                 + "\t  => \t Length: (" +  negationPhrase.length() 
				                                 + ")" + "\t sentence.begin => "  +   "\n ");*/
				                                
			}//end while				                
	        return result;
	   
	  }//end method
	        

	  ///////// ======================================================================= ///////////
	  
	  /**
	   * Lists setup.
	   */
	  public void setUpNegationLists() {		   
		   preNegationPhrases = new ArrayList<String>();
		   preUncertaintyPhrases = new ArrayList<String>();
	       postNegationPhrases = new ArrayList<String>();	  
	       postUncertaintyPhrases = new ArrayList<String>();
	       doublePreNegationPhrases = new ArrayList<String>();
	       doublePostNegationPhrases = new ArrayList<String>();  
	       morphologicalPhrases  = new ArrayList<String>();  
	       
	 }//end method
	 
	 
	 
	 /** 
	     * @param Configure Lists
	 */
	    public void configureNegationLists(ArrayList<String> list){
	        
	        
	        //System.out.print("\n \n \n\n \n  ********************************************************\n \n \n \n");
	        
	        for (int i=0; i<list.size();i++){
	            String   line =  list.get(i);
	            String line_words[]= line.split("\t");
	           
	            //System.out.println ("Line: " + i + " " + line);
	            
	            if(line_words[1].equals("[pre-negation]")){
	                preNegationPhrases.add(line_words[0].toLowerCase());            
	            }
	            
	            if(line_words[1].equals("[post-negation]")){
	                postNegationPhrases.add(line_words[0].toLowerCase());            
	            }
	           
	            
	            if(line_words[1].equals("[uncertainty]")){
	            	preUncertaintyPhrases.add(line_words[0].toLowerCase());            
	            }
	            
	            if(line_words[1].equals("[post-uncertainty]")){
	            	postUncertaintyPhrases.add(line_words[0].toLowerCase());            
	            }
	                                       
	            if(line_words[1].equals("[double-negation-pre]")){
	                doublePreNegationPhrases.add(line_words[0].toLowerCase());            
	            }
	            
	            if(line_words[1].equals("[double-negation-post]")){
	                doublePostNegationPhrases.add(line_words[0].toLowerCase());            
	            }
	            
	            if(line_words[1].equals("[morphological-negation]")){
	                morphologicalPhrases.add(line_words[0].toLowerCase());            
	            }
	           
	        }//end for
	     
	        
	 }//End Method
	 
	 
	    public void printNegatedPhrases(){
	    	System.out.println("\n \n************************************************* \n");
	        System.out.println("\n Pre negation \n");
	        for (int i=0; i<preNegationPhrases.size();i++){
	            System.out.println(i + ": " + preNegationPhrases.get(i));
	            
	        }
	        
	        System.out.println("========================================================= \n \n");
	        
	        System.out.println("\n Post negation \n");
	        for (int i=0; i<postNegationPhrases.size();i++){
	            System.out.println(i + ": " + postNegationPhrases.get(i));
	            
	        }
	        
	     
	        
	        System.out.println("========================================================= \n\n ");
	        
	        
	     
	        
	        System.out.println("========================================================= \n\n\n ");
	        
	        System.out.println("\n Double Negation Pre \n");
	        for (int i=0; i<doublePreNegationPhrases.size();i++){
	            System.out.println(i + ": " + doublePreNegationPhrases.get(i));
	            
	        }
	        
	        System.out.println("\n Double Negation Post\n");
	        
	        for (int i=0; i<doublePostNegationPhrases.size();i++){
	            System.out.println(i + ": " + doublePostNegationPhrases.get(i));
	            
	        }
	        
	        System.out.println("\n Adversative Words\n");
	        
	       
	        
	        System.out.println("========================================================= \n\n ");
	    }//end method

	
	
	

}
