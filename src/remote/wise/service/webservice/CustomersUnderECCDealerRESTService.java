package remote.wise.service.webservice;

import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.apache.logging.log4j.Logger;
import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;

import remote.wise.service.datacontract.NonDBMSCustomerEntity;
import remote.wise.service.implementation.CustomersUnderECCDealerRESTImpl;

/**
 * @author SU334449
 *
 */

@Path("/customersUnderDealer")
public class CustomersUnderECCDealerRESTService {

	@GET
	@Path("getCustomers")
	@Produces(MediaType.APPLICATION_JSON)
	public List<NonDBMSCustomerEntity> getRegions(@QueryParam("eccCode") String eccCode) throws CustomFault{

		Logger flogger = FatalLoggerClass.logger;
		List<NonDBMSCustomerEntity> nonDBMScustomerList = new ArrayList<NonDBMSCustomerEntity>();
		CustomersUnderECCDealerRESTImpl implObj = new CustomersUnderECCDealerRESTImpl();
		if(!(eccCode.equals(null) || eccCode.isEmpty())){
			try{
				nonDBMScustomerList = implObj.getCustomersUnderDealer(eccCode);
			}catch(Exception e){
				flogger.info("Exception in CustomersUnderECCDealerRESTService: "+e);
			}
		}
		return nonDBMScustomerList;
	}
}



