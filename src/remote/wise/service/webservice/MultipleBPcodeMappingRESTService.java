/**
 * 
 */
package remote.wise.service.webservice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.JsonMappingException;
import org.json.simple.JSONObject;

import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.MultipleBPcodeMappingImpl;
import remote.wise.util.CommonUtil;
import remote.wise.util.ListToStringConversion;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * @author ROOPN5
 *
 */
@Path("/MultipleBPcodeMappingRESTService")
public class MultipleBPcodeMappingRESTService {
	
	@GET
	@Path("getLLCodeData")
	@Produces(MediaType.APPLICATION_JSON)
	public String getLLCodeData(@QueryParam("loginID") String loginID,@QueryParam("accountCode") String accountCode,@QueryParam("pageNum") int pageNum) throws CustomFault{

		Logger iLogger = InfoLoggerClass.logger;
		List<HashMap<String,Object>> response =new ArrayList<HashMap<String,Object>>();
		String result=null;
		
		//DF20170919 @Roopa getting decoded UserId
		loginID=new CommonUtil().getUserId(loginID);
		iLogger.info("Decoded userId::"+loginID);

		//DF20180713: KO369761 - Security Check added for input text fields.
		CommonUtil util = new CommonUtil();
		String isValidinput=null;
		isValidinput = util.inputFieldValidation(accountCode);
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		isValidinput = util.inputFieldValidation(String.valueOf(pageNum));
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		
		Gson gson = new Gson();

		iLogger.info("MultipleBPcodeMappingRESTService WebService Input-----> loginID:"+loginID);
		long startTime = System.currentTimeMillis();

		response = new MultipleBPcodeMappingImpl().getMultipleBPcodeData(accountCode,pageNum);
		
		//DF20181008-KO369761-XSS validation of output response contract
		for(int i = 0;i<response.size();i++){
			isValidinput = util.inputFieldValidation((String)response.get(i).get("Account_ID"));
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = util.inputFieldValidation((String)response.get(i).get("Account_Name"));
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = util.inputFieldValidation((String)response.get(i).get("mapping_code"));
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}

		if(response!=null)
			result = gson.toJsonTree(
					response,
					new TypeToken<List<HashMap<String,Object>>>() {
					}.getType()).toString();

		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:MultipleBPcodeMappingRESTService~executionTime:"+(endTime-startTime)+"~"+""+"~"+result);
		
		return result;


	}
	
	@GET
	@Path("getMappedAccountDataForLLCode")
	@Produces(MediaType.APPLICATION_JSON)
	public String getMappedAccountDataForLLCode(@QueryParam("LLCode") String LLCode) throws CustomFault{

		Logger iLogger = InfoLoggerClass.logger;
		List<HashMap<String,Object>> response =new ArrayList<HashMap<String,Object>>();
		String result=null;
		
		//DF20180713: KO369761 - Security Check added for input text fields.
		CommonUtil util = new CommonUtil();
		String isValidinput=null;
		isValidinput = util.inputFieldValidation(LLCode);
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		
		Gson gson = new Gson();

		iLogger.info("MultipleBPcodeMappingRESTService::getMappedAccountDataForLLCode WebService Input-----> LLCode:"+LLCode);
		long startTime = System.currentTimeMillis();

		response = new MultipleBPcodeMappingImpl().getMappedAccountDataForLLCode(LLCode);
		
		//DF20181008-KO369761-XSS validation of output response contract
		for(int i = 0;i<response.size();i++){
			isValidinput = util.inputFieldValidation((String)response.get(i).get("Account_ID"));
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = util.inputFieldValidation((String)response.get(i).get("Account_Name"));
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = util.inputFieldValidation((String)response.get(i).get("Account_Code"));
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = util.inputFieldValidation((String)response.get(i).get("mapping_code"));
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}


		if(response!=null)
			result = gson.toJsonTree(
					response,
					new TypeToken<List<HashMap<String,Object>>>() {
					}.getType()).toString();

		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:MultipleBPcodeMappingRESTService~executionTime:"+(endTime-startTime)+"~"+""+"~"+result);
		
		return result;


	}
	@POST
	@Path("setMappedAccountDataForLLCode")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String setMappedAccountDataForLLCode(@Context HttpHeaders httpHeaders, @JsonProperty LinkedHashMap<String,String> fields) throws CustomFault
	{
		
		String result=null;
		String csrfToken = null;
		boolean isValidCSRF = false;
		Logger iLogger = InfoLoggerClass.logger;
		
		if(fields.get("loginID")==null)
		{
			result="loginID is mandatory";	
			return result;
		}
		
		else if(fields.get("LLCode")==null)
		{
			result="LLCode is mandatory";	
			return result;
		}
		else if(fields.get("mappingAccCodeList")==null || fields.get("mappingAccCodeList").split(",").length==0){
			result="MappingList is mandatory";
			
			return result;
		}
		else{

			//DF20180713: KO369761 - Security Check added for input text fields.
			CommonUtil util = new CommonUtil();
			String isValidinput=null;

			// DF20181015 Avinash Xavier : CSRF Token Validation ---Start---.
			if (httpHeaders.getRequestHeader("CSRFTOKEN") != null) {
				csrfToken = httpHeaders.getRequestHeader("CSRFTOKEN").get(0);
			}
			if (csrfToken != null && fields.get("loginID") != null) {
				isValidCSRF = util.validateANTICSRFTOKEN(fields.get("loginID"),
						csrfToken);
			}
			iLogger.info("MultipleBPcodeMappingRESTService :: setMappedAccountDataForLLCode ::   csrftoken isValidCSRF :: "
					+ isValidCSRF);
			if (!isValidCSRF) {
				iLogger.info("MultipleBPcodeMappingRESTService :: setMappedAccountDataForLLCode ::  Invalid request.");
				throw new CustomFault("Invalid request.");
			} else {
				util.deleteANTICSRFTOKENS(fields.get("loginID"), csrfToken,
						"one");
			}
			// DF20181015 Avinash Xavier : CSRF Token Validation ---end---.
			
			isValidinput = util.inputFieldValidation(fields.get("LLCode"));
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			
			ListToStringConversion conversion = new ListToStringConversion();
			List<String> mappingAccCodeList=Arrays.asList(fields.get("mappingAccCodeList").split(","));
			String mappingAccCodeListAsString=conversion.getStringList(mappingAccCodeList).toString();
			
			isValidinput = util.inputFieldValidation(mappingAccCodeListAsString);
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			
			result=new MultipleBPcodeMappingImpl().setMappedAccountDataForLLCode(fields);
		}
		
		
		JSONObject json = new JSONObject();
		json.put("RESULT", result);

		result=json.toJSONString();
		
		return result;
		
	}
	
