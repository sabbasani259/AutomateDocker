package remote.wise.service.implementation;

import java.util.LinkedList;
import java.util.List;

import remote.wise.businessobject.ConfigAppBO;
import remote.wise.exception.CustomFault;
import remote.wise.service.datacontract.ConfigAppReqContract;
import remote.wise.service.datacontract.ConfigAppRespContract;


public class ConfigAppImpl {
	
	public List<ConfigAppRespContract> getConfigAppSettings(ConfigAppReqContract reqObj)throws CustomFault
	{
		List<ConfigAppRespContract> configAppResponse=new LinkedList<ConfigAppRespContract>();
		ConfigAppBO configBO=new ConfigAppBO();
		List<ConfigAppBO> ConfigDetail=configBO.getConfigAppSettings(reqObj.getConfiguration_id());
		for(int i=0;i<ConfigDetail.size();i++)
		{
			ConfigAppRespContract ConfigAppObjResponseObj=new ConfigAppRespContract();
			
			ConfigAppObjResponseObj.setConfiguration_id(ConfigDetail.get(i).getConfiguration_id());
			ConfigAppObjResponseObj.setServices(ConfigDetail.get(i).getServices());	
			ConfigAppObjResponseObj.setStatus(ConfigDetail.get(i).isStatus());
			ConfigAppObjResponseObj.setModifiedBy(ConfigDetail.get(i).getModifiedBy());
			ConfigAppObjResponseObj.setModifiedOn(ConfigDetail.get(i).getModifiedOn());
			
			configAppResponse.add(ConfigAppObjResponseObj);
		}
		return configAppResponse;
	}
	
	public String setConfigAppSettings(List<ConfigAppRespContract> reqObj)throws CustomFault
	{
		String value=null;
		ConfigAppBO configBO=new ConfigAppBO();		
		for(int i=0;i<reqObj.size();i++){
			
			int congifId=reqObj.get(i).getConfiguration_id();
			String name=reqObj.get(i).getModifiedBy();
			String serviceName=reqObj.get(i).getServices();
			boolean status=reqObj.get(i).isStatus();
			value=configBO.setConfigAppSettings(congifId,serviceName,status,name);
		}
		return value;
	}
}
