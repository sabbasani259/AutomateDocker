package remote.wise.service.implementation;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

//import org.apache.log4j.Logger;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessentity.ContactEntity;
import remote.wise.businessentity.GroupUserMapping;
import remote.wise.businessobject.AssetCustomGroupDetailsBO;
import remote.wise.businessobject.AssetDetailsBO;
import remote.wise.businessobject.LandmarkCategoryBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
import remote.wise.service.datacontract.AssetDashboardReqContract;
import remote.wise.service.datacontract.AssetDashboardRespContract;
import remote.wise.util.CommonUtil;
import remote.wise.util.HibernateUtil;

/** Implementation class to get Asset dashboard details
 * @author Rajani Nagaraju
 *
 */
public class AssetDashboardImpl 
{
	
	//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
	/*public static WiseLogger businessError = WiseLogger.getLogger("AssetDashboardImpl:","businessError");
	public static WiseLogger fatalError = WiseLogger.getLogger("AssetDashboardImpl:","fatalError");*/
	
	String serialNumber;
	String nickName;
	String machineStatus;
	String lifeHours;
	String fuelLevel;
	String dueForService;
	String latitude;
	String longitude;
	String notes;
	String connectivityStatus;
	String externalBatteryInVolts;
	String externalBatteryStatus;
	String highCoolantTemperature;
	String lowEngineOilPressure;
	String modelName;
	String profileName;
	String engineTypeName;
	String engineStatus;
	//DefectId:20140211 Added new parameter as assetImage @suprava
	String assetImage;
	//DefectId:20141208 - Rajani Nagaraju - Return the last Pkt Received time (Either through GPRS / SMS )
	String lastPktReceivedTime;
	//DF20180808:KO369761-Returns Country code of individual asset.
	String countryCode;
	//DF20161222 @Mamatha Time spent in Dual mode will be displayed on the Fleet General tab for a VIN
	String ecModeHrs;
	String powerModeHrs;


	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	
	
	/**
	 * @return the lastPktReceivedTime
	 */
	public String getLastPktReceivedTime() {
		return lastPktReceivedTime;
	}
	/**
	 * @param lastPktReceivedTime the lastPktReceivedTime to set
	 */
	public void setLastPktReceivedTime(String lastPktReceivedTime) {
		this.lastPktReceivedTime = lastPktReceivedTime;
	}
	/**
	 * @return the assetImage
	 */
	public String getAssetImage() {
		return assetImage;
	}
	/**
	 * @param assetImage the assetImage to set
	 */
	public void setAssetImage(String assetImage) {
		this.assetImage = assetImage;
	}
	/**
	 * @return the engineStatus
	 */
	public String getEngineStatus() {
		return engineStatus;
	}
	/**
	 * @param engineStatus the engineStatus to set
	 */
	public void setEngineStatus(String engineStatus) {
		this.engineStatus = engineStatus;
	}
	//DefectID: DF20131212 - Rajani Nagaraju - To return the last communicated timestamp of the machine.
	String lastReportedTime;
		
		
	/**
	 * @return the lastReportedTime
	 */
	public String getLastReportedTime() {
		return lastReportedTime;
	}
	/**
	 * @param lastReportedTime the lastReportedTime to set
	 */
	public void setLastReportedTime(String lastReportedTime) {
		this.lastReportedTime = lastReportedTime;
	}
	
