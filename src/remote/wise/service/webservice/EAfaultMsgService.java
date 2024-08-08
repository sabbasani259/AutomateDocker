package remote.wise.service.webservice;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;



import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.EAfaultMsgDetailsReqContract;
import remote.wise.service.datacontract.EAfaultMsgDetailsRespContract;
import remote.wise.service.implementation.EAerrorDetailsImpl;
//import remote.wise.util.WiseLogger;

/** This service gets the details of failed messages from Enterprise Application and also to set the reprocessed date
 * @author Rajani Nagaraju
 *
 */

@WebService(name = "EAfaultMsgService")
public class EAfaultMsgService 
{
	
	/** This method returns the details of the list of messages received from EA and are failed to be processed by the application
	 * @param reqObj specifies the filters to get the required fault message details
	 * @return the details of messages received from Enterprise Application and are failed to be processed
	 */
	@WebMethod(operationName = "getFaultMsgDetails", action = "getFaultMsgDetails")
	public List<EAfaultMsgDetailsRespContract> getFaultMsgDetails(@WebParam(name="reqObj" ) EAfaultMsgDetailsReqContract reqObj) 
	  {
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("EAfaultMsgService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger"); 
		iLogger.info("---- Webservice Input ------");
		iLogger.info("Reprocess TimeStamp:"+reqObj.getReprocessTimeStamp()+",  "+"Reprocess JobCode:"+reqObj.getReprocessJobCode()+",  "+
				 "Message Id:"+reqObj.getMessageId());
		
		long startTime = System.currentTimeMillis();
		List<EAfaultMsgDetailsRespContract> response = new EAerrorDetailsImpl().getFaultMessageDetails(reqObj);
		long endTime = System.currentTimeMillis();
		
		iLogger.info("serviceName:EAfaultMsgService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		
		iLogger.info("----- Webservice Output-----");
		for(int i=0; i<response.size(); i++)
		{
			iLogger.info("MessageId:"+response.get(i).getMessageId()+",  "+"MessageString:"+response.get(i).getMessageString()+",   " +
					"ReprocessJobCode:"+response.get(i).getReprocessJobCode()+",  "+"Process:"+response.get(i).getProcess()+",  "+
					"FileRef:"+response.get(i).getFileName()+",  "+"FailureCounter:"+response.get(i).getFailureCounter()+",  "+
					"ReprocessTimeStamp:"+response.get(i).getReprocessTimeStamp()+",  "+"Sequence:"+response.get(i).getSequence());
		}
		return response;
		
	  }
	
	
	/** This method sets the required time to be scheduled for reprocessing the failed messages
	 * @param reqObj specifies the reprocessing timestamp to be scheduled for the particular message 
	 * @return Returns the success status
	*/
	@WebMethod(operationName = "SetAlertMode", action = "SetAlertMode")
	public String setReprocessDate(@WebParam(name="reqObj" ) List<EAfaultMsgDetailsReqContract> reqObj)
	  {
		
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("EAfaultMsgService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger"); 
		
		iLogger.info("----- Webservice Input-----");
		for(int i=0; i<reqObj.size(); i++)
		{
			iLogger.info("Reprocess TimeStamp:"+reqObj.get(i).getReprocessTimeStamp()+",  "+"Message Id:"+reqObj.get(i).getMessageId());
		}
		
		long startTime = System.currentTimeMillis();
		String response = new EAerrorDetailsImpl().setFaultMsgReprocessDate(reqObj);
		long endTime = System.currentTimeMillis();
		iLogger.info("serviceName:EAfaultMsgService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		
		iLogger.info("----- Webservice Output-----");
		iLogger.info("Status:"+response);
		
		return response;
		
	  }
}
