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
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
//CR337 : 20220721 : Dhiraj K : Property file read.
import java.util.Properties;
import java.util.Map.Entry;

import org.hibernate.Query;
import org.hibernate.Session;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.CommonUtil;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.HibernateUtil;

import remote.wise.businessentity.AccountEntity;
import remote.wise.businessentity.FleetSummaryChartTempDataEntity;



public class FleetSummaryDetailsRESTImpl {

	@SuppressWarnings("rawtypes")
	public String getFleetSummaryServiceDetails(){

		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		
		//DF20170814 @Roopa adding OEM Global role also
		String result = "SUCCESS";
		String selectQuery = "select aos.accountId,g.timeKey,sum(g.engineOffHours),sum(g.EngineRunningBand1+g.EngineRunningBand2),sum(g.EngineRunningBand3+g.EngineRunningBand4+g.EngineRunningBand5+g.EngineRunningBand6+g.EngineRunningBand7+g.EngineRunningBand8)";
		String fromQuery = " from AssetMonitoringFactDataDayAgg_json g, AssetOwnerSnapshotEntity aos ";
		String whereQuery = " where aos.accountType in ('OEM','OEM Global') and aos.serialNumber=g.serialNumber and g.timeKey = (select max(timeKey) from AssetMonitoringFactDataDayAgg_json) group by aos.accountId";
		String finalQuery = selectQuery+fromQuery+whereQuery;

		Query query1 = session.createQuery(finalQuery);
		try{
			Iterator iterator1 = query1.list().iterator();
			Object[] resultQ = null;
			while(iterator1.hasNext()){
				resultQ = (Object[]) iterator1.next();
				AccountEntity account = (AccountEntity) resultQ[0];
				int accountId = account.getAccount_id();
				Timestamp maxTime = (Timestamp) resultQ[1];
				String timeStamp = maxTime.toString();
				Double totalOffHours = (Double)resultQ[2];
				Double totalIdleHours = (Double)resultQ[3];
				Double totalWorkingHours = (Double)resultQ[4];
				String findQ = ("from FleetSummaryChartTempDataEntity where accountId = "+accountId);
				Query query2 = session.createQuery(findQ);
				Iterator iterator2 = query2.list().iterator();
				if(iterator2.hasNext()){
					FleetSummaryChartTempDataEntity fleetDataEntity = (FleetSummaryChartTempDataEntity) iterator2.next();
					fleetDataEntity.setEngineOffHours(totalOffHours);
					fleetDataEntity.setEngineIdleHours(totalIdleHours);
					fleetDataEntity.setEngineWorkingHours(totalWorkingHours);
					fleetDataEntity.setTimeStamp(timeStamp);
					session.update(fleetDataEntity);
				}else{
					FleetSummaryChartTempDataEntity fleetDataEntity = new FleetSummaryChartTempDataEntity();
					fleetDataEntity.setAccountId(accountId);
					fleetDataEntity.setEngineOffHours(totalOffHours);
					fleetDataEntity.setEngineIdleHours(totalIdleHours);
					fleetDataEntity.setEngineWorkingHours(totalWorkingHours);
					fleetDataEntity.setTimeStamp(timeStamp);
					session.save(fleetDataEntity);
				}
				iLogger.info("fleet utiliazation temp data "+timeStamp+" total engine off hours: "+totalOffHours+" total engine off hours: "+totalIdleHours+" total engine working hours: "+totalWorkingHours);
			}

		}catch(Exception e){
			fLogger.fatal("Exception :"+e);
			result = "FAILURE";
		}finally{
			if(session.getTransaction().isActive()){
				session.getTransaction().commit();
			}if(session.isOpen()){
				session.flush();
				session.close();
			}
		}
		return result;
	}
	//DF20190201 ::Mani :: Populating the fleet_summary_chart_tempdata from MOOL
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public String getFleetSummaryServiceDetails_New()
			{
			Logger iLogger = InfoLoggerClass.logger;
			Logger fLogger = FatalLoggerClass.logger;
			String result = "SUCCESS";
			String MDAoutput=null;
			String MDAresult=null;
			HashMap<Integer,FleetSummaryChartTempDataEntity> fleetSummaryData=new HashMap<Integer, FleetSummaryChartTempDataEntity>();
			HashMap<Integer, HashMap<String, String>> accountDetails=new HashMap<Integer, HashMap<String, String>>();
			HashMap<String, String> requiredDetails=null;
			FleetSummaryChartTempDataEntity fleetDataEntity = null;
			Connection prodConn = null,prodConn1 = null;
			Statement stmnt = null,stmnt1 = null;
			ResultSet res = null,res1 = null;
			Session session=null;
			//CR337.sn
			String connIP=null;
			String connPort=null;
			Properties prop = null;
			try{
				prop = CommonUtil.getDepEnvProperties();
				connIP = prop.getProperty("MDA_ServerIP");
				connPort = prop.getProperty("MDA_ServerPort");
				iLogger.info("FleetSummaryDetailsRESTImpl : MDAIP : "+ connIP + ":MDAPort:"+connPort);
			}catch(Exception e){
				fLogger.fatal("FleetSummaryDetailsRESTImpl : getFleetSummaryServiceDetails_New : " +
						"Exception in getting Server Details for MDA Layer from properties file: " +e);
			}
			//CR337.en
			try{
				try{
					HashMap<String,Double> MDAOutputMap = null;
					String accountFilter="";
					String countryCode = null,account_Code=null;
					//Fetching yesterday's data.
					Calendar cal = Calendar.getInstance();
					cal.add(Calendar.DATE, -1);
					DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			        String yesterdayDate = dateFormat.format(cal.getTime());
			        //Todays date for timestamp
			        Calendar cal1 = Calendar.getInstance();
					DateFormat Format = new SimpleDateFormat("yyyy-MM-dd");
			        String todayDate = Format.format(cal1.getTime());
			        ConnectMySQL connSql = new ConnectMySQL();
					prodConn = connSql.getConnection();
					stmnt = prodConn.createStatement();
					//DF20190204 :: Fetching the accounts of global,regional and zonal types and publishing the temp table for fleet util chart
					String accountDetailsQuery = "select a.account_id,a.account_code,a.CountryCode,tt.tenancy_type_name" +
								" from tenancy_type tt,account a,account_tenancy at, tenancy t where tt.Tenancy_Type_ID in (0,1,2)" +
								" and tt.Tenancy_Type_ID=t.Tenancy_Type_ID and at.Tenancy_ID=t.Tenancy_ID and at.Account_ID=a.Account_ID;";
						
						res=stmnt.executeQuery(accountDetailsQuery);
						while(res.next())
						{
							requiredDetails=new HashMap<String, String>();
							int accountId=res.getInt("account_id");
							String accountCode=res.getString("account_code");
							String country_code=res.getString("CountryCode");
							String tenancyTypeName=res.getString("tenancy_type_name");
							requiredDetails.put("account_code", accountCode);
							requiredDetails.put("CountryCode", country_code);
							requiredDetails.put("tenancy_type_name", tenancyTypeName);
							accountDetails.put(accountId, requiredDetails);
						}
						//closing the connection
						if (res != null)
							try {
								res.close();
							} catch (SQLException e1) {
								e1.printStackTrace();
							}

						if (stmnt != null)
							try {
								stmnt.close();
							} catch (SQLException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}

						if (prodConn != null) {
							try {
								prodConn.close();
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
						Iterator it=accountDetails.entrySet().iterator();
						while(it.hasNext())
						{
							try
							{
							fleetDataEntity = new FleetSummaryChartTempDataEntity();	
							Map.Entry<Integer, Object> entry=(Entry<Integer, Object>) it.next();
							int accountId=entry.getKey();
							HashMap<String, String> valuesMap=(HashMap<String, String>) entry.getValue();
							String tenancy_type_name=valuesMap.get("tenancy_type_name");
							countryCode=valuesMap.get("CountryCode");
							account_Code=valuesMap.get("account_code");
							if(tenancy_type_name!=null && !tenancy_type_name.equalsIgnoreCase(""))
							{
								if(tenancy_type_name.equalsIgnoreCase("Global"))
								{
									accountFilter=null;
									countryCode = null;
								}
								else if(tenancy_type_name.equalsIgnoreCase("Regional"))
								{
									accountFilter="RegionCode";
								}
								else if(tenancy_type_name.equalsIgnoreCase("Zonal"))
								{
									accountFilter="ZonalCode";
								}
								else
								{
									accountFilter="";
								}
							}
							//String url = "http://10.210.196.206:26030/MoolDAReports/LLFleetSummaryService/getLLFleetSummary?accountFilter="//CR337.o
							String url = "http://"+connIP+":"+connPort+"/MoolDAReports/LLFleetSummaryService/getLLFleetSummary?accountFilter=" //CR337.n
									+ accountFilter
									+ "&accountIDList="
									+ account_Code
									+ "&period=Date"
									+ "&value1="
									+ yesterdayDate
									+ "&value2="
									+ yesterdayDate
									+ "&loginID="
									+ "ServiceUser"
									+ "&countryCode=";

							if(countryCode != null)
								url = url+URLEncoder.encode(countryCode,"UTF-8");
							else
								url = url+countryCode;

						iLogger.info("MDA FleetSummaryService ::Mool URL for accountid : "
								+ accountId + ": URL : " + url);
							URL MDAUrl = new URL(url);
							 HttpURLConnection conn =(HttpURLConnection) MDAUrl.openConnection();
							    conn.setRequestMethod("GET"); 
							    conn.setRequestProperty("Accept", "application/json");
								  if (conn.getResponseCode() != 200  && conn.getResponseCode() != 204) {
									  iLogger.info("MDAReports report status: FAILURE for FleetSummaryService Service Report ::Response Code:"+conn.getResponseCode());
									  throw new RuntimeException("Failed : HTTP error code : " +conn.getResponseCode()); 
									  }
								  iLogger.info("MDAReports report status: SUCCESS for FleetSummaryService Service Report ::Response Code:"+conn.getResponseCode());
								  BufferedReader br = new BufferedReader(new  InputStreamReader((conn.getInputStream())));
								  
								  //System.out.println("MDA FleetSummaryService Output from Server .... \n");
								  iLogger.info("MDA FleetSummaryService Output from Server for account id :"+accountId+" \n");
								  while ((MDAoutput = br.readLine()) != null) { 
									  //System.out.println("MDA json output "+MDAoutput); 
									  iLogger.info("MDA FleetSummaryService json output for account id : "+accountId+" :: "+MDAoutput);
									  MDAresult =MDAoutput; } 
								  try{
									  //DF20190214 :MANI: Handling mongo exception,updating m/c count*24 if error from mongo for that account id
								  if(!MDAresult.contains("Error") && !MDAresult.contains("Exception")){
								  MDAOutputMap = new Gson().fromJson(MDAresult, new TypeToken<HashMap<String, Double>>(){}.getType());
								  fleetDataEntity.setAccountId(accountId);
								  fleetDataEntity.setEngineIdleHours(MDAOutputMap.get("idletime"));
								  fleetDataEntity.setEngineOffHours(MDAOutputMap.get("EngineOffTime"));
								  fleetDataEntity.setEngineWorkingHours(MDAOutputMap.get("WorkingTime"));
								  fleetDataEntity.setTimeStamp(todayDate);
								  fleetSummaryData.put(accountId, fleetDataEntity);
								  }
								  else if(MDAresult.contains("Error"))
								  {
									  prodConn1 = connSql.getConnection();
									  stmnt1 = prodConn1.createStatement();
									  String machineCountQuery="select count(serial_number) as TotalMachineCount from asset_owner_snapshot where account_id="+accountId;
									  iLogger.info("MDA FleetSummaryService :: Query for total number of Assets for account_id "+accountId);
									  res1=stmnt1.executeQuery(machineCountQuery);
									  int totalMachineCount=0;
									  if(res1.next())
									  {
										  totalMachineCount=res1.getInt("TotalMachineCount");
									  }
									  iLogger.info("MDA FleetSummaryService :: total number of Assets for account_id "+accountId+" : "+totalMachineCount);
									  long engineOffHours=totalMachineCount*24;
									  fleetDataEntity.setAccountId(accountId);
									  fleetDataEntity.setEngineOffHours(engineOffHours);
									  fleetDataEntity.setTimeStamp(todayDate);
									  fleetSummaryData.put(accountId, fleetDataEntity);
								  }
								  else if(MDAresult.contains("Exception"))
								  {
									  result = "FAILURE";
								  }
								  else
								  {
									  continue;
								  }
								//closing the connection
									if (res1 != null)
										try {
											res1.close();
										} catch (SQLException e1) {
											e1.printStackTrace();
										}

									if (stmnt1 != null)
										try {
											stmnt1.close();
										} catch (SQLException e1) {
											// TODO Auto-generated catch block
											e1.printStackTrace();
										}

									if (prodConn1 != null) {
										try {
											prodConn1.close();
										} catch (SQLException e) {
											e.printStackTrace();
										}
									}
								  }catch(Exception e)
								  {
									  result = "FAILURE";
									  fLogger.error("Exception in record fetching : "+e,e.getMessage());
										e.printStackTrace();
								  }
								  conn.disconnect();
							
						}catch (Exception e) {
							result = "FAILURE";
							fLogger.error("Exception in record: "+e);
							e.printStackTrace();
						}
							
						}
						
			}catch (Exception e) {
				result = "FAILURE";
				fLogger.error("Exception occured: "+e);
				e.printStackTrace();
			}
			session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();
			
			Iterator it2=accountDetails.entrySet().iterator();
			while(it2.hasNext())
			{
				try
				{
				Map.Entry<Integer, Object> entry=(Entry<Integer, Object>) it2.next();
				int accountId=entry.getKey();
				if(fleetSummaryData.containsKey(accountId))
				{
				String findQ = ("from FleetSummaryChartTempDataEntity where accountId = "+fleetSummaryData.get(accountId).getAccountId());
				iLogger.info("MDA FleetSummaryService findQ: "+findQ);
				Query query2 = session.createQuery(findQ);
				Iterator iterator2 = query2.list().iterator();
				if(iterator2.hasNext()){
					fleetDataEntity = (FleetSummaryChartTempDataEntity) iterator2.next();
					fleetDataEntity.setEngineIdleHours(fleetSummaryData.get(accountId).getEngineIdleHours());
					fleetDataEntity.setEngineOffHours(fleetSummaryData.get(accountId).getEngineOffHours());
					fleetDataEntity.setEngineWorkingHours(fleetSummaryData.get(accountId).getEngineWorkingHours());
					fleetDataEntity.setTimeStamp(fleetSummaryData.get(accountId).getTimeStamp());
					session.update(fleetDataEntity);
				}
				else
				{
					if(fleetSummaryData.get(accountId)!=null)
					{
					session.save(fleetSummaryData.get(accountId));
					}
				}
				}
				}
				catch (Exception e) {
					result = "FAILURE";
					fLogger.error("Exception occured: "+e);
					e.printStackTrace();
				}
			}
			}
			catch (Exception e) {
				result = "FAILURE";
				fLogger.error("Exception: "+e);
				e.printStackTrace();
			}
			finally {
				if (res != null)
					try {
						res.close();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}

				if (stmnt != null)
					try {
						stmnt.close();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}

				if (prodConn != null) {
					try {
						prodConn.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}

				if(session.getTransaction().isActive()){
					session.getTransaction().commit();
				}if(session.isOpen()){
					session.flush();
					session.close();
				}
				if (res1 != null)
					try {
						res1.close();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}

				if (stmnt1 != null)
					try {
						stmnt1.close();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				if (prodConn1 != null) {
					try {
						prodConn1.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			
			}
			return result;
			}
}
