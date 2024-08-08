package remote.wise.service.implementation;

import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import remote.wise.businessobject.ActiveMachineListReportBO;
import remote.wise.businessobject.MachineCommunicationReportBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
import remote.wise.service.datacontract.ActiveMachineListInputContract;
import remote.wise.service.datacontract.ActiveMachineListOutputContract;
import remote.wise.service.datacontract.MachineCommunicationOutputContract;
//import remote.wise.util.WiseLogger;

public class ActiveMachineListImpl {

	//public static WiseLogger businessError = WiseLogger.getLogger("ActiveMachineListImpl:","businessError");
	public List<ActiveMachineListOutputContract> getActiveMachine(ActiveMachineListInputContract reqObj)throws CustomFault {
		// TODO Auto-generated method stub
		Logger bLogger = BusinessErrorLoggerClass.logger;
    	
		List<ActiveMachineListOutputContract> respList = new LinkedList<ActiveMachineListOutputContract>();
		try
		{
			if(reqObj.getToDate()==null)
			{
				throw new CustomFault("FromDate and Todate should be specified");
			}
		ActiveMachineListReportBO activeMachineListReportBO= new ActiveMachineListReportBO();
		List<ActiveMachineListReportBO> activeMachineListBOList =  activeMachineListReportBO.getactiveMachineList(reqObj.getToDate());
		
		for(int i=0;i<activeMachineListBOList.size();i++)
		{	
			ActiveMachineListOutputContract respContractObj=new ActiveMachineListOutputContract();
		
			respContractObj.setSerialNumber(activeMachineListBOList.get(i).getSerialNumber());
			respContractObj.setInstallDate(activeMachineListBOList.get(i).getInstallDate());
			respContractObj.setProfile(activeMachineListBOList.get(i).getProfile());
			respContractObj.setModel(activeMachineListBOList.get(i).getModel());
			respContractObj.setRollOffDate(activeMachineListBOList.get(i).getRollOffDate());
			respList.add(respContractObj);
		}
		}
		catch(CustomFault e)
		{
			bLogger.error("Custom Fault: "+ e.getFaultInfo());
		}
		return respList;
	
	}

}
