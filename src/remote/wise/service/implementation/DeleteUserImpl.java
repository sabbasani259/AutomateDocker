package remote.wise.service.implementation;

////import org.apache.log4j.Logger;

import org.apache.logging.log4j.Logger;

import remote.wise.businessobject.UserDetailsBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
import remote.wise.service.datacontract.DeleteUserReqContract;
//import remote.wise.util.WiseLogger;

public class DeleteUserImpl {
	
	String loginId;
	
	//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
	//public static WiseLogger businessError = WiseLogger.getLogger("DeleteUserImpl:","businessError");

	/**
	 * @return the loginId
	 */
	public String getLoginId() {
		return loginId;
	}

	/**
	 * @param loginId the loginId to set
	 */
	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}
	/**
	 * method to delete user by making status as 0.
	 * @param req
	 * @return String
	 * @throws CustomFault
	 */
	public String deleteUser(DeleteUserReqContract req) throws CustomFault{
		Logger bLogger = BusinessErrorLoggerClass.logger;
		
		if(req.getLoginId() == null || req.getLoginId().equals("")){
			bLogger.error("Login Id is not provided");
			throw new CustomFault("Provide login id !");
		}
		
		UserDetailsBO user=new UserDetailsBO();
		String message = user.deleteUser(req.getLoginId());
		return message;
	}

}
