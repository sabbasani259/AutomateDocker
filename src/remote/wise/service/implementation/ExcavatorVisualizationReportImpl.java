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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.Logger;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.ListToStringConversion;

/**
 * @author ROOPN5
 *
 */
public class ExcavatorVisualizationReportImpl {

	public HashMap<String,Object> getExcavatorChartsAndPeriodData(String AssetID, String startDate, String endDate, String MsgID,String model, String region, String zone, String dealer, String customer){



		HashMap<String, Object> respObj = null;
		Logger bLogger = BusinessErrorLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
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
		double PeriodTravelTime=0.0;
		double PeriodSlewTime=0.0;
		double PeriodFuelUsedInLPB=0.0;
		double PeriodFuelUsedInMPB=0.0;
		double PeriodFuelUsedInHPB=0.0;
		double PeriodLBand=0.0;
		double PeriodGBand=0.0;
		double PeriodHBand=0.0;
		double PeriodHPlusBand=0.0;
		double PeriodHammerUseTime=0.0;
		double PeriodTotalFuel=0.0;
		double PeriodFuelLoss=0.0;
		double PeriodCarbonEmission=0.0;
		int PeriodEngineOnCount=0;
		int PeriodEngineOffCount=0;
		int PeriodHighRPMShutdownCount=0;
		int PeriodLongEngineIdlingCount=0;
		double PeriodPowerBoostTime=0.0;
		double PeriodHammerAbuseCount=0.0;
		double PeriodGear1FwdUtilization=0.0;
		double PeriodGear2FwdUtilization=0.0;
		double PeriodGear3FwdUtilization=0.0;
		double PeriodGear4FwdUtilization=0.0;
		double PeriodGear5FwdUtilization=0.0;
		double PeriodGear6FwdUtilization=0.0;
		double PeriodGear1BkwdUtilization=0.0;
		double PeriodGear2BkwdUtilization=0.0;
		double PeriodGear3BkwdUtilization=0.0;
		double PeriodGear4BkwdUtilization=0.0;
		double PeriodGear5BkwdUtilization=0.0;
		double PeriodGear6BkwdUtilization=0.0;
		double PeriodGear1LockupUtilization=0.0;
		double PeriodGear2LockupUtilization=0.0;
		double PeriodGear3LockupUtilization=0.0;
		double PeriodGear4LockupUtilization=0.0;
		double PeriodGear5LockupUtilization=0.0;
		double PeriodGear6LockupUtilization=0.0;
		double PeriodAverageFuelConsumption=0.0;
		double PowerBandLowHrs=0.0;
		double PowerBandMedHrs=0.0;
		double PowerBandHighHrs=0.0;
		double PeriodFuelUsedInLPBLtrs=0.0;
		double PeriodFuelUsedInMPBLtrs=0.0;
		double PeriodFuelUsedInHPBLtrs=0.0;
		double PeriodLBandHrs=0.0;
		double PeriodGBandHrs=0.0;
		double PeriodHBandHrs=0.0;
		double PeriodHPlusBandHrs=0.0;
		int PeriodNo_AutoIdle_Events=0;
		int PeriodNo_AutoOFF_Events=0;

		if(startDate==null || endDate==null)
		{
			bLogger.error("ExcavatorDashboardRESTService:getExcavatorChartsAndPeriodData: Mandatory parameters are null");
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

			if(AssetID!=null && (AssetID.trim().length()==7 || AssetID.trim().length()==17)){

				if(AssetID.trim().length()==7)
				{
					String machineNumber = AssetID;
					AssetID = getSerialNumberMachineNumber(machineNumber);
					if(AssetID==null)
					{//invalid machine number
						bLogger.error("ExcavatorDashboardRESTService:getExcavatorChartsAndPeriodData:MachineNum"+ machineNumber + "does not exist !!!");
						return null ;
					}
				}

				/*query = "Select aa.*,bb.*,convert(bb.PeriodTotalFuel/aa.PeriodHMR,decimal(10,1)) as PeriodAverageFuelConsumption "+
						"from"+
						"(Select a.AssetID,a.TxnDate,"+
						"ifnull(a.ModelName,'') as ModelName,"+
						"ifnull(a.CustomerName,'') as CustomerName,"+
						"ifnull(a.CustContact,'') as CustContact,"+
						"ifnull(a.DealerName,'') as DealerName,"+
						"ifnull(a.ZonalName,'') as ZonalName,"+
						"CONVERT(ifnull(sum(a.PowerBandLow)/(sum(a.PowerBandLow)+sum(a.PowerBandMed)+sum(a.PowerBandHigh))*100,'0.0') , decimal(10,1)) as PowerBandLowPerc,"+
						"CONVERT(ifnull(sum(a.PowerBandMed)/(sum(a.PowerBandLow)+sum(a.PowerBandMed)+sum(a.PowerBandHigh))*100,'0.000') , decimal(10,1)) as PowerBandMedPerc,CONVERT(ifnull(sum(a.PowerBandHigh)/(sum(a.PowerBandLow)+sum(a.PowerBandMed)+sum(a.PowerBandHigh))*100,'0.000') , decimal(10,1)) as PowerBandHighPerc,"+
						"convert(ifnull(sum(a.PeriodHMR),'0.0'),decimal(10,1)) as PeriodHMR,"+
						"CONVERT(ifnull(sum(a.EngineOnTime),'0.000') , decimal(10,1)) as EngineOnTime,convert(ifnull(sum(a.EngineOffTime),'0.000'), decimal(10,1)) as EngineOffTime,"+
						"convert(ifnull(sum(a.idletime),'0.000'), decimal(10,1))  as idletime,"+
						"convert(ifnull(sum(a.WorkingTime),'0.000'), decimal(10,1))  as WorkingTime, "+
						"convert(ifnull(sum(a.PowerBandLow),'0.0'),decimal(10,1)) as PowerBandLowHrs,"+
						"convert(ifnull(sum(a.PowerBandMed),'0.0'),decimal(10,1)) as PowerBandMedHrs,"+
						"convert(ifnull(sum(a.PowerBandHigh),'0.0'),decimal(10,1)) as PowerBandHighHrs"+
						" from factInsight_dayAgg a where a.AssetID='"+AssetID+"' and a.TxnDate between '"+startDate+"' and '"+endDate+"') aa inner join "+
						"(Select b.AssetID,b.TxnDate,"+
						"convert(ifnull(sum(b.FinishTravellingTime-b.StartTravellingTime),'0.0'),decimal(10,1)) as PeriodTravelTime,"+
						"convert(ifnull(sum(b.FinishSlewTime-b.StartSlewTime),'0.0'),decimal(10,1)) as PeriodSlewTime,"+ 
						"CONVERT(ifnull(sum(b.FuelUsedInLPB)/(sum(b.FuelUsedInLPB)+sum(b.FuelUsedInMPB)+sum(b.FuelUsedInHPB))*100,'0.000') , decimal(10,1)) as PeriodFuelUsedInLPB,CONVERT(ifnull(sum(b.FuelUsedInMPB)/(sum(b.FuelUsedInLPB)+sum(b.FuelUsedInMPB)+sum(b.FuelUsedInHPB))*100,'0.000') , decimal(10,1)) as PeriodFuelUsedInMPB,"+
						"CONVERT(ifnull(sum(b.FuelUsedInHPB)/(sum(b.FuelUsedInLPB)+sum(b.FuelUsedInMPB)+sum(b.FuelUsedInHPB))*100,'0.000') , decimal(10,1)) as PeriodFuelUsedInHPB,"+
						"convert(ifnull(sum(b.FinishLMode-b.StartLMode)/(sum(b.FinishLMode-b.StartLMode)+sum(b.FinishGMode-b.StartGMode)+sum(b.FinishHMode-b.StartHMode)+sum(b.FinishHPlusMode-b.StartHPlusMode))*100, '0.0'),decimal(10,1)) as PeriodLBand,"+
						"convert(ifnull(sum(b.FinishGMode-b.StartGMode)/(sum(b.FinishLMode-b.StartLMode)+sum(b.FinishGMode-b.StartGMode)+sum(b.FinishHMode-b.StartHMode)+sum(b.FinishHPlusMode-b.StartHPlusMode))*100,'0.0'),decimal(10,1)) as PeriodGBand,"+
						"convert(ifnull(sum(b.FinishHMode-b.StartHMode)/(sum(b.FinishLMode-b.StartLMode)+sum(b.FinishGMode-b.StartGMode)+sum(b.FinishHMode-b.StartHMode)+sum(b.FinishHPlusMode-b.StartHPlusMode))*100, '0.0'),decimal(10,1)) as PeriodHBand,"+
						"convert(ifnull(sum(b.FinishHPlusMode-b.StartHPlusMode)/(sum(b.FinishLMode-b.StartLMode)+sum(b.FinishGMode-b.StartGMode)+sum(b.FinishHMode-b.StartHMode)+sum(b.FinishHPlusMode-b.StartHPlusMode))*100, '0.0'),decimal(10,1)) as PeriodHPlusBand,"+
						"convert(ifnull(sum(b.FinishHammerUseTime-b.StartHammerUseTime),'0.0'),decimal(10,1)) as PeriodHammerUseTime,"+
						"convert(ifnull(sum(b.FinishFuelInLtrs-b.StartFuelInLtrs),'0.0'),decimal(10,1)) as PeriodTotalFuel,"+
						"convert(ifnull(sum(b.FuelLoss),'0.0'),decimal(10,1)) as PeriodFuelLoss,"+
						"convert(ifnull(sum(b.CarbonEmission),'0.0'),decimal(10,1)) as PeriodCarbonEmission,"+
						"convert(ifnull(sum(b.EngineOnCount),'0'),decimal(10,1)) as PeriodEngineOnCount,"+
						"convert(ifnull(sum(b.EngineOffCount),'0'),decimal(10,1)) as PeriodEngineOffCount,"+
						"convert(ifnull(sum(b.Column19-b.Column37),'0'),decimal(10,1)) as PeriodHighRPMShutdownCount,"+
						"convert(ifnull(sum(b.Column18-b.Column38),'0'),decimal(10,1))as PeriodLongEngineIdlingCount,"+
						"convert(ifnull(sum(b.PowerBoostTime),'0.0'),decimal(10,1)) as PeriodPowerBoostTime,"+
						"convert(ifnull(sum(b.HammerAbuseCount-b.Column39),'0.0'),decimal(10,1)) as PeriodHammerAbuseCount,"+
						"convert(ifnull(sum(b.Gear1FwdUtilization),'0.0'),decimal(10,1)) as PeriodGear1FwdUtilization,"+
						"convert(ifnull(sum(b.Gear2FwdUtilization),'0.0'),decimal(10,1)) as PeriodGear2FwdUtilization,"+
						"convert(ifnull(sum(b.Gear31FwdUtilization),'0.0'),decimal(10,1)) as PeriodGear3FwdUtilization,"+
						"convert(ifnull(sum(b.Gear4FwdUtilization),'0,0'),decimal(10,1)) as PeriodGear4FwdUtilization,"+
						"convert(ifnull(sum(b.Gear5FwdUtilization),'0.0'),decimal(10,1)) as PeriodGear5FwdUtilization,"+
						"convert(ifnull(sum(b.Gear6FwdUtilization),'0.0'),decimal(10,1)) as PeriodGear6FwdUtilization,"+
						"convert(ifnull(sum(b.Gear1BkwdUtilization),'0.0'),decimal(10,1)) as PeriodGear1BkwdUtilization,"+
						"convert(ifnull(sum(b.Gear2BkwdUtilization),'0.0'),decimal(10,1)) as PeriodGear2BkwdUtilization,"+
						"convert(ifnull(sum(b.Gear3BkwdUtilization),'0.0'),decimal(10,1)) as PeriodGear3BkwdUtilization,"+
						"convert(ifnull(sum(b.Gear4BkwdUtilization),'0.0'),decimal(10,1)) as PeriodGear4BkwdUtilization,"+
						"convert(ifnull(sum(b.Gear5BkwdUtilization),'0.0'),decimal(10,1)) as PeriodGear5BkwdUtilization,"+
						"convert(ifnull(sum(b.Gear6BkwdUtilization),'0.0'),decimal(10,1)) as PeriodGear6BkwdUtilization,"+
						"convert(ifnull(sum(b.Gear1LockupUtilization),'0.0'),decimal(10,1)) as PeriodGear1LockupUtilization,"+
						"convert(ifnull(sum(b.Gear2LockupUtilization),'0.0'),decimal(10,1)) as PeriodGear2LockupUtilization,"+
						"convert(ifnull(sum(b.Gear3LockupUtilization),'0.0'),decimal(10,1)) as PeriodGear3LockupUtilization,"+
						"convert(ifnull(sum(b.Gear4LockupUtilization),'0.0'),decimal(10,1)) as PeriodGear4LockupUtilization,"+
						"convert(ifnull(sum(b.Gear5LockupUtilization),'0.0'),decimal(10,1)) as PeriodGear5LockupUtilization,"+
						"convert(ifnull(sum(b.Gear6LockupUtilization),'0.0'),decimal(10,1)) as PeriodGear6LockupUtilization, "+
						"convert(ifnull(sum(b.FuelUsedInLPB),'0.0'),decimal(10,1)) as PeriodFuelUsedInLPBLtrs,"+
						"convert(ifnull(sum(b.FuelUsedInMPB),'0.0'),decimal(10,1)) as PeriodFuelUsedInMPBLtrs,"+
						"convert(ifnull(sum(b.FuelUsedInHPB),'0.0'),decimal(10,1)) as PeriodFuelUsedInHPBLtrs,"+
						"convert(ifnull(sum(b.FinishLMode-b.StartLMode),'0.0'),decimal(10,1)) as PeriodLBandHrs,"+
						"convert(ifnull(sum(b.FinishGMode-b.StartGMode),'0.0'),decimal(10,1)) as PeriodGBandHrs,"+
						"convert(ifnull(sum(b.FinishHMode-b.StartHMode),'0.0'),decimal(10,1)) as PeriodHBandHrs,"+
						"convert(ifnull(sum(b.FinishHPlusMode-b.StartHPlusMode),'0.0'),decimal(10,1)) as PeriodHPlusBandHrs, "+
						"ifnull(sum(b.No_AutoIdle_Events-b.Column35), 0) as PeriodNo_AutoIdle_Events,"+
						"ifnull(sum(b.No_AutoOFF_Events-b.Column36),0) as PeriodNo_AutoOFF_Events "+
						"from factInsight_dayAgg_extended b where b.AssetID='"+AssetID+"' and b.TxnDate between '"+startDate+"' and '"+endDate+"'"+
						")bb ON aa.AssetID=bb.AssetID";*/



				query="Select aa.*,bb.*,convert(bb.PeriodTotalFuel/aa.PeriodHMR,decimal(10,1)) as PeriodAverageFuelConsumption "+
						" from"+
						" (Select a.AssetID,a.TxnDate,"+
						"ifnull(a.ModelName,'') as ModelName,"+
						"ifnull(a.CustomerName,'') as CustomerName,"+
						"ifnull(a.CustContact,'') as CustContact,"+
						"ifnull(a.DealerName,'') as DealerName,"+
						"ifnull(a.ZonalName,'') as ZonalName,"+
						"convert(ifnull((a.PowerBandLow),'0.0'),decimal(10,1)) as PowerBandLowHrs,"+
						"convert(ifnull((a.PowerBandMed),'0.0'),decimal(10,1)) as PowerBandMedHrs,"+
						"convert(ifnull((a.PowerBandHigh),'0.0'),decimal(10,1)) as PowerBandHighHrs,"+
						"CONVERT(ifnull((a.PowerBandLow/(a.PowerBandLow+a.PowerBandMed+a.PowerBandHigh))*100,'0.0') , decimal(10,1)) as PowerBandLowPerc,"+
						"CONVERT(ifnull((a.PowerBandMed/(a.PowerBandLow+a.PowerBandMed+a.PowerBandHigh))*100,'0.000') , decimal(10,1)) as PowerBandMedPerc,CONVERT(ifnull((a.PowerBandHigh/(a.PowerBandLow+a.PowerBandMed+a.PowerBandHigh))*100,'0.000') , decimal(10,1)) as PowerBandHighPerc,"+
						//"convert(ifnull(a.PeriodHMR,'0.0'),decimal(10,1)) as PeriodHMR,"+ //Df20180312 @Roopa taking running band data for period CMH calc
						"CONVERT(ifnull(a.EngineOnTime,'0.000') , decimal(10,1)) as PeriodHMR,"+
						"CONVERT(ifnull(a.EngineOnTime,'0.000') , decimal(10,1)) as EngineOnTime,convert(ifnull(a.EngineOffTime,'0.000'), decimal(10,1)) as EngineOffTime,"+
						"convert(ifnull(a.idletime,'0.000'), decimal(10,1))  as idletime,"+
						"convert(ifnull(a.WorkingTime,'0.000'), decimal(10,1))  as WorkingTime"+
						" from factInsight_dayAgg a where a.AssetID='"+AssetID+"' and a.TxnDate between '"+startDate+"' and '"+endDate+"' group by a.TxnDate) aa inner join"+
						" (Select b.AssetID,b.TxnDate,"+
						"convert(ifnull(b.FinishTravellingTime-b.StartTravellingTime,'0.0'),decimal(10,1)) as PeriodTravelTime,"+
						"convert(ifnull(b.FinishSlewTime-b.StartSlewTime,'0.0'),decimal(10,1)) as PeriodSlewTime,"+
						" convert(ifnull(b.FuelUsedInLPB,'0.0'),decimal(10,1)) as PeriodFuelUsedInLPBLtrs,"+
						" convert(ifnull(b.FuelUsedInMPB,'0.0'),decimal(10,1)) as PeriodFuelUsedInMPBLtrs,"+
						" convert(ifnull(b.FuelUsedInHPB,'0.0'),decimal(10,1)) as PeriodFuelUsedInHPBLtrs,"+
						"CONVERT(ifnull(b.FuelUsedInLPB/(b.FuelUsedInLPB+b.FuelUsedInMPB+b.FuelUsedInHPB)*100,'0.000') , decimal(10,1)) as PeriodFuelUsedInLPB,CONVERT(ifnull(b.FuelUsedInMPB/(b.FuelUsedInLPB+b.FuelUsedInMPB+b.FuelUsedInHPB)*100,'0.000') , decimal(10,1)) as PeriodFuelUsedInMPB,"+
						"CONVERT(ifnull(b.FuelUsedInHPB/(b.FuelUsedInLPB+b.FuelUsedInMPB+b.FuelUsedInHPB)*100,'0.000') , decimal(10,1)) as PeriodFuelUsedInHPB,"+
						"convert(ifnull((b.FinishLMode-b.StartLMode),'0.0'),decimal(10,1)) as PeriodLBandHrs,"+
						"convert(ifnull((b.FinishGMode-b.StartGMode),'0.0'),decimal(10,1)) as PeriodGBandHrs,"+
						"convert(ifnull((b.FinishHMode-b.StartHMode),'0.0'),decimal(10,1)) as PeriodHBandHrs,"+
						"convert(ifnull((b.FinishHPlusMode-b.StartHPlusMode),'0.0'),decimal(10,1)) as PeriodHPlusBandHrs,"+
						"convert(ifnull((b.FinishLMode-b.StartLMode)/((b.FinishLMode-b.StartLMode)+(b.FinishGMode-b.StartGMode)+(b.FinishHMode-b.StartHMode)+(b.FinishHPlusMode-b.StartHPlusMode))*100, '0.0'),decimal(10,1)) as PeriodLBand,"+
						"convert(ifnull((b.FinishGMode-b.StartGMode)/((b.FinishLMode-b.StartLMode)+(b.FinishGMode-b.StartGMode)+(b.FinishHMode-b.StartHMode)+(b.FinishHPlusMode-b.StartHPlusMode))*100,'0.0'),decimal(10,1)) as PeriodGBand,"+
						"convert(ifnull((b.FinishHMode-b.StartHMode)/((b.FinishLMode-b.StartLMode)+(b.FinishGMode-b.StartGMode)+(b.FinishHMode-b.StartHMode)+(b.FinishHPlusMode-b.StartHPlusMode))*100, '0.0'),decimal(10,1)) as PeriodHBand,"+
						"convert(ifnull((b.FinishHPlusMode-b.StartHPlusMode)/((b.FinishLMode-b.StartLMode)+(b.FinishGMode-b.StartGMode)+(b.FinishHMode-b.StartHMode)+sum(b.FinishHPlusMode-b.StartHPlusMode))*100, '0.0'),decimal(10,1)) as PeriodHPlusBand,"+
						"convert(ifnull((b.FinishHammerUseTime-b.StartHammerUseTime),'0.0'),decimal(10,1)) as PeriodHammerUseTime,"+
						"convert(ifnull((b.FinishFuelInLtrs-b.StartFuelInLtrs),'0.0'),decimal(10,1)) as PeriodTotalFuel,"+
						"convert(ifnull((b.FuelLoss),'0.0'),decimal(10,1)) as PeriodFuelLoss,"+
						"convert(ifnull((b.CarbonEmission),'0.0'),decimal(10,1)) as PeriodCarbonEmission,"+
						"convert(ifnull((b.EngineOnCount),'0'),decimal(10,1)) as PeriodEngineOnCount,"+
						"convert(ifnull((b.EngineOffCount),'0'),decimal(10,1)) as PeriodEngineOffCount,"+
						"convert(ifnull((b.Column19-b.Column37),'0'),decimal(10,1)) as PeriodHighRPMShutdownCount,"+
						"convert(ifnull((b.Column18-b.Column38),'0'),decimal(10,1))as PeriodLongEngineIdlingCount,"+
						"convert(ifnull((b.PowerBoostTime),'0.0'),decimal(10,1)) as PeriodPowerBoostTime,"+
						"convert(ifnull((b.HammerAbuseCount-b.Column39),'0.0'),decimal(10,1)) as PeriodHammerAbuseCount,"+
						"convert(ifnull((b.Gear1FwdUtilization),'0.0'),decimal(10,1)) as PeriodGear1FwdUtilization,"+
						"convert(ifnull((b.Gear2FwdUtilization),'0.0'),decimal(10,1)) as PeriodGear2FwdUtilization,"+
						"convert(ifnull((b.Gear31FwdUtilization),'0.0'),decimal(10,1)) as PeriodGear3FwdUtilization,"+
						"convert(ifnull((b.Gear4FwdUtilization),'0,0'),decimal(10,1)) as PeriodGear4FwdUtilization,"+
						"convert(ifnull((b.Gear5FwdUtilization),'0.0'),decimal(10,1)) as PeriodGear5FwdUtilization,"+
						"convert(ifnull((b.Gear6FwdUtilization),'0.0'),decimal(10,1)) as PeriodGear6FwdUtilization,"+
						"convert(ifnull((b.Gear1BkwdUtilization),'0.0'),decimal(10,1)) as PeriodGear1BkwdUtilization,"+
						"convert(ifnull((b.Gear2BkwdUtilization),'0.0'),decimal(10,1)) as PeriodGear2BkwdUtilization,"+
						"convert(ifnull((b.Gear3BkwdUtilization),'0.0'),decimal(10,1)) as PeriodGear3BkwdUtilization,"+
						"convert(ifnull((b.Gear4BkwdUtilization),'0.0'),decimal(10,1)) as PeriodGear4BkwdUtilization,"+
						"convert(ifnull((b.Gear5BkwdUtilization),'0.0'),decimal(10,1)) as PeriodGear5BkwdUtilization,"+
						"convert(ifnull((b.Gear6BkwdUtilization),'0.0'),decimal(10,1)) as PeriodGear6BkwdUtilization,"+
						"convert(ifnull((b.Gear1LockupUtilization),'0.0'),decimal(10,1)) as PeriodGear1LockupUtilization,"+
						"convert(ifnull((b.Gear2LockupUtilization),'0.0'),decimal(10,1)) as PeriodGear2LockupUtilization,"+
						"convert(ifnull((b.Gear3LockupUtilization),'0.0'),decimal(10,1)) as PeriodGear3LockupUtilization,"+
						"convert(ifnull((b.Gear4LockupUtilization),'0.0'),decimal(10,1)) as PeriodGear4LockupUtilization,"+
						"convert(ifnull((b.Gear5LockupUtilization),'0.0'),decimal(10,1)) as PeriodGear5LockupUtilization,"+
						"convert(ifnull((b.Gear6LockupUtilization),'0.0'),decimal(10,1)) as PeriodGear6LockupUtilization,"+
						"ifnull((b.No_AutoIdle_Events-b.Column35), 0) as PeriodNo_AutoIdle_Events,"+
						"ifnull((b.No_AutoOFF_Events-b.Column36),0) as PeriodNo_AutoOFF_Events "+
						" from factInsight_dayAgg_extended b where b.AssetID='"+AssetID+"' and b.TxnDate between '"+startDate+"' and '"+endDate+"' group by b.TxnDate"+
						")bb ON aa.AssetID=bb.AssetID and aa.TxnDate=bb.TxnDate";



			}

			else{

				selectQuery="Select aa.*,bb.*,convert(bb.PeriodTotalFuel/aa.PeriodHMR,decimal(10,1)) as PeriodAverageFuelConsumption ";
				fromQuery="from ";
				leftQuery="Select count(distinct AssetID) as machineCount,a.Column4 as MsgID,a.ModelCode, a.AssetID,a.TxnDate,"+
						"ifnull(a.ModelName,'') as ModelName,"+
						"ifnull(a.CustomerName,'') as CustomerName,"+
						"ifnull(a.CustContact,'') as CustContact,"+
						"ifnull(a.DealerName,'') as DealerName,"+
						"ifnull(a.ZonalName,'') as ZonalName,"+
						"CONVERT(ifnull(sum(a.PowerBandLow)/(sum(a.PowerBandLow)+sum(a.PowerBandMed)+sum(a.PowerBandHigh))*100,'0.0') , decimal(10,1)) as PowerBandLowPerc,"+
						"CONVERT(ifnull(sum(a.PowerBandMed)/(sum(a.PowerBandLow)+sum(a.PowerBandMed)+sum(a.PowerBandHigh))*100,'0.000') , decimal(10,1)) as PowerBandMedPerc,CONVERT(ifnull(sum(a.PowerBandHigh)/(sum(a.PowerBandLow)+sum(a.PowerBandMed)+sum(a.PowerBandHigh))*100,'0.000') , decimal(10,1)) as PowerBandHighPerc,"+
						//"convert(ifnull(sum(a.PeriodHMR),'0.0'),decimal(10,1)) as PeriodHMR,"+ //Df20180312 @Roopa taking running band data for period CMH calc
						"CONVERT(ifnull(sum(a.EngineOnTime),'0.000') , decimal(10,1)) as PeriodHMR,"+
						"CONVERT(ifnull(sum(a.EngineOnTime),'0.000') , decimal(10,1)) as EngineOnTime,convert(ifnull(sum(a.EngineOffTime),'0.000'), decimal(10,1)) as EngineOffTime,"+
						"convert(ifnull(sum(a.idletime),'0.000'), decimal(10,1))  as idletime,"+
						"convert(ifnull(sum(a.WorkingTime),'0.000'), decimal(10,1))  as WorkingTime, "+
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


				rightQuery="Select count(distinct AssetID) as machineCount,b.Column21 as MsgID,b.Column5 as ModelCode,b.AssetID,b.TxnDate,"+
						"convert(ifnull(sum(b.FinishTravellingTime-b.StartTravellingTime),'0.0'),decimal(10,1)) as PeriodTravelTime,"+
						"convert(ifnull(sum(b.FinishSlewTime-b.StartSlewTime),'0.0'),decimal(10,1)) as PeriodSlewTime,"+ 
						"CONVERT(ifnull(sum(b.FuelUsedInLPB)/(sum(b.FuelUsedInLPB)+sum(b.FuelUsedInMPB)+sum(b.FuelUsedInHPB))*100,'0.000') , decimal(10,1)) as PeriodFuelUsedInLPB,CONVERT(ifnull(sum(b.FuelUsedInMPB)/(sum(b.FuelUsedInLPB)+sum(b.FuelUsedInMPB)+sum(b.FuelUsedInHPB))*100,'0.000') , decimal(10,1)) as PeriodFuelUsedInMPB,"+
						"CONVERT(ifnull(sum(b.FuelUsedInHPB)/(sum(b.FuelUsedInLPB)+sum(b.FuelUsedInMPB)+sum(b.FuelUsedInHPB))*100,'0.000') , decimal(10,1)) as PeriodFuelUsedInHPB,"+
						"convert(ifnull(sum(b.FinishLMode-b.StartLMode)/(sum(b.FinishLMode-b.StartLMode)+sum(b.FinishGMode-b.StartGMode)+sum(b.FinishHMode-b.StartHMode)+sum(b.FinishHPlusMode-b.StartHPlusMode))*100, '0.0'),decimal(10,1)) as PeriodLBand,"+
						"convert(ifnull(sum(b.FinishGMode-b.StartGMode)/(sum(b.FinishLMode-b.StartLMode)+sum(b.FinishGMode-b.StartGMode)+sum(b.FinishHMode-b.StartHMode)+sum(b.FinishHPlusMode-b.StartHPlusMode))*100,'0.0'),decimal(10,1)) as PeriodGBand,"+
						"convert(ifnull(sum(b.FinishHMode-b.StartHMode)/(sum(b.FinishLMode-b.StartLMode)+sum(b.FinishGMode-b.StartGMode)+sum(b.FinishHMode-b.StartHMode)+sum(b.FinishHPlusMode-b.StartHPlusMode))*100, '0.0'),decimal(10,1)) as PeriodHBand,"+
						"convert(ifnull(sum(b.FinishHPlusMode-b.StartHPlusMode)/(sum(b.FinishLMode-b.StartLMode)+sum(b.FinishGMode-b.StartGMode)+sum(b.FinishHMode-b.StartHMode)+sum(b.FinishHPlusMode-b.StartHPlusMode))*100, '0.0'),decimal(10,1)) as PeriodHPlusBand,"+
						"convert(ifnull(sum(b.FinishHammerUseTime-b.StartHammerUseTime),'0.0'),decimal(10,1)) as PeriodHammerUseTime,"+
						"convert(ifnull(sum(b.FinishFuelInLtrs-b.StartFuelInLtrs),'0.0'),decimal(10,1)) as PeriodTotalFuel,"+
						"convert(ifnull(sum(b.FuelLoss),'0.0'),decimal(10,1)) as PeriodFuelLoss,"+
						"convert(ifnull(sum(b.CarbonEmission),'0.0'),decimal(10,1)) as PeriodCarbonEmission,"+
						"convert(ifnull(sum(b.EngineOnCount),'0'),decimal(10,1)) as PeriodEngineOnCount,"+
						"convert(ifnull(sum(b.EngineOffCount),'0'),decimal(10,1)) as PeriodEngineOffCount,"+
						"convert(ifnull(sum(b.Column19-b.Column37),'0'),decimal(10,1)) as PeriodHighRPMShutdownCount,"+
						"convert(ifnull(sum(b.Column18-b.Column38),'0'),decimal(10,1))as PeriodLongEngineIdlingCount,"+
						"convert(ifnull(sum(b.PowerBoostTime),'0.0'),decimal(10,1)) as PeriodPowerBoostTime,"+
						"convert(ifnull(sum(b.HammerAbuseCount-b.Column39),'0.0'),decimal(10,1)) as PeriodHammerAbuseCount,"+
						"convert(ifnull(sum(b.Gear1FwdUtilization),'0.0'),decimal(10,1)) as PeriodGear1FwdUtilization,"+
						"convert(ifnull(sum(b.Gear2FwdUtilization),'0.0'),decimal(10,1)) as PeriodGear2FwdUtilization,"+
						"convert(ifnull(sum(b.Gear31FwdUtilization),'0.0'),decimal(10,1)) as PeriodGear3FwdUtilization,"+
						"convert(ifnull(sum(b.Gear4FwdUtilization),'0,0'),decimal(10,1)) as PeriodGear4FwdUtilization,"+
						"convert(ifnull(sum(b.Gear5FwdUtilization),'0.0'),decimal(10,1)) as PeriodGear5FwdUtilization,"+
						"convert(ifnull(sum(b.Gear6FwdUtilization),'0.0'),decimal(10,1)) as PeriodGear6FwdUtilization,"+
						"convert(ifnull(sum(b.Gear1BkwdUtilization),'0.0'),decimal(10,1)) as PeriodGear1BkwdUtilization,"+
						"convert(ifnull(sum(b.Gear2BkwdUtilization),'0.0'),decimal(10,1)) as PeriodGear2BkwdUtilization,"+
						"convert(ifnull(sum(b.Gear3BkwdUtilization),'0.0'),decimal(10,1)) as PeriodGear3BkwdUtilization,"+
						"convert(ifnull(sum(b.Gear4BkwdUtilization),'0.0'),decimal(10,1)) as PeriodGear4BkwdUtilization,"+
						"convert(ifnull(sum(b.Gear5BkwdUtilization),'0.0'),decimal(10,1)) as PeriodGear5BkwdUtilization,"+
						"convert(ifnull(sum(b.Gear6BkwdUtilization),'0.0'),decimal(10,1)) as PeriodGear6BkwdUtilization,"+
						"convert(ifnull(sum(b.Gear1LockupUtilization),'0.0'),decimal(10,1)) as PeriodGear1LockupUtilization,"+
						"convert(ifnull(sum(b.Gear2LockupUtilization),'0.0'),decimal(10,1)) as PeriodGear2LockupUtilization,"+
						"convert(ifnull(sum(b.Gear3LockupUtilization),'0.0'),decimal(10,1)) as PeriodGear3LockupUtilization,"+
						"convert(ifnull(sum(b.Gear4LockupUtilization),'0.0'),decimal(10,1)) as PeriodGear4LockupUtilization,"+
						"convert(ifnull(sum(b.Gear5LockupUtilization),'0.0'),decimal(10,1)) as PeriodGear5LockupUtilization,"+
						"convert(ifnull(sum(b.Gear6LockupUtilization),'0.0'),decimal(10,1)) as PeriodGear6LockupUtilization, "+
						"convert(ifnull(sum(b.FuelUsedInLPB),'0.0'),decimal(10,1)) as PeriodFuelUsedInLPBLtrs,"+
						"convert(ifnull(sum(b.FuelUsedInMPB),'0.0'),decimal(10,1)) as PeriodFuelUsedInMPBLtrs,"+
						"convert(ifnull(sum(b.FuelUsedInHPB),'0.0'),decimal(10,1)) as PeriodFuelUsedInHPBLtrs,"+
						"convert(ifnull(sum(b.FinishLMode-b.StartLMode),'0.0'),decimal(10,1)) as PeriodLBandHrs,"+
						"convert(ifnull(sum(b.FinishGMode-b.StartGMode),'0.0'),decimal(10,1)) as PeriodGBandHrs,"+
						"convert(ifnull(sum(b.FinishHMode-b.StartHMode),'0.0'),decimal(10,1)) as PeriodHBandHrs,"+
						"convert(ifnull(sum(b.FinishHPlusMode-b.StartHPlusMode),'0.0'),decimal(10,1)) as PeriodHPlusBandHrs, "+
						"ifnull(sum(b.No_AutoIdle_Events-b.Column35), 0) as PeriodNo_AutoIdle_Events,"+
						"ifnull(sum(b.No_AutoOFF_Events-b.Column36),0) as PeriodNo_AutoOFF_Events "+
						"from factInsight_dayAgg_extended b where b.TxnDate between '"+startDate+"' and '"+endDate+"'";

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

				joinQuery="ON aa.ModelCode=bb.ModelCode";


				query=selectQuery+fromQuery+"("+leftQuery+")"+"aa "+"inner join "+"("+rightQuery+")"+"bb "+joinQuery;

			}

			iLogger.info("ExcavatorDashboardRESTService:getExcavatorChartsAndPeriodData:Select Query::"+query);

			ConnectMySQL connMySql = new ConnectMySQL();
			prodConnection = connMySql.getProdDb2Connection();
			statement = prodConnection.createStatement();
			rs = statement.executeQuery(query);

			

			while(rs.next()){
				respObj = new HashMap<String, Object>();

				int machineCount=0;
				if(AssetID==null){
					machineCount=rs.getInt("machineCount");
				}


				if(AssetID!=null){

					respObj.put("AssetID",rs.getString("AssetID"));

					respObj.put("machineCount", machineCount);
					respObj.put("ModelName", rs.getString("ModelName"));
					respObj.put("CustomerName", rs.getString("CustomerName"));
					respObj.put("CustContact", rs.getString("CustContact"));
					respObj.put("DealerName", rs.getString("DealerName"));
					respObj.put("ZonalName", rs.getString("ZonalName"));

					
					PeriodHMR=PeriodHMR+rs.getDouble("PeriodHMR");
					EngineOnTime=EngineOnTime+rs.getDouble("EngineOnTime");
					EngineOffTime=EngineOffTime+rs.getDouble("EngineOffTime");
					idletime=idletime+rs.getDouble("idletime");
					WorkingTime=WorkingTime+rs.getDouble("WorkingTime");
					PeriodTravelTime=PeriodTravelTime+rs.getDouble("PeriodTravelTime");
					PeriodSlewTime=PeriodSlewTime+rs.getDouble("PeriodSlewTime");
					/*PeriodFuelUsedInLPB=PeriodFuelUsedInLPB+rs.getDouble("PeriodFuelUsedInLPB");
					PeriodFuelUsedInMPB=PeriodFuelUsedInMPB+rs.getDouble("PeriodFuelUsedInMPB");
					PeriodFuelUsedInHPB=PeriodFuelUsedInHPB+rs.getDouble("PeriodFuelUsedInHPB");*/
					
					/*PeriodLBand=PeriodLBand+rs.getDouble("PeriodLBand");
					PeriodGBand=PeriodGBand+rs.getDouble("PeriodGBand");
					PeriodHBand=PeriodHBand+rs.getDouble("PeriodHBand");
					PeriodHPlusBand=PeriodHPlusBand+rs.getDouble("PeriodHPlusBand");*/
					
					PeriodHammerUseTime=PeriodHammerUseTime+rs.getDouble("PeriodHammerUseTime");
					PeriodTotalFuel=PeriodTotalFuel+rs.getDouble("PeriodTotalFuel");
					PeriodFuelLoss=PeriodFuelLoss+rs.getDouble("PeriodFuelLoss");
					PeriodCarbonEmission=PeriodCarbonEmission+rs.getInt("PeriodCarbonEmission");
					PeriodEngineOnCount=PeriodEngineOnCount+rs.getInt("PeriodEngineOnCount");
					PeriodEngineOffCount=PeriodEngineOffCount+rs.getInt("PeriodEngineOffCount");
					PeriodHighRPMShutdownCount=PeriodHighRPMShutdownCount+rs.getInt("PeriodHighRPMShutdownCount");
					PeriodLongEngineIdlingCount=PeriodLongEngineIdlingCount+rs.getInt("PeriodLongEngineIdlingCount");
					PeriodPowerBoostTime=PeriodPowerBoostTime+rs.getDouble("PeriodPowerBoostTime");
					PeriodHammerAbuseCount=PeriodHammerAbuseCount+rs.getDouble("PeriodHammerAbuseCount");
					PeriodGear1FwdUtilization=PeriodGear1FwdUtilization+rs.getDouble("PeriodGear1FwdUtilization");
					PeriodGear2FwdUtilization=PeriodGear2FwdUtilization+rs.getDouble("PeriodGear2FwdUtilization");
					PeriodGear3FwdUtilization=PeriodGear3FwdUtilization+rs.getDouble("PeriodGear3FwdUtilization");
					PeriodGear4FwdUtilization=PeriodGear4FwdUtilization+rs.getDouble("PeriodGear4FwdUtilization");
					PeriodGear5FwdUtilization=PeriodGear5FwdUtilization+rs.getDouble("PeriodGear5FwdUtilization");
					PeriodGear6FwdUtilization=PeriodGear6FwdUtilization+rs.getDouble("PeriodGear6FwdUtilization");
					PeriodGear1BkwdUtilization=PeriodGear1BkwdUtilization+rs.getDouble("PeriodGear1BkwdUtilization");
					PeriodGear2BkwdUtilization=PeriodGear2BkwdUtilization+rs.getDouble("PeriodGear2BkwdUtilization");
					PeriodGear3BkwdUtilization=PeriodGear3BkwdUtilization+rs.getDouble("PeriodGear3BkwdUtilization");
					PeriodGear4BkwdUtilization=PeriodGear4BkwdUtilization+rs.getDouble("PeriodGear4BkwdUtilization");
					PeriodGear5BkwdUtilization=PeriodGear5BkwdUtilization+rs.getDouble("PeriodGear5BkwdUtilization");
					PeriodGear6BkwdUtilization=PeriodGear6BkwdUtilization+rs.getDouble("PeriodGear6BkwdUtilization");
					PeriodGear1LockupUtilization=PeriodGear1LockupUtilization+rs.getDouble("PeriodGear1LockupUtilization");
					PeriodGear2LockupUtilization=PeriodGear2LockupUtilization+rs.getDouble("PeriodGear2LockupUtilization");
					PeriodGear3LockupUtilization=PeriodGear3LockupUtilization+rs.getDouble("PeriodGear3LockupUtilization");
					PeriodGear4LockupUtilization=PeriodGear4LockupUtilization+rs.getDouble("PeriodGear4LockupUtilization");
					PeriodGear5LockupUtilization=PeriodGear5LockupUtilization+rs.getDouble("PeriodGear5LockupUtilization");
					PeriodGear6LockupUtilization=PeriodGear6LockupUtilization+rs.getDouble("PeriodGear6LockupUtilization");
				//	PeriodAverageFuelConsumption=PeriodAverageFuelConsumption+rs.getDouble("PeriodAverageFuelConsumption");
					
					PeriodAverageFuelConsumption=PeriodTotalFuel/PeriodHMR;
					
					PowerBandLowHrs=PowerBandLowHrs+rs.getDouble("PowerBandLowHrs");
					PowerBandMedHrs=PowerBandMedHrs+rs.getDouble("PowerBandMedHrs");
					PowerBandHighHrs=PowerBandHighHrs+rs.getDouble("PowerBandHighHrs");
					
					PowerBandLowPerc=(PowerBandLowHrs/(PowerBandLowHrs+PowerBandMedHrs+PowerBandHighHrs))*100;
					PowerBandMedPerc=(PowerBandMedHrs/(PowerBandLowHrs+PowerBandMedHrs+PowerBandHighHrs))*100;
					PowerBandHighPerc=(PowerBandHighHrs/(PowerBandLowHrs+PowerBandMedHrs+PowerBandHighHrs))*100;
					
					PeriodFuelUsedInLPBLtrs=PeriodFuelUsedInLPBLtrs+rs.getDouble("PeriodFuelUsedInLPBLtrs");
					PeriodFuelUsedInMPBLtrs=PeriodFuelUsedInMPBLtrs+rs.getDouble("PeriodFuelUsedInMPBLtrs");
					PeriodFuelUsedInHPBLtrs=PeriodFuelUsedInHPBLtrs+rs.getDouble("PeriodFuelUsedInHPBLtrs");
					
					PeriodFuelUsedInLPB=(PeriodFuelUsedInLPBLtrs/(PeriodFuelUsedInLPBLtrs+PeriodFuelUsedInMPBLtrs+PeriodFuelUsedInHPBLtrs))*100;
					PeriodFuelUsedInMPB=(PeriodFuelUsedInMPBLtrs/(PeriodFuelUsedInLPBLtrs+PeriodFuelUsedInMPBLtrs+PeriodFuelUsedInHPBLtrs))*100;
					PeriodFuelUsedInHPB=(PeriodFuelUsedInHPBLtrs/(PeriodFuelUsedInLPBLtrs+PeriodFuelUsedInMPBLtrs+PeriodFuelUsedInHPBLtrs))*100;
					
					PeriodLBandHrs=PeriodLBandHrs+rs.getDouble("PeriodLBandHrs");
					PeriodGBandHrs=PeriodGBandHrs+rs.getDouble("PeriodGBandHrs");
					PeriodHBandHrs=PeriodHBandHrs+rs.getDouble("PeriodHBandHrs");
					PeriodHPlusBandHrs=PeriodHPlusBandHrs+rs.getDouble("PeriodHPlusBandHrs");
					
					PeriodLBand=(PeriodLBandHrs/(PeriodLBandHrs+PeriodGBandHrs+PeriodHBandHrs+PeriodHPlusBandHrs))*100;
					PeriodGBand=(PeriodGBandHrs/(PeriodLBandHrs+PeriodGBandHrs+PeriodHBandHrs+PeriodHPlusBandHrs))*100;
					PeriodHBand=(PeriodHBandHrs/(PeriodLBandHrs+PeriodGBandHrs+PeriodHBandHrs+PeriodHPlusBandHrs))*100;
					PeriodHPlusBand=(PeriodHPlusBandHrs/(PeriodLBandHrs+PeriodGBandHrs+PeriodHBandHrs+PeriodHPlusBandHrs))*100;
					
					PeriodNo_AutoIdle_Events=PeriodNo_AutoIdle_Events+rs.getInt("PeriodNo_AutoIdle_Events");
					PeriodNo_AutoOFF_Events=PeriodNo_AutoOFF_Events+rs.getInt("PeriodNo_AutoOFF_Events");

					respObj.put("PowerBandLowPerc",decformat.format( PowerBandLowPerc));
					respObj.put("PowerBandMedPerc",decformat.format( PowerBandMedPerc));
					respObj.put("PowerBandHighPerc",decformat.format( PowerBandHighPerc));
					respObj.put("PeriodHMR",decformat.format( PeriodHMR));
					respObj.put("EngineOnTime",decformat.format( EngineOnTime));
					respObj.put("EngineOffTime",decformat.format( EngineOffTime));
					respObj.put("idletime",decformat.format( idletime));
					respObj.put("WorkingTime",decformat.format( WorkingTime));
					respObj.put("PeriodTravelTime",decformat.format( PeriodTravelTime));
					respObj.put("PeriodSlewTime",decformat.format( PeriodSlewTime));
					respObj.put("PeriodFuelUsedInLPB",decformat.format( PeriodFuelUsedInLPB));
					respObj.put("PeriodFuelUsedInMPB",decformat.format( PeriodFuelUsedInMPB));
					respObj.put("PeriodFuelUsedInHPB",decformat.format( PeriodFuelUsedInHPB));
					respObj.put("PeriodLBand",decformat.format( PeriodLBand));
					respObj.put("PeriodGBand",decformat.format( PeriodGBand));
					respObj.put("PeriodHBand",decformat.format( PeriodHBand));
					respObj.put("PeriodHPlusBand",decformat.format( PeriodHPlusBand));
					respObj.put("PeriodHammerUseTime",decformat.format( PeriodHammerUseTime));
					respObj.put("PeriodTotalFuel",decformat.format( PeriodTotalFuel));
					respObj.put("PeriodFuelLoss",decformat.format( PeriodFuelLoss));
					respObj.put("PeriodCarbonEmission",decformat.format( PeriodCarbonEmission));
					respObj.put("PeriodEngineOnCount",PeriodEngineOnCount);
					respObj.put("PeriodEngineOffCount",PeriodEngineOffCount);
					respObj.put("PeriodHighRPMShutdownCount",PeriodHighRPMShutdownCount);
					respObj.put("PeriodLongEngineIdlingCount",PeriodLongEngineIdlingCount);
					respObj.put("PeriodPowerBoostTime",decformat.format( PeriodPowerBoostTime));
					respObj.put("PeriodHammerAbuseCount",PeriodHammerAbuseCount);
					respObj.put("PeriodGear1FwdUtilization",decformat.format( PeriodGear1FwdUtilization));
					respObj.put("PeriodGear2FwdUtilization",decformat.format( PeriodGear2FwdUtilization));
					respObj.put("PeriodGear3FwdUtilization",decformat.format( PeriodGear3FwdUtilization));
					respObj.put("PeriodGear4FwdUtilization",decformat.format( PeriodGear4FwdUtilization));
					respObj.put("PeriodGear5FwdUtilization",decformat.format( PeriodGear5FwdUtilization));
					respObj.put("PeriodGear6FwdUtilization",decformat.format( PeriodGear6FwdUtilization));
					respObj.put("PeriodGear1BkwdUtilization",decformat.format( PeriodGear1BkwdUtilization));
					respObj.put("PeriodGear2BkwdUtilization",decformat.format( PeriodGear2BkwdUtilization));
					respObj.put("PeriodGear3BkwdUtilization",decformat.format( PeriodGear3BkwdUtilization));
					respObj.put("PeriodGear4BkwdUtilization",decformat.format( PeriodGear4BkwdUtilization));
					respObj.put("PeriodGear5BkwdUtilization",decformat.format( PeriodGear5BkwdUtilization));
					respObj.put("PeriodGear6BkwdUtilization",decformat.format( PeriodGear6BkwdUtilization));
					respObj.put("PeriodGear1LockupUtilization",decformat.format( PeriodGear1LockupUtilization));
					respObj.put("PeriodGear2LockupUtilization",decformat.format( PeriodGear2LockupUtilization));
					respObj.put("PeriodGear3LockupUtilization",decformat.format( PeriodGear3LockupUtilization));
					respObj.put("PeriodGear4LockupUtilization",decformat.format( PeriodGear4LockupUtilization));
					respObj.put("PeriodGear5LockupUtilization",decformat.format( PeriodGear5LockupUtilization));
					respObj.put("PeriodGear6LockupUtilization",decformat.format( PeriodGear6LockupUtilization));
					respObj.put("PeriodAverageFuelConsumption",decformat.format( PeriodAverageFuelConsumption));

					respObj.put("PowerBandLowHrs",decformat.format( PowerBandLowHrs));
					respObj.put("PowerBandMedHrs",decformat.format( PowerBandMedHrs));
					respObj.put("PowerBandHighHrs",decformat.format( PowerBandHighHrs));
					respObj.put("PeriodFuelUsedInLPBLtrs",decformat.format( PeriodFuelUsedInLPBLtrs));
					respObj.put("PeriodFuelUsedInMPBLtrs",decformat.format( PeriodFuelUsedInMPBLtrs));
					respObj.put("PeriodFuelUsedInHPBLtrs",decformat.format( PeriodFuelUsedInHPBLtrs));
					respObj.put("PeriodLBandHrs",decformat.format( PeriodLBandHrs));
					respObj.put("PeriodGBandHrs",decformat.format( PeriodGBandHrs));
					respObj.put("PeriodHBandHrs",decformat.format( PeriodHBandHrs));
					respObj.put("PeriodHPlusBandHrs",decformat.format( PeriodHPlusBandHrs));

					respObj.put("PeriodNo_AutoIdle_Events", PeriodNo_AutoIdle_Events);
					respObj.put("PeriodNo_AutoOFF_Events", PeriodNo_AutoOFF_Events);
					
					
					//DF20180503 @Roopa adding new fields with default values
					respObj.put("PowerBandIdle", 0.0);
					respObj.put("PowerBandIdlePerct", 0.0);
					respObj.put("PeriodFuelLoss", 0.0);
					respObj.put("PeriodFuelLossPerct", 0.0);

					/*respObj.put("PowerBandLowPerc", rs.getDouble("PowerBandLowPerc"));
					respObj.put("PowerBandMedPerc", rs.getDouble("PowerBandMedPerc"));
					respObj.put("PowerBandHighPerc", rs.getDouble("PowerBandHighPerc"));
					respObj.put("PeriodHMR", rs.getDouble("PeriodHMR"));
					respObj.put("EngineOnTime", rs.getDouble("EngineOnTime"));
					respObj.put("EngineOffTime", rs.getDouble("EngineOffTime"));
					respObj.put("idletime", rs.getDouble("idletime"));
					respObj.put("WorkingTime", rs.getDouble("WorkingTime"));
					respObj.put("PeriodTravelTime", rs.getDouble("PeriodTravelTime"));
					respObj.put("PeriodSlewTime", rs.getDouble("PeriodSlewTime"));
					respObj.put("PeriodFuelUsedInLPB", rs.getDouble("PeriodFuelUsedInLPB"));
					respObj.put("PeriodFuelUsedInMPB", rs.getDouble("PeriodFuelUsedInMPB"));
					respObj.put("PeriodFuelUsedInHPB", rs.getDouble("PeriodFuelUsedInHPB"));
					respObj.put("PeriodLBand", rs.getDouble("PeriodLBand"));
					respObj.put("PeriodGBand", rs.getDouble("PeriodGBand"));
					respObj.put("PeriodHBand", rs.getDouble("PeriodHBand"));
					respObj.put("PeriodHPlusBand", rs.getDouble("PeriodHPlusBand"));
					respObj.put("PeriodHammerUseTime", rs.getDouble("PeriodHammerUseTime"));
					respObj.put("PeriodTotalFuel", rs.getDouble("PeriodTotalFuel"));
					respObj.put("PeriodFuelLoss", rs.getDouble("PeriodFuelLoss"));
					respObj.put("PeriodCarbonEmission", rs.getDouble("PeriodCarbonEmission"));
					respObj.put("PeriodEngineOnCount", rs.getInt("PeriodEngineOnCount"));
					respObj.put("PeriodEngineOffCount", rs.getInt("PeriodEngineOffCount"));
					respObj.put("PeriodHighRPMShutdownCount", rs.getDouble("PeriodHighRPMShutdownCount"));
					respObj.put("PeriodLongEngineIdlingCount", rs.getDouble("PeriodLongEngineIdlingCount"));
					respObj.put("PeriodPowerBoostTime", rs.getDouble("PeriodPowerBoostTime"));
					respObj.put("PeriodHammerAbuseCount", rs.getDouble("PeriodHammerAbuseCount"));
					respObj.put("PeriodGear1FwdUtilization", rs.getDouble("PeriodGear1FwdUtilization"));
					respObj.put("PeriodGear2FwdUtilization", rs.getDouble("PeriodGear2FwdUtilization"));
					respObj.put("PeriodGear3FwdUtilization", rs.getDouble("PeriodGear3FwdUtilization"));
					respObj.put("PeriodGear4FwdUtilization", rs.getDouble("PeriodGear4FwdUtilization"));
					respObj.put("PeriodGear5FwdUtilization", rs.getDouble("PeriodGear5FwdUtilization"));
					respObj.put("PeriodGear6FwdUtilization", rs.getDouble("PeriodGear6FwdUtilization"));
					respObj.put("PeriodGear1BkwdUtilization", rs.getDouble("PeriodGear1BkwdUtilization"));
					respObj.put("PeriodGear2BkwdUtilization", rs.getDouble("PeriodGear2BkwdUtilization"));
					respObj.put("PeriodGear3BkwdUtilization", rs.getDouble("PeriodGear3BkwdUtilization"));
					respObj.put("PeriodGear4BkwdUtilization", rs.getDouble("PeriodGear4BkwdUtilization"));
					respObj.put("PeriodGear5BkwdUtilization", rs.getDouble("PeriodGear5BkwdUtilization"));
					respObj.put("PeriodGear6BkwdUtilization", rs.getDouble("PeriodGear6BkwdUtilization"));
					respObj.put("PeriodGear1LockupUtilization", rs.getDouble("PeriodGear1LockupUtilization"));
					respObj.put("PeriodGear2LockupUtilization", rs.getDouble("PeriodGear2LockupUtilization"));
					respObj.put("PeriodGear3LockupUtilization", rs.getDouble("PeriodGear3LockupUtilization"));
					respObj.put("PeriodGear4LockupUtilization", rs.getDouble("PeriodGear4LockupUtilization"));
					respObj.put("PeriodGear5LockupUtilization", rs.getDouble("PeriodGear5LockupUtilization"));
					respObj.put("PeriodGear6LockupUtilization", rs.getDouble("PeriodGear6LockupUtilization"));
					respObj.put("PeriodAverageFuelConsumption", rs.getDouble("PeriodAverageFuelConsumption"));

					respObj.put("PowerBandLowHrs", rs.getDouble("PowerBandLowHrs"));
					respObj.put("PowerBandMedHrs", rs.getDouble("PowerBandMedHrs"));
					respObj.put("PowerBandHighHrs", rs.getDouble("PowerBandHighHrs"));
					respObj.put("PeriodFuelUsedInLPBLtrs", rs.getDouble("PeriodFuelUsedInLPBLtrs"));
					respObj.put("PeriodFuelUsedInMPBLtrs", rs.getDouble("PeriodFuelUsedInMPBLtrs"));
					respObj.put("PeriodFuelUsedInHPBLtrs", rs.getDouble("PeriodFuelUsedInHPBLtrs"));
					respObj.put("PeriodLBandHrs", rs.getDouble("PeriodLBandHrs"));
					respObj.put("PeriodGBandHrs", rs.getDouble("PeriodGBandHrs"));
					respObj.put("PeriodHBandHrs", rs.getDouble("PeriodHBandHrs"));
					respObj.put("PeriodHPlusBandHrs", rs.getDouble("PeriodHPlusBandHrs"));

					respObj.put("PeriodNo_AutoIdle_Events", rs.getDouble("PeriodNo_AutoIdle_Events"));
					respObj.put("PeriodNo_AutoOFF_Events", rs.getDouble("PeriodNo_AutoOFF_Events"));*/
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
					respObj.put("PeriodTravelTime", decformat.format(rs.getDouble("PeriodTravelTime")/machineCount));
					respObj.put("PeriodSlewTime", decformat.format(rs.getDouble("PeriodSlewTime")/machineCount));
					respObj.put("PeriodFuelUsedInLPB", decformat.format(rs.getDouble("PeriodFuelUsedInLPB")/machineCount));
					respObj.put("PeriodFuelUsedInMPB", decformat.format(rs.getDouble("PeriodFuelUsedInMPB")/machineCount));
					respObj.put("PeriodFuelUsedInHPB", decformat.format(rs.getDouble("PeriodFuelUsedInHPB")/machineCount));
					respObj.put("PeriodLBand", decformat.format(rs.getDouble("PeriodLBand")/machineCount));
					respObj.put("PeriodGBand", decformat.format(rs.getDouble("PeriodGBand")/machineCount));
					respObj.put("PeriodHBand", decformat.format(rs.getDouble("PeriodHBand")/machineCount));
					respObj.put("PeriodHPlusBand", decformat.format(rs.getDouble("PeriodHPlusBand")/machineCount));
					respObj.put("PeriodHammerUseTime", decformat.format(rs.getDouble("PeriodHammerUseTime")/machineCount));
					respObj.put("PeriodTotalFuel", decformat.format(rs.getDouble("PeriodTotalFuel")/machineCount));
					respObj.put("PeriodFuelLoss", decformat.format(rs.getDouble("PeriodFuelLoss")/machineCount));
					respObj.put("PeriodCarbonEmission", decformat.format(rs.getDouble("PeriodCarbonEmission")/machineCount));
					respObj.put("PeriodEngineOnCount", (int) Math.round(rs.getDouble("PeriodEngineOnCount")/machineCount));
					respObj.put("PeriodEngineOffCount", (int) Math.round(rs.getDouble("PeriodEngineOffCount")/machineCount));
					respObj.put("PeriodHighRPMShutdownCount", (int) Math.round(rs.getDouble("PeriodHighRPMShutdownCount")/machineCount));
					respObj.put("PeriodLongEngineIdlingCount", (int) Math.round(rs.getDouble("PeriodLongEngineIdlingCount")/machineCount));
					respObj.put("PeriodPowerBoostTime", decformat.format(rs.getDouble("PeriodPowerBoostTime")/machineCount));
					respObj.put("PeriodHammerAbuseCount", (int) Math.round(rs.getDouble("PeriodHammerAbuseCount")/machineCount));
					respObj.put("PeriodGear1FwdUtilization", decformat.format(rs.getDouble("PeriodGear1FwdUtilization")/machineCount));
					respObj.put("PeriodGear2FwdUtilization", decformat.format(rs.getDouble("PeriodGear2FwdUtilization")/machineCount));
					respObj.put("PeriodGear3FwdUtilization", decformat.format(rs.getDouble("PeriodGear3FwdUtilization")/machineCount));
					respObj.put("PeriodGear4FwdUtilization", decformat.format(rs.getDouble("PeriodGear4FwdUtilization")/machineCount));
					respObj.put("PeriodGear5FwdUtilization", decformat.format(rs.getDouble("PeriodGear5FwdUtilization")/machineCount));
					respObj.put("PeriodGear6FwdUtilization", decformat.format(rs.getDouble("PeriodGear6FwdUtilization")/machineCount));
					respObj.put("PeriodGear1BkwdUtilization", decformat.format(rs.getDouble("PeriodGear1BkwdUtilization")/machineCount));
					respObj.put("PeriodGear2BkwdUtilization", decformat.format(rs.getDouble("PeriodGear2BkwdUtilization")/machineCount));
					respObj.put("PeriodGear3BkwdUtilization", decformat.format(rs.getDouble("PeriodGear3BkwdUtilization")/machineCount));
					respObj.put("PeriodGear4BkwdUtilization", decformat.format(rs.getDouble("PeriodGear4BkwdUtilization")/machineCount));
					respObj.put("PeriodGear5BkwdUtilization", decformat.format(rs.getDouble("PeriodGear5BkwdUtilization")/machineCount));
					respObj.put("PeriodGear6BkwdUtilization", decformat.format(rs.getDouble("PeriodGear6BkwdUtilization")/machineCount));
					respObj.put("PeriodGear1LockupUtilization", decformat.format(rs.getDouble("PeriodGear1LockupUtilization")/machineCount));
					respObj.put("PeriodGear2LockupUtilization", decformat.format(rs.getDouble("PeriodGear2LockupUtilization")/machineCount));
					respObj.put("PeriodGear3LockupUtilization", decformat.format(rs.getDouble("PeriodGear3LockupUtilization")/machineCount));
					respObj.put("PeriodGear4LockupUtilization", decformat.format(rs.getDouble("PeriodGear4LockupUtilization")/machineCount));
					respObj.put("PeriodGear5LockupUtilization", decformat.format(rs.getDouble("PeriodGear5LockupUtilization")/machineCount));
					respObj.put("PeriodGear6LockupUtilization", decformat.format(rs.getDouble("PeriodGear6LockupUtilization")/machineCount));
					respObj.put("PeriodAverageFuelConsumption", decformat.format(rs.getDouble("PeriodAverageFuelConsumption")/machineCount));

					respObj.put("PowerBandLowHrs", decformat.format(rs.getDouble("PowerBandLowHrs")/machineCount));
					respObj.put("PowerBandMedHrs", decformat.format(rs.getDouble("PowerBandMedHrs")/machineCount));
					respObj.put("PowerBandHighHrs", decformat.format(rs.getDouble("PowerBandHighHrs")/machineCount));
					respObj.put("PeriodFuelUsedInLPBLtrs", decformat.format(rs.getDouble("PeriodFuelUsedInLPBLtrs")/machineCount));
					respObj.put("PeriodFuelUsedInMPBLtrs", decformat.format(rs.getDouble("PeriodFuelUsedInMPBLtrs")/machineCount));
					respObj.put("PeriodFuelUsedInHPBLtrs", decformat.format(rs.getDouble("PeriodFuelUsedInHPBLtrs")/machineCount));
					respObj.put("PeriodLBandHrs", decformat.format(rs.getDouble("PeriodLBandHrs")/machineCount));
					respObj.put("PeriodGBandHrs", decformat.format(rs.getDouble("PeriodGBandHrs")/machineCount));
					respObj.put("PeriodHBandHrs", decformat.format(rs.getDouble("PeriodHBandHrs")/machineCount));
					respObj.put("PeriodHPlusBandHrs", decformat.format(rs.getDouble("PeriodHPlusBandHrs")/machineCount));

					respObj.put("PeriodNo_AutoIdle_Events", (int) Math.round(rs.getDouble("PeriodNo_AutoIdle_Events")/machineCount));
					respObj.put("PeriodNo_AutoOFF_Events", (int) Math.round(rs.getDouble("PeriodNo_AutoOFF_Events")/machineCount));
					
					//DF20180503 @Roopa adding new fields with default values
					respObj.put("PowerBandIdle", 0.0);
					respObj.put("PowerBandIdlePerct", 0.0);
					respObj.put("PeriodFuelLoss", 0.0);
					respObj.put("PeriodFuelLossPerct", 0.0);

				}

			}

		} catch (SQLException e) {
			fLogger.fatal("ExcavatorDashboardRESTService:getExcavatorChartsAndPeriodData: Exception Caught:"+e.getMessage());
		}
		catch(Exception e){
			fLogger.fatal("ExcavatorDashboardRESTService:getExcavatorLifeData:: Exception Caught:"+e.getMessage());
		}
		finally{
			try { 
				if (rs != null)
					rs.close(); 
			} catch (Exception e) {
				fLogger.fatal("ExcavatorDashboardRESTService:getExcavatorLifeData:: Exception Caught:"+e.getMessage());
			}

			if(statement!=null){
				try {
					statement.close();
				} catch (SQLException e) {
					fLogger.fatal("ExcavatorDashboardRESTService:getExcavatorLifeData:: Exception Caught:"+e.getMessage());
				}
			}
			if(prodConnection != null){
				try {
					prodConnection.close();
				} catch (SQLException e) {
					fLogger.fatal("ExcavatorDashboardRESTService:getExcavatorLifeData:: Exception Caught:"+e.getMessage());
				}
			}
		}


		return respObj;



	}

	public HashMap<String, Object> getExcavatorLifeData(String AssetID) {

		HashMap<String, Object> respObj = null;
		Logger bLogger = BusinessErrorLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		Connection prodConnection = null;
		Statement statement = null;
		ResultSet rs = null;

		String query =null;

		if(AssetID==null)
		{
			bLogger.error("ExcavatorDashboardRESTService:getExcavatorLifeData:Mandatory parameters are null");
			return null;
		}
		try {

			if(AssetID!=null && (AssetID.trim().length()==7 || AssetID.trim().length()==17)){
				if(AssetID.trim().length()==7)
				{
					String machineNumber = AssetID;
					AssetID = getSerialNumberMachineNumber(machineNumber);
					if(AssetID==null)
					{//invalid machine number
						bLogger.error("ExcavatorDashboardRESTService:ggetExcavatorLifeData:MachineNum"+ machineNumber + "does not exist !!!");
						return null ;
					}
				}
				//if(AssetID.trim().length()==17){
				query = "select aa.*,bb.* from (select b.AssetID,b.TxnDate," +
						"CONVERT(ifnull((b.FinishHammerUseTime),'0.0'),decimal(10,1)) as LifeHammerUseTime," +
						"CONVERT(ifnull((b.FinishFuelInLtrs),'0.0'),decimal(10,1)) as LifeTotalFuel," +
						"CONVERT(ifnull((b.AvgFuelConsumption),'0.0'),decimal(10,1)) as LifeAvgFuelConsumption," +
						"ifnull(b.Column19,0) as LifeHighRPMShutdownCount,ifnull(b.Column18,0) as LifeLongEngineIdlingCount," +
						"ifnull(b.HammerAbuseCount,0) as LifeHammerAbuseCount, " +
						"ifnull(b.No_AutoIdle_Events,0) as LifeNo_AutoIdle_Events,ifnull(b.No_AutoOFF_Events,0) as LifeNo_AutoOFF_Events "+
						"from factInsight_dayAgg_extended b where b.AssetID='"+AssetID+"' and b.FinishFuelInLtrs is not null " +
						"order by b.TxnDate desc limit 1) bb inner join"+
						" (select a.AssetID,a.TxnDate,CONVERT(ifnull(a.FinishEngRunHrs,'0.0'),decimal(10,1)) as LifeTotalMachineHrs " +
						"from factInsight_dayAgg a where a.AssetID='"+AssetID+"' order by a.TxnDate desc limit 1) aa ON aa.AssetID=bb.AssetID ";
				//	}
				/*else if(AssetID.trim().length()==7){

					 query = "select aa.*,bb.* from (select b.AssetID,b.TxnDate," +
							"CONVERT(ifnull((b.FinishHammerUseTime),'0.0'),decimal(10,1)) as LifeHammerUseTime," +
							"CONVERT(ifnull((b.FinishFuelInLtrs),'0.0'),decimal(10,1)) as LifeTotalFuel," +
							"CONVERT(ifnull((b.AvgFuelConsumption),'0.0'),decimal(10,1)) as LifeAvgFuelConsumption," +
							"ifnull(b.Column19,0) as LifeHighRPMShutdownCount,ifnull(b.Column18,0) as LifeLongEngineIdlingCount," +
							"ifnull(b.HammerAbuseCount,0) as LifeHammerAbuseCount, " +
							"ifnull(b.No_AutoIdle_Events,0) as LifeNo_AutoIdle_Events,ifnull(b.No_AutoOFF_Events,0) as LifeNo_AutoOFF_Events "+
							"from factInsight_dayAgg_extended b where b.AssetID like '%"+AssetID+"' and b.FinishFuelInLtrs is not null " +
							"order by b.TxnDate desc limit 1) bb inner join"+
							" (select a.AssetID,a.TxnDate,CONVERT(ifnull(a.FinishEngRunHrs,'0.0'),decimal(10,1)) as LifeTotalMachineHrs " +
							"from factInsight_dayAgg a where a.AssetID like '%"+AssetID+"' order by a.TxnDate desc limit 1) aa ON aa.AssetID=bb.AssetID ";

				}
				else{

					bLogger.error("ExcavatorDashboardRESTService:getExcavatorLifeData:Invalid Serial Number");
					return null;

				}*/

			}

			iLogger.info("ExcavatorDashboardRESTService:getExcavatorLifeData:Select Query::"+query);

			ConnectMySQL connMySql = new ConnectMySQL();
			prodConnection = connMySql.getProdDb2Connection();
			statement = prodConnection.createStatement();
			rs = statement.executeQuery(query);

			while(rs.next()){
				respObj = new HashMap<String, Object>();
				respObj.put("AssetID",rs.getString("AssetID"));
				respObj.put("LifeHammerUseTime", rs.getDouble("LifeHammerUseTime"));
				respObj.put("LifeTotalFuel", rs.getDouble("LifeTotalFuel"));
				respObj.put("LifeAvgFuelConsumption", rs.getDouble("LifeAvgFuelConsumption"));
				respObj.put("LifeHighRPMShutdownCount", rs.getInt("LifeHighRPMShutdownCount"));
				respObj.put("LifeLongEngineIdlingCount", rs.getInt("LifeLongEngineIdlingCount"));
				respObj.put("LifeHammerAbuseCount", rs.getInt("LifeHammerAbuseCount"));
				respObj.put("LifeTotalMachineHrs", rs.getDouble("LifeTotalMachineHrs"));
				respObj.put("LifeNo_AutoIdle_Events", rs.getDouble("LifeNo_AutoIdle_Events"));
				respObj.put("LifeNo_AutoOFF_Events", rs.getDouble("LifeNo_AutoOFF_Events"));

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fLogger.fatal("ExcavatorDashboardRESTService:getExcavatorLifeData:: SQL Exception Caught:"+e.getMessage());
		}
		catch(Exception e){
			fLogger.fatal("ExcavatorDashboardRESTService:getExcavatorLifeData:: Exception Caught:"+e.getMessage());
		}
		finally{
			try { 
				if (rs != null)
					rs.close(); 
			} catch (Exception e) {
				fLogger.fatal("ExcavatorDashboardRESTService:getExcavatorLifeData:: Exception Caught:"+e.getMessage());
			}

			if(statement!=null){
				try {
					statement.close();
				} catch (SQLException e) {
					fLogger.fatal("ExcavatorDashboardRESTService:getExcavatorLifeData:: Exception Caught:"+e.getMessage());
				}
			}
			if(prodConnection != null){
				try {
					prodConnection.close();
				} catch (SQLException e) {
					fLogger.fatal("ExcavatorDashboardRESTService:getExcavatorLifeData:: Exception Caught:"+e.getMessage());
				}
			}
		}



		return respObj;



	}

	public List<HashMap<String, Object>> getExcavatorDayWiseData(
			String AssetID, String startDate, String endDate, String MsgID,String model, String region, String zone, String dealer, String customer) {

		List<HashMap<String, Object>> respList = new ArrayList<HashMap<String,Object>>();
		Logger bLogger = BusinessErrorLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		Connection prodConnection = null;
		Statement statement = null;
		ResultSet rs = null;

		DateFormat dateStr = new SimpleDateFormat("yyyy-MM-dd");

		Date start=null;
		Date tempDate=null;
		String tempStringDate=null;

		String query=null;
		String selectQuery=null;

		String leftQuery=null;
		String rightQuery=null;
		String joinQuery=null;

		String fromQuery=null;

		String leftGroupQuery=null;
		String rightGroupQuery=null;


		DecimalFormat decformat=new DecimalFormat("0.0");

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
			bLogger.error("ExcavatorDashboardRESTService:getExcavatorDayWiseData:Mandatory parameters are null");
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

			if(AssetID!=null && (AssetID.trim().length()==7 || AssetID.trim().length()==17)){

				if(AssetID.trim().length()==7)
				{
					String machineNumber = AssetID;
					AssetID = getSerialNumberMachineNumber(machineNumber);
					if(AssetID==null)
					{//invalid machine number
						bLogger.error("ExcavatorDashboardRESTService:getExcavatorDayWiseData:MachineNum"+ machineNumber + "does not exist !!!");
						return null ;
					}
				}

				query = "select aa.*,bb.*,bb.DateTotalFuel/aa.DateTotalMachineHrs as DateAverageFuelConsumption " +
						"from (select b.AssetID,b.TxnDate,CONVERT(ifnull((b.FinishHammerUseTime-b.StartHammerUseTime),'0.0'),decimal(10,1)) as DateHammerUseTime,"+
						"CONVERT(ifnull((b.FinishFuelInLtrs-b.StartFuelInLtrs),'0.0'),decimal(10,1)) as DateTotalFuel,CONVERT(ifnull((b.FuelLoss),'0.0'),decimal(10,1)) as DateFuelLoss,CONVERT(ifnull((b.CarbonEmission),'0.0'),decimal(10,1)) as DateCarbonEmission,b.EngineOnCount as DateEngineOnCount,b.EngineOffCount as DateEngineOffCount,"+
						"ifnull(b.Column19-b.Column37,0) as DateHighRPMShutdownCount,"+
						"ifnull(b.Column18-b.Column38,0) as DateLongEngineIdlingCount,"+
						"CONVERT(ifnull((b.PowerBoostTime),'0.0'),decimal(10,1)) as DatePowerBoostTime,"+
						"ifnull(b.HammerAbuseCount-b.Column39,0) as DateHammerAbuseCount, " +
						"ifnull((b.No_AutoIdle_Events-b.Column35),0) as DayNo_AutoIdle_Events,"+
						"ifnull((b.No_AutoOFF_Events-b.Column36),0) as DayNo_AutoOFF_Events "+
						"from factInsight_dayAgg_extended b where b.AssetID='"+AssetID+"' and b.TxnDate between '"+startDate+"' and '"+endDate+"' " +
						"group by b.TxnDate) bb inner join"+
						" (select a.AssetID,a.TxnDate," +
						//"CONVERT(ifnull(a.PeriodHMR,'0.0'),decimal(10,1)) as DateTotalMachineHrs " + //Df20180312 @Roopa taking running band data for period CMH calc
						"CONVERT(ifnull(a.EngineOnTime,'0.0') , decimal(10,1)) as DateTotalMachineHrs "+

						"from factInsight_dayAgg a where a.AssetID='"+AssetID+"' and a.TxnDate between '"+startDate+"' and '"+endDate+"' " +
						"group by a.TxnDate) aa ON aa.AssetID=bb.AssetID and aa.TxnDate=bb.TxnDate";

			}
			else{

				selectQuery="select aa.*,bb.*,bb.DateTotalFuel/aa.DateTotalMachineHrs as DateAverageFuelConsumption ";
				fromQuery="from ";
				leftQuery="select count(distinct AssetID) as machineCount," +
						"b.Column21 as MsgID,b.Column5 as ModelCode,b.AssetID,b.TxnDate," +
						"CONVERT(ifnull(sum(b.FinishHammerUseTime-b.StartHammerUseTime),'0.0'),decimal(10,1)) as DateHammerUseTime,"+
						"CONVERT(ifnull(sum(b.FinishFuelInLtrs-b.StartFuelInLtrs),'0.0'),decimal(10,1)) as DateTotalFuel," +
						"CONVERT(ifnull(sum(b.FuelLoss),'0.0'),decimal(10,1)) as DateFuelLoss," +
						"CONVERT(ifnull(sum(b.CarbonEmission),'0.0'),decimal(10,1)) as DateCarbonEmission," +
						"ifnull(sum(b.EngineOnCount),0) as DateEngineOnCount,ifnull(sum(b.EngineOffCount),0) as DateEngineOffCount,"+
						"ifnull(sum(b.Column19-b.Column37),0) as DateHighRPMShutdownCount,"+
						"ifnull(sum(b.Column18-b.Column38),0) as DateLongEngineIdlingCount,"+
						"CONVERT(ifnull(sum(b.PowerBoostTime),'0.0'),decimal(10,1)) as DatePowerBoostTime,"+
						"ifnull(sum(b.HammerAbuseCount-b.Column39),0) as DateHammerAbuseCount,"+
						"ifnull(sum(b.No_AutoIdle_Events-b.Column35),0) as DayNo_AutoIdle_Events,"+
						"ifnull(sum(b.No_AutoOFF_Events-b.Column36),0) as DayNo_AutoOFF_Events from factInsight_dayAgg_extended b " +
						"where b.TxnDate between '"+startDate+"' and '"+endDate+"'";

				leftQuery=leftQuery+" and b.Column21 in ("+MsgID+")";

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

				rightQuery="select count(distinct AssetID) as machineCount,a.Column4 as MsgID,a.ModelCode,a.AssetID,a.TxnDate," +
						//"CONVERT(ifnull(sum(a.PeriodHMR),'0.0'),decimal(10,1)) as DateTotalMachineHrs " + //Df20180312 @Roopa taking running band data for period CMH calc
						"CONVERT(ifnull(sum(a.EngineOnTime),'0.0') , decimal(10,1)) as DateTotalMachineHrs "+
						"from factInsight_dayAgg a " +
						"where  a.TxnDate between '"+startDate+"' and '"+endDate+"'";

				rightQuery=rightQuery+" and a.Column4 in ("+MsgID+")";

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

			iLogger.info("ExcavatorDashboardRESTService:getExcavatorDayWiseData:Select Query::"+query);

			ConnectMySQL connMySql = new ConnectMySQL();
			prodConnection = connMySql.getProdDb2Connection();
			statement = prodConnection.createStatement();
			rs = statement.executeQuery(query);

			while(rs.next()){

				HashMap<String, Object> respObj=new HashMap<String, Object>();
				respObj = new HashMap<String, Object>();

				tempDate = cal1.getTime();
				tempStringDate=dateStr.format(tempDate);

				Date currDate=dateStr.parse(rs.getString("TxnDate"));

				while(! tempDate.equals(currDate)){



					HashMap<String, Object> respObj1  = new HashMap<String, Object>();

					//respObj1.put("AssetID",AssetID);
					respObj1.put("TxnDate",tempStringDate);
					respObj1.put("machineCount", "0");
					respObj1.put("DateHammerUseTime", "0.0");
					respObj1.put("DateTotalFuel", "0.0");
					respObj1.put("DateFuelLoss", "0.0");
					respObj1.put("DateCarbonEmission", "0.0");
					respObj1.put("DateEngineOnCount", "0");
					respObj1.put("DateEngineOffCount", "0");
					respObj1.put("DateHighRPMShutdownCount", "0");
					respObj1.put("DateLongEngineIdlingCount", "0");
					respObj1.put("DatePowerBoostTime", "0.0");
					respObj1.put("DateHammerAbuseCount","0");
					respObj1.put("DateTotalMachineHrs", "0.0");
					respObj1.put("DateAverageFuelConsumption", "0.0");

					respObj1.put("DayNo_AutoIdle_Events", "0");
					respObj1.put("DayNo_AutoOFF_Events", "0");

					respList.add(respObj1);

					cal1.add(Calendar.DATE, +1);

					tempDate = cal1.getTime();
					tempStringDate=dateStr.format(tempDate);
				}

				int machineCount=0;
				if(AssetID==null){
					machineCount=rs.getInt("machineCount");
				}
				//respObj.put("AssetID",rs.getString("AssetID"));
				respObj.put("TxnDate",rs.getString("TxnDate"));
				respObj.put("machineCount", machineCount);

				if(AssetID!=null){
					respObj.put("DateHammerUseTime", rs.getDouble("DateHammerUseTime"));
					respObj.put("DateTotalFuel", rs.getDouble("DateTotalFuel"));
					respObj.put("DateFuelLoss", rs.getDouble("DateFuelLoss"));
					respObj.put("DateCarbonEmission", rs.getDouble("DateCarbonEmission"));
					respObj.put("DateEngineOnCount", rs.getInt("DateEngineOnCount"));
					respObj.put("DateEngineOffCount", rs.getInt("DateEngineOffCount"));
					respObj.put("DateHighRPMShutdownCount", rs.getInt("DateHighRPMShutdownCount"));
					respObj.put("DateLongEngineIdlingCount", rs.getInt("DateLongEngineIdlingCount"));
					respObj.put("DatePowerBoostTime", rs.getDouble("DatePowerBoostTime"));
					respObj.put("DateHammerAbuseCount", rs.getInt("DateHammerAbuseCount"));
					respObj.put("DateTotalMachineHrs", rs.getDouble("DateTotalMachineHrs"));
					respObj.put("DateAverageFuelConsumption", rs.getDouble("DateAverageFuelConsumption"));

					respObj.put("DayNo_AutoIdle_Events", rs.getInt("DayNo_AutoIdle_Events"));
					respObj.put("DayNo_AutoOFF_Events", rs.getInt("DayNo_AutoOFF_Events"));
				}
				else{

					respObj.put("DateHammerUseTime", decformat.format(rs.getDouble("DateHammerUseTime")/machineCount));
					respObj.put("DateTotalFuel", decformat.format(rs.getDouble("DateTotalFuel")/machineCount));
					respObj.put("DateFuelLoss", decformat.format(rs.getDouble("DateFuelLoss")/machineCount));
					respObj.put("DateCarbonEmission", decformat.format(rs.getDouble("DateCarbonEmission")/machineCount));
					respObj.put("DateEngineOnCount", (int) Math.round(rs.getDouble("DateEngineOnCount")/machineCount));
					respObj.put("DateEngineOffCount", (int) Math.round(rs.getDouble("DateEngineOffCount")/machineCount));
					respObj.put("DateHighRPMShutdownCount", (int) Math.round(rs.getDouble("DateHighRPMShutdownCount")/machineCount));
					respObj.put("DateLongEngineIdlingCount", (int) Math.round(rs.getDouble("DateLongEngineIdlingCount")/machineCount));
					respObj.put("DatePowerBoostTime", decformat.format(rs.getDouble("DatePowerBoostTime")/machineCount));
					respObj.put("DateHammerAbuseCount", (int) Math.round(rs.getDouble("DateHammerAbuseCount")/machineCount));
					respObj.put("DateTotalMachineHrs", decformat.format(rs.getDouble("DateTotalMachineHrs")/machineCount));
					respObj.put("DateAverageFuelConsumption", decformat.format(rs.getDouble("DateAverageFuelConsumption")/machineCount));

					respObj.put("DayNo_AutoIdle_Events", (int) Math.round(rs.getDouble("DayNo_AutoIdle_Events")/machineCount));
					respObj.put("DayNo_AutoOFF_Events", (int) Math.round(rs.getDouble("DayNo_AutoOFF_Events")/machineCount));

				}

				respList.add(respObj);

				cal1.add(Calendar.DATE, +1);

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fLogger.fatal("ExcavatorDashboardRESTService:getExcavatorDayWiseData:: SQL Exception Caught:"+e.getMessage());
		}
		catch(Exception e){
			fLogger.fatal("ExcavatorDashboardRESTService:getExcavatorDayWiseData:: Exception Caught:"+e.getMessage());
		}
		finally{
			try { 
				if (rs != null)
					rs.close(); 
			} catch (Exception e) {
				fLogger.fatal("ExcavatorDashboardRESTService:getExcavatorDayWiseData:: Exception Caught:"+e.getMessage());
			}

			if(statement!=null){
				try {
					statement.close();
				} catch (SQLException e) {
					fLogger.fatal("ExcavatorDashboardRESTService:getExcavatorDayWiseData:: Exception Caught:"+e.getMessage());
				}
			}
			if(prodConnection != null){
				try {
					prodConnection.close();
				} catch (SQLException e) {
					fLogger.fatal("ExcavatorDashboardRESTService:getExcavatorDayWiseData:: Exception Caught:"+e.getMessage());
				}
			}
		}



		return respList;



	}

	public HashMap<String, Object> getStartAndEndEngineRunHrsData(
			String AssetID, String startDate, String endDate) {

		HashMap<String, Object> respObj = null;
		Logger bLogger = BusinessErrorLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		Connection prodConnection = null;
		Statement statement = null;
		ResultSet rs = null;

		if(AssetID==null || startDate==null || endDate==null)
		{
			bLogger.error("StartAndEndEngineRunHrsRESTService:getStartAndEndEngineRunHrsData: Mandatory parameters are null");
			return null;
		}
		try {
			if(AssetID.trim().length()==7)
			{
				String machineNumber = AssetID;
				AssetID = getSerialNumberMachineNumber(machineNumber);
				if(AssetID==null)
				{//invalid machine number
					bLogger.error("ExcavatorDashboardRESTService:getStartAndEndEngineRunHrsData:MachineNum"+ machineNumber + "does not exist !!!");
					return null ;
				}
			}
			String query = "select aa.AssetID,aa.FinishEngRunHrs,bb.StartEngRunHrs from "+
					"(select a.AssetID,a.TxnDate,"+
					"convert(ifnull(a.FinishEngRunHrs,'0.0'),decimal(10,1)) as FinishEngRunHrs "+
					"from factInsight_dayAgg a where a.AssetID='"+AssetID+"' " +
					"and a.TxnDate between '"+startDate+"' and '"+endDate+"') aa inner join " +
					"(select b.AssetID,max(b.TxnDate) as TxnDate,"+
					"convert(ifnull(b.StartEngRunHrs,'0.0'),decimal(10,1)) as StartEngRunHrs " +
					"from factInsight_dayAgg b where b.AssetID='"+AssetID+"' and b.TxnDate between '"+startDate+"' and '"+endDate+"')bb " +
					"ON aa.AssetID=bb.AssetID and aa.TxnDate=bb.TxnDate;";

			iLogger.info("StartAndEndEngineRunHrsRESTService:getStartAndEndEngineRunHrsData: Select Query::"+query);

			ConnectMySQL connMySql = new ConnectMySQL();
			prodConnection = connMySql.getProdDb2Connection();
			statement = prodConnection.createStatement();
			rs = statement.executeQuery(query);

			while(rs.next()){
				respObj = new HashMap<String, Object>();
				respObj.put("AssetID",rs.getString("AssetID"));
				respObj.put("FinishEngRunHrs", rs.getDouble("FinishEngRunHrs"));
				respObj.put("StartEngRunHrs", rs.getDouble("StartEngRunHrs"));

			}

			if(respObj==null){
				respObj = new HashMap<String, Object>();
				respObj.put("AssetID",AssetID);
				respObj.put("FinishEngRunHrs", 0.0);
				respObj.put("StartEngRunHrs", 0.0);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fLogger.fatal("StartAndEndEngineRunHrsRESTService:getStartAndEndEngineRunHrsData:: SQL Exception Caught:"+e.getMessage());
		}
		catch(Exception e){
			fLogger.fatal("StartAndEndEngineRunHrsRESTService:getStartAndEndEngineRunHrsData:: Exception Caught:"+e.getMessage());
		}
		finally{
			try { 
				if (rs != null)
					rs.close(); 
			} catch (Exception e) {
				fLogger.fatal("StartAndEndEngineRunHrsRESTService:getStartAndEndEngineRunHrsData:: Exception Caught:"+e.getMessage());
			}

			if(statement!=null){
				try {
					statement.close();
				} catch (SQLException e) {
					fLogger.fatal("StartAndEndEngineRunHrsRESTService:getStartAndEndEngineRunHrsData:: Exception Caught:"+e.getMessage());
				}
			}
			if(prodConnection != null){
				try {
					prodConnection.close();
				} catch (SQLException e) {
					fLogger.fatal("StartAndEndEngineRunHrsRESTService:getStartAndEndEngineRunHrsData:: Exception Caught:"+e.getMessage());
				}
			}
		}



		return respObj;



	}
	public String getSerialNumberMachineNumber(String machineNumber){

		Logger fLogger = FatalLoggerClass.logger;
		Connection prodConnection = null;
		Statement statement = null;
		ResultSet rs = null;
		String serialNumber=null;

		try {
			if(machineNumber!=null){
				String query = "select Serial_Number from asset where Machine_Number='"+ machineNumber + "'";
				ConnectMySQL connMySql = new ConnectMySQL();
				prodConnection = connMySql.getConnection();
				statement = prodConnection.createStatement();
				rs = statement.executeQuery(query);

				while(rs.next()){

					serialNumber = rs.getString("Serial_Number");
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		finally {
			try { 
				if (rs != null)
					rs.close(); 
			} catch (Exception e) {
				fLogger.fatal("getSerialNumberMachineNumber:: Exception Caught:"+e.getMessage());
			}

			if(statement!=null){
				try {
					statement.close();
				} catch (SQLException e) {
					fLogger.fatal("getSerialNumberMachineNumber:: Exception Caught:"+e.getMessage());
				}
			}
			if(prodConnection != null){
				try {
					prodConnection.close();
				} catch (SQLException e) {
					fLogger.fatal("getSerialNumberMachineNumber:: Exception Caught:"+e.getMessage());
				}
			}
		}

		return serialNumber;		
	}

}