	/**
	 * @return the serialNumber
	 */
	public String getSerialNumber() {
		return serialNumber;
	}
	/**
	 * @param serialNumber the serialNumber to set
	 */
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	/**
	 * @return the nickName
	 */
	public String getNickName() {
		return nickName;
	}
	/**
	 * @param nickName the nickName to set
	 */
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	/**
	 * @return the machineStatus
	 */
	public String getMachineStatus() {
		return machineStatus;
	}
	/**
	 * @param machineStatus the machineStatus to set
	 */
	public void setMachineStatus(String machineStatus) {
		this.machineStatus = machineStatus;
	}
	/**
	 * @return the lifeHours
	 */
	public String getLifeHours() {
		return lifeHours;
	}
	/**
	 * @param lifeHours the lifeHours to set
	 */
	public void setLifeHours(String lifeHours) {
		this.lifeHours = lifeHours;
	}
	/**
	 * @return the fuelLevel
	 */
	public String getFuelLevel() {
		return fuelLevel;
	}
	/**
	 * @param fuelLevel the fuelLevel to set
	 */
	public void setFuelLevel(String fuelLevel) {
		this.fuelLevel = fuelLevel;
	}
	/**
	 * @return the dueForService
	 */
	public String getDueForService() {
		return dueForService;
	}
	/**
	 * @param dueForService the dueForService to set
	 */
	public void setDueForService(String dueForService) {
		this.dueForService = dueForService;
	}
	/**
	 * @return the latitude
	 */
	public String getLatitude() {
		return latitude;
	}
	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	/**
	 * @return the longitude
	 */
	public String getLongitude() {
		return longitude;
	}
	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	/**
	 * @return the notes
	 */
	public String getNotes() {
		return notes;
	}
	/**
	 * @param notes the notes to set
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}
	/**
	 * @return the connectivityStatus
	 */
	public String getConnectivityStatus() {
		return connectivityStatus;
	}
	/**
	 * @param connectivityStatus the connectivityStatus to set
	 */
	public void setConnectivityStatus(String connectivityStatus) {
		this.connectivityStatus = connectivityStatus;
	}
	/**
	 * @return the externalBatteryInVolts
	 */
	public String getExternalBatteryInVolts() {
		return externalBatteryInVolts;
	}
	/**
	 * @param externalBatteryInVolts the externalBatteryInVolts to set
	 */
	public void setExternalBatteryInVolts(String externalBatteryInVolts) {
		this.externalBatteryInVolts = externalBatteryInVolts;
	}
	/**
	 * @return the highCoolantTemperature
	 */
	public String getHighCoolantTemperature() {
		return highCoolantTemperature;
	}
	/**
	 * @param highCoolantTemperature the highCoolantTemperature to set
	 */
	public void setHighCoolantTemperature(String highCoolantTemperature) {
		this.highCoolantTemperature = highCoolantTemperature;
	}
	/**
	 * @return the lowEngineOilPressure
	 */
	public String getLowEngineOilPressure() {
		return lowEngineOilPressure;
	}
	/**
	 * @param lowEngineOilPressure the lowEngineOilPressure to set
	 */
	public void setLowEngineOilPressure(String lowEngineOilPressure) {
		this.lowEngineOilPressure = lowEngineOilPressure;
	}
	/**
	 * @return the modelName
	 */
	public String getModelName() {
		return modelName;
	}
	/**
	 * @param modelName the modelName to set
	 */
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	/**
	 * @return the profileName
	 */
	public String getProfileName() {
		return profileName;
	}
	/**
	 * @param profileName the profileName to set
	 */
	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}
	
	
	/**
	 * @return the externalBatteryStatus
	 */
	public String getExternalBatteryStatus() {
		return externalBatteryStatus;
	}
	/**
	 * @param externalBatteryStatus the externalBatteryStatus to set
	 */
	public void setExternalBatteryStatus(String externalBatteryStatus) {
		this.externalBatteryStatus = externalBatteryStatus;
	}
	/**
	 * @return the engineTypeName
	 */
	public String getEngineTypeName() {
		return engineTypeName;
	}
	/**
	 * @param engineTypeName the engineTypeName to set
	 */
	public void setEngineTypeName(String engineTypeName) {
		this.engineTypeName = engineTypeName;
	}
	//get Asset Dashboard details for the machines under the specified tenancy/User
	/** This method returns the dashboard details for the accessible list of Machines
	 * @param reqObj Input filter criteria is specified through this reqObj
	 * @return Returns the asset dashboard details
	 * @throws CustomFault
	 */
	public List<AssetDashboardRespContract> getAssetDashboardDetails(AssetDashboardReqContract reqObj,String nickName) throws CustomFault
	{
		
		//Logger businessError = Logger.getLogger("businessErrorLogger");
		//Logger fatalError = Logger.getLogger("fatalErrorLogger");
		Logger bLogger = BusinessErrorLoggerClass.logger;
    	//Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
    	Logger iLogger = InfoLoggerClass.logger;
		int pageNumber = reqObj.getPageNumber();
		long startTime=System.currentTimeMillis();
		
		//create response Object
		iLogger.info("into the implementation class loginId::"+reqObj.getLoginId());
		List<AssetDashboardRespContract> responseList = new LinkedList<AssetDashboardRespContract>();
		
		//Create AssetDetailsBO obj
		AssetDetailsBO assetDetails = new AssetDetailsBO();
		List<AssetDashboardImpl> assetDashboardImpList = new LinkedList<AssetDashboardImpl>();
		
				
//		try
//		{
			//check whether the userTenancyId is specified - mandatory parameter
			if(reqObj.getUserTenancyIdList()==null || reqObj.getUserTenancyIdList().isEmpty())
			{
				bLogger.error("Custom Fault: Tenancy Id of user is not specified");
				throw new CustomFault("Tenancy Id of user is not specified");
			}
			
			//check whether the Login Id is specified
			if(reqObj.getLoginId()==null)
			{
				bLogger.error("Custom Fault: Login Id not specified");
				throw new CustomFault("Login Id not specified");
			}
//			Keerthi : 12/02/14 : SEARCH : serial no. should be of 7 digits or 17 digits.
			if(reqObj.getSerialNumber()!=null){
				if(!(reqObj.getSerialNumber().trim().length()>=7 && reqObj.getSerialNumber().trim().length()<=17)){
					bLogger.error("Custom Fault: Invalid Serial Number "+reqObj.getSerialNumber()+". Length should be between 7 and 17");
					throw new CustomFault("Invalid Serial Number "+reqObj.getSerialNumber()+". Length should be between 7 and 17");
				}
				//DF20171011: KO369761 - Security Check added for input text fields.
				CommonUtil util = new CommonUtil();
				String isValidinput = util.inputFieldValidation(reqObj.getSerialNumber());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			
//			DefectId:20150706 @Suprava Adding Mobile Number as New Search criteria in Fleet General Tab.
			//DF20180307 :@Mani addition of retrofit filter in fleet. whose value is sent in mobilenumber.Hence skipping the same for mobile number validation.
			if(reqObj.getMobilenumber()!=null && (!reqObj.getMobilenumber().equalsIgnoreCase("Retrofit"))){
				if(!(reqObj.getMobilenumber().trim().length()==10)){
					bLogger.error("Custom Fault: Invalid Mobile Number "+reqObj.getMobilenumber()+". Length should be 10 digit");
					throw new CustomFault("Invalid Mobile Number "+reqObj.getMobilenumber()+". Length should be 10 digit");
				}
				//DF20171011: KO369761 - Security Check added for input text fields.
				CommonUtil util = new CommonUtil();
				String isValidinput = util.inputFieldValidation(reqObj.getMobilenumber());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			//End DefectId:20150706
//		}
//		
//		catch(CustomFault e)
//		{
//			businessError.error("Custom Fault: "+ e.getFaultInfo());
//		}
		
		//DF23082019:Abhishek::commented the logic as it was not used in getAssetDashboardDetails() of AssetDetailsBO class
		/*DomainServiceImpl domainService = new DomainServiceImpl();
		ContactEntity contact = domainService.getContactDetails(reqObj.getLoginId());
		
		if(contact==null || contact.getContact_id()==null)
		{
			throw new CustomFault("Invalid LoginId");
		}*/
		
		//check whether the page number is specified
		if(pageNumber==0)
		{
			pageNumber=1;
		}
		
		//DF23082019:Abhishek::commented the logic as it was not used in getAssetDashboardDetails() of AssetDetailsBO class		
		//If the user is tenancyAdmin
//		if(contact.getIs_tenancy_admin()==1)
//		{
			iLogger.info("USER IS TENANCY ADMIN LoginId::"+reqObj.getLoginId());
			 assetDashboardImpList = assetDetails.getAssetDashboardDetails(reqObj.getUserTenancyIdList(), 
					reqObj.getTenancyIdList(), reqObj.getSerialNumber(),nickName, reqObj.getMachineProfileIdList(), reqObj.getModelList(), 
					reqObj.getMachineGroupIdList(),reqObj.getMachineGroupTypeIdList(), reqObj.getAlertTypeIdList(), 
					reqObj.getAlertSeverityList(), pageNumber, reqObj.getLandmarkIdList(),reqObj.getLandmarkCategoryIdList(),reqObj.isOwnStock(),reqObj.getMobilenumber(),reqObj.getLoginId());
//		}
		
		
		//if the user is not tenancy admin
		/*else
		{
			List<Integer> customMachineGroupIdList = new LinkedList<Integer>();
			if( (reqObj.getMachineGroupTypeIdList()==null || reqObj.getMachineGroupTypeIdList().isEmpty()) && 
			  (reqObj.getMachineGroupIdList()==null || reqObj.getMachineGroupIdList().isEmpty()) )
			{
				 Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			     session.beginTransaction();
			     
			     try
			     {
			    	 Query query = session.createQuery("from GroupUserMapping where contact_id='"+contact.getContact_id()+"'");
			    	 Iterator itr = query.list().iterator();
			    	 while(itr.hasNext())
			    	 {
			    		 GroupUserMapping groupUser = (GroupUserMapping) itr.next();
			    		 customMachineGroupIdList.add(groupUser.getGroup_id().getGroup_id());
			    	 }
			    	 iLogger.info(reqObj.getLoginId() +"Inside Impl- Dashboard3");
			     }
			     
			      
			    catch(Exception e)
				{
					fLogger.fatal("Exception :"+e);
				}
		            
		        finally
		        {
		             if(session.getTransaction().isActive())
		             {
		                  session.getTransaction().commit();
		             }
		                  
		             if(session.isOpen())
		             {
		                  session.flush();
		                  session.close();
		             }
		                  
		         }
			}
			
			else
			{
				if(reqObj.getMachineGroupIdList()!=null)
					customMachineGroupIdList.addAll(reqObj.getMachineGroupIdList());
			}
			
			assetDashboardImpList = assetDetails.getAssetDashboardDetails(reqObj.getUserTenancyIdList(), 
					reqObj.getTenancyIdList(), reqObj.getSerialNumber(), reqObj.getMachineProfileIdList(), reqObj.getModelList(), 
					customMachineGroupIdList,reqObj.getMachineGroupTypeIdList(), reqObj.getAlertTypeIdList(), 
					reqObj.getAlertSeverityList(), pageNumber, reqObj.getLandmarkIdList(),reqObj.getLandmarkCategoryIdList(),reqObj.isOwnStock(),reqObj.getMobilenumber(),reqObj.getLoginId());
		}*/
		
		
		
	
		//set the response Object
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
		long responseFillTime = System.currentTimeMillis();
		for(int r=0; r<assetDashboardImpList.size(); r++)
		{
			AssetDashboardRespContract response = new AssetDashboardRespContract();
			response.setDueForService(assetDashboardImpList.get(r).getDueForService());
			response.setFuelLevel(assetDashboardImpList.get(r).getFuelLevel());
			response.setLatitude(assetDashboardImpList.get(r).getLatitude());
			response.setLifeHours(assetDashboardImpList.get(r).getLifeHours());
			response.setLongitude(assetDashboardImpList.get(r).getLongitude());
			
			//DF20161222 @Mamatha Time spent in Dual mode will be displayed on the Fleet General tab for a VIN
			response.setMachineStatus(assetDashboardImpList.get(r).getMachineStatus() + "|" 
			+ assetDashboardImpList.get(r).getEcModeHrs() +"|" + assetDashboardImpList.get(r).getPowerModeHrs());
			
			response.setNickName(assetDashboardImpList.get(r).getNickName());
			response.setNotes(assetDashboardImpList.get(r).getNotes());
			response.setSerialNumber(assetDashboardImpList.get(r).getSerialNumber());
			response.setProfileName(assetDashboardImpList.get(r).getProfileName());
			response.setModelName(assetDashboardImpList.get(r).getModelName());
			response.setConnectivityStatus(assetDashboardImpList.get(r).getConnectivityStatus());
			response.setExternalBatteryInVolts(assetDashboardImpList.get(r).getExternalBatteryInVolts());
			response.setHighCoolantTemperature(assetDashboardImpList.get(r).getHighCoolantTemperature());
			response.setLowEngineOilPressure(assetDashboardImpList.get(r).getLowEngineOilPressure());
			response.setExternalBatteryStatus(assetDashboardImpList.get(r).getExternalBatteryStatus());
			response.setEngineTypeName(assetDashboardImpList.get(r).getEngineTypeName());
			
			//DefectID: DF20131212 - Rajani Nagaraju - To return the last communicated timestamp of the machine.
			response.setLastReportedTime(assetDashboardImpList.get(r).getLastReportedTime());
			
			//DefectId:201402076 Engine_Status newParameter added 2014-02-06 @Suprava 
			response.setEngineStatus(assetDashboardImpList.get(r).getEngineStatus());
			
			//DefectId:20140211 -AssetImageFileName added as new Parameter @Suprava
			response.setAssetImage(assetDashboardImpList.get(r).getAssetImage());
			
			//DefectId:20141208 - Rajani Nagaraju - Return the last Pkt Received time (Either through GPRS / SMS )
			response.setLastPktReceivedTime(assetDashboardImpList.get(r).getLastPktReceivedTime());
			
			//DF20180808-KO369761- Sending asset country code to the UI(Appending country code to the machine status
			if(r != assetDashboardImpList.size()-1){
				if(response.getSerialNumber() !=null)
					response.setSerialNumber(response.getSerialNumber()+"|"+assetDashboardImpList.get(r).getCountryCode());
				else
					response.setSerialNumber("|"+assetDashboardImpList.get(r).getCountryCode());
			}
			responseList.add(response);
			
			
		}
		long endTime=System.currentTimeMillis();
		iLogger.info(" AssetDashBoard Impl Ending loginid::"+reqObj.getLoginId()+" response filling time::"+(endTime-responseFillTime));
		iLogger.info(" AssetDashBoard Impl Ending loginid::"+reqObj.getLoginId()+" impl time::"+(endTime-startTime));
		
		return responseList;
	}
	
	public String getEcModeHrs() {
		return ecModeHrs;
	}
	public void setEcModeHrs(String ecModeHrs) {
		this.ecModeHrs = ecModeHrs;
	}
	public String getPowerModeHrs() {
		return powerModeHrs;
	}
	public void setPowerModeHrs(String powerModeHrs) {
		this.powerModeHrs = powerModeHrs;
	}
}
