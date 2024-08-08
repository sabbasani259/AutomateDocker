/**
 * 
 */
package remote.wise.service.implementation;

import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.hibernate.Session;


import remote.wise.businessobject.EAIntegrationBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
import remote.wise.service.datacontract.EAInterfaceDetailReqContract;
import remote.wise.service.datacontract.EAInterfaceDetailRespContract;
import remote.wise.service.datacontract.EAInterfaceReqContract;
import remote.wise.service.datacontract.EAInterfaceRespContract;
import remote.wise.util.CommonUtil;
import remote.wise.util.HibernateUtil;
//import remote.wise.util.WiseLogger;

/**
 * @author sunayak
 *
 */
public class EAInterfaceImpl {

	/*public static WiseLogger businessError = WiseLogger.getLogger("EAInterfaceImpl:","businessError");
	public static WiseLogger fatalError = WiseLogger.getLogger("EAIntegrationBO:","fatalError");*/

	private String InterfaceFileName;
	private int processedRecord ;
	private int rejectedRecord ;

	//DF20140813 - Rajani Nagaraju - Web based search for EA File Processing Status
	private String interfaceName;
	private String status;
	private String reasonForRejection;
	private String fileName;

	//DF20140813 - Suprava Nayak - Web based search for EA File Processing Status
	String record;
	String failureForRejection;
	int reProcessCount ;
	
	//DF20180404 - KO369761 - file processed time field was added for interface backtraking.
	String fileProcessedTime;

	public String getFileProcessedTime() {
		return fileProcessedTime;
	}

	public void setFileProcessedTime(String fileProcessedTime) {
		this.fileProcessedTime = fileProcessedTime;
	}

	/**
	 * @return the record
	 */
	public String getRecord() {
		return record;
	}

	/**
	 * @param record the record to set
	 */
	public void setRecord(String record) {
		this.record = record;
	}

	/**
	 * @return the failureForRejection
	 */
	public String getFailureForRejection() {
		return failureForRejection;
	}

	/**
	 * @param failureForRejection the failureForRejection to set
	 */
	public void setFailureForRejection(String failureForRejection) {
		this.failureForRejection = failureForRejection;
	}

	/**
	 * @return the reProcessCount
	 */
	public int getReProcessCount() {
		return reProcessCount;
	}

	/**
	 * @param reProcessCount the reProcessCount to set
	 */
	public void setReProcessCount(int reProcessCount) {
		this.reProcessCount = reProcessCount;
	}

	/**
	 * @return the interfaceFileName
	 */
	public String getInterfaceFileName() {
		return InterfaceFileName;
	}

	/**
	 * @param interfaceFileName the interfaceFileName to set
	 */
	public void setInterfaceFileName(String interfaceFileName) {
		InterfaceFileName = interfaceFileName;
	}

	/**
	 * @return the processedRecord
	 */
	public int getProcessedRecord() {
		return processedRecord;
	}

	/**
	 * @param processedRecord the processedRecord to set
	 */
	public void setProcessedRecord(int processedRecord) {
		this.processedRecord = processedRecord;
	}

	/**
	 * @return the rejectedRecord
	 */
	public int getRejectedRecord() {
		return rejectedRecord;
	}

	/**
	 * @param rejectedRecord the rejectedRecord to set
	 */
	public void setRejectedRecord(int rejectedRecord) {
		this.rejectedRecord = rejectedRecord;
	}


	/**
	 * @return the interfaceName
	 */
	public String getInterfaceName() {
		return interfaceName;
	}

	/**
	 * @param interfaceName the interfaceName to set
	 */
	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the reasonForRejection
	 */
	public String getReasonForRejection() {
		return reasonForRejection;
	}

