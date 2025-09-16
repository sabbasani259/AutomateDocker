package remote.wise.service.webservice;


import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.JsonMappingException;
import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.DOutLetMappingImpl;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *  WebService class to fetch zone ,dealer and outlet_type  from Dealer_outlet_mapping.
 * @author Shajesh Gangadharan
 * @author Bidisha Shaw
 */


@Path("/DealerOutletMapping")
public class DOutLetMapping {
	
	/*WS to return unique org_units*/
	@GET
	@Path("getUniqueOrgUnit")
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getUniqueOrgUnit( ) {
		Logger iLogger = InfoLoggerClass.logger;		 
		List<String> result = new DOutLetMappingImpl().getUniqueOrgUnit();
		iLogger.info("DealerOutletMapping : getUniqueOrgUnitDetails : Response ::"+ result);
		System.out.println("{outlet_type :"+result+"}");
		return result;
		}
	
	
	/*WS to return unique zones*/
	@GET
	@Path("getUniqueZonesDetails")
	@Produces(MediaType.APPLICATION_JSON)
	public List<HashMap<String, String>> getUniqueZones(@QueryParam("orgUnit") String orgUnit) {
		Logger iLogger = InfoLoggerClass.logger;		
		List<HashMap<String, String>> result = new DOutLetMappingImpl().getUniqueZoneDetails(orgUnit);
		iLogger.info("DealerOutletMapping : getUniqueZones : Response ::" + "{Zones :"+result+"}");
		System.out.println("DealerOutletMapping : getUniqueZones : Response ::");
		System.out.println("{Zones :"+result+"}");
		return result;
		}
			
	/*WS to return unique dealers*/
	@GET
	@Path("getUniqueDealersDetails")
	@Produces(MediaType.APPLICATION_JSON)
	public List<HashMap<String, String>> getUniqueDealers(@QueryParam("zone") String zone,@QueryParam("orgUnit") String orgUnit) {
			Logger iLogger = InfoLoggerClass.logger;			
			List<HashMap<String, String>> result = new DOutLetMappingImpl().getUniqueDealersDetails(zone,orgUnit);
			iLogger.info("DealerOutletMapping : getUniqueDealers : Response ::"+ "{dealer :"+result+"}");
			System.out.println("DealerOutletMapping : getUniqueDealers : Response ::");
			System.out.println("{dealer :"+result+"}");
			return result;
			}
	
	/*WS to return unique category/outlet_Types*/
	@GET
	@Path("getUniqueCategoryDetails")
	@Produces(MediaType.APPLICATION_JSON)
	public List<HashMap<String, String>> getUniqueCategories( @QueryParam("dealer") String dealer,@QueryParam("orgUnit") String orgUnit) {
		Logger iLogger = InfoLoggerClass.logger;		 
		List<HashMap<String, String>> result = new DOutLetMappingImpl().getUniqueOutletTypeDetails(dealer,orgUnit);
		iLogger.info("DealerOutletMapping : getUniqueCategories : Response ::"+ result);
		System.out.println("{outlet_type :"+result+"}");
		return result;
		}
	
	/*WS to return ZonalDistributionDetails*/
	@SuppressWarnings("unchecked")
	@POST
	@Path("getZonalDistributionDetails")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public List<HashMap<String, String>> getZonalDistributionDetails (final @JsonProperty("reqObj") LinkedHashMap<String,Object> reqObj)throws CustomFault, JsonGenerationException, JsonMappingException, IOException {
		Logger iLogger = InfoLoggerClass.logger;
		String orgUnits=null;
		List<String> zones=null;
		List<String> dealers=null;
		List<String> outlet_types=null;
		System.out.println("---- Webservice Input ------");
		for(int i=0;i<reqObj.size();i++){
		if(reqObj.get("org_unit")!=null)
				orgUnits=(String) reqObj.get("org_unit");	
		if(reqObj.get("zone")!=null)
			zones=(List<String>) reqObj.get("zone");		
		if(reqObj.get("dealer")!=null)
			dealers=(List<String>) reqObj.get("dealer");		
		if(reqObj.get("outlet_type")!=null)
			outlet_types=(List<String>) reqObj.get("outlet_type");
		}
		List<HashMap<String, String>> result = new DOutLetMappingImpl().getZonalDistributionDetails(orgUnits,zones,dealers,outlet_types);
		System.out.println("---- Webservice output ------");
		iLogger.info("DealerOutletMapping : getZonalDistributionDetails : Response ::"+ result);
		System.out.println("getZonalDistributionDetails"+result);
		return result;
		}
	
