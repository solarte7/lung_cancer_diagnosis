package es.upm.ctb.midas.uncertainty;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.cuni.mff.ufal.udpipe.Model;
import cz.cuni.mff.ufal.udpipe.Pipeline;
import cz.cuni.mff.ufal.udpipe.ProcessingError;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.util.CoreMap;

public class ScopeResolution {
	ArrayList<String> terminationTerms;	
	ArrayList<String> uncertaintyTerms;	
	ArrayList<String> adversativeWordsSpanish;
	ArrayList<String> doublePostNegationPhrases;
	ArrayList<String> morphologicalPhrases; 
	HashMap<String, String> categoriesMap;
	
	Model udPipeModel;
	TreeManager treeManager;
	
	
	 int PRE_NEGATION ;
	 int POST_NEGATION;	 
	 int SHORT_SENTENCE_HEURISTIC; 
	 int LARGE_SENTENCE_HEURISTIC; 	
	 static int SEMANTIC_TOKEN_HEURISCTIC = 9;
	 static int TOKENS_AFTER_NEGATION_HEURISTIC = 5;

	
	
	
	public ScopeResolution(ArrayList<String> dictionary1, int pre, int post, int shortSent, int largeSent) {
		PRE_NEGATION = pre;
		POST_NEGATION = post;
		SHORT_SENTENCE_HEURISTIC = shortSent;
		LARGE_SENTENCE_HEURISTIC = largeSent;
		categoriesMap = new HashMap<String, String>();
		
		
		
		setUpNegationLists();
		configureNegationLists(dictionary1);
		
		terminationTerms.addAll(uncertaintyTerms);	    
		
	    ArrayList<String> tmp1= new ArrayList<String>();
		tmp1.addAll(terminationTerms);

		terminationTerms.clear();	
		terminationTerms.clear();		
		
		MyStringSorter sorter = new MyStringSorter();
		terminationTerms = sorter.sortRules(tmp1);
		
		loadUDPipe();	
		treeManager = new TreeManager();
	}
	
	
	
	 /** Calcula el scope de la negación
	   * @param negationsMap
	   * @param sentence
	   * @param negationType
	   * @return
	   */
	  
	  public ArrayList <Scope> getNegationScope(HashMap<Integer,CuePosition> negationsMap, CoreMap sentence,  int negationType, String filePath){
	      ArrayList <Scope> scopeList = new ArrayList<Scope>();	      
	      
	      //System.out.println("\n \n Iniciando cálculo del Scope Mode: ("+ negationType  +   ") \n");	      
	       for (HashMap.Entry<Integer, CuePosition> entry :negationsMap.entrySet()) {
	          
	           CuePosition negatedPosition=  entry.getValue();
	           
	           if(negationType == PRE_NEGATION){  	               
	               Scope scope = calculatePreNegationScope(negatedPosition, sentence, filePath);               
	               scopeList.add(scope);             
	           }
	            
	           if(negationType == POST_NEGATION){	             
	            	 Scope scope = calculatePostNegationScope(negatedPosition, sentence,  filePath);                 
	                 scopeList.add(scope);	                	                 
	           }
	         
	           
	       }//end for.
	     
	      return scopeList;
	      
	  }//end method
	  
	  
	  /* =====================================================================================================
	   *  ============================  Methods to calculate Pre Negation Scope =============================
	   * =====================================================================================================
	   */
	  
	  	  
	  /**
	   * Calcula el scope de un término pre-negado
	   * @param negatedPosition
	   * @param sent
	   * @return
	   */
	  public Scope calculatePreNegationScope(CuePosition negatedPosition, CoreMap sent, String filePath){
		      	  	
		  		   int beginScope = negatedPosition.getEnd();
		  		   String cue = negatedPosition.getNegationPhrase();
		  		   
		  		   //System.out.println("\n Negation cue:" + negatedPosition.getNegationPhrase() + "\t Begin Scope: " + beginScope  + " \tContiguos: " + negatedPosition.getIsContinuos());
		  		   
		  		   int endScope=sent.toString().length() - 1;//Vamos a partir como endScope, el end de la sentencia.	  		   
		  		   String method=""; //solo se usa para tomar métricas, luego se puede borrar
		  		   	  		   
		  		   //Si el término negado es contínuo Ej: No Dolor, No fiebre, No tos.              
	               if(negatedPosition.getIsContinuos() == true){            	  
	            	   endScope =getPreContiguosScope(negatedPosition, sent, filePath);	            	   
	            	   method = "continuos_neg"; 
	                  
	               }//end if is continuos.	               
	               
	               /*Si el término negado NO  es contínuo se procede así:
	                * 1) Primero,  Si la sentencia es corta, se aplica heurística, Sino se busca alguna de las sig. condiciones
	                * 2)  Si la sentencia está entre el primer y tercer cuartil (Termination term)
	                * 3)  Si la sentencia es mayo al tercer cuartil (Sintacti Tree Analisis)
	                * 3)  
	                */
	               else { 
	            	      int tokensNumber = sent.get(TokensAnnotation.class).size();
	            	   	   //Primero se evalua si la sentencia es corta => Se aplica heurística
	            	      if (tokensNumber < SHORT_SENTENCE_HEURISTIC) {	                	   
		                	   	endScope = sent.toString().length();	                	   	
		                	   	//System.out.println("\n Scope Method-> Sentencia corta, aplicando heúristica \n");
		                	   	method = "short_sent_heuristic";
		                  }
	            	      else {
	            	    	  if ( (tokensNumber  >= SHORT_SENTENCE_HEURISTIC) && (tokensNumber  < LARGE_SENTENCE_HEURISTIC) ) {
	            	    		  endScope = getScopeMedianSentence(sent,  negatedPosition);
	            	    			method = "medium_sentence";
	            	    	  }
	            	    	  else {
	            	    		  if ((tokensNumber  >= LARGE_SENTENCE_HEURISTIC)) {
	            	    			  endScope = getScopeLargeSentence(sent, negatedPosition);
	            	    			  method = "large_sentence";
	            	    		  }
	            	    	  }//else
	            	      }//else
	               }//else
		                   
		       //Para garantizar que el scope no salga de los limites de la sentencia.           
	           if(endScope > sent.toString().length()) {
	        	   endScope =sent.toString().length() -1;
	           }
	           
	           if(endScope < beginScope) {
	        	   endScope =sent.toString().length() -1;
	           }
	           
	           //System.out.println("---> scope " + beginScope + " " + endScope);
	           Scope scope = new Scope();
	           
	           scope.setBegin(beginScope);          
	           scope.setEnd(endScope);
	           scope.setPhrase(negatedPosition.getNegationPhrase());
	           scope.setSentence(sent.toString());
	           scope.setFilePath(filePath);
	           try {
	           scope.setType(categoriesMap.get(cue));
	           }
	           catch(Exception e){
	        	   scope.setType("pre");
	           }
	           
	           	           
	        return scope;
	  
	  } //end method
	  
