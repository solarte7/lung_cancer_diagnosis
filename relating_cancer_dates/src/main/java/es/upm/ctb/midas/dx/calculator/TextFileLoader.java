package es.upm.ctb.midas.dx.calculator;
import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner; // Import the Scanner class to read text files
//import es.upm.ctb.midas.annotator.textPreProcessing.database.TextDocument;

//import es.upm.ctb.midas.clikes.negation.NegatedPosition;



public class TextFileLoader {

	ArrayList<String> speculations;
	ArrayList<String> subject;
	ArrayList<String> negations;
	ArrayList<String> other;
	ArrayList<String> phrases;
	ArrayList<String> cancerExpresions;
	ArrayList<String> notesDx;
	HashMap <Integer, Integer > errorMas30;

	

	
	
	public TextFileLoader() {	
		
	    //errorMas30 = new  HashMap <Integer, Integer >();
	    loadDictionary();
	    loadPhrases();
	    loadCancerExpresions();
	    loadNotesDx();
	}
	 
	 public ArrayList<String> getSpeculations() {
		return speculations;
	}

	
	public ArrayList<String> getSubject() {
		return subject;
	}


	public ArrayList<String> getNegations() {
		return negations;
	}

	
	public ArrayList<String> getOther() {
		return other;
	}
	
	public ArrayList<String> getPhrases() {
		return phrases;
	}
	
	public ArrayList<String> getCancerExpresions() {
		return cancerExpresions;
	}

	

	public ArrayList<String> getNotesDx() {
		return notesDx;
	}
	
	public HashMap<Integer, Integer> getErrorMas30() {
		return errorMas30;
	}

	///======================================================================================================== ////////////////////////
	
	
	
	public void readErrorMas30(String filePath) {
		ArrayList<String> lines =new ArrayList<String>();
		try {
		      File myObj = new File(filePath);
		      Scanner myReader = new Scanner(myObj);
		      while (myReader.hasNextLine()) {
		        String data = myReader.nextLine();		       
		        lines.add(data);
		      }
		      myReader.close();
		      System.out.println("Filters loaded");
		      
		    } catch (FileNotFoundException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		    }
		
		loadEhrs(lines);
		
	}
	
	
	public void readFilters(String filePath) {
		ArrayList<String> filters =new ArrayList<String>();
		
		    try {
		      File myObj = new File(filePath);
		      Scanner myReader = new Scanner(myObj);
		      while (myReader.hasNextLine()) {
		        String data = myReader.nextLine();		       
		        filters.add(data);
		      }
		      myReader.close();
		      System.out.println("Filters loaded");
		      
		    } catch (FileNotFoundException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		    }
		    
		    //setUp filters
		    configureFilterLists(filters);
		    loadPhrases();
		    
   }//end method
	
public void configureFilterLists(ArrayList list){        
        
        //System.out.print("\n \n \n\n \n  ********************************************************\n \n \n \n");
        
        for (int i=0; i<list.size();i++){
            String   line = (String) list.get(i);
            String line_words[]= line.split("\t");
           // System.out.println ("Line: " + i + "\t"+ line);
           
            if(line_words[1].equals("[especulativas]")){
                speculations.add(line_words[0].toLowerCase());            
            }
            
            if(line_words[1].equals("[negativas]")){
               negations.add(line_words[0].toLowerCase());            
            }
            
            if(line_words[1].equals("[otros_cancer]")){
                other.add(line_words[0].toLowerCase());            
            }
            
            if(line_words[1].equals("[sujeto]")){
                subject.add(line_words[0].toLowerCase());            
            }
            
                                
           // System.out.println("Speculations " + speculations.size());
  
            
        }//end for
    
    }
  
  /**
   * load subcategories
   */

