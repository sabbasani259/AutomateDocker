package remote.wise.service.implementation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.logging.log4j.Logger;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.ConnectMySQL;

public class TenancyTypeImpl 
{
	public String getTenancyType(int tenancyId)
	{
		String tenancyType="";
		Logger fLogger = FatalLoggerClass.logger;
		
		Connection con=null;
		Statement stmt=null;
		
		try
		{
			con = new ConnectMySQL().getConnection();
			stmt = con.createStatement();
			
			String query = "select b.Tenancy_Type_Name from tenancy a, tenancy_type b where a.tenancy_type_id=b.tenancy_type_id" +
					" and a.tenancy_id="+tenancyId;
			ResultSet rs = stmt.executeQuery(query);
			
			while(rs.next())
			{
				tenancyType = rs.getString("Tenancy_Type_Name");
			}
			
		}
		catch(Exception e)
		{
			fLogger.fatal("TenancyTypeImpl:getTenancyType():Exception:"+e.getMessage(),e);
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
				fLogger.fatal("TenancyTypeImpl:getTenancyType():Exception in closing the connection:"+e.getMessage(),e);
			}
		}
		
		return tenancyType;
	}
}
