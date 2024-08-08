/**
 * 
 */
package remote.wise.service.webservice;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.UserAlertsRespContract;
import remote.wise.service.implementation.AlertDashBoardRESTImpl;
import remote.wise.util.CommonUtil;

/**
 * @author roopn5
 *
 */
@Path("/AlertDashBoardRESTService")
public class AlertDashBoardRESTService {
	
	@GET
	@Path("getUserAlerts")
	@Produces(MediaType.APPLICATION_JSON)
	public List<UserAlertsRespContract> getUserAlerts(@QueryParam("loginTenancyID") int loginTenancyID, @QueryParam("pageNumber") int pageNumber, @QueryParam("serialNumber") String serialNumber, @QueryParam("loginID") String loginID){
		String alertJSONArray= null;
		Logger iLogger = InfoLoggerClass.logger;
		
		List<UserAlertsRespContract> userAlertsList = new ArrayList<UserAlertsRespContract>();
		
		iLogger.info("AlertDashBoardRESTService:WebService Input-----> loginTenancyID:"+loginTenancyID);
		long startTime = System.currentTimeMillis();
		
		try {
			//DF20180806:KO369761 - Validating VIN hierarchy against login id/tenancy id
			if(serialNumber != null){
				CommonUtil utilObj = new CommonUtil();
				String serialNum = utilObj.validateVIN(loginTenancyID, serialNumber);
				if(serialNum == null || serialNum.equalsIgnoreCase("FAILURE")){
					throw new CustomFault("Invalid VIN Number");
				}
			}

			userAlertsList = new AlertDashBoardRESTImpl().getUserAlerts(loginTenancyID, pageNumber, serialNumber,loginID);
		} catch (CustomFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		long endTime=System.currentTimeMillis();
		iLogger.info("AlertDashBoardRESTService:WebService Output -----> alertJSONArray:"+alertJSONArray);
		iLogger.info("serviceName:AlertDashBoardRESTService~executionTime:"+(endTime - startTime)+"~"+""+"~");
		
		return userAlertsList;
		
		
	}

}