	/*WS to return DealersDistributionDetails*/
	@SuppressWarnings("unchecked")
	@POST
	@Path("getDealersDistributionDetails")
	@Produces(MediaType.APPLICATION_JSON)	
	@Consumes(MediaType.APPLICATION_JSON)
	public List<HashMap<String, Map<String, String>>> getDealersDistributionDetails (final @JsonProperty("reqObj") LinkedHashMap<String,Object> reqObj)throws CustomFault, JsonGenerationException, JsonMappingException, IOException {
		Logger iLogger = InfoLoggerClass.logger;
		String orgUnits=null;
		List<String> zones=null;
		List<String> dealers=null;
		List<String> outlet_types=null;
		System.out.println("---- Webservice Input ------");
		for(int i=0;i<reqObj.size();i++){
		if(reqObj.get("org_unit")!=null)
				orgUnits=(String) reqObj.get("org_unit");	
		if(reqObj.get("zone")!=null)
			zones=(List<String>) reqObj.get("zone");		
		if(reqObj.get("dealer")!=null)
			dealers=(List<String>) reqObj.get("dealer");		
		if(reqObj.get("outlet_type")!=null)
			outlet_types=(List<String>) reqObj.get("outlet_type");
		}
		List<HashMap<String, Map<String, String>>> result = new DOutLetMappingImpl().getDealersDistributionDetails(orgUnits,zones,dealers,outlet_types);
		System.out.println("---- Webservice output ------");
		iLogger.info("DealerOutletMapping : getDealersDistributionDetails : Response ::"+ result);
		System.out.println("getDealersDistributionDetails"+result);
		return (List<HashMap<String, Map<String, String>>>) result;
		}
	
	/*WS to return DealersAndTouchPointCountDetails*/
	@SuppressWarnings("unchecked")
	@POST
	@Path("getDealersAndTouchPointCountDetails")
	@Produces(MediaType.APPLICATION_JSON)	
	@Consumes(MediaType.APPLICATION_JSON)
	public List<HashMap<String, String>> getDealersAndTouchPointCountDetails (final @JsonProperty("reqObj") LinkedHashMap<String,Object> reqObj)throws CustomFault, JsonGenerationException, JsonMappingException, IOException {
		Logger iLogger = InfoLoggerClass.logger;
		String orgUnits=null;
		List<String> zones=null;
		List<String> dealers=null;
		List<String> outlet_types=null;
		System.out.println("---- Webservice Input ------");
		for(int i=0;i<reqObj.size();i++){
		if(reqObj.get("org_unit")!=null)
			orgUnits=(String) reqObj.get("org_unit");	
		if(reqObj.get("zone")!=null)
			zones=(List<String>) reqObj.get("zone");		
		if(reqObj.get("dealer")!=null)
			dealers=(List<String>) reqObj.get("dealer");		
		if(reqObj.get("outlet_type")!=null)
			outlet_types=(List<String>) reqObj.get("outlet_type");
		}	 
		List<HashMap<String, String>> result = new DOutLetMappingImpl().getDealersAndTouchPointCountDetails(orgUnits,zones,dealers,outlet_types);
		System.out.println("---- Webservice output ------");
		iLogger.info("DealerOutletMapping : getDealersAndTouchPointCountDetails : Response ::"+ result);
		System.out.println("getDealersAndTouchPointCountDetails"+result);
		return result;
		}
	
