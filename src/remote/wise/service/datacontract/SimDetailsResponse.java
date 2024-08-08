package remote.wise.service.datacontract;

import java.util.ArrayList;

//20231127 CR452-AirtelApiIntegration-prasad
public class SimDetailsResponse {

    public String activationDate;
    public String basketId;
    public String circleName;
    public String dataType;
    public String dataUnits;
    public String defaultPlanName;
    public String description;
    public String expirationDate;
    public String imsi;
    public String inProgressJob;
    public String inProgressJobOrder;
    public String isPaired;
    public String isPrepaidSim;
    public String lsi;
    public String mobileNo;
    public String onboardingDate;
    public String operatorName;
    public String parentBasketId;
    public String planCode;
    public String planName;
    public Boolean isarp;//sn:CR452
    
    public Boolean getIsarp() {
		return isarp;
	}
	public void setIsarp(Boolean isarp) {
		this.isarp = isarp;
	}
	public String getDefaultPlanName() {
		return defaultPlanName;
	}
	public void setDefaultPlanName(String defaultPlanName) {
		this.defaultPlanName = defaultPlanName;
	}
	public String getExpirationDate() {
		return expirationDate;
	}
	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}
	public String getInProgressJob() {
		return inProgressJob;
	}
	public void setInProgressJob(String inProgressJob) {
		this.inProgressJob = inProgressJob;
	}
	public String getInProgressJobOrder() {
		return inProgressJobOrder;
	}
	public void setInProgressJobOrder(String inProgressJobOrder) {
		this.inProgressJobOrder = inProgressJobOrder;
	}
	public String getSafeCustodyDate() {
		return safeCustodyDate;
	}
	public void setSafeCustodyDate(String safeCustodyDate) {
		this.safeCustodyDate = safeCustodyDate;
	}
	public String getSimInfo1() {
		return simInfo1;
	}
	public void setSimInfo1(String simInfo1) {
		this.simInfo1 = simInfo1;
	}
	public String getSimInfo2() {
		return simInfo2;
	}
	public void setSimInfo2(String simInfo2) {
		this.simInfo2 = simInfo2;
	}
	public String getSimInfo3() {
		return simInfo3;
	}
	public void setSimInfo3(String simInfo3) {
		this.simInfo3 = simInfo3;
	}
	public String getSimTrailEndTime() {
		return simTrailEndTime;
	}
	public void setSimTrailEndTime(String simTrailEndTime) {
		this.simTrailEndTime = simTrailEndTime;
	}
	public String getSimTrailStartTime() {
		return simTrailStartTime;
	}
	public void setSimTrailStartTime(String simTrailStartTime) {
		this.simTrailStartTime = simTrailStartTime;
	}
	public String planType;
    public String safeCustodyDate;
    public ArrayList<SimApnVO> simApnVO;
    public String simId;
    public String simInfo1;
    public String simInfo2;
    public String simInfo3;
    public String simNo;
    public String simTrailEndTime;
    public String simTrailStartTime;
    public String status;
    
    
	public String getSimId() {
		return simId;
	}
	public void setSimId(String simId) {
		this.simId = simId;
	}
	public String getMobileNo() {
		return mobileNo;
	}
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
	public String getSimNo() {
		return simNo;
	}
	public void setSimNo(String simNo) {
		this.simNo = simNo;
	}
	public String getImsi() {
		return imsi;
	}
	public void setImsi(String imsi) {
		this.imsi = imsi;
	}
	public String getLsi() {
		return lsi;
	}
	public void setLsi(String lsi) {
		this.lsi = lsi;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getIsPaired() {
		return isPaired;
	}
	public void setIsPaired(String isPaired) {
		this.isPaired = isPaired;
	}
	public String getIsPrepaidSim() {
		return isPrepaidSim;
	}
	public void setIsPrepaidSim(String isPrepaidSim) {
		this.isPrepaidSim = isPrepaidSim;
	}
	public String getCircleName() {
		return circleName;
	}
	public void setCircleName(String circleName) {
		this.circleName = circleName;
	}
	public String getPlanCode() {
		return planCode;
	}
	public void setPlanCode(String planCode) {
		this.planCode = planCode;
	}
	public String getPlanName() {
		return planName;
	}
	public void setPlanName(String planName) {
		this.planName = planName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public String getDataUnits() {
		return dataUnits;
	}
	public void setDataUnits(String dataUnits) {
		this.dataUnits = dataUnits;
	}
	public String getPlanType() {
		return planType;
	}
	public void setPlanType(String planType) {
		this.planType = planType;
	}
	public String getOnboardingDate() {
		return onboardingDate;
	}
	public void setOnboardingDate(String onboardingDate) {
		this.onboardingDate = onboardingDate;
	}
	public String getActivationDate() {
		return activationDate;
	}
	public void setActivationDate(String activationDate) {
		this.activationDate = activationDate;
	}
	public String getParentBasketId() {
		return parentBasketId;
	}
	public void setParentBasketId(String parentBasketId) {
		this.parentBasketId = parentBasketId;
	}
	public String getBasketId() {
		return basketId;
	}
	public void setBasketId(String basketId) {
		this.basketId = basketId;
	}
	public ArrayList<SimApnVO> getSimApnVO() {
		return simApnVO;
	}
	public void setSimApnVO(ArrayList<SimApnVO> simApnVO) {
		this.simApnVO = simApnVO;
	}
	public String getOperatorName() {
		return operatorName;
	}
	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

}
