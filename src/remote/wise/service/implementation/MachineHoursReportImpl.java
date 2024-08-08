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
import remote.wise.service.datacontract.MachineHoursReportReqContract;
import remote.wise.service.datacontract.MachineHoursReportRespContract;
//import remote.wise.util.WiseLogger;
import remote.wise.util.HibernateUtil;
import remote.wise.util.ListToStringConversion;

public class MachineHoursReportImpl {
	
	//DF:2013:12:13 : Conversion from Long to Double for all the required fields : Suprava
	//DefectId:1337 - Suprava - 20130923 - Log4j Changes - Using static logger object all throughout the application
//	public static WiseLogger businessError = WiseLogger.getLogger("MachineHoursReportImpl:","businessError");
	
	private String machineGroupName;//TABLE-machine_group_dimension
	private int machineGroupId;
	private String machineName;
	private int machineProfileId;
    private double TotalMachineHours;
	private double MachineHours;//InPeriod;
	private String status;
	private long workingTime;//durationInStatus;
	private Timestamp lastEngineRun;
	private Timestamp lastReported;
	private String serialNumber;
	private int tenancyId;
	private String machineProfileName;
	//DefectID:1383 - Rajani Nagaraju - 20130930 - Query modification to return correct resultset
    private int modelId;
    private String modelName;
    
	/**
	 * @return the tenancyId
	 */
	public int getTenancyId() {
		return tenancyId;
	}


	/**
	 * @param tenancyId the tenancyId to set
	 */
	public void setTenancyId(int tenancyId) {
		this.tenancyId = tenancyId;
	}


	public String getSerialNumber() {
		return serialNumber;
	}


	/**
	 * @return the machineGroupId
	 */
	public int getMachineGroupId() {
		return machineGroupId;
	}


	/**
	 * @param machineGroupId the machineGroupId to set
	 */
	public void setMachineGroupId(int machineGroupId) {
		this.machineGroupId = machineGroupId;
	}