	  /**
	   * Get contiguos scope (pre-cue)
	   *  //En este caso el end Scope, es el siguiente témino negado.
	   */
	  public int getPreContiguosScope(CuePosition negatedPosition, CoreMap sent, String filePath) {
		     
		  	int tokensNumber = sent.get(TokensAnnotation.class).size();
			int beginScope = negatedPosition.getEnd();
				 
			    
			int nextCuePosition = negatedPosition.getNextPosition();
			int endScope = nextCuePosition;  
			String sentenceText=sent.toString();
			
			int tokensBetweenCueAndNext = validateTokensNumberScope(sent, negatedPosition, nextCuePosition);
			    
		    if(tokensBetweenCueAndNext < SHORT_SENTENCE_HEURISTIC) {    	
		    	   endScope= nextCuePosition -1;
		        	
		    	   ArrayList<Integer> positions =  checkSpecialCaseTerminationTerm(sentenceText, beginScope);
		        	if (positions.size() > 0) {
		        		int x= positions.get(0);		        	
			        	if (x > 0 && x < nextCuePosition) {
			        		endScope= x - 1;
			        	}
			        	
			        	
		        	}//if
		        	//System.out.println("\n Scope Method->  ( nextCuePosition - beginScope)  " +  endScope + " " + nextCuePosition);
		     }//if
		     else {  
		        	int commas= countCommas(sent.toString());
		        	if ((commas > 2) && (negatedPosition.getContiguosCuesNumber()>= 2) && (tokensNumber < LARGE_SENTENCE_HEURISTIC)) {		        		
		        		ArrayList<Integer> positions =  checkSpecialCaseTerminationTerm(sentenceText, beginScope);
		        		 endScope= nextCuePosition -1;
		        		
		        		 if (positions.size() > 0) {
			        		int x= positions.get(0);		        	
				        	if (x > 0 && x < nextCuePosition) {
				        		endScope= x - 1;
				        	}
				            
		        		}//end if
			        					        	
				        if ((tokensBetweenCueAndNext) > (SHORT_SENTENCE_HEURISTIC*2)) {
				        		endScope = countTokensAfterNegation (sent, negatedPosition);
				        }
				        
				        //System.out.println("Scope Method-> Término > 2 commas");
		        		
		        	}//end if
		        	else {
		        		
		         	   	int specialCharPos = findContiguosSpecialCharactersAfterNegation(sent, negatedPosition);		         	   	
		         	    if(specialCharPos > 0 ) {		         	    	    
			         	   	endScope =specialCharPos;            	   			
			         	   	
			         	   	//System.out.println("Scope Method-> Término negado contínuo sin comas " + (specialCharPos - beginScope));		         	    	
		         	    }
		         	   	else {       
		         	   		int termination_term = findNextTerminationTerm(sent,  negatedPosition);
		         	   		if( (termination_term > beginScope)) {
					 		  endScope = termination_term;					 		                    
					 		  //System.out.println("Scope Method-> contiguos, termination_term");						 		                       
		         	   		}
		         	   		else {
		         	   			endScope = countTokensAfterNegation (sent, negatedPosition);
		         	   			//System.out.println("Scope Method-> contiguos, Default");	
		         	   		}		         	   		
		         	   	 }//else		         	 
		        	 }//else
		       }//else
		   
		        
		  return endScope;	   
	  }//end
	  
	  /**
	   * Calcula  el scope en una sentencia mediana
	   */
	  public int getScopeMedianSentence(CoreMap sent, CuePosition negatedPosition1) {
		  int beginScope1 = negatedPosition1.getEnd();
		  int endScope1=sent.toString().length();
		  String method="";
		  //System.out.println("\n BEGIN Scope Median Sentence"); 
		  int characterStop= findFirstCharacterPositonToStop(sent, negatedPosition1);
	        if (characterStop > beginScope1 ) {         	   			
          	  endScope1 = characterStop -1;				                	 		                	  
          	  //System.out.println("\n Scope Method-> Se encontró un caracter de parada\n");
          	  method = "character_stop";
          }			                	   		 
          else {
          	
          	 int termination_term = findNextTerminationTerm(sent,  negatedPosition1);
		 		 if(termination_term > beginScope1 ) {
			 		  endScope1 = termination_term -1;					 		                    
			 		  //System.out.println("\n Scope Method-> Se encontró Termination-term \n");
			 		  method = "Termination term";							 		                       
		 		 }
		 		 else {    
		 			
					  int nextTokenPosition= getNextTokenPartOfSpeech (sent, negatedPosition1);									    
					  if (nextTokenPosition > beginScope1 ) {
						  endScope1 = nextTokenPosition-1;
						  //System.out.println("\n Scope Method->  Buscando POS Taggin \n");
						  method = "POS tagger";
				      }
					  else { // Si el modelo Udpipe no es nulo.											  
							  if((udPipeModel==null) == false) {
								  try{
									  String input="horizontal";
									  String output="matxin";														   
									  Pipeline pipeline = new Pipeline(udPipeModel, input, Pipeline.getDEFAULT(), Pipeline.getDEFAULT(), output);													  
									  String xml = pipeline.process(sent.toString());													  
									  endScope1 = treeManager.getScope(xml, sent.toString(), negatedPosition1.getNegationPhrase());
									  int tokensNumber =  validateTokensNumberScope(sent, negatedPosition1, endScope1);
									 
									 if (tokensNumber > (SHORT_SENTENCE_HEURISTIC*2)) {
										 endScope1 = countTokensAfterNegation (sent, negatedPosition1);
									 }
									  
									  //System.out.println("\n Scope Method-> Parsing Tree  tokenScope: " + tokensNumber + " " + endScope1);
									  method = "parsing_tree";															 												    
								  }
								  catch(Exception e){
									  endScope1 = countTokensAfterNegation (sent, negatedPosition1);
									  method = "default";
								  }
							   }//if
							  else {
								  endScope1 = countTokensAfterNegation (sent, negatedPosition1);
								  method = "default";
							  }
				 
					   }//else
			         
		         }//else				              	   
          }//else	      
	       return endScope1;
		  
	  }//end method.
	  
	  /**
	   * Calcula el scope en una sentencia larga.
	   */
	  
	  public int  getScopeLargeSentence( CoreMap sent, CuePosition negatedPosition1) {
		
			  int beginScope1 = negatedPosition1.getEnd();
			  int endScope1=sent.toString().length();
			  String method="";
			  
			// Si el modelo Udpipe no es nulo.											  
			  if((udPipeModel==null) == false) {
				  try{
					  String input="horizontal";
					  String output="matxin";														   
					  Pipeline pipeline = new Pipeline(udPipeModel, input, Pipeline.getDEFAULT(), Pipeline.getDEFAULT(), output);													  
					  String xml = pipeline.process(sent.toString());													  
					  endScope1 = treeManager.getScope(xml, sent.toString(), negatedPosition1.getNegationPhrase());
					  //System.out.println("\n ### Scope Method-> Parsing Tree \n" + (endScope1-beginScope1));
					  method = "parsing_tree";
					 
					  int tokensInTheScope= validateTokensNumberScope(sent, negatedPosition1, endScope1);
					  
					  //El número de tokens negados es muy grande, es mayor al promedio de los corpus de prueba.
					  if(tokensInTheScope >  (SHORT_SENTENCE_HEURISTIC*2)  || tokensInTheScope < 1) {
						  endScope1 = findNextTerminationTerm(sent,  negatedPosition1);
						  if(endScope1 <= beginScope1) {
							  endScope1 = countTokensAfterNegation (sent, negatedPosition1);							  
						  }
					  }//end if
				  }
				  catch(Exception e){
					  endScope1 = countTokensAfterNegation (sent, negatedPosition1);
					  method = "default";
				  }
			   }//if
			  else {
				  endScope1 = countTokensAfterNegation (sent, negatedPosition1);
				  method = "default";
			  }
			  
		  return endScope1;
	  }
	 	   
