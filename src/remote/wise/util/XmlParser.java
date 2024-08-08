package remote.wise.util;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

//import org.apache.log4j.Logger;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;

/** Parse the XML using DOM parser
 * @author Rajani Nagaraju
 *
 */
public class XmlParser implements Serializable
{
	HashMap<String, HashMap<String,String>> paramType_parametersMap = new  HashMap<String, HashMap<String,String>>();
	String serialNumber;
	Timestamp transactionTime;
	String version;
	String messageId;
	String fwVersionNumber;
	
	
	public String getFwVersionNumber() {
		return fwVersionNumber;
	}
	public void setFwVersionNumber(String fwVersionNumber) {
		this.fwVersionNumber = fwVersionNumber;
	}
	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}
	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}
	
	/**
	 * @return the messageId
	 */
	public String getMessageId() {
		return messageId;
	}
	/**
	 * @param messageId the messageId to set
	 */
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	
	
	/**
	 * @return HashMap<ParameterType, HashMap<Parameter,Value>>
	 */
	public HashMap<String, HashMap<String, String>> getParamType_parametersMap() {
		return paramType_parametersMap;
	}
	/**
	 * @param paramType_parametersMap HashMap with ParameterType as key and HashMap<Parameter,Value>> as value
	 */
	public void setParamType_parametersMap(
			HashMap<String, HashMap<String, String>> paramType_parametersMap) {
		this.paramType_parametersMap = paramType_parametersMap;
	}


	/**
	 * @return serialNumber as String
	 */
	public String getSerialNumber() {
		return serialNumber;
	}
	/**
	 * @param serialNumber VIN as Stirng input
	 */
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	/**
	 * @return the transactionTime
	 */
	public Timestamp getTransactionTime() {
		return transactionTime;
	}
	/**
	 * @param transactionTime the transactionTime to set
	 */
	public void setTransactionTime(Timestamp transactionTime) {
		this.transactionTime = transactionTime;
	}
	
	
	/** This method parse the given XML using DOM parser
	 * @param xmlInputString XML as input String
	 * @return Returns the parsed data
	 * @throws UnsupportedEncodingException
	 */
	public XmlParser parseXml(String xmlInputString) 
	{
		InputSource xmlInput = null;
		//Logger fatalError = Logger.getLogger("fatalErrorLogger");
		Logger fatalError = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		
		try 
		{
			xmlInput = new InputSource(new ByteArrayInputStream(xmlInputString.getBytes("utf-8")));
		} 
		catch (Exception e1)
		{
			fatalError.fatal("Exception :"+e1);
		}
		
		String messageVersion = null;
		String snapshotTime = null;
		String equipmentHeader = null;
		String serialNumber = null;
		String nonAEMPparameters = null;
		String msgId = null;
		String fotaVersionNumber=null;
				
		Properties prop = new Properties();
		try
		{
			prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
			messageVersion= prop.getProperty("MessageVersion");
			snapshotTime= prop.getProperty("SnapshotTime");
			equipmentHeader= prop.getProperty("EquipmentHeader");
			serialNumber = prop.getProperty("SerialNumber");
			nonAEMPparameters = prop.getProperty("Non_AEMP_Parameters");
			msgId = prop.getProperty("MessageId");
			fotaVersionNumber = prop.getProperty("REC_FWVersionNumber");
						
		}
		catch(Exception e)
		{
			fatalError.fatal("Exception :"+e);
		}
		
		
		try
		{
			//Using factory get an instance of document builder
			DocumentBuilderFactory buildFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = buildFactory.newDocumentBuilder();
			
			//parse the XML 
			Document document = documentBuilder.parse (xmlInput);
			
			
			//document.getDocumentElement () - returns the root node - Message
			Element rootNode =document.getDocumentElement();
			rootNode.normalize();
			
			//get the XML version
			version=rootNode.getAttribute(messageVersion);
						
			//get the message received timestamp
			String messageReceivedTime = rootNode.getAttribute(snapshotTime);
			messageReceivedTime = messageReceivedTime.replace("T", " ");
			messageReceivedTime = messageReceivedTime.replace("Z", " ");
			//convert string date to Timestamp
			//DefectID: 1394 - Rajani Nagaraju - 20131003 - HH corresponds to 24 hour format and earlier it was hh which is 12hr am/pm format
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date msgReceivedDate = dateFormat.parse(messageReceivedTime);
			transactionTime = new Timestamp(msgReceivedDate.getTime());
			
			
           
						
			
			//Iterate Child nodes and obtain Parameter Types, Parameters and their corresponding values
			NodeList childNodes = rootNode.getChildNodes();
			int totalChildNodes = childNodes.getLength();
		
			
			for(int i=1; i<totalChildNodes; i=i+2)
			{
				Node childNode = childNodes.item(i);
				Element childElement = (Element)childNode;
				
				
				if(childNode.getNodeName().equalsIgnoreCase(equipmentHeader))
				{
					NodeList equipmentHeaderChild = childNode.getChildNodes();
					
					for(int j=0; j<equipmentHeaderChild.getLength(); j++)
					{
						Node headerChild = equipmentHeaderChild.item(j);
						
						if( (headerChild.getNodeName().equalsIgnoreCase(serialNumber)))
						{
							this.serialNumber = headerChild.getTextContent();
							
						}
					}
				}
				
				else
				{
					String parameterType = childNode.getNodeName();
					
					//get the corresponding parameters and their values
					HashMap<String, String> parameterValueMap = new HashMap<String, String>();
					if(parameterType.equalsIgnoreCase(nonAEMPparameters))
					{
						NodeList nonAEMPparamTypes = childNode.getChildNodes();
						
						for(int m=1; m<nonAEMPparamTypes.getLength(); m=m+2 )
						{
							Node normalParameterType = nonAEMPparamTypes.item(m);
							
							//DefectID: DF20131030 - Rajani Nagaraju - Tow away alert changes implementation - Based on new parameter GPS Fix
							//Temporary Fix. This parameter has to be included under FirmwareParameters (Parameter Type). As a temporary fix this is hard-coded
							if(normalParameterType.getNodeName().equalsIgnoreCase("REC_GPSFix"))
							{
								String gpsFixValue = normalParameterType.getTextContent();
								parameterValueMap.put(normalParameterType.getNodeName().substring(4),gpsFixValue);
								paramType_parametersMap.put(normalParameterType.getNodeName().substring(4), parameterValueMap);
								continue;
							}
//							Keerthi : 12/02/14 : extracting fota version number
							if(normalParameterType.getNodeName().equalsIgnoreCase(fotaVersionNumber))
							{
								this.fwVersionNumber = normalParameterType.getTextContent();
								continue;
							}
							
							if(normalParameterType.getNodeName().equalsIgnoreCase(msgId))
							{
								messageId = normalParameterType.getTextContent();
								continue;
							}
							
							else
							{
								NodeList nonAEMPparamList = normalParameterType.getChildNodes();
								parameterValueMap = new HashMap<String, String>();
								
								for(int n=1; n<nonAEMPparamList.getLength(); n=n+2)
								{
									
									Node nonAEMPparams = nonAEMPparamList.item(n);
									
									String nonAEMPparamName;
									if(normalParameterType.getNodeName().contains("Parameter"))
										nonAEMPparamName = nonAEMPparams.getNodeName().substring(4);
									else
										nonAEMPparamName = nonAEMPparams.getNodeName();
									
									if( (nonAEMPparamList.item(n+2) != null ) && (nonAEMPparamList.item(n+2).getNodeName().equalsIgnoreCase(nonAEMPparamName+"Units")))
									{
										if(nonAEMPparams.getChildNodes().getLength()>3)
										{
											
											NodeList furtherChildNodes = nonAEMPparams.getChildNodes();
																						
											for(int r=1; r<furtherChildNodes.getLength(); r=r+2)
											{
												String paramVal = null;
												
												Node actualChildNode = furtherChildNodes.item(r);
												
												if(actualChildNode.getChildNodes().getLength()>3)
												{
													NodeList finalChildNode = actualChildNode.getChildNodes();
													
													for(int q=1; q<finalChildNode.getLength(); q=q+2)
													{
														if(paramVal!=null)
															paramVal = paramVal + "-" +finalChildNode.item(q).getTextContent();
														else
															paramVal = finalChildNode.item(q).getTextContent();
													}
													
													parameterValueMap.put(actualChildNode.getNodeName(), paramVal + " unit "+ nonAEMPparamList.item(n+2).getTextContent());
												}
												
												else
												{
													parameterValueMap.put(actualChildNode.getNodeName(), actualChildNode.getChildNodes().item(1) + " unit "+ nonAEMPparamList.item(n+2).getTextContent());
												}
												
											}
												
											n= n+2;
											continue;
										}
										
										else
										{
											
											parameterValueMap.put(nonAEMPparamName, nonAEMPparams.getChildNodes().item(1).getTextContent() +" unit "+ nonAEMPparamList.item(n+2).getTextContent());
											n= n+2;
											continue;
										}
									}
									
									else
									{
										parameterValueMap.put(nonAEMPparamName,nonAEMPparams.getTextContent());
									}
									
									
									paramType_parametersMap.put(normalParameterType.getNodeName(), parameterValueMap);
								}
								
							}
							
						}
						
					}
										
					
					else
					{
						
						NodeList parameters = childNode.getChildNodes();
						String previousParamName = null;
						String upperCombinedContent = null;
						String lastNodeName = null;
						int combine=0;
					
						for(int k=1; k<parameters.getLength(); k=k+2)
						{
							Node monitoringParameter = parameters.item(k);
						
							String parameterName = monitoringParameter.getNodeName();
							String parameterValue;
						
																				
							if(monitoringParameter.getChildNodes().getLength()>1)
							{
								int childLength = monitoringParameter.getChildNodes().getLength();
								
								String previousValue = null;
								String combinedContent = null;
								String previosNodeName = null;
								int innerCombine =0;
						
								//for the scenarios like feet inches
								for(int g=1; g<childLength; g= g+2)
								{
									Node child = monitoringParameter.getChildNodes().item(g);
									
																		
									if(child.getNodeName().toUpperCase().contains("TIME"))
									{
										if( (g+2) <childLength)
											continue;
										else
											innerCombine=0;
									}
									
									if(child.getNodeName().toUpperCase().contains("UNIT"))
									{
										previousValue = " unit "+child.getTextContent();
										innerCombine =1;
									}
								
									else if (innerCombine==1)
									{
										if( (g+2) <childLength)
										{
											if(combinedContent!=null)
												combinedContent = combinedContent + "-" +child.getTextContent();
											else
												combinedContent = child.getTextContent();
											previosNodeName = monitoringParameter.getNodeName();
										}
										
										else
										{
																				
											if(combinedContent!=null)
												combinedContent = combinedContent + "-" +child.getTextContent();
											else
												combinedContent = child.getTextContent();
											
											previosNodeName = monitoringParameter.getNodeName();
											parameterValueMap.put(previosNodeName,combinedContent+previousValue);
											
										}
									}
									
									else
									{
										if (! (child.getNodeName().toUpperCase().contains("TIME")))
											parameterValueMap.put(monitoringParameter.getNodeName(),child.getTextContent());
										else
											parameterValueMap.put(previosNodeName,combinedContent+previousValue);
									}
								}
								
								
							}
							
							else
							{
								if(monitoringParameter.getNodeName().toUpperCase().contains("TIME"))
								{
									if( (k+2) <parameters.getLength())
										continue;
									else
										combine=0;
								}
								
								if(monitoringParameter.getNodeName().toUpperCase().contains("UNIT"))
								{
									previousParamName =  " unit "+monitoringParameter.getTextContent();
									combine = 1;
								}
								
								else if(combine ==1)
								{
									if( (k+2) <parameters.getLength())
									{
										if(upperCombinedContent!=null)
											upperCombinedContent = upperCombinedContent +"-"+monitoringParameter.getTextContent();
										else
											upperCombinedContent = monitoringParameter.getTextContent();
										lastNodeName = monitoringParameter.getNodeName();
									}
									else
									{
										if(upperCombinedContent!=null)
											upperCombinedContent = upperCombinedContent +"-"+monitoringParameter.getTextContent();
										else
											upperCombinedContent = monitoringParameter.getTextContent();
																			
										lastNodeName = monitoringParameter.getNodeName();
										parameterValueMap.put(lastNodeName,upperCombinedContent+previousParamName);
									}
									
								}
								
								else
								{
									if(! (monitoringParameter.getNodeName().toUpperCase().contains("TIME")))
										parameterValueMap.put(monitoringParameter.getNodeName(),monitoringParameter.getTextContent());
									else
										parameterValueMap.put(lastNodeName,upperCombinedContent+previousParamName);
								}
							}
																	
					
						}
						
						paramType_parametersMap.put(parameterType, parameterValueMap);
					}
					
				}
				
			}
			
			
				//Df20160315 @Roopa Fix for one day date offset- FW Issue- But interium fix from the server side
				
			       //start transactionTime+1 logic for leap year date greater than Feb29
					
					if(fwVersionNumber!=null && (fwVersionNumber.equalsIgnoreCase("07.09.08") || fwVersionNumber.equalsIgnoreCase("7.9.8** ") || fwVersionNumber.equalsIgnoreCase("09.01.00") || fwVersionNumber.equalsIgnoreCase("7.9.8")))
					{
						iLogger.info(transactionTime+" calling getConvertedTSforLeapYear for the Received fwVersion :"+fwVersionNumber);
						Timestamp packetTimestamp = transactionTime;
						
						transactionTime=getConvertedTSforLeapYear(transactionTime,msgReceivedDate);
						
						iLogger.info(serialNumber+":"+packetTimestamp+" : received fwVersion :"+fwVersionNumber+"|converted timestaamp:"+transactionTime);
						
						
						
					}
					
				//end transactionTime+1 logic for leap year date greater than Feb29
			
					
		}
		
		catch(Exception e)
		{
			fatalError.fatal("Exception :"+e);
		}
	
		return this;
	
	}
	
