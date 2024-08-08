package remote.wise.service.webservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import remote.wise.util.AssetUtil;

@Path("/FetchAssetVinFromAssetMachineRESTService")
public class FetchAssetVinFromAssetMachineRESTService {

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/fetchVinNoUsingMachineNo")
	public String fetchVinNoUsingMachineNo(@QueryParam("machineNumber")String machineNumber){
		if(machineNumber!=null)
		return AssetUtil.getVinNoUsingMachineNo(machineNumber);
		else
		return "Issue with data,Please check";	
	}
}
