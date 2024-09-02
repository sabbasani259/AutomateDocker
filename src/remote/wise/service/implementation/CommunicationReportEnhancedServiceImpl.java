package remote.wise.service.implementation;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.net.ssl.SSLHandshakeException;

import net.sf.json.JSONSerializer;

import org.apache.logging.log4j.Logger;

import remote.wise.dao.CommunicationReportEnhancedServiceDAO;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.GetDataFromAMSOEM_Task;

public class CommunicationReportEnhancedServiceImpl {

	private static boolean forAllVin;
	private static String vins;
	private static Logger iLogger = InfoLoggerClass.logger;
	private static Logger fLogger = FatalLoggerClass.logger;
	volatile static int countOfAPICall;
//	volatile static int count;
	//private static int machineProcessedCount = 0;

	public static Map<String, String> getDataFromAMSOEM(int startLimit,
			int endLimit, boolean forAllVin, String vins){
		Map<String, String> vinLatLongMap = CommunicationReportEnhancedServiceDAO
				.getDataFrom_AMSandOEM(startLimit, endLimit, forAllVin, vins);
		iLogger.info("getDataFromAMSOEM() result returned "+vinLatLongMap);
		return vinLatLongMap;
	}

	public static Map<String, String> getAddressOfMachines(
			Map<String, String> vinLatLongMap) throws SSLHandshakeException, InterruptedException {
		iLogger.info("Inside getAddressOfMachines()" +vinLatLongMap);
		long start = System.currentTimeMillis();
		iLogger.info("Current Thread " + Thread.currentThread() + "  " + start);
		String address = null;
		//countOfAPICall=0;
		Map<String, String> machineAddressMap = new HashMap<String, String>();

		Set<Map.Entry<String, String>> set = vinLatLongMap.entrySet();
		for (Map.Entry<String, String> entry : set) {
			String latLongValue = entry.getValue();
			String vin = entry.getKey().toString();
			String [] arr=null;
			/*if(countOfAPICall%1==0){
				//countOfAPICall=0;
				System.out.println("Sleeping for 1 sec : "+countOfAPICall);
				Thread.sleep(1000);
			}*/
			address = callOpenStreetAPI(vin, latLongValue);
			countOfAPICall++;
			if (address != null) {
				if(!address.isEmpty()){
				  arr = address.split("\\~");
				}
				iLogger.info("address from open street API for Vin "+vin+" is : "+address);
				if (!arr[2].contains("null")){
					System.out.println(vin+":"+address);
					machineAddressMap.put(vin, address);}
				else{
					iLogger.info("address from open street API for Vin "+vin+" is : "+address);
					System.out.println("address with null "+vin);
					iLogger.info("address with null "+vin);
				}
			}
		}
		
		iLogger.info("Total time taken by Current Thread "
				+ Thread.currentThread() + " for processing "
				+ machineAddressMap.size() + " is "
				+ (System.currentTimeMillis() - start));
		iLogger.info("getAddressOfMachines() end");
		return machineAddressMap;
	}

	public static int setAddressOfMachineInComReport(
			Map<String, String> addressMap) {
		int count= CommunicationReportEnhancedServiceDAO
				.updateCommAddressOfMachineInComReport(addressMap);
		return count;

	}


	static String callOpenStreetAPI(String vin, String latlong)
			throws SSLHandshakeException {
		long start = System.currentTimeMillis();
		iLogger.info(Thread.currentThread() + " for API call started at "
				+ start);
		String address = null, city = null, state = null;
		BufferedReader br = null;
		String output;
		String out = "";
		String lat=null,lon=null;
		if(latlong!=null){
		String latLong[] = latlong.split(",");
		lat=latLong[0];
		lon=latLong[1];
		}
		
			try{
			URL url = 
					new URL("https://nominatim.openstreetmap.org/reverse?accept-language=en&format=json&lat="+lat+"&lon="+lon+"&zoom=18&addressdetails=1");
			iLogger.info(new Date() + "|calling openstreetmap API|" + url);
			//System.out.println(new Date() + "|calling openstreetmap API|" + url);
			//SSLFix.execute();
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Connection", "close");
			conn.setConnectTimeout(5000); // set timeout to 5 sec
			conn.setReadTimeout(5000);
			conn.connect();
			if (conn.getResponseCode() != 200) {
				iLogger.info("Failed : HTTP error code : "
						+ conn.getResponseCode());
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}
			// Reading data's from url
			br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream()),"UTF-8"));

			while ((output = br.readLine()) != null) {
				out += output;
			}
			if (br != null) {
				br.close();
			}
			conn.disconnect();
			// System.out.println(new Date()+"|API response|"+out);
			org.json.simple.JSONObject obj = (org.json.simple.JSONObject) org.json.simple.JSONValue
					.parse(out);

			net.sf.json.JSONObject jsonObj = (net.sf.json.JSONObject) JSONSerializer
					.toJSON(out);
			
			Map mapAddress = ((Map)jsonObj.get("address")); 
