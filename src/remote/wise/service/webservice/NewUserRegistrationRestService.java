package remote.wise.service.webservice;
/**
 * CR469-20408644:Sai Divya:APIs for NewUserRegistration
 */
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import remote.wise.dao.NewUserRegistrationDao;
import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.UserAuthenticationRespContract;
import remote.wise.service.datacontract.UserDetailsRespContract;
import remote.wise.service.implementation.NewUserRegistrationImpl;
import remote.wise.service.implementation.UserDetailsImpl;
import remote.wise.util.CommonUtil;
import remote.wise.util.DateUtil;
import remote.wise.util.ListToStringConversion;


/**
 * @author  :Sai Divya
 * 20240424
 */

@Path("/NewUserRegistrationService")
public class NewUserRegistrationRestService {
	
	
	@POST
	@Path("setNewUserDetails")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String saveNewUserDetail(HashMap<String, String> userDetails,@QueryParam("loginID") String loginID) throws CustomFault{
		
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		String response = "FAILURE";
		iLogger.info("===================================");
		long startTime = System.currentTimeMillis();
		
		try {
			iLogger.info("Webservice input : " + userDetails);

			CommonUtil util = new CommonUtil();
			String isValidinput = null;
			String csrfToken = null;
			iLogger.info(loginID);
			 if (loginID != null) {
		            iLogger.info("Initial login ID: " + loginID);
		            String userID=new CommonUtil().getUserId(loginID);
		        
		            if (userID == null) {
		                throw new CustomFault("Invalid Login ID: " + loginID);
		            } else {
		                // Set the login ID to the retrieved user ID
		            	 userDetails.put("userID", userID);
		                iLogger.info("Updated login ID with user ID: " +loginID);
		            }
		        } else {
		            iLogger.info("Login ID is null.");
		        }


			
			isValidinput = util.inputFieldValidation(userDetails.get("firstName"));
			if (!isValidinput.equals("SUCCESS")) {
				throw new CustomFault(isValidinput);
				
			}
			
			isValidinput = util.inputFieldValidation(userDetails.get("lastName"));
			if (!isValidinput.equals("SUCCESS")) {
				throw new CustomFault(isValidinput);
			}
			
			isValidinput = util.inputFieldValidation(userDetails.get("emailId"));
		
			if (!isValidinput.equals("SUCCESS")) {
				throw new CustomFault(isValidinput);
			}
		
			isValidinput = util.inputFieldValidation(userDetails.get("mobileNumber"));
			if (!isValidinput.equals("SUCCESS")) {
				throw new CustomFault(isValidinput);
			}
			
			isValidinput = util.inputFieldValidation(userDetails.get("approvedBy"));
			if (!isValidinput.equals("SUCCESS")) {
				throw new CustomFault(isValidinput);
			}
			
			isValidinput = util.inputFieldValidation(userDetails.get("language"));
			if (!isValidinput.equals("SUCCESS")) {
				throw new CustomFault(isValidinput);
			}
			
			isValidinput = util.inputFieldValidation(userDetails.get("countryCode"));
			if (!isValidinput.equals("SUCCESS")) {
				throw new CustomFault(isValidinput);
			}
			
			isValidinput = util.inputFieldValidation(userDetails.get("approvedStatus"));
			if (!isValidinput.equals("SUCCESS")) {
				throw new CustomFault(isValidinput);
			}
			
			isValidinput = util.inputFieldValidation(userDetails.get("raisedBy"));
			if (!isValidinput.equals("SUCCESS")) {
				throw new CustomFault(isValidinput);
			}
			isValidinput = util.inputFieldValidation(userDetails.get("zonalCode"));
			
			if (!isValidinput.equals("SUCCESS")) {
				throw new CustomFault(isValidinput);
			}
			
			isValidinput = util.inputFieldValidation(userDetails.get("department"));
			if (!isValidinput.equals("SUCCESS")) {
				
				throw new CustomFault(isValidinput);
			}
			isValidinput = util.inputFieldValidation(userDetails.get("loginTenancyId"));
			if (!isValidinput.equals("SUCCESS")) {
				
				throw new CustomFault(isValidinput);
			}
			isValidinput = util.inputFieldValidation(userDetails.get("reason"));
			if (!isValidinput.equals("SUCCESS")) {
				
				throw new CustomFault(isValidinput);
			}
			isValidinput = util.inputFieldValidation(userDetails.get("roleID"));
			if (!isValidinput.equals("SUCCESS")) {
				
				throw new CustomFault(isValidinput);
			}
			isValidinput = util.inputFieldValidation(userDetails.get("roleName"));
			if (!isValidinput.equals("SUCCESS")) {
				
				throw new CustomFault(isValidinput);
			}
			isValidinput = util.inputFieldValidation(userDetails.get("timezone"));
			if (!isValidinput.equals("SUCCESS")) {
				
				throw new CustomFault(isValidinput);
			}
			iLogger.info(userDetails.get("firstName"));
			NewUserRegistrationImpl impl = new NewUserRegistrationImpl();
			response = impl.createUser(userDetails);
			iLogger.info(response);
			
			long endTime=System.currentTimeMillis();
			iLogger.info("NewUserRegistrationRestService:userClient:WebService End:status:"+response+":ExecutionTime:"+(endTime-startTime)+"~"+""+"~");
		} catch (Exception e) {
			e.printStackTrace();
			fLogger.error("Exception caught : " + e.getMessage());
		}
		return response;
		
	}
	
