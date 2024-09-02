package remote.wise.service.implementation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;

import java.util.List;
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
import remote.wise.service.datacontract.MachineActivityReportReqContract;
import remote.wise.service.datacontract.MachineActivityReportRespContract;
import remote.wise.util.HibernateUtil;
//import remote.wise.util.WiseLogger;
/**
 * MachineActivityReportImpl will allow to Get Machine Activity Report for given LoginId, Period, List of Tenancy ID and for filters if provided
 * @author jgupta41
 *
 */

public class MachineActivityReportImpl {
	//DF:2013:12:13 : Conversion from Long to Double for all the required fields : Suprava
	//DefectId:1337 - Suprava - 20130923 - Log4j Changes - Using static logger object all throughout the application
	//public static WiseLogger businessError = WiseLogger.getLogger("MachineActivityReportImpl:","businessError");
	
	private int MachineGroup_Id;
	private String MachineGroup_Name;
	private int AssetGroup_ID;
	private String ProfileName;      
	private double TotalMachineLifeHours;
	private int AssetTypeId;
	private int Tenancy_ID;
	private String tenancyName;
	private String serialNumber;
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public String getTenancyName() {
		return tenancyName;
	}
	public void setTenancyName(String tenancyName) {
		this.tenancyName = tenancyName;
	}
	public int getTenancy_ID() {
		return Tenancy_ID;
	}
	public void setTenancy_ID(int tenancy_ID) {
		Tenancy_ID = tenancy_ID;
	}

