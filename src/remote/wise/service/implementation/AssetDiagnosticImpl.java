package remote.wise.service.implementation;

import java.io.UnsupportedEncodingException;
import org.w3c.dom.Document;
import org.apache.xml.serialize.OutputFormat;

import java.io.File;
import java.io.PrintWriter;
import java.io.Writer;
import java.io.StringWriter;
import java.io.StringReader;
import org.apache.xml.serialize.XMLSerializer;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import remote.wise.businessentity.AssetEntity;
import remote.wise.businessentity.AssetExtendedDetailsEntity;
import remote.wise.businessentity.AssetMonitoringDetailEntity;
import remote.wise.businessentity.AssetMonitoringHeaderEntity;
import remote.wise.businessentity.AssetMonitoringSnapshotEntity;
import remote.wise.businessentity.MonitoringParameters;
import remote.wise.businessentity.RejectedPacketDetailsEntity;
import remote.wise.businessobject.AssetDetailsBO;
import remote.wise.businessobject.AssetMonitoringDetailsBO;
import remote.wise.exception.CustomFault;
import remote.wise.handler.EmailHandler;
import remote.wise.handler.EmailTemplate;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
import remote.wise.util.Geofencing;
import remote.wise.util.HibernateUtil;
import remote.wise.util.IstGmtTimeConversion;
import remote.wise.util.StaticProperties;
//import remote.wise.util.WiseLogger;
import remote.wise.util.XmlParser;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.exception.GenericJDBCException;
import org.hibernate.exception.JDBCConnectionException;

public class AssetDiagnosticImpl implements Callable, Runnable
{
	//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
	/*public static WiseLogger businessError = WiseLogger.getLogger("AssetDiagnosticImpl:","businessError");
	public static WiseLogger fatalError = WiseLogger.getLogger("AssetDiagnosticImpl:","fatalError");
	public static WiseLogger rejectedPacketsInfo = WiseLogger.getLogger("AssetDiagnosticImpl:","rejectedPackets");
	public static WiseLogger infoLogger = WiseLogger.getLogger("AssetDiagnosticImpl:","info");*/
	
	//DF20140218 - Rajani Nagaraju - To process the XML data packets from Unprocessed folder where the XMLs are dropped from edge proxy, since
	// the edge proxy is modified to drop the files into a folder rather than calling the Webservice directly - This change is to avoid the loss of data packets if the WISE service is down
	public String inputXmlPacket = null;
	public XmlParser parser;
	public String alertGenerationStatus="FAILURE";
	
	@Override
	public Object call() throws Exception 
	{
		// TODO Auto-generated method stub
		Logger iLogger = InfoLoggerClass.logger;
		String serialNumber = StringUtils.substringBetween(this.inputXmlPacket, "<SerialNumber>", "</SerialNumber>").substring(3);
		String transactionTime = StringUtils.substringBetween(this.inputXmlPacket, "SnapshotTime=\"", "\"").replace("T", " ").replace("Z", "");
		
		Calendar cal = Calendar.getInstance();
		String startDate = StaticProperties.dateTimeSecFormat.format(cal.getTime());
		long startTime = System.currentTimeMillis();
		iLogger.info(serialNumber+":"+transactionTime+":"+" ADSImpl Processing StartTime: "+startDate);
		
		String status =null;
		status = setAssetMonitoringDataNew(this.inputXmlPacket);
		
		Calendar cal1 = Calendar.getInstance();
		String endDate = StaticProperties.dateTimeSecFormat.format(cal1.getTime());
		long endTime=System.currentTimeMillis();
		iLogger.info(serialNumber+":"+transactionTime+":"+" ADS Processing EndTime: "+endDate);
		iLogger.info(serialNumber+":"+transactionTime+":"+"Webservice Execution Time in ms:"+(endTime-startTime));
		iLogger.info(serialNumber+":"+transactionTime+":"+" Webservice Output Status: "+status);
		
		return status;
	}
	
	/** This method sets the diagnostic data received from the device 
	 * DefectID 955: Added by Rajani Nagaraju - 20130715
	 * DefectId: - 1435 - Rajani Nagaraju - 20131010 - Update rejected packets logger if the packet is rejected for any reason
	 * @param xmlInput Diagnostic details received as XML input
	 * @return Returns the Status String
	 * @throws CustomFault
	  */
	public String setAssetMonitoringData(String xmlInput)
	{

		Logger bLogger = BusinessErrorLoggerClass.logger;
    	Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
    	Logger rLogger = RejectedPktLoggerClass.logger;
		AssetMonitoringDetailsBO assetMonitoringDetails = new AssetMonitoringDetailsBO();

		AssetDiagnosticImpl implObj = new AssetDiagnosticImpl();

		//DefectID: 20140318 - Rajani Nagaraju - In order to avoid data loss on Deployment Errors - Get the FileName
		//------------------- get the required folder paths from properties file
		String DataPackets_UnprocessedFolderPath=null;
		String DataPackets_ProcessedFolderPath=null;
		String DataPackets_InProcessFolderPath = null;

		try
		{
			//	Properties prop1 = new Properties();
			//prop1.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));

			//			DataPackets_UnprocessedFolderPath = prop1.getProperty("DataPackets_UnprocessedFolderPath");
			//			DataPackets_ProcessedFolderPath = prop1.getProperty("DataPackets_ProcessedFolderPath");
			//			//DefectID: 20140408 - Rajani Nagaraju - Introducing the Intermediate Folder Path to avoid re-processing the same file from different threads
			//			DataPackets_InProcessFolderPath = prop1.getProperty("DataPackets_InProcessFolderPath");

			/*	if (prop1.getProperty("deployenvironment").equalsIgnoreCase("SIT")) {
    			DataPackets_UnprocessedFolderPath = prop1.getProperty("DataPackets_UnprocessedFolderPath_SIT");
    			DataPackets_ProcessedFolderPath = prop1.getProperty("DataPackets_ProcessedFolderPath_SIT");
    			DataPackets_InProcessFolderPath = prop1.getProperty("DataPackets_InProcessFolderPath_SIT");
			} else if (prop1.getProperty("deployenvironment").equalsIgnoreCase("DEV")) {
				DataPackets_UnprocessedFolderPath = prop1.getProperty("DataPackets_UnprocessedFolderPath_DEV");
    			DataPackets_ProcessedFolderPath = prop1.getProperty("DataPackets_ProcessedFolderPath_DEV");
    			DataPackets_InProcessFolderPath = prop1.getProperty("DataPackets_InProcessFolderPath_DEV");
			} else if (prop1.getProperty("deployenvironment").equalsIgnoreCase("QA")) {
				DataPackets_UnprocessedFolderPath = prop1.getProperty("DataPackets_UnprocessedFolderPath_QA");
    			DataPackets_ProcessedFolderPath = prop1.getProperty("DataPackets_ProcessedFolderPath_QA");
    			DataPackets_InProcessFolderPath = prop1.getProperty("DataPackets_InProcessFolderPath_QA");
			} else if (prop1.getProperty("deployenvironment").equalsIgnoreCase("PROD")) {
				DataPackets_UnprocessedFolderPath = prop1.getProperty("DataPackets_UnprocessedFolderPath_PROD");
    			DataPackets_ProcessedFolderPath = prop1.getProperty("DataPackets_ProcessedFolderPath_PROD");
    			DataPackets_InProcessFolderPath = prop1.getProperty("DataPackets_InProcessFolderPath_PROD");
			} else {
				DataPackets_UnprocessedFolderPath = prop1.getProperty("DataPackets_UnprocessedFolderPath");
    			DataPackets_ProcessedFolderPath = prop1.getProperty("DataPackets_ProcessedFolderPath");
    			DataPackets_InProcessFolderPath = prop1.getProperty("DataPackets_InProcessFolderPath");
			}
			 */

			DataPackets_UnprocessedFolderPath ="/user/JCBLiveLink/EdgeProxy/data/M2M/conf/WISE_Unprocessed_XMLs";
			DataPackets_ProcessedFolderPath = "/user/JCBLiveLink/EdgeProxy/data/M2M/conf/AEMP_OUTPUT";
			DataPackets_InProcessFolderPath ="/user/JCBLiveLink/EdgeProxy/data/M2M/conf/InProcess_XMLs";
		}

		catch(Exception e)
		{
			fLogger.fatal("Exception :"+e);

			Writer result = new StringWriter();
			PrintWriter printWriter = new PrintWriter(result);
			e.printStackTrace(printWriter);
			String err = result.toString();
			fLogger.fatal("Exception trace: "+err);
			try 
			{
				printWriter.close();
				result.close();
			} 

			catch (IOException e1) 
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}


			return "FAILURE";
		}

		String[] inputSplitString = xmlInput.split("\\|", 2);
		if(inputSplitString.length<2)
		{
			fLogger.fatal("AssetDiagnosticInput not properly created - In the format FileName|xmlInputString");
			return "FAILURE";
		}


		xmlInput = inputSplitString[1];
		String processingFileName = inputSplitString[0];
		int lastTransNumber = 0;
		//DefectID: 20140408 - Rajani Nagaraju - Introducing the Intermediate Folder Path to avoid re-processing the same file from different threads
		//File processingFile = new File(DataPackets_UnprocessedFolderPath,processingFileName);
		File processingFile = new File(DataPackets_InProcessFolderPath,processingFileName);
		File archivedFile = null;
		XmlParser parser =null;

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		Timestamp currentTime = new Timestamp(new Date().getTime());


		try
		{


			//Format the received input XML
			String xmlFormattedString = implObj.format(xmlInput);
			if(xmlFormattedString.equalsIgnoreCase("FAILURE"))
			{
				//DefectId: - 1435 - Rajani Nagaraju - 20131010 - Update rejected packets logger if the packet is rejected for any reason
				//DefectID: DF20131230 - Rajani Nagaraju - Fixing Robustness of Loggers. RejectedPackets Log level changed from 'DEBUG' to 'WARN'
				rLogger.warn("Invalid XML packet format");
				rLogger.warn(xmlInput);

				//DF20141117 - Rajani Nagaraju - Log Rejected Record into DB
				RejectedPacketDetailsEntity rejectedPkt = new RejectedPacketDetailsEntity();
				rejectedPkt.setCreatedTime(currentTime);
				rejectedPkt.setRejectionCause("Invalid XML packet format: "+xmlInput);
				//DefectID:20141121 @ session closed exception
				if(! (session.isOpen() ))
				{
					session = HibernateUtil.getSessionFactory().getCurrentSession();
					session.getTransaction().begin();
				}
				session.save(rejectedPkt);

				//DefectID: 20140318 - Rajani Nagaraju - In order to avoid data loss on Deployment Errors - If the packet is rejected, move the data packet from Unprocessed_Xml to AEMP_Output folder
				archivedFile = new File(DataPackets_ProcessedFolderPath,processingFileName);

				if(archivedFile.exists())
					archivedFile.delete();
				boolean moveStatus = processingFile.renameTo(archivedFile);

				if(moveStatus)
				{
					iLogger.info("Data Packet is Rejected - Invalid XML packet format");
					iLogger.info("File "+processingFile.getName()+" is moved successfully from processing to archived folder");
				}

				return "FAILURE";
			}


			//parser the XML and obtain the hashmap of parameterTypes, parameter and their corresponding values
			XmlParser xmlParser = new XmlParser();
			iLogger.info("Before XML parsing" + processingFileName);
			parser = xmlParser.parseXml(xmlFormattedString);
			iLogger.info("After XML parsing" + processingFileName);
		}

