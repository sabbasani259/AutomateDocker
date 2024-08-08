/**
 * 
 */
package remote.wise.service.implementation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import org.apache.logging.log4j.Logger;

import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.util.ConnectMySQL;

/**
 * @author koti
 *
 */
public class BHLLifeReportImpl {

	public HashMap<String,Object> getBHLLifeReport(String AssetID){


		HashMap<String, Object> respObj = null;
		Logger bLogger = BusinessErrorLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		Connection prodConnection = null;
		Statement statement = null;
		ResultSet rs = null;
		String query =null;
		if(AssetID==null)
		{
			bLogger.error("VisualizationDashBoardRESTService:getMachineReports:Mandatory parameter serial number is null");
			return null;
		}
		try {

			if(AssetID!=null && (AssetID.trim().length()==7 || AssetID.trim().length()==17)){
				
				if(AssetID.trim().length()==7)
				{
					String machineNumber = AssetID;
					AssetID = new ExcavatorVisualizationReportImpl().getSerialNumberMachineNumber(machineNumber);
					if(AssetID==null)
					{//invalid machine number
						bLogger.error("BHLChartandPeriodRESTService:getBHLChartsAndPeriods: MachineNum"+ machineNumber + "does not exist !!!");
						return null ;
					}
				}
				//if(AssetID.trim().length()==17){
			 query = "select aa.*,bb.* from (select b.AssetID,b.TxnDate,convert(ifnull(b.ENG_IDL_HRS, 0.0), decimal (10 , 1 )) as LifeENG_IDL_HRS," +
					"convert(ifnull(b.Column17, 0.0), decimal (10 , 1 )) as LifeRoadingHRS," +
					"convert(ifnull(b.LOAD_JOB_HRS, 0.0), decimal (10 , 1 )) as LifeLOAD_JOB_HRS," +
					"convert(ifnull(b.column16, 0.0), decimal (10 , 1 )) as LifeExcavationHRS," +
					"convert(ifnull(b.EXA_ECO_MOD_HRS, 0.0), decimal (10 , 1 )) as LifeEXA_ECO_MOD_HRS," +
					"convert(ifnull(b.EXA_ACT_MOD_HRS, 0.0), decimal (10 , 1 )) as LifeEXA_ACT_MOD_HRS," +
					"convert(ifnull(b.EXA_PWR_MOD_HRS, 0.0), decimal (10 , 1 )) as LifeEXA_PWR_MOD_HRS," +
					"convert(ifnull(b.DSC_CON_HRS, 0.0), decimal (10 , 1 )) as LifeDSC_CON_HRS," +
					"convert(ifnull(b.FWD_DIR_HRS, 0.0), decimal (10 , 1 )) as LifeFWD_DIR_HRS," +
					"convert(ifnull(b.NEU_DIR_HRS, 0.0), decimal (10 , 1 )) as LifeNEU_DIR_HRS," +
					"convert(ifnull(b.REV_DIR_HRS, 0.0), decimal (10 , 1 )) as LifeREV_DIR_HRS," +
					"ifnull(b.Hyd_Choke_Events,0) as LifeHyd_Choke_Events,ifnull(b.No_AutoIdle_Events,0) as LifeNo_AutoIdle_Events," +
					"ifnull(b.No_AutoOFF_Events,0) as LifeNo_AutoOFF_Events," +
					"convert(ifnull(b.Column10, 0.0), decimal (10 , 1 )) as LifeAttachment " +
					"from factInsight_dayAgg_extended b where b.AssetID='"+AssetID+"' and b.ENG_IDL_HRS is not null " +
							"order by b.TxnDate desc limit 1) bb inner join "+
					"(select a.AssetID,a.TxnDate,convert(ifnull(a.FinishEngRunHrs, 0.0), decimal (10 , 1 )) as LifeTotalMachineHrs " +
					"from factInsight_dayAgg a where a.AssetID='"+AssetID+"' order by a.TxnDate desc limit 1) aa ON aa.AssetID=bb.AssetID";
				//}
				/*else if(AssetID.trim().length()==7){

					 query = "select aa.*,bb.* from (select b.AssetID,b.TxnDate,convert(ifnull(b.ENG_IDL_HRS, 0.0), decimal (10 , 1 )) as LifeENG_IDL_HRS," +
							"convert(ifnull(b.Column17, 0.0), decimal (10 , 1 )) as LifeRoadingHRS," +
							"convert(ifnull(b.LOAD_JOB_HRS, 0.0), decimal (10 , 1 )) as LifeLOAD_JOB_HRS," +
							"convert(ifnull(b.column16, 0.0), decimal (10 , 1 )) as LifeExcavationHRS," +
							"convert(ifnull(b.EXA_ECO_MOD_HRS, 0.0), decimal (10 , 1 )) as LifeEXA_ECO_MOD_HRS," +
							"convert(ifnull(b.EXA_ACT_MOD_HRS, 0.0), decimal (10 , 1 )) as LifeEXA_ACT_MOD_HRS," +
							"convert(ifnull(b.EXA_PWR_MOD_HRS, 0.0), decimal (10 , 1 )) as LifeEXA_PWR_MOD_HRS," +
							"convert(ifnull(b.DSC_CON_HRS, 0.0), decimal (10 , 1 )) as LifeDSC_CON_HRS," +
							"convert(ifnull(b.FWD_DIR_HRS, 0.0), decimal (10 , 1 )) as LifeFWD_DIR_HRS," +
							"convert(ifnull(b.NEU_DIR_HRS, 0.0), decimal (10 , 1 )) as LifeNEU_DIR_HRS," +
							"convert(ifnull(b.REV_DIR_HRS, 0.0), decimal (10 , 1 )) as LifeREV_DIR_HRS," +
							"ifnull(b.Hyd_Choke_Events,0) as LifeHyd_Choke_Events,ifnull(b.No_AutoIdle_Events,0) as LifeNo_AutoIdle_Events," +
							"ifnull(b.No_AutoOFF_Events,0) as LifeNo_AutoOFF_Events," +
							"convert(ifnull(b.Column10, 0.0), decimal (10 , 1 )) as LifeAttachment " +
							"from factInsight_dayAgg_extended b where b.AssetID like '%"+AssetID+"' and b.ENG_IDL_HRS is not null " +
									"order by b.TxnDate desc limit 1) bb inner join "+
							"(select a.AssetID,a.TxnDate,convert(ifnull(a.FinishEngRunHrs, 0.0), decimal (10 , 1 )) as LifeTotalMachineHrs " +
							"from factInsight_dayAgg a where a.AssetID like '%"+AssetID+"' order by a.TxnDate desc limit 1) aa ON aa.AssetID=bb.AssetID";
						
				}
				else{

					bLogger.error("BHLLifeRESTService:getBHLLifeData: Invalid serial number");
					return null;
				
				}*/
			}
			else{
				bLogger.error("BHLLifeRESTService:getBHLLifeData: Invalid serial number");
				return null;	
			}

			ConnectMySQL connMySql = new ConnectMySQL();
			prodConnection = connMySql.getProdDb2Connection();
			statement = prodConnection.createStatement();
			rs = statement.executeQuery(query);

			while(rs.next()){
				respObj = new HashMap<String, Object>();
				respObj.put("AssetID",rs.getString("AssetID"));
				respObj.put("LifeTotalMachineHrs", rs.getDouble("LifeTotalMachineHrs"));
				respObj.put("LifeENG_IDL_HRS", rs.getDouble("LifeENG_IDL_HRS"));
				respObj.put("LifeRoadingHRS", rs.getDouble("LifeRoadingHRS"));
				respObj.put("LifeLOAD_JOB_HRS", rs.getDouble("LifeLOAD_JOB_HRS"));
				respObj.put("LifeExcavationHRS", rs.getDouble("LifeExcavationHRS"));
				respObj.put("LifeEXA_ECO_MOD_HRS", rs.getDouble("LifeEXA_ECO_MOD_HRS"));
				respObj.put("LifeEXA_ACT_MOD_HRS", rs.getDouble("LifeEXA_ACT_MOD_HRS"));
				respObj.put("LifeEXA_PWR_MOD_HRS", rs.getDouble("LifeEXA_PWR_MOD_HRS"));
				respObj.put("LifeDSC_CON_HRS", rs.getDouble("LifeDSC_CON_HRS"));
				respObj.put("LifeFWD_DIR_HRS", rs.getDouble("LifeFWD_DIR_HRS"));
				respObj.put("LifeNEU_DIR_HRS", rs.getDouble("LifeNEU_DIR_HRS"));
				respObj.put("LifeREV_DIR_HRS", rs.getDouble("LifeREV_DIR_HRS"));
				respObj.put("LifeHyd_Choke_Events", rs.getInt("LifeHyd_Choke_Events"));
				respObj.put("LifeNo_AutoIdle_Events", rs.getInt("LifeNo_AutoIdle_Events"));
				respObj.put("LifeNo_AutoOFF_Events", rs.getInt("LifeNo_AutoOFF_Events"));
				respObj.put("LifeAttachment", rs.getDouble("LifeAttachment"));

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fLogger.fatal("VisualizationDashBoardRESTService:getMachineReports: Exception Caught:"+e.getMessage());
		}
		catch(Exception e){
			fLogger.fatal("BHLChartandPeriodRESTService:getBHLChartsAndPeriods:: Exception Caught:"+e.getMessage());
		}
		finally{
			try { 
				if (rs != null)
					rs.close(); 
			} catch (Exception e) {
				fLogger.fatal("BHLChartandPeriodRESTService:getBHLChartsAndPeriods:: Exception Caught:"+e.getMessage());
			}

			if(statement!=null){
				try {
					statement.close();
				} catch (SQLException e) {
					fLogger.fatal("BHLChartandPeriodRESTService:getBHLChartsAndPeriods:: Exception Caught:"+e.getMessage());
				}
			}
			if(prodConnection != null){
				try {
					prodConnection.close();
				} catch (SQLException e) {
					fLogger.fatal("BHLChartandPeriodRESTService:getBHLChartsAndPeriods:: Exception Caught:"+e.getMessage());
				}
			}
		}


		return respObj;

	}

}