//			Map mapAddress1=((Map)obj.get("address"));
			address=(String) jsonObj.get("display_name"); 
			
			 Iterator<Map.Entry> itr1 = mapAddress.entrySet().iterator(); 
		        while (itr1.hasNext()) { 
		            Map.Entry pair = itr1.next(); 
		            if(pair.getKey().equals("state_district") || pair.getKey().equals("city"))
		            	city=(String) pair.getValue();
		            if(pair.getKey().equals("state") || pair.getKey().equals("region"))
		            	state=(String) pair.getValue();
		        } 
		        
		} catch (SSLHandshakeException e) {
			fLogger.info("Not able to call googleAPI,so existing!!,Please check and execute again "
					+ e.getMessage());
			throw new SSLHandshakeException("Not able to call googleAPI");
		} catch (Exception e) {
			fLogger.info("Issue is with GoogleAPI call " + e.getMessage());
		}
        
		iLogger.info("Total time taken by Current Thread "
				+ Thread.currentThread() + " for a single API is "
				+ (System.currentTimeMillis() - start)); 
		/*if(city==null)
			city="Bangalore";
		if(state==null)
			state="Karnataka";
		if(address==null)
			address="Wipro S2 Block EC4 Bangalore Karnataka";*/


		return city + "~" + state + "~" + address;
	}
	// This method will have executor framework which will schedule three tasks
	// fetchDataFromDB,ProcessGoogleApIOnFetchedData and updating data to
	// com_rep_oem

	/*public static Date getCurrentDate() {
		Calendar calendar = Calendar.getInstance();
		 calendar.setTime(new Date());
		 String pattern = "yyyy-MM-dd"; 
		 SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		
		//calendar.add(Calendar.HOUR_OF_DAY, -12);
		//Date date = calendar.getTime();
		String date= simpleDateFormat.format(new Date());
		long dat=Long.parseLong(date);
		return dat;
	}*/

	public static String getFormatedVinsToQuery(String[] vinList) {
		// to add comma in the list after each element
		String vins = "";
		if (vinList.length > 0) {
			StringBuilder sb = new StringBuilder();
			for (String vin : vinList)
				sb.append("'").append(vin.trim()).append("'").append(",");
			vins = sb.deleteCharAt(sb.length() - 1).toString();
		}
		return vins;
	}

	public static String updateCommAddressOfVins(boolean forAllMac,
			String machines,int machineProcessedCount) throws SSLHandshakeException {
		ExecutorService executorService = null;
		String result = "";
		String[] vinCount = null;
		int startLimit = 0;
		machineProcessedCount=0;
		if (machines != null)
			vinCount = machines.split(",");
		//CommunicationReportEnhancedServiceImpl.isProcessingFirstTime=isProcessingFirstTime;
		try {
			
			executorService = Executors.newFixedThreadPool(100);
			forAllVin = forAllMac;
			int max=0;
			
			if(forAllVin)
		{
			max = CommunicationReportEnhancedServiceDAO
					.getTotalRecordsPresentInDB();
		}
			else{
				vins = machines;
				max = vinCount.length;
				}
		System.out.println("fetching data from DB starts");	
		iLogger.info("\n========fetching data from DB starts===============================================");

			List<GetDataFromAMSOEM_Task> fetchTaskList = new ArrayList<>();
			long start = System.currentTimeMillis();
			for (startLimit = 0; startLimit < max; startLimit += 1000) {

				fetchTaskList.add(new GetDataFromAMSOEM_Task(startLimit, 1000,
						forAllMac, vins));
			}
			List<Future<Map<String, String>>> vinLatLonMapFutureList = executorService
					.invokeAll(fetchTaskList);

			iLogger.info("Total time taken  for processing "
					+ vinLatLonMapFutureList.size() + " is "
					+ (System.currentTimeMillis() - start));
			iLogger.info("\n========fetching ends===============================================================");
			System.out.println("fetching data from DB ends");
			executorService.shutdownNow();
			Thread.sleep(10000);
			//executorService = Executors.newFixedThreadPool(10);
			iLogger.info("\n========API Call starts======");
			//List<GetAddressOfMachines_Task> APITaskListList = new ArrayList<>();
			start = System.currentTimeMillis();

			List<Map<String,String>> addressMapList = new ArrayList<>();
			for (int i = 0; i < vinLatLonMapFutureList.size(); i++) {
				
				Future<Map<String, String>> future = vinLatLonMapFutureList
						.get(i);
				Map<String, String> addressMap=CommunicationReportEnhancedServiceImpl.getAddressOfMachines(future.get());
				addressMapList.add(addressMap);
			}
			/*List<Future<Map<String, String>>> fetchAddressFutureList = executorService
					.invokeAll(APITaskListList);*/
			iLogger.info("Total time taken  for processing API"
					 + " is "
					+ (System.currentTimeMillis() - start));
			iLogger.info("\n========API Call Ends======");

			iLogger.info("\n========updating DB starts======");
			List<Map<String, String>> updateDbTaskList = new ArrayList<>();
			start = System.currentTimeMillis();
			for (int i = 0; i < addressMapList.size(); i++) {
				Map<String, String> vinAddress= addressMapList.get(i);
				Integer count=CommunicationReportEnhancedServiceDAO.updateCommAddressOfMachineInComReport(vinAddress);
				machineProcessedCount+=count;
			}
			
			/*
			for(SetAddressOfMachineInComReport_Task task:updateDbTaskList){
				Future<Integer> future=executorService.submit(task);
			Integer count=	(Integer)future.get();
			machineProcessedCount+=count;
			}*/
			/*List<Future<Integer>> machineCountFutureList=executorService.invokeAll(updateDbTaskList);
			for (int i = 0; i < machineCountFutureList.size(); i++) {
				Future<Integer> future = machineCountFutureList
						.get(i);
				machineProcessedCount+=(Integer)future.get();
			
			}*/
			iLogger.info("Total time taken  for processing "
					+ updateDbTaskList.size() + " is "
					+ (System.currentTimeMillis() - start));
			iLogger.info("\n========updating DB ends======");

			result += "Success";
			if (machines != null) {
				if (!machines.isEmpty())
					machineProcessedCount = vinCount.length;
			}
		} catch (Exception e) {
			result += "Failure";
		}
		iLogger.info("updateCommAddressOfVins(): result after total operation :"
				+ result);
//		if(!nullAddressExist)
//		isProcessingFirstTime=false;
		executorService.shutdown();
		return result + ":" + machineProcessedCount+":"+countOfAPICall;
	}

	public static boolean checkIfAddressNull() {
		System.out.println("inside checkIfAddressNull");
		int count=CommunicationReportEnhancedServiceDAO.getCountWithNullAddressInDB();
		if(count>=1)
		return true;
		else 
		return false;
					
	}

	public static boolean checkIfVinExists(String vins) {
		boolean isExists=CommunicationReportEnhancedServiceDAO.checkIfVinExists(vins);
		return isExists;
	}

	public static String updateCommAddressOfVinsFromUtilReport(){
		String result = ""; 
		Integer machineProcessedCount=0;
		try {
			
			iLogger.info("\n========updating DB starts======");
			machineProcessedCount=new CommunicationReportEnhancedServiceDAO().updateAddress();
			iLogger.info("\n========updating DB ends======");
			result += "Success";
			
		} catch (Exception e) {
			result += "Failure";
		}
		iLogger.info(" result after total operation :"
				+ result);
		return result + ":" + machineProcessedCount;
	}


	/*public static void updateTheRecordsLastUpdatedInLast12HoursToNull() {
	boolean isUpdated=	CommunicationReportEnhancedServiceDAO.updateTheRecordsLastUpdatedInLast12HoursToNull();
		
	}*/
}
