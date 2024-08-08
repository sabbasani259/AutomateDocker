package remote.wise.service.implementation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import jxl.SheetSettings;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.UnderlineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

////import org.apache.log4j.Logger;

import remote.wise.businessentity.ContactEntity;
import remote.wise.businessobject.ReportDetailsBO;
import remote.wise.exception.CustomFault;
import remote.wise.handler.SendEmail;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
import remote.wise.service.datacontract.MachineHoursServiceReportReqContract;
import remote.wise.service.datacontract.MachineHoursServiceReportRespContract;
import remote.wise.service.datacontract.NotificationSummaryRespContract;
import remote.wise.util.HibernateUtil;
import remote.wise.util.ListToStringConversion;
//import remote.wise.util.WiseLogger;
/**
 * 
 * @author tejgm
 * Implenention gets the brief description on the time taken by the machine.
 */
public class MachineHoursServiceReportImpl {
	
	//DF:2013:12:13 : Conversion from Long to Double for all the required fields : Suprava
	//public static WiseLogger businessError = WiseLogger.getLogger("MachineHoursServiceReportImpl:","businessError");
	private int tenancyId;
	private String TenancyName;
	private int MachineGroupId;
	private String MachineGroupName; 
	private int MachineProfileId;// AssetGroupId, TABLE-asset_class_dimension
	private String MachineProfileName;//AssetGroupName,TABLE-asset_class_dimension 
	private int  ModelId;// AssetTypeId,TABLE-asset_class_dimension
	private String ModelName;// AssetTypeName,TABLE-asset_class_dimension
	private String SerialNumber;
	
	
	private String NickName;
	private double TotalMachineLifeHours;	
	//private int serviceScheduleId;//LastScheduledServicedHours;	 
//	private int durationSchedule;//LastScheduledServicedHours
//	private int durationSchedule1;//NextPlannedServiceHours   
	private double LastServiceHour;
	private String severity;//added by smitha
	private String dealerName;
	
	
	
	/**
	 * @return the dealerName
	 */
	public String getDealerName() {
		return dealerName;
	}
	/**
	 * @param dealerName the dealerName to set
	 */
	public void setDealerName(String dealerName) {
		this.dealerName = dealerName;
	}
	
	
	/**
	 * @return the eventId
	 */
	
	public String getSeverity() {
		return severity;
	}
	/**
	 * @return the eventId
	 */
	
	public void setSeverity(String severity) {
		this.severity = severity;
	}
	public double getLastServiceHour() {
		return LastServiceHour;
	}
	public void setLastServiceHour(double lastServiceHour) {
		LastServiceHour = lastServiceHour;
	}
	private String ApproximateServiceDate;
	public String getApproximateServiceDate() {
		return ApproximateServiceDate;
	}
	public void setApproximateServiceDate(String approximateServiceDate) {
		ApproximateServiceDate = approximateServiceDate;
	}
	private double hoursToNextService;
	public double getHoursToNextService() {
		return hoursToNextService;
	}
	public void setHoursToNextService(double hoursToNextService) {
		this.hoursToNextService = hoursToNextService;
	}
	private String ScheduleName;
	public String getScheduleName() {
		return ScheduleName;
	}
	public void setScheduleName(String scheduleName) {
		ScheduleName = scheduleName;
	}
	private String ServiceName;//LastActualServicedDate;
	public String getServiceName() {
		return ServiceName;
	}
	public void setServiceName(String serviceName) {
		ServiceName = serviceName;
	}
	private String serviceDate;//LastActualServicedDate;
	//private String scheduleDate;//NextPlannedServiceDate;
	private String Location;
	private String Status;
//	private String scheduledDateList;//NextPlannedServiceDate;
	
//	private String lastService;//ServiceName,TABLE-Service_Schedule
    private String nextService;//serviceName, TABLE-asset_Service_Schedule
//    private String serviceSchedule;//serviceScheduleName ,TABLE-product_profile
    
    Logger iLogger = InfoLoggerClass.logger;
    
