package remote.wise.service.implementation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import remote.wise.util.ConnectMySQL;

public class AssetGroupNameListImpl {

	public HashMap<String, String> getAssetGroupNameDetails(String userId) {
		
		HashMap<String, String> resp= new HashMap<String, String>();
		
		String query = "select distinct cags.Group_ID, Group_Name from custom_asset_group_snapshot cags " +
				"Inner Join custom_asset_group cag on(cags.Group_ID = cag.Group_ID ) where cags.user_Id='"+userId+"'";
		Connection conn =null;
		Statement stmnt =null;
		ResultSet rs =null;
		try {
			conn =new ConnectMySQL().getConnection();
			stmnt = conn.createStatement();
			rs = stmnt.executeQuery(query);
			while(rs.next()){
				resp.put(rs.getString("Group_Id"),rs.getString("Group_Name"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			if(conn!=null){
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(stmnt!=null){
				try {
					stmnt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return resp;
	}

	
}
