package remote.wise.service.webservice;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;

import remote.wise.EAintegration.handler.ErrorMessageHandler;
import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.ZonalInformationReqContract;
import remote.wise.service.implementation.ZonalInformationImpl;
//import remote.wise.util.WiseLogger;

@WebService(name = "ZonalInformationService")
public class ZonalInformationService {
	//WiseLogger infoLogger = WiseLogger.getLogger("ZonalInformationService:","info");
	Logger iLogger = InfoLoggerClass.logger;
	@WebMethod(operationName = "setZonalDetails", action = "setZonalDetails")
	public String setZonalDetails(@WebParam(name="zonalName") String zonalName,
								  @WebParam(name="zonalCode") String zonalCode,
								  @WebParam(name="messageId") String messageId,
					 			  @WebParam(name="fileRef") String fileRef,
					 			  @WebParam(name="process") String process,
					 			  @WebParam(name="reprocessJobCode") String reprocessJobCode) throws CustomFault, ParseException, IOException{
		String response;
		
		iLogger.info("EA Processing: ZonalInformation: "+messageId+": ---- Webservice Input ------");
		iLogger.info("EA Processing: ZonalInformation: "+messageId+": zonal name :"+zonalName+", zonal code :"+zonalCode+","+"Message Id:"+messageId+",  "+"File Reference:"+fileRef+
				",  "+"Process:"+process+",  "+"Reprocess JobCode:"+reprocessJobCode);
		
		long startTime = System.currentTimeMillis();
		
		response = new ZonalInformationImpl().setZonalInformations(zonalName,zonalCode,messageId);
		
		long endTime = System.currentTimeMillis();
		iLogger.info("EA Processing: ZonalInformation: "+messageId+": Webservice Execution Time in ms:"+(endTime-startTime));
		iLogger.info("EA Processing: ZonalInformation: "+messageId+": ----- Webservice Output-----");
		iLogger.info("EA Processing: ZonalInformation: "+messageId+": Status:"+response);
		
		//DF20140715 - Rajani Nagaraju -  Robust Logging for EA File Processing 
		String faultCause =null;
		if(response.split("-").length>1)
		{
			faultCause=response.split("-")[1];
			response = response.split("-")[0].trim();
					
		}
		
		if(response.equalsIgnoreCase("FAILURE")){
			if( zonalName == null || zonalName.equals("") || (!(zonalName.replaceAll("\\s","").length()>0)) ){
				zonalName="";
			}				
			if( zonalCode == null || zonalCode.equals("") || (!(zonalCode.replaceAll("\\s","").length()>0)) ){
				zonalCode="";
			}
			String messageString = zonalName+"|"+zonalCode;

			ErrorMessageHandler errorHandler = new ErrorMessageHandler();
			errorHandler.handleErrorMessages(messageId, messageString, fileRef, process, reprocessJobCode,faultCause);
		}		
		
		
		iLogger.info("serviceName:ZonalInformationService~executionTime:"+(endTime-startTime)+"~"+""+"~"+response);
		return response;
	}
}
