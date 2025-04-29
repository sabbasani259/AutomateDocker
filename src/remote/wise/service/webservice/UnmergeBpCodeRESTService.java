package remote.wise.service.webservice;

import java.util.LinkedHashMap;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.UnmergeBpCodeImpl;
import remote.wise.util.CommonUtil;

@Path("/UnmergeBpCodeRESTService")
public class UnmergeBpCodeRESTService {
	@POST
	@Path("updateMappingCode")
	@Consumes(MediaType.APPLICATION_JSON)
	public String updateMappingCode(LinkedHashMap<String, Object> reqObj){
		Logger infoLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		String response = null;     
		String responseMsg = null;
		List<String> accountCodeList=null;
		String userID=null;
		for(int i=0;i<reqObj.size();i++){
			if(reqObj.get("accountCodeList")!=null){
				accountCodeList=(List<String>) reqObj.get("accountCodeList");
			}}	
		if(accountCodeList!=null && !accountCodeList.isEmpty()){
		try{
				// LL-147 : Sai Divya : Traceability for BP code un-merging.sn
				String loginID = (String) reqObj.get("loginID");
				infoLogger.info("Received LoginID" + loginID);
				if (loginID != null) {

					infoLogger.info("Initial login ID: " + loginID);
					userID = new CommonUtil().getUserId(loginID);

					if (userID == null) {
						throw new CustomFault("Invalid Login ID: " + loginID);
					} else {
						// Set the login ID to the retrieved user ID

						infoLogger.info("Updated login ID with user ID: " + loginID);
					}

				} else {
					infoLogger.info("Login ID is null.");
				}
				// LL-147 : Sai Divya : Traceability for BP code un-merging.en
			infoLogger.info("Webservice input : "+accountCodeList);
			UnmergeBpCodeImpl implObj = new UnmergeBpCodeImpl();
			response = implObj.updateMappingCode(accountCodeList,userID);
			infoLogger.info("Webservice Output: " + response);
		}catch(Exception e){
			fLogger.error("Exception:"+e.getMessage());
		}
		
		if(response==null || response.isEmpty() )
			responseMsg="Successfully updated!!";
		else
			responseMsg="Some BP codes which are not able to update as not available in System : "+response;
		}
		else
			responseMsg="please enter some accountCode!!It is empty";
		return responseMsg;
	}
}
