package remote.wise.service.implementation;

import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import remote.wise.businessobject.ActiveMachineListReportBO;
import remote.wise.businessobject.MachineBillingReportBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
import remote.wise.service.datacontract.ActiveMachineListOutputContract;
import remote.wise.service.datacontract.MachineBillingReportInputContract;
import remote.wise.service.datacontract.MachineBillingReportOutputContract;
//import remote.wise.util.WiseLogger;

public class MachineBillingReportImpl {
//	public static WiseLogger businessError = WiseLogger.getLogger("MachineBillingReportImpl:","businessError");

	public List<MachineBillingReportOutputContract> getMachineReport(
			MachineBillingReportInputContract reqObj) {
		// TODO Auto-generated method stub
		Logger bLogger = BusinessErrorLoggerClass.logger;
		
		List<MachineBillingReportOutputContract> respList = new LinkedList<MachineBillingReportOutputContract>();
		try
		{
			if(reqObj.getFromDate()==null || reqObj.getToDate()==null)
			{
				throw new CustomFault("FromDate and Todate should be specified");
			}
			MachineBillingReportBO machineBillingReportBO= new MachineBillingReportBO();
		List<MachineBillingReportBO> machineBillingReportBOList =  machineBillingReportBO.machineBillingReportList(reqObj.getFromDate(),reqObj.getToDate());
		
		for(int i=0;i<machineBillingReportBOList.size();i++)
		{	
			MachineBillingReportOutputContract respContractObj=new MachineBillingReportOutputContract();
		
			respContractObj.setSerialNumber(machineBillingReportBOList.get(i).getSerialNumber());
			respContractObj.setInstallDate(machineBillingReportBOList.get(i).getInstallDate());
			respContractObj.setProfile(machineBillingReportBOList.get(i).getProfile());
			respContractObj.setModel(machineBillingReportBOList.get(i).getModel());
			respContractObj.setRollOffDate(machineBillingReportBOList.get(i).getRollOffDate());
			respContractObj.setOldNew(machineBillingReportBOList.get(i).getOldNew());
			respContractObj.setNewRolledMachine(machineBillingReportBOList.get(i).getNewRolledMachine());
			respContractObj.setActualMachineCount(machineBillingReportBOList.get(i).getActualMachineCount());
			respContractObj.setBillingCalculation(machineBillingReportBOList.get(i).getBillingCalculation());
			respContractObj.setPreviousBilledCount(machineBillingReportBOList.get(i).getPreviousBilledCount());
			respContractObj.setInvoicedAmount(machineBillingReportBOList.get(i).getInvoicedAmount());
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
