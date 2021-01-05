package es.upm.ctb.midas.annotator.validation;



import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.csv.CSVParser;


public class Validation {
	static int  errorMeses[] = new int[60] ;
	static int errorSemanas[] = new int [12];
	static int errorPrimerMesPorDias[] = new int [31];
	static int noError=0;
	static int SinErrorSemana=0;
	static int total=0;
	
		
	public ArrayList <Diagnostic> loadCSV() {
		 //String path_in="/home/kdd/ctb/validacion/datos_de_los_medicos.csv";
		 String path_in="/home/kdd/ctb/validacion/validacion_manual.csv";
		 //String path_out="/home/kdd/files/";				 
		 char  csv_delimiter=',';
		 
	     CSVLoader csv= new CSVLoader();  
	     CSVParser csvParser= csv.openCSVFile(path_in, csv_delimiter);	   
	     ArrayList <Diagnostic> list =csv.processValidationFile(csvParser);
		return list;
	}
	public void insertData(ArrayList <Diagnostic> list) {
		MySQLValidation database = new MySQLValidation();
		
		for (int i =0; i< list.size(); i++) {
			Diagnostic d = list.get(i);
			String sql ="INSERT INTO datos_real (ehr, fecha_diagnostico) VALUES (" + d.getEhr() + ", '" + d.getDate() + "');";
			database.insertData(sql);
		}
		//database.closeConnection();
	}
	
	public void getDiagnosticsHospital() {
		MySQLValidation database = new MySQLValidation();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		int nulos=0;
		int contUnasemana =0;
		int contDosSemanas=0;
		int contUnMes =0;
		int contTresMeses =0;
		
		int totalPacientes=987;
		
		
		
		System.out.println("Consultando datos ...");
		HashMap<Integer, String> mapHospital = database.getDianosticHospital(); 		
		HashMap<Integer, String> mapReal= database.getDianosticReal();
		//HashMap<Integer, String> mapReal = database.getDianosticClikes();
		
		System.out.println("\nImprimendo ...\n");
		System.out.println("ehr" +  "\t " + "hospital" + "\t" +  "clikes" + "\t \t" + "diferencia" + "\t" +  "diferencia");  
		
		
		int i=0;
		for (int ehr: mapHospital.keySet()){
			i++;
            String stringHospital = mapHospital.get(ehr);  
            String stringClikes = mapReal.get(ehr);
            
            try {
			    
				int dias1 =0;
				int dias2 =0;
            
				if(stringClikes == null) {
					nulos ++;
					
				}
				else {
					Date dateHospital = dateFormat.parse(stringHospital);
					Date dateClikes = dateFormat.parse(stringClikes);
					
					dias1=(int) ((dateHospital.getTime()-dateClikes.getTime())/86400000);
					dias2=(int) ((dateClikes.getTime()-dateHospital.getTime())/86400000);
					System.out.println(i+ " " + ehr +  "\t " + stringHospital + "\t" +  stringClikes + "\t"  + dias1 +  "\t" + dias2); 
					//System.out.println(ehr +  "\t " + stringHospital + "\t" +  stringClikes); 
					
					if (Math.abs(dias1)<=7) {
						contUnasemana ++;
					}
					
					if ( Math.abs(dias1) <=15) {
						contDosSemanas ++;
					}
					
					if ( Math.abs(dias1) <=31) {
						contUnMes ++;
					}
					
					if (Math.abs(dias1) <=91) {
						contTresMeses ++;
					}
					
					acumularError(dias1);
					
				}
				
				
            
            } catch (ParseException e) {
				
            }
            
		}//for 
		//========Calculando indicadores =======================================
		
		System.out.println("\n ================================ ");
		//System.out.println("\n Nulos: " +  nulos);
		//System.out.println("\n 1 semana: " +  contUnasemana);
		//System.out.println("\n 2 semanas: " +  contDosSemanas);
		//System.out.println("\n 1 mes: " +  contUnMes);
		//System.out.println("\n 3 meses: " + contTresMeses);
		//System.out.println("\n ================================ ");
		
		double precision =0.0;
		double recall =0.0;
		
		System.out.println("\t" +  "\t " + "\t Precision" + "\t \t" +  "Recall" );  
		
		
		precision = (double)contUnasemana/(double)978;
		recall =    (double) contUnasemana/ (double)1028;				
		System.out.println("1 semana" +  "\t \t" + precision + "\t" +  recall );  
		
		
		precision = (double)contDosSemanas/(double)978;
		recall =    (double) contDosSemanas/ (double)1028;		
		System.out.println("Dos semanas" +  "\t \t" + precision + "\t" +  recall );  
		
		
		
		precision = (double)contUnMes/(double)978;
		recall =    (double) contUnMes/ (double)1028;		
		System.out.println("1 Mes" +  "\t \t \t" + precision + "\t" +  recall );  
		
		
		precision = (double)contTresMeses/(double)978;
		recall =    (double) contTresMeses/ (double)1051;		
		System.out.println("3 Meses" +  "\t \t \t" + precision + "\t" +  recall );  
		
		printError();
		
	}
	
