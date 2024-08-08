//CR334 : 20221118 : Dhiraj K : Changes for Billing and ARD table update

package remote.wise.service.webservice;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.annotate.JsonProperty;

import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.RetrofitInstallationserviceImpl;
import remote.wise.util.BillingSubscriptionHistory;
import remote.wise.util.CommonUtil;


/**
 * @author Mani
 *
 */
@Path("/RetrofitInstallationservice")
public class RetrofitInstallationservice {
	@POST
	@Path("/setRetrofitInstallationservice")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public List<String> retrofitmentInstallation(@JsonProperty List<HashMap<String, String>> inputObj)throws CustomFault{
		Logger infoLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		List<String> result = null;
		try{
			long startTime = System.currentTimeMillis();
			infoLogger.info("Webservice input : "+inputObj);
			
			//DF20181008-KO369761-XSS validation of input request contract
			CommonUtil util = new CommonUtil();
			String isValidinput=null;

			for(int i = 0;i<inputObj.size();i++){
				isValidinput = util.inputFieldValidation(inputObj.get(i).get("serial_number"));
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
				isValidinput = util.inputFieldValidation(inputObj.get(i).get("install_date"));
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
				isValidinput = util.inputFieldValidation(inputObj.get(i).get("renewal_date"));
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
				isValidinput = util.inputFieldValidation(inputObj.get(i).get("mode_of_subscription"));
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			
			RetrofitInstallationserviceImpl implObj = new RetrofitInstallationserviceImpl();
			result =implObj.doRetrofitInstallation(inputObj);
			infoLogger.info("Webservice output : "+result);
			long endTime = System.currentTimeMillis();
			infoLogger.info("serviceName:RetrofitInstallationservice~executionTime:"+(endTime-startTime)+"~"+""+"~");
		}catch(Exception e){
			e.printStackTrace();
			fLogger.error("Exception caught : "+e.getMessage());
		}

		//CR334.sn************************************************************************************************************
		// Invoke Billability module service to update billing.billing_subsHistory Table
		if(result.contains("SUCCESS")) {
			infoLogger.info("Invoking Billability Module");
			infoLogger.info("object size" + inputObj.size());
			infoLogger.info("object size" + inputObj);
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			for(int i = 0;i<inputObj.size();i++){
				String vin = inputObj.get(i).get("serial_number");
				String subscribedFrom = inputObj.get(i).get("subscribed_from");
				String renewalDate = inputObj.get(i).get("renewal_date");
				//int yearsOfSubscription = Integer.parseInt(inputObj.get(i).get("mode_of_subscription"));
				String subscriptionStartDate = LocalDate.parse(subscribedFrom, dtf2).format(dtf) + " 00:00:00";
				String subscriptionEndDate = LocalDate.parse(renewalDate, dtf2).format(dtf) + " 00:00:00";

				new BillingSubscriptionHistory().updateSubsHistory(vin, subscriptionStartDate, subscriptionEndDate);
			}

		}
		//CR334.en************************************************************************************************************	

		return result;
		
	}
	@GET
	@Path("/getRetrofitReport")
	@Produces(MediaType.APPLICATION_JSON)
	public List<HashMap<String,String>> getRetrofitReport(@Context HttpHeaders httpHeaders, @QueryParam("tenancy_id") int tenancyId, @QueryParam("loginId") String loginId) throws CustomFault
	{
		Logger infoLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		List<HashMap<String,String>> result=new LinkedList<HashMap<String,String>>();
		try{
			long startTime = System.currentTimeMillis();
			infoLogger.info("Webservice:: Retrofitment download for tenancy id::"+tenancyId);
			
			String csrfToken = null;
			CommonUtil util = new CommonUtil();
			//DF20181011 - KO369761 - Validating the CSRF Token against login id.
			if(httpHeaders.getRequestHeader("CSRFTOKEN")!=null)
			{
				csrfToken=httpHeaders.getRequestHeader("CSRFTOKEN").get(0);
			}
			infoLogger.info("Retrofitment ::  received csrftoken :: "+csrfToken);
			boolean isValidCSRF=false;
			if(csrfToken!=null){
				isValidCSRF=util.validateANTICSRFTOKEN(loginId,csrfToken);
			}
			infoLogger.info("Retrofitment ::   csrftoken isValidCSRF :: "+isValidCSRF);
			if(!isValidCSRF)
			{
				infoLogger.info("Retrofitment ::  Invalid request.");
				throw new CustomFault("Invalid request.");
			}
			
			//DF20181008-KO369761-XSS validation of input request contract
			String isValidinput=null;
			
			isValidinput = util.inputFieldValidation(String.valueOf(tenancyId));
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			
			RetrofitInstallationserviceImpl implObj = new RetrofitInstallationserviceImpl();
			result =implObj.downloadRetrofitReport(tenancyId);
			infoLogger.info("Webservice output :Retrofitment download for tenancy id::"+tenancyId+"::"+result);
			
			for(int i = 0;i<result.size();i++){
				isValidinput = util.inputFieldValidation(result.get(i).get("serial_number"));
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
				isValidinput = util.inputFieldValidation(result.get(i).get("install_date"));
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
				isValidinput = util.inputFieldValidation(result.get(i).get("customer_name"));
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
				isValidinput = util.inputFieldValidation(result.get(i).get("IMSI"));
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
				isValidinput = util.inputFieldValidation(result.get(i).get("zone"));
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
				isValidinput = util.inputFieldValidation(result.get(i).get("zone"));
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			
			long endTime = System.currentTimeMillis();
			infoLogger.info("serviceName:RetrofitInstallationservice~executionTime:"+(endTime-startTime)+"~"+""+"~");
		}catch(Exception e){
			e.printStackTrace();
			fLogger.error("Exception caught : "+e.getMessage());
		}
		return result;
		
	}
	

}
