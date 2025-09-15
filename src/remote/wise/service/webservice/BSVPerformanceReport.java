package remote.wise.service.webservice;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import remote.wise.AutoReportScheduler.AutoReportAuditTrial;
import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.ACUResponse;
import remote.wise.util.AirtelUtil;


@Path("/BSVPerformanceReport")

public class BSVPerformanceReport {
 
	@GET
	@Path("/bsvperformancereportservice")
	@Produces(MediaType.APPLICATION_JSON)
	public String bsvperformancereportservice(@QueryParam("sessionID") String sessionID) {
		Logger iLogger = InfoLoggerClass.logger;
		Logger fatalLogger = FatalLoggerClass.logger;
		 String Response = "";

		String mongodbURL="http://10.210.196.20:26020/MoolDAReports/BSVPerformanceReportService/generateCOMPCPerfReport?accountFilter=DealerCode&accountIDList=MDKN964&dateFilter=Week&value1=27&value2=2025&profileCodeList=COMPC&modelCodeList=Z117&assetID=PUNVSAAXCR3427101&loginID=dGVzdDY3NTY3MA==&countryCode=91&sessionID="+sessionID;		
 		try
		{
			Response=getCall(mongodbURL);
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
	
		}

		return Response;

	}
	
	public  String getCall(String url) {
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;

		String result = null;

		String output = null;

		try {	

			URL urL = new URL(url);

			HttpURLConnection conn = (HttpURLConnection) urL.openConnection();

			conn.setDoOutput(true);

			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-Type", "text/plain; charset=utf8");
			 conn.setRequestProperty("Accept", "application/json");
			// conn.setRequestMethod("GET");

		

			//conn.setRequestProperty("Accept", "application/json");			
 
			if (conn.getResponseCode() != 200 && conn.getResponseCode() != 204) {

				fLogger.error("Exception occured while calling " + url

						+ " " + conn.getResponseCode() +" contactId: ");

				throw new RuntimeException("Failed : HTTP error code : "

						+ conn.getResponseCode());

			}

			BufferedReader br = new BufferedReader(new InputStreamReader(

					(conn.getInputStream())));
 
			while ((output = br.readLine()) != null) {

				result = output;
 
			}

			iLogger.info("Result for Url "+url+" result  --> " + result +" contactId: ");

			conn.disconnect();
 
		} catch (MalformedURLException e) {

			fLogger.error("Exception occured while calling1 " + url

					+ " " + e.getMessage());
 
		} catch (Exception e) {

			fLogger.error("Exception occured while calling2 " + url

					+ " " + e.getMessage());

		}

		return result;

	}
 

}
