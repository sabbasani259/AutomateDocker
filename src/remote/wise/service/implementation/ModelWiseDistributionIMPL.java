package remote.wise.service.implementation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.ConnectMySQL;
import scala.collection.mutable.LinkedHashMap;

//CR448 : 20231127 : prasad : Model Wise Distribution on the Overview Map 
public class ModelWiseDistributionIMPL {
	
	public List<String> getZoneDetails() {
		String status = "SUCCESS";
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		String query = "select distinct Zone from com_rep_oem_enhanced";
		List<String> zoneList = new ArrayList<>();
		ConnectMySQL connFactory = new ConnectMySQL();
		try (Connection connection = connFactory.getConnection(); Statement statement = connection.createStatement()) {
			ResultSet rs = statement.executeQuery(query);
			while (rs.next()) {
				if( null != rs.getString("Zone") ){
				String zone = rs.getString("Zone");
				zoneList.add(zone);
				}
			}
		} catch (Exception e) {
			fLogger.fatal("ZoneDetails()::issue while Fecthing zonedeatils from DB " + e.getMessage());
			status = "FAILURE";
		}
		return zoneList;
	}

	public  List<String> getDealerNames(String zone) {
		String status="SUCCESS";
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		String zone1 = null;;
		if(null != zone && zone.length() > 1 )
		    zone1  = "'not null'";
		String query = "select distinct Dealer_Name from com_rep_oem_enhanced  where ( "+zone1+" is null ||   zone in ("+zone+"))";
		
		iLogger.info("querry "+ query);
		List<String> dealerNameList = new ArrayList<>();
		ConnectMySQL connFactory = new ConnectMySQL();
		try (Connection connection = connFactory.getConnection();
			Statement statement = connection.createStatement()) {
		ResultSet rs = statement.executeQuery(query);
		while(rs.next())
		{
			if( null != rs.getString("Dealer_Name") ){
				String Dealer_Name = rs.getString("Dealer_Name");
				dealerNameList.add(Dealer_Name);
				}
		}
		}catch (Exception e) {
			fLogger.fatal("DealerNames()::issue while Fecthing DealerNames from DB "
					+ e.getMessage());
			status="FAILURE";
		}
		return dealerNameList;
	}
	
	public  List<String> getCustomerNames(String zone , String dealerName ) {
		String status="SUCCESS";
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		String zone1  = null;
		if(null != zone && zone.length() > 1 )
	    zone1 = "'not null'";
		String dealerName1 = null;
		if(null != dealerName && dealerName.length() > 1 )
			dealerName1  = "'not null'";
		String query =   "select distinct CustomerName from com_rep_oem_enhanced  where  ( ("+zone1+" is null ||   zone in ("+zone+")) and   ("+dealerName1+" is null ||   Dealer_Name in ("+dealerName+")))";
	
		iLogger.info("querry "+ query);
		List<String> customerNameList = new ArrayList<>();
		
		ConnectMySQL connFactory = new ConnectMySQL();
		try (Connection connection = connFactory.getConnection();
			Statement statement = connection.createStatement()) {
		ResultSet rs = statement.executeQuery(query);
		while(rs.next())
		{
			if( null != rs.getString("CustomerName") ){
				String CustomerName = rs.getString("CustomerName");
				customerNameList.add(CustomerName);
				}
			
		}
		}catch (Exception e) {
			fLogger.fatal("CustomerNames()::issue while Fecthing CustomerNames from DB "
					+ e.getMessage());
			status="FAILURE";
		}
		return customerNameList;
	}
	
	public  List<String> getProfileNames() {
		String status="SUCCESS";
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		String query =" select distinct Profile from com_rep_oem_enhanced";
		List<String> profileList = new ArrayList<>();
		ConnectMySQL connFactory = new ConnectMySQL();
		try (Connection connection = connFactory.getConnection();
			Statement statement = connection.createStatement()) {
		ResultSet rs = statement.executeQuery(query);
		while(rs.next())
			{
				if (null != rs.getString("Profile")) {
					String Profile = rs.getString("Profile");
					profileList.add(Profile);
				}

			}
		}catch (Exception e) {
			fLogger.fatal("Profile()::issue while Fecthing Profile from DB "
					+ e.getMessage());
			status="FAILURE";
		}
		return profileList;
	}
	
