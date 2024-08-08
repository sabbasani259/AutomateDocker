package remote.wise.service.implementation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.Logger;

import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.ListToStringConversion;

/**
 * WebService implementation class to fetch zone ,dealer and outlet_type from
 * dealer_outlet_mapping.
 * 
 * @author Shajesh Gangadharan
 * @author Bidisha Shaw
 * 
 */
public class DOutLetMappingImpl {
	Logger iLogger = InfoLoggerClass.logger;
	HashMap<String, String> mappings = null;
	
	public List<String> getUniqueOrgUnit() {
		//List<HashMap<String, String>> orgUnits = new LinkedList<HashMap<String, String>>();
		List<String> orgUnits=new ArrayList<String>();
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;
		try {

			ConnectMySQL connMySql = new ConnectMySQL();
			connection = connMySql.getConnection();
			statement = connection.createStatement();
			String selectQuery = "select distinct(org_unit) from  dealer_outlet_mapping";
			iLogger.info(" constructed Query : ---> " + selectQuery);
			rs = statement.executeQuery(selectQuery);
			while (rs.next()) {
				//mappings = new HashMap<String, String>();				
				if(rs.getString("org_unit") != null){orgUnits.add(rs.getString("org_unit"));}else{orgUnits.add("NA");}
				//orgUnits.add(mappings);
			}

		} catch (Exception e) {
			e.printStackTrace();
			iLogger.info("Exception in Fetching orgUnit deatils");
		}
		finally {
		    try { rs.close(); } catch (Exception e) { e.printStackTrace(); }
		    try { statement.close(); } catch (Exception e) { e.printStackTrace(); }
		    try { connection.close(); } catch (Exception e) { e.printStackTrace(); }
		}
		return orgUnits;
	}
	/*WS implementation for return unique zones*/
	public List<HashMap<String, String>> getUniqueZoneDetails(String orgUnit) {
		List<HashMap<String, String>> zones = new LinkedList<HashMap<String, String>>();
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;		
		try {

			ConnectMySQL connMySql = new ConnectMySQL();
			connection = connMySql.getConnection();
			statement = connection.createStatement();
			String selectQuery = null;
			if(orgUnit==null || orgUnit.isEmpty()){
				selectQuery = "select distinct(zone ), Zonal_Code from  dealer_outlet_mapping";
			}
			else if(orgUnit.equalsIgnoreCase("JCB")){
				selectQuery = "select distinct(zone ), Zonal_Code from  dealer_outlet_mapping where org_unit in ('"+ orgUnit +"')";
			}
			else{
				selectQuery = "select distinct(zone ), Zonal_Code from  dealer_outlet_mapping where org_unit in ("+ orgUnit +")";
	
			}
			iLogger.info(" constructed Query : ---> " + selectQuery);
			rs = statement.executeQuery(selectQuery);
			while (rs.next()) {
				mappings = new HashMap<String, String>();				
				if(rs.getString("zone") != null){mappings.put("zone", rs.getString("zone"));}else{mappings.put("zone", "NA");}
				if(rs.getString("Zonal_Code") != null){mappings.put("Zonal_Code", rs.getString("Zonal_Code"));}else{mappings.put("Zonal_Code", "NA");}
				zones.add(mappings);
			}

		} catch (Exception e) {
			e.printStackTrace();
			iLogger.info("Exception in Fetching Zone deatils");
		}
		finally {
		    try { rs.close(); } catch (Exception e) { e.printStackTrace(); }
		    try { statement.close(); } catch (Exception e) { e.printStackTrace(); }
		    try { connection.close(); } catch (Exception e) { e.printStackTrace(); }
		}
		return zones;
	}


