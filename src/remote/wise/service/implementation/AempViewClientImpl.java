 // CR419 :Santosh : 20230714 :Aemp Changes
package remote.wise.service.implementation;
import java.util.List;

import javax.ws.rs.core.HttpHeaders;

import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import remote.wise.dao.AempClientDao;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.pojo.ClientDetailsDAO;
import remote.wise.util.CommonUtil;

public class AempViewClientImpl {
	
public String viewClient(HttpHeaders httpHeaders) {
		Logger iLogger = InfoLoggerClass.logger;
		
		String status="SUCCESS";
		String result;
		String userID=null;
		userID=httpHeaders.getRequestHeader("LoginId").get(0);
		
		
//		1. Validate Login Id
		if(httpHeaders.getRequestHeader("LoginId").get(0) !=null) {
			iLogger.info("Login Id received from UI"+httpHeaders.getRequestHeader("LoginId").get(0));
			userID = new CommonUtil().getUserId(httpHeaders.getRequestHeader("LoginId").get(0));
//			userID=httpHeaders.getRequestHeader("LoginId").get(0);
			iLogger.info("LoginId : " + userID);
			if(userID==null) {
				status = "FAILURE:LoginId is invalid";
				iLogger.info(status);
				return status;
			}
		} else {
			status = "FAILURE:LoginId is invalid";
			iLogger.info(status);
			return status;
		}
		
		// view client from Aemp
		List<ClientDetailsDAO> viewClient = new AempClientDao().viewClientData();
		
		
		if(viewClient.isEmpty()) {
			status = "FAILURE-Error occured in view Client. Please try again";
			iLogger.info(status);
		}
		Gson gson1 = new GsonBuilder().create();
		
		result = gson1.toJson(viewClient);
//		iLogger.info(viewClient);
		
		
		return result;
}

}