	  /**
	   * Busca una conjuncion de parada. 
	   * @param sentenceText
	   * @param conjunction
	   * @param negationBegin
	   * @return La posicion de la conjuncion
	   */
	  public  int findNextTerminationTerm(CoreMap sentence, CuePosition negationPos) {
		  int terminationPosition = -1;//Posición donde se encuentra el termino de terminación
		  ArrayList <Integer> termPositions = new ArrayList<Integer> ();
		 
		  
		  int negationBegin = negationPos.getEnd(); //Modificado sep 30 2020      
	      String sentenceText = sentence.toString();
	     // int sentenceBegin = sentence.getBegin();
		        
	      
	      for(int i=0; i< terminationTerms.size(); i++) {	    	  
	    	  	String terminationWord = terminationTerms.get(i);	     
				String pattern = "\\b" + terminationWord + "\\b";	                
				Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
				Matcher m = p.matcher(sentenceText);
				                   		
	           int counter=0;	               
		        //Guardo la primera conjuncion que aparece.
		        while(m.find()) {                           
		        		int temp =	m.start();	                      
		                //La conjunción debe estar despues  del begin del termino negado
		                if((temp  >  negationBegin)  ){		                	
		                    	   terminationPosition =  temp; 
		                           counter ++; 		           
		                           termPositions.add(terminationPosition);
		                           if(termPositions.size() > 4) {
		                        	   i= terminationTerms.size();
		                           }
		                          
		                           //System.out.println("Termination term -> " + terminationWord + " " +  terminationPosition);
		                 }
		                 
		               
		                     
				}//while  
	      }//for
	      
	      //Se evaluan casos particulares
	      ArrayList <Integer> specialCases = checkSpecialCaseTerminationTerm(sentenceText, negationBegin);
	      if (specialCases.size() > 0) {
	    	  termPositions.addAll(specialCases);
	      }
	     	      
	      ///Rules.

		 
	       if (termPositions.size() ==1) {
	    	   terminationPosition = termPositions.get(0);
	    	   
	       }
	       else {
	    	   if ((termPositions.size() >1)) {
	    		   terminationPosition = getMinTerminationPosition( termPositions, negationBegin, sentenceText.length());
	    	   }
	      }
	       
	      int tokenslimit =  validateTokensNumberScope(sentence, negationPos, terminationPosition);
	       //Esto es para asegurar que el termination term  y negation cue no esten muy lejos
	      if (tokenslimit > (SHORT_SENTENCE_HEURISTIC*2)) {
	    	   terminationPosition = -1;
	      }
	      
	       return terminationPosition;
			
	  }//end method
	  
	  /***
	   * Get a special case of termination term
	   */
	 
	  public ArrayList <Integer>  checkSpecialCaseTerminationTerm(String sentenceText1, int negationBegin1){
		  
		  ArrayList <Integer> termPositions = new ArrayList<Integer>();
		  /// este es un caso particular de los términos de terminación..
	      try {
	    	 
	    	  int x = sentenceText1.lastIndexOf(", con");
	    	  int y = sentenceText1.lastIndexOf(", que");
			  if (x > 0 && x > negationBegin1 ) {
			    	
			    	termPositions.add(x);
			    	//System.out.println("Termination term -> , con " );
			  }
			     
			  if (y > 0 && y > negationBegin1 ) {				    	
				    	termPositions.add(y);
				    	//System.out.println("Termination term -> , que " );
			  }
	      }
	      catch(Exception e) {
	    	
		  }
	      
	      //System.out.println("Termination terms size -> " + termPositions.size()  + " x= " + x);
	     // System.out.println("Sentence " + sentenceText);	      
	      return termPositions;
	  }//end
	  
	  
	  /**
	   * Se usa para cuando hay varios terminos de terminación.
	   * Retorna el que está mas cerca al cue word.
	   */
	  public int getMinTerminationPosition(ArrayList <Integer> termPositions1, int negationBegin1, int limit) {
		  int index=limit; //distence beween cue and termination term
		  
		  
		 
		  for (Integer currentPosition: termPositions1) {	  
			  
			  if (currentPosition < index ) {				
				 index= currentPosition;
			  }
			  
			  //System.out.println( " Current: " + currentPosition + " index: " + index + "\n");
		  }
		  //System.out.println("Index " + index);
		  return index;
		  
		  
	  }
	  
	  /**
	   * Busca el primer caracter de parada por ejemplo: , ;   :
	   * @param sent
	   * @param negatedPosition
	   */
	  public int  findFirstCharacterPositonToStop(CoreMap sent, CuePosition negatedPosition) {
		  int stopPosition=0;
		  int endNegation= negatedPosition.getEnd();	  
		  int tokensAfterNegation = 0;
		  int commasIndicator=0;
		  
		  //FSArray tokens= sent.getTokens();
		  
		  for (CoreLabel token: sent.get(TokensAnnotation.class)) {
				  
			  if (token.beginPosition() > endNegation ) {
				    tokensAfterNegation ++;
				  	 String textToken = token.get(TextAnnotation.class);
					 //PartOfSpeech posTagger= (PartOfSpeech) token.getPos();
				  	 String posTagger  = token.get(PartOfSpeechAnnotation.class);
					 //System.out.println("*** Conjunction POS: " + posTagger.getTag() + " " + token.getCoveredText());
				  		 
					 if (posTagger.equals("fpa") || posTagger.equals("fpt") ) {							 
						 if(tokensAfterNegation < (SHORT_SENTENCE_HEURISTIC*2)) {
				    		 stopPosition = token.beginPosition();
				    		 break;
				    	 }
											 
					 }
					 else {
							boolean stopCharacter = find_pre_StopCharacter(textToken);							  
						    if(stopCharacter == true) {	
						    	 if(tokensAfterNegation < (SHORT_SENTENCE_HEURISTIC*2)  ) {
						    		 stopPosition = token.beginPosition();
						    		 
						    		 //System.out.println("777 textToken parada " + textToken + " scope " + stopPosition);
						    		 break;
						    	 }
							}	
							 
						 
					 }// end else
					 
					 if(textToken.contains(",")) {
						 commasIndicator = token.beginPosition();
					 }
				 
				 
			  }//end if

		  }//end for
		  
		 		  
		  // No se encontró un caracter de parada y hay pocos (4) tokens despues de la negación
		  //Ej. sin focalidad motora ni sensitiva , lenguaje normal.
		  if ((stopPosition) == 0 && (tokensAfterNegation < (SHORT_SENTENCE_HEURISTIC -1)) ) {
			 if (commasIndicator <= 0){
				 stopPosition = sent.toString().length();
			 }
			 else {
				 stopPosition = commasIndicator;
			 }
			 
		  }
		  		  
		  //System.out.println("Stop Charater: " + stopPosition + " -> " );
		  return stopPosition;
		 
	  }//end method
	  
	 
	  /**
	   * Busca el siguiente token usando POS
	   * @param sentence
	   * @param beginOfNegation
	   */
	  public int getNextTokenPartOfSpeech(CoreMap sentence1, CuePosition negatedPos) {
		  ArrayList<Integer> verbs = new ArrayList<Integer>();
		  ArrayList<Integer> tokenSpecial = new ArrayList<Integer>();
		  int conjunction =0;
		  int counterAllTokens=0;		  
		  int tokenScope = 0;
		  
		 
		  
		  
		  int endOfNegation = negatedPos.getEnd();	
		  int verbIndicative=0;
		
		  //FSArray tokens = sentence.getTokens();
		
		  for (CoreLabel myToken: sentence1.get(TokensAnnotation.class)) {		
			  
			  if (myToken.beginPosition() >= endOfNegation) {			  
				 String  posTagg = myToken.get(PartOfSpeechAnnotation.class);
				 counterAllTokens ++;
				 
				 //System.out.println(myToken.get(TextAnnotation.class) + " POS --> " + posTagg + " -> " + myToken.endPosition());
				 String tokenText = myToken.get(TextAnnotation.class);		 
				 
				 if (posTagg.contains("cc") && (tokenText.length()>3)) {//es una conjuncion				 
					 if (tokenText.equals("y")== false  && tokenText.equals("o")== false) {
						 conjunction= myToken.beginPosition();
						 break;
					 }
				 }
				 
				 if (posTagg.contains("v")){//es un verbo
					 verbIndicative ++;
					 if (counterAllTokens >= 3) {
						 verbs.add(myToken.beginPosition());
					 }
				 }
				 
					 
				 if (tokenText.equals("se") || tokenText.equals("y")) {
					 tokenSpecial.add(myToken.beginPosition());
				 }
												  
			  }
		  }//for
		
		  //rules
		  if(conjunction > 0) {
			  tokenScope = conjunction;
			  //System.out.println("POS 1 --> " + tokenScope);
		  }
		  else {
			  if(tokenSpecial.size() > 0 && verbIndicative > 0) {
				  tokenScope = tokenSpecial.get(0);
				 // System.out.println("POS 2 --> " + tokenScope);
			  }
			  else { 
				  if(verbs.size() > 0) {				  
					  tokenScope=verbs.get(0);
					  //System.out.println("POS 3 --> " + tokenScope);
				  }
				  
			  }
		  }//else
		  
		  // To validate a scope limit.
		  if (counterAllTokens > (SHORT_SENTENCE_HEURISTIC*2)) {
			  tokenScope =0;
		  }
		  else {			   
			  	int tokensAfterScope = countTokensAfterScopePOS(sentence1, tokenScope);
			  	if (tokensAfterScope > 0 && tokensAfterScope < 5 ) {
			  		tokenScope = sentence1.toString().length() -1;
			  	}
		  }
		  
		  return tokenScope;
	  }//end method
	  
