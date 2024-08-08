package remote.wise.service.implementation;

import java.util.LinkedList;
////import org.apache.log4j.Logger;
import java.util.List;

import org.apache.logging.log4j.Logger;

import remote.wise.businessobject.AssetDetailsBO;
import remote.wise.businessobject.ServiceDetailsBO;
import remote.wise.businessobject.TenancyBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.AssetExtendedRespContract;

import remote.wise.service.datacontract.ServiceHistoryReqContract;
import remote.wise.service.datacontract.ServiceHistoryRespContract;
import remote.wise.service.datacontract.ServiceScheduleRespContract;
//import remote.wise.util.WiseLogger;

public class ServiceHistoryImpl {
	//Defect Id 1337 - Logger changes
//		public static WiseLogger businessError = WiseLogger.getLogger("ServiceHistoryImpl:","businessError");

	public List<ServiceHistoryRespContract> getServiceHistory(ServiceHistoryReqContract historyReq)throws CustomFault
	{
		
		 Logger infoLogger = InfoLoggerClass.logger;
			Logger fatalError = FatalLoggerClass.logger;
			Logger businessError = BusinessErrorLoggerClass.logger;
		List<ServiceHistoryRespContract> respList = new LinkedList<ServiceHistoryRespContract>(); 
		ServiceDetailsBO serviceBo = new ServiceDetailsBO();
		List<ServiceDetailsBO> serviceDetailsBO =  serviceBo.getServiceHistory(historyReq.getSerialNumber());
		for(int i=0;i<serviceDetailsBO.size();i++){
			ServiceHistoryRespContract respContractObj = new ServiceHistoryRespContract();
			respContractObj.setJobCardNumber(serviceDetailsBO.get(i).getJobCardNumber());
			respContractObj.setScheduleName(serviceDetailsBO.get(i).getScheduleName());
			respContractObj.setServiceDate(serviceDetailsBO.get(i).getServiceDate().toString());
			respContractObj.setServiceName(serviceDetailsBO.get(i).getServiceName());
			//DF20180423:IM20018382 - An additional field jobCardDetails.
			respContractObj.setJobCardDetails(serviceDetailsBO.get(i).getJobCardDetails());
			respList.add(respContractObj);
			
			
			
		}
		
		
		return respList;
	}
	

