/*
 * JCB6622 : 20240805 : Dhiraj Kumar : Email Flooding security issue fix
 */
package remote.wise.service.webservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.apache.logging.log4j.Logger;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.ForgotPasswordImpl;

@Path("/ForgotPasswordExtendedService")
public class ForgotPasswordExtendedService {
    
    
    @GET
    @Path("updateForgotPassCount")
    @Produces(MediaType.APPLICATION_JSON)
    public String updateForgotPassCount(){
	String status = "FAILURE";
	Logger iLogger = InfoLoggerClass.logger;
	long startTime = System.currentTimeMillis();
	status = new ForgotPasswordImpl().updateForgotPassCount();
	long endTime=System.currentTimeMillis();
	iLogger.info("executionTime:"+(endTime-startTime)+"~"+""+"~");
	return status;
    }
}