  public void loadNotesDx() {
	  notesDx = new ArrayList<String>();	  
	  ArrayList<String> notes1 = new ArrayList<String>();
	  
	  //Prioridad alta 234
	  notes1.add ("Juicio clínico Cex");
	  notes1.add ("Juicio clínico Hos");	  
	  notes1.add ("Anamnesis Cex");
	  notes1.add ("Anamnesis Hos");
	  
	  //Prioridad media
	  notes1.add ("JUSTIFICACIÓN DE TRATAMIENTO ");	  
	  notes1.add ("Evolución Méd Cex");
	  notes1.add ("ITC Oncología Médica");
	  notes1.add ("Evolución Méd HDía");
	  notes1.add ("Evolución Méd Hos");
	  notes1.add ("Comité de Tumores");
	  notes1.add ("Evolución Médica Urgencias");
	  notes1.add ("ITC Cirugía Torácica");
	  notes1.add ("ITC Cuidados Paliativos");	  
	  notes1.add ("ITC Oncología Radioterápica");
	  notes1.add ("Res situación Cex");
	  notes1.add ("Res situación Hos");
	  notes1.add ("Tratamiento Cex");
	  notes1.add ("Tratamiento HDía");	  
	  notes1.add ("tratamiento hos");
	  notes1.add ("Motivo de Ingreso");
	
	  
	  //Prioridad Baja
	  notes1.add ("Nota de Urgencias");
	  notes1.add ("informe de seguimiento consulta externa");
	  notes1.add ("informe alta de urgencias");
	  notes1.add ("ITC Medicina Interna");
	  notes1.add ("ITC Medicina Interna Autoinmunes");
	  notes1.add ("ITC Medicina Interna Enfermedades Hepáticas");
	  notes1.add ("ITC Medicina Interna Infeccioso");	 
	  notes1.add ("Expl física Cex");
	  notes1.add ("Expl física Hos");	 	 
	  notes1.add ("pr complement hos");
	  notes1.add ("pr complement cex");
	  
	 
	  //Text Normalization
	   for (int i=0; i < notes1.size();i++) {
		  String text= notes1.get(i);
		  text=cleanAcents(text.toLowerCase());
		  notesDx.add(text);
	   }
	  
	
	 
  }

/**
 * 
 */
	public void loadCancerExpresions() {
		cancerExpresions = new ArrayList<String>();
		
		cancerExpresions.add("cancer");
		cancerExpresions.add("cáncer");
		cancerExpresions.add("ca");
		cancerExpresions.add("carcinoma");
		cancerExpresions.add("adenocarcinoma");
		cancerExpresions.add("pulmón");
		cancerExpresions.add("pulmon");
		cancerExpresions.add("pulmonar");
		cancerExpresions.add("neoplasia");		
		cancerExpresions.add("adenoca");
}

  /**
   * Configura frases especultivas
   */

   public void loadPhrases() {
	   phrases = new ArrayList<String>();
	   
	   String r1 = "(?i)(tras\\s*iniciar\\s*estudios?)";
	   String r2 = "(?i)(tras seguimiento)";
	   String r3 = "(?i)hace\\s*(\\d{1,2}|un[ao]?|dos|tres|cuatro|cinco|seis|siete|ocho|nueve|diez)\\s*a[nñ]os?";
	   String r4 = "(?i)(\\d{1,2}|un[ao]?|dos|tres|cuatro|cinco|seis|siete|ocho|nueve|diez)\\s*años\\s*antes?";
	   String r5= "(?i)hace\\s*(\\d{1,2}|un[ao]?|dos|tres|cuatro|cinco|seis|siete|ocho|nueve|diez)\\s*años?";
	   String r6= "(?i)para confirmar";
	   String r7= "(?i)puede tener";
	   String r8= "(?i)comité de tumores";
	   String r9= "(?i)comite de tumores";
	   String r10= "(?i)pendiente de PET";
	   String r11= "(?i)en otro hospital";
	   String r12= "(?i)a\\s*los\\s*(\\d{1,2}|un[ao]?|dos|tres|cuatro|cinco|seis|siete|ocho|nueve|diez)\\s*a[nñ]os?\\s*de\\s*ca(ncer)?"; //temporal
	   String r13= "(?i)desde\\s*(el)?\\s* \\d{1,2}\\/\\d{1,2}\\s?-?(hasta)?\\s?\\d{1,2}\\/\\d{1,2}";
	   String r14 = "(?i)negativo\\s*(\\w*)?(\\s*)?(\\w*)?(\\s)*(can?c?e?r?)";
	   String r15 = "(?i)si\\s*se\\s*confirma\\s*que";
	   String r16 = "(?i)sometido\\s*a";
	   String r17=  "(?i)y rt-taxol en agosto 2001";//temporal
	   String r18= "(?i)operado\\s*mediante\\s*lobectomia";//temporal
	   String r19= "(?i)discutido\\s*en\\s*sesion";
	   String r21 = "(?i)intervenido\\s*de";
	   String r22 = "(?i)\\d{1,2}\\/\\d{1,2}-\\d{1,2}\\/\\d{1,2}\\/\\d{1,4}\\/\\d{1,2}-\\d{1,2}\\/\\d{1,2}\\/\\d{1,4}";//temporal
	   String r23 = "(?i)Progresi[oó]n\\s*[{uú]nica\\s*del\\s*tumor";
	   String r241= "(?i)(Citolog[ií]a NEGATIVA para celulas tumorales malignas)";//temporal
	   String r25 = "(?i)con\\s*(qmt|tto|quimio)\\s*desde\\s*\\w*\\s*\\s*\\d{1,4}\\s*(hasta|a)?\\s*\\w*\\s\\d{1,4}";
	   String r26=" (?i)antes\\s*del\\s*diagn[oó]stico\\s*de";
	   String r27 = "(?i)(no descartable?)";
	   String r28 = "(?i)hasta el proximo control";//temporal
	   String r29 = "(?i)antecedentes\\s*familiares";//
	   String r30 = "(?i)antecedentes\\s*personales";//
	   String r31 = "(?i)(desde)?\\s*hace\\s*(\\d{1,2}|un[ao]?|dos|tres|cuatro|cinco|seis|siete|ocho|nueve|diez)?\\s*(meses|a[nñ]os)";
	   String r32 = "(?i)no\\s*podemos\\s*descartar";//
	   String r33 = "(?i)no\\s*se\\s*puede\\s*descartar";//con antecedente
	   String r34 = "(?i)con\\s*antecedentes?";
	   String r35 = "(?i)resumen";
	   phrases.add(r1);
	   phrases.add(r2);  phrases.add(r3);   phrases.add(r4);  phrases.add(r5); 
	   phrases.add(r6);  phrases.add(r7);   phrases.add(r8);  phrases.add(r9);  
	   phrases.add(r10); phrases.add(r11);  phrases.add(r12); phrases.add(r13);  
	   phrases.add(r14); phrases.add(r15);  phrases.add(r16);  phrases.add(r17); 
	   phrases.add(r18); phrases.add(r19);  phrases.add(r21);  phrases.add(r22);
	   phrases.add(r23); phrases.add(r241); phrases.add(r25);  phrases.add(r26); 
	   phrases.add(r27); phrases.add(r28);  phrases.add(r29); phrases.add(r30);
	   phrases.add(r31); phrases.add(r32); phrases.add(r33);   phrases.add(r34);
	   phrases.add(r35);
	   
	  
   }//end