	/*WS to return DealersDeatilsForMap*/
	@SuppressWarnings("unchecked")
	@POST
	@Path("getDealersDeatilsForMap")
	@Produces(MediaType.APPLICATION_JSON)	
	@Consumes(MediaType.APPLICATION_JSON)
	public List<HashMap<String, String>> getDealersDeatilsForMap (final @JsonProperty("reqObj") LinkedHashMap<String,Object> reqObj)throws CustomFault, JsonGenerationException, JsonMappingException, IOException {
		Logger iLogger = InfoLoggerClass.logger;
		String orgUnits=null;
		List<String> zones=null;
		List<String> dealers=null;
		List<String> outlet_types=null;
		System.out.println("---- Webservice Input ------");
		for(int i=0;i<reqObj.size();i++){
		if(reqObj.get("org_unit")!=null)
				orgUnits=(String) reqObj.get("org_unit");	
		if(reqObj.get("zone")!=null)
			zones=(List<String>) reqObj.get("zone");		
		if(reqObj.get("dealer")!=null)
			dealers=(List<String>) reqObj.get("dealer");		
		if(reqObj.get("outlet_type")!=null)
			outlet_types=(List<String>) reqObj.get("outlet_type");
		}	 
		List<HashMap<String, String>> result = new DOutLetMappingImpl().getDealersDeatilsForMap(orgUnits,zones,dealers,outlet_types);
		System.out.println("---- Webservice output ------");
		iLogger.info("DealerOutletMapping : getDealersDeatilsForMap : Response ::"+ result);
		System.out.println("getDealersDeatilsForMap"+result);
		return result;
		}
	
	/*WS to return default selection Details*/	
	@SuppressWarnings("unchecked")
	@POST
	@Path("getDefaultSelectionDataDetails")
	@Produces(MediaType.APPLICATION_JSON)	
	@Consumes(MediaType.APPLICATION_JSON)
	public List<HashMap<String, Map<String, String>>> getDefaultSelectionDataDetails (final @JsonProperty("reqObj") LinkedHashMap<String,Object> reqObj)throws CustomFault, JsonGenerationException, JsonMappingException, IOException {
		Logger iLogger = InfoLoggerClass.logger;
		String orgUnits=null;
		List<String> outlet_types=null;
		if(reqObj.get("outlet_type")!=null){
			outlet_types=(List<String>) reqObj.get("outlet_type");
		}	
		if(reqObj.get("org_unit")!=null)
			orgUnits=(String) reqObj.get("org_unit");	
		List<HashMap<String, Map<String, String>>> result = new DOutLetMappingImpl().getDefaultSelectionDataDetails(orgUnits,outlet_types);
		System.out.println("---- Webservice output ------");
		iLogger.info("DealerOutletMapping : getDefaultSelectionDataDetails : Response ::"+ result);
		System.out.println("getDefaultSelectionDataDetails"+result);
		return (List<HashMap<String, Map<String, String>>>) result;
		}
	/*WS to return zonal wise profile Details*/	
	//Shajesh : 2021-01-18 : Additional Changes
	@SuppressWarnings("unchecked")
	@POST
	@Path("getZoneWiseProfileDetails")
	@Produces(MediaType.APPLICATION_JSON)	
	@Consumes(MediaType.APPLICATION_JSON)
	public List<HashMap<String, Map<String, String>>> getZoneWiseProfileDetails (final @JsonProperty("reqObj") LinkedHashMap<String,Object> reqObj)throws CustomFault, JsonGenerationException, JsonMappingException, IOException {
		List<HashMap<String, Map<String, String>>> result = null;
		Logger iLogger = InfoLoggerClass.logger;
		List<String> zone_codes=null;
		if(reqObj.get("zone_code")!=null){
			zone_codes=(List<String>) reqObj.get("zone_code");
		}
		System.out.println("---- Webservice input ------"+zone_codes);
		result =  new DOutLetMappingImpl().getZoneWiseProfileDetails(zone_codes);
		System.out.println("---- Webservice output ------");
		iLogger.info("DealerOutletMapping : getZoneWiseProfileDetails : Response ::"+ result);
		System.out.println("getZoneWiseProfileDetails"+result);
		
		return  (List<HashMap<String, Map<String, String>>>)result;
	}
	
