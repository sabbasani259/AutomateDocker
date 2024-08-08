/**
 * 
 */
package remote.wise.service.webservice;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.logging.log4j.Logger;

import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.service.implementation.AccountTenancyImpl;
import remote.wise.service.implementation.ProfileCodeImpl;

/**
 * @author KI270523
 *
 */
@Path("/AccountTenancyService")
public class AccountTenancyService {
	
	@GET()
	@Produces("text/plain")
	@Path("/getAccountTenancyDetails")
	public String getAccountTenancyDetails(@QueryParam("tenancyId")String tenancyId){
		Logger bLogger = BusinessErrorLoggerClass.logger;
		
		String [] tenancyList = null;
		String tenancyID = null;
		String result=null;
		try
		{

		if(tenancyId==null){
			throw new CustomFault("Tenancy id should not be empty or null");
		}
		
		AccountTenancyImpl omplObj=new AccountTenancyImpl();
		
		 result=omplObj.getAccountFromTenancy(tenancyId);
		}catch(CustomFault e)
		{
			bLogger.error("Custom Fault: "+ e.getFaultInfo());
		}
				
	     return result;
	}
	
	
//DF - 20191115- Deepthi - Included a new Impl for getting the Account code	
	@GET()
	@Produces("text/plain")
	@Path("/getAccountCode")
	public List<String> getAccountCode(@QueryParam("tenancyId")String tenancyId){
		Logger bLogger = BusinessErrorLoggerClass.logger;
		
		String [] tenancyList = null;
		String tenancyID = null;
		List<String> result=null;
		try
		{

		if(tenancyId==null){
			throw new CustomFault("Tenancy id should not be empty or null");
		}
		
		AccountTenancyImpl omplObj=new AccountTenancyImpl();
		
		 result=omplObj.getAccountCodeFromTenancy(tenancyId);
		}catch(CustomFault e)
		{
			bLogger.error("Custom Fault: "+ e.getFaultInfo());
		}
				
	     return result;
	}

}
