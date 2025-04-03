package remote.wise.service.webservice;
/*
 * CR498 : Sai Divya : 20250403 : APIs for Dealer master data & SMS Notifications 
 */
import java.util.HashMap;
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
import org.apache.logging.log4j.Logger;
import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.DealerDataAndSMSNotificationsImpl;
import remote.wise.util.CommonUtil;
import remote.wise.util.NotificationSubscriberDealersTopic;


/**
 * @author :Sai Divya 20241119
 */

@Path("/DealerDataAndSMSNotifications")
public class DealerDataAndSMSNotificationsRestService {

	@POST
	@Path("createDealerDetails")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String addDEalerUser(HashMap<String, String> dealerDetails, @QueryParam("loginID") String loginID,
			@QueryParam("pflag") String pflag) {

		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		String response = "FAILURE";
		long startTime = System.currentTimeMillis();

		try {
			iLogger.info("Webservice input : " + dealerDetails);
			iLogger.info("pflag :" + pflag);

			CommonUtil util = new CommonUtil();
			String isValidinput = null;
			String csrfToken = null;
			iLogger.info(loginID);
			if (loginID != null) {
				iLogger.info("Initial login ID: " + loginID);
				String userID = new CommonUtil().getUserId(loginID);

				if (userID == null) {
					throw new CustomFault("Invalid Login ID: " + loginID);
				} else {
					// Set the login ID to the retrieved user ID
					dealerDetails.put("userID", userID);
					iLogger.info("Updated login ID with user ID: " + loginID);
				}
			} else {

				iLogger.info("Login ID is null.");
				throw new CustomFault("Invalid Login ID: " + loginID);
			}

			if (dealerDetails.get("dealerCode") != null && !dealerDetails.get("dealerCode").isEmpty()) {
				isValidinput = util.inputFieldValidation(dealerDetails.get("dealerCode"));
				if (!isValidinput.equals("SUCCESS")) {
					throw new CustomFault(isValidinput);
				}
			} else {
				response = "FAILURE: dealerCode is not provided";
				iLogger.error(response);
				return response;
			}

			if (dealerDetails.get("dealerPrincipalName") != null
					&& !dealerDetails.get("dealerPrincipalName").isEmpty()) {
				isValidinput = util.inputFieldValidation(dealerDetails.get("dealerPrincipalName"));
				if (!isValidinput.equals("SUCCESS")) {
					throw new CustomFault(isValidinput);
				}
			} else {
				response = "FAILURE: dealerPrincipleName is not provided";
				iLogger.error(response);
				return response;
			}

			if (dealerDetails.get("address") != null && !dealerDetails.get("address").isEmpty()) {
				isValidinput = util.inputFieldValidation(dealerDetails.get("address"));
				if (!isValidinput.equals("SUCCESS")) {
					throw new CustomFault(isValidinput);
				}
			} else {
				response = "FAILURE: adress is not provided";
				iLogger.error(response);
				return response;
			}

			isValidinput = util.inputFieldValidation(dealerDetails.get("mobileNumber"));
			if (!isValidinput.equals("SUCCESS")) {
				throw new CustomFault(isValidinput);
			}
			isValidinput = util.inputFieldValidation(dealerDetails.get("timeZone"));
			if (!isValidinput.equals("SUCCESS")) {
				throw new CustomFault(isValidinput);
			}

			if (dealerDetails.get("email") != null && !dealerDetails.get("email").isEmpty()) {
				isValidinput = util.inputFieldValidation(dealerDetails.get("email"));
				if (!isValidinput.equals("SUCCESS")) {
					throw new CustomFault(isValidinput);
				}
			} else {
				response = "FAILURE: email is not provided";
				iLogger.error(response);
				return response;
			}

			if (dealerDetails.get("city") != null && !dealerDetails.get("city").isEmpty()) {
				isValidinput = util.inputFieldValidation(dealerDetails.get("city"));
				if (!isValidinput.equals("SUCCESS")) {
					throw new CustomFault(isValidinput);
				}
			} else {
				response = "FAILURE: city is not provided";
				iLogger.error(response);
				return response;
			}

			if (dealerDetails.get("zipCode") != null && !dealerDetails.get("zipCode").isEmpty()) {
				isValidinput = util.inputFieldValidation(dealerDetails.get("zipCode"));
				if (!isValidinput.equals("SUCCESS")) {
					throw new CustomFault(isValidinput);
				}
			} else {
				response = "FAILURE: zipCode is not provided";
				iLogger.error(response);
				return response;
			}

			if (dealerDetails.get("countryName") != null && !dealerDetails.get("countryName").isEmpty()) {
				isValidinput = util.inputFieldValidation(dealerDetails.get("countryName"));
				if (!isValidinput.equals("SUCCESS")) {
					throw new CustomFault(isValidinput);
				}
			} else {
				response = "FAILURE: countryName is not provided";
				iLogger.error(response);
				return response;
			}

			if (dealerDetails.get("dealerAccountName") != null && !dealerDetails.get("dealerAccountName").isEmpty()) {
				isValidinput = util.inputFieldValidation(dealerDetails.get("dealerAccountName"));
				if (!isValidinput.equals("SUCCESS")) {
					throw new CustomFault(isValidinput);
				}
			} else {
				response = "FAILURE: dealerAccountName is not provided";
				iLogger.error(response);
				return response;
			}

			if (dealerDetails.get("state") != null && !dealerDetails.get("state").isEmpty()) {
				isValidinput = util.inputFieldValidation(dealerDetails.get("state"));
				if (!isValidinput.equals("SUCCESS")) {
					throw new CustomFault(isValidinput);
				}
			} else {
				response = "FAILURE: state is not provided";
				iLogger.error(response);
				return response;
			}

			DealerDataAndSMSNotificationsImpl impl = new DealerDataAndSMSNotificationsImpl();
			response = impl.createNotificationDealer(dealerDetails, pflag);
			iLogger.info(response);

			if(pflag.equals("1"))
			{
				 new
				 NotificationSubscriberDealersTopic().sendPrincipleDetailsToKafka(dealerDetails);
			}
			 // Invoke Interface Producer to publish data on kafka topic
			 new
			 NotificationSubscriberDealersTopic().sendDealerDetailsToKafka(dealerDetails);

			long endTime = System.currentTimeMillis();
			iLogger.info("DealerDataAndSMSNotifications:userClient:WebService End:status:" + response
					+ ":ExecutionTime:" + (endTime - startTime) + "~" + "" + "~");
		} catch (Exception e) {
			e.printStackTrace();
			fLogger.error("Exception caught : " + e.getMessage());
		}
		return response;
	}

