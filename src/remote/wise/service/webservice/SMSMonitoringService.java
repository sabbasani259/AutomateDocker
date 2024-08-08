package remote.wise.service.webservice;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import remote.wise.service.datacontract.SMSMonitoringRespContract;
import remote.wise.service.implementation.SMSMonitoringImpl;

@WebService(name = "SMSMonitoringService")
public class SMSMonitoringService 
{
	@WebMethod(operationName = "getLatestSMSDetails", action = "getLatestSMSDetails")
	public List<SMSMonitoringRespContract> getLatestSMSDetails(@WebParam(name="mobileNumber") String mobileNumber)
	{
		List<SMSMonitoringRespContract> responseList = new SMSMonitoringImpl().getSMSDetails(mobileNumber);
		return responseList;
	}

}
