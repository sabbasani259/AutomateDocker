package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.LLIRenewalAdminReportImpl;
import remote.wise.util.CommonUtil;
import remote.wise.util.ListToStringConversion;
import remote.wise.exception.CustomFault;

/**
 * @author KO369761
 *
 */

@Path("/LLIRenewalAdminReport")
public class LLIRenewalAdminReportService {

	@POST
	@Path("getLLIRenewalAdminReport")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes({MediaType.APPLICATION_JSON})
	public List<HashMap<String,String>> getLLIRenewalAdminReport(LinkedHashMap<String,Object> reqObj) throws CustomFault{
		Logger infoLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		List<HashMap<String,String>> response = new LinkedList<HashMap<String,String>>();
		
		try{
			long startTime = System.currentTimeMillis();
			infoLogger.info("Webservice input :"+reqObj);
			
			//DF20180925 ::: MA369757 :: Security checks for all input fields	
			CommonUtil util = new CommonUtil();
			String isValidinput=null;
			ListToStringConversion convert=new ListToStringConversion();

			for(int i=0;i<reqObj.size();i++)
			{
				if(reqObj.get("tenancyIdList")!=null){
					List<Integer> tenacnyList=(List<Integer>) reqObj.get("tenancyIdList");
					String tenacnyListString=convert.getIntegerListString(tenacnyList).toString();
					isValidinput = util.inputFieldValidation(tenacnyListString);
					if(!isValidinput.equals("SUCCESS")){
						throw new CustomFault(isValidinput);
					}
				}
				if(reqObj.get("filter_flag")!=null){
					isValidinput = util.inputFieldValidation(reqObj.get("filter_flag").toString());
					if(!isValidinput.equals("SUCCESS")){
						throw new CustomFault(isValidinput);
					}
				}
				if(reqObj.get("start_date")!=null){
					isValidinput = util.inputFieldValidation(reqObj.get("start_date").toString());
					if(!isValidinput.equals("SUCCESS")){
						throw new CustomFault(isValidinput);
					}
				}
				if(reqObj.get("end_date")!=null){
					isValidinput = util.inputFieldValidation(reqObj.get("end_date").toString());
					if(!isValidinput.equals("SUCCESS")){
						throw new CustomFault(isValidinput);
					}
				}

			}
			
			LLIRenewalAdminReportImpl implObj = new LLIRenewalAdminReportImpl();
			
			response = implObj.getLLIRenewalAdminReport(reqObj);
			
			//DF20190423:IM20018382-Commenting the validations as functionality changed to download excel
			//DF20180925 ::: MA369757 :: Security checks for all input fields	
			/*for(int i=0;i<response.size();i++)
			{
				if(response.get(i).get("serial_number")!=null){
					isValidinput = util.inputFieldValidation(response.get(i).get("serial_number"));
					if(!isValidinput.equals("SUCCESS")){
						throw new CustomFault(isValidinput);
					}
				}
				if(response.get(i).get("install_date")!=null){
					isValidinput = util.inputFieldValidation(response.get(i).get("install_date"));
					if(!isValidinput.equals("SUCCESS")){
						throw new CustomFault(isValidinput);
					}
				}
				if(response.get(i).get("renewal_date")!=null){
					isValidinput = util.inputFieldValidation(response.get(i).get("renewal_date"));
					if(!isValidinput.equals("SUCCESS")){
						throw new CustomFault(isValidinput);
					}
				}
				if(response.get(i).get("customer_name")!=null){
					isValidinput = util.inputFieldValidation(response.get(i).get("customer_name"));
					if(!isValidinput.equals("SUCCESS")){
						throw new CustomFault(isValidinput);
					}
				}
				if(response.get(i).get("customer_contact")!=null){
					isValidinput = util.inputFieldValidation(response.get(i).get("customer_contact"));
					if(!isValidinput.equals("SUCCESS")){
						throw new CustomFault(isValidinput);
					}
				}
				if(response.get(i).get("zone")!=null){
					isValidinput = util.inputFieldValidation(response.get(i).get("zone"));
					if(!isValidinput.equals("SUCCESS")){
						throw new CustomFault(isValidinput);
					}
				}
				if(response.get(i).get("dealer")!=null){
					isValidinput = util.inputFieldValidation(response.get(i).get("dealer"));
					if(!isValidinput.equals("SUCCESS")){
						throw new CustomFault(isValidinput);
					}
				}
				if(response.get(i).get("IMSI")!=null){
					isValidinput = util.inputFieldValidation(response.get(i).get("IMSI"));
					if(!isValidinput.equals("SUCCESS")){
						throw new CustomFault(isValidinput);
					}
				}
				if(response.get(i).get("IMEI")!=null){
					isValidinput = util.inputFieldValidation(response.get(i).get("IMEI"));
					if(!isValidinput.equals("SUCCESS")){
						throw new CustomFault(isValidinput);
					}
				}
				if(response.get(i).get("ICCID")!=null){
					isValidinput = util.inputFieldValidation(response.get(i).get("ICCID"));
					if(!isValidinput.equals("SUCCESS")){
						throw new CustomFault(isValidinput);
					}
				}
				if(response.get(i).get("OverDueRenewalDate")!=null){
					isValidinput = util.inputFieldValidation(response.get(i).get("OverDueRenewalDate"));
					if(!isValidinput.equals("SUCCESS")){
						throw new CustomFault(isValidinput);
					}
				}
				if(response.get(i).get("renewedDate")!=null){
					isValidinput = util.inputFieldValidation(response.get(i).get("renewedDate"));
					if(!isValidinput.equals("SUCCESS")){
						throw new CustomFault(isValidinput);
					}
				}
				if(response.get(i).get("mode_of_subscription")!=null){
					isValidinput = util.inputFieldValidation(response.get(i).get("mode_of_subscription"));
					if(!isValidinput.equals("SUCCESS")){
						throw new CustomFault(isValidinput);
					}
				}

			}*/
			infoLogger.info("Webservice ouput : "+response);
			long endTime = System.currentTimeMillis();
			infoLogger.info("serviceName:LLIRenewalAdminReportService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		}catch(Exception e){
			e.printStackTrace();
			fLogger.error("Exception:"+e.getMessage());
		}
		return response;
	}
}

