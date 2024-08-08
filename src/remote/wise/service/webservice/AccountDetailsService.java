package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

//import org.apache.log4j.Logger;

import remote.wise.exception.CustomFault;
import remote.wise.service.implementation.AccountDetailsImpl;

/**
 * 
 * @author jgupta41
 *
 */
@WebService
public class AccountDetailsService {

	/**
	 * 
	 * @param accountCode
	 * @param accountName
	 * @param Location
	 * @param Email
	 * @param Address
	 * @param phoneNumber
	 * @param region
	 * @param zone
	 * @param MobNumber
	 * @param NoOfEmployees
	 * @param parentaccCode
	 * @param yearStarted
	 * @param country
	 * @param state
	 * @param city
	 * @param zipCode
	 * @return
	 * @throws CustomFault
	 */

	@WebMethod(operationName = "SetAccountDetails", action = "SetAccountDetails")
	public String setAccountDetails(@WebParam(name="AccountCode") String accountCode, @WebParam(name="AccountName")String accountName, @WebParam(name="Location") String Location,@WebParam(name="Email") String Email, @WebParam(name="Address") String Address, @WebParam(name="PhoneNumber") String phoneNumber,@WebParam(name="Region") String region, 
			@WebParam(name="Zone") String zone, @WebParam(name="MobNumber") String MobNumber, @WebParam(name="NoOfEmp") Long NoOfEmployees, @WebParam(name="ParentsAccCode") String parentaccCode,@WebParam(name="YearStarted") Date yearStarted, @WebParam(name="Country") String country, @WebParam(name="State") String state, @WebParam(name="City") String city, @WebParam(name="ZipCode") String zipCode )throws CustomFault
			{
		/*Logger infoLogger = Logger.getLogger("infoLogger");
		long startTime = System.currentTimeMillis();
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSS");
		String startDate = sdf.format(cal.getTime());
		infoLogger.info("Current Startdate : "+startDate);
		infoLogger.info("---- Webservice Input ------");
		infoLogger.info("AccountCode:"+accountCode+",  "+" AccountName:"+accountName+" ,   "+" Location:"+Location+", "+" Email:"+Email+",  "+" Address:"+Address+"," +
				""+" phoneNumber:"+phoneNumber+","+" Region:"+region+", "+" Zone:"+zone+", "+" MobNumber:"+MobNumber+", "+" NoOfEmp:"+NoOfEmployees+","+" ParentsAccCode:"+parentaccCode+","+" YearStarted:"+yearStarted+", "+" Country:"+country+", "+" State:"+state+", "+" City:"+city+","+" ZipCode:"+zipCode+" ");
		String response = new AccountDetailsImpl().setAccountDetails(accountCode, accountName, Location, Email, Address, phoneNumber, region, zone, MobNumber, NoOfEmployees, parentaccCode, yearStarted, country, state, city, zipCode);
		infoLogger.info("----- Webservice Output-----");
		infoLogger.info("Status:"+response+"");
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSS");
		String endDate = sdf1.format(cal1.getTime());
		infoLogger.info("Current Enddate : "+endDate);
		long endTime = System.currentTimeMillis();
		infoLogger.info("Webservice Execution Time in ms:"+(endTime-startTime));*/
		return "Success";
			}
}
