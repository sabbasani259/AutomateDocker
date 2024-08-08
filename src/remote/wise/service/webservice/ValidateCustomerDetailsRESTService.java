package remote.wise.service.webservice;

import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.apache.logging.log4j.Logger;
import remote.wise.log.InfoLogging.InfoLoggerClass;

import remote.wise.service.datacontract.CustomerDetailsRespContract;
import remote.wise.service.implementation.CustomerDetailsRESTImpl;

/**
 * @author SU334449
 *
 */

@Path("/validateCustomers")
public class ValidateCustomerDetailsRESTService {

	@GET
	@Path("getCustomerDetails")
	@Produces(MediaType.APPLICATION_JSON)
	public List<CustomerDetailsRespContract> getCustomerDetails(@QueryParam("customerCode") String customerCode){

		Logger iLogger = InfoLoggerClass.logger;
		List<CustomerDetailsRespContract>customerList = new ArrayList<CustomerDetailsRespContract>();
		iLogger.info("CustomerDetailsRESTService:WebService Input-----> customerCode:"+customerCode);
		long startTime = System.currentTimeMillis();
		customerList = new CustomerDetailsRESTImpl().getCustomerDetails(customerCode);
		long endTime=System.currentTimeMillis();
		iLogger.info("CustomerDetailsRESTService:WebService Output -----> customerList:"+customerList);
		iLogger.info("serviceName:ValidateCustomerDetailsRESTService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		return customerList;
	}
}
