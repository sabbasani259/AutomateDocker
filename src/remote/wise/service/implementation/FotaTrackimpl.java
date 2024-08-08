package remote.wise.service.implementation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.ConnectMySQL;

public class FotaTrackimpl {

	public HashMap<String,String> getFotatrackdetails(String vin){
		HashMap<String,String> trackList=new HashMap<String,String>();
		Logger infoLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		Connection prodConnection = null;
		Statement statement = null;
		ResultSet rs=null;
		try{
			ConnectMySQL connMySql = new ConnectMySQL();
			//For local
			//prodConnection = connMySql.getEdgeProxyNewConnection_local();
			//For remote
			prodConnection = connMySql.getEdgeProxyNewConnection();
			statement = prodConnection.createStatement();
			String query="";
			if(vin.length()>7)
			{
				query = "select vin_no,imei_number,ep_timeStamp,device_version,device_MIP_version,device_NIP_version,chunks_downloaded,total_chunks,fota_received_timestamp,last_updated,version_downloading,fota_final_timestamp,integer_resultcode from Fota_Track where vin_no=\'"
						+ vin+"\';";
			}
			else{
				query="select vin_no,imei_number,ep_timeStamp,device_version,device_MIP_version,device_NIP_version,chunks_downloaded,total_chunks,fota_received_timestamp,last_updated,version_downloading,fota_final_timestamp,integer_resultcode from Fota_Track where vin_no like \'%"
						+ vin+"%\';";
			}
			infoLogger.info("Webservice trackFota/getFotaTrackDetails query :"+query);
			System.out.println("Webservice trackFota/getFotaTrackDetails query :"+query);
			rs=statement.executeQuery(query);
			String imei = "NA", ep_timeStamp = "NA", device_version = "NA", chunks_downloaded = "NA", total_chunks = "NA", fota_received_timestamp = "NA";
			String last_updated="NA",version_downloading="NA",fota_final_timestamp="NA", device_MIP_version="NA",device_NIP_version="NA";
			while(rs.next())
			{	
				trackList.put("vin", rs.getString("vin_no"));
				if(rs.getString("imei_number")!=null)
					imei=rs.getString("imei_number");
				if(rs.getString("ep_timeStamp")!=null)
					ep_timeStamp=rs.getString("ep_timeStamp");
				if(rs.getString("device_version")!=null)
					device_version=rs.getString("device_version");
				if(rs.getString("chunks_downloaded")!=null)
					chunks_downloaded=rs.getString("chunks_downloaded");
				if(rs.getString("total_chunks")!=null)
					total_chunks=rs.getString("total_chunks");
				if(rs.getString("fota_received_timestamp")!=null)
					fota_received_timestamp=rs.getString("fota_received_timestamp");
				if(rs.getString("last_updated")!=null)
					last_updated=rs.getString("last_updated");
				if(rs.getString("version_downloading")!=null)
					version_downloading=rs.getString("version_downloading");
				if( rs.getString("fota_final_timestamp")!=null)
					fota_final_timestamp= rs.getString("fota_final_timestamp");
				if(rs.getString("device_MIP_version") != null){
					device_MIP_version = rs.getString("device_MIP_version");
				}
				if(rs.getString("device_NIP_version") != null){
					device_NIP_version = rs.getString("device_NIP_version");
				}
				trackList.put("imei_number", imei);
				trackList.put("ep_timeStamp", ep_timeStamp);
				trackList.put("device_version", device_version);
				trackList.put("chunks_downloaded", chunks_downloaded);
				trackList.put("total_chunks", total_chunks);
				trackList.put("fota_received_timestamp", fota_received_timestamp);
				trackList.put("last_updated", last_updated);
				trackList.put("version_downloading", version_downloading);
				trackList.put("fota_final_timestamp", fota_final_timestamp);
				trackList.put("device_MIP_version", device_MIP_version);
				trackList.put("device_NIP_version", device_NIP_version);
				String status=rs.getString("integer_resultcode");
				int statuscode=0;
				if(status!=null && !status.isEmpty()){
				statuscode=Integer.parseInt(status,16);
				}
				infoLogger.info("Webservice trackFota/getFotaTrackDetails Resultcode : "+status+" Decimal value :"+statuscode);
				if(status!=null && !status.isEmpty()){
					switch(statuscode)
					{
					case 0:
					{
						trackList.put("status","Invalid. The first request.");break;
					}
					case 200:
					{
						trackList.put("status","Successful");break;
					}
					case 202:
					{
						trackList.put("status","Successful. Will be processed later.");
						break;
					}
					case 400:
					{
						trackList.put("status","Management Client error. based on User or Device behavior");break;
					}
					case 401:
					{
						trackList.put("status","User chose not to accept the operation when prompted");break;
					}
					case 402:
					{
						trackList.put("status","Corrupted firmware update package, did not store correctly.  Detected, for example, by mismatched CRCs between actual and expected.");
						break;}
					case 403:
					{
						trackList.put("status","Firmware Update Package – Device Mismatch");break;
					}
					case 405:
					{
						trackList.put("status","Firmware Update Package Not Acceptable");break;
					}
					case 406:
					{
						trackList.put("status","Alternate Download  Authentication Failure");break;
					}
					case 407:
					{
						trackList.put("status","Alternate Download  Request Time out");break;
					}
					case 408:
					{
						trackList.put("status","The device does not support the requested operation.");break;
					}
					case 409:
					{
						trackList.put("status","Undefined Error");break;
					}
					case 410:
					{
						trackList.put("status","Firmware Update Failed");break;
					}
					case 411:
					{
						trackList.put("status","Malformed or Bad URL");break;
					}
					case 412:
					{
						trackList.put("status","Alternate Download Server Unavailable");break;
					}
					case 500:
					{
						trackList.put("status","Alternate Download Server Error");break;
					}
					case 501:
					{
						trackList.put("status","Download fails due to device is out of memory");break;
					}
					case 502:
					{
						trackList.put("status","Firmware update fails due to device out of memory");break;
					}
					case 503:
					{
						trackList.put("status","Download fails due to network issues");break;
					}
					default :
					{
						if(statuscode>=250 && statuscode<=299)
						{
							trackList.put("status","Successful. Vendor Specified");
						}
						else if(statuscode>=450 && statuscode<=499)
						{
							trackList.put("status","Client Error. Vendor Specified");
						}
						else if(statuscode>=550 && statuscode<=599)
						{
							trackList.put("status","Alternate Download Server Error. Vendor Specified");
						}
						else{
							trackList.put("status","Unknown resultCode");
						}
						
					}
					}
				}
				else
				{
					trackList.put("status","NA");	
				}				
			}

		}catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("Exception occured in FotaTrackimpl :"+e.getMessage());
			//System.out.println("Exception "+e.getMessage());
		}finally{
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
		return trackList;
	}
}
