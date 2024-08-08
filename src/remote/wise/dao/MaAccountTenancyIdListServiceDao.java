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
import remote.wise.util.ListToStringConversion;

/*
 * DF100001559:20220525:Dhiraj K:Consolidated MA Customer Mismatch with the count
 */

public class MaAccountTenancyIdListServiceDao {

	public static List<Integer> tenancyOfMaAccountHolders() {
		Logger iLogger = InfoLoggerClass.logger;
		//Logger fLogger = FatalLoggerClass.logger;
		//DF100001559.sn
		List<String> accountCodeOfMAccountHoldersList = new ArrayList<>();
		String accountCodeListQuery = "select account_code from account where mapping_code in (select mapping_code from account where MAFlag=1)";
		ConnectMySQL connFactory = new ConnectMySQL();
		try (Connection connection = connFactory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet rs = statement.executeQuery(accountCodeListQuery);) {

			while (rs.next()) {
				accountCodeOfMAccountHoldersList.add(rs.getString("account_code"));
			}

		}catch(SQLException se){
			iLogger.info("issue with query "+accountCodeListQuery+se.getMessage());
		}catch(Exception e){
			iLogger.info("tenancyOfMaAccountHolders() issue "+e.getMessage());
		}

		ListToStringConversion conversionObj = new ListToStringConversion();
		String accountCodeOfMAccountHoldersListString = conversionObj.getStringList(accountCodeOfMAccountHoldersList).toString();
		//DF100001559.en
		List<Integer> tenancyOfMaAccountHolderslist = new ArrayList<>();
		//DF100001559.so
		//String interfaceListQuery = "select distinct(at.Tenancy_ID) from account acc, account_tenancy at,tenancy ten,asset_owner_snapshot aos where acc.MAFlag=1  and acc.account_id = at.account_id and at.Tenancy_ID = ten.Tenancy_ID and aos.Account_ID=acc.Account_ID and aos.Account_Type='Customer'";
		//DF100001559.eo
		//DF100001559.sn
		String interfaceListQuery = "select b.tenancy_id from account a, account_tenancy b where a.account_id=b.account_id and a.account_code in (" + accountCodeOfMAccountHoldersListString + ")";
		//DF100001559.en
		try (Connection connection = connFactory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet rs = statement.executeQuery(interfaceListQuery);) {

			while (rs.next()) {
				tenancyOfMaAccountHolderslist.add(rs.getInt("Tenancy_ID"));
			}

		}catch(SQLException se){
			iLogger.info("issue with query "+interfaceListQuery+se.getMessage());
		}catch(Exception e){
			iLogger.info("tenancyOfMaAccountHolders() issue "+e.getMessage());
		}
		return tenancyOfMaAccountHolderslist;
	}
	public static void main(String[] args) {
		new MaAccountTenancyIdListServiceDao().tenancyOfMaAccountHolders();
	}
}