	  /**
	   * Check tokens after scope.It is used to validate scope limit.
	   */
	  public int countTokensAfterScopePOS(CoreMap sentence1, int scopePosition) {
		  int num=0;
		  for (CoreLabel myToken: sentence1.get(TokensAnnotation.class)) {		
			  
			  if (myToken.beginPosition() >= scopePosition) {
				  num++;
			  }		  
		  }
		  return num;
	  }
	
	  /**
	   * Busca el siguiente token a negar usndo propiedades POS TAgger
	   * @param sentence
	   * @param beginOfNegation
	   */
	  public int getNextTokenToNegate(CoreMap sentence1, CuePosition negatedPos) {
		 
		  int endNextToken = 0;	  
		 
		  
		  //Se usa para contar el número de tokens que quedan después del término negado
		  int counter=0;
		  int endOfNegation = negatedPos.getEnd();
		  
		  //FSArray tokens = sentence.getTokens();
		  for (CoreLabel myToken: sentence1.get(TokensAnnotation.class)) {
			 	  
			  
			  if (myToken.beginPosition() >= endOfNegation) {
				  
				  counter++;			 
				  //Si el siguiente token tiene una longitud mayor o igual a heuristic_1
				  if(myToken.get(PartOfSpeechAnnotation.class).length()  >= SEMANTIC_TOKEN_HEURISCTIC) {
					  endNextToken = myToken.endPosition();
					  
				  }
			  }
		  }//for
		  
		  //Si el número de tokens despues del término negado es pequeño, el end será el final de la sentencia
		  if (counter <= TOKENS_AFTER_NEGATION_HEURISTIC ) {
			  endNextToken = sentence1.toString().length();
		  }
		  
		  return endNextToken;
	  }//end method
	  
	  
	  
	  /**
	   * Busca la posicion de la primera coma (,) o signo de parada que hay entre una negacion y otra.
	   * Esto solo se usa para negaciones continuas. Ej. no hta, no dolor, no colore....
	   * @param sent
	   * @param negatedPosition
	   * @return
	   */
	  
	  public int  findContiguosSpecialCharactersAfterNegation(CoreMap sent, CuePosition negatedPosition) {
		  
		  int  lastPosition = -1;
		  int endNegation= negatedPosition.getEnd();	  
		  String textToken = "";	
		  int contTokensAfterNeg = 0;
		  	  
		  for (CoreLabel token: sent.get(TokensAnnotation.class)) {
			  	  
			  if (token.beginPosition() > endNegation) {
				  		contTokensAfterNeg ++;
			    	    textToken = token.get(TextAnnotation.class);	    	
						
						//System.out.println("Token: " +  textToken); 	
						
						if (find_pre_StopCharacter(textToken)){						 
							   lastPosition = token.beginPosition();
							   break;							      
						}
						else {			
								     
							  if(textToken.equalsIgnoreCase("ni") || textToken.equalsIgnoreCase("no") ||textToken.equalsIgnoreCase("sin") ) {						  
							    lastPosition = token.beginPosition() ;	
							    break;
							   }
						
						 }//else
			     }//if
		
		    }// end for
		 
		  //System.out.println("First pos " +  lastPosition + " comas: " + countComas + " firts coma " + firstComa + " last token: " + textToken);
		  
		  //Validate the size of scope: Asegura que el caracter de parada y el cue, no esten muy lejos.
		  if (contTokensAfterNeg >= (SHORT_SENTENCE_HEURISTIC*2)) {
			  lastPosition = -1;
		  }
		  		  
		  return lastPosition;
		
	  }//end method
	  
	  
	  /**
	   * 
	   * @param myToken
	   * @return
	   */
	  
	  public boolean find_pre_StopCharacter (String myToken) {
		  
		  boolean contains =false;
		  
		  if (myToken.contains(";")) {
			 contains =true;
		  }
		  else {
							
				if (myToken.contains("(")) {
					contains =true;
				}								
				else {
					if (myToken.contains(")")) {
						contains =true;
					}
					else {
															
							 if (myToken.equals("*")) {
								contains =true;
							 }
							 else {										
								 if (myToken.equals("no")) {
									contains =true;
								 }
								 else {										
									 if (myToken.equals("sin")) {
										contains =true;
									 }	
									 else {										
										 if (myToken.equals("ni")) {
											contains =true;
										 }
										 else {
											 if (myToken.equals(":")) {
													contains =true;
										 }
								   }
										
									 }	 
							
						   }
						}							
					}					
			    }		  
		   }//else
			
	       return contains;     
	      
	  }//end method
	  
	  /**
	   * Busca un apalabra adversativa de parada.
	   * @param text1
	   * @return
	   */
	  public boolean findAdversativePhrase(String text1) {
		  boolean result= false;
		  String textToken= text1.toLowerCase();
		  
		  for(int i=0; i < adversativeWordsSpanish.size(); i++ ) {
			  String advWord = adversativeWordsSpanish.get(i);
			  
			  if(textToken.equals(advWord)) {
				  result = true;
				  i = adversativeWordsSpanish.size();
			  }
		  }
		  
		  return result;
	  }
	  
