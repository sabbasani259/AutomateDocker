package remote.wise.service.datacontract;

public class AssetGroupUsersReqContract {

	
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

	
}
