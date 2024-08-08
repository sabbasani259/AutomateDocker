package remote.wise.service.implementation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;

////import org.apache.log4j.Logger;

import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
import remote.wise.service.datacontract.Landmark;
import remote.wise.service.datacontract.LandmarkCategory;
import remote.wise.service.datacontract.MachineGroupDetails;
import remote.wise.service.datacontract.MachineGroupTypeDetails;
import remote.wise.businessobject.UserDetailsBO;
import remote.wise.exception.CustomFault;
import remote.wise.service.datacontract.MGLandMarkReqContract;
import remote.wise.service.datacontract.MGLandMarkRespContract;
//import remote.wise.util.WiseLogger;

public class MGLandMarkDetailsImpl {
	
	Map<Integer,String> machineGroupTypeMap;
	Map<Integer,List<MachineGroupDetails>> machineGroupMap;	
	Map<Integer,String> landmarkCategory;	
	Map<Integer,List<Landmark>> landmarkMap;
	
/*	static Logger infoLogger = Logger.getLogger("infoLogger");
	static Logger businessError = Logger.getLogger("businessErrorLogger");*/
	
	//DefectId:1337 - Suprava - 20130923 - Log4j Changes - Using static logger object all throughout the application
	/*public static WiseLogger businessError = WiseLogger.getLogger("MGLandMarkDetailsImpl:","businessError");
	public static WiseLogger infoLogger = WiseLogger.getLogger("AssetDiagnosticImpl:","info");*/
	/**
	 * @return the machineGroupTypeMap
	 */
	public Map<Integer, String> getMachineGroupTypeMap() {
		return machineGroupTypeMap;
	}

	/**
	 * @param machineGroupTypeMap the machineGroupTypeMap to set
	 */
	public void setMachineGroupTypeMap(Map<Integer, String> machineGroupTypeMap) {
		this.machineGroupTypeMap = machineGroupTypeMap;
	}

	/**
	 * @return the machineGroupMap
	 */
	public Map<Integer, List<MachineGroupDetails>> getMachineGroupMap() {
		return machineGroupMap;
	}

	/**
	 * @param machineGroupMap the machineGroupMap to set
	 */
	public void setMachineGroupMap(
			Map<Integer, List<MachineGroupDetails>> machineGroupMap) {
		this.machineGroupMap = machineGroupMap;
	}	
	
	/**
	 * @return the landmarkMap
	 */
	public Map<Integer, List<Landmark>> getLandmarkMap() {
		return landmarkMap;
	}

	/**
	 * @param landmarkMap the landmarkMap to set
	 */
	public void setLandmarkMap(Map<Integer, List<Landmark>> landmarkMap) {
		this.landmarkMap = landmarkMap;
	}

	/**
	 * @return the landmarkCategory
	 */
	public Map<Integer, String> getLandmarkCategory() {
		return landmarkCategory;
	}

	/**
	 * @param landmarkCategory the landmarkCategory to set
	 */
	public void setLandmarkCategory(Map<Integer, String> landmarkCategory) {
		this.landmarkCategory = landmarkCategory;
	}
	
	public MGLandMarkRespContract getMGLandMarkDetails(MGLandMarkReqContract request) throws CustomFault{
		Logger bLogger = BusinessErrorLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
    	
		if(request.getLoginTenancyId() == 0){
			bLogger.error("Login ID is not provided.");
    		throw new CustomFault("Provide Login ID.");    		
    	}
		UserDetailsBO details=new UserDetailsBO();
		MGLandMarkDetailsImpl impl=details.getMGLandMarkDetails(request.getLoginTenancyId());
		MGLandMarkRespContract resp=new MGLandMarkRespContract();
		
        List<LandmarkCategory> landmarkCategoryList=null;
		landmarkCategory = impl.getLandmarkCategory();
		landmarkMap = impl.getLandmarkMap();
		
		//DF20180206 @Roopa NUll check handling
		if(landmarkMap!=null && landmarkCategory!=null){
		for(int landmarkCategoryID : landmarkMap.keySet()){
			if(landmarkCategoryList == null){
				landmarkCategoryList = new ArrayList<LandmarkCategory>();
			}
			LandmarkCategory lc=new LandmarkCategory();
			lc.setLandmarkCategoryId(landmarkCategoryID);
			lc.setLandmarkCategoryName(landmarkCategory.get(landmarkCategoryID));
			lc.setLandmark(landmarkMap.get(landmarkCategoryID));	
			landmarkCategoryList.add(lc);
		}
		resp.setLandmarkCategoryList(landmarkCategoryList);
		}
		
		List<MachineGroupTypeDetails> machineGroupTypeList=null;
		machineGroupTypeMap =impl.getMachineGroupTypeMap();
		machineGroupMap = impl.getMachineGroupMap();
		if(machineGroupTypeMap!=null && machineGroupMap!=null){
		for(int machineGroupTypeId : machineGroupTypeMap.keySet()){
			iLogger.info("group type id "+machineGroupTypeId);
			if(machineGroupTypeList == null){
				machineGroupTypeList = new ArrayList<MachineGroupTypeDetails>();
			}
			MachineGroupTypeDetails mgte=new MachineGroupTypeDetails();
			mgte.setMachineGroupTypeId(machineGroupTypeId);
			mgte.setMachineGroupTypeName(machineGroupTypeMap.get(machineGroupTypeId));
			mgte.setMachineGroupList(machineGroupMap.get(machineGroupTypeId));
			iLogger.info("get "+machineGroupMap.get(machineGroupTypeId));
			machineGroupTypeList.add(mgte);
		}
		
		iLogger.info("machineGroupTypeList "+machineGroupTypeList);
		resp.setMachineGroupTypeList(machineGroupTypeList);
		}
		return resp; 
	}

}