	private String AssetTypeName;
	private double MachineHours;
	private String Status;
	private long Duration_in_status;
	private String Location;
	//DefectID:20150223 @Suprava Added DealerName as additional Parameter
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
	public int getMachineGroup_Id() {
		return MachineGroup_Id;
	}
	public void setMachineGroup_Id(int machineGroup_Id) {
		MachineGroup_Id = machineGroup_Id;
	}
	public String getMachineGroup_Name() {
		return MachineGroup_Name;
	}
	public void setMachineGroup_Name(String machineGroup_Name) {
		MachineGroup_Name = machineGroup_Name;
	}
	public int getAssetGroup_ID() {
		return AssetGroup_ID;
	}
	public void setAssetGroup_ID(int assetGroup_ID) {
		AssetGroup_ID = assetGroup_ID;
	}
	public String getProfileName() {
		return ProfileName;
	}
	public void setProfileName(String profileName) {
		ProfileName = profileName;
	}
	public double getTotalMachineLifeHours() {
		return TotalMachineLifeHours;
	}
	public void setTotalMachineLifeHours(double totalMachineLifeHours) {
		TotalMachineLifeHours = totalMachineLifeHours;
	}
	public int getAssetTypeId() {
		return AssetTypeId;
	}
	public void setAssetTypeId(int assetTypeId) {
		AssetTypeId = assetTypeId;
	}
	public String getAssetTypeName() {
		return AssetTypeName;
	}
	public void setAssetTypeName(String assetTypeName) {
		AssetTypeName = assetTypeName;
	}
	public double getMachineHours() {
		return MachineHours;
	}
	public void setMachineHours(double machineHours) {
		MachineHours = machineHours;
	}
	public String getStatus() {
		return Status;
	}
	public void setStatus(String status) {
		Status = status;
	}
	public long getDuration_in_status() {
		return Duration_in_status;
	}
	public void setDuration_in_status(long duration_in_status) {
		Duration_in_status = duration_in_status;
	}
	public String getLocation() {
		return Location;
	}
	public void setLocation(String location) {
		Location = location;
	}
	
	
	//*********************************Get Machine Activity Report for given LoginId, Period, List of Tenancy and for filters if provided********************************************************
	/** DefectId:  - Custom Date Implementation - 20131011 - Rajani Nagaraju
	 * DefectId: DF20131022 - Rajani Nagaraju - Usage Machine hours Service BO can be called rather than diff Logic, since these two reports has same data
	 * DefectID:1406 - Rajani Nagaraju - 20131028 - MachineGrouping issue in Reports and sending Report Totals information
	 * This method will return List of Machine Activities for given LoginId,Custom Dates or Period, List of Tenancy and for filters if provided
	 * @param machineActivityReq:Get Machine Activity Report for given LoginId,Custom Dates or Period, List of Tenancy and for filters
	 * @return respList:Returns List of Machine Activities 
	 * @throws CustomFault:custom exception is thrown when the LoginId ,Period,Tenancy_ID is not specified, Tenancy ID is invalid or not specified
	 */
	public List<MachineActivityReportRespContract> getMachineActivityReport(MachineActivityReportReqContract machineActivityReq)throws CustomFault
	{
		//Changes Done by Juhi On 7May 2013
		//Logger businessError = Logger.getLogger("businessErrorLogger");
		Logger bLogger = BusinessErrorLoggerClass.logger;
		Logger infoLogger = InfoLoggerClass.logger;
		try
		{
			//Custom Dates (fromDate,toDate) added by Juhi on 12-August-2013 
			//DefectId:  DF20131014 - Custom Date Implementation - 20131014 - Rajani Nagaraju - && changed to || here - since for custom dates, both fromDate and todate is required
			if(((machineActivityReq.getPeriod()==null))&&((machineActivityReq.getFromDate()==null) || (machineActivityReq.getToDate()==null)))
			{
				throw new CustomFault("Either Period or custom dates are not specified");
			}
			
			if(machineActivityReq.getTenancy_ID()==null)
			{
				throw new CustomFault("Tenancy Id List should be specified");
			}
			if(machineActivityReq.getLoginId()==null)
			{
				throw new CustomFault("LoginId should be specified");
			}
			
			
			if(machineActivityReq.getPeriod()!=null)
            {
                  if( !(machineActivityReq.getPeriod().equalsIgnoreCase("Week") || machineActivityReq.getPeriod().equalsIgnoreCase("Month") ||
                		  machineActivityReq.getPeriod().equalsIgnoreCase("Quarter") || machineActivityReq.getPeriod().equalsIgnoreCase("Year") ||
                		  machineActivityReq.getPeriod().equalsIgnoreCase("Last Week") ||      machineActivityReq.getPeriod().equalsIgnoreCase("Last Month") ||
                		  machineActivityReq.getPeriod().equalsIgnoreCase("Last Quarter") || machineActivityReq.getPeriod().equalsIgnoreCase("Last Year") ) )
                  {
                        throw new CustomFault("Invalid Time Period");
                        
                  }
            }

		}
		
		catch(CustomFault e)
		{
			bLogger.error("Custom Fault: "+ e.getFaultInfo());
		}
		//Changes Done by Juhi On 7 May 2013
		List<MachineActivityReportRespContract> respList = new LinkedList<MachineActivityReportRespContract>();
		ReportDetailsBO machineActivityReportBO=new ReportDetailsBO();
		
		List<MachineHoursReportImpl> machineActivityReportImpl = new LinkedList<MachineHoursReportImpl>();
		
		//Custom Dates (fromDate,toDate) added by Juhi on 12-August-2013 
		//machineActivityReportImpl=machineActivityReportBO.getMachineActivityReportList( machineActivityReq.getFromDate(),
									//machineActivityReq.getToDate() ,machineActivityReq.getPeriod(),machineActivityReq.getMachineGroup_ID(),
									//machineActivityReq.getMachineProfile_ID(),machineActivityReq.getTenancy_ID(),	machineActivityReq.getModel_ID(),
									//machineActivityReq.getMachineGroupType_ID(),machineActivityReq.getLoginId(),machineActivityReq.isGroupingOnAssetGroup());
		
		//DefectId: DF20131022 - Rajani Nagaraju - Usage Machine hours Service BO can be called rather than diff Logic, since these two reports has same data i/p and o/p
		machineActivityReportImpl= machineActivityReportBO.getMachineHoursReportService(machineActivityReq.getFromDate(),machineActivityReq.getToDate(),
									machineActivityReq.getPeriod(),machineActivityReq.getMachineGroup_ID(),machineActivityReq.getMachineProfile_ID(),
									machineActivityReq.getTenancy_ID(),machineActivityReq.getModel_ID(), machineActivityReq.isGroupingOnAssetGroup(),
									machineActivityReq.getLoginTenancyIdList(), machineActivityReq.getMinThreshold(),machineActivityReq.getMaxThreshold());
		  String loginID = machineActivityReq.getLoginId();
			
			ContactEntity contact;String userRole = null;
			DomainServiceImpl domainService = new DomainServiceImpl();
			contact = domainService.getContactDetails(loginID);
			if(contact==null || contact.getContact_id()==null)
			{
				try {
					throw new CustomFault("Invalid LoginId");
				} catch (CustomFault e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(contact.getRole()!=null){
				userRole = contact.getRole().getRole_name();
			}
			
			
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
    		
			//Added by Roopa @DF20151104 for excel changes(Report will be sent as an email to your email id)	
			
			if((userRole!=null) && (userRole.equalsIgnoreCase("JCB Admin") || userRole.equalsIgnoreCase("Customer Care") || userRole.equalsIgnoreCase("JCB HO"))){
			Date d=new Date();
			String jasperDate = null;
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

			try {
				jasperDate = sdf.format(d);
			}
			catch(Exception e){
				e.printStackTrace();
			}
			try {

			
				File FileName=new File(reportsDir+"Usage_Machine_Activity_Report_"+loginID+"_"+jasperDate+".xls");





                WorkbookSettings ws = new WorkbookSettings();
                WritableWorkbook workbook = Workbook.createWorkbook(
                            FileName, ws);

                WritableSheet workSheet = null;
                workSheet = workbook.createSheet("Usage_Machine_Activity_Report", 0);

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

                if(machineActivityReportImpl.size()>0){

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
                      workSheet.addCell(new jxl.write.Label(1, 0, "Cummulative Hour Meter Reading",
                                  normalFormat));
                      workSheet.addCell(new jxl.write.Label(2, 0, "Machine Hours",
                                  normalFormat));
                      workSheet.addCell(new jxl.write.Label(3, 0,"Last Received Status", normalFormat));

                      workSheet.addCell(new jxl.write.Label(4, 0,"Location", normalFormat));
                      workSheet.addCell(new jxl.write.Label(5, 0,"Dealer Name", normalFormat));

                      int p = 0;
                      for(int i=0;i<machineActivityReportImpl.size();i++){
                            workSheet.addCell(new jxl.write.Label(0, p + 1,
                            		machineActivityReportImpl.get(i).getSerialNumber(), normalFormat1));

                            workSheet.addCell(new jxl.write.Label(1, p + 1,
                            		machineActivityReportImpl.get(i).getTotalMachineHours()+"", normalFormat3));

                            workSheet.addCell(new jxl.write.Label(2, p + 1,
                                        String.valueOf(machineActivityReportImpl.get(i).getMachineHours()), normalFormat3));

                            //$F{status}=="1"?"Working":"Off"

                            String status;

                            if(machineActivityReportImpl.get(i).getStatus().equalsIgnoreCase("1")){

                                  status="Working";
                            }
                            else{
                                  status="Off";
                            }

                            workSheet.addCell(new jxl.write.Label(3, p + 1,
                                        status, normalFormat3));


                            workSheet.addCell(new jxl.write.Label(4, p + 1,
                            		machineActivityReportImpl.get(i).getLocation(), normalFormat3));
                            workSheet.addCell(new jxl.write.Label(5, p + 1,
                            		machineActivityReportImpl.get(i).getDealerName(), normalFormat3));




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



			}
			catch (Exception e) {
				e.printStackTrace();

			}
			
			/*try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			
			File sourceFile = new File(reportsDir+"Usage_Machine_Activity_Report_"+loginID+"_"+jasperDate+".xls");
			infoLogger.info("Usage_Machine_Activity_Report_ sourece File:"+sourceFile);
			SendEmail email = new SendEmail();
			String fileToBeAttcahed =reportsDir+"Usage_Machine_Activity_Report_"+loginID+"_"+jasperDate+".xls";
			//String loginTenancyID = reqobj.getLoginId();
			infoLogger.info("Usage_Machine_Activity_Report_"+loginID);
			/*String emailIDQuery = "select c.primary_email_id from ContactEntity c" +
					" where c.contact_id = '"+loginID+"'";
			infoLogger.info("Utilization summary report Query:"+emailIDQuery);
			Session session= null;
			
				session = HibernateUtil.getSessionFactory().openSession();
				session.beginTransaction(); 		    
			    Query query = session.createQuery(emailIDQuery);
			    Iterator itr = query.list().iterator();
			    String ReceiverMail="";
			   // Object[] result = null;
			    while(itr.hasNext())
			    {
			    	//result = (Object[])itr.next();
			    	ReceiverMail = (String)itr.next();
			    }*/
			 String receiverMail=contact.getPrimary_email_id();
			    infoLogger.info("Usage_Machine_Activity_Report_ Primary Email ID:"+receiverMail);
			    FileOutputStream fos;
			    String duefilesToBeAttcahed = "";
				try {
					//fos = new FileOutputStream("/user/Downloads/Usage_Machine_Hours_Report_"+loginID+"_"+jasperDate+".zip");
					//duefilesToBeAttcahed = "/user/Downloads/Usage_Machine_Hours_Report_"+loginID+"_"+jasperDate+".zip";
					fos = new FileOutputStream(reportsDir+"Usage_Machine_Activity_Report_"+loginID+"_"+jasperDate+".zip");
					duefilesToBeAttcahed =reportsDir+"Usage_Machine_Activity_Report_"+loginID+"_"+jasperDate+".zip";
					infoLogger.info("fileToBeAttcahed :"+duefilesToBeAttcahed);
					ZipOutputStream zos = new ZipOutputStream(fos);
					addToZipFile(fileToBeAttcahed,zos);
					zos.close();
					fos.close();
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
			String emailSubject = "Machine Activity report";
			String emailBody = "Dear Customer \r\n Please find the attached Machine Activity report.\r\n Thanks, \r\n Deepthi. ";
			String result = email.sendMailWithAttachment(receiverMail, emailSubject, emailBody, duefilesToBeAttcahed);
			String emailTo = "soorneedi.babu@wipro.com;deepthi.rao@wipro.com";
			
			email.sendMailWithAttachment(receiverMail, emailSubject, emailBody, duefilesToBeAttcahed);
			File zipFile = new File(reportsDir+"Usage_Machine_Activity_Report_"+loginID+"_"+jasperDate+".zip");
			zipFile.delete();
			sourceFile.delete();
			
	//end
			
			}
			else{
		MachineActivityReportRespContract  respContractObj=null;
		for(int i=0;i<machineActivityReportImpl.size();i++)
		{
			respContractObj=new MachineActivityReportRespContract();
			respContractObj.setSerialNumber(machineActivityReportImpl.get(i).getSerialNumber());
			
			respContractObj.setMachineGroup_Id(machineActivityReportImpl.get(i).getMachineGroupId());
			respContractObj.setMachineGroup_Name(machineActivityReportImpl.get(i).getMachineGroupName());
			
			respContractObj.setAssetGroup_ID(machineActivityReportImpl.get(i).getMachineProfileId());
			respContractObj.setProfileName(machineActivityReportImpl.get(i).getMachineProfileName());
			
			respContractObj.setAssetTypeId(machineActivityReportImpl.get(i).getModelId());
			respContractObj.setAssetTypeName(machineActivityReportImpl.get(i).getModelName());
			
			respContractObj.setTenancy_ID(machineActivityReportImpl.get(i).getTenancyId());
			respContractObj.setTenancyName(machineActivityReportImpl.get(i).getTenancyName());
		
			respContractObj.setLocation(machineActivityReportImpl.get(i).getLocation());
			//DefectID:20150223 @Suprava Added DealerName as additional Parameter
			respContractObj.setDealerName(machineActivityReportImpl.get(i).getDealerName());
			
			//Changes  made by Juhi on 2013-06-04
			if(machineActivityReportImpl.get(i).getTotalMachineHours()<0)
				respContractObj.setTotalMachineLifeHours(0);
			else
				respContractObj.setTotalMachineLifeHours(machineActivityReportImpl.get(i).getTotalMachineHours());
			
			if( (machineActivityReportImpl.get(i).getMachineHours()== 0 ) || (machineActivityReportImpl.get(i).getMachineHours()<0) )
				respContractObj.setMachineHours(0);
			else
			{
				double machineHoursDouble = machineActivityReportImpl.get(i).getMachineHours();
				//Commenting below code DF:20131219 
			//	int machineHoursInt = (int) machineHoursDouble;
				respContractObj.setMachineHours(machineHoursDouble);
			}
			
			respContractObj.setStatus(machineActivityReportImpl.get(i).getStatus());
			
			if(machineActivityReportImpl.get(i).getDurationInCurrentStatus()<0)
				respContractObj.setDuration_in_status(0);
			else
				respContractObj.setDuration_in_status(machineActivityReportImpl.get(i).getDurationInCurrentStatus());

			respList.add(respContractObj);
			
		}
		
	}
			return respList;
	}
	
	//********************************* End of Get Machine Activity Report for given LoginId, Period, List of Tenancy and for filters if provided********************************************************
	
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