	//Df20180613 @Roopa VIN based mapping
	@GET
	@Path("getVINDetails")
	@Produces(MediaType.APPLICATION_JSON)
	public String getVINDetails(@Context HttpHeaders httpHeaders, @QueryParam("loginID") String loginID,@QueryParam("vin") String vin) throws CustomFault{

		Logger iLogger = InfoLoggerClass.logger;
		HashMap<String,Object> response =new HashMap<String,Object>();
		String result=null;
		String csrfToken = null;
		boolean isValidCSRF = false;
		CommonUtil util = new CommonUtil();
		
		// DF20181015 Avinash Xavier : CSRF Token Validation ---Start---.
		if (httpHeaders.getRequestHeader("CSRFTOKEN") != null) {
			csrfToken = httpHeaders.getRequestHeader("CSRFTOKEN").get(0);
		}
		if (csrfToken != null
				&& (loginID != null && !loginID.equalsIgnoreCase("null"))) {
			isValidCSRF = util.validateANTICSRFTOKEN(loginID, csrfToken);
		}
		iLogger.info("MultipleBPcodeMappingRESTService :: getVINDetails ::   csrftoken isValidCSRF :: "
				+ isValidCSRF);
		if (!isValidCSRF) {
			iLogger.info("MultipleBPcodeMappingRESTService :: getVINDetails ::  Invalid request.");
			throw new CustomFault("Invalid request.");
		}
		// DF20181015 Avinash Xavier : CSRF Token Validation ---end---.
		
		//DF20180713: KO369761 - Security Check added for input text fields.
		String isValidinput=null;
		isValidinput = util.inputFieldValidation(vin);
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		
		Gson gson = new Gson();

		iLogger.info("MultipleBPcodeMappingRESTService::getVINDetails WebService Input-----> loginID:"+loginID);
		long startTime = System.currentTimeMillis();

		response = new MultipleBPcodeMappingImpl().getVINDetails(loginID,vin);
		
		//DF20181008-KO369761-XSS validation of output response contract
		isValidinput = util.inputFieldValidation((String)response.get("DealerName"));
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		isValidinput = util.inputFieldValidation((String)response.get("CustomerName"));
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		isValidinput = util.inputFieldValidation((String)response.get("BPCode"));
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		isValidinput = util.inputFieldValidation((String)response.get("VIN"));
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}

		if(response!=null)
			result = gson.toJson(
					response,
					new TypeToken<HashMap<String,Object>>() {
					}.getType()).toString();

		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:MultipleBPcodeMappingRESTService~executionTime:"+(endTime-startTime)+"~"+""+"~"+result);

