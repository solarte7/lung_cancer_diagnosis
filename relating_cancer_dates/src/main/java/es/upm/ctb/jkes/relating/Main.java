package es.upm.ctb.jkes.relating;
import es.upm.ctb.jkes.relating.databases.*;

public class Main {
	LinkingDatesToCancer linker;
	public void Main() {
		
	}
	public void init() {
		linker = new LinkingDatesToCancer();
		linker.loadUDPipe();
		linker.loadDataBase();
		//System.out.println("ok");
	}
	public void process() {
		linker.linking();
	}
	
	public static void main(String a[]) {
		Main main =new Main();
		main.init();
		main.process();
		
		
	}
}
