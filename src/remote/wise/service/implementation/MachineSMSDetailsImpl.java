package remote.wise.service.implementation;

import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import remote.wise.businessobject.LandmarkCategoryBO;
import remote.wise.businessobject.MachineSMSReportBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
import remote.wise.service.datacontract.LandmarkDetailsReqContract;
import remote.wise.service.datacontract.LandmarkDetailsRespContract;
import remote.wise.service.datacontract.MachineSMSInputContract;
import remote.wise.service.datacontract.MachineSMSOutputContract;
//import remote.wise.util.WiseLogger;

public class MachineSMSDetailsImpl {

	//public static WiseLogger businessError = WiseLogger.getLogger("MachineSMSDetailsImpl:","businessError");
	public List< MachineSMSOutputContract> getMachineSMSReport( MachineSMSInputContract MachineSMSreq)throws CustomFault
	{
		Logger bLogger = BusinessErrorLoggerClass.logger;
		
		List<MachineSMSOutputContract> respList = new LinkedList<MachineSMSOutputContract>();
		try
		{
			if(MachineSMSreq.getFromDate()==null || MachineSMSreq.getToDate()==null)
			{
				bLogger.error("FromDate and Todate should be specified");
				throw new CustomFault("FromDate and Todate should be specified");
			}
		
		MachineSMSReportBO machineSMSReportBO= new MachineSMSReportBO();
		List<MachineSMSReportBO> machineSMSReportBOList =  machineSMSReportBO.getMachineSMSCount(MachineSMSreq.getFromDate(),MachineSMSreq.getToDate());
		
		for(int i=0;i<machineSMSReportBOList.size();i++)
		{	MachineSMSOutputContract respContractObj=new MachineSMSOutputContract();
		
			respContractObj.setSMSCount(machineSMSReportBOList.get(i).getSMSCount());
			respContractObj.setSerialNumber(machineSMSReportBOList.get(i).getSerialNumber());
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