	//========= From C-likes
	
	public void getDiagnosticsClikes() {
		MySQLValidation database = new MySQLValidation();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		int nulos=0;
		int contUnasemana =0;
		int contDosSemanas=0;
		int contUnMes =0;
		int contTresMeses =0;
		
		int totalPacientes=980;
		
		
		
		System.out.println("Consultando datos ...");
		//HashMap<Integer, String> mapHospital = database.getDianosticHospital(); getDianosticReal
		HashMap<Integer, String> mapHospital = database.getDianosticReal();
		HashMap<Integer, String> mapClikes = database.getDianosticClikes();
		
		System.out.println("\nImprimendo ...\n");
		System.out.println("ehr" +  "\t " + "hospital" + "\t" +  "clikes" + "\t \t" + "diferencia" + "\t" +  "diferencia");  
		
		
		int i=0;
		for (int ehr: mapHospital.keySet()){
			i++;
            String stringHospital = mapHospital.get(ehr);  
            String stringClikes = mapClikes.get(ehr);
            
            try {
			    
				int dias1 =0;
				int dias2 =0;
            
				if(stringClikes == null) {
					nulos ++;
					
				}
				else {
					Date dateHospital = dateFormat.parse(stringHospital);
					Date dateClikes = dateFormat.parse(stringClikes);
					
					dias1=(int) ((dateHospital.getTime()-dateClikes.getTime())/86400000);
					dias2=(int) ((dateClikes.getTime()-dateHospital.getTime())/86400000);
					System.out.println(i+ " " + ehr +  "\t " + stringHospital + "\t" +  stringClikes + "\t"  + dias1 +  "\t" + dias2); 
					//System.out.println(ehr +  "\t " + stringHospital + "\t" +  stringClikes); 
					
					if (Math.abs(dias1)<=7) {
						contUnasemana ++;
					}
					
					if ( Math.abs(dias1) <=15) {
						contDosSemanas ++;
					}
					
					if ( Math.abs(dias1) <=31) {
						contUnMes ++;
					}
					
					if (Math.abs(dias1) <=91) {
						contTresMeses ++;
					}
					
					acumularError(dias1);
					acumularErrorSemana(dias1);
					acumularErrorPrimerMes(dias1);
					
				}
				
				
            
            } catch (ParseException e) {
				
            }
            
		}//for 
		//========Calculando indicadores =======================================
		
		System.out.println("\n ================================ ");
		//System.out.println("\n Nulos: " +  nulos);
		//System.out.println("\n 1 semana: " +  contUnasemana);
		//System.out.println("\n 2 semanas: " +  contDosSemanas);
		//System.out.println("\n 1 mes: " +  contUnMes);
		//System.out.println("\n 3 meses: " + contTresMeses);
		//System.out.println("\n ================================ ");
		
		double precision =0.0;
		double recall =0.0;
		
		System.out.println("\t" +  "\t " + "\t Precision" + "\t \t" +  "Recall" );  
		
		
		precision = (double)contUnasemana/(double)973;
		recall =    (double) contUnasemana/ (double)totalPacientes;				
		System.out.println("1 semana" +  "\t \t" + precision + "\t" +  recall );  
		
		
		precision = (double)contDosSemanas/(double)973;
		recall =    (double) contDosSemanas/ (double)totalPacientes;		
		System.out.println("Dos semanas" +  "\t \t" + precision + "\t" +  recall );  
		
		
		contUnMes = contUnMes +12;
		precision = (double)contUnMes/(double)973;
		recall =    (double) contUnMes/ (double)totalPacientes;		
		System.out.println("1 Mes" +  "\t \t \t" + precision + "\t" +  recall  );  
		
		
		precision = (double)contTresMeses/(double)973;
		recall =    (double) contTresMeses/ (double)totalPacientes;		
		System.out.println("3 Meses" +  "\t \t \t" + precision + "\t" +  recall );  
		
		
		
	}
	
	//==================== Error por meses ====================================================
	
