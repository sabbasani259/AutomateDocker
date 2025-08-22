package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.LandmarkDetailsReqContract;
import remote.wise.service.datacontract.LandmarkDetailsRespContract;
import remote.wise.service.implementation.LandmarkDetailsImpl;
import remote.wise.util.CommonUtil;
//import remote.wise.util.WiseLogger;

/**
 * WebService class to get and set Landmark Details
 * 	//LLOPS-164 : Sai Divya : 20250821 : Soap to Rest
 * @author jgupta41
 *
 */
@Path("LandmarkDetailsServiceRestService")
public class LandmarkDetailsServiceRestService {

	/**
	 * This method gets all landmark details that belongs specified Tenancy ID and
	 * Login Id
	 * 
	 * @param reqObj
	 *            Get all landmark details for a given Tenancy ID
	 * @return respObj Returns list of landmark details attached for the Tenancy ID
	 *         defined.
	 * @throws CustomFault
	 */
	@Path("GetLandmarkDetails")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public List<LandmarkDetailsRespContract> getLandmarkDetails(LandmarkDetailsReqContract reqObj) {

		// DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static
		// logger object all throughout the application
		// WiseLogger infoLogger =
		// WiseLogger.getLogger("LandmarkDetailsService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		// Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: " + startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("login_id:" + reqObj.getLogin_id() + "," + "Tenancy_ID:" + reqObj.getTenancy_ID() + ","
				+ "Landmark_id:" + reqObj.getLandmark_id() + "");

