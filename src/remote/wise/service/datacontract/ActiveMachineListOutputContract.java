package remote.wise.service.datacontract;

public class ActiveMachineListOutputContract {

String SerialNumber;
String RollOffDate;
String Profile;
String Model;
String InstallDate;
/**
 * @return the serialNumber
 */
public String getSerialNumber() {
	return SerialNumber;
}
/**
 * @param serialNumber the serialNumber to set
 */
public void setSerialNumber(String serialNumber) {
	SerialNumber = serialNumber;
}
/**
 * @return the rollOffDate
 */
public String getRollOffDate() {
	return RollOffDate;
}
/**
 * @param rollOffDate the rollOffDate to set
 */
public void setRollOffDate(String rollOffDate) {
	RollOffDate = rollOffDate;
}
/**
 * @return the profile
 */
public String getProfile() {
	return Profile;
}
/**
 * @param profile the profile to set
 */
public void setProfile(String profile) {
	Profile = profile;
}
/**
 * @return the model
 */
public String getModel() {
	return Model;
}
/**
 * @param model the model to set
 */
public void setModel(String model) {
	Model = model;
}
/**
 * @return the installDate
 */
public String getInstallDate() {
	return InstallDate;
}
/**
 * @param installDate the installDate to set
 */
public void setInstallDate(String installDate) {
	InstallDate = installDate;
}
}
