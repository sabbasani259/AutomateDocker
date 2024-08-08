package remote.wise.service.implementation;

import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import remote.wise.businessobject.DetailedUsercommunicationReportBO;
import remote.wise.businessobject.MachineBillingReportBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
import remote.wise.service.datacontract.DetailedUsercommunicationReportInputContract;
import remote.wise.service.datacontract.DetailedUsercommunicationReportOutputContract;
import remote.wise.service.datacontract.MachineBillingReportOutputContract;
//import remote.wise.util.WiseLogger;

public class DetailedUsercommunicationReportImpl {

	//public static WiseLogger businessError = WiseLogger.getLogger("DetailedUsercommunicationReportImpl:","businessError");
	public List<DetailedUsercommunicationReportOutputContract> getDetailedUsercommunication(
			DetailedUsercommunicationReportInputContract reqObj) {
		// TODO Auto-generated method stub
		Logger bLogger = BusinessErrorLoggerClass.logger;
		
		List<DetailedUsercommunicationReportOutputContract> respList = new LinkedList<DetailedUsercommunicationReportOutputContract>();
		try{

			if(reqObj.getFromDate()==null || reqObj.getToDate()==null)
			{
				throw new CustomFault("FromDate and Todate should be specified");
			}
			if((reqObj.getContactId()==null || reqObj.getContactId().isEmpty()) && (reqObj.getPhoneNumber()==null || reqObj.getPhoneNumber().isEmpty()))
			{
				throw new CustomFault("FromDate and Todate should be specified");
			}
			DetailedUsercommunicationReportBO detailedUsercommunicationReportBO= new DetailedUsercommunicationReportBO();
		List<DetailedUsercommunicationReportBO> detailedUsercommunicationReportBOList =  detailedUsercommunicationReportBO.detailedUsercommunicationList(reqObj.getFromDate(),reqObj.getToDate(),reqObj.getContactId(),reqObj.getPhoneNumber(),reqObj.getEmail(),reqObj.getSms());
		
		for(int i=0;i<detailedUsercommunicationReportBOList.size();i++)
		{	
			DetailedUsercommunicationReportOutputContract respContractObj=new DetailedUsercommunicationReportOutputContract();
		
			respContractObj.setSerialNumber(detailedUsercommunicationReportBOList.get(i).getSerialNumber());
			respContractObj.setEventGeneratedTime(detailedUsercommunicationReportBOList.get(i).getEventGeneratedTime());
			respContractObj.setAlertDesc(detailedUsercommunicationReportBOList.get(i).getAlertDesc());
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
