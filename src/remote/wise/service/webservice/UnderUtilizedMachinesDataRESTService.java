package remote.wise.service.webservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import remote.wise.service.implementation.UnderUtilizedMachinesDataImpl;

@Path("/UnderUtilizedMachinesDataRESTService")
public class UnderUtilizedMachinesDataRESTService {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getUnderUtilizedMachinesData")
	public String getUnderUtilizedMachinpesData() {
		
		String result = new UnderUtilizedMachinesDataImpl().getUnderUtiliedMachinesData();
		if(result!=null)
		return result;
		else
			return "Issue with data,Please check";
		}
	}