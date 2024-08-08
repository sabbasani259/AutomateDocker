/*
 * CR462 : 20240305 : SAI DIVYA : Service FOR UnallocatedMachineReport
 */


package remote.wise.service.webservice;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import remote.wise.service.implementation.DataForUnallocatedMachineRestServiceImpl;
@Path("/DataForUnallocatedMachineRestService")
public class DataForUnallocatedMachineRestService {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes({MediaType.APPLICATION_JSON})
	@Path("/fetchDataForUnallocatedMachine")
	public List<Map<String,Object>> fetchDataForUnallocatedMachine() {
		
		DataForUnallocatedMachineRestServiceImpl obj=new DataForUnallocatedMachineRestServiceImpl();
		obj.saveToCopy(obj.getOwnerData());
		return new DataForUnallocatedMachineRestServiceImpl().getOwnerData();
	}
}
