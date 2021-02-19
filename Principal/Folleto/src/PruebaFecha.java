import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PruebaFecha {
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("HOLA FECHA");
		
		String MES[] = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
	    //String DIA[] = {"Domingo", "Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado"};
		  // System.out.println((DIA[date.getDayOfWeek().getValue()] + " " + date.getDayOfMonth() + " "+MES[date.getMonthValue()-1]+" del " + date.getYear()));

	    LocalDate date = LocalDate.now();
	    String fechaHoy = (date.getDayOfMonth() + "/"+MES[date.getMonthValue()-1]+"/" + date.getYear());
	    System.out.println(fechaHoy);
	    
	    
	    date = date.plusYears(1);
	    String fechaDespues = (date.getDayOfMonth() + "/"+MES[date.getMonthValue()-1]+"/" + date.getYear());
	    System.out.println(fechaDespues);
	}
}