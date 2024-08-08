package remote.wise.client.MachineCommReportService;


public class ClientSample {

	public static void main(String[] args) {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        MachineCommReportServiceService service1 = new MachineCommReportServiceService();
	        System.out.println("Create Web Service...");
	        MachineCommReportService port1 = service1.getMachineCommReportServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.getMachineCommReport());
	        System.out.println("Create Web Service...");
	        MachineCommReportService port2 = service1.getMachineCommReportServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.getMachineCommReport());
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