	/**
	 * Configure dictionary
	 */
	public void loadDictionary() {
	
		//Especulativas
			String elements1[] = {"dudas", "dudoso", "dudosa", "posible", "posilbe", "sugestiva",
								"sugestivo", "sugestivos", "sugieren", "sugiere", "previo", "proyecto", 
								"valoración", "valoracion", "modelo", "sugerente", "sugiere", "posibilidad",
								"consulta",  "podria", "podrá", "podría",  "sospecha", 
								"sospechosa", "valoración", "valoracion", "valorar", 
								"preocupado", "descartar", "descartado", "descartada", "compatible", "resuelto"};
		
			 speculations = new ArrayList<String> (Arrays.asList(elements1));	 
			      	
			     
			    //other
			String elements2[]  = {"mama", "prostata",  "higado", "colon", "prostático", 
					                "prostatico", "próstata",  "estómago", "estomago",
			    		           "vejiga", "estomacal", "riñón", "riñon", "pancreas",  "páncreas",  
			    		           "leucemia", "cirugía", "cirugia", "laringe", "supraglotis", "recto", 
			    		           "rectal", "cilindro", "cilindros", "vesícula", 
			    		           "vesical", "ovario", "tiroides", "osteopenia",
			    		           "linfaticos", "linfáticos", "linfatico", "faringe", 
			    		           "utero", "ulcera", "ulcerosa", "reumatica",  "urotelial", "cardiaco", "cardiaca"};
			     
			other = new ArrayList<String> (Arrays.asList(elements2));	 
			     
			     	
			 //Negations			     
			 String elements3[]  = {"puede ser negativo"};
			 negations = new ArrayList<String> (Arrays.asList(elements3));	
			   
			    		 
			 //Sujeto
			 String elements4[]  = {"AF", "af:", "AF:", "familiares", "padre", "madre", 
					               "hermano", "hno", "hna", "hermana", "hermano", "mamá", 
					                "abuelo", "abuela", "abuel", "paterno", "paterna",
			     		            "materno", "materna", "familiar", "pariente","-p", "-m", "tio", "tia", 
			     		            "esposa", "esposo", "marido","herman","primo", "prima",
			     		           "primos", "primas","hermanos", "hermanas",
			     		            "muerto", "muerta", "muierto", "fallecida", "fallecido"};
			     
			 subject = new ArrayList<String> (Arrays.asList(elements4));	
			     
			     
	}//end
	
	
	
	/**
	 * Load ehrs
	 * @param list
	 */
	public void loadEhrs(ArrayList<String> list){
	//System.out.println("\n ***************  error mas 30 +   ");
	
	   for (int i=0; i<list.size();i++){
		String   line = (String) list.get(i);
	   String line_words[]= line.split("\t");
	   errorMas30.put(Integer.parseInt(line_words[0]), Integer.parseInt(line_words[1]));
	 }

  } 
	/*Clean acents
	 * 
	 */
	public String cleanAcents(String text) {
		 
		        text = Normalizer.normalize(text, Normalizer.Form.NFD);
		        text = text.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
		        return text;
	  
	}
		
	public static void main(String a[]) {
		TextFileLoader reader=new TextFileLoader();
		String filtros= "/home/kdd/ctb/validacion/Diagnostico/filtrosDx.txt";
		//String filtros= "/resources/filtrosDx.txt";
		//String errors= "/home/kdd/ctb/validacion/Diagnostico/errorMas30.txt";
		
		//reader.readFilters(filtros);
		//reader.readErrorMas30(errors);
		
		System.out.println ("Speculations " + reader.getSpeculations().size());
		System.out.println ("Subject " + reader.getSubject().size());
		System.out.println ("Negation " + reader.getNegations().size());
		System.out.println ("Other " + reader.getOther().size());
		System.out.println ("Phrases " + reader.getPhrases().size());
		System.out.println ("cleaned " +reader.cleanAcents("apé amá"));
		//System.out.println ("(corazon(-".contains("corazon"));
	}//main
	
}//end class


