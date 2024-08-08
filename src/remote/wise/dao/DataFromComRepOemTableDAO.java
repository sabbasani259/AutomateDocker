package remote.wise.dao;

 

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.Map;

 

import org.apache.logging.log4j.Logger;

 

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.ConnectMySQL;

 

public class DataFromComRepOemTableDAO {

 

    private Logger iLogger = InfoLoggerClass.logger;
    private Logger fLogger = FatalLoggerClass.logger;
    private ConnectMySQL connFactory = new ConnectMySQL();

 

    public Map<String, Map<String, Object>> fetchDataFromDB(String vins) {
        String query = "select Serial_Number,tmh,Pkt_Created_TS,Pkt_Recd_TS,"
                + "Roll_Off_Date,device_status,SIM_No,Installed_date,BP_code,Renew_state,Country,Owner from com_rep_oem where Serial_Number in("
                + vins + ")";
        iLogger.info(query);
        Map<String, Map<String, Object>> map = null;

 

        try (Connection connection = connFactory.getConnection();
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet rs = statement.executeQuery(query);) {

 

            map = new LinkedHashMap<String, Map<String, Object>>();
            Map<String, Object> tableMap = null;
            while (rs.next()) {
                tableMap = new LinkedHashMap<String, Object>();
                tableMap.put("Serial_Number", rs.getString("Serial_Number"));
                tableMap.put("tmh", rs.getString("tmh"));
                if(rs.getString("Pkt_Created_TS")!=null)
                tableMap.put("Pkt_Created_TS", rs.getString("Pkt_Created_TS").split("\\.")[0]);
                else
                tableMap.put("Pkt_Created_TS", rs.getString("Pkt_Created_TS"));
                if(rs.getString("Pkt_Recd_TS")!=null)
                tableMap.put("Pkt_Recd_TS", rs.getString("Pkt_Recd_TS").split("\\.")[0]);
                else
                tableMap.put("Pkt_Recd_TS", rs.getString("Pkt_Recd_TS"));    
                if(rs.getString("Roll_Off_Date")!=null)
                tableMap.put("Roll_Off_Date", rs.getString("Roll_Off_Date").split("\\.")[0]);
                else
                tableMap.put("Roll_Off_Date", rs.getString("Roll_Off_Date"));    
                tableMap.put("device_status", rs.getString("device_status"));
                tableMap.put("SIM_No", rs.getString("SIM_No"));
                if(rs.getString("Installed_date")!=null)
                tableMap.put("Installed_date", rs.getString("Installed_date").split("\\.")[0]);
                else
                tableMap.put("Installed_date", rs.getString("Installed_date"));
                tableMap.put("BP_code", rs.getString("BP_code"));
                tableMap.put("Renew_state", rs.getString("Renew_state"));
                tableMap.put("Country", rs.getString("Country"));
                tableMap.put("Owner", rs.getString("Owner"));

 

                map.put(rs.getString("Serial_Number"), tableMap);
            }

 

        } catch (Exception e) {
            fLogger.fatal(e.getMessage());
            fLogger.info(e.getMessage());
                    }
        return map;
    }
}