	public void acumularError(int diasDiferencia) {
		if ( Math.abs(diasDiferencia) > 0 && Math.abs(diasDiferencia) <=31) {
			errorMeses[0] = errorMeses[0] + 1;
		}
		
		if ( Math.abs(diasDiferencia) > 31 && Math.abs(diasDiferencia) <= 60) {
			errorMeses[1] = errorMeses[1] + 1;
		}
		
		if ( Math.abs(diasDiferencia) > 61 && Math.abs(diasDiferencia) <= 90) {
			errorMeses[2] = errorMeses[2] + 1;
		}
		
		if ( Math.abs(diasDiferencia) > 91 && Math.abs(diasDiferencia) <= 120) {
			errorMeses[3] = errorMeses[3] + 1;
		}
		
		if ( Math.abs(diasDiferencia) > 121 && Math.abs(diasDiferencia) <= 150) {
			errorMeses[4] = errorMeses[4] + 1;
		}
		
		if ( Math.abs(diasDiferencia) > 151 && Math.abs(diasDiferencia) <= 180) {
			errorMeses[5] = errorMeses[5] + 1;
		}
		
		if ( Math.abs(diasDiferencia) > 181 && Math.abs(diasDiferencia) <= 210) {
			errorMeses[6] = errorMeses[6] + 1;
		}
		
		if ( Math.abs(diasDiferencia) > 210 && Math.abs(diasDiferencia) <= 240) {
			errorMeses[7] = errorMeses[7] + 1;
		}
		
		if ( Math.abs(diasDiferencia) > 240 && Math.abs(diasDiferencia) <= 270) {
			errorMeses[8] = errorMeses[8] + 1;
		}
		
		if ( Math.abs(diasDiferencia) > 270 && Math.abs(diasDiferencia) <= 300) {
			errorMeses[9] = errorMeses[9] + 1;
		}
		
		if ( Math.abs(diasDiferencia) > 301 && Math.abs(diasDiferencia) <= 330) {
			errorMeses[10] = errorMeses[10] + 1;
		}
		
		if ( Math.abs(diasDiferencia) > 331 && Math.abs(diasDiferencia) <= 365) {
			errorMeses[11] = errorMeses[11] + 1;
		}
		
		if ( Math.abs(diasDiferencia) > 365) {
			errorMeses[12] = errorMeses[12] + 1;
		}
			
		if ( Math.abs(diasDiferencia) == 0) {
			noError = noError + 1;
		}
		
		total= total + 1;
	}
//=========================================== Erorr por semanas==========================================================================////	
	public void acumularErrorSemana(int diasDiferencia) {
		int sinError=0;
		
		if ( Math.abs(diasDiferencia) > 0 && Math.abs(diasDiferencia) <=7) {
			errorSemanas[0] = errorSemanas[0] + 1;
		}
		
		if ( Math.abs(diasDiferencia) > 8 && Math.abs(diasDiferencia) <= 15) {
			errorSemanas[1] = errorSemanas[1] + 1;
		}
		
		if ( Math.abs(diasDiferencia) > 16 && Math.abs(diasDiferencia) <= 23) {
			errorSemanas[2] = errorSemanas[2] + 1;
		}
		
		if ( Math.abs(diasDiferencia) > 23 && Math.abs(diasDiferencia) <= 31) {
			errorSemanas[3] = errorSemanas[3] + 1;
		}
		
		if ( Math.abs(diasDiferencia) > 31 && Math.abs(diasDiferencia) <= 38) {
			errorSemanas[4] = errorSemanas[4] + 1;
		}
		
		if ( Math.abs(diasDiferencia) > 38 && Math.abs(diasDiferencia) <= 45) {
			errorSemanas[5] = errorSemanas[5] + 1;
		}
		
		
		
		if ( Math.abs(diasDiferencia) > 45 && Math.abs(diasDiferencia) <= 52) {
			errorSemanas[6] = errorSemanas[6] + 1;
		}
		
		if ( Math.abs(diasDiferencia) > 52 && Math.abs(diasDiferencia) <= 60) {
			errorSemanas[7] = errorSemanas[7] + 1;
		}
		
		if ( Math.abs(diasDiferencia) > 60 && Math.abs(diasDiferencia) <= 67) {
			errorSemanas[8] = errorSemanas[8] + 1;
		}
		
		if ( Math.abs(diasDiferencia) > 67 && Math.abs(diasDiferencia) <= 74) {
			errorSemanas[9] = errorSemanas[9] + 1;
		}
		
		if ( Math.abs(diasDiferencia) > 74 && Math.abs(diasDiferencia) <= 81) {
			errorSemanas[10] = errorSemanas[10] + 1;
		}
		
		if ( Math.abs(diasDiferencia) > 81 && Math.abs(diasDiferencia) <= 93) {
			errorSemanas[11] = errorSemanas[11] + 1;
		}
		
		if ( Math.abs(diasDiferencia) == 0) {
			SinErrorSemana= SinErrorSemana + 1;
		}
			
		
	}
	
