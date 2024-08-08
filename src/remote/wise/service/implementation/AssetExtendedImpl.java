package remote.wise.service.implementation;

////import org.apache.log4j.Logger;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
////import org.apache.log4j.Logger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;

import remote.wise.businessentity.AssetEntity;
import remote.wise.businessobject.AssetDetailsBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.AssetExtendedReqContract;
import remote.wise.service.datacontract.AssetExtendedRespContract;
import remote.wise.util.CommonUtil;
//import remote.wise.util.WiseLogger;
import remote.wise.util.ConnectMySQL;



public class AssetExtendedImpl 
{
	//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
	//public static WiseLogger businessLogger = WiseLogger.getLogger("AssetExtendedImpl:","businessError");

	private AssetEntity SerialNumber;
	private String OperatingStartTime;
	private String OperatingEndTime;
	private String UsageCategory;
	private String Offset;
	private String DriverName;
	private String DriverContactNumber;
	private String notes;
	private int primaryOwnerId;
	private String fwVersionNumber;
	//CR256: Added Sale Date 
private String saleDate;
	
	
	public String getSaleDate() {
		return saleDate;
	}


	public void setSaleDate(String saleDate) {
		this.saleDate = saleDate;
	}
	

	public String getFwVersionNumber() {
		return fwVersionNumber;
	}


	public void setFwVersionNumber(String fwVersionNumber) {
		this.fwVersionNumber = fwVersionNumber;
	}

	//DF20131108 - Rajani Nagaraju - To update Edge Proxy table on RollOff with status of machine as Normal
	private String deviceStatus;

	//static Logger businessLogger = Logger.getLogger("businessErrorLogger");
	public String getNotes() {
		return notes;
	}


	public int getPrimaryOwnerId() {
		return primaryOwnerId;
	}


	public void setPrimaryOwnerId(int primaryOwnerId) {
		this.primaryOwnerId = primaryOwnerId;
	}


	public void setNotes(String notes) {
		this.notes = notes;
	}


	public AssetEntity getSerialNumber() {
		return SerialNumber;
	}


	public void setSerialNumber(AssetEntity serialNumber) {
		SerialNumber = serialNumber;
	}


	public String getOperatingStartTime() {
		return OperatingStartTime;
	}


	public void setOperatingStartTime(String operatingStartTime) {
		OperatingStartTime = operatingStartTime;
	}


	public String getOperatingEndTime() {
		return OperatingEndTime;
	}


	public void setOperatingEndTime(String operatingEndTime) {
		OperatingEndTime = operatingEndTime;
	}


	public String getUsageCategory() {
		return UsageCategory;
	}


	public void setUsageCategory(String usageCategory) {
		UsageCategory = usageCategory;
	}


	public String getOffset() {
		return Offset;
	}


	public void setOffset(String offset) {
		Offset = offset;
	}


	public String getDriverName() {
		return DriverName;
	}


	public void setDriverName(String driverName) {
		DriverName = driverName;
	}


	public String getDriverContactNumber() {
		return DriverContactNumber;
	}


	public void setDriverContactNumber(String driverContactNumber) {
		DriverContactNumber = driverContactNumber;
	}

	/**
	 * @return the deviceStatus
	 */
	public String getDeviceStatus() {
		return deviceStatus;
	}


	/**
	 * @param deviceStatus the deviceStatus to set
	 */
	public void setDeviceStatus(String deviceStatus) {
		this.deviceStatus = deviceStatus;
	}

