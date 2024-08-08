package remote.wise.dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.logging.log4j.Logger;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.ConnectMySQL;

//
 public class CommunicationReportEnhancedServiceDAO {

	private static Logger iLogger = InfoLoggerClass.logger;
	private static Logger fLogger = FatalLoggerClass.logger;
	private static ConnectMySQL connFactory = new ConnectMySQL();
	volatile static int countOfProperAddress; 
	@SuppressWarnings("deprecation")
	public static Map<String,String> getDataFrom_AMSandOEM(int start,int end, boolean forAllVin, String vins) {
		iLogger.info("Inside getDataFrom_AMSandOEM() : start");
		long startExec=System.currentTimeMillis();
		iLogger.info("Current Thread  "+Thread.currentThread()+" for fetching records starts at " +startExec);
		String query=null;
		java.sql.Date date=new java.sql.Date(System.currentTimeMillis());
		//Timestamp currentTimestamp = new Timestamp(Long.parseLong(date));
		 if(forAllVin)
			{
					query = "select a.Serial_Number,json_extract(a.TxnData,'$.LAT'),  json_extract(a.TxnData,'$.LONG')from asset_monitoring_snapshot a, com_rep_oem_enhanced b  where a.Serial_Number=b.Serial_Number and b.adressLastUpdated<'"+date+"'" +" limit "+start+","+end;
					iLogger.info("Records considered for current exec : "+System.currentTimeMillis());
//			 query="select a.Serial_Number,json_extract(a.TxnData,'$.LAT'),  json_extract(a.TxnData,'$.LONG') from asset_monitoring_snapshot a,com_rep_oem_enhanced b  where " +
//						" a.Latest_Created_Timestamp>="+"'"+timestamp+"'"+ " and a.Serial_Number = b.Serial_Number limit "+start+","+end;
			}	
			else
			{	
				query = "select a.Serial_Number,json_extract(a.TxnData,'$.LAT'),  json_extract(a.TxnData,'$.LONG') from asset_monitoring_snapshot a,com_rep_oem_enhanced b   where a.Serial_Number in(select b.Serial_Number from com_rep_oem_enhanced " +
						"where b.Serial_Number in("+vins+")"+")";
				iLogger.info("Records considered for passed as argument by user : "+System.currentTimeMillis());
			}
			iLogger.info("getDataFrom_AMSandOEM() : "+query);
			Map<String,String> vinLatLongMap =new HashMap<String,String>();
			String serialNum=null;
			String value=null;

			try (Connection connection = connFactory.getConnection_UPTIME();
					PreparedStatement statement = connection.prepareStatement(query);
					ResultSet rs = statement.executeQuery(query);) {
				
				while (rs.next()) {
					value=null;
					if(rs.getObject("Serial_Number")!=null)
						serialNum=rs.getObject("Serial_Number").toString();
					if(rs.getObject("json_extract(a.TxnData,'$.LAT')")!=null)
						value=rs.getObject("json_extract(a.TxnData,'$.LAT')").toString().replace("\"", "");
					value+=",";
					if(rs.getObject("json_extract(a.TxnData,'$.LONG')")!=null)
						value+=rs.getObject("json_extract(a.TxnData,'$.LONG')").toString().replace("\"", "");
					
					vinLatLongMap.put(serialNum, value);
				}
				
			}catch(Exception e){
				fLogger.info("Issue at fetchDataFromDB_AMS : "+e.getMessage());
			}
			iLogger.info("total time taken by Current Thread "+Thread.currentThread()+" for fetching and processing "+vinLatLongMap.size()+" is " +(System.currentTimeMillis()-startExec));
			iLogger.info("Inside getDataFrom_AMSandOEM()  end");
			return vinLatLongMap;
	}

	
	
	
	/*public static int updateCommAddressOfMachineInComReport(Map<String,String> addressMap) {
	String updateQuery = "UPDATE com_rep_oem_enhanced "
            + "SET communicating_city = ?, "
            + "communicating_state = ?, "
            + "communicating_address = ? "
            + "WHERE Serial_Number = ?";
	iLogger.info("Inside updateCommAddressOfMachineInComReport() : start");
	long start=System.currentTimeMillis();
	iLogger.info("Current Thread "+Thread.currentThread()+" for updation records start at " +start);
	String address = null, city = null, state = null;
	int countOfProperAddress=0;
	int recordsAffected []=null;int countOfUpdatedRecords=0;
	try (Connection connection = connFactory.getConnection_UPTIME();
		 PreparedStatement pstatement = connection.prepareStatement(updateQuery);)
	{
		Set<Map.Entry<String,String>> set= addressMap.entrySet(); 
		 for (Map.Entry<String, String> entry: set) 
		 {
			 if(entry.getValue()!=null)
			 {
				String vin=entry.getKey().toString(); 
			    String[] addressArr = entry.getValue().toString().split("~");
			    if(addressArr!=null)
			    {	
			    	city = addressArr[0]; state = addressArr[1]; address = addressArr[2];
			    	//if(!city.contains("null"))
			    	pstatement.setString(1, city);
			    	//if(!state.contains("null"))
			    	pstatement.setString(2, state);
			    	if(!address.contains("null")){
			    		countOfProperAddress+=1;
			    		pstatement.setString(3, address);
			    	}
			    	pstatement.setString(4, vin);
			    	pstatement.addBatch();
			    }
			 }   
			 
		 }
		recordsAffected = pstatement.executeBatch();
	}catch(Exception e){
		fLogger.info("Issue at updateCommAddressOfMachineInComReport : "+e.getMessage());
	}
	iLogger.info("Total time taken by Current Thread "+Thread.currentThread() +" for updating "+addressMap.size()+(System.currentTimeMillis()-start));
	iLogger.info("updateCommAddressOfMachineInComReport() end updated for ");
	
	if(!addressMap.isEmpty())
		countOfUpdatedRecords=addressMap.size();
//	if(recordsAffected!=null)
//		countOfUpdatedRecords=recordsAffected.length;
	return countOfProperAddress;
}*/

	public static int getTotalRecordsPresentInDB() { 
		String query="select count(*) from asset_monitoring_snapshot a,com_rep_oem_enhanced b  where a.Serial_Number = b.Serial_Number";
		iLogger.info("getTotalRecordsPresentInDB() "+query);
		int max = 0;
		
		try (Connection connection = connFactory.getConnection_UPTIME();
				PreparedStatement statement = connection.prepareStatement(query);
				ResultSet rs = statement.executeQuery(query);) {
		
			rs.next();
			max=rs.getInt("count(*)");
			
		}catch(Exception e){
			iLogger.info("Issue at getTotalRecordsPresentInDB : "+e.getMessage());
		}
		return max;
	}
	
	public static int getCountWithNullAddressInDB() { 
		String query="select count(*) from asset_monitoring_snapshot a,com_rep_oem_enhanced b where a.Serial_Number = b.Serial_Number " +
				"and(b.communicating_address is null)";
		iLogger.info("getCountWithNullAddressInDB() "+query);
		int countOfNullAddress = 0;
		
		try (Connection connection = connFactory.getConnection_UPTIME();
				PreparedStatement statement = connection.prepareStatement(query);
				ResultSet rs = statement.executeQuery(query);) {
		
			rs.next();
			countOfNullAddress=rs.getInt("count(*)");
			
		}catch(Exception e){
			iLogger.info("Issue at getTotalRecordsPresentInDB : "+e.getMessage());
		}
		return countOfNullAddress;
	}

	public static boolean checkIfVinExists(String vins) {
		List<String> resultVinList=new ArrayList<String>();
		String[] vinArr=vins.split(",");
		List<String> vinList = Arrays.asList(vinArr);
		boolean isAllVinPresent=true;
		String query="select Serial_Number from asset_monitoring_snapshot" +
				" where Serial_Number in("+vins+")";
		iLogger.info("checkIfVinExists() "+query);
		
		try (Connection connection = connFactory.getConnection_UPTIME();
				PreparedStatement statement = connection.prepareStatement(query);
				ResultSet rs = statement.executeQuery(query);) {
		
			
			while (rs.next()) {
				 resultVinList.add(rs.getString("Serial_Number"));
			}
			
			if (resultVinList != null)
			{
				if(vinList!=null){
				if(resultVinList.size()!=vinList.size())
					isAllVinPresent=false;
				}
			}
		}catch(Exception e){
			iLogger.info("Issue with machine "+vins+" passed as argumetnt "+e.getMessage());
			e.printStackTrace();
		}
			return isAllVinPresent;
	}
	
	/*public static boolean updateTheRecordsLastUpdatedInLast12HoursToNull() {
		Date twelveHourBeforeTimeStamp=CommunicationReportEnhancedServiceImpl.getTwelveHourBeforeDate();
		Timestamp timestamp = new Timestamp(twelveHourBeforeTimeStamp.getTime());
		String query="update com_rep_oem_enhanced set communicating_address=null where Serial_Number in(select Serial_Number from asset_monitoring_snapshot"
                      +" where Latest_Created_Timestamp>="+"'"+timestamp+"'"+")";
		iLogger.info("getCountWithNullAddressInDB() "+query);
		boolean isUpdated = true;
		try (Connection connection = connFactory.getConnection_UPTIME();
				PreparedStatement statement = connection.prepareStatement(query);) {
			statement.executeUpdate(query);
		}catch(Exception e){
			isUpdated=false;
			iLogger.info("Issue at updateTheRecordsLastUpdatedInLast12HoursToNull() : "+e.getMessage());
		}
		return isUpdated;
	}*/
	
	/*public static int getRecordsFor12hourBefore() {
		Date twelveHourBeforeTimeStamp=CommunicationReportEnhancedServiceImpl.getTwelveHourBeforeDate();
		Timestamp timestamp = new Timestamp(twelveHourBeforeTimeStamp.getTime());
		iLogger.info("Records considered for twelveHourBefore exec : "+timestamp);
		String query="select count(*) from asset_monitoring_snapshot a,com_rep_oem_enhanced b  where" +
				" a.Latest_Created_Timestamp>="+"'"+timestamp+"'"+ " and a.Serial_Number = b.Serial_Number"
				+" and b.Last_Updated_Timestamp<= DATE_SUB(NOW(),INTERVAL 1 HOUR)";
		String query="";
	//	if(isProcessingFirstTime)
			query="select count(*) from asset_monitoring_snapshot a,com_rep_oem_enhanced b  where" +
					" a.Latest_Created_Timestamp>="+"'"+timestamp+"'"+ " and a.Serial_Number = b.Serial_Number";
		else
			query="select count(*) from asset_monitoring_snapshot a,com_rep_oem_enhanced b  where" +
				" a.Latest_Created_Timestamp>="+"'"+timestamp+"'"+ " and a.Serial_Number = b.Serial_Number"
				+" and (b.communicating_address is null or b.communicating_address='null' or b.communicating_address like '%?%')";
		iLogger.info("getRecordsFor12hourBefore() : "+query);
		int countOf12HourBeforeAddress = 0;
		
		try (Connection connection = connFactory.getConnection_UPTIME();
				PreparedStatement statement = connection.prepareStatement(query);
				ResultSet rs = statement.executeQuery(query);) {
		
			rs.next();
			countOf12HourBeforeAddress=rs.getInt("count(*)");
			
		}catch(Exception e){
			iLogger.info("Issue at getTotalRecordsPresentInDB : "+e.getMessage());
		}
		return countOf12HourBeforeAddress;
	}*/
	
	public static int updateCommAddressOfMachineInComReport(Map<String,String> addressMap) {
		String updateQuery=null;
		iLogger.info("Inside updateCommAddressOfMachineInComReport() : start");
		iLogger.info("addressMap : "+addressMap);
		long start=System.currentTimeMillis();
		iLogger.info("Current Thread "+Thread.currentThread()+" for updation records start at " +start);
		String address = null, city = null, state = null;
		//String validAddressStringSet = "[^A-Za-z0-9\\s\\,\\~\\`\\!\\@\\#\\$\\%\\^\\&\\*\\(\\)\\-\\_\\+\\=\\{\\}\\[\\]\\:\\;\"\'\\<\\>\\/\\\\\\|\\,\\.]";
		
		int recordsAffected []=null;int countOfUpdatedRecords=0;
		try (Connection connection = connFactory.getConnection_UPTIME();
			 Statement statement = connection.createStatement();)
		{
			Set<Map.Entry<String,String>> set= addressMap.entrySet(); 
			//countOfProperAddress=0;
			 for (Map.Entry<String, String> entry: set) 
			 {
				 if(entry.getValue()!=null)
				 {
					String vin=entry.getKey().toString(); 
				    String[] addressArr = entry.getValue().toString().split("~");
				    if(addressArr!=null)
				    {	
				    	city = addressArr[0];
				    	city=city.replaceAll("[^A-Za-z0-9, ]+", "");
				    	state = addressArr[1]; 
				    	state=state.replaceAll("[^A-Za-z0-9, ]+", "");
				    	address = addressArr[2];
				    	address=address.replaceAll("[^A-Za-z0-9, ]+", "");
				    	if((city.contains("null")) && (state.contains("null"))  && (!address.contains("null"))){
				    		city="Data Not Received";
				    		state="Data Not Received";
				    	}
				    	if((city.contains("null")) && (!state.contains("null"))  && (!address.contains("null")))
				    		city="Data Not Received";
				    	if((!city.contains("null")) && (state.contains("null"))  && (!address.contains("null")))
				    		state="Data Not Received";
				    	
				    	java.sql.Date date=new java.sql.Date(System.currentTimeMillis());
				    	if((!address.contains("null"))){
				    			 address=address.replaceAll("[^A-Za-z0-9, ]+", "");
				    			 if(address!=null && address.trim().isEmpty())
			                    		address="Data Not Received";
				    		updateQuery="update com_rep_oem_enhanced set communicating_city='"+city+"'"+",communicating_state='"+state+"'"+"" +
			                		",communicating_address='"+address+"'" +",adressLastUpdated="+"'"+date+"'"+" where Serial_Number='"+vin+"'";
				    		iLogger.info("updateQuery = "+updateQuery);
				    		int countOfUpdate=0;
				    		try{
				    		countOfUpdate=statement.executeUpdate(updateQuery);
				    		countOfUpdatedRecords+=countOfUpdate;
				    		}catch(SQLException e){
				    			iLogger.info("Issue at updateCommAddressOfMachineInComReport : "+e.getMessage());
				    		}
				    	}
				    		
				    }
				 }   
				 
			 }
		}catch(Exception e){
			iLogger.info("Issue at updateCommAddressOfMachineInComReport : "+e.getMessage());
		}
		iLogger.info("Total time taken by Current Thread "+Thread.currentThread() +" for updating "+addressMap.size()+" is : "+(System.currentTimeMillis()-start));
		iLogger.info("updateCommAddressOfMachineInComReport() end updated for ");
		
		/*if(!addressMap.isEmpty())
			countOfUpdatedRecords=addressMap.size();*/
//		if(recordsAffected!=null)
//			countOfUpdatedRecords=recordsAffected.length;
		return countOfUpdatedRecords;
	}
	
	public  Integer updateAddress() throws IOException {
		 String updateQuery=null;
		 String resourceFolderPath = "";
			Properties prop = new Properties();
			prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
			resourceFolderPath = prop.getProperty("CommunicationReportsDir_PROD");
			String pattern = "*.csv";

			FileFilter fileFilter = new WildcardFileFilter(pattern);
			File resourceFolder = new File(resourceFolderPath);
			File[] processFiles = resourceFolder.listFiles(fileFilter);
		 
		 int countOfUpdatedRecords=0;
		 long start=System.currentTimeMillis();
		 String city=null,state=null,address=null,vin=null;
		 try (Connection connection = connFactory.getConnection_UPTIME();
				Statement statement = connection.createStatement();
				BufferedReader lineReader = new BufferedReader(new FileReader(processFiles[0]));)
		{
           String lineText = null;
           //lineReader.readLine();// skip header line
           String[] columnCount=lineReader.readLine().split(",",-1); //skipping header  line for taking count of columns
           
           
           while ((lineText = lineReader.readLine()) != null) {
           	lineText=lineText.replace("\"", "");
           	iLogger.info(lineText);
           	//String validAddressStringSet = "[^A-Za-z0-9\\s\\,\\~\\`\\!\\@\\#\\$\\%\\^\\&\\*\\(\\)\\-\\_\\+\\=\\{\\}\\[\\]\\:\\;\"\'\\<\\>\\/\\\\\\|\\,\\.]";
               String[] data = lineText.split(",",-1);
               if(data.length>columnCount.length){
               int addressWordCount=data.length-columnCount.length;
                
                address = "";
              // addressWordCount+=7;
               StringBuilder sb = new StringBuilder();
               for(int i=0;i<=addressWordCount;i++)
               	sb.append(data[i]).append(",");
                  // address += data[i]+",";
               
                address = sb.deleteCharAt(sb.length() - 1).toString();
                address=address.replaceAll("[^A-Za-z0-9, ]+", "");
                if(address!=null && address.trim().isEmpty())
            		address="Data Not Received";
                vin = data[addressWordCount+1];
                city = data[addressWordCount+4];
                city=city.replaceAll("[^A-Za-z0-9, ]+", "");
                if(city!=null && city.trim().isEmpty())
                	city="Data Not Received";
                state = data[addressWordCount+13];
                state=state.replaceAll("[^A-Za-z0-9, ]+", "");
                if(state!=null && state.trim().isEmpty())
                	state="Data Not Received";
                
               }
               if(data.length<=columnCount.length)
               {
                    address = data[0];
                    	address=address.replaceAll("[^A-Za-z0-9, ]+", "");
                    	if(address!=null && address.trim().isEmpty())
                    		address="Data Not Received"; 	
                    	vin=data[1];
                    city = data[4];
                    city=city.replaceAll("[^A-Za-z0-9, ]+", "");
                    if(city!=null && city.trim().isEmpty())
                    	city="Data Not Received";
                    state = data[13];
                    state=state.replaceAll("[^A-Za-z0-9, ]+", "");
                    if(state!=null && state.trim().isEmpty())
                    	state="Data Not Received";
               }
               java.sql.Date date=new java.sql.Date(System.currentTimeMillis());
               updateQuery="update com_rep_oem_enhanced set communicating_city='"+city+"'"+",communicating_state='"+state+"'"+"" +
               		",communicating_address='"+address+"'" +",adressLastUpdated="+"'"+date+"'"+" where Serial_Number='"+vin+"'";
               int count = 0;
               try{
              count=  statement.executeUpdate(updateQuery);
               }catch(SQLException e){
               	iLogger.info("Issue at updateCommAddressOfMachineInComReport for vin : "+vin+e.getMessage());
               	iLogger.info("Issue in line : "+lineText+e.getMessage());
               	e.printStackTrace();
               }
             iLogger.info(updateQuery);
             countOfUpdatedRecords+=count;  
             if(countOfUpdatedRecords%20==0)
           	  Thread.sleep(20);
           }

		}catch(Exception e){
			e.printStackTrace();
			iLogger.info("Issue at updateAddress() for vin : "+vin+e.getMessage());
		}
		iLogger.info("Total time taken : "+(System.currentTimeMillis()-start)+" for updating "+countOfUpdatedRecords);
		return countOfUpdatedRecords;
	}
	
	/*public static void main(String[] args) {
		//CommunicationReportEnhancedServiceDAO.getDataFrom_AMSandOEM(1, 100, true, null);
//		String validAddressStringSet = "[^A-Za-z0-9\\s\\,\\~\\`\\!\\@\\#\\$\\%\\^\\&\\*\\(\\)\\-\\_\\+\\=\\{\\}\\[\\]\\:\\;\"\'\\<\\>\\/\\\\\\|\\,\\.]";
		String address="B4, Doctor's Quarters, Urban Hospital, \"Raliyati $#@!~&*()-;':,.?><Rd, Lakshmi Nagar, Dahod, Gujarat 389151, ";
//		String dataa=address.replaceAll("[~!@#$%^&*()-=_+{}}|\\;':\"><?]+", "");
		String dataa=address.replaceAll("[^A-Za-z0-9, ]+", "");
		System.out.println(dataa);
		
		
		
	}*/
	
}
 


 
 