	/*WS to return dealer wise profile Details*/	
	//Shajesh : 2021-01-18 : Additional Changes
	@POST
	@Path("getDealerWiseProfileDetails")
	@Produces(MediaType.APPLICATION_JSON)	
	@Consumes(MediaType.APPLICATION_JSON)
	public List<HashMap<String, Map<String, String>>> getDealerWiseProfileDetails (final @JsonProperty("reqObj") LinkedHashMap<String,Object> reqObj)throws CustomFault, JsonGenerationException, JsonMappingException, IOException {
		List<HashMap<String, Map<String, String>>> result = null;
		Logger iLogger = InfoLoggerClass.logger;
		/*List<String> dealer_codes=null;
		if(reqObj.get("dealer_code")!=null){
			dealer_codes=(List<String>) reqObj.get("dealer_code");
		}*/
		String dealer_codes = null;
		if(reqObj.get("dealer_code")!=null){
		dealer_codes= (String) reqObj.get("dealer_code");
		}
		System.out.println("---- Webservice input ------"+dealer_codes);
		result =  new DOutLetMappingImpl().getDealerWiseProfileDetails(dealer_codes);
		System.out.println("---- Webservice output ------");
		iLogger.info("DealerOutletMapping : getDealerWiseProfileDetails : Response ::"+ result);
		System.out.println("getDealerWiseProfileDetails"+result);
		
		return  (List<HashMap<String, Map<String, String>>>)result;
	}
	
	/*WS to return machine tagged  based on the input selected zone Details*/	
	//Shajesh : 2021-01-22 : Additional Changes
	@SuppressWarnings("unchecked")
	@POST
	@Path("getMachinesByZoneWiseForMap")
	@Produces(MediaType.APPLICATION_JSON)	
	@Consumes(MediaType.APPLICATION_JSON)
	public List<HashMap<String,String>> getMachinesByZoneWiseForMap (final @JsonProperty("reqObj") LinkedHashMap<String,Object> reqObj)throws CustomFault, JsonGenerationException, JsonMappingException, IOException {
		List<HashMap<String,String>> result = null;
		Logger iLogger = InfoLoggerClass.logger;
		List<String> zonalCode=null;
		if(reqObj.get("zone_code")!=null){
			zonalCode=(List<String>) reqObj.get("zone_code");
		}
		System.out.println("---- Webservice input ------"+zonalCode);
		result =  new DOutLetMappingImpl().getMachinesByZoneWiseForMap(zonalCode);
		System.out.println("---- Webservice output ------");
		iLogger.info("DealerOutletMapping : getMachinesByZoneWiseForMap : Response ::"+ result);
		System.out.println("getMachinesByZoneWiseForMap"+result);
		
		return result;
	}
	
