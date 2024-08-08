package remote.wise.service.webservice;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.AccountTypeCustomerToMACustomerImpl;

@Path("/AccountTypeCustomerToMACustomerRESTService")
public class AccountTypeCustomerToMACustomerRESTService {

	// DF - 20191202- Mamatha - method to get all the accounts of type customer
	@GET
	@Path("getAccountTypeCustomer")
	@Produces(MediaType.APPLICATION_JSON)
	public HashMap<Integer, String> getAccountTypeCustomer(
			@QueryParam("pageNumber") int pageNumber,
			@QueryParam("searchParam") String searchParam,
			@QueryParam("searchValue") String searchValue) throws CustomFault {
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		HashMap<Integer, String> result = new HashMap<Integer, String>();

		try {
			long startTime = System.currentTimeMillis();
			iLogger.info("AccountTypeCustomerToMACustomerRESTService:getAccountCode:WebService Input: "
					+ pageNumber);

			result = new AccountTypeCustomerToMACustomerImpl()
					.getAccountTypeCustomer(pageNumber, searchParam,
							searchValue);

			long endTime = System.currentTimeMillis();
			iLogger.info("AccountTypeCustomerToMACustomerRESTService:getAccountCode:WebService Output -----> "
					+ result
					+ "; Total Time taken in ms:"
					+ (endTime - startTime));
			iLogger.info("serviceName:AccountTypeCustomerToMACustomerRESTService~executionTime:"+(endTime - startTime)+"~"+""+"~");

		} catch (Exception e) {
			e.printStackTrace();
			fLogger.error("Exception:" + e.getMessage());
		}
		return result;
	}

	// DF - 20191202- Mamatha - method to set MAFlag for all the accounts of
	// type customer
	@POST
	@Path("setMAFlag")
	@Produces("text/plain")
	@Consumes({ MediaType.APPLICATION_JSON })
	public String setMAFlag(HashMap<String, Object> reqObj) throws CustomFault {
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		String response = "Failure";
		// ListToStringConversion conversion = new ListToStringConversion();

		// @QueryParam("loginTenancyID") int loginTenancyID,
		// @QueryParam("accountIdList") List<String> accountIdList
		try {
			long startTime = System.currentTimeMillis();
			iLogger.info("AccountTypeCustomerToMACustomerRESTService:setMAFlag:WebService Input: "
					+ reqObj);

			String accountId = reqObj.get("accountIdList").toString();
			List<String> accountIdList = Arrays.asList(accountId);
			String loginTenancyID = (String) reqObj.get("loginTenancyID");

			/*
			 * CommonUtil utilObj = new CommonUtil(); String isValidInput =
			 * utilObj.inputFieldValidation(loginTenancyID); if
			 * (!isValidInput.equalsIgnoreCase("SUCCESS")) { throw new
			 * CustomFault(isValidInput); }
			 */

			response = new AccountTypeCustomerToMACustomerImpl()
					.updateCustomerAccount(accountIdList);

			long endTime = System.currentTimeMillis();
			iLogger.info("AccountTypeCustomerToMACustomerRESTService:setMAFlag:WebService Output -----> "
					+ response
					+ "; Total Time taken in ms:"
					+ (endTime - startTime));
			iLogger.info("serviceName:AccountTypeCustomerToMACustomerRESTService~executionTime:"+(endTime - startTime)+"~"+""+"~"+response);
		} catch (Exception e) {
			e.printStackTrace();
			fLogger.error("Exception:" + e.getMessage());
		}
		return response;
	}
}