	  /*
	   *  ====================================================================================================
	   *  ============================  Methods to calculate Post Negation Scope =============================
	   *  ====================================================================================================
	   */
	 
	  
	  /**
	   * Calcula el scope de un término post-negado
	   * @param negatedPosition
	   * @param sentence
	   * @return
	   */
	  	public Scope calculatePostNegationScope( CuePosition negatedPosition, CoreMap sentence, String filePath ){
	  	    /*Como inicio el begin_scope de la negación va a ser el de la sentencia, 
	  	     * pero este valor  se va a ir modificando a medida que va corriendo el Algoritmo.
	  	     */
	  			  		
			  //int beginScope = sentence.getBegin();	
	  		  String cue = negatedPosition.getNegationPhrase();	  		  
			  int beginScope = 0;	
			  int endScope= negatedPosition.getBegin() - 1;
			  String method=""; //temporal solo se usa para tomar métricas
			  
			  //System.out.println("\n \n Negation cue:" + negatedPosition.getNegationPhrase() + "\t End Scope: " +endScope  + " \tContiguos: " + negatedPosition.getIsContinuos());
			 			 	
			  if(negatedPosition.getIsContinuos() == true) {
				     //Es un término contínuo Ej: Glucosa: negativo, bilirrubina: negativo, sangre: negativo.
					  
				      //System.out.println("######### Post-negation ==>  Término Conitinuo == true");
					  method = "continuos_neg";						  			
					  int charPosition = findSpecialCharacterContinuosPostNegation(sentence, negatedPosition);
					  
					  if(charPosition > beginScope) {
						  beginScope = charPosition + 1;
					  }
					  else {
						  	int lastPos = negatedPosition.getLastPostion();
						  	//if (lastPos > sentence.getBegin()) {
						  	if (lastPos > 1) {						  		
						  		beginScope = lastPos;
						  	}
				      }
					  
			  }//end if
			  
		  
			  /*Si el término no es contínuo se  selecciona una de las siguientes opciones en orden de importancia:
			   * 1) Se aplica heurística de sentencia corta.
			   * 2) Se busca una caracter de parada, 
			   * 3) Se busca una conjunción  de parada
			   * 3) 
			   */
		  
			  if(negatedPosition.getIsContinuos() == false){
				    	
				  	if( sentence.get(TokensAnnotation.class).size() <= SHORT_SENTENCE_HEURISTIC ) {
						  
						  //beginScope = sentence.getBegin();
						  beginScope = 0;
						  endScope = endScope + 1;						  
						  //System.out.println("######### Post-negation ==>  Sentencia corta");
						  method =  "short_sent_heuristic";
						  
					}
					else {
						 		// caso especial: el scope está hacia la derecha (Caso especial)
						        boolean pattern1Rigth = checkPostNegationPattern1(sentence, negatedPosition);
							    
						        if (pattern1Rigth == true) { 
									 beginScope = negatedPosition.getEnd();								
									// endScope= sentence.getEnd() ;
									 endScope= sentence.toString().length();
									 
									 //System.out.println("######### Post-negation Pattern==> Pattern left o right");
									 method =  "short_sent_heuristic";
								 }
							     				  
							    else {
							    	    // caso especial: el scope está hacia izquierda pero es una serie separada por comas 
							    	     boolean pattern2 = checkPostNegationPattern2(sentence, negatedPosition);
							    	     if (pattern2 == true) {
							    	    	// beginScope = sentence.getBegin();
							    	    	 beginScope = 1;							    	    	 
							    	    	 //System.out.println("######### Post-negation Pattern==> Pattern left short");
											 method =  "short_sent_heuristic";							    	    	 
							    	     }
							    	     else {   //caso especial l anegación dentro de parentesis: (Prueba x negativo)
									    	  		int checkParentesis = checkForParentesis(sentence, negatedPosition);										   
									    	  		if (checkParentesis > beginScope) {											    	  			
														beginScope = checkParentesis;
														if((endScope -beginScope) < SHORT_SENTENCE_HEURISTIC*2) {
															beginScope = findTerminationCharacterOrTermPost(sentence, negatedPosition)   ;
														}														
														 //System.out.println("\n Se encontró un parentesis de parada");
												         method = "character_stop";
									    	  		}
									    	  		else {
									    	  			//Find for termination term
									    	  			beginScope = findTerminationCharacterOrTermPost(sentence, negatedPosition);
									    	  		}
									    	  		
										  	
									}//else
						}//else
							 	 
				}//else
			  
					
			}//end if getIsContinuos() == false
			  
		  //System.out.println ("#### POOOOS : BEgin " + beginScope + " END " + endScope);
		
		  if (beginScope<0) {
			  beginScope=0; //sentence.begin()
		  }
		  else {
			  if (beginScope > endScope) {
				  beginScope = endScope -1; ;
			  }
		  }
		  
		  Scope scope = new Scope();
	      scope.setBegin(beginScope);
	      scope.setEnd(endScope);
	      scope.setPhrase(negatedPosition.getNegationPhrase());
	      scope.setSentence(sentence.toString());
	      scope.setFilePath(filePath);
	     
	      try {
	           scope.setType(categoriesMap.get(cue));
	           }
	           catch(Exception e){
	        	   scope.setType("pos");
	      }
	      	   	      
	      return scope;
		  
	  }
	  /**
	   * Find for termination term or post.
	   * @param sentence1
	   * @param negatedPosition1
	   * @return
	   */
	  //Find for a termination term o termination character  
	  public int findTerminationCharacterOrTermPost( CoreMap sentence1,CuePosition negatedPosition1) {
			
		  int beginScope = 0;	
		  int endScope= negatedPosition1.getBegin() - 1;
		  String method="";
		
			int charPosition = findLastCharacterPositonToStopPosNegation(sentence1, negatedPosition1);
			int numberTokensScope = validateTokensNumberScopePost(sentence1, negatedPosition1, charPosition);
			//System.out.println("\n Tokens  PosNeg " + numberTokensScope);
			
			//Para que el cue y el aracter deparada no estén pegados
			if ( (charPosition > beginScope) && ((endScope-charPosition) > SHORT_SENTENCE_HEURISTIC)) { 				 
		  	   	beginScope = charPosition;
				    
		  	   	//System.out.println("\n Se encontró un caracter de parada PosNeg");
			    method = "character_stop";
			}
			else {
			 
				 int terminationTerm =  finTerminationTermPosNegation(sentence1, negatedPosition1);
				 int posTagg = getTokenPartOfSpeechPostNegation (sentence1, negatedPosition1);
				
				 if ( (terminationTerm > beginScope) && (posTagg > beginScope)) {
					 //Select Max postition
					 if (terminationTerm >  posTagg  ) {
						 beginScope = terminationTerm;
					 }
					 else {
						 beginScope = posTagg;
					 }
				 }
				 else {
					 if(terminationTerm > beginScope) {																	 
							beginScope = terminationTerm;																 		    
							//System.out.println("\n Se encontró termination-term ");
							method = "conjunction_stop";
					 }//if
					 else {
						 if(posTagg  > beginScope) {		
							 beginScope = posTagg;
							 //System.out.println("\n Se encontró POS-term ");
						 }
						 
					 }
					 
				 }
				 
				 
				 
				 
				 
		
      }//else
			
	  return beginScope;
		  
   }
	  
