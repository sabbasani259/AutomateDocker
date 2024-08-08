package remote.wise.service.webservice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.VinNoFromGroupIdImpl;
import remote.wise.util.CommonUtil;


	/**
	 * @author AJ20119610
	 *
	 */
	@Path("/VinNoFromGroupIdRESTService")
	public class VinNoFromGroupIdRESTService {
		@GET
		@Path("/VinNoFromGroupId")
		@Produces(MediaType.TEXT_PLAIN)
		public List<String> getGroupDetails(@QueryParam("groupId") String groupId)throws CustomFault {
			Logger infoLogger = InfoLoggerClass.logger;
			Logger fLogger = FatalLoggerClass.logger;
			List<String> response = null;
			CommonUtil util = new CommonUtil();

				try {
					
					
					infoLogger.info("Webservice input : "
							+ " groupId : "+groupId);
					
					
					String isValidgroupId=null;
					isValidgroupId = util.inputFieldValidation(String.valueOf(groupId));
					if(!isValidgroupId.equalsIgnoreCase("SUCCESS")){
						throw new CustomFault(isValidgroupId);
					}
					VinNoFromGroupIdImpl implObj = new VinNoFromGroupIdImpl();
					String[] arr=groupId.split(",");
					List<Integer> list = new ArrayList<>();
					for(String id : arr)
						list.add(Integer.parseInt(id));
					response = implObj.getVins(list);
					infoLogger.info("Webservice Output: " + response);
				} catch (Exception e) {
					fLogger.fatal("Exception:" + e.getMessage());
				}
					return response;
		}
		
//		public static void main(String[] args) throws CustomFault {
//			new VinNoFromGroupIdRESTService().getGroupDetails("626,663");
//		}
}