	/*WS to return machine count details for a particular dealer in a zone */	
	//Shajesh : 2021-01-25 : Additional Changes
	@SuppressWarnings("unchecked")
	@POST
	@Path("getMachineCountOfDealersInZoneForMap")
	@Produces(MediaType.APPLICATION_JSON)	
	@Consumes(MediaType.APPLICATION_JSON)
	public List<HashMap<String, String>> getMachineCountOfDealersInZoneForMap (final @JsonProperty("reqObj") LinkedHashMap<String,Object> reqObj)throws CustomFault, JsonGenerationException, JsonMappingException, IOException {
		List<HashMap<String, String>> result = null;
		Logger iLogger = InfoLoggerClass.logger;
		List<String> zone_codes=null;
		List<String> dealer_codes=null;
		if(reqObj.get("zone_code")!=null){
			zone_codes=(List<String>) reqObj.get("zone_code");
		}		
		if(reqObj.get("dealer_code")!=null){
			dealer_codes=(List<String>) reqObj.get("dealer_code");
		}
		System.out.println("---- Webservice input ------"+"Zones"+ zone_codes +"\t"+ "Dealers" + dealer_codes);
		result =  new DOutLetMappingImpl().getMachineCountOfDealersInZoneForMap(zone_codes,dealer_codes);
		System.out.println("---- Webservice output ------");
		iLogger.info("DealerOutletMapping : getMachineCountOfDealersInZoneForMap : Response ::"+ result);
		System.out.println("getMachineCountOfDealersInZoneForMap"+result);
		
		return (List<HashMap<String, String>>)result;
	}
	/*WS to return profile wise filtering */	
	//Shajesh : 2021-04-13 : Additional Changes
	/*@SuppressWarnings("unchecked")
	@POST
	@Path("getProfileWiseFilter")
	@Produces(MediaType.APPLICATION_JSON)		
	@Consumes(MediaType.APPLICATION_JSON)
	public List<HashMap<String,String>> getProfileWiseFilter (final @JsonProperty("reqObj") LinkedHashMap<String,Object> reqObj)throws CustomFault, JsonGenerationException, JsonMappingException, IOException {
		
		List<HashMap<String,String>> result = null;
		Logger iLogger = InfoLoggerClass.logger;
		List<String> profiles=null;
		if(reqObj.get("profile")!=null){
			profiles=(List<String>) reqObj.get("profile");
		}
		System.out.println("---- Webservice input ------"+ "profiles :::"+ profiles);
		iLogger.info("---- Webservice input ------"+"profiles"+ profiles);
		result =  new DOutLetMappingImpl().getProfileWiseFilter(profiles);
		System.out.println("---- Webservice output ------");
		iLogger.info("DealerOutletMapping ::: getPrifileWiseFilter ::: Response :: "+ result);
		return result;
		
	}
	WS to return machine model count details associated with a profile 	
	//Shajesh : 2021-04-13 : Additional Changes
	@SuppressWarnings("unchecked")
	@POST
	@Path("getModelCountAssociatedWithProfile")
	@Produces(MediaType.APPLICATION_JSON)		
	@Consumes(MediaType.APPLICATION_JSON)
	public List<HashMap<String,Map<String,String>>> getModelCountAssociatedWithProfile (final @JsonProperty("reqObj") LinkedHashMap<String,Object> reqObj)throws CustomFault, JsonGenerationException, JsonMappingException, IOException {
		
		List<HashMap<String,Map<String,String>>> response = null;
		Logger iLogger = InfoLoggerClass.logger;
		List<String> profiles=null;
		if(reqObj.get("profile")!= null){
			profiles=(List<String>) reqObj.get("profile");
		}
		System.out.println("---- Webservice input ------"+ " profiles ::: "+ profiles);
		iLogger.info("---- Webservice input ------"+"profiles"+ profiles);
		response =  new DOutLetMappingImpl().getModelCountAssociatedWithProfile(profiles);
		System.out.println("---- Webservice output ------");
		iLogger.info("DealerOutletMapping ::: getModelCountAssociatedWithProfile ::: Response ::"+ response);
		return response;
		
	}*/
	/*WS to return machine model count details associated with a profile and zone 	*/
	//Shajesh : 2021-05-04 : Additional Changes
	@SuppressWarnings("unchecked")
	@POST
	@Path("getZoneWiseModelDetails")
	@Produces(MediaType.APPLICATION_JSON)		
	@Consumes(MediaType.APPLICATION_JSON)
	public List<HashMap<String,String>> getZoneWiseModelDetails (final @JsonProperty("reqObj") LinkedHashMap<String,Object> reqObj)throws CustomFault, JsonGenerationException, JsonMappingException, IOException {
		
		List<HashMap<String,String>> result = null;
		Logger iLogger = InfoLoggerClass.logger;
		List<String> profiles=null;
		List<String> zone_codes=null;
		if(reqObj.get("zone_code")!=null){
			zone_codes=(List<String>) reqObj.get("zone_code");
		}
		if(reqObj.get("profile")!=null){
			profiles=(List<String>) reqObj.get("profile");
		}
		System.out.println("---- Webservice input ------"+ "profiles :::"+ profiles+"zone_codes"+ zone_codes);
		iLogger.info("---- Webservice input ------"+"profiles"+ profiles+"zone_codes"+ zone_codes);
		result =  new DOutLetMappingImpl().getZoneWiseModelDetails(zone_codes,profiles);
		iLogger.info("---- Webservice output ------");
		iLogger.info("DealerOutletMapping ::: getZoneWiseModelDetails ::: Response :: "+ result);
		return result;
		
	}
	
	/*WS to return machine model count details associated with a profile and dealer 	*/
	//Shajesh : 2021-05-04 : Additional Changes
	@SuppressWarnings("unchecked")
	@POST
	@Path("getDelaerWiseModelDetails")
	@Produces(MediaType.APPLICATION_JSON)		
	@Consumes(MediaType.APPLICATION_JSON)
	public List<HashMap<String,String>> getDelaerWiseModelDetails (final @JsonProperty("reqObj") LinkedHashMap<String,Object> reqObj)throws CustomFault, JsonGenerationException, JsonMappingException, IOException {
		
		List<HashMap<String,String>> result = null;
		Logger iLogger = InfoLoggerClass.logger;
		List<String> profiles=null;
		List<String> dealer_codes=null;
		if(reqObj.get("dealer_code")!=null){
			dealer_codes=(List<String>) reqObj.get("dealer_code");
		}
		if(reqObj.get("profile")!=null){
			profiles=(List<String>) reqObj.get("profile");
		}
		System.out.println("---- Webservice input ------"+ "profiles :::"+ profiles+"dealer_codes"+ dealer_codes);
		iLogger.info("---- Webservice input ------"+"profiles"+ profiles+"dealer_codes"+ dealer_codes);
		result =  new DOutLetMappingImpl().getDelaerWiseModelDetails(dealer_codes,profiles);
		iLogger.info("---- Webservice output ------");
		iLogger.info("DealerOutletMapping ::: getDelaerWiseModelDetails ::: Response :: "+ result);
		return result;
		
	}
	