	/**
	 * method to get signal Strength of machine
	 * @param serial_number
	 * @return signalStrength
	 * @throws SQLException
	 */
	public String getSignalStrength(String serial_number ) throws SQLException{
		String signalStrength="";		
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		Connection prodConnection = null;
		Statement statement = null;
		ResultSet rs = null;
		try{
			ConnectMySQL connMySql = new ConnectMySQL();
			prodConnection = connMySql.getConnection();
			statement = prodConnection.createStatement();
			iLogger.info("Fetching records from asset_monitoring_snapshot----------------" );
			rs= statement.executeQuery("select json_extract(TxnData,'$.Signal_Strength') as SignalStrength  from asset_monitoring_snapshot where Serial_Number='"+serial_number +"'");
			while(rs.next()){
					if(rs.getObject("SignalStrength")!=null){						
					signalStrength=rs.getObject("SignalStrength").toString();
					iLogger.info("Fetching signal strength from asset_monitoring_snapshot----------------"+signalStrength);
					}
					iLogger.info(" signal strength  unavailable ----------------" );
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("Exception in fetching data from mysql::"+e.getMessage());
		}
		finally{
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
		return signalStrength;
	}
	// Shajesh : 20220105 : CR-260-DisplayOtherECUAddressonFleetGeneral 
	public String getMachineECUSoftwareID(String serial_number) throws SQLException{        
        String MachineECUSoftwareID =null;
        String MachineECUAddr =null;
        String MachineECUAddr2 =null;
        String MachineECUAddr3 =null;
        String MachineECUAddr4 =null;
        Logger fLogger = FatalLoggerClass.logger;
        Logger iLogger = InfoLoggerClass.logger;
        //Check if Machine is BSV or BSIV
        boolean ecuFlag = new CommonUtil().validateBSVorBSIVMachines(serial_number);
        Connection prodConnection = null;
        Statement statement = null;
        ResultSet rs = null;
        try{
            ConnectMySQL connMySql = new ConnectMySQL();
            prodConnection = connMySql.getConnection();
            statement = prodConnection.createStatement();
            iLogger.info("--- Fetching records from asset_monitoring_snapshot for Software_ID_ECU2 ---" );            

           rs= statement.executeQuery("select json_extract(TxnData,'$.Software_ID_ECU2') as MachineECUSoftwareID,"
                    + " json_extract(TxnData,'$.Software_ID_ECU') as MachineECU,"
                    + " json_extract(TxnData,'$.Software_ID_ECU_2') as MachineECU2,"
                    + " json_extract(TxnData,'$.Software_ID_ECU_3') as MachineECU3,"
                    + " json_extract(TxnData,'$.Software_ID_ECU_4') as MachineECU4"
                    + " from asset_monitoring_snapshot where Serial_Number='"+serial_number +"'");
            while(rs.next()){
                if (ecuFlag) {
                    if(rs.getObject("MachineECU")!=null) {
                        MachineECUAddr =rs.getObject("MachineECU").toString().replace("\"","");
                        if(MachineECUAddr.substring(0, 5).equalsIgnoreCase("21527")) {
                            MachineECUSoftwareID = MachineECUAddr.substring(5);
                            iLogger.info("-- MachineECU from asset_monitoring_snapshot-----" +MachineECUSoftwareID);
                            break;
                        }
                    }
                    if(rs.getObject("MachineECU2")!=null) {
                        MachineECUAddr2 =rs.getObject("MachineECU2").toString().replace("\"","");
                        if(MachineECUAddr2.substring(0, 5).equalsIgnoreCase("21527")) {
                            MachineECUSoftwareID = MachineECUAddr2.substring(5);
                            iLogger.info("-- MachineECU2 from asset_monitoring_snapshot-----" +MachineECUSoftwareID);
                            break;
                        }
                    }
                    if(rs.getObject("MachineECU3")!=null) {
                        MachineECUAddr3 =rs.getObject("MachineECU3").toString().replace("\"","");
                        if(MachineECUAddr3.substring(0, 5).equalsIgnoreCase("21527")) {
                            MachineECUSoftwareID = MachineECUAddr3.substring(5);
                            iLogger.info("-- MachineECU3 from asset_monitoring_snapshot-----" +MachineECUSoftwareID);
                            break;
                        }
                    }
                    if(rs.getObject("MachineECU4")!=null) {
                        MachineECUAddr4 =rs.getObject("MachineECU4").toString().replace("\"","");
                        if(MachineECUAddr4.substring(0, 5).equalsIgnoreCase("21527")) {
                            MachineECUSoftwareID = MachineECUAddr4.substring(5);
                            iLogger.info("-- MachineECU4 from asset_monitoring_snapshot-----" +MachineECUSoftwareID);
                            break;
                        }
                    }
                    if (MachineECUSoftwareID == null) {
                    	iLogger.info("-- MachineECU  unavailable so setting as NA --------" +MachineECUSoftwareID);
                    	 MachineECUSoftwareID="NA";
                    }
                }else {
                    if(rs.getObject("MachineECUSoftwareID")!=null){
                        iLogger.info("-- Software_ID_ECU2 from asset_monitoring_snapshot-----" +rs.getObject("MachineECUSoftwareID"));
                        MachineECUSoftwareID=rs.getObject("MachineECUSoftwareID").toString();
                    }else{
                        MachineECUSoftwareID="NA";
                        iLogger.info(" -------- Software_ID_ECU2  unavailable so setting as NA --------" +MachineECUSoftwareID);
                    }
                }
            }

       }
        catch(Exception e)
        {
            e.printStackTrace();
            fLogger.fatal("Exception in fetching data from mysql::"+e.getMessage());
        }
        finally{
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
        return MachineECUSoftwareID;
    }
	/**
	 * method to get asset extended details
	 * @param assetReqObj
	 * @return AssetExtendedRespContract
	 * @throws CustomFault
	 */
	public AssetExtendedRespContract getAssetExtendedDetails(AssetExtendedReqContract assetReqObj)throws CustomFault
	{
		Logger bLogger = BusinessErrorLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;

		if(assetReqObj.getSerialNumber()==null)
		{
			bLogger.error("VIN is not specified");
			throw new CustomFault("VIN is not specified");
		}

		AssetDetailsBO assetBO=new AssetDetailsBO();
		AssetExtendedImpl assetDetailsBo = assetBO.getAssetExtendedDetails(assetReqObj.getSerialNumber());

		AssetExtendedRespContract respObj = new AssetExtendedRespContract();

		respObj.setDriverContactNumber(assetDetailsBo.getDriverContactNumber());
		respObj.setDriverName(assetDetailsBo.getDriverName());
		respObj.setOffset(assetDetailsBo.getOffset());
		respObj.setOperatingEndTime(assetDetailsBo.getOperatingEndTime());
		respObj.setOperatingStartTime(assetDetailsBo.getOperatingStartTime());
		respObj.setUsageCategory(assetDetailsBo.getUsageCategory());
		respObj.setNotes(assetDetailsBo.getNotes());
		respObj.setPrimaryOwnerId(assetDetailsBo.getPrimaryOwnerId());
		//FW Version Number : To be displayed in the portal: 2015-03-04 : Deepthi
		//respObj.setFWVersionNumber(assetDetailsBo.getFwVersionNumber());
		//CR256: Added Sale Date for displaying on Fleet General		
		//DF20131108 - Rajani Nagaraju - To update Edge Proxy table on RollOff with status of machine as Normal
		respObj.setDeviceStatus(assetDetailsBo.getDeviceStatus());

		CommonUtil util = new CommonUtil();
		String isValidinput=null;
		//DF20180924 :: MA369757 :: security check for fields.
		if(assetDetailsBo.getDriverContactNumber()!=null){
			isValidinput = util.inputFieldValidation(assetDetailsBo.getDriverContactNumber());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}
		if(assetDetailsBo.getDriverName()!=null){
			isValidinput = util.inputFieldValidation(assetDetailsBo.getDriverName());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}
		if(assetDetailsBo.getOffset()!=null){
			isValidinput = util.inputFieldValidation(assetDetailsBo.getOffset());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}
		if(assetDetailsBo.getOperatingEndTime()!=null){
			isValidinput = util.inputFieldValidation(assetDetailsBo.getOperatingEndTime());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}
		if(assetDetailsBo.getUsageCategory()!=null){
			isValidinput = util.inputFieldValidation(assetDetailsBo.getUsageCategory());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}
		if(assetDetailsBo.getOperatingStartTime()!=null){
			isValidinput = util.inputFieldValidation(assetDetailsBo.getOperatingStartTime());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}
		if(assetDetailsBo.getNotes()!=null){
			isValidinput = util.inputFieldValidation(assetDetailsBo.getNotes());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}
		isValidinput = util.inputFieldValidation(assetDetailsBo.getPrimaryOwnerId()+"");
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}

		if(assetDetailsBo.getFwVersionNumber()!=null){
			isValidinput = util.inputFieldValidation(assetDetailsBo.getFwVersionNumber());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}
		if(assetDetailsBo.getDeviceStatus()!=null){
			isValidinput = util.inputFieldValidation(assetDetailsBo.getDeviceStatus());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}

		//Mahesh : 20221129 - Extended Changes.so
//		String signalStrength;
//		try {
//			signalStrength = getSignalStrength(assetReqObj.getSerialNumber()).replaceAll("\\\"","");
//			respObj.setDeviceStatus(assetDetailsBo.getDeviceStatus()+ " | "+signalStrength);
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
		//eo
		// Shajesh : 20220105 : CR-260-DisplayOtherECUAddressonFleetGeneral 
		String machineECUSoftwareID;
		try {
			machineECUSoftwareID = getMachineECUSoftwareID(assetReqObj.getSerialNumber()).replaceAll("\\\"","");
			//Dhiraj k : 20220705 : Changes for incorrect value on ui
			//respObj.setFWVersionNumber(assetDetailsBo.getFwVersionNumber()+"#" +assetDetailsBo.getSaleDate() +" | "+machineECUSoftwareID);
			respObj.setFWVersionNumber(assetDetailsBo.getFwVersionNumber() + " | " + machineECUSoftwareID);
			iLogger.info("AssetExtendedImpl :: getAssetExtendedDetails :: Software_ID_ECU2 " + respObj.getFWVersionNumber());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return respObj;
	}



	

	public String setAssetExtendedDetails (AssetExtendedRespContract assetResp)throws CustomFault
	{
		Logger bLogger = BusinessErrorLoggerClass.logger;

		if(assetResp.getSerialNumber()==null)
		{
			bLogger.error("VIN is not specified");
			throw new CustomFault("VIN not specified");
		}

		AssetDetailsBO assetBO = new AssetDetailsBO();

		//DF20131108 - Rajani Nagaraju - To update Edge Proxy table on RollOff with status of machine as Normal
//		String flag = assetBO.setAssetExtendedDetails(assetResp.getSerialNumber(), assetResp.getOperatingStartTime(), assetResp.getOperatingEndTime(), assetResp.getOffset(),
//				assetResp.getDriverName(), assetResp.getDriverContactNumber(), assetResp.getUsageCategory(),assetResp.getNotes(), assetResp.getDeviceStatus(),assetResp.getCmhLoginId());

		String flag = assetBO.setAssetExtendedDetails(
                assetResp.getSerialNumber(), assetResp.getOperatingStartTime(),
                assetResp.getOperatingEndTime(), assetResp.getOffset(),
                assetResp.getDriverName(), assetResp.getDriverContactNumber(),
                assetResp.getUsageCategory(), assetResp.getNotes(),
                assetResp.getDeviceStatus(), assetResp.getCmhLoginId());
		return flag;

	}
	
	//added by santosh
	
		@SuppressWarnings({ "unchecked" })
		public void setAssetMoolDAService(AssetExtendedRespContract assetResp){
			
			Logger iLogger = InfoLoggerClass.logger;
			Logger fLogger = FatalLoggerClass.logger;
			
			String usageCategory, finalJsonString, connIP, connPort;
			
			usageCategory = finalJsonString = connIP = connPort = null;
			ConnectMySQL connFactory = new ConnectMySQL();
			
			String acuQ = ("select usgaeCategory from asset_profile where" +
					" serialNumber='"+assetResp.getSerialNumber()+"'");
			
			try(Connection conn = connFactory.getConnection();
					Statement st = conn.createStatement();
					ResultSet rs = st.executeQuery(acuQ)){
				HashMap<String,String> finalProfileDetailsMap = new HashMap<String,String>();
				finalProfileDetailsMap.put("AssetID", assetResp.getSerialNumber());
				JSONObject jsonObj = null;
				while(rs.next()){
					usageCategory = rs.getString("usgaeCategory");
					
					if(usageCategory != null)
						finalProfileDetailsMap.put("usageCategory", usageCategory);
						finalProfileDetailsMap.put("RenewalFlag", "1");
					
				}
//				if(usageCategory != null)
//					finalProfileDetailsMap.put("UsageCategory", assetResp.getSerialNumber());
				
				jsonObj = new JSONObject();
				jsonObj.putAll(finalProfileDetailsMap);
				finalJsonString = jsonObj.toString();
				iLogger.info("AssetProfileDetails:setAssetMoolDAService:"+finalJsonString);
			
			
				//Invoking the REST URL from WISE to MOOL DA.
				try{
					Properties prop = new Properties();
					prop.load(getClass().getClassLoader().getResourceAsStream
							("remote/wise/resource/properties/configuration.properties"));
					connIP = prop.getProperty("MDA_ServerIP");
					connPort = prop.getProperty("MDA_ServerPort");
				}catch(Exception e){
					fLogger.fatal("AssetProfileDetails:setAssetMoolDAService: " +
							"Exception in getting Server Details for MDA Layer from properties file: " +e);
					throw new Exception("Error reading from properties file");
				}try{
					
					URL url = new URL("http://"+connIP+":"+connPort+"/MoolDA/NickNameUpdate/" +
							"inputPayloadDetails?inputPayloadMap="+URLEncoder.encode(finalJsonString, "UTF-8"));
					
					HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
					
					httpConn.setRequestProperty("Content-Type", "text/plain; charset=utf8");
					httpConn.setRequestProperty("Accept", "text/plain");
					httpConn.setRequestMethod("GET");
					
					if (httpConn.getResponseCode() != 200 && httpConn.getResponseCode() != 204) {
						iLogger.info("MDAReports report status: FAILURE for setAssetMoolDAService Report VIN:"+"serialNu"+" ::Response Code:"+httpConn.getResponseCode());
						throw new Exception("Failed : HTTP error code : "
								+ httpConn.getResponseCode());
						
					}
					
					iLogger.info("MDAReports report status: SUCCESS for setAssetMoolDAService Report VIN:"+"serialNu"+" ::Response Code:"+httpConn.getResponseCode());
					
					BufferedReader br = new BufferedReader(new InputStreamReader(
							(httpConn.getInputStream())));
					String outputResponse = null;
					
					while((outputResponse = br.readLine()) != null){
						iLogger.info("AssetProfileDetails:setAssetMoolDAService:" +
								" Response from {/MoolDA/assetProfile/}is: "+outputResponse+" for VIN:"+"serialNu");
						
					}
					
				}catch(Exception e){
					fLogger.fatal("AssetProfileDetails:setAssetMoolDAService:" +
							" Exception in invoking the {/MoolDA/assetProfile/} URL: "+ e.getMessage()+" for VIN:"+"serialNu");
					throw new Exception("REST URL not available");
				}
			}catch(Exception e){
				fLogger.fatal("AssetProfileDetails:setAssetMoolDAService:Exception Cause: " +e.getMessage());
				String rejectionPoint = "AssetMoolDAService";
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date date = new Date();
				String createdTimeStamp = dateFormat.format(date);
				try(Connection conn = connFactory.getConnection();
						Statement st = conn.createStatement()){
					String insertQ = "INSERT INTO MOOLDA_FaultDetails (SerialNumber, ProfileData, RejectionPoint, Created_TimeStamp)"
							+ " VALUES ('" + assetResp.getSerialNumber() + "','" + finalJsonString + "','" + rejectionPoint + "','" + createdTimeStamp +"')";
					st.execute(insertQ);
					iLogger.info("AssetProfileDetails:setAssetMoolDAService: Succesfully persisted" +
							" the faultDetails for assetId: "+assetResp.getSerialNumber());
				}catch(Exception e1){
					fLogger.fatal("AssetProfileDetails:setAssetMoolDAService:" +
							" Exception in persisting the faults details in " +
							"AssetProfileFaultDetails table "+ e1.getMessage());
				}
			}
			
		}


	public String UpdateCHMRDetails (AssetExtendedRespContract assetResp)throws CustomFault
	{
		Logger bLogger = BusinessErrorLoggerClass.logger;

		if(assetResp.getSerialNumber()==null)
		{
			bLogger.error("VIN is not specified");
			throw new CustomFault("VIN not specified");
		}

		AssetDetailsBO assetBO = new AssetDetailsBO();

		//DF20131108 - Rajani Nagaraju - To update Edge Proxy table on RollOff with status of machine as Normal
		String flag = assetBO.UpdateCHMRDetails(assetResp.getSerialNumber(), assetResp.getOffset(),
				assetResp.getApplication_timestamp(), assetResp.getFirmware_timestamp(),assetResp.getCmhLoginId(),assetResp.getCmhrflag(),assetResp.getPreviousCMHR());


		return flag;

	}

	public String updateClearBacklogTracebilityData (String vinNo,String loginId,String forwardTimeStamp,int sFlag)throws CustomFault
	{
		Logger bLogger = BusinessErrorLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;

		if(vinNo==null)
		{
			bLogger.error("VIN is not specified");
			throw new CustomFault("VIN not specified");
		}

		AssetDetailsBO assetBO = new AssetDetailsBO();
		try
		{

			Timestamp requestTS=new Timestamp(new Date().getTime());
			SimpleDateFormat dformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String d= dformat.format(requestTS);
			
			

			String flag = assetBO.clearBacklogTracebilityData(vinNo,loginId,d,forwardTimeStamp,sFlag);
			iLogger.info("resp : "+flag);
			return flag;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return "FAILURE";
		}


		

	}
}
