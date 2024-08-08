package remote.wise.EAintegration.EAclient;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.logging.log4j.Logger;

import remote.wise.EAintegration.clientPackage.JcbRollOffService.CustomFault;
import remote.wise.EAintegration.clientPackage.JcbRollOffService.JcbRollOffService;
import remote.wise.EAintegration.clientPackage.JcbRollOffService.JcbRollOffServiceService;
import remote.wise.EAintegration.dataContract.JcbRollOffInputContract;
import remote.wise.EAintegration.handler.ErrorMessageHandler;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.CommonUtil;

public class JcbRollOffClient 
{
	public String invokeJcbRollOffService(JcbRollOffInputContract reqObj) throws CustomFault
	{
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		
		String status="SUCCESS";
		int wsdlError=0;
		JcbRollOffServiceService service1 =null;
		JcbRollOffService port1=null;
		
		try
		{
			//creating WebService Client
			iLogger.info("JCBRollOff - "+reqObj.getSerialNumber()+" : Get the wsdl location");
			service1 = new JcbRollOffServiceService();
			
			//Create WebService
			iLogger.info("JCBRollOff - "+reqObj.getSerialNumber()+" : Get the webservice port");
			port1 = service1.getJcbRollOffServicePort();
			
			//Call webservice method
			iLogger.info("JCBRollOff - "+reqObj.getSerialNumber()+" :Invoke webservice");
			iLogger.info("JCBRollOff - Input"+"SerialNUm:"+reqObj.getSerialNumber()+", EngineNUm:"+reqObj.getEngineNumber()+", ChasisNum :"+reqObj.getChasisNumber()+"" +
					" , MessageId:"+reqObj.getMessageId()+", fileRef:"+reqObj.getFileRef()+", Process:"+reqObj.getProcess()+", RJobCode:"+reqObj.getReprocessJobCode() + 
					" , Make:"+reqObj.getMake()+", Build Date:"+reqObj.getBuiltDate()+", Machine Number:"+reqObj.getMachineNumber());
			status = port1.setVinMachineNameMapping(reqObj.getSerialNumber(), reqObj.getEngineNumber(), reqObj.getChasisNumber(), 
					reqObj.getMessageId(), reqObj.getFileRef(), reqObj.getProcess(), reqObj.getReprocessJobCode(), reqObj.getMake(), reqObj.getBuiltDate(), reqObj.getMachineNumber());
			iLogger.info("JCBRollOff - "+reqObj.getSerialNumber()+" :Returned from webservice, Status: "+status);
		}
		
		/* DF20150806 - Rajani Nagaraju - If the wsdl is not available catch the error and take necessary actions */
		catch(Exception e)
		{
			wsdlError=1;
			
			status="FAILURE-JCBRollOffService :WSDL not available";
			
			fLogger.fatal("JCBRollOff - "+reqObj.getSerialNumber()+"JCBRollOffService:WSDL not available");
			
			
			e.printStackTrace();

			Writer result = new StringWriter();
			PrintWriter printWriter = new PrintWriter(result);
			e.printStackTrace(printWriter);
			String err = result.toString();
			fLogger.fatal("JCBRollOff - "+reqObj.getSerialNumber() + " :Exception trace: "+err);
			try 
			{
				printWriter.close();
				result.close();
			} 

			catch (IOException e1) 
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}
		
		/* DF20150806 - Rajani Nagaraju - If the wsdl is not available, store the details in Faultdetails table 
		 * so that the record will be reprocessed automatically. Otherwise, since the file is already moved to archived, we will have no trace of records
		 * that are not processed
		 */
		if(wsdlError==1 || port1==null || service1==null)
		{
			if(reqObj.getSerialNumber()==null)
				reqObj.setSerialNumber("");
			if(reqObj.getEngineNumber()==null)
				reqObj.setEngineNumber("");
			if(reqObj.getChasisNumber()==null)
				reqObj.setChasisNumber("");
			if(reqObj.getMake()==null)
				reqObj.setMake("");
			if(reqObj.getBuiltDate()==null)
				reqObj.setBuiltDate("");
			if(reqObj.getMachineNumber()==null)
				reqObj.setMachineNumber("");
			String messageString = reqObj.getSerialNumber()+"|"+reqObj.getEngineNumber()+"|"+reqObj.getChasisNumber()+"|"+
							reqObj.getMake()+"|"+reqObj.getBuiltDate()+"|"+reqObj.getMachineNumber();
			
			fLogger.fatal("JCBRollOff - "+messageString+" Exception:: Put the record into fault details");
			
			ErrorMessageHandler errorHandler = new ErrorMessageHandler();
			//DF20140715 - Rajani Nagaraju -  Robust Logging for EA File Processing 
			/*errorHandler.handleErrorMessages(reqObj.getMessageId(), messageString, reqObj.getFileRef(), reqObj.getProcess(), 
					reqObj.getReprocessJobCode(),"JCBRollOffService WSDL not available");*/
			errorHandler.handleErrorMessages_new(reqObj.getMessageId(), messageString, reqObj.getFileRef(), reqObj.getProcess(), 
					reqObj.getReprocessJobCode(),"JCBRollOffService WSDL not available","0002","Service Invokation");
			
			//DF20180207:KO369761 - Inserting datacount to log details table for tracing.
			if(reqObj.getMessageId().split("\\|").length<2){
				String uStatus=CommonUtil.updateInterfaceLogDetails(reqObj.getFileRef(), "failureCount", 1);
				iLogger.info("Status on updating data into interface log details table :"+uStatus);
			}
		}
		else{
			//DF20180207:KO369761 - Inserting datacount to log details table for tracing.
			String uStatus = null;
			if(reqObj.getMessageId().split("\\|").length<2){
				 uStatus = CommonUtil.updateInterfaceLogDetails(reqObj.getFileRef(), "successfullServiceInvocation", 1);
				 iLogger.info("Status on updating data into interface log details table :"+uStatus);
			 } 
		}
		
		
		return status;
		
	}
	
	
}
