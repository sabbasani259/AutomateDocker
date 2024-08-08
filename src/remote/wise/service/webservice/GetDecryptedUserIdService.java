package remote.wise.service.webservice;

import java.util.HashMap;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.annotate.JsonProperty;

import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.CommonUtil;

@Path("/UserId")
public class GetDecryptedUserIdService {

@POST
@Path("/getUserId")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public HashMap<String, String> getDecryptedId(@JsonProperty HashMap<String, String> inputObj)throws CustomFault
{
	//DF20180312 @Mani :: get decrypted user id for an encrypted userid
	Logger infoLogger = InfoLoggerClass.logger;
	Logger fLogger = FatalLoggerClass.logger;
	HashMap<String, String> result=new HashMap<String, String>();
	String response=null;
	try{
		long startTime = System.currentTimeMillis();
		CommonUtil implObj = new CommonUtil();
		for(int i=0;i<inputObj.size();i++)
		{
			String userId=inputObj.get("loginId");
			infoLogger.info("Webservice:: to get decrypted userId for :"+userId);
			response =implObj.getUserId(userId);
			result.put("loginId", response);
			infoLogger.info("Webservice output :Encrypted ::"+userId+" Decrypted :: "+response);
			
		}
		
		
		long endTime = System.currentTimeMillis();
		infoLogger.info("serviceName:GetDecryptedUserIdService~executionTime:"+(endTime-startTime)+"~"+response+"~");
		
	}catch(Exception e){
		e.printStackTrace();
		fLogger.error("Exception caught : "+e.getMessage());
	}
	return result;
}
}
