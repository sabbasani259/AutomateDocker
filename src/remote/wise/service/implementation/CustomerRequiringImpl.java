package remote.wise.service.implementation;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import remote.wise.businessobject.CustomerRequiringBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
import remote.wise.service.datacontract.CustomerRequiringReqContract;
import remote.wise.service.datacontract.CustomerRequiringRespContract;
//import remote.wise.util.WiseLogger;
/**
 * CustomerRequiringImpl  will allow to get Customers and related  assets that pending for Service under given Dealer 
 * @author jgupta41
 *
 */

public class CustomerRequiringImpl {
	//public static WiseLogger infoLogger = WiseLogger.getLogger("AssetDiagnosticImpl:","info");
	private String CustomerName;
	private String DealerName;
	private String CustomerContactNumber;
	private List<String> MachineServiceDetails;
	private int NumberOfMachineDueForService;
	public int getNumberOfMachineDueForService() {
		return NumberOfMachineDueForService;
	}
	public void setNumberOfMachineDueForService(int numberOfMachineDueForService) {
		NumberOfMachineDueForService = numberOfMachineDueForService;
	}
	public List<String> getMachineServiceDetails() {
		return MachineServiceDetails;
	}
	public void setMachineServiceDetails(List<String> machineServiceDetails) {
		MachineServiceDetails = machineServiceDetails;
	}
	public String getCustomerName() {
		return CustomerName;
	}
	public void setCustomerName(String customerName) {
		CustomerName = customerName;
	}
public String getDealerName() {
		return DealerName;
	}
	public void setDealerName(String dealerName) {
		DealerName = dealerName;
	}
	public String getCustomerContactNumber() {
		return CustomerContactNumber;
	}
	public void setCustomerContactNumber(String customerContactNumber) {
		CustomerContactNumber = customerContactNumber;
	}
	//******************************Get the customers which requires service under given List of  LoginTenancy_ID*****************
	/**
	 * This method gets Customers and related  assets that pending for Service under given Dealer Tenancy ID
	 * @param customerRequiringReq:Get all Customers and related  assets pending for Service under given Dealer Tenancy ID
	 * @return respList:Returns List of Customers and related  assets that pending for Service
	 * @throws CustomFault
	 */
	public List<CustomerRequiringRespContract> getCustomerRequiring(CustomerRequiringReqContract customerRequiringReq)throws CustomFault
	{
		//Logger bLogger = BusinessErrorLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
    	
		CustomerRequiringRespContract respContractObj=null;
		List<CustomerRequiringRespContract> respList = new LinkedList<CustomerRequiringRespContract>();
		List<CustomerRequiringImpl> customerRequiringImpl=new LinkedList<CustomerRequiringImpl>();
		iLogger.info("customerRequiringReq:" + customerRequiringReq);
		CustomerRequiringBO customerRequiringBO=new CustomerRequiringBO();
		iLogger.info("customerRequiringReq.getLoginTenancy_ID() " +customerRequiringReq.getLoginTenancy_ID());
		
		//customerRequiringImpl=customerRequiringBO.getCustomerRequiring(customerRequiringReq.getLoginTenancy_ID());
		
		
		//DF20160729 @Roopa Calling new method with Query tweaking 
		
		customerRequiringImpl=customerRequiringBO.getCustomerRequiring_new(customerRequiringReq.getLoginTenancy_ID());
		
		//For sorting done by Juhi on 12-September-2013 for defect id:1245
		Collections.sort(customerRequiringImpl, new transactionComparator());
		
		for(int i=0;i<customerRequiringImpl.size();i++)
		{
			respContractObj=new CustomerRequiringRespContract();
			respContractObj.setCustomerName(customerRequiringImpl.get(i).getCustomerName());
			respContractObj.setCustomerContactNumber(customerRequiringImpl.get(i).getCustomerContactNumber());
			respContractObj.setMachineServiceDetails(customerRequiringImpl.get(i).getMachineServiceDetails());
			respContractObj.setDealerName(customerRequiringImpl.get(i).getDealerName());
			respContractObj.setNumberOfMachineDueForService(customerRequiringImpl.get(i).getNumberOfMachineDueForService());
			respList.add(respContractObj);
		}
		return respList;
	}
	//******************************End of Get the customers which requires service under given List of  LoginTenancy_ID*****************
	//For sorting done by Juhi on 12-September-2013 for defect id:1245
	class transactionComparator implements Comparator<CustomerRequiringImpl>
	{

		@Override
		public int compare(CustomerRequiringImpl arg0, CustomerRequiringImpl arg1) {
			// TODO Auto-generated method stub
			//return arg0.getNumberOfMachineDueForService()-arg1.getNumberOfMachineDueForService();
			return arg1.getNumberOfMachineDueForService()-arg0.getNumberOfMachineDueForService();
		}
		
		
	}}