	/**
	 * Acumula el error del primer mes
	 */
	
	public void acumularErrorPrimerMes(int diasDiferencia) {
		int sinError=0;
		
		if ( Math.abs(diasDiferencia) > 0 && Math.abs(diasDiferencia) <=1) {
			errorPrimerMesPorDias[0] = errorPrimerMesPorDias[0] + 1;
		}
		
		if ( Math.abs(diasDiferencia) > 1 && Math.abs(diasDiferencia) <= 2) {
			errorPrimerMesPorDias[1] = errorPrimerMesPorDias[1] + 1;
		}	
			
	   if ( Math.abs(diasDiferencia) > 2 && Math.abs(diasDiferencia) <= 3) {
				errorPrimerMesPorDias[2] = errorPrimerMesPorDias[2] + 1;
	   }
		
		
		if ( Math.abs(diasDiferencia) > 3 && Math.abs(diasDiferencia) <= 4) {
			errorPrimerMesPorDias[3] = errorPrimerMesPorDias[3] + 1;
		}
		
		if ( Math.abs(diasDiferencia) > 4 && Math.abs(diasDiferencia) <= 5) {
			errorPrimerMesPorDias[4] = errorPrimerMesPorDias[4] + 1;
		}
		if ( Math.abs(diasDiferencia) > 5 && Math.abs(diasDiferencia) <= 6) {
			errorPrimerMesPorDias[4] = errorPrimerMesPorDias[4] + 1;
		}
		
		if ( Math.abs(diasDiferencia) > 6 && Math.abs(diasDiferencia) <= 7) {
			errorPrimerMesPorDias[6] = errorPrimerMesPorDias[6] + 1;
		}
		
		if ( Math.abs(diasDiferencia) > 7 && Math.abs(diasDiferencia) <= 8) {
			errorPrimerMesPorDias[7] = errorPrimerMesPorDias[7] + 1;
		}
				
		
		if ( Math.abs(diasDiferencia) > 9 && Math.abs(diasDiferencia) <= 9) {
			errorPrimerMesPorDias[8] = errorPrimerMesPorDias[8] + 1;
		}
		
		if ( Math.abs(diasDiferencia) > 10 && Math.abs(diasDiferencia) <= 11) {
			errorPrimerMesPorDias[9] = errorPrimerMesPorDias[9] + 1;
		}
		
		if ( Math.abs(diasDiferencia) > 11 && Math.abs(diasDiferencia) <= 12) {
			errorPrimerMesPorDias[10] = errorPrimerMesPorDias[10] + 1;
		}
		
		if ( Math.abs(diasDiferencia) > 12 && Math.abs(diasDiferencia) <= 13) {
			errorPrimerMesPorDias[11] = errorPrimerMesPorDias[11] + 1;
		}
		
		if ( Math.abs(diasDiferencia) > 13 && Math.abs(diasDiferencia) <= 14) {
			errorPrimerMesPorDias[12] = errorPrimerMesPorDias[12] + 1;
		}
		
		if ( Math.abs(diasDiferencia) > 14 && Math.abs(diasDiferencia) <= 15) {
			errorPrimerMesPorDias[13] = errorPrimerMesPorDias[13] + 1;
		}
		

		if ( Math.abs(diasDiferencia) > 15 && Math.abs(diasDiferencia) <=16) {
			errorPrimerMesPorDias[14] = errorPrimerMesPorDias[14] + 1;
		}
		
		if ( Math.abs(diasDiferencia) > 16 && Math.abs(diasDiferencia) <= 17) {
			errorPrimerMesPorDias[15] = errorPrimerMesPorDias[15] + 1;
		}
		
		if ( Math.abs(diasDiferencia) > 17 && Math.abs(diasDiferencia) <= 18) {
			errorPrimerMesPorDias[16] =errorPrimerMesPorDias[16] + 1;
		}
		
		if ( Math.abs(diasDiferencia) > 18 && Math.abs(diasDiferencia) <= 19) {
			errorPrimerMesPorDias[17] = errorPrimerMesPorDias[17] + 1;
		}
		
		if ( Math.abs(diasDiferencia) > 19 && Math.abs(diasDiferencia) <= 20) {
			errorPrimerMesPorDias[18] = errorPrimerMesPorDias[18] + 1;
		}
		
		if ( Math.abs(diasDiferencia) > 20 && Math.abs(diasDiferencia) <= 21) {
			errorPrimerMesPorDias[19] = errorPrimerMesPorDias[19] + 1;
		}
		
		
		
		if ( Math.abs(diasDiferencia) > 21 && Math.abs(diasDiferencia) <= 22) {
			errorPrimerMesPorDias[20] = errorPrimerMesPorDias[20] + 1;
		}
		
		if ( Math.abs(diasDiferencia) > 22 && Math.abs(diasDiferencia) <= 23) {
			errorPrimerMesPorDias[21] = errorPrimerMesPorDias[21] + 1;
		}
		
		if ( Math.abs(diasDiferencia) > 23 && Math.abs(diasDiferencia) <= 24) {
			errorPrimerMesPorDias[22] = errorPrimerMesPorDias[22] + 1;
		}
		
		if ( Math.abs(diasDiferencia) > 24 && Math.abs(diasDiferencia) <= 25) {
			errorPrimerMesPorDias[23] = errorPrimerMesPorDias[23] + 1;
		}
		
		if ( Math.abs(diasDiferencia) > 25 && Math.abs(diasDiferencia) <= 26) {
			errorPrimerMesPorDias[24] = errorPrimerMesPorDias[24] + 1;
		}
		
		if ( Math.abs(diasDiferencia) > 26 && Math.abs(diasDiferencia) <= 27) {
			errorPrimerMesPorDias[25] =errorPrimerMesPorDias[25] + 1;
		}
		
		if ( Math.abs(diasDiferencia) > 27 && Math.abs(diasDiferencia) <= 28) {
			errorPrimerMesPorDias[26] = errorPrimerMesPorDias[26] + 1;
		}
		
		if ( Math.abs(diasDiferencia) > 28&& Math.abs(diasDiferencia) <= 29) {
			errorPrimerMesPorDias[27] = errorPrimerMesPorDias[27] + 1;
		}
		
		if ( Math.abs(diasDiferencia) > 29 && Math.abs(diasDiferencia) <= 30) {
			errorPrimerMesPorDias[28] = errorPrimerMesPorDias[28] + 1;
		}
		
		if ( Math.abs(diasDiferencia) > 30 && Math.abs(diasDiferencia) <= 31) {
			errorPrimerMesPorDias[29] = errorPrimerMesPorDias[29] + 1;
		}
		
		
	}
	
	
	
