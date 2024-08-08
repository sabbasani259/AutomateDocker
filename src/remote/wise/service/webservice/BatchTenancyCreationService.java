package remote.wise.service.webservice;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;

import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.BatchTenancyCreationImpl;

@WebService(name = "BatchTenancyCreationService")
public class BatchTenancyCreationService 
{
	@WebMethod(operationName = "createTenancyAsBatch", action = "createTenancyAsBatch")
	public int createTenancyAsBatch(@WebParam(name="accountId" )String accountId)
	{
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger"); 
		iLogger.info("Batch Tenancy Creation : ---- Actual Webservice Input ------");
		iLogger.info("Batch Tenancy Creation : accountId "+accountId);
		
		long startTime = System.currentTimeMillis();
		int response = new BatchTenancyCreationImpl().createTenancy(accountId);
		long endTime = System.currentTimeMillis();
		iLogger.info("Batch Tenancy Creation :: Webservice Execution Time in ms:"+(endTime-startTime));
		
		iLogger.info("Batch Tenancy Creation : ----- Actual Webservice Output-----");
		iLogger.info("Batch Tenancy Creation : New Tenancy count:"+response);
		
		iLogger.info("serviceName:BatchTenancyCreationService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		
		return response;
	}
}
