/**
 * 
 */
package remote.wise.service.webservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.LinkedAccountCodeImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * @author ROOPN5
 *
 */
@Path("/LinkedAccountCodesForMappingCode")
public class LinkedAccountCodeService {
	Gson gson = new Gson();

	@POST
	@Path("getListofLinkedAccountCodes")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes({MediaType.APPLICATION_JSON})
	public String getListofLinkedAccountCodes(LinkedHashMap<String,Object> reqObj){


		Logger iLogger = InfoLoggerClass.logger;
		List<HashMap<String,Object>> response =new ArrayList<HashMap<String,Object>>();
		String result=null;

		

		List accountCodeList = (List) reqObj.get("accountCodeList");
		

		iLogger.info("LinkedAccountCodeService:getListofLinkedAccountCodes WebService Input-----> "+" tenancyIdList:"+accountCodeList);
		long startTime = System.currentTimeMillis();


		if(accountCodeList==null){



			HashMap<String, Object> respObj1  = new HashMap<String, Object>();
			respObj1.put("Error", "accountCodeList is mandatory.");
			response.add(respObj1);

			if(response!=null)
				result = gson.toJsonTree(
						response,
						new TypeToken<List<HashMap<String,Object>>>() {
						}.getType()).toString();

			return result;

		}

		response = new LinkedAccountCodeImpl().getListofLinkedAccountCodes(accountCodeList);

		if(response!=null)
			result = gson.toJsonTree(
					response,
					new TypeToken<List<HashMap<String,Object>>>() {
					}.getType()).toString();

		long endTime=System.currentTimeMillis();
		iLogger.info("LinkedAccountCodeService:getListofLinkedAccountCodes WebService Output -----> response:"+response);
		iLogger.info("serviceName:LinkedAccountCodeService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		
		return result;



	}

}