		catch(Exception e)
		{
			fLogger.fatal("Error in Parsing XML : Exception :"+e);
			e.printStackTrace();

			Writer result = new StringWriter();
			PrintWriter printWriter = new PrintWriter(result);
			e.printStackTrace(printWriter);
			String err = result.toString();
			fLogger.fatal("Exception trace: "+err);
			try 
			{
				printWriter.close();
				result.close();
			} 

			catch (IOException e1) 
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		try
		{
			//check for the existence of Serial number

			if(parser.getSerialNumber()==null)
			{
				//DefectId: - 1435 - Rajani Nagaraju - 20131010 - Update rejected packets logger if the packet is rejected for any reason
				//DefectID: DF20131230 - Rajani Nagaraju - Fixing Robustness of Loggers. RejectedPackets Log level changed from 'DEBUG' to 'WARN'
				rLogger.warn("Packet with no VIN");
				rLogger.warn(xmlInput);

				//DF20141117 - Rajani Nagaraju - Log Rejected Record into DB
				RejectedPacketDetailsEntity rejectedPkt = new RejectedPacketDetailsEntity();
				rejectedPkt.setCreatedTime(currentTime);
				rejectedPkt.setRejectionCause("Packet with no VIN: "+xmlInput);
				//DefectID:20141121 @ session closed exception
				if(! (session.isOpen() ))
				{
					session = HibernateUtil.getSessionFactory().getCurrentSession();
					session.getTransaction().begin();
				}
				session.save(rejectedPkt);

				//DefectID: 20140318 - Rajani Nagaraju - In order to avoid data loss on Deployment Errors - If the packet is rejected, move the data packet from Unprocessed_Xml to AEMP_Output folder
				archivedFile = new File(DataPackets_ProcessedFolderPath,processingFileName);

				if(archivedFile.exists())
					archivedFile.delete();
				boolean moveStatus = processingFile.renameTo(archivedFile);

				if(moveStatus)
				{
					iLogger.info("Data Packet is Rejected - Packet with no VIN");
					iLogger.info("File "+processingFile.getName()+" is moved successfully from processing to archived folder");
				}

				return "FAILURE";

			}

			//check for transaction time
			//Logger rejectedPacketsInfo = Logger.getLogger("rejectedPacketsInfoLogger");
			if(parser.getTransactionTime()==null)
			{
				//Added by Rajani 20130626, to log the XML input record when the packet is rejected from Application
				//DefectID: DF20131230 - Rajani Nagaraju - Fixing Robustness of Loggers. RejectedPackets Log level changed from 'DEBUG' to 'WARN'
				rLogger.warn("Packet with Snapshot time NULL");
				rLogger.warn(xmlInput);

				//DF20141117 - Rajani Nagaraju - Log Rejected Record into DB
				RejectedPacketDetailsEntity rejectedPkt = new RejectedPacketDetailsEntity();
				rejectedPkt.setCreatedTime(currentTime);
				rejectedPkt.setRejectionCause("Packet with Snapshot time NULL: "+xmlInput);
				//DefectID:20141121 @ session closed exception
				if(! (session.isOpen() ))
				{
					session = HibernateUtil.getSessionFactory().getCurrentSession();
					session.getTransaction().begin();
				}
				session.save(rejectedPkt);


				//DefectID: 20140318 - Rajani Nagaraju - In order to avoid data loss on Deployment Errors - If the packet is rejected, move the data packet from Unprocessed_Xml to AEMP_Output folder
				archivedFile = new File(DataPackets_ProcessedFolderPath,processingFileName);

				if(archivedFile.exists())
					archivedFile.delete();
				boolean moveStatus = processingFile.renameTo(archivedFile);

				if(moveStatus)
				{
					iLogger.info("Data Packet is Rejected - Packet with Snapshot time NULL");
					iLogger.info("File "+processingFile.getName()+" is moved successfully from processing to archived folder");
				}
				return "FAILURE";
			}


			//DF20140519 - Rajani Nagaraju - Moving the code from AssetMonitoringDetailsBO to this class to maintain the same format of text in Rejected packets
			// Validate SerialNumber - Authentication packet should have been received earlier for the serialNumber
			AssetDetailsBO assetDetails = new AssetDetailsBO();
			AssetEntity assetEnt = assetDetails.getAssetEntity(parser.getSerialNumber().substring(3));
			if (assetEnt == null || assetEnt.getSerial_number() == null) 
			{
				//DefectId: - 1435-  Rajani Nagaraju - 20131010 - Update rejected packets logger if the packet is rejected for any reason
				//DefectID: DF20131230 - Rajani Nagaraju - Fixing Robustness of Loggers. RejectedPackets Log level changed from 'DEBUG' to 'WARN'
				rLogger.warn(parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime()+" :Invalid VIN - "+parser.getSerialNumber().substring(3)+", JCBRollOff data is not yet received for this VIN");
				//rejectedPacketsInfo.warn(xmlInput);

				//DF20141117 - Rajani Nagaraju - Log Rejected Record into DB
				RejectedPacketDetailsEntity rejectedPkt = new RejectedPacketDetailsEntity();
				rejectedPkt.setCreatedTime(currentTime);
				rejectedPkt.setSerialNumber(parser.getSerialNumber().substring(3));
				rejectedPkt.setTransactionTime(parser.getTransactionTime());
				rejectedPkt.setRejectionCause("JCBRollOff data is not yet received for this VIN ");
				//DefectID:20141121 @ session closed exception
				if(! (session.isOpen() ))
				{
					session = HibernateUtil.getSessionFactory().getCurrentSession();
					session.getTransaction().begin();
				}
				session.save(rejectedPkt);


				//DefectID: 20140318 - Rajani Nagaraju - In order to avoid data loss on Deployment Errors - If the packet is rejected, move the data packet from Unprocessed_Xml to AEMP_Output folder
				archivedFile = new File(DataPackets_ProcessedFolderPath,processingFileName);

				if(archivedFile.exists())
					archivedFile.delete();
				boolean moveStatus = processingFile.renameTo(archivedFile);

				if(moveStatus)
				{
					iLogger.info("Data Packet is Rejected - JCBRollOff data is not yet received for this VIN");
					iLogger.info("File "+processingFile.getName()+" is moved successfully from processing to archived folder");
				}

				return "FAILURE";
			}



			//----------Check for the correctness of TransactionTimeStamp
			int currentYear = Calendar.getInstance().get(Calendar.YEAR); //get the currentYear
			//System.out.println("currentYear:"+currentYear);
			int receivedYear = Integer.parseInt(String.valueOf(parser.getTransactionTime()).substring(0, 4));
			//System.out.println("receivedYear:"+receivedYear);
			if( (receivedYear<currentYear-1) || (receivedYear>currentYear+1) )
			{
				//Added by Rajani 20130626, to log the XML input record when the packet is rejected from Application
				//DefectID: DF20131230 - Rajani Nagaraju - Fixing Robustness of Loggers. RejectedPackets Log level changed from 'DEBUG' to 'WARN'
				rLogger.warn(parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime()+" :Packet with Invalid Snapshot time - Not in the range of CurrentYear-1 and CurrentYear+1");
				//rejectedPacketsInfo.warn(xmlInput);

				//DF20141117 - Rajani Nagaraju - Log Rejected Record into DB
				RejectedPacketDetailsEntity rejectedPkt = new RejectedPacketDetailsEntity();
				rejectedPkt.setCreatedTime(currentTime);
				rejectedPkt.setSerialNumber(parser.getSerialNumber().substring(3));
				rejectedPkt.setTransactionTime(parser.getTransactionTime());
				rejectedPkt.setRejectionCause("Packet with Invalid Snapshot time - Not in the range of CurrentYear-1 and CurrentYear+1");
				//DefectID:20141121 @ session closed exception
				if(! (session.isOpen() ))
				{
					session = HibernateUtil.getSessionFactory().getCurrentSession();
					session.getTransaction().begin();
				}
				session.save(rejectedPkt);

				//DefectID: 20140318 - Rajani Nagaraju - In order to avoid data loss on Deployment Errors - If the packet is rejected, move the data packet from Unprocessed_Xml to AEMP_Output folder
				archivedFile = new File(DataPackets_ProcessedFolderPath,processingFileName);

				if(archivedFile.exists())
					archivedFile.delete();
				boolean moveStatus = processingFile.renameTo(archivedFile);

				if(moveStatus)
				{
					iLogger.info("Data Packet is Rejected - Packet with Invalid Snapshot time - Not in the range of CurrentYear-1 and CurrentYear+1");
					iLogger.info("File "+processingFile.getName()+" is moved successfully from processing to archived folder");
				}

				return "FAILURE";
			}

			//DefectID: DF20140319 - Rajani Nagaraju - Check for the correctness of TransactionTimeStamp
			//If IST(TransactionTimeStamp) > Sysdate + 3hrs (3hrs is a buffer), then reject the packet
			IstGmtTimeConversion timeFormatConObj = new IstGmtTimeConversion();
			Timestamp timestampInIST = timeFormatConObj.convertGmtToIst(parser.getTransactionTime());
			Timestamp currentTimestampInIST = new Timestamp(new java.util.Date().getTime());
			long diff=(currentTimestampInIST.getTime()-timestampInIST.getTime())/(60*60 * 1000);
			// System.out.println("diff:"+diff);
			if(diff<-3)
			{
				rLogger.warn(parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime()+" :Packet with Invalid Snapshot time - Not in the range of CurrentSystime and CurrentSystime+3 Hours");
				rLogger.warn(parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime()+"CurrentSysTime: "+currentTimestampInIST+", TransactionTime in IST:"+timestampInIST);
				//rejectedPacketsInfo.warn(xmlInput);

				//DF20141117 - Rajani Nagaraju - Log Rejected Record into DB
				RejectedPacketDetailsEntity rejectedPkt = new RejectedPacketDetailsEntity();
				rejectedPkt.setCreatedTime(currentTime);
				rejectedPkt.setSerialNumber(parser.getSerialNumber().substring(3));
				rejectedPkt.setTransactionTime(parser.getTransactionTime());
				rejectedPkt.setRejectionCause("Packet with Invalid Snapshot time - Not in the range of CurrentSystime and CurrentSystime+3 Hours");
				//DefectID:20141121 @ session closed exception
				if(! (session.isOpen() ))
				{
					session = HibernateUtil.getSessionFactory().getCurrentSession();
					session.getTransaction().begin();
				}
				session.save(rejectedPkt);


				//DefectID: 20140318 - Rajani Nagaraju - In order to avoid data loss on Deployment Errors - If the packet is rejected, move the data packet from Unprocessed_Xml to AEMP_Output folder
				archivedFile = new File(DataPackets_ProcessedFolderPath,processingFileName);

				if(archivedFile.exists())
					archivedFile.delete();
				boolean moveStatus = processingFile.renameTo(archivedFile);

				if(moveStatus)
				{
					iLogger.info("Data Packet is Rejected - Packet with Invalid Snapshot time - Not in the range of CurrentSystime and CurrentSystime+3 Hours");
					iLogger.info("File "+processingFile.getName()+" is moved successfully from processing to archived folder");
				}

				return "FAILURE";
			}

			//Added by Rajani 20130626
			//Reject the packet if the snapshotTime is less than the snapshotTime of the last received transaction for the same VIN
			//- Packets should be received in the sequence of eventGeneratedTime since hardware is maintaining a FIFO queue in sending the packets and 
			// all throughout from Firmware upto application it is Synchronous communication

			//Added by Rajani 20130715 - DefectID 955
			//Modification to the above logic : Event Packets are received in sequence and Log packets are received in Sequence
			//These two might not be in sequence when considered together - Because of the FW requirement, if the GSM is 'OFF' all the logs/event generated.. 
			//.. will be in Queue and as soon as the GSN is 'ON', first all the event packets are sent in sequence since Events are of high priority..
			//.. and then the log packets in sequence

			//Get the MessageID type of the received packet
			String receivedMessageId = parser.getMessageId();

			if(receivedMessageId==null)
			{
				//DefectID: DF20131230 - Rajani Nagaraju - Fixing Robustness of Loggers. RejectedPackets Log level changed from 'DEBUG' to 'WARN'
				rLogger.warn(parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime()+" :MessageID is NULL");
				//rejectedPacketsInfo.warn(xmlInput);

				//DF20141117 - Rajani Nagaraju - Log Rejected Record into DB
				RejectedPacketDetailsEntity rejectedPkt = new RejectedPacketDetailsEntity();
				rejectedPkt.setCreatedTime(currentTime);
				rejectedPkt.setSerialNumber(parser.getSerialNumber().substring(3));
				rejectedPkt.setTransactionTime(parser.getTransactionTime());
				rejectedPkt.setRejectionCause("MessageID is NULL");
				//DefectID:20141121 @ session closed exception
				if(! (session.isOpen() ))
				{
					session = HibernateUtil.getSessionFactory().getCurrentSession();
					session.getTransaction().begin();
				}
				session.save(rejectedPkt);

				//DefectID: 20140318 - Rajani Nagaraju - In order to avoid data loss on Deployment Errors - If the packet is rejected, move the data packet from Unprocessed_Xml to AEMP_Output folder
				archivedFile = new File(DataPackets_ProcessedFolderPath,processingFileName);

				if(archivedFile.exists())
					archivedFile.delete();
				boolean moveStatus = processingFile.renameTo(archivedFile);

				if(moveStatus)
				{
					iLogger.info("Data Packet is Rejected - MessageID is NULL");
					iLogger.info("File "+processingFile.getName()+" is moved successfully from processing to archived folder");
				}


				return "FAILURE";
			}

			//Logger fatalError = Logger.getLogger("fatalErrorLogger");

			//DefectID: 20140318 - Rajani Nagaraju - In order to avoid data loss on Deployment Errors 
			//Adding outer try - If HibernateUtil cannot be instantiated, same is captured in catch block and the service returns with FAILURE.
			//Data packet will still remain in Unprocessed_XML folder, so when HibernateUtil is up, data packets will be picked up and processed.
			/*try
		{
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();*/


			/*try 
        {*/
			//Modified by Rajani Nagaraju 20130715 - DefectID 955
			/*Query query = session.createQuery(" select transactionTime from AssetMonitoringHeaderEntity where transactionNumber = " +
        			"(select max(a.transactionNumber) from AssetMonitoringHeaderEntity a, RecordTypeEntity b where a.serialNumber='"+parser.getSerialNumber().substring(3)+"'" +
        					" and a.recordTypeId= b.recordTypeId and b.messageId= "+receivedMessageId+" )");*/
			if(! (session.isOpen() ))
			{
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.getTransaction().begin();
			}	

			//DF20141215 - Rajani Nagaraju - To store the txn against which the packet was rejected
			/*Query query = session.createQuery(" select max(a.transactionTime) from AssetMonitoringHeaderEntity a, RecordTypeEntity b where a.serialNumber='"+parser.getSerialNumber().substring(3)+"'" +
        					" and a.recordTypeId= b.recordTypeId and b.messageId= '"+receivedMessageId+"' ");*/

			iLogger.info("Before executing AMH Query" + parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime());
			Query lastTransactionPkt =null;
			
			Timestamp transactionTime = null;
			if(receivedMessageId.equals("010")){
				 lastTransactionPkt = session.createQuery(" select a.transactionNumber,a.transactionTime from AssetMonitoringHeaderEntity a " +
					" where a.serialNumber='"+parser.getSerialNumber().substring(3)+"'" +
					" and a.recordTypeId=3 " +
					//" and a.transactionTime < '"+parser.getTransactionTime()+"'" +
					" order by a.transactionTime desc " ).setFirstResult(0).setMaxResults(1);
			} else {
				 lastTransactionPkt = session.createQuery(" select a.transactionNumber,a.transactionTime from AssetMonitoringSnapshotEntity a " +
						" where a.serialNumber='"+parser.getSerialNumber().substring(3)+"'");
			}
			iLogger.info("Before executing AMH Query" + parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime());


			Iterator amhitr = lastTransactionPkt.list().iterator();
			Object[] amhresult =null;
			iLogger.info("After executing AMH Query" + parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime());
			while(amhitr.hasNext())
			{
				amhresult = (Object[])amhitr.next();
				if(receivedMessageId.equals("010")){
					lastTransNumber = (Integer) amhresult[0];
				} else {
					AssetMonitoringHeaderEntity amh = (AssetMonitoringHeaderEntity)amhresult[0];
					lastTransNumber = amh.getTransactionNumber();
					amh=null;
				}
				transactionTime = (Timestamp) amhresult[1];

		/*	Query query = session.createQuery(" select amh.transactionTime, amh.transactionNumber from AssetMonitoringHeaderEntity amh where" +
					" amh.serialNumber='"+parser.getSerialNumber().substring(3)+"' and amh.transactionTime = (select max(a.transactionTime) from AssetMonitoringHeaderEntity a, " +
					" RecordTypeEntity b where a.serialNumber='"+parser.getSerialNumber().substring(3)+"'" +
					" and a.recordTypeId= b.recordTypeId and b.messageId= '"+receivedMessageId+"' )");
*/
			
		
			/*Iterator itr = query.list().iterator();
			Object[] result =null;*/
			
			//while(itr.hasNext())
			//{
				//DF20141215 - Rajani Nagaraju - To store the txn against which the packet was rejected
				//Timestamp transactionTime = (Timestamp)itr.next();
				//result = (Object[])itr.next();
			//	Timestamp transactionTime = (Timestamp) result[0];
			//	Integer txnNum = (Integer) result[1];

				if((transactionTime!=null ) && (parser.getTransactionTime().before(transactionTime)))
				{
					//Added by Rajani 20130626, to log the XML input record when the packet is rejected from Application
					//DefectID: DF20131230 - Rajani Nagaraju - Fixing Robustness of Loggers. RejectedPackets Log level changed from 'DEBUG' to 'WARN'
					rLogger.warn(parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime()+" :Packet with Invalid Snapshot time: Backdated Packet");
					//rejectedPacketsInfo.warn(xmlInput);

					//DF20141117 - Rajani Nagaraju - Log Rejected Record into DB
					RejectedPacketDetailsEntity rejectedPkt = new RejectedPacketDetailsEntity();
					rejectedPkt.setCreatedTime(currentTime);
					rejectedPkt.setSerialNumber(parser.getSerialNumber().substring(3));
					rejectedPkt.setTransactionTime(parser.getTransactionTime());
					//DF20141215 - Rajani Nagaraju - To store the txn against which the packet was rejected
					rejectedPkt.setTxnForRejection(lastTransNumber);
					rejectedPkt.setRejectionCause("Packet with Invalid Snapshot time : Backdated Packet");
					if(! (session.isOpen() ))
					{
						session = HibernateUtil.getSessionFactory().getCurrentSession();
						session.getTransaction().begin();
					}
					session.save(rejectedPkt);

					//DefectID: 20140318 - Rajani Nagaraju - In order to avoid data loss on Deployment Errors - If the packet is rejected, move the data packet from Unprocessed_Xml to AEMP_Output folder
					archivedFile = new File(DataPackets_ProcessedFolderPath,processingFileName);

					if(archivedFile.exists())
						archivedFile.delete();
					boolean moveStatus = processingFile.renameTo(archivedFile);

					if(moveStatus)
					{
						iLogger.info("Data Packet is Rejected - Packet with Invalid Snapshot time ");
						iLogger.info("File "+processingFile.getName()+" is moved successfully from processing to archived folder");
					}

					return "FAILURE";
				}
				
			}


			//DefectID: DF20131226 - To reject the packet if the Packet is the Tow-away alert and the Machine is currently in In-Transit mode
			HashMap<String, HashMap<String,String>> hashMap = parser.getParamType_parametersMap();

			//Read the required data from Properties file
			String eventParameter =null;
			String logParameters1 =null;
			String ToAwayParameterName =null;
			String TransitDeviceStatus =null;
			String HelloPacketParamName=null;
			// DF20140523 sn
			String ExternalBatteryemoval =null;
			String MachineBatterylow =null;
			String InternalBatteryCharge=null;
			// DF20140523 en

		//	Properties prop = new Properties();
			//prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
			eventParameter = "EventParameters" ;//prop.getProperty("EventParameters");
			//DefectId:20140529 sn
			logParameters1 = "LogParameters";//prop.getProperty("LogParameters");
			//DefectId:20140529 sn

			//DefectId:20140728 Wrong Fuel level value Changes for Ignition-off/Engine-off cycle
			if( hashMap.containsKey(logParameters1))
			{
				//get the current fuelLevel in volts
				HashMap<String, String> logParamValuesMap1 = (HashMap<String, String>) hashMap.get(logParameters1);

				if(! (session.isOpen() ))
				{
					session = HibernateUtil.getSessionFactory().getCurrentSession();
					session.getTransaction().begin();
				}
				//Identify the fuel level that was sent before the transaction timestamp sent in current packet
				String FuelLevel =null;
				FuelLevel = "FuelLevel";//prop.getProperty("FuelLevel");
				int lastPkt=0;
				//double currentFuelLevel=0.0; 
				Timestamp transactionTimestamp = null;
				String ignitionOn =null;
				String engineOn =null;
				double prevFuelLevel=0.0;
				String prevFuelValue=null;
				double prevFuelLevel1=0.0;
				prevFuelValue = "0";//prop.getProperty("PrevFuelLevel");
				prevFuelLevel1 = Double.parseDouble((String)prevFuelValue);
				String currentFuelLevel = logParamValuesMap1.get(FuelLevel);
				//currentFuelLevel = Double.parseDouble((String)currentFuelValue);
				//System.out.println("fuel level value read from configuration property file:"+prevFuelLevel1);
				//To get the machine Ignition/Engine status from AMH and AMD table 


				iLogger.info("Before executing IGStatus" + parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime());
				/*Query prevLocQuery1 = session.createQuery(" select a.serialNumber, b.parameterValue, " +
						" b.parameterId as parameterId, a.transactionTime " +
						" from AssetMonitoringHeaderEntity a , AssetMonitoringDetailEntity b " +
						" where a.transactionNumber = b.transactionNumber " +
						" and a.serialNumber='"+parser.getSerialNumber().substring(3)+"' " +
						" and b.parameterId in (12,18)" +
						" and a.transactionTime = (select max(c.transactionTime) from AssetMonitoringHeaderEntity c " +
						" where c.serialNumber='"+parser.getSerialNumber().substring(3)+"' " +
						" and c.transactionTime < '"+parser.getTransactionTime()+"')");
*/
				
				Query prevLocQuery1 = session.createQuery(" select  b.parameterValue, " +
						" b.parameterId as parameterId  " +
						" from  AssetMonitoringDetailEntity b " +
						" where b.transactionNumber = '"+lastTransNumber+"' " +						
						" and b.parameterId in (12,18)" );


				Iterator prevLocItr = prevLocQuery1.list().iterator();
				iLogger.info("After executing IGStatus" + parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime());
				Object[] resultSet = null;
				while(prevLocItr.hasNext())
				{
					resultSet = (Object[]) prevLocItr.next();
					MonitoringParameters paramObj = (MonitoringParameters)resultSet[1];
					int parameterId = paramObj.getParameterId();
					if(parameterId==12)//IgnitionOn
					{
						ignitionOn = ((String)resultSet[0]);
					}
					else if(parameterId==18)//EngineOn
					{
						engineOn = ((String)resultSet[0]);
					}
				}
				//Get FuelLevel value from previous log packet 
				//DefectId:201411 To Handle Null Check
				if(ignitionOn!=null && ignitionOn.equalsIgnoreCase("0") && engineOn!=null && engineOn.equalsIgnoreCase("0")){

					/*Query lastTransactionPkt = session.createQuery(" select a.serialNumber,a.fuelLevel from AssetMonitoringSnapshotEntity a " +
							" where a.serialNumber='"+parser.getSerialNumber().substring(3)+"'" );
					//DefectId:20141224 @Suprava 
						//	" and a.transactionTime < '"+parser.getTransactionTime()+"' " 
					Iterator lastTransactionPktItr = lastTransactionPkt.list().iterator();
					Object[] fuelResultSet = null;
					while(lastTransactionPktItr.hasNext())
					{
						lastPkt=1;
						fuelResultSet = (Object[]) lastTransactionPktItr.next();
						if(fuelResultSet[1]!=null) //Fuel-Level
						{
							prevFuelLevel = Double.parseDouble((String)fuelResultSet[1]);
						}
					}*/
					//DefectId:20150108 @Suprava Nayak To Generated correct fuel theft alert
					
					iLogger.info("Before executing Fuel Theft*** " + parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime());
					
					/*Query lastTransactionPkt = session.createQuery(" select a.serialNumber,b.parameterValue from AssetMonitoringHeaderEntity a ,AssetMonitoringDetailEntity b" +
							" where a.serialNumber='"+parser.getSerialNumber().substring(3)+"'" +
							" and a.transactionNumber = b.transactionNumber" +
							" and b.parameterId =5 " +
							" and a.recordTypeId=3 " +
							" and a.transactionTime < '"+parser.getTransactionTime()+"'" +
					" order by a.transactionTime desc " ).setFirstResult(0).setMaxResults(1);*/
					
					Query lastTransactionPktFuel = session.createQuery(" select b.parameterValue from AssetMonitoringDetailEntity b" +
							" where b.transactionNumber = '"+lastTransNumber+"' " +									
							" and b.parameterId =5 " );
							
						
					
					Iterator lastTransactionPktItrfuel = lastTransactionPktFuel.list().iterator();
					//System.out.println("Size:" + lastTransactionPkt.list().size());
					iLogger.info("After executing Fuel Theft*** " + parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime());
					/*Object[] fuelResultSet = null;
					while(lastTransactionPktItrfuel.hasNext())
					{
						lastPkt=1;
						fuelResultSet = (Object[]) lastTransactionPktItrfuel.next();
						if(fuelResultSet[0]!=null) //Fuel-Level
						{
							prevFuelLevel = Double.parseDouble((String)fuelResultSet[0]);
						}
					}*/
					String fuelResultSet = null;
					while(lastTransactionPktItrfuel.hasNext())
					{
						lastPkt=1;
						fuelResultSet = (String) lastTransactionPktItrfuel.next();
						if(fuelResultSet!=null)
							prevFuelLevel = Double.parseDouble((String)fuelResultSet);
						
					}
					
					//DefectId:20150108 en
					for(int i=0; i<logParamValuesMap1.size(); i++){
						if( ( (String)logParamValuesMap1.keySet().toArray()[i] ).equalsIgnoreCase(FuelLevel) )
						{

							//Update the FuelLevel value in log packet with previous log packet fuel value
							if(lastPkt==1)
							{
								logParamValuesMap1.put(FuelLevel, String.valueOf(prevFuelLevel));
								//infoLogger.info("fuellevel:*********after update"+logParamValuesMap1.get(FuelLevel));
							}
							//Update the FuelLevel value in log packet with Default value 0 if No log packet is present
							else
							{
								logParamValuesMap1.put(FuelLevel, String.valueOf(prevFuelLevel1));
								//infoLogger.info("fuellevel Update for the log packrt < 10 min:*********after update"+logParamValuesMap1.get(FuelLevel));
							}
							hashMap.put(logParameters1, logParamValuesMap1);
							parser.setParamType_parametersMap(hashMap);

						}
					}

				}

			}

			//DefectId:20140728 End

			//ToAwayParameterName = prop.getProperty("ToAwayParameterName");

			ToAwayParameterName="TO_AWAY";
			TransitDeviceStatus = "TRANSIT";
			ExternalBatteryemoval="EXTERNAL_BATTERY_REMOVED";
			MachineBatterylow="MACHINE_BATTERY_LOW";
			InternalBatteryCharge="InternalBatteryCharge";
			HelloPacketParamName="HELLO";
			//TransitDeviceStatus = prop.getProperty("TransitDeviceStatus");
			// DF20140523 sn
			/*ExternalBatteryemoval = prop.getProperty("ExternalBatteryemoval");
				MachineBatterylow = prop.getProperty("MachineBatterylow");
				InternalBatteryCharge = prop.getProperty("InternalBatteryCharge");
				HelloPacketParamName=prop.getProperty("HelloPacketParamName");*/


			// DF20140523 en
			//DF20140515 - Rajani Nagaraju - GPS Filtering Logic for wrong coordinates
			/*if(hashMap.containsKey(eventParameter))
			{
				HashMap<String, String> paramValues = (HashMap<String, String>) hashMap.get(eventParameter);
				if(paramValues.containsKey(ToAwayParameterName))
				{
					String value = paramValues.get(ToAwayParameterName);
					if(value.equalsIgnoreCase("1"))
					{
						Query machineStatusQuery = session.createQuery(" from AssetExtendedDetailsEntity where serial_number='"+parser.getSerialNumber().substring(3)+"'");
						Iterator machineStatusItr = machineStatusQuery.list().iterator();
						while(machineStatusItr.hasNext())
						{
							AssetExtendedDetailsEntity assetExtended = (AssetExtendedDetailsEntity)machineStatusItr.next();
							if(assetExtended.getDevice_Status().equalsIgnoreCase(TransitDeviceStatus))
							{
								//DefectID: DF20131230 - Rajani Nagaraju - Fixing Robustness of Loggers. RejectedPackets Log level changed from 'DEBUG' to 'WARN'
								rejectedPacketsInfo.warn("Tow-away Alert received, but the Machine is in In-Transit mode");
								rejectedPacketsInfo.warn(xmlInput);

								 //DefectID: 20140318 - Rajani Nagaraju - In order to avoid data loss on Deployment Errors - If the packet is rejected, move the data packet from Unprocessed_Xml to AEMP_Output folder
								archivedFile = new File(DataPackets_ProcessedFolderPath,processingFileName);

								if(archivedFile.exists())
									archivedFile.delete();
						        boolean moveStatus = processingFile.renameTo(archivedFile);

						       if(moveStatus)
						        {
						        	infoLogger.info("Data Packet is Rejected - Tow-away Alert received, but the Machine is in In-Transit mode");
						        	infoLogger.info("File "+processingFile.getName()+" is moved successfully from processing to archived folder");
						        }

			        			return "FAILURE";
							}
						}

					}
				}
			}*/

			//DF20140515 - Rajani Nagaraju - GPS Filtering Logic for wrong coordinates
			String value =null;
			// DF20140523 sn -- Suprava Nayak - Wrong GPS filtering for battery related events
			String value1 =null;
			String value2=null;
			String value3=null;
			//DF20140606 - Rajani Nagaraju - To handle GPS bouncing in Hello packets received in Transit mode
			String value5=null;
			// DF20140523 en
			if(hashMap.containsKey(eventParameter))
			{
				HashMap<String, String> paramValues = (HashMap<String, String>) hashMap.get(eventParameter);
				HashMap<String, String> paramValues1 = (HashMap<String, String>) hashMap.get(logParameters1);
				if(paramValues.containsKey(ToAwayParameterName))
				{
					value = paramValues.get(ToAwayParameterName);
					//infoLogger.info("ToAwayParameterName_value"+value);
				}
				// DF20140523 sn
				else if(paramValues.containsKey(ExternalBatteryemoval)||paramValues.containsKey(MachineBatterylow)||paramValues1.containsKey(InternalBatteryCharge)
						|| paramValues.containsKey(HelloPacketParamName))
				{
					value1 = paramValues.get(ExternalBatteryemoval);
					//infoLogger.info("ExternalBatteryemoval_value:"+value1);
					value2 = paramValues.get(MachineBatterylow);
					//infoLogger.info("MachineBatterylow:"+value2);
					value3 = paramValues1.get(InternalBatteryCharge);
					//infoLogger.info("InternalBatteryCharge:"+value3);
					String[] value4 = value3.split(" unit Percentage");
					value3 = value4[0].trim();
					//infoLogger.info("value3********"+value4[0].trim());
					value5 = paramValues.get(HelloPacketParamName);
					//infoLogger.info("Hello Packet:"+value5);
				}
				// DF20140523 en
			}
			//This should be a log packet
			else
			{
				value="2";
			}
			String LogParameters=null;
			LogParameters ="LogParameters";// prop.getProperty("LogParameters");

			if((value !=null && (value.equalsIgnoreCase("1"))) || (value5!=null && (value5.equalsIgnoreCase("1"))) || 
					(value1!=null && value1.equalsIgnoreCase("1")) || (value2!=null && value2.equalsIgnoreCase("1")) 
					|| ((value1!=null && value1.equalsIgnoreCase("1")) && (value3!=null && (Long.valueOf(value3) < 75))))
			{
				if(! (session.isOpen() ))
				{
					session = HibernateUtil.getSessionFactory().getCurrentSession();
					session.getTransaction().begin();
				}
				Query machineStatusQuery = session.createQuery(" from AssetExtendedDetailsEntity where serial_number='"+parser.getSerialNumber().substring(3)+"'");
				Iterator machineStatusItr = machineStatusQuery.list().iterator();
				AssetExtendedDetailsEntity assetExtended =null;
				while(machineStatusItr.hasNext())
				{
					assetExtended = (AssetExtendedDetailsEntity)machineStatusItr.next();
				}

				//Tow-away alert received in Transit Mode - Ignore the packet
				//DF20140805 - Rajani Nagaraju - Checking for NULL condition of "value"
				//if( (assetExtended.getDevice_Status().equalsIgnoreCase(TransitDeviceStatus)) && value.equalsIgnoreCase("1"))
				if( (assetExtended.getDevice_Status().equalsIgnoreCase(TransitDeviceStatus)) && value!=null && value.equalsIgnoreCase("1"))
				{
					//DefectID: DF20131230 - Rajani Nagaraju - Fixing Robustness of Loggers. RejectedPackets Log level changed from 'DEBUG' to 'WARN'
					rLogger.warn(parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime()+" :Tow-away Alert received, but the Machine is in In-Transit mode");
					//rejectedPacketsInfo.warn(xmlInput);

					//DF20141117 - Rajani Nagaraju - Log Rejected Record into DB
					RejectedPacketDetailsEntity rejectedPkt = new RejectedPacketDetailsEntity();
					rejectedPkt.setCreatedTime(currentTime);
					rejectedPkt.setSerialNumber(parser.getSerialNumber().substring(3));
					rejectedPkt.setTransactionTime(parser.getTransactionTime());
					rejectedPkt.setRejectionCause("Tow-away Alert received, but the Machine is in In-Transit mode");
					session.save(rejectedPkt);


					//DefectID: 20140318 - Rajani Nagaraju - In order to avoid data loss on Deployment Errors - If the packet is rejected, move the data packet from Unprocessed_Xml to AEMP_Output folder
					archivedFile = new File(DataPackets_ProcessedFolderPath,processingFileName);

					if(archivedFile.exists())
						archivedFile.delete();
					boolean moveStatus = processingFile.renameTo(archivedFile);

					if(moveStatus)
					{
						iLogger.info("Data Packet is Rejected - Tow-away Alert received, but the Machine is in In-Transit mode");
						iLogger.info("File "+processingFile.getName()+" is moved successfully from processing to archived folder");
					}

					return "FAILURE";
				}

				//Either the tow-away alert in Normal Mode OR Log Packet with Transit Mode
				//defectId:20140528 @suprava added condition InternalBatteryChargeLow or ExternalBatteryemoval or MachineBatterylow
				if( (value!=null && value.equalsIgnoreCase("1"))|| (value1!=null && value1.equalsIgnoreCase("1")) || 
						(value2!=null && value2.equalsIgnoreCase("1")) || ((value1!=null && value1.equalsIgnoreCase("1")) && (value3!=null && (Long.valueOf(value3) < 75)) )
						|| ( (value5!=null) && (value5.equalsIgnoreCase("1")) &&  (assetExtended.getDevice_Status().equalsIgnoreCase(TransitDeviceStatus)) ) )
				{
					String packet=null;
					if(value!=null && value.equalsIgnoreCase("1"))
						packet = "Tow-away";
					/*else if(value!=null && value.equalsIgnoreCase("2"))
							packet = "Log in transit mode";*/
					else if(value5!=null && value5.equalsIgnoreCase("1"))
						packet = "HELLO packet in transit mode";
					////defectId:20140528 @suprava
					else 
						packet = "InternalBatteryChargeLow or ExternalBatteryemoval or MachineBatterylow";

					//Get the current Lat Long
					String Location = null;
					String Latitude = null;
					String Longitude = null;
					Location ="Location";// prop.getProperty("Location");
					Latitude = "Latitude";//prop.getProperty("Latitude");
					Longitude ="Longitude";//prop.getProperty("Longitude");

					String latValue = null;
					String longValue = null;

					HashMap<String, String> paramValuesMap = (HashMap<String, String>) hashMap.get(Location);
					for(int i=0; i<paramValuesMap.size(); i++)
					{
						if( ( (String)paramValuesMap.keySet().toArray()[i] ).equalsIgnoreCase(Latitude) )
						{
							latValue = paramValuesMap.get((String)paramValuesMap.keySet().toArray()[i]);

							//convert to the valid Latitude value
							double paramValueInDouble = Double.valueOf(latValue.replace('N', ' ').replace('E', ' ').trim());
							Double convertedValueInDouble = (((paramValueInDouble/100)%1)*2+(paramValueInDouble/100)*3)/3;
							latValue = convertedValueInDouble.toString();
						}
						if( ( (String)paramValuesMap.keySet().toArray()[i] ).equalsIgnoreCase(Longitude) )
						{
							longValue = paramValuesMap.get((String)paramValuesMap.keySet().toArray()[i]);

							//convert to the valid longitude value
							double paramValueInDouble = Double.valueOf(longValue.replace('N', ' ').replace('E', ' ').trim());
							Double convertedValueInDouble = (((paramValueInDouble/100)%1)*2+(paramValueInDouble/100)*3)/3;
							longValue = convertedValueInDouble.toString();
						}
					}

					double currentLat= Double.parseDouble(latValue);
					double currentLong = Double.parseDouble(longValue);


					String FirmwareParameters ="GPSFix";// prop.getProperty("FirmwareParameters");
					String GPSFix ="GPSFix";// prop.getProperty("GPSFix");
					//Check for the GPS Fix value in received packet
					String currentGPSfix =null;
					if(hashMap.containsKey(FirmwareParameters))
					{
						HashMap<String, String> paramValuesMapp = (HashMap<String, String>) hashMap.get(FirmwareParameters);
						for(int i=0; i<paramValuesMapp.size(); i++)
						{
							if( ( (String)paramValuesMapp.keySet().toArray()[i] ).equalsIgnoreCase(GPSFix) )
							{
								currentGPSfix = paramValuesMapp.get((String)paramValuesMapp.keySet().toArray()[i]);
							}
						}
					}
					//DefectID:20140605 - @Suprava - GPS fix = 0 map the event packet with old GPS co-ordinate
					if(currentGPSfix.equalsIgnoreCase("0") && (value!=null && value.equalsIgnoreCase("1")))
					{
						//Reject the Packet and send notification email
						String emailTo ="deepthi.rao@wipro.com";// prop.getProperty("JcbGenericMailId");
						String emailSubject = "Incorrect GPS: Packet Rejected for "+parser.getSerialNumber().substring(3);
						String emailBody = " PIN: "+parser.getSerialNumber().substring(3)+";Time & Date: " +
						" "+parser.getTransactionTime()+";GPS Coordinate: "+currentLat+","+currentLong+";" +
						"Packet received: "+packet+";" +
						"Message: Incorrect GPS coordinate received and hence packet rejected"	;
						emailBody = emailBody.replaceAll(";", "\n");

						EmailTemplate emailObj = new EmailTemplate();
						emailObj.setTo(emailTo);
						emailObj.setSubject(emailSubject);
						emailObj.setBody(emailBody);
						//DF20140102 - Rajani Nagaraju - To provide the InfoLoggers in a specific pattern
						emailObj.setSerialNumber(parser.getSerialNumber().substring(3));
						emailObj.setTransactionTime(parser.getTransactionTime()+" ");

						iLogger.info(parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime()+"  :"+"---- Email Notification ------");
						iLogger.info(parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime()+"  :"+"To: "+ emailTo);
						iLogger.info(parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime()+"  :"+"Subject: "+ emailSubject);
						iLogger.info(parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime()+"  :"+"Body: "+ emailBody);

						new EmailHandler().handleEmail("jms/queue/emailQ", emailObj,0);

						rLogger.warn(parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime()+" :Packet received:"+packet+", but the GPS Fix is 0");
						//rejectedPacketsInfo.warn(xmlInput);

						//DF20141117 - Rajani Nagaraju - Log Rejected Record into DB
						RejectedPacketDetailsEntity rejectedPkt = new RejectedPacketDetailsEntity();
						rejectedPkt.setCreatedTime(currentTime);
						rejectedPkt.setSerialNumber(parser.getSerialNumber().substring(3));
						rejectedPkt.setTransactionTime(parser.getTransactionTime());
						rejectedPkt.setRejectionCause("Packet received, but the GPS Fix is 0");
						session.save(rejectedPkt);


						//DefectID: 20140318 - Rajani Nagaraju - In order to avoid data loss on Deployment Errors - If the packet is rejected, move the data packet from Unprocessed_Xml to AEMP_Output folder
						archivedFile = new File(DataPackets_ProcessedFolderPath,processingFileName);

						if(archivedFile.exists())
							archivedFile.delete();
						boolean moveStatus = processingFile.renameTo(archivedFile);

						if(moveStatus)
						{
							iLogger.info("Data Packet is Rejected - Packet received:"+packet+", but the GPS Fix is 0");
							iLogger.info("File "+processingFile.getName()+" is moved successfully from processing to archived folder");
						}

						return "FAILURE";
					}

					if(! (session.isOpen() ))
					{
						session = HibernateUtil.getSessionFactory().getCurrentSession();
						session.getTransaction().begin();
					}
					// DF20140523 n
					String IgnitionOn =null;
					//Find the Lat Long of last received packet
					iLogger.info("Before  executing GPS Filtering" + parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime());
					
					Query prevLocQuery = session.createQuery(" select a.serialNumber, b.parameterValue, " +
							" b.parameterId as parameterId, a.transactionTime " +
							" from AssetMonitoringHeaderEntity a , AssetMonitoringDetailEntity b " +
							" where a.transactionNumber = b.transactionNumber " +
							" and a.serialNumber='"+parser.getSerialNumber().substring(3)+"' " +
							// DF20140523 o
							//" and b.parameterId in (1,2)" +
							// DF20140523 n
							" and b.parameterId in (1,2,12)" +
							" and a.transactionTime = (select max(c.transactionTime) from AssetMonitoringHeaderEntity c " +
							" where c.serialNumber='"+parser.getSerialNumber().substring(3)+"' " +
							" and c.transactionTime < '"+parser.getTransactionTime()+"')");
					double prevLat=0.0;
					double prevLong=0.0;
					Timestamp prevTxnTime = null;

					Iterator prevLocItr = prevLocQuery.list().iterator();
					iLogger.info("After  executing GPS Filtering" + parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime());
					Object res[] = null;
					while(prevLocItr.hasNext())
					{
						res = (Object[]) prevLocItr.next();
						AssetEntity asset = (AssetEntity)res[0];
						MonitoringParameters paramObj = (MonitoringParameters)res[2];
						int parameterId = paramObj.getParameterId();
						if(parameterId==1) //Latitude
						{
							prevLat = Double.parseDouble((String)res[1]);
						}
						else if(parameterId==2) //Longitude
						{
							prevLong = Double.parseDouble((String)res[1]);
						}
						//defectId:20140528 @suprava sn
						else if(parameterId==12)//IgnitionOn
						{
							IgnitionOn = ((String)res[1]);
						}
						//defectId:20140528 @suprava en
						prevTxnTime = (Timestamp) res[3];
					}
					double distanceInKm = new Geofencing().calDistance(prevLat,prevLong,currentLat,currentLong);

					//DF20150109 - Rajani Nagaraju  - Handle Null Pointer Exception
					if( (IgnitionOn!=null) && (IgnitionOn.equalsIgnoreCase("0"))){
						//Find the distance (in Kms) travalled by the machine since last communication
						//	double distanceInKm = new Geofencing().calDistance(prevLat,prevLong,currentLat,currentLong);
						//Time period between two communication packets
						//System.out.println("distanceInKm:"+distanceInKm);
						//infoLogger.info("distanceInKm:"+distanceInKm);
						double diffInHours=(parser.getTransactionTime().getTime()-prevTxnTime.getTime())/(60*60 * 1000.0);
						double speed=0;
						if(diffInHours!=0)
							speed = distanceInKm/diffInHours;
						//infoLogger.info("speed:*********of the machine"+speed);
						//System.out.println("speed:*********of the machine"+speed);
						if(speed>120)
						{
							//System.out.println("INside 120***");
							//defectId:20140528 @suprava sn
							if((value2!=null && value2.equalsIgnoreCase("1")) ||((value1!=null && value1.equalsIgnoreCase("1")) &&(value3!=null && (Long.valueOf(value3) < 75)))|| (value1!=null && value1.equalsIgnoreCase("1") ) )
							{
								//System.out.println("Inside Battery Event***");
								for(int i=0; i<paramValuesMap.size(); i++){
									if( ( (String)paramValuesMap.keySet().toArray()[i] ).equalsIgnoreCase(Latitude) )
									{
										double dprevLat = prevLat;
										int intLat =(int)dprevLat; 
										double val = (dprevLat - intLat)*60/100;
										double finalVal = (intLat +val )*100;

										//	infoLogger.info("prevLat:********* before"+prevLat);

										//paramValuesMap.put(Latitude, String.valueOf(prevLat));
										paramValuesMap.put(Latitude, String.valueOf(finalVal));
										//infoLogger.info("prevLat:*********after"+paramValuesMap.get(Latitude));
									}
									if( ( (String)paramValuesMap.keySet().toArray()[i] ).equalsIgnoreCase(Longitude) )
									{
										double dprevLong = prevLong;
										int intLong =(int)dprevLong; 
										double val = (dprevLong - intLong)*60/100;
										double finalLongVal = (intLong +val )*100;


										//infoLogger.info("prevLat:*********before"+prevLong);
										//paramValuesMap.put(Longitude, String.valueOf(prevLong));
										paramValuesMap.put(Longitude, String.valueOf(finalLongVal));
										//infoLogger.info("prevLat:*********after"+paramValuesMap.get(Longitude));
									}
								}
								hashMap.put(Location, paramValuesMap);
								parser.setParamType_parametersMap(hashMap);

								//	infoLogger.info("latitude "+parser.getParamType_parametersMap().get(Location).get(Latitude));
								//	infoLogger.info("Longitude "+parser.getParamType_parametersMap().get(Location).get(Longitude));
								//	infoLogger.info("longitue "+hashMap.get(Location).get(Longitude));
								//defectId:20140528 @suprava en
							}
							if((value!=null && value.equalsIgnoreCase("1")) || (value5!=null && value5.equalsIgnoreCase("1"))){
								//Reject the Packet and send notification email
								String emailTo = "deepthi.rao@wipro.com";//prop.getProperty("JcbGenericMailId");
								String emailSubject = "Incorrect GPS: Packet Rejected for "+parser.getSerialNumber().substring(3);
								String emailBody = " PIN: "+parser.getSerialNumber().substring(3)+";Time & Date: " +
								" "+parser.getTransactionTime()+";GPS Coordinate: "+currentLat+","+currentLong+";" +
								"Packet received: "+packet+";" +
								"Message: Incorrect GPS coordinate received and hence packet rejected"	;
								emailBody = emailBody.replaceAll(";", "\n");

								EmailTemplate emailObj = new EmailTemplate();
								emailObj.setTo(emailTo);
								emailObj.setSubject(emailSubject);
								emailObj.setBody(emailBody);
								//DF20140102 - Rajani Nagaraju - To provide the InfoLoggers in a specific pattern
								emailObj.setSerialNumber(parser.getSerialNumber().substring(3));
								emailObj.setTransactionTime(parser.getTransactionTime()+" ");

								iLogger.info(parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime()+"  :"+"---- Email Notification ------");
								iLogger.info(parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime()+"  :"+"To: "+ emailTo);
								iLogger.info(parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime()+"  :"+"Subject: "+ emailSubject);
								iLogger.info(parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime()+"  :"+"Body: "+ emailBody);

								new EmailHandler().handleEmail("jms/queue/emailQ", emailObj,0);

								rLogger.warn(parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime()+" :Packet received:"+packet+", GPS Coordinates are incorrect");
								//rejectedPacketsInfo.warn(xmlInput);

								//DF20141117 - Rajani Nagaraju - Log Rejected Record into DB
								RejectedPacketDetailsEntity rejectedPkt = new RejectedPacketDetailsEntity();
								rejectedPkt.setCreatedTime(currentTime);
								rejectedPkt.setSerialNumber(parser.getSerialNumber().substring(3));
								rejectedPkt.setTransactionTime(parser.getTransactionTime());
								rejectedPkt.setRejectionCause("Packet received, but the GPS Coordinates are incorrect");
								session.save(rejectedPkt);


								//DefectID: 20140318 - Rajani Nagaraju - In order to avoid data loss on Deployment Errors - If the packet is rejected, move the data packet from Unprocessed_Xml to AEMP_Output folder
								archivedFile = new File(DataPackets_ProcessedFolderPath,processingFileName);

								if(archivedFile.exists())
									archivedFile.delete();
								boolean moveStatus = processingFile.renameTo(archivedFile);

								if(moveStatus)
								{
									iLogger.info("Data Packet is Rejected - Packet received:"+packet+", GPS Coordinates are incorrect");
									iLogger.info("File "+processingFile.getName()+" is moved successfully from processing to archived folder");
								}

								return "FAILURE";
							}
						}
						//DefectId:20141028 @Suprava sn
						else if((distanceInKm>10 && value!=null && value.equalsIgnoreCase("1")) || (distanceInKm<0.5 && value!=null && value.equalsIgnoreCase("1")))
						{
							//System.out.println("distanceInKm inside else when tow-away =1 gps value=0 and :"+distanceInKm);
							//	infoLogger.info("distanceInKm >10 and when tow-away =1 gps value=0 and :"+distanceInKm);
							//if((value!=null && value.equalsIgnoreCase("1"))){
							//Ignore the packet and send notification email
							String emailTo ="deepthi.rao@wipro.com";// prop.getProperty("JcbGenericMailId");
							String emailSubject = "Incorrect GPS: Packet Rejected for "+parser.getSerialNumber().substring(3);
							String emailBody = " PIN: "+parser.getSerialNumber().substring(3)+";Time & Date: " +
							" "+parser.getTransactionTime()+";GPS Coordinate: "+currentLat+","+currentLong+";" +
							"Packet received: "+packet+";" +
							"Message: Incorrect GPS coordinate received and hence packet rejected"	;
							emailBody = emailBody.replaceAll(";", "\n");

							EmailTemplate emailObj = new EmailTemplate();
							emailObj.setTo(emailTo);
							emailObj.setSubject(emailSubject);
							emailObj.setBody(emailBody);
							//DF20140102 - Rajani Nagaraju - To provide the InfoLoggers in a specific pattern
							emailObj.setSerialNumber(parser.getSerialNumber().substring(3));
							emailObj.setTransactionTime(parser.getTransactionTime()+" ");

							iLogger.info(parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime()+"  :"+"---- Email Notification ------");
							iLogger.info(parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime()+"  :"+"To: "+ emailTo);
							iLogger.info(parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime()+"  :"+"Subject: "+ emailSubject);
							iLogger.info(parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime()+"  :"+"Body: "+ emailBody);

							new EmailHandler().handleEmail("jms/queue/emailQ", emailObj,0);

							rLogger.warn(parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime()+" :Packet received:"+packet+", but the GPS Fix is 0");
							//rejectedPacketsInfo.warn(xmlInput);

							//DF20141117 - Rajani Nagaraju - Log Rejected Record into DB
							RejectedPacketDetailsEntity rejectedPkt = new RejectedPacketDetailsEntity();
							rejectedPkt.setCreatedTime(currentTime);
							rejectedPkt.setSerialNumber(parser.getSerialNumber().substring(3));
							rejectedPkt.setTransactionTime(parser.getTransactionTime());
							rejectedPkt.setRejectionCause("Packet received, but the GPS Fix is 0");
							session.save(rejectedPkt);

							//DefectID: 20140318 - Rajani Nagaraju - In order to avoid data loss on Deployment Errors - If the packet is rejected, move the data packet from Unprocessed_Xml to AEMP_Output folder
							archivedFile = new File(DataPackets_ProcessedFolderPath,processingFileName);

							if(archivedFile.exists())
								archivedFile.delete();
							boolean moveStatus = processingFile.renameTo(archivedFile);

							if(moveStatus)
							{
								iLogger.info("Data Packet is Rejected - Packet received:"+packet+", but the GPS Fix is 0");
								iLogger.info("File "+processingFile.getName()+" is moved successfully from processing to archived folder");
							}

							return "FAILURE";

							//}
						}
						//DefectId:20141028 @Suprava en
					}
					else if( (IgnitionOn!=null) && (IgnitionOn.equalsIgnoreCase("1")) )
					{
						if((distanceInKm>10 && value!=null && value.equalsIgnoreCase("1")) || (distanceInKm<0.5 && value!=null && value.equalsIgnoreCase("1")))
						{
							//System.out.println("distanceInKm inside else when tow-away =1 gps value=0 and :"+distanceInKm);
							//	infoLogger.info("distanceInKm >10 and when tow-away =1 gps value=0 and :"+distanceInKm);
							//if((value!=null && value.equalsIgnoreCase("1"))){
							//Ignore the packet and send notification email
							String emailTo = "deepthi.rao@wipro.com";//prop.getProperty("JcbGenericMailId");
							String emailSubject = "Incorrect GPS: Packet Rejected for "+parser.getSerialNumber().substring(3);
							String emailBody = " PIN: "+parser.getSerialNumber().substring(3)+";Time & Date: " +
							" "+parser.getTransactionTime()+";GPS Coordinate: "+currentLat+","+currentLong+";" +
							"Packet received: "+packet+";" +
							"Message: Incorrect GPS coordinate received and hence packet rejected"	;
							emailBody = emailBody.replaceAll(";", "\n");

							EmailTemplate emailObj = new EmailTemplate();
							emailObj.setTo(emailTo);
							emailObj.setSubject(emailSubject);
							emailObj.setBody(emailBody);
							//DF20140102 - Rajani Nagaraju - To provide the InfoLoggers in a specific pattern
							emailObj.setSerialNumber(parser.getSerialNumber().substring(3));
							emailObj.setTransactionTime(parser.getTransactionTime()+" ");

							iLogger.info(parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime()+"  :"+"---- Email Notification ------");
							iLogger.info(parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime()+"  :"+"To: "+ emailTo);
							iLogger.info(parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime()+"  :"+"Subject: "+ emailSubject);
							iLogger.info(parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime()+"  :"+"Body: "+ emailBody);

							new EmailHandler().handleEmail("jms/queue/emailQ", emailObj,0);

							rLogger.warn(parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime()+" :Packet received:"+packet+", but the GPS Fix is 0");
							//rejectedPacketsInfo.warn(xmlInput);

							//DF20141117 - Rajani Nagaraju - Log Rejected Record into DB
							RejectedPacketDetailsEntity rejectedPkt = new RejectedPacketDetailsEntity();
							rejectedPkt.setCreatedTime(currentTime);
							rejectedPkt.setSerialNumber(parser.getSerialNumber().substring(3));
							rejectedPkt.setTransactionTime(parser.getTransactionTime());
							rejectedPkt.setRejectionCause("Packet received, but the GPS Fix is 0");
							session.save(rejectedPkt);

							//DefectID: 20140318 - Rajani Nagaraju - In order to avoid data loss on Deployment Errors - If the packet is rejected, move the data packet from Unprocessed_Xml to AEMP_Output folder
							/*archivedFile = new File(DataPackets_ProcessedFolderPath,processingFileName);

							if(archivedFile.exists())
								archivedFile.delete();
							boolean moveStatus = processingFile.renameTo(archivedFile);

							if(moveStatus)
							{
							   	infoLogger.info("Data Packet is Rejected - Packet received:"+packet+", but the GPS Fix is 0");
							   	infoLogger.info("File "+processingFile.getName()+" is moved successfully from processing to archived folder");
							}*/

							return "FAILURE";

							//}
						}	
					}
				}

			}        


		}

		catch(Exception e)
		{
			fLogger.fatal(parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime()+" :Exception :"+e);
			e.printStackTrace();

			Writer result = new StringWriter();
			PrintWriter printWriter = new PrintWriter(result);
			e.printStackTrace(printWriter);
			String err = result.toString();
			fLogger.fatal(parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime()+" :Exception trace: "+err);
			try 
			{
				printWriter.close();
				result.close();
			} 

			catch (IOException e1) 
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			//DF20150112 - Rajani Nagaraju - IF DB Connection is not obtained for writing into Rejection Log, retain the file and exit
			if(e instanceof JDBCConnectionException || e instanceof SQLException || e instanceof GenericJDBCException)
			{
				if(e.getMessage().contains("Cannot open connection"))
				{
					return "FAILURE";
				}
			}


		}
		finally
		{
			if(session!=null && session.isOpen())  
				if(session.getTransaction().isActive())
				{
					session.getTransaction().commit();
				}

			if(session.isOpen())
			{
				if(session.getTransaction().isActive())
					session.flush();
				session.close();
			}

		}

		//}

		/*catch(Exception e)
		{
			fatalError.fatal("Exception :"+e);
			return "FAILURE";
		}
		 */

		//Create a new Thread to generate the Alert
		//	new AlertGenerationImpl(parser);
		this.parser=parser;
		iLogger.info(parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime()+"Create New Thread");
		Thread t = new Thread(this, "Child Thread");
		iLogger.info(parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime()+"Start New Thread");


		//DF20150114 - Rajani Nagaraju - To_AWAY with value '0' was getting added to IGNITION_ON packet (frm ADS) and hence affecting alert generation
		//- was trying to close any open tow away alert - Because of the same parser object that is referred in ADS and AGS. Hence creating new parser
		// object for ADS and the manipulation of which doesnot affect AGS
		XmlParser newParser = new XmlParser();
		newParser.setFwVersionNumber(parser.getFwVersionNumber());
		newParser.setMessageId(parser.getMessageId());
		newParser.setSerialNumber(parser.getSerialNumber());
		newParser.setTransactionTime(parser.getTransactionTime());
		newParser.setVersion(parser.getVersion());

		HashMap<String, HashMap<String,String>> newHas = new  HashMap<String, HashMap<String,String>>();
		HashMap<String, HashMap<String,String>> has = parser.getParamType_parametersMap();
		Iterator hasItr = has.entrySet().iterator();
		while(hasItr.hasNext())
		{
			Map.Entry pairs = (Map.Entry)hasItr.next();
			String st = (String)pairs.getKey();
			HashMap<String,String> hp = (HashMap<String,String>)pairs.getValue();
			HashMap<String,String> newHp = new  HashMap<String,String>();
			newHp.putAll(hp);
			newHas.put(st, newHp);
		}
		newParser.setParamType_parametersMap(newHas);


		t.start();


		//	String status = assetMonitoringDetails.setRemoteMonitoringData(parser,processingFileName);
		iLogger.info(parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime()+"Call Asset Diagnostic Service" +"lastTransNumber" +lastTransNumber);
		//  String status = assetMonitoringDetails.setRemoteMonitoringData(parser,processingFileName);
		
		String status = assetMonitoringDetails.setRemoteMonitoringData(newParser,processingFileName,lastTransNumber);
		iLogger.info(parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime()+"AAfter Asset Diagnostic Service" +"lastTransNumber" +lastTransNumber);
		iLogger.info(parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime()+"Wait for Child threads to terminate");
		try {
			t.join();
		} 

		catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Writer result = new StringWriter();
			PrintWriter printWriter = new PrintWriter(result);
			e.printStackTrace(printWriter);
			String err = result.toString();
			fLogger.fatal("Exception trace: "+err);
			try 
			{
				printWriter.close();
				result.close();
			} 

			catch (IOException e1) 
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}

