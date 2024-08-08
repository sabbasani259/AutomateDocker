package remote.wise.businessobject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.Logger;

import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.ConnectMySQL;

public class MachineCountDetailsBO {

	public HashMap<String,Integer> getMachineCount(List<String> accountIdList) throws CustomFault
	{
		HashMap<String,Integer> machineCountDetails = new HashMap<String,Integer>();
		Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
    	Logger bLogger = BusinessErrorLoggerClass.logger;
    	
		if(accountIdList==null)
		{
			bLogger.error("MachineCountDetailsBO :: Please pass a Account Id");
			throw new CustomFault("MachineCountDetailsBO :: Please pass a Account ID");
		
		}

	    iLogger.info("**********MachineCountDetailsBO Starts******************");
	    
	    ConnectMySQL connMySql = new ConnectMySQL();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		Statement stmt_1 = null;
		ResultSet rs_1 = null;
		
		try
		{
			conn = connMySql.getConnection();
			stmt = conn.createStatement();
			stmt_1 = conn.createStatement();
			
			for(String account_Id : accountIdList) {
				
				String accountIdString = null;
				int count = 0;
				String getAccountIdQ = "select a.account_id from account_tenancy a, account b where b.status=true and a.account_id=b.account_id "+
		    			   " and b.mapping_code in (select c.mapping_code from account c where c.status=true and c.account_id ="+account_Id+")";
			    rs = stmt.executeQuery(getAccountIdQ);
			    while(rs.next())
			    {
			    	if(accountIdString==null)
			    		accountIdString = rs.getString("account_id");
			    	else
			    		accountIdString = accountIdString+","+rs.getString("account_id");
			    }
			    String getCount ="select Count(*) as count from asset_monitoring_snapshot ams,asset_owner_snapshot aos,asset a where ams.Serial_Number=aos.Serial_Number and aos.Account_ID in "+
	      				 " ("+accountIdString+") and ams.Latest_Transaction_Timestamp >= aos.Ownership_Start_Date and ams.Serial_Number=a.Serial_Number and a.Status= 1";
	    		rs_1 = stmt_1.executeQuery(getCount);
	    		if(rs_1.next())
				{
	    			count = rs_1.getInt("count");
				}
	    		machineCountDetails.put(account_Id, count);
			}
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal(":Exception:"+e.getMessage());
		}
		finally
		{
			if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			if(rs_1!=null)
				try {
					rs_1.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			if(stmt!=null)
				try {
					stmt.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			if(stmt_1!=null)
				try {
					stmt_1.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		
		}	
		
		return machineCountDetails;
	}
	
}
