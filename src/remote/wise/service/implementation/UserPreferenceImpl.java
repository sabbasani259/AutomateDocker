package remote.wise.service.implementation;

import remote.wise.businessentity.CatalogValuesEntity;
import remote.wise.businessentity.PreferenceCatalogEntity;
import remote.wise.businessentity.PreferenceEntity;
import remote.wise.businessobject.UserDetailsBO;
import remote.wise.exception.CustomFault;
import remote.wise.service.datacontract.UserPreferenceReqContract;
import remote.wise.service.datacontract.UserPreferenceRespContract;
import java.util.List;
import java.util.LinkedList;
/**
 * 
 * @author tejgm
 * This Webservice class is to get summary on notifications.
 */
public class UserPreferenceImpl {
	
	
	private String contactId;
	private int CatalogValueId;
    private int catalogId;
	private String catalogValue;
	private String catalogName;
	
	
	public String getContactId() {
		return contactId;
	}
   public void setContactId(String contactId) {
		this.contactId = contactId;
	}
   
public int getCatalogValueId() {
	return CatalogValueId;
}
public void setCatalogValueId(int catalogValueId) {
	CatalogValueId = catalogValueId;
}
public int getCatalogId() {
	return catalogId;
}
public void setCatalogId(int catalogId) {
	this.catalogId = catalogId;
}
	public String getCatalogValue() {
		return catalogValue;
	}

	public void setCatalogValue(String catalogValue) {
		this.catalogValue = catalogValue;
	}

	public String getCatalogName() {
		return catalogName;
	}

	public void setCatalogName(String catalogName) {
		this.catalogName = catalogName;
	}
	
/**
 * 
 * @param contactId is passsed to get the prefefence.
 * @return respList
 * @throws CustomFault
 */
	public List<UserPreferenceRespContract> getUserPreference(UserPreferenceReqContract contactId)throws CustomFault
	{
		List<UserPreferenceRespContract> respList=new LinkedList<UserPreferenceRespContract>();
		
		UserDetailsBO userDetBO=new UserDetailsBO();
		List<UserPreferenceImpl> userPreference=userDetBO.getUserPreference(contactId.getContact());
		for(int i=0;i<userPreference.size();i++)
		{
			UserPreferenceRespContract respObj=new UserPreferenceRespContract();
			
			respObj.setContactId(userPreference.get(i).getContactId());
			respObj.setCatalogValueId(userPreference.get(i).getCatalogValueId());
			respObj.setCatalogId(userPreference.get(i).getCatalogId());
			respObj.setCatalogValue(userPreference.get(i).getCatalogValue());
			respObj.setCatalogName(userPreference.get(i).getCatalogName());
			
			respList.add(respObj);
		}	
		return respList;
	}
	/**
	 * 
	 * @param userPreResp userPreResp is passed as a input to set the values on the preference
	 * @return response that was set as a preference
	 * @throws CustomFault
	 */
	public String setUserPreference(UserPreferenceRespContract userPreResp)throws CustomFault
	{
		UserDetailsBO userDetBo=new UserDetailsBO();
		
		String response=userDetBo.setUserPreference(userPreResp.getContactId(),userPreResp.getCatalogValueId());
		return response;
	}
	

}
