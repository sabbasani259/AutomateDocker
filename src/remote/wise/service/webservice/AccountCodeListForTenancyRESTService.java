package remote.wise.service.webservice;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.CommonUtil;
import remote.wise.util.DateUtil;
import remote.wise.util.ListToStringConversion;

@Path("/AccountCodeListForTenancyRESTService")
public class AccountCodeListForTenancyRESTService {
	@POST
	@Path("getAccountCodeList")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String getAccountCodeList(LinkedHashMap<String, Object> reqObj){
		Logger infoLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		String response = null;             
		try{
			infoLogger.info("Webservice input : "+reqObj);
			CommonUtil util = new CommonUtil();
			String isValidinput=null;
			ListToStringConversion convert=new ListToStringConversion();
			for(int i=0;i<reqObj.size();i++){
				if(reqObj.get("tenancyIdList")!=null){
					List<Integer> tenacnyList=(List<Integer>) reqObj.get("tenancyIdList");
					String tenacnyListString=convert.getIntegerListString(tenacnyList).toString();
					isValidinput = util.inputFieldValidation(tenacnyListString);
					if(!isValidinput.equals("SUCCESS")){
						throw new CustomFault(isValidinput);
					}
				}
			}
			DateUtil dateUtilObj = new DateUtil();
			List<Integer> tenacnyList=(List) reqObj.get("tenancyIdList");
			response = dateUtilObj.getAccountCodeListForTheTenancy(tenacnyList);
			infoLogger.info("Webservice Output: " + response);
		}catch(Exception e){
			e.printStackTrace();
			fLogger.error("Exception:"+e.getMessage());
		}
		return response;
	}
}
