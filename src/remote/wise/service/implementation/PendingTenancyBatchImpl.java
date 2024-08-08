package remote.wise.service.implementation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedList;

import org.apache.logging.log4j.Logger;

import remote.wise.businessentity.AccountEntity;
import remote.wise.businessentity.AccountEntityPOJO;
import remote.wise.businessobject.PendingTenancyBO;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.ConnectMySQL;

public class PendingTenancyBatchImpl implements Runnable
{
	Thread t;
	int accountId=0;
	
	public PendingTenancyBatchImpl()
	{
		
	}
	
	public PendingTenancyBatchImpl(int accountId)
	{
		this.accountId=accountId;
		t = new Thread(this, "PendingTenancyBatchImpl");
		t.start();
	}
	
	public void run() 
	{
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		
		Connection con=null;
		Statement stmt=null;
		
		try
		{
			long startTime = System.currentTimeMillis();
			
			String query=null;
			//-------------STEP1: Get the list of accounts for which tenancy has to be created.
			if(this.accountId==0)
			{
				query= "select a.Account_ID,a.Account_Name, a.Description,a.Status, a.Parent_ID,a.Phone,a.Mobile,a.Client_ID," +
						" a.Email_ID,a.Account_Code,a.timeZone, a.CountryCode,a.mapping_code, b.Account_Name as ParentTenancyName " +
						" from account a, account b where a.Parent_ID=b.Account_ID and " +
						" a.account_id not in (select account_id from account_tenancy) and a.Status=1" ;
			}
			else
			{
				query= "select a.Account_ID,a.Account_Name, a.Description,a.Status, a.Parent_ID,a.Phone,a.Mobile,a.Client_ID," +
						" a.Email_ID,a.Account_Code,a.timeZone, a.CountryCode,a.mapping_code, b.Account_Name as ParentTenancyName " +
						" from account a, account b where a.Parent_ID=b.Account_ID and " +
						" a.account_id ="+this.accountId+" and a.account_id not in (select account_id from account_tenancy) and a.Status=1";
			}
				
			con = new ConnectMySQL().getConnection();
			stmt = con.createStatement();
			LinkedList<AccountEntityPOJO> accountList = new LinkedList<AccountEntityPOJO>();
			ResultSet rs = stmt.executeQuery(query);
			
			while(rs.next())
			{
				AccountEntityPOJO accountObj = new AccountEntityPOJO();
				accountObj.setAccount_id(rs.getInt("Account_ID"));
				accountObj.setAccount_name(rs.getString("Account_Name"));
				accountObj.setDescription(rs.getString("Description"));
				accountObj.setStatus(rs.getBoolean("Status"));
				accountObj.setParent_account_id(rs.getInt("Parent_ID"));
				accountObj.setPhone_no(rs.getString("Phone"));
				accountObj.setMobile_no(rs.getString("Mobile"));
				accountObj.setClient_id(rs.getInt("Client_ID"));
				accountObj.setEmailId(rs.getString("Email_ID"));
				accountObj.setAccountCode(rs.getString("Account_Code"));
				accountObj.setTimeZone(rs.getString("timeZone"));
				accountObj.setCountryCode(rs.getString("CountryCode"));
				accountObj.setMappingCode(rs.getString("mapping_code"));
				accountObj.setParent_account_name(rs.getString("ParentTenancyName"));
				
				accountList.add(accountObj);
			}
			
			iLogger.info("PendingTenancyBatchService:PendingTenancyBatchImpl:No. of tenancies to be created:"+accountList.size());
			
			for(int i=0; i<accountList.size(); i++)
			{
				String status = new PendingTenancyBO().createPendingTenancy(accountList.get(i));
				iLogger.info("PendingTenancyBatchService:PendingTenancyBatchImpl:AccountID:"+accountList.get(i).getAccount_id()+"; Tenancy Creation Status:"+status);
			}
			
			long endTime = System.currentTimeMillis();
			iLogger.info("PendingTenancyBatchService:createPendingTenancies:WebService Execution time in ms:"+(endTime - startTime));
		}
		
		catch(Exception e)
		{
			fLogger.fatal("PendingTenancyBatchService:PendingTenancyBatchImpl:Exception:"+e.getMessage());
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
				fLogger.fatal("PendingTenancyBatchService:PendingTenancyBatchImpl:Exception in closing the connection:"+e.getMessage(),e);
			}
		}
	
	}
}
