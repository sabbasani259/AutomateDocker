package remote.wise.service.datacontract;

import java.sql.Timestamp;
import java.util.List;

public class MapRespContract {
private String Nickname;
private String OperatingStartTime;
private String OperatingEndTime;
private String Latitude;
private String SerialNumber;
private String Longitude;
private Long ActiveAlert;
private String EngineStatus;
private int ProfileCode;
private String ProfileName;
private String LastReportedTime;
//Added by Juhi on 10-September-2013
private String Severity;

public String getSeverity() {
	return Severity;
}
public void setSeverity(String severity) {
	Severity = severity;
}
public String getSerialNumber() {
	return SerialNumber;
}
public void setSerialNumber(String serialNumber) {
	SerialNumber = serialNumber;
}
public String getEngineStatus() {
	return EngineStatus;
}
public void setEngineStatus(String engineStatus) {
	EngineStatus = engineStatus;
}
public Long getActiveAlert() {
	return ActiveAlert;
}
public void setActiveAlert(Long activeAlert) {
	ActiveAlert = activeAlert;
}
public String getLongitude() {
	return Longitude;
}
public void setLongitude(String longitude) {
	Longitude = longitude;
}
public String getLatitude() {
	return Latitude;
}
public void setLatitude(String latitude) {
	Latitude = latitude;
}
public String getOperatingStartTime() {
	return OperatingStartTime;
}
public void setOperatingStartTime(String operatingStartTime) {
	OperatingStartTime = operatingStartTime;
}
public String getOperatingEndTime() {
	return OperatingEndTime;
}
public void setOperatingEndTime(String operatingEndTime) {
	OperatingEndTime = operatingEndTime;
}
private String TotalMachineHours;

public String getTotalMachineHours() {
	return TotalMachineHours;
}

public void setTotalMachineHours(String totalMachineHours) {
	TotalMachineHours = totalMachineHours;
}

public String getNickname() {
	return Nickname;
}

public void setNickname(String nickname) {
	Nickname = nickname;
}

public int getProfileCode() {
	return ProfileCode;
}
public void setProfileCode(int profileCode) {
	ProfileCode = profileCode;
}
public String getProfileName() {
	return ProfileName;
}
public void setProfileName(String profileName) {
	ProfileName = profileName;
}
public String getLastReportedTime() {
	return LastReportedTime;
}
public void setLastReportedTime(String lastReportedTime) {
	LastReportedTime = lastReportedTime;
}


}