	@POST
	@Path("/addSubscriberDetails")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String addDealerDetails(@QueryParam("dealerCode") String dealerCode, Map<String, Object> subscriberDetails,
			@QueryParam("loginID") String loginID) {
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		String response = "FAILURE";
		long startTime = System.currentTimeMillis();
		try {

			CommonUtil util = new CommonUtil();
			String isValidinput = null;
			String csrfToken = null;
			iLogger.info(loginID);
			if (loginID != null) {
				iLogger.info("Initial login ID: " + loginID);
				String userID = new CommonUtil().getUserId(loginID);

				if (userID == null) {
					throw new CustomFault("Invalid Login ID: " + loginID);
				} else {
					iLogger.info("Updated login ID with user ID: " + loginID);
				}
			} else {
				iLogger.info("Login ID is null.");
				throw new CustomFault("Invalid Login ID: " + loginID);
			}

			DealerDataAndSMSNotificationsImpl impl = new DealerDataAndSMSNotificationsImpl();
			response = impl.createSubscriberDetails(dealerCode, subscriberDetails);
			iLogger.info(response);

			if ("SUCCESS".equals(response)) {
				//send the data to kafka topic
				NotificationSubscriberDealersTopic obj = new NotificationSubscriberDealersTopic();
				Map<String, Object> formattedData = obj.formatNotificationData(dealerCode, subscriberDetails);
				iLogger.info("formattedData" + formattedData);
//			//	 Send formatted data to Kafka
				obj.sendSubscriberDetailsToKafka(dealerCode, formattedData);
				long endTime = System.currentTimeMillis();
				iLogger.info("DealerDataAndSMSNotifications:userClient:WebService End:status:" + response
						+ ":ExecutionTime:" + (endTime - startTime) + "~" + "" + "~");
				return response;
			} else {
				return response;
			}
		} catch (Exception e) {
			e.printStackTrace();
			fLogger.error("Exception caught: " + e.getMessage());
			return "Internal Server Error";
		}
	}

