package remote.wise.service.implementation;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import remote.wise.businessobject.FuelUtilizationDetailBO;
import remote.wise.exception.CustomFault;
import remote.wise.service.datacontract.FuelUtilizationDetailReqContract;
import remote.wise.service.datacontract.FuelUtilizationDetailRespContract;
//import remote.wise.util.WiseLogger;
/**
 * FuelUtilizationDetailImpl will allow to List ofFuel Utilization Details for specified LoginId and SerialNumber
 * @author jgupta41
 *
 */

public class FuelUtilizationDetailImpl {
	//public static WiseLogger infoLogger = WiseLogger.getLogger("FuelUtilizationDetailImpl:","info");
	private String serialNumber;
	private String period;
	//added by smitha on 13th sept 2013
//	HashMap<Integer,String> hourFuelLevelMap;
	TreeMap<Integer,String> hourFuelLevelMap;
	
	/**
	 * @return the hourFuelLevelMap
	 */
	public TreeMap<Integer, String> getHourFuelLevelMap() {
		return hourFuelLevelMap;
	}
	/**
	 * @param hourFuelLevelMap the hourFuelLevelMap to set
	 */
	public void setHourFuelLevelMap(TreeMap<Integer, String> hourFuelLevelMap) {
		this.hourFuelLevelMap = hourFuelLevelMap;
	}
	//ended on 13th sept 2013
	/*private Timestamp timeDuration;
	private String fuelLevel;
	public String getFuelLevel() {
		return fuelLevel;
	}
	public void setFuelLevel(String fuelLevel) {
		this.fuelLevel = fuelLevel;
	}*/
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
	}
	
	
	
	/*public Timestamp getTimeDuration() {
		return timeDuration;
	}
	public void setTimeDuration(Timestamp timeDuration) {
		this.timeDuration = timeDuration;
	}*/
	//*******************************************Get Fuel Utilization Detail for given LoginId and SerialNumber ********************
	/**
	 * This method will return List of  Fuel Utilization Detail for given LoginId and SerialNumber
	 * @param fuelUtilizationReq:Get Fuel Utilization Detail for given LoginId and SerialNumber
	 * @return respList:Returns List of Fuel Utilization Details 
	 * @throws CustomFault: custom exception is thrown when the LoginId ,Period,SerialNumber is not specified, SerialNumber is invalid or not specified
	 */
	public List<FuelUtilizationDetailRespContract> getFuelUtilizationDetail(FuelUtilizationDetailReqContract fuelUtilizationReq)throws CustomFault
	{
		List<FuelUtilizationDetailRespContract> respList = new LinkedList<FuelUtilizationDetailRespContract>();
	FuelUtilizationDetailBO fuelUtilizationDetailBO=new FuelUtilizationDetailBO();	
	List<FuelUtilizationDetailImpl> fuelUtilizationDetailImpl = new LinkedList<FuelUtilizationDetailImpl>();
	try {
		fuelUtilizationDetailImpl=fuelUtilizationDetailBO.getFuelUtilizationDetailList(fuelUtilizationReq.getPeriod(),fuelUtilizationReq.getLoginId(),fuelUtilizationReq.getSerialNumber());
	} catch (Exception e) {
		e.printStackTrace();
	}
			

	for(int i=0;i<fuelUtilizationDetailImpl.size();i++)
	{	FuelUtilizationDetailRespContract  respContractObj=new FuelUtilizationDetailRespContract();
		respContractObj.setSerialNumber(fuelUtilizationDetailImpl.get(i).getSerialNumber());
		respContractObj.setPeriod(fuelUtilizationDetailImpl.get(i).getPeriod());
		/*respContractObj.setTimeDuration(fuelUtilizationDetailImpl.get(i).getTimeDuration().toString());
		respContractObj.setFuelLevel(fuelUtilizationDetailImpl.get(i).getFuelLevel());*/
		respContractObj.setHourFuelLevelMap(fuelUtilizationDetailImpl.get(i).getHourFuelLevelMap());
		respList.add(respContractObj);
	
	}
	
	
	return respList;
	}
	//*******************************************End of Get Fuel Utilization Detail for given LoginId and SerialNumber ********************
	
}
