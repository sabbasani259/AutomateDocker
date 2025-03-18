package remote.wise.service.webservice;
/**
 * CR517-20408644:Sai Divya:API for RemoteDiagnostics
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import remote.wise.dao.RemoteDiagnosticsDAO;
import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.CommonUtil;

@Path("/RemoteDiagnosticsRestService")
public class RemoteDiagnosticsRestService {

	@GET
	@Path("/getURL")
	@Produces(MediaType.APPLICATION_JSON)
	public Response generateUrl(@QueryParam("userName") String username) {
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		RemoteDiagnosticsDAO obj = new RemoteDiagnosticsDAO();
		try {
			if (username == null || username.isEmpty()) {
				JSONObject jsonResponse = new JSONObject();
				jsonResponse.put("status", 400);
				jsonResponse.put("message", "Username field is required");
				return Response.status(400).entity(jsonResponse.toString()).build();
			}

			Map<String, String> userCredentials = obj.passWordFromDB(username);
			String secretKey = "rdllapppsecret";
			String userType;
			
			if (userCredentials.get("roleName").equals("Dealer")
					|| userCredentials.get("roleName").equals("Dealer Admin")) {
				userType = "Dealer";
			} else if (userCredentials.get("roleName").equals("Super Admin")
					|| userCredentials.get("roleName").equals("JCB Account")) {
				userType = "Admin";
			} else {
				userType = "";
				iLogger.info("userType is not allowed" + userCredentials.get("roleName"));
				;
			}
			iLogger.info("userType :" + userType);
			String encryptedUserName = RemoteDiagnosticsDAO.encrypt(username, secretKey);
			String encryptedRoleName = userType.isEmpty() ? "" : RemoteDiagnosticsDAO.encrypt(userType, secretKey);
			String encryptedPassWord = RemoteDiagnosticsDAO.encrypt(userCredentials.get("decryptedPassword"), secretKey);

			iLogger.info("encryptedUserName :" + encryptedUserName);
			iLogger.info("encryptedRoleName :" + encryptedRoleName);
			iLogger.info("encryptedPassWord :" + encryptedPassWord);
			// Create the JSON string input for the POST request
			JSONObject jsonInput = new JSONObject();
			jsonInput.put("key1", encryptedUserName);
			jsonInput.put("key2", encryptedPassWord);
			jsonInput.put("key3", encryptedRoleName);		

			String api_url = null;
			Properties prop = new Properties();

			try {
				prop.load(getClass().getClassLoader()
						.getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			api_url = prop.getProperty("RemoteDiagnosticsUrl");
			
			String result = obj.postCall(api_url, jsonInput.toString());
			
			iLogger.info("result:"+result);
			// Check if the password is empty
			if (encryptedPassWord == null || encryptedPassWord.isEmpty()) {
				JSONObject errorResponse = new JSONObject();
				errorResponse.put("status", 400);
				errorResponse.put("message", "Password field is required");
				return Response.status(400).entity(errorResponse.toString()).build();
			}
			// Check if the user type is empty
			else if (encryptedRoleName == null || encryptedRoleName.isEmpty()) {
				JSONObject errorResponse = new JSONObject();
				errorResponse.put("status", 400);
				errorResponse.put("message", "User Type field is required");
				return Response.status(400).entity(errorResponse.toString()).build();
			} 
			else if (result != null && result.contains("200")) {
				JSONObject externalResponse = new JSONObject(result);
				// Extracting the unique ID from the response
				String url=externalResponse.getString("url");
				
				JSONObject jsonResponse = new JSONObject();
				jsonResponse.put("url",url);
				jsonResponse.put("message", "Success");
				jsonResponse.put("status", 200);
				// Returning the response with status 200 and the URL with the unique ID
				return Response.ok(jsonResponse.toString()).build();
			} else {
				JSONObject errorResponse = new JSONObject();
				errorResponse.put("url", "null");
				errorResponse.put("status", 400);
				errorResponse.put("message", "Remote Diagnostics Application is not available now. Please try again after some time");
				iLogger.info("message ", result);
				return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse.toString()).build();
			}
		}

		catch (JSONException e) {
			fLogger.fatal("Error processing the response: ", e);
			return Response.status(500).entity("{\"message\": \"Error processing response\", \"status\": 500}").build();
		}

	}

}
