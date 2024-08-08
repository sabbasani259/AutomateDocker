package remote.wise.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.logging.log4j.Logger;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.ConnectMySQL;

public class UserNotLoggedDataDao {
	 private Logger iLogger = InfoLoggerClass.logger;
	    private Logger fLogger = FatalLoggerClass.logger;
	    private ConnectMySQL connFactory = new ConnectMySQL();

	    public  List<LinkedHashMap<String, Object>> getUserNotLoggedDataFromDB(String accountFilter, String accountIdList) {
	    	   String query ="";
	    	   
	    	if((accountFilter==null && accountIdList==null) ||
	    			accountFilter.equalsIgnoreCase("null") && accountIdList.equalsIgnoreCase("null")){
	    		query = "select * from userNotLoggedDetails ";
	    	}
	    	else{
	    		query = "select * from userNotLoggedDetails where  " +accountFilter +"  in  (" + accountIdList+")";
	    	}
	        iLogger.info(query);
	        System.out.println(query);
	        List<LinkedHashMap<String, Object>> mapList = new ArrayList<>();

	 

	        try (Connection connection = connFactory.getConnection();
	                PreparedStatement statement = connection.prepareStatement(query);
	                ResultSet rs = statement.executeQuery(query);) {

	 

	            LinkedHashMap<String, Object> tableMap = null;
	            while (rs.next()) {
	                tableMap = new LinkedHashMap<String, Object>();
	                if(rs.getString("contact_id")!=null && !rs.getString("contact_id").isEmpty()){
	                tableMap.put("contact_id", rs.getString("contact_id"));
	                }
	                else{ tableMap.put("contact_id","NULL");}
	                
	                if(rs.getString("first_name")!=null  && !rs.getString("first_name").isEmpty()){
	                tableMap.put("first_name", rs.getString("first_name"));
	                }
	                else{ tableMap.put("first_name","NULL");}
	                
	                if(rs.getString("last_name")!=null && !rs.getString("last_name").isEmpty()){
	                tableMap.put("last_name", rs.getString("last_name"));
	                }
	                else{ tableMap.put("last_name","NULL");}
	                
	                if(rs.getString("primary_moblie_number")!=null && !rs.getString("primary_moblie_number").isEmpty()){
		                tableMap.put("primary_moblie_number", rs.getString("primary_moblie_number"));
	                }
	                else{ tableMap.put("primary_moblie_number","NULL");}
	                
	                if(rs.getString("serial_number")!=null && !rs.getString("serial_number").isEmpty()){
		                tableMap.put("serial_number", rs.getString("serial_number"));
	                }
	                else{ tableMap.put("serial_number","NULL");}
	                
	                if(rs.getString("customercode")!=null && !rs.getString("customercode").isEmpty()){
		                tableMap.put("customercode", rs.getString("customercode"));
	                }
	                else{ tableMap.put("customercode","NULL");}
	                
	                if(rs.getString("zonalcode")!=null && !rs.getString("zonalcode").isEmpty()){
		                tableMap.put("zonalcode", rs.getString("zonalcode"));
	                }
	                else{ tableMap.put("zonalcode","NULL");}
	                
	                if(rs.getString("zone")!=null && !rs.getString("zone").isEmpty()){
		                tableMap.put("zone", rs.getString("zone"));
	                }
	                else{ tableMap.put("zone","NULL");} 
	                
	                if(rs.getString("dealer_name")!=null && !rs.getString("dealer_name").isEmpty()){
		                tableMap.put("dealer_name", rs.getString("dealer_name"));
	                }
	                else{ tableMap.put("dealer_name","NULL");}
	                
	                if(rs.getString("DealerCode")!=null && !rs.getString("DealerCode").isEmpty()){
		                tableMap.put("DealerCode", rs.getString("DealerCode"));}
	                else{ tableMap.put("DealerCode","NULL");}
//	                ---------------------------------------------------------------------------
	                if(rs.getString("installed_date")!=null && !rs.getString("installed_date").isEmpty()){
		                tableMap.put("installed_date", rs.getString("installed_date"));
	                }
	                else{ tableMap.put("installed_date","NULL");}
	                
	                if(rs.getString("roll_off_date")!=null && !rs.getString("roll_off_date").isEmpty()){
		                tableMap.put("roll_off_date", rs.getString("roll_off_date"));
	                }
	                else{ tableMap.put("roll_off_date","NULL");}
	                
	                if(rs.getString("renewal_date")!=null && !rs.getString("renewal_date").isEmpty()){
		                tableMap.put("renewal_date", rs.getString("renewal_date"));
	                }
	                else{ tableMap.put("renewal_date","NULL");}
	                
	                if(rs.getString("renew_state")!=null && !rs.getString("renew_state").isEmpty()){
		                tableMap.put("renew_state", rs.getString("renew_state"));
	                }
	                else{ tableMap.put("renew_state","NULL");}
	                
	                if(rs.getString("model")!=null && !rs.getString("model").isEmpty()){
		                tableMap.put("model", rs.getString("model"));
	                }
	                else{ tableMap.put("model","NULL");}
	                
	                if(rs.getString("prodProfile")!=null && !rs.getString("prodProfile").isEmpty()){
		                tableMap.put("prodProfile", rs.getString("prodProfile"));
	                }
	                else{ tableMap.put("prodProfile","NULL");}
	               
	          
	                mapList.add(tableMap);
	            }


	        } catch (Exception e) {
	            fLogger.fatal(e.getMessage());
	            fLogger.info(e.getMessage());
	        }
			return mapList;
	    }
}