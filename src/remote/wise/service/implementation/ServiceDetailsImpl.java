package remote.wise.service.implementation;

import java.util.LinkedList;
import java.util.List;

import remote.wise.businessobject.ServiceDetailsBO;
import remote.wise.exception.CustomFault;

import remote.wise.service.datacontract.ServiceScheduleReqContract;
import remote.wise.service.datacontract.ServiceScheduleRespContract;

 /**
  * 
  * @author tejgm
  * Method will get details on Scheduling services on machine
  */


public class ServiceDetailsImpl {
/**
 * 
 * @param serviceSchedule
 * @return respListServiceSchedule
 * @throws CustomFault
 */
	
	/*public List<ServiceScheduleRespContract> getServiceScheduleDetails(ServiceScheduleReqContract serviceSchedule)throws CustomFault
	
	{
		List<ServiceScheduleRespContract> respListServiceSchedule = new LinkedList<ServiceScheduleRespContract>();
		ServiceDetailsBO serviceBo = new ServiceDetailsBO();
		List<ServiceDetailsBO> serviceDetailsBO =  serviceBo.getServiceDetails(serviceSchedule.getSerialNumber());
		for(int i=0;i<serviceDetailsBO.size();i++){
			ServiceScheduleRespContract respObj = new ServiceScheduleRespContract();
			respObj.setDealerName(serviceDetailsBO.get(i).getDealerName());
			respObj.setScheduleName(serviceDetailsBO.get(i).getScheduleName());
			respObj.setServiceName(serviceDetailsBO.get(i).getServiceName());
			respObj.setHoursToNextSvc(serviceDetailsBO.get(i).getHoursToNextService());
			respObj.setServiceDate(serviceDetailsBO.get(i).getServiceDate().toString());
			respListServiceSchedule.add(respObj);
		}
		return respListServiceSchedule;
		
	}*/
}
