package remote.wise.service.webservice;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.UserTokenIdsRESTImpl;
import remote.wise.util.CommonUtil;
import remote.wise.exception.CustomFault;

/**
 *  WebService class to delete old user token ids from idMaster table.
 * @author KO369761
 *
 */

@Path("/UserTokenService")
public class UserTokenIdsRESTService {
	
	@GET
	@Path("deleteUserTokens")
	@Produces({ MediaType.TEXT_PLAIN })
	public String deleteUserTokens() {

		Logger iLogger = InfoLoggerClass.logger;
		long startTime = System.currentTimeMillis();
		String response = "SUCCESS";
		
		iLogger.info("UserTokenService:: DeleteUserTokens WebService Start");
		
		response = new UserTokenIdsRESTImpl().deleteUserTokens();
		
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:UserTokenService~executionTime:"+(endTime-startTime)+"~"+""+"~"+response);

		return response;
		
	}
	
	@GET
	@Path("validateUserToken")
	@Produces({ MediaType.TEXT_PLAIN })
	public int validateUserToken(@QueryParam ("tokenID")String tokenId) {

		Logger iLogger = InfoLoggerClass.logger;
		long startTime = System.currentTimeMillis();
		int response = 0;
		
		iLogger.info("UserTokenService:: validateUserToken WebService Start::tokenId::"+tokenId);
		
		if(tokenId == null)
			return response;
		
		response = new UserTokenIdsRESTImpl().validateUserToken(tokenId);
		
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:UserTokenService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		
		return response;
		
	}
	
	@GET
	@Path("signOut")
	@Produces({ MediaType.TEXT_PLAIN })
	public String userSignOut(@QueryParam ("tokenID")String tokenId) throws CustomFault {

		Logger iLogger = InfoLoggerClass.logger;
		long startTime = System.currentTimeMillis();
		String response = "SUCCESS";
		String userId = null;
		
		iLogger.info("UserTokenService:: validateUserToken WebService Start::tokenId::"+tokenId);
		
		//DF20181015 - Deleting all csrf tokens of respective user.
		new CommonUtil().deleteANTICSRFTOKENS(tokenId,"NA","All");
		
		//DF20170919 @Roopa getting decoded UserId
		userId=new CommonUtil().getUserId(tokenId);
		iLogger.info("Decoded userId::"+userId);
		
		if(tokenId == null){
			throw new CustomFault("Invalid User Id");
		}
		
		response = new UserTokenIdsRESTImpl().userSignOut(userId);
		
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:UserTokenService~executionTime:"+(endTime-startTime)+"~"+""+"~"+response);
		

		return response;
		
	}
}
