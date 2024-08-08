package remote.wise.service.datacontract;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;



public class AssetGroupUsersRespContract {

	
	List<String> ContactId;
	String loginId;
	int groupId;
	int LoginTenancyId;
	

	public int getLoginTenancyId() {
		return LoginTenancyId;
	}

	public void setLoginTenancyId(int loginTenancyId) {
		LoginTenancyId = loginTenancyId;
	}

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public List<String> getContactId() {
		return ContactId;
	}

	public void setContactId(List<String> contactId) {
		ContactId = contactId;
	}


	
	
}
