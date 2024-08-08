package remote.wise.service.webservice;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonProperty;

import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.ModelWiseDistributionIMPL;
import remote.wise.util.ListToStringConversion;

//CR448 : 20231127 : prasad : Model Wise Distribution on the Overview Map 
@Path("/ModelWiseDistribution")
public class ModelWiseDistribution {
	
	@GET
	@Path("/getZoneNames")
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getZoneNames() throws Exception {
		List<String> response = new ModelWiseDistributionIMPL().getZoneDetails();
		return response;

	}
	
//	@GET
//	@Path("/getDealerNames")
//	@Produces(MediaType.APPLICATION_JSON)
//	public List<String> getDealerNames(@QueryParam("zone") String zone) throws Exception {
//		if(zone != null){
//			zone = zone.trim();
//			zone = "'"+zone+"'";
//		}
//		List<String> response = new ModelWiseDistributionIMPL().getDealerNames(zone);
//		return response;
//	}
	
	
	@POST
	@Path("/getDealerNames")
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getDealerNames(  @JsonProperty LinkedHashMap<String, Object> fields) throws Exception {
		String zone = null ;
		List<String> zoneList  = (List<String>) fields.get("zone");
		if(zoneList!= null &&  !zoneList.isEmpty()){
			ListToStringConversion obj = new ListToStringConversion();
			 zone = obj.getStringList(zoneList).toString();		
		}
		List<String> response = new ModelWiseDistributionIMPL().getDealerNames(zone);
		return response;
	}
	
	
//	@GET
//	@Path("/getCustomerNames")
//	@Produces(MediaType.APPLICATION_JSON)
//	public List<String> getCustomerNames(@QueryParam("zone") String zone , @QueryParam("dealerName") String dealerName) throws Exception {
//		if(zone != null){
//			zone = zone.trim();
//			zone = "'"+zone+"'";
//		}
//		if(dealerName != null){
//			dealerName = dealerName.trim();
//			dealerName = "'"+dealerName+"'";
//		}
//		List<String> response = new ModelWiseDistributionIMPL().getCustomerNames(zone , dealerName);
//		return response;
//	}
	
	@POST
	@Path("/getCustomerNames")
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getCustomerNames(@JsonProperty LinkedHashMap<String, Object> fields) throws Exception {
		String zone = null;
		String dealerName = null;
		List<String> zoneList = (List<String>) fields.get("zone");
		List<String> dealerNameList = (List<String>) fields.get("dealerName");
		if (zoneList != null && !zoneList.isEmpty()) {
			
			ListToStringConversion obj = new ListToStringConversion();
			zone = obj.getStringList(zoneList).toString();
		}
		if (dealerNameList != null && !dealerNameList.isEmpty()){
			
			ListToStringConversion obj = new ListToStringConversion();
			dealerName = obj.getStringList(dealerNameList).toString();
		}
		List<String> response = new ModelWiseDistributionIMPL().getCustomerNames(zone, dealerName);
		return response;
	}
	
	@GET
	@Path("/getProfileNames")
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getProfileNames() throws Exception {
		List<String> response = new ModelWiseDistributionIMPL().getProfileNames();
		return response;
	}
	
//	@POST
//	@Path("/getModelNames")
//	@Produces(MediaType.APPLICATION_JSON)
//	public List<String> getModelNames(@QueryParam("profile") String profile) throws Exception {
//		if (profile != null) {
//			profile = profile.trim();
//			profile = "'" + profile + "'";}
//		List<String> response = new ModelWiseDistributionIMPL().getModelNames(profile);
//		return response;
//	}
	
	@POST
	@Path("/getModelNames")
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getModelNames(@JsonProperty LinkedHashMap<String,Object> fields) throws Exception {
		String profile = null ;
		List<String> profileList = (List<String>) fields.get("profile");
		if(profileList != null && !profileList.isEmpty()) {
		
			ListToStringConversion obj = new ListToStringConversion();
			profile = obj.getStringList(profileList).toString();	
		}
		
		List<String> response = new ModelWiseDistributionIMPL().getModelNames(profile);
		return response;
	}
	

