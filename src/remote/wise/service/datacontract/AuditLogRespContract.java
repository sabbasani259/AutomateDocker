package remote.wise.service.datacontract;

import java.sql.Timestamp;

public class AuditLogRespContract {
private String UserName;
private String UserId;
private String LastLoginDate;
public String getUserName() {
	return UserName;
}
public void setUserName(String userName) {
	UserName = userName;
}
public String getUserId() {
	return UserId;
}
public void setUserId(String userId) {
	UserId = userId;
}
public String getLastLoginDate() {
	return LastLoginDate;
}
public void setLastLoginDate(String lastLoginDate) {
	LastLoginDate = lastLoginDate;
}


}
