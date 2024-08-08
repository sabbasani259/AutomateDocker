/**
 * JCB6266 : 20221111 : Dhiraj K : logging and Fault table update issue 
 */
package remote.wise.service.webservice;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.apache.logging.log4j.Logger;
import remote.wise.EAintegration.handler.ErrorMessageHandler;
import remote.wise.log.InfoLogging.InfoLoggerClass;

import remote.wise.service.implementation.ServiceHistoryImpl;

@Path("/setServiceDetails")
public class ServiceHistoryDetailsRESTService {

	@GET
	//DF20190423:IM20018382-Adding the additonal field jobCardDetails
	//DF20200128:aj20119610-Adding the additonal field callTypeId
	//DF20200204:aj20119610-Adding the additional field jobCardDetails in the @path
	@Path("{serialNumber}/{dealerCode}/{jobCardNumber}/{dbmsPartCode}/{servicedDate}/{jobCardDetails}/{callTypeId}")
	@Produces(MediaType.TEXT_PLAIN)
	public String setServiceHistoryDetails(@PathParam("serialNumber")String serialNumber, 
			@PathParam("dealerCode")String dealerCode, 
			@PathParam("jobCardNumber")String jobCardNumber, 
			@PathParam("dbmsPartCode")String dbmsPartCode,
			@PathParam("servicedDate")String servicedDate,
			@PathParam("jobCardDetails")String jobCardDetails,
			@PathParam("callTypeId")String callTypeId,
			@DefaultValue(" ") @QueryParam("messageId")String messageId,
			@DefaultValue(" ") @QueryParam("fileRef")String fileRef, 
			@DefaultValue(" ") @QueryParam("process")String process,
			@DefaultValue(" ") @QueryParam("reprocessJobCode")String reprocessJobCode
//			@DefaultValue("NA") @QueryParam("jobCardDetails") String jobCardDetails
			){
		//DF20190423:IM20018382-Adding the additonal field jobCardDetails
		String responseStatus = "SUCCESS-Record Processed";
		Logger infoLogger = InfoLoggerClass.logger;
		infoLogger.info("EA Processing: AssetServiceDetails: "+messageId+": ---- Webservice Input ------");
		infoLogger.info("EA Processing: AssetServiceDetails: "+messageId+": Serial Number:"+serialNumber+",  "+"DealerCode:"+dealerCode+",  "+"JobCardNumber:"+jobCardNumber+
				",  "+"Dbms PartCode:"+dbmsPartCode+", "+"call type id:"+callTypeId+",  "+"Serviced Date:"+servicedDate+",  "+"Job Card Details:"+jobCardDetails);
		long startTime = System.currentTimeMillis();
		//System.out.println("Going to IMPL");//JCB6266.o
		//infoLogger.info("Going to IMPL");//JCB6266.o

		//Remove quote(') if present in jobCardDetails
		jobCardDetails = jobCardDetails.replace("'", "");//JCB6266.n
		
		responseStatus= new ServiceHistoryImpl().setServiceHistoryDetails(serialNumber,dealerCode,jobCardNumber,dbmsPartCode,servicedDate, jobCardDetails,callTypeId, messageId);
		long endTime = System.currentTimeMillis();
		infoLogger.info("EA Processing: AssetServiceDetails: "+messageId+": Webservice Execution Time in ms:"+(endTime-startTime));
		infoLogger.info("EA Processing: AssetServiceDetails: "+messageId+": ----- Webservice Output-----");
		infoLogger.info("EA Processing: AssetServiceDetails: "+messageId+": Status: "+responseStatus);

		//DF20140715 - Rajani Nagaraju -  Robust Logging for EA File Processing 
		String faultCause =null;
		if(responseStatus.split("-").length>1){
			faultCause=responseStatus.split("-")[1];
			responseStatus = responseStatus.split("-")[0].trim();

		}
		//If Failure in insertion, put the message to fault_details table
		if(responseStatus.equalsIgnoreCase("FAILURE")){
			if(serialNumber==null)
				serialNumber="";
			if(dealerCode==null)
				dealerCode="";
			if(jobCardNumber==null)
				jobCardNumber="";
			if(dbmsPartCode==null)
				dbmsPartCode="";
			if(servicedDate==null)
				servicedDate="";
			//DF20190423:IM20018382-Null check for additonal field jobCardDetails
			if(jobCardDetails==null)
				jobCardDetails="";
			//DF20200131:aj201009610-Null check for additional field callTypeId
			if(callTypeId==null)
				callTypeId="";
			//System.out.println("Failure in insertion because some parameter is null");//JCB6266.o
			//infoLogger.info("Failure in insertion because some parameter is null");//JCB6266.o
			//DF20200131:aj201009610-Reshuffle field callTypeId to messageString
			String messageString = serialNumber+"|"+dealerCode+"|"+jobCardNumber+"|"+dbmsPartCode+"|"+servicedDate+"|"+jobCardDetails+"|"+callTypeId;
			infoLogger.info("Message String : " + messageString +  ":: Response status :: " + responseStatus);//JCB6266.n
			ErrorMessageHandler errorHandler = new ErrorMessageHandler();
			errorHandler.handleErrorMessages(messageId, messageString, fileRef, process, reprocessJobCode,faultCause);
		}
		return responseStatus;
	}
}
