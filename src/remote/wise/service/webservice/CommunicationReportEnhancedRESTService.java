package remote.wise.service.webservice;

import java.io.FileNotFoundException;

import javax.net.ssl.SSLHandshakeException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.CommunicationReportEnhancedServiceImpl;

//This service is to fetch the address of the all the machines related to JCB using googleAPI and
//put them in the communication report
/**
 * @author Ajay Kumar
 * 
 */
@Path("/CommunicationReportEnhancedRESTService")
public class CommunicationReportEnhancedRESTService {

	String resultString, result;
	boolean forAllVins;
	Logger iLogger = InfoLoggerClass.logger;
	int attemptForNullCount;
	boolean recordsStillNull;
	int machineProcessedCount;
	String vins;
	String updateFlag;
	int countOfAPICall;

	@GET
	@Path("/updateCommAddressOfMachines")
	@Produces(MediaType.TEXT_PLAIN)
	public String updateCommAddressOfMachines(@QueryParam("updateFlagForAllVins") String updateFlagForAllVins,
			@QueryParam("Serial_Numbers") String Serial_Numbers) throws FileNotFoundException {
		machineProcessedCount = 0;
		resultString = null;
		String totalMachineProcessed = "";
		long startTime = System.currentTimeMillis();
		forAllVins = true;
		vins = null;
		boolean isVinExists = false;
		updateFlag=updateFlagForAllVins;
		
		if (Serial_Numbers != null) {
			forAllVins = false;
			if (Serial_Numbers.isEmpty())
				forAllVins = true;
			else {
				vins = CommunicationReportEnhancedServiceImpl
						.getFormatedVinsToQuery(Serial_Numbers.split(","));
				isVinExists = CommunicationReportEnhancedServiceImpl
						.checkIfVinExists(vins);
			}
		}

		
		try{
		if (isVinExists) 
	   {
			// This code block is only for the vins passed as arguments manually.
			resultString = CommunicationReportEnhancedServiceImpl
					.updateCommAddressOfVins(forAllVins, vins,
							 machineProcessedCount);
			manipulatingResponse();
		} else
			//This block will take care for all machines.
			updateRecords();
		
		}catch(SSLHandshakeException e){
			iLogger.info("updateCommAddressOfMachines()"+e.getMessage());
		}catch(Exception e){
			iLogger.info("updateCommAddressOfMachines()"+e.getMessage());
		}
		long endTime = System.currentTimeMillis();
		totalMachineProcessed = machineProcessedCount + "";
		System.out.println("Time taken : " + (endTime - startTime) + " for "
				+ totalMachineProcessed + " machines");
		iLogger.info("Time taken : " + (endTime - startTime) + " to process "
				+ totalMachineProcessed + " machines"+" for noOfAPI calls : "+countOfAPICall);
		return result;
	}

	private void updateRecords() throws SSLHandshakeException {
	//---------------------------------------------------------------------------------------------	
		if(updateFlag.equals("1")){
		//update address from util report
		resultString = CommunicationReportEnhancedServiceImpl.updateCommAddressOfVinsFromUtilReport();
		manipulatingResponse();
		
		//update address for remaining vins
			resultString = CommunicationReportEnhancedServiceImpl
					.updateCommAddressOfVins(forAllVins, vins,
							 machineProcessedCount);
		manipulatingResponse();
		}
	//------------------------------------------------------------------------------------------------	
		if(updateFlag.equals("2")){
			resultString = CommunicationReportEnhancedServiceImpl
					.updateCommAddressOfVins(forAllVins, vins,
							 machineProcessedCount);
		manipulatingResponse();
		}
		//------------------------------------------------------------------------------------------------		
		if(updateFlag.equals("0")){
			resultString = CommunicationReportEnhancedServiceImpl.updateCommAddressOfVinsFromUtilReport();
			manipulatingResponse();
		}
		//------------------------------------------------------------------------------------------------	
	}

	public void manipulatingResponse() {
		if (resultString != null) {
			if (resultString.split(":").length >= 2){
				machineProcessedCount += Integer.parseInt(resultString
						.split(":")[1]);
				if(resultString.split(":").length ==3)
				countOfAPICall += Integer.parseInt(resultString
						.split(":")[2]);
				}
			
			iLogger.info("machines processed :"+machineProcessedCount);
		}
	}
	
	/*public static void main(String[] args) throws FileNotFoundException {
		new CommunicationReportEnhancedRESTService().updateCommAddressOfMachines("0", null);
	}*/
}