	/**
	 * @param reasonForRejection the reasonForRejection to set
	 */
	public void setReasonForRejection(String reasonForRejection) {
		this.reasonForRejection = reasonForRejection;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public List<EAInterfaceRespContract> getEASummaryData(
			EAInterfaceReqContract reqObj) 
			{
		// TODO Auto-generated method stub
		Logger bLogger = BusinessErrorLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		
		String serialNumber = null ,interfaceName = null; //interfaceName 
		String serachDate = reqObj.getSearchDate();
		
		
		/**DF20190220 - Z1007653 :: To get Interface list for Interface functionality (Admin tab)
		 * Pattern of serialNumber now: 'serial_number#interface_name'
		 * Introducing logic to split (by #) and fetch serial number and interface name from 
		 * reqObj.getSerialNumber()*/
		try {
			if(reqObj.getSerialNumber() != null) {
				if(reqObj.getSerialNumber().split("#").length > 1) {
					interfaceName = reqObj.getSerialNumber().split("#")[1];
					serialNumber = reqObj.getSerialNumber().split("#")[0];
				}
				else
					serialNumber = reqObj.getSerialNumber();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			fLogger.error("Exception while splitting serial no. and interface name: "+ e1.getMessage());
		}
		//----DF20190220: Logic end----
		
		/*String serialNumber = reqObj.getSerialNumber();
		String serachDate = reqObj.getSearchDate();*/
		
		if(serialNumber!=null)
			serialNumber=serialNumber.trim();
		if(serachDate!=null)
			serachDate=serachDate.trim();

		List<EAInterfaceRespContract> respList = new LinkedList<EAInterfaceRespContract>();

		EAIntegrationBO eaIntegrationBO=new EAIntegrationBO();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		try
		{
			if((serialNumber==null || serialNumber.trim().length()==0) && (serachDate==null || serachDate.trim().length()==0))
				throw new CustomFault("SerialNumber or SerachDate Is mandatory");
			//DF20140813 - Suprava Nayak - Web based search for EA File Processing Status
			if((serialNumber!=null) && (serachDate!=null) && !serialNumber.equalsIgnoreCase("NA") && !serachDate.equalsIgnoreCase("NA"))
				throw new CustomFault("Either SerialNumber or SerachDate Is mandatory");

		}

		catch(CustomFault e)
		{
			bLogger.error("Custom Fault: "+ e.getFaultInfo());
			return respList;
		}


		try
		{
			List<EAInterfaceImpl> eaInterface = new LinkedList<EAInterfaceImpl>();

			//DF20140813 - Rajani Nagaraju - Web based search for EA File Processing Status
			if(reqObj.getSerialNumber()!=null && !reqObj.getSerialNumber().contains("NA"))
			{				
				//Check for Serial Number or Machine Number
				serialNumber=serialNumber.replaceFirst("^0+(?!$)", "");
				//AwakeID: JCB0269 - Rajani Nagaraju - Difference in Interface details when a machine is searched by 17 Digit Vin No and 7 Digit Machine No -START
				//When user enters complete 17 digit VIN, then get the machine number for the same, since machine number will be stored in EA_File_details and not the VIN
				if(serialNumber.length()>7)
				{
					serialNumber=serialNumber.substring(serialNumber.length()-7);
				}
				//AwakeID: JCB0269 - Rajani Nagaraju - Difference in Interface details when a machine is searched by 17 Digit Vin No and 7 Digit Machine No -END
				
				//DF20190220: Z1007653 - Adding parameter interfaceName to get details for a specific interface for a VIN.
				eaInterface = eaIntegrationBO.getEAVinSummaryDetails(serialNumber,interfaceName);

				//Converting the response in EAInterfaceRespContract return type
				for(int i=0;i<eaInterface.size();i++)
				{
					EAInterfaceRespContract respObj = new EAInterfaceRespContract();

					//DF20180411 - KO369761 - file processed time also added in the response.
					//respObj.setInterfaceName(eaInterface.get(i).getInterfaceName());
					if(eaInterface.get(i).getFileProcessedTime() != null)
						respObj.setInterfaceName(eaInterface.get(i).getInterfaceName()+"|"+eaInterface.get(i).getFileProcessedTime());
					else
						respObj.setInterfaceName(eaInterface.get(i).getInterfaceName()+"|");
					
					respObj.setStatus(eaInterface.get(i).getStatus());
					respObj.setReasonForRejection(eaInterface.get(i).getReasonForRejection());
					respObj.setFileName(eaInterface.get(i).getFileName());

					respList.add(respObj);
				}

			}			
			
			else
			{
				//DF20190220: Z1007653 - adding condition if Date search is selected as "Weekly"
				if(!"Week".equalsIgnoreCase(reqObj.getSearchDate()))
					eaInterface = eaIntegrationBO.getEADateSummaryDetails(reqObj.getSearchDate(),interfaceName);
				else if("Week".equalsIgnoreCase(reqObj.getSearchDate()))
					eaInterface = eaIntegrationBO.getEAWeekSummaryDetails(reqObj.getSearchDate(),interfaceName);

				for(int i=0;i<eaInterface.size();i++)
				{
					EAInterfaceRespContract respObj = new EAInterfaceRespContract();

					//DF20180411 - KO369761 - file processed time also added in the response.
					//respObj.setInterfaceFileName(eaInterface.get(i).getInterfaceFileName());
					if(eaInterface.get(i).getFileProcessedTime() != null)
						respObj.setInterfaceName(eaInterface.get(i).getInterfaceFileName()+"|"+eaInterface.get(i).getFileProcessedTime());
					else
						respObj.setInterfaceName(eaInterface.get(i).getInterfaceFileName()+"|");

					respObj.setProcessedRecord(eaInterface.get(i).getProcessedRecord());
					respObj.setRejectedRecord(eaInterface.get(i).getRejectedRecord());

					respList.add(respObj);
				}	

			}

			if(! (session.isOpen() ))
			{
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.getTransaction().begin();
			}


		}

		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("Exception :"+e);
		}

		finally
		{
			if(session.isOpen())
			{
				if(session.getTransaction().isActive())
				{
					session.getTransaction().commit();
				}
			}

			if(session.isOpen())
			{
				session.flush();
				session.close();
			}

		}

		return respList;	

			}

	//DF20140813 - Suprava Nayak - Web based search for EA File Processing Status
	public List<EAInterfaceDetailRespContract> getEADetailData(
			EAInterfaceDetailReqContract reqObj) {
		// TODO Auto-generated method stub
		Logger bLogger = BusinessErrorLoggerClass.logger;
		//Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
    	
		List<EAInterfaceDetailRespContract> respList = new LinkedList<EAInterfaceDetailRespContract>();

		EAIntegrationBO eaIntegrationBO=new EAIntegrationBO();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		try
		{
			if(reqObj.getFileName()==null || reqObj.getFileName().isEmpty())
				throw new CustomFault("SerialNumber or SerachDate Is mandatory");
		}

		catch(CustomFault e)
		{
			bLogger.error("Custom Fault: "+ e.getFaultInfo());
			return respList;
		}
		try{

			List<EAInterfaceImpl> eaInterface = new LinkedList<EAInterfaceImpl>();

			if(reqObj.getFileName()!=null && (!reqObj.getFileName().isEmpty()))
			{
				eaInterface = eaIntegrationBO.getEADetailData(reqObj.getFileName());

				for(int i=0;i<eaInterface.size();i++)
				{
					EAInterfaceDetailRespContract respObj = new EAInterfaceDetailRespContract();

					respObj.setRecord(eaInterface.get(i).getRecord());
					respObj.setFailureForRejection(eaInterface.get(i).getFailureForRejection());
					respObj.setReProcessCount(eaInterface.get(i).getReProcessCount());

					respList.add(respObj);
				}

			}
			if(! (session.isOpen() ))
			{
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.getTransaction().begin();
			}


		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("Exception :"+e);
		}

		finally
		{
			if(session.isOpen())
			{
				if(session.getTransaction().isActive())
				{
					session.getTransaction().commit();
				}
			}

			if(session.isOpen())
			{
				session.flush();
				session.close();
			}

		}
		return respList;
	}

}
