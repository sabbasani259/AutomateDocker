package remote.wise.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;


import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.ConnectMySQL;

public class DataForUnallocatedMachineDao {
	

    private Logger iLogger = InfoLoggerClass.logger;
    private Logger fLogger = FatalLoggerClass.logger;
    private ConnectMySQL connFactory = new ConnectMySQL();

 

    public List<Map<String,Object>> fetchDataFromDB() {
        String query ="select com_rep_oem_enhanced.* , asset_profile.usgaeCategory from asset_profile join com_rep_oem_enhanced on asset_profile.serialNumber=com_rep_oem_enhanced.Serial_Number where  com_rep_oem_enhanced.owner='unallocated' or plant not in ('HAR','PUN','RAJ');";
        iLogger.info(query);
        List<Map<String,Object>> list=new ArrayList<>();
        Map<String, Object> tableMap = null;
        
        
        try (Connection connection = connFactory.getConnection();
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet rs = statement.executeQuery(query);) {
        	
            while (rs.next()) {
                tableMap = new LinkedHashMap<String, Object>();
                tableMap.put("Serial_Number", rs.getString("Serial_Number"));
                tableMap.put("Profile",rs.getString("Profile"));
                tableMap.put("Model",rs.getString("Model"));
                tableMap.put("Zone",rs.getString("Zone"));
                tableMap.put("Owner",rs.getString("Owner"));
                tableMap.put("Machines hours",rs.getString("tmh"));
                tableMap.put("version",rs.getString("version"));
                tableMap.put("Pkt_Created_TS",rs.getString("Pkt_Created_TS"));
                tableMap.put("Pkt_Recd_TS",rs.getString("Pkt_Recd_TS"));
                tableMap.put("Roll_Off_Date",rs.getString("Roll_Off_Date"));
                tableMap.put("Installed_date",rs.getString("Installed_date"));
                tableMap.put("Lat",rs.getString("Lat"));
                tableMap.put("Lon",rs.getString("Lon"));
                tableMap.put("City",rs.getString("City"));
                tableMap.put("State",rs.getString("State"));
                tableMap.put("device_status",rs.getString("device_status"));
                tableMap.put("SIM_No",rs.getString("SIM_No"));
                String networkprovider = null;
              
				if (!rs.getString("SIM_NO").equalsIgnoreCase("NA") && rs.getString("SIM_No") != null  ) {
				String simTypeCheckFlag = rs.getString("SIM_No").substring(0, 5);
				
				iLogger.info(simTypeCheckFlag);
				
				if (simTypeCheckFlag.contains("40410"))
					tableMap.put("Network Provider",networkprovider = "Vi");
				else if (simTypeCheckFlag.contains("40410"))
					tableMap.put("Network Provider",networkprovider = "Airtel"); 
				else if (simTypeCheckFlag.contains("405"))
					tableMap.put("Network Provider",networkprovider = "Jio");
				else
					tableMap.put("Network Provider",networkprovider ="Others"); 
				}
				else{
					tableMap.put("Network Provider",networkprovider ="NA");
				}
                
//				String meachinetype = null;//CR465.so
//				
//				if (!rs.getString("version").equalsIgnoreCase("NA") && rs.getString("version") != null  ) {
//					String v = version.split("\\.")[0];
//			       
//					int meachinetypeCheckFlag = Integer.parseInt(v);
//                     //MEID100007576-prasad-20230516.n
//					if (meachinetypeCheckFlag >= 30 && meachinetypeCheckFlag < 50) {
//						meachinetype = "LL4";
//
//					}  //MEID100007576-prasad-20230516.n
//					else if (meachinetypeCheckFlag >= 50) {
//						meachinetype = "LL Lite";
//					} else {
//						meachinetype = "LL2";
//					}
//				} else {
//					meachinetype = "NA";
//				}//CR465.eo
				
				String machineType=null;//CR465.sn
				if ( rs.getString("version") != null && !rs.getString("version").equalsIgnoreCase("NA"))
				{
					
					iLogger.info(rs.getString("version"));
					String versionParts = rs.getString("version").split("\\.")[0];
					int machinetypeCheckFlag= Integer.parseInt(versionParts);
					
					if(machinetypeCheckFlag < 30)
					{
						tableMap.put("machineType",machineType="LL2");
					}
					else if (machinetypeCheckFlag >= 30 && machinetypeCheckFlag < 50) {
						
						tableMap.put("machineType",machineType = "LL4");
					}  
					else if (machinetypeCheckFlag == 50) {
						tableMap.put("machineType",machineType = "LL Lite");
					} else if (machinetypeCheckFlag > 50) {
						tableMap.put("machineType",machineType = "LL4");
					}
				} else {
					tableMap.put("machineType",machineType = "NA");
				}//CR465.en
                tableMap.put("renew_state",rs.getString("renew_state"));
                tableMap.put("country",rs.getString("country"));
                tableMap.put("plant",rs.getString("plant"));
                tableMap.put("extended_warranty",rs.getString("extended_warranty"));
                tableMap.put("Comm_State",rs.getString("Comm_State"));
                tableMap.put("Comm_District",rs.getString("Comm_District"));
                tableMap.put("Comm_City",rs.getString("Comm_City"));
                tableMap.put("Comm_Address",rs.getString("Comm_Address"));
               
                tableMap.put("NIP",rs.getString("NIP"));
                tableMap.put("version",rs.getString("version"));
                tableMap.put("BP_code",rs.getString("BP_code"));
                tableMap.put("Altitude in Meters",rs.getString("altitude"));
                tableMap.put("Customer Contact No",rs.getString("Customer_Mobile"));
                String usgaeCategory=rs.getString("usgaeCategory"); 
                if ("0".equals(usgaeCategory) || usgaeCategory == null || usgaeCategory.trim().isEmpty()) {
                    usgaeCategory = "NA";
                }
                 
                tableMap.put("usgaeCategory", usgaeCategory);
                 list.add(tableMap);
                 iLogger.info(tableMap);
             
            }
        } catch (Exception e) {
        	e.printStackTrace();
            fLogger.fatal(e.getMessage());
            fLogger.info(e.getMessage());
                    }
        return list;
    }

}
