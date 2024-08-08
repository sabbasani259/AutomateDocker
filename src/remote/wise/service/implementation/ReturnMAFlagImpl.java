package remote.wise.service.implementation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.Logger;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.ConnectMySQL;

//CR308 : DHIRAJ K : 20220426 : Beyond Warranty closures from the web potal for MA users
public class ReturnMAFlagImpl {

	public String ReturnMAFlag(String contactId){
		String MAFlag=null;
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		
		Connection con = null;
		Statement statement = null;
		ResultSet resultSet=null;
		
		try{
			
			ConnectMySQL connectionObj = new ConnectMySQL();
			con = connectionObj.getConnection();
			statement = con.createStatement();
//			byte[] encodedBytes = Base64.decodeBase64(contactId.getBytes());
//			contactId= new String(encodedBytes);
			System.out.println("contactId"+ contactId);
			resultSet = statement.executeQuery("select a.account_id,a.MAFlag,b.contact_id  from account a, account_contact b where a.MAFlag=1 and a.account_id = b.account_id and b.contact_id= '"+contactId+"'");
			while(resultSet.next())
			{
				MAFlag = resultSet.getString("MAFlag");
			}
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return MAFlag;
	}
}