	/**
	 * @param totalMachineHours the totalMachineHours to set
	 */
	public void setTotalMachineHours(int totalMachineHours) {
		TotalMachineHours = totalMachineHours;
	}


	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}


	public Timestamp getLastReported() {
		return lastReported;
	}


	public void setLastReported(Timestamp lastReported) {
		this.lastReported = lastReported;
	}


	private String location;
	
	private String tenancyName;
	private String assetGroupName;
	private long durationInCurrentStatus;
	//DefectId:20150220 @ Suprava Added New Parameter
	private String dealerName;
	
	public String getDealerName() {
		return dealerName;
	}


	public void setDealerName(String dealerName) {
		this.dealerName = dealerName;
	}


	public String getTenancyName() {
		return tenancyName;
	}


	public void setTenancyName(String tenancyName) {
		this.tenancyName = tenancyName;
	}


	public String getAssetGroupName() {
		return assetGroupName;
	}


	public void setAssetGroupName(String assetGroupName) {
		this.assetGroupName = assetGroupName;
	}


	public long getDurationInCurrentStatus() {
		return durationInCurrentStatus;
	}


	public void setDurationInCurrentStatus(long durationInCurrentStatus) {
		this.durationInCurrentStatus = durationInCurrentStatus;
	}


	public String getMachineGroupName() {
		return machineGroupName;
	}


	public void setMachineGroupName(String machineGroupName) {
		this.machineGroupName = machineGroupName;
	}


	public String getMachineName() {
		return machineName;
	}


	public void setMachineName(String machineName) {
		this.machineName = machineName;
	}
	public int getMachineProfileId() {
		return machineProfileId;
	}


	public void setMachineProfileId(int machineProfileId) {
		this.machineProfileId = machineProfileId;
	}

	


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	


	public double getTotalMachineHours() {
		return TotalMachineHours;
	}


	public void setTotalMachineHours(double totalMachineHours) {
		TotalMachineHours = totalMachineHours;
	}


	public double getMachineHours() {
		return MachineHours;
	}


	public void setMachineHours(double machineHours) {
		MachineHours = machineHours;
	}


	public long getWorkingTime() {
		return workingTime;
	}


	public void setWorkingTime(long workingTime) {
		this.workingTime = workingTime;
	}


	public Timestamp getLastEngineRun() {
		return lastEngineRun;
	}


	public void setLastEngineRun(Timestamp lastEngineRun) {
		this.lastEngineRun = lastEngineRun;
	}
	public String getMachineProfileName() {
		return machineProfileName;
	}

	public void setMachineProfileName(String machineProfileName) {
		this.machineProfileName = machineProfileName;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}


	public int getModelId() {
		return modelId;
	}


	public void setModelId(int modelId) {
		this.modelId = modelId;
	}


	
	public String getModelName() {
		return modelName;
	}


	public void setModelName(String modelName) {
		this.modelName = modelName;
	}


	public List<MachineHoursReportRespContract> getMachineHoursReportService(MachineHoursReportReqContract reqObj) 
	{
		List<MachineHoursReportRespContract> listRespObj=new LinkedList<MachineHoursReportRespContract>(); 
		ReportDetailsBO reportObj=new ReportDetailsBO();
		//Added logger by Juhi On 13 August 2013
		//Logger businessError = Logger.getLogger("businessErrorLogger");
		Logger bLogger = BusinessErrorLoggerClass.logger;
		Logger infoLogger = InfoLoggerClass.logger;
		//DefectID:1406 - Rajani Nagaraju - 20131028 - MachineGrouping issue in Reports and sending Report Totals information
		String loginId = "";
		try
		{
			
			// DefectID 20160318 @Kishore code changes for sending report over mail to the login id
			//System.out.println(reqObj.getPeriod());
			infoLogger.info("reqObj.getPeriod()"+reqObj.getPeriod());
			
			
			if(reqObj.getPeriod()!=null)
			{
				String[] periodSplit = reqObj.getPeriod().split("\\|");
				if(periodSplit.length>0)
					loginId = periodSplit[0];
				
				if(periodSplit.length>1)
					reqObj.setPeriod(periodSplit[1]);
				else
					reqObj.setPeriod(null);
			}
			//System.out.println("loginId----"+loginId);
			infoLogger.info("loginId------"+loginId);
			
			if(reqObj.getLoginTenancyIdList()==null || reqObj.getLoginTenancyIdList().isEmpty())
			{
				throw new CustomFault("Login TenancyId not specified");
			}
			
			if(reqObj.getPeriod()!=null)
			{
				if( !(reqObj.getPeriod().equalsIgnoreCase("Week") || reqObj.getPeriod().equalsIgnoreCase("Month") ||
						reqObj.getPeriod().equalsIgnoreCase("Quarter") || reqObj.getPeriod().equalsIgnoreCase("Year") ||
						reqObj.getPeriod().equalsIgnoreCase("Last Week") ||	reqObj.getPeriod().equalsIgnoreCase("Last Month") ||
						reqObj.getPeriod().equalsIgnoreCase("Last Quarter") || reqObj.getPeriod().equalsIgnoreCase("Last Year") ) )
				{
					throw new CustomFault("Invalid Time Period");
					
				}
			}
			//Custom Dates (fromDate,toDate) added by Juhi on 13-August-2013 
			//DefectId:DF20131011  - Custom Date Implementation - 20131011 - Rajani Nagaraju - && changed to || here - since for custom dates, both fromDate and todate is required
			if(((reqObj.getPeriod()==null))&&((reqObj.getFromDate()==null) || (reqObj.getToDate()==null)))
			{
				throw new CustomFault("Either Period or custom dates are not specified");
			}
			
			if(reqObj.getTenancyIdList()==null || reqObj.getTenancyIdList().isEmpty())
			{
				throw new CustomFault("Tenancy Id List should be specified");
			}
			infoLogger.info("start of Machine hours report service------------");
			List<MachineHoursReportImpl> implObj=reportObj.getMachineHoursReportService(reqObj.getFromDate(),reqObj.getToDate(),reqObj.getPeriod(),
												reqObj.getMachineGroupIdList(),reqObj.getMachineProfileIdList(),reqObj.getTenancyIdList(),
												reqObj.getModelList(), reqObj.isGroupingOnAssetGroup(), reqObj.getLoginTenancyIdList(), 0, 0);
	
			MachineHoursReportRespContract respObj=null;
			 ListToStringConversion conversionObj = new ListToStringConversion();
				String tenancyIDList = conversionObj.getIntegerListString(reqObj.getLoginTenancyIdList()).toString();
				infoLogger.info("start of Machine hours report service after tenancyIDList------------");
		    	/*String contactQuery = "select c from AccountTenancyMapping a_t,AccountContactMapping a_c,ContactEntity c" +
						" where a_t.tenancy_id in ("+tenancyIDList+") and a_c.account_id = a_t.account_id and c.contact_id = a_c.contact_id ";*/
				
				// DefectID 20160318 @Kishore code changes for sending report over mail to the login id
				String contactQuery = "from ContactEntity c" +
			                " where c.contact_id = '"+loginId+"'"; 
				infoLogger.info("Machine hours report service report Query:"+contactQuery); // control not coming here
				Session session= null;
				
					session = HibernateUtil.getSessionFactory().openSession();
					session.beginTransaction(); 		    
				    Query query = session.createQuery(contactQuery);
				    Iterator itr = query.list().iterator();
				    String userRole = null;
				    ContactEntity contact = null;
				    infoLogger.info("Usage_Machine_Hours_Report user role query");
				    if(itr.hasNext())
				    {
				    	
				    	contact = (ContactEntity)itr.next();
				    }
				    String loginID = contact.getContact_id();
				    infoLogger.info("loginID-------:"+loginID);
				    userRole = contact.getRole().getRole_name();
				    infoLogger.info("Usage_Machine_Hours_Report user role login id--"+loginID+" "+userRole);
				    //System.out.println(userRole);
				    String jasperDate = null;
				    
				  //Added by Roopa @DF20160307 for report email path need to be read from properties file
		            
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


		        if((userRole!=null) && (userRole.equalsIgnoreCase("JCB Account") || userRole.equalsIgnoreCase("Customer Care") || userRole.equalsIgnoreCase("JCB HO"))){
		        try {
		        	
		        		Date d=new Date();
		        		
		        		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		        		jasperDate = sdf.format(d);
		        		infoLogger.info("user role for machine hours---");
		            
		        		 File tempFile = new File(reportsDir+"Usage_Machine_Hours_Report_"+loginID+"_"+jasperDate+".xls");
		        		// File tempFile=new File("D:/"+"Usage_Machine_Hours_Report_"+loginID+"_"+jasperDate+".xls");
		        		 infoLogger.info("File for machine hours---");



		                  WorkbookSettings ws = new WorkbookSettings();
		                  WritableWorkbook workbook = Workbook.createWorkbook(
		                		  tempFile, ws);

		                  WritableSheet workSheet = null;
		                  workSheet = workbook.createSheet("Usage_Machine_Hours_Report", 0);

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
		                        workSheet.addCell(new jxl.write.Label(1, 0, "Total Machine Life Hours",
		                                    normalFormat));
		                        workSheet.addCell(new jxl.write.Label(2, 0, "Machine Hours(In Period)",
		                                    normalFormat));
		                        workSheet.addCell(new jxl.write.Label(3, 0, "Last Received Status",
		                                    normalFormat));
		                        workSheet.addCell(new jxl.write.Label(4, 0, "Last Engine Run",
		                                    normalFormat));
		                        workSheet.addCell(new jxl.write.Label(5, 0, "Last Reported",
		                                    normalFormat));
		                        workSheet.addCell(new jxl.write.Label(6, 0,
		                                    "Location", normalFormat));

		                        workSheet.addCell(new jxl.write.Label(7, 0,
		                                    "Dealer Name", normalFormat));

		                        int p = 0;
		                        for(int i=0;i<implObj.size();i++){
		                              workSheet.addCell(new jxl.write.Label(0, p + 1,
		                            		  implObj.get(i).getSerialNumber(), normalFormat1));

		                              workSheet.addCell(new jxl.write.Label(1, p + 1,""+
		                            		  implObj.get(i).getTotalMachineHours(), normalFormat3));

		                              workSheet.addCell(new jxl.write.Label(2, p + 1,
		                                          String.valueOf(implObj.get(i).getMachineHours()), normalFormat3));

		                              //($F{status}=="1")?"ON":( ($F{status}=="0") ? "OFF" : $F{status})

		                              String status;

		                              if(implObj.get(i).getStatus().equalsIgnoreCase("1")){

		                                    status="ON";
		                              }
		                              else if(implObj.get(i).getStatus().equalsIgnoreCase("0")){
		                                    status="OFF";
		                              }

		                              else{
		                                    status=implObj.get(i).getStatus();
		                              }

		                              workSheet.addCell(new jxl.write.Label(3, p + 1,
		                                          status, normalFormat3));

		                              workSheet.addCell(new jxl.write.Label(4, p + 1,
		                            		  ""+implObj.get(i).getLastEngineRun(), normalFormat3));

		                              workSheet.addCell(new jxl.write.Label(5, p + 1,
		                            		  ""+implObj.get(i).getLastReported(), normalFormat3));

		                              workSheet.addCell(new jxl.write.Label(6, p + 1,
		                            		  implObj.get(i).getLocation(), normalFormat3));

		                              workSheet.addCell(new jxl.write.Label(7, p + 1,
		                            		  implObj.get(i).getDealerName(), normalFormat3));


		                              p++;
		                        }
		                  }



		                  else {


		                        workSheet.setColumnView(0, 15);
		                        workSheet.addCell(new jxl.write.Label(0, 0,
		                                    "NO DATA FOUND", normalFormat1));
		                  }
		                  workbook.write();
		                  workbook.close();
		                  infoLogger.info("workbook created for machine hours---");
		        	}catch (Exception e) {
		        		e.printStackTrace();
		        	}

		             
		        
		        infoLogger.info("Mailing Machine Hours report: start "+jasperDate);
		        String duefilesToBeAttcahed=null;
		        File sourceFile = new File(reportsDir+"Usage_Machine_Hours_Report_"+loginID+"_"+jasperDate+".xls");
		        //File sourceFile = new File("D:/Usage_Machine_Hours_Report_"+loginID+"_"+jasperDate+".xls");
				infoLogger.info("Fleet summary report sourece File:"+sourceFile);
				SendEmail email = new SendEmail();
				String fileToBeAttcahed =reportsDir+"Usage_Machine_Hours_Report_"+loginID+"_"+jasperDate+".xls";
				infoLogger.info("Machine Hours report loginID:"+loginID);
				
					/*session = HibernateUtil.getSessionFactory().getCurrentSession();
					session.beginTransaction(); 		    
				    Query emailQuery = session.createQuery(emailIDQuery);
				    Iterator emailItr = emailQuery.list().iterator();
				    String ReceiverMail="";
				   // Object[] result = null;
				    while(emailItr.hasNext())
				    {
				    	//result = (Object[])itr.next();
				    	ReceiverMail = (String)itr.next();
				    }*/
				    String receiverMail = contact.getPrimary_email_id();
				    infoLogger.info("Machine Hours report Primary Email ID:"+receiverMail);
				    FileOutputStream fos;
				   
					try {
						
						fos = new FileOutputStream(reportsDir+"Usage_Machine_Hours_Report_"+loginID+"_"+jasperDate+".zip");
						infoLogger.info("sending file for machine hours---");
						duefilesToBeAttcahed =reportsDir+"Usage_Machine_Hours_Report_"+loginID+"_"+jasperDate+".zip";
						infoLogger.info("fileToBeAttcahed :"+duefilesToBeAttcahed);
						ZipOutputStream zos = new ZipOutputStream(fos);
						addToZipFile(fileToBeAttcahed,zos);
						zos.close();
						fos.close();
						infoLogger.info("closing the files---");
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//String fileToBeAttcahed= fileToBeAttcahed1;
					catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				    
				//String emailTo = "soorneedi.babu@wipro.com;deepthi.rao@wipro.com";
				String emailSubject = "Usage machine hours report";
				String emailBody = "Dear Customer \r\n Please find the attached Machine Hours report.\r\n Thanks, \r\n Deepthi. ";
				String result = email.sendMailWithAttachment(receiverMail, emailSubject, emailBody, duefilesToBeAttcahed);
				
				infoLogger.info("Machine Hours report email status for the emailId:"+receiverMail+":"+result);
				
				File zipFile = new File(reportsDir+"Usage_Machine_Hours_Report_"+loginID+"_"+jasperDate+".zip");
				zipFile.delete();
				sourceFile.delete();
				//end of sending fleet report as an attachment in email
		        }
		        else
		        {
			for(int i=0;i<implObj.size();i++)
			{
					respObj=new MachineHoursReportRespContract();
					respObj.setMachineGroupName(implObj.get(i).getMachineGroupName());
					respObj.setMachineGroupId(implObj.get(i).getMachineGroupId());
					respObj.setMachineProfileId(implObj.get(i).getMachineProfileId());			
					respObj.setMachineName(implObj.get(i).getMachineName());
					respObj.setMachineProfileName(implObj.get(i).getMachineProfileName());
					if(implObj.get(i).getTotalMachineHours()<0)
						respObj.setTotalMachineHours(0.0);
					else
						respObj.setTotalMachineHours(implObj.get(i).getTotalMachineHours());
					
					if( (implObj.get(i).getMachineHours()==0 ) || (implObj.get(i).getMachineHours()<0) )
						respObj.setMachineHours(0.0);
					else
						respObj.setMachineHours(implObj.get(i).getMachineHours());
			
			
					respObj.setStatus(implObj.get(i).getStatus());
			
					if(implObj.get(i).getLastEngineRun()!=null)
					{
						respObj.setLastEngineRun(implObj.get(i).getLastEngineRun().toString());	
					}					
				respObj.setLocation(implObj.get(i).getLocation());
				respObj.setTenancyName(implObj.get(i).getTenancyName());
				respObj.setTenancyId(implObj.get(i).getTenancyId());
				
				respObj.setAssetGroupName(implObj.get(i).getAssetGroupName());
				
				if(implObj.get(i).getDurationInCurrentStatus()<0)
					respObj.setDurationInCurrentStatus(0);
				else
					respObj.setDurationInCurrentStatus(implObj.get(i).getDurationInCurrentStatus());
				
				respObj.setSerialNumber(implObj.get(i).getSerialNumber());
				
				if(implObj.get(i).getLastReported()!=null)
					respObj.setLastReported(implObj.get(i).getLastReported().toString());
				
				//DefectID:1383 - Rajani Nagaraju - 20130930 - Query modification to return correct resultset 
				respObj.setModelId(implObj.get(i).getModelId());
				respObj.setModelName(implObj.get(i).getModelName());
				
				//DefectId:20150220 @ Suprava Added New Parameter
				respObj.setDealerName(implObj.get(i).getDealerName());
				
				listRespObj.add(respObj);
			}
		}
		        }catch(CustomFault e)
				{
					bLogger.error("Custom Fault: "+ e.getFaultInfo());
				}
		
		
		return listRespObj;
	}
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
