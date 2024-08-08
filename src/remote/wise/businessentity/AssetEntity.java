package remote.wise.businessentity;

import java.io.Serializable;
import java.sql.Timestamp;


public class AssetEntity extends BaseBusinessEntity implements Serializable
{
	private AssetControlUnitEntity serial_number;
	private int primary_owner_id;
	private String  nick_name,description, chasisNumber;
	private Timestamp purchase_date,install_date,sale_Date;
	private boolean active_status;
	private ClientEntity client_id;
	private ProductEntity productId;
	private SiteNameAssociationForMACustomerEntity siteId;
	private String make;
	private String machineNumber;
	private Timestamp dateTime;
	private Timestamp transferDate;
	//DF20150330 - Rajani Nagaraju - Adding this column to drive partitioning
	private int segmentId;
	//DF20170801 : SU334449 - Adding this column to denote different timeZones for SAARC countries.
	private String timeZone;
	//DF20171207 : KO369761 - Adding these columns for Live Link Renewal.
	private int renewal_flag;
	private Timestamp renewal_date;
	//DF20180305 : @Mani Adding new column for retrofitment machines flag
	private int retrofitFlag;
	//Df20171218 @Roopa adding new column country cide for map service changes 

	private String countrycode;
	//DF20200327 :Ramu B extendedWarrantyType for extended Warranty
	private String extendedWarrantyType;
	//DF20191109:Abhishek::Add extendedWarrantyFlag for extended Warranty.
	private int extendedWarrantyFlag;
	
	private int PremFlag; //CR353.n
	
	
	

	public Timestamp getSale_Date() {
		return sale_Date;
	}

	public void setSale_Date(Timestamp sale_Date) {
		this.sale_Date = sale_Date;
	}

	public int getExtendedWarrantyFlag() {
		return extendedWarrantyFlag;
	}

	public void setExtendedWarrantyFlag(int extendedWarrantyFlag) {
		this.extendedWarrantyFlag = extendedWarrantyFlag;
	}
	public int getRetrofitFlag() {
		return retrofitFlag;
	}

	public void setRetrofitFlag(int retrofitFlag) {
		this.retrofitFlag = retrofitFlag;
	}

	public int getRenewal_flag() {
		return renewal_flag;
	}

	public void setRenewal_flag(int renewal_flag) {
		this.renewal_flag = renewal_flag;
	}

	public Timestamp getRenewal_date() {
		return renewal_date;
	}

	public void setRenewal_date(Timestamp renewal_date) {
		this.renewal_date = renewal_date;
	}

	public String getCountrycode() {
		return countrycode;
	}

	public void setCountrycode(String countrycode) {
		this.countrycode = countrycode;
	}




	/**
	 * @return the segmentId
	 */
	public int getSegmentId() {
		return segmentId;
	}

	/**
	 * @param segmentId the segmentId to set
	 */
	public void setSegmentId(int segmentId) {
		this.segmentId = segmentId;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	public String getMachineNumber() {
		return machineNumber;
	}

	public Timestamp getDateTime() {
		return dateTime;
	}

	public void setDateTime(Timestamp dateTime) {
		this.dateTime = dateTime;
	}

	public void setMachineNumber(String machineNumber) {
		this.machineNumber = machineNumber;
	}

	public AssetEntity(){}

	public AssetEntity(String serial_number)
	{
		super.key = new String(serial_number);
		AssetEntity e= (AssetEntity)read(this);
		if(e!= null)
		{
			setSerial_number(e.getSerial_number());
			setPrimary_owner_id(e.getPrimary_owner_id());
			setNick_name(e.getNick_name());
			setDescription(e.getDescription());
			setPurchase_date(e.getPurchase_date());
			setInstall_date(e.getInstall_date());
			setActive_status(e.isActive_status());
			setClient_id(e.getClient_id());
			setProductId(e.getProductId());
			setChasisNumber(e.getChasisNumber());
			setMake(e.getMake());
			setMachineNumber(e.getMachineNumber());
			setDateTime(e.getDateTime());
		}
	}

	public AssetControlUnitEntity getSerial_number() {
		return serial_number;
	}

	public void setSerial_number(AssetControlUnitEntity serial_number) {
		this.serial_number = serial_number;
	}

	public int getPrimary_owner_id() {
		return primary_owner_id;
	}

	public void setPrimary_owner_id(int primary_owner_id) {
		this.primary_owner_id = primary_owner_id;
	}

	public String getNick_name() {
		return nick_name;
	}

	public void setNick_name(String nick_name) {
		this.nick_name = nick_name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Timestamp getPurchase_date() {
		return purchase_date;
	}

	public void setPurchase_date(Timestamp purchase_date) {
		this.purchase_date = purchase_date;
	}

	public Timestamp getInstall_date() {
		return install_date;
	}

	public void setInstall_date(Timestamp install_date) {
		this.install_date = install_date;
	}

	public boolean isActive_status() {
		return active_status;
	}

	public void setActive_status(boolean active_status) {
		this.active_status = active_status;
	}

	public ClientEntity getClient_id() {
		return client_id;
	}

	public void setClient_id(ClientEntity client_id) {
		this.client_id = client_id;
	}

	public ProductEntity getProductId() {
		return productId;
	}

	public void setProductId(ProductEntity productId) {
		this.productId = productId;
	}

	public String getChasisNumber() {
		return chasisNumber;
	}

	public void setChasisNumber(String chasisNumber) {
		this.chasisNumber = chasisNumber;
	}

	public String getMake() {
		return make;
	}

	public void setMake(String make) {
		this.make = make;
	}

	public Timestamp getTransferDate() {
		return transferDate;
	}

	public void setTransferDate(Timestamp transferDate) {
		this.transferDate = transferDate;
	}
	public void setExtendedWarrantyType(String extendedWarrantyType) {
		this.extendedWarrantyType = extendedWarrantyType;
	}
	
	public String getExtendedWarrantyType() {
		return extendedWarrantyType;
	}

	public SiteNameAssociationForMACustomerEntity getSiteId() {
		return siteId;
	}

	public void setSiteId(SiteNameAssociationForMACustomerEntity siteId) {
		this.siteId = siteId;
	}

	public int getPremFlag() {
		return PremFlag;
	}

	public void setPremFlag(int premFlag) {
		PremFlag = premFlag;
	}


}
