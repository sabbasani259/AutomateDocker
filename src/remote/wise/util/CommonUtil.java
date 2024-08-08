/*
 * ME100005771 : 20230315 : Dhiraj K : Password Decryption Issue
 * JCB6371 : 20230404 : Dhiraj K : Subscription history not getting update for Billing for Rolloff
 *CR308-20230403_prasad Configuration based approach for Weather data and New model codes addition from Wise
 * CR352 : 20230505 : Dhiraj K : Retrofitment Changes
 * JCB417: Prasanna :20230607 : Changes for updating tenancy table
 * CR419 :Santosh : 20230714 :Aemp Changes
 */
package remote.wise.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import redis.clients.jedis.Jedis;
import remote.wise.businessentity.InterfaceLogDetails;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.ExcavatorVisualizationReportImpl;

public class CommonUtil {
	//public static WiseLogger infoLogger = WiseLogger.getLogger("CommonUtil:","info");
	static Properties prop = new Properties();
	public static Properties getDepEnvProperties() {
		Logger iLogger = InfoLoggerClass.logger;
		//Logger fLogger = FatalLoggerClass.logger;
		try {
			prop.load(CommonUtil.class.getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
			//System.out.println("prop:" + prop);
		}catch (Exception e) {
			e.printStackTrace();
//			iLogger.debug("Exception loading properties file"+e.getMessage());
		}
		//System.out.println("prop:" + prop);
		return prop;
	}


	public int convertSegment_TxnDate(String segmentId, String txnTimeStamp){


		String txnDateString=segmentId+"";

		int txnDate=Integer.parseInt(txnDateString);

		return txnDate;


	}
	// Keerthi : 2017.02.16 : method to fetch the corresponding country code for the input country, to send SMS

	public String getCountryCode(String countryName){
		String countryCode = "";

		Connection prodConnection = null;
		Statement statement = null;
		ResultSet rs = null;
		String query=null;
		try{
			if(countryName != null){
				ConnectMySQL connMySql = new ConnectMySQL();
				prodConnection = connMySql.getConnection();
				statement = prodConnection.createStatement();
				query = "SELECT country_code FROM country_codes WHERE country_name LIKE '%"+countryName+"%'";
				rs = statement.executeQuery(query);

				while(rs.next()){
					countryCode = rs.getString("country_code");
				}
				if(countryCode == null){
					countryCode = "";
				}
			}
			else{
				countryCode = "";
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}	
		finally {
			if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}

			if(statement!=null)
				try {
					statement.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			if (prodConnection != null) {
				try {
					prodConnection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		}
		return countryCode;
	}

	public Map getAggregatedTxnData(List<HashMap> TxnDataList,List aggregatedData, int rowCount){

		//DF20170517 @Roopa for the parameters where aggregation logic is diff

		/* eg::Fuel_rate (server side calculation)=sum of fuel rate in all log pkts/no of log pkts
				 Carbon_emission (server side calculation)=sum of Carbon_emission in all log pkts/no of log pkts
				 WBVS(server side calculation)=sum of WBVS in all log pkts/no of log pkts*/




		HashMap<String,String> sampleMap=new HashMap<String, String>();
		//sampleMap.put("Fuel_rate", "NA");
		sampleMap.put("Carbon_emission", "NA");
		sampleMap.put("WBVS", "NA");

		//DF20180227 @Roopa GENSET server side aggregated param's

		sampleMap.put("GEN_BLK_VOLTAGE_PHASEA", "NA");
		sampleMap.put("GEN_VOLTAGE_PHASEB", "NA");
		sampleMap.put("GEN_VOLTAGE_PHASEC", "NA");
		sampleMap.put("GEN_CURRENT_PHASEA", "NA");
		sampleMap.put("GEN_CURRENT_PHASEB", "NA");
		sampleMap.put("GEN_CURRENT_PHASEC", "NA");
		sampleMap.put("GEN_AC_VOLTAGE", "NA");
		sampleMap.put("GEN_NEUTRAL_AC_RMS_VOLTAGE", "NA");
		sampleMap.put("GEN_PHASEA_LINE_VOLTAGE", "NA");
		sampleMap.put("GEN_PHASEB_LINE_VOLTAGE", "NA");
		sampleMap.put("GEN_PHASEC_LINE_VOLTAGE", "NA");
		sampleMap.put("GEN_PHASEC_LINE_NEUTRAL_VOLTAGE", "NA");
		sampleMap.put("GEN_FREQUENCY", "NA");
		sampleMap.put("overall_Power_Factor", "NA");
		sampleMap.put("load_on_phaseA", "NA");
		sampleMap.put("load_on_phaseB", "NA");
		sampleMap.put("load_on_phaseC", "NA");
		//DF20180301 Missed parameters in GENSET
		sampleMap.put("GEN_PHASEB_LINE_NEUTRAL_VOLTAGE", "NA");
		sampleMap.put("Engine_Coolant_temp", "NA");
		sampleMap.put("Gen_Oil_Pressure", "NA");


		Map aggregate_TxnData = new HashMap();
		Iterator itr = aggregatedData.iterator();
		while(itr.hasNext()){
			String key = (String) itr.next();
			double key_value = 0.0;


			Iterator TxnDataItr = TxnDataList.iterator();
			while(TxnDataItr.hasNext()){
				Map TxnDataMap = (Map) TxnDataItr.next();
				if(TxnDataMap.containsKey(key)){

					key_value +=  Double.parseDouble((String)TxnDataMap.get(key));

				}
			}

			if(key!=null && sampleMap.containsKey(key)){
				aggregate_TxnData.put(key, key_value/rowCount);
			}
			else{
				aggregate_TxnData.put(key, key_value/60); //converting in terms of hrs(one day period)
			}
		}

		//DF20170623 @Roopa diff logic for fuel_rate,IEPB,MEPB,HEPB and LEPB

		double fuelrate=0.0;
		double fuelrateVal=0.0;

		double FLEPB=0.0;

		double FIEPB=0.0;


		double FMEPB=0.0;


		double FHEPB=0.0;


		double FuelUsedInLPB=0.0;
		double FuelUsedInMPB=0.0;
		double FuelUsedInHPB=0.0;
		double FuelLoss=0.0;
		double FuelUsedInWorking=0.0;

		Iterator TxnDataItr = TxnDataList.iterator();
		while(TxnDataItr.hasNext()){
			Map TxnDataMap = (Map) TxnDataItr.next(); 

			if(TxnDataMap.containsKey("Fuel_rate")){
				fuelrate= Double.parseDouble((String)TxnDataMap.get("Fuel_rate"))/60;
				fuelrateVal +=fuelrate;
			}
			if(TxnDataMap.containsKey("LEPB")){
				FLEPB=fuelrate*Double.parseDouble((String)TxnDataMap.get("LEPB"));
				FuelUsedInLPB +=FLEPB;
			}
			if(TxnDataMap.containsKey("IEPB")){
				FIEPB=fuelrate*Double.parseDouble((String)TxnDataMap.get("IEPB"));
				FuelLoss +=FIEPB;
			}
			if(TxnDataMap.containsKey("MEPB")){
				FMEPB=fuelrate*Double.parseDouble((String)TxnDataMap.get("MEPB"));
				FuelUsedInMPB +=FMEPB;
			}
			if(TxnDataMap.containsKey("HEPB")){
				FHEPB=fuelrate*Double.parseDouble((String)TxnDataMap.get("HEPB"));
				FuelUsedInHPB +=FHEPB;
			}

		}
		FuelUsedInWorking=FuelUsedInLPB+FuelUsedInMPB+FuelUsedInHPB;

		aggregate_TxnData.put("Fuel_rate",fuelrateVal);

		aggregate_TxnData.put("FuelUsedInLPB", FuelUsedInLPB);
		aggregate_TxnData.put("FuelUsedInMPB", FuelUsedInMPB);
		aggregate_TxnData.put("FuelUsedInHPB", FuelUsedInHPB);
		aggregate_TxnData.put("FuelLoss", FuelLoss);
		aggregate_TxnData.put("FuelUsedInWorking", FuelUsedInWorking);


		return aggregate_TxnData;
	}


	public Map getAccumulatedTxnData(HashMap accumulatedTransactionalData,
			List accumulatedParameters) {
		// TODO Auto-generated method stub
		Iterator itr = accumulatedParameters.iterator();
		Map accumulated_TxnData = new HashMap();
		while(itr.hasNext()){
			String key = (String) itr.next();
			double key_value = 0.0;
			if(accumulatedTransactionalData.containsKey(key)){
				key_value = Double.parseDouble((String)accumulatedTransactionalData.get(key));
				accumulated_TxnData.put(key, key_value);
			}
		}
		return accumulated_TxnData;
	}

	public String insertData(String insertQuery){
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;

		Connection prodConnection = null;
		Statement statement = null;
		ConnectMySQL connMySql = new ConnectMySQL();
		int insertCount=0;

		try{

			
			prodConnection = connMySql.getConnection();
			statement = prodConnection.createStatement();

			 insertCount= statement.executeUpdate(insertQuery);


		}

		catch(Exception e)
		{
			
			//DF20180608 @Roopa trying multiple times if the error comes.
			
			/*e.printStackTrace();
			fLogger.fatal("Exception in inserting records to table::idMaster::"+e.getMessage());
			return "FAILURE";*/
			
			fLogger.fatal("Exception in inserting records to table::idMaster::Trying second time"+e.getMessage());
			
			try{

				
				prodConnection = connMySql.getConnection();
				statement = prodConnection.createStatement();

				 insertCount= statement.executeUpdate(insertQuery);


			}

			catch(Exception e1)
			{
				
				fLogger.fatal("Exception in inserting records to table::idMaster::Trying Third time"+e1.getMessage());
				
				try{

					
					prodConnection = connMySql.getConnection();
					statement = prodConnection.createStatement();

					 insertCount= statement.executeUpdate(insertQuery);


				}

				catch(Exception e2)
				{
					e2.printStackTrace();
					fLogger.fatal("Exception in inserting records to table::idMaster::"+e2.getMessage());
					return "FAILURE";
					
				}
				
			}
		}

		finally {
			if(statement!=null)
				try {
					statement.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			if (prodConnection != null) {
				try {
					prodConnection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		}

		return "SUCCESS";
	}

	/*public String getUserId(String uniqueId){
		String contact_Id=null;
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;

		Connection prodConnection = null;
		Statement statement = null;
		ResultSet rs=null;
		String TS=null;
		
		ConnectMySQL connMySql = new ConnectMySQL();

		String query="Select timeStamp from idMaster where Id='"+uniqueId+"'";

		try{
			
			prodConnection = connMySql.getConnection();
			statement = prodConnection.createStatement();

			rs=statement.executeQuery(query);

			while(rs.next()){
				TS=rs.getString("timeStamp");
			}
			byte[] encodedBytes = Base64.decodeBase64(uniqueId.getBytes());

			if(TS!=null){
				contact_Id= new String(encodedBytes).replaceAll(TS, "").replaceAll("\\s","");
				//System.out.println("contact_Id:"+contact_Id);
			}
			//DF20180518 - KO369761 - Commented else block to avoid sending same login id for security changes.
			else{
				//contact_Id= new String(encodedBytes).replaceAll("[0-9]", "").replaceAll("\\s","");
				contact_Id=uniqueId;
			}
		}

		
		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("Exception Error in validating the userId from idMaster::"+e.getMessage());
			return "FAILURE";
			
			//DF20180608 @Roopa trying multiple times if the error comes.
			
			fLogger.fatal("Exception Error in validating the userId from idMaster::Trying Second time."+e.getMessage());
			
			try{
				
				prodConnection = connMySql.getConnection();
				statement = prodConnection.createStatement();

				rs=statement.executeQuery(query);

				while(rs.next()){
					TS=rs.getString("timeStamp");
				}
				byte[] encodedBytes = Base64.decodeBase64(uniqueId.getBytes());

				if(TS!=null){
					contact_Id= new String(encodedBytes).replaceAll(TS, "").replaceAll("\\s","");
					//System.out.println("contact_Id:"+contact_Id);
				}
			}
			
			catch(Exception e1)
			{
				fLogger.fatal("Exception Error in validating the userId from idMaster::Trying Third time."+e1.getMessage());
				
				try{
					
					prodConnection = connMySql.getConnection();
					statement = prodConnection.createStatement();

					rs=statement.executeQuery(query);

					while(rs.next()){
						TS=rs.getString("timeStamp");
					}
					byte[] encodedBytes = Base64.decodeBase64(uniqueId.getBytes());

					if(TS!=null){
						contact_Id= new String(encodedBytes).replaceAll(TS, "").replaceAll("\\s","");
						//System.out.println("contact_Id:"+contact_Id);
					}
				}
				
				catch(Exception e2)
				{
					e2.printStackTrace();
					fLogger.fatal("Exception Error in validating the userId from idMaster::"+e2.getMessage());
					return "FAILURE";	
				}
				
			}
		}

		finally {
			
			if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			if(statement!=null)
				try {
					statement.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			if (prodConnection != null) {
				try {
					prodConnection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		}

		return contact_Id;
	}*/

	public Integer getInvalidCredCounter(String userId){
		int counter=0;
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;

		Connection prodConnection = null;
		Statement statement = null;
		ResultSet rs=null;


		String query="Select errorLogCounter from contact where contact_id='"+userId+"'";

		try{
			ConnectMySQL connMySql = new ConnectMySQL();
			prodConnection = connMySql.getConnection();
			statement = prodConnection.createStatement();

			rs=statement.executeQuery(query);

			while(rs.next()){
				counter=rs.getInt("errorLogCounter");
			}

		}

		catch (SQLException e) {

			e.printStackTrace();
			fLogger.fatal("SQL Exception Error in getting errorLogCounter from contact table"+"::"+e.getMessage());
		} 

		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("Exception Error in getting errorLogCounter from contact table::"+e.getMessage());
		}

		finally {
			if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			if(statement!=null)
				try {
					statement.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			if (prodConnection != null) {
				try {
					prodConnection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		}

		return counter;
	}



	public String getDecryptedPassword(String userId){

		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		
		ConnectMySQL connMySql = new ConnectMySQL();

		Connection prodConnection = null;
		Statement statement = null;
		ResultSet rs=null;
		String pwd=null;

		String query="SELECT CAST(AES_DECRYPT(password, primary_moblie_number) AS CHAR(50)) password "+
				"FROM  contact where contact_id='"+userId+"'";

		try{
			
			prodConnection = connMySql.getConnection();
			statement = prodConnection.createStatement();

			rs=statement.executeQuery(query);

			while(rs.next()){
				pwd=rs.getString("password");
			}

		}

		catch (Exception e) {
			
			//DF20180608 @Roopa trying multiple times if the error comes.
			
			fLogger.fatal("Exception Error in getting DecryptedPassword from contact table"+"::Trying Second time."+e.getMessage());

			/*e.printStackTrace();
			fLogger.fatal("SQL Exception Error in getting DecryptedPassword from contact table"+"::"+e.getMessage());
			return "FAILURE";*/
			
			try{
				
				prodConnection = connMySql.getConnection();
				statement = prodConnection.createStatement();

				rs=statement.executeQuery(query);

				while(rs.next()){
					pwd=rs.getString("password");
				}

			}
			
			catch (Exception e1) {
				
				fLogger.fatal("Exception Error in getting DecryptedPassword from contact table"+"::Trying Third time."+e1.getMessage());
				
				try{
					
					prodConnection = connMySql.getConnection();
					statement = prodConnection.createStatement();

					rs=statement.executeQuery(query);

					while(rs.next()){
						pwd=rs.getString("password");
					}

				}
				
				catch (Exception e11) {
					e11.printStackTrace();
					fLogger.fatal("Exception Error in getting DecryptedPassword from contact table after 3 attempts."+"::"+e11.getMessage());
					return "FAILURE";
				}
				
			}
		} 

		finally {
			if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			if(statement!=null)
				try {
					statement.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			if (prodConnection != null) {
				try {
					prodConnection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		}

		return pwd;


	}
	public String getEncryptedPassword(String userId){

		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;

		Connection prodConnection = null;
		Statement statement = null;
		ResultSet rs=null;
		String pwd=null;

		String query="SELECT password "+
				"FROM  contact where contact_id='"+userId+"'";

		try{
			ConnectMySQL connMySql = new ConnectMySQL();
			prodConnection = connMySql.getConnection();
			statement = prodConnection.createStatement();

			rs=statement.executeQuery(query);

			while(rs.next()){
				pwd=rs.getString("password");
			}

		}

		catch(Exception e)
		{
			/*e.printStackTrace();
			fLogger.fatal("Exception Error in getting EncryptedPassword from contact table::"+e.getMessage());
			return "FAILURE";*/
			
//DF20180608 @Roopa trying multiple times if the error comes.
			
			fLogger.fatal("Exception Error in getting DecryptedPassword from contact table"+"::Trying Second time."+e.getMessage());
			
			try{
				ConnectMySQL connMySql = new ConnectMySQL();
				prodConnection = connMySql.getConnection();
				statement = prodConnection.createStatement();

				rs=statement.executeQuery(query);

				while(rs.next()){
					pwd=rs.getString("password");
				}

			}

			catch(Exception e1)
			{
				fLogger.fatal("Exception Error in getting DecryptedPassword from contact table"+"::Trying Third time."+e1.getMessage());
				try{
					ConnectMySQL connMySql = new ConnectMySQL();
					prodConnection = connMySql.getConnection();
					statement = prodConnection.createStatement();

					rs=statement.executeQuery(query);

					while(rs.next()){
						pwd=rs.getString("password");
					}

				}

				catch(Exception e2)
				{
					e2.printStackTrace();
					fLogger.fatal("Exception Error in getting EncryptedPassword from contact table::After 3 attempts."+e2.getMessage());
					return "FAILURE";
				}
				
			}
		}

		finally {
			if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			if(statement!=null)
				try {
					statement.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			if (prodConnection != null) {
				try {
					prodConnection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		}

		return pwd;


	}

	//DF20171011: KO369761 - Generic method for Security Check added for input text fields.
	public String inputFieldValidation(String inputFieldText) {

		Properties prop = new Properties();
		Logger bLogger = BusinessErrorLoggerClass.logger;
		String status = "SUCCESS";
		if(inputFieldText!=null && !inputFieldText.equalsIgnoreCase("") && !inputFieldText.equalsIgnoreCase("null")){

			try {
				prop.load(getClass()
						.getClassLoader()
						.getResourceAsStream(
								"remote/wise/resource/properties/configuration.properties"));

				String sqlKeywords = prop.getProperty("SQL_Keywords");
				List<String> sqlKeywordsList = new ArrayList<String>(Arrays.asList(sqlKeywords.split(",")));
				inputFieldText = inputFieldText.toUpperCase();

				for(int i = 0; i<sqlKeywordsList.size();i++){
					if(inputFieldText.contains(sqlKeywordsList.get(i))){
						status = "Invalid input text";
						bLogger.error("Invalid input text :"+ inputFieldText);
						return status;
					}
				}

				//DF20190111 :MANI: Adding " (double quotes) in validation
				if(!inputFieldText.matches("[A-Za-z0-9\\_/.,'\\@\\-\\–\\|\"\\&\\(\\)\\#\\:\\!\\\\*\\+\\t\\n\\r ]+")){
					status = "Invalid input text";
					bLogger.error("Invalid input text :"+ inputFieldText);
				}
				else{
					status = "SUCCESS";
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return status;
	}
	
	public String responseValidation(String responseText) {
		Properties prop = new Properties();
		Logger bLogger = BusinessErrorLoggerClass.logger;
		String status = "SUCCESS";
		if(responseText!=null && !responseText.equalsIgnoreCase("") && !responseText.equalsIgnoreCase("null")){

			try {
				prop.load(getClass()
						.getClassLoader()
						.getResourceAsStream(
								"remote/wise/resource/properties/configuration.properties"));

				String sqlKeywords = prop.getProperty("SQL_Keywords");
				List<String> sqlKeywordsList = new ArrayList<String>(Arrays.asList(sqlKeywords.split(",")));
				responseText = responseText.toUpperCase();

				for(int i = 0; i<sqlKeywordsList.size();i++){
					if(responseText.equalsIgnoreCase(sqlKeywordsList.get(i))){
						status = "Invalid input text";
						bLogger.error("Invalid input text :"+ responseText);
						return status;
					}
				}

				//DF20190111 :MANI: Adding " (double quotes) in validation
				if(!responseText.matches("[A-Za-z0-9\\_/.,'\\@\\-\\–\\|\"\\&\\(\\)\\#\\:\\!\\\\*\\+\\t\\n\\r ]+")){
					status = "Invalid input text";
					bLogger.error("Invalid input text :"+ responseText);
				}
				else{
					status = "SUCCESS";
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return status;
	}


	// INTERFACES NEW DESIGN 05022018 fname=filename is primary key for the
	// DF20180205 :@Maniratnam ## Interfaces backTracking-- Common method to insert into the interface log details table
	public String insertInterfaceLogDetails(String fName,int totalCount,int successfullyPutToQ) {
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		InterfaceLogDetails interfacedata = null;
		String status = "SUCCESS";
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		try {
			if (!(fName.isEmpty())) {
				String findQ = ("from InterfaceLogDetails where filename = '"+fName+"'");
				Query query = session.createQuery(findQ);
				Iterator iterator = query.list().iterator();
				if(iterator.hasNext()){
					interfacedata = (InterfaceLogDetails) iterator.next();
					interfacedata.setTotalCount(totalCount);
					interfacedata.setSuccessfullyputtoqueue(successfullyPutToQ);
					session.update(interfacedata);
				}else{
					interfacedata = new InterfaceLogDetails();
					interfacedata.setFilename(fName);
					interfacedata.setTotalCount(totalCount);
					interfacedata.setSuccessfullyputtoqueue(successfullyPutToQ);
					session.save(interfacedata);
				}
				iLogger.info("Interface_Log_Details ::Inserted into Interface_Log_Details table with the file name "
						+ interfacedata.getFilename());
				status = "SUCCESS";
			} else {
				iLogger.info("Interface_Log_Details ::Mandatory parameter file name is empty");
				status = "FAILURE";
			}
		} catch (Exception e) {
			fLogger.fatal("Interface_Log_Details ::exception occured in inserting into Interface_Log_Details table. Exception:: "
					+ e.getMessage());
			status = "FAILURE";
		} finally {
			try{
				if (session.getTransaction().isActive()) {
					session.getTransaction().commit();
				}
				if (session.isOpen()) {
					session.flush();
					session.close();
				}
			}
			catch(Exception e)
			{
				try{
					if (session.isOpen()) {
						session.flush();
						session.close();
					}

					session = HibernateUtil.getSessionFactory().openSession();
					session.beginTransaction();

					interfacedata = null;
					String findQ = ("from InterfaceLogDetails where filename = '"+fName+"'");
					Query query = session.createQuery(findQ);
					Iterator iterator = query.list().iterator();
					if(iterator.hasNext()){
						interfacedata = (InterfaceLogDetails) iterator.next();
						interfacedata.setTotalCount(totalCount);
						interfacedata.setSuccessfullyputtoqueue(successfullyPutToQ);
						session.update(interfacedata);
						status = "SUCCESS";
					}
				}
				catch (Exception e2) {
					// TODO: handle exception
					fLogger.fatal("Interface_Log_Details ::exception occured in inserting into Interface_Log_Details table. Exception:: "
							+ e.getMessage());
					status = "FAILURE";
				}
				finally {
					try{
						if (session.getTransaction().isActive()) {
							session.getTransaction().commit();
						}
						if (session.isOpen()) {
							session.flush();
							session.close();
						}
					}
					catch (Exception e3) {
						// TODO: handle exception
						fLogger.fatal("Interface_Log_Details :: Exception in committing hibernate session");
						status = "FAILURE";
					}
				}
			}
		}
		return status;
	}

	// DF20180205 :@Maniratnam ## Interfaces backTracking-- Common method to update the interface log details table
	public static synchronized String updateInterfaceLogDetails(String fName, String columnName, int value) {
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		Connection con = null;
		Statement statement = null;
		ResultSet rSet = null;
		try {
			ConnectMySQL connectionObj = new ConnectMySQL();
			con = connectionObj.getConnection();
			statement = con.createStatement();
			int i = 0;

			if (!(fName.isEmpty())) {
				rSet = statement.executeQuery("select * from Interface_Log_Details where fileName = '"+fName+"'");
				if(!rSet.next()){
					try{
						i = statement.executeUpdate("INSERT INTO Interface_Log_Details VALUES ('"+fName+"',0,0,0,0,0)");
						iLogger.info("new row inserted::"+fName+"::"+i);
					}catch (Exception e) {
						e.printStackTrace();
						fLogger.fatal("Interface_Log_Details:: Exception occured in updating the Interface_Log_Details table. Exception ::"
								+ e.getMessage());
					}
				}
				if(columnName.equals("successfullyputtoqueue")){
					i = statement.executeUpdate("update Interface_Log_Details set Successfully_put_to_queue = Successfully_put_to_queue+1 where fileName = '"+fName+"'");
					iLogger.info(fName+"::updated count ::"+i);
				}
				if(columnName.equals( "successfullServiceInvocation")){
					i = statement.executeUpdate("update Interface_Log_Details set Successfull_Service_Invocation = Successfull_Service_Invocation+1 where fileName = '"+fName+"'");
					iLogger.info(fName+"::updated count ::"+i);
				}
				if(columnName.equals( "sucessCount")){
					i = statement.executeUpdate("update Interface_Log_Details set Success_Count = Success_Count+1 where fileName = '"+fName+"'");
					iLogger.info(fName+"::updated count ::"+i);
				}
				if(columnName.equals( "failureCount")){
					if(value == 1)
						i = statement.executeUpdate("update Interface_Log_Details set Failure_Count = Failure_Count+1 where fileName = '"+fName+"'");
					else
						i = statement.executeUpdate("update Interface_Log_Details set Failure_Count = Failure_Count-1 where fileName = '"+fName+"'");
					iLogger.info(fName+"::updated count ::"+i);
				}
				return "SUCCESS";

			}else{
				iLogger.info("Interface_Log_Details::Mandatory parameter file name is empty");
				return "FAILURE";
			}

		} catch (SQLException e) {
			e.printStackTrace();
			fLogger.fatal("Interface_Log_Details:: Exception occured in updating the Interface_Log_Details table. Exception ::"
					+ e.getMessage());
			return "FAILURE";
		}
		catch (Exception e) {
			fLogger.fatal("Interface_Log_Details:: Exception occured in updating the Interface_Log_Details table. Exception ::"
					+ e.getMessage(),e);
			return "FAILURE";
		}
		
// Shivam : 20211125 :  Awake ID: 5686 -start
		finally {
			if(rSet!=null)
				try {
					rSet.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}

			if(statement!=null)
				try {
					statement.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			if (con!= null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
	// Shivam : 20211125 :  Awake ID: 5686 -End
		}
	}
	
	
	
	//DF20180314 @Roopa getting communicating msg id for the VIN
		public String getMsgIdForVIN(String VIN){

			Logger fLogger = FatalLoggerClass.logger;
			Logger iLogger = InfoLoggerClass.logger;

			Connection prodConnection = null;
			Statement statement = null;
			ResultSet rs=null;
			String msgid=null;
			
			if(VIN!=null && (VIN.trim().length()==7 || VIN.trim().length()==17)){

				if(VIN.trim().length()==7)
				{
					
					VIN = new ExcavatorVisualizationReportImpl().getSerialNumberMachineNumber(VIN);
					if(VIN==null)
					{//invalid machine number
						fLogger.error("MachineNum"+ VIN + "does not exist !!!");
						return null ;
					}
				}
			}

			String query="select CAST(TRIM(BOTH '\"' FROM  ams.TxnData ->'$.MSG_ID') AS CHAR) as msgid from asset_monitoring_snapshot ams where serial_number='"+VIN+"'";

			try{
				ConnectMySQL connMySql = new ConnectMySQL();
				prodConnection = connMySql.getConnection();
				statement = prodConnection.createStatement();

				rs=statement.executeQuery(query);

				while(rs.next()){
					msgid=rs.getString("msgid");
				}

			}

			catch (SQLException e) {

				e.printStackTrace();
				fLogger.fatal("SQL Exception in getting msgid from the asset_monitoring_snapshot table"+"::"+e.getMessage());
				return "FAILURE";
			} 

			catch(Exception e)
			{
				e.printStackTrace();
				fLogger.fatal("Exception in getting msgid from the asset_monitoring_snapshot table::"+e.getMessage());
				return "FAILURE";
			}

			finally {
				if(statement!=null)
					try {
						statement.close();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				if (prodConnection != null) {
					try {
						prodConnection.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}

			}

			return msgid;


		}
		
		
		public String validateVIN(int loginTenancyId,String serial_Number){
			
			String vin=null;
			
			Logger fLogger = FatalLoggerClass.logger;
			Logger iLogger = InfoLoggerClass.logger;

			Connection prodConnection = null;
			Statement statement = null;
			ResultSet rs=null;
			
			if(serial_Number!=null && (serial_Number.trim().length()==7 || serial_Number.trim().length()==17)){

				if(serial_Number.trim().length()==7)
				{
					
					serial_Number = new ExcavatorVisualizationReportImpl().getSerialNumberMachineNumber(serial_Number);
					if(serial_Number==null)
					{//invalid machine number
						fLogger.error("MachineNum"+ serial_Number + "does not exist !!!");
						return null ;
					}
				}
			}
			

			String query="select aos.serial_number from asset_owner_snapshot aos where " +
					"aos.account_id in(select account_id from account where " +
					"mapping_code in(select mapping_code from account where " +
					"account_id in(select account_id from account_tenancy where " +
					"tenancy_id in("+loginTenancyId+")))) and aos.serial_number='"+serial_Number+"'";

			try{
				ConnectMySQL connMySql = new ConnectMySQL();
				prodConnection = connMySql.getConnection();
				statement = prodConnection.createStatement();

				rs=statement.executeQuery(query);

				while(rs.next()){
					vin=rs.getString("serial_number");
				}

			}

			catch (SQLException e) {

				e.printStackTrace();
				fLogger.fatal("SQL Exception in getting msgid from the asset_monitoring_snapshot table"+"::"+e.getMessage());
				return "FAILURE";
			} 

			catch(Exception e)
			{
				e.printStackTrace();
				fLogger.fatal("Exception in getting msgid from the asset_monitoring_snapshot table::"+e.getMessage());
				return "FAILURE";
			}

			finally {
				if(statement!=null)
					try {
						statement.close();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				if (prodConnection != null) {
					try {
						prodConnection.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}

			}

			return vin;
			
		}
		//DF20180313 @Mani :: To get countrycode for accountcode
		public String CountryCodeforAccountCode(String accountcode)
		{

			String countryCode = "";

			Connection prodConnection = null;
			Statement statement = null;
			ResultSet rs = null;
			String query = null;
			try {
				if (accountcode != null) {
					ConnectMySQL connMySql = new ConnectMySQL();
					prodConnection = connMySql.getConnection();
					statement = prodConnection.createStatement();
					query = "select CountryCode from account where Account_Code='"
							+ accountcode +"'";
					rs = statement.executeQuery(query);

					while (rs.next()) {
						countryCode = rs.getString("CountryCode");
					}
					if (countryCode == null) {
						countryCode = "";
					}
				} else {
					countryCode = "";
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (rs != null)
					try {
						rs.close();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}

				if (statement != null)
					try {
						statement.close();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				if (prodConnection != null) {
					try {
						prodConnection.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}

			}
			return countryCode;
		
			
		}
		//DF20180613 @Roopa 
		public String validateVINAgainstUserId(String serialNumber,String userId){
			String Serial_Number=null;
			Logger fLogger = FatalLoggerClass.logger;
			Logger iLogger = InfoLoggerClass.logger;
			
			Connection prodConnection = null;
			Statement statement = null;
			ResultSet rs = null;
			String query = null;
			ConnectMySQL connMySql = new ConnectMySQL();	
			
			//DF20181023 - Query was corrected by KO369761
			if(serialNumber.length()==17)
				query="select aos.serial_number from asset_owner_snapshot aos where aos.account_id in(select account_id from account where mapping_code in(select mapping_code from account where account_id in(select account_id from account_contact where Contact_ID = '"+userId+"' ))) and aos.serial_number= '"+serialNumber+"'";
			else
				query="select aos.serial_number from asset_owner_snapshot aos where aos.account_id in(select account_id from account where mapping_code in(select mapping_code from account where account_id in(select account_id from account_contact where Contact_ID = '"+userId+"' ))) and aos.serial_number like '%"+serialNumber+"%'";

			try{
				prodConnection = connMySql.getConnection();
				
				statement = prodConnection.createStatement();
				
				rs = statement.executeQuery(query);

				while (rs.next()) {
					Serial_Number = rs.getString("serial_number");
				}
			}
			catch (Exception e) {
				e.printStackTrace();
				fLogger.fatal("Exception in Validating VIN against the userID::"+e.getMessage());
			} finally {
				if (rs != null)
					try {
						rs.close();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}

				if (statement != null)
					try {
						statement.close();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				if (prodConnection != null) {
					try {
						prodConnection.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
			return Serial_Number;
		}
		
		/*
		 * DF20180821-KO369761-SecurityChangeFixes
		 * New logic implementation for decrypting encoded token id.
		 */
		public String getUserId(String uniqueId) {

			String contact_Id=null;
			Logger fLogger = FatalLoggerClass.logger;
			String encodedUserId = null;

			Connection prodConnection = null;
			Statement statement = null;
			ResultSet rs=null;

			ConnectMySQL connMySql = new ConnectMySQL();
			String query="Select userId from idMaster where Id='"+uniqueId+"' and status = 1";

			try{
				prodConnection = connMySql.getConnection();
				statement = prodConnection.createStatement();
				rs=statement.executeQuery(query);

				while(rs.next()){
					encodedUserId = rs.getString("userId");
				}

				if(encodedUserId!=null){
					byte[] encodedBytes = Base64.decodeBase64(encodedUserId.getBytes());
					contact_Id= new String(encodedBytes);
				}

			}catch(Exception e)
			{
				e.printStackTrace();
				fLogger.fatal("Exception Error in validating the userId from idMaster::Trying Second time."+e.getMessage());

				try{
					prodConnection = connMySql.getConnection();
					statement = prodConnection.createStatement();

					rs=statement.executeQuery(query);

					while(rs.next()){
						encodedUserId = rs.getString("userId");
					}

					if(encodedUserId!=null){
						byte[] encodedBytes = Base64.decodeBase64(encodedUserId.getBytes());
						contact_Id= new String(encodedBytes);
					}
				}catch(Exception e1)
				{
					fLogger.fatal("Exception Error in validating the userId from idMaster::Trying Third time."+e1.getMessage());

					try{
						prodConnection = connMySql.getConnection();
						statement = prodConnection.createStatement();

						rs=statement.executeQuery(query);

						while(rs.next()){
							encodedUserId = rs.getString("userId");
						}

						if(encodedUserId!=null){
							byte[] encodedBytes = Base64.decodeBase64(encodedUserId.getBytes());
							contact_Id= new String(encodedBytes);
						}
					}catch(Exception e2)
					{
						e2.printStackTrace();
						fLogger.fatal("Exception Error in validating the userId from idMaster::"+e2.getMessage());
						return "FAILURE";	
					}
				}
			}
			finally {

				if(rs!=null)
					try {
						rs.close();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				if(statement!=null)
					try {
						statement.close();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				if (prodConnection != null) {
					try {
						prodConnection.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}

			}

			return contact_Id;
		}
		
		
		//DF20180731 - KO369761: Deleting existing token ids for the user after password reset.
		public String deleteUserTokenIds(String userId){

			Logger fLogger = FatalLoggerClass.logger;
			String status = "SUCCESS";
			Connection prodConnection = null;
			Statement statement = null;

			try{

				//DF20180917 - Rajani Nagaraju - AutoReporting - Fixed user to be used where in sessionId for the user should not be deactivated as the job runs for more than half an hour
				if(userId!=null && userId.equalsIgnoreCase("repo00265"))
				{
					return "SUCCESS";
					
				}
				ConnectMySQL connMySql = new ConnectMySQL();
				String deleteQ = "delete FROM idMaster where CAST(FROM_BASE64(userId) AS CHAR(10000) ) = '"+userId+"'";

				prodConnection = connMySql.getConnection();
				statement = prodConnection.createStatement();
				statement.executeUpdate(deleteQ);

			}catch (Exception e) {
				e.printStackTrace();
				fLogger.fatal("Exception Caught::"+e.getMessage());
				status = "FAILURE";
			}
			finally {
				if(statement!=null)
					try {
						statement.close();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				if (prodConnection != null) {
					try {
						prodConnection.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
			
			return status;
		}
		
		
		public int getTenancyIdFromLoginId(String userId){

			Logger fLogger = FatalLoggerClass.logger;
			int tenancyId = 0;
			Connection prodConnection = null;
			Statement statement = null;
			ResultSet rs = null;

			try{

				ConnectMySQL connMySql = new ConnectMySQL();
				String selectQ = "select at.tenancy_id from account_contact ac,account_tenancy at where ac.contact_id = '"+userId+"' and ac.account_id = at.account_id";

				prodConnection = connMySql.getConnection();
				statement = prodConnection.createStatement();
				rs = statement.executeQuery(selectQ);

				if(rs.next()){
					tenancyId = rs.getInt("tenancy_id");
				}

			}catch (Exception e) {
				e.printStackTrace();
				fLogger.fatal("Exception Caught::"+e.getMessage());
			}
			finally {
				if(rs!=null)
					try {
						rs.close();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				if(statement!=null)
					try {
						statement.close();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				if (prodConnection != null) {
					try {
						prodConnection.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
			
			return tenancyId;
		}
		

		/*
		 * DF20180821-KO369761 - SecurityCheckChanges
		 * Validating User Role against given Login ID
		 */		
		public boolean roleValidationAgainstLoginId(String loginId, int roleId){

			Logger fLogger = FatalLoggerClass.logger;
			boolean status = true;
			int contactRoleId = 0;
			Connection prodConnection = null;
			Statement statement = null;
			ResultSet rs = null;

			try{
				if(loginId == null || roleId == 0){
					return status;
				}

				ConnectMySQL connMySql = new ConnectMySQL();
				String selectQ = "select role_id from contact where contact_id = '"+loginId+"'";

				prodConnection = connMySql.getConnection();
				statement = prodConnection.createStatement();
				rs = statement.executeQuery(selectQ);

				if(rs.next()){
					contactRoleId = rs.getInt("role_id");
				}

				if(contactRoleId != roleId)
					status = false;

			}catch (Exception e) {
				e.printStackTrace();
				fLogger.fatal("Exception Caught::"+e.getMessage());
			}
			finally {
				if(rs!=null)
					try {
						rs.close();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				if(statement!=null)
					try {
						statement.close();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				if (prodConnection != null) {
					try {
						prodConnection.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}

			return status;
		}
		
		
		//DF20181004 :: MA369757 :: csrf :: validating the csrf token against a loginid stored in redis.
		public boolean validateANTICSRFTOKEN(String loginid,String tokenId)
		{
			Properties prop = new Properties();
			Logger infoLogger = InfoLoggerClass.logger;
			Logger fatalError = FatalLoggerClass.logger;
			Logger businessLogger = BusinessErrorLoggerClass.logger;

			infoLogger.info("validateANITCSRFTOKEN encrypted loginid :: "+loginid);

			if(loginid == null || tokenId == null){
				businessLogger.error("token or login id is null::"+loginid+" ::"+tokenId);
				return false;
			}

			Set<String> tokens=null;
			boolean valid=false;
			Jedis jedis=null;
			try{
				try {
					prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
				} catch (IOException e) {
					fatalError.error("Exception while reading conf properties file "+e.getMessage());
				}
				infoLogger.info("validateANITCSRFTOKEN for loginid :: "+loginid+" against token : "+tokenId);
				String redisURL = prop.getProperty("geocodingredisurl");
				String redisPORT = prop.getProperty("geocodingredisport");
				jedis = new Jedis(redisURL,Integer.valueOf(redisPORT));
				loginid="CSRFKEY_"+loginid;
				if(jedis.exists(loginid)){
					tokens=jedis.smembers(loginid);
					//redisConnection.hset(latlonbucket, key, locationDetails.toString());
					Iterator iter = tokens.iterator();
					while(iter.hasNext())
					{
						String storedToken=iter.next().toString();
						storedToken=storedToken.split("\\|")[0];
						infoLogger.info("validateANITCSRFTOKEN :: stored tokens :"+storedToken);
						if(storedToken.equalsIgnoreCase(tokenId))
						{
							infoLogger.info("validateANITCSRFTOKEN :: matched tokens :: storedToken:"+storedToken+" received token :"+tokenId);
							valid=true;
						}
					}
				}
				else{
					infoLogger.info("validateANITCSRFTOKEN :: No tokens exists for loginid :: "+loginid);
				}
			}
			catch(Exception e){
				fatalError.error("Exception in validateANITCSRFTOKEN "+e.getMessage());
			} 
			finally{
				try{
					if(jedis != null && jedis.isConnected()){
						jedis.disconnect();
					}
				}
				catch(Exception e){
					fatalError.error("Exception while closing Redis Connection "+e.getMessage());
				} 
			}

			return valid;
		}
		
		
		// DF20181004 :: MA369757 :: csrf :: deleting the csrf token against a
		// loginid stored in redis.Flag=one : del one token,All: del all tokens of a
		// login id
		public String deleteANTICSRFTOKENS(String loginid,String token,String flag)
		{
			Properties prop = new Properties();
			Logger infoLogger = InfoLoggerClass.logger;
			Logger fatalError = FatalLoggerClass.logger;
			infoLogger.info("deleteANTICSRFTOKENS for loginid :: "+loginid);
			//String tokenId="";
			Set<String> tokens=null;
			String status="SUCCESS";Long deletedkeys=0L;
			Jedis jedis =null;
			try{
				try {
					prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
				} catch (IOException e) {
					fatalError.error("deleteANTICSRFTOKENS :: Exception while reading conf properties file "+e.getMessage());
				}
				infoLogger.info("deleteANTICSRFTOKENS delete tokens for loginid :: "+loginid);
				String redisURL = prop.getProperty("geocodingredisurl");
				String redisPORT = prop.getProperty("geocodingredisport");
				loginid="CSRFKEY_"+loginid;
				jedis = new Jedis(redisURL,Integer.valueOf(redisPORT));
				if(flag!=null && !flag.isEmpty()){
					//delete single token on validation
					if(flag.equalsIgnoreCase("one")){
						if(jedis.exists(loginid))
						{
							tokens=jedis.smembers(loginid);
							//redisConnection.hset(latlonbucket, key, locationDetails.toString());
							Iterator iter = tokens.iterator();
							while(iter.hasNext())
							{
								String storedToken=iter.next().toString();
								String tokenOnly=storedToken.split("\\|")[0];
								infoLogger.info("deleteANTICSRFTOKENS :: stored tokens :"+storedToken);
								if(tokenOnly.equalsIgnoreCase(token))
								{
									deletedkeys=jedis.srem(loginid,storedToken);
								}
							}
							infoLogger.info("deleteANTICSRFTOKENS deleted token :: "+token+" for loginid :: "+loginid);
						}
						else
						{
							infoLogger.info("deleteANTICSRFTOKENS No login id exists in Redis :login id: "+loginid);
						}
					}
					if(flag.equalsIgnoreCase("All")){
						if(jedis.exists(loginid))
						{
							deletedkeys=jedis.del(loginid);
							infoLogger.info("deleteANTICSRFTOKENS deleted All tokens for loginid :: "+loginid+" deletedkeys :"+deletedkeys);
						}
					}
					else
					{
						infoLogger.info("deleteANTICSRFTOKENS No login id exists in Redis :login id: "+loginid);
					}
				}
			}
			catch(Exception e){
				status="FAILURE";
				fatalError.error("deleteANTICSRFTOKENS Exception in "+e.getMessage());
			} 
			finally{
				try{
					if(jedis != null && jedis.isConnected()){
						jedis.disconnect();
					}
				}
				catch(Exception e){
					fatalError.error("deleteANTICSRFTOKENS Exception while closing Redis Connection "+e.getMessage());
				} 
			}
			infoLogger.info("deleteANTICSRFTOKENS :: status of deleting Csrf token for loginid "+loginid+" ");
			return status;

		}
		
		public boolean validateBSVorBSIVMachines(String serialNumber) {

	           boolean flag = false;
	            Logger fLogger = FatalLoggerClass.logger;
	            Logger iLogger = InfoLoggerClass.logger;
	            String assetTypeCode = null;
	           /* List<String> validBSVMachines = Arrays.asList("3DXS5", "3DXU5", "3DXX5", "4DXS5", "Z3DN", "ZDU4", "ZDX4",
	                    "ZDS4", "ZDXX4", "ZDXU4", "Z4DB", "Z4DJ", "ZDXS4");*/
	            Properties reader = new Properties(); //CR308.sn
	            String path = "/data3/JCBLiveLink/MECUIDModelList/MECUIDModelList.properties";
	            String BSVMachines = null;
	            try {
					reader.load(new FileInputStream(path));
					BSVMachines = reader.getProperty("BSV_Machines");    
				} catch (IOException e1) {
					e1.printStackTrace();
				}
	             
	            iLogger.info("BSV_Machines is : " + BSVMachines);
	            List<String> validBSVMachines = new ArrayList<String>(Arrays.asList(BSVMachines.split(",")));	           
	            iLogger.info("List of BSV_Machines is : " + validBSVMachines); //CR308.en
	            String query = "SELECT at.Asset_Type_Code from asset_type at, products p, asset a " +
	                    "WHERE a.serial_number = '" + serialNumber + "' AND a.product_id = p.product_id " +
	                    "AND p.asset_type_id = at.asset_type_id";
	            ConnectMySQL connMySql = new ConnectMySQL();
	            try(Connection prodConnection = connMySql.getConnection();
	                    Statement statement = prodConnection.createStatement();
	                    ResultSet rs= statement.executeQuery(query)){
	                            
	                while (rs.next()) {
	                    assetTypeCode = rs.getString("Asset_Type_Code");
	                }
	                iLogger.info("Serial Number:" + serialNumber + "::ModelCode:"+assetTypeCode);
	            }
	            catch(Exception e){
	                e.printStackTrace();
	                fLogger.fatal("Exception in fetching Model data from mysql::"+e.getMessage());
	            }
	            
	            if (validBSVMachines.contains(assetTypeCode))
	                flag = true;
	            
	            return flag;
	        }

		//ME100005771.sn
		public String encryptPassword(String decryptedPass, String contactId, String mobileNum) {
			Logger fLogger = FatalLoggerClass.logger;
			Logger iLogger = InfoLoggerClass.logger;

			String encryptedPwd=null;

			String query="SELECT CAST(AES_ENCRYPT('"+ decryptedPass +"','"+mobileNum+"') AS CHAR(50)) as encodedPwd from contact where contact_id ='"+contactId+"';";

			ConnectMySQL connMySql = new ConnectMySQL();
			try(Connection prodConnection = connMySql.getConnection();
					Statement statement = prodConnection.createStatement();
					ResultSet rs = statement.executeQuery(query)){
				while(rs.next()){
					encryptedPwd=rs.getString("encodedPwd");
				}
				iLogger.info("Encrypted password:"+encryptedPwd);
			}
			catch(Exception e){
				fLogger.fatal("Exception:", e);
			}
			return encryptedPwd;
		}
		//ME100005771.en
		
		//JCB6371.sn
		public String getRolloffDate(String vin) {
			Logger fLogger = FatalLoggerClass.logger;
			Logger iLogger = InfoLoggerClass.logger;

			String query="SELECT Rolloff_Date FROM asset WHERE serial_number='" + vin + "'";
			String rolloffDate=null;
			ConnectMySQL connMySql = new ConnectMySQL();
			try(Connection prodConnection = connMySql.getConnection();
					Statement statement = prodConnection.createStatement();
					ResultSet rs = statement.executeQuery(query)){
				while(rs.next()){
					rolloffDate=rs.getString("Rolloff_Date").split("\\.")[0];
				}
				iLogger.info("RolloffDate:"+rolloffDate);
			}
			catch(Exception e){
				fLogger.fatal("Exception:", e);
			}
			return rolloffDate;
		}
		//JCB6371.en
		
		//CR352.sn
		public List<Integer> getAccountIdsForVinFromTenancyIds(String vin, String tenancyIds) {
			Logger fLogger = FatalLoggerClass.logger;
			Logger iLogger = InfoLoggerClass.logger;

			List<Integer> accountIdList = new ArrayList<Integer>();
			String query="select account_id, serial_number from asset_owner_snapshot where account_id in (select account_id from account_tenancy where tenancy_id"
					+ " in (" + tenancyIds + ")) and serial_number like '%"+vin+"%'";
			ConnectMySQL connMySql = new ConnectMySQL();
			try(Connection prodConnection = connMySql.getConnection();
					Statement statement = prodConnection.createStatement();
					ResultSet rs = statement.executeQuery(query)){
				while(rs.next()){
					accountIdList.add(rs.getInt("account_id"));
				}
				iLogger.info("accountIdList:"+accountIdList);
			}
			catch(Exception e){
				fLogger.fatal("Exception:", e);
			}
			return accountIdList;
		}
		
		
		public List<Integer> getAccountIdsForTenancyIds(String tenancyIds) {
			Logger fLogger = FatalLoggerClass.logger;
			Logger iLogger = InfoLoggerClass.logger;

			List<Integer> accountIdList = new ArrayList<Integer>();
			String query="select account_id from account_tenancy where tenancy_id in (" + tenancyIds + ")";
			ConnectMySQL connMySql = new ConnectMySQL();
			try(Connection prodConnection = connMySql.getConnection();
					Statement statement = prodConnection.createStatement();
					ResultSet rs = statement.executeQuery(query)){
				while(rs.next()){
					accountIdList.add(rs.getInt("account_id"));
				}
				iLogger.info("accountIdList:"+accountIdList);
			}
			catch(Exception e){
				fLogger.fatal("Exception:", e);
			}
			return accountIdList;
		}
		//CR352.en	

        //JCB6417.sn
		public List<Integer> getTenancyIdListByAcountIds(String accountIdsList) {
			Logger fLogger = FatalLoggerClass.logger;
			Logger iLogger = InfoLoggerClass.logger;

			List<Integer> tenancyIdList = new ArrayList<Integer>();
			String query="select tenancy_id from account_tenancy where account_id in (" + accountIdsList + ")";
			ConnectMySQL connMySql = new ConnectMySQL();
			try(Connection prodConnection = connMySql.getConnection();
					Statement statement = prodConnection.createStatement();
					ResultSet rs = statement.executeQuery(query)){
				while(rs.next()){
					tenancyIdList.add(rs.getInt("tenancy_id"));
				}
				iLogger.info("tenancyIdList:"+tenancyIdList);
			}
			catch(Exception e){
				fLogger.fatal("Exception:", e);
			}
			return tenancyIdList;
		}
		//JCB6417.en
		//CR419.sn
		public String createAESSecretCode() {

			KeyGenerator keyGenerator = null;
			SecretKey secretKey=null;
			String secretKeyString=null;
			Logger fLogger = FatalLoggerClass.logger;
			try {
				keyGenerator = KeyGenerator.getInstance("AES");
				secretKey = keyGenerator.generateKey();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				fLogger.fatal("Exception:", e);
			}
			if(secretKey !=null) {
				secretKeyString = java.util.Base64.getEncoder().encodeToString(secretKey.getEncoded());
				secretKeyString= secretKeyString+System.currentTimeMillis();
			}
			return secretKeyString;
		}

		public List<Integer> getTenancyIdListFromLoginId(String loginId){
			Logger fLogger = FatalLoggerClass.logger;
			//String selectQ = "select at.tenancy_id from account_contact ac,account_tenancy at where ac.contact_id = '"+loginId+"' and ac.account_id = at.account_id";
			String selectQ = "SELECT  " + 
					"    * " + 
					"FROM " + 
					"    account_tenancy " + 
					"WHERE " + 
					"    account_id IN (SELECT  " + 
					"            account_id " + 
					"        FROM " + 
					"            account " + 
					"        WHERE " + 
					"            mapping_code IN (SELECT  " + 
					"                    mapping_code " + 
					"                FROM " + 
					"                    account " + 
					"                WHERE " + 
					"                    account_code IN (SELECT  " + 
					"                            account_code " + 
					"                        FROM " + 
					"                            account_contact ac, " + 
					"                            account a " + 
					"                        WHERE " + 
					"                            ac.account_id = a.account_id " + 
					"                                AND ac.contact_id = '"+loginId+"')))";


			List<Integer> tenancyList = new ArrayList<>();
			ConnectMySQL connMySql = new ConnectMySQL();
			try(Connection prodConnection = connMySql.getConnection();
					Statement statement = prodConnection.createStatement()){
				ResultSet rs = statement.executeQuery(selectQ);
				while(rs.next()) {
					int tenancyId  = rs.getInt("tenancy_id");
					tenancyList.add(tenancyId);
				}
			} catch (Exception e) {
				e.printStackTrace();
				fLogger.fatal("Eception occurred:"+ e.getMessage());
			}
			return tenancyList;
		}

		public String getAESSecretCode( int clientId) {
			Logger fLogger = FatalLoggerClass.logger;
			String secretCode = null;
			String selectQ = "select CAST(AES_DECRYPT(SecretCode, Email_Id) AS CHAR(128)) as pass from aemp_user_details where client_id="+clientId;
			ConnectMySQL connMySql = new ConnectMySQL();
			try(Connection prodConnection = connMySql.getConnection();
					Statement statement = prodConnection.createStatement()){
				ResultSet rs = statement.executeQuery(selectQ);
				if(rs.next()) {
					secretCode  = rs.getString("pass");
				}
			} catch (Exception e) {
				e.printStackTrace();
				fLogger.fatal("Eception occurred:"+ e.getMessage());
			}
			return secretCode;
		}
		//CR419.en

		//CR397.sn
		public String getVin(String vin) {
			Logger fLogger = FatalLoggerClass.logger;
			String query = "SELECT serial_number from asset WHERE serial_number ='" + vin+"' and status=1";
			String tmpVin=vin;
			ConnectMySQL connMySql = new ConnectMySQL();
			try(Connection prodConnection = connMySql.getConnection();
					Statement statement = prodConnection.createStatement()){
				ResultSet rs = statement.executeQuery(query);
				if(rs.next())
					vin  = rs.getString("serial_number");
				if (vin == null) {
					query = "SELECT serial_number from asset WHERE machine_number = '"+ tmpVin+"' and status =1";
					ResultSet rs2 = statement.executeQuery(query);
					if(rs2.next())
						vin  = rs.getString("serial_number");
				}else {
					return vin;
				}
			} catch (Exception e) {
				e.printStackTrace();
				fLogger.fatal("Eception occurred:"+ e.getMessage());
			}
			return vin;
		}
		//CR397.en
}
