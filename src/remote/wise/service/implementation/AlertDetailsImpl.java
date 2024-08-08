package remote.wise.service.implementation;

////import org.apache.log4j.Logger;

import org.apache.logging.log4j.Logger;

import remote.wise.businessentity.AssetEntity;
import remote.wise.businessobject.EventDetailsBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
import remote.wise.service.datacontract.AlertDetailsReqContract;
import remote.wise.service.datacontract.AlertDetailsRespContract;
//import remote.wise.util.WiseLogger;

/** Implementation Class to get the Alert details and set the alert comments
 * @author Rajani Nagaraju
 *
 */
public class AlertDetailsImpl 
{
	//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
	//public static WiseLogger businessError = WiseLogger.getLogger("AlertDetailsImpl:","businessError");
	
	String serialNumber;
	String lifeHours;
	String latitude;
	String longitude;
	String notes;
	int assetEventId;
	String profileName;
	
	/**
	 * @return serial number as String
	 */
	public String getSerialNumber() {
		return serialNumber;
	}
	/**
	 * @param serialNumber VIN as String input
	 */
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	
	/**
	 * @return Cumulative Engine Hours returned as String
	 */
	public String getLifeHours() {
		return lifeHours;
	}
	/**
	 * @param lifeHours CumulativeEngineHours as String input
	 */
	public void setLifeHours(String lifeHours) {
		this.lifeHours = lifeHours;
	}
	
	/**
	 * @return Latitude Coordinate returned as String
	 */
	public String getLatitude() {
		return latitude;
	}
	/**
	 * @param latitude Latitude Coordinate as String input
	 */
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	
	/**
	 * @return Longitude Coordinate returned as String
	 */
	public String getLongitude() {
		return longitude;
	}
	/**
	 * @param longitude Longitude Coordinate as String input
	 */ 
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	
	/**
	 * @return Alert Comments returned as String
	 */
	public String getNotes() {
		return notes;
	}
	/**
	 * @param notes AlertComments as String input
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	
	/**
	 * @return assetEventId from eventHistory returned as integer
	 */
	public int getAssetEventId() {
		return assetEventId;
	}
	/**
	 * @param assetEventId assetEventId as Integer Input
	 */
	public void setAssetEventId(int assetEventId) {
		this.assetEventId = assetEventId;
	}
	
	
	public String getProfileName() {
		return profileName;
	}
	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}
	//******************************* get Alert Details for a given alertId and serialNumber ****************************************
	/** This method returns the Alert details for the specified assetEvent
	 * @param reqObj VIN and assetEventId is specified in the reqObj
	 * @return returns the AlertDetails for a given AssetEvent 
	 * @throws CustomFault custom exception is thrown when serialNumber or assetEventId is not specified
	 */
	public AlertDetailsRespContract getAlertDetails(AlertDetailsReqContract reqObj) throws CustomFault
	{
		
		//Logger businessError = Logger.getLogger("businessErrorLogger");
		Logger bLogger = BusinessErrorLoggerClass.logger;
    	
		AlertDetailsRespContract response = new AlertDetailsRespContract();
		
		//Serial Number is mandatory input parameter
		if(reqObj.getSerialNumber()==null)
		{
			bLogger.error("Serial Number not specified");
			throw new CustomFault("Provide Serial Number");
		}
		
		
		//validate serial number
		DomainServiceImpl domainService = new DomainServiceImpl();
		AssetEntity asset = domainService.getAssetDetails(reqObj.getSerialNumber());
		if(asset==null || asset.getSerial_number()==null)
		{
			bLogger.error("Invalid Serial Number");
			throw new CustomFault("Invalid Serial Number");
		}
		
		//AssetEvent Id is mandatory input parameter
		if(reqObj.getAssetEventId()==0)
		{
			bLogger.error("Asset Event Id should be specified");
			throw new CustomFault("Asset Event Id should be specified");
		}
		
		
		EventDetailsBO eventDetailsObj = new EventDetailsBO();
		AlertDetailsImpl alertDetails = eventDetailsObj.getAlertDetails(reqObj.getSerialNumber(),reqObj.getAssetEventId());
		
		response.setSerialNumber(alertDetails.getSerialNumber());
		response.setLifeHours(alertDetails.getLifeHours());
		response.setLatitude(alertDetails.getLatitude());
		response.setLongitude(alertDetails.getLongitude());
		response.setAssetEventId(alertDetails.getAssetEventId());
		response.setNotes(alertDetails.getNotes());
		response.setProfileName(alertDetails.getProfileName());
		
		return response;
	}
	//******************************* END of get Alert Details for a given alertId and serialNumber ****************************************
	
	
	
	//*************************************** set Alert Comments **********************************
	/** This method sets the alert comments
	 * @param reqObj Comment to be set for an alert is specified
	 * @return status message of alert comment update
	 * @throws CustomFault when assetEventId is not specified
	 */
	public String setAlertComments(AlertDetailsRespContract reqObj) throws CustomFault
	{
		//Logger businessError = Logger.getLogger("businessErrorLogger");
		Logger bLogger = BusinessErrorLoggerClass.logger;
    	
		if(reqObj.getAssetEventId()==0)
		{
			bLogger.error("Asset Event Id should be specified");
			throw new CustomFault("Asset Event ID should be specified");
		}
		
		EventDetailsBO eventDetailsObj = new EventDetailsBO();
		String status = eventDetailsObj.setAlertComments(reqObj.getAssetEventId(),reqObj.getNotes());
		return status;
	}
	//***************************************END of set Alert Comments **********************************
	
}
