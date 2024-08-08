package remote.wise.service.implementation;

import java.sql.Connection;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.Logger;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.util.ConnectMySQL;

public class BreakfastReportImpl 
{
	public String setBreakfastReportPref(String loginID, String accountCode, int preference, String Source)
	{
		String status="SUCCESS";
		Logger fLogger=FatalLoggerClass.logger;
		
		Connection con=null;
		Statement stmt=null;
		ResultSet rs = null;
		
		try
		{		    		    
		    //Get DB connection
		    con = new ConnectMySQL().getConnection();
			stmt=con.createStatement();
			
			String currrentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			String updateQ ="update BreakfastReportPreference set Preference="+preference+", Source='"+Source+"', LastUpdatedTime='"+currrentTime+"'" +
					" where UserID='"+loginID+"'";
			int updateCount = stmt.executeUpdate(updateQ);
			
			if(updateCount==0)
			{
				String insertQuery = null;
				
				if(accountCode!=null)
					insertQuery="INSERT INTO BreakfastReportPreference(UserID,AccountCode,Preference,Source,LastUpdatedTime) " +
						"values ('"+loginID+"','"+accountCode+"'," +
						preference+", '"+Source+"', '"+currrentTime+"')";
				else
					insertQuery="INSERT INTO BreakfastReportPreference(UserID,Preference,Source,LastUpdatedTime) " +
							"values ('"+loginID+"'," +
							preference+", '"+Source+"', '"+currrentTime+"')";
				
				stmt.executeUpdate(insertQuery);
			}
		}	
		catch(Exception e)
		{
			status="FAILURE";
			fLogger.fatal("BreakfastReportImpl:setBreakfastReportPref:DAL:loginID:"+loginID+"; Exception:",e.getMessage(),e);
		}
		
		finally
		{
			try
			{
				if(stmt!=null)
					stmt.close();
				if(con!=null)
					con.close();
			}
			catch(Exception e)
			{
				fLogger.fatal("BreakfastReportImpl:setBreakfastReportPref:DAL:loginID:"+loginID+"; Exception in closing the connection:"+e.getMessage(),e);
			}
		}
		
		return status;
	}
	
	//***********************************************************************************************************
	public int getBFReportPreference(String loginID, String source)
	{
		int preference=0;
		
		Logger fLogger = FatalLoggerClass.logger;
		
		Connection con=null;
		Statement stmt=null;
		ResultSet rs = null;
		
		try
		{
			//Get DB connection
			con = new ConnectMySQL().getConnection();
			stmt = con.createStatement();
			
			String query = "select Preference from BreakfastReportPreference where UserID='"+loginID+"'";
			rs = stmt.executeQuery(query);
			while(rs.next())
			{
				preference= rs.getInt("Preference");
			}
		}
		catch(Exception e)
		{
			fLogger.fatal("BreakfastReportImpl:getBFReportPreference:DAL:loginID:"+loginID+":Exception:",e.getMessage(),e);
		}
		finally
		{
			try
			{
				if(stmt!=null)
					stmt.close();
				if(con!=null)
					con.close();
			}
			catch(Exception e)
			{
				fLogger.fatal("BreakfastReportImpl:getBFReportPreference:DAL:loginID:"+loginID+
						"Exception in closing the connection:"+e.getMessage(),e);
			}			
		}
		return preference;
	}
}
