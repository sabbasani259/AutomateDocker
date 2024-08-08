package remote.wise.service.webservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.apache.logging.log4j.Logger;

import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.VinAsNickNameImpl;
import remote.wise.util.CommonUtil;


/**
 * @author AJ20119610
 *
 */
@Path("/VinAsNickNameRESTService")
public class VinAsNickNameRESTService {
	@GET
	@Path("updateNicknameForVin")
	public String setNicknameForVin(@QueryParam("vin")String vin,@QueryParam("nickName") String nickName)throws CustomFault {
		Logger infoLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		String response = null;
		String responseMsgInUI = null;
		CommonUtil util = new CommonUtil();

			try {
				infoLogger.info("Webservice input : " + "vin : " + vin
						+ " nickName : " + nickName);
				String isValidVin=null;
				String isValidNickName=null;
				isValidVin = util.inputFieldValidation(String.valueOf(vin));
				if(!isValidVin.equalsIgnoreCase("SUCCESS")){
					throw new CustomFault(isValidVin);
				}
				isValidNickName = util.inputFieldValidation(String.valueOf(nickName));
				if(!isValidNickName.equalsIgnoreCase("SUCCESS")){
					throw new CustomFault(isValidNickName);
				}
				VinAsNickNameImpl implObj = new VinAsNickNameImpl();
				response = implObj.setNicknameForVin(vin, nickName);
				infoLogger.info("Webservice Output: " + response);
			} catch (Exception e) {
				fLogger.error("Exception:" + e.getMessage());
			}

		if (response != null && !response.isEmpty()) {
			if (response.equalsIgnoreCase("Success"))
				responseMsgInUI = "Successfully updated!!";
			else if (response.equalsIgnoreCase("failure"))
				responseMsgInUI = "Issue in processing";
			else
				responseMsgInUI = response;
		} else
			responseMsgInUI = response;

		return responseMsgInUI;
	}
	
	/*public static void main(String[] args) {
		System.out.println(new VinAsNickNameRESTService().setNicknameForVin("HAR135WSC01991997", "test1"));
	}
*/}
