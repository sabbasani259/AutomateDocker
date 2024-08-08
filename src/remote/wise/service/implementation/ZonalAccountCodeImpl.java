/**
 * 
 */
package remote.wise.service.implementation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.logging.log4j.Logger;

import remote.wise.businessobject.UserDetailsBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.service.datacontract.ZonalAccountReqContract;
import remote.wise.service.datacontract.ZonalAccountRespContract;

/**
 * @author roopn5
 *
 */
public class ZonalAccountCodeImpl {
	
	Map<String,TreeMap<String,String>> zoneDealerMap;
	Map<String,String> zoneMap;
	public List<ZonalAccountRespContract> getZones(ZonalAccountReqContract reqObj) throws CustomFault{
		
		Logger bLogger = BusinessErrorLoggerClass.logger;
		
		
		if(reqObj.getLoginId() == null || reqObj.getLoginId().equals("")){
			bLogger.error("Login ID is not provided.");
    		throw new CustomFault("Provide Login ID.");    		
    	}
    	if(reqObj.getLoginTenancyId()== 0){
    		bLogger.error("Tenancy Id is not provided.");
    		throw new CustomFault("Provide tenancy id of user.");    		
    	}
    	
    	ZonalAccountRespContract  respEntity=null;
		List<ZonalAccountRespContract> dealersList=new ArrayList<ZonalAccountRespContract>();
		
		UserDetailsBO bo=new UserDetailsBO();
		
		ZonalAccountCodeImpl impl=bo.getZonalAccount(reqObj.getLoginId(),reqObj.getLoginTenancyId());
		zoneDealerMap = impl.getZoneDealerMap();
		zoneMap = impl.getZoneMap();
		
		if(!zoneMap.isEmpty()){
			for (String zoneAccCode : zoneDealerMap.keySet() ) {
				respEntity=new ZonalAccountRespContract();
				respEntity.setZoneAccountCode(zoneAccCode);
				respEntity.setZoneAccountName(zoneMap.get(zoneAccCode));
				respEntity.setDealerMap(zoneDealerMap.get(zoneAccCode));  		  
				dealersList.add(respEntity);
	  		} 
		}		
		return dealersList;
		
	
	}
	
	/**
	 * @return the zoneDealerMap
	 */
	public Map<String, TreeMap<String, String>> getZoneDealerMap() {
		return zoneDealerMap;
	}

	/**
	 * @param zoneDealerMap the zoneDealerMap to set
	 */
	public void setZoneDealerMap(Map<String, TreeMap<String, String>> zoneDealerMap) {
		this.zoneDealerMap = zoneDealerMap;
	}

	/**
	 * @return the zoneMap
	 */
	public Map<String, String> getZoneMap() {
		return zoneMap;
	}
	/**
	 * @param zoneMap the zoneMap to set
	 */
	public void setZoneMap(Map<String, String> zoneMap) {
		this.zoneMap = zoneMap;
	}

}
