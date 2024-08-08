package remote.wise.service.webservice;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import remote.wise.service.implementation.UserNotLoggedDataImpl;

@Path("/UserNotLoggedRESTService")
public class UserNotLoggedRESTService {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getUserNotLoggedData")
	public String getUserNotLoggedData(@QueryParam("accountFilter") String accountFilter,@QueryParam("accountIdList") String accountIdList) {
		
		String result = new UserNotLoggedDataImpl().getUserNotLoggedData(accountFilter,accountIdList);
		if(result!=null)
		return result;
		else
			return "Issue with data,Please check";
		}
	/*public static void main(String[] args) {
		String accountFilter="zonalcode";
		String accountIdList="'0002'";
		String res = new UserNotLoggedDataImpl().getUserNotLoggedData(accountFilter,accountIdList);
		System.out.println(res);
	}*/
	}