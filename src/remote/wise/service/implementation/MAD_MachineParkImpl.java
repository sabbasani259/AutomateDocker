/**
 * 
 */
package remote.wise.service.implementation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.Logger;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.DateUtil;
import remote.wise.util.ListToStringConversion;

/**
 * @author ROOPN5
 *
 */
public class MAD_MachineParkImpl {

	public List<HashMap<String, Object>> getMachineParkDetails(String loginID, List tenancyIdList,List profileIdList, String DetailedView){

		HashMap<String, Object> respObj = null;
		List<HashMap<String, Object>> responseList=new ArrayList<HashMap<String,Object>>();
		
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		Connection prodConnection = null;
		Statement statement = null;
		ResultSet rs = null;
		String accountIdListAsString=null;
		String profileIdlistAsString=null;

		String selectQuery=null;
		String fromQuery=null;
		String whereQuery=null;
		String groupByQuery=null;
		String finalQuery=null;

		//Df20180129 @Roopa Multiple BP code changes--Taking the list of users that are mapped to given account code, based on Mapping code
		try{
			if(tenancyIdList!=null){

				accountIdListAsString=new DateUtil().getAccountListForTheTenancy(tenancyIdList);
			}

			if(profileIdList!=null){
				profileIdlistAsString=new ListToStringConversion().getIntegerListString(profileIdList).toString();
			}
			
			if(DetailedView==null || DetailedView.equalsIgnoreCase("false")){

			selectQuery= "select count(*) as machineCount,ag.Asseet_Group_Name as profileName";
			}
			else{
				selectQuery= "select a.Serial_Number,ag.Asseet_Group_Name as profileName";	
			}
			
			fromQuery= " from asset a,asset_group ag,products p,asset_owner_snapshot aos ";
			
			whereQuery=	" where aos.Account_ID in ("+accountIdListAsString+") and a.Serial_Number=aos.Serial_Number and a.Product_ID=p.Product_ID and p.Asset_Group_ID=ag.Asset_Group_ID ";
			
			if(profileIdlistAsString!=null){
				whereQuery=whereQuery+ " and p.Asset_Group_ID in("+profileIdlistAsString+")";	
			}
			
			if(DetailedView==null || DetailedView.equalsIgnoreCase("false")){
			groupByQuery= " group by ag.Asset_Group_ID";
			}
			else{
				groupByQuery= " group by a.Serial_Number";	
			}
			
			finalQuery=selectQuery+fromQuery+whereQuery+groupByQuery;
			
			iLogger.info("MADashBoardDetailsRESTService:getMADMachineParkDetails: Final Query::"+finalQuery);

			ConnectMySQL connMySql = new ConnectMySQL();
			prodConnection = connMySql.getConnection();
			statement = prodConnection.createStatement();
			rs = statement.executeQuery(finalQuery);

			
			if(DetailedView==null || DetailedView.equalsIgnoreCase("false")){
			while(rs.next()){
				respObj = new HashMap<String, Object>();
				
				respObj.put("machineCount", rs.getInt("machineCount"));
				respObj.put("profileName", rs.getString("profileName"));
				
				responseList.add(respObj);
			}
			}
			else{
				while(rs.next()){
				respObj = new HashMap<String, Object>();
				
				respObj.put("Serial_Number", rs.getString("Serial_Number"));
				respObj.put("profileName", rs.getString("profileName"));
				
				responseList.add(respObj);
				}
			}

		}

		catch(Exception e){
			fLogger.fatal("MADashBoardDetailsRESTService:getMADMachineParkDetails:: Exception Caught:"+e.getMessage());
		}
		finally{
			try { 
				if (rs != null)
					rs.close(); 
			} catch (Exception e) {
				fLogger.fatal("MADashBoardDetailsRESTService:getMADMachineParkDetails:: Exception Caught:"+e.getMessage());
			}

			if(statement!=null){
				try {
					statement.close();
				} catch (SQLException e) {
					fLogger.fatal("MADashBoardDetailsRESTService:getMADMachineParkDetails:: Exception Caught:"+e.getMessage());
				}
			}
			if(prodConnection != null){
				try {
					prodConnection.close();
				} catch (SQLException e) {
					fLogger.fatal("MADashBoardDetailsRESTService:getMADMachineParkDetails:: Exception Caught:"+e.getMessage());
				}
			}
		}

		return responseList;

	}

}