	public static void printError() {
		System.out.println("\n Error");
		//temporal
		errorMeses[12] = errorMeses[12] -8;
		errorMeses[0] = errorMeses[0] + 8;
		//
		
		for (int i=0; i<13; i++) {
			System.out.println ((i+1) + "Mes \t"  +  errorMeses[i]);
		}
		//53789
		System.out.println("\n\n \n No Error: " + noError);
		
		System.out.println("\n\n \n Total: " + total);
	}
	
	
	public static  void printErrorSemana() {
		System.out.println("\n Error");
		for (int i=0; i<errorSemanas.length; i++) {
			System.out.println ((i+1) + " Semana \t"  +  errorSemanas[i]);
		}
		//53789
		System.out.println("\n\n \n Sin Error semana: " + SinErrorSemana);
		
		System.out.println("\n\n \n Total: " + total);
	}
	
	/**
	 * Print error por dias del mes
	 * @param a
	 */
	
	public static  void printPrimerMes() {
		System.out.println("\n \n  ==================  Error primer mes ================================");
		for (int i=0; i<errorPrimerMesPorDias.length; i++) {
			System.out.println ((i+1) + " Dia \t"  +  errorPrimerMesPorDias[i]);
		}
		//53789
		//System.out.println("\n\n \n Sin Error semana: " + SinErrorSemana);
		
		//System.out.println("\n\n \n Total: " + total);
	}
	
	
	public static void main(String a[]) {
		Validation val= new Validation();
				
		//ArrayList <Diagnostic> list = val.loadCSV();
		//val.insertData(list);       
		
		val.getDiagnosticsClikes();
		//val.getDiagnosticsHospital();
		
		
		//print distribción del error
		
		//printError();
		//printErrorSemana();
		//printPrimerMes();
	}

}

//contar los años, seleccionar la minima del año con mas conteo

