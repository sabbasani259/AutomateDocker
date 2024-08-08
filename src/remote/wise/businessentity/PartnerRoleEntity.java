package remote.wise.businessentity;

public class PartnerRoleEntity extends BaseBusinessEntity
{
	int partnerId;
	String partnerRole;
	String reversePartnerRole;
	
	public int getPartnerId() {
		return partnerId;
	}
	public void setPartnerId(int partnerId) {
		this.partnerId = partnerId;
	}
	
	
	public String getPartnerRole() {
		return partnerRole;
	}
	public void setPartnerRole(String partnerRole) {
		this.partnerRole = partnerRole;
	}
	
	
	public String getReversePartnerRole() {
		return reversePartnerRole;
	}
	public void setReversePartnerRole(String reversePartnerRole) {
		this.reversePartnerRole = reversePartnerRole;
	}
	
	
}
