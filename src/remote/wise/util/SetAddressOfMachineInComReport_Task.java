package remote.wise.util;

import java.util.Map;
import java.util.concurrent.Callable;

import remote.wise.service.implementation.CommunicationReportEnhancedServiceImpl;

//This task helps in updating data(machine city,state and address) in com_rep_oem
public class SetAddressOfMachineInComReport_Task implements Callable<Integer>{

	Map<String,String> addressMap;
	public SetAddressOfMachineInComReport_Task(Map<String,String> addressMap)
	{
		this.addressMap=addressMap;
	}
	
	@Override
	public Integer call() throws Exception {
		System.out.println("address in com_oem "+addressMap);
		int machineUpdatedCount=	CommunicationReportEnhancedServiceImpl.setAddressOfMachineInComReport(addressMap);
		return machineUpdatedCount;	
	}
}