	public  List<String> getVersionsList(String fwSlipt) {
		String status="SUCCESS";
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		String fwSlipt1 = null;;
		if(null != fwSlipt && fwSlipt.length() > 1 )
			fwSlipt1   = "'not null'";
		String query =" SELECT distinct  version , fw FROM (SELECT *, "
				+ " CASE when version >= 0  and  version < 30 THEN 'LL2'"
				+ " when version >= 30  and  version < 50 THEN 'LL4'"
				+ " when version >= 50   THEN 'LL Lite' END AS fw "
				+ " FROM com_rep_oem_enhanced) AS temp  where ( "+fwSlipt1+" is null || fw in("+fwSlipt+")) order by version";
		List<String> versionList = new ArrayList<>();
		ConnectMySQL connFactory = new ConnectMySQL();
		try (Connection connection = connFactory.getConnection();
			Statement statement = connection.createStatement()) {
		ResultSet rs = statement.executeQuery(query);
		while(rs.next())
			{
				if (null != rs.getString("version")) {
					String version = rs.getString("version");
					versionList.add(version);
				}

			}
		}catch (Exception e) {
			fLogger.fatal("getVersionsList()::issue while Fecthing version from DB "
					+ e.getMessage());
			status="FAILURE";
		}
		return versionList;
	}
	
	public  List<String> getModelNames(String Profile) {
		String status="SUCCESS";
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		String Profile1 = null;
		if(null != Profile && Profile.length() > 1 )
			Profile1   = "'not null'";
		String query = " select distinct Model from com_rep_oem_enhanced  where ( "+Profile1+" is null ||   Profile  in ("+Profile+"))";
		iLogger.info("querry "+ query);
		List<String> modelList = new ArrayList<>();
		ConnectMySQL connFactory = new ConnectMySQL();
		try (Connection connection = connFactory.getConnection();
			Statement statement = connection.createStatement()) {
		ResultSet rs = statement.executeQuery(query);
		while(rs.next())
		{
			if (null != rs.getString("Model")) {
				String Model = rs.getString("Model");
				modelList.add(Model);
			}
			
		}
		}catch (Exception e) {
			fLogger.fatal("ModelNames()::issue while Fecthing ModelNames from DB "
					+ e.getMessage());
			status="FAILURE";
		}
		return modelList;
	}
	