	@POST
	@Path("/getVersions")
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getVersions(@JsonProperty LinkedHashMap<String, Object> fields) throws Exception {
		String fwSlipt = null ;
		List<String> FWTypeList = (List<String>) fields.get("FWType");
		if(FWTypeList != null && !FWTypeList.isEmpty()) {
		
			ListToStringConversion obj = new ListToStringConversion();
			fwSlipt = obj.getStringList(FWTypeList).toString();	
		}
		
		List<String> response = new ModelWiseDistributionIMPL().getVersionsList(fwSlipt);
		return response;
	}
//	@POST
//	@Path("/search")
//	@Produces(MediaType.APPLICATION_JSON)
//	public  List<Map<String ,String>> getSearchDetails(@QueryParam("zone") String zone ,@QueryParam("dealerName") String dealerName, @QueryParam("coustmer")String coustmer , @QueryParam("profile")String profile ,@QueryParam("model") String model) throws Exception {
//		
//		if(zone != null){
//			zone = zone.trim();
//			zone = "'"+zone+"'";
//		}
//		if(dealerName != null){
//			dealerName = dealerName.trim();
//			dealerName = "'"+dealerName+"'";
//		}
//		if (profile != null) {
//			profile = profile.trim();
//			profile = "'" + profile + "'";}
//		
//		if(coustmer != null){
//			coustmer = coustmer.trim();
//			coustmer = "'"+coustmer+"'";
//		}
//		if (model != null) {
//			model = model.trim();
//			model = "'" + model + "'";}
//		 List<Map<String ,String>> response = new ModelWiseDistributionIMPL().getLatAndLonDetails(zone, dealerName, coustmer, profile, model);
//		return response;
//	}
	@POST
	@Path("/search")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Map<String,String>> getSearchDetails(@JsonProperty LinkedHashMap<String, Object> fields) throws Exception {
		Logger iLogger = InfoLoggerClass.logger;
		String zone = null;
		String dealerName = null;
		String profile = null ;
		String model = null;
		String coustmer = null;
		String simType = null;
		String fwSpit = null;
		String version =null;
		List<String> profileList = (List<String>) fields.get("profile");
		List<String> zoneList  = (List<String>) fields.get("zone");
		List<String> dealerNameList = (List<String>) fields.get("dealerName");
		List<String> coustmerList  = (List<String>) fields.get("coustmer");
		List<String> modelList = (List<String>) fields.get("model");
		List<String> simTypeList =  (List<String>) fields.get("simType");
		List<String> fwSplitList =  (List<String>) fields.get("FWType");
		List<String> versionList =  (List<String>) fields.get("version");
		String connectivity =  (String) fields.get("connectivity");
		String pageNumber =  (String) fields.get("pageNumber");
		String pageSize =  (String) fields.get("pageSize");
	    iLogger.info("profileList " +profileList +" zoneList "+zoneList+" dealerNameList " +dealerNameList+ " coustmerList "+coustmerList
			+" modelList "+modelList + " simTypeList "+simTypeList+" fwType "+ fwSplitList+ " versionList "+versionList+ " connectivity "+connectivity + " pageNumber "+pageNumber+ " pageSize "+pageSize);
	    
	 	ListToStringConversion obj = new ListToStringConversion();
	 	
		if(zoneList!= null &&  !zoneList.isEmpty()){
			zone = obj.getStringList(zoneList).toString();		
		}
		
		if(profileList != null && !profileList.isEmpty()) {
			profile = obj.getStringList(profileList).toString();	
		}
	
		if (dealerNameList != null && !dealerNameList.isEmpty()){
			dealerName = obj.getStringList(dealerNameList).toString();
		}
		
		if (coustmerList != null && !coustmerList.isEmpty()){
			coustmer = obj.getStringList(coustmerList).toString();
		}
		
		if (modelList != null && !modelList.isEmpty()){
			model = obj.getStringList(modelList).toString();
		}
		
		if (simTypeList != null && !simTypeList.isEmpty()){
			simType  = obj.getStringList(simTypeList).toString();
		}
		if (fwSplitList != null && !fwSplitList.isEmpty()){
			fwSpit  = obj.getStringList(fwSplitList).toString();
		}
		if (versionList != null && !versionList.isEmpty()){
			version  = obj.getStringList(versionList).toString();
		}
		iLogger.info("zone "+ zone+ " dealerName "+dealerName+" coustmer " +coustmer+" profile " +profile+" model " +model +" simType "+simType+ " fwType "+fwSpit + " version "+version +" connectivity " +connectivity);

		List<Map<String,String>> response = new ModelWiseDistributionIMPL().getLatAndLonDetails(zone, dealerName, coustmer, profile, model,simType , fwSpit ,connectivity , version , pageNumber , pageSize);
		return response;
	}


}