	  /**
	   * Busca el último caracter de parada para post-negation. 
	   * El end de este caracter debe ser menor al begin de la posicion negada.
	   * @param sent
	   * @param negatedPosition
	   * @return
	   */
	  public int  findLastCharacterPositonToStopPosNegation(CoreMap sent, CuePosition negatedPosition) {
		  int conIndicator=0;
		  int beginScope=0;
		  int beginNegation= negatedPosition.getBegin();
		  
		  //Busca si hay fechas, para evitar caracteres de parada que aparecen en las fechas: "(,/-"
		  boolean isDate = findSpecialChracterInDates(sent, negatedPosition);
		  
		  ArrayList<Integer> positions = new ArrayList<Integer>();	  
		  //FSArray tokens = sent.getTokens();
		
		  for (CoreLabel token: sent.get(TokensAnnotation.class)) {			  
			  	if(token.endPosition() < beginNegation) {				
			  		String textToken = token.get(TextAnnotation.class);				  
			  		boolean stopCharacter= false;				  
				  
			  		if(isDate == false) {					  
					  stopCharacter = findCharacterPost(textToken);//					  
			  		}
			  		else {					  
					  stopCharacter = findCharacterPost2(textToken);					  
			  		}
				  
			  		if(stopCharacter == true) {				  
					  positions.add(token.endPosition());				  
			  		}
				  
			  		if(token.endPosition() >= beginNegation) {
					  break;
			  		}
				  
			  		
			 }//if
				  
				  
		  }//for
		  
			  
		  if (positions.size() > 0) {
			  beginScope = positions.get(positions.size() - 1);  
			  
			//Si el signo de parada y la negación están muy cerca y cuando esta no sea una sentencia muy larga se omite la parada.
			  if ( ((negatedPosition.getBegin() - beginScope ) <= (SEMANTIC_TOKEN_HEURISCTIC + 3)) && conIndicator <=0){				 
		    	  
				  if (positions.size() > 1) {
					  beginScope = positions.get(positions.size() - 2);
				  }
				  else {
					  beginScope =0;
				  }
		    	 
		      }
			  
		  }
		  
		  
		  return beginScope;
	  }//end method
	  
	  /**
	   * 
	   * @param sentence
	   * @param conjunction
	   * @param negationBegin
	   * @return
	   */
	  public  int finTerminationTermPosNegation(CoreMap sentence, CuePosition negationPos) {
	      
		  int terminationTerm = 0;//Posición donde se encuentra la conjunción.
		  
		   
		  int negationBegin = negationPos.getBegin();
	         
	      String sentenceText = sentence.toString();
	      //int sentenceBegin = sentence.getBegin();
	      //int sentenceBegin = 1;
	     
	      ArrayList <Integer> positions = new ArrayList <Integer>();
	      
	     
	      for(int i=0; i< terminationTerms.size(); i++) {
	    	  
	    	  	String conjunction1 = terminationTerms.get(i);	     
				String pattern = "\\b" + conjunction1 + "\\b";
				
				Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
				Matcher m = p.matcher(sentenceText);
							
			    //Guardo la ultima conjuncion que aparece antes del término negado.
			    while(m.find()) {                           
			      		int temp = m.end();			                    
			              //La conjunción debe estar despues  del begin del termino negado
			              if( temp  <  negationBegin) {
			            	  	  terminationTerm =  temp;
			            	  	  //Evita que el termino de parada y la negacion esten cerca
			            	  	  
			            	  	  if((negationBegin - terminationTerm) > SHORT_SENTENCE_HEURISTIC) {
			            	  		  positions.add(terminationTerm);
			            	  	   //System.out.println(" termination term POS" +   conjunction1);
			            	  	  }
			              }
			              
			              
						  if(temp >= negationBegin) {
							  i = terminationTerms.size();							 
						  }
			                   
				}//while   
			      
			     
	      }// for
	      
	    
	    terminationTerm  =  getMaxTerminationTermPOSNegation( positions, negationBegin);
	
	      
	  
	     return terminationTerm;
			
	 }//end method
	  
	  /**
	   * Obtiene el POS tagg mas cercano al cue
	   */
	  public int  getMaxTerminationTermPOSNegation(ArrayList <Integer> positions1, int negationPostion) {
		 int max =0;
		  
		  for (Integer position: positions1) {
			  if (position > max) {
				  max = position;
				  
			  }
		  }
		  return max;
		  
	  }
	 
	  /**
	   * Busca el siguiente token usando POS
	   * @param sentence
	   * @param beginOfNegation
	   */
	  public int getTokenPartOfSpeechPostNegation(CoreMap sentence1, CuePosition negatedPos) {
		  ArrayList<Integer> terms = new ArrayList<Integer>();
		  int verbPosition =0;
		  int conjunction =0;
		  int counterAllTokens=0;		   
		  int tokenScope = 0;	  
		  
		  int negationPosition = negatedPos.getBegin();	
		
		
		  //FSArray tokens = sentence.getTokens();
		
		  for (CoreLabel myToken: sentence1.get(TokensAnnotation.class)) {		
			  
			  if (myToken.endPosition() < negationPosition) {			  
				 String  posTagg = myToken.get(PartOfSpeechAnnotation.class);
				 counterAllTokens ++;
				 //System.out.println(myToken.get(TextAnnotation.class) + " POS --> " + posTagg);
				 String tokenText = myToken.get(TextAnnotation.class);		 
				 
								 
				 if (posTagg.contains("v")){//es un verbo
					 verbPosition = myToken.endPosition();
					 if((negationPosition - verbPosition) > (SHORT_SENTENCE_HEURISTIC*2)) {						 
						 terms.add (verbPosition);
						 //System.out.println("POOOOOOOOOOOOOOS --> " + tokenText);
					 }
					
				 }
				 
															  
			  }
		  }//for
		
		 
		  tokenScope = getMaxTerminationTermPOSNegation(terms, negationPosition);		 
		  
		  return tokenScope;
	  }//end method
	  
	    
	  /**
	   * Busca estos caracteres de parada
	   * @param myToken
	   * @return
	   */
	  public boolean findCharacterPost(String myToken) {
		  
		  boolean contains =false;
		 
		        						
						if (myToken.contains(";")) {
							contains =true;
						}
						else {
						
								if (myToken.contains("(")) {
									contains =true;
								}
								else {
									
									if (myToken.contains(")")) {
										contains =true;
									}
									
									else {
										
										if (myToken.contains("II")) {
											contains =true;
										}
										else {
											if (myToken.contains("III")) {
												contains =true;
											}
											else {
												if (myToken.contains("IV")) {
													contains =true;
												}
												else {
													if (myToken.contains(":")) {
														contains =true;
													}
												
												   
												}
											}
										}
											
									}
								}
									
					}//else
			
						
	        return contains;     
	      
	  }//end method
	  
	  /**
	   * Busca caracteres de parada solo cuando hay presencia de fechas.
	   * @param myToken
	   * @return 
	   */
	  public boolean findCharacterPost2 (String myToken) {
		  
		  boolean contains =false;
		 
		 if (myToken.contains(";")) {
			contains =true;
		}
		else {
			if (myToken.contains(":")) {
				contains =true;
			}
		}
	
	        return contains;     
	      
	  }//end method
	  
	  
	  /**
	   * Caso especial, cuando l anegación está entre paréntesis Ej: (XYZ negativo)
	   * @param sent
	   * @param neg
	   * @return
	   */
	  public int checkForParentesis(CoreMap sent, CuePosition neg) {
		  
	      int position = 0;
	      
		  String textSentence = sent.toString();	
		  //int negationLimitInThisText = (neg.getEnd() - sent.getBegin()) + 3;
		  int negationLimitInThisText = ((neg.getEnd() - 1) +  3);
		
		  String subSentenceText = "";
		  
		  if(textSentence.length() > negationLimitInThisText) {
			  try {
				  subSentenceText = textSentence.substring(0,negationLimitInThisText);
			  }
			  catch(Exception e) {
				  subSentenceText = textSentence;
			  }
		  }
		  else {
			  subSentenceText = textSentence;
		  }
		  
		  int index1= subSentenceText.indexOf("(");
		  int index2 = subSentenceText.indexOf(")");
		  
		  int negationBegin = neg.getBegin();
		  
			  
		  if(index1 > 0) {
			  if( (1 + index1) <  negationBegin  && (1 + index2) >  negationBegin) {
				 
				  //position = sent.getBegin() + index1 ;	
				  position = 1 + index1 ;	
				  
				  //System.out.println("Parentesis: " + position);
				  
			  }
			  
		  }
		 
		  return position;
	  }//end
	  
