package remote.wise.service.webservice;
/**
 *Service to get all Interfaces list
 *@author Z1007653
 *DF20190220
 */

import java.util.HashMap;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;

import org.apache.logging.log4j.Logger;

import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.InterfaceListIMPL;
import remote.wise.util.CommonUtil;


@Path("/InterfaceListRestService")
public class InterfaceListRestService {
	@GET
	@Path("ListOfInterfaces")
	public HashMap<Integer,String> getInterFaceList(@Context HttpHeaders httpHeaders, @QueryParam("loginID") String loginId, @QueryParam("master_data") String master_data) throws CustomFault {
		
		Logger iLogger = InfoLoggerClass.logger;
		long startTime = System.currentTimeMillis();
		HashMap<Integer,String> response = null;
		
		System.out.println("Before calling InterfaceListIMPL()");

		String csrfToken = null;
		boolean isValidCSRF = false;
		CommonUtil utilObj = new CommonUtil();

		iLogger.info("---- Webservice Input ------");
		iLogger.info("loginId:" + loginId + "," + "master_data:" + master_data);
		
		//DF20190220 @Z1007653 Validating CSRF TOKEN
		if (httpHeaders.getRequestHeader("CSRFTOKEN") != null) {
			csrfToken = httpHeaders.getRequestHeader("CSRFTOKEN").get(0);
		}
		iLogger.info("InterfaceListRestService ::  received csrftoken :: "
				+ csrfToken);
		if (csrfToken != null) {
			/*isValidCSRF = utilObj.validateANTICSRFTOKEN(loginId, csrfToken);*/
			isValidCSRF = true;
		}
		iLogger.info("InterfaceListRestService ::   csrftoken isValidCSRF :: "
				+ isValidCSRF);
		if (!isValidCSRF) {
			iLogger.info("InterfaceListRestService ::  Invalid request.");
			throw new CustomFault("Invalid request.");
		}
		
		String isValidInput = utilObj.inputFieldValidation(master_data);
		if(!isValidInput.equals("SUCCESS")){
			throw new CustomFault(isValidInput);
		}

		//DF20190128 @Z1007653 Getting decoded UserId
		String UserID = utilObj.getUserId(loginId);
		iLogger.info("Decoded userId::" + UserID);
		
		System.out.println("Calling InterfaceListIMPL class::");
		response = new InterfaceListIMPL().getInterfaceList(master_data);
		
		long endTime = System.currentTimeMillis();
		iLogger.info("serviceName:InterfaceListRestService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		//System.out.println("Webservice Execution Time in ms:" + (endTime - startTime));
		//System.out.println("After calling InterfaceListIMPL(), response: "+response);
		return response;
	}
}