	@GET
	@Path("/user/v1/status")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public List<Map<String, String>> getUserDetails(@QueryParam("loginTenancyList") String loginTenancyList)
	{
		Logger infoLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		List<Map<String, String>> response = new LinkedList<>();
		try {
		
			if (!(loginTenancyList == null || loginTenancyList.isEmpty())) {

				String[] loginTenancyIdList = loginTenancyList.split(",");
				List<Integer> loginTenancyIdIntList = new ArrayList<Integer>();
				for (String tenancyid : loginTenancyIdList) {
					loginTenancyIdIntList.add(Integer.parseInt(tenancyid.trim()));
				}
				List<Integer> userTenancyList = new DateUtil().getLinkedTenancyListForTheTenancy(loginTenancyIdIntList);

				loginTenancyList = new ListToStringConversion().getIntegerListString(userTenancyList).toString();
			}
		 response=new NewUserRegistrationDao().getUserDetailsforTenancy(loginTenancyList);
		}
		
		catch(Exception e){
			e.printStackTrace();
			fLogger.error("Exception:"+e.getMessage());
		}
			return response;
	}
	

	
	
	@POST
	@Path("updateUserDetails")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String updateUserDetails(@QueryParam("loginID") String loginID,
	                                @QueryParam("status") String status,
	                                @QueryParam("id") String id,  @QueryParam("approvedBy") String approvedBy) {

	    String response = "FAILURE";
	    Logger infoLogger = InfoLoggerClass.logger;
	    Logger fLogger = FatalLoggerClass.logger;
	    String userID;
	    // Validate input fields if needed
        
	    try {
	    	

	        // Example of logging the received parameter
	        infoLogger.info("Received approvedBy: " + approvedBy);
	    	
	        if (loginID != null) {
	            infoLogger.info("Initial login ID: " + loginID);

	            // Retrieve the user ID directly from the login ID
	             userID = new CommonUtil().getUserId(loginID);

	            if (userID == null) {
	                throw new CustomFault("Invalid Login ID: " + loginID);
	            } else {
	            	
	                infoLogger.info("Updated login ID with user ID: " + userID);
	            }
	        } else {
	            infoLogger.info("Login ID is null.");
	            throw new CustomFault("Login ID is required.");
	        }

	        
	        // Instantiate the DAO and delegate the rest of the operations
	        NewUserRegistrationDao userDao = new NewUserRegistrationDao();
	        boolean updateSuccess = userDao.updateUserDetails(id, status,approvedBy);

	        if (updateSuccess) {
	            response = "SUCCESS:UserDetails Updated Successfully";
	        } else {
	            response = "FAILURE";
	        }

	    } catch (CustomFault e) {
	        fLogger.error("CustomFault encountered: " + e.getMessage(), e);
	        response = e.getMessage();
	    } catch (Exception e) {
	        e.printStackTrace();
	        fLogger.error("Exception: " + e.getMessage(), e);
	        response = "Exception occurred: " + e.getMessage();
	    }

	    return response;
	}


