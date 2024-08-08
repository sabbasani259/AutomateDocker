/**
 * 
 */
package remote.wise.service.implementation;

import java.util.List;

import org.apache.logging.log4j.Logger;

import remote.wise.businessobject.AlertDetailsRESTBO;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;

/**
 * @author roopn5
 *
 */
public class AlertDetailsRESTImpl {
	
	public String getAlertDetails(List<Integer> loginTenancyIdList, List<Integer> customGroupIdList){
		

		String alertJSONArray=null;
		Logger bLogger = BusinessErrorLoggerClass.logger;
		
		if(loginTenancyIdList!=null){
		if(loginTenancyIdList.size()==0)
		{
			bLogger.error("AlertDetailsRESTService:getAlertDetails:Mandatory parameter loginTenancyId is 0");
			return null;
		}
		}
		
		if(customGroupIdList!=null){
			if(customGroupIdList.size()==0)
			{
				bLogger.error("AlertDetailsRESTService:getAlertDetails:Mandatory parameter customGroupIdList is 0");
				return null;
			}
			}
		
		alertJSONArray = new AlertDetailsRESTBO().getAlertDetails(loginTenancyIdList,customGroupIdList);
		
		return alertJSONArray;
	
	}

}
