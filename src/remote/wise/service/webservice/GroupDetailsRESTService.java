package remote.wise.service.webservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.GroupDetailsImpl;
import remote.wise.util.CommonUtil;


	/**
	 * @author AJ20119610
	 *
	 */
	@Path("/GroupDetailsRESTService")
	public class GroupDetailsRESTService {
		@GET
		@Produces(MediaType.APPLICATION_JSON)
		@Path("/getGroupDetails")
		public String getGroupDetails(@QueryParam("userId") String userId)throws CustomFault {
			Logger infoLogger = InfoLoggerClass.logger;
			Logger fLogger = FatalLoggerClass.logger;
			String response = null;
			CommonUtil util = new CommonUtil();

				try {
					
					
					infoLogger.info("Webservice input : "
							+ " userId : "+userId);
					String isuserId=null;
					isuserId = util.inputFieldValidation(String.valueOf(userId));
					if(!isuserId.equalsIgnoreCase("SUCCESS")){
						throw new CustomFault(isuserId);
					}
					
					String isValiduserId=null;
					isValiduserId = util.inputFieldValidation(String.valueOf(userId));
					if(!isValiduserId.equalsIgnoreCase("SUCCESS")){
						throw new CustomFault(isValiduserId);
					}
					GroupDetailsImpl implObj = new GroupDetailsImpl();
					response = implObj.getGroupDetails(userId);
					infoLogger.info("Webservice Output: " + response);
				} catch (Exception e) {
					fLogger.fatal("Exception:" + e.getMessage());
				}

				if(response!=null)
					return response;
					else
						return "Issue with data,Please check";
		}
		
		}