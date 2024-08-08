/**
 * 
 */
package remote.wise.service.implementation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
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
public class BHLChartandPeriodImpl {

	public HashMap<String,Object> getBHLChartsAndPeriods(String assetID, String startDate, String endDate, String MsgID, String model, String region, String zone, String dealer, String customer){


		HashMap<String, Object> respObj = null;
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

		DecimalFormat decformat=new DecimalFormat("0.0");
		
		//DF20180223 @Roopa Period data logic changes(to match the day wise data)

		double PowerBandLowPerc=0.0;
		double PowerBandMedPerc=0.0;
		double PowerBandHighPerc=0.0;
		double PeriodHMR=0.0;
		double EngineOnTime=0.0;
		double EngineOffTime=0.0;
		double idletime=0.0;
		double WorkingTime=0.0;
		double PeriodIdling=0.0;
		double PeriodAttachment=0.0;
		double PeriodRoading=0.0;
		double PeriodLoading=0.0;
		double PeriodExcavation=0.0;
		double PeriodEconomyMode=0.0;
		double PeriodActiveMode=0.0;
		double PeriodPowerMode=0.0;
		double Gear1=0.0;
		double Gear2=0.0;
		double Gear3=0.0;
		double Gear4=0.0;
		double PeriodForwardDirection=0.0;
		double PeriodNeutralDirection=0.0;
		double PeriodReverseDirection=0.0;
		double PRB1=0.0;
		double PRB2=0.0;
		double PRB3=0.0;
		double PRB4=0.0;
		double PRB5=0.0;
		double PRB6=0.0;
		double PRBEM1=0.0;
		double PRBEM2=0.0;
		double PRBEM3=0.0;
		double PRBEM4=0.0;
		double PRBEM5=0.0;
		double PRBEM6=0.0;
		double PDRBEM1=0.0;
		double PDRBEM2=0.0;
		double PDRBEM3=0.0;
		double PDRBEM4=0.0;
		double PDRBEM5=0.0;
		double PDRBEM6=0.0;
		int PeriodEngineOnCount=0;
		double PeriodDSC_CON_HRS=0;
		int PeriodHyd_Choke_Events=0;
		int PeriodNo_AutoIdle_Events=0;
		int PeriodNo_AutoOFF_Events=0;
		double periodGear1=0.0;
		double periodGear2=0.0;
		double periodGear3=0.0;
		double periodGear4=0.0;
		double PeriodIdlingPerct=0.0;
		double PeriodAttachmentPerct=0.0;
		double PeriodRoadingPerct=0.0;
		double PeriodLoadingPerct=0.0;
		double PeriodExcavationPerct=0.0;
		double PeriodEconomyModePerct=0.0;
		double PeriodActiveModePerct=0.0;
		double PeriodPowerModePerct=0.0;
		double PeriodPRB1=0.0;
		double PeriodPRB2=0.0;
		double PeriodPRB3=0.0;
		double PeriodPRB4=0.0;
		double PeriodPRB5=0.0;
		double PeriodPRB6=0.0;
		double PeriodPRBEM1=0.0;
		double PeriodPRBEM2=0.0;
		double PeriodPRBEM3=0.0;
		double PeriodPRBEM4=0.0;
		double PeriodPRBEM5=0.0;
		double PeriodPRBEM6=0.0;
		double PeriodPDRBEM1=0.0;
		double PeriodPDRBEM2=0.0;
		double PeriodPDRBEM3=0.0;
		double PeriodPDRBEM4=0.0;
		double PeriodPDRBEM5=0.0;
		double PeriodPDRBEM6=0.0;
		double PowerBandLowHrs=0.0;
		double PowerBandMedHrs=0.0;
		double PowerBandHighHrs=0.0;

		/*if(assetID==null)
		{
			bLogger.error("BHLChartandPeriodRESTService:getBHLChartsAndPeriods:Mandatory parameter serial number is null");
			return null;
		}*/
		if(startDate==null || endDate==null)
		{
			bLogger.error("BHLChartandPeriodRESTService:getBHLChartsAndPeriods: Mandatory parameters are null");
			return null;
		}

		if(MsgID!=null){

			List<String> msgIdList=Arrays.asList(MsgID.split(","));

			MsgID=new ListToStringConversion().getStringList(msgIdList).toString();
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
			//Df20171211 @Roopa Changing pump displacement band column
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


				/*query = "Select aa.*,bb.* from "+
						"(Select a.AssetID,a.TxnDate,a.ModelName,ifnull(a.CustomerName,'') as CustomerName,ifnull(a.CustContact,'') as CustContact,ifnull(a.DealerName,'') as DealerName,ifnull(a.ZonalName,'') as ZonalName," +
						"CONVERT(ifnull(sum(a.PowerBandLow)/(sum(a.PowerBandLow)+sum(a.PowerBandMed)+sum(a.PowerBandHigh))*100,'0.0') , decimal(10,1)) as PowerBandLowPerc," +
						"CONVERT(ifnull(sum(a.PowerBandMed)/(sum(a.PowerBandLow)+sum(a.PowerBandMed)+sum(a.PowerBandHigh))*100,'0.0') , decimal(10,1)) as PowerBandMedPerc," +
						"CONVERT(ifnull(sum(a.PowerBandHigh)/(sum(a.PowerBandLow)+sum(a.PowerBandMed)+sum(a.PowerBandHigh))*100,'0.0') , decimal(10,1)) as PowerBandHighPerc," +
						"CONVERT(ifnull(sum(a.PeriodHMR),'0.0'),decimal(10,1)) as PeriodHMR,CONVERT(ifnull(sum(a.EngineOnTime),'0.0') , decimal(10,1)) as EngineOnTime," +
						"convert(ifnull(sum(a.EngineOffTime),'0.0'), decimal(10,1)) as EngineOffTime,convert(ifnull(sum(a.idletime),'0.0'), decimal(10,1))  as idletime," +
						"convert(ifnull(sum(a.WorkingTime),'0.0'), decimal(10,1))  as WorkingTime,"+
						"convert(ifnull(sum(a.PowerBandLow),'0.0'),decimal(10,1)) as PowerBandLowHrs,"+
						"convert(ifnull(sum(a.PowerBandMed),'0.0'),decimal(10,1)) as PowerBandMedHrs,"+
						"convert(ifnull(sum(a.PowerBandHigh),'0.0'),decimal(10,1)) as PowerBandHighHrs"+
						" from factInsight_dayAgg a where a.AssetID='"+assetID+"' and a.TxnDate between '"+startDate+"' and '"+endDate+"') aa inner join"+
						" (select b.AssetID,b.TxnDate, CONVERT(ifnull(sum(b.ENG_IDL_HRS-b.Column22),'0.0'),decimal(10,1)) as PeriodIdling," +
						"CONVERT(ifnull(sum(b.Column10-b.Column23),'0.0'),decimal(10,1)) as PeriodAttachment,CONVERT(ifnull(sum(b.Column17-b.Column24) ,'0.0'),decimal(10,1)) as PeriodRoading," +
						"CONVERT(ifnull(sum(b.LOAD_JOB_HRS-b.Column25),'0.0'),decimal(10,1)) as PeriodLoading,CONVERT(ifnull(sum(b.column16-b.Column26),'0.0'),decimal(10,1)) as PeriodExcavation," +
						"CONVERT(ifnull(sum(b.EXA_ECO_MOD_HRS-b.Column27),'0.0'),decimal(10,1)) as PeriodEconomyMode,CONVERT(ifnull(sum(b.EXA_ACT_MOD_HRS-b.Column28),'0.0'),decimal(10,1)) as PeriodActiveMode,"+
						"CONVERT(ifnull(sum(b.EXA_PWR_MOD_HRS-b.Column29),'0.0'),decimal(10,1)) as PeriodPowerMode,"+
						"convert(ifnull(sum(b.Column11)/(sum(b.Column11)+sum(b.Column12)+sum(b.Column13)+sum(b.Column14))*100,'0'), decimal(10,1)) as Gear1,"+
						"convert(ifnull(sum(b.Column12)/(sum(b.Column11)+sum(b.Column12)+sum(b.Column13)+sum(b.Column14))*100,'0'), decimal(10,1)) as Gear2,"+
						"convert(ifnull(sum(b.Column13)/(sum(b.Column11)+sum(b.Column12)+sum(b.Column13)+sum(b.Column14))*100,'0'), decimal(10,1)) as Gear3,"+
						"convert(ifnull(sum(b.Column14)/(sum(b.Column11)+sum(b.Column12)+sum(b.Column13)+sum(b.Column14))*100,'0'), decimal(10,1)) as Gear4,"+
						"CONVERT(ifnull(sum(b.FWD_DIR_HRS-b.Column30),'0.0'),decimal(10,1)) as PeriodForwardDirection,CONVERT(ifnull(sum(b.NEU_DIR_HRS-b.Column31),'0.0'),decimal(10,1)) as PeriodNeutralDirection," +
						"CONVERT(ifnull(sum(b.REV_DIR_HRS-b.Column32),'0.0'),decimal(10,1)) as PeriodReverseDirection,"+
						"convert( ifnull(sum(b.PRB1)/(sum(b.PRB1)+sum(b.PRB2)+sum(b.PRB3)+sum(b.PRB4)+sum(b.PRB5)+sum(b.PRB6))*100,'0.0') , decimal (10 , 1 )) as PRB1,"+
						"convert( ifnull(sum(b.PRB2)/(sum(b.PRB1)+sum(b.PRB2)+sum(b.PRB3)+sum(b.PRB4)+sum(b.PRB5)+sum(b.PRB6))*100,'0.0') , decimal (10 , 1 )) as PRB2,"+
						"convert( ifnull(sum(b.PRB3)/(sum(b.PRB1)+sum(b.PRB2)+sum(b.PRB3)+sum(b.PRB4)+sum(b.PRB5)+sum(b.PRB6))*100,'0.0') , decimal (10 , 1 )) as PRB3,"+
						"convert( ifnull(sum(b.PRB4)/(sum(b.PRB1)+sum(b.PRB2)+sum(b.PRB3)+sum(b.PRB4)+sum(b.PRB5)+sum(b.PRB6))*100,'0.0') , decimal (10 , 1 )) as PRB4,"+
						"convert( ifnull(sum(b.PRB5)/(sum(b.PRB1)+sum(b.PRB2)+sum(b.PRB3)+sum(b.PRB4)+sum(b.PRB5)+sum(b.PRB6))*100,'0.0') , decimal (10 , 1 )) as PRB5,"+
						"convert( ifnull(sum(b.PRB6)/(sum(b.PRB1)+sum(b.PRB2)+sum(b.PRB3)+sum(b.PRB4)+sum(b.PRB5)+sum(b.PRB6))*100,'0.0') , decimal (10 , 1 )) as PRB6,"+
						"convert( ifnull(sum(b.PRBEM1)/(sum(b.PRBEM1)+sum(b.PRBEM2)+sum(b.PRBEM3)+sum(b.PRBEM4)+sum(b.PRBEM5)+sum(b.PRBEM6))*100,'0.0') , decimal (10 , 1 )) as PRBEM1,"+
						"convert( ifnull(sum(b.PRBEM2)/(sum(b.PRBEM1)+sum(b.PRBEM2)+sum(b.PRBEM3)+sum(b.PRBEM4)+sum(b.PRBEM5)+sum(b.PRBEM6))*100, 0.0) , decimal (10 , 1 )) as PRBEM2,"+
						"convert( ifnull(sum(b.PRBEM3)/(sum(b.PRBEM1)+sum(b.PRBEM2)+sum(b.PRBEM3)+sum(b.PRBEM4)+sum(b.PRBEM5)+sum(b.PRBEM6))*100, 0.0) , decimal (10 , 1 )) as PRBEM3,"+
						"convert( ifnull(sum(b.PRBEM4)/(sum(b.PRBEM1)+sum(b.PRBEM2)+sum(b.PRBEM3)+sum(b.PRBEM4)+sum(b.PRBEM5)+sum(b.PRBEM6))*100, 0.0) , decimal (10 , 1 )) as PRBEM4,"+
						"convert( ifnull(sum(b.PRBEM5)/(sum(b.PRBEM1)+sum(b.PRBEM2)+sum(b.PRBEM3)+sum(b.PRBEM4)+sum(b.PRBEM5)+sum(b.PRBEM6))*100, 0.0) , decimal (10 , 1 )) as PRBEM5,"+
						"convert( ifnull(sum(b.PRBEM6)/(sum(b.PRBEM1)+sum(b.PRBEM2)+sum(b.PRBEM3)+sum(b.PRBEM4)+sum(b.PRBEM5)+sum(b.PRBEM6))*100, 0.0) , decimal (10 , 1 )) as PRBEM6,"+
						"convert(ifnull(sum(b.PDRBLM1)/(sum(b.PDRBLM1)+sum(b.PDRBLM2)+sum(b.PDRBLM3)+sum(b.PDRBLM4)+sum(b.PDRBLM5)+sum(b.PDRBLM6))*100, 0.0), decimal (10 , 1 )) as PDRBEM1,"+
						"convert(ifnull(sum(b.PDRBLM2)/(sum(b.PDRBLM1)+sum(b.PDRBLM2)+sum(b.PDRBLM3)+sum(b.PDRBLM4)+sum(b.PDRBLM5)+sum(b.PDRBLM6))*100, 0.0), decimal (10 , 1 )) as PDRBEM2,"+
						"convert(ifnull(sum(b.PDRBLM3)/(sum(b.PDRBLM1)+sum(b.PDRBLM2)+sum(b.PDRBLM3)+sum(b.PDRBLM4)+sum(b.PDRBLM5)+sum(b.PDRBLM6))*100, 0.0), decimal (10 , 1 )) as PDRBEM3,"+
						"convert(ifnull(sum(b.PDRBLM4)/(sum(b.PDRBLM1)+sum(b.PDRBLM2)+sum(b.PDRBLM3)+sum(b.PDRBLM4)+sum(b.PDRBLM5)+sum(b.PDRBLM6))*100, 0.0), decimal (10 , 1 )) as PDRBEM4,"+
						"convert(ifnull(sum(b.PDRBLM5)/(sum(b.PDRBLM1)+sum(b.PDRBLM2)+sum(b.PDRBLM3)+sum(b.PDRBLM4)+sum(b.PDRBLM5)+sum(b.PDRBLM6))*100, 0.0), decimal (10 , 1 )) as PDRBEM5,"+
						"convert(ifnull(sum(b.PDRBLM6)/(sum(b.PDRBLM1)+sum(b.PDRBLM2)+sum(b.PDRBLM3)+sum(b.PDRBLM4)+sum(b.PDRBLM5)+sum(b.PDRBLM6))*100, 0.0), decimal (10 , 1 )) as PDRBEM6,"+
						"ifnull(sum(b.EngineOnCount), 0) as PeriodEngineOnCount,convert(ifnull(sum(b.DSC_CON_HRS-b.Column33), 0.0), decimal (10 , 1 )) as PeriodDSC_CON_HRS," +
						"ifnull(sum(b.Hyd_Choke_Events-b.Column34), 0) as PeriodHyd_Choke_Events,ifnull(sum(b.No_AutoIdle_Events-b.Column35), 0) as PeriodNo_AutoIdle_Events," +
						"ifnull(sum(b.No_AutoOFF_Events-b.Column36),0) as PeriodNo_AutoOFF_Events,convert(ifnull(sum(b.Column11), 0.0), decimal (10 , 1 )) as periodGear1," +
						"convert(ifnull(sum(b.Column12), 0.0), decimal (10 , 1 )) as periodGear2,convert(ifnull(sum(b.Column13), 0.0), decimal (10 , 1 )) as periodGear3," +
						"convert(ifnull(sum(b.Column14), 0.0), decimal (10 , 1 )) as periodGear4, " +
						"convert(ifnull(sum(b.ENG_IDL_HRS-b.Column22)/(sum(b.ENG_IDL_HRS-b.Column22)+sum(b.Column10-b.Column23)+sum(b.Column17-b.Column24)+sum(b.LOAD_JOB_HRS-b.Column25)+sum(b.column16-b.Column26))*100,'0'), decimal(10,1)) as PeriodIdlingPerct,"+
						"convert(ifnull(sum(b.Column10-b.Column23)/(sum(b.ENG_IDL_HRS-b.Column22)+sum(b.Column10-b.Column23)+sum(b.Column17-b.Column24)+sum(b.LOAD_JOB_HRS-b.Column25)+sum(b.column16-b.Column26))*100,'0'), decimal(10,1)) as PeriodAttachmentPerct,"+
						"convert(ifnull(sum(b.Column17-b.Column24)/(sum(b.ENG_IDL_HRS-b.Column22)+sum(b.Column10-b.Column23)+sum(b.Column17-b.Column24)+sum(b.LOAD_JOB_HRS-b.Column25)+sum(b.column16-b.Column26))*100,'0'), decimal(10,1)) as PeriodRoadingPerct,"+
						"convert(ifnull(sum(b.LOAD_JOB_HRS-b.Column25)/(sum(b.ENG_IDL_HRS-b.Column22)+sum(b.Column10-b.Column23)+sum(b.Column17-b.Column24)+sum(b.LOAD_JOB_HRS-b.Column25)+sum(b.column16-b.Column26))*100,'0'), decimal(10,1)) as PeriodLoadingPerct,"+
						"convert(ifnull(sum(b.column16-b.Column26)/(sum(b.ENG_IDL_HRS-b.Column22)+sum(b.Column10-b.Column23)+sum(b.Column17-b.Column24)+sum(b.LOAD_JOB_HRS-b.Column25)+sum(b.column16-b.Column26))*100,'0'), decimal(10,1)) as PeriodExcavationPerct,"+
						"convert(ifnull(sum(b.EXA_ECO_MOD_HRS-b.Column27)/(sum(b.EXA_ECO_MOD_HRS-b.Column27)+sum(b.EXA_ACT_MOD_HRS-b.Column28)+sum(b.EXA_PWR_MOD_HRS-b.Column29))*100,'0'), decimal(10,1)) as PeriodEconomyModePerct,"+
						"convert(ifnull(sum(b.EXA_ACT_MOD_HRS-b.Column28)/(sum(b.EXA_ECO_MOD_HRS-b.Column27)+sum(b.EXA_ACT_MOD_HRS-b.Column28)+sum(b.EXA_PWR_MOD_HRS-b.Column29))*100,'0'), decimal(10,1)) as PeriodActiveModePerct,"+
						"convert(ifnull(sum(b.EXA_PWR_MOD_HRS-b.Column29)/(sum(b.EXA_ECO_MOD_HRS-b.Column27)+sum(b.EXA_ACT_MOD_HRS-b.Column28)+sum(b.EXA_PWR_MOD_HRS-b.Column29))*100,'0'), decimal(10,1)) as PeriodPowerModePerct,"+
						"CONVERT(ifnull(sum(b.PRB1),'0.0'),decimal(10,1)) as PeriodPRB1,"+
						"CONVERT(ifnull(sum(b.PRB2),'0.0'),decimal(10,1)) as PeriodPRB2,"+
						"CONVERT(ifnull(sum(b.PRB3),'0.0'),decimal(10,1)) as PeriodPRB3,"+
						"CONVERT(ifnull(sum(b.PRB4),'0.0'),decimal(10,1)) as PeriodPRB4,"+
						"CONVERT(ifnull(sum(b.PRB5),'0.0'),decimal(10,1)) as PeriodPRB5,"+
						"CONVERT(ifnull(sum(b.PRB6),'0.0'),decimal(10,1)) as PeriodPRB6,"+
						"CONVERT(ifnull(sum(b.PRBEM1),'0.0'),decimal(10,1)) as PeriodPRBEM1,"+
						"CONVERT(ifnull(sum(b.PRBEM2),'0.0'),decimal(10,1)) as PeriodPRBEM2,"+
						"CONVERT(ifnull(sum(b.PRBEM3),'0.0'),decimal(10,1)) as PeriodPRBEM3,"+
						"CONVERT(ifnull(sum(b.PRBEM4),'0.0'),decimal(10,1)) as PeriodPRBEM4,"+
						"CONVERT(ifnull(sum(b.PRBEM5),'0.0'),decimal(10,1)) as PeriodPRBEM5,"+
						"CONVERT(ifnull(sum(b.PRBEM6),'0.0'),decimal(10,1)) as PeriodPRBEM6,"+
						"CONVERT(ifnull(sum(b.PDRBLM1),'0.0'),decimal(10,1)) as PeriodPDRBEM1,"+
						"CONVERT(ifnull(sum(b.PDRBLM2),'0.0'),decimal(10,1)) as PeriodPDRBEM2,"+
						"CONVERT(ifnull(sum(b.PDRBLM3),'0.0'),decimal(10,1)) as PeriodPDRBEM3,"+
						"CONVERT(ifnull(sum(b.PDRBLM4),'0.0'),decimal(10,1)) as PeriodPDRBEM4,"+
						"CONVERT(ifnull(sum(b.PDRBLM5),'0.0'),decimal(10,1)) as PeriodPDRBEM5,"+
						"CONVERT(ifnull(sum(b.PDRBLM6),'0.0'),decimal(10,1)) as PeriodPDRBEM6"+
						" from factInsight_dayAgg_extended b where b.AssetID='"+assetID+"' and b.TxnDate between '"+startDate+"' and '"+endDate+"') bb " +
						"ON aa.AssetID=bb.AssetID";*/

				query = "Select aa.*,bb.* from "+
						"(Select a.AssetID,a.TxnDate,a.ModelName,ifnull(a.CustomerName,'') as CustomerName,ifnull(a.CustContact,'') as CustContact,ifnull(a.DealerName,'') as DealerName,ifnull(a.ZonalName,'') as ZonalName," +
						"CONVERT(ifnull((a.PowerBandLow)/((a.PowerBandLow)+(a.PowerBandMed)+(a.PowerBandHigh))*100,'0.0') , decimal(10,1)) as PowerBandLowPerc," +
						"CONVERT(ifnull((a.PowerBandMed)/((a.PowerBandLow)+(a.PowerBandMed)+(a.PowerBandHigh))*100,'0.0') , decimal(10,1)) as PowerBandMedPerc," +
						"CONVERT(ifnull((a.PowerBandHigh)/((a.PowerBandLow)+(a.PowerBandMed)+(a.PowerBandHigh))*100,'0.0') , decimal(10,1)) as PowerBandHighPerc," +
						//"CONVERT(ifnull((a.PeriodHMR),'0.0'),decimal(10,1)) as PeriodHMR," + //Df20180312 @Roopa taking running band data for period CMH calc
						"CONVERT(ifnull((a.EngineOnTime),'0.0') , decimal(10,1)) as PeriodHMR," +
						"CONVERT(ifnull((a.EngineOnTime),'0.0') , decimal(10,1)) as EngineOnTime," +
						"convert(ifnull((a.EngineOffTime),'0.0'), decimal(10,1)) as EngineOffTime,convert(ifnull((a.idletime),'0.0'), decimal(10,1))  as idletime," +
						"convert(ifnull((a.WorkingTime),'0.0'), decimal(10,1))  as WorkingTime,"+
						"convert(ifnull((a.PowerBandLow),'0.0'),decimal(10,1)) as PowerBandLowHrs,"+
						"convert(ifnull((a.PowerBandMed),'0.0'),decimal(10,1)) as PowerBandMedHrs,"+
						"convert(ifnull((a.PowerBandHigh),'0.0'),decimal(10,1)) as PowerBandHighHrs"+
						" from factInsight_dayAgg a where a.AssetID='"+assetID+"' and a.TxnDate between '"+startDate+"' and '"+endDate+"' group by a.TxnDate) aa inner join"+
						" (select b.AssetID,b.TxnDate, CONVERT(ifnull((b.ENG_IDL_HRS-b.Column22),'0.0'),decimal(10,1)) as PeriodIdling," +
						"CONVERT(ifnull((b.Column10-b.Column23),'0.0'),decimal(10,1)) as PeriodAttachment,CONVERT(ifnull((b.Column17-b.Column24) ,'0.0'),decimal(10,1)) as PeriodRoading," +
						"CONVERT(ifnull((b.LOAD_JOB_HRS-b.Column25),'0.0'),decimal(10,1)) as PeriodLoading,CONVERT(ifnull((b.column16-b.Column26),'0.0'),decimal(10,1)) as PeriodExcavation," +
						"CONVERT(ifnull((b.EXA_ECO_MOD_HRS-b.Column27),'0.0'),decimal(10,1)) as PeriodEconomyMode,CONVERT(ifnull((b.EXA_ACT_MOD_HRS-b.Column28),'0.0'),decimal(10,1)) as PeriodActiveMode,"+
						"CONVERT(ifnull((b.EXA_PWR_MOD_HRS-b.Column29),'0.0'),decimal(10,1)) as PeriodPowerMode,"+
						"convert(ifnull((b.Column11)/((b.Column11)+(b.Column12)+(b.Column13)+(b.Column14))*100,'0'), decimal(10,1)) as Gear1,"+
						"convert(ifnull((b.Column12)/((b.Column11)+(b.Column12)+(b.Column13)+(b.Column14))*100,'0'), decimal(10,1)) as Gear2,"+
						"convert(ifnull((b.Column13)/((b.Column11)+(b.Column12)+(b.Column13)+(b.Column14))*100,'0'), decimal(10,1)) as Gear3,"+
						"convert(ifnull((b.Column14)/((b.Column11)+(b.Column12)+(b.Column13)+(b.Column14))*100,'0'), decimal(10,1)) as Gear4,"+
						"CONVERT(ifnull((b.FWD_DIR_HRS-b.Column30),'0.0'),decimal(10,1)) as PeriodForwardDirection,CONVERT(ifnull((b.NEU_DIR_HRS-b.Column31),'0.0'),decimal(10,1)) as PeriodNeutralDirection," +
						"CONVERT(ifnull((b.REV_DIR_HRS-b.Column32),'0.0'),decimal(10,1)) as PeriodReverseDirection,"+
						"convert( ifnull((b.PRB1)/((b.PRB1)+(b.PRB2)+(b.PRB3)+(b.PRB4)+(b.PRB5)+(b.PRB6))*100,'0.0') , decimal (10 , 1 )) as PRB1,"+
						"convert( ifnull((b.PRB2)/((b.PRB1)+(b.PRB2)+(b.PRB3)+(b.PRB4)+(b.PRB5)+(b.PRB6))*100,'0.0') , decimal (10 , 1 )) as PRB2,"+
						"convert( ifnull((b.PRB3)/((b.PRB1)+(b.PRB2)+(b.PRB3)+(b.PRB4)+(b.PRB5)+(b.PRB6))*100,'0.0') , decimal (10 , 1 )) as PRB3,"+
						"convert( ifnull((b.PRB4)/((b.PRB1)+(b.PRB2)+(b.PRB3)+(b.PRB4)+(b.PRB5)+(b.PRB6))*100,'0.0') , decimal (10 , 1 )) as PRB4,"+
						"convert( ifnull((b.PRB5)/((b.PRB1)+(b.PRB2)+(b.PRB3)+(b.PRB4)+(b.PRB5)+(b.PRB6))*100,'0.0') , decimal (10 , 1 )) as PRB5,"+
						"convert( ifnull((b.PRB6)/((b.PRB1)+(b.PRB2)+(b.PRB3)+(b.PRB4)+(b.PRB5)+(b.PRB6))*100,'0.0') , decimal (10 , 1 )) as PRB6,"+
						"convert( ifnull((b.PRBEM1)/((b.PRBEM1)+(b.PRBEM2)+(b.PRBEM3)+(b.PRBEM4)+(b.PRBEM5)+(b.PRBEM6))*100,'0.0') , decimal (10 , 1 )) as PRBEM1,"+
						"convert( ifnull((b.PRBEM2)/((b.PRBEM1)+(b.PRBEM2)+(b.PRBEM3)+(b.PRBEM4)+(b.PRBEM5)+(b.PRBEM6))*100, 0.0) , decimal (10 , 1 )) as PRBEM2,"+
						"convert( ifnull((b.PRBEM3)/((b.PRBEM1)+(b.PRBEM2)+(b.PRBEM3)+(b.PRBEM4)+(b.PRBEM5)+(b.PRBEM6))*100, 0.0) , decimal (10 , 1 )) as PRBEM3,"+
						"convert( ifnull((b.PRBEM4)/((b.PRBEM1)+(b.PRBEM2)+(b.PRBEM3)+(b.PRBEM4)+(b.PRBEM5)+(b.PRBEM6))*100, 0.0) , decimal (10 , 1 )) as PRBEM4,"+
						"convert( ifnull((b.PRBEM5)/((b.PRBEM1)+(b.PRBEM2)+(b.PRBEM3)+(b.PRBEM4)+(b.PRBEM5)+(b.PRBEM6))*100, 0.0) , decimal (10 , 1 )) as PRBEM5,"+
						"convert( ifnull((b.PRBEM6)/((b.PRBEM1)+(b.PRBEM2)+(b.PRBEM3)+(b.PRBEM4)+(b.PRBEM5)+(b.PRBEM6))*100, 0.0) , decimal (10 , 1 )) as PRBEM6,"+
						"convert(ifnull((b.PDRBLM1)/((b.PDRBLM1)+(b.PDRBLM2)+(b.PDRBLM3)+(b.PDRBLM4)+(b.PDRBLM5)+(b.PDRBLM6))*100, 0.0), decimal (10 , 1 )) as PDRBEM1,"+
						"convert(ifnull((b.PDRBLM2)/((b.PDRBLM1)+(b.PDRBLM2)+(b.PDRBLM3)+(b.PDRBLM4)+(b.PDRBLM5)+(b.PDRBLM6))*100, 0.0), decimal (10 , 1 )) as PDRBEM2,"+
						"convert(ifnull((b.PDRBLM3)/((b.PDRBLM1)+(b.PDRBLM2)+(b.PDRBLM3)+(b.PDRBLM4)+(b.PDRBLM5)+(b.PDRBLM6))*100, 0.0), decimal (10 , 1 )) as PDRBEM3,"+
						"convert(ifnull((b.PDRBLM4)/((b.PDRBLM1)+(b.PDRBLM2)+(b.PDRBLM3)+(b.PDRBLM4)+(b.PDRBLM5)+(b.PDRBLM6))*100, 0.0), decimal (10 , 1 )) as PDRBEM4,"+
						"convert(ifnull((b.PDRBLM5)/((b.PDRBLM1)+(b.PDRBLM2)+(b.PDRBLM3)+(b.PDRBLM4)+(b.PDRBLM5)+(b.PDRBLM6))*100, 0.0), decimal (10 , 1 )) as PDRBEM5,"+
						"convert(ifnull((b.PDRBLM6)/((b.PDRBLM1)+(b.PDRBLM2)+(b.PDRBLM3)+(b.PDRBLM4)+(b.PDRBLM5)+(b.PDRBLM6))*100, 0.0), decimal (10 , 1 )) as PDRBEM6,"+
						"ifnull((b.EngineOnCount), 0) as PeriodEngineOnCount,convert(ifnull((b.DSC_CON_HRS-b.Column33), 0.0), decimal (10 , 1 )) as PeriodDSC_CON_HRS," +
						"ifnull((b.Hyd_Choke_Events-b.Column34), 0) as PeriodHyd_Choke_Events,ifnull((b.No_AutoIdle_Events-b.Column35), 0) as PeriodNo_AutoIdle_Events," +
						"ifnull((b.No_AutoOFF_Events-b.Column36),0) as PeriodNo_AutoOFF_Events,convert(ifnull((b.Column11), 0.0), decimal (10 , 1 )) as periodGear1," +
						"convert(ifnull((b.Column12), 0.0), decimal (10 , 1 )) as periodGear2,convert(ifnull((b.Column13), 0.0), decimal (10 , 1 )) as periodGear3," +
						"convert(ifnull((b.Column14), 0.0), decimal (10 , 1 )) as periodGear4, " +
						"convert(ifnull((b.ENG_IDL_HRS-b.Column22)/((b.ENG_IDL_HRS-b.Column22)+(b.Column10-b.Column23)+(b.Column17-b.Column24)+(b.LOAD_JOB_HRS-b.Column25)+(b.column16-b.Column26))*100,'0'), decimal(10,1)) as PeriodIdlingPerct,"+
						"convert(ifnull((b.Column10-b.Column23)/((b.ENG_IDL_HRS-b.Column22)+(b.Column10-b.Column23)+(b.Column17-b.Column24)+(b.LOAD_JOB_HRS-b.Column25)+(b.column16-b.Column26))*100,'0'), decimal(10,1)) as PeriodAttachmentPerct,"+
						"convert(ifnull((b.Column17-b.Column24)/((b.ENG_IDL_HRS-b.Column22)+(b.Column10-b.Column23)+(b.Column17-b.Column24)+(b.LOAD_JOB_HRS-b.Column25)+(b.column16-b.Column26))*100,'0'), decimal(10,1)) as PeriodRoadingPerct,"+
						"convert(ifnull((b.LOAD_JOB_HRS-b.Column25)/((b.ENG_IDL_HRS-b.Column22)+(b.Column10-b.Column23)+(b.Column17-b.Column24)+(b.LOAD_JOB_HRS-b.Column25)+(b.column16-b.Column26))*100,'0'), decimal(10,1)) as PeriodLoadingPerct,"+
						"convert(ifnull((b.column16-b.Column26)/((b.ENG_IDL_HRS-b.Column22)+(b.Column10-b.Column23)+(b.Column17-b.Column24)+(b.LOAD_JOB_HRS-b.Column25)+(b.column16-b.Column26))*100,'0'), decimal(10,1)) as PeriodExcavationPerct,"+
						"convert(ifnull((b.EXA_ECO_MOD_HRS-b.Column27)/((b.EXA_ECO_MOD_HRS-b.Column27)+(b.EXA_ACT_MOD_HRS-b.Column28)+(b.EXA_PWR_MOD_HRS-b.Column29))*100,'0'), decimal(10,1)) as PeriodEconomyModePerct,"+
						"convert(ifnull((b.EXA_ACT_MOD_HRS-b.Column28)/((b.EXA_ECO_MOD_HRS-b.Column27)+(b.EXA_ACT_MOD_HRS-b.Column28)+(b.EXA_PWR_MOD_HRS-b.Column29))*100,'0'), decimal(10,1)) as PeriodActiveModePerct,"+
						"convert(ifnull((b.EXA_PWR_MOD_HRS-b.Column29)/((b.EXA_ECO_MOD_HRS-b.Column27)+(b.EXA_ACT_MOD_HRS-b.Column28)+(b.EXA_PWR_MOD_HRS-b.Column29))*100,'0'), decimal(10,1)) as PeriodPowerModePerct,"+
						"CONVERT(ifnull((b.PRB1),'0.0'),decimal(10,1)) as PeriodPRB1,"+
						"CONVERT(ifnull((b.PRB2),'0.0'),decimal(10,1)) as PeriodPRB2,"+
						"CONVERT(ifnull((b.PRB3),'0.0'),decimal(10,1)) as PeriodPRB3,"+
						"CONVERT(ifnull((b.PRB4),'0.0'),decimal(10,1)) as PeriodPRB4,"+
						"CONVERT(ifnull((b.PRB5),'0.0'),decimal(10,1)) as PeriodPRB5,"+
						"CONVERT(ifnull((b.PRB6),'0.0'),decimal(10,1)) as PeriodPRB6,"+
						"CONVERT(ifnull((b.PRBEM1),'0.0'),decimal(10,1)) as PeriodPRBEM1,"+
						"CONVERT(ifnull((b.PRBEM2),'0.0'),decimal(10,1)) as PeriodPRBEM2,"+
						"CONVERT(ifnull((b.PRBEM3),'0.0'),decimal(10,1)) as PeriodPRBEM3,"+
						"CONVERT(ifnull((b.PRBEM4),'0.0'),decimal(10,1)) as PeriodPRBEM4,"+
						"CONVERT(ifnull((b.PRBEM5),'0.0'),decimal(10,1)) as PeriodPRBEM5,"+
						"CONVERT(ifnull((b.PRBEM6),'0.0'),decimal(10,1)) as PeriodPRBEM6,"+
						"CONVERT(ifnull((b.PDRBLM1),'0.0'),decimal(10,1)) as PeriodPDRBEM1,"+
						"CONVERT(ifnull((b.PDRBLM2),'0.0'),decimal(10,1)) as PeriodPDRBEM2,"+
						"CONVERT(ifnull((b.PDRBLM3),'0.0'),decimal(10,1)) as PeriodPDRBEM3,"+
						"CONVERT(ifnull((b.PDRBLM4),'0.0'),decimal(10,1)) as PeriodPDRBEM4,"+
						"CONVERT(ifnull((b.PDRBLM5),'0.0'),decimal(10,1)) as PeriodPDRBEM5,"+
						"CONVERT(ifnull((b.PDRBLM6),'0.0'),decimal(10,1)) as PeriodPDRBEM6"+
						" from factInsight_dayAgg_extended b where b.AssetID='"+assetID+"' and b.TxnDate between '"+startDate+"' and '"+endDate+"' group by b.TxnDate) bb " +
						"ON aa.AssetID=bb.AssetID and aa.TxnDate=bb.TxnDate";


			}
			else{

				selectQuery="Select aa.*,bb.* ";
				fromQuery="from ";
				leftQuery="Select count(distinct AssetID) as machineCount,a.Column4 as MsgID,a.ModelCode, a.AssetID,a.TxnDate,a.ModelName,ifnull(a.CustomerName,'') as CustomerName,ifnull(a.CustContact,'') as CustContact,ifnull(a.DealerName,'') as DealerName,ifnull(a.ZonalName,'') as ZonalName," +
						"CONVERT(ifnull(sum(a.PowerBandLow)/(sum(a.PowerBandLow)+sum(a.PowerBandMed)+sum(a.PowerBandHigh))*100,'0.0') , decimal(10,1)) as PowerBandLowPerc," +
						"CONVERT(ifnull(sum(a.PowerBandMed)/(sum(a.PowerBandLow)+sum(a.PowerBandMed)+sum(a.PowerBandHigh))*100,'0.0') , decimal(10,1)) as PowerBandMedPerc," +
						"CONVERT(ifnull(sum(a.PowerBandHigh)/(sum(a.PowerBandLow)+sum(a.PowerBandMed)+sum(a.PowerBandHigh))*100,'0.0') , decimal(10,1)) as PowerBandHighPerc," +
						//"CONVERT(ifnull(sum(a.PeriodHMR),'0.0'),decimal(10,1)) as PeriodHMR," + //Df20180312 @Roopa taking running band data for period CMH calc
						"CONVERT(ifnull(sum(a.EngineOnTime),'0.0') , decimal(10,1)) as PeriodHMR," +
						"CONVERT(ifnull(sum(a.EngineOnTime),'0.0') , decimal(10,1)) as EngineOnTime," +
						"convert(ifnull(sum(a.EngineOffTime),'0.0'), decimal(10,1)) as EngineOffTime,convert(ifnull(sum(a.idletime),'0.0'), decimal(10,1))  as idletime," +
						"convert(ifnull(sum(a.WorkingTime),'0.0'), decimal(10,1))  as WorkingTime,"+
						"convert(ifnull(sum(a.PowerBandLow),'0.0'),decimal(10,1)) as PowerBandLowHrs,"+
						"convert(ifnull(sum(a.PowerBandMed),'0.0'),decimal(10,1)) as PowerBandMedHrs,"+
						"convert(ifnull(sum(a.PowerBandHigh),'0.0'),decimal(10,1)) as PowerBandHighHrs"+
						" from factInsight_dayAgg a where a.TxnDate between '"+startDate+"' and '"+endDate+"'";

				leftQuery=leftQuery+" and a.Column4 in ("+MsgID+")";

				if(model!=null && ! model.equalsIgnoreCase("null")){

					leftQuery=leftQuery+" and a.ModelCode='"+model+"'";

				}

				if(region!=null && ! region.equalsIgnoreCase("null")){

					leftQuery=leftQuery+" and a.Column2 in ("+region+")";

				}
				if(zone!=null && ! zone.equalsIgnoreCase("null")){

					leftQuery=leftQuery+" and a.ZonalCode in ("+zone+")";

				}

				if(dealer!=null && ! dealer.equalsIgnoreCase("null")){

					leftQuery=leftQuery+" and a.DealerCode in ("+dealer+")";

				}

				if(customer!=null && ! customer.equalsIgnoreCase("null")){

					leftQuery=leftQuery+" and a.CustCode in ("+customer+")";

				}

				rightQuery="select count(distinct AssetID) as machineCount,b.Column21 as MsgID,b.Column5 as ModelCode,b.AssetID,b.TxnDate, CONVERT(ifnull(sum(b.ENG_IDL_HRS-b.Column22),'0.0'),decimal(10,1)) as PeriodIdling," +
						"CONVERT(ifnull(sum(b.Column10-b.Column23),'0.0'),decimal(10,1)) as PeriodAttachment,CONVERT(ifnull(sum(b.Column17-b.Column24) ,'0.0'),decimal(10,1)) as PeriodRoading," +
						"CONVERT(ifnull(sum(b.LOAD_JOB_HRS-b.Column25),'0.0'),decimal(10,1)) as PeriodLoading,CONVERT(ifnull(sum(b.column16-b.Column26),'0.0'),decimal(10,1)) as PeriodExcavation," +
						"CONVERT(ifnull(sum(b.EXA_ECO_MOD_HRS-b.Column27),'0.0'),decimal(10,1)) as PeriodEconomyMode,CONVERT(ifnull(sum(b.EXA_ACT_MOD_HRS-b.Column28),'0.0'),decimal(10,1)) as PeriodActiveMode,"+
						"CONVERT(ifnull(sum(b.EXA_PWR_MOD_HRS-b.Column29),'0.0'),decimal(10,1)) as PeriodPowerMode,"+
						"convert(ifnull(sum(b.Column11)/(sum(b.Column11)+sum(b.Column12)+sum(b.Column13)+sum(b.Column14))*100,'0'), decimal(10,1)) as Gear1,"+
						"convert(ifnull(sum(b.Column12)/(sum(b.Column11)+sum(b.Column12)+sum(b.Column13)+sum(b.Column14))*100,'0'), decimal(10,1)) as Gear2,"+
						"convert(ifnull(sum(b.Column13)/(sum(b.Column11)+sum(b.Column12)+sum(b.Column13)+sum(b.Column14))*100,'0'), decimal(10,1)) as Gear3,"+
						"convert(ifnull(sum(b.Column14)/(sum(b.Column11)+sum(b.Column12)+sum(b.Column13)+sum(b.Column14))*100,'0'), decimal(10,1)) as Gear4,"+
						"CONVERT(ifnull(sum(b.FWD_DIR_HRS-b.Column30),'0.0'),decimal(10,1)) as PeriodForwardDirection,CONVERT(ifnull(sum(b.NEU_DIR_HRS-b.Column31),'0.0'),decimal(10,1)) as PeriodNeutralDirection," +
						"CONVERT(ifnull(sum(b.REV_DIR_HRS-b.Column32),'0.0'),decimal(10,1)) as PeriodReverseDirection,"+
						"convert( ifnull(sum(b.PRB1)/(sum(b.PRB1)+sum(b.PRB2)+sum(b.PRB3)+sum(b.PRB4)+sum(b.PRB5)+sum(b.PRB6))*100,'0.0') , decimal (10 , 1 )) as PRB1,"+
						"convert( ifnull(sum(b.PRB2)/(sum(b.PRB1)+sum(b.PRB2)+sum(b.PRB3)+sum(b.PRB4)+sum(b.PRB5)+sum(b.PRB6))*100,'0.0') , decimal (10 , 1 )) as PRB2,"+
						"convert( ifnull(sum(b.PRB3)/(sum(b.PRB1)+sum(b.PRB2)+sum(b.PRB3)+sum(b.PRB4)+sum(b.PRB5)+sum(b.PRB6))*100,'0.0') , decimal (10 , 1 )) as PRB3,"+
						"convert( ifnull(sum(b.PRB4)/(sum(b.PRB1)+sum(b.PRB2)+sum(b.PRB3)+sum(b.PRB4)+sum(b.PRB5)+sum(b.PRB6))*100,'0.0') , decimal (10 , 1 )) as PRB4,"+
						"convert( ifnull(sum(b.PRB5)/(sum(b.PRB1)+sum(b.PRB2)+sum(b.PRB3)+sum(b.PRB4)+sum(b.PRB5)+sum(b.PRB6))*100,'0.0') , decimal (10 , 1 )) as PRB5,"+
						"convert( ifnull(sum(b.PRB6)/(sum(b.PRB1)+sum(b.PRB2)+sum(b.PRB3)+sum(b.PRB4)+sum(b.PRB5)+sum(b.PRB6))*100,'0.0') , decimal (10 , 1 )) as PRB6,"+
						"convert( ifnull(sum(b.PRBEM1)/(sum(b.PRBEM1)+sum(b.PRBEM2)+sum(b.PRBEM3)+sum(b.PRBEM4)+sum(b.PRBEM5)+sum(b.PRBEM6))*100,'0.0') , decimal (10 , 1 )) as PRBEM1,"+
						"convert( ifnull(sum(b.PRBEM2)/(sum(b.PRBEM1)+sum(b.PRBEM2)+sum(b.PRBEM3)+sum(b.PRBEM4)+sum(b.PRBEM5)+sum(b.PRBEM6))*100, 0.0) , decimal (10 , 1 )) as PRBEM2,"+
						"convert( ifnull(sum(b.PRBEM3)/(sum(b.PRBEM1)+sum(b.PRBEM2)+sum(b.PRBEM3)+sum(b.PRBEM4)+sum(b.PRBEM5)+sum(b.PRBEM6))*100, 0.0) , decimal (10 , 1 )) as PRBEM3,"+
						"convert( ifnull(sum(b.PRBEM4)/(sum(b.PRBEM1)+sum(b.PRBEM2)+sum(b.PRBEM3)+sum(b.PRBEM4)+sum(b.PRBEM5)+sum(b.PRBEM6))*100, 0.0) , decimal (10 , 1 )) as PRBEM4,"+
						"convert( ifnull(sum(b.PRBEM5)/(sum(b.PRBEM1)+sum(b.PRBEM2)+sum(b.PRBEM3)+sum(b.PRBEM4)+sum(b.PRBEM5)+sum(b.PRBEM6))*100, 0.0) , decimal (10 , 1 )) as PRBEM5,"+
						"convert( ifnull(sum(b.PRBEM6)/(sum(b.PRBEM1)+sum(b.PRBEM2)+sum(b.PRBEM3)+sum(b.PRBEM4)+sum(b.PRBEM5)+sum(b.PRBEM6))*100, 0.0) , decimal (10 , 1 )) as PRBEM6,"+
						"convert(ifnull(sum(b.PDRBLM1)/(sum(b.PDRBLM1)+sum(b.PDRBLM2)+sum(b.PDRBLM3)+sum(b.PDRBLM4)+sum(b.PDRBLM5)+sum(b.PDRBLM6))*100, 0.0), decimal (10 , 1 )) as PDRBEM1,"+
						"convert(ifnull(sum(b.PDRBLM2)/(sum(b.PDRBLM1)+sum(b.PDRBLM2)+sum(b.PDRBLM3)+sum(b.PDRBLM4)+sum(b.PDRBLM5)+sum(b.PDRBLM6))*100, 0.0), decimal (10 , 1 )) as PDRBEM2,"+
						"convert(ifnull(sum(b.PDRBLM3)/(sum(b.PDRBLM1)+sum(b.PDRBLM2)+sum(b.PDRBLM3)+sum(b.PDRBLM4)+sum(b.PDRBLM5)+sum(b.PDRBLM6))*100, 0.0), decimal (10 , 1 )) as PDRBEM3,"+
						"convert(ifnull(sum(b.PDRBLM4)/(sum(b.PDRBLM1)+sum(b.PDRBLM2)+sum(b.PDRBLM3)+sum(b.PDRBLM4)+sum(b.PDRBLM5)+sum(b.PDRBLM6))*100, 0.0), decimal (10 , 1 )) as PDRBEM4,"+
						"convert(ifnull(sum(b.PDRBLM5)/(sum(b.PDRBLM1)+sum(b.PDRBLM2)+sum(b.PDRBLM3)+sum(b.PDRBLM4)+sum(b.PDRBLM5)+sum(b.PDRBLM6))*100, 0.0), decimal (10 , 1 )) as PDRBEM5,"+
						"convert(ifnull(sum(b.PDRBLM6)/(sum(b.PDRBLM1)+sum(b.PDRBLM2)+sum(b.PDRBLM3)+sum(b.PDRBLM4)+sum(b.PDRBLM5)+sum(b.PDRBLM6))*100, 0.0), decimal (10 , 1 )) as PDRBEM6,"+
						"ifnull(sum(b.EngineOnCount), 0) as PeriodEngineOnCount,convert(ifnull(sum(b.DSC_CON_HRS-b.Column33), 0.0), decimal (10 , 1 )) as PeriodDSC_CON_HRS," +
						"ifnull(sum(b.Hyd_Choke_Events-b.Column34), 0) as PeriodHyd_Choke_Events,ifnull(sum(b.No_AutoIdle_Events-b.Column35), 0) as PeriodNo_AutoIdle_Events," +
						"ifnull(sum(b.No_AutoOFF_Events-b.Column36),0) as PeriodNo_AutoOFF_Events,convert(ifnull(sum(b.Column11), 0.0), decimal (10 , 1 )) as periodGear1," +
						"convert(ifnull(sum(b.Column12), 0.0), decimal (10 , 1 )) as periodGear2,convert(ifnull(sum(b.Column13), 0.0), decimal (10 , 1 )) as periodGear3," +
						"convert(ifnull(sum(b.Column14), 0.0), decimal (10 , 1 )) as periodGear4, " +
						"convert(ifnull(sum(b.ENG_IDL_HRS-b.Column22)/(sum(b.ENG_IDL_HRS-b.Column22)+sum(b.Column10-b.Column23)+sum(b.Column17-b.Column24)+sum(b.LOAD_JOB_HRS-b.Column25)+sum(b.column16-b.Column26))*100,'0'), decimal(10,1)) as PeriodIdlingPerct,"+
						"convert(ifnull(sum(b.Column10-b.Column23)/(sum(b.ENG_IDL_HRS-b.Column22)+sum(b.Column10-b.Column23)+sum(b.Column17-b.Column24)+sum(b.LOAD_JOB_HRS-b.Column25)+sum(b.column16-b.Column26))*100,'0'), decimal(10,1)) as PeriodAttachmentPerct,"+
						"convert(ifnull(sum(b.Column17-b.Column24)/(sum(b.ENG_IDL_HRS-b.Column22)+sum(b.Column10-b.Column23)+sum(b.Column17-b.Column24)+sum(b.LOAD_JOB_HRS-b.Column25)+sum(b.column16-b.Column26))*100,'0'), decimal(10,1)) as PeriodRoadingPerct,"+
						"convert(ifnull(sum(b.LOAD_JOB_HRS-b.Column25)/(sum(b.ENG_IDL_HRS-b.Column22)+sum(b.Column10-b.Column23)+sum(b.Column17-b.Column24)+sum(b.LOAD_JOB_HRS-b.Column25)+sum(b.column16-b.Column26))*100,'0'), decimal(10,1)) as PeriodLoadingPerct,"+
						"convert(ifnull(sum(b.column16-b.Column26)/(sum(b.ENG_IDL_HRS-b.Column22)+sum(b.Column10-b.Column23)+sum(b.Column17-b.Column24)+sum(b.LOAD_JOB_HRS-b.Column25)+sum(b.column16-b.Column26))*100,'0'), decimal(10,1)) as PeriodExcavationPerct,"+
						"convert(ifnull(sum(b.EXA_ECO_MOD_HRS-b.Column27)/(sum(b.EXA_ECO_MOD_HRS-b.Column27)+sum(b.EXA_ACT_MOD_HRS-b.Column28)+sum(b.EXA_PWR_MOD_HRS-b.Column29))*100,'0'), decimal(10,1)) as PeriodEconomyModePerct,"+
						"convert(ifnull(sum(b.EXA_ACT_MOD_HRS-b.Column28)/(sum(b.EXA_ECO_MOD_HRS-b.Column27)+sum(b.EXA_ACT_MOD_HRS-b.Column28)+sum(b.EXA_PWR_MOD_HRS-b.Column29))*100,'0'), decimal(10,1)) as PeriodActiveModePerct,"+
						"convert(ifnull(sum(b.EXA_PWR_MOD_HRS-b.Column29)/(sum(b.EXA_ECO_MOD_HRS-b.Column27)+sum(b.EXA_ACT_MOD_HRS-b.Column28)+sum(b.EXA_PWR_MOD_HRS-b.Column29))*100,'0'), decimal(10,1)) as PeriodPowerModePerct,"+
						"CONVERT(ifnull(sum(b.PRB1),'0.0'),decimal(10,1)) as PeriodPRB1,"+
						"CONVERT(ifnull(sum(b.PRB2),'0.0'),decimal(10,1)) as PeriodPRB2,"+
						"CONVERT(ifnull(sum(b.PRB3),'0.0'),decimal(10,1)) as PeriodPRB3,"+
						"CONVERT(ifnull(sum(b.PRB4),'0.0'),decimal(10,1)) as PeriodPRB4,"+
						"CONVERT(ifnull(sum(b.PRB5),'0.0'),decimal(10,1)) as PeriodPRB5,"+
						"CONVERT(ifnull(sum(b.PRB6),'0.0'),decimal(10,1)) as PeriodPRB6,"+
						"CONVERT(ifnull(sum(b.PRBEM1),'0.0'),decimal(10,1)) as PeriodPRBEM1,"+
						"CONVERT(ifnull(sum(b.PRBEM2),'0.0'),decimal(10,1)) as PeriodPRBEM2,"+
						"CONVERT(ifnull(sum(b.PRBEM3),'0.0'),decimal(10,1)) as PeriodPRBEM3,"+
						"CONVERT(ifnull(sum(b.PRBEM4),'0.0'),decimal(10,1)) as PeriodPRBEM4,"+
						"CONVERT(ifnull(sum(b.PRBEM5),'0.0'),decimal(10,1)) as PeriodPRBEM5,"+
						"CONVERT(ifnull(sum(b.PRBEM6),'0.0'),decimal(10,1)) as PeriodPRBEM6,"+
						"CONVERT(ifnull(sum(b.PDRBLM1),'0.0'),decimal(10,1)) as PeriodPDRBEM1,"+
						"CONVERT(ifnull(sum(b.PDRBLM2),'0.0'),decimal(10,1)) as PeriodPDRBEM2,"+
						"CONVERT(ifnull(sum(b.PDRBLM3),'0.0'),decimal(10,1)) as PeriodPDRBEM3,"+
						"CONVERT(ifnull(sum(b.PDRBLM4),'0.0'),decimal(10,1)) as PeriodPDRBEM4,"+
						"CONVERT(ifnull(sum(b.PDRBLM5),'0.0'),decimal(10,1)) as PeriodPDRBEM5,"+
						"CONVERT(ifnull(sum(b.PDRBLM6),'0.0'),decimal(10,1)) as PeriodPDRBEM6"+
						" from factInsight_dayAgg_extended b where b.TxnDate between '"+startDate+"' and '"+endDate+"'";

				rightQuery=rightQuery+" and b.Column21 in ("+MsgID+")";

				if(model!=null && ! model.equalsIgnoreCase("null")){

					rightQuery=rightQuery+" and b.Column5='"+model+"'";

				}

				if(region!=null && ! region.equalsIgnoreCase("null")){

					rightQuery=rightQuery+" and b.Column20 in ("+region+")";

				}
				if(zone!=null && ! zone.equalsIgnoreCase("null")){

					rightQuery=rightQuery+" and b.Column7 in ("+zone+")";

				}

				if(dealer!=null && ! dealer.equalsIgnoreCase("null")){

					rightQuery=rightQuery+" and b.Column8 in ("+dealer+")";

				}

				if(customer!=null && ! customer.equalsIgnoreCase("null")){

					rightQuery=rightQuery+" and b.Column9 in ("+customer+")";

				}

				joinQuery="ON aa.MsgID=bb.MsgID";


				query=selectQuery+fromQuery+"("+leftQuery+")"+"aa "+"inner join "+"("+rightQuery+")"+"bb "+joinQuery;
			}

			ConnectMySQL connMySql = new ConnectMySQL();
			prodConnection = connMySql.getProdDb2Connection();
			statement = prodConnection.createStatement();
			rs = statement.executeQuery(query);

			while(rs.next()){
				respObj = new HashMap<String, Object>();

				int machineCount=0;
				if(assetID==null){
					machineCount=rs.getInt("machineCount");
				}


				respObj.put("machineCount", machineCount);

				if(assetID!=null){
					respObj.put("AssetID",rs.getString("AssetID"));
					respObj.put("ModelName", rs.getString("ModelName"));
					respObj.put("CustomerName", rs.getString("CustomerName"));

					respObj.put("CustContact", rs.getString("CustContact"));

					respObj.put("DealerName", rs.getString("DealerName"));


					respObj.put("ZonalName", rs.getString("ZonalName"));

					/*PowerBandLowPerc=PowerBandLowPerc+rs.getDouble("PowerBandLowPerc");
					PowerBandMedPerc=PowerBandMedPerc+rs.getDouble("PowerBandMedPerc");
					PowerBandHighPerc=PowerBandHighPerc+rs.getDouble("PowerBandHighPerc");*/
					PeriodHMR=PeriodHMR+rs.getDouble("PeriodHMR");
					EngineOnTime=EngineOnTime+rs.getDouble("EngineOnTime");
					EngineOffTime=EngineOffTime+rs.getDouble("EngineOffTime");
					idletime=idletime+rs.getDouble("idletime");
					WorkingTime=WorkingTime+rs.getDouble("WorkingTime");
					
					PeriodIdling=PeriodIdling+rs.getDouble("PeriodIdling");
					PeriodAttachment=PeriodAttachment+rs.getDouble("PeriodAttachment");
					PeriodRoading=PeriodRoading+rs.getDouble("PeriodRoading");
					PeriodLoading=PeriodLoading+rs.getDouble("PeriodLoading");
					PeriodExcavation=PeriodExcavation+rs.getDouble("PeriodExcavation");
					
					PeriodIdlingPerct=(PeriodIdling/(PeriodIdling+PeriodAttachment+PeriodRoading+PeriodLoading+PeriodExcavation))*100;
					PeriodAttachmentPerct=(PeriodAttachment/(PeriodIdling+PeriodAttachment+PeriodRoading+PeriodLoading+PeriodExcavation))*100;
					PeriodRoadingPerct=(PeriodRoading/(PeriodIdling+PeriodAttachment+PeriodRoading+PeriodLoading+PeriodExcavation))*100;
					PeriodLoadingPerct=(PeriodLoading/(PeriodIdling+PeriodAttachment+PeriodRoading+PeriodLoading+PeriodExcavation))*100;
					PeriodExcavationPerct=(PeriodExcavation/(PeriodIdling+PeriodAttachment+PeriodRoading+PeriodLoading+PeriodExcavation))*100;
					
					PeriodEconomyMode=PeriodEconomyMode+rs.getDouble("PeriodEconomyMode");
					PeriodActiveMode=PeriodActiveMode+rs.getDouble("PeriodActiveMode");
					PeriodPowerMode=PeriodPowerMode+rs.getDouble("PeriodPowerMode");
					
					PeriodEconomyModePerct=(PeriodEconomyMode/(PeriodEconomyMode+PeriodActiveMode+PeriodPowerMode))*100;
					PeriodActiveModePerct=(PeriodActiveMode/(PeriodEconomyMode+PeriodActiveMode+PeriodPowerMode))*100;
					PeriodPowerModePerct=(PeriodPowerMode/(PeriodEconomyMode+PeriodActiveMode+PeriodPowerMode))*100;
					
					/*Gear1=Gear1+rs.getDouble("Gear1");
					Gear2=Gear2+rs.getDouble("Gear2");
					Gear3=Gear3+rs.getDouble("Gear3");
					Gear4=Gear4+rs.getDouble("Gear4");*/
					
					PeriodForwardDirection=PeriodForwardDirection+rs.getDouble("PeriodForwardDirection");
					PeriodNeutralDirection=PeriodNeutralDirection+rs.getDouble("PeriodNeutralDirection");
					PeriodReverseDirection=PeriodReverseDirection+rs.getDouble("PeriodReverseDirection");
					
					/*PRB1=PRB1+rs.getDouble("PRB1");
					PRB2=PRB2+rs.getDouble("PRB2");
					PRB3=PRB3+rs.getDouble("PRB3");
					PRB4=PRB4+rs.getDouble("PRB4");
					PRB5=PRB5+rs.getDouble("PRB5");
					PRB6=PRB6+rs.getDouble("PRB6");*/
					
					/*PRBEM1=PRBEM1+rs.getDouble("PRBEM1");
					PRBEM2=PRBEM2+rs.getDouble("PRBEM2");
					PRBEM3=PRBEM3+rs.getDouble("PRBEM3");
					PRBEM4=PRBEM4+rs.getDouble("PRBEM4");
					PRBEM5=PRBEM5+rs.getDouble("PRBEM5");
					PRBEM6=PRBEM6+rs.getDouble("PRBEM6");*/
					
					/*PDRBEM1=PDRBEM1+rs.getDouble("PDRBEM1");
					PDRBEM2=PDRBEM2+rs.getDouble("PDRBEM2");
					PDRBEM3=PDRBEM3+rs.getDouble("PDRBEM3");
					PDRBEM4=PDRBEM4+rs.getDouble("PDRBEM4");
					PDRBEM5=PDRBEM5+rs.getDouble("PDRBEM5");
					PDRBEM6=PDRBEM6+rs.getDouble("PDRBEM6");*/
					
					PeriodEngineOnCount=PeriodEngineOnCount+rs.getInt("PeriodEngineOnCount");
					PeriodDSC_CON_HRS=PeriodDSC_CON_HRS+rs.getDouble("PeriodDSC_CON_HRS");
					PeriodHyd_Choke_Events=PeriodHyd_Choke_Events+rs.getInt("PeriodHyd_Choke_Events");
					PeriodNo_AutoIdle_Events=PeriodNo_AutoIdle_Events+rs.getInt("PeriodNo_AutoIdle_Events");
					PeriodNo_AutoOFF_Events=PeriodNo_AutoOFF_Events+rs.getInt("PeriodNo_AutoOFF_Events");
					
					periodGear1=periodGear1+rs.getDouble("periodGear1");
					periodGear2=periodGear2+rs.getDouble("periodGear2");
					periodGear3=periodGear3+rs.getDouble("periodGear3");
					periodGear4=periodGear4+rs.getDouble("periodGear4");
					
					Gear1=(periodGear1/(periodGear1+periodGear2+periodGear3+periodGear4))*100;
					Gear2=(periodGear2/(periodGear1+periodGear2+periodGear3+periodGear4))*100;
					Gear3=(periodGear3/(periodGear1+periodGear2+periodGear3+periodGear4))*100;
					Gear4=(periodGear4/(periodGear1+periodGear2+periodGear3+periodGear4))*100;
					
					/*PeriodIdlingPerct=PeriodIdlingPerct+rs.getDouble("PeriodIdlingPerct");
					PeriodAttachmentPerct=PeriodAttachmentPerct+rs.getDouble("PeriodAttachmentPerct");
					PeriodRoadingPerct=PeriodRoadingPerct+rs.getDouble("PeriodRoadingPerct");
					PeriodLoadingPerct=PeriodLoadingPerct+rs.getDouble("PeriodLoadingPerct");
					PeriodExcavationPerct=PeriodExcavationPerct+rs.getDouble("PeriodExcavationPerct");*/
					
					/*PeriodEconomyModePerct=PeriodEconomyModePerct+rs.getDouble("PeriodEconomyModePerct");
					PeriodActiveModePerct=PeriodActiveModePerct+rs.getDouble("PeriodActiveModePerct");
					PeriodPowerModePerct=PeriodPowerModePerct+rs.getDouble("PeriodPowerModePerct");*/
					
					PeriodPRB1=PeriodPRB1+rs.getDouble("PeriodPRB1");
					PeriodPRB2=PeriodPRB2+rs.getDouble("PeriodPRB2");
					PeriodPRB3=PeriodPRB3+rs.getDouble("PeriodPRB3");
					PeriodPRB4=PeriodPRB4+rs.getDouble("PeriodPRB4");
					PeriodPRB5=PeriodPRB5+rs.getDouble("PeriodPRB5");
					PeriodPRB6=PeriodPRB6+rs.getDouble("PeriodPRB6");
					
					PRB1=(PeriodPRB1/(PeriodPRB1+PeriodPRB2+PeriodPRB3+PeriodPRB4+PeriodPRB5+PeriodPRB6))*100;
					PRB2=(PeriodPRB2/(PeriodPRB1+PeriodPRB2+PeriodPRB3+PeriodPRB4+PeriodPRB5+PeriodPRB6))*100;
					PRB3=(PeriodPRB3/(PeriodPRB1+PeriodPRB2+PeriodPRB3+PeriodPRB4+PeriodPRB5+PeriodPRB6))*100;
					PRB4=(PeriodPRB4/(PeriodPRB1+PeriodPRB2+PeriodPRB3+PeriodPRB4+PeriodPRB5+PeriodPRB6))*100;
					PRB5=(PeriodPRB5/(PeriodPRB1+PeriodPRB2+PeriodPRB3+PeriodPRB4+PeriodPRB5+PeriodPRB6))*100;
					PRB6=(PeriodPRB6/(PeriodPRB1+PeriodPRB2+PeriodPRB3+PeriodPRB4+PeriodPRB5+PeriodPRB6))*100;
					
					PeriodPRBEM1=PeriodPRBEM1+rs.getDouble("PeriodPRBEM1");
					PeriodPRBEM2=PeriodPRBEM2+rs.getDouble("PeriodPRBEM2");
					PeriodPRBEM3=PeriodPRBEM3+rs.getDouble("PeriodPRBEM3");
					PeriodPRBEM4=PeriodPRBEM4+rs.getDouble("PeriodPRBEM4");
					PeriodPRBEM5=PeriodPRBEM5+rs.getDouble("PeriodPRBEM5");
					PeriodPRBEM6=PeriodPRBEM6+rs.getDouble("PeriodPRBEM6");
					
					PRBEM1=(PeriodPRBEM1/(PeriodPRBEM1+PeriodPRBEM2+PeriodPRBEM3+PeriodPRBEM4+PeriodPRBEM5+PeriodPRBEM6))*100;
					PRBEM2=(PeriodPRBEM2/(PeriodPRBEM1+PeriodPRBEM2+PeriodPRBEM3+PeriodPRBEM4+PeriodPRBEM5+PeriodPRBEM6))*100;
					PRBEM3=(PeriodPRBEM3/(PeriodPRBEM1+PeriodPRBEM2+PeriodPRBEM3+PeriodPRBEM4+PeriodPRBEM5+PeriodPRBEM6))*100;
					PRBEM4=(PeriodPRBEM4/(PeriodPRBEM1+PeriodPRBEM2+PeriodPRBEM3+PeriodPRBEM4+PeriodPRBEM5+PeriodPRBEM6))*100;
					PRBEM5=(PeriodPRBEM5/(PeriodPRBEM1+PeriodPRBEM2+PeriodPRBEM3+PeriodPRBEM4+PeriodPRBEM5+PeriodPRBEM6))*100;
					PRBEM6=(PeriodPRBEM6/(PeriodPRBEM1+PeriodPRBEM2+PeriodPRBEM3+PeriodPRBEM4+PeriodPRBEM5+PeriodPRBEM6))*100;
					
					
					PeriodPDRBEM1=PeriodPDRBEM1+rs.getDouble("PeriodPDRBEM1");
					PeriodPDRBEM2=PeriodPDRBEM2+rs.getDouble("PeriodPDRBEM2");
					PeriodPDRBEM3=PeriodPDRBEM3+rs.getDouble("PeriodPDRBEM3");
					PeriodPDRBEM4=PeriodPDRBEM4+rs.getDouble("PeriodPDRBEM4");
					PeriodPDRBEM5=PeriodPDRBEM5+rs.getDouble("PeriodPDRBEM5");
					PeriodPDRBEM6=PeriodPDRBEM6+rs.getDouble("PeriodPDRBEM6");
					
					PDRBEM1=(PeriodPDRBEM1/(PeriodPDRBEM1+PeriodPDRBEM2+PeriodPDRBEM3+PeriodPDRBEM4+PeriodPDRBEM5+PeriodPDRBEM6))*100;
					PDRBEM2=(PeriodPDRBEM2/(PeriodPDRBEM1+PeriodPDRBEM2+PeriodPDRBEM3+PeriodPDRBEM4+PeriodPDRBEM5+PeriodPDRBEM6))*100;
					PDRBEM3=(PeriodPDRBEM3/(PeriodPDRBEM1+PeriodPDRBEM2+PeriodPDRBEM3+PeriodPDRBEM4+PeriodPDRBEM5+PeriodPDRBEM6))*100;
					PDRBEM4=(PeriodPDRBEM4/(PeriodPDRBEM1+PeriodPDRBEM2+PeriodPDRBEM3+PeriodPDRBEM4+PeriodPDRBEM5+PeriodPDRBEM6))*100;
					PDRBEM5=(PeriodPDRBEM5/(PeriodPDRBEM1+PeriodPDRBEM2+PeriodPDRBEM3+PeriodPDRBEM4+PeriodPDRBEM5+PeriodPDRBEM6))*100;
					PDRBEM6=(PeriodPDRBEM6/(PeriodPDRBEM1+PeriodPDRBEM2+PeriodPDRBEM3+PeriodPDRBEM4+PeriodPDRBEM5+PeriodPDRBEM6))*100;
					
					PowerBandLowHrs=PowerBandLowHrs+rs.getDouble("PowerBandLowHrs");
					PowerBandMedHrs=PowerBandMedHrs+rs.getDouble("PowerBandMedHrs");
					PowerBandHighHrs=PowerBandHighHrs+rs.getDouble("PowerBandHighHrs");
					
					PowerBandLowPerc=(PowerBandLowHrs/(PowerBandLowHrs+PowerBandMedHrs+PowerBandHighHrs))*100;
					PowerBandMedPerc=(PowerBandMedHrs/(PowerBandLowHrs+PowerBandMedHrs+PowerBandHighHrs))*100;
					PowerBandHighPerc=(PowerBandHighHrs/(PowerBandLowHrs+PowerBandMedHrs+PowerBandHighHrs))*100;

					respObj.put("PowerBandLowPerc", decformat.format(PowerBandLowPerc));
					respObj.put("PowerBandMedPerc", decformat.format(PowerBandMedPerc));
					respObj.put("PowerBandHighPerc", decformat.format(PowerBandHighPerc));
					respObj.put("PeriodHMR", decformat.format(PeriodHMR));
					respObj.put("EngineOnTime", decformat.format(EngineOnTime));
					respObj.put("EngineOffTime", decformat.format(EngineOffTime));
					respObj.put("idletime", decformat.format(idletime));
					respObj.put("WorkingTime", decformat.format(WorkingTime));
					respObj.put("PeriodIdling", decformat.format(PeriodIdling));
					respObj.put("PeriodAttachment", decformat.format(PeriodAttachment));
					respObj.put("PeriodRoading", decformat.format(PeriodRoading));

					respObj.put("PeriodLoading", decformat.format(PeriodLoading));

					respObj.put("PeriodExcavation", decformat.format(PeriodExcavation));

					respObj.put("PeriodEconomyMode", decformat.format(PeriodEconomyMode));

					respObj.put("PeriodActiveMode", decformat.format(PeriodActiveMode));

					respObj.put("PeriodPowerMode", decformat.format(PeriodPowerMode));

					respObj.put("Gear1", decformat.format(Gear1));

					respObj.put("Gear2", decformat.format(Gear2));

					respObj.put("Gear3", decformat.format(Gear3));

					respObj.put("Gear4", decformat.format(Gear4));

					respObj.put("PeriodForwardDirection", decformat.format(PeriodForwardDirection));

					respObj.put("PeriodNeutralDirection", decformat.format(PeriodNeutralDirection));

					respObj.put("PeriodReverseDirection", decformat.format(PeriodReverseDirection));

					respObj.put("PRB1", decformat.format(PRB1));

					respObj.put("PRB2", decformat.format(PRB2));

					respObj.put("PRB3", decformat.format(PRB3));

					respObj.put("PRB4", decformat.format(PRB4));

					respObj.put("PRB5", decformat.format(PRB5));

					respObj.put("PRB6", decformat.format(PRB6));

					respObj.put("PRBEM1", decformat.format(PRBEM1));

					respObj.put("PRBEM2", decformat.format(PRBEM2));

					respObj.put("PRBEM3", decformat.format(PRBEM3));

					respObj.put("PRBEM4", decformat.format(PRBEM4));

					respObj.put("PRBEM5", decformat.format(PRBEM5));

					respObj.put("PRBEM6", decformat.format(PRBEM6));

					respObj.put("PDRBEM1", decformat.format(PDRBEM1));

					respObj.put("PDRBEM2", decformat.format(PDRBEM2));

					respObj.put("PDRBEM3", decformat.format(PDRBEM3));

					respObj.put("PDRBEM4", decformat.format(PDRBEM4));

					respObj.put("PDRBEM5", decformat.format(PDRBEM5));

					respObj.put("PDRBEM6", decformat.format(PDRBEM6));

					respObj.put("PeriodEngineOnCount", PeriodEngineOnCount);
					respObj.put("PeriodDSC_CON_HRS", decformat.format(PeriodDSC_CON_HRS));
					respObj.put("PeriodHyd_Choke_Events", PeriodHyd_Choke_Events);
					respObj.put("PeriodNo_AutoIdle_Events", PeriodNo_AutoIdle_Events);
					respObj.put("PeriodNo_AutoOFF_Events", PeriodNo_AutoOFF_Events);

					respObj.put("periodGear1",decformat.format(periodGear1));
					respObj.put("periodGear2",decformat.format(periodGear2));
					respObj.put("periodGear3",decformat.format(periodGear3));
					respObj.put("periodGear4",decformat.format(periodGear4));


					respObj.put("PeriodIdlingPerct",decformat.format(PeriodIdlingPerct));
					respObj.put("PeriodAttachmentPerct",decformat.format(PeriodAttachmentPerct));
					respObj.put("PeriodRoadingPerct",decformat.format(PeriodRoadingPerct));
					respObj.put("PeriodLoadingPerct",decformat.format(PeriodLoadingPerct));
					respObj.put("PeriodExcavationPerct",decformat.format(PeriodExcavationPerct));
					respObj.put("PeriodEconomyModePerct",decformat.format(PeriodEconomyModePerct));
					respObj.put("PeriodActiveModePerct",decformat.format(PeriodActiveModePerct));
					respObj.put("PeriodPowerModePerct",decformat.format(PeriodPowerModePerct));
					respObj.put("PeriodPRB1",decformat.format(PeriodPRB1));
					respObj.put("PeriodPRB2",decformat.format(PeriodPRB2));
					respObj.put("PeriodPRB3",decformat.format(PeriodPRB3));
					respObj.put("PeriodPRB4",decformat.format(PeriodPRB4));
					respObj.put("PeriodPRB5",decformat.format(PeriodPRB5));
					respObj.put("PeriodPRB6",decformat.format(PeriodPRB6));
					respObj.put("PeriodPRBEM1",decformat.format(PeriodPRBEM1));
					respObj.put("PeriodPRBEM2",decformat.format(PeriodPRBEM2));
					respObj.put("PeriodPRBEM3",decformat.format(PeriodPRBEM3));
					respObj.put("PeriodPRBEM4",decformat.format(PeriodPRBEM4));
					respObj.put("PeriodPRBEM5",decformat.format(PeriodPRBEM5));
					respObj.put("PeriodPRBEM6",decformat.format(PeriodPRBEM6));

					respObj.put("PeriodPDRBEM1",decformat.format(PeriodPDRBEM1));
					respObj.put("PeriodPDRBEM2",decformat.format(PeriodPDRBEM2));
					respObj.put("PeriodPDRBEM3",decformat.format(PeriodPDRBEM3));
					respObj.put("PeriodPDRBEM4",decformat.format(PeriodPDRBEM4));
					respObj.put("PeriodPDRBEM5",decformat.format(PeriodPDRBEM5));
					respObj.put("PeriodPDRBEM6",decformat.format(PeriodPDRBEM6));

					respObj.put("PowerBandLowHrs",decformat.format(PowerBandLowHrs));
					respObj.put("PowerBandMedHrs",decformat.format(PowerBandMedHrs));
					respObj.put("PowerBandHighHrs",decformat.format(PowerBandHighHrs));




					/*respObj.put("PowerBandLowPerc", rs.getDouble("PowerBandLowPerc"));


					respObj.put("PowerBandMedPerc", rs.getDouble("PowerBandMedPerc"));



					respObj.put("PowerBandHighPerc", rs.getDouble("PowerBandHighPerc"));


					respObj.put("PeriodHMR", rs.getDouble("PeriodHMR"));


					respObj.put("EngineOnTime", rs.getDouble("EngineOnTime"));


					respObj.put("EngineOffTime", rs.getDouble("EngineOffTime"));

					respObj.put("idletime", rs.getDouble("idletime"));

					respObj.put("WorkingTime", rs.getDouble("WorkingTime"));

					if(rs.getString("PeriodIdling")!=null)
						respObj.put("PeriodIdling", rs.getDouble("PeriodIdling"));
					else
						respObj.put("PeriodIdling","0");
					if(rs.getString("PeriodAttachment")!=null)
						respObj.put("PeriodAttachment", rs.getDouble("PeriodAttachment"));
					else
						respObj.put("PeriodAttachment","0");
					if(rs.getString("PeriodRoading")!=null)
						respObj.put("PeriodRoading", rs.getDouble("PeriodRoading"));
					else
						respObj.put("PeriodRoading","0");
					if(rs.getString("PeriodLoading")!=null)
						respObj.put("PeriodLoading", rs.getDouble("PeriodLoading"));
					else
						respObj.put("PeriodLoading","0");
					if(rs.getString("PeriodExcavation")!=null)
						respObj.put("PeriodExcavation", rs.getDouble("PeriodExcavation"));
					else
						respObj.put("PeriodExcavation","0");
					if(rs.getString("PeriodEconomyMode")!=null)
						respObj.put("PeriodEconomyMode", rs.getDouble("PeriodEconomyMode"));
					else
						respObj.put("PeriodEconomyMode","0");
					if(rs.getString("PeriodActiveMode")!=null)
						respObj.put("PeriodActiveMode", rs.getDouble("PeriodActiveMode"));
					else
						respObj.put("PeriodActiveMode","0");
					if(rs.getString("PeriodPowerMode")!=null)
						respObj.put("PeriodPowerMode", rs.getDouble("PeriodPowerMode"));
					else
						respObj.put("PeriodPowerMode","0");

					respObj.put("Gear1", rs.getDouble("Gear1"));


					respObj.put("Gear2", rs.getDouble("Gear2"));

					respObj.put("Gear3", rs.getDouble("Gear3"));

					respObj.put("Gear4", rs.getDouble("Gear4"));

					if(rs.getString("PeriodForwardDirection")!=null)
						respObj.put("PeriodForwardDirection", rs.getDouble("PeriodForwardDirection"));
					else
						respObj.put("PeriodForwardDirection","0");
					if(rs.getString("PeriodNeutralDirection")!=null)
						respObj.put("PeriodNeutralDirection", rs.getDouble("PeriodNeutralDirection"));
					else
						respObj.put("PeriodNeutralDirection","0");
					if(rs.getString("PeriodReverseDirection")!=null)
						respObj.put("PeriodReverseDirection", rs.getDouble("PeriodReverseDirection"));
					else
						respObj.put("PeriodReverseDirection","0");

					respObj.put("PRB1", rs.getDouble("PRB1"));

					respObj.put("PRB2", rs.getDouble("PRB2"));

					respObj.put("PRB3", rs.getDouble("PRB3"));

					respObj.put("PRB4", rs.getDouble("PRB4"));

					respObj.put("PRB5", rs.getDouble("PRB5"));

					respObj.put("PRB6", rs.getDouble("PRB6"));

					respObj.put("PRBEM1", rs.getDouble("PRBEM1"));

					respObj.put("PRBEM2", rs.getDouble("PRBEM2"));

					respObj.put("PRBEM3", rs.getDouble("PRBEM3"));

					respObj.put("PRBEM4", rs.getDouble("PRBEM4"));

					respObj.put("PRBEM5", rs.getDouble("PRBEM5"));

					respObj.put("PRBEM6", rs.getDouble("PRBEM6"));

					respObj.put("PDRBEM1", rs.getDouble("PDRBEM1"));

					respObj.put("PDRBEM2", rs.getDouble("PDRBEM2"));

					respObj.put("PDRBEM3", rs.getDouble("PDRBEM3"));

					respObj.put("PDRBEM4", rs.getDouble("PDRBEM4"));

					respObj.put("PDRBEM5", rs.getDouble("PDRBEM5"));

					respObj.put("PDRBEM6", rs.getDouble("PDRBEM6"));

					respObj.put("PeriodEngineOnCount", rs.getDouble("PeriodEngineOnCount"));
					respObj.put("PeriodDSC_CON_HRS", rs.getDouble("PeriodDSC_CON_HRS"));
					respObj.put("PeriodHyd_Choke_Events", rs.getDouble("PeriodHyd_Choke_Events"));
					respObj.put("PeriodNo_AutoIdle_Events", rs.getDouble("PeriodNo_AutoIdle_Events"));
					respObj.put("PeriodNo_AutoOFF_Events", rs.getDouble("PeriodNo_AutoOFF_Events"));

					respObj.put("periodGear1",rs.getDouble("periodGear1"));
					respObj.put("periodGear2",rs.getDouble("periodGear2"));
					respObj.put("periodGear3",rs.getDouble("periodGear3"));
					respObj.put("periodGear4",rs.getDouble("periodGear4"));


					respObj.put("PeriodIdlingPerct",rs.getDouble("PeriodIdlingPerct"));
					respObj.put("PeriodAttachmentPerct",rs.getDouble("PeriodAttachmentPerct"));
					respObj.put("PeriodRoadingPerct",rs.getDouble("PeriodRoadingPerct"));
					respObj.put("PeriodLoadingPerct",rs.getDouble("PeriodLoadingPerct"));
					respObj.put("PeriodExcavationPerct",rs.getDouble("PeriodExcavationPerct"));
					respObj.put("PeriodEconomyModePerct",rs.getDouble("PeriodEconomyModePerct"));
					respObj.put("PeriodActiveModePerct",rs.getDouble("PeriodActiveModePerct"));
					respObj.put("PeriodPowerModePerct",rs.getDouble("PeriodPowerModePerct"));
					respObj.put("PeriodPRB1",rs.getDouble("PeriodPRB1"));
					respObj.put("PeriodPRB2",rs.getDouble("PeriodPRB2"));
					respObj.put("PeriodPRB3",rs.getDouble("PeriodPRB3"));
					respObj.put("PeriodPRB4",rs.getDouble("PeriodPRB4"));
					respObj.put("PeriodPRB5",rs.getDouble("PeriodPRB5"));
					respObj.put("PeriodPRB6",rs.getDouble("PeriodPRB6"));
					respObj.put("PeriodPRBEM1",rs.getDouble("PeriodPRBEM1"));
					respObj.put("PeriodPRBEM2",rs.getDouble("PeriodPRBEM2"));
					respObj.put("PeriodPRBEM3",rs.getDouble("PeriodPRBEM3"));
					respObj.put("PeriodPRBEM4",rs.getDouble("PeriodPRBEM4"));
					respObj.put("PeriodPRBEM5",rs.getDouble("PeriodPRBEM5"));
					respObj.put("PeriodPRBEM6",rs.getDouble("PeriodPRBEM6"));

					respObj.put("PeriodPDRBEM1",rs.getDouble("PeriodPDRBEM1"));
					respObj.put("PeriodPDRBEM2",rs.getDouble("PeriodPDRBEM2"));
					respObj.put("PeriodPDRBEM3",rs.getDouble("PeriodPDRBEM3"));
					respObj.put("PeriodPDRBEM4",rs.getDouble("PeriodPDRBEM4"));
					respObj.put("PeriodPDRBEM5",rs.getDouble("PeriodPDRBEM5"));
					respObj.put("PeriodPDRBEM6",rs.getDouble("PeriodPDRBEM6"));

					respObj.put("PowerBandLowHrs",rs.getDouble("PowerBandLowHrs"));
					respObj.put("PowerBandMedHrs",rs.getDouble("PowerBandMedHrs"));
					respObj.put("PowerBandHighHrs",rs.getDouble("PowerBandHighHrs"));*/

				}
				else{



					respObj.put("PowerBandLowPerc", decformat.format(rs.getDouble("PowerBandLowPerc")/machineCount));


					respObj.put("PowerBandMedPerc", decformat.format(rs.getDouble("PowerBandMedPerc")/machineCount));



					respObj.put("PowerBandHighPerc", decformat.format(rs.getDouble("PowerBandHighPerc")/machineCount));


					respObj.put("PeriodHMR", decformat.format(rs.getDouble("PeriodHMR")/machineCount));


					respObj.put("EngineOnTime", decformat.format(rs.getDouble("EngineOnTime")/machineCount));


					respObj.put("EngineOffTime", decformat.format(rs.getDouble("EngineOffTime")/machineCount));

					respObj.put("idletime", decformat.format(rs.getDouble("idletime")/machineCount));

					respObj.put("WorkingTime", decformat.format(rs.getDouble("WorkingTime")/machineCount));

					if(rs.getString("PeriodIdling")!=null)
						respObj.put("PeriodIdling", decformat.format(rs.getDouble("PeriodIdling")/machineCount));
					else
						respObj.put("PeriodIdling","0");
					if(rs.getString("PeriodAttachment")!=null)
						respObj.put("PeriodAttachment", decformat.format(rs.getDouble("PeriodAttachment")/machineCount));
					else
						respObj.put("PeriodAttachment","0");
					if(rs.getString("PeriodRoading")!=null)
						respObj.put("PeriodRoading", decformat.format(rs.getDouble("PeriodRoading")/machineCount));
					else
						respObj.put("PeriodRoading","0");
					if(rs.getString("PeriodLoading")!=null)
						respObj.put("PeriodLoading", decformat.format(rs.getDouble("PeriodLoading")/machineCount));
					else
						respObj.put("PeriodLoading","0");
					if(rs.getString("PeriodExcavation")!=null)
						respObj.put("PeriodExcavation", decformat.format(rs.getDouble("PeriodExcavation")/machineCount));
					else
						respObj.put("PeriodExcavation","0");
					if(rs.getString("PeriodEconomyMode")!=null)
						respObj.put("PeriodEconomyMode", decformat.format(rs.getDouble("PeriodEconomyMode")/machineCount));
					else
						respObj.put("PeriodEconomyMode","0");
					if(rs.getString("PeriodActiveMode")!=null)
						respObj.put("PeriodActiveMode", decformat.format(rs.getDouble("PeriodActiveMode")/machineCount));
					else
						respObj.put("PeriodActiveMode","0");
					if(rs.getString("PeriodPowerMode")!=null)
						respObj.put("PeriodPowerMode", decformat.format(rs.getDouble("PeriodPowerMode")/machineCount));
					else
						respObj.put("PeriodPowerMode","0");

					respObj.put("Gear1", decformat.format(rs.getDouble("Gear1")/machineCount));


					respObj.put("Gear2", decformat.format(rs.getDouble("Gear2")/machineCount));

					respObj.put("Gear3", decformat.format(rs.getDouble("Gear3")/machineCount));

					respObj.put("Gear4", decformat.format(rs.getDouble("Gear4")/machineCount));

					if(rs.getString("PeriodForwardDirection")!=null)
						respObj.put("PeriodForwardDirection", decformat.format(rs.getDouble("PeriodForwardDirection")/machineCount));
					else
						respObj.put("PeriodForwardDirection","0");
					if(rs.getString("PeriodNeutralDirection")!=null)
						respObj.put("PeriodNeutralDirection", decformat.format(rs.getDouble("PeriodNeutralDirection")/machineCount));
					else
						respObj.put("PeriodNeutralDirection","0");
					if(rs.getString("PeriodReverseDirection")!=null)
						respObj.put("PeriodReverseDirection", decformat.format(rs.getDouble("PeriodReverseDirection")/machineCount));
					else
						respObj.put("PeriodReverseDirection","0");

					respObj.put("PRB1", decformat.format(rs.getDouble("PRB1")/machineCount));

					respObj.put("PRB2", decformat.format(rs.getDouble("PRB2")/machineCount));

					respObj.put("PRB3", decformat.format(rs.getDouble("PRB3")/machineCount));

					respObj.put("PRB4", decformat.format(rs.getDouble("PRB4")/machineCount));

					respObj.put("PRB5", decformat.format(rs.getDouble("PRB5")/machineCount));

					respObj.put("PRB6", decformat.format(rs.getDouble("PRB6")/machineCount));

					respObj.put("PRBEM1", decformat.format(rs.getDouble("PRBEM1")/machineCount));

					respObj.put("PRBEM2", decformat.format(rs.getDouble("PRBEM2")/machineCount));

					respObj.put("PRBEM3", decformat.format(rs.getDouble("PRBEM3")/machineCount));

					respObj.put("PRBEM4", decformat.format(rs.getDouble("PRBEM4")/machineCount));

					respObj.put("PRBEM5", decformat.format(rs.getDouble("PRBEM5")/machineCount));

					respObj.put("PRBEM6", decformat.format(rs.getDouble("PRBEM6")/machineCount));

					respObj.put("PDRBEM1", decformat.format(rs.getDouble("PDRBEM1")/machineCount));

					respObj.put("PDRBEM2", decformat.format(rs.getDouble("PDRBEM2")/machineCount));

					respObj.put("PDRBEM3", decformat.format(rs.getDouble("PDRBEM3")/machineCount));

					respObj.put("PDRBEM4", decformat.format(rs.getDouble("PDRBEM4")/machineCount));

					respObj.put("PDRBEM5", decformat.format(rs.getDouble("PDRBEM5")/machineCount));

					respObj.put("PDRBEM6", decformat.format(rs.getDouble("PDRBEM6")/machineCount));

					respObj.put("PeriodEngineOnCount", (int) Math.round(rs.getDouble("PeriodEngineOnCount")/machineCount));
					respObj.put("PeriodDSC_CON_HRS", decformat.format(rs.getDouble("PeriodDSC_CON_HRS")/machineCount));
					respObj.put("PeriodHyd_Choke_Events", (int) Math.round(rs.getDouble("PeriodHyd_Choke_Events")/machineCount));
					respObj.put("PeriodNo_AutoIdle_Events", (int) Math.round(rs.getDouble("PeriodNo_AutoIdle_Events")/machineCount));
					respObj.put("PeriodNo_AutoOFF_Events", (int) Math.round(rs.getDouble("PeriodNo_AutoOFF_Events")/machineCount));

					respObj.put("periodGear1",decformat.format(rs.getDouble("periodGear1")/machineCount));
					respObj.put("periodGear2",decformat.format(rs.getDouble("periodGear2")/machineCount));
					respObj.put("periodGear3",decformat.format(rs.getDouble("periodGear3")/machineCount));
					respObj.put("periodGear4",decformat.format(rs.getDouble("periodGear4")/machineCount));


					respObj.put("PeriodIdlingPerct",decformat.format(rs.getDouble("PeriodIdlingPerct")/machineCount));
					respObj.put("PeriodAttachmentPerct",decformat.format(rs.getDouble("PeriodAttachmentPerct")/machineCount));
					respObj.put("PeriodRoadingPerct",decformat.format(rs.getDouble("PeriodRoadingPerct")/machineCount));
					respObj.put("PeriodLoadingPerct",decformat.format(rs.getDouble("PeriodLoadingPerct")/machineCount));
					respObj.put("PeriodExcavationPerct",decformat.format(rs.getDouble("PeriodExcavationPerct")/machineCount));
					respObj.put("PeriodEconomyModePerct",decformat.format(rs.getDouble("PeriodEconomyModePerct")/machineCount));
					respObj.put("PeriodActiveModePerct",decformat.format(rs.getDouble("PeriodActiveModePerct")/machineCount));
					respObj.put("PeriodPowerModePerct",decformat.format(rs.getDouble("PeriodPowerModePerct")/machineCount));
					respObj.put("PeriodPRB1",decformat.format(rs.getDouble("PeriodPRB1")/machineCount));
					respObj.put("PeriodPRB2",decformat.format(rs.getDouble("PeriodPRB2")/machineCount));
					respObj.put("PeriodPRB3",decformat.format(rs.getDouble("PeriodPRB3")/machineCount));
					respObj.put("PeriodPRB4",decformat.format(rs.getDouble("PeriodPRB4")/machineCount));
					respObj.put("PeriodPRB5",decformat.format(rs.getDouble("PeriodPRB5")/machineCount));
					respObj.put("PeriodPRB6",decformat.format(rs.getDouble("PeriodPRB6")/machineCount));
					respObj.put("PeriodPRBEM1",decformat.format(rs.getDouble("PeriodPRBEM1")/machineCount));
					respObj.put("PeriodPRBEM2",decformat.format(rs.getDouble("PeriodPRBEM2")/machineCount));
					respObj.put("PeriodPRBEM3",decformat.format(rs.getDouble("PeriodPRBEM3")/machineCount));
					respObj.put("PeriodPRBEM4",decformat.format(rs.getDouble("PeriodPRBEM4")/machineCount));
					respObj.put("PeriodPRBEM5",decformat.format(rs.getDouble("PeriodPRBEM5")/machineCount));
					respObj.put("PeriodPRBEM6",decformat.format(rs.getDouble("PeriodPRBEM6")/machineCount));

					respObj.put("PeriodPDRBEM1",decformat.format(rs.getDouble("PeriodPDRBEM1")/machineCount));
					respObj.put("PeriodPDRBEM2",decformat.format(rs.getDouble("PeriodPDRBEM2")/machineCount));
					respObj.put("PeriodPDRBEM3",decformat.format(rs.getDouble("PeriodPDRBEM3")/machineCount));
					respObj.put("PeriodPDRBEM4",decformat.format(rs.getDouble("PeriodPDRBEM4")/machineCount));
					respObj.put("PeriodPDRBEM5",decformat.format(rs.getDouble("PeriodPDRBEM5")/machineCount));
					respObj.put("PeriodPDRBEM6",decformat.format(rs.getDouble("PeriodPDRBEM6")/machineCount));

					respObj.put("PowerBandLowHrs",decformat.format(rs.getDouble("PowerBandLowHrs")/machineCount));
					respObj.put("PowerBandMedHrs",decformat.format(rs.getDouble("PowerBandMedHrs")/machineCount));
					respObj.put("PowerBandHighHrs",decformat.format(rs.getDouble("PowerBandHighHrs")/machineCount));


				}


			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fLogger.fatal("BHLChartandPeriodRESTService:getBHLChartsAndPeriods: Exception Caught:"+e.getMessage());
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
