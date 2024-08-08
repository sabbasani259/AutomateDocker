//CR337 : 20220721 : Dhiraj K : Property file read.
package com.wipro.mcoreapp.businessobject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Properties;

import org.apache.logging.log4j.Logger;

import remote.wise.EAintegration.handler.MoolErrorMsgHandler;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.CommonUtil;

public class AssetClusterDefinition 
{	
	public String setClusterDefn(String assetId, int segmentId)
	{

		String setClusterDefStatus = "SUCCESS";
		String failureCause ="";
		
		BufferedReader br =null;
		HttpURLConnection conn =null;
		
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		//CR337.sn
		String connIP=null;
		String connPort=null;
		Properties prop = null;
		try{
			prop = CommonUtil.getDepEnvProperties();
			connIP = prop.getProperty("MoolServerIP");
			connPort = prop.getProperty("MoolServerPort");
		}catch(Exception e){
			fLogger.fatal("AssetClusterDefination : setClusterDefn : " +
					"Exception in getting Server Details for Mool Layer from properties file: " +e);
		}
		//CR337.en
		
		try
		{
			if(assetId==null)
			{
				fLogger.fatal("MOOL:EADataPopulation:AssetDetails:MAsset:setAssetDetails for :"+assetId+": Mandatory Parameter assetId is null");
				setClusterDefStatus = "FAILURE";
			}
			
			else
			{
				String output = null;
				// Dhiraj K : 20220630 : Change IP for AWS server
				//String  clusterDefURL = "http://10.179.12.5:26020/MOOL/ClusterDefinitionService/setAssetCluster?assetId="+assetId+"&segmentId="+segmentId; 
				//String  clusterDefURL = "http://10.210.196.206:26020/MOOL/ClusterDefinitionService/setAssetCluster?assetId="+assetId+"&segmentId="+segmentId;
				String  clusterDefURL = "http://" + connIP+":" + connPort + "/MOOL/ClusterDefinitionService/setAssetCluster?assetId="+assetId+"&segmentId="+segmentId;
				
				iLogger.info("MOOL:EADataPopulation:AssetDetails:MAsset:setAssetDetails for :"+assetId);
				
				URL url = new URL(clusterDefURL);
				conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				
				if (conn.getResponseCode() != 200) 
				{
	                fLogger.fatal("MOOL:EADataPopulation:AssetDetails:MAsset:setAssetDetails for :"+assetId+":HTTP Error in invoking REST service:"+conn.getResponseMessage()+"" +
	                		"; Response Code:"+conn.getResponseCode());
	                setClusterDefStatus="FAILURE";
	                failureCause = conn.getResponseMessage();
	                
				}
				else
				{		
					br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
	         
					while ((output = br.readLine()) != null) 
						setClusterDefStatus = output;
				}
			}   
						
		}
		
		catch(MalformedURLException e1 )
		{
			e1.printStackTrace();
			setClusterDefStatus="FAILURE";
			fLogger.fatal("MOOL:EADataPopulation:AssetDetails:MAsset:setAssetDetails for :"+assetId+":Malformed URL Exception:"+e1.getMessage());
			failureCause = e1.getMessage();
		}
		
		catch(IOException e2)
		{
			e2.printStackTrace();
			setClusterDefStatus="FAILURE";
			fLogger.fatal("MOOL:EADataPopulation:AssetDetails:MAsset:setAssetDetails for :"+assetId+":IOException:"+e2.getMessage());
			failureCause = e2.getMessage();
		}
		
		catch(Exception e3)
		{
			e3.printStackTrace();
			setClusterDefStatus="FAILURE";
			fLogger.fatal("MOOL:EADataPopulation:AssetDetails:MAsset:setAssetDetails for :"+assetId+":Exception:"+e3.getMessage());
			failureCause = e3.getMessage();
		}
		
		finally
		{
			try 
			{
				if(br!=null)
					br.close();
				
				if(conn!=null)
					conn.disconnect();
					
			} 
			catch (IOException e) 
			{
				fLogger.fatal("MOOL:EADataPopulation:AssetDetails:MAsset:setAssetDetails for :"+assetId+":Error in closing the BufferedReader:"+e.getMessage());
			}
			
		}
		
		
		//----------------------------- If any exception is thrown / Failure in persisting the data in MOOL, store the details in mool_fault_details table
		//for later reprocessing of the same
		if(setClusterDefStatus.equalsIgnoreCase("FAILURE"))
		{
			fLogger.fatal("MOOL:EADataPopulation:AssetDetails:MAsset:setAssetDetails for :"+assetId+":FAILURE in invoking MOOL URL for setting cluster definition");
		}
		
		return setClusterDefStatus;
	
	}
}
