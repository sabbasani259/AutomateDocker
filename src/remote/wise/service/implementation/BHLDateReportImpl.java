/**
 * 
 */
package remote.wise.service.implementation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.ListToStringConversion;

/**
 * @author koti
 *
 */
public class BHLDateReportImpl {

	public List<HashMap<String,Object>> getBHLDateReport(String assetID,String startDate,String endDate, String msgID, String model, String region, String zone, String dealer, String customer){


		List<HashMap<String,Object>> response = new  LinkedList<HashMap<String,Object>>();
		Logger bLogger = BusinessErrorLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		Connection prodConnection = null;
		Statement statement = null;
		ResultSet rs = null;

		String query=null;

		String selectQuery=null;

		String leftQuery=null;
		String rightQuery=null;
		String joinQuery=null;

		String fromQuery=null;

		String leftGroupQuery=null;
		String rightGroupQuery=null;

		DecimalFormat decformat=new DecimalFormat("0.0");

		DateFormat dateStr = new SimpleDateFormat("yyyy-MM-dd");

		Date start=null;
		Date tempDate=null;
		String tempStringDate=null;

		Calendar cal1  = Calendar.getInstance();
		try {
			start = dateStr.parse(startDate);


			cal1.setTime(start);
			//cal1.add(Calendar.DATE, +1);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}



		if(startDate==null || endDate==null)
		{
			bLogger.error("VisualizationDashBoardRESTService:getMachineReports:Mandatory parameters are null");
			return null;
		}

		if(msgID!=null){

			List<String> msgIdList=Arrays.asList(msgID.split(","));

			msgID=new ListToStringConversion().getStringList(msgIdList).toString();
		}
		if(region!=null){

			List<String> regionList=Arrays.asList(region.split(","));

			region=new ListToStringConversion().getStringList(regionList).toString();
		}
		if(zone!=null){

			List<String> zoneList=Arrays.asList(zone.split(","));

			zone=new ListToStringConversion().getStringList(zoneList).toString();
		}
		if(dealer!=null){

			List<String> dealerList=Arrays.asList(dealer.split(","));

			dealer=new ListToStringConversion().getStringList(dealerList).toString();
		}
		if(customer!=null){

			List<String> customerList=Arrays.asList(customer.split(","));

			customer=new ListToStringConversion().getStringList(customerList).toString();
		}
		try {
			if(assetID!=null && (assetID.trim().length()==7 || assetID.trim().length()==17)){
				
				if(assetID.trim().length()==7)
				{
					String machineNumber = assetID;
					assetID = new ExcavatorVisualizationReportImpl().getSerialNumberMachineNumber(machineNumber);
					if(assetID==null)
					{//invalid machine number
						bLogger.error("BHLChartandPeriodRESTService:getBHLChartsAndPeriods: MachineNum"+ machineNumber + "does not exist !!!");
						return null ;
					}
				}
				
				//if(assetID.trim().length()==17){
				query = "select aa.*,bb.* from (select  b.AssetID,b.TxnDate,convert(ifnull((b.ENG_IDL_HRS-b.Column22), 0.0), decimal (10 , 1 )) as DayENG_IDL_HRS," +
						"convert(ifnull((b.Column17-b.Column24), 0.0), decimal (10 , 1 )) as DayRoadingHRS," +
						"convert(ifnull((b.LOAD_JOB_HRS-b.Column25), 0.0), decimal (10 , 1 )) as DayLOAD_JOB_HRS," +
						"convert(ifnull((b.column16-b.Column26), 0.0), decimal (10 , 1 )) as DayExcavationHRS," +
						"convert(ifnull((b.EXA_ECO_MOD_HRS-b.Column27), 0.0), decimal (10 , 1 )) as DayEXA_ECO_MOD_HRS," +
						"convert(ifnull((b.EXA_ACT_MOD_HRS-b.Column28), 0.0), decimal (10 , 1 )) as DayEXA_ACT_MOD_HRS," +
						"convert(ifnull((b.EXA_PWR_MOD_HRS-b.Column29), 0.0), decimal (10 , 1 )) as DayEXA_PWR_MOD_HRS," +
						"convert(ifnull((b.DSC_CON_HRS -b.Column33), 0.0), decimal (10 , 1 )) as DayDSC_CON_HRS," +
						"convert(ifnull((b.FWD_DIR_HRS-b.Column30), 0.0), decimal (10 , 1 )) as DayFWD_DIR_HRS," +
						"convert(ifnull((b.NEU_DIR_HRS-b.Column31), 0.0), decimal (10 , 1 )) as DayNEU_DIR_HRS," +
						"convert(ifnull((b.REV_DIR_HRS-b.Column32), 0.0), decimal (10 , 1 )) as DayREV_DIR_HRS," +
						"ifnull((b.Hyd_Choke_Events-b.Column34) ,0)as DayHyd_Choke_Events," +
						"ifnull((b.No_AutoIdle_Events-b.Column35),0) as DayNo_AutoIdle_Events," +
						"ifnull((b.No_AutoOFF_Events-b.Column36),0) as DayNo_AutoOFF_Events," +
						"convert(ifnull((b.Column10-b.Column23), 0.0), decimal (10 , 1 )) as DayAttachment," +
						"ifnull(b.EngineOnCount,0) as DayEngineOnCount,convert(ifnull((b.Column11), 0.0), decimal (10 , 1 )) as DayGear1," +
						"convert(ifnull((b.Column12), 0.0), decimal (10 , 1 )) as DayGear2,convert(ifnull((b.Column13), 0.0), decimal (10 , 1 )) as DayGear3," +
						"convert(ifnull((b.Column14), 0.0), decimal (10 , 1 )) as DayGear4 " +
						"from factInsight_dayAgg_extended b where b.AssetID='"+assetID+"' and b.TxnDate between '"+startDate+"' and '"+endDate+"' group by b.TxnDate) bb " +
						"inner join "+
						"(select  a.AssetID,a.TxnDate," +
						//"convert(ifnull(a.PeriodHMR, 0.0), decimal (10 , 1 )) as DatePeriodHMR " +
						"convert(ifnull(a.EngineOnTime, 0.0), decimal (10 , 1 )) as DatePeriodHMR " +
						"from factInsight_dayAgg a where a.AssetID='"+assetID+"' and a.TxnDate between '"+startDate+"' and '"+endDate+"' group by a.TxnDate) aa " +
						"ON aa.AssetID=bb.AssetID and aa.TxnDate=bb.TxnDate";
				//}
				/*else if(assetID.trim().length()==7){
					

					query = "select aa.*,bb.* from (select  b.AssetID,b.TxnDate,convert(ifnull((b.ENG_IDL_HRS-b.Column22), 0.0), decimal (10 , 1 )) as DayENG_IDL_HRS," +
							"convert(ifnull((b.Column17-b.Column24), 0.0), decimal (10 , 1 )) as DayRoadingHRS," +
							"convert(ifnull((b.LOAD_JOB_HRS-b.Column25), 0.0), decimal (10 , 1 )) as DayLOAD_JOB_HRS," +
							"convert(ifnull((b.column16-b.Column26), 0.0), decimal (10 , 1 )) as DayExcavationHRS," +
							"convert(ifnull((b.EXA_ECO_MOD_HRS-b.Column27), 0.0), decimal (10 , 1 )) as DayEXA_ECO_MOD_HRS," +
							"convert(ifnull((b.EXA_ACT_MOD_HRS-b.Column28), 0.0), decimal (10 , 1 )) as DayEXA_ACT_MOD_HRS," +
							"convert(ifnull((b.EXA_PWR_MOD_HRS-b.Column29), 0.0), decimal (10 , 1 )) as DayEXA_PWR_MOD_HRS," +
							"convert(ifnull((b.DSC_CON_HRS -b.Column33), 0.0), decimal (10 , 1 )) as DayDSC_CON_HRS," +
							"convert(ifnull((b.FWD_DIR_HRS-b.Column30), 0.0), decimal (10 , 1 )) as DayFWD_DIR_HRS," +
							"convert(ifnull((b.NEU_DIR_HRS-b.Column31), 0.0), decimal (10 , 1 )) as DayNEU_DIR_HRS," +
							"convert(ifnull((b.REV_DIR_HRS-b.Column32), 0.0), decimal (10 , 1 )) as DayREV_DIR_HRS," +
							"ifnull((b.Hyd_Choke_Events-b.Column34) ,0)as DayHyd_Choke_Events," +
							"ifnull((b.No_AutoIdle_Events-b.Column35),0) as DayNo_AutoIdle_Events," +
							"ifnull((b.No_AutoOFF_Events-b.Column36),0) as DayNo_AutoOFF_Events," +
							"convert(ifnull((b.Column10-b.Column23), 0.0), decimal (10 , 1 )) as DayAttachment," +
							"ifnull(b.EngineOnCount,0) as DayEngineOnCount,convert(ifnull((b.Column11), 0.0), decimal (10 , 1 )) as DayGear1," +
							"convert(ifnull((b.Column12), 0.0), decimal (10 , 1 )) as DayGear2,convert(ifnull((b.Column13), 0.0), decimal (10 , 1 )) as DayGear3," +
							"convert(ifnull((b.Column14), 0.0), decimal (10 , 1 )) as DayGear4 " +
							"from factInsight_dayAgg_extended b where b.AssetID like '%"+assetID+"' and b.TxnDate between '"+startDate+"' and '"+endDate+"' group by b.TxnDate) bb " +
							"inner join "+
							"(select  a.AssetID,a.TxnDate,convert(ifnull(a.PeriodHMR, 0.0), decimal (10 , 1 )) as DatePeriodHMR " +
							"from factInsight_dayAgg a where a.AssetID like '%"+assetID+"' and a.TxnDate between '"+startDate+"' and '"+endDate+"' group by a.TxnDate) aa " +
							"ON aa.AssetID=bb.AssetID and aa.TxnDate=bb.TxnDate";
					
					
				}
				else{
					bLogger.error("BHLDateRESTService:getBHLDate: Invalid Serial Number");
					return null;
				}*/

			}
			else{

				selectQuery="select aa.*,bb.* ";
				fromQuery="from ";
				leftQuery="select count(distinct AssetID) as machineCount,b.Column21 as MsgID,b.Column5 as ModelCode," +
						"b.AssetID,b.TxnDate,convert(ifnull(sum(b.ENG_IDL_HRS-b.Column22), 0.0), decimal (10 , 1 )) as DayENG_IDL_HRS," +
						"convert(ifnull(sum(b.Column17-b.Column24), 0.0), decimal (10 , 1 )) as DayRoadingHRS," +
						"convert(ifnull(sum(b.LOAD_JOB_HRS-b.Column25), 0.0), decimal (10 , 1 )) as DayLOAD_JOB_HRS," +
						"convert(ifnull(sum(b.column16-b.Column26), 0.0), decimal (10 , 1 )) as DayExcavationHRS," +
						"convert(ifnull(sum(b.EXA_ECO_MOD_HRS-b.Column27), 0.0), decimal (10 , 1 )) as DayEXA_ECO_MOD_HRS," +
						"convert(ifnull(sum(b.EXA_ACT_MOD_HRS-b.Column28), 0.0), decimal (10 , 1 )) as DayEXA_ACT_MOD_HRS," +
						"convert(ifnull(sum(b.EXA_PWR_MOD_HRS-b.Column29), 0.0), decimal (10 , 1 )) as DayEXA_PWR_MOD_HRS," +
						"convert(ifnull(sum(b.DSC_CON_HRS -b.Column33), 0.0), decimal (10 , 1 )) as DayDSC_CON_HRS," +
						"convert(ifnull(sum(b.FWD_DIR_HRS-b.Column30), 0.0), decimal (10 , 1 )) as DayFWD_DIR_HRS," +
						"convert(ifnull(sum(b.NEU_DIR_HRS-b.Column31), 0.0), decimal (10 , 1 )) as DayNEU_DIR_HRS," +
						"convert(ifnull(sum(b.REV_DIR_HRS-b.Column32), 0.0), decimal (10 , 1 )) as DayREV_DIR_HRS," +
						"ifnull(sum(b.Hyd_Choke_Events-b.Column34) ,0)as DayHyd_Choke_Events," +
						"ifnull(sum(b.No_AutoIdle_Events-b.Column35),0) as DayNo_AutoIdle_Events," +
						"ifnull(sum(b.No_AutoOFF_Events-b.Column36),0) as DayNo_AutoOFF_Events," +
						"convert(ifnull(sum(b.Column10-b.Column23), 0.0), decimal (10 , 1 )) as DayAttachment," +
						"ifnull(sum(b.EngineOnCount),0) as DayEngineOnCount," +
						"convert(ifnull(sum(b.Column11), 0.0), decimal (10 , 1 )) as DayGear1," +
						"convert(ifnull(sum(b.Column12), 0.0), decimal (10 , 1 )) as DayGear2," +
						"convert(ifnull(sum(b.Column13), 0.0), decimal (10 , 1 )) as DayGear3," +
						"convert(ifnull(sum(b.Column14), 0.0), decimal (10 , 1 )) as DayGear4 from factInsight_dayAgg_extended b " +
						"where  b.TxnDate between '"+startDate+"' and '"+endDate+"'";

				leftQuery=leftQuery+" and b.Column21 in ("+msgID+")";

				if(model!=null && ! model.equalsIgnoreCase("null")){

					leftQuery=leftQuery+" and b.Column5='"+model+"'";

				}

				if(region!=null && ! region.equalsIgnoreCase("null")){

					leftQuery=leftQuery+" and b.Column20 in ("+region+")";

				}
				if(zone!=null && ! zone.equalsIgnoreCase("null")){

					leftQuery=leftQuery+" and b.Column7 in ("+zone+")";

				}

				if(dealer!=null && ! dealer.equalsIgnoreCase("null")){

					leftQuery=leftQuery+" and b.Column8 in ("+dealer+")";

				}

				if(customer!=null && ! customer.equalsIgnoreCase("null")){

					leftQuery=leftQuery+" and b.Column9 in ("+customer+")";

				}

				leftGroupQuery=" group by b.TxnDate";

				leftQuery=leftQuery+leftGroupQuery;

				rightQuery="select count(distinct AssetID) as machineCount,a.Column4 as MsgID,a.ModelCode," +
						"a.AssetID,a.TxnDate," +
						//"convert(ifnull(sum(a.PeriodHMR), 0.0), decimal (10 , 1 )) as DatePeriodHMR " +
						"convert(ifnull(sum(a.EngineOnTime), 0.0), decimal (10 , 1 )) as DatePeriodHMR " +
						"from factInsight_dayAgg a where a.TxnDate between '"+startDate+"' and '"+endDate+"'";

				rightQuery=rightQuery+" and a.Column4 in ("+msgID+")";

				if(model!=null && ! model.equalsIgnoreCase("null")){

					rightQuery=rightQuery+" and a.ModelCode='"+model+"'";

				}

				if(region!=null && ! region.equalsIgnoreCase("null")){

					rightQuery=rightQuery+" and a.Column2 in ("+region+")";

				}
				if(zone!=null && ! zone.equalsIgnoreCase("null")){

					rightQuery=rightQuery+" and a.ZonalCode in ("+zone+")";

				}

				if(dealer!=null && ! dealer.equalsIgnoreCase("null")){

					rightQuery=rightQuery+" and a.DealerCode in ("+dealer+")";

				}

				if(customer!=null && ! customer.equalsIgnoreCase("null")){

					rightQuery=rightQuery+" and a.CustCode in ("+customer+")";

				}

				rightGroupQuery=" group by a.TxnDate";

				rightQuery=rightQuery+rightGroupQuery;

				joinQuery="ON aa.TxnDate=bb.TxnDate";


				query=selectQuery+fromQuery+"("+leftQuery+")"+"bb "+"inner join "+"("+rightQuery+")"+"aa "+joinQuery;

			}

			ConnectMySQL connMySql = new ConnectMySQL();
			prodConnection = connMySql.getProdDb2Connection();
			statement = prodConnection.createStatement();
			rs = statement.executeQuery(query);
			HashMap<String, Object> respObj = null;

			while(rs.next()){
				respObj = new HashMap<String, Object>();

				tempDate = cal1.getTime();
				tempStringDate=dateStr.format(tempDate);

				Date currDate=dateStr.parse(rs.getString("TxnDate"));

				while(! tempDate.equals(currDate)){

					HashMap<String, Object> respObj1  = new HashMap<String, Object>();

					//respObj1.put("AssetID",assetID);
					respObj1.put("machineCount", "0");
					respObj1.put("DatePeriodHMR", "0.0");
					respObj1.put("TxnDate", tempStringDate);
					respObj1.put("DayENG_IDL_HRS", "0.0");
					respObj1.put("DayRoadingHRS", "0.0");
					respObj1.put("DayLOAD_JOB_HRS", "0.0");
					respObj1.put("DayExcavationHRS", "0.0");
					respObj1.put("DayEXA_ECO_MOD_HRS", "0.0");
					respObj1.put("DayEXA_ACT_MOD_HRS", "0.0");
					respObj1.put("DayEXA_PWR_MOD_HRS", "0.0");
					respObj1.put("DayDSC_CON_HRS", "0.0");
					respObj1.put("DayFWD_DIR_HRS", "0.0");
					respObj1.put("DayNEU_DIR_HRS", "0.0");
					respObj1.put("DayREV_DIR_HRS", "0.0");
					respObj1.put("DayHyd_Choke_Events", "0");
					respObj1.put("DayNo_AutoIdle_Events", "0");
					respObj1.put("DayNo_AutoOFF_Events", "0");
					respObj1.put("DayAttachment", "0.0");
					respObj1.put("DayEngineOnCount", "0");
					respObj1.put("DayGear1", "0.0");
					respObj1.put("DayGear2", "0.0");
					respObj1.put("DayGear3", "0.0");
					respObj1.put("DayGear4", "0.0");
					response.add(respObj1);

					cal1.add(Calendar.DATE, +1);

					tempDate = cal1.getTime();
					tempStringDate=dateStr.format(tempDate);
				}
				
				int machineCount=0;
				if(assetID==null){
				 machineCount=rs.getInt("machineCount");
				}
				//respObj.put("AssetID",rs.getString("AssetID"));
				respObj.put("TxnDate", rs.getString("TxnDate"));
				respObj.put("machineCount", machineCount);
				
				if(assetID!=null){
				respObj.put("DatePeriodHMR", rs.getDouble("DatePeriodHMR"));
				respObj.put("DayENG_IDL_HRS", rs.getDouble("DayENG_IDL_HRS"));
				respObj.put("DayRoadingHRS", rs.getDouble("DayRoadingHRS"));
				respObj.put("DayLOAD_JOB_HRS", rs.getDouble("DayLOAD_JOB_HRS"));
				respObj.put("DayExcavationHRS", rs.getDouble("DayExcavationHRS"));
				respObj.put("DayEXA_ECO_MOD_HRS", rs.getDouble("DayEXA_ECO_MOD_HRS"));
				respObj.put("DayEXA_ACT_MOD_HRS", rs.getDouble("DayEXA_ACT_MOD_HRS"));
				respObj.put("DayEXA_PWR_MOD_HRS", rs.getDouble("DayEXA_PWR_MOD_HRS"));
				respObj.put("DayDSC_CON_HRS", rs.getDouble("DayDSC_CON_HRS"));
				respObj.put("DayFWD_DIR_HRS", rs.getDouble("DayFWD_DIR_HRS"));
				respObj.put("DayNEU_DIR_HRS", rs.getDouble("DayNEU_DIR_HRS"));
				respObj.put("DayREV_DIR_HRS", rs.getDouble("DayREV_DIR_HRS"));
				respObj.put("DayHyd_Choke_Events", rs.getInt("DayHyd_Choke_Events"));
				respObj.put("DayNo_AutoIdle_Events", rs.getInt("DayNo_AutoIdle_Events"));
				respObj.put("DayNo_AutoOFF_Events", rs.getInt("DayNo_AutoOFF_Events"));
				respObj.put("DayAttachment", rs.getDouble("DayAttachment"));
				respObj.put("DayEngineOnCount", rs.getInt("DayEngineOnCount"));
				respObj.put("DayGear1", rs.getDouble("DayGear1"));
				respObj.put("DayGear2", rs.getDouble("DayGear2"));
				respObj.put("DayGear3", rs.getDouble("DayGear3"));
				respObj.put("DayGear4", rs.getDouble("DayGear4"));
				}
				else{

					respObj.put("DatePeriodHMR", decformat.format(rs.getDouble("DatePeriodHMR")/machineCount));
					respObj.put("DayENG_IDL_HRS", decformat.format(rs.getDouble("DayENG_IDL_HRS")/machineCount));
					respObj.put("DayRoadingHRS", decformat.format(rs.getDouble("DayRoadingHRS")/machineCount));
					respObj.put("DayLOAD_JOB_HRS", decformat.format(rs.getDouble("DayLOAD_JOB_HRS")/machineCount));
					respObj.put("DayExcavationHRS", decformat.format(rs.getDouble("DayExcavationHRS")/machineCount));
					respObj.put("DayEXA_ECO_MOD_HRS", decformat.format(rs.getDouble("DayEXA_ECO_MOD_HRS")/machineCount));
					respObj.put("DayEXA_ACT_MOD_HRS", decformat.format(rs.getDouble("DayEXA_ACT_MOD_HRS")/machineCount));
					respObj.put("DayEXA_PWR_MOD_HRS", decformat.format(rs.getDouble("DayEXA_PWR_MOD_HRS")/machineCount));
					respObj.put("DayDSC_CON_HRS", decformat.format(rs.getDouble("DayDSC_CON_HRS")/machineCount));
					respObj.put("DayFWD_DIR_HRS", decformat.format(rs.getDouble("DayFWD_DIR_HRS")/machineCount));
					respObj.put("DayNEU_DIR_HRS", decformat.format(rs.getDouble("DayNEU_DIR_HRS")/machineCount));
					respObj.put("DayREV_DIR_HRS", decformat.format(rs.getDouble("DayREV_DIR_HRS")/machineCount));
					respObj.put("DayHyd_Choke_Events", (int) Math.round(rs.getDouble("DayHyd_Choke_Events")/machineCount));
					respObj.put("DayNo_AutoIdle_Events", (int) Math.round(rs.getDouble("DayNo_AutoIdle_Events")/machineCount));
					respObj.put("DayNo_AutoOFF_Events", (int) Math.round(rs.getDouble("DayNo_AutoOFF_Events")/machineCount));
					respObj.put("DayAttachment", decformat.format(rs.getDouble("DayAttachment")/machineCount));
					respObj.put("DayEngineOnCount", (int) Math.round(rs.getDouble("DayEngineOnCount")/machineCount));
					respObj.put("DayGear1", decformat.format(rs.getDouble("DayGear1")/machineCount));
					respObj.put("DayGear2", decformat.format(rs.getDouble("DayGear2")/machineCount));
					respObj.put("DayGear3", decformat.format(rs.getDouble("DayGear3")/machineCount));
					respObj.put("DayGear4", decformat.format(rs.getDouble("DayGear4")/machineCount));
					
				}
				response.add(respObj);

				cal1.add(Calendar.DATE, +1);

			}

			/*Date tempDate = cal1.getTime();
			String tempStringDate=dateStr.format(tempDate);
			if(! tempStringDate.equalsIgnoreCase(endDate)){

			}*/

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


		return response;

	}

}