	  /**
	   * Este método calcula hacia donde debe apuntar el scope, dependiciendo de la poscion donde aparace la palabra negativos o negativas
	   * Son frases que continene la palabra negativos o negativas, aveces se niega lo de la izquierda o aveces lo de a derecha
	   * Ejemplo:   Las serologias para VIH, VHB y VHC resultaron negativas (scope hacia la izquierda)
	   *          - MARCADORES NEGATIVOS: TTF1, CROMOGRANINA, SINAPTOFISINA. (scope hacia la derecha)
	   *  
	   * @param sent
	   * @param neg
	   */
	  public boolean checkPostNegationPattern1(CoreMap sent, CuePosition neg) {
		 
		  boolean result =false;
		 
		  
		  String negPhrase= neg.getNegationPhrase();
		
		  if (negPhrase.contains("negativas")  || negPhrase.contains("negativos")  ){
			  
			  String sentenceText= sent.toString();
			  sentenceText = sentenceText.toLowerCase();
			  
			  int indexNeg = sentenceText.indexOf(negPhrase);
			  int left = indexNeg;
			  int right= sentenceText.length() - indexNeg;
			 
			  //System.out.println("Left: " + left  + "  rigth: " + right);
			  
			  //scope hacia la derecha de la negacion
			  if (left < right) {
				  if (sentenceText.contains("negativos:") == true || sentenceText.contains("negativas:") ) {
					  result = true;
				  }
				  
			  }
			  
		  }//end if 
		 
		
		 return result;
	  }
	  
	  /**
	   *   * Caso especial: cuando la frase de negación es en negativos o negativas
	   *   Caso especial: Las serologias para VIH, VHB y VHC resultaron negativas 
	   * @param sent
	   * @param neg
	   */
	  public boolean checkPostNegationPattern2(CoreMap sent, CuePosition neg) {
		 
		  boolean result =false;
		  String negPhrase= neg.getNegationPhrase();		
		  if (negPhrase.contains("negativas")  || negPhrase.contains("negativos")  ){
				int limit = SHORT_SENTENCE_HEURISTIC * 2;
				int countTokens =0;
				int countComas =0;
				int negationBegin =neg.getBegin();
				  
				//FSArray tokens = sent.getTokens();
				  
				 for (CoreLabel token: sent.get(TokensAnnotation.class)) {
					  
					  
					  
					  if (token.endPosition() < negationBegin) {
						  countTokens ++;
	 					  String textToken = token.get(TextAnnotation.class);
	 					  if(textToken.equals(",") || textToken.equals("y")) {
	 						 countComas ++;
	 					  }
					  }
					  else {
						  break;
					  }
						 
						 
				 }//end for
				  
			 if (countComas >=2 && countTokens <= limit){
				 result = true;
			 }
			  
		
		}//if
		
		 return result;
	  }
	  
	  /**
	   * 
	   * @param sentence
	   * @param negatedPos
	   * @return
	   */
	  public int getLastTokenPartOfSpeech(CoreMap sentence, CuePosition negatedPos) {
			 
		  int beginLastToken = 0;	  
		 
		  
		  //Se usa para contar el número de tokens que quedan después del término negado
		 
		  int countTokens=0;
		 
		  int beginOfNegation = negatedPos.getBegin();
		  
		  //FSArray tokens = sentence.getTokens();
		  ArrayList<Integer> positions = new ArrayList<Integer>();
		  
		  for (CoreLabel myToken: sentence.get(TokensAnnotation.class)) {	 			  
			  
			  if (myToken.beginPosition() < beginOfNegation) {
				  countTokens++;
				  
				  String pos = myToken.get(PartOfSpeechAnnotation.class);	
				  
				  //System.out.println("token: "+ myToken.getCoveredText() + "\t \t pos:" + pos.getTag() + "\n");
				  
				  if(pos.contains("nc")) {
					 
					  beginLastToken = myToken.beginPosition();
					  positions.add(beginLastToken);
					
					  //System.out.println("PartOfSpeech: NOUN");
				  }
				  
				  if(pos.equals("ao0000") || pos.equals("aq0000")) {
					  
					  beginLastToken = myToken.beginPosition();
					  positions.add(beginLastToken);
					  //System.out.println("PartOfSpeech: ADJ");
					 
				  }
				  
				  //para salir del ciclo
				  if(positions.size() > 2) {
					  break;
				  }
				  
				  //para salir del ciclo
				  if(countTokens>=5) {
					 break;
				  }
				  
				  			  
			  }
		  }//for
		  
		  
		 
		  if (positions.size() > 0) {
			  //System.out.println("\n !!! End Next token: Size " +  positions.size() + "    first pos: " + positions.get(0));
			  beginLastToken = positions.get(0);
			  
		  }
		 
		  return beginLastToken;
	  }//end method
	  

	  
	  
	   /**
	   * Se usa para buscar expresiones que indican doble negación en Post negados.
	  */
	    
