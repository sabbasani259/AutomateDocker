package remote.wise.service.webservice;

import javax.jws.WebMethod;
import javax.jws.WebService;

import remote.wise.service.implementation.VinLoadDistributionImpl;

@WebService(name = "VinLoadDistributionService")
public class VinLoadDistributionService 
{
	@WebMethod(operationName = "distributeLoad", action = "distributeLoad")
	public String distributeLoad() 
	{
		String status="SUCCESS";
		
		VinLoadDistributionImpl implObj = new VinLoadDistributionImpl();
		implObj.distributeLoad();
		
		return status;
	}
}
