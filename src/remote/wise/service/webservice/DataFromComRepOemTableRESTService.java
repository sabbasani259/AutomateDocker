package remote.wise.service.webservice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.annotate.JsonProperty;

import remote.wise.service.implementation.DataFromComRepOemTableRESTServiceImpl;

@Path("/DataFromComRepOemTableRESTService")
public class DataFromComRepOemTableRESTService {

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes({MediaType.APPLICATION_JSON})
	@Path("/fetchDataFromComRepOemTable")
	public String fetchDataFromComRepOemTable(final @JsonProperty("reqObj") LinkedHashMap<String,Object> reqObj) {
		if(reqObj.get("vinQueryList")!=null){
		List<String> vinQueryList=(List<String>) reqObj.get("vinQueryList");
		String result = new DataFromComRepOemTableRESTServiceImpl().getMapData(vinQueryList);
		if(result!=null)
		return result;
		else
			return "Issue with data,Please check";
		}else{
			return "issue with params,Please check parameters";
		}
		
	}
}
