package remote.wise.service.datacontract;

public class LandmarkCategoryReqContract {
	private String login_id;
	private int Tenancy_ID;
	
	public String getLogin_id() {
		return login_id;
	}
	public void setLogin_id(String login_id) {
		this.login_id = login_id;
	}
	
	public int getTenancy_ID() {
		return Tenancy_ID;
	}
	public void setTenancy_ID(int tenancy_ID) {
		Tenancy_ID = tenancy_ID;
	}
	
}
