package remote.wise.service.webservice;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.UserDetailsReqContract;
import remote.wise.service.datacontract.UserDetailsRespContract;
import remote.wise.service.implementation.UserDetailsImpl;
import remote.wise.util.CommonUtil;
import remote.wise.util.ListToStringConversion;

	@Path("/UserDetailsServiceRESTService")
	public class UserDetailsServiceRESTService 
	{
		
	@POST
	@Path("/getMAConsolidatedUserDetails")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces(MediaType.APPLICATION_JSON)
	public String getMAConsolidatedUserDetails(final @JsonProperty("reqObj") LinkedHashMap<String,Object> reqObj) throws CustomFault, JsonGenerationException, JsonMappingException, IOException
	{
		
				Logger iLogger = InfoLoggerClass.logger;
				//Logger infoLogger = Logger.getLogger("infoLogger");
				Calendar cal = Calendar.getInstance();
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
				String startDate = sdf.format(cal.getTime());
				iLogger.info("Current Startdate: "+startDate);
				long startTime = System.currentTimeMillis();
				UserDetailsReqContract userReqContract = new UserDetailsReqContract();
				CommonUtil util = new CommonUtil();
				String isValidinput=null;
				
				iLogger.info("---- Webservice Input ------");
				List<Integer> tenancyIdList=null;
				String userId=null;
				String isMACustConsolidatedLogin = null;
				if(reqObj.get("tenancyIdList")!=null)
					tenancyIdList=(List<Integer>) reqObj.get("tenancyIdList");
				
				if(reqObj.get("userId")!=null)
					userId=(String) reqObj.get("userId");
				
				if(reqObj.get("userId")!=null)
					userId=(String) reqObj.get("userId");
				if(reqObj.get("isMACustConsolidatedLogin")!=null)
				isMACustConsolidatedLogin=(String) reqObj.get("isMACustConsolidatedLogin");
				
				userReqContract.setTenacyIdList(tenancyIdList);
				userReqContract.setMACustConsolidatedLogin(isMACustConsolidatedLogin);
				//userReqContract.setUserId(userId);
				if(userId!=null)
					userReqContract.setUserId(userId);
				if(userId!=null && !(userId.contains("|"))){
						String UserID=new CommonUtil().getUserId(userId);
						userReqContract.setUserId(UserID);
						iLogger.info("Decoded userId::"+userReqContract.getUserId());
				}
						
				List<UserDetailsRespContract> userRespObj =  new UserDetailsImpl().getUserDetails(userReqContract);
//				List<Map<String,Object>> usersList = new ArrayList<>();
				Map<String, Object> map=null;
				Map<String,Map<String,Object>> dataMap = new HashMap<>();
				iLogger.info("----- Webservice Output-----");
				for(int i=0; i<userRespObj.size(); i++)
				{
					map = new HashMap<String,Object>();
					iLogger.info("FirstName:"+userRespObj.get(i).getFirst_name()+",    "+"LastName:"+userRespObj.get(i).getLast_name()+",   " +
							"CountryCode: "+userRespObj.get(i).getCountryCode()+", RoleName: "+userRespObj.get(i).getRole_name()+",   " +
							"role_id: "+userRespObj.get(i).getRole_id()+", PrimaryEmailId: "+userRespObj.get(i).getPrimaryEmailId()+",   " +
							"PrimaryMobNumber: "+userRespObj.get(i).getPrimaryMobNumber()+", Tenancy_id: "+userRespObj.get(i).getTenancy_id()+",   " +
							"LoginId: "+userRespObj.get(i).getLoginId()+",country_code:"+ userRespObj.get(i).getCountryCode()+", TimeZone: "+userRespObj.get(i).getTimeZone()+",   " +
							"Language: "+userRespObj.get(i).getLanguage());
					
					isValidinput = util.inputFieldValidation(userRespObj.get(i).getFirst_name());
					if(!isValidinput.equals("SUCCESS")){
						throw new CustomFault(isValidinput);
					}else
						map.put("FirstName", userRespObj.get(i).getFirst_name());
						
					
					isValidinput = util.inputFieldValidation(userRespObj.get(i).getLast_name());
					if(!isValidinput.equals("SUCCESS")){
						throw new CustomFault(isValidinput);
					}else
						map.put("LastName", userRespObj.get(i).getLast_name());
					
					isValidinput = util.inputFieldValidation(userRespObj.get(i).getPrimaryEmailId());
					if(!isValidinput.equals("SUCCESS")){
						throw new CustomFault(isValidinput);
					}else
						map.put("PrimaryEmailId", userRespObj.get(i).getPrimaryEmailId());
					
					isValidinput = util.inputFieldValidation(userRespObj.get(i).getPrimaryMobNumber());
					if(!isValidinput.equals("SUCCESS")){
						throw new CustomFault(isValidinput);
					}else
						map.put("PrimaryMobNumber", userRespObj.get(i).getPrimaryMobNumber());
					
					isValidinput = util.inputFieldValidation(userRespObj.get(i).getLoginId());
					if(!isValidinput.equals("SUCCESS")){
						throw new CustomFault(isValidinput);
					}else
						map.put("LoginId", userRespObj.get(i).getLoginId());
					
					isValidinput = util.inputFieldValidation(userRespObj.get(i).getLanguage());
					if(!isValidinput.equals("SUCCESS")){
						throw new CustomFault(isValidinput);
					}else
						map.put("Language", userRespObj.get(i).getLanguage());
					
					isValidinput = util.inputFieldValidation(userRespObj.get(i).getRole_name());
					if(!isValidinput.equals("SUCCESS")){
						throw new CustomFault(isValidinput);
					}else
						map.put("RoleId", userRespObj.get(i).getRole_id());
					
					isValidinput = util.inputFieldValidation(userRespObj.get(i).getTimeZone());
					if(!isValidinput.equals("SUCCESS")){
						throw new CustomFault(isValidinput);
					}else
						map.put("TimeZone", userRespObj.get(i).getTimeZone());
					
					isValidinput = util.inputFieldValidation(userRespObj.get(i).getCountryCode());
					if(!isValidinput.equals("SUCCESS")){
						throw new CustomFault(isValidinput);
					}else
						map.put("CountryCode", userRespObj.get(i).getCountryCode());
					
					ListToStringConversion convObj = new ListToStringConversion();
					String asssetGrpIdList = convObj.getIntegerListString(userRespObj.get(i).getAsset_group_id()).toString();
					isValidinput = util.inputFieldValidation(asssetGrpIdList);
					if(!isValidinput.equals("SUCCESS")){
						throw new CustomFault(isValidinput);
					}

					String asssetGrpNameList = convObj.getStringList(userRespObj.get(i).getAsset_group_name()).toString();
					isValidinput = util.inputFieldValidation(asssetGrpNameList);
					if(!isValidinput.equals("SUCCESS")){
						throw new CustomFault(isValidinput);
					}
					
					isValidinput = util.inputFieldValidation(String.valueOf(userRespObj.get(i).getIs_tenancy_admin()));
					if(!isValidinput.equals("SUCCESS")){
						throw new CustomFault(isValidinput);
					}
					
					isValidinput = util.inputFieldValidation(String.valueOf(userRespObj.get(i).getTenancy_id()));
					if(!isValidinput.equals("SUCCESS")){
						throw new CustomFault(isValidinput);
					}else
						map.put("Tenancy_id", userRespObj.get(i).getTenancy_id());
					
					isValidinput = util.inputFieldValidation(String.valueOf(userRespObj.get(i).getTenancyAdminCount()));
					if(!isValidinput.equals("SUCCESS")){
						throw new CustomFault(isValidinput);
					}
					dataMap.put(userRespObj.get(i).getLoginId(), map);
					System.out.println(map);
				}
				Calendar cal1 = Calendar.getInstance();
				SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
				String endDate = sdf1.format(cal1.getTime());
				iLogger.info("Current Enddate: "+endDate);
				ObjectMapper objectMapper = new ObjectMapper();
				String userDetailsJSONString = "";
					userDetailsJSONString = objectMapper.writeValueAsString(dataMap);
				long endTime=System.currentTimeMillis();
				iLogger.info("Webservice Execution Time in ms:"+(endTime-startTime));
				//String userDetailsJSONString = new JSONObject(map).toString();
				return userDetailsJSONString;
		
	}
	public static void main(String[] args) throws JsonGenerationException, JsonMappingException, CustomFault, IOException {
		List<Integer> list = new ArrayList<>();
		list.add(38011);
		list.add(38011);
		list.add(59527);
		list.add(59527);
		list.add(59027);
		list.add(59527);
		list.add(59527);
		list.add(59527);
		list.add(59527);
		list.add(59527);
		list.add(76642);
		list.add(76607);
		list.add(59527);
		list.add(45028);
		list.add(75579);
		list.add(59527);
		list.add(59527);
		list.add(59527);
		list.add(45028);
		list.add(59527);
		list.add(59527);
		list.add(79692);
		list.add(76607);
		list.add(105717);
		list.add(45028);
		list.add(59527);
		list.add(59527);
		list.add(59527);
		list.add(59527);
		list.add(59527);
		list.add(45028);
		list.add(52776);
		list.add(59527);
		list.add(59527);
		list.add(59527);
		list.add(85862);
		list.add(59527);
		list.add(59527);
		list.add(76642);
		list.add(59527);
		list.add(59527);
		list.add(59527);
		list.add(82493);
		list.add(44237);
		list.add(95748);
		list.add(101190);
		list.add(39204);
		list.add(72634);
		list.add(96949);
		list.add(72634);
		list.add(96949);
		list.add(72634);
		list.add(96949);
		list.add(72634);
		list.add(96949);
		list.add(72634);
		list.add(96949);
		list.add(72634);
		list.add(96949);
		list.add(72633);
		list.add(80284);
		list.add(96952);
		list.add(72633);
		list.add(80284);
		list.add(96952);
		list.add(72633);
		list.add(80284);
		list.add(96952);
		list.add(72633);
		list.add(80284);
		list.add(96952);
		list.add(72633);
		list.add(80284);
		list.add(96952);
		list.add(288);
		list.add(51455);
		list.add(79979);
		list.add(38011);
		list.add(45028);
		list.add(58812);
		list.add(72634);
		list.add(96949);
		list.add(72634);
		list.add(96949);
		list.add(72634);
		list.add(96949);
		list.add(72634);
		list.add(96949);
		list.add(72634);
		list.add(96949);
		list.add(72634);
		list.add(96949);
		list.add(72634);
		list.add(96949);
		list.add(72634);
		list.add(96949);
		list.add(72634);
		list.add(96949);
		list.add(72634);
		list.add(96949);
		list.add(72633);
		list.add(80284);
		list.add(96952);
		list.add(72633);
		list.add(80284);
		list.add(96952);
		list.add(72633);
		list.add(80284);
		list.add(96952);
		list.add(72633);
		list.add(80284);
		list.add(96952);
		list.add(72633);
		list.add(80284);
		list.add(96952);
		list.add(72633);
		list.add(80284);
		list.add(96952);
		list.add(72633);
		list.add(80284);
		list.add(96952);
		list.add(72633);
		list.add(80284);
		list.add(96952);
		list.add(72633);
		list.add(80284);
		list.add(96952);
		list.add(72633);
		list.add(80284);
		list.add(96952);
		list.add(72633);
		list.add(80284);
		list.add(96952);
		list.add(72633);
		list.add(80284);
		list.add(96952);
		list.add(292);
		list.add(45028);
		list.add(72634);
		list.add(96949);
		list.add(72634);
		list.add(96949);
		list.add(72634);
		list.add(96949);
		list.add(72634);
		list.add(96949);
		list.add(72634);
		list.add(96949);
		list.add(72633);
		list.add(80284);
		list.add(96952);
		list.add(72633);
		list.add(80284);
		list.add(96952);
		list.add(72633);
		list.add(80284);
		list.add(96952);
		list.add(72633);
		list.add(80284);
		list.add(96952);
		list.add(72633);
		list.add(80284);
		list.add(96952);
		list.add(72633);
		list.add(80284);
		list.add(96952);
		list.add(72633);
		list.add(80284);
		list.add(96952);
		list.add(45028);
		list.add(72634);
		list.add(96949);
		list.add(72634);
		list.add(96949);
		list.add(72634);
		list.add(96949);
		list.add(72634);
		list.add(96949);
		list.add(72634);
		list.add(96949);
		list.add(72633);
		list.add(80284);
		list.add(96952);
		list.add(72633);
		list.add(80284);
		list.add(96952);
		list.add(72633);
		list.add(80284);
		list.add(96952);
		list.add(72633);
		list.add(80284);
		list.add(96952);
		list.add(72633);
		list.add(80284);
		list.add(96952);
		list.add(291);
		list.add(288);
		list.add(51455);
		list.add(79979);
		list.add(59526);
		list.add(72634);
		list.add(96949);
		list.add(72634);
		list.add(96949);
		list.add(72634);
		list.add(96949);
		list.add(72634);
		list.add(96949);
		list.add(72634);
		list.add(96949);
		list.add(72634);
		list.add(96949);
		list.add(72633);
		list.add(80284);
		list.add(96952);
		list.add(72633);
		list.add(80284);
		list.add(96952);
		list.add(72633);
		list.add(80284);
		list.add(96952);
		list.add(72633);
		list.add(80284);
		list.add(96952);
		list.add(72633);
		list.add(80284);
		list.add(96952);
		list.add(72633);
		list.add(80284);
		list.add(96952);
		list.add(290);
		list.add(10040);
		list.add(288);
		list.add(51455);
		list.add(79979);
		list.add(72634);
		list.add(96949);
		list.add(72634);
		list.add(96949);
		list.add(72634);
		list.add(96949);
		list.add(72634);
		list.add(96949);
		list.add(72634);
		list.add(96949);
		list.add(72634);
		list.add(96949);
		list.add(72633);
		list.add(80284);
		list.add(96952);
		list.add(72633);
		list.add(80284);
		list.add(96952);
		list.add(72633);
		list.add(80284);
		list.add(96952);
		list.add(58821);
		list.add(72633);
		list.add(80284);
		list.add(96952);
		list.add(72634);
		list.add(96949);
		list.add(72634);
		list.add(96949);
		list.add(72634);
		list.add(96949);
		list.add(72634);
		list.add(96949);
		list.add(72634);
		list.add(96949);
		list.add(72633);
		list.add(80284);
		list.add(96952);
		list.add(72633);
		list.add(80284);
		list.add(96952);
		list.add(72633);
		list.add(80284);
		list.add(96952);
		list.add(72633);
		list.add(80284);
		list.add(96952);
		list.add(72633);
		list.add(80284);
		list.add(96952);
		LinkedHashMap<String,Object> reqObj=new LinkedHashMap<String,Object>();
		reqObj.put("tenancyIdList", list);
		reqObj.put("isMACustConsolidatedLogin", "true");
		//reqObj.put("userId","name|mohan");
		new UserDetailsServiceRESTService().getMAConsolidatedUserDetails(reqObj);
	}
	
}
