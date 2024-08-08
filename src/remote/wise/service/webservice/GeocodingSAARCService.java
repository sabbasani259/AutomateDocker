/**
 * 
 */
package remote.wise.service.webservice;

import java.io.IOException;
import java.util.Properties;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.logging.log4j.Logger;

import redis.clients.jedis.Jedis;
import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.LocationDetails;
import remote.wise.util.CommonUtil;
import remote.wise.util.GetSetLocationJedis;

/**
 * @author sh828875
 *
 */
@Path("/GeocodingSAARCService")
public class GeocodingSAARCService {
	
	@GET()
	@Path("getLocationDetails")
	@Produces("text/plain")
	public String geoCodingLibraryForSAARC(@QueryParam("lattitude") String lattitude,@QueryParam("longitude") String longitude) throws CustomFault{

		Properties prop = new Properties();
		Logger infoLogger = InfoLoggerClass.logger;
		Logger fatalError = FatalLoggerClass.logger;
		infoLogger.info("Request for address "+lattitude+" "+longitude);
		CommonUtil utilObj = new CommonUtil();
		
		//DF20180925 ::: KO369761 :: Security checks for all input fields
		String isValidinput = utilObj.inputFieldValidation(lattitude);
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		isValidinput = utilObj.inputFieldValidation(longitude);
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}

		try {
			prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
		} catch (IOException e) {
			fatalError.error("Exception while reading conf properties file "+e.getMessage());
		}

		String redisURL = prop.getProperty("geocodingredisurl");
		String redisPORT = prop.getProperty("geocodingredisport");

		Jedis redisConnection = new Jedis(redisURL,Integer.valueOf(redisPORT));
		LocationDetails locObj = GetSetLocationJedis.RetriveLocationDetailsForSAARC(lattitude,longitude,redisConnection);

		//DF20160721 jayashri added redis closing connection
		try{
			if(redisConnection != null || redisConnection.isConnected()){
				redisConnection.disconnect();
			}
		}
		catch(Exception e){
			fatalError.error("Exception while closing Redis Connection "+e.getMessage());
		} 
		return locObj.getAddress();
	}
}
