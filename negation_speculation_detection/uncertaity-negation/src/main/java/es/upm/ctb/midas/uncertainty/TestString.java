package es.upm.ctb.midas.uncertainty;

public class TestString {
	public static void main(String a[]) {
		String sentence = "Paciente con ,concronico ,con  que reagudizado. se reañiza un tratamiento depletivo suave con lo que no desaparece la disnea y normaliza el BNP.";
		int x= sentence.lastIndexOf(", con");		
		System.out.println(x);
	}
}
