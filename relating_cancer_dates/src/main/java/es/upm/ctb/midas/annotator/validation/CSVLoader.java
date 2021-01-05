package es.upm.ctb.midas.annotator.validation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import es.upm.ctb.midas.annotator.validation.*;

import java.sql.Connection;




public class CSVLoader {
	int cont;
	//DataBaseDocument dataBaseDoc;
	
	
	
	public CSVLoader() {
		 
		
	}
	
	
	
	
	
	public int getCont() {
		return this.cont;
	}
	
	public  CSVParser openCSVFile(String pathFile, char delimiter ) {
		
		try {
			BufferedReader reader= new BufferedReader(new FileReader(pathFile));
        
			CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
			    .withDelimiter(delimiter).withRecordSeparator('\n').withFirstRecordAsHeader()
			);

	       System.out.println("CSV File was open -> " + pathFile );	     
	       return csvParser;
	   }
		catch(Exception e){
			System.out.println("Error to open CSV: " + e.getMessage());
			return null;
		}
		
		
			
}//end method
   public ArrayList <Diagnostic> processValidationFile(CSVParser parser) {
	   ArrayList <Diagnostic> diagList = new ArrayList<Diagnostic>();
	   int i=1;
	   try {
			
			for (CSVRecord row :parser) {
				int ehr =Integer.parseInt(row.get("EHR"));
				String date = row.get("fecha_diagnostico");
				System.out.println(i + " => " +  ehr  + "  " + date);
				i++;
				Diagnostic d = new Diagnostic();
				d.setEhr(ehr);
				d.setDate(date);
				diagList.add(d);
				
			}
	   }catch(Exception e) {
		   System.out.print("Error " + e.getMessage());
		}
	   
	   
	   return diagList;
	   
   }
	
	public void processCSVFile(CSVParser parser) {   
		
		int i=0;	
		try {
			
				for (CSVRecord row :parser) {		
					    i++;
					    Date fechaNac=parseDate(row.get("FECHA_NAC"));
				    	Date fechaInicio=parseDate(row.get("FEC_INI_PROCESO"));
				    	Date fechaCreaObjeto=parseDate(row.get("FECHA_CREA_OBJETO"));
				    	String ehr = row.get("MEDICALRECORD");
				    	String sexo= row.get("SEXO");
				    	String estadoProceso =  row.get("ESTADO_PROCESO");
				    	String tipoObjeto =  row.get("TIPO_OBJETO");
				    	String plantillaNota =  row.get("PLANTILLA_NOTA");
				    	String descripcion =  row.get("DESCRIPCION");
				    	String tituloInforme = row.get("TITULO_INFORME");
				    	String textoInforme = row.get("TEXTO_INFORME");
				    	
				    	
				    	System.out.println("i: " + i);
				    	System.out.println("EHR: " + ehr);   	
				    	System.out.println("SEXO: " + sexo);
				    	System.out.println("FECHA NAC: " + row.get("FECHA_NAC") +  "  ==> " +  fechaNac.toString());				    	
				    	System.out.println("FECHA INI: : " + row.get("FEC_INI_PROCESO") +  "  ==> " +  fechaInicio.toString());
				    	System.out.println("FECHA CREACION: " + row.get("FECHA_CREA_OBJETO") +  "  ==> " +  fechaCreaObjeto.toString());
				    	System.out.println("ESATADO: " + estadoProceso);
				    	System.out.println("TIPO: " + tipoObjeto);
				    	System.out.println("PLANTILLA: " + plantillaNota);
				    	System.out.println("DESCRIP: " + descripcion);
				    	System.out.println("TITULO: " + tituloInforme);
				    	System.out.println("TEXTO: " + textoInforme);
				    	System.out.println(" =================================================================\n");
				    	
				    	
				       
				        
				       Document doc = new Document();
				      
				       doc.setEhr(Integer.parseInt(ehr));
				       doc.setSexo(sexo);
				       doc.setFechaNacimiento(fechaNac);
				       doc.setFechaInicio(fechaInicio);
				       doc.setFechaCreaObjeto(fechaCreaObjeto);
				       doc.setEstadoProceso(estadoProceso);
				       doc.setTipoObjeto(tipoObjeto);
				       doc.setPlantillaNota(plantillaNota);
				       doc.setDescripcion(descripcion);
				       doc.setTituloInforme(tituloInforme);
				       doc.setTextoInforme(textoInforme);
				       
				       
				       /*-------------------------------------------------------------
				        Saving document to DataBase
				       ---------------------------------------------------------------
				       */
				       //this.dataBaseDoc.saveDocument(doc); //JHS
				       
				     
				       
				       
				        
				}	             	
		  }  
		catch(Exception e) {
			System.out.println("Error: " +  e.getMessage());
			System.out.println("i " +  i);
		}
		
		

		
 }//end
	//Obtiene una fecha en el formato deseado
	public Date parseDate(String d1) {
		
		if(d1.equals ("") || d1.equals(null)) {
			System.out.println("\n \n ***************************************  ");
			System.out.println("Error al obtener la fecha ");
			System.out.println("\n \n *******************************************\n");
			cont ++;
			return new Date();
			
		}
		else {
			try {
				
				 SimpleDateFormat inSDF = new SimpleDateFormat("dd/mm/yy");
				 SimpleDateFormat outSDF = new SimpleDateFormat("yyyy-mm-dd");
				 Date date1 = inSDF.parse(d1);
				 
				
				 String d2= outSDF.format(date1);			
				 System.out.println("d2 " + d2);
				 
				 Date date2= (Date)outSDF.parse(d2);
				 
				
				return date2;
				
			} catch (ParseException ex) {				
				System.out.println("Error getting Date: ");
				Date date2= new Date();
				System.out.println("\n \n ***************************************  ");
				System.out.println("Error " + ex.toString() );
				System.out.println("\n \n *******************************************\n");
				cont++;
				return date2;
				
				
				
			} 
		}
			
		
			
			 
		
	}
	
	
	public String cleanText(String textInput) {
		String textOutput="";
		textOutput = textInput;
		//textOutput= this.eliminateHTMLLabels(textInput);
		//textOutput= this.eliminateNonPrintCharacter(textOutput);
		//textOutput= this.eliminateSignedBy(textOutput);
		return textOutput;
	}
	
	public String getDocumetID() {
		return "";
	}
	
	
	
	
	
	public static void main(String a[]) {
			 
			 String path_in="/home/kdd/ctb/validacion/correct_dx_copia.csv";
			 //String path_out="/home/kdd/files/";				 
			 char  csv_delimiter=';';		 
			 
		     CSVLoader csv= new CSVLoader();      			 
	
			System.out.println("File: " + path_in);
			CSVParser csvParser= csv.openCSVFile(path_in, csv_delimiter);	    					   
		    System.out.println("\nProcesing Documents \n ");
			//csv.processCSVFile(csvParser);
			//System.out.println("Cont: " + csv.getCont());
			csv.processValidationFile(csvParser);
					    
					   
	       

	}
}
