package remote.wise.service.datacontract;

public class DetailMachineInfoReqContract {
	private int role_id;
	private String  contact_id;

	/**
	 * @return the contact_id
	 */
	public String getContact_id() {
		return contact_id;
	}
	/**
	 * @param contact_id the contact_id to set
	 */
	public void setContact_id(String contact_id) {
		this.contact_id = contact_id;
	}
	/**
	 * @return the role_id
	 */
	public int getRole_id() {
		return role_id;
	}
	/**
	 * @param role_id the role_id to set
	 */
	public void setRole_id(int role_id) {
		this.role_id = role_id;
	}
	
}
