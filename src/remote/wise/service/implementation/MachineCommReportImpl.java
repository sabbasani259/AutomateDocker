package remote.wise.service.implementation;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.logging.log4j.Logger;

import remote.wise.businessobject.ReportDetailsBO;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
import remote.wise.service.datacontract.MachineCommReportRespContract;
//import remote.wise.util.WiseLogger;

public class MachineCommReportImpl 
{
	//public static WiseLogger infoLogger = WiseLogger.getLogger("MachineCommReportImpl:","info");
	
	public List<MachineCommReportRespContract> getMachineCommReport()
	{
		//Logger bLogger = BusinessErrorLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
    	
		List<MachineCommReportRespContract> responseList = new ReportDetailsBO().getDailyCommunicationReport();
		
		iLogger.info("Machine Count Before Sorting: "+responseList.size());
		//Sort the data obtained from 4 Queries together based on Packet Received Time
		Collections.sort(responseList, new PacketReceivedTime());
		Collections.reverse(responseList);
		
		iLogger.info("Machine Count After Sorting: "+responseList.size());
		
		return responseList;
	}
	
	class PacketReceivedTime implements Comparator<MachineCommReportRespContract>
	{
		@Override
		public int compare(MachineCommReportRespContract arg0, MachineCommReportRespContract arg1) 
		{
			// TODO Auto-generated method stub
			return (arg0.getPktReceivedTimestamp().compareTo(arg1.getPktReceivedTimestamp()));
		} 
	}
}
