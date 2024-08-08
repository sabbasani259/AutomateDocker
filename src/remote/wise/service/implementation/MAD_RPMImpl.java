/**
 * CR337 : 20220721 : Dhiraj K : Property file read.
 */
package remote.wise.service.implementation;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.CommonUtil;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.DateUtil;
import remote.wise.util.ListToStringConversion;
import remote.wise.util.StaticProperties;

/**
 * @author ROOPN5
 *
 */
public class MAD_RPMImpl {
	
	public List<HashMap<String, Object>> getRPMDetails(String loginID, List tenancyIdList,List profileCodeList,String startDate,String endDate,String accountType, String detailedView){

		HashMap<String, Object> respObj = null;
		List<HashMap<String, Object>> responseList=new ArrayList<HashMap<String,Object>>();

		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		Connection prodConnection = null;
		Statement statement = null;
		ResultSet rs = null;
		String accountCodeListAsString=null;
		String profileCodelistAsString=null;

		String selectQuery=null;
		String fromQuery=null;
		String whereQuery=null;
		String groupByQuery=null;
		String finalQuery=null;
		List<String> AccountCodeList = new ArrayList<String>();
		String MDAaccountIdListAsString=null;
		String MDAassetGroupCodeList=null;
		String MDAoutput=null;
		String MDAresult=null;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date date1 = null,date2=null,forMDA=null;
		try {
			date1 = format.parse(startDate);
			 date2 = format.parse(endDate);
			 forMDA=format.parse("2018-03-01");
		} catch (ParseException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		//Df20180129 @Roopa Multiple BP code changes--Taking the list of users that are mapped to given account code, based on Mapping code
		iLogger.info("MAD_RPMImpl input : loginid= "+loginID+" tenancyIdList "+tenancyIdList+" profileCodeList "+profileCodeList+" startDate "+startDate+" endDate "+endDate+" accountType "+accountType+" detailedView "+detailedView);

		try{
			if(tenancyIdList!=null){

				accountCodeListAsString=new DateUtil().getAccountCodeListForTheTenancy(tenancyIdList);
				AccountCodeList.add(accountCodeListAsString.replace("\'", "").split(",")[0]);
				MDAaccountIdListAsString=accountCodeListAsString.replace("\'", "");
			}
			iLogger.info("MAD_RPMImpl : AccountCodeList "+AccountCodeList+" MDAaccountIdListAsString "+MDAaccountIdListAsString);

			if(profileCodeList!=null){
				profileCodelistAsString=new ListToStringConversion().getStringList(profileCodeList).toString();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("MADashBoardDetailsRESTService:getRPMDetails: "+"Error in converting the list to String :"+e.getMessage());
			return responseList;
		}


		Properties props = new Properties();

		Properties deployEnvProp=null;
		try
		{
			deployEnvProp= StaticProperties.getConfProperty();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("MADashBoardDetailsRESTService:getRPMDetails: "+"Error in intializing property File :"+e.getMessage());
			return responseList;
		}
		String ReportFetchDB = deployEnvProp.getProperty("ReportFetchDB");

		/*if(ReportFetchDB!=null && ReportFetchDB.equalsIgnoreCase("NDB"))*/
		if(date1.before(forMDA) && date2.before(forMDA)){

			try{

				if(detailedView==null || detailedView.equalsIgnoreCase("false")){
				selectQuery= "select CONVERT(ifnull(sum(a.PowerBandLow)/(sum(a.PowerBandLow)+sum(a.PowerBandMed)+sum(a.PowerBandHigh))*100,'0.0') , decimal(10,1)) as PowerBandLowPerc," +
						"CONVERT(ifnull(sum(a.PowerBandMed)/(sum(a.PowerBandLow)+sum(a.PowerBandMed)+sum(a.PowerBandHigh))*100,'0.0') , decimal(10,1)) as PowerBandMedPerc," +
						"CONVERT(ifnull(sum(a.PowerBandHigh)/(sum(a.PowerBandLow)+sum(a.PowerBandMed)+sum(a.PowerBandHigh))*100,'0.0') , decimal(10,1)) as PowerBandHighPerc," +
						"a.ProfileName ";
				}
				else{

					selectQuery= "select CONVERT(ifnull(sum(a.PowerBandLow)/(sum(a.PowerBandLow)+sum(a.PowerBandMed)+sum(a.PowerBandHigh))*100,'0.0') , decimal(10,1)) as PowerBandLowPerc," +
							"CONVERT(ifnull(sum(a.PowerBandMed)/(sum(a.PowerBandLow)+sum(a.PowerBandMed)+sum(a.PowerBandHigh))*100,'0.0') , decimal(10,1)) as PowerBandMedPerc," +
							"CONVERT(ifnull(sum(a.PowerBandHigh)/(sum(a.PowerBandLow)+sum(a.PowerBandMed)+sum(a.PowerBandHigh))*100,'0.0') , decimal(10,1)) as PowerBandHighPerc," +
							"a.AssetID as Serial_Number ";
						
				}

				fromQuery= " from factInsight_dayAgg a ";

				whereQuery=	" where a.TxnDate between '"+startDate+"' and '"+endDate+"' ";

				if(accountType!=null){
					if(accountType.equalsIgnoreCase("region")){

						whereQuery=whereQuery+" and a.Column2 in ("+accountCodeListAsString+")";

					}
					if(accountType.equalsIgnoreCase("zone")){

						whereQuery=whereQuery+" and a.ZonalCode in ("+accountCodeListAsString+")";

					}

					if(accountType.equalsIgnoreCase("dealer")){

						whereQuery=whereQuery+" and a.DealerCode in ("+accountCodeListAsString+")";

					}

					if(accountType.equalsIgnoreCase("customer")){

						whereQuery=whereQuery+" and a.CustCode in ("+accountCodeListAsString+")";

					}
				}

				if(profileCodelistAsString!=null){
					whereQuery=whereQuery+ " and a.ProfileCode in("+profileCodelistAsString+")";	
				}
				if(detailedView==null || detailedView.equalsIgnoreCase("false")){
				groupByQuery= " group by a.ProfileCode";
				}
				else{
					groupByQuery= " group by a.AssetID";	
				}

				finalQuery=selectQuery+fromQuery+whereQuery+groupByQuery;

				iLogger.info("MADashBoardDetailsRESTService:getRPMDetails: Final Query::"+finalQuery);

				ConnectMySQL connMySql = new ConnectMySQL();
				prodConnection = connMySql.getProdDb2Connection();
				statement = prodConnection.createStatement();
				rs = statement.executeQuery(finalQuery);


				if(detailedView==null || detailedView.equalsIgnoreCase("false")){
				while(rs.next()){
					respObj = new HashMap<String, Object>();
				

					respObj.put("PowerBandLowPerct", rs.getDouble("PowerBandLowPerc"));
					respObj.put("PowerBandMedPerct", rs.getDouble("PowerBandMedPerc"));
					respObj.put("PowerBandHighPerct", rs.getDouble("PowerBandHighPerc"));
					respObj.put("ProfileName", rs.getString("ProfileName"));

					responseList.add(respObj);
				}
				}
				else{

					while(rs.next()){
						respObj = new HashMap<String, Object>();
					

						respObj.put("PowerBandLowPerct", rs.getDouble("PowerBandLowPerc"));
						respObj.put("PowerBandMedPerct", rs.getDouble("PowerBandMedPerc"));
						respObj.put("PowerBandHighPerct", rs.getDouble("PowerBandHighPerc"));
						respObj.put("Serial_Number", rs.getString("Serial_Number"));

						responseList.add(respObj);
					}
						
				}

			}

			catch(Exception e){
				fLogger.fatal("MADashBoardDetailsRESTService:getRPMDetails:: Exception Caught:"+e.getMessage());
			}
			finally{
				try { 
					if (rs != null)
						rs.close(); 
				} catch (Exception e) {
					fLogger.fatal("MADashBoardDetailsRESTService:getRPMDetails:: Exception Caught:"+e.getMessage());
				}

				if(statement!=null){
					try {
						statement.close();
					} catch (SQLException e) {
						fLogger.fatal("MADashBoardDetailsRESTService:getRPMDetails:: Exception Caught:"+e.getMessage());
					}
				}
				if(prodConnection != null){
					try {
						prodConnection.close();
					} catch (SQLException e) {
						fLogger.fatal("MADashBoardDetailsRESTService:getRPMDetails:: Exception Caught:"+e.getMessage());
					}
				}
			}

		}
		else{

			//Fetch data from MONGODB
			
			iLogger.info("Fetch from MONGO");
			//MOOL DB changes
			Connection prodConn = null;
			Statement stmnt = null;
			ResultSet res = null,rt=null;
			try{
			ConnectMySQL connSql = new ConnectMySQL();
			prodConn = connSql.getConnection();
			stmnt = prodConn.createStatement();
			List<HashMap<String,Object>> MDAOutputMap = null;
			DateUtil dateUtil1 = new DateUtil();
			ListToStringConversion conversionObj = new ListToStringConversion();
			//For accountFilter filling
			if(AccountCodeList!=null && AccountCodeList.size()>0){
				String countrycodeQuery="select account_id,CountryCode from account where Account_Code="+AccountCodeList.get(0);
				//System.out.println("countrycodeQuery "+countrycodeQuery);
				iLogger.info("For MongoDB countrycodeQuery "+countrycodeQuery);
				rt=stmnt.executeQuery(countrycodeQuery);
				String countryCode=null;
				String account_id=null;
				if(rt.next())
				{
					countryCode=rt.getString("CountryCode");
					account_id=rt.getString("account_id");
					countryCode= URLEncoder.encode(countryCode, "UTF-8");
				}
					String acntTncyQuery = "select Tenancy_Type_Name from tenancy_type where Tenancy_Type_ID in (select Tenancy_Type_ID from tenancy where Tenancy_ID in (select Tenancy_ID from account_tenancy where Account_ID="
							+ account_id + "))";
					iLogger.info("For MongoDB acntTncyNameQuery "+acntTncyQuery);
			res=stmnt.executeQuery(acntTncyQuery);
			String tenancy_type_name="";
			if(res.next())
			{
				tenancy_type_name=res.getString("Tenancy_Type_Name");
			}
			String accountFilter="";
			if(tenancy_type_name!=null && !tenancy_type_name.equalsIgnoreCase(""))
			{
				if(tenancy_type_name.equalsIgnoreCase("Global"))
				{
					accountFilter=null;
					countryCode=null;

				}
				else if(tenancy_type_name.equalsIgnoreCase("Regional"))
				{
					accountFilter="RegionCode";
				}
				else if(tenancy_type_name.equalsIgnoreCase("Zonal"))
				{
					accountFilter="ZonalCode";
				}
				else if(tenancy_type_name.equalsIgnoreCase("Dealer"))
				{
					accountFilter="DealerCode";
				}
				else if(tenancy_type_name.equalsIgnoreCase("Customer"))
				{
					accountFilter="CustCode";
				}
				else
				{
					accountFilter="";
				}
				if(profileCodeList!=null){
				StringBuilder MDAassetGroupCodeListBuilder=conversionObj.getStringWithoutQuoteList(profileCodeList);
				MDAassetGroupCodeList=MDAassetGroupCodeListBuilder.toString();
				MDAassetGroupCodeList=ListToStringConversion.removeLastComma(MDAassetGroupCodeList);}
				respObj = new HashMap<String, Object>(); //MDAResponse=new AlertSummaryRespContract();
				ObjectMapper mapper = new ObjectMapper();
				//CR337.sn
				String connIP=null;
				String connPort=null;
				Properties prop = null;
				try{
					prop = CommonUtil.getDepEnvProperties();
					connIP = prop.getProperty("MDA_ServerIP");
					connPort = prop.getProperty("MDA_ServerPort");
					iLogger.info("MAD_RPMImpl:MDAIP" + connIP + " :: MDAPort" +connPort);
				}catch(Exception e){
					fLogger.fatal("MAD_RPMImpl : getRPMDetails : " +
							"Exception in getting Server Details for MDA Layer from properties file: " +e);
				}
				//CR337.en

				try{
							//String url="http://10.210.196.206:26030/MoolDAReports/MADashBoardService/getMAD_RPM?accountFilter=" //CR337.o
							String url="http://"+connIP+":"+connPort+"/MoolDAReports/MADashBoardService/getMAD_RPM?accountFilter=" //CR337.n
									+ accountFilter
									+ "&accountIDList="
									+ MDAaccountIdListAsString
									+ "&dateFilter=Date"
									+ "&value1="
									+ startDate
									+"&value2="
									+endDate
									+"&profileCodeList="
									+MDAassetGroupCodeList
									+ "&loginID="
									+ loginID
									+ "&countryCode="
									+countryCode
									+"&detailedView="
									+detailedView;
							//System.out.println("MDA MADashBoardService/getMAD_RPM  URL : "+url);
							iLogger.info("MDA MADashBoardService/getMAD_RPM URL : "+url);
							URL MDAUrl = new URL(url);
							 HttpURLConnection conn =(HttpURLConnection) MDAUrl.openConnection();
							    conn.setRequestMethod("GET"); 
							    conn.setRequestProperty("Accept", "application/json");
								  if (conn.getResponseCode() != 200  && conn.getResponseCode() != 204) {
									  iLogger.info("MDAReports report status: FAILURE for getMAD_RPM Service Report ::Response Code:"+conn.getResponseCode());
									  throw new RuntimeException("Failed : HTTP error code : " +conn.getResponseCode()); 
									  }
								  iLogger.info("MDAReports report status: SUCCESS for getMAD_RPM Service Report ::Response Code:"+conn.getResponseCode());
								  BufferedReader br = new BufferedReader(new  InputStreamReader((conn.getInputStream())));
								  
								  System.out.println("MDA MADashBoardService/getMAD_RPM Output from Server .... \n");
								  while ((MDAoutput = br.readLine()) != null) { 
									  //System.out.println("MDA AlertSummary json output "+MDAoutput); 
									  iLogger.info("MDA MADashBoardService/getMAD_RPM json output "+MDAoutput);
									  MDAresult =MDAoutput; } 
								  MDAOutputMap = new Gson().fromJson(MDAresult, new TypeToken<ArrayList<HashMap<String, Object>>>(){}.getType());
								  for(HashMap<String, Object> map: MDAOutputMap){
										respObj = new HashMap<String, Object>();
									  for(Entry<String, Object> mapEntry: map.entrySet()){
								  if(detailedView.equalsIgnoreCase("true")){
									  if (mapEntry.getKey().toString().contains("Serial_Number"))
									  {
									  respObj.put("Serial_Number", mapEntry.getValue());}
									  if (mapEntry.getKey().toString().contains("LowPowerBandPerct"))
									  {
										  respObj.put("PowerBandLowPerct", mapEntry.getValue());}
									  if (mapEntry.getKey().toString().contains("MedPowerBandPerct"))
									  {
										  respObj.put("PowerBandMedPerct", mapEntry.getValue());}
									  if (mapEntry.getKey().toString().contains("HighPowerBandPerct"))
									  {
										  respObj.put("PowerBandHighPerct", mapEntry.getValue());}
										
								  }
								  else{
									  if (mapEntry.getKey().toString().contains("ProfileName"))
									  {
									  respObj.put("ProfileName", mapEntry.getValue());
									  }
									  if (mapEntry.getKey().toString().contains("LowPowerBandPerct"))
									  {
										  respObj.put("PowerBandLowPerct", mapEntry.getValue());}
									  if (mapEntry.getKey().toString().contains("MedPowerBandPerct"))
									  {
										  respObj.put("PowerBandMedPerct", mapEntry.getValue());}
									  if (mapEntry.getKey().toString().contains("HighPowerBandPerct"))
									  {
										  respObj.put("PowerBandHighPerct", mapEntry.getValue());}

								  }
									  }	responseList.add(respObj);
									  }
								  //System.out.println("MDAResponse LLAlertSummaryService outmap : "+MDAOutputMap);
								  //iLogger.info("MDAResponse MADashBoardService/getMAD_RPM outmap : "+MDAOutputMap);
								  conn.disconnect();
				}catch(Exception e)
				{
					e.printStackTrace();
					fLogger.fatal("Error occured while connecting to Mongo DB "+e.getMessage());
				}
				
				}
			
			
			}
			else{
				
				throw new CustomFault("AccountCodeList is empty");
			}
				
		}catch(Exception e)
		{	e.printStackTrace();
			fLogger.fatal("Exception occured "+e.getMessage());
		}
			finally {
				if (res != null){
					try {
						res.close();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
				if (rt != null){
					try {
						rt.close();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
				if (stmnt != null){
					try {
						stmnt.close();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

				if (prodConn != null) {
					try {
						prodConn.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}

			}
		
		
		
		}

		return responseList;

	}

}