	public List<Map<String, String>> getLatAndLonDetails(String zone, String dealerName, String coustmer,
			String profile, String model , String simTypeCheckFlag , String  FwSlipt , String connectivity , String version , String pageNumber ,String pageSize) {
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		
			
		String zone1 = null;
		if (null != zone && zone.length() > 1)
			zone1 = "'not null'";
		String dealerName1 = null;
		if (null != dealerName && dealerName.length() > 1)
			dealerName1 = "'not null'";
		String Profile1 = null;
		if (null != profile && profile.length() > 1)
			Profile1 = "'not null'";
		String coustmer1 = null;
		if (null != coustmer && coustmer.length() > 1)
			coustmer1 = "'not null'";
		String model1 = null;
		if (null != model && model.length() > 1)
			model1 = "'not null'";

		
		String connectivity1 = null;
		if (null != connectivity && connectivity.length() > 1)
			connectivity1 = "'not null'";
		
		String simTypeCheckFlag1 = null;
		if (null != simTypeCheckFlag && simTypeCheckFlag.length() > 1)
			simTypeCheckFlag1 = "'not null'";
		
		String FwSlipt1 = null;
		if (null != FwSlipt && FwSlipt.length() > 1)
			FwSlipt1 = "'not null'";
		
		String vrsion1 = null;
		if (null != version && version.length() > 1)
			vrsion1 = "'not null'";
		
	
		if (null == pageNumber || pageNumber.isEmpty())
			pageNumber = "0";

		if (null == pageSize || pageSize.isEmpty())
			pageSize = "5000";
//		String query = "select Serial_Number, Lat , Lon from  com_rep_oem_enhanced where  ( (" + zone1
//				+ " is null ||   zone in ("+zone+")) and  " + " (" + dealerName1 + " is null ||   Dealer_Name in ("+dealerName+")) "
//				+ " and (" + coustmer1 + " is null ||   CustomerName  in ("+coustmer+")) and   ("
//				+ Profile1 + " is null ||   Profile in ("+profile+"))" + " and   (" + model1 + " is null ||   Model in ("+model+")) and ("+vi+" is null || SIM_No like '"+vi+"%')"
//				+ " and ("+jio+" is null || SIM_No like '"+jio+"%')  and ("+airtel+" is null || SIM_No like '"+airtel+"%') "
//				+ " and ( version >= "+start+"  and  version < "+end+") and ( "+connectivity1+" is null || Pkt_Recd_TS >= '"+connectivity+"'))";
		
//		String countQuery = "select count(Serial_Number) as countSN from (SELECT *, "
//				+ " CASE WHEN SIM_No like '405%' THEN 'Jio' WHEN SIM_No like '40411%' THEN 'Vi' WHEN SIM_No like '40410%' THEN 'Airtel'"
//				+ " when SIM_No not LIKE '40410%' and SIM_No not LIKE '40411%'  and SIM_No not LIKE '405%' THEN 'Others' END AS simtype ,"
//                +" CASE  when version >= 0  and  version < 30 THEN 'LL2' when version >= 30  and  version < 50 THEN 'LL4' when version >= 50   THEN 'Lite'  END AS fw"
//                + "  FROM com_rep_oem_enhanced) as temp where  ( (" + zone1 + " is null ||   zone in ("+zone+")) and  "
//				 + " (" + dealerName1 + " is null ||   Dealer_Name in ("+dealerName+")) and "
//				+ "  (" + coustmer1 + " is null ||   CustomerName  in ("+coustmer+")) and   ("
//				+ Profile1 + " is null ||   Profile in ("+profile+"))" + " and   (" + model1 + " is null ||   Model in ("+model+")) "
//				+" and ("+ simTypeCheckFlag1+" is null ||  simtype in ("+simTypeCheckFlag+")) " 
//				+" and ("+ FwSlipt1+" is null ||  fw in ("+FwSlipt+")) " 
//				+" and ("+ vrsion1+" is null ||  version in ("+version+")) " 
//				+ " and ( "+connectivity1+" is null || Pkt_Recd_TS >= '"+connectivity+"'))";
		
		
		String query = "select Serial_Number,Lat,Lon  from (SELECT *, "
				+ " CASE WHEN SIM_No like '405%' THEN 'Jio' WHEN SIM_No like '40411%' THEN 'Vi' WHEN SIM_No like '40410%' THEN 'Airtel'"
				+ " when SIM_No not LIKE '40410%' and SIM_No not LIKE '40411%'  and SIM_No not LIKE '405%' THEN 'Others' END AS simtype ,"
                +" CASE  when version >= 0  and  version < 30 THEN 'LL2' when version >= 30  and  version < 50 THEN 'LL4' when version >= 50   THEN 'Lite'  END AS fw"
                + "  FROM com_rep_oem_enhanced) as temp where  ( (" + zone1 + " is null ||   zone in ("+zone+")) and  "
				 + " (" + dealerName1 + " is null ||   Dealer_Name in ("+dealerName+")) and "
				+ "  (" + coustmer1 + " is null ||   CustomerName  in ("+coustmer+")) and   ("
				+ Profile1 + " is null ||   Profile in ("+profile+"))" + " and   (" + model1 + " is null ||   Model in ("+model+")) "
				+" and ("+ simTypeCheckFlag1+" is null ||  simtype in ("+simTypeCheckFlag+")) " 
				+" and ("+ FwSlipt1+" is null ||  fw in ("+FwSlipt+")) " 
				+" and ("+ vrsion1+" is null ||  version in ("+version+")) " 
				+ " and ( "+connectivity1+" is null || Pkt_Recd_TS >= '"+connectivity+"')) limit "+pageNumber+","+pageSize;
		
	//	Map<String,Object> respMap = new HashMap();
		iLogger.info("querry : " + query);
		ConnectMySQL connFactory = new ConnectMySQL();
		
//		try (Connection connection = connFactory.getConnection(); Statement statement = connection.createStatement()) {
//			ResultSet countRs = statement.executeQuery(countQuery);
//			while (countRs.next()) {
//				if (null != countRs.getString("countSN")) {
////					Map<String, String> countMap = new HashMap<>();
////					countMap.put("totalCount", countRs.getString("countSN"));
//					respMap.put("totalCount", countRs.getString("countSN"));
//				}
//			}
//		} catch (Exception e) {
//			fLogger.fatal("ModelNames()::issue while Fecthing count of Serial numbers from DB " + e.getMessage());
//		}
		
		List<Map<String, String>> dataList = new ArrayList<Map<String,String>>();
		try (Connection connection = connFactory.getConnection(); Statement statement = connection.createStatement()) {
			ResultSet rs = statement.executeQuery(query);
			while (rs.next()) {
				if (null != rs.getString("Serial_Number")) {
					Map<String, String> latAndLonMap = new HashMap<>();
					latAndLonMap.put("vin", rs.getString("Serial_Number"));
					latAndLonMap.put("Lat", rs.getString("Lat"));
					latAndLonMap.put("Lon", rs.getString("Lon"));
					dataList.add(latAndLonMap);
				
				}
			}
		//	respMap.put("Data", dataList);
		} catch (Exception e) {
			fLogger.fatal("ModelNames()::issue while Fecthing ModelNames from DB " + e.getMessage());
		}
		return  dataList;
	}

}