	/*WS implementation for return unique dealers*/
	//2021-01-21: Added DealerCode for Networktb-Terittory Dealer***************
	public List<HashMap<String, String>> getUniqueDealersDetails(String zone,String orgUnit) {
		List<HashMap<String, String>> dealers = new LinkedList<HashMap<String, String>>();
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;
		String selectQuery = null;
		/*HashMap<String, String> mappings = null;*/
		try {
			ConnectMySQL connMySql = new ConnectMySQL();
			connection = connMySql.getConnection();
			statement = connection.createStatement();
			/*WS implementation to return unique dealers if the input parameter is null*/
			if(orgUnit==null | orgUnit.isEmpty()){
			if ( zone == null ){
				selectQuery = "select distinct(dealer ),Dealer_Code, zone from  dealer_outlet_mapping";
				iLogger.info(" constructed Query : ---> " + selectQuery);
				System.out.println(" constructed Query : ---> " + selectQuery);	
								
			}else{
			selectQuery = "select distinct(dealer) ,Dealer_Code from  dealer_outlet_mapping where zone in ('"+ zone +"')";
			iLogger.info(" constructed Query : ---> " + selectQuery);	
			System.out.println(" constructed Query : ---> " + selectQuery);	
			}}
			else{
				if ( zone == null){
					selectQuery = "select distinct(dealer ),Dealer_Code, zone from  dealer_outlet_mapping where org_unit in ("+ orgUnit +")";
					iLogger.info(" constructed Query : ---> " + selectQuery);
					System.out.println(" constructed Query : ---> " + selectQuery);	
									
				}
				else{
					selectQuery = "select distinct(dealer ),Dealer_Code, zone from  dealer_outlet_mapping where zone in ('"+ zone +"')  and  org_unit in ("+ orgUnit +")";
					iLogger.info(" constructed Query : ---> " + selectQuery);
					System.out.println(" constructed Query : ---> " + selectQuery);	
									
				}
			}
			rs = statement.executeQuery(selectQuery);
			while (rs.next()) {
				mappings = new HashMap<String, String>();
				if(rs.getString("dealer") != null){mappings.put("dealer", rs.getString("dealer"));}else{mappings.put("dealer", "NA");}
				if(rs.getString("Dealer_Code") != null){mappings.put("Dealer_Code", rs.getString("Dealer_Code"));}else{mappings.put("Dealer_Code", "NA");}
				dealers.add(mappings);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			iLogger.info("Exception in Fetching Dealers deatils");
		}finally {
		    try { rs.close(); } catch (Exception e) { e.printStackTrace(); }
		    try { statement.close(); } catch (Exception e) { e.printStackTrace(); }
		    try { connection.close(); } catch (Exception e) { e.printStackTrace(); }
		}
		
		return dealers;
	}
	
	/*WS implementation for return unique category/outlet_Types*/
	public List<HashMap<String, String>> getUniqueOutletTypeDetails(String dealer,String orgUnit) {
		List<HashMap<String, String>> outlet_types = new LinkedList<HashMap<String, String>>();
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;
		String selectQuery = null;
		/*HashMap<String, String> mappings = null;*/

		try {
			ConnectMySQL connMySql = new ConnectMySQL();
			connection = connMySql.getConnection();
			statement = connection.createStatement();
			/*WS implementation to return unique category/outlet_Types if the input parameter is null*/
			if(orgUnit==null | orgUnit.isEmpty()){
			if(dealer == null){
				selectQuery ="select distinct(outlet_type) from  dealer_outlet_mapping";
				iLogger.info(" constructed Query : ---> " + selectQuery);
				System.out.println(" constructed Query : ---> " + selectQuery);	
				rs = statement.executeQuery(selectQuery);			
				while (rs.next()) {
					mappings = new HashMap<String, String>();
					if(rs.getString("outlet_type") != null){mappings.put("outlet_type", rs.getString("outlet_type"));}else{mappings.put("outlet_type", "NA");}
					outlet_types.add(mappings);
				}
								
			}else{
				selectQuery = "select distinct(outlet_type), dealer from  dealer_outlet_mapping where dealer in ('"+ dealer +"')";
				iLogger.info(" constructed Query : ---> " + selectQuery);
				System.out.println(" constructed Query : ---> " + selectQuery);	
				rs = statement.executeQuery(selectQuery);			
				while (rs.next()) {
					mappings = new HashMap<String, String>();
					if(rs.getString("outlet_type") != null){mappings.put("outlet_type", rs.getString("outlet_type"));}else{mappings.put("outlet_type", "NA");}
					if(rs.getString("dealer") != null){mappings.put("dealer", rs.getString("dealer"));}else{mappings.put("dealer", "NA");}				
					outlet_types.add(mappings);
				}
			}
			}
			else{
				if(dealer == null){
					selectQuery ="select distinct(outlet_type) from  dealer_outlet_mapping where  org_unit in ("+ orgUnit +")";
					iLogger.info(" constructed Query : ---> " + selectQuery);
					System.out.println(" constructed Query : ---> " + selectQuery);	
					rs = statement.executeQuery(selectQuery);			
					while (rs.next()) {
						mappings = new HashMap<String, String>();
						if(rs.getString("outlet_type") != null){mappings.put("outlet_type", rs.getString("outlet_type"));}else{mappings.put("outlet_type", "NA");}
						outlet_types.add(mappings);
					}
									
				}
				else{
					selectQuery = "select distinct(outlet_type), dealer from  dealer_outlet_mapping where dealer in ('"+ dealer +"')  and  org_unit in ("+ orgUnit +")";
					iLogger.info(" constructed Query : ---> " + selectQuery);
					System.out.println(" constructed Query : ---> " + selectQuery);	
					rs = statement.executeQuery(selectQuery);			
					while (rs.next()) {
						mappings = new HashMap<String, String>();
						if(rs.getString("outlet_type") != null){mappings.put("outlet_type", rs.getString("outlet_type"));}else{mappings.put("outlet_type", "NA");}
						if(rs.getString("dealer") != null){mappings.put("dealer", rs.getString("dealer"));}else{mappings.put("dealer", "NA");}				
						outlet_types.add(mappings);
					}
				}
			}
			

		} catch (Exception e) {
			e.printStackTrace();
			iLogger.info("Exception in Fetching outlet_type deatils");
		}finally {
		    try { rs.close(); } catch (Exception e) { e.printStackTrace(); }
		    try { statement.close(); } catch (Exception e) { e.printStackTrace(); }
		    try { connection.close(); } catch (Exception e) { e.printStackTrace(); }
		}
		return outlet_types;
	}

	/*WS implementation for return ZonalDistributionDetails*/
	
	public List<HashMap<String, String>> getZonalDistributionDetails(String orgUnit,List<String> zones,List<String> dealers,List<String> outlet_types) {
		List<HashMap<String, String>> zonaldistributiondeatils = new LinkedList<HashMap<String, String>>();
		
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;
		String selectQuery = null;
		//String orgUnit=null;
		String zone = "";
		String dealer = "";
		String outlet_type = "";
		HashMap<String, String> mappings = null;	
		ListToStringConversion convert=new ListToStringConversion();
		//if(orgUnits != null){orgUnit =convert.getStringList(orgUnits).toString();}else{orgUnit = null;}
		if(zones != null){zone =convert.getStringList(zones).toString();}else{zone = null;}
		if(dealers != null){dealer =convert.getStringList(dealers).toString();}else{dealer = null;}
		if(outlet_types != null){outlet_type =convert.getStringList(outlet_types).toString();}else{outlet_type = null;}
		
		System.out.println("---------- ZONE--------------"+zone);
		System.out.println("---------- DEALER--------------"+dealer);
		System.out.println("---------- OUTLETTYPE--------------"+outlet_type);
		try {
			ConnectMySQL connMySql = new ConnectMySQL();
			connection = connMySql.getConnection();
			statement = connection.createStatement();
			/*getZonalDistributionDetails if customer not selecting zone, dealer and outlet_type*/
			/*if(zone == null && dealer == null && outlet_type == null )*/
			if((zone == null || zone.isEmpty()) && (dealer == null || dealer.isEmpty()) && (outlet_type == null|| outlet_type.isEmpty())){
				System.out.println("------ getZonalDistributionDetails: unselected -----------");
				if(orgUnit==null || orgUnit.isEmpty() || orgUnit.equalsIgnoreCase("JCB")){
				selectQuery ="select outlet_type, count(*) from dealer_outlet_mapping group by outlet_type";
				}
				else{
					selectQuery ="select outlet_type, count(*) from dealer_outlet_mapping  where org_unit in ("+ orgUnit +") group by outlet_type ";
				}
				iLogger.info(" constructed Query : ---> " + selectQuery);
				rs = statement.executeQuery(selectQuery);
				while(rs.next()){
					mappings = new HashMap<String, String>();
					if(rs.getString("outlet_type") != null){
						mappings.put("outlet_type", rs.getString("outlet_type"));
					}else{
						mappings.put("outlet_type", "NA");
					}
					if(rs.getString("count(*)") != null){
						mappings.put("count", rs.getString("count(*)"));
					}else{
						mappings.put("count", "NA");
					}
					zonaldistributiondeatils.add(mappings);
				}
				
			}	
			/*getZonalDistributionDetails if customer  selecting zone*/
			/*else if(dealer == null && outlet_type == null){	*/
			else if((dealer == null || dealer.isEmpty())&& (outlet_type == null || outlet_type.isEmpty())){
				System.out.println("------ getZonalDistributionDetails : zone selected -----------");
				if(orgUnit==null || orgUnit.isEmpty() || orgUnit.equalsIgnoreCase("JCB")){
				selectQuery = "select outlet_type, count(*) from dealer_outlet_mapping where zone in ("+ zone +") group by outlet_type";
				}
				else{
					selectQuery = "select outlet_type, count(*) from dealer_outlet_mapping where zone in ("+ zone +") and org_unit in ("+ orgUnit +")  group by outlet_type";
				}
				iLogger.info(" constructed Query:Total no of Touch Point : ---> " + selectQuery);
				rs = statement.executeQuery(selectQuery);
				while(rs.next()){
					mappings = new HashMap<String, String>();
					if(rs.getString("outlet_type") != null){
						mappings.put("outlet_type", rs.getString("outlet_type"));
					}else{
						mappings.put("outlet_type", "NA");
					}
					if(rs.getString("count(*)") != null){
						mappings.put("count", rs.getString("count(*)"));
					}else{
						mappings.put("count", "NA");
					}
					zonaldistributiondeatils.add(mappings);
				}
				
			}	
			/*getZonalDistributionDetails if customer  selecting dealer*/
			/*else if(outlet_type == null && zone == null){*/
			else if((zone == null ||zone.isEmpty()) && (outlet_type == null ||outlet_type.isEmpty())){
				System.out.println("------ getZonalDistributionDetails : dealer selected -----------");
				if(orgUnit==null || orgUnit.isEmpty() || orgUnit.equalsIgnoreCase("JCB")){
				selectQuery = "select outlet_type, count(*) from dealer_outlet_mapping where dealer in ("+ dealer +") group by outlet_type";
				}
				else{
					selectQuery = "select outlet_type, count(*) from dealer_outlet_mapping where dealer in ("+ dealer +") and org_unit in ("+ orgUnit +") group by outlet_type";
				}
				iLogger.info(" constructed Query:Total no of Touch Point : ---> " + selectQuery);
				rs = statement.executeQuery(selectQuery);
				while(rs.next()){
					mappings = new HashMap<String, String>();					
					if(rs.getString("outlet_type") != null){
						mappings.put("outlet_type", rs.getString("outlet_type"));
					}else{
						mappings.put("outlet_type", "NA");
					}
					if(rs.getString("count(*)") != null){
						mappings.put("count", rs.getString("count(*)"));
					}else{
						mappings.put("count", "NA");
					}
					zonaldistributiondeatils.add(mappings);
				}
				
			}	
			/*getZonalDistributionDetails if customer  selecting outlet_type*/
			/*else if(zone == null && dealer == null){*/
			else if((zone == null || zone.isEmpty()) && (dealer == null || dealer.isEmpty())){
				System.out.println("------ getZonalDistributionDetails : oulettype selected -----------");
				if(orgUnit==null || orgUnit.isEmpty() || orgUnit.equalsIgnoreCase("JCB")){
				selectQuery = "select outlet_type, count(*) from dealer_outlet_mapping where outlet_type in ("+ outlet_type +") group by outlet_type";
				}
				else{
					selectQuery = "select outlet_type, count(*) from dealer_outlet_mapping where outlet_type in ("+ outlet_type +") and org_unit in ("+ orgUnit +") group by outlet_type";
				}
				iLogger.info(" constructed Query:Total no of Touch Point : ---> " + selectQuery);
				rs = statement.executeQuery(selectQuery);
				while(rs.next()){
					mappings = new HashMap<String, String>();
					if(rs.getString("outlet_type") != null){
						mappings.put("outlet_type", rs.getString("outlet_type"));
					}else{
						mappings.put("outlet_type", "NA");
					}
					if(rs.getString("count(*)") != null){
						mappings.put("count", rs.getString("count(*)"));
					}else{
						mappings.put("count", "NA");
					}
					zonaldistributiondeatils.add(mappings);
				}
				
			}
			/*getZonalDistributionDetails if customer  selecting all*/
			else if(zone != null  && dealer != null && outlet_type != null){
			/*else if((zone != null || zone !="") && (dealer != null || dealer != "") && (outlet_type != null|| outlet_type !="")){*/
				System.out.println("------ getZonalDistributionDetails : all selected -----------");
				if(orgUnit==null || orgUnit.isEmpty() || orgUnit.equalsIgnoreCase("JCB")){
				selectQuery = "select outlet_type, count(*) from dealer_outlet_mapping where zone in ("+ zone +") and dealer in ("+ dealer +") and  outlet_type in ("+ outlet_type +") group by outlet_type";
				}
				else{
					selectQuery = "select outlet_type, count(*) from dealer_outlet_mapping where zone in ("+ zone +") and dealer in ("+ dealer +") and  outlet_type in ("+ outlet_type +")  and org_unit in ("+ orgUnit +") group by outlet_type";
				}
				iLogger.info(" constructed Query:Total no of Touch Point : ---> " + selectQuery);
				rs = statement.executeQuery(selectQuery);
				while(rs.next()){
					mappings = new HashMap<String, String>();
					if(rs.getString("outlet_type") != null){
						mappings.put("outlet_type", rs.getString("outlet_type"));
					}else{
						mappings.put("outlet_type", "NA");
					}
					if(rs.getString("count(*)") != null){
						mappings.put("count", rs.getString("count(*)"));
					}else{
						mappings.put("count", "NA");
					}
					zonaldistributiondeatils.add(mappings);
				}
				
			}
			/*getZonalDistributionDetails if customer  selecting zone and dealer*/
			else if(zone != null && dealer != null ){	
			/*else if((zone != null || zone != "") && (dealer != null || dealer != "")){*/
				System.out.println("------ getZonalDistributionDetails : zone and dealer selected -----------");
				if(orgUnit==null || orgUnit.isEmpty() || orgUnit.equalsIgnoreCase("JCB")){
				selectQuery = "select outlet_type, count(*) from dealer_outlet_mapping where zone in ("+ zone +") and dealer in ("+ dealer +") group by outlet_type";
				}
				else{
					selectQuery = "select outlet_type, count(*) from dealer_outlet_mapping where zone in ("+ zone +") and dealer in ("+ dealer +") and org_unit in ("+ orgUnit +")  group by outlet_type";
				}
				iLogger.info(" constructed Query:Total no of Touch Point : ---> " + selectQuery);
				rs = statement.executeQuery(selectQuery);
				while(rs.next()){
					mappings = new HashMap<String, String>();
					if(rs.getString("outlet_type") != null){
						mappings.put("outlet_type", rs.getString("outlet_type"));
					}else{
						mappings.put("outlet_type", "NA");
					}
					if(rs.getString("count(*)") != null){
						mappings.put("count", rs.getString("count(*)"));
					}else{
						mappings.put("count", "NA");
					}
					zonaldistributiondeatils.add(mappings);
				}
				
				
			}
			/*getZonalDistributionDetails if customer  selecting outlet_type and dealer*/
			else if(dealer != null && outlet_type != null ){
			/*else if((dealer != null || dealer != "") && (outlet_type != null|| outlet_type != "")){*/
				System.out.println("------ getZonalDistributionDetails:  dealer and outlet_type selected -----------");
				if(orgUnit==null || orgUnit.isEmpty() || orgUnit.equalsIgnoreCase("JCB")){
				selectQuery = "select outlet_type, count(*) from dealer_outlet_mapping where dealer in ("+ dealer +") and outlet_type in ("+ outlet_type +") group by outlet_type";
				}
				else{
					selectQuery = "select outlet_type, count(*) from dealer_outlet_mapping where dealer in ("+ dealer +") and outlet_type in ("+ outlet_type +") and org_unit in ("+ orgUnit +")  group by outlet_type";
				}
				iLogger.info(" constructed Query:Total no of Touch Point : ---> " + selectQuery);
				rs = statement.executeQuery(selectQuery);
				while(rs.next()){
					mappings = new HashMap<String, String>();
					
					if(rs.getString("outlet_type") != null){
						mappings.put("outlet_type", rs.getString("outlet_type"));
					}else{
						mappings.put("outlet_type", "NA");
					}
					if(rs.getString("count(*)") != null){
						mappings.put("count", rs.getString("count(*)"));
					}else{
						mappings.put("count", "NA");
					}
					zonaldistributiondeatils.add(mappings);
				}
				
			}
			/*getZonalDistributionDetails if customer  selecting outlet_type and zone*/
			else if(outlet_type != null && zone != null ){
			/*else if((outlet_type != null || outlet_type != "") && (zone != null|| zone != "")){*/
				System.out.println("------ getZonalDistributionDetails :  outlet_type and zone selected -----------");
				if(orgUnit==null || orgUnit.isEmpty() || orgUnit.equalsIgnoreCase("JCB")){
				selectQuery = "select outlet_type, count(*) from dealer_outlet_mapping where zone in ("+ zone +") and outlet_type in ("+ outlet_type +") group by outlet_type";
				}
				else{
					selectQuery = "select outlet_type, count(*) from dealer_outlet_mapping where zone in ("+ zone +") and outlet_type in ("+ outlet_type +")  and org_unit in ("+ orgUnit +")  group by outlet_type";
				}
				iLogger.info(" constructed Query:Total no of Touch Point : ---> " + selectQuery);
				rs = statement.executeQuery(selectQuery);
				while(rs.next()){
					mappings = new HashMap<String, String>();
					if(rs.getString("outlet_type") != null){
						mappings.put("outlet_type", rs.getString("outlet_type"));
					}else{
						mappings.put("outlet_type", "NA");
					}
					if(rs.getString("count(*)") != null){
						mappings.put("count", rs.getString("count(*)"));
					}else{
						mappings.put("count", "NA");
					}
					zonaldistributiondeatils.add(mappings);
				}				
			}			
		}catch (Exception e) {
			e.printStackTrace();
			iLogger.info("Exception in fecthing getZonalDistributionDetails");
		}finally {
		    try { rs.close(); } catch (Exception e) { e.printStackTrace(); }
		    try { statement.close(); } catch (Exception e) { e.printStackTrace(); }
		    try { connection.close(); } catch (Exception e) { e.printStackTrace(); }
		}
		return zonaldistributiondeatils;
	}
	/*WS implementation for return DealersDistributionDetails*/
	public List<HashMap<String, Map<String, String>>> getDealersDistributionDetails(String  orgUnit,List<String> zones,List<String> dealers,List<String> outlet_types) {
		List<HashMap<String, Map<String, String>>> dealersdistributiondeatils = new LinkedList<HashMap<String,Map<String, String>>>();
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;
		String selectQuery = null;
		Map<String, String> mappings = null;
		/*ListToStringConversion convert=new ListToStringConversion();
		String zone =convert.getStringList(zones).toString();
		String dealer =convert.getStringList(dealers).toString();
		String outlet_type =convert.getStringList(outlet_types).toString();*/
		//String orgUnit="";
		String zone ="";
		String dealer="";
		String outlet_type ="";
		ListToStringConversion convert=new ListToStringConversion();
		//if(orgUnits != null){orgUnit =convert.getStringList(orgUnits).toString();}else{orgUnit = null;}
		if(zones != null){zone =convert.getStringList(zones).toString();}else{zone = null;}
		if(dealers != null){dealer =convert.getStringList(dealers).toString();}else{dealer = null;}
		if(outlet_types != null){outlet_type =convert.getStringList(outlet_types).toString();}else{outlet_type = null;}
		System.out.println("---------- ZONE--------------"+zone);
		System.out.println("---------- DEALER--------------"+dealer);
		System.out.println("---------- OUTLETTYPE--------------"+outlet_type);
		Map<String, Map<String, String>> map = new LinkedHashMap<String, Map<String, String>>();
		try {
			ConnectMySQL connMySql = new ConnectMySQL();
			connection = connMySql.getConnection();
			statement = connection.createStatement();
			if((zone == null || zone.isEmpty()) && (dealer == null || dealer.isEmpty()) && (outlet_type == null|| outlet_type.isEmpty())){
				System.out.println("------ getDealersDistributionDetails :  none selected -----------");
				if(orgUnit==null || orgUnit.isEmpty() || orgUnit.equalsIgnoreCase("JCB")){
				selectQuery ="select dealer,outlet_type, count(*) from dealer_outlet_mapping group by outlet_type,dealer";
				}
				else{
					selectQuery ="select dealer,outlet_type, count(*) from dealer_outlet_mapping  where org_unit in ("+ orgUnit +") group by outlet_type,dealer";
				}
				iLogger.info(" constructed Query : ---> " + selectQuery);
				rs = statement.executeQuery(selectQuery);	
				while(rs.next()){					
					mappings = new HashMap<String, String>();
					if(rs.getString("outlet_type") !=null){						
						mappings.put(rs.getString("outlet_type"), rs.getString("count(*)"));
					if(map.containsKey(rs.getString("dealer"))){
						for(Map.Entry<String, String> val : map.get(rs.getString("dealer")).entrySet()){
							mappings.put(val.getKey(), val.getValue());							
						}
						map.put(rs.getString("dealer"), mappings);
					}else{
						map.put(rs.getString("dealer"), mappings);	
					}
					}					
				}	
				dealersdistributiondeatils.add((HashMap<String, Map<String, String>>) map);
				}	
			else if((dealer == null || dealer.isEmpty())&& (outlet_type == null || outlet_type.isEmpty())){
				System.out.println("------ getDealersDistributionDetails :  zone selected -----------");
				if(orgUnit==null || orgUnit.isEmpty() || orgUnit.equalsIgnoreCase("JCB")){
				selectQuery ="select dealer,outlet_type, count(*) from dealer_outlet_mapping where zone in ("+ zone +") group by outlet_type,dealer";
				}
				else{
					selectQuery ="select dealer,outlet_type, count(*) from dealer_outlet_mapping where zone in ("+ zone +")  and  org_unit in ("+ orgUnit +") group by outlet_type,dealer";
				}
				iLogger.info(" constructed Query : ---> " + selectQuery);
				rs = statement.executeQuery(selectQuery);	
				while(rs.next()){					
					mappings = new HashMap<String, String>();
					if(rs.getString("outlet_type") !=null){						
						mappings.put(rs.getString("outlet_type"), rs.getString("count(*)"));
					if(map.containsKey(rs.getString("dealer"))){
						for(Map.Entry<String, String> val : map.get(rs.getString("dealer")).entrySet()){
							mappings.put(val.getKey(), val.getValue());							
						}
						map.put(rs.getString("dealer"), mappings);
					}else{
						map.put(rs.getString("dealer"), mappings);	
					}
					}
					}
				dealersdistributiondeatils.add((HashMap<String, Map<String, String>>) map);
				}	
			else if((zone == null ||zone.isEmpty()) && (outlet_type == null ||outlet_type.isEmpty())){
				System.out.println("------ getDealersDistributionDetails :  dealer selected -----------");
				if(orgUnit==null || orgUnit.isEmpty() || orgUnit.equalsIgnoreCase("JCB")){
				selectQuery ="select dealer,outlet_type, count(*) from dealer_outlet_mapping where dealer in ("+ dealer +") group by outlet_type,dealer";
				}
				else{
					selectQuery ="select dealer,outlet_type, count(*) from dealer_outlet_mapping where dealer in ("+ dealer +") and  org_unit in ("+ orgUnit +") group by outlet_type,dealer";
				}
				iLogger.info(" constructed Query : ---> " + selectQuery);
				rs = statement.executeQuery(selectQuery);	
				while(rs.next()){					
					mappings = new HashMap<String, String>();
					if(rs.getString("outlet_type") !=null){						
						mappings.put(rs.getString("outlet_type"), rs.getString("count(*)"));
					if(map.containsKey(rs.getString("dealer"))){
						for(Map.Entry<String, String> val : map.get(rs.getString("dealer")).entrySet()){
							mappings.put(val.getKey(), val.getValue());							
						}
						map.put(rs.getString("dealer"), mappings);
					}else{
						map.put(rs.getString("dealer"), mappings);	
					}
					}
					}
				dealersdistributiondeatils.add((HashMap<String, Map<String, String>>) map);
				}	
			else if((zone == null || zone.isEmpty()) && (dealer == null || dealer.isEmpty())){
				System.out.println("------ getDealersDistributionDetails :  outlet_type selected -----------");
				if(orgUnit==null || orgUnit.isEmpty() || orgUnit.equalsIgnoreCase("JCB")){
				selectQuery ="select dealer,outlet_type, count(*) from dealer_outlet_mapping where outlet_type in ("+ outlet_type +") group by outlet_type,dealer";
				}
				else{
					selectQuery ="select dealer,outlet_type, count(*) from dealer_outlet_mapping where outlet_type in ("+ outlet_type +") and  org_unit in ("+ orgUnit +")  group by outlet_type,dealer";
				}
				iLogger.info(" constructed Query : ---> " + selectQuery);
				rs = statement.executeQuery(selectQuery);	
				while(rs.next()){					
					mappings = new HashMap<String, String>();
					if(rs.getString("outlet_type") !=null){						
						mappings.put(rs.getString("outlet_type"), rs.getString("count(*)"));
					if(map.containsKey(rs.getString("dealer"))){
						for(Map.Entry<String, String> val : map.get(rs.getString("dealer")).entrySet()){
							mappings.put(val.getKey(), val.getValue());							
						}
						map.put(rs.getString("dealer"), mappings);
					}else{
						map.put(rs.getString("dealer"), mappings);	
					}
					}
					}
				dealersdistributiondeatils.add((HashMap<String, Map<String, String>>) map);
				}	
			else if(zone != null && dealer != null && outlet_type != null){
				System.out.println("------ getDealersDistributionDetails :  all selected -----------");
				if(orgUnit==null || orgUnit.isEmpty() || orgUnit.equalsIgnoreCase("JCB")){
				selectQuery ="select dealer,outlet_type, count(*) from dealer_outlet_mapping where zone in ("+ zone +") and dealer in ("+ dealer +") and  outlet_type in ("+ outlet_type +") group by outlet_type,dealer";
				}
				else{
					selectQuery ="select dealer,outlet_type, count(*) from dealer_outlet_mapping where zone in ("+ zone +") and dealer in ("+ dealer +") and  outlet_type in ("+ outlet_type +") and org_unit in ("+ orgUnit +") group by outlet_type,dealer";
				}
				iLogger.info(" constructed Query : ---> " + selectQuery);
				rs = statement.executeQuery(selectQuery);	
				while(rs.next()){					
					mappings = new HashMap<String, String>();
					if(rs.getString("outlet_type") !=null){						
						mappings.put(rs.getString("outlet_type"), rs.getString("count(*)"));
					if(map.containsKey(rs.getString("dealer"))){
						for(Map.Entry<String, String> val : map.get(rs.getString("dealer")).entrySet()){
							mappings.put(val.getKey(), val.getValue());							
						}
						map.put(rs.getString("dealer"), mappings);
					}else{
						map.put(rs.getString("dealer"), mappings);	
					}
					}
					
				}				
				dealersdistributiondeatils.add((HashMap<String, Map<String, String>>) map);
				}
			else if(zone != null && dealer != null ){
				System.out.println("------ getDealersDistributionDetails :  zone and dealer selected -----------");
				if(orgUnit==null || orgUnit.isEmpty() || orgUnit.equalsIgnoreCase("JCB")){
				selectQuery ="select dealer,outlet_type, count(*) from dealer_outlet_mapping where zone in ("+ zone +") and dealer in ("+ dealer +") group by outlet_type,dealer";
				}
				else{
					selectQuery ="select dealer,outlet_type, count(*) from dealer_outlet_mapping where zone in ("+ zone +") and dealer in ("+ dealer +") and org_unit in ("+ orgUnit +")  group by outlet_type,dealer";	
				}
				iLogger.info(" constructed Query : ---> " + selectQuery);
				rs = statement.executeQuery(selectQuery);	
				while(rs.next()){					
					mappings = new HashMap<String, String>();
					if(rs.getString("outlet_type") !=null){						
						mappings.put(rs.getString("outlet_type"), rs.getString("count(*)"));
					if(map.containsKey(rs.getString("dealer"))){
						for(Map.Entry<String, String> val : map.get(rs.getString("dealer")).entrySet()){
							mappings.put(val.getKey(), val.getValue());							
						}
						map.put(rs.getString("dealer"), mappings);
					}else{
						map.put(rs.getString("dealer"), mappings);	
					}
					}
					
				}				
				dealersdistributiondeatils.add((HashMap<String, Map<String, String>>) map);
				}	
			else if(dealer != null && outlet_type != null ){
				System.out.println("------ getDealersDistributionDetails :  dealer and outlet_type selected -----------");
				if(orgUnit==null || orgUnit.isEmpty() || orgUnit.equalsIgnoreCase("JCB")){
				selectQuery ="select dealer,outlet_type, count(*) from dealer_outlet_mapping where dealer in ("+ dealer +") and outlet_type in ("+ outlet_type +") group by outlet_type,dealer";
				}
				else{
					selectQuery ="select dealer,outlet_type, count(*) from dealer_outlet_mapping where dealer in ("+ dealer +") and outlet_type in ("+ outlet_type +") and org_unit in ("+ orgUnit +")  group by outlet_type,dealer";	
				}
				iLogger.info(" constructed Query : ---> " + selectQuery);
				rs = statement.executeQuery(selectQuery);	
				while(rs.next()){					
					mappings = new HashMap<String, String>();
					if(rs.getString("outlet_type") !=null){						
						mappings.put(rs.getString("outlet_type"), rs.getString("count(*)"));
					if(map.containsKey(rs.getString("dealer"))){
						for(Map.Entry<String, String> val : map.get(rs.getString("dealer")).entrySet()){
							mappings.put(val.getKey(), val.getValue());							
						}
						map.put(rs.getString("dealer"), mappings);
					}else{
						map.put(rs.getString("dealer"), mappings);	
					}
					}
					
				}		
				dealersdistributiondeatils.add((HashMap<String, Map<String, String>>) map);
				}	
			else if(outlet_type != null && zone != null ){
				System.out.println("------ getDealersDistributionDetails :  outlet_type and zone selected -----------");
				if(orgUnit==null || orgUnit.isEmpty() || orgUnit.equalsIgnoreCase("JCB")){
				selectQuery ="select dealer,outlet_type, count(*) from dealer_outlet_mapping where zone in ("+ zone +") and outlet_type in ("+ outlet_type +") group by outlet_type,dealer";
				}
				else{
					selectQuery ="select dealer,outlet_type, count(*) from dealer_outlet_mapping where zone in ("+ zone +") and outlet_type in ("+ outlet_type +")  and org_unit in ("+ orgUnit +") group by outlet_type,dealer";	
				}
				iLogger.info(" constructed Query : ---> " + selectQuery);
				rs = statement.executeQuery(selectQuery);	
				while(rs.next()){					
					mappings = new HashMap<String, String>();
					if(rs.getString("outlet_type") !=null){						
						mappings.put(rs.getString("outlet_type"), rs.getString("count(*)"));
					if(map.containsKey(rs.getString("dealer"))){
						for(Map.Entry<String, String> val : map.get(rs.getString("dealer")).entrySet()){
							mappings.put(val.getKey(), val.getValue());							
						}
						map.put(rs.getString("dealer"), mappings);
					}else{
						map.put(rs.getString("dealer"), mappings);	
					}
					}
					
				}		
				dealersdistributiondeatils.add((HashMap<String, Map<String, String>>) map);
				}	
		}catch (Exception e) {
			e.printStackTrace();
			iLogger.info("Exception in fecthing getDealersDistributionDetails");
		}finally {
		    try { rs.close(); } catch (Exception e) { e.printStackTrace(); }
		    try { statement.close(); } catch (Exception e) { e.printStackTrace(); }
		    try { connection.close(); } catch (Exception e) { e.printStackTrace(); }
		}
		return dealersdistributiondeatils;
	}
	/*WS implementation for return DealersAndTouchPointCountDetails*/
	public List<HashMap<String, String>> getDealersAndTouchPointCountDetails(String orgUnit,List<String> zones,List<String> dealers,List<String> outlet_types) {
		List<HashMap<String, String>> dealersandtouchpointdeatils = new LinkedList<HashMap<String, String>>();
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;
		String selectQuery = null;
		HashMap<String, String> mappings = null;
		//String orgUnit="";
		String zone ="";
		String dealer="";
		String outlet_type ="";
		ListToStringConversion convert=new ListToStringConversion();
		//if(orgUnits != null){orgUnit =convert.getStringList(orgUnits).toString();}else{orgUnit = null;}
		if(zones != null){zone =convert.getStringList(zones).toString();}else{zone = null;}
		if(dealers != null){dealer =convert.getStringList(dealers).toString();}else{dealer = null;}
		if(outlet_types != null){outlet_type =convert.getStringList(outlet_types).toString();}else{outlet_type = null;}
		System.out.println("---------- ZONE--------------"+zone);
		System.out.println("---------- DEALER--------------"+dealer);
		System.out.println("---------- OUTLETTYPE--------------"+outlet_type);

		try {
			ConnectMySQL connMySql = new ConnectMySQL();
			connection = connMySql.getConnection();
			statement = connection.createStatement();
			/*condition return total touch point and total distinct dealers if zone, dealer and outlet_type are null*/
			if((zone == null || zone.isEmpty()) && (dealer == null || dealer.isEmpty()) && (outlet_type == null || outlet_type.isEmpty()) ){
				System.out.println("-------- condition return total touch point and total distinct dealers if zone, dealer and outlet_type are null  ----------");
				if(orgUnit==null || orgUnit.isEmpty() || orgUnit.equalsIgnoreCase("JCB")){
				selectQuery = "select count(distinct(dealer)) , count(*) from dealer_outlet_mapping ;";	
				}
				else{
					selectQuery = "select count(distinct(dealer)) , count(*) from dealer_outlet_mapping where org_unit in ("+ orgUnit +")";
				}
				iLogger.info(" constructed Query:: ---> " + selectQuery);
				rs = statement.executeQuery(selectQuery);
				while(rs.next()){
					mappings = new HashMap<String, String>();
					
					if(rs.getString("count(distinct(dealer))") != null){
						mappings.put("totaldealers", rs.getString("count(distinct(dealer))"));
					}else{
						mappings.put("totaldealers", "NA");
					}
					if(rs.getString("count(*)") != null){
						mappings.put("totaltouchpoint", rs.getString("count(*)"));
					}else{
						mappings.put("totaltouchpoint", "NA");
					}
					dealersandtouchpointdeatils.add(mappings);
				}
				
			}
			/*condition return total touch point and total distinct dealers if dealer , outlet_type are null*/
			else if((dealer == null||dealer.isEmpty()) && (outlet_type == null ||outlet_type.isEmpty()) ){
				System.out.println("----  condition return total touch point and total distinct dealers if dealer , outlet_type are null   ----");
				if(orgUnit==null || orgUnit.isEmpty() || orgUnit.equalsIgnoreCase("JCB")){
				selectQuery = "select count(distinct(dealer)), count(*) from dealer_outlet_mapping  where zone in ("+ zone +")";
				}
				else{
					selectQuery = "select count(distinct(dealer)), count(*) from dealer_outlet_mapping  where zone in ("+ zone +") and org_unit in ("+ orgUnit +")";
				}
				iLogger.info(" constructed Query:Total no of Touch Point : ---> " + selectQuery);
				rs = statement.executeQuery(selectQuery);
				while(rs.next()){
					mappings = new HashMap<String, String>();
					if(rs.getString("count(distinct(dealer))") != null){
						mappings.put("totaldealers", rs.getString("count(distinct(dealer))"));
					}else{
						mappings.put("totaldealers", "NA");
					}
					if(rs.getString("count(*)") != null){
						mappings.put("totaltouchpoint", rs.getString("count(*)"));
					}else{
						mappings.put("totaltouchpoint", "NA");
					}
					dealersandtouchpointdeatils.add(mappings);
				}
				
			}
			/*condition return total touch point and total distinct dealers if zone , outlet_type are null*/
			else if((outlet_type == null|| outlet_type.isEmpty()) && (zone == null||zone.isEmpty())){
				System.out.println("----   condition return total touch point and total distinct dealers if zone , outlet_type are null  ----");
				if(orgUnit==null || orgUnit.isEmpty() || orgUnit.equalsIgnoreCase("JCB")){
				selectQuery = "select count(distinct(dealer)), count(*) from dealer_outlet_mapping  where dealer in ("+ dealer +")";
				}
				else{
					selectQuery = "select count(distinct(dealer)), count(*) from dealer_outlet_mapping  where dealer in ("+ dealer +") and org_unit in ("+ orgUnit +")";
				}
				iLogger.info(" constructed Query:Total no of Touch Point : ---> " + selectQuery);
				rs = statement.executeQuery(selectQuery);
				while(rs.next()){
					mappings = new HashMap<String, String>();
					if(rs.getString("count(distinct(dealer))") != null){
						mappings.put("totaldealers", rs.getString("count(distinct(dealer))"));
					}else{
						mappings.put("totaldealers", "NA");
					}
					if(rs.getString("count(*)") != null){
						mappings.put("totaltouchpoint", rs.getString("count(*)"));
					}else{
						mappings.put("totaltouchpoint", "NA");
					}
					dealersandtouchpointdeatils.add(mappings);
				}
				
			}
			/*condition return total touch point and total distinct dealers if zone , dealer are null*/
			else if((zone == null|| zone.isEmpty()) && (dealer == null || dealer.isEmpty())){
				System.out.println("----  condition return total touch point and total distinct dealers if zone , dealer are null   ----");
				if(orgUnit==null || orgUnit.isEmpty() || orgUnit.equalsIgnoreCase("JCB")){
				selectQuery = "select count(distinct(dealer)), count(*) from dealer_outlet_mapping  where outlet_type in ("+ outlet_type +")";
				}
				else{
					selectQuery = "select count(distinct(dealer)), count(*) from dealer_outlet_mapping  where outlet_type in ("+ outlet_type +") and org_unit in ("+ orgUnit +")";
				}
				iLogger.info(" constructed Query:Total no of Touch Point : ---> " + selectQuery);
				rs = statement.executeQuery(selectQuery);
				while(rs.next()){
					mappings = new HashMap<String, String>();
					
					if(rs.getString("count(distinct(dealer))") != null){
						mappings.put("totaldealers", rs.getString("count(distinct(dealer))"));
					}else{
						mappings.put("totaldealers", "NA");
					}
					if(rs.getString("count(*)") != null){
						mappings.put("totaltouchpoint", rs.getString("count(*)"));
					}else{
						mappings.put("totaltouchpoint", "NA");
					}
					dealersandtouchpointdeatils.add(mappings);
				}
				
			}
			/*condition return total touch point and total distinct dealers if zone , dealer and outlet_type are not null*/
			else if(zone != null  && dealer != null  && outlet_type != null){
				System.out.println("----   condition return total touch point and total distinct dealers if zone , dealer and outlet_type are not null  ----");
				if(orgUnit==null || orgUnit.isEmpty() || orgUnit.equalsIgnoreCase("JCB")){
				selectQuery = "select count(distinct(dealer)), count(*) from dealer_outlet_mapping where zone in ("+ zone +") and dealer in ("+ dealer +") and  outlet_type in ("+ outlet_type +")";
				}
				else{
					selectQuery = "select count(distinct(dealer)), count(*) from dealer_outlet_mapping where zone in ("+ zone +") and dealer in ("+ dealer +") and  outlet_type in ("+ outlet_type +")  and org_unit in ("+ orgUnit +")";
				}
				iLogger.info(" constructed Query : ---> " + selectQuery);
				rs = statement.executeQuery(selectQuery);
				while(rs.next()){
					mappings = new HashMap<String, String>();
					
					if(rs.getString("count(distinct(dealer))") != null){
						mappings.put("totaldealers", rs.getString("count(distinct(dealer))"));
					}else{
						mappings.put("totaldealers", "NA");
					}
					if(rs.getString("count(*)") != null){
						mappings.put("totaltouchpoint", rs.getString("count(*)"));
					}else{
						mappings.put("totaltouchpoint", "NA");
					}
					dealersandtouchpointdeatils.add(mappings);
				}
				
			}
			/*condition return total touch point and total distinct dealers if zone and dealer are not null*/
			else if(zone != null && dealer != null ){
				System.out.println("----  condition return total touch point and total distinct dealers if zone and dealer are not null   ----");
				if(orgUnit==null || orgUnit.isEmpty() || orgUnit.equalsIgnoreCase("JCB")){
				selectQuery = "select count(distinct(dealer)), count(*) from dealer_outlet_mapping where zone in ("+ zone +") and dealer in ("+ dealer +") ";
				}
				else{
					selectQuery = "select count(distinct(dealer)), count(*) from dealer_outlet_mapping where zone in ("+ zone +") and dealer in ("+ dealer +")  and org_unit in ("+ orgUnit +")";
				}
				iLogger.info(" constructed Query : ---> " + selectQuery);
				rs = statement.executeQuery(selectQuery);
				while(rs.next()){
					mappings = new HashMap<String, String>();
					
					if(rs.getString("count(distinct(dealer))") != null){
						mappings.put("totaldealers", rs.getString("count(distinct(dealer))"));
					}else{
						mappings.put("totaldealers", "NA");
					}
					if(rs.getString("count(*)") != null){
						mappings.put("totaltouchpoint", rs.getString("count(*)"));
					}else{
						mappings.put("totaltouchpoint", "NA");
					}
					dealersandtouchpointdeatils.add(mappings);
				}
				
				
			}
			/*condition return total touch point and total distinct dealers if outlet_type and dealer are not null*/
			else if(dealer != null && outlet_type != null ){
				System.out.println("----  condition return total touch point and total distinct dealers if outlet_type and dealer are not null   ----");
				if(orgUnit==null || orgUnit.isEmpty() || orgUnit.equalsIgnoreCase("JCB")){
				selectQuery = "select count(distinct(dealer)), count(*) from dealer_outlet_mapping where dealer in ("+ dealer +") and  outlet_type in ("+ outlet_type +")";
				}
				else{
					selectQuery = "select count(distinct(dealer)), count(*) from dealer_outlet_mapping where dealer in ("+ dealer +") and  outlet_type in ("+ outlet_type +") and org_unit in ("+ orgUnit +")";
				}
				iLogger.info(" constructed Query : ---> " + selectQuery);
				rs = statement.executeQuery(selectQuery);
				while(rs.next()){
					mappings = new HashMap<String, String>();					
					if(rs.getString("count(distinct(dealer))") != null){
						mappings.put("totaldealers", rs.getString("count(distinct(dealer))"));
					}else{
						mappings.put("totaldealers", "NA");
					}
					if(rs.getString("count(*)") != null){
						mappings.put("totaltouchpoint", rs.getString("count(*)"));
					}else{
						mappings.put("totaltouchpoint", "NA");
					}
					dealersandtouchpointdeatils.add(mappings);
				}
				
			}
			/*condition return total touch point and total distinct dealers if outlet_type and zone are not null*/
			else if(outlet_type != null && zone != null ){
				System.out.println("----  condition return total touch point and total distinct dealers if outlet_type and zone are not null   ----");
				if(orgUnit==null || orgUnit.isEmpty() || orgUnit.equalsIgnoreCase("JCB")){
				selectQuery = "select count(distinct(dealer)), count(*) from dealer_outlet_mapping where zone in ("+ zone +") and  outlet_type in ("+ outlet_type +")";
				}
				else{
					selectQuery = "select count(distinct(dealer)), count(*) from dealer_outlet_mapping where zone in ("+ zone +") and  outlet_type in ("+ outlet_type +")  and org_unit in ("+ orgUnit +")";
				}
				iLogger.info(" constructed Query : ---> " + selectQuery);
				rs = statement.executeQuery(selectQuery);
				while(rs.next()){
					mappings = new HashMap<String, String>();					
					if(rs.getString("count(distinct(dealer))") != null){
						mappings.put("totaldealers", rs.getString("count(distinct(dealer))"));
					}else{
						mappings.put("totaldealers", "NA");
					}
					if(rs.getString("count(*)") != null){
						mappings.put("totaltouchpoint", rs.getString("count(*)"));
					}else{
						mappings.put("totaltouchpoint", "NA");
					}
					dealersandtouchpointdeatils.add(mappings);
				}
				
			}
		
			
		}catch (Exception e) {
			e.printStackTrace();
			iLogger.info("Exception in fecthing getDealersAndTouchPointCountDetails");
		}finally {
		    try { rs.close(); } catch (Exception e) { e.printStackTrace(); }
		    try { statement.close(); } catch (Exception e) { e.printStackTrace(); }
		    try { connection.close(); } catch (Exception e) { e.printStackTrace(); }
		}
		return dealersandtouchpointdeatils;
	}

	/*WS implementation for return DealersDeatilsForMap*/
	public List<HashMap<String, String>> getDealersDeatilsForMap(String orgUnit,List<String> zones,List<String> dealers,List<String> outlet_types) {
		List<HashMap<String, String>> dealersdeatilsformap = new LinkedList<HashMap<String, String>>();
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;
		String selectQuery = null;
		HashMap<String, String> mappings = null;		
		/*ListToStringConversion convert=new ListToStringConversion();
		String zone =convert.getStringList(zones).toString();
		String dealer =convert.getStringList(dealers).toString();
		String outlet_type =convert.getStringList(outlet_types).toString();*/
		//String orgUnit="";
		String zone ="";
		String dealer="";
		String outlet_type ="";
		ListToStringConversion convert=new ListToStringConversion();
		//if(orgUnits != null){orgUnit =convert.getStringList(orgUnits).toString();}else{orgUnit = null;}
		if(zones != null){zone =convert.getStringList(zones).toString();}else{zone = null;}
		if(dealers != null){dealer =convert.getStringList(dealers).toString();}else{dealer = null;}
		if(outlet_types != null){outlet_type =convert.getStringList(outlet_types).toString();}else{outlet_type = null;}
		System.out.println("---------- ZONE--------------"+zone);
		System.out.println("---------- DEALER--------------"+dealer);
		System.out.println("---------- OUTLETTYPE--------------"+outlet_type);

		try {
			ConnectMySQL connMySql = new ConnectMySQL();
			connection = connMySql.getConnection();
			statement = connection.createStatement();
			
			if((zone == null || zone.isEmpty()) && (dealer == null || dealer.isEmpty()) && (outlet_type == null|| outlet_type.isEmpty())){
				System.out.println("---- getDealersDeatilsForMap : None Selected   ----");
				if(orgUnit==null || orgUnit.isEmpty() || orgUnit.equalsIgnoreCase("JCB")){
				selectQuery = "select dealer,outlet_type,address,location,lat,lon from dealer_outlet_mapping";
				}
				else{
					selectQuery = "select dealer,outlet_type,address,location,lat,lon from dealer_outlet_mapping  where org_unit in ("+ orgUnit +")";
				}
				iLogger.info(" constructed Query : ---> " + selectQuery);
				rs = statement.executeQuery(selectQuery);
				while(rs.next()){
					mappings = new HashMap<String, String>();
					if(rs.getString("dealer") != null){
						mappings.put("dealer", rs.getString("dealer"));
					}else{
						mappings.put("dealer", "NA");
					}
					if(rs.getString("outlet_type") != null){
						mappings.put("outlet_type", rs.getString("outlet_type"));
					}else{
						mappings.put("outlet_type", "NA");
					}
					if(rs.getString("address") != null){
						mappings.put("address", rs.getString("address").replace("–", ""));
					}else{
						mappings.put("address", "NA");
					}
					
					if(rs.getString("location") != null){
						mappings.put("location", rs.getString("location"));
					}else{
						mappings.put("location", "NA");
					}
					if(rs.getString("lat") != null){
						mappings.put("lat", rs.getString("lat"));
					}else{
						mappings.put("lat", "NA");
					}
					if(rs.getString("lon") != null){
						mappings.put("lon", rs.getString("lon"));
					}else{
						mappings.put("lon", "NA");
					}
					dealersdeatilsformap.add(mappings);
				}
				
				
			}

			else if((dealer == null || dealer.isEmpty())&& (outlet_type == null || outlet_type.isEmpty())){
				System.out.println("---- getDealersDeatilsForMap : dealer and outlet_type are null   ----");
				if(orgUnit==null || orgUnit.isEmpty() || orgUnit.equalsIgnoreCase("JCB")){
				selectQuery = "select dealer,outlet_type,address,location,lat,lon from dealer_outlet_mapping where zone in ("+zone+")";
				}
				else{
					selectQuery = "select dealer,outlet_type,address,location,lat,lon from dealer_outlet_mapping where zone in ("+zone+") and org_unit in ("+ orgUnit +")";
				}
				iLogger.info(" constructed Query : ---> " + selectQuery);
				rs = statement.executeQuery(selectQuery);
				while(rs.next()){
					mappings = new HashMap<String, String>();
					if(rs.getString("dealer") != null){
						mappings.put("dealer", rs.getString("dealer"));
					}else{
						mappings.put("dealer", "NA");
					}
					if(rs.getString("outlet_type") != null){
						mappings.put("outlet_type", rs.getString("outlet_type"));
					}else{
						mappings.put("outlet_type", "NA");
					}
					if(rs.getString("address") != null){
						mappings.put("address", rs.getString("address").replace("–", ""));
					}else{
						mappings.put("address", "NA");
					}
					
					if(rs.getString("location") != null){
						mappings.put("location", rs.getString("location"));
					}else{
						mappings.put("location", "NA");
					}
					if(rs.getString("lat") != null){
						mappings.put("lat", rs.getString("lat"));
					}else{
						mappings.put("lat", "NA");
					}
					if(rs.getString("lon") != null){
						mappings.put("lon", rs.getString("lon"));
					}else{
						mappings.put("lon", "NA");
					}
					dealersdeatilsformap.add(mappings);
				}
				
				
			}
			else if((zone == null ||zone.isEmpty()) && (outlet_type == null ||outlet_type.isEmpty())){
				System.out.println("---- getDealersDeatilsForMap : zone and outlet_type are null    ----");
				if(orgUnit==null || orgUnit.isEmpty() || orgUnit.equalsIgnoreCase("JCB")){
				selectQuery = "select dealer,outlet_type,address,location,lat,lon from dealer_outlet_mapping where dealer in ("+dealer+")";
				}
				else{
					selectQuery = "select dealer,outlet_type,address,location,lat,lon from dealer_outlet_mapping where dealer in ("+dealer+") and org_unit in ("+ orgUnit +")";
				}
				iLogger.info(" constructed Query : ---> " + selectQuery);
				rs = statement.executeQuery(selectQuery);
				while(rs.next()){
					mappings = new HashMap<String, String>();
					if(rs.getString("dealer") != null){
						mappings.put("dealer", rs.getString("dealer"));
					}else{
						mappings.put("dealer", "NA");
					}
					if(rs.getString("outlet_type") != null){
						mappings.put("outlet_type", rs.getString("outlet_type"));
					}else{
						mappings.put("outlet_type", "NA");
					}
					if(rs.getString("address") != null){
						mappings.put("address", rs.getString("address").replace("–", ""));
					}else{
						mappings.put("address", "NA");
					}
					
					if(rs.getString("location") != null){
						mappings.put("location", rs.getString("location"));
					}else{
						mappings.put("location", "NA");
					}
					if(rs.getString("lat") != null){
						mappings.put("lat", rs.getString("lat"));
					}else{
						mappings.put("lat", "NA");
					}
					if(rs.getString("lon") != null){
						mappings.put("lon", rs.getString("lon"));
					}else{
						mappings.put("lon", "NA");
					}
					dealersdeatilsformap.add(mappings);
				}
				
			}
			else if((zone == null || zone.isEmpty()) && (dealer == null || dealer.isEmpty())){
				System.out.println("---- getDealersDeatilsForMap : dealer and zone are null    ----");
				if(orgUnit==null || orgUnit.isEmpty() || orgUnit.equalsIgnoreCase("JCB")){
				selectQuery = "select dealer,outlet_type,address,location,lat,lon from dealer_outlet_mapping where outlet_type in ("+outlet_type+")";
				}
				else{
					selectQuery = "select dealer,outlet_type,address,location,lat,lon from dealer_outlet_mapping where outlet_type in ("+outlet_type+")  and org_unit in ("+ orgUnit +")";
				}
				iLogger.info(" constructed Query : ---> " + selectQuery);
				rs = statement.executeQuery(selectQuery);
				while(rs.next()){
					mappings = new HashMap<String, String>();
					if(rs.getString("dealer") != null){
						mappings.put("dealer", rs.getString("dealer"));
					}else{
						mappings.put("dealer", "NA");
					}
					if(rs.getString("outlet_type") != null){
						mappings.put("outlet_type", rs.getString("outlet_type"));
					}else{
						mappings.put("outlet_type", "NA");
					}
					if(rs.getString("address") != null){
						mappings.put("address", rs.getString("address").replace("–", ""));
					}else{
						mappings.put("address", "NA");
					}
					
					if(rs.getString("location") != null){
						mappings.put("location", rs.getString("location"));
					}else{
						mappings.put("location", "NA");
					}
					if(rs.getString("lat") != null){
						mappings.put("lat", rs.getString("lat"));
					}else{
						mappings.put("lat", "NA");
					}
					if(rs.getString("lon") != null){
						mappings.put("lon", rs.getString("lon"));
					}else{
						mappings.put("lon", "NA");
					}
					dealersdeatilsformap.add(mappings);
				}
				
			}

			else if(zone != null & dealer != null & outlet_type != null){
				System.out.println("---- getDealersDeatilsForMap : all Selected   ----");
				if(orgUnit==null || orgUnit.isEmpty() || orgUnit.equalsIgnoreCase("JCB")){
				selectQuery = "select dealer,outlet_type,address,location,lat,lon from dealer_outlet_mapping where zone in ("+zone+") and dealer in ("+dealer+") and outlet_type in ("+outlet_type+")";
				}
				else{
					selectQuery = "select dealer,outlet_type,address,location,lat,lon from dealer_outlet_mapping where zone in ("+zone+") and dealer in ("+dealer+") and outlet_type in ("+outlet_type+") and org_unit in ("+ orgUnit +")";
				}
				iLogger.info(" constructed Query : ---> " + selectQuery);
				rs = statement.executeQuery(selectQuery);
				while(rs.next()){
					mappings = new HashMap<String, String>();
					if(rs.getString("dealer") != null){
						mappings.put("dealer", rs.getString("dealer"));
					}else{
						mappings.put("dealer", "NA");
					}
					if(rs.getString("outlet_type") != null){
						mappings.put("outlet_type", rs.getString("outlet_type"));
					}else{
						mappings.put("outlet_type", "NA");
					}
					if(rs.getString("address") != null){
						mappings.put("address", rs.getString("address").replace("–", ""));
					}else{
						mappings.put("address", "NA");
					}
					
					if(rs.getString("location") != null){
						mappings.put("location", rs.getString("location"));
					}else{
						mappings.put("location", "NA");
					}
					if(rs.getString("lat") != null){
						mappings.put("lat", rs.getString("lat"));
					}else{
						mappings.put("lat", "NA");
					}
					if(rs.getString("lon") != null){
						mappings.put("lon", rs.getString("lon"));
					}else{
						mappings.put("lon", "NA");
					}
					dealersdeatilsformap.add(mappings);
				}
			}

			else if(zone != null & dealer != null ){
				System.out.println("---- getDealersDeatilsForMap : zone and dealer Selected   ----");
				if(orgUnit==null || orgUnit.isEmpty() || orgUnit.equalsIgnoreCase("JCB")){
				selectQuery = "select dealer,outlet_type,address,location,lat,lon from dealer_outlet_mapping where zone in ("+zone+") and dealer in ("+dealer+")";
				}
				else{
					selectQuery = "select dealer,outlet_type,address,location,lat,lon from dealer_outlet_mapping where zone in ("+zone+") and dealer in ("+dealer+") and org_unit in ("+ orgUnit +")";
				}
				iLogger.info(" constructed Query : ---> " + selectQuery);
				rs = statement.executeQuery(selectQuery);
				while(rs.next()){
					mappings = new HashMap<String, String>();
					if(rs.getString("dealer") != null){
						mappings.put("dealer", rs.getString("dealer"));
					}else{
						mappings.put("dealer", "NA");
					}
					if(rs.getString("outlet_type") != null){
						mappings.put("outlet_type", rs.getString("outlet_type"));
					}else{
						mappings.put("outlet_type", "NA");
					}
					if(rs.getString("address") != null){
						mappings.put("address", rs.getString("address").replace("–", ""));
					}else{
						mappings.put("address", "NA");
					}
					
					if(rs.getString("location") != null){
						mappings.put("location", rs.getString("location"));
					}else{
						mappings.put("location", "NA");
					}
					if(rs.getString("lat") != null){
						mappings.put("lat", rs.getString("lat"));
					}else{
						mappings.put("lat", "NA");
					}
					if(rs.getString("lon") != null){
						mappings.put("lon", rs.getString("lon"));
					}else{
						mappings.put("lon", "NA");
					}
					dealersdeatilsformap.add(mappings);
				}
				
			}
			else if(dealer != null & outlet_type != null ){
				System.out.println("---- getDealersDeatilsForMap : dealer and outlet_type Selected   ----");
				if(orgUnit==null || orgUnit.isEmpty() || orgUnit.equalsIgnoreCase("JCB")){
				selectQuery = "select dealer,outlet_type,address,location,lat,lon from dealer_outlet_mapping where dealer in ("+dealer+") and outlet_type in ("+outlet_type+")";
				}
				else{
					selectQuery = "select dealer,outlet_type,address,location,lat,lon from dealer_outlet_mapping where dealer in ("+dealer+") and outlet_type in ("+outlet_type+") and org_unit in ("+ orgUnit +")";
				}
				iLogger.info(" constructed Query : ---> " + selectQuery);
				rs = statement.executeQuery(selectQuery);
				while(rs.next()){
					mappings = new HashMap<String, String>();
					if(rs.getString("dealer") != null){
						mappings.put("dealer", rs.getString("dealer"));
					}else{
						mappings.put("dealer", "NA");
					}
					if(rs.getString("outlet_type") != null){
						mappings.put("outlet_type", rs.getString("outlet_type"));
					}else{
						mappings.put("outlet_type", "NA");
					}
					if(rs.getString("address") != null){
						mappings.put("address", rs.getString("address").replace("–", ""));
					}else{
						mappings.put("address", "NA");
					}
					
					if(rs.getString("location") != null){
						mappings.put("location", rs.getString("location"));
					}else{
						mappings.put("location", "NA");
					}
					if(rs.getString("lat") != null){
						mappings.put("lat", rs.getString("lat"));
					}else{
						mappings.put("lat", "NA");
					}
					if(rs.getString("lon") != null){
						mappings.put("lon", rs.getString("lon"));
					}else{
						mappings.put("lon", "NA");
					}
					dealersdeatilsformap.add(mappings);
				}
				
			}
			else if(outlet_type != null & zone != null ){
				System.out.println("---- getDealersDeatilsForMap : outlet_type and zone Selected   ----");
				if(orgUnit==null || orgUnit.isEmpty() || orgUnit.equalsIgnoreCase("JCB")){
				selectQuery = "select dealer,outlet_type,address,location,lat,lon from dealer_outlet_mapping where outlet_type in ("+outlet_type+") and zone in ("+zone+")";
				}
				else{
					selectQuery = "select dealer,outlet_type,address,location,lat,lon from dealer_outlet_mapping where outlet_type in ("+outlet_type+") and zone in ("+zone+") and org_unit in ("+ orgUnit +")";
				}
				iLogger.info(" constructed Query : ---> " + selectQuery);
				rs = statement.executeQuery(selectQuery);
				while(rs.next()){
					mappings = new HashMap<String, String>();
					if(rs.getString("dealer") != null){
						mappings.put("dealer", rs.getString("dealer"));
					}else{
						mappings.put("dealer", "NA");
					}
					if(rs.getString("outlet_type") != null){
						mappings.put("outlet_type", rs.getString("outlet_type"));
					}else{
						mappings.put("outlet_type", "NA");
					}
					if(rs.getString("address") != null){
						mappings.put("address", rs.getString("address").replace("–", ""));
					}else{
						mappings.put("address", "NA");
					}
					
					if(rs.getString("location") != null){
						mappings.put("location", rs.getString("location"));
					}else{
						mappings.put("location", "NA");
					}
					if(rs.getString("lat") != null){
						mappings.put("lat", rs.getString("lat"));
					}else{
						mappings.put("lat", "NA");
					}
					if(rs.getString("lon") != null){
						mappings.put("lon", rs.getString("lon"));
					}else{
						mappings.put("lon", "NA");
					}
					dealersdeatilsformap.add(mappings);
				}
				
			}
		}catch (Exception e) {
			e.printStackTrace();
			iLogger.info("Exception in fecthing getDealersDeatilsForMap");
		}finally {
		    try { rs.close(); } catch (Exception e) { e.printStackTrace(); }
		    try { statement.close(); } catch (Exception e) { e.printStackTrace(); }
		    try { connection.close(); } catch (Exception e) { e.printStackTrace(); }
		}
		return dealersdeatilsformap;
	}
	/*WS implementation for return DefaultSelectionDataDetails*/
	public List<HashMap<String, Map<String, String>>> getDefaultSelectionDataDetails(String orgUnit,List<String> outlet_types) {
		List<HashMap<String, Map<String, String>>> getdefaultselectiondatadetails = new LinkedList<HashMap<String,Map<String, String>>>();
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;
		String selectQuery = null;
		Map<String, String> mappings = null;	
		//String orgUnit=null;
		String outlet_type ="";
		ListToStringConversion convert=new ListToStringConversion();
		//if(orgUnits != null){orgUnit =convert.getStringList(orgUnits).toString();}else{orgUnit = null;}
		if(outlet_types != null){outlet_type =convert.getStringList(outlet_types).toString();}else{outlet_type = null;}
		Map<String, Map<String, String>> map = new LinkedHashMap<String, Map<String, String>>();
		try{
			ConnectMySQL connMySql = new ConnectMySQL();
			connection = connMySql.getConnection();
			statement = connection.createStatement();
			if(outlet_type == null || outlet_type.isEmpty()){
			System.out.println("------ getdefaultselectiondatadetails :  outlet_type Empty -----------");
			if(orgUnit==null || orgUnit.isEmpty() || orgUnit.equalsIgnoreCase("JCB")){
				selectQuery ="select zone, outlet_type, count(*) from dealer_outlet_mapping group by zone,outlet_type";
			}
			else{
				selectQuery ="select zone, outlet_type, count(*) from dealer_outlet_mapping where org_unit in ("+ orgUnit +") group by zone,outlet_type";
			}
				iLogger.info(" constructed Query : ---> " + selectQuery);
				rs = statement.executeQuery(selectQuery);	
				while(rs.next()){					
					mappings = new HashMap<String, String>();
					if(rs.getString("outlet_type") !=null){						
						mappings.put(rs.getString("outlet_type"), rs.getString("count(*)"));
					if(map.containsKey(rs.getString("zone"))){
						for(Map.Entry<String, String> val : map.get(rs.getString("zone")).entrySet()){
							mappings.put(val.getKey(), val.getValue());							
						}
						map.put(rs.getString("zone"), mappings);
					}else{
						map.put(rs.getString("zone"), mappings);	
					}
					}					
				}	
				getdefaultselectiondatadetails.add((HashMap<String, Map<String, String>>) map);
			}
			else{
				System.out.println("------ getdefaultselectiondatadetails :  outlet_type selectd -----------");
				if(orgUnit==null || orgUnit.isEmpty() || orgUnit.equalsIgnoreCase("JCB")){
				selectQuery ="select zone, outlet_type, count(*) from dealer_outlet_mapping where outlet_type in ("+outlet_type+") group by zone, outlet_type";
				}
				else{
					selectQuery ="select zone, outlet_type, count(*) from dealer_outlet_mapping where outlet_type in ("+outlet_type+") and org_unit in ("+ orgUnit +") group by zone, outlet_type";
				}
				iLogger.info(" constructed Query : ---> " + selectQuery);
				rs = statement.executeQuery(selectQuery);	
				while(rs.next()){					
					mappings = new HashMap<String, String>();
					if(rs.getString("outlet_type") !=null){						
						mappings.put(rs.getString("outlet_type"), rs.getString("count(*)"));
					if(map.containsKey(rs.getString("zone"))){
						for(Map.Entry<String, String> val : map.get(rs.getString("zone")).entrySet()){
							mappings.put(val.getKey(), val.getValue());							
						}
						map.put(rs.getString("zone"), mappings);
					}else{
						map.put(rs.getString("zone"), mappings);	
					}
					}					
				}	
				getdefaultselectiondatadetails.add((HashMap<String, Map<String, String>>) map);
			
			}				
			
		}catch (Exception e) {
			iLogger.info("Execption in getdefaultselectiondatadetails()");
			e.printStackTrace();
		}finally {
		    try { rs.close(); } catch (Exception e) { e.printStackTrace(); }
		    try { statement.close(); } catch (Exception e) { e.printStackTrace(); }
		    try { connection.close(); } catch (Exception e) { e.printStackTrace(); }
		}
		return getdefaultselectiondatadetails;
	}

	//Shajesh : 2021-01-18 : Additional Changes - Implementation for getting profile zone wise
	
		public List<HashMap<String, Map<String, String>>> getZoneWiseProfileDetails(List<String> zonal_codes) throws CustomFault {
			List<HashMap<String, Map<String, String>>> getzonewiseprofiledetails = new LinkedList<HashMap<String,Map<String, String>>>();
			Logger iLogger = InfoLoggerClass.logger;
			Logger fLogger = FatalLoggerClass.logger;
			Connection connection = null;
			Statement statement = null;
			ResultSet rs = null;
			String selectQuery = null;
			HashMap<String, String> mappings = null;		
			String zonal_code ="";		
			ListToStringConversion convert=new ListToStringConversion();
			if(zonal_codes != null){zonal_code =convert.getStringList(zonal_codes).toString();}else{zonal_code = null;}		
			System.out.println("---------- ZONE--------------"+zonal_code);	
			Map<String, Map<String, String>> map = new LinkedHashMap<String, Map<String, String>>();
			try {
				ConnectMySQL connMySql = new ConnectMySQL();
				connection = connMySql.getConnection();
				statement = connection.createStatement();
				if(zonal_code == null){
					System.out.println(" Zone  code is null");
					// 2021-02-11 : Shajesh : for fetching active machines only : 2021-06015 :Add AND a.Renewal_Flag=1
					selectQuery = "select croe.zone, croe.tmh, croe.profile, COUNT(*), a.status from com_rep_oem_enhanced croe, asset a where a.status=1 AND a.Renewal_Flag=1 AND	a.serial_number=croe.serial_number AND croe.zone is not null AND croe.profile is not null group by croe.profile, croe.zone ";
					iLogger.info(" constructed Query:: ---> " + selectQuery);
					
				}else{
					if(zonal_code.contains("NA")){
						System.out.println(" Zone  code is  NA");
						// 2021-02-11 : Shajesh : for fetching active machines only : 2021-06015 :Add AND a.Renewal_Flag=1
						selectQuery = "select croe.zone, croe.tmh, croe.profile, COUNT(*), a.status, country from com_rep_oem_enhanced croe, asset a where country IN ('Bhutan' , 'Nepal','Myanmar','Bangaldesh','Srilanka') AND  a.status=1 AND a.Renewal_Flag=1 AND	a.serial_number=croe.serial_number AND croe.zone is not null and croe.profile is not null group by croe.profile, croe.zone ";				
						iLogger.info(" constructed Query:: ---> " + selectQuery);
					}else{
					System.out.println(" Zone  code is not null"+zonal_code);
					// 2021-02-11 : Shajesh : for fetching active machines only : 2021-06015 :Add AND a.Renewal_Flag=1
					selectQuery = "select croe.zone, croe.tmh, croe.profile, COUNT(*), a.status from com_rep_oem_enhanced croe, asset a where zonalcode in ("+ zonal_code +") AND  a.status=1 AND a.Renewal_Flag=1  AND	a.serial_number=croe.serial_number AND croe.zone is not null and croe.profile is not null group by croe.profile, croe.zone ";				
					iLogger.info(" constructed Query:: ---> " + selectQuery);
					}
				}
					System.out.println(selectQuery);
					rs = statement.executeQuery(selectQuery);
					while(rs.next()){
						mappings = new HashMap<String, String>();						
						if((rs.getString("profile") !=null && !rs.getString("profile").isEmpty()) && (rs.getString("zone") !=null || !rs.getString("zone").isEmpty())){						
							mappings.put(rs.getString("profile"), rs.getString("count(*)"));
						if(map.containsKey(rs.getString("zone"))){
							for(Map.Entry<String, String> val : map.get(rs.getString("zone")).entrySet()){
								mappings.put(val.getKey(), val.getValue());							
							}
							map.put(rs.getString("zone"), mappings);
						}else{
							map.put(rs.getString("zone"), mappings);	
						}
						}					
					}
					getzonewiseprofiledetails.add((HashMap<String, Map<String, String>>) map);			
				
			}catch (Exception e) {
				e.printStackTrace();
				fLogger.info("Exception in getZoneWiseProfileDetails"+ e);
			}finally {
			    try { rs.close(); } catch (Exception e) { e.printStackTrace(); }
			    try { statement.close(); } catch (Exception e) { e.printStackTrace(); }
			    try { connection.close(); } catch (Exception e) { e.printStackTrace(); }
			}
			return getzonewiseprofiledetails;
		}

		//Shajesh : 2021-01-18 : Additional Changes - Implementation for getting profile dealer wise
		public List<HashMap<String, Map<String, String>>> getDealerWiseProfileDetails(String dealer_code) {
			List<HashMap<String, Map<String, String>>> getdealerwiseprofiledetails = new LinkedList<HashMap<String,Map<String, String>>>();
			Logger iLogger = InfoLoggerClass.logger;
			Logger fLogger = FatalLoggerClass.logger;
			Connection connection = null;
			Statement statement = null;
			ResultSet rs = null;
			String selectQuery = null;
			HashMap<String, String> mappings = null;			
			/*String dealer_code ="";	
			ListToStringConversion convert=new ListToStringConversion();
			if(dealer_codes != null){dealer_code =convert.getStringList(dealer_codes).toString();}else{dealer_code = null;}	*/
			System.out.println("---------- DEALER_CODE--------------"+dealer_code);	
			Map<String, Map<String, String>> map = new LinkedHashMap<String, Map<String, String>>();
			try {
				ConnectMySQL connMySql = new ConnectMySQL();
				connection = connMySql.getConnection();
				statement = connection.createStatement();
				if(dealer_code == null){
					System.out.println("---------- dealer code is  null--------------");
					// 2021-02-11 : Shajesh : for fetching active machines only 
					selectQuery = "select croe.DealerCode, croe.profile, COUNT(*), a.status FROM com_rep_oem_enhanced croe, asset a where a.status = 1 AND a.serial_number = croe.serial_number and croe.DealerCode is not null and  croe.profile is not null GROUP BY croe.profile,croe.DealerCode";
					iLogger.info(" constructed Query:: ---> " + selectQuery);
					
				}else{
					System.out.println("---------- dealer code is  not null--------------"+dealer_code);
					//selectQuery = " select DealerCode, Profile , count(*) from com_rep_oem_enhanced where DealerCode in ("+ dealer_code +")group by profile ";
					// 2021-02-11 : Shajesh : for fetching active machines only 
					selectQuery = "select croe.DealerCode, croe.profile, COUNT(*), a.status FROM com_rep_oem_enhanced croe, asset a WHERE croe.DealerCode IN ('"+ dealer_code +"') and a.status=1 AND a.serial_number = croe.serial_number and croe.DealerCode is not null and  croe.profile is not null GROUP BY croe.profile,croe.DealerCode";
					iLogger.info(" constructed Query:: ---> " + selectQuery);
					
				}
					rs = statement.executeQuery(selectQuery);
					while(rs.next()){
						mappings = new HashMap<String, String>();
						System.out.println("--- Profile ---"+rs.getString("profile"));
						System.out.println("--- DealerCode ---"+rs.getString("DealerCode"));
						System.out.println("--- count(*) ---"+rs.getString("COUNT(*)"));
						if((rs.getString("profile") != null && !rs.getString("profile").isEmpty()) && (rs.getString("DealerCode") != null || !rs.getString("DealerCode").isEmpty())){						
							mappings.put(rs.getString("profile"), rs.getString("COUNT(*)"));
						if(map.containsKey(rs.getString("DealerCode"))){
							for(Map.Entry<String, String> val : map.get(rs.getString("DealerCode")).entrySet()){
								mappings.put(val.getKey(), val.getValue());							
							}
							map.put(rs.getString("DealerCode"), mappings);
						}else{
							map.put(rs.getString("DealerCode"), mappings);	
						}
						}					
					}
					getdealerwiseprofiledetails.add((HashMap<String, Map<String, String>>) map);
					
								
			}catch (Exception e) {
				e.printStackTrace();
				fLogger.info("Exception in getZoneWiseProfileDetails"+ e);
			}finally {
			    try { rs.close(); } catch (Exception e) { e.printStackTrace(); }
			    try { statement.close(); } catch (Exception e) { e.printStackTrace(); }
			    try { connection.close(); } catch (Exception e) { e.printStackTrace(); }
			}
			return getdealerwiseprofiledetails;
		}
		
		//Shajesh : 2021-01-22 : Additional Changes - Implementation for getting machine by  zone wise
				public List<HashMap<String,String>> getMachinesByZoneWiseForMap(List<String> zone_codes) {
					List<HashMap<String, String>> getmachinesbyzonewiseformap = new LinkedList<HashMap<String, String>>();
					/*List<String> val= new ArrayList<>();*/
					Connection connection = null;
					Statement statement = null;
					ResultSet rs = null;
					String selectQuery = null;
					HashMap<String, String> mappings = null;				
					String zone_code ="";			
					ListToStringConversion convert=new ListToStringConversion();
					if(zone_codes != null){zone_code =convert.getStringList(zone_codes).toString();}else{zone_code = null;}			
					System.out.println("---------- ZONE--------------"+zone_code);
					
					try {
						ConnectMySQL connMySql = new ConnectMySQL();
						connection = connMySql.getConnection();
						statement = connection.createStatement();
						if(zone_code == null ){
							System.out.println("---- getMachinesByZoneWise : Default Selection  ----");
							selectQuery = "SELECT c.profile,c.serial_number,c.lat, c.lon,c.tmh, c.ZonalCode, d.location,d.Zonal_Code FROM com_rep_oem_enhanced c, dealer_outlet_mapping d WHERE  c.ZonalCode = d.Zonal_Code GROUP BY c.serial_number";
							iLogger.info(" constructed Query : ---> " + selectQuery);
							
						}else{
							if(zone_code.contains("NA")){
								System.out.println(" Zone  code is  NA");
								selectQuery = "SELECT c.profile,c.serial_number,c.lat, c.lon,c.tmh, c.ZonalCode,c.country, d.location,d.Zonal_Code FROM com_rep_oem_enhanced c, dealer_outlet_mapping d WHERE c.country IN ('Bhutan' , 'Nepal','Myanmar','Bangaldesh','Srilanka') and  c.ZonalCode = d.Zonal_Code GROUP BY c.serial_number";				
								iLogger.info(" constructed Query:: ---> " + selectQuery);
							}else{
							System.out.println("---- getMachinesByZoneWise : Zone_Code Selection  ----"+zone_code);
							selectQuery = "SELECT c.profile,c.serial_number,c.lat, c.lon ,c.tmh,c.ZonalCode,d.location,d.Zonal_Code FROM com_rep_oem_enhanced c, dealer_outlet_mapping d WHERE d.Zonal_Code IN ("+ zone_code +") AND c.ZonalCode = d.Zonal_Code GROUP BY c.serial_number";
							iLogger.info(" constructed Query : ---> " + selectQuery);
							}
							
						}
							rs = statement.executeQuery(selectQuery);							
							while(rs.next()){
								mappings = new HashMap<String, String>();
								if(rs.getString("serial_number") != null){mappings.put("serial_number", rs.getString("serial_number"));
								}else{mappings.put("serial_number", "NA");}
								if(rs.getString("profile") != null){mappings.put("profile", rs.getString("profile"));
								}else{mappings.put("profile", "NA");}
								if(rs.getString("location") != null){mappings.put("location", rs.getString("location"));
								}else{mappings.put("location", "NA");}
								if(rs.getString("lat") != null){mappings.put("lat", rs.getString("lat"));
								}else{mappings.put("lat", "NA");}
								if(rs.getString("lon") != null){mappings.put("lon", rs.getString("lon"));
								}else{mappings.put("lon", "NA");}
								if(rs.getString("tmh") != null){mappings.put("tmh", rs.getString("tmh"));
								}else{mappings.put("tmh", "NA");}
								getmachinesbyzonewiseformap.add(mappings);								
						}
						
					}catch (Exception e) {
						e.printStackTrace();
						iLogger.info("Exception in fecthing getMachinesByZoneWiseForMap");
					}finally {
					    try { rs.close(); } catch (Exception e) { e.printStackTrace(); }
					    try { statement.close(); } catch (Exception e) { e.printStackTrace(); }
					    try { connection.close(); } catch (Exception e) { e.printStackTrace(); }
					}
					
				return getmachinesbyzonewiseformap;
			}

				//Shajesh : 2021-01-25 : Additional Changes - Implementation for getting machine for a dealer in a zone 
				public List<HashMap<String, String>> getMachineCountOfDealersInZoneForMap(List<String> zones, List<String> dealers) {
					List<HashMap<String, String>> getmachinecountofdealersinzoneformap = new LinkedList<HashMap<String,String>>();
					Logger iLogger = InfoLoggerClass.logger;
					Connection connection = null;
					Statement statement = null;
					ResultSet rs = null;
					String selectQuery = null;
					HashMap<String, String> mappings = null;	
					String dealer_code ="";	
					String zone_code ="";	
					ListToStringConversion convert=new ListToStringConversion();
					if(zones != null){zone_code =convert.getStringList(zones).toString();}else{zone_code = null;}						
					if(dealers != null){dealer_code =convert.getStringList(dealers).toString();}else{dealer_code = null;}	
					System.out.println("---------- ZONE_CODE--------------"+zone_code);
					System.out.println("---------- DEALER_CODE--------------"+dealer_code);	
					try {
						ConnectMySQL connMySql = new ConnectMySQL();
						connection = connMySql.getConnection();
						statement = connection.createStatement();
						if(zone_code != null && dealer_code != null ){
							System.out.println("getMachineCountOfDealersInZoneForMap : Input Zone -> "+ zone_code +"Dealer -> "+dealer_code);													
							selectQuery = "SELECT c.profile,c.serial_number,c.lat,c.lon,c.tmh,c.ZonalCode,c.dealercode,d.location,d.Zonal_Code,d.dealer_code FROM com_rep_oem_enhanced c,dealer_outlet_mapping d WHERE	c.dealercode IN ("+ dealer_code +") AND c.Zonalcode IN ("+ zone_code +") AND c.dealercode = d.dealer_code AND c.ZonalCode = d.Zonal_Code GROUP BY c.dealercode,c.ZonalCode,c.serial_number";
							iLogger.info(" constructed Query : ---> " + selectQuery);
						}
						else{
							System.out.println("getMachineCountOfDealersInZoneForMap : Deafult selection");							
							selectQuery = "SELECT c.profile,c.serial_number,c.lat,d.lon,c.tmh,c.ZonalCode,c.dealercode,d.location,d.Zonal_Code,d.dealer_code FROM com_rep_oem_enhanced c,dealer_outlet_mapping d WHERE	c.dealercode = d.dealer_code AND c.ZonalCode = d.Zonal_Code GROUP BY c.dealercode,c.ZonalCode,c.serial_number";
							iLogger.info(" constructed Query : ---> " + selectQuery);
						}
						rs = statement.executeQuery(selectQuery);						
						while(rs.next()){
							
							mappings = new HashMap<String, String>();
							if(rs.getString("serial_number") != null){mappings.put("serial_number", rs.getString("serial_number"));
							}else{mappings.put("serial_number", "NA");}							
							if(rs.getString("profile") != null){mappings.put("profile", rs.getString("profile"));
							}else{mappings.put("profile", "NA");}							
							if(rs.getString("location") != null){mappings.put("location", rs.getString("location"));
							}else{mappings.put("location", "NA");}
							if(rs.getString("lat") != null){mappings.put("lat", rs.getString("lat"));							
							}else{mappings.put("lat","NA");}
							if(rs.getString("lon") != null){mappings.put("lon", rs.getString("lon"));							
							}else{mappings.put("lon", "NA");}
							if(rs.getString("tmh") != null){mappings.put("tmh", rs.getString("tmh"));							
							}else{mappings.put("tmh", "NA");}
									
							getmachinecountofdealersinzoneformap.add(mappings);	
						}											
					}catch (Exception e) {
						e.printStackTrace();
						iLogger.info("Exception in fecthing getMachineCountOfDealersInZoneForMap");
					}finally {
					    try { rs.close(); } catch (Exception e) { e.printStackTrace(); }
					    try { statement.close(); } catch (Exception e) { e.printStackTrace(); }
					    try { connection.close(); } catch (Exception e) { e.printStackTrace(); }
					}
					return getmachinecountofdealersinzoneformap;
				}

				/*//Shajesh : 2021-04-13 : Additional Changes - Implementation for getting profile wise filer				
				@SuppressWarnings("unused")
				public List<HashMap<String, String>> getProfileWiseFilter(List<String> profiles) {
					List<HashMap<String, String>> getProfileWiseFilterDeatils = new LinkedList<HashMap<String, String>>();
					Connection connection = null;
					Statement statement = null;
					ResultSet rs = null;
					String selectQuery = null;
					HashMap<String, String> mappings = null;				
					String pro_file ="";			
					ListToStringConversion convert=new ListToStringConversion();
					if(pro_file != null){pro_file =convert.getStringList(profiles).toString();	
					}else {pro_file=null;}
					iLogger.info("---------- profile--------------"+pro_file);	
					
					try {
						ConnectMySQL connMySql = new ConnectMySQL();
						connection = connMySql.getConnection();
						statement = connection.createStatement();
						if(pro_file != null && !pro_file.isEmpty() && pro_file.length() != 0){
							System.out.println("---- getProfileWiseFilter :Profile Selection  ----");
							selectQuery = "SELECT  croe.serial_number, croe.profile, croe.lat, croe.lon, croe.tmh, a.status FROM com_rep_oem_enhanced croe, asset a WHERE a.status=1 AND croe.serial_number = a.serial_number AND croe.profile in ("+ pro_file +") AND croe.profile is not null GROUP BY croe.profile,croe.serial_number";
							iLogger.info(" constructed Query : ---> " + selectQuery);
							System.out.println(" constructed Query : ---> " + selectQuery);
							
						}
						else{
							System.out.println("---- getProfileWiseFilter :  Default Selection  ----");							
							selectQuery = "SELECT  croe.serial_number, croe.profile, croe.lat, croe.lon, croe.tmh, a.status FROM com_rep_oem_enhanced croe, asset a WHERE a.status=1 AND croe.serial_number = a.serial_number AND croe.profile in ('Backhoe','Excavators','LoadAll','compactors','Wheel Loaders') AND croe.profile is not null GROUP BY croe.profile,croe.serial_number";
							iLogger.info(" constructed Query : ---> " + selectQuery);
							System.out.println(" constructed Query : ---> " + selectQuery);
							
						}
						rs = statement.executeQuery(selectQuery);							
						while(rs.next()){
							
							mappings = new HashMap<String, String>();							
							if(rs.getString("serial_number") != null){mappings.put("serial_number", rs.getString("serial_number"));
							}else{mappings.put("serial_number", "NA");}
							if(rs.getString("profile") != null){mappings.put("profile", rs.getString("profile"));
							}else{mappings.put("profile", "NA");}
							if(rs.getString("tmh") != null){mappings.put("tmh", rs.getString("tmh"));
							}else{mappings.put("tmh", "NA");}
							if(rs.getString("lat") != null){mappings.put("lat", rs.getString("lat"));							
							}else{mappings.put("lat","NA");}							
							if(rs.getString("lon") != null){mappings.put("lon", rs.getString("lon"));							
							}else{mappings.put("lon", "NA");}							
							getProfileWiseFilterDeatils.add(mappings);
						}
						
					}catch (Exception e) {
						e.printStackTrace();
						iLogger.info("Exception in fecthing getProfileWiseFilter");
					}finally {
					    try { rs.close(); } catch (Exception e) { e.printStackTrace(); }
					    try { statement.close(); } catch (Exception e) { e.printStackTrace(); }
					    try { connection.close(); } catch (Exception e) { e.printStackTrace(); }
					}
					return getProfileWiseFilterDeatils;
				}

				//Shajesh: 2021-04-15 : Additional Changes - Implementation for getting model count associated with a profile wise filer
				public List<HashMap<String,Map<String,String>>> getModelCountAssociatedWithProfile(List<String> profiles) {
					List<HashMap<String, Map<String, String>>> getModelCountAssociatedWithProfiledetails = new LinkedList<HashMap<String,Map<String, String>>>();
					Logger iLogger = InfoLoggerClass.logger;
					Connection connection = null;
					Statement statement = null;
					ResultSet rs = null;
					String selectQuery = null;
					HashMap<String, String> mappings = null;		
					String pro_file ="";		
					ListToStringConversion convert=new ListToStringConversion();
					if(profiles != null){pro_file =convert.getStringList(profiles).toString();
					}else{pro_file = null;}		
					iLogger.info("---------- profile--------------"+pro_file);	
					Map<String, Map<String, String>> map = new LinkedHashMap<String, Map<String, String>>();
					try {
						ConnectMySQL connMySql = new ConnectMySQL();
						connection = connMySql.getConnection();
						statement = connection.createStatement();
						if(pro_file != null && !pro_file.isEmpty() && pro_file.length() != 0){
							System.out.println(" ******* getModelCountAssociatedWithProfile ******** ->  Profile selection ");
							selectQuery = "SELECT croe.profile,croe.model, count(*), a.status FROM com_rep_oem_enhanced croe, asset a WHERE a.status=1 AND croe.serial_number = a.serial_number AND croe.profile in ("+ pro_file +") AND croe.profile is not null AND croe.model is not null GROUP BY croe.model";
							iLogger.info(" constructed Query:: ---> " + selectQuery);
											
						}else{
							System.out.println(" ******* getModelCountAssociatedWithProfile ******** ->  Default selection ");
							selectQuery = "SELECT croe.profile,croe.model, count(*), a.status FROM com_rep_oem_enhanced croe, asset a WHERE a.status=1 AND croe.serial_number = a.serial_number AND croe.profile in ('Backhoe','Excavators','LoadAll','compactors','Wheel Loaders') AND croe.profile is not null AND croe.model is not null GROUP BY croe.model";							
							iLogger.info(" constructed Query:: ---> " + selectQuery);
						}
						rs = statement.executeQuery(selectQuery);
						while(rs.next()){
							mappings = new HashMap<String, String>();
							if((rs.getString("profile") !=null && !rs.getString("profile").isEmpty()) && (rs.getString("model") !=null || !rs.getString("model").isEmpty())){
								mappings.put(rs.getString("model"),rs.getString("count(*)"));
								if(map.containsKey(rs.getString("profile"))){
									for(Map.Entry<String, String> val : map.get(rs.getString("profile")).entrySet()){
										mappings.put(val.getKey(), val.getValue());							
									}
									map.put(rs.getString("profile"), mappings);
								}else{
									map.put(rs.getString("profile"), mappings);	
								}
								
							}							
						}
						getModelCountAssociatedWithProfiledetails.add((HashMap<String, Map<String, String>>)map);
					}catch (Exception e) {
						e.printStackTrace();
						iLogger.info("Exception in fecthing getModelCountAssociatedWithProfile");
					}finally {
					    try { rs.close(); } catch (Exception e) { e.printStackTrace(); }
					    try { statement.close(); } catch (Exception e) { e.printStackTrace(); }
					    try { connection.close(); } catch (Exception e) { e.printStackTrace(); }
					}
					return getModelCountAssociatedWithProfiledetails;
				}*/


				//Shajesh: 2021-06-03 : Additional Changes - Implementation for getting model count associated with a profile and zone wise filer
				public List<HashMap<String,String>> getZoneWiseModelDetails(List<String> zone_codes, List<String> profiles) {
					List<HashMap<String, String>> getzonewisemodeldetails = new LinkedList<HashMap<String, String>>();
					Logger iLogger = InfoLoggerClass.logger;
					Connection connection = null;
					Statement statement = null;
					ResultSet rs = null;
					String selectQuery = null;
					HashMap<String, String> mappings = null;					
					String pro_file ="";	
					String zone_code ="";
					ListToStringConversion convert=new ListToStringConversion();
					if(profiles != null){pro_file =convert.getStringList(profiles).toString();
					}else{pro_file = null;}	
					if(zone_codes != null){zone_code =convert.getStringList(zone_codes).toString();
					}else{zone_code = null;}	
					iLogger.info("---------- profile--------------"+pro_file);	
					iLogger.info("---------- zone_code--------------"+zone_code);	
					//Map<String, Map<String, String>> map = new LinkedHashMap<String, Map<String, String>>();
					//Map<String, Map<String, Map<String, String>>> map1 = new LinkedHashMap<String, Map<String, Map<String, String>>>();
				
					try {
						ConnectMySQL connMySql = new ConnectMySQL();
						connection = connMySql.getConnection();
						statement = connection.createStatement();
						if((pro_file != null && !pro_file.isEmpty() && pro_file.length() != 0) 
								&&(zone_code != null && !zone_code.isEmpty() && zone_code.length() != 0)){
							iLogger.info(" ******* getZoneWiseModelDetails ******** ->  Profile and zone selection ");
							selectQuery = "SELECT croe.profile, croe.model, COUNT(*), croe.ZonalCode, a.status FROM com_rep_oem_enhanced croe, asset a WHERE a.status = 1 AND croe.serial_number = a.serial_number AND croe.profile IN ("+ pro_file +") AND croe.ZonalCode IN ("+ zone_code +") AND croe.model IS NOT NULL  GROUP BY croe.model , croe.profile , croe.ZonalCode";
							iLogger.info(" constructed Query:: ---> " + selectQuery);
						}else if(zone_code == null ||zone_code.isEmpty()||zone_code.length()==0 ){
							iLogger.info(" ******* getZoneWiseModelDetails ******** ->  Profile selection and Zone is null");
							selectQuery = "SELECT croe.profile, croe.model, COUNT(*), croe.ZonalCode, a.status FROM com_rep_oem_enhanced croe, asset a WHERE a.status = 1 AND croe.serial_number = a.serial_number AND croe.profile IN ("+ pro_file +")  AND  croe.model IS NOT NULL AND croe.ZonalCode IS NOT NULL GROUP BY croe.model , croe.profile , croe.ZonalCode";
							iLogger.info(" constructed Query:: ---> " + selectQuery);
						}
							rs = statement.executeQuery(selectQuery);
							while(rs.next()){
								mappings = new HashMap<String, String>();
									if(rs.getString("model") != null){mappings.put("model", rs.getString("model"));
									}else{mappings.put("model", "NA");}
									if(rs.getString("COUNT(*)") != null){mappings.put("count", rs.getString("COUNT(*)"));
									}else{mappings.put("count", "NA");}
									if(rs.getString("profile") != null){mappings.put("profile", rs.getString("profile"));
									}else{mappings.put("profile", "NA");}
									if(rs.getString("ZonalCode") != null){mappings.put("ZonalCode", rs.getString("ZonalCode"));
									}else{mappings.put("ZonalCode", "NA");}
									
									/*mappings.put(rs.getString("count(*)"),rs.getString("model"));
									if(map.containsKey(rs.getString("profile"))){
										for(Map.Entry<String, String> val : map.get(rs.getString("profile")).entrySet()){
											mappings.put(val.getKey(), val.getValue());							
										}
										map.put(rs.getString("profile"), mappings);
									}else{
										map.put(rs.getString("profile"), mappings);	
									}*/
									
									getzonewisemodeldetails.add(mappings);
									
									}
							System.out.println("**************** getzonewisemodeldetails *******************************"+getzonewisemodeldetails);							
					}catch (Exception e) {
						e.printStackTrace();
						iLogger.info("Exception in fecthing getModelCountAssociatedWithProfile");
					}finally {
					    try { rs.close(); } catch (Exception e) { e.printStackTrace(); }
					    try { statement.close(); } catch (Exception e) { e.printStackTrace(); }
					    try { connection.close(); } catch (Exception e) { e.printStackTrace(); }
					}
					return getzonewisemodeldetails;
					
				}

				//Shajesh: 2021-06-03 : Additional Changes - Implementation for getting model count associated with a profile and dealer wise filer
				public List<HashMap<String,String>> getDelaerWiseModelDetails(List<String> dealer_codes, List<String> profiles) {
					List<HashMap<String, String>> getdealerwisemodeldetails = new LinkedList<HashMap<String, String>>();
					Logger iLogger = InfoLoggerClass.logger;
					Connection connection = null;
					Statement statement = null;
					ResultSet rs = null;
					String selectQuery = null;
					HashMap<String, String> mappings = null;		
					String pro_file ="";	
					String dealer_code ="";
					ListToStringConversion convert=new ListToStringConversion();
					if(profiles != null){pro_file =convert.getStringList(profiles).toString();
					}else{pro_file = null;}	
					if(dealer_codes != null){dealer_code =convert.getStringList(dealer_codes).toString();
					}else{dealer_code = null;}	
					iLogger.info("---------- profile--------------"+pro_file);	
					iLogger.info("---------- zone_code--------------"+dealer_code);		
					//Map<String, Map<String, String>> map = new LinkedHashMap<String, Map<String, String>>();					
					try {
						ConnectMySQL connMySql = new ConnectMySQL();
						connection = connMySql.getConnection();
						statement = connection.createStatement();
						if((pro_file != null && !pro_file.isEmpty() && pro_file.length() != 0) &&(dealer_code != null && !dealer_code.isEmpty() && dealer_code.length() != 0)){
							iLogger.info(" ******* getDealerWiseModelDetails ******** ->  Profile and Dealer selection ");
							selectQuery = "SELECT croe.profile, croe.model, COUNT(*), croe.dealercode, a.status FROM com_rep_oem_enhanced croe, asset a WHERE a.status = 1 AND croe.serial_number = a.serial_number AND croe.profile IN ("+ pro_file +") AND croe.dealercode IN ("+ dealer_code +")  AND croe.model IS NOT NULL GROUP BY croe.model , croe.profile , croe.dealercode";
							iLogger.info(" constructed Query:: ---> " + selectQuery);							
							rs = statement.executeQuery(selectQuery);
							while(rs.next()){
								mappings = new HashMap<String, String>();								
									if(rs.getString("model") != null){mappings.put("model", rs.getString("model"));
									}else{mappings.put("model", "NA");}
									if(rs.getString("COUNT(*)") != null){mappings.put("count", rs.getString("COUNT(*)"));
									}else{mappings.put("count", "NA");}
									if(rs.getString("profile") != null){mappings.put("profile", rs.getString("profile"));
									}else{mappings.put("profile", "NA");}
									if(rs.getString("dealercode") != null){mappings.put("dealercode", rs.getString("dealercode"));
									}else{mappings.put("dealercode", "NA");}									

									/*mappings.put(rs.getString("count(*)"),rs.getString("model"));									
									if(map.containsKey(rs.getString("profile"))){
										for(Map.Entry<String, String> val : map.get(rs.getString("profile")).entrySet()){
											mappings.put(val.getKey(), val.getValue());							
										}
										map.put(rs.getString("profile"), mappings);
									}else{
										map.put(rs.getString("profile"), mappings);	
									}*/	
									
									getdealerwisemodeldetails.add(mappings);
									}
							}							
							System.out.println("**************** getdealerwisemodeldetails *******************************"+getdealerwisemodeldetails);		
										
					}catch (Exception e) {
						e.printStackTrace();
						iLogger.info("Exception in fecthing getModelCountAssociatedWithProfile");
					}finally {
					    try { rs.close(); } catch (Exception e) { e.printStackTrace(); }
					    try { statement.close(); } catch (Exception e) { e.printStackTrace(); }
					    try { connection.close(); } catch (Exception e) { e.printStackTrace(); }
					}
					
				return getdealerwisemodeldetails;
				}

				//Shajesh: 2021-06-03 : Additional Changes
				public List<HashMap<String,String>> getMachinesForMapUnderZoneAndProfile(List<String> zone_codes, List<String> profiles) {
					List<HashMap<String, String>> getmachinesformapunderzoneandprofile = new LinkedList<HashMap<String, String>>();
					Logger iLogger = InfoLoggerClass.logger;
					Connection connection = null;
					Statement statement = null;
					ResultSet rs = null;
					String selectQuery = null;
					HashMap<String, String> mappings = null;					
					String pro_file ="";	
					String zone_code ="";
					ListToStringConversion convert=new ListToStringConversion();
					if(profiles != null){pro_file =convert.getStringList(profiles).toString();
					}else{pro_file = null;}	
					if(zone_codes != null){zone_code =convert.getStringList(zone_codes).toString();
					}else{zone_code = null;}	
					iLogger.info("---------- profile--------------"+pro_file);	
					iLogger.info("---------- zone_code--------------"+zone_code);	
					//Map<String, Map<String, String>> map = new LinkedHashMap<String, Map<String, String>>();
					//Map<String, Map<String, Map<String, String>>> map1 = new LinkedHashMap<String, Map<String, Map<String, String>>>();
				
					try {
						ConnectMySQL connMySql = new ConnectMySQL();
						connection = connMySql.getConnection();
						statement = connection.createStatement();
						if((pro_file != null && !pro_file.isEmpty() && pro_file.length() != 0) 
								&&(zone_code != null && !zone_code.isEmpty() && zone_code.length() != 0)){
							iLogger.info(" ******* getMachinesForMapUnderZoneAndProfile ******** ->  Profile and zone selection ");
							selectQuery = "SELECT croe.serial_number,croe.ZonalCode,croe.profile,croe.lat, croe.lon,croe.tmh, a.status FROM com_rep_oem_enhanced croe, asset a WHERE a.status = 1 AND croe.serial_number = a.serial_number AND croe.profile IN ("+ pro_file +") AND croe.ZonalCode IN ("+ zone_code +") AND croe.model IS NOT NULL GROUP BY croe.serial_number , croe.profile , croe.ZonalCode";
							iLogger.info(" constructed Query:: ---> " + selectQuery);
						}else if(zone_code == null ||zone_code.isEmpty()||zone_code.length()==0 ){
							iLogger.info(" ******* getMachinesForMapUnderZoneAndProfile ******** ->  Profile selection and Zone is null");
							selectQuery = "SELECT croe.serial_number,croe.ZonalCode,croe.profile,croe.lat, croe.lon,croe.tmh, a.status FROM com_rep_oem_enhanced croe, asset a WHERE a.status = 1 AND croe.serial_number = a.serial_number AND croe.profile IN ("+ pro_file +")  AND croe.model IS NOT NULL AND croe.ZonalCode IS NOT NULL GROUP BY croe.serial_number , croe.profile , croe.ZonalCode";
							iLogger.info(" constructed Query:: ---> " + selectQuery);
						}
							rs = statement.executeQuery(selectQuery);
							while(rs.next()){
								mappings = new HashMap<String, String>();																							
									if(rs.getString("serial_number") != null){mappings.put("serial_number", rs.getString("serial_number"));
									}else{mappings.put("serial_number", "NA");}
									if(rs.getString("profile") != null){mappings.put("profile", rs.getString("profile"));
									}else{mappings.put("profile", "NA");}
									if(rs.getString("ZonalCode") != null){mappings.put("ZonalCode", rs.getString("ZonalCode"));
									}else{mappings.put("ZonalCode", "NA");}
									/*if(rs.getString("location") != null){mappings.put("location", rs.getString("location"));
									}else{mappings.put("location", "NA");}*/
									if(rs.getString("lat") != null){mappings.put("lat", rs.getString("lat"));
									}else{mappings.put("lat", "NA");}
									if(rs.getString("lon") != null){mappings.put("lon", rs.getString("lon"));
									}else{mappings.put("lon", "NA");}
									if(rs.getString("tmh") != null){mappings.put("tmh", rs.getString("tmh"));
									}else{mappings.put("tmh", "NA");}
									
									getmachinesformapunderzoneandprofile.add(mappings);
							}
							
							
							System.out.println("**************** getMachinesForMapUnderZoneAndProfile *******************************"+getmachinesformapunderzoneandprofile);							
					}catch (Exception e) {
						e.printStackTrace();
						iLogger.info("Exception in fecthing getMachinesForMapUnderZoneAndProfile");
					}finally {
					    try { rs.close(); } catch (Exception e) { e.printStackTrace(); }
					    try { statement.close(); } catch (Exception e) { e.printStackTrace(); }
					    try { connection.close(); } catch (Exception e) { e.printStackTrace(); }
					}
					return getmachinesformapunderzoneandprofile;
				}

				//Shajesh: 2021-06-03 : Additional Changes
				public List<HashMap<String,String>> getMachinesForMapUnderDealerAndProfile(List<String> dealer_codes, List<String> profiles) {
					List<HashMap<String, String>> getmachinesformapunderdealerandprofile = new LinkedList<HashMap<String, String>>();
					Logger iLogger = InfoLoggerClass.logger;
					Connection connection = null;
					Statement statement = null;
					ResultSet rs = null;
					String selectQuery = null;
					HashMap<String, String> mappings = null;		
					String pro_file ="";	
					String dealer_code ="";
					ListToStringConversion convert=new ListToStringConversion();
					if(profiles != null){pro_file =convert.getStringList(profiles).toString();
					}else{pro_file = null;}	
					if(dealer_codes != null){dealer_code =convert.getStringList(dealer_codes).toString();
					}else{dealer_code = null;}	
					iLogger.info("---------- profile--------------"+pro_file);	
					iLogger.info("---------- zone_code--------------"+dealer_code);		
					//Map<String, Map<String, String>> map = new LinkedHashMap<String, Map<String, String>>();
					//Map<String, Map<String, Map<String, String>>> map1 = new LinkedHashMap<String, Map<String, Map<String, String>>>();
					try {
						ConnectMySQL connMySql = new ConnectMySQL();
						connection = connMySql.getConnection();
						statement = connection.createStatement();
						if((pro_file != null && !pro_file.isEmpty() && pro_file.length() != 0) &&(dealer_code != null && !dealer_code.isEmpty() && dealer_code.length() != 0)){
							iLogger.info(" ******* getMachinesForMapUnderDealerAndProfile ******** ->  Profile and Dealer selection ");
							selectQuery = "SELECT croe.serial_number,croe.dealercode,croe.profile,croe.lat, croe.lon,croe.tmh, a.status FROM com_rep_oem_enhanced croe, asset a WHERE a.status = 1 AND croe.serial_number = a.serial_number AND croe.profile IN ("+ pro_file +") AND croe.dealercode IN ("+ dealer_code +") AND croe.model IS NOT NULL GROUP BY croe.serial_number , croe.profile , croe.dealercode";
							iLogger.info(" constructed Query:: ---> " + selectQuery);							
							rs = statement.executeQuery(selectQuery);
							while(rs.next()){
								mappings = new HashMap<String, String>();																							
								if(rs.getString("serial_number") != null){mappings.put("serial_number", rs.getString("serial_number"));
								}else{mappings.put("serial_number", "NA");}
								if(rs.getString("profile") != null){mappings.put("profile", rs.getString("profile"));
								}else{mappings.put("profile", "NA");}
								if(rs.getString("dealercode") != null){mappings.put("dealercode", rs.getString("dealercode"));
								}else{mappings.put("dealercode", "NA");}
								/*if(rs.getString("location") != null){mappings.put("location", rs.getString("location"));
								}else{mappings.put("location", "NA");}*/
								if(rs.getString("lat") != null){mappings.put("lat", rs.getString("lat"));
								}else{mappings.put("lat", "NA");}
								if(rs.getString("lon") != null){mappings.put("lon", rs.getString("lon"));
								}else{mappings.put("lon", "NA");}
								if(rs.getString("tmh") != null){mappings.put("tmh", rs.getString("tmh"));
								}else{mappings.put("tmh", "NA");}
								getmachinesformapunderdealerandprofile.add(mappings);
							}
							
							System.out.println("**************** getMachinesForMapUnderDealerAndProfile *******************************"+getmachinesformapunderdealerandprofile);		
						}
						
					}catch (Exception e) {
						e.printStackTrace();
						iLogger.info("Exception in fecthing getMachinesForMapUnderDealerAndProfile");
					}finally {
					    try { rs.close(); } catch (Exception e) { e.printStackTrace(); }
					    try { statement.close(); } catch (Exception e) { e.printStackTrace(); }
					    try { connection.close(); } catch (Exception e) { e.printStackTrace(); }
					}
					
				return getmachinesformapunderdealerandprofile;
				}
				
				
	public List<HashMap<String, Map<String, String>>> getOrgWiseOutletDetails(List<String> orgUnits, List<String> outlet_types, List<String> zones, List<String> dealers) {
					List<HashMap<String, Map<String, String>>> getOrgWiseOutletDetails = new LinkedList<HashMap<String,Map<String, String>>>();
					Connection connection = null;
					Statement statement = null;
					ResultSet rs = null;
					String selectQuery = null;
					Map<String, String> mappings = null;	
					String orgUnit=null;
					String outlet_type=null;
					String zone=null;
					String dealer=null;
					ListToStringConversion convert=new ListToStringConversion();
					if(orgUnits != null){orgUnit =convert.getStringList(orgUnits).toString();}else{orgUnit = null;}
					if(outlet_types != null){outlet_type =convert.getStringList(outlet_types).toString();}else{outlet_type = null;}
					if(zones != null){zone =convert.getStringList(zones).toString();}else{zone = null;}
					if(dealers != null){dealer =convert.getStringList(dealers).toString();}else{dealer = null;}
					Map<String, Map<String, String>> map = new LinkedHashMap<String, Map<String, String>>();
					try{
						ConnectMySQL connMySql = new ConnectMySQL();
						connection = connMySql.getConnection();
						statement = connection.createStatement();
						selectQuery="select org_unit, outlet_type, count(*) from dealer_outlet_mapping where org_unit in ("+ orgUnit +") ";
						if(outlet_type!=null){
								selectQuery=selectQuery + "  and outlet_type in ("+ outlet_type +") " ;
						}
						if(zone!=null){
							selectQuery=selectQuery+ "  and  zone in ("+ zone +")";
							}
						if(dealer!=null){
							selectQuery=selectQuery+ "  and  dealer in ("+ dealer +")";
						}
						selectQuery=selectQuery+" group by org_unit,outlet_type ";
						iLogger.info("DOutLetMappingImpl ::   getOrgWiseOutletDetails:: constructed Query : ---> " + selectQuery);
						System.out.println(selectQuery);
						rs = statement.executeQuery(selectQuery);	
						while(rs.next()){					
							mappings = new HashMap<String, String>();
							if(rs.getString("outlet_type") !=null){						
								mappings.put(rs.getString("outlet_type"), rs.getString("count(*)"));
							if(map.containsKey(rs.getString("org_unit"))){
								for(Map.Entry<String, String> val : map.get(rs.getString("org_unit")).entrySet()){
									mappings.put(val.getKey(), val.getValue());							
								}
								map.put(rs.getString("org_unit"), mappings);
							}else{
								map.put(rs.getString("org_unit"), mappings);	
							}
							}					
						}	
						getOrgWiseOutletDetails.add((HashMap<String, Map<String, String>>) map);	
					}
					catch (Exception e) {
						iLogger.info(" DOutLetMappingImpl::getOrgWiseOutletDetails::Execption in getOrgWiseOutletDetails()");
						e.printStackTrace();
					}finally {
					    try { rs.close(); } catch (Exception e) { e.printStackTrace(); }
					    try { statement.close(); } catch (Exception e) { e.printStackTrace(); }
					    try { connection.close(); } catch (Exception e) { e.printStackTrace(); }
					}
					return getOrgWiseOutletDetails;
				}
	
	public List<HashMap<String, Map<String, String>>> getOrgWiseZonalDetails(
			List<String> orgUnits, List<String> zones, List<String> dealers, List<String> outlet_types) {
		List<HashMap<String, Map<String, String>>> getOrgWiseZonalDetails = new LinkedList<HashMap<String,Map<String, String>>>();
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;
		String selectQuery = null;
		Map<String, String> mappings = null;	
		String orgUnit=null;
		String zone=null;
		String dealer=null;
		String outlet_type=null;
		ListToStringConversion convert=new ListToStringConversion();
		if(orgUnits != null){orgUnit =convert.getStringList(orgUnits).toString();}else{orgUnit = null;}
		if(zones != null){zone =convert.getStringList(zones).toString();}else{zone = null;}
		if(dealers != null){dealer =convert.getStringList(dealers).toString();}else{dealer = null;}
		if(outlet_types != null){outlet_type =convert.getStringList(outlet_types).toString();}else{outlet_type = null;}
		Map<String, Map<String, String>> map = new LinkedHashMap<String, Map<String, String>>();
		try{
			ConnectMySQL connMySql = new ConnectMySQL();
			connection = connMySql.getConnection();
			statement = connection.createStatement();
			selectQuery="select org_unit, zone, count(*) from dealer_outlet_mapping  where org_unit in ("+ orgUnit +") ";
			if(zone!=null){
				selectQuery=selectQuery+ "  and  zone in ("+ zone +")";
				}
			if(dealer!=null){
				selectQuery=selectQuery+ "  and  dealer in ("+ dealer +")";
			}
			
			if(outlet_type!=null){
				selectQuery=selectQuery + "  and outlet_type in ("+ outlet_type +") " ;
			}
			selectQuery=selectQuery+" group by org_unit,zone ";
			iLogger.info("DOutLetMappingImpl ::   getOrgWiseZonalDetails:: constructed Query : ---> " + selectQuery);
			System.out.println(selectQuery);
			rs = statement.executeQuery(selectQuery);	
			while(rs.next()){					
				mappings = new HashMap<String, String>();
				if(rs.getString("zone") !=null){						
					mappings.put(rs.getString("zone"), rs.getString("count(*)"));
				if(map.containsKey(rs.getString("org_unit"))){
					for(Map.Entry<String, String> val : map.get(rs.getString("org_unit")).entrySet()){
						mappings.put(val.getKey(), val.getValue());							
					}
					map.put(rs.getString("org_unit"), mappings);
				}else{
					map.put(rs.getString("org_unit"), mappings);	
				}
				}					
			}	
			getOrgWiseZonalDetails.add((HashMap<String, Map<String, String>>) map);	
		
		}
		catch (Exception e) {
			iLogger.info(" DOutLetMappingImpl::getOrgWiseZonalDetails::Execption in getOrgWiseZonalDetails()");
			e.printStackTrace();
		}finally {
		    try { rs.close(); } catch (Exception e) { e.printStackTrace(); }
		    try { statement.close(); } catch (Exception e) { e.printStackTrace(); }
		    try { connection.close(); } catch (Exception e) { e.printStackTrace(); }
		}
		return getOrgWiseZonalDetails;
	}
	public  List<HashMap<String, String>> getOrgWiseOutletDetailsForMap(List<String> orgUnits, List<String> zones, List<String> dealers, List<String> outlet_types) {
		List<HashMap<String, String>>  getOrgWiseOutletDetailsForMap = new  ArrayList<HashMap<String, String>> ();
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;
		String selectQuery = null;
		Map<String, String> mappings = null;	
		String orgUnit=null;
		String zone=null;
		String dealer=null;
		String outlet_type=null;
		ListToStringConversion convert=new ListToStringConversion();
		if(orgUnits != null){orgUnit =convert.getStringList(orgUnits).toString();}else{orgUnit = null;}
		if(zones != null){zone =convert.getStringList(zones).toString();}else{zone = null;}
		if(dealers != null){dealer =convert.getStringList(dealers).toString();}else{dealer = null;}
		if(outlet_types != null){outlet_type =convert.getStringList(outlet_types).toString();}else{outlet_type = null;}
		try{
			ConnectMySQL connMySql = new ConnectMySQL();
			connection = connMySql.getConnection();
			statement = connection.createStatement();
			//selectQuery="select distinct( outlet_type) from dealer_outlet_mapping  where org_unit in ("+ orgUnit +") ";  
			 selectQuery="select org_unit,dealer,outlet_type,address,location,lat,lon from dealer_outlet_mapping  where org_unit in ("+ orgUnit +") ";  
				if(zone!=null){
				selectQuery=selectQuery+ "  and  zone in ("+ zone +")";
				}
				if(dealer!=null){
					selectQuery=selectQuery+ "  and  dealer in ("+ dealer +")";
				}
				if(outlet_type!=null){
					selectQuery=selectQuery+ "  and  outlet_type in ("+ outlet_type +")";
				}
			iLogger.info("DOutLetMappingImpl ::   getOrgWiseOutletDetailsForMap:: constructed Query : ---> " + selectQuery);
			rs = statement.executeQuery(selectQuery);
			while(rs.next()){
				mappings = new HashMap<String, String>();
				if(rs.getString("org_unit") != null){
					mappings.put("org_unit", rs.getString("org_unit"));
				}else{
					mappings.put("org_unit", "NA");
				}
				if(rs.getString("dealer") != null){
					mappings.put("dealer", rs.getString("dealer"));
				}else{
					mappings.put("dealer", "NA");
				}
				if(rs.getString("outlet_type") != null){
					mappings.put("outlet_type", rs.getString("outlet_type"));
				}else{
					mappings.put("outlet_type", "NA");
				}
				if(rs.getString("address") != null){
					mappings.put("address", rs.getString("address").replace("–", ""));
				}else{
					mappings.put("address", "NA");
				}
				
				if(rs.getString("location") != null){
					mappings.put("location", rs.getString("location"));
				}else{
					mappings.put("location", "NA");
				}
				if(rs.getString("lat") != null){
					mappings.put("lat", rs.getString("lat"));
				}else{
					mappings.put("lat", "NA");
				}
				if(rs.getString("lon") != null){
					mappings.put("lon", rs.getString("lon"));
				}else{
					mappings.put("lon", "NA");
				}
				getOrgWiseOutletDetailsForMap.add((HashMap<String, String>) mappings);
			}
		}
		catch (Exception e) {
			iLogger.info(" DOutLetMappingImpl::getOrgWiseOutletDetailsForMap::Execption in getOrgWiseOutletDetailsForMap()");
			e.printStackTrace();
		}finally {
		    try { rs.close(); } catch (Exception e) { e.printStackTrace(); }
		    try { statement.close(); } catch (Exception e) { e.printStackTrace(); }
		    try { connection.close(); } catch (Exception e) { e.printStackTrace(); }
		}
		return getOrgWiseOutletDetailsForMap;
	}
			
				
}
	
	
