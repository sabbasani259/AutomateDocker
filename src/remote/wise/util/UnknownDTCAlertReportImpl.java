package remote.wise.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;

/*
CR344 : VidyaSagarM : 20220817 : UnknownDTCAlertReportImpl checking all fields to get mysql query and then get the list of UnknownDTCAlertReportRespContract
CR344.sn
 */

public class UnknownDTCAlertReportImpl {
	/*public static void main(String args[])
	{
		int dmVersion =1;
		//String dateFilter="YesterDay"; 
		String dateFilter="CurrentWeek"; 
		
		//String dateFilter="PreviousWeek"; 
		//String dateFilter="Month"; 
		//String dateFilter="Week"; 
		//String dateFilter="Date"; 
		String value1="08"; String value2="2022";
		//String modelCodeList="Z3DX";
		String modelCodeList=null;
		String loginID="login1"; String AssetID="null";
		String profileCodeList="Backhoe";
		//String profileCodeList="profile1,profile2,profile3,profile10";
		new UnknownDTCAlertReportImpl().getAlertReportData(dmVersion, dateFilter, value1, value2, modelCodeList, loginID, AssetID, profileCodeList);
	
	}*/
	
	private static Logger fLogger = FatalLoggerClass.logger;
	private static Logger iLogger = InfoLoggerClass.logger;
	public static int startdate;
	public static int enddate;
	public StringBuffer query;
	public UnknownDTCFetchData unknownDTCFetchData=null;
	public static SimpleDateFormat formatterKey = new SimpleDateFormat("yyyyMMdd");
	
	public List<UnknownDTCAlertReportRespContract>  getAlertReportData(int dmVersion ,
			String dateFilter, String value1, String value2,
			String modelCodeList,String loginID, String AssetID, String profileCodeList){
		unknownDTCFetchData=new UnknownDTCFetchData();
		startdate=0;
		enddate=0;
		query=new StringBuffer("");
		query.append("select * from unknown_dtc_details where ");
	//	String responseList = "SUCCESS";

		List<UnknownDTCAlertReportRespContract> responseList = new LinkedList<UnknownDTCAlertReportRespContract>();

		try{
			
			if(dateFilter!=null && dateFilter.equalsIgnoreCase("null"))
				dateFilter=null;

			if(value1!=null && value1.equalsIgnoreCase("null"))
				value1=null;

			if(value2!=null && value2.equalsIgnoreCase("null"))
				value2=null;

			if(modelCodeList!=null && modelCodeList.equalsIgnoreCase("null"))
				modelCodeList=null;

			if(loginID!=null && loginID.equalsIgnoreCase("null"))
				loginID=null;

			if(AssetID!=null && AssetID.equalsIgnoreCase("null"))
				AssetID=null;
			
			if(profileCodeList!=null && profileCodeList.equalsIgnoreCase("null"))
				profileCodeList=null;
			
			if(loginID==null)
			{
				fLogger.fatal("LoginID is null so not valid");
				return responseList;
			}
			if(dateFilter==null || value1==null || value2==null)
			{
				fLogger.fatal("MDAReports:DTCAlertReportService:loginID:"+loginID+":DTCAlertReportImpl:" +
						"dateFilter is NULL");
				return responseList;
			}

			if(AssetID!=null)
			{
				query.append(" AssetID ='"+AssetID+"' and ");

			}
			if(dmVersion==1)
			{
				query.append(" dm1 <>'' and ");

			}
			else if(dmVersion==2)
			{
				query.append("  dm2 <>'' and ");

			}
			else if(dmVersion==3)
			{
				query.append("  dm3 <>'' and ");

			}
		    if(dateFilter.equalsIgnoreCase("Date")){

				SimpleDateFormat frontendformat=new SimpleDateFormat("yyyy-MM-dd");
				Date frontendstartDate=frontendformat.parse(value1);
				Date frontendEndDate=frontendformat.parse(value2);
				startdate = Integer.parseInt(formatterKey.format(frontendstartDate));
				enddate = Integer.parseInt(formatterKey.format(frontendEndDate));	
			}
			
			else if (dateFilter.equalsIgnoreCase("CurrentWeek")){

				SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
				Calendar cal = Calendar.getInstance();
				enddate=Integer.parseInt(formatter.format(cal.getTime()));
				cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
				startdate=Integer.parseInt(formatter.format(cal.getTime()));
				
			}
			else if (dateFilter.equalsIgnoreCase("PreviousWeek")){

				SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
				cal.add(Calendar.DAY_OF_YEAR, -1);
				enddate=Integer.parseInt(formatter.format(cal.getTime()));
				cal.add(Calendar.DAY_OF_YEAR, -6);
				startdate=Integer.parseInt(formatter.format(cal.getTime()));
				
			}

			else if(dateFilter.equalsIgnoreCase("Month")){
				
				Date sdvalue = new Date();
				Date edvalue = new Date();

				Calendar c = Calendar.getInstance();
							  
				Calendar currentCal = Calendar.getInstance();
				
				int year = Integer.valueOf(value2);
				int month =Integer.valueOf(value1);
				int day = 1;

				c.set(year, month-1, day);
				int numOfDaysInMonth = c.getActualMaximum(Calendar.DAY_OF_MONTH);
				sdvalue = c.getTime();
				
				
				c.add(Calendar.DAY_OF_MONTH, numOfDaysInMonth-1);
			

				if(c.getTime().after(currentCal.getTime())){
				
					edvalue = currentCal.getTime();

				}
				if(c.getTime().before(currentCal.getTime())){
				    c.set(Calendar.HOUR_OF_DAY, 0);

				    c.set(Calendar.MINUTE, 0);

				    c.set(Calendar.SECOND, 0);

				    c.set(Calendar.MILLISECOND, 0);
					edvalue = c.getTime();
						
				}

				if(c.getTime().equals(currentCal.getTime())){
					edvalue = c.getTime();
				}
				startdate=Integer.parseInt(formatterKey.format(sdvalue));
				enddate=Integer.parseInt(formatterKey.format(edvalue));
		
			
			}
		
		    if(startdate!=0 && enddate!=0)
		    {
				query.append(" PartitionKey >="+startdate+" and PartitionKey <="+enddate);
		    	
		    }
			if(dateFilter.equalsIgnoreCase("Yesterday"))
			{
				 final Calendar cal = Calendar.getInstance();
				    cal.add(Calendar.DATE, -1);
				    startdate=Integer.parseInt(formatterKey.format(cal.getTime()));
					query.append(" PartitionKey ="+startdate);

			}
			
			iLogger.info("the final query:"+query);
			System.out.println(query);
		}catch(Exception e){
			e.printStackTrace();
			fLogger.fatal("MDAReports:DTCAlertReportService:loginID:"+loginID+":DTCAlertReportImpl:" +
					"Exception in getting details:"+e.getMessage());

		}
		responseList=unknownDTCFetchData.getUnknownDTCReport(query,modelCodeList,profileCodeList);
		//System.out.println(responseList.toString());
		iLogger.info("the respose data:"+responseList.toString());

		return responseList;

	}

}
//CR344.en
