package remote.wise.service.webservice;

import java.util.HashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.annotate.JsonProperty;

import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.CommonUtil;
@Path("/CountryCode")
public class CountryCodeforAccountCodeService {
	@POST
	@Path("/getCountryCode")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public HashMap<String, String> getCountryCodeforAccountCode(@JsonProperty HashMap<String, String> inputObj)throws CustomFault
	{
		//DF20180313 @Mani :: get countrycode for accountcode
		Logger infoLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		HashMap<String, String> result=new HashMap<String, String>();
		String response=null;
		try{
			long startTime = System.currentTimeMillis();
			CommonUtil implObj = new CommonUtil();
			for(int i=0;i<inputObj.size();i++)
			{
				String accountcode=inputObj.get("accountCode");
				infoLogger.info("Webservice:: to get Country code for accountcode :"+accountcode);
				response =implObj.CountryCodeforAccountCode(accountcode);
				result.put("countryCode", response);
				infoLogger.info("Webservice output :account Code ::"+accountcode+" Country Code :: "+response);
				
			}
			
			
			long endTime = System.currentTimeMillis();
			infoLogger.info("serviceName:CountryCodeforAccountCodeService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		}catch(Exception e){
			e.printStackTrace();
			fLogger.error("Exception caught : "+e.getMessage());
		}
		return result;
}
}