	  public int find_Double_PostNegation1 (CoreMap sent, String negationPhrase) {
		  
		  int position = 0;
				  
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
	  
	  
	  /**
	   * Busca el último caracter de parada para post-negation. 
	   * El end de este caracter debe ser menor al begin de la posicion negada.
	   * @param sent
	   * @param negatedPosition
	   * @return
	   */
	  public int  findSpecialCharacterContinuosPostNegation(CoreMap sent, CuePosition negatedPosition) {
			
		  int beginScope=0;
		  int beginNegation= negatedPosition.getBegin();
		 
		  //FSArray tokens = sent.getTokens();
		
		  for (CoreLabel token: sent.get(TokensAnnotation.class)) {		  
				 
				  int tokenBegin =token.beginPosition();
				 
				  if(tokenBegin < beginNegation) {
					  
					  String textToken = token.get(TextAnnotation.class);
					  boolean stopCharacter = findCharacterPost(textToken);
					  
					  if(stopCharacter == true) {
						  
						  beginScope =token.endPosition();
						  //System.out.println("****     last Position: " + lastPosition + " token " + token.getCoveredText());
					  }
					  
					  if(tokenBegin >=beginNegation) {
						  break;
					  }					  
			 }
				  
				 
		  }//for
		  
		
		  return beginScope;
	  }//end method
	  
	  
	  /**
	   * Busca si hay Fechas para evitar caracteres de parada que están dentro de las fechas como: (, /, )
	   * @param sentence
	   * @param negationPhrase
	   * @return
	   */
	  public  boolean findSpecialChracterInDates(CoreMap sentence, CuePosition neg) {
		 	  
		    int index1 = 0;
		    boolean resp = false;
			
		    String   regex="(\\()?\\d{1,2}\\s*[\\-|\\/|\\-]\\s*\\d{1,2}\\s*[\\-|\\/|\\-]\\s*\\d{1,4}\\s*(\\))?";;
									                
			//System.out.println(pattern);
			String sentenceText = sentence.toString();			    
			
			Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(sentenceText);
						
			while(m.find()) {				
				index1  =m.end(); 
		    }//end while
			
			
			if (index1 > 0) {				
	     		//int result = sentence.getBegin() + index1;
	     		int result = 1 + index1;
				
				if(result < neg.getBegin()) {				
					resp = true;
				}
			
			}
			
			return resp;
	 
	  }//end method
		  
		  
	
	  /**
	   * Count commas
	   * @param s
	   * @return
	   */
	  public int countCommas(String s) {
		  int commas = 0;
		   for(int i=0;i<s.length();i++){
			     if(s.charAt(i)==','){
			       commas++;
			    }
		   }
		   return commas;
	  }
	  
	  
	  
	  /**
	   * 
	   * @param sentence1
	   * @param negatedPos
	   * @return
	   */
	  
	  public int countTokensAfterNegation(CoreMap sentence1, CuePosition negatedPos) {
		  int endOfNegation = negatedPos.getEnd();
		  int count=0;
		  int last=endOfNegation;	  		 
		  for (CoreLabel myToken: sentence1.get(TokensAnnotation.class)) {					  
				if (myToken.beginPosition() > endOfNegation) {	
					count++;
					last=myToken.beginPosition();
		        }
				if (count>=5) {//initial condition NEgex Algorithm
					break;
				}

		  }
		  return last;
	  }
	  
	  
	  /**
	   * 
	   * @param sentence1
	   * @param negatedPos
	   * @return
	   */
	  
	  public int countTokensBeforeNegation(CoreMap sentence1, CuePosition negatedPos, int begin) {
		  int endOfNegation = negatedPos.getBegin();
		  int count=0;
		  int last=endOfNegation;	  		 
		  for (CoreLabel myToken: sentence1.get(TokensAnnotation.class)) {					  
				if (myToken.beginPosition() < endOfNegation) {	
					count++;
					last=myToken.beginPosition();
		        }
				if (count>=5) {//initial condition NEgex Algorithm
					break;
				}

		  }
		  return last;
	  }
	  
	  /**
	   * To validate the number of tokens in the Pre scope.
	   * @param sentence1
	   * @param negatedPos
	   * @return
	   */
	  
	  public int validateTokensNumberScope(CoreMap sentence1, CuePosition negatedPos, int scopeLimit) {
		  int endOfNegation = negatedPos.getEnd();
		  int count=0;
		  	  		 
		  for (CoreLabel myToken: sentence1.get(TokensAnnotation.class)) {	
			 int tokenPosition = myToken.beginPosition();
			 if (tokenPosition > endOfNegation && tokenPosition < scopeLimit) {	
					count++;					
					
		     }//end if.
			 
			 if(tokenPosition >= scopeLimit) {
					break;
			 }
			 
			 
		  }//end for.		 
		 
		  return count;
	  }//
	  
	  
	  /**
	   * To validate the number of tokens in the Pos-scope.
	   * @param sentence1
	   * @param negatedPos
	   * @return
	   */
	  
	  public int validateTokensNumberScopePost(CoreMap sentence1, CuePosition negatedPos, int scopeBegin) {
		  int endOfNegation = negatedPos.getBegin();
		  int count=0;
		  	  		 
		  for (CoreLabel myToken: sentence1.get(TokensAnnotation.class)) {	
			 int tokenPosition = myToken.beginPosition();
			 if ( tokenPosition > scopeBegin && tokenPosition < endOfNegation ) {	
				 //System.out.println(" pos token: " +  myToken.originalText());
					count++;					
					
		     }//end if.
			 
			 if(tokenPosition > endOfNegation) {
					break;
			 }
			 
			 
		  }//end for.		 
		 
		  return count;
	  }//
	  
	  
	  /////==========================================================
	  
	  public ArrayList <Scope> getMorphologicalScope(HashMap<Integer,CuePosition> negationsMap, CoreMap sentence){
	      ArrayList <Scope> scopeList = new ArrayList<Scope>();	      
	      
	      //System.out.println("\n \n Iniciando cálculo del Scope Mode: ("+ negationType  +   ") \n");	      
	       for (HashMap.Entry<Integer, CuePosition> entry :negationsMap.entrySet()) {
	          
	           CuePosition negatedPosition=  entry.getValue();
	           int beginScope = negatedPosition.getEnd();
	          
	           Scope scope = new Scope();	           
	           scope.setBegin(beginScope + 1);          
	           scope.setEnd(beginScope + 1);
	           scope.setPhrase(negatedPosition.getNegationPhrase());
	           scope.setSentence(sentence.toString());
	           
	           scope.setType("Morphological-neg");
	           scopeList.add(scope);
	       }//end for
	       
           	           
        return scopeList;
	       
	     
	       
	  }
	  
	 /**
	  * Inicializa listas y diccionarios
	  */
	  public void setUpNegationLists() {	
		 terminationTerms = new ArrayList<String>();
		 uncertaintyTerms = new ArrayList<String>();
		 adversativeWordsSpanish = new ArrayList<String>();	  
		 doublePostNegationPhrases = new ArrayList<String>();	  
	       
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
		                categoriesMap.put(line_words[0].toLowerCase(), "Negation");		                
		            }
		            
		            
		            if(line_words[1].equals("[post-negation]")){		                
		                categoriesMap.put(line_words[0].toLowerCase(), "Negation");
		            }
		            
		            		            
		            if(line_words[1].equals("[uncertainty]")){
		            	uncertaintyTerms.add(line_words[0].toLowerCase());		            	
		            	categoriesMap.put(line_words[0].toLowerCase(), "Uncertain");		            	
		            }		            
		                  
		            if(line_words[1].equals("[post-uncertainty]")){
		            	categoriesMap.put(line_words[0].toLowerCase(), "Uncertain");		            	
		            }
		            

		            if(line_words[1].equals("[termination-term]")){
		                terminationTerms.add(line_words[0].toLowerCase());            
		            }
		            
		            if(line_words[1].equals("[double-negation-post]")){
		                doublePostNegationPhrases.add(line_words[0].toLowerCase());            
		            }
		            
		            
		            if(line_words[1].equals("[adversative]")){
		                adversativeWordsSpanish.add(line_words[0].toLowerCase());            
		            }
		            
		            if(line_words[1].equals("[double-negation-post]")){
		                doublePostNegationPhrases.add(line_words[0].toLowerCase());            
		            }
		            
		            if(line_words[1].equals("[morphological-negation]")){
		            	 categoriesMap.put(line_words[0].toLowerCase(), "Negation");	            
		            }
		          
		       }//end for
		       
		       addOtherTerminationTerms(); 
		   
		 }//End Method
		  
		 public void addOtherTerminationTerms() {
			 terminationTerms.add(" - ");
		     terminationTerms.add(", con");
		     terminationTerms.add(", que");
		 }
		    
		 public void printTerminationTerms(){
		    	System.out.println("\n \n************************************************* \n");
		        System.out.println("\n Termination Terms \n");
		        for (int i=0; i<terminationTerms.size();i++){
		            System.out.println(i + ": " + terminationTerms.get(i));
		            
		        }
		        
		        System.out.println("========================================================= \n \n");
		        
		        System.out.println("\n Adversative \n");
		        for (int i=0; i<adversativeWordsSpanish.size();i++){
		            System.out.println(i + ": " + adversativeWordsSpanish.get(i));
		            
		        }
		        
		     
		               
		        
		        System.out.println("========================================================= \n\n ");
		    }//end method

	  
	  
	  /**
	     * Load UdPipe
	     */
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
				System.out.println("*** Error: No se ha podido leer el modelo UdPipe, verifique la ruta del archivo. ****\n");
				
			}
						
		}
	    
	    

}