		if(this.alertGenerationStatus.equalsIgnoreCase("SUCCESS"))
		{
			iLogger.info(parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime()+"Move the File from Inprocess to AEMP Output Folder");
			//Once the Data is succesfully inserted into DB, Move the XML Data Packet from Unprocessed_XML to AEMP_OUTPUT folder
			archivedFile = new File(DataPackets_ProcessedFolderPath,processingFileName);

			if(archivedFile.exists())
				archivedFile.delete();
			boolean moveStatus = processingFile.renameTo(archivedFile);

			if(moveStatus)
			{
				iLogger.info("File "+processingFile.getName()+" is moved successfully from processing to archived folder");
			}
		}

		iLogger.info("Main Thread Exiting");
		return status;


	}

	public void run() 
	{
		//Logger fatalError = Logger.getLogger("fatalErrorLogger"); 
		
    	Logger fLogger = FatalLoggerClass.logger;
    	//Logger rLogger = RejectedPktLoggerClass.logger;
		try 
		{
			String status = new AlertGenerationImpl().generateAlerts(this.parser);
			this.alertGenerationStatus=status;
		}

		catch (Exception e) 
		{
			fLogger.fatal("Child Interrupted");
			fLogger.fatal("Exception :"+e);
			
			Writer result = new StringWriter();
    	    PrintWriter printWriter = new PrintWriter(result);
    	    e.printStackTrace(printWriter);
    	    String err = result.toString();
    	    fLogger.fatal("Exception trace: "+err);
    	    try 
    	    {
    	    	printWriter.close();
        	    result.close();
			} 
    	    
    	    catch (IOException e1) 
    	    {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	}
	
	//***************************** VIN Registration ***************************************
	/** Implementation method to handle VIN Registration
	 * @param xmlInput VIN details received as XML input
	 * @return returns the Registration acknowledgement as XML String
	 * @throws UnsupportedEncodingException
	 * @throws CustomFault
	 * @throws SQLException
	 */
	public String registerVIN(String xmlInput) 
	{
		String xmlString =null;
		xmlString = "";
		Logger bLogger = BusinessErrorLoggerClass.logger;
	    
    	Logger fLogger = FatalLoggerClass.logger;
		/*String xmlString ="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
						  "<Message>" +
						  "<RegistrationStatus>&</RegistrationStatus>" +
						  "<CummulativeEngineHours>#</CummulativeEngineHours>" +
						  "</Message>";*/
		
		//Logger businessError = Logger.getLogger("businessErrorLogger");
        //Logger fatalError = Logger.getLogger("fatalErrorLogger");
        
		try
		{
			AssetMonitoringDetailsBO assetMonitoringDetails = new AssetMonitoringDetailsBO();
			
	    	
			//AssetDiagnosticImpl implObj = new AssetDiagnosticImpl();
			
			//DF20141107 - Rajani Nagaraju - START - Accepting registration data as "|" seperated String instead of XML
			//Format the received input XML
			/*String xmlFormattedString = implObj.format(xmlInput);
			if(xmlFormattedString.equalsIgnoreCase("FAILURE"))
			{
				return "FAILURE";
			}
			
			//parse the XML and obtain the hashmap of parameterTypes, parameter and their corresponding values
			XmlParser xmlParser = new XmlParser();
			XmlParser parser = xmlParser.parseXml(xmlFormattedString);
		
			//check for the existence of Serial number
			if(parser.getSerialNumber()==null)
			{
				return "FAILURE";
			}
		
			//check for transaction time
			if(parser.getTransactionTime()==null)
			{
				return "FAILURE";
			}
		
			String status = assetMonitoringDetails.setVinRegistration(parser);*/
			//DF20141107 - Rajani Nagaraju - END - Accepting registration data as "|" seperated String instead of XML
			
			//DF20141107 - Rajani Nagaraju - START - Accepting registration data as "|" seperated String instead of XML
			 String[] msgString = xmlInput.split("\\|");
			 if(msgString.length<4)
			 {
				 bLogger.equals("VIN Registration Service Input: "+xmlInput+" : Not received in the required format");
				 return "FAILURE";
			 }
				 
			 String status = assetMonitoringDetails.setVinRegistration(msgString[0],msgString[1],msgString[2],msgString[3]);
			//DF20141107 - Rajani Nagaraju - END - Accepting registration data as "|" seperated String instead of XML
			String machineStatus = null;
			String engineHours = null;
			//DefectId:20140710 @Suprava Engine hour defult value changes to 5 *start*
			Properties prop = new Properties();
			prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
			if(status.equalsIgnoreCase("SUCCESS"))
			{
				machineStatus = "SUCCESS";
				//engineHours ="0";
				engineHours = prop.getProperty("Engine_Hours");
			}
			//20140710: end
			else if(status.equalsIgnoreCase("FAILURE"))
			{
				machineStatus = "FAILURE";
				engineHours ="0";
			}
			else
			{
				machineStatus = "SUCCESS";
				engineHours = status;
			}
		
			//generate the xml String
		/*	xmlString = xmlString.replaceAll("&", machineStatus);
			xmlString = xmlString.replaceAll("#", engineHours);*/
			
			xmlString = machineStatus + "|" + engineHours;
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("Exception :"+e);
		}
		finally
		{
			
		}
        
        return xmlString;
		
	}
	//***************************************** END of VIN Registration ***************************************
	public String format(String unformattedXml) 
	{
		//Logger fatalError = Logger.getLogger("fatalErrorLogger");
		//Logger bLogger = BusinessErrorLoggerClass.logger;
	    
    	Logger fLogger = FatalLoggerClass.logger;
		try 
        {
            final Document document = parseXmlFile(unformattedXml);
            OutputFormat format = new OutputFormat(document);
            format.setLineWidth(65);
            format.setIndenting(true);
            format.setIndent(2);
            Writer out = new StringWriter();
            XMLSerializer serializer = new XMLSerializer(out, format);
            serializer.serialize(document);

            return out.toString();
        } 
        catch (Exception e) 
        {
        	fLogger.fatal("Exception :"+e);
        	return "FAILURE";
        }
    }
	

    private Document parseXmlFile(String in) 
    {
        try 
        {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(in));
            return db.parse(is);
        }
        catch (ParserConfigurationException e) 
        {
            throw new RuntimeException(e);
        } 
        catch (SAXException e) 
        {
            throw new RuntimeException(e);
        } 
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
	}
    
    
    //*******************************************Set Remote Monitoring Data New*****************************************
    
    public String setAssetMonitoringDataNew(String xmlInput)
	{

    	Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
    	Logger rLogger = RejectedPktLoggerClass.logger;
    	
    	//Get SerialNumber and Transaction time to maintain the Logger format
    	String serialNumber = StringUtils.substringBetween(xmlInput, "<SerialNumber>", "</SerialNumber>").substring(3);
		String transactionTime = StringUtils.substringBetween(xmlInput, "SnapshotTime=\"", "\"").replace("T", " ").replace("Z", "");
		Properties prop=null;
		String prevTxnLat=null, prevTxnLong=null, prevTxnFuelLevel=null, prevTxnIgnitionStatus="0", prevTxnEngineStatus="0";
		AssetMonitoringHeaderEntity prevTxnEntity = null;
		String currentGPSfix =null, currentLat=null, currentLong=null, currentInternalBatteryLevel=null;
		int prevLogtransactionNumber=0;
		
		iLogger.info("ADI:"+serialNumber+":"+transactionTime+":"+"Read from property file - START");
		try
		{
			prop= StaticProperties.getConfProperty();
		}
		catch(Exception e)
		{
			fLogger.fatal("ADI:"+serialNumber+":"+transactionTime+":"+" -Error in intializing property File :"+e);
			return "FAILURE";
		}
		
		
		//------------------- File Movement - get the required folder paths from properties file - START
//		String deploymentEnv = prop.getProperty("deployenvironment");
//		String DataPackets_UnprocessedFolderPath = prop.getProperty("DataPackets_UnprocessedFolderPath_"+deploymentEnv);
		
		//DF20150319 - Rajani Nagaraju - VIN based file storage in AEMP output folder
		//String DataPackets_ProcessedFolderPath = prop.getProperty("DataPackets_ProcessedFolderPath_"+deploymentEnv);
//		String DataPackets_ProcessedFolderPath = prop.getProperty("DataPackets_ProcessedFolderPath_"+deploymentEnv)+"/"+serialNumber;
		
//		String DataPackets_InProcessFolderPath = prop.getProperty("DataPackets_InProcessFolderPath_"+deploymentEnv);
		//------------------- File Movement - get the required folder paths from properties file - END
		iLogger.info("ADI:"+serialNumber+":"+transactionTime+":"+"Read from property file - END");
					
		String[] inputSplitString = xmlInput.split("\\|", 2);
		if(inputSplitString.length<2)
		{
			fLogger.fatal("ADI:"+serialNumber+":"+transactionTime+":"+"AssetDiagnosticInput not properly created - In the format FileName|xmlInputString");
			return "DATAERROR";
		}

		
		XmlParser parser =null;
		String processingFileName =null;

		try
		{
			xmlInput = inputSplitString[1];
			processingFileName=inputSplitString[0];
//			File processingFile = new File(DataPackets_InProcessFolderPath,processingFileName);
			File archivedFile = null;
			
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();

			Timestamp currentTime = new Timestamp(new Date().getTime());

			//********************************************** STEP1: Check for Packet Rejections
			
			//----------------------Step 1.1: Parse the XML and build HashMap
			try
			{
				//Check the format of the received input XML
				String xmlFormattedString = format(xmlInput);
				if(xmlFormattedString.equalsIgnoreCase("FAILURE"))
				{
					rLogger.warn("ADI:"+serialNumber+":"+transactionTime+":"+"Invalid XML packet format: "+xmlInput);
					
					RejectedPacketDetailsEntity rejectedPkt = new RejectedPacketDetailsEntity();
					rejectedPkt.setCreatedTime(currentTime);
					rejectedPkt.setRejectionCause("Invalid XML packet format: "+xmlInput);
					session.save(rejectedPkt);

					//DefectID: 20140318 - Rajani Nagaraju - In order to avoid data loss on Deployment Errors - If the packet is rejected, move the data packet from Unprocessed_Xml to AEMP_Output folder
//					archivedFile = new File(DataPackets_ProcessedFolderPath,processingFileName);
//					archivedFile.mkdirs();
//					if(archivedFile.exists())
//						archivedFile.delete();
//					boolean moveStatus = processingFile.renameTo(archivedFile);
//
//					if(moveStatus)
//					{
//						iLogger.info("ADI:"+serialNumber+":"+transactionTime+":"+"Data Packet is Rejected - Invalid XML packet format");
//						iLogger.info("ADI:"+serialNumber+":"+transactionTime+":"+"File "+processingFile.getName()+" is moved successfully from processing to archived folder");
//					}

					return "DATAERROR";
				}


				//Parse the XML and obtain the hashmap of parameterTypes, parameter and their corresponding values
				XmlParser xmlParser = new XmlParser();
				iLogger.info("ADI:"+serialNumber+":"+transactionTime+":"+"XML parsing - START");
				parser = xmlParser.parseXml(xmlFormattedString);
				iLogger.info("ADI:"+serialNumber+":"+transactionTime+" updated TransactionNumber+1:"+parser.getTransactionTime());
				iLogger.info("ADI:"+serialNumber+":"+transactionTime+":"+"XML parsing - END");
			}

			catch(Exception e)
			{
				fLogger.fatal("ADI:"+serialNumber+":"+transactionTime+":"+"Error in Parsing XML : Exception :"+e);
				return "DATAERROR";
			}

			
			try
			{
				iLogger.info("ADI:"+serialNumber+":"+transactionTime+":"+"Check for Packet Rejections - START");
				//-------------------- Step 1.2 - check for the existence of Serial number
				if(parser.getSerialNumber()==null)
				{
					rLogger.warn("ADI:"+serialNumber+":"+transactionTime+":"+"Packet with no VIN");
					rLogger.warn("ADI:"+serialNumber+":"+transactionTime+":"+xmlInput);

					RejectedPacketDetailsEntity rejectedPkt = new RejectedPacketDetailsEntity();
					rejectedPkt.setCreatedTime(currentTime);
					rejectedPkt.setRejectionCause("Packet with no VIN: "+xmlInput);
					session.save(rejectedPkt);

					//DefectID: 20140318 - Rajani Nagaraju - In order to avoid data loss on Deployment Errors - If the packet is rejected, move the data packet from Unprocessed_Xml to AEMP_Output folder
//					archivedFile = new File(DataPackets_ProcessedFolderPath,processingFileName);
//					archivedFile.mkdirs();
//					if(archivedFile.exists())
//						archivedFile.delete();
//					boolean moveStatus = processingFile.renameTo(archivedFile);
//
//					if(moveStatus)
//					{
//						iLogger.info("ADI:"+serialNumber+":"+transactionTime+":"+"Data Packet is Rejected - Packet with no VIN");
//						iLogger.info("ADI:"+serialNumber+":"+transactionTime+":"+"File "+processingFile.getName()+" is moved successfully from processing to archived folder");
//					}
	
					return "DATAERROR";

				}

				
				//------------------------ Step 1.3 - Check for the Transaction time
				if(parser.getTransactionTime()==null)
				{	
					rLogger.warn("ADI:"+serialNumber+":"+transactionTime+":"+"Packet with Snapshot time NULL");
					rLogger.warn("ADI:"+serialNumber+":"+transactionTime+":"+xmlInput);

					RejectedPacketDetailsEntity rejectedPkt = new RejectedPacketDetailsEntity();
					rejectedPkt.setCreatedTime(currentTime);
					rejectedPkt.setRejectionCause("Packet with Snapshot time NULL: "+xmlInput);
					session.save(rejectedPkt);

					//DefectID: 20140318 - Rajani Nagaraju - In order to avoid data loss on Deployment Errors - If the packet is rejected, move the data packet from Unprocessed_Xml to AEMP_Output folder
//					archivedFile = new File(DataPackets_ProcessedFolderPath,processingFileName);
//					archivedFile.mkdirs();
//					if(archivedFile.exists())
//						archivedFile.delete();
//					boolean moveStatus = processingFile.renameTo(archivedFile);
//
//					if(moveStatus)
//					{
//						iLogger.info("ADI:"+serialNumber+":"+transactionTime+":"+"Data Packet is Rejected - Packet with Snapshot time NULL");
//						iLogger.info("ADI:"+serialNumber+":"+transactionTime+":"+"File "+processingFile.getName()+" is moved successfully from processing to archived folder");
//					}
					return "DATAERROR";
				}


				// ------------------------ Step 1.4 - Validate SerialNumber - Authentication packet should have been received earlier for the serialNumber
				serialNumber = parser.getSerialNumber().substring(3);
				String receivedMessageId = parser.getMessageId();
				
				AssetEntity assetEnt=null;
				Query assetQ = session.createQuery("from AssetEntity where serial_number='"+serialNumber+"' and active_status=1");
				Iterator assetItr = assetQ.list().iterator();
				if(assetItr.hasNext())
				{
					assetEnt = (AssetEntity)assetItr.next();
				}
				
				if (assetEnt == null || assetEnt.getSerial_number() == null) 
				{
					rLogger.warn("ADI:"+serialNumber+":"+transactionTime+":"+" :Invalid VIN , JCBRollOff data is not yet received for this VIN");
					
					RejectedPacketDetailsEntity rejectedPkt = new RejectedPacketDetailsEntity();
					rejectedPkt.setCreatedTime(currentTime);
					rejectedPkt.setSerialNumber(serialNumber);
					rejectedPkt.setTransactionTime(parser.getTransactionTime());
					rejectedPkt.setRejectionCause("JCBRollOff data is not yet received for this VIN ");
					session.save(rejectedPkt);

//					archivedFile = new File(DataPackets_ProcessedFolderPath,processingFileName);
//					archivedFile.mkdirs();
//					if(archivedFile.exists())
//						archivedFile.delete();
//					boolean moveStatus = processingFile.renameTo(archivedFile);
//					if(moveStatus)
//					{
//						iLogger.info("ADI:"+serialNumber+":"+transactionTime+":"+"Data Packet is Rejected - JCBRollOff data is not yet received for this VIN");
//						iLogger.info("ADI:"+serialNumber+":"+transactionTime+":"+"File "+processingFile.getName()+" is moved successfully from processing to archived folder");
//					}
	
					return "REJECTED";
				}

				//------------------------ Step 1.5 - Check for the MessageId
				if(receivedMessageId==null)
				{
					rLogger.warn("ADI:"+serialNumber+":"+transactionTime+":"+" :MessageID is NULL");
					
					RejectedPacketDetailsEntity rejectedPkt = new RejectedPacketDetailsEntity();
					rejectedPkt.setCreatedTime(currentTime);
					rejectedPkt.setSerialNumber(serialNumber);
					rejectedPkt.setTransactionTime(parser.getTransactionTime());
					rejectedPkt.setRejectionCause("MessageID is NULL");
					session.save(rejectedPkt);

//					archivedFile = new File(DataPackets_ProcessedFolderPath,processingFileName);
//					archivedFile.mkdirs();
//					if(archivedFile.exists())
//						archivedFile.delete();
//					boolean moveStatus = processingFile.renameTo(archivedFile);
//
//					if(moveStatus)
//					{
//						iLogger.info("ADI:"+serialNumber+":"+transactionTime+":"+"Data Packet is Rejected - MessageID is NULL");
//						iLogger.info("ADI:"+serialNumber+":"+transactionTime+":"+"File "+processingFile.getName()+" is moved successfully from processing to archived folder");
//					}


					return "DATAERROR";
				}
				
				
				//----------------------- Step 1.6 - Check if the MessageId is other than log or Event
				if(! (receivedMessageId.equalsIgnoreCase("001") || receivedMessageId.equalsIgnoreCase("010")) )
				{
					rLogger.warn("ADI:"+serialNumber+":"+transactionTime+":"+" Invalid MessageID: "+receivedMessageId);
					//No Need to Log this detail into Rejected Logs table
					
//					archivedFile = new File(DataPackets_ProcessedFolderPath,processingFileName);
//					archivedFile.mkdirs();
//					if(archivedFile.exists())
//						archivedFile.delete();
//					boolean moveStatus = processingFile.renameTo(archivedFile);
//					if(moveStatus)
//					{
//						iLogger.info("ADI:"+serialNumber+":"+transactionTime+":"+"Data Packet is Rejected - Invalid MessageID: "+receivedMessageId);
//						iLogger.info("ADI:"+serialNumber+":"+transactionTime+":"+"File "+processingFile.getName()+" is moved successfully from processing to archived folder");
//					}
	
					return "REJECTED";
				}

				
				//------------------------ Step 1.7 Check for the correctness of TransactionTimeStamp
				//Should be in the range of CurrentYear-1 and CurrentYear+1 - To consider the scenario of backdated pkts say of Dec30 received on Jan 12
				int currentYear = Calendar.getInstance().get(Calendar.YEAR); //get the currentYear
				int receivedYear = Integer.parseInt(String.valueOf(parser.getTransactionTime()).substring(0, 4));
				if( (receivedYear<currentYear-1) || (receivedYear>currentYear+1) )
				{	
					rLogger.warn("ADI:"+serialNumber+":"+transactionTime+":"+" :Packet with Invalid Snapshot time - Not in the range of CurrentYear-1 and CurrentYear+1");
				
					RejectedPacketDetailsEntity rejectedPkt = new RejectedPacketDetailsEntity();
					rejectedPkt.setCreatedTime(currentTime);
					rejectedPkt.setSerialNumber(serialNumber);
					rejectedPkt.setTransactionTime(parser.getTransactionTime());
					rejectedPkt.setRejectionCause("Packet with Invalid Snapshot time - Not in the range of CurrentYear-1 and CurrentYear+1");
					session.save(rejectedPkt);

					//DefectID: 20140318 - Rajani Nagaraju - In order to avoid data loss on Deployment Errors - If the packet is rejected, move the data packet from Unprocessed_Xml to AEMP_Output folder
//					archivedFile = new File(DataPackets_ProcessedFolderPath,processingFileName);
//					archivedFile.mkdirs();
//					if(archivedFile.exists())
//						archivedFile.delete();
//					boolean moveStatus = processingFile.renameTo(archivedFile);
//					if(moveStatus)
//					{
//						iLogger.info("ADI:"+serialNumber+":"+transactionTime+":"+"Data Packet is Rejected - Packet with Invalid Snapshot time - Not in the range of CurrentYear-1 and CurrentYear+1");
//						iLogger.info("ADI:"+serialNumber+":"+transactionTime+":"+"File "+processingFile.getName()+" is moved successfully from processing to archived folder");
//					}
	
					return "REJECTED";
				}

				//If IST(TransactionTimeStamp) > Sysdate + 3hrs (3hrs is a buffer), then reject the packet
				Timestamp txntimestampInIST = new IstGmtTimeConversion().convertGmtToIst(parser.getTransactionTime());
				long diff=(currentTime.getTime()-txntimestampInIST.getTime())/(60*60 * 1000);
				if(diff<-3)
				{
					rLogger.warn("ADI:"+serialNumber+":"+transactionTime+":"+" :Packet with Invalid Snapshot time - Not in the range of CurrentSystime and CurrentSystime+3 Hours");
					rLogger.warn("ADI:"+serialNumber+":"+transactionTime+":"+"CurrentSysTime: "+currentTime+", TransactionTime in IST:"+txntimestampInIST);
				
					RejectedPacketDetailsEntity rejectedPkt = new RejectedPacketDetailsEntity();
					rejectedPkt.setCreatedTime(currentTime);
					rejectedPkt.setSerialNumber(serialNumber);
					rejectedPkt.setTransactionTime(parser.getTransactionTime());
					rejectedPkt.setRejectionCause("Packet with Invalid Snapshot time - Not in the range of CurrentSystime and CurrentSystime+3 Hours");
					session.save(rejectedPkt);

//					archivedFile = new File(DataPackets_ProcessedFolderPath,processingFileName);
//					archivedFile.mkdirs();
//					if(archivedFile.exists())
//						archivedFile.delete();
//					boolean moveStatus = processingFile.renameTo(archivedFile);
//	
//					if(moveStatus)
//					{
//						iLogger.info("ADI:"+serialNumber+":"+transactionTime+":"+"Data Packet is Rejected - Packet with Invalid Snapshot time - Not in the range of CurrentSystime and CurrentSystime+3 Hours");
//						iLogger.info("ADI:"+serialNumber+":"+transactionTime+":"+"File "+processingFile.getName()+" is moved successfully from processing to archived folder");
//					}

					return "REJECTED";
				}

				
				
				
				
				//------------------------ Step 1.8 Check for Backdated Packet 
				//Added by Rajani 20130626
				//Reject the packet if the snapshotTime is less than the snapshotTime of the last received transaction for the same VIN
				//- Packets should be received in the sequence of eventGeneratedTime since hardware is maintaining a FIFO queue in sending the packets and 
				// all throughout from Firmware upto application it is Synchronous communication
	
				//Added by Rajani 20130715 - DefectID 955
				//Modification to the above logic : Event Packets are received in sequence and Log packets are received in Sequence
				//These two might not be in sequence when considered together - Because of the FW requirement, if the GSM is 'OFF' all the logs/event generated.. 
				//.. will be in Queue and as soon as the GSN is 'ON', first all the event packets are sent in sequence since Events are of high priority.... and then the log packets in sequence
				iLogger.info("ADI:"+serialNumber+":"+transactionTime+":"+ "Before executing AMH Query to get the Previous Packet");
				Timestamp prevTxnTimestamp=null;
				AssetMonitoringHeaderEntity prevLogTxn = null;
				AssetMonitoringHeaderEntity prevEventTxn = null;
				
				Query prevTxnQ =session.createQuery(" from AssetMonitoringSnapshotEntity where serialNumber='"+serialNumber+"'"); 
				Iterator prevTxnItr = prevTxnQ.list().iterator();
				AssetMonitoringSnapshotEntity ams = null;
				if(prevTxnItr.hasNext())
				{
					ams = (AssetMonitoringSnapshotEntity)prevTxnItr.next();
					
					prevLogTxn = ams.getLatestLogTxn();
					prevEventTxn = ams.getLatestEventTxn();
					
					iLogger.info("ADI:"+serialNumber+":"+transactionTime+":"+"Retrieved data from AMS; prevLogTxn:"+prevLogTxn+"; prevEventTxn:"+prevEventTxn);
					if( (receivedMessageId.equals("010")) && prevLogTxn!=null)
						prevTxnTimestamp = prevLogTxn.getTransactionTime();
					else if( (prevEventTxn!=null) && (receivedMessageId.equals("001")) )
						prevTxnTimestamp = prevEventTxn.getTransactionTime();
					
					if(prevLogTxn!=null)
						prevLogtransactionNumber=prevLogTxn.getTransactionNumber();
					
				}

				if((prevTxnTimestamp!=null ) && (parser.getTransactionTime().before(prevTxnTimestamp)))
				{
					rLogger.warn("ADI:"+serialNumber+":"+transactionTime+":"+" :Packet with Invalid Snapshot time: Backdated Packet");
					
					RejectedPacketDetailsEntity rejectedPkt = new RejectedPacketDetailsEntity();
					rejectedPkt.setCreatedTime(currentTime);
					rejectedPkt.setSerialNumber(serialNumber);
					rejectedPkt.setTransactionTime(parser.getTransactionTime());
					rejectedPkt.setTxnForRejection(ams.getTransactionNumber().getTransactionNumber());
					rejectedPkt.setRejectionCause("Packet with Invalid Snapshot time : Backdated Packet");
					session.save(rejectedPkt);

//					archivedFile = new File(DataPackets_ProcessedFolderPath,processingFileName);
//					archivedFile.mkdirs();
//					if(archivedFile.exists())
//						archivedFile.delete();
//					boolean moveStatus = processingFile.renameTo(archivedFile);
//					if(moveStatus)
//					{
//						iLogger.info("ADI:"+serialNumber+":"+transactionTime+":"+"Data Packet is Rejected - Packet with Invalid Snapshot time ");
//						iLogger.info("ADI:"+serialNumber+":"+transactionTime+":"+"File "+processingFile.getName()+" is moved successfully from processing to archived folder");
//					}

					return "REJECTED";
				}
				iLogger.info("ADI:"+serialNumber+":"+transactionTime+":"+"Check for Packet Rejections - END");
				
				
				//----------------- STEP 2.0 - Get the required parameters from HashMap
				HashMap<String, HashMap<String,String>> hashMap = parser.getParamType_parametersMap();
				
				HashMap<String, String> eventParamValuesMap = (HashMap<String, String>) hashMap.get("EventParameters");
				HashMap<String, String> logParamValuesMap = (HashMap<String, String>) hashMap.get("LogParameters");
				HashMap<String, String> locparamValuesMap = (HashMap<String, String>) hashMap.get("Location");
				if(hashMap.containsKey("GPSFix"))
				{
					HashMap<String, String> fwParamValueMap = (HashMap<String, String>) hashMap.get("GPSFix");
					currentGPSfix = fwParamValueMap.get("GPSFix");
				}
				currentInternalBatteryLevel=logParamValuesMap.get("InternalBatteryCharge");
				String[] internalBatteryWithunit= currentInternalBatteryLevel.split(" unit Percentage");
				currentInternalBatteryLevel=internalBatteryWithunit[0].trim();
				
				currentLat=locparamValuesMap.get("Latitude");
				double latValueInDouble = Double.valueOf(currentLat.replace('N', ' ').replace('E', ' ').trim());
				Double convertedLatValueInDouble = (((latValueInDouble/100)%1)*2+(latValueInDouble/100)*3)/3;
				currentLat = convertedLatValueInDouble.toString();
				
				currentLong=locparamValuesMap.get("Longitude");
				double longValueInDouble = Double.valueOf(currentLong.replace('N', ' ').replace('E', ' ').trim());
				Double convertedLongValueInDouble = (((longValueInDouble/100)%1)*2+(longValueInDouble/100)*3)/3;
				currentLong = convertedLongValueInDouble.toString();
				
				
				//****************************** Step 2 - Get the latest txn for the VIN and few monitoring parameters for the same
				iLogger.info("ADI:"+serialNumber+":"+transactionTime+":"+"Get Previous Transaction Details - START");
				
				//Find the latest received transaction for the given VIN
				Timestamp t1=null, t2=null;
				if(prevLogTxn!=null && (parser.getTransactionTime().after(prevLogTxn.getTransactionTime()) || parser.getTransactionTime().equals(prevLogTxn.getTransactionTime()) ))
					t1=prevLogTxn.getTransactionTime();
				
				if(prevEventTxn!=null && ( parser.getTransactionTime().after(prevEventTxn.getTransactionTime()) || parser.getTransactionTime().equals(prevEventTxn.getTransactionTime()) ))
					t2=prevEventTxn.getTransactionTime();
				
				if(t1==null && t2!=null)
					prevTxnEntity=prevEventTxn;
				
				else if(t2==null && t1!=null)
					prevTxnEntity=prevLogTxn;
				
				else if (t1!=null && t2!=null)
				{
					if(t1.before(t2))
						prevTxnEntity=prevEventTxn;
					else
						prevTxnEntity=prevLogTxn;
				}
				
				
				if( (receivedMessageId.equals("010")) && (prevEventTxn!=null && prevEventTxn.getTransactionTime().after(parser.getTransactionTime()))
						&& (prevEventTxn!=null && prevLogTxn!=null && prevEventTxn.getTransactionTime().after(prevLogTxn.getTransactionTime())) )
				
				{
					iLogger.info("ADI:"+serialNumber+":"+transactionTime+":"+" Querying AMH to get Previous Transaction - START");
					//Find the Latest Txn from AMH and AMD tables
					long startTime = System.currentTimeMillis();
					//Df20150330 - Rajani nagaraju - Adding SegmentId in where clause will pick the VIN from the specific partition and hence improves performance
					Query lastTxnQ = session.createQuery(" from AssetMonitoringHeaderEntity where serialNumber='"+serialNumber+"' " +
							" and transactionTime < '"+transactionTime+"' and segmentId="+assetEnt.getSegmentId()+" order by transactionTime desc").setFirstResult(0).setMaxResults(1);
					Iterator lastTxnItr = lastTxnQ.list().iterator();
					if(lastTxnItr.hasNext())
					{
						AssetMonitoringHeaderEntity amh = (AssetMonitoringHeaderEntity)lastTxnItr.next();
						prevTxnEntity=amh;
					}
					long endTime=System.currentTimeMillis();
					iLogger.info("ADI:"+serialNumber+":"+transactionTime+":"+" Querying AMH to get Previous Transaction - END");
					iLogger.info(currentTime+": ADI:"+serialNumber+":"+transactionTime+":"+" Total Time taken for Querying AMH to get Previous Transaction_"+(endTime-startTime));
				}
				
				if(prevTxnEntity!=null)
					iLogger.info("ADI:"+serialNumber+":"+transactionTime+":"+"Calculated Previous Txn Number:"+prevTxnEntity.getTransactionNumber());
				
				// Get the Parameter Values corresponding to prevTxnEntity
				if(prevTxnEntity!=null) // - If this is not the first packet received for the machine
				{
					Query prevTxnParameterQ = session.createQuery(" select b.parameterId,a.parameterValue from AssetMonitoringDetailEntity a," +
							" MonitoringParameters b where a.parameterId=b.parameterId and a.transactionNumber='"+prevTxnEntity.getTransactionNumber()+"'" +
									" and b.parameterId in (1,2,5,12,18) ");
					Object[] result = null;
					Iterator prevTxnParameterItr = prevTxnParameterQ.list().iterator();
					while(prevTxnParameterItr.hasNext())
					{
						result = (Object[])prevTxnParameterItr.next();
						int parameterId = (Integer)result[0];
						if(parameterId==1 && result[1]!=null )
							prevTxnLat=result[1].toString();
						else if(parameterId==2 && result[1]!=null )
							prevTxnLong = result[1].toString();
						else if (parameterId==5 && result[1]!=null )
							prevTxnFuelLevel = result[1].toString();
						else if (parameterId==12 && result[1]!=null )
							prevTxnIgnitionStatus = result[1].toString();
						else if (parameterId==18 && result[1]!=null )
							prevTxnEngineStatus = result[1].toString();
					}
				}	
				iLogger.info("ADI:"+serialNumber+":"+transactionTime+":"+"Get Previous Transaction Details - END");
				
				
				
				
				//****************************** Step 3 - Fuel Level Bouncing when Ignition and Engine is OFF
				iLogger.info("ADI:"+serialNumber+":"+transactionTime+":"+"Check for Fuel Level bounce - START");
				//Check for FuelLevelbounce - If the Log packet is received after Ignition OFF and Engine OFF, fuel level might come wrong
				//Hence replace the fuel level with prev Log pkt fuel level
				if( (receivedMessageId.equals("010")) && prevTxnIgnitionStatus.equals("0") && prevTxnEngineStatus.equals("0") )
				{
					//Get the Fuel Level of prevLog packet
					String prevLogPktFuel =null;
					if(prevLogTxn!=null)
					{
						Query prevLogTxnFuelQ = session.createQuery(" from AssetMonitoringDetailEntity where " +
								" transactionNumber='"+prevLogTxn.getTransactionNumber()+"' and parameterId=5 ");
						Iterator prevLogTxnFuelItr = prevLogTxnFuelQ.list().iterator();
						while(prevLogTxnFuelItr.hasNext())
						{
							AssetMonitoringDetailEntity amd = (AssetMonitoringDetailEntity)prevLogTxnFuelItr.next();
							prevLogPktFuel = amd.getParameterValue();
						}
					}
					
					//Replace the Fuel Level in the current hashmap with the prev log txn fuel level - Ignoring the small possibilty of low fuel event received in between
					//HashMap<String, String> logParamValuesMap = (HashMap<String, String>) hashMap.get("LogParameters");
					for(int i=0; i<logParamValuesMap.size(); i++)
					{
						if( ( (String)logParamValuesMap.keySet().toArray()[i] ).equalsIgnoreCase("FuelLevel") )
						{
							//Update the FuelLevel value in log packet with previous log packet fuel value
							if(prevLogPktFuel!=null)
								logParamValuesMap.put("FuelLevel", prevLogPktFuel);
							else
								logParamValuesMap.put("FuelLevel", "0.0");
							
							hashMap.put("LogParameters", logParamValuesMap);
							parser.setParamType_parametersMap(hashMap);

						}
					}
				
				}
				iLogger.info("ADI:"+serialNumber+":"+transactionTime+":"+"Check for Fuel Level bounce - END");
				
				
				
				//************************************ Step 4 - GPS Bouncing - Packet Rejection 
				iLogger.info("ADI:"+serialNumber+":"+transactionTime+":"+"Check for GPS bounce - START");
				
				/*//----------------- STEP 4.1 - Get the required parameters from HashMap
							
				HashMap<String, String> eventParamValuesMap = (HashMap<String, String>) hashMap.get("EventParameters");
				HashMap<String, String> logParamValuesMap = (HashMap<String, String>) hashMap.get("LogParameters");
				HashMap<String, String> locparamValuesMap = (HashMap<String, String>) hashMap.get("Location");
				if(hashMap.containsKey("GPSFix"))
				{
					HashMap<String, String> fwParamValueMap = (HashMap<String, String>) hashMap.get("GPSFix");
					currentGPSfix = fwParamValueMap.get("GPSFix");
				}
				currentInternalBatteryLevel=logParamValuesMap.get("InternalBatteryCharge");
				String[] internalBatteryWithunit= currentInternalBatteryLevel.split(" unit Percentage");
				currentInternalBatteryLevel=internalBatteryWithunit[0].trim();
				
				currentLat=locparamValuesMap.get("Latitude");
				double latValueInDouble = Double.valueOf(currentLat.replace('N', ' ').replace('E', ' ').trim());
				Double convertedLatValueInDouble = (((latValueInDouble/100)%1)*2+(latValueInDouble/100)*3)/3;
				currentLat = convertedLatValueInDouble.toString();
				
				currentLong=locparamValuesMap.get("Longitude");
				double longValueInDouble = Double.valueOf(currentLong.replace('N', ' ').replace('E', ' ').trim());
				Double convertedLongValueInDouble = (((longValueInDouble/100)%1)*2+(longValueInDouble/100)*3)/3;
				currentLong = convertedLongValueInDouble.toString();
				*/
				//---------------------- STEP 4.2 - Get the current status of the machine - NORMAL/TRANSIT
				String machineStatus=null;
				Query assetStatusQ = session.createQuery(" from AssetExtendedDetailsEntity where serial_number='"+serialNumber+"'");
				Iterator assetStatusItr = assetStatusQ.list().iterator();
				if(assetStatusItr.hasNext())
				{
					AssetExtendedDetailsEntity assetExdendedEnt = (AssetExtendedDetailsEntity)assetStatusItr.next();
					machineStatus= assetExdendedEnt.getDevice_Status();
				}
				
				
				//---------------------- STEP 4.2 - GPS Bounce in case of tow-away
				if((eventParamValuesMap!=null) && (eventParamValuesMap.containsKey("TO_AWAY")))
				{
					//------------ 4.2.1 If the machine is in IN-TRANSIT mode - reject the packet
					if(machineStatus.equalsIgnoreCase("TRANSIT"))
					{
						rLogger.warn("ADI:"+serialNumber+":"+transactionTime+":"+" :Tow-away alert received in TRANSIT mode hence rejecting the packet");
						
						RejectedPacketDetailsEntity rejectedPkt = new RejectedPacketDetailsEntity();
						rejectedPkt.setCreatedTime(currentTime);
						rejectedPkt.setSerialNumber(serialNumber);
						rejectedPkt.setTransactionTime(parser.getTransactionTime());
						rejectedPkt.setRejectionCause("Tow-away Alert received, but the Machine is in In-Transit mode");
						session.save(rejectedPkt);

//						archivedFile = new File(DataPackets_ProcessedFolderPath,processingFileName);
//						archivedFile.mkdirs();
//						if(archivedFile.exists())
//							archivedFile.delete();
//						boolean moveStatus = processingFile.renameTo(archivedFile);
//						if(moveStatus)
//						{
//							iLogger.info("ADI:"+serialNumber+":"+transactionTime+":"+"Tow-away alert received in TRANSIT mode hence rejecting the packet ");
//							iLogger.info("ADI:"+serialNumber+":"+transactionTime+":"+"File "+processingFile.getName()+" is moved successfully from processing to archived folder");
//						}

						return "REJECTED";
					}
					
					//------------ 4.2.2 - If the Current GPS Fix is 0 - reject the packet
					if(currentGPSfix.equalsIgnoreCase("0"))
					{
						rLogger.warn("ADI:"+serialNumber+":"+transactionTime+":"+" :Tow-away alert received with GPS Fix is 0, hence rejecting the packet");
						
						RejectedPacketDetailsEntity rejectedPkt = new RejectedPacketDetailsEntity();
						rejectedPkt.setCreatedTime(currentTime);
						rejectedPkt.setSerialNumber(serialNumber);
						rejectedPkt.setTransactionTime(parser.getTransactionTime());
						rejectedPkt.setRejectionCause("Tow-away Alert received with GPS Fix is 0, hence rejecting the packet");
						session.save(rejectedPkt);

//						archivedFile = new File(DataPackets_ProcessedFolderPath,processingFileName);
//						archivedFile.mkdirs();
//						if(archivedFile.exists())
//							archivedFile.delete();
//						boolean moveStatus = processingFile.renameTo(archivedFile);
//						if(moveStatus)
//						{
//							iLogger.info("ADI:"+serialNumber+":"+transactionTime+":"+"Tow-away alert received with GPS Fix is 0, hence rejecting the packet ");
//							iLogger.info("ADI:"+serialNumber+":"+transactionTime+":"+"File "+processingFile.getName()+" is moved successfully from processing to archived folder");
//						}

						
						//Send an email Notification stating the rejection
						String emailTo = prop.getProperty("JcbGenericMailId");
						String emailSubject = "Incorrect GPS: "+serialNumber+":"+transactionTime+":"+"Tow-away alert received with GPS Fix 0";
						String emailBody = " PIN: "+serialNumber+";Time & Date: " +transactionTime+";GPS Coordinate: "+currentLat+","+currentLong+";" +
						"Packet received: TOW-away; Message: Incorrect GPS coordinate received with GPS Fix 0 and hence packet rejected"	;
						emailBody = emailBody.replaceAll(";", "\n");
						
						EmailTemplate emailObj = new EmailTemplate();
						emailObj.setTo(emailTo);
						emailObj.setSubject(emailSubject);
						emailObj.setBody(emailBody);
						emailObj.setSerialNumber(serialNumber);
						emailObj.setTransactionTime(transactionTime);
						new EmailHandler().handleEmail("jms/queue/emailQ", emailObj,0);
						
						
						return "REJECTED";
					}
					
					
					
					//------------ 4.2.3 - Calculate Speed and Distance Travelled in KM of this machine since previous communication
					if(prevTxnLat!=null && prevTxnLong!=null && currentLat!=null && currentLong!=null)
					{
						double distanceInKm = new Geofencing().calDistance(Double.parseDouble(prevTxnLat),Double.parseDouble(prevTxnLong),
																			Double.parseDouble(currentLat),Double.parseDouble(currentLong));
						double diffInHours=(parser.getTransactionTime().getTime()-prevTxnEntity.getTransactionTime().getTime())/(60*60 * 1000.0);
						double speed=0;
						if(diffInHours!=0)
							speed = distanceInKm/diffInHours;
						
						if(speed>120 || distanceInKm>10 || distanceInKm<0.2)
						{
							rLogger.warn("ADI:"+serialNumber+":"+transactionTime+":"+" :Tow-away alert with incorrect GPS(with invalid Speed and distance)" +
																						", hence rejecting the packet");
							
							RejectedPacketDetailsEntity rejectedPkt = new RejectedPacketDetailsEntity();
							rejectedPkt.setCreatedTime(currentTime);
							rejectedPkt.setSerialNumber(serialNumber);
							rejectedPkt.setTransactionTime(parser.getTransactionTime());
							rejectedPkt.setRejectionCause("Tow-away Alert received with incorrect GPS(with invalid Speed and distance), hence rejecting the packet");
							session.save(rejectedPkt);

//							archivedFile = new File(DataPackets_ProcessedFolderPath,processingFileName);
//							archivedFile.mkdirs();
//							if(archivedFile.exists())
//								archivedFile.delete();
//							boolean moveStatus = processingFile.renameTo(archivedFile);
//							if(moveStatus)
//							{
//								iLogger.info("ADI:"+serialNumber+":"+transactionTime+":"+"Tow-away alert received with incorrect GPS(with invalid Speed and distance), hence rejecting the packet ");
//								iLogger.info("ADI:"+serialNumber+":"+transactionTime+":"+"File "+processingFile.getName()+" is moved successfully from processing to archived folder");
//							}

							
							//Send an email Notification stating the rejection
							String emailTo = prop.getProperty("JcbGenericMailId");
							String emailSubject = "Incorrect GPS: "+serialNumber+":"+transactionTime+":"+"Tow-away alert received with incorrect coordinates";
							String emailBody = " PIN: "+serialNumber+";Time & Date: " +transactionTime+";GPS Coordinate: "+currentLat+","+currentLong+";" +
							"Packet received: TOW-away; Message: Incorrect GPS coordinate received with invalid speed/distance and hence packet is rejected"	;
							emailBody = emailBody.replaceAll(";", "\n");
							
							EmailTemplate emailObj = new EmailTemplate();
							emailObj.setTo(emailTo);
							emailObj.setSubject(emailSubject);
							emailObj.setBody(emailBody);
							emailObj.setSerialNumber(serialNumber);
							emailObj.setTransactionTime(transactionTime);
							new EmailHandler().handleEmail("jms/queue/emailQ", emailObj,0);
							
							
							return "REJECTED";
						}
						
					}
					
				}
				
				else if( (eventParamValuesMap!=null) && ( (eventParamValuesMap.containsKey("EXTERNAL_BATTERY_REMOVED")) || (eventParamValuesMap.containsKey("MACHINE_BATTERY_LOW")) || 
						  (eventParamValuesMap.containsKey("HELLO") && machineStatus.equalsIgnoreCase("TRANSIT")) ||
						  (eventParamValuesMap.containsKey("EXTERNAL_BATTERY_REMOVED") && currentInternalBatteryLevel!=null && Long.valueOf(currentInternalBatteryLevel)<75)
						  ) && (prevTxnIgnitionStatus.equalsIgnoreCase("0"))
					    )
				{
					String packetReceived=null;
					if(eventParamValuesMap.containsKey("EXTERNAL_BATTERY_REMOVED"))
						packetReceived="EXTERNAL_BATTERY_REMOVED";
					if(packetReceived==null && (eventParamValuesMap.containsKey("MACHINE_BATTERY_LOW")))
						packetReceived="MACHINE_BATTERY_LOW";
					if(packetReceived==null && eventParamValuesMap.containsKey("HELLO") && machineStatus.equalsIgnoreCase("TRANSIT"))
						packetReceived="HELLO PACKET IN TRANSIT MODE";
					if(packetReceived==null && (eventParamValuesMap.containsKey("EXTERNAL_BATTERY_REMOVED") && currentInternalBatteryLevel!=null && Long.valueOf(currentInternalBatteryLevel)<75))
						packetReceived="LOW INTERNAL BATTERY CHARGE WITH EXTERNAL BATTERY REMOVED";
					
					if(prevTxnLat!=null && prevTxnLong!=null && currentLat!=null && currentLong!=null)
					{
						double distanceInKm = new Geofencing().calDistance(Double.parseDouble(prevTxnLat),Double.parseDouble(prevTxnLong),
											Double.parseDouble(currentLat),Double.parseDouble(currentLong));
						double diffInHours=(parser.getTransactionTime().getTime()-prevTxnEntity.getTransactionTime().getTime())/(60*60 * 1000.0);
						double speed=0;
						if(diffInHours!=0)
							speed = distanceInKm/diffInHours;

						if(speed>120 && !(eventParamValuesMap.containsKey("HELLO")))
						{
							locparamValuesMap.put("Latitude", prevTxnLat);
							locparamValuesMap.put("Longitude", prevTxnLong);
							hashMap.put("Location", locparamValuesMap);
							parser.setParamType_parametersMap(hashMap);
							
							currentLat = prevTxnLat;
							currentLong = prevTxnLong;
								
							iLogger.info("ADI:"+serialNumber+":"+transactionTime+":"+" Invalid GPS coordinates received in Ignition off condition, " +
									"hence replacing the Lat Long of the current packet. Packet received:"+packetReceived);
						}
						
						else if ( speed>120 && (eventParamValuesMap.containsKey("HELLO") && machineStatus.equalsIgnoreCase("TRANSIT")) )
						{
							rLogger.warn("ADI:"+serialNumber+":"+transactionTime+":"+" Invalid GPS coordinates received in Ignition off condition, " +
									"hence rejecting the packet. Packet received:"+packetReceived);
						
							RejectedPacketDetailsEntity rejectedPkt = new RejectedPacketDetailsEntity();
							rejectedPkt.setCreatedTime(currentTime);
							rejectedPkt.setSerialNumber(serialNumber);
							rejectedPkt.setTransactionTime(parser.getTransactionTime());
							rejectedPkt.setRejectionCause(" Invalid GPS coordinates received in Ignition off condition for Hello pkt in Transit mode");
							session.save(rejectedPkt);
							
//							archivedFile = new File(DataPackets_ProcessedFolderPath,processingFileName);
//							archivedFile.mkdirs();
//							if(archivedFile.exists())
//							archivedFile.delete();
//							boolean moveStatus = processingFile.renameTo(archivedFile);
//							if(moveStatus)
//							{
//								iLogger.info("ADI:"+serialNumber+":"+transactionTime+":"+"Invalid GPS coordinates received in Ignition off condition,, hence rejecting the packet. Packet received:"+packetReceived);
//								iLogger.info("ADI:"+serialNumber+":"+transactionTime+":"+"File "+processingFile.getName()+" is moved successfully from processing to archived folder");
//							}
							
							
							//Send an email Notification stating the rejection
							String emailTo = prop.getProperty("JcbGenericMailId");
							String emailSubject = "Incorrect GPS: "+serialNumber+":"+transactionTime+":"+"Invalid GPS coordinates received in Ignition off condition";
							String emailBody = " PIN: "+serialNumber+";Time & Date: " +transactionTime+";GPS Coordinate: "+currentLat+","+currentLong+";" +
							"Packet received:"+packetReceived+"; Message: Incorrect GPS coordinate received in Ignition Off condition and hence packet is rejected"	;
							emailBody = emailBody.replaceAll(";", "\n");
							
							EmailTemplate emailObj = new EmailTemplate();
							emailObj.setTo(emailTo);
							emailObj.setSubject(emailSubject);
							emailObj.setBody(emailBody);
							emailObj.setSerialNumber(serialNumber);
							emailObj.setTransactionTime(transactionTime);
							new EmailHandler().handleEmail("jms/queue/emailQ", emailObj,0);
							
							
							return "REJECTED";
						}
					}
				}
				iLogger.info("ADI:"+serialNumber+":"+transactionTime+":"+"Check for GPS bounce - END");
			}

			catch(Exception e)
			{
				fLogger.fatal("ADI:"+serialNumber+":"+transactionTime+":"+" :Exception :"+e);
				
				Writer result = new StringWriter();
				PrintWriter printWriter = new PrintWriter(result);
				e.printStackTrace(printWriter);
				String err = result.toString();
				fLogger.fatal("ADI:"+parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime()+" :Exception trace: "+err);
				try 
				{
					printWriter.close();
					result.close();
				} 
	
				catch (IOException e1) 
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				//DF20150112 - Rajani Nagaraju - IF DB Connection is not obtained for writing into Rejection Log, retain the file and exit
				if(e instanceof JDBCConnectionException || e instanceof SQLException || e instanceof GenericJDBCException)
				{
					if(e.getMessage().contains("Cannot open connection"))
					{
						return "FAILURE";
					}
				}
			}
		
			finally
			{
				try
				{
					if(session!=null && session.isOpen())  
						if(session.getTransaction().isActive())
						{
							session.getTransaction().commit();
						}
				}
				catch(Exception e)
				{
					fLogger.fatal("ADI:"+serialNumber+":"+transactionTime+":"+" :Exception :"+e);
				}
	
				if(session.isOpen())
				{
					if(session.getTransaction().isActive())
						session.flush();
					session.close();
				}

			}
		}
		
		catch(Exception e)
		{
			fLogger.fatal("ADI:"+serialNumber+":"+transactionTime+":"+" :Exception :"+e);
			
			Writer result = new StringWriter();
			PrintWriter printWriter = new PrintWriter(result);
			e.printStackTrace(printWriter);
			String err = result.toString();
			fLogger.fatal(serialNumber+":"+transactionTime+":"+" :Exception trace: "+err);
			try 
			{
				printWriter.close();
				result.close();
			} 

			catch (IOException e1) 
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			return "FAILURE";
		}
		
		
		//Create a new Thread to generate the Alert
		iLogger.info("ADI:"+serialNumber+":"+transactionTime+"Call AGS");
	//	new AlertGenerationImpl(parser);
		
//Df20160315 @Roopa Fix for one day date offset- FW Issue- But interium fix from the server side - START
	
		XmlParser newParser = new XmlParser();
		newParser.setFwVersionNumber(parser.getFwVersionNumber());
		newParser.setMessageId(parser.getMessageId());
		newParser.setSerialNumber(parser.getSerialNumber());
		newParser.setTransactionTime(parser.getTransactionTime());
		newParser.setVersion(parser.getVersion());
		/*new AlertGenerationImpl(parser, serialNumber, transactionTime, currentLat, currentLong, 
				currentGPSfix, currentInternalBatteryLevel, prevTxnIgnitionStatus, prevTxnEngineStatus,
				prevTxnLat, prevTxnLong, prevLogtransactionNumber);*/
		
		String convertedTs=String.valueOf(newParser.getTransactionTime());
		
		new AlertGenerationImpl(parser, serialNumber, convertedTs, currentLat, currentLong, 
				currentGPSfix, currentInternalBatteryLevel, prevTxnIgnitionStatus, prevTxnEngineStatus,
				prevTxnLat, prevTxnLong, prevLogtransactionNumber, null);
		
		//DF20150114 - Rajani Nagaraju - To_AWAY with value '0' was getting added to IGNITION_ON packet (frm ADS) and hence affecting alert generation
		//- was trying to close any open tow away alert - Because of the same parser object that is referred in ADS and AGS. Hence creating new parser
		// object for ADS and the manipulation of which doesnot affect AGS
		

		HashMap<String, HashMap<String,String>> newHas = new  HashMap<String, HashMap<String,String>>();
		HashMap<String, HashMap<String,String>> has = parser.getParamType_parametersMap();
		Iterator hasItr = has.entrySet().iterator();
		while(hasItr.hasNext())
		{
			Map.Entry pairs = (Map.Entry)hasItr.next();
			String st = (String)pairs.getKey();
			HashMap<String,String> hp = (HashMap<String,String>)pairs.getValue();
			HashMap<String,String> newHp = new  HashMap<String,String>();
			newHp.putAll(hp);
			newHas.put(st, newHp);
		}
		newParser.setParamType_parametersMap(newHas);

		iLogger.info("ADI:"+serialNumber+":"+transactionTime+"Call ADS");
		int txnNum=0;
		if(prevTxnEntity!=null)
			txnNum = prevTxnEntity.getTransactionNumber();
		
		String status = new AssetMonitoringDetailsBO().setRemoteMonitoringDataNew(newParser,processingFileName,serialNumber,convertedTs,txnNum,currentLat, currentLong);
		iLogger.info("ADI:"+serialNumber+":"+transactionTime+":status:"+status+"|Returned from ADS");
		
		return status;
	
	}
    
}
