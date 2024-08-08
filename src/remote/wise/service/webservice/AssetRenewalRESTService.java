/*
 * CR334 : 20220830 : Dhiraj K : Billability Module Integration changes
 * CR334 : 20221118 : Dhiraj K : Changes for Billing and ARD table update
 * CR388 20230125 - Prasad -PaginationLogic_LLIRenewal
 * ME100005235 : 20230306 : Dhiraj K : WS to send the renewal date to MoolDA 
 * ME100009742 : 20231117 : Dhiraj K : Fleet shows "Vin expired" but appearing on the Billing report
 * CR463 :20240321 : Dhiraj : Renewal data push to Mobile APP
 */
package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.annotate.JsonProperty;

import com.wipro.mda.AssetSubscriptionDetails;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.AssetRenewalRESTImpl;
import remote.wise.util.BillingSubscriptionHistory;
import remote.wise.util.CommonUtil;
import remote.wise.EAintegration.Qhandler.SubscriptionDataProducer;
import remote.wise.exception.CustomFault;

/**
 * @author KO369761
 *
 */

@Path("/AssetRenewalService")
public class AssetRenewalRESTService {

	@GET
	@Path("getRenewalMachines")
	@Produces(MediaType.APPLICATION_JSON)	
	public List<HashMap<String,String>> getAssetRenewalMachines(@QueryParam("tenancy_id") int tenancyId ,
			@QueryParam("pageNumber") String pageNumber,@QueryParam("pageSize") String pageSize) throws CustomFault{
		Logger infoLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		List<HashMap<String,String>> response = new LinkedList<HashMap<String,String>>();
	
		//validating pagination 
		int pagNum, pageSiz ;
		if(pageNumber==null || pageSize==null){
			List<HashMap<String, String>> resp = new ArrayList<>();
			HashMap<String, String> msg =  new HashMap<String, String>();
			msg.put("msg", "Both pageNumber and pageSize are mandatory");
			resp.add(msg);
			return resp;
			//return "Both pageNumber and pageSize are mandatory";
		}
		else
		{
			pagNum=Integer.parseInt(pageNumber);
			pageSiz=Integer.parseInt(pageSize);
		}//
		
		try{
			long startTime = System.currentTimeMillis();
			infoLogger.info("Webservice input - tenancy_id :: "+tenancyId);
			
			//DF20181008-KO369761-XSS validation of input request contract
			CommonUtil util = new CommonUtil();
			String isValidinput=null;
			isValidinput = util.inputFieldValidation(String.valueOf(tenancyId));
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			
			AssetRenewalRESTImpl implObj = new AssetRenewalRESTImpl();
			response = implObj.getRenewalMachines(tenancyId, pagNum ,pageSiz);
			
			for(int i = 0;i<response.size();i++){
				isValidinput = util.inputFieldValidation(response.get(i).get("serial_number"));
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
				isValidinput = util.inputFieldValidation(response.get(i).get("expiry_date"));
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			
			infoLogger.info("Webservice output :"+response);
			long endTime = System.currentTimeMillis();
			infoLogger.info("serviceName:AssetRenewalService~executionTime:"+(endTime-startTime)+"~"+""+"~");
			
		}catch(Exception e){
			e.printStackTrace();
			fLogger.error("Tenancy::"+tenancyId+" Exception caught : "+e.getMessage());
		}
		return response;
	}
	
	// CR388 St 
	@POST()
	@Path("searchVinFromList")
	@Produces(MediaType.APPLICATION_JSON)
	public List<HashMap<String,String>> searchVinFromList (Map<String, String> reqMap ) { 
		
		Logger infoLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		List<HashMap<String,String>> response = new LinkedList<HashMap<String,String>>();
		String serialNumberListInString =  reqMap.get("serialNumberList");
		int tenancyId = Integer.parseInt((String) reqMap.get("tenancyId"));
		
		List<String> serialNumbersList = new ArrayList<String>(Arrays.asList(serialNumberListInString.split(",")));
		System.out.println("tenancyId" + tenancyId);

		try{
			long startTime = System.currentTimeMillis();
			infoLogger.info("Webservice input - tenancy_id :: "+tenancyId);
			
			
			CommonUtil util = new CommonUtil();
			String isValidinput=null;
			isValidinput = util.inputFieldValidation(String.valueOf(tenancyId));
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			
			AssetRenewalRESTImpl implObj = new AssetRenewalRESTImpl();
			response = implObj.getSelectVins(tenancyId ,serialNumbersList );
			System.out.println("response in output " + response);
			for(int i = 0;i<response.size();i++){
				isValidinput = util.inputFieldValidation(response.get(i).get("serial_number"));
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
				isValidinput = util.inputFieldValidation(response.get(i).get("expiry_date"));
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			
			infoLogger.info("Webservice output :"+response);
			long endTime = System.currentTimeMillis();
			infoLogger.info("serviceName:AssetRenewalService~executionTime:"+(endTime-startTime)+"~"+""+"~");
			
		}catch(Exception e){
			e.printStackTrace();
			fLogger.error("Tenancy::"+tenancyId+" Exception caught : "+e.getMessage());
		}
		
		return  response;
	}
	//CR388 end
	@POST
	@Path("setRenewalMachines")
	@Produces(MediaType.TEXT_HTML)
	@Consumes(MediaType.APPLICATION_JSON)
	public String setAssetRenewalMachines(@JsonProperty List<HashMap<String, String>> inputObj) throws CustomFault{
		Logger infoLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		String result = "FAILURE";
		try{
			long startTime = System.currentTimeMillis();
			infoLogger.info("Webservice input : "+inputObj);
			
			//DF20181008-KO369761-XSS validation of input request contract
			CommonUtil util = new CommonUtil();
			String isValidinput=null;
			AssetRenewalRESTImpl implObj = new AssetRenewalRESTImpl(); //CR334.n
			for(int i = 0;i<inputObj.size();i++){
				isValidinput = util.inputFieldValidation(inputObj.get(i).get("serial_number"));
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
				isValidinput = util.inputFieldValidation(inputObj.get(i).get("subscribed_from"));
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
				
				//CR334.n
				boolean validationFlag = implObj.doSubsriptionValidation(inputObj.get(i).get("serial_number"), 
						inputObj.get(i).get("subscribed_from"));
				if(!validationFlag) {
					throw new CustomFault("Machines can't be renewed before expiry date");
				}
				//CR334.n
			}
			//AssetRenewalRESTImpl implObj = new AssetRenewalRESTImpl(); //CR334.o
			//result = implObj.setRenewalMachines(inputObj);//ME100009742.o
			result = implObj.setRenewalMachinesV2(inputObj);//ME100009742.n
			infoLogger.info("Webservice output : "+result);
			long endTime = System.currentTimeMillis();
			infoLogger.info("serviceName:AssetRenewalService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		}catch(Exception e){
			e.printStackTrace();
			fLogger.error("Exception caught : "+e.getMessage());
		}
		//CR334.sn************************************************************************************************************
		// Invoke Billability module service to update billing.billing_subsHistory Table
		if(result.equalsIgnoreCase("SUCCESS")) {
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
				new AssetSubscriptionDetails().setAssetSubscriptionDetails(vin);//ME100005235.n
				//CR463.sn
				HashMap<String, String> payload = new HashMap<>();
				String txnKey =  "SubcriptionData_"+ vin;
				payload.put("serialNumber", vin);
				payload.put("renewalEndDate", LocalDate.parse(renewalDate, dtf2).format(dtf));
				new SubscriptionDataProducer(txnKey,payload);
				//CR463.en
			}

		}
		//CR334.en************************************************************************************************************	

		return result;
	}
}

