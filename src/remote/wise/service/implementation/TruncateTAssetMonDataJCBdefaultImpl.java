package remote.wise.service.implementation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import remote.wise.util.ConnectMySQL;

public class TruncateTAssetMonDataJCBdefaultImpl {
	// DF20200102 @Mamatha getting SMS details
	public String setTAssetMonDataJCBdefaultData() {
		ConnectMySQL connFactory = new ConnectMySQL();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String response = "FAILURE";

		try {
			conn = connFactory.getDatalakeConnection_3309();
			stmt = conn.createStatement();
			String Query = "SELECT table_name `Table`,round(((data_length + index_length) / 1024 / 1024 / 1024 ), 2) "
					+ " FROM information_schema.TABLES WHERE table_schema = 'wise' AND table_name = 'TAssetMonData_JCB_default' "
					+ " AND round(((data_length + index_length) / 1024 / 1024 / 1024 ), 2) > 40.00 ";

			rs = stmt.executeQuery(Query);

			if (rs.next()) {
				stmt.executeUpdate("TRUNCATE TAssetMonData_JCB_default ");
				response = "SUCCESS";
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}

			if (stmt != null)
				try {
					stmt.close();
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

		return response;

	}

}