    Logger errorLogger=FatalLoggerClass.logger;
	/**
	 * 
	 * @param reqObj passing reqObj as a input to get brief description on the time taken by the machine that also includes idle time.
	 * @return listResponse gets the list of response as the time taken by the asset in a perticular location that also includes services that are scheduled for a individual machine
	 */
	public List<MachineHoursServiceReportRespContract> getMachineHoursService(MachineHoursServiceReportReqContract reqObj)
	{
		Logger bLogger = BusinessErrorLoggerClass.logger;
		
		
	//	Logger businessError = Logger.getLogger("businessErrorLogger");
		List<MachineHoursServiceReportRespContract> listResponse=new LinkedList<MachineHoursServiceReportRespContract>();
		ReportDetailsBO reportDetailsBo=new ReportDetailsBO();
		//Changes Done by Juhi On 6 May 2013
		String loginId="";
		loginId=reqObj.getLoginId();
		//System.out.println("loginId----------"+loginId);
		iLogger.info("loginId---------"+loginId);
		try
		{
			//Custom Dates (fromDate,toDate) added by Juhi on 13-August-2013 
//			if(((reqObj.getPeriod()==null))&&(reqObj.getFromDate()==null) && (reqObj.getToDate()==null))
			/*if((reqObj.getFromDate()==null) && (reqObj.getToDate()==null))
			{
				businessError.error("Please pass either Period or custom dates");
				throw new CustomFault("Either Period or custom dates are not specified");
			}*/
			
			if(reqObj.getTenancyIdList()==null)
			{
				throw new CustomFault("Tenancy Id List should be specified");
			}
			
			
			/*if(reqObj.getPeriod()!=null)
            {
                  if( !(reqObj.getPeriod().equalsIgnoreCase("Week") || reqObj.getPeriod().equalsIgnoreCase("Month") ||
                              reqObj.getPeriod().equalsIgnoreCase("Quarter") || reqObj.getPeriod().equalsIgnoreCase("Year") ||
                              reqObj.getPeriod().equalsIgnoreCase("Last Week") ||      reqObj.getPeriod().equalsIgnoreCase("Last Month") ||
                              reqObj.getPeriod().equalsIgnoreCase("Last Quarter") || reqObj.getPeriod().equalsIgnoreCase("Last Year") ) )
                  {
                        throw new CustomFault("Invalid Time Period");
                        
                  }
      }*/

		}
		
		catch(CustomFault e)
		{
			bLogger.error("Custom Fault: "+ e.getFaultInfo());
		}
		//Changes Done by Juhi On 6 June 2013
		MachineHoursServiceReportRespContract respObj=null;
//		List<MachineHoursServiceReportImpl> implObj=reportDetailsBo.getMachineHoursService(reqObj.getFromDate(),reqObj.getToDate(),reqObj.getPeriod(),reqObj.getMachineGroupIdList(),reqObj.getMachineProfileIdList(),reqObj.getTenancyIdList(),reqObj.getModelIdList(),reqObj.isGroupingOnAssetGroup());
		//Defect Id:1406 Added loginTenancyIdList by Juhi on 29-October-2013
		List<MachineHoursServiceReportImpl> implObj=reportDetailsBo.getMachineHoursService(reqObj.getMachineGroupIdList(),reqObj.getMachineProfileIdList(),reqObj.getTenancyIdList(),reqObj.getModelIdList(),reqObj.isGroupingOnAssetGroup(),reqObj.getLoginTenancyIdList());
		
		ListToStringConversion conversionObj = new ListToStringConversion();
        String tenancyIDList = conversionObj.getIntegerListString(reqObj.getLoginTenancyIdList()).toString();
        /*String contactQuery = "select c from AccountTenancyMapping a_t,AccountContactMapping a_c,ContactEntity c" +
                      " where a_t.tenancy_id in ("+tenancyIDList+") and a_c.account_id = a_t.account_id and c.contact_id = a_c.contact_id and c.active_status=1";*/
        
     // DefectID 20160318 @Kishore code changes for sending report over mail to the login id
        String contactQuery = "from ContactEntity c" +
                " where c.contact_id = '"+loginId+"'"; 
        iLogger.info("Machine hours service report contact query---------"+contactQuery);
        Session session= null;
       
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();                  
            Query query = session.createQuery(contactQuery);
            Iterator itr = query.list().iterator();
            String userRole = null;
            ContactEntity contact = null;
            if(itr.hasNext())
            {
               contact = (ContactEntity)itr.next();
            }
            String loginID = contact.getContact_id();
            iLogger.info("loginID---------"+loginID);
            userRole = contact.getRole().getRole_name();
            iLogger.info("userRole---------"+userRole);
            
            
            //Added by Roopa @DF20160307 for repprt email path need to be read from properties file
            
        	String reportsPath=null,reportsDir=null;
    		Properties prop1 = new Properties();
    		try{
    			prop1.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
    			
    			if (prop1.getProperty("deployenvironment").equalsIgnoreCase("SIT")) {
    				reportsPath= prop1.getProperty("reportsPath");
    				reportsDir = prop1.getProperty("reportsDir");
    			} else if (prop1.getProperty("deployenvironment").equalsIgnoreCase("DEV")) {
    				reportsPath= prop1.getProperty("reportsPath");
    				reportsDir = prop1.getProperty("reportsDir");
    			} else if (prop1.getProperty("deployenvironment").equalsIgnoreCase("QA")) {
    				reportsPath= prop1.getProperty("reportsPath");
    				reportsDir = prop1.getProperty("reportsDir");
    			} else if (prop1.getProperty("deployenvironment").equalsIgnoreCase("PROD")) {
    				reportsPath= prop1.getProperty("reportsPath_PROD");
    				reportsDir = prop1.getProperty("reportsDir_PROD");
    			} else {
    				reportsPath= prop1.getProperty("reportsPath");
    				reportsDir = prop1.getProperty("reportsDir");
    			}

    		}
    		catch(Exception e){
    			e.printStackTrace();
    		}
           
		 //Added by Kishore @DF20160106 for excel changes(Report will be sent as an email for the login user, user must be either JCB Account or Customer Care or JCB HO)                
    		//CR469:20408644:Sai Divya  roleName Changed from JCB Admin to JCB Account 
         if((userRole!=null) && (userRole.equalsIgnoreCase("JCB Account") || userRole.equalsIgnoreCase("Customer Care") || userRole.equalsIgnoreCase("JCB HO"))){
        	 iLogger.info("service hours report sending as a mail process start---------");
        	 // ***********   New excel changes by Kishore ****************** //        	 
             // ***********  Appending the current date to excel file name for Unique identification *****  //
        	 
             Date d=new Date();
             String jasperDate = null;
             SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
  
             try {
                   jasperDate = sdf.format(d);
             }
             catch(Exception e){
                   errorLogger.error(e.getMessage());
             }
  
             //String FileName="Machine_Hours_Service_Report_"+jasperDate+".xls";
  
             try {
  
            	// File tempFile = new File("D:/" + "Service_MachineHours_Report_"+loginID+"_"+jasperDate+".xls");
            	File tempFile = new File(reportsDir+"Service_MachineHours_Report_"+loginID+"_"+jasperDate+".xls");
            	 String FileName = tempFile.getAbsolutePath();
  
                   WorkbookSettings ws = new WorkbookSettings();
                   WritableWorkbook workbook = Workbook.createWorkbook(new File(
                               FileName), ws);
  
                   WritableSheet workSheet = null;
                   workSheet = workbook.createSheet("Machine_Hours_Service_Report", 0);
  
                   SheetSettings sh = workSheet.getSettings();
                   WritableFont normalFont = new WritableFont(
                               WritableFont.createFont("Calibri"),
                               WritableFont.DEFAULT_POINT_SIZE, WritableFont.BOLD,
                               false, UnderlineStyle.NO_UNDERLINE);
                   WritableCellFormat normalFormat1 = new WritableCellFormat(
                               normalFont);
  
                   normalFormat1.setAlignment(jxl.format.Alignment.JUSTIFY);
                   normalFormat1.setVerticalAlignment(VerticalAlignment.CENTRE);
  
                   normalFormat1.setBorder(jxl.format.Border.ALL,
                               jxl.format.BorderLineStyle.THIN,
                               jxl.format.Colour.BLACK);
  
                   if(implObj.size()>0){
                	   iLogger.info("service hours report excel generation start---------");
                         WritableCellFormat normalFormat = new WritableCellFormat(
                                     normalFont);
                         normalFormat.setAlignment(jxl.format.Alignment.CENTRE);
                         normalFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
                         normalFormat.setBackground(jxl.format.Colour.GRAY_25);
                         normalFormat.setWrap(true);
                         normalFormat.setBorder(jxl.format.Border.ALL,
                                     jxl.format.BorderLineStyle.THIN,
                                     jxl.format.Colour.BLACK);
  
                         WritableCellFormat normalFormat3 = new WritableCellFormat(
                                     normalFont);
                         normalFormat3.setAlignment(jxl.format.Alignment.CENTRE);               
                         normalFormat3.setVerticalAlignment(VerticalAlignment.CENTRE);
                         normalFormat3.setWrap(true);
                         normalFormat3.setBorder(jxl.format.Border.ALL,
                                     jxl.format.BorderLineStyle.THIN,
                                     jxl.format.Colour.BLACK);
  
  
                         // first two columns justified,rest centered
                         workSheet.setColumnView(0, 10);
                         workSheet.setColumnView(1, 20);
                         workSheet.setColumnView(2, 15);
                         workSheet.setColumnView(8, 40);
                         workSheet.addCell(new jxl.write.Label(0, 0, " Machine",
                                     normalFormat));
                         workSheet.addCell(new jxl.write.Label(1, 0, "Schedule Name",
                                     normalFormat));
                         workSheet.addCell(new jxl.write.Label(2, 0, "Total Machine Life Hours",
                                     normalFormat));
                         workSheet.addCell(new jxl.write.Label(3, 0, "Last Service Name",
                                     normalFormat));
                         workSheet.addCell(new jxl.write.Label(4, 0, "Last Service Date",
                                     normalFormat));
                         workSheet.addCell(new jxl.write.Label(5, 0, "Last Service Hours",
                                     normalFormat));
                         workSheet.addCell(new jxl.write.Label(6, 0,
                                     "Next Service Name", normalFormat));
                         workSheet.addCell(new jxl.write.Label(7, 0,
                                     "Hours to Next Service", normalFormat));
                         workSheet.addCell(new jxl.write.Label(8, 0, "Approximate Service Date",
                                     normalFormat));
                         workSheet.addCell(new jxl.write.Label(9, 0, "Location",
                                     normalFormat));
                         workSheet.addCell(new jxl.write.Label(10, 0, "Status",
                                     normalFormat));
                         workSheet.addCell(new jxl.write.Label(11, 0, "Dealer Name",
                                     normalFormat));
                         int p = 0;
                         for(int i=0;i<implObj.size();i++){
                               workSheet.addCell(new jxl.write.Label(0, p + 1,
                            		   implObj.get(i).getSerialNumber(), normalFormat1));
  
                               workSheet.addCell(new jxl.write.Label(1, p + 1,
                            		   implObj.get(i).getScheduleName(), normalFormat3));
  
                               workSheet.addCell(new jxl.write.Label(2, p + 1,
                            		   String.valueOf(implObj.get(i).getTotalMachineLifeHours()), normalFormat3));
  
                               workSheet.addCell(new jxl.write.Label(3, p + 1,
                            		   implObj.get(i).getServiceName(), normalFormat3));
  
                               workSheet.addCell(new jxl.write.Label(4, p + 1,
                                           (implObj.get(i).getServiceDate()==null?"":implObj.get(i).getServiceDate().substring( 0, 10 )), normalFormat3));
  
                               workSheet.addCell(new jxl.write.Label(5, p + 1,
                                           (String.valueOf(implObj.get(i).getLastServiceHour())=="0"?"":String.valueOf(implObj.get(i).getLastServiceHour())), normalFormat3));
  
                               workSheet.addCell(new jxl.write.Label(6, p + 1,
                            		   implObj.get(i).getNextService(), normalFormat3));
  
                               workSheet.addCell(new jxl.write.Label(7, p + 1,
                            		   String.valueOf(implObj.get(i).getHoursToNextService()), normalFormat3));
  
                               workSheet.addCell(new jxl.write.Label(8, p + 1,
                                           (implObj.get(i).getApproximateServiceDate()==null?"":implObj.get(i).getApproximateServiceDate().substring( 0, 10 )), normalFormat3));
  
                               workSheet.addCell(new jxl.write.Label(9, p + 1,
                            		   implObj.get(i).getLocation(), normalFormat3));
  
                               workSheet.addCell(new jxl.write.Label(10, p + 1,
                            		   implObj.get(i).getStatus(), normalFormat3));
  
                               workSheet.addCell(new jxl.write.Label(11, p + 1,
                            		   implObj.get(i).getDealerName(), normalFormat3));
                               p++;
                         }
                         iLogger.info("service hours report excel generation end---------");
                   }
  
                        
  
                   else {
  
  
                         workSheet.setColumnView(0, 15);
                         workSheet.addCell(new jxl.write.Label(0, 0,
                                     "NO DATA FOUND", normalFormat1));
                   }
                   workbook.write();
                   workbook.close();
                  iLogger.info("Excel file has been created---");
                 
  
             }
             catch (Exception e) {
                   errorLogger.error(e.getMessage());
                   errorLogger.error("Error in generating excel report"
                               + e.getMessage());
  
             }
             //end
             
            
             
             File sourceFile = new File(reportsDir+ "Service_MachineHours_Report_"+loginID+"_"+jasperDate+".xls");
            // File sourceFile = new File("D:/" + "Service_MachineHours_Report_"+loginID+"_"+jasperDate+".xls");
             iLogger.info("Service Machine Hours report source File:"+sourceFile);
             SendEmail email = new SendEmail();
            // String fileToBeAttached = "D:/" + "Service_MachineHours_Report_"+loginID+"_"+jasperDate+".xls";
             String fileToBeAttached = reportsDir+"Service_MachineHours_Report_"+loginID+"_"+jasperDate+".xls";
            // String loginTenancyID = reqobj.getLoginId();
             iLogger.info("Service Machine Hours report loginID:"+loginID);
             
             String ReceiverMail= contact.getPrimary_email_id();
             iLogger.info("Service Machine Hours report ReceiverMail:"+ReceiverMail);
             FileOutputStream fos;
             String serviceHoursfilesToBeAttcahed = null;
 			try {
 				fos = new FileOutputStream(reportsDir+"Service_MachineHours_Report_"+loginID+"_"+jasperDate+".zip");
 				serviceHoursfilesToBeAttcahed = reportsDir+"Service_MachineHours_Report_"+loginID+"_"+jasperDate+".zip";
 				iLogger.info("fileToBeAttcahed :"+serviceHoursfilesToBeAttcahed);
 				ZipOutputStream zos = new ZipOutputStream(fos);
 				addToZipFile(fileToBeAttached,zos);
 				zos.close();
 				fos.close();
 			} catch (FileNotFoundException e) {
 				// TODO Auto-generated catch block
 				errorLogger.fatal(e);
 			}
 			//String fileToBeAttcahed= fileToBeAttcahed1;
 			catch (IOException e) {
 				// TODO Auto-generated catch block
 				errorLogger.fatal(e);
 			}
                 
             //String emailTo = "soorneedi.babu@wipro.com;deepthi.rao@wipro.com";
             String emailSubject = "Service machine hours report";
             String emailBody = "Dear Customer \r\n \r\n Please find the attcheched Service Machine hours report.\r\n \r\n \r\nThanks, \r\n Deepthi. ";
          //   email.sendMailWithAttachment(ReceiverMail, emailSubject, emailBody, fileToBeAttached);
             email.sendMailWithAttachment(ReceiverMail, emailSubject, emailBody, serviceHoursfilesToBeAttcahed);
             File zipFile = new File(reportsDir+"Service_MachineHours_Report_"+loginID+"_"+jasperDate+".zip");
				zipFile.delete();
             sourceFile.delete();
 
         }
         else{
        	 
        
		
		for(int i=0;i<implObj.size();i++)
		{
			respObj=new MachineHoursServiceReportRespContract();
			respObj.setTenancyId(implObj.get(i).getTenancyId());
			respObj.setTenancyName(implObj.get(i).getTenancyName());		
			respObj.setSerialNumber(implObj.get(i).getSerialNumber());		
			respObj.setMachineGroupId(implObj.get(i).getMachineGroupId());
			respObj.setMachineGroupName(implObj.get(i).getMachineGroupName());
			respObj.setMachineProfileId(implObj.get(i).getMachineProfileId());
			respObj.setMachineProfileName(implObj.get(i).getMachineProfileName());	
			respObj.setModelId(implObj.get(i).getModelId());
			respObj.setModelName(implObj.get(i).getModelName());
			respObj.setStatus(implObj.get(i).getStatus());
			respObj.setLocation(implObj.get(i).getLocation());
			
			if(implObj.get(i).getTotalMachineLifeHours()<0)
				respObj.setTotalMachineLifeHours(0);
			else
				respObj.setTotalMachineLifeHours(implObj.get(i).getTotalMachineLifeHours());		
			respObj.setNickName(implObj.get(i).getNickName());
			respObj.setServiceName(implObj.get(i).getServiceName());
	
			respObj.setServiceDate(implObj.get(i).getServiceDate());
			respObj.setScheduleName(implObj.get(i).getScheduleName());
			respObj.setLastServiceHour(implObj.get(i).getLastServiceHour());
			respObj.setScheduleName(implObj.get(i).getScheduleName());
			respObj.setNextService(implObj.get(i).getNextService());
			respObj.setApproximateServiceDate(implObj.get(i).getApproximateServiceDate());
			respObj.setHoursToNextService(implObj.get(i).getHoursToNextService());
			respObj.setSeverity(implObj.get(i).getSeverity());
			respObj.setDealerName(implObj.get(i).getDealerName());
	    	listResponse.add(respObj);
			
		}
         }
		return listResponse;
		
	}
	public int getTenancyId() {
		return tenancyId;
	}
	public void setTenancyId(int tenancyId) {
		this.tenancyId = tenancyId;
	}
	public String getTenancyName() {
		return TenancyName;
	}
	public void setTenancyName(String tenancyName) {
		TenancyName = tenancyName;
	}
	public int getMachineGroupId() {
		return MachineGroupId;
	}
	public void setMachineGroupId(int machineGroupId) {
		MachineGroupId = machineGroupId;
	}
	public String getMachineGroupName() {
		return MachineGroupName;
	}
	public void setMachineGroupName(String machineGroupName) {
		MachineGroupName = machineGroupName;
	}
	public int getMachineProfileId() {
		return MachineProfileId;
	}
	public void setMachineProfileId(int machineProfileId) {
		MachineProfileId = machineProfileId;
	}
	public String getMachineProfileName() {
		return MachineProfileName;
	}
	public void setMachineProfileName(String machineProfileName) {
		MachineProfileName = machineProfileName;
	}
	public int getModelId() {
		return ModelId;
	}
	public void setModelId(int modelId) {
		ModelId = modelId;
	}
	public String getModelName() {
		return ModelName;
	}
	public void setModelName(String modelName) {
		ModelName = modelName;
	}
	public String getSerialNumber() {
		return SerialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		SerialNumber = serialNumber;
	}
	public String getNickName() {
		return NickName;
	}
	public void setNickName(String nickName) {
		NickName = nickName;
	}
	public double getTotalMachineLifeHours() {
		return TotalMachineLifeHours;
	}
	public void setTotalMachineLifeHours(double totalMachineLifeHours) {
		TotalMachineLifeHours = totalMachineLifeHours;
	}
	
