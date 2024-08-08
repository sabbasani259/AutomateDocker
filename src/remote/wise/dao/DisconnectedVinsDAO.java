package remote.wise.dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.ConnectMySQL;

public class DisconnectedVinsDAO {

	public String updateBlockedVinsAndFileWithCount(String inputFile,String sourceDir,String destinationDir, String date) throws IOException {
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		ConnectMySQL connFactory = new ConnectMySQL();
		String result = "SUCCESS";
		BufferedReader br = new BufferedReader(new FileReader(sourceDir+inputFile));
		iLogger.info("BufferedReader :" +br);
		//String line = br.readLine();
		int count=0;
//		if(line!=null)
//			count=0;
		String query="";
		String line=null;
		
		//updating the BlockVINTraceability table
			try (Connection connection = connFactory.getConnection();
					Statement statement = connection.createStatement()
							
					) {
				while((line = br.readLine()) != null){
					try{
					count++;
					String[] input= line.split("\\|");
					String VINNumber=input[0];
					String SIMNumber=input[1];
					query = "insert into BlockedVins (VINNumber,SIMNumber,StatusFlag,updatedTimestamp)" +
							" values("+"'"+VINNumber+"',"+"'"+SIMNumber+"',"+1+",'"+date+"'"+")";
					iLogger.info("Query for updateBlockedVinsAndFileWithCount : "+query);
					statement.executeUpdate(query);
					}catch(Exception e){
						fLogger.fatal("updateBlockedVinsAndFileWithCount() Issue with the query:"+query
								+ e.getMessage());
					}
				}
	
			} catch (Exception e) {
				result="FAILURE";
				fLogger.fatal("updateBlockedVinsAndFileWithCount()::issue while updating the BlockedVins table "
						+ e.getMessage());
			}finally{
				br.close();
			}
			
			//updating the BlockTraceabilityFileLevel table with file and count
			
			query = "insert into BlockTraceabilityFileLevel (FileName,FileRecCount)" +
					" values("+"'"+sourceDir+inputFile+"',"+count+")";
			try (Connection connection = connFactory.getConnection();
					PreparedStatement statement = connection
							.prepareStatement(query);
					) {
				
					statement.executeUpdate(query);
	
			} catch (Exception e) {
				result="FAILURE";
				fLogger.fatal("updateBlockedVinsAndFileWithCount()::issue while updating the BlockTraceabilityFileLevel table"
						+ e.getMessage());
			}
			//For Unix
			 sourceDir = "/user/JCBLiveLink/Data/SimDiscconectedData/current_data/"; 
			 destinationDir = "/user/JCBLiveLink/Data/SimDiscconectedData/archived_data/";
			//moving the file from the input folder and putting it in archived folder.
			try{
	        Files.move(Paths.get(sourceDir + inputFile), Paths.get(destinationDir + inputFile), StandardCopyOption.REPLACE_EXISTING);
	        iLogger.info("moving the file from the input folder to archived folder");
			}catch(Exception e){
				result="FAILURE";
				fLogger.fatal("issue in moving the file from the input folder to archived folder"
						+ e.getMessage());
				e.printStackTrace();
	        }
		return result;
	}

	public String updateTraceabilityForBlockedVin(String date) throws IOException {
		
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		ConnectMySQL connFactory = new ConnectMySQL();
		String result = "SUCCESS";
		String query=null;
		query="select VINNumber,SIMNumber from BlockedVins where StatusFlag=1 and updatedTimestamp='"+date+"'";
		

		iLogger.info("Query for fetching data from blockedvins table : "+query);
		Map<String,String> vinMap=new HashMap<>();
		//getting the vins updated on the day
			try (Connection connection = connFactory.getConnection();
					PreparedStatement statement = connection
							.prepareStatement(query);
					ResultSet rs=statement.executeQuery();
					) {
				while(rs.next()){
					vinMap.put(rs.getString("VINNumber"), rs.getString("SIMNumber"));
				}
	
			} catch (Exception e) {
				result="FAILURE";
				fLogger.fatal("updateAssetTable()::issue while fetching data from  the blockedVIn table "
						+ e.getMessage());
			}
			
			//updating the BlockVINTraceability table
			try (Connection connection = connFactory.getConnection();
					PreparedStatement statement = connection
							.prepareStatement(query);
					) {
				
				try{
				for(String vin:vinMap.keySet()){
					String sim=vinMap.get(vin);
					query = "insert into BlockVINTraceability (VINNumber,SIMNumber,RequestTS,UserRequested)" +
							" values("+"'"+vin+"',"+"'"+sim+"',"+"'"+date+"',"+"'Batch'"+")";
					iLogger.info("Query for updateTraceabilityForBlockedVin : "+query);
					statement.executeUpdate(query);
				}
				}catch(Exception e){
					result="FAILURE";
					fLogger.fatal("updateAssetTable()::issue while updating the BlockVINTraceability table "
							+ e.getMessage());
				}
	
			} catch (Exception e) {
				result="FAILURE";
				fLogger.fatal("updateAssetTable()::issue while updating the BlockVINTraceability table "
						+ e.getMessage());
			}


		return result;
	}

	public String updateAssetTable(String date) throws IOException {
		
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		ConnectMySQL connFactory = new ConnectMySQL();
		String result = "SUCCESS";
		String query=null;
		
		query="select VINNumber from BlockedVins where StatusFlag=1 and updatedTimestamp='"+date+"'";

		iLogger.info("Query to get data from asset table : "+query);
		List<String> vinList= new ArrayList<String>();
		
		//getting the vins updated on the day
			try (Connection connection = connFactory.getConnection();
					PreparedStatement statement = connection
							.prepareStatement(query);
					ResultSet rs=statement.executeQuery();
					) {
				while(rs.next()){
					vinList.add(rs.getString("VINNumber"));
				}
	
			} catch (Exception e) {
				result="FAILURE";
				fLogger.fatal("updateAssetTable()::issue while fetching data from  the blockedVIn table "
						+ e.getMessage());
			}
		
		
		
		//updating the BlockVINTraceability table
			try (Connection connection = connFactory.getConnection();
					PreparedStatement statement = connection
							.prepareStatement(query);
					) {
				for(String vin:vinList){
					query = "update asset set DisconnectedStatus=0"+
							" where serial_number="+"'"+vin+"'";
					iLogger.info("Query for updateAssetTable : "+query);
					statement.executeUpdate(query);
				}
	
			} catch (Exception e) {
				result="FAILURE";
				fLogger.fatal("updateAssetTable()::issue while updating the asset table "
						+ e.getMessage());
			}

		return result;
	}
}