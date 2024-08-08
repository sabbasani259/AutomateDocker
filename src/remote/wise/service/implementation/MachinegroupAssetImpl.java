package remote.wise.service.implementation;
//CR335-20220624-Balaji MDA reports will send the list of Machine Group Ids as an input and return the list of Asset Ids as a response.
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.Logger;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.StaticProperties;

public class MachinegroupAssetImpl {
	/*public static void main(String[] args) {
		List<String> list = new ArrayList<String>();
		list.add("6");
		list.add("7");
		list.add("8");
		MachinegroupAssetImpl impl=new MachinegroupAssetImpl();
		impl.getMachineGroupIDs(list);
	}*/
	public List<String> getMachineGroupAsset(String userID){
		
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		
		List<String> machinesUnderUser = new ArrayList<String>();
		Connection prodConnection = null;
		PreparedStatement query = null;
		ResultSet rs = null;
		
		String assetQuery = "";
		assetQuery = "SELECT Asset_Id from custom_asset_group_snapshot where user_Id = ?";
		
		Properties prop=null;
		try
		{
			prop= StaticProperties.getConfProperty();
		}
		catch(Exception e)
		{
			fLogger.fatal("Machines under User "+"Error in intializing property File :"+e.getMessage());
		}


		String PersistToInMemory = prop.getProperty("PersistTo_InMemory");

		if(Boolean.parseBoolean(PersistToInMemory)){
			/*<!=========START persisting in In memory==============>*/
			iLogger.info("Machines under User "+"Persist to In Memory storage");
			/*<!=========END persisting in In memory==============>*/
		}
		else{
			/*<!=========START fetching from Native Database==============>*/
			
			try{

				ConnectMySQL connMySql = new ConnectMySQL();
				prodConnection = connMySql.getConnection();
				query = prodConnection.prepareStatement(assetQuery);
				query.setString(1, userID);
				iLogger.info("Query String: " + query.toString());
				rs = query.executeQuery();
				int count = 0;
				while(rs.next()){
					count = count + 1;
					machinesUnderUser.add(rs.getString(1));
				}
				iLogger.info("Size of output list: " + count);
			}
			catch (SQLException e) {

				e.printStackTrace();
				fLogger.fatal("Machines under User "+"SQL Exception in fetching data from mysql::"+e.getMessage());
			} 
			catch(Exception e)
			{
				e.printStackTrace();
				fLogger.fatal("Machines under User "+"Exception in fetching data from mysql::"+e.getMessage());
			}

			finally {
				if(rs!=null)
					try {
						rs.close();
						} catch (SQLException e1) {
							e1.printStackTrace();
						}

				if(query!=null)
					try {
						query.close();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				if (prodConnection != null) {
					try {
						prodConnection.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}

			}
		
		/*<!=========END fetching from Native Database==============>*/
		
		}
		iLogger.info("Size of the output list: " + machinesUnderUser.size());
		return machinesUnderUser;
		
	}
	//CR335-MachineGroupIDS-SN
	public List<String> getMachineGroupIDs(String machineGroupIds){

		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		List<String> machinesUnderGroupId = new ArrayList<String>();
		Connection prodConnection = null;
		PreparedStatement query = null;
		ResultSet rs = null;

		ConnectMySQL connMySql = new ConnectMySQL();
		prodConnection = connMySql.getConnection();
		//prodConnection = connMySql.getStagingConnection();  
		try
		{ 
			String[] gropIds=machineGroupIds.split(",");
			for(String grpId : gropIds)
			{
				
				String assetQuery = "";
				assetQuery = "SELECT Asset_Id from custom_asset_group_snapshot where Group_ID IN (" + grpId + ")";	
				query = prodConnection.prepareStatement(assetQuery);
				iLogger.info("Query String: " + query.toString());
				rs = query.executeQuery();
				int count = 0;
				while(rs.next()){
					count = count + 1;
					machinesUnderGroupId.add(rs.getString(1));
				}
				iLogger.info("Size of output list: " + count);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
			fLogger.fatal("Machines under GroupIDs "+"SQL Exception in fetching data from mysql::"+e.getMessage());
		} 
		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("Machines under GroupIDs "+"Exception in fetching data from mysql::"+e.getMessage());
		}
		finally {
			if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}

			if(query!=null)
				try {
					query.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			if (prodConnection != null) {
				try {
					prodConnection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		}
		return machinesUnderGroupId;
	}
	//CR335-MachineGroupIDS-EN
}