	public String getServiceDate() {
		return serviceDate;
	}
	public void setServiceDate(String serviceDate) {
		this.serviceDate = serviceDate;
	}
	public String getLocation() {
		return Location;
	}
	public void setLocation(String location) {
		Location = location;
	}
	public String getStatus() {
		return Status;
	}
	public void setStatus(String status) {
		Status = status;
	}
	
	/*public int getDurationSchedule() {
		return durationSchedule;
	}
	public void setDurationSchedule(int durationSchedule) {
		this.durationSchedule = durationSchedule;
	}
	public int getDurationSchedule1() {
		return durationSchedule1;
	}
	public void setDurationSchedule1(int durationSchedule1) {
		this.durationSchedule1 = durationSchedule1;
	}
	public String getScheduledDateList() {
		return scheduledDateList;
	}
	public void setScheduledDateList(String scheduledDateList) {
		this.scheduledDateList = scheduledDateList;
	}*/
	/*public String getLastService() {
		return lastService;
	}
	public void setLastService(String lastService) {
		this.lastService = lastService;
	}*/
	public String getNextService() {
		return nextService;
	}
	public void setNextService(String nextService) {
		this.nextService = nextService;
	}
	/*public String getServiceSchedule() {
		return serviceSchedule;
	}
	public void setServiceSchedule(String serviceSchedule) {
		this.serviceSchedule = serviceSchedule;
	}*/
	
	public void addToZipFile(String fileName, ZipOutputStream zos) throws FileNotFoundException, IOException {
		Logger infoLogger = InfoLoggerClass.logger;
		infoLogger.info("Writing '" + fileName + "' to zip file");

		File file = new File(fileName);
		FileInputStream fis = new FileInputStream(file);
		ZipEntry zipEntry = new ZipEntry(fileName);
		zos.putNextEntry(zipEntry);

		byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0) {
			zos.write(bytes, 0, length);
		}

		zos.closeEntry();
		fis.close();
	}
	

}
