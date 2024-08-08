package remote.wise.service.webservice;

import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import redis.clients.jedis.Jedis;
import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
/**
 * @author MANI
 * to delete csrf tokens from redis which are 15 minutes old.
 *
 */
@Path("/deleteCsrfTokens")
public class CsrfTokenDeletionService {

	private final static Long MINUTES = 60 * 60 * 1000L;
	@GET
	@Path("deleteTokens")
	@Produces({ MediaType.TEXT_PLAIN })
	public String deleteCsrfTokens() throws CustomFault {
		Properties prop = new Properties();
		Logger infoLogger = InfoLoggerClass.logger;
		Logger fatalError = FatalLoggerClass.logger;
		infoLogger.info("CsrfTokenDeletionService for every 15 mins.");
		//String tokenId="";
		Set<String> tokens=null,keys=null;
		String status="SUCCESS";Long deletedkeys=0L;
		Jedis jedis =null;
		try{
			Long timetoCompare=System.currentTimeMillis()-MINUTES;
		try {
			
			prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
		} catch (IOException e) {
			fatalError.error("CsrfTokenDeletionService :: Exception while reading conf properties file "+e.getMessage());
		}
		String redisURL = prop.getProperty("geocodingredisurl");
		String redisPORT = prop.getProperty("geocodingredisport");
		jedis = new Jedis(redisURL,Integer.valueOf(redisPORT));
		keys=jedis.keys("CSRFKEY*");
		if(keys!=null)
		{
			
			//redisConnection.hset(latlonbucket, key, locationDetails.toString());
			Iterator iter = keys.iterator();
			while(iter.hasNext())
			{
				String key=iter.next().toString();
				infoLogger.info("CsrfTokenDeletionService :: KEY "+key);
				if(jedis.exists(key)){
					tokens=jedis.smembers(key);
					Iterator tokenItr = tokens.iterator();
					while(tokenItr.hasNext())
					{
						String storedToken=tokenItr.next().toString();
						infoLogger.info("CsrfTokenDeletionService :: storedToken "+storedToken);
						//String tokenOnly=storedToken.split("\\|")[0];
						String tokentimeStamp=storedToken.split("\\|")[1];
						Long tokenGeneratedTime= Long.parseLong(tokentimeStamp);
						if(tokenGeneratedTime<timetoCompare)
						{
							infoLogger.info("CsrfTokenDeletionService :: Deleting 15 mins before token :::"+storedToken+" :: for the key :"+key);
							deletedkeys=jedis.srem(key,storedToken);
						}
						
						
					}
				}
				
			}
		}
		else
		{
			infoLogger.info("CsrfTokenDeletionService for every 15 mins :: no keys exists");

		}
		
		}
		catch(Exception e){
			status="FAILURE";
			fatalError.error(" Exception in "+e.getMessage());
		} 
		finally{
			try{
				  if(jedis != null || jedis.isConnected()){
					  jedis.disconnect();
				  }
				}
				catch(Exception e){
					fatalError.error(" Exception while closing Redis Connection "+e.getMessage());
				} 
		}
		infoLogger.info(" CsrfTokenDeletionService :: status of deleting Csrf tokens "+status);
		return status;
		
	}
	
}
