package es.upm.ctb.midas.annotator.validation;





import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.csv.CSVParser;



public class ValidationTemp {
	static int  errorMeses[] = new int[60] ;
	static int errorSemanas[] = new int [12];
	static int noError=0;
	static int SinErrorSemana=0;
	static int total=0;
	static int contMitadMes=0;
	
	ArrayList<String> errorMas30= new ArrayList<String>();
	static int errorPorDia[] = new int [60];
	
	
		
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
		
		int totalPacientes=1016;
		
		
		
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
					
					//========================== temp //=======================
					
					
					
				}//end else
				
				
            
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
		recall =    (double) contUnasemana/ (double)totalPacientes;				
		System.out.println("1 semana" +  "\t \t" + precision + "\t" +  recall );  
		
		
		precision = (double)contDosSemanas/(double)978;
		recall =    (double) contDosSemanas/ (double)totalPacientes;		
		System.out.println("Dos semanas" +  "\t \t" + precision + "\t" +  recall );  
		
		
		
		precision = (double)contUnMes/(double)978;
		recall =    (double) contUnMes/ (double)totalPacientes;		
		System.out.println("1 Mes" +  "\t \t \t" + precision + "\t" +  recall );  
		
		
		precision = (double)contTresMeses/(double)978;
		recall =    (double) contTresMeses/ (double)totalPacientes;		
		System.out.println("3 Meses" +  "\t \t \t" + precision + "\t" +  recall );  
		
		printError();
		
	}
	
	public void getDiagnosticsClikes(String tableName) {
		MySQLValidation database = new MySQLValidation();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		int nulos=0;
		int contUnasemana =0;
		int contDosSemanas=0;
		int contUnMes =0;
		int contTresMeses =0;
		
		int totalPacientes=1028;
		
		
		
		System.out.println("Consultando datos ...");
		//HashMap<Integer, String> mapHospital = database.getDianosticHospital(); getDianosticReal
		HashMap<Integer, String> mapAnnotManual = database.getDianosticReal();
		HashMap<Integer, Date> mapClikes = database.getDianosticDatesOsw(tableName);
		
		System.out.println("\nImprimendo ...\n");
		System.out.println("ehr" +  "\t " + "Manual" + "\t" +  "clikes" + "\t \t" + "diferencia" + "\t" +  "diferencia");  
		
		
		int i=0;
		for (int ehr: mapClikes.keySet()){
			 try {
			i++;
            String stringAnnotManual = mapAnnotManual.get(ehr);  
            Date dateClikes = mapClikes.get(ehr);
            String dayC = dateClikes.toString().split("-")[2];
            
            //System.out.println("cik " + dateClikes+ " day -->" + dayC);
         	
            int dias1 =0;
			int dias2 =0;
            
			if(dateClikes == null) {
					nulos ++;
					
			}
			else {
					
					if(stringAnnotManual!= null) {
						
						Date dateAnntManual = dateFormat.parse(stringAnnotManual);
						//Date dateClikes = dateFormat.parse(stringClikes);
						
						dias1=(int) ((dateAnntManual.getTime()-dateClikes.getTime())/86400000);
						dias2=(int) ((dateClikes.getTime()-dateAnntManual.getTime())/86400000);
						System.out.println(i + "\t" + ehr +  "\t" + stringAnnotManual + "\t" +  dateClikes + "\t"  + dias1 +  "\t" + dias2); 
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
						acumularErrorPorDia(dias1); 
						
						
						// solo temporal
						if (Math.abs(dias1) > 30) {
							errorMas30.add(ehr + "	" + Math.abs(dias1));
						}
					}
				}
			
			  if(dayC.equals("15") && (Math.abs(dias2) !=0 && (Math.abs(dias2)<= 31)) ) {
				  contMitadMes++;
				  System.out.print("\n" + i + " Mitad " + contMitadMes +  "\t " + dateClikes + "\t \n\n");
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
		
		if ( Math.abs(diasDiferencia) > 331 && Math.abs(diasDiferencia) <= 360) {
			errorMeses[11] = errorMeses[11] + 1;
		}
		
		if ( Math.abs(diasDiferencia) > 360) {
			errorMeses[12] = errorMeses[12] + 1;
		}
			
		if ( Math.abs(diasDiferencia) == 0 ) {
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
	
	
	//=========================================================================================================
	
	public void acumularErrorPorDia(int diasDiferencia) {
		switch(diasDiferencia) {
		case 1:
			errorPorDia[0]= errorPorDia[0]+ 1;
			break;
		
		case 2:
			errorPorDia[1]= errorPorDia[1]+ 1;
			break;
			
		case 3:
			errorPorDia[2]= errorPorDia[2]+ 1;
			break;
			
		case 4:
			errorPorDia[3]= errorPorDia[3]+ 1;
			break;
			
		case 5:
			errorPorDia[4]= errorPorDia[4]+ 1;
			break;
			
			
		case 6:
			errorPorDia[5]= errorPorDia[5]+ 1;
			break;
		
		
		case 7:
			errorPorDia[6]= errorPorDia[6]+ 1;
			break;
			
		case 8:
			errorPorDia[7]= errorPorDia[7]+ 1;
			break;
			
		case 9:
			errorPorDia[8]= errorPorDia[8]+ 1;
			break;
			
		case 10:
			errorPorDia[9]= errorPorDia[9]+ 1;
			break;
			
			
			
			
		//---------------
			
		case 11:
			errorPorDia[10]= errorPorDia[10]+ 1;
			break;
		
		
		case 12:
			errorPorDia[11]= errorPorDia[11]+ 1;
			break;
			
		case 13:
			errorPorDia[12]= errorPorDia[12]+ 1;
			break;
			
		case 14:
			errorPorDia[13]= errorPorDia[13]+ 1;
			break;
			
		case 15:
			errorPorDia[14]= errorPorDia[14]+ 1;
			break;
			
			
		case 16:
			errorPorDia[15]= errorPorDia[15]+ 1;
			break;
		
		
		case 17:
			errorPorDia[16]= errorPorDia[16]+ 1;
			break;
			
		case 18:
			errorPorDia[17]= errorPorDia[17]+ 1;
			break;
			
		case 19:
			errorPorDia[18]= errorPorDia[18]+ 1;
			break;
			
		case 20:
			errorPorDia[19]= errorPorDia[19]+ 1;
			break;
			
			
		//------------------
		
	
			
		//---------------
				
		case 21:
				errorPorDia[20]= errorPorDia[20]+ 1;
				break;
			
			
		case 22:
			errorPorDia[21]= errorPorDia[21]+ 1;
			break;
				
		case 23:
				errorPorDia[22]= errorPorDia[22]+ 1;
				break;
				
		case 24:
				errorPorDia[23]= errorPorDia[23]+ 1;
				break;
				
		case 25:
				errorPorDia[24]= errorPorDia[24]+ 1;
				break;
				
				
		case 26:
				errorPorDia[25]= errorPorDia[25]+ 1;
				break;
			
			
		case 27:
			errorPorDia[26]= errorPorDia[26]+ 1;
			break;
				
		case 28:
				errorPorDia[27]= errorPorDia[27]+ 1;
				break;
				
		case 29:
				errorPorDia[28]= errorPorDia[28]+ 1;
				break;
				
		case 30:
				errorPorDia[29]= errorPorDia[29]+ 1;
				break;
				
		////////////////
				
		case 31:
			errorPorDia[30]= errorPorDia[30]+ 1;
			break; 
		
		case 32:
			errorPorDia[31]= errorPorDia[31]+ 1;
			break; 
			
	  
		case 33:
			errorPorDia[32]= errorPorDia[32]+ 1;
			break; 
		
		case 34:
			errorPorDia[32]= errorPorDia[33]+ 1;
			break; 
			
		case 35:
			errorPorDia[34]= errorPorDia[34]+ 1;
			break; 
		
		case 36:
			errorPorDia[35]= errorPorDia[35]+ 1;
			break; 
			
	  
		case 37:
			errorPorDia[36]= errorPorDia[36]+ 1;
			break; 
		
		case 38:
			errorPorDia[37]= errorPorDia[37]+ 1;
			break; 
		
			

		case 39:
			errorPorDia[38]= errorPorDia[38]+ 1;
			break; 
		
		case 40:
			errorPorDia[39]= errorPorDia[39]+ 1;
			break; 
			
		
	    //-----------------------------
			
	
		case 41:
			errorPorDia[40]= errorPorDia[40]+ 1;
			break; 
			
		
		case 42:
			errorPorDia[41]= errorPorDia[41]+ 1;
			break; 
			
		case 43:
			errorPorDia[42]= errorPorDia[42]+ 1;
			break; 
			
		case 44:
			errorPorDia[43]= errorPorDia[43]+ 1;
			break; 
			
		case 45:
			errorPorDia[44]= errorPorDia[44]+ 1;
			break; 
			
		
		case 46:
			errorPorDia[45]= errorPorDia[45]+ 1;
			break; 
			
		case 47:
			errorPorDia[46]= errorPorDia[46]+ 1;
			break; 
			
		case 48:
			errorPorDia[47]= errorPorDia[47]+ 1;
			break; 
			
		
		case 49:
			errorPorDia[48]= errorPorDia[48]+ 1;
			break; 
			
		case 50:
			errorPorDia[49]= errorPorDia[49]+ 1;
			break; 
		
			
		///---------------------
			
		case 51:
			errorPorDia[50]= errorPorDia[50]+ 1;
			break; 
			
		
		case 52:
			errorPorDia[41]= errorPorDia[51]+ 1;
			break; 
			
		case 53:
			errorPorDia[52]= errorPorDia[52]+ 1;
			break; 
			
		case 54:
			errorPorDia[53]= errorPorDia[53]+ 1;
			break; 
			
		case 55:
			errorPorDia[54]= errorPorDia[54]+ 1;
			break; 
			
		
		case 56:
			errorPorDia[55]= errorPorDia[55]+ 1;
			break; 
			
		case 57:
			errorPorDia[56]= errorPorDia[56]+ 1;
			break; 
			
		case 58:
			errorPorDia[57]= errorPorDia[57]+ 1;
			break; 
			
		
		case 59:
			errorPorDia[58]= errorPorDia[58]+ 1;
			break; 
			
		case 60:
			errorPorDia[59]= errorPorDia[59]+ 1;
			break; 
		
	}//switcch
	


	
	}
	
	public static void printError() {
		System.out.println("\n Error");
		for (int i=0; i<13; i++) {
			System.out.println ((i+1) + "Mes \t"  +  errorMeses[i]);
		}
		//53789
		System.out.println("\n\n \n No Error: " + noError);
		System.out.println("\n\n \n Total: " + total);
		System.out.println("\n\n \n Mitad Mes: " + contMitadMes);
		
		
		
		
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
	
	//imprime error por diaas
	public static void printErrorPorDia() {
		System.out.println("\n Error por dia");
		for (int i=0; i<60; i++) {
			System.out.println ((i+1) + " Dia: \t"  +  errorPorDia[i]);
		}
		//53789
		
	}
	
	
	
	//temporal
	public void printErrorMayor30() {
		System.out.println ("=============================================================");
		for (int i=0; i<errorMas30.size(); i++) {
			System.out.println (errorMas30.get(i));
		}
		System.out.println ("=============================================================");
	}
	
	//-----------------------------------------------------------------------------------------------------------
	public static void main(String a[]) {
		ValidationTemp val= new ValidationTemp();
				
		//ArrayList <Diagnostic> list = val.loadCSV();
		//val.insertData(list);  
		
		 //val.getDiagnosticsHospital();patient_firstdx0
		//val.getDiagnosticsClikes("patient_firstdx0");
			
			val.getDiagnosticsClikes("diagnosis_date");
		
			//print distribción del error
			
			printError();
			printErrorSemana();
			
			System.out.println ("\n+++++++ Error Por dia ++++++++++");
			printErrorPorDia();
			//System.out.println ("\n+++++++ Error MAyor 2 ++++++++++");
			//val.printErrorMayor30();

	   
		

		
	}

}

//contar los años, seleccionar la minima del año con mas conteo



