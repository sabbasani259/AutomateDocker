package remote.wise.service.implementation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.AssetUtil;
import remote.wise.util.CommonUtil;
import remote.wise.util.ConnectMySQL;

public class DealerOutletMappingImpl {
	public String getmappingdata(String roleName,List<Integer> tenancyIdList,String loginId){
		//20181116 ::MA369757 :: Service to get dealer outlets
		String result = null;
		Logger infoLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		ConnectMySQL connMySql = new ConnectMySQL();
		Connection prodConnection = null,prodConnection1 = null;
		Statement statement = null,statement1 = null;
		ResultSet rs = null,rs1 = null;
		String Query="",Query2="";
		int roleId=0,dealerId=0;
		List<HashMap<String,String>> mappingList=new LinkedList<HashMap<String,String>>();
		List<Integer> dealerIdList=new ArrayList<>();
		try{
			String tenancyIdListStr=AssetUtil.getIDListAsCommaSeperated(tenancyIdList);
			if(roleName.equalsIgnoreCase("Customer")||roleName.equalsIgnoreCase("Customer Fleet Manager"))
			{
				Query = "select c.role_id,a.Parent_ID from contact c,account a,account_contact ac,account_tenancy at where c.contact_id=ac.Contact_ID and ac.Account_ID=a.Parent_ID and a.Account_ID=at.account_id and at.tenancy_id in("
						+ tenancyIdListStr+")";
				infoLogger.info("DealerOutletMappingService :: DealerOutletMappingImpl ::  Query :"+Query);
				prodConnection = connMySql.getConnection();
				statement = prodConnection.createStatement();
				rs=statement.executeQuery(Query);
				while(rs.next()){
					roleId=rs.getInt("role_id");
					//Check for Dealer or Dealer Admin
					if(roleId==5||roleId==6){
					dealerId=rs.getInt("Parent_ID");
					dealerIdList.add(dealerId);
					}
				}
				
			}
			else if(roleName.equalsIgnoreCase("Dealer Admin")||roleName.equalsIgnoreCase("Dealer"))
				{
					Query="select Account_ID from account_tenancy where Tenancy_ID in("+tenancyIdListStr+")";
					infoLogger.info("DealerOutletMappingService :: DealerOutletMappingImpl ::  Query :"+Query);
					prodConnection = connMySql.getConnection();
					statement = prodConnection.createStatement();
					rs=statement.executeQuery(Query);
					while(rs.next()){
						dealerId=rs.getInt("Account_ID");
						dealerIdList.add(dealerId);
					}
				}
			else{
				infoLogger.info("DealerOutletMappingService :: Invalid rolename.");
				throw new CustomFault("Invalid rolename.");
			}
			if(dealerIdList.size()!=0)
			{
				String dealerIdListAsString=AssetUtil.getIDListAsCommaSeperated(dealerIdList);
				Query2="select DealerName,Lat,Lng from DealerOutlet where DealerAccId in("+dealerIdListAsString+")";
				infoLogger.info("DealerOutletMappingService :: DealerOutletMappingImpl ::  Query for DealerOutletMapping table : "+Query2);
				prodConnection1 = connMySql.getConnection();
				statement1 = prodConnection1.createStatement();
				rs1=statement1.executeQuery(Query2);
				HashMap<String,String> mappings = null;
				while(rs1.next())
				{
					mappings = new HashMap<String,String>();
					mappings.put("accountName", rs1.getString("DealerName"));
					mappings.put("latitude", rs1.getString("Lat"));
					mappings.put("longitude", rs1.getString("Lng"));
					mappingList.add(mappings);
				}
				
			}
			
			Gson gson = new Gson(); 
			if(mappingList!=null)
				result = gson.toJsonTree(
						mappingList,
						new TypeToken<List<HashMap<String,String>>>() {
						}.getType()).toString();
			
			
		}catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("DealerOutletMappingService ::DealerOutletMappingImpl: loginId : "+loginId+": Exception :: "+e.getMessage(),e);
		}
		finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}

			if (statement != null)
				try {
					statement.close();
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
			if (rs1 != null)
				try {
					rs1.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}

			if (statement1 != null)
				try {
					statement1.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			if (prodConnection1 != null) {
				try {
					prodConnection1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		}
		return result;
	}
}