	/*WS to return machine under the zone and profile details associated 	*/
	//Shajesh : 2021-06-03 : Additional Changes
	@SuppressWarnings("unchecked")
	@POST
	@Path("getMachinesForMapUnderZoneAndProfile")
	@Produces(MediaType.APPLICATION_JSON)		
	@Consumes(MediaType.APPLICATION_JSON)
	public List<HashMap<String,String>> getMachinesForMapUnderZoneAndProfile (final @JsonProperty("reqObj") LinkedHashMap<String,Object> reqObj)throws CustomFault, JsonGenerationException, JsonMappingException, IOException {
		
		List<HashMap<String,String>> result = null;
		Logger iLogger = InfoLoggerClass.logger;
		List<String> profiles=null;
		List<String> zone_codes=null;
		if(reqObj.get("zone_code")!=null){
			zone_codes=(List<String>) reqObj.get("zone_code");
		}
		if(reqObj.get("profile")!=null){
			profiles=(List<String>) reqObj.get("profile");
		}
		System.out.println("---- Webservice input ------"+ "profiles :::"+ profiles+"zone_codes"+ zone_codes);
		iLogger.info("---- Webservice input ------"+"profiles"+ profiles+"zone_codes"+ zone_codes);
		result =  new DOutLetMappingImpl().getMachinesForMapUnderZoneAndProfile(zone_codes,profiles);
		iLogger.info("---- Webservice output ------");
		iLogger.info("DealerOutletMapping ::: getMachinesForMapUnderZoneAndProfile ::: Response :: "+ result);
		return result;
		
	}
	
	/*WS to return machine under the Dealer and profile details associated 	*/
	//Shajesh : 2021-06-03 : Additional Changes
	@SuppressWarnings("unchecked")
	@POST
	@Path("getMachinesForMapUnderDealerAndProfile")
	@Produces(MediaType.APPLICATION_JSON)		
	@Consumes(MediaType.APPLICATION_JSON)
	public List<HashMap<String,String>> getMachinesForMapUnderDealerAndProfile (final @JsonProperty("reqObj") LinkedHashMap<String,Object> reqObj)throws CustomFault, JsonGenerationException, JsonMappingException, IOException {
		
		List<HashMap<String,String>> result = null;
		Logger iLogger = InfoLoggerClass.logger;
		List<String> profiles=null;
		List<String> dealer_codes=null;
		if(reqObj.get("dealer_code")!=null){
			dealer_codes=(List<String>) reqObj.get("dealer_code");
		}
		if(reqObj.get("profile")!=null){
			profiles=(List<String>) reqObj.get("profile");
		}
		System.out.println("---- Webservice input ------"+ "profiles :::"+ profiles+"dealer_codes"+ dealer_codes);
		iLogger.info("---- Webservice input ------"+"profiles"+ profiles+"dealer_codes"+ dealer_codes);
		result =  new DOutLetMappingImpl().getMachinesForMapUnderDealerAndProfile(dealer_codes,profiles);
		iLogger.info("---- Webservice output ------");
		iLogger.info("DealerOutletMapping ::: getMachinesForMapUnderDealerAndProfile ::: Response :: "+ result);
		return result;
	}

