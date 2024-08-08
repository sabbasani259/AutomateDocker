package remote.wise.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.ConnectMySQL;

public class MaAssetVinListServiceDao {

	public static List<String> AssetVinNumberOfMaAccountHolders() {
		// TODO Auto-generated method stub
		Logger iLogger = InfoLoggerClass.logger;
		List<String> assetVinNumberOfMaAccountHolderslist = new ArrayList<>();
		String query = "SELECT serial_number FROM asset_owner_snapshot a, account b WHERE a.Account_Type='Customer' and a.account_id=b.account_id and b.MAFlag = 1";
		iLogger.info("--- Query-------- : "+query);	
		ConnectMySQL connFactory = new ConnectMySQL();
		try (Connection connection = connFactory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet rs = statement.executeQuery(query);){
			while (rs.next()){
				assetVinNumberOfMaAccountHolderslist.add(rs.getString("serial_number"));	
				
			}
			
		}catch(SQLException se){
			iLogger.info("issue with query "+query+se.getMessage());
		}catch(Exception e){
			iLogger.info("AssetVinNumberOfMaAccountHolders() issue "+e.getMessage());
		}

		return assetVinNumberOfMaAccountHolderslist;
	}
}
