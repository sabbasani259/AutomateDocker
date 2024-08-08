//CR321V2-20221130-VidyaSagarM-Fota report to have batch count 1000 
package remote.wise.util;
public class FotaStatusReportV2 {

	String VIN;
	String IMEI;
	String currentFirmware;
	String NIPVersion;
	String MIPVersion;
	String latestFotaRequestTS;
	String FOTApktsdownloaded;
	String totalFOTApkts;
	String lastFOTApktdownloadedTS;	


	String HMRbeforeFota;
	String fotaFinalTimestamp;
	String resultCode;


	String latestFOTABINconfigured;
	String dateofFOTAConfiguration;


	String upgradedFirmwareAfterFOTA;
	String previousFirmware;
	String  lastFOTACompletionDate;
	String HMRafterFota;

	String scriptLastUpdatedTime;
	String FOTASanityStatus;

	
	public String getPreviousFirmware() {
		return previousFirmware;
	}
	public void setPreviousFirmware(String previousFirmware) {
		this.previousFirmware = previousFirmware;
	}
	public String getDateofFOTAConfiguration() {
		return dateofFOTAConfiguration;
	}
	public void setDateofFOTAConfiguration(String dateofFOTAConfiguration) {
		this.dateofFOTAConfiguration = dateofFOTAConfiguration;
	}
	public String getHMRbeforeFota() {
		return HMRbeforeFota;
	}
	public void setHMRbeforeFota(String hMRbeforeFota) {
		HMRbeforeFota = hMRbeforeFota;
	}
	public String getLastFOTACompletionDate() {
		return lastFOTACompletionDate;
	}
	public String getHMRafterFota() {
		return HMRafterFota;
	}
	public String getUpgradedFirmwareAfterFOTA() {
		return upgradedFirmwareAfterFOTA;
	}
	public String getFotaFinalTimestamp() {
		return fotaFinalTimestamp;
	}
	public String getResultCode() {
		return resultCode;
	}
	public void setFOTASanityStatus(String fOTASanityStatus) {
		FOTASanityStatus = fOTASanityStatus;
	}
	public void setLastFOTACompletionDate(String lastFOTACompletionDate) {
		this.lastFOTACompletionDate = lastFOTACompletionDate;
	}
	public void setHMRafterFota(String hMRafterFota) {
		HMRafterFota = hMRafterFota;
	}
	public void setUpgradedFirmwareAfterFOTA(String upgradedFirmwareAfterFOTA) {
		this.upgradedFirmwareAfterFOTA = upgradedFirmwareAfterFOTA;
	}
	public void setFotaFinalTimestamp(String fotaFinalTimestamp) {
		this.fotaFinalTimestamp = fotaFinalTimestamp;
	}
	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}
	public String getScriptLastUpdatedTime() {
		return scriptLastUpdatedTime;
	}
	public void setScriptLastUpdatedTime(String scriptLastUpdatedTime) {
		this.scriptLastUpdatedTime = scriptLastUpdatedTime;
	}
	public String getVIN() {
		return VIN;
	}
	public void setVIN(String vIN) {
		VIN = vIN;
	}
	public String getIMEI() {
		return IMEI;
	}
	public void setIMEI(String iMEI) {
		IMEI = iMEI;
	}
	public String getCurrentFirmware() {
		return currentFirmware;
	}
	public void setCurrentFirmware(String currentFirmware) {
		this.currentFirmware = currentFirmware;
	}

	public String getLatestFotaRequestTS() {
		return latestFotaRequestTS;
	}
	public void setLatestFotaRequestTS(String latestFotaRequestTS) {
		this.latestFotaRequestTS = latestFotaRequestTS;
	}
	public String getFOTApktsdownloaded() {
		return FOTApktsdownloaded;
	}
	public void setFOTApktsdownloaded(String fOTApktsdownloaded) {
		FOTApktsdownloaded = fOTApktsdownloaded;
	}
	public String getTotalFOTApkts() {
		return totalFOTApkts;
	}
	public void setTotalFOTApkts(String totalFOTApkts) {
		this.totalFOTApkts = totalFOTApkts;
	}
	public String getLastFOTApktdownloadedTS() {
		return lastFOTApktdownloadedTS;
	}
	public void setLastFOTApktdownloadedTS(String lastFOTApktdownloadedTS) {
		this.lastFOTApktdownloadedTS = lastFOTApktdownloadedTS;
	}
	public String getFOTASanityStatus() {
		return FOTASanityStatus;
	}
	public void setfOTASanityStatus(String FOTASanityStatus) {
		this.FOTASanityStatus = FOTASanityStatus;
	}
	public String getLatestFOTABINconfigured() {
		return latestFOTABINconfigured;
	}
	public void setLatestFOTABINconfigured(String latestFOTABINconfigured) {
		this.latestFOTABINconfigured = latestFOTABINconfigured;
	}
	
	public String getNIPVersion() {
		return NIPVersion;
	}
	public String getMIPVersion() {
		return MIPVersion;
	}
	public void setNIPVersion(String nIPVersion) {
		NIPVersion = nIPVersion;
	}
	public void setMIPVersion(String mIPVersion) {
		MIPVersion = mIPVersion;
	}
	@Override
	public String toString() {
		return "FotaStatusReportV2 [VIN=" + VIN + ", IMEI=" + IMEI + ", currentFirmware=" + currentFirmware
				+ ", NIPVersion=" + NIPVersion + ", MIPVersion=" + MIPVersion + ", latestFotaRequestTS="
				+ latestFotaRequestTS + ", FOTApktsdownloaded=" + FOTApktsdownloaded + ", totalFOTApkts="
				+ totalFOTApkts + ", lastFOTApktdownloadedTS=" + lastFOTApktdownloadedTS + ", HMRbeforeFota="
				+ HMRbeforeFota + ", fotaFinalTimestamp=" + fotaFinalTimestamp + ", resultCode=" + resultCode
				+ ", latestFOTABINconfigured=" + latestFOTABINconfigured + ", dateofFOTAConfiguration="
				+ dateofFOTAConfiguration + ", upgradedFirmwareAfterFOTA=" + upgradedFirmwareAfterFOTA
				+ ", lastFOTACompletionDate=" + lastFOTACompletionDate + ", HMRafterFota=" + HMRafterFota
				+ ", scriptLastUpdatedTime=" + scriptLastUpdatedTime + ", FOTASanityStatus=" + FOTASanityStatus + "]";
	}


}
//CR321V2