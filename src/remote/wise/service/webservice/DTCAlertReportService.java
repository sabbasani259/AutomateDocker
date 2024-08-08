/**
 * 
 */
package remote.wise.service.webservice;


import java.util.List;
import java.util.Properties;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.logging.log4j.Logger;

import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.DTCAlertReportServiceImpl;
import remote.wise.util.CommonUtil;

@Path("/DTCAlertReportService")
public class DTCAlertReportService {

	
	@GET
	@Path("/getAlertPreference")
	@Produces("text/plain")
	public List<String> getAlertPreference(@QueryParam("roleId") int roleId,@QueryParam("preference") int preference) throws CustomFault {
		Properties prop = new Properties();
		Logger infoLogger = InfoLoggerClass.logger;
		
		infoLogger.info("getAlertPreference inputs: "+"roleId: "+roleId+" "+"preference: "+preference);
		CommonUtil utilObj = new CommonUtil();

		String isValidinput = utilObj.inputFieldValidation(roleId+"");
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		isValidinput = utilObj.inputFieldValidation(preference+"");
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		
		List<String> response = new DTCAlertReportServiceImpl().getAlertPreferenceList(roleId,preference);
		infoLogger.info("WebService output for getAlertPreference : "+response);
		
		return response;
		
	}
	
}