	@GET
	@Path("/subscriberDetails")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Map<String, Map<String, String>>> getNotifications(@QueryParam("dealerCode") String dealerCode) {

		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		Map<String, Map<String, Map<String, String>>> response = new HashMap<>();
		DealerDataAndSMSNotificationsImpl impl = new DealerDataAndSMSNotificationsImpl();
		if (dealerCode == null) {
			iLogger.info("Dealer details not found in the DataBase");
			fLogger.info("Dealer details not found in the DataBase");
		}
		response = impl.getNotifications(dealerCode);
		iLogger.info("response:" + response);
//		NotificationSubscriberDealersTopic obj = new NotificationSubscriberDealersTopic();
//		Map<String, Object> formattedData = obj.formatNotificationData(dealerCode, response);
//		iLogger.info("formattedData" + formattedData);
//	//	 Send formatted data to Kafka
//		obj.sendSubscriberDetailsToKafka(dealerCode, formattedData);
		return response;
	}

	@GET
	@Path("/dealerDetailsbyDealerCode")
	@Produces(MediaType.APPLICATION_JSON)
	public Response fetchDealerDetailsbyDealerCode(@QueryParam("dealerCode") String dealerCode) {

		DealerDataAndSMSNotificationsImpl impl = new DealerDataAndSMSNotificationsImpl();
		List<Map<String, Object>> dealerDetails = impl.fetchDealerDetailsbyDealerCode(dealerCode);
		Logger iLogger = InfoLoggerClass.logger;
		// Check if no dealer details were found
		if (dealerDetails.isEmpty()) {
			return Response.status(Response.Status.NOT_FOUND).entity("Dealer details not found in the DataBase")
					.build();
		}

		iLogger.info("response :" + dealerDetails);
		// Return the dealer details as a JSON response
		return Response.ok(dealerDetails).build();
	}

	@GET
	@Path("/dealerDetails")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDealerDetails(@QueryParam("tenancyId") String tenancyId) {
		Logger iLogger = InfoLoggerClass.logger;
		iLogger.info("tenancyId" + tenancyId);
		DealerDataAndSMSNotificationsImpl impl = new DealerDataAndSMSNotificationsImpl();
		List<Map<String, Object>> dealerDetails = impl.fetchNotificationDealerDetails(tenancyId);

		// Check if no dealer details were found
		if (dealerDetails.isEmpty()) {
			return Response.status(Response.Status.NOT_FOUND).entity("Dealer details not found in the DataBase")
					.build();
		}

		iLogger.info("response :" + dealerDetails);
		// Return the dealer details as a JSON response
		return Response.ok(dealerDetails).build();
	}

	@GET
	@Path("/principalDetails")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPrincipalDetails(@QueryParam("tenancyId") String tenancyId) {
		Logger iLogger = InfoLoggerClass.logger;
		iLogger.info("tenancyId" + tenancyId);
		DealerDataAndSMSNotificationsImpl impl = new DealerDataAndSMSNotificationsImpl();
		List<Map<String, Object>> dealerDetails = impl.fetchPrincipalDetails(tenancyId);
		// Check if no dealer details were found
		if (dealerDetails.isEmpty()) {
			return Response.status(Response.Status.NOT_FOUND).entity("Dealer details not found in the DataBase")
					.build();
		}

		iLogger.info("response :" + dealerDetails);
		// Return the dealer details as a JSON response
		return Response.ok(dealerDetails).build();
	}

