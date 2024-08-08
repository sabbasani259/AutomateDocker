package remote.wise.service.implementation;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

import remote.wise.businessobject.EventDetailsBO;
import remote.wise.businessobject.UserDetailsBO;
import remote.wise.exception.CustomFault;

import remote.wise.service.datacontract.AuditLogReqContract;
import remote.wise.service.datacontract.AuditLogRespContract;
/**
* CustomerRequiringImpl  will allow to getAudit Log Details for given Tenancy 
* @author jgupta41
*
*/
public class AuditLogDetailsImpl {
	private String UserId;
	private String UserName;
	private String LastLoginDate;
	
	public String getUserId() {
		return UserId;
	}

	public void setUserId(String userId) {
		UserId = userId;
	}

	public String getUserName() {
		return UserName;
	}

	public void setUserName(String userName) {
		UserName = userName;
	}

	public String getLastLoginDate() {
		return LastLoginDate;
	}

	public void setLastLoginDate(String lastLoginDate) {
		LastLoginDate = lastLoginDate;
	}
	//*******************************Get Audit Log Details for given Tenancy **************************************
/**
 * This method gets Audit Log Details for given Tenancy Id 
 * @param reqObj:Get Audit Log Details for given Tenancy Id
 * @return respList:Returns List of Audit Log Details 
 * @throws CustomFault
 */
	public List<AuditLogRespContract> getAuditLogDetails(AuditLogReqContract reqObj) throws CustomFault{
		
		List<AuditLogRespContract> respList = new LinkedList<AuditLogRespContract>();
		UserDetailsBO UserBO=new UserDetailsBO();
		List<AuditLogDetailsImpl> auditLogDetailsBO = new LinkedList<AuditLogDetailsImpl>();
		auditLogDetailsBO = UserBO.getAuditLogDetails(reqObj.getTenancyId(),reqObj.getFromDate(), reqObj.getToDate());
		for(int i=0;i<auditLogDetailsBO.size();i++){
			AuditLogRespContract respObj = new AuditLogRespContract();
			respObj.setUserId(auditLogDetailsBO.get(i).getUserId());
			respObj.setUserName(auditLogDetailsBO.get(i).getUserName());
			respObj.setLastLoginDate(auditLogDetailsBO.get(i).getLastLoginDate());
			respList.add(respObj);
		}
		return respList;
	}
	//*******************************End of Get Audit Log Details for given Tenancy **************************************
}
