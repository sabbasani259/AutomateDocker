package remote.wise.service.webservice;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;
import remote.wise.EAintegration.handler.ErrorMessageHandler;
import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.ExtendedWarrantyServiceImpl;
import remote.wise.util.CommonUtil;

@Path("/ExtendedWarrantyRESTService")
public class ExtendedWarrentyRESTService {
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/updateExtendedWarrantyDetails")
	public String updateExtendedWarrantyDetails(@QueryParam("vinNumber")String vinNumber,@QueryParam("callTypeId")String callTypeId
			,@QueryParam("monthlyVisitFlag")String monthlyVisitFlag,@QueryParam("extendedWarantyFlag")String extendedWarantyFlag,
			@DefaultValue(" ") @QueryParam("messageId")String messageId,
			@DefaultValue(" ") @QueryParam("fileRef")String fileRef, 
			@DefaultValue(" ") @QueryParam("process")String process,
			@DefaultValue(" ") @QueryParam("reprocessJobCode")String reprocessJobCode){
		
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		iLogger.info("ExtendedWarrantyRESTService:updateExtendedWarrantyDetails:WebService input: vinNumber :"+vinNumber+" callTypeId :"+callTypeId+
				" monthlyVisitFlag :"+monthlyVisitFlag+" extendedWarantyFlag :"+extendedWarantyFlag+" messageId :"+messageId+" fileRef :"+fileRef 
				+" process :"+process+" reprocessJobCode :"+reprocessJobCode);
		CommonUtil util = new CommonUtil();
		String isValidinput=null;
		String response = null;
		long startTime=System.currentTimeMillis();
		try{
			if(!(vinNumber==null||vinNumber.equals("")||vinNumber.equals(" "))){
				isValidinput = util.inputFieldValidation(vinNumber);
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			else{
				throw new Exception("Mandatory Parameter VINNumber is missing");
			}
			if(!(callTypeId==null||callTypeId.equals("")||callTypeId.equals(" "))){
				
				isValidinput=util.inputFieldValidation(callTypeId);
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
				
			}
			else{
				throw new Exception("Mandatory Parameter CallTypeId is missing");
			}
			if(!(monthlyVisitFlag==null||monthlyVisitFlag.equals("")||monthlyVisitFlag.equals(" "))){
				isValidinput = util.inputFieldValidation(monthlyVisitFlag);
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			else{
				throw new Exception("Mandatory Parameter monthlyVisitFlag is missing");
			}
			if(!(extendedWarantyFlag==null||extendedWarantyFlag.equals("")||extendedWarantyFlag.equals(" "))){
				isValidinput = util.inputFieldValidation(extendedWarantyFlag);
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			else{
				throw new Exception("Mandatory Parameter extendedWarantyFlag is missing");
			}
			if(!(messageId==null||messageId.equals("")||messageId.equals(" "))){
				isValidinput = util.inputFieldValidation(messageId);
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(!(fileRef==null||fileRef.equals("")||fileRef.equals(" "))){
				isValidinput = util.inputFieldValidation(fileRef);
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(!(process==null||process.equals("")||process.equals(" "))){
				isValidinput = util.inputFieldValidation(process);
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(!(reprocessJobCode==null||reprocessJobCode.equals("")||reprocessJobCode.equals(" "))){
				isValidinput = util.inputFieldValidation(reprocessJobCode);
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			iLogger.info("calling updateWarrentDetails in ExtendedWarrantyServiceImpl "+vinNumber+callTypeId+monthlyVisitFlag+extendedWarantyFlag+messageId+fileRef+process+reprocessJobCode);
			
			response = new ExtendedWarrantyServiceImpl().updateWarrantyDetails(vinNumber,callTypeId,monthlyVisitFlag,extendedWarantyFlag,messageId,fileRef,process,reprocessJobCode);
			long endTime=System.currentTimeMillis();
			iLogger.info("ExtendedWarrantyRESTService:updateExtendedWarrantyDetails:WebService Output -----> "+response+"; Total Time taken in ms:"+(endTime - startTime));
			iLogger.info("serviceName:ExtendedWarrantyRESTService~executionTime:"+(endTime-startTime)+"~"+""+"~");
			
		}
		catch(Exception e){
			e.printStackTrace();
			fLogger.error("Exception:"+e.getMessage(),e,e);
			
			//update fault details table.
//			DF20119610 removed addition param so that proper messaged can be put in fault_details
			String messageString = vinNumber+"|"+callTypeId+"|"+monthlyVisitFlag+"|"+
					extendedWarantyFlag;

			fLogger.fatal("Extended Warranty - "+messageString+" Exception:: Put the record into fault details");

			ErrorMessageHandler errorHandler = new ErrorMessageHandler();
			
			errorHandler.handleErrorMessages_new(messageId, messageString, fileRef, process, 
					reprocessJobCode,e.getMessage(),"0002","Service Invokation");

			
			if(messageId!=null && messageId.split("\\|").length<2){
				String uStatus=CommonUtil.updateInterfaceLogDetails(fileRef, "failureCount", 1);
				iLogger.info("Status on updating data into interface log details table :"+uStatus);
			}
		}
		return response;

	}
}
