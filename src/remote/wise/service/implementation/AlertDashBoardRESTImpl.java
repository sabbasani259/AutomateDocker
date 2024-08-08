/**
 * 
 */
package remote.wise.service.implementation;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import remote.wise.businessentity.ContactEntity;
import remote.wise.businessobject.AlertDashBoardRESTBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.service.datacontract.UserAlertsRespContract;
import remote.wise.util.CommonUtil;

/**
 * @author roopn5
 *
 */
public class AlertDashBoardRESTImpl {
	
public List<UserAlertsRespContract> getUserAlerts(int loginTenancyId, int pageNumber, String serialNumber, String loginId) throws CustomFault{
		

	List<UserAlertsRespContract> userAlertsList = new ArrayList<UserAlertsRespContract>();
		Logger bLogger = BusinessErrorLoggerClass.logger;
		ContactEntity contact;
		String userRole;
		//check for the mandatory parameters and validate them
		//1.Contact
		if(loginId==null)
		{
			throw new CustomFault("Please provide LoginId");
		}
	
		DomainServiceImpl domainService = new DomainServiceImpl();
		contact = domainService.getContactDetails(loginId);
		if(contact==null || contact.getContact_id()==null)
		{
			throw new CustomFault("Invalid LoginId");
		}
		
		//DF20171011: KO369761 - Security Check added for input text fields.
		if(serialNumber!=null){

			CommonUtil util = new CommonUtil();
			String isValidinput = util.inputFieldValidation(serialNumber);
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}
		
		if(contact.getRole()!=null)
			userRole = contact.getRole().getRole_name();
		else
			throw new CustomFault("User is not assigned a role");
		
		if(loginTenancyId==0)
		{
			bLogger.error("AlertDashBoardRESTService:getUserAlerts:Mandatory parameter loginTenancyId is 0");
			throw new CustomFault("User Tenancy Id should be passed");
		}
		//DF20200117 : Ramu B  :: Passing loginId 
		userAlertsList = new AlertDashBoardRESTBO().getUserAlerts(loginTenancyId, pageNumber, serialNumber, loginId);
		
		return userAlertsList;
	
	}

}