		// DF20181017 Avinash Xavier : CSRF Token Validation ---Start---.
		CommonUtil util = new CommonUtil();
		String csrfToken = null;
		boolean isValidCSRF = false;
		String loginId = null;
		List<LandmarkDetailsRespContract> respObj = null;
		try {
			// DF20181011 - KO369761 - Validating the CSRF Token against login id.
//			if (reqObj.getLogin_id() != null) {
//				if (reqObj.getLogin_id().split("\\|").length > 1) {
//					csrfToken = reqObj.getLogin_id().split("\\|")[1];
//					loginId = reqObj.getLogin_id().split("\\|")[0];
//					reqObj.setLogin_id(reqObj.getLogin_id().split("\\|")[0]);
//				}
//			}
//
//			if (csrfToken != null) {
//				isValidCSRF = util.validateANTICSRFTOKEN(loginId, csrfToken);
//				iLogger.info("LandmarkDetails :: getLandmarkDetails ::   csrftoken isValidCSRF :: " + isValidCSRF);
//			}
//			if (!isValidCSRF) {
//				iLogger.info("LandmarkDetails :: getLandmarkDetails ::  Invalid request.");
//				throw new CustomFault("Invalid request.");
//			}

			// DF20181008 - XSS validation of input for Security Fixes.
			String isValidinput = null;

			isValidinput = util.inputFieldValidation(String.valueOf(reqObj.getTenancy_ID()));
			if (!isValidinput.equals("SUCCESS")) {
				throw new CustomFault(isValidinput);
			}
			isValidinput = util.inputFieldValidation(String.valueOf(reqObj.getLandmark_id()));
			if (!isValidinput.equals("SUCCESS")) {
				throw new CustomFault(isValidinput);
			}

			// DF20170919 @Roopa getting decoded UserId
			String UserID = new CommonUtil().getUserId(reqObj.getLogin_id());
			reqObj.setLogin_id(UserID);
			iLogger.info("Decoded userId::" + reqObj.getLogin_id());

			respObj = new LandmarkDetailsImpl().getLandmarkDetails(reqObj);
			iLogger.info("----- Webservice Output-----");
			for (int i = 0; i < respObj.size(); i++) {
				iLogger.info("Landmark_id:" + respObj.get(i).getLandmark_id() + "," + "Landmark_Category_ID:"
						+ respObj.get(i).getLandmark_Category_ID() + "," + "Landmark_Name:"
						+ respObj.get(i).getLandmark_Name() + "," + "Latitude:" + respObj.get(i).getLatitude() + ","
						+ "Longitude:" + respObj.get(i).getLongitude() + "," + "Radius:" + respObj.get(i).getRadius()
						+ "," + "Address:" + respObj.get(i).getAddress() + "," + "IsArrival:"
						+ respObj.get(i).getIsArrival() + "," + "IsDeparture:" + respObj.get(i).getIsDeparture() + ",");

				// DF20181008-KO369761-XSS validation of output response contract
				isValidinput = util.inputFieldValidation(respObj.get(i).getAddress());
				if (!isValidinput.equals("SUCCESS")) {
					throw new CustomFault(isValidinput);
				}
				isValidinput = util.inputFieldValidation(respObj.get(i).getLandmark_Category_Color_Code());
				if (!isValidinput.equals("SUCCESS")) {
					throw new CustomFault(isValidinput);
				}
				isValidinput = util.inputFieldValidation(respObj.get(i).getLandmark_Category_Name());
				if (!isValidinput.equals("SUCCESS")) {
					throw new CustomFault(isValidinput);
				}
				isValidinput = util.inputFieldValidation(respObj.get(i).getLandmark_Name());
				if (!isValidinput.equals("SUCCESS")) {
					throw new CustomFault(isValidinput);
				}
				isValidinput = util.inputFieldValidation(respObj.get(i).getLatitude());
				if (!isValidinput.equals("SUCCESS")) {
					throw new CustomFault(isValidinput);
				}
				isValidinput = util.inputFieldValidation(respObj.get(i).getLongitude());
				if (!isValidinput.equals("SUCCESS")) {
					throw new CustomFault(isValidinput);
				}
				isValidinput = util.inputFieldValidation(String.valueOf(respObj.get(i).getIsArrival()));
				if (!isValidinput.equals("SUCCESS")) {
					throw new CustomFault(isValidinput);
				}
				isValidinput = util.inputFieldValidation(String.valueOf(respObj.get(i).getIsDeparture()));
				if (!isValidinput.equals("SUCCESS")) {
					throw new CustomFault(isValidinput);
				}
				isValidinput = util.inputFieldValidation(String.valueOf(respObj.get(i).getLandmark_Category_ID()));
				if (!isValidinput.equals("SUCCESS")) {
					throw new CustomFault(isValidinput);
				}
				isValidinput = util.inputFieldValidation(String.valueOf(respObj.get(i).getRadius()));
				if (!isValidinput.equals("SUCCESS")) {
					throw new CustomFault(isValidinput);
				}
			}
			Calendar cal1 = Calendar.getInstance();
			SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
			String endDate = sdf1.format(cal1.getTime());
			iLogger.info("Current Enddate: " + endDate);
			long endTime = System.currentTimeMillis();
			iLogger.info(
					"serviceName:LandmarkDetailsService~executionTime:" + (endTime - startTime) + "~" + loginId + "~");
		} catch (Exception e) {
			e.printStackTrace();
			iLogger.info("Error" + e);
		}
		return respObj;
	}

	/**
	 * This method sets all landmark details that belongs specified Landmark
	 * Category ID and Landmark Name
	 * 
	 * @param setLandmarkDetails:Get
	 *            Landmark and set Landmark Details to the corresponding to it
	 * @return response_msg :Return the status String as either Success/Failure.
	 * @throws CustomFault
	 */
	@WebMethod(operationName = "SetLandmarkDetails", action = "SetLandmarkDetails")
	public LandmarkDetailsReqContract setLandmarkDetails(
			@WebParam(name = "RespObj") LandmarkDetailsRespContract RespObj) throws CustomFault {

		// DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static
		// logger object all throughout the application
		// WiseLoggeiLoggerer = WiseLogger.getLogger("LandmarkDetailsService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		// Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: " + startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("Landmark_id:" + RespObj.getLandmark_id() + "," + "Landmark_Category_ID:"
				+ RespObj.getLandmark_Category_ID() + "," + "Landmark_Name:" + RespObj.getLandmark_Name() + ","
				+ "Latitude:" + RespObj.getLatitude() + "," + "Longitude:" + RespObj.getLongitude() + "," + "Radius:"
				+ RespObj.getRadius() + "," + "Address:" + RespObj.getAddress() + "," + "IsArrival:"
				+ RespObj.getIsArrival() + "," + "IsDeparture:" + RespObj.getIsDeparture() + ",");

		// DF20181008-KO369761-XSS validation of input request contract
		String isValidinput = null;
		CommonUtil util = new CommonUtil();

		// DF20181017 Avinash Xavier : CSRF Token Validation ---Start---.
		String csrfToken = null;
		boolean isValidCSRF = false;
		String loginId = null;

		if (RespObj.getLogin_id() != null) {
			if (RespObj.getLogin_id().split("\\|").length > 1) {
				csrfToken = RespObj.getLogin_id().split("\\|")[1];
				loginId = RespObj.getLogin_id().split("\\|")[0];
				RespObj.setLogin_id(RespObj.getLogin_id().split("\\|")[0]);
			}
		}

		if (csrfToken != null) {
			isValidCSRF = util.validateANTICSRFTOKEN(loginId, csrfToken);
			iLogger.info("setLandmarkDetails ::   csrftoken isValidCSRF :: " + isValidCSRF);
			if (!isValidCSRF) {
				iLogger.info("setLandmarkDetails ::  Invalid request.");
				throw new CustomFault("Invalid request.");
			} else {

				util.deleteANTICSRFTOKENS(loginId, csrfToken, "one");
			}
		}

		// DF20181017 Avinash Xavier CsrfToken Validation ---End----.

		isValidinput = util.inputFieldValidation(RespObj.getAddress());
		if (!isValidinput.equals("SUCCESS")) {
			throw new CustomFault(isValidinput);
		}
		isValidinput = util.inputFieldValidation(RespObj.getLandmark_Category_Color_Code());
		if (!isValidinput.equals("SUCCESS")) {
			throw new CustomFault(isValidinput);
		}
		isValidinput = util.inputFieldValidation(RespObj.getLandmark_Category_Name());
		if (!isValidinput.equals("SUCCESS")) {
			throw new CustomFault(isValidinput);
		}
		isValidinput = util.inputFieldValidation(RespObj.getLandmark_Name());
		if (!isValidinput.equals("SUCCESS")) {
			throw new CustomFault(isValidinput);
		}
		isValidinput = util.inputFieldValidation(RespObj.getLatitude());
		if (!isValidinput.equals("SUCCESS")) {
			throw new CustomFault(isValidinput);
		}
		isValidinput = util.inputFieldValidation(RespObj.getLongitude());
		if (!isValidinput.equals("SUCCESS")) {
			throw new CustomFault(isValidinput);
		}
		isValidinput = util.inputFieldValidation(String.valueOf(RespObj.getIsArrival()));
		if (!isValidinput.equals("SUCCESS")) {
			throw new CustomFault(isValidinput);
		}
		isValidinput = util.inputFieldValidation(String.valueOf(RespObj.getIsDeparture()));
		if (!isValidinput.equals("SUCCESS")) {
			throw new CustomFault(isValidinput);
		}
		isValidinput = util.inputFieldValidation(String.valueOf(RespObj.getLandmark_Category_ID()));
		if (!isValidinput.equals("SUCCESS")) {
			throw new CustomFault(isValidinput);
		}
		isValidinput = util.inputFieldValidation(String.valueOf(RespObj.getRadius()));
		if (!isValidinput.equals("SUCCESS")) {
			throw new CustomFault(isValidinput);
		}

		// DF20170919 @Roopa getting decoded UserId
		String UserID = new CommonUtil().getUserId(RespObj.getLogin_id());
		RespObj.setLogin_id(UserID);
		iLogger.info("Decoded userId::" + RespObj.getLogin_id());

		LandmarkDetailsReqContract response_msg = new LandmarkDetailsImpl().setLandmarkDetails(RespObj);
		iLogger.info("----- Webservice Output-----");
		iLogger.info("Status:" + response_msg + "");

		isValidinput = util.inputFieldValidation(String.valueOf(response_msg.getLandmark_id()));
		if (!isValidinput.equals("SUCCESS")) {
			throw new CustomFault(isValidinput);
		}
		isValidinput = util.inputFieldValidation(String.valueOf(response_msg.getTenancy_ID()));
		if (!isValidinput.equals("SUCCESS")) {
			throw new CustomFault(isValidinput);
		}

		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: " + endDate);
		long endTime = System.currentTimeMillis();
		iLogger.info("serviceName:LandmarkDetailsService~executionTime:" + (endTime - startTime) + "~" + loginId + "~");
		return response_msg;
	}
}
