package remote.wise.service.webservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


import org.apache.logging.log4j.Logger;

import com.wipro.MachineDown.MachineDownServiceImpl;

import remote.wise.log.InfoLogging.InfoLoggerClass;


@Path("/MachineDownService")
public class MachineDownService {
	
	@GET
	@Path("/processMachineDown")
	@Produces(MediaType.TEXT_PLAIN)
	public String processMachineDown() {
		
		Logger iLogger = InfoLoggerClass.logger;
		iLogger.info("WISE:MachineDownInterface -Start");
		
		MachineDownServiceImpl machineDownServiceImpl = new MachineDownServiceImpl();
	
		machineDownServiceImpl.invokeProcessing();
		iLogger.info("WISE:MachineDownInterface -End");
		return "SUCCESS";
	
	}
	
	
	@GET
	@Path("/testMethod")
	@Produces(MediaType.TEXT_PLAIN)
	public String testMethod()
	{
		Logger iLogger = InfoLoggerClass.logger;
		iLogger.info("invoked test method");
		iLogger.info("invoked test method1");
		return "SUCCESS";
	}
	
	
	@GET
	@Path("/testMethod2")
	@Produces("text/plain")
	public String testMethod2()
	{
		Logger iLogger = InfoLoggerClass.logger;
		iLogger.info("invoked test method");
		iLogger.info("invoked test method1");
		return "SUCCESS";
	}
	
	
	public static void main(String[] args) {

		Logger iLogger = InfoLoggerClass.logger;
		iLogger.info("WISE:MachineDownInterface -Start");
		
		MachineDownServiceImpl machineDownServiceImpl = new MachineDownServiceImpl();
	
		machineDownServiceImpl.invokeProcessing();
		iLogger.info("WISE:MachineDownInterface -End");
		
	}

	

}
