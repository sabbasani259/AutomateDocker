/**
 * 
 */
package remote.wise.service.implementation;

import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import remote.wise.businessobject.ETLfactDataBO;
import remote.wise.businessobject.HAJassetDetailsBO;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.HAJAssetDetailsRespContract;
//import remote.wise.util.WiseLogger;

/**
 * @author sunayak
 *
 */
public class HAJassetDetailsImpl {

//	public static WiseLogger businessError = WiseLogger.getLogger("MachineHoursReportImpl:","businessError");
	
	private String serialNumber;
    private String assetGroupName;
    private String assetTypeName;
    private int primaryAccountId;
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
	 * @return the assetGroupName
	 */
	public String getAssetGroupName() {
		return assetGroupName;
	}
	/**
	 * @param assetGroupName the assetGroupName to set
	 */
	public void setAssetGroupName(String assetGroupName) {
		this.assetGroupName = assetGroupName;
	}
	/**
	 * @return the assetTypeName
	 */
	public String getAssetTypeName() {
		return assetTypeName;
	}
	/**
	 * @param assetTypeName the assetTypeName to set
	 */
	public void setAssetTypeName(String assetTypeName) {
		this.assetTypeName = assetTypeName;
	}
	/**
	 * @return the primaryAccountId
	 */
	public int getPrimaryAccountId() {
		return primaryAccountId;
	}
	/**
	 * @param primaryAccountId the primaryAccountId to set
	 */
	public void setPrimaryAccountId(int primaryAccountId) {
		this.primaryAccountId = primaryAccountId;
	}
	public List<HAJAssetDetailsRespContract> getHAJassetDetail() {
		
		 Logger infoLogger = InfoLoggerClass.logger;
			Logger fatalError = FatalLoggerClass.logger;
			Logger businessError = BusinessErrorLoggerClass.logger;
		// TODO Auto-generated method stub
		List<HAJAssetDetailsRespContract> listRespObj=new LinkedList<HAJAssetDetailsRespContract>();
		
			HAJassetDetailsBO HAJassetDetailsObj=new HAJassetDetailsBO();
			List<HAJassetDetailsImpl> implObj=HAJassetDetailsObj.getHAJassetDetailService();
	
			HAJAssetDetailsRespContract respObj=null;
			for(int i=0;i<implObj.size();i++)
			{
					respObj=new HAJAssetDetailsRespContract();
					respObj.setVin(implObj.get(i).getSerialNumber());
					respObj.setAccountid(implObj.get(i).getPrimaryAccountId());
					respObj.setProfilecode(implObj.get(i).getAssetGroupName());
					respObj.setModelcode(implObj.get(i).getAssetTypeName());
					listRespObj.add(respObj);
			}
		
		return listRespObj;
	}
	//******************************************Start of setHAJassetDetailsData************************************************************
	/**
	 *  This method will set to insert data in HAJassetSnapshot table for a each round off time.
	 * @return SUCCESS
	 */
	public String setHAJassetDetailsData() {
		// TODO Auto-generated method stub
		HAJassetDetailsBO HAJassetDetailsObj=new HAJassetDetailsBO();
		String flag=HAJassetDetailsObj.setHAJassetData(); 
		return flag;
	}
	public void updateHAJassetDetail() {
		// TODO Auto-generated method stub
		HAJassetDetailsBO HAJassetDetailsObj=new HAJassetDetailsBO();
		HAJassetDetailsObj.updateHAJassetDetailService();
	}
}
