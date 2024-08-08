package remote.wise.util;

/*
CR344 : VidyaSagarM : 20220817 : UnknownErrorCodeResponse
CR344.sn
 */
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder(value = { 
		"AssetID",
		"ProfileName",
		"ModelName",
		"CustomerName",
		"DealerName",
		"Machine_Hours",
		"DTCAlertCode",
		"SPN",
		"FMI",
		"SourceAddress_Decimal",
		"SourceAddress_Hexadecimal",
		"DtcAlertGenerationTime",
		"DTC_status",
		"Location",
		"Zonal_Name"})
public class UnknownDTCAlertReportRespContract {



	@JsonProperty("AssetID")
	public String AssetID;
	
	@JsonProperty("ProfileName")
	public String ProfileName;
	
	@JsonProperty("ModelName")
	public String ModelName;
	
	@JsonProperty("CustomerName")
	public String CustomerName;

	@JsonProperty("DealerName")
	public String DealerName;

	@JsonProperty("Machine_Hours")
	public String Machine_Hours;
	
	@JsonProperty("DTCAlertCode")
	public String DTCAlertCode;
	
	@JsonProperty("SPN")
	public String SPN;
	
	@JsonProperty("FMI")
	public String FMI;
	
	@JsonProperty("SourceAddress_Hexadecimal")
	public String SourceAddress_Hexadecimal;
	
	@JsonProperty("SourceAddress_Decimal")
	public String SourceAddress_Decimal;
	
	@JsonProperty("DtcAlertGenerationTime")
	public String DtcAlertGenerationTime;
	
	@JsonProperty("DTC_status")
	public String DTC_status;
	
	@JsonProperty("Location")
	public String Location;
	
	public boolean isValidModelOrProfile=false;
	
	@Override
	public String toString() {
		return "UnknownDTCAlertReportRespContract [AssetID=" + AssetID + ", ProfileName=" + ProfileName + ", ModelName="
				+ ModelName + ", CustomerName=" + CustomerName + ", DealerName=" + DealerName + ", Machine_Hours="
				+ Machine_Hours + ", DTCAlertCode=" + DTCAlertCode + ", SPN=" + SPN + ", FMI=" + FMI
				+ ", SourceAddress_Hexadecimal=" + SourceAddress_Hexadecimal + ", SourceAddress_Decimal="
				+ SourceAddress_Decimal + ", DtcAlertGenerationTime=" + DtcAlertGenerationTime + ", DTC_status="
				+ DTC_status + ", Location=" + Location + ", ZonalName=" + ZonalName + "]";
	}

	@JsonProperty("ZonalName")
	public String ZonalName;
	

}
//CR344.en
