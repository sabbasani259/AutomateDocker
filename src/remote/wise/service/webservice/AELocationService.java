/**
 * 
 */
package remote.wise.service.webservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import remote.wise.businessobject.EventDetailsBO;
import remote.wise.log.InfoLogging.InfoLoggerClass;

/**
 * @author AJAY
 *
 */
@Path("/AELocationDetailsService")
public class AELocationService
{
  @GET
  @Path("updateAELocation")
  @Produces(MediaType.TEXT_PLAIN)
  public String updateAELocation(@QueryParam("startDate") String startDate, @QueryParam("endDate") String endDate)
  {
    String alertJSONArray = null;
    Logger iLogger = InfoLoggerClass.logger;

    iLogger.info("AELocationDetailsService:WebService Input-----> startDate:" + startDate + " endDate:" + endDate);
    long startTime = System.currentTimeMillis();

    alertJSONArray = new EventDetailsBO().updateAddess_assetEvent(startDate, endDate);

    long endTime = System.currentTimeMillis();
    iLogger.info("AELocationDetailsService:WebService Output -----> alertJSONArray:" + alertJSONArray + "; Total Time taken in ms:" + (endTime - startTime));
    iLogger.info("serviceName:AELocationDetailsService~executionTime:"+(endTime - startTime)+"~"+""+"~");
    
    return alertJSONArray;
  }
}