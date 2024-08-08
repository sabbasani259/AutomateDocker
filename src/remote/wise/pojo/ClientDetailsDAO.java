 // CR419 :Santosh : 20230714 :Aemp Changes
package remote.wise.pojo;

public class ClientDetailsDAO {

	private int clientId;
	private String clientName;
	private String email;
	private String mobile_No;
	private int status;
	private String UserId;
	private String machineCount;
	private String subScriptionStartDate;
	private String subScriptionEndDate;
	
	private String lastAuthenticationTS;
	
    private String lastAccessedTSForTSAuth; 
    private String lastAccessedTSForSSAuth; 
    private String lastAccessedTSForTS; 
    private String lastAccessedTSForSSFleet; 
    private String lastAccessedTSForSSFleetSingle;
    private String aggrementDate;
    
    public String getLastAccessedTSForTSAuth() {
		return lastAccessedTSForTSAuth;
	}
	public void setLastAccessedTSForTSAuth(String lastAccessedTSForTSAuth) {
		this.lastAccessedTSForTSAuth = lastAccessedTSForTSAuth;
	}
	public String getLastAccessedTSForSSAuth() {
		return lastAccessedTSForSSAuth;
	}
	public void setLastAccessedTSForSSAuth(String lastAccessedTSForSSAuth) {
		this.lastAccessedTSForSSAuth = lastAccessedTSForSSAuth;
	}
	public String getLastAccessedTSForTS() {
		return lastAccessedTSForTS;
	}
	public void setLastAccessedTSForTS(String lastAccessedTSForTS) {
		this.lastAccessedTSForTS = lastAccessedTSForTS;
	}
	public String getLastAccessedTSForSSFleet() {
		return lastAccessedTSForSSFleet;
	}
	public void setLastAccessedTSForSSFleet(String lastAccessedTSForSSFleet) {
		this.lastAccessedTSForSSFleet = lastAccessedTSForSSFleet;
	}
	public String getLastAccessedTSForSSFleetSingle() {
		return lastAccessedTSForSSFleetSingle;
	}
	public void setLastAccessedTSForSSFleetSingle(String lastAccessedTSForSSFleetSingle) {
		this.lastAccessedTSForSSFleetSingle = lastAccessedTSForSSFleetSingle;
	}
	
	
	public int getClientId() {
		return clientId;
	}
	public void setClientId(int clientId) {
		this.clientId = clientId;
	}
	public String getClientName() {
		return clientName;
	}
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getMobile_No() {
		return mobile_No;
	}
	public void setMobile_No(String mobile_No) {
		this.mobile_No = mobile_No;
	}
	public String getUserId() {
		return UserId;
	}
	public void setUserId(String userId) {
		UserId = userId;
	}
	public String getMachineCount() {
		return machineCount;
	}
	public void setMachineCount(String machineCount) {
		this.machineCount = machineCount;
	}
	public String getSubScriptionStartDate() {
		return subScriptionStartDate;
	}
	public void setSubScriptionStartDate(String subScriptionStartDate) {
		this.subScriptionStartDate = subScriptionStartDate;
	}
	public String getSubScriptionEndDate() {
		return subScriptionEndDate;
	}
	public void setSubScriptionEndDate(String subScriptionEndDate) {
		this.subScriptionEndDate = subScriptionEndDate;
	}
	public String getLastAuthenticationTS() {
		return lastAuthenticationTS;
	}
	public void setLastAuthenticationTS(String lastAuthenticationTS) {
		this.lastAuthenticationTS = lastAuthenticationTS;
	}
	
	public String getAggrementDate() {
		return aggrementDate;
	}
	public void setAggrementDate(String aggrementDate) {
		this.aggrementDate = aggrementDate;
	}
	
}