	@POST
	@Path("/generateOTPtoMobile")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String generateOTPToMobile(@QueryParam("loginID") String loginID,
	                                  @QueryParam("mobileNumber") String mobileNumber) {
	    String userID = null;
	    String response = "FAILURE";
	    Logger infoLogger = InfoLoggerClass.logger;
	    Logger fLogger = FatalLoggerClass.logger;

	    try {
	        if (loginID != null) {
	            infoLogger.info("Initial login ID: " + loginID);

	            // Retrieve the user ID directly from the login ID
	            userID = new CommonUtil().getUserId(loginID);

	            if (userID == null) {
	                throw new CustomFault("Invalid Login ID: " + loginID);
	            } else {
	                
	                infoLogger.info("Updated login ID with user ID: " + userID);
	            }
	        } else {
	            infoLogger.info("Login ID is null.");
	        }
	        
	        NewUserRegistrationImpl impl = new NewUserRegistrationImpl();
	        response = impl.generateOTPMobile(mobileNumber);
	    } catch (Exception e) {
	        e.printStackTrace();
	        fLogger.error("Exception caught: " + e.getMessage());
	        response = "FAILURE: " + e.getMessage();
	    }

	    return response;
	}
	
	
	
	@POST
	@Path("/generateOTPtoEmailID")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String generateOTPToEmailID(@QueryParam("loginID") String loginID,
	                                  @QueryParam("emailID") String emailID) {
	    String userID = null;
	    String response = "FAILURE";
	    Logger infoLogger = InfoLoggerClass.logger;
	    Logger fLogger = FatalLoggerClass.logger;

	    try {
	        if (loginID != null) {
	            infoLogger.info("Initial login ID: " + loginID);

	            // Retrieve the user ID directly from the login ID
	            userID = new CommonUtil().getUserId(loginID);

	            if (userID == null) {
	                throw new CustomFault("Invalid Login ID: " + loginID);
	            } else {
	                
	                infoLogger.info("Updated login ID with user ID: " + userID);
	            }
	        } else {
	            infoLogger.info("Login ID is null.");
	        }
	        
	        NewUserRegistrationImpl impl = new NewUserRegistrationImpl();
	        response = impl.generateOTPemailID(emailID);
	    } catch (Exception e) {
	        e.printStackTrace();
	        fLogger.error("Exception caught: " + e.getMessage());
	        response = "FAILURE: " + e.getMessage();
	    }

	    return response;
	}
	
	
	
	
	
	@POST
	@Path("/validateOTPtoMobile")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String validateOTP(@QueryParam("mobileNumber") String mobileNumber,
	                          @QueryParam("otp") String otp) {
	    String response = "FAILURE";
	    Logger infoLogger = InfoLoggerClass.logger;
	    Logger fLogger = FatalLoggerClass.logger;
	    NewUserRegistrationDao obj=new NewUserRegistrationDao();
	    response=obj.validateOTP(mobileNumber,otp);
	    return response;
	}
	
	

	@POST
	@Path("/validateOTPtoEmail")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String validateOTPtoEmail(@QueryParam("emailID") String emailID,
	                          @QueryParam("otp") String otp) {
	    String response = "FAILURE";
	    Logger infoLogger = InfoLoggerClass.logger;
	    Logger fLogger = FatalLoggerClass.logger;
	    NewUserRegistrationDao obj=new NewUserRegistrationDao();
	    response=obj.validateOTPtoEmail(emailID,otp);
	    return response;
	}
	
	
	
	
	/** 
	 * @author suresh soorneedi
	 * this method will get all the languages that supports sms translation
	 * @return responseList: returns the list of languages
	 * throws exception CustomFault
	 */

	@GET
	@Path("/getLanguages")
	@Produces(MediaType.APPLICATION_JSON) 
	@Consumes(MediaType.APPLICATION_JSON)
	public String getLanguages()
	{
		Logger iLogger = InfoLoggerClass.logger;
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("-----webservice input-----");
		String response = "FAILURE";
		CommonUtil util = new CommonUtil();
		String isValidinput = null;
		try {
			List<String> responseList = new UserDetailsImpl().getLanguages();
			response = new ObjectMapper().writeValueAsString(responseList);
			iLogger.info("----- Webservice Output-----");
			for(int i=0;i<responseList.size();i++)
			{
				iLogger.info("Language:"+responseList.get(i));

				isValidinput = util.inputFieldValidation(responseList.get(i));
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
		}catch (CustomFault e) {
			response = e.getFaultInfo();
			iLogger.info("Exception occurred" + e.getFaultInfo());
		}catch (Exception e) {
			response = e.getMessage();
			iLogger.info("Exception occurred" + e.getMessage());
		}
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:UserDetailsService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		return response;
	}

	
	
	
	
}
	


