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

/*
 * DF100001559:20220525:Dhiraj K:Consolidated MA Customer Mismatch with the count
 */

public class MaAccountCodesListServiceDao {

	public static List<String> accountCodesOfMaAccountHolders() {
		Logger iLogger = InfoLoggerClass.logger;
		//Logger fLogger = FatalLoggerClass.logger;

		List<String> accountCodesOfMaAccountHolderslist = new ArrayList<>();
		//DF100001559.so
		//String interfaceListQuery = "select distinct (account_code) from account a, asset_owner_snapshot b where a.account_id=b.account_id and a.maflag=1 and b.account_type='Customer'";
		//DF100001559.eo
		//DF100001559.sn
		String interfaceListQuery = "select distinct(account_code) from account where mapping_code in (select mapping_code from account where MAFlag=1)";
		//DF100001559.eo
		iLogger.info("interfaceListQuery = "+interfaceListQuery);
		ConnectMySQL connFactory = new ConnectMySQL();
		try (Connection connection = connFactory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet rs = statement.executeQuery(interfaceListQuery);) {

			while (rs.next()) {
				accountCodesOfMaAccountHolderslist.add(rs.getString("account_code"));
			}

		}catch(SQLException se){
			iLogger.info("issue with query "+interfaceListQuery+se.getMessage());
		}catch(Exception e){
			iLogger.info("accountCodesOfMaAccountHolders() issue "+e.getMessage());
		}
		return accountCodesOfMaAccountHolderslist;
	}
}