	@POST
	@Path("/delete")
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteDealer(@QueryParam("dealerCode") String dealerCode,
			@QueryParam("NotificationDEalerID") String NotificationDEalerID) {
		DealerDataAndSMSNotificationsImpl impl = new DealerDataAndSMSNotificationsImpl();
		boolean isDeleted = impl.deleteDealerByCode(dealerCode, NotificationDEalerID);

		if (isDeleted) {
			return "SUCCESS";
		} else {
			return "FAILURE:Dealer with DealerCode: " + dealerCode + " not found.";
		}
	}

	@POST
	@Path("updateDealerDetails")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String updateUserDetails(HashMap<String, String> dealerDetails,@QueryParam("NotificationDealerID") String notificationDealerID, @QueryParam("loginID") String loginID) {

		String response = "FAILURE";
		Logger infoLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		String userID;
		try {

			infoLogger.info("Received NotificationDealerID: " + notificationDealerID);
			infoLogger.info("Received Dealer details:"+dealerDetails);

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
			DealerDataAndSMSNotificationsImpl impl = new DealerDataAndSMSNotificationsImpl();
			boolean updateSuccess = impl.updatedealerDetails(dealerDetails, notificationDealerID);

			if (updateSuccess) {
				response = "SUCCESS: Dealer Details Updated Successfully";
				//Sending updated dealer details to kafka topic
				 new
				 NotificationSubscriberDealersTopic().sendDealerDetailsToKafka(dealerDetails);
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
	@Path("updatePrincipleDealerDetails")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String updatePrincipleDealerDetails(HashMap<String, String> principleDetails,
			@QueryParam("principleDealerID") String principleDealerID, @QueryParam("loginID") String loginID) {

		String response = "FAILURE";
		Logger infoLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		String userID;
		try {

			infoLogger.info("Received principleDealerID: " + principleDealerID);

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
			DealerDataAndSMSNotificationsImpl impl = new DealerDataAndSMSNotificationsImpl();
			boolean updateSuccess = impl.updatePrincipleDealerDetails(principleDetails, principleDealerID);

			if (updateSuccess) {
				response = "SUCCESS: Principle Details Updated Successfully";
				//Sending updated principle details to kafka topic
				 new
				 NotificationSubscriberDealersTopic().sendPrincipleDetailsToKafka(principleDetails);
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

	@GET
	@Path("getsubscriberDetailsUnderDealerShip")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Map<String, Object>> getsubscriberdetailsUnderDealerShip(@QueryParam("tenancyId") String tenancyId,@QueryParam("roleName") String roleName) {
		Logger infoLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		List<Map<String, Object>> response = null;
		//Integer tenancyId=null;
		DealerDataAndSMSNotificationsImpl impl = new DealerDataAndSMSNotificationsImpl();

//		try {
//			if (loginID != null) {
//				infoLogger.info("Initial login ID: " + loginID);
//
//				// Retrieve the user ID directly from the login ID
//				String userID = new CommonUtil().getUserId(loginID);
//				 List<Integer> tenancyIdlist=new CommonUtil().getTenancyIdListFromLoginId(userID);
//				 if(tenancyIdlist.size()>0)
//				 {
//					 tenancyId=tenancyIdlist.get(0);
//				 }
//				if (userID == null) {
//					throw new CustomFault("Invalid Login ID: " + loginID);
//				} else {
//					infoLogger.info("Updated login ID with user ID: " + userID);
//				}
//			} else {
//				infoLogger.info("Login ID is null.");
//				throw new CustomFault("Login ID is required.");
//			}
//		} catch (CustomFault e) {
//			fLogger.info("CustomFault encountered: " + e.getMessage());
//			e.printStackTrace();
//		}

		response = impl.getSubscriberDetailsUnderDealership(roleName,tenancyId);
		infoLogger.info("response: " + response);

		return response;
	}
}
