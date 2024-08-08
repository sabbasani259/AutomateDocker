/**
 * 
 */
package remote.wise.service.implementation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import remote.wise.businessobject.UserDetailsBO;
import remote.wise.exception.CustomFault;
import remote.wise.service.datacontract.CityRespContract;

/**
 * @author roopn5
 *
 */
public class CityImpl {
	private int cityId;
	private String cityName;
	/**
	 * @return the cityId
	 */
	public int getCityId() {
		return cityId;
	}
	/**
	 * @param cityId the cityId to set
	 */
	public void setCityId(int cityId) {
		this.cityId = cityId;
	}
	/**
	 * @return the cityName
	 */
	public String getCityName() {
		return cityName;
	}
	/**
	 * @param cityName the cityName to set
	 */
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	
	public List<CityRespContract> getCities(String stateId)throws CustomFault
	{
		
		
		List<CityRespContract> respList = new ArrayList<CityRespContract>();
		UserDetailsBO userBO=new UserDetailsBO();
		List<CityImpl> CityImplList = userBO.getCities(stateId);	
		
		CityRespContract respObj = null;
		CityImpl implObj = null;
		Iterator<CityImpl> listIterator=CityImplList.iterator();
		while(listIterator.hasNext()){			
			implObj = listIterator.next();
			respObj = new CityRespContract();
			respObj.setCityId(implObj.getCityId());
			respObj.setCityName(implObj.getCityName());
			respList.add(respObj);
		}		
		return respList;		
	}
	
	public String getCitiesForRESTService(String stateId){
		String result = null;
		
		
		UserDetailsBO userBO=new UserDetailsBO();
		List<CityImpl> CityImplList = null;
		try {
			CityImplList = userBO.getCities(stateId);
		} catch (CustomFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		CityRespContract respObj = null;
		CityImpl implObj = null;
		
		HashMap<String,String> cityMap=new HashMap<String,String>();
		JSONObject jsonobj=new JSONObject();
		JSONArray jarry=new JSONArray();
		
		
		Iterator<CityImpl> listIterator=CityImplList.iterator();
		while(listIterator.hasNext()){			
			implObj = listIterator.next();
			cityMap.put(String.valueOf(implObj.getCityId()), implObj.getCityName());
		}	
		jsonobj.putAll(cityMap);
		jarry.add(jsonobj);
		
		  result=jarry.toString();
		return result;
	}

}
