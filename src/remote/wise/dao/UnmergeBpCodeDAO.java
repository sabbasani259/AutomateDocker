/*
 * JCB417: Prasanna :20230607 : Changes for updating tenancy table
 */
package remote.wise.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


import org.apache.logging.log4j.Logger;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.ConnectMySQL;

public class UnmergeBpCodeDAO {
	Logger fLogger = FatalLoggerClass.logger;
	Logger iLogger = InfoLoggerClass.logger;

	public String updateMappingCodeForCorrespondingAccountCode(
			String accountCode) {
		ConnectMySQL connFactory = new ConnectMySQL();
		
		String response = "Success";	
		String updateQueryForMappingCodes = "update account set mapping_code="
				+ "'" + accountCode + "'" + " where Account_Code=" + "'"
				+ accountCode + "'";
		String updateQueryForMACustomer = "update account set MAFlag=0 where Account_Code="
				+ "'" + accountCode + "'";
		String UpdateQuearyForMappingCodeTenancy = "update tenancy set mapping_code='"+accountCode+"' where Tenancy_Code='"+accountCode+"'" ;//JCB417.n
		iLogger.info("updateQueryForMappingCodes : "+updateQueryForMappingCodes);
		iLogger.info("updateQueryForMACustomer : "+updateQueryForMACustomer);
		iLogger.info("updateQueryForMappingCodeTenancy : "+UpdateQuearyForMappingCodeTenancy);//JCB417.n
		try (Connection connection = connFactory.getConnection();
				PreparedStatement statement = connection
						.prepareStatement(updateQueryForMappingCodes);
				PreparedStatement statement1 = connection
						.prepareStatement(updateQueryForMACustomer);
				PreparedStatement statement2 = connection
						.prepareStatement(UpdateQuearyForMappingCodeTenancy);) {
			statement.executeUpdate(updateQueryForMappingCodes);
			statement1.executeUpdate(updateQueryForMACustomer);//JCB417.n
			statement2.executeUpdate(UpdateQuearyForMappingCodeTenancy);//JCB417.n
			
		   return response ;//JCB417.n
			
		} catch (Exception e) {
			response = "failure";
			fLogger.fatal("updateMappingCodeForCorrespondingAccountCode()::issue while updating DB "
					+ e.getMessage());
		}
		return response;
	}

	public boolean searchAccountCode(String accountCode) {
		String searchQuery = "select Account_Code  from account  where  Account_Code="
				+ "'" + accountCode + "'";
		boolean found = false;
		iLogger.info("searchQuery : "+searchQuery);
		ConnectMySQL connFactory = new ConnectMySQL();
		try (Connection connection = connFactory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet rs = statement.executeQuery(searchQuery);) {

			while (rs.next()) {
				found = true;
			}

		} catch (SQLException se) {
			fLogger.fatal("issue with query " + searchQuery + se.getMessage());
		} catch (Exception e) {
			fLogger.fatal("searchAccountCode() issue " + e.getMessage());
		}
		return found;
	}
}
