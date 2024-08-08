package remote.wise.service.implementation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.logging.log4j.Logger;

////import org.apache.log4j.Logger;
import remote.wise.businessobject.UserDetailsBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
import remote.wise.service.datacontract.DealersUnderZoneReqContract;
import remote.wise.service.datacontract.DealersUnderZoneRespContract;
//import remote.wise.util.WiseLogger;

public class DealersUnderZoneImpl {
	
Map<Integer,TreeMap<Integer,String>> zoneDealerMap;
Map<Integer,String> zoneMap;

//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
/*public static WiseLogger infoLogger = WiseLogger.getLogger("DealersUnderZoneImpl:","info");
public static WiseLogger businessError = WiseLogger.getLogger("DealersUnderZoneImpl:","businessError");*/


public Map<Integer, TreeMap<Integer, String>> getZoneDealerMap() {
	return zoneDealerMap;
}

public void setZoneDealerMap(
		Map<Integer, TreeMap<Integer, String>> zoneDealerMap) {
	this.zoneDealerMap = zoneDealerMap;
}

/**
 * @return the zoneMap
 */
public Map<Integer, String> getZoneMap() {
	return zoneMap;
}

/**
 * @param zoneMap the zoneMap to set
 */
public void setZoneMap(Map<Integer, String> zoneMap) {
	this.zoneMap = zoneMap;
}

/**
 * method to get zones and dealers for logged-in user
 * @param reqObj
 * @return List<DealersUnderZoneRespContract>
 * @throws CustomFault
 */
	public List<DealersUnderZoneRespContract> getDealers(DealersUnderZoneReqContract reqObj) throws CustomFault{
		Logger bLogger = BusinessErrorLoggerClass.logger;
		
		if(reqObj.getLoginId() == null || reqObj.getLoginId().equals("")){
			bLogger.error("Login ID is not provided.");
    		throw new CustomFault("Provide Login ID.");    		
    	}
    	if(reqObj.getLoginTenancyId()== 0){
    		bLogger.error("Tenancy Id is not provided.");
    		throw new CustomFault("Provide tenancy id of user.");    		
    	}
    	
		DealersUnderZoneRespContract  respEntity=null;
		List<DealersUnderZoneRespContract> dealersList=new ArrayList<DealersUnderZoneRespContract>();
		UserDetailsBO bo=new UserDetailsBO();
		DealersUnderZoneImpl impl=bo.getDealersForZone(reqObj.getLoginId(),reqObj.getLoginTenancyId());
		zoneDealerMap = impl.getZoneDealerMap();
		zoneMap = impl.getZoneMap();
		
		if(!zoneMap.isEmpty()){
			for (int zoneId : zoneDealerMap.keySet() ) {
				respEntity=new DealersUnderZoneRespContract();
				respEntity.setZoneId(zoneId);
				respEntity.setZoneName(zoneMap.get(zoneId));
				respEntity.setDealerMap(zoneDealerMap.get(zoneId));  		  
				dealersList.add(respEntity);
	  		} 
		}		
		return dealersList;
	}
	
	

}