	/* ws to return	Org wise Outlet counts */
	@SuppressWarnings("unchecked")
	@POST
	@Path("getOrgWiseOutletDetails")
	@Produces(MediaType.APPLICATION_JSON)	
	@Consumes(MediaType.APPLICATION_JSON)
	public List<HashMap<String, Map<String, String>>> getOrgWiseOutletDetails (final @JsonProperty("reqObj") LinkedHashMap<String,Object> reqObj)throws CustomFault, JsonGenerationException, JsonMappingException, IOException {
		Logger iLogger = InfoLoggerClass.logger;
		List<String> orgUnits=null;
		List<String> zones=null;
		List<String> dealers=null;
		List<String> outlet_types=null;
		if(reqObj.get("org_unit")!=null)
			orgUnits=(List<String>) reqObj.get("org_unit");	
		if(reqObj.get("outlet_type")!=null)
			outlet_types=(List<String>) reqObj.get("outlet_type");
		if(reqObj.get("zone")!=null)
			zones=(List<String>) reqObj.get("zone");		
		if(reqObj.get("dealer")!=null)
			dealers=(List<String>) reqObj.get("dealer");
		List<HashMap<String, Map<String, String>>> result = new DOutLetMappingImpl().getOrgWiseOutletDetails(orgUnits,outlet_types,zones,dealers);
		System.out.println("---- Webservice output ------");
		iLogger.info("DealerOutletMapping : getOrgWiseOutletDetails : Response ::"+ result);
		System.out.println("getOrgWiseOutletDetails"+result);
		return (List<HashMap<String, Map<String, String>>>) result;
		}
	
	
	/* ws to return	Org wise zonal counts */
	@SuppressWarnings("unchecked")
	@POST
	@Path("getOrgWiseZonalDetails")
	@Produces(MediaType.APPLICATION_JSON)	
	@Consumes(MediaType.APPLICATION_JSON)
	public List<HashMap<String, Map<String, String>>> getOrgWiseZonalDetails (final @JsonProperty("reqObj") LinkedHashMap<String,Object> reqObj)throws CustomFault, JsonGenerationException, JsonMappingException, IOException {
		Logger iLogger = InfoLoggerClass.logger;
		List<String> orgUnits=null;
		List<String> zones=null;
		List<String> dealers=null;
		List<String> outlet_types=null;
		if(reqObj.get("org_unit")!=null)
			orgUnits=(List<String>) reqObj.get("org_unit");	
		if(reqObj.get("zone")!=null)
			zones=(List<String>) reqObj.get("zone");		
		if(reqObj.get("dealer")!=null)
			dealers=(List<String>) reqObj.get("dealer");
		if(reqObj.get("outlet_type")!=null)
			outlet_types=(List<String>) reqObj.get("outlet_type");	
		List<HashMap<String, Map<String, String>>> result = new DOutLetMappingImpl().getOrgWiseZonalDetails(orgUnits,zones,dealers,outlet_types);
		System.out.println("---- Webservice output ------");
		iLogger.info("DealerOutletMapping : getOrgWiseZonalDetails : Response ::"+ result);
		System.out.println("getOrgWiseZonalDetails"+result);
		return (List<HashMap<String, Map<String, String>>>) result;
		}	
	@SuppressWarnings("unchecked")
	@POST
	@Path("getOrgWiseOutletDetailsForMap")
	@Produces(MediaType.APPLICATION_JSON)	
	@Consumes(MediaType.APPLICATION_JSON)
	public  List<HashMap<String, String>>  getOrgWiseOutletDetailsForMap (final @JsonProperty("reqObj") LinkedHashMap<String,Object> reqObj)throws CustomFault, JsonGenerationException, JsonMappingException, IOException {
		Logger iLogger = InfoLoggerClass.logger;
		List<String> orgUnits=null;
		List<String> zones=null;
		List<String> dealers=null;
		List<String> outlet_types=null;
		if(reqObj.get("org_unit")!=null)
			orgUnits=(List<String>) reqObj.get("org_unit");	
		if(reqObj.get("zone")!=null)
			zones=(List<String>) reqObj.get("zone");		
		if(reqObj.get("dealer")!=null)
			dealers=(List<String>) reqObj.get("dealer");
		if(reqObj.get("outlet_type")!=null)
			outlet_types=(List<String>) reqObj.get("outlet_type");	
		 List<HashMap<String, String>> result = new DOutLetMappingImpl().getOrgWiseOutletDetailsForMap(orgUnits,zones,dealers,outlet_types);
		System.out.println("---- Webservice output ------");
		iLogger.info("DealerOutletMapping : getOrgWiseOutletDetailsForMap : Response ::"+ result);
		System.out.println("getOrgWiseOutletDetailsForMap"+result);
		return ( List<HashMap<String, String>> ) result;
		}
	}
