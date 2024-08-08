/**
 * 
 */
package remote.wise.service.webservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.CountryCodeServiceImpl;
import remote.wise.util.CommonUtil;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * @author ROOPN5
 *
 */
@Path("/CountryCodeRESTService")
public class CountryCodeRESTService {
	
	@GET
	@Path("getCountryCodeData")
	@Produces(MediaType.APPLICATION_JSON)
	public String getCountryCodeData(@QueryParam("loginID") String loginID){

		Logger iLogger = InfoLoggerClass.logger;
		List<HashMap<String,Object>> response =new ArrayList<HashMap<String,Object>>();
		String result=null;
		
		Gson gson = new Gson();

		iLogger.info("CountryCodeRESTService WebService Input-----> loginID:"+loginID);
		long startTime = System.currentTimeMillis();

		response = new CountryCodeServiceImpl().getCountryCodeData();

		if(response!=null)
			result = gson.toJsonTree(
					response,
					new TypeToken<List<HashMap<String,Object>>>() {
					}.getType()).toString();

		long endTime=System.currentTimeMillis();
		iLogger.info("CountryCodeRESTService WebService Output -----> response:"+response+"; Total Time taken in ms:"+(endTime - startTime));
		iLogger.info("serviceName:CountryCodeRESTService~executionTime:"+(endTime-startTime)+"~"+loginID+"~"+result);
		
		return result;


	}
	//DF20180912 MA369757- for vin countrycode
	@GET
	@Path("getCountryCodeForVin")
	@Produces(MediaType.TEXT_PLAIN)
	public String getVinCountryCode(@QueryParam("vin") String vin) throws CustomFault
	{
		Logger iLogger = InfoLoggerClass.logger;
		Logger bLogger = BusinessErrorLoggerClass.logger;
		String response=null;
		iLogger.info("CountryCodeRESTService getCountryCodeForVin WebService Input-----> vin:"+vin);
		long startTime = System.currentTimeMillis();
		
		//DF20190119-KO369761 - Input field validation added.
		CommonUtil util = new CommonUtil();
		String isValidinput = util.inputFieldValidation(vin);
		if(!isValidinput.equals("SUCCESS")){
			bLogger.error("Invalid input text::"+vin);
			throw new CustomFault(isValidinput);
		}

		response = new CountryCodeServiceImpl().vinCountryCode(vin);

		long endTime=System.currentTimeMillis();
		iLogger.info("CountryCodeRESTService getCountryCodeForVin WebService Output -----> response:"+response+"; Total Time taken in ms:"+(endTime - startTime));

		return response;
		
	}

}
