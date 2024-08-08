package remote.wise.service.webservice;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import remote.wise.businessobject.AssetDetailsBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.AssetExtendedImpl;
import remote.wise.util.CommonUtil;


/**
 * @author AJ20119610
 *
 */
@Path("/UpdateClearBacklogTracebilityDataRestService")
public class UpdateClearBacklogTracebilityDataRestService {
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/updateClearBacklogTracebilityData")
	public String updateClearBacklogTracebilityData(@QueryParam("vinNo") String vinNo,@QueryParam("contact_id") String contact_id,@QueryParam("fwdTimeStamp") String fwdTimeStamp,@QueryParam("statusFlag") String statusFlag)throws CustomFault {
		Logger infoLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		String response = null;
		int sFlag=1;
		try {


			infoLogger.info("Webservice input : "
					+ " vinNo : "+vinNo+" contact_id:"+contact_id+ " fwdTimeStamp : "+fwdTimeStamp+" statusFlag:"+statusFlag);
			
			
			AssetExtendedImpl implObj = new AssetExtendedImpl();
			response = implObj.updateClearBacklogTracebilityData(vinNo,contact_id,fwdTimeStamp,sFlag);
			infoLogger.info("Webservice Output: " + response);
			
		} catch (Exception e) {
			response="FAILURE";
			fLogger.fatal("Exception:" + e.getMessage());
		}
		String response2="SUCCESS";
		try
		{
			
		if(response.equalsIgnoreCase("success") && fwdTimeStamp==null)	
		{
			response2=new AssetDetailsBO().updateClearBacklogFlag(vinNo, sFlag);
		}
	}catch(Exception e )
	{
		response2="FAILURE";
	}
		String actualResponse=response+"~"+response2;
		String[] resultArr= actualResponse.split("~");
		if(resultArr[0].equalsIgnoreCase("Success") && resultArr[1].equalsIgnoreCase("Success"))
			actualResponse="SUCCESS";
		if(resultArr[0].equalsIgnoreCase("Entry already there in progress"))
			actualResponse="Entry already there in progress";
		if(resultArr[0].equalsIgnoreCase("FAILURE") || resultArr[1].equalsIgnoreCase("FAILURE"))
			actualResponse="FAILURE";
		System.out.println(actualResponse);
		return actualResponse;

	}
	public static void main(String[] args) throws CustomFault, Exception {
//	String response2="success";
//	String response="Entry already there in progress";
//	String actualResponse=response+"~"+response2;
//	String[] resultArr= actualResponse.split("~");
//	
//	if(resultArr[0].equalsIgnoreCase("Success") && resultArr[1].equalsIgnoreCase("Success"))
//		actualResponse="SUCCESS";
//	if(resultArr[0].equalsIgnoreCase("Entry already there in progress"))
//		actualResponse="Entry already there in progress";
//	if(resultArr[0].equalsIgnoreCase("FAILURE") || resultArr[1].equalsIgnoreCase("FAILURE"))
//		actualResponse="FAILURE";
//	System.out.println("actualResponse = "+actualResponse);
		//new UpdateClearBacklogTracebilityDataRestService().updateClearBacklogTracebilityData(null, null, null, "1");
}
}
	