		return result;


	}
	
	@GET
	@Path("downloadDetails")
	@Produces(MediaType.APPLICATION_JSON)
	public String downloadDetails(@Context HttpHeaders httpHeaders, @QueryParam("loginID") String loginID,@QueryParam("BPCode") String BPCode) throws CustomFault{

		Logger iLogger = InfoLoggerClass.logger;
		List<HashMap<String, Object>> respObjList = new ArrayList<HashMap<String,Object>>();
		String result=null;
		String csrfToken = null;
		boolean isValidCSRF = false;
		
		//DF20180713: KO369761 - Security Check added for input text fields.
		CommonUtil util = new CommonUtil();
		String isValidinput=null;
		
		// DF20181015 Avinash Xavier : CSRF Token Validation ---Start---.
		if (httpHeaders.getRequestHeader("CSRFTOKEN") != null) {
			csrfToken = httpHeaders.getRequestHeader("CSRFTOKEN").get(0);
		}

		if (csrfToken != null
				&& (loginID != null && !loginID.equalsIgnoreCase("null"))) {
			isValidCSRF = util.validateANTICSRFTOKEN(loginID, csrfToken);
		}
		iLogger.info("MultipleBPcodeMappingRESTService :: getVINDetails ::   csrftoken isValidCSRF :: "
				+ isValidCSRF);
		if (!isValidCSRF) {
			iLogger.info("MultipleBPcodeMappingRESTService :: getVINDetails ::  Invalid request.");
			throw new CustomFault("Invalid request.");
		}
		// DF20181015 Avinash Xavier : CSRF Token Validation ---end---.
		
		isValidinput = util.inputFieldValidation(BPCode);
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		
		Gson gson = new Gson();

		iLogger.info("MultipleBPcodeMappingRESTService::downloadDetails WebService Input-----> loginID:"+loginID);
		long startTime = System.currentTimeMillis();

		respObjList = new MultipleBPcodeMappingImpl().downloadDetails(loginID,BPCode);
		
		//DF20181008-KO369761-XSS validation of output response contract
		for(int i = 0;i<respObjList.size();i++){
			isValidinput = util.inputFieldValidation((String)respObjList.get(i).get("DealerName"));
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = util.inputFieldValidation((String)respObjList.get(i).get("CustomerName"));
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = util.inputFieldValidation((String)respObjList.get(i).get("BPCode"));
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = util.inputFieldValidation((String)respObjList.get(i).get("VIN"));
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}

		if(respObjList!=null)
			result = gson.toJsonTree(
					respObjList,
					new TypeToken<List<HashMap<String,Object>>>() {
					}.getType()).toString();

		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:MultipleBPcodeMappingRESTService~executionTime:"+(endTime-startTime)+"~"+""+"~"+result);

		return result;


	}
	
	// Shajesh : 2021-01-14 : BP Code unmerge at VIN level
	@SuppressWarnings("unchecked")
		@POST
		@Path("updateMappingCodeByVin")
		@Produces(MediaType.APPLICATION_JSON)
		@Consumes(MediaType.APPLICATION_JSON)
		public String updateMappingCodeByVIN(
				final @JsonProperty("reqObj") LinkedHashMap<String, Object> reqObj)
				throws CustomFault, JsonGenerationException, JsonMappingException,
				IOException {

			Logger infoLogger = InfoLoggerClass.logger;
			Logger fLogger = FatalLoggerClass.logger;
			String response = null;
			String userID=null;
			List<String> vinNumberList = null;
			for (int i = 0; i < reqObj.size(); i++) {
				if (reqObj.get("VIN") != null) {
					vinNumberList = (List<String>) reqObj.get("VIN");
				}
			}
		// LL-147 : Sai Divya : Traceability for BP code un-merging .sn
		String loginID = (String) reqObj.get("loginID");
		infoLogger.info("Received LoginID" + loginID);
		if (loginID != null) {

			infoLogger.info("Initial login ID: " + loginID);
			userID = new CommonUtil().getUserId(loginID);

			if (userID == null) {
				throw new CustomFault("Invalid Login ID: " + loginID);
			} else {
				// Set the login ID to the retrieved user ID

				infoLogger.info("Updated login ID with user ID: " + loginID);
			}

		} else {
			infoLogger.info("Login ID is null.");
		}
		// LL-147 : Sai Divya : Traceability for BP code un-merging .en
			if (vinNumberList != null && !vinNumberList.isEmpty()) {
				try {
					infoLogger.info("Webservice input : " + vinNumberList);
					response = new MultipleBPcodeMappingImpl().updateMappingCodeByVin(vinNumberList,userID);
					infoLogger.info("Webservice Output: " + response);
				} catch (Exception e) {
					fLogger.error("Exception:" + e.getMessage());
				}

			}
			return response;
		}
	

}
