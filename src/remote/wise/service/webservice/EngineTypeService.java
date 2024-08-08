package remote.wise.service.webservice;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;


import remote.wise.EAintegration.handler.ErrorMessageHandler;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.EngineTypeImpl;
//import remote.wise.util.WiseLogger;

/**  Webservice to obtain the Engine type details from EA system
 * @author Rajani Nagaraju
 *
 */
@WebService(name = "EngineTypeService")
public class EngineTypeService 
{
	
	/** Web Method to capture Engine Type details received from EA system
	 * @param engineTypeName Engine Type Name
	 * @param engineTypeCode Engine Type Code
	 * @param messageId
	 * @param fileRef
	 * @param process
	 * @param reprocessJobCode
	 * @return
	 */
	@WebMethod(operationName = "setEngineTypeDetails", action = "setEngineTypeDetails")
	public String setEngineDetails(@WebParam(name="engineTypeName" ) String engineTypeName, 
			 			  @WebParam(name="engineTypeCode" ) String engineTypeCode,
			 			  @WebParam(name="messageId" ) String messageId,
			 			  @WebParam(name="fileRef" ) String fileRef,
			 			  @WebParam(name="process" ) String process,
			 			  @WebParam(name="reprocessJobCode" ) String reprocessJobCode)
	{
		String response = "SUCCESS";
		
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("EngineTypeService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		
		//Logger infoLogger = Logger.getLogger("infoLogger"); 
		iLogger.info("---- Actual Webservice Input ------");
		iLogger.info("Engine Type Name:"+engineTypeName+",  "+"Engine Type Code:"+engineTypeCode +
				",  "+"Message Id:"+messageId+",  "+"File Reference:"+fileRef+
				",  "+"Process:"+process+",  "+"Reprocess JobCode:"+reprocessJobCode);
		
		long startTime = System.currentTimeMillis();
		response = new EngineTypeImpl().setEngineType(engineTypeName,engineTypeCode);
		long endTime = System.currentTimeMillis();
		iLogger.info("serviceName:EngineTypeService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		
		iLogger.info("----- Actual Webservice Output-----");
		iLogger.info("Status:"+response);
		
		//DF20140715 - Rajani Nagaraju -  Robust Logging for EA File Processing 
				String faultCause =null;
				if(response.split("-").length>1)
				{
					faultCause=response.split("-")[1];
					response = response.split("-")[0].trim();
							
				}
				
		//If Failure in insertion, put the message to fault_details table
		if(response.equalsIgnoreCase("FAILURE"))
		{
			if(engineTypeName==null)
				engineTypeName="";
			if(engineTypeCode==null)
				engineTypeCode="";
			String messageString = engineTypeName+"|"+engineTypeCode;
			ErrorMessageHandler errorHandler = new ErrorMessageHandler();
			errorHandler.handleErrorMessages(messageId, messageString, fileRef, process, reprocessJobCode,faultCause);
		}
		
		return response;
		
	}
}
