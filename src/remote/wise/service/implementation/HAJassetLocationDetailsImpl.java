/**
 * 
 */
package remote.wise.service.implementation;

import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import remote.wise.businessobject.HAJassetDetailsBO;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.HAJAssetDetailsRespContract;
import remote.wise.service.datacontract.HAJAssetLocationDetailsRespContract;
//import remote.wise.util.WiseLogger;

/**
 * @author sunayak
 *
 */
public class HAJassetLocationDetailsImpl {
//	public static WiseLogger businessError = WiseLogger.getLogger("HAJassetLocationDetailsImpl:","businessError");
	private String serialNumber;
    private String latitude;
    private String longitude;
	 
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

	public List<HAJAssetLocationDetailsRespContract> getHAJlocationDetails() {
		
		 Logger infoLogger = InfoLoggerClass.logger;
			Logger fatalError = FatalLoggerClass.logger;
			Logger businessError = BusinessErrorLoggerClass.logger;
		// TODO Auto-generated method stub
		List<HAJAssetLocationDetailsRespContract> listRespObj=new LinkedList<HAJAssetLocationDetailsRespContract>();
		
			HAJassetDetailsBO HAJassetDetailsObj=new HAJassetDetailsBO();
			List<HAJassetLocationDetailsImpl> implObj=HAJassetDetailsObj.getHAJlocationDetail();
	
			HAJAssetLocationDetailsRespContract respObj=null;
			for(int i=0;i<implObj.size();i++)
			{
					respObj=new HAJAssetLocationDetailsRespContract();
					respObj.setVin(implObj.get(i).getSerialNumber());
					respObj.setLatitude(implObj.get(i).getLatitude());
					respObj.setLongitude(implObj.get(i).getLongitude());
					listRespObj.add(respObj);
			}
		
		return listRespObj;
	}

	public String setHAJassetLocationDetailsData() {
		// TODO Auto-generated method stub
		HAJassetDetailsBO HAJassetDetailsObj=new HAJassetDetailsBO();
		String flag=HAJassetDetailsObj.setHAJassetLocationDetails(); 
		return flag;
	}

	public void updateHAJLocationDetail() {
		// TODO Auto-generated method stub
		HAJassetDetailsBO HAJassetDetailsObj=new HAJassetDetailsBO();
		HAJassetDetailsObj.updateHAJassetLocationDetail();
	}

}
