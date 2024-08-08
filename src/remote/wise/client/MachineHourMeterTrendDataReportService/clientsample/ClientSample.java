package remote.wise.client.MachineHourMeterTrendDataReportService.clientsample;

import remote.wise.client.MachineHourMeterTrendDataReportService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        MachineHourMeterTrendDataReportServiceService service1 = new MachineHourMeterTrendDataReportServiceService();
	        System.out.println("Create Web Service...");
	        MachineHourMeterTrendDataReportService port1 = service1.getMachineHourMeterTrendDataReportServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.getMachineTrendDataForAllZones(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Server said: " + port1.getMachineHourMeterTrendData(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        MachineHourMeterTrendDataReportService port2 = service1.getMachineHourMeterTrendDataReportServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.getMachineTrendDataForAllZones(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Server said: " + port2.getMachineHourMeterTrendData(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
