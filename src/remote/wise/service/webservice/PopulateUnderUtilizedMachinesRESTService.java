package remote.wise.service.webservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import remote.wise.service.implementation.PopulateUnderUtilizedMachinesServiceImpl;

@Path("/PopulateUnderUtilizedMachinesRESTService")
public class PopulateUnderUtilizedMachinesRESTService {

	@GET
	@Path("/populateUnderUtilizedMachines")
	public String populateUnderUtilizedMachines() {
		
		PopulateUnderUtilizedMachinesServiceImpl impl = new PopulateUnderUtilizedMachinesServiceImpl();
		String result=impl.getUnderUtilizedMachinesFromMoolLayer();
		if(result!=null && result.equalsIgnoreCase("Success")){
			return "Success";
		}else 
			return "Issue while processing UnderUtilizedmachines";
			
	}
}
