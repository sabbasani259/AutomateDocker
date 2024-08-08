package remote.wise.service.implementation;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessentity.ContactEntity;
import remote.wise.businessentity.GroupUserMapping;
import remote.wise.businessobject.MapBO2;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
import remote.wise.service.datacontract.MapReqContract;
import remote.wise.service.datacontract.MapRespContract;
import remote.wise.util.HibernateUtil;
//import remote.wise.util.WiseLogger;

/**
 * MapImpl will allow to List of Map Details for specified LoginId and List of SerialNumbers and filters if any
 * @author jgupta41
 *
 */

public class MapImpl {
	
	/*public static WiseLogger businessError = WiseLogger.getLogger("AssetDashboardImpl:","businessError");
	public static WiseLogger fatalError = WiseLogger.getLogger("AssetDashboardImpl:","fatalError");*/
	
	private String Nickname;
	private String TotalMachineHours;
	private Timestamp OperatingStartTime;
	private Timestamp OperatingEndTime;
	private String Latitude;
	private String Longitude;
	private Long ActiveAlert;
	private String EngineStatus;
	private String SerialNumber;
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
	public Timestamp getOperatingStartTime() {
		return OperatingStartTime;
	}
	public void setOperatingStartTime(Timestamp operatingStartTime) {
		OperatingStartTime = operatingStartTime;
	}
	public Timestamp getOperatingEndTime() {
		return OperatingEndTime;
	}
	public void setOperatingEndTime(Timestamp operatingEndTime) {
		OperatingEndTime = operatingEndTime;
	}
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
	
	//*******************************************Get Map Details for given LoginId and List of SerialNumbers ********************

	
	/**
	 * This method will return List of Map Details for given LoginId and List of SerialNumbers and filters if any
	 * @param mapReq:Get Map Details for given LoginId and List of SerialNumbers
	 * @return respList:Returns List of Map Details
	 * @throws CustomFault:custom exception is thrown when the LoginId ,Period,SerialNumber is not specified, SerialNumber is invalid or not specified
	 */
	public List<MapRespContract> getMap(MapReqContract mapReq)throws CustomFault
	{
		
    	Logger fLogger = FatalLoggerClass.logger;
    	//Logger rLogger = RejectedPktLoggerClass.logger;
		
		List<MapRespContract> respList = new LinkedList<MapRespContract>();
		MapBO2 mapBo=new MapBO2();
		List<MapImpl> mapImpl = new LinkedList<MapImpl>();
		DomainServiceImpl domainService = new DomainServiceImpl();
		ContactEntity contact = domainService.getContactDetails(mapReq.getLoginId());
		if(contact==null || contact.getContact_id()==null)
		{
			throw new CustomFault("Invalid LoginId");
		}
		
		//If the user is tenancyAdmin
		if(contact.getIs_tenancy_admin()==1)
		{
		mapImpl=mapBo.getMap(contact, mapReq.getSerialNumberList(),mapReq.getAlertSeverityList(),mapReq.getAlertTypeIdList(),mapReq.getLandmark_IdList(),mapReq.getLandmarkCategory_IdList(),mapReq.getTenancy_ID(),mapReq.getMachineGroupIdList(),mapReq.getMachineProfileIdList(),mapReq.getModelIdList(),mapReq.isOwnStock(),mapReq.getLoginUserTenancyList());
		}
		
		
		//if the user is not tenancy admin
		else{
		List<Integer> customMachineGroupIdList = new LinkedList<Integer>();
		if((mapReq.getMachineGroupIdList()==null || mapReq.getMachineGroupIdList().isEmpty()) )
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
			if(mapReq.getMachineGroupIdList()!=null)
				customMachineGroupIdList.addAll(mapReq.getMachineGroupIdList());
		}
		
		mapImpl=mapBo.getMap(contact, mapReq.getSerialNumberList(),mapReq.getAlertSeverityList(),mapReq.getAlertTypeIdList(),mapReq.getLandmark_IdList(),mapReq.getLandmarkCategory_IdList(),mapReq.getTenancy_ID(),customMachineGroupIdList,mapReq.getMachineProfileIdList(),mapReq.getModelIdList(),mapReq.isOwnStock(),mapReq.getLoginUserTenancyList());
		}
		for(int i=0;i<mapImpl.size();i++)
		{
			MapRespContract  respContractObj=new MapRespContract();
			respContractObj.setNickname(mapImpl.get(i).getNickname());
			if(mapImpl.get(i).getOperatingStartTime()!=null)
				respContractObj.setOperatingStartTime(mapImpl.get(i).getOperatingStartTime().toString());
			else{
				respContractObj.setOperatingStartTime("");
			}
			if(mapImpl.get(i).getOperatingEndTime()!=null)
				respContractObj.setOperatingEndTime(mapImpl.get(i).getOperatingEndTime().toString());
			else{
				respContractObj.setOperatingEndTime("");
			}
			respContractObj.setTotalMachineHours(mapImpl.get(i).getTotalMachineHours());
			respContractObj.setLatitude(mapImpl.get(i).getLatitude());
			respContractObj.setLongitude(mapImpl.get(i).getLongitude());
			respContractObj.setActiveAlert(mapImpl.get(i).getActiveAlert());
			respContractObj.setEngineStatus(mapImpl.get(i).getEngineStatus());
			respContractObj.setProfileCode(mapImpl.get(i).getProfileCode());
			respContractObj.setProfileName(mapImpl.get(i).getProfileName());
			respContractObj.setSeverity(mapImpl.get(i).getSeverity());
			if(mapImpl.get(i).getLastReportedTime()!=null){
				respContractObj.setLastReportedTime(mapImpl.get(i).getLastReportedTime());
			}			
			respContractObj.setSerialNumber(mapImpl.get(i).getSerialNumber());
			respList.add(respContractObj);
		
		}
		
  	    
		return respList;
	}
	//*******************************************End of Get Map Details for given LoginId and List of SerialNumbers ********************

	
	//************************************************* Map BO Optimization *****************************************************
	public List<MapRespContract> getNewMapDetails(MapReqContract mapReq,int mapMarkerKey,String countryCode)
	{
		List<MapImpl> mapImpl=null;
		List<MapRespContract> respList = new LinkedList<MapRespContract>();
		MapBO2 boObj = new MapBO2();
		Logger iLogger = InfoLoggerClass.logger;
		long startTime = System.currentTimeMillis();
		
		iLogger.info("Into the Implementaiton Class login id ::"+mapReq.getLoginId());
		if(mapReq.getSerialNumberList()!=null && mapReq.getSerialNumberList().size()>0)
		{
			iLogger.info("Map Service:"+mapReq.getLoginId()+":"+mapReq.getLoginUserTenancyList()+":"+mapReq.getSerialNumberList().get(0)+"" +
					":Invoke BO for a VIN");
			
			MapImpl implObj = boObj.getFleetMapDetails(mapReq.getSerialNumberList().get(0),mapReq.getLoginId(),mapReq.getMachineGroupIdList());
			mapImpl = new LinkedList<MapImpl>();
			mapImpl.add(implObj);
			iLogger.info("Map Service:"+mapReq.getLoginId()+":"+mapReq.getLoginUserTenancyList()+":"+mapReq.getSerialNumberList().get(0)+"" +
					":Response received in IMPL");
		}
		
		else
		{
			iLogger.info("Map Service:"+mapReq.getLoginId()+":"+mapReq.getLoginUserTenancyList()+"Page index:"+mapMarkerKey+":Invoke BO");
//			Keerthi : 2017.02.07 : asset profile and model filter for overview page
			mapImpl = boObj.getOverviewMapDetails(mapReq.getLoginId(), mapReq.getAlertSeverityList(), mapReq.getAlertTypeIdList(), 
					mapReq.getTenancy_ID(), mapReq.isOwnStock(), mapReq.getLoginUserTenancyList(), mapReq.getMachineProfileIdList(),mapReq.getModelIdList(),mapMarkerKey,countryCode,mapReq.getMachineGroupIdList());
			iLogger.info("Map Service:"+mapReq.getLoginId()+":"+mapReq.getLoginUserTenancyList()+":Response received in IMPL");
		}
		
		long responseStartTime = System.currentTimeMillis();
		for(int i=0;i<mapImpl.size();i++)
		{
			MapRespContract  respContractObj=new MapRespContract();
			respContractObj.setNickname(mapImpl.get(i).getNickname());
			if(mapImpl.get(i).getOperatingStartTime()!=null)
				respContractObj.setOperatingStartTime(mapImpl.get(i).getOperatingStartTime().toString());
			else
				respContractObj.setOperatingStartTime("");
			
			if(mapImpl.get(i).getOperatingEndTime()!=null)
				respContractObj.setOperatingEndTime(mapImpl.get(i).getOperatingEndTime().toString());
			else
				respContractObj.setOperatingEndTime("");
			
			respContractObj.setTotalMachineHours(mapImpl.get(i).getTotalMachineHours());
			respContractObj.setLatitude(mapImpl.get(i).getLatitude());
			respContractObj.setLongitude(mapImpl.get(i).getLongitude());
			respContractObj.setEngineStatus(mapImpl.get(i).getEngineStatus());
			respContractObj.setProfileName(mapImpl.get(i).getProfileName());
			respContractObj.setSeverity(mapImpl.get(i).getSeverity());
			respContractObj.setSerialNumber(mapImpl.get(i).getSerialNumber());
		
			respList.add(respContractObj);
	
		}
		long endTime=System.currentTimeMillis();
		
		iLogger.info("MapService login id :"+mapReq.getLoginId()+" time to fill the response in ms "+(endTime-responseStartTime));
		
		iLogger.info("Map Service:"+mapReq.getLoginId()+":"+mapReq.getLoginUserTenancyList()+":"+mapReq.getSerialNumberList()+" impl execution time " +
				"in ms:"+(endTime-startTime));
		//System.out.println("MapService MAP IMPL From New AMS Webservice Execution Time in ms:"+(endTime-startTime));
	   
		return respList;
	}
	
	//************************************************* Map BO Optimization *****************************************************
}
