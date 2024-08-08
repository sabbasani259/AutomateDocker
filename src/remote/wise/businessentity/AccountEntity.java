package remote.wise.businessentity;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class AccountEntity extends BaseBusinessEntity
{
	private int account_id;
	private String account_name,description,site_location,phone_no,mobile_no,emailId;
	private boolean status;
	private Long no_of_employees;
	private AccountEntity parent_account_id;
	private Date year_started;
	private int owner_id;
	private ClientEntity client_id;
	private String accountCode;
	private AddressEntity addressId;
	private String fax;
	private String timeZone;
	private String countryCode;
	private boolean MAFlag;
	
	//Df20180117 @Roopa Multiple BP code changes
	private String mappingCode;
	//DF20190312 :mani: account tracebility for creation and updation
		private Timestamp createdOn;
		private Timestamp updatedOn;
	
	public AccountEntity(){}
	
	public AccountEntity(int accountId)
	{
		super.key = new Integer(accountId);
		AccountEntity a = (AccountEntity)read(this);
		if(a != null)
		{
			setAccount_id(a.getAccount_id());
			setAccount_name(a.getAccount_name());
			setDescription(a.getDescription());
			setPhone_no(a.getPhone_no());
			setMobile_no(a.getMobile_no());
			setEmailId(a.getEmailId());
			setStatus(a.isStatus());
			setAccountCode(a.getAccountCode());
			setParent_account_id(a.getParent_account_id());
			setOwner_id(a.getOwner_id());
			setAddressId(a.getAddressId());
			setFax(a.getFax());
			setClient_id(a.getClient_id());
			setMappingCode(a.getMappingCode());
		}
	}
	
	

	public String getMappingCode() {
		return mappingCode;
	}

	public void setMappingCode(String mappingCode) {
		this.mappingCode = mappingCode;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}
	
	public String getAccountCode() {
		return accountCode;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	
	public void setAccountCode(String accountCode) {
		this.accountCode = accountCode;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public ClientEntity getClient_id() {
		return client_id;
	}
	public void setClient_id(ClientEntity client_id) {
		this.client_id = client_id;
	}
	
	public int getAccount_id() {
		return account_id;
	}
	public void setAccount_id(int account_id) {
		this.account_id = account_id;
	}
	

	public Long getNo_of_employees() {
		return no_of_employees;
	}
	public void setNo_of_employees(Long no_of_employees) {
		this.no_of_employees = no_of_employees;
	}
	
	
	public int getOwner_id() {
		return owner_id;
	}

	public void setOwner_id(int owner_id) {
		this.owner_id = owner_id;
	}
	
	public String getPhone_no() {
		return phone_no;
	}
	public void setPhone_no(String phone_no) {
		this.phone_no = phone_no;
	}
	
	public String getMobile_no() {
		return mobile_no;
	}
	public void setMobile_no(String mobile_no) {
		this.mobile_no = mobile_no;
	}
	
	public String getAccount_name() {
		return account_name;
	}
	public void setAccount_name(String account_name) {
		this.account_name = account_name;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getSite_location() {
		return site_location;
	}
	public void setSite_location(String site_location) {
		this.site_location = site_location;
	}
	
		
		
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	
	public AccountEntity getParent_account_id() {
		return parent_account_id;
	}
	public void setParent_account_id(AccountEntity parent_account_id) {
		this.parent_account_id = parent_account_id;
	}
	
	public Date getYear_started() {
		return year_started;
	}
	public void setYear_started(Date year_started) {
		this.year_started = year_started;
	}
	
	public AddressEntity getAddressId() {
		return addressId;
	}

	public void setAddressId(AddressEntity addressId) {
		this.addressId = addressId;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public Timestamp getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Timestamp createdOn) {
		this.createdOn = createdOn;
	}

	public Timestamp getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Timestamp updatedOn) {
		this.updatedOn = updatedOn;
	}
	
	public boolean isMAFlag() {
		return MAFlag;
	}

	public void setMAFlag(boolean mAFlag) {
		MAFlag = mAFlag;
	}
}
