/**
 * 
 */
package remote.wise.service.webservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import remote.wise.service.implementation.ProfileCodeImpl;

/**
 * @author KI270523
 *
 */

@Path("/ProfileCodeService")
public class ProfileCodeService {
	
	@GET()
	@Produces("text/plain")
	@Path("/getProfileCodeDetails")
	public String getProfileCodeDetails(){
		
		ProfileCodeImpl omplObj=new ProfileCodeImpl();
		
		String result=omplObj.getProfileCodeDetails();
				
	     return result;
	}

}
