/**
 * 
 */
package remote.wise.service.webservice;

import java.util.ArrayList;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.logging.log4j.Logger;

import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.HAJsendSMSpktImpl;
import remote.wise.service.implementation.SmsListernerImpl;
import remote.wise.util.FeedTransformer;
import remote.wise.util.ResponseObject;
//import remote.wise.util.WiseLogger;

/**
 * @author sunayak
 *
 */
@Path("/HAJsendSMSpkt")
public class HAJsendSMSpktService {

	@GET
	@Produces("application/json")
	public String receivePacket(@QueryParam("phoneNumber") String phone_Number,@QueryParam("otpMsg") String otp_msg,@QueryParam("userEmailId") String user_EmailId,@QueryParam("channel") String channel) 
	{String jsonResponse = null;
	//WiseLogger infoLogger = WiseLogger.getLogger("AssetExtendedService:","info");
	
	 Logger infoLogger = InfoLoggerClass.logger;
		Logger fatalError = FatalLoggerClass.logger;
		Logger businessError = BusinessErrorLoggerClass.logger;
	try {
		ArrayList<ResponseObject> feedData1 = null;
		ArrayList<ResponseObject> feedData2 = null;
		ArrayList<ResponseObject> resultSet = new ArrayList<ResponseObject>();;

		HAJsendSMSpktImpl implObj = new HAJsendSMSpktImpl();

		if(channel!=null && channel.equalsIgnoreCase("sms"))
		{
			feedData1 = implObj.HAJsendSMSpkt(phone_Number, otp_msg, user_EmailId);
		}
		else if(channel!=null && channel.equalsIgnoreCase("email"))
		{
			feedData2 = implObj.HAJsendEmailpkt(otp_msg, user_EmailId);
		}
		else if(channel!=null && channel.equalsIgnoreCase("both"))
		{
			feedData1 = implObj.HAJsendSMSpkt(phone_Number, otp_msg, user_EmailId);
			feedData2 = implObj.HAJsendEmailpkt(otp_msg, user_EmailId);
		}

		if(channel.equalsIgnoreCase("sms")){
			resultSet.addAll(feedData1);
		}
		else if(channel.equalsIgnoreCase("email")){
			resultSet.addAll(feedData2);
		}
		else if(channel.equalsIgnoreCase("both")){
			resultSet.addAll(feedData1);
			resultSet.addAll(feedData2);
		}

		jsonResponse = FeedTransformer.ConvertToJson(resultSet);


	} catch (Exception e) {
		infoLogger.info("Exception Error");
	}
	return jsonResponse;
	}
}
