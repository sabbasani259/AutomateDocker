package remote.wise.service.implementation;

import java.util.LinkedList;
import java.util.List;

import remote.wise.businessentity.ProductEntity;
import remote.wise.businessobject.ServiceDetailsBO;
import remote.wise.exception.CustomFault;
import remote.wise.service.datacontract.MasterServiceScheduleRequestContract;
import remote.wise.service.datacontract.MasterServiceScheduleResponseContract;
/** Implementation class to set and get the serviceScheduleId,serviceName,scheduleName,durationSchedule,engineHoursSchedule for the specified productId
 * @author jgupta41
 *
 */
public class MasterServiceScheduleImpl {
	  /** This method gets serviceName,scheduleName,durationSchedule,engineHoursSchedule for the specified assetTypeId,engineTypeId,assetGroupId
	    * @param reqObj Get the details of  assetTypeId,engineTypeId,assetGroupId,serviceName by passing the same to this request Object
	    * @return Returns respObj for the specified assetTypeId,engineTypeId and assetGroupId or serviceName 
	    * @throws CustomFault
	    */
	public List<MasterServiceScheduleResponseContract> getMasterServiceScheduleDetails(MasterServiceScheduleRequestContract reqObj)throws CustomFault
	{
		List<MasterServiceScheduleResponseContract> masterResponse=new LinkedList<MasterServiceScheduleResponseContract>();
		ServiceDetailsBO serviceBO=new ServiceDetailsBO();
		List<ServiceDetailsBO> ServiceDetail=serviceBO.getMasterServiceScheduleDetails(reqObj.getAssetTypeId(),reqObj.getEngineTypeId(),reqObj.getAssetGroupId(),reqObj.getScheduleName());
		for(int i=0;i<ServiceDetail.size();i++)
		{
			MasterServiceScheduleResponseContract masterResponseObj=new MasterServiceScheduleResponseContract();
			
			masterResponseObj.setServiceName(ServiceDetail.get(i).getServiceName());
		    masterResponseObj.setScheduleName(ServiceDetail.get(i).getScheduleName());			
						
			masterResponseObj.setDurationSchedule(ServiceDetail.get(i).getDurationSchedule());
			masterResponseObj.setEngineHoursSchedule(ServiceDetail.get(i).getEngineHoursSchedule());
			masterResponseObj.setDbmsPartCode(ServiceDetail.get(i).getDbmsPartCode());
			masterResponseObj.setAssetGroupId(ServiceDetail.get(i).getAssetGroupId());
			masterResponseObj.setAssetTypeId(ServiceDetail.get(i).getAssetTypeId());
			masterResponseObj.setEngineTypeId(ServiceDetail.get(i).getEngineTypeId());
			masterResponseObj.setAsset_group_name(ServiceDetail.get(i).getAsset_group_name());
			masterResponseObj.setAsset_type_name(ServiceDetail.get(i).getAsset_type_name());
			masterResponseObj.setEngineTypeName(ServiceDetail.get(i).getEngineTypeName());
			masterResponse.add(masterResponseObj);
		}
		return masterResponse;
	}
	/** This method sets the Service Schedule details of a Product
	 * @param servicePlan - Standard Warranty, Extended Warranty, or annual service contract , or 2000 hrs
	 * @param serviceName Name of the service like  First Free Service
	 * @param scheduleName Name of the service schedule such as  Model - Backhoe Loader - JCB Engine
	 * @param dbmsPartCode dbmsPartCode associated with the schedule
	 * @param assetGroupCode profile Code
	 * @param assetTypeCode model Code
	 * @param engineTypeCode Engine Type Code
	 * @param engineHours Schedule in terms of Engine Hours
	 * @param days Schedule in terms of days
	 * @return Returns the status String
	 */
	public String setMasterServiceScheduleDetails(String servicePlan, String serviceName, String scheduleName, String dbmsPartCode,
										String assetGroupCode,String assetTypeCode,String engineTypeCode,String engineHours,String days)
	{
		String status = "SUCCESS";
		ServiceDetailsBO serviceBO=new ServiceDetailsBO();
		
		//Check for Mandatory Parameters
		if(dbmsPartCode==null || assetGroupCode==null || assetTypeCode==null || engineTypeCode==null || engineHours==null || days==null ||
				dbmsPartCode.trim()==null || assetGroupCode.trim()==null || assetTypeCode.trim()==null || engineTypeCode.trim()==null ||
				engineHours.trim()==null || days.trim()==null )
		{
				status = "FAILURE";
				return status;
		}
		
		String chkdbmsPartCode = dbmsPartCode.replaceAll("\\s","") ;
		String chkAssetGroupCode = assetGroupCode.replaceAll("\\s","") ;
		String chkAssetTypeCode = assetTypeCode.replaceAll("\\s","") ;
		String chkEngineTypeCode = engineTypeCode.replaceAll("\\s","") ;
		String chkEngineHours = engineHours.replaceAll("\\s","") ;
		String chkDays = days.replaceAll("\\s","") ;
		
		if( (!(chkdbmsPartCode.length()>0))|| (!(chkAssetGroupCode.length()>0)) || (!(chkAssetTypeCode.length()>0)) ||
				(!(chkEngineTypeCode.length()>0)) || (!(chkEngineHours.length()>0)) || (!(chkDays.length()>0)) )
		{
			status = "FAILURE";
			return status;
		}
		
		status=serviceBO.setMasterServiceScheduleDetails(servicePlan,serviceName, scheduleName, 
				dbmsPartCode, assetGroupCode, assetTypeCode, engineTypeCode, engineHours, days);
		
		return status;
	}
}