public Timestamp getConvertedTSforLeapYear(Timestamp temptransactionTime, Date receivedDate){
		
		Timestamp covertedTimestamp = temptransactionTime;
		
		Logger iLogger = InfoLoggerClass.logger;
		
        iLogger.info("Received transactionTime :"+temptransactionTime);
		
		
		int receivedYear = Integer.parseInt(String.valueOf(temptransactionTime).substring(0, 4));
		
		iLogger.info("receivedYear :"+receivedYear);
		
		 if((receivedYear % 400 == 0) || ((receivedYear % 4 == 0) && (receivedYear % 100 != 0))){
			 iLogger.info("Year " + receivedYear + " is a leap year");
			 
			 int month= Integer.parseInt(String.valueOf(temptransactionTime).substring(5, 7));
			 iLogger.info("receivedMonth :"+month);
			 
			 int day=Integer.parseInt(String.valueOf(temptransactionTime).substring(8, 10));
			 
			 iLogger.info("received day :"+day);
			 
			 if((month==2 && day>29) || month>2){
				 
				
					Calendar c = Calendar.getInstance(); 
					c.setTime(receivedDate); 
					c.add(Calendar.DATE, 1);
					receivedDate = c.getTime();

					covertedTimestamp = new Timestamp(receivedDate.getTime());
					
					
			 }
			 
			 
			 
		 }
			 
        else{
        	iLogger.info("Year " + receivedYear + " is not a leap year");	
        
        }
		
		
		
	return covertedTimestamp;	
	}
	
	
}