	/** This method sets the service history details of a specific VIN
	 * @param serialNumber  VIN as String input
	 * @param dealerCode service dealer Code
	 * @param jobCardNumber jobCard Number of the service done
	 * @param dbmsPartCode DBMS partCode of the service being done
	 * @param servicedDate Date of when the service is done
	 * @return Returns the Status
	 */
	//DF20190423:IM20018382-Adding the additonal field jobCardDetails
	public String setServiceHistoryDetails (String serialNumber, String dealerCode, String jobCardNumber,
											String dbmsPartCode,  String servicedDate,String jobCardDetails, String callTypeId,String messageId)
	{
		 Logger infoLogger = InfoLoggerClass.logger;
			Logger fatalError = FatalLoggerClass.logger;
			Logger businessError = BusinessErrorLoggerClass.logger;
		//Logger businessError = Logger.getLogger("businessErrorLogger");
		String status = "SUCCESS-Record Processed";
		
		ServiceDetailsBO serviceBO = new ServiceDetailsBO();
		
		//Check for Mandatory Parameters
		//DF20140715 - Rajani Nagaraju - Robust Logging for EA File Processing 
		if(serialNumber==null || serialNumber.trim()==null || serialNumber.replaceAll("\\s","").length()==0)
		{
			status = "FAILURE-Mandatory Parameter SerialNumber is NULL";
			businessError.error("EA Processing: AssetServiceDetails: "+messageId+" : Mandatory Parameter SerialNumber is NULL");
			return status;
		}
		
		if(dealerCode==null || dealerCode.trim()==null || dealerCode.replaceAll("\\s","").length()==0 )
		{
			status = "FAILURE-Mandatory Parameter DealerCode is NULL";
			businessError.error("EA Processing: AssetServiceDetails: "+messageId+" : Mandatory Parameter DealerCode is NULL");
			return status;
		}
		
		if(dbmsPartCode==null ||  dbmsPartCode.trim()==null ||  dbmsPartCode.replaceAll("\\s","").length()==0)
		{
			status = "FAILURE-Mandatory Parameter dbmsPartCode is NULL";
			businessError.error("EA Processing: AssetServiceDetails: "+messageId+" : Mandatory Parameter dbmsPartCode is NULL");
			return status;
		}
		
		if(servicedDate==null || servicedDate.trim()==null || servicedDate.replaceAll("\\s","").length()==0)
		{
			status = "FAILURE-Mandatory Parameter servicedDate is NULL";
			businessError.error("EA Processing: AssetServiceDetails: "+messageId+" : Mandatory Parameter servicedDate is NULL");
			return status;
		}
		
		if( jobCardNumber==null || jobCardNumber.trim()==null || jobCardNumber.replaceAll("\\s","").length()==0)
		{
			status = "FAILURE-Mandatory Parameter jobCardNumber is NULL";
			businessError.error("EA Processing: AssetServiceDetails: "+messageId+" : Mandatory Parameter jobCardNumber is NULL");
			return status;
		}
		//DF20190423:IM20018382-Null check for the additonal field jobCardDetails
		if( jobCardDetails==null || jobCardDetails.trim()==null || jobCardDetails.replaceAll("\\s","").length()==0)
		{
			//status = "FAILURE-Mandatory Parameter jobCardDetails is NULL";
			//System.out.println("Job card details is null----Inside jobcarddetails validation");
			infoLogger.info("Job card details is null----Inside Job card details validation");
			/*businessError.error("EA Processing: AssetServiceDetails: "+messageId+" : Mandatory Parameter jobCardDetails is NULL");
			return status;*/
		}
		
		//DF20191107:Abhishek:: To check call type id is present or not.
		if( callTypeId==null || callTypeId.trim()==null || callTypeId.replaceAll("\\s","").length()==0)
		{
			status = "FAILURE-Mandatory Parameter callTypeId  is NULL";
			businessError.error("EA Processing: AssetServiceDetails: "+messageId+" : Mandatory Parameter callTypeId is NULL");
			return status;
		}
		try
		{
			TenancyBO tenancyBoObj = new TenancyBO();
			//DF20140325 - Rajani Nagaraju - Get the Corresponding LL Account Code from ECC/CRM Dealer Code from Mapping table
			String llAccountCode = tenancyBoObj.getLLAccountCode(dealerCode);
			infoLogger.info("llAccountCode:"+llAccountCode);
			
			if(llAccountCode==null)
			{
				fatalError.fatal("Data not found in Mapping table for the Dealer AccountCode:"+dealerCode);
				throw new CustomFault("Data not found in Mapping table for the Dealer AccountCode:"+dealerCode);
			}
			else
			{
				dealerCode = llAccountCode;
			}
			
			
			/*String chkSerialNum = serialNumber.replaceAll("\\s","") ;
			String chkDealerCode = dealerCode.replaceAll("\\s","") ;
			String chkJobCodeNum = jobCardNumber.replaceAll("\\s","") ;
			String chkDbmsPartCode = dbmsPartCode.replaceAll("\\s","") ;
			String chkServicedDate = servicedDate.replaceAll("\\s","") ;
			
			
			if( (!(chkSerialNum.length()>0))|| (!(chkDealerCode.length()>0)) || (!(chkJobCodeNum.length()>0)) ||
					(!(chkDbmsPartCode.length()>0)) || (!(chkServicedDate.length()>0)) )
			{
				status = "FAILURE";
				return status;
			}*/
			//System.out.println("Calling BO");
			//infoLogger.info("Calling BO");
			infoLogger.info("Calling Service BO with params :: " + "serialNumber=" +serialNumber+ " dealerCode="+dealerCode+ " jobCardNumber="+jobCardNumber+
					" dbmsPartCode="+dbmsPartCode+ " servicedDate="+servicedDate+ " jobCardDetails="+jobCardDetails + " callTypeId="+callTypeId+ " messageId="+messageId);
			status = serviceBO.setServiceDetails(serialNumber, dealerCode, jobCardNumber, dbmsPartCode, servicedDate, jobCardDetails,callTypeId, messageId);
		}
		
		catch(CustomFault e)
		{
			status = "FAILURE-"+e.getFaultInfo();
			businessError.error("EA Processing: AssetServiceDetails: "+messageId+" : "+e.getFaultInfo());
		}
		 
		return status;
	}


}
