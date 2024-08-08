package remote.wise.service.webservice;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;

import remote.wise.EAintegration.handler.ErrorMessageHandler;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.DealerMappingImpl;
//import remote.wise.util.WiseLogger;

@WebService(name = "DealerMappingService")
public class DealerMappingService 
{
	@WebMethod(operationName = "setDealerMapping", action = "setDealerMapping")
	public String setDealerMapping(@WebParam(name="eccCode") String eccCode,
			                       @WebParam(name="crmCode") String crmCode,
			                       @WebParam(name="llCode") String llCode,
			                       @WebParam(name="dealerName") String dealerName,
			                       @WebParam(name="messageId" ) String messageId,
		    			 		   @WebParam(name="fileRef" ) String fileRef,
		    			 		   @WebParam(name="process" ) String process,
		    			 		   @WebParam(name="reprocessJobCode" ) String reprocessJobCode)
	{
		String response = "SUCCESS";
		
		//WiseLogger infoLogger = WiseLogger.getLogger("DealerMappingService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		iLogger.info("EA Processing: DealerMapping: "+messageId+":---- Webservice Input ------");
		iLogger.info("EA Processing: DealerMapping: "+messageId+": eccCode: "+eccCode+",  "+"crmCode: "+crmCode+",   " +
						"llCode: "+llCode+",  "+"dealerName: "+dealerName+",  messageId:"+messageId);
		
		long startTime = System.currentTimeMillis();
				
		response = new DealerMappingImpl().setDealerMappingDetails(eccCode, crmCode, llCode, dealerName, messageId);
		long endTime=System.currentTimeMillis();
		iLogger.info("EA Processing: DealerMapping: "+messageId);
		iLogger.info("serviceName:DealerMappingService~executionTime:"+(endTime-startTime)+"~"+""+"~"+response);
		
		iLogger.info("EA Processing: DealerMapping: "+messageId+": ----- Webservice Output-----");
		iLogger.info("EA Processing: DealerMapping: "+messageId+": Status:"+response);
		
		String faultCause =null;
		if(response.split("-").length>1)
		{
			faultCause=response.split("-")[1];
			response = response.split("-")[0].trim();
					
		}
		
		//If Failure in insertion, put the message to fault_details table
		if(response.equalsIgnoreCase("FAILURE"))
		{
			if(eccCode==null)
				eccCode="";
			else
				eccCode = eccCode.replaceAll("\\s","") ; 
			
			if(crmCode==null)
				crmCode="";
			else
				crmCode = crmCode.replaceAll("\\s","") ; 
			
			
			if(llCode==null)
				llCode="";
			else
				llCode = llCode.replaceAll("\\s","") ;
			
			
			if(dealerName==null)
				dealerName="";
			else
				dealerName = dealerName.replaceAll("\\s","") ;
			
			String messageString = eccCode+"|"+crmCode+"|"+llCode+"|"+dealerName;
			ErrorMessageHandler errorHandler = new ErrorMessageHandler();
			errorHandler.handleErrorMessages(messageId, messageString, fileRef, process, reprocessJobCode, faultCause);
		}
		
		return response;
	}
	
}	
