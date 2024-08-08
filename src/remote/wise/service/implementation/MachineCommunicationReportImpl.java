package remote.wise.service.implementation;

import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import remote.wise.businessobject.MachineCommunicationReportBO;
import remote.wise.businessobject.MachineSMSReportBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
import remote.wise.service.datacontract.MachineCommunicationInputContract;
import remote.wise.service.datacontract.MachineCommunicationOutputContract;
import remote.wise.service.datacontract.MachineSMSInputContract;
import remote.wise.service.datacontract.MachineSMSOutputContract;
//import remote.wise.util.WiseLogger;


public class MachineCommunicationReportImpl {
	
	//public static WiseLogger businessError = WiseLogger.getLogger("MachineCommunicationReportImpl:","businessError");
	public List< MachineCommunicationOutputContract> getMachineCommunication( MachineCommunicationInputContract MachineCommreq)throws CustomFault
	{
		Logger bLogger = BusinessErrorLoggerClass.logger;
	
		List<MachineCommunicationOutputContract> respList = new LinkedList<MachineCommunicationOutputContract>();
		try
		{
			if(MachineCommreq.getFromDate()==null && MachineCommreq.getToDate()==null)
			{
				throw new CustomFault("FromDate and Todate should be specified");
			}
			
		}
		catch(CustomFault e)
		{
			bLogger.error("Custom Fault: "+ e.getFaultInfo());
		}
		
		MachineCommunicationReportBO machineCommunicationReportBO= new MachineCommunicationReportBO();
		List<MachineCommunicationReportBO> machineCommunicationReportBOList =  machineCommunicationReportBO.getmachineCommunicationDetail(MachineCommreq.getFromDate(),MachineCommreq.getToDate());
		
		for(int i=0;i<machineCommunicationReportBOList.size();i++)
		{	MachineCommunicationOutputContract respContractObj=new MachineCommunicationOutputContract();
		
			respContractObj.setAlertType(machineCommunicationReportBOList.get(i).getAlertType());
			respContractObj.setDateTime(machineCommunicationReportBOList.get(i).getDateTime());
			respContractObj.setSerialNumber(machineCommunicationReportBOList.get(i).getSerialNumber());
			respList.add(respContractObj);
		}
		return respList;
	}


}
