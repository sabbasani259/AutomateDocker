package remote.wise.service.implementation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
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
import remote.wise.service.datacontract.DueOverDueReportReqContract;
import remote.wise.service.datacontract.DueOverDueReportRespContract;
//import remote.wise.util.WiseLogger;
import remote.wise.util.HibernateUtil;

/** Implementation class that returns the service due/overdue details
 * @author Rajani Nagaraju
 *
 */
public class DueOverDueReportImpl 
{
	//DF:2013:12:13 : Conversion from Long to Double for all the required fields : Suprava
	//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
	//public static WiseLogger businessError = WiseLogger.getLogger("DueOverDueReportImpl:","businessError");
	
	
	int tenancyId;
	String tenancyName;
	int machineGroupId;
	String machineGroupName;
	int machineProfileId;
	String machineProfileName;
	int modelId;
	String modelName;
	String machineName;
	String serialNumber;
	static boolean dueOverdueWorkSheetFlag;
	String customerName;
	String customerContactNumber;
	String operatorName;
	String operatorContactNumber;
	String dealerName;
	String dealerContactNumber;
	
	double dueOrOverDueHours;
	int dueOrOverDueDays;
	
	String serviceName;
	String scheduleName;
	String plannedServiceDate;
	double plannedServiceHours;
	double totalMachineHours;
	int serviceScheduleId;
	String DealerName;
	
	
	public double getTotalMachineHours() {
		return totalMachineHours;
	}

	public void setTotalMachineHours(double totalMachineHours) {
		this.totalMachineHours = totalMachineHours;
	}

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

	/**
	 * @return the tenancyName
	 */
	public String getTenancyName() {
		return tenancyName;
	}

	/**
	 * @param tenancyName the tenancyName to set
	 */
	public void setTenancyName(String tenancyName) {
		this.tenancyName = tenancyName;
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
	 * @return the machineGroupName
	 */
	public String getMachineGroupName() {
		return machineGroupName;
	}

	/**
	 * @param machineGroupName the machineGroupName to set
	 */
	public void setMachineGroupName(String machineGroupName) {
		this.machineGroupName = machineGroupName;
	}

	/**
	 * @return the machineProfileId
	 */
	public int getMachineProfileId() {
		return machineProfileId;
	}

	/**
	 * @param machineProfileId the machineProfileId to set
	 */
	public void setMachineProfileId(int machineProfileId) {
		this.machineProfileId = machineProfileId;
	}

	/**
	 * @return the machineProfileName
	 */
	public String getMachineProfileName() {
		return machineProfileName;
	}

	/**
	 * @param machineProfileName the machineProfileName to set
	 */
	public void setMachineProfileName(String machineProfileName) {
		this.machineProfileName = machineProfileName;
	}

	/**
	 * @return the modelId
	 */
	public int getModelId() {
		return modelId;
	}

	/**
	 * @param modelId the modelId to set
	 */
	public void setModelId(int modelId) {
		this.modelId = modelId;
	}

	/**
	 * @return the modelName
	 */
	public String getModelName() {
		return modelName;
	}

	/**
	 * @param modelName the modelName to set
	 */
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	/**
	 * @return the machineName
	 */
	public String getMachineName() {
		return machineName;
	}

	/**
	 * @param machineName the machineName to set
	 */
	public void setMachineName(String machineName) {
		this.machineName = machineName;
	}

	/**
	 * @return the serialNumber
	 */
	public String getSerialNumber() {
		return serialNumber;
	}

	/**
	 * @param serialNumber the serialNumber to set
	 */
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	/**
	 * @return the customerName
	 */
	public String getCustomerName() {
		return customerName;
	}

	/**
	 * @param customerName the customerName to set
	 */
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	/**
	 * @return the customerContactNumber
	 */
	public String getCustomerContactNumber() {
		return customerContactNumber;
	}

	/**
	 * @param customerContactNumber the customerContactNumber to set
	 */
	public void setCustomerContactNumber(String customerContactNumber) {
		this.customerContactNumber = customerContactNumber;
	}

	/**
	 * @return the operatorName
	 */
	public String getOperatorName() {
		return operatorName;
	}

	/**
	 * @param operatorName the operatorName to set
	 */
	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	/**
	 * @return the operatorContactNumber
	 */
	public String getOperatorContactNumber() {
		return operatorContactNumber;
	}

	/**
	 * @param operatorContactNumber the operatorContactNumber to set
	 */
	public void setOperatorContactNumber(String operatorContactNumber) {
		this.operatorContactNumber = operatorContactNumber;
	}

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
	 * @return the dealerContactNumber
	 */
	public String getDealerContactNumber() {
		return dealerContactNumber;
	}

	/**
	 * @param dealerContactNumber the dealerContactNumber to set
	 */
	public void setDealerContactNumber(String dealerContactNumber) {
		this.dealerContactNumber = dealerContactNumber;
	}

	/**
	 * @return the dueOrOverDueHours
	 */
	public double getDueOrOverDueHours() {
		return dueOrOverDueHours;
	}

	/**
	 * @param dueOrOverDueHours the dueOrOverDueHours to set
	 */
	public void setDueOrOverDueHours(double dueOrOverDueHours) {
		this.dueOrOverDueHours = dueOrOverDueHours;
	}

	/**
	 * @return the dueOrOverDueDays
	 */
	public int getDueOrOverDueDays() {
		return dueOrOverDueDays;
	}

	/**
	 * @param dueOrOverDueDays the dueOrOverDueDays to set
	 */
	public void setDueOrOverDueDays(int dueOrOverDueDays) {
		this.dueOrOverDueDays = dueOrOverDueDays;
	}

	/**
	 * @return the serviceName
	 */
	public String getServiceName() {
		return serviceName;
	}

	/**
	 * @param serviceName the serviceName to set
	 */
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	/**
	 * @return the scheduleName
	 */
	public String getScheduleName() {
		return scheduleName;
	}

	/**
	 * @param scheduleName the scheduleName to set
	 */
	public void setScheduleName(String scheduleName) {
		this.scheduleName = scheduleName;
	}

	/**
	 * @return the plannedServiceDate
	 */
	public String getPlannedServiceDate() {
		return plannedServiceDate;
	}

	/**
	 * @param plannedServiceDate the plannedServiceDate to set
	 */
	public void setPlannedServiceDate(String plannedServiceDate) {
		this.plannedServiceDate = plannedServiceDate;
	}

	/**
	 * @return the plannedServiceHours
	 */
	public double getPlannedServiceHours() {
		return plannedServiceHours;
	}

	/**
	 * @param plannedServiceHours the plannedServiceHours to set
	 */
	public void setPlannedServiceHours(double plannedServiceHours) {
		this.plannedServiceHours = plannedServiceHours;
	}

	
	/** This method returns the List of machines under service due/overdue
	 * @param reqObj Input filters to get the list of VINs
	 * @return due/overdue details of VINs 
	 * @throws CustomFault
	 * @throws ParseException 
	 */
	public List<DueOverDueReportRespContract> getDueOverDueMachines(DueOverDueReportReqContract reqObj) throws CustomFault
	{
		List<DueOverDueReportRespContract> responseList = new LinkedList<DueOverDueReportRespContract>();
		
		//Logger businessError = Logger.getLogger("businessErrorLogger");
		Logger bLogger = BusinessErrorLoggerClass.logger;
		
		Logger infoLogger = InfoLoggerClass.logger;
		
		try
		{
			if(reqObj.getLoginId()==null)
			{
				throw new CustomFault("Login Id should be specified");
			}
			
			if(reqObj.getTenancyIdList()==null || reqObj.getTenancyIdList().isEmpty())
			{
				throw new CustomFault("Tenancy Id should be specified");
			}
		}
		
		catch(CustomFault e)
		{
			bLogger.error("Custom Fault: "+ e.getFaultInfo());
		}
		
		ReportDetailsBO reportDetails = new ReportDetailsBO();
		
		List<DueOverDueReportImpl> implObjList = reportDetails.getDueOverdueMachines(reqObj.getLoginId(), reqObj.getTenancyIdList(),
				reqObj.getMachineGroupIdList(), reqObj.getMachineProfileIdList(), reqObj.getModelIdList(), reqObj.isOverdueReport());
		
		String loginID = reqObj.getLoginId();
		
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
		
		String overdue="";
		
		String due="";
		
		//overdue report
		if(reqObj.isOverdueReport()){

			//Added by Roopa @DF20151104 for excel changes(Report will be sent as an email to your email id)	
			
			
			
			try {

			//String FileName="C:/Service_Machine_OverDue_Report"+jasperDate+".xls";
			 File tempFile = new File(reportsDir+"Service_Machine_OverDue_Report_"+loginID+"_"+jasperDate+".xls");
	   	     String FileName = tempFile.getAbsolutePath();
			

				WorkbookSettings ws = new WorkbookSettings();
				WritableWorkbook workbook = Workbook.createWorkbook(new File(
						FileName), ws);

				WritableSheet workSheet = null;
				workSheet = workbook.createSheet("Service_Machine_OverDue_Report", 0);

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

				if(implObjList!=null && implObjList.size()>0){
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
				workSheet.addCell(new jxl.write.Label(0, 0, "Machine",
						normalFormat));
				workSheet.addCell(new jxl.write.Label(1, 0, "Model",
						normalFormat));
				workSheet.addCell(new jxl.write.Label(2, 0, "Total machine life hours",
						normalFormat));
				workSheet.addCell(new jxl.write.Label(3, 0, "Service Schedule",
						normalFormat));
				workSheet.addCell(new jxl.write.Label(4, 0, "Service name",
						normalFormat));
				workSheet.addCell(new jxl.write.Label(5, 0, "Planned service date",
						normalFormat));
				workSheet.addCell(new jxl.write.Label(6, 0, "Planned service hours",
						normalFormat));

				workSheet.addCell(new jxl.write.Label(7, 0, "Due by hours",
						normalFormat));

				workSheet.addCell(new jxl.write.Label(8, 0, "Due by days",
						normalFormat));

				workSheet.addCell(new jxl.write.Label(9, 0, "Customer name",
						normalFormat));

				workSheet.addCell(new jxl.write.Label(10, 0, "Customer contact",
						normalFormat));

				workSheet.addCell(new jxl.write.Label(11, 0, "Dealer name",
						normalFormat));

				workSheet.addCell(new jxl.write.Label(12, 0, "Dealer contact",
						normalFormat));

				workSheet.addCell(new jxl.write.Label(13, 0, "Operator name",
						normalFormat));

				workSheet.addCell(new jxl.write.Label(14, 0, "Operator contact",
						normalFormat));

				int p1 = 0;
				for(int i=0;i<implObjList.size();i++){
					workSheet.addCell(new jxl.write.Label(0, p1 + 1,
							implObjList.get(i).getSerialNumber(), normalFormat3));

					workSheet.addCell(new jxl.write.Label(1, p1 + 1,
							implObjList.get(i).getModelName(), normalFormat1));

					workSheet.addCell(new jxl.write.Label(2, p1 + 1,
							String.valueOf(implObjList.get(i).getTotalMachineHours()), normalFormat1));

					workSheet.addCell(new jxl.write.Label(3, p1 + 1,
							implObjList.get(i).getScheduleName(), normalFormat1));

					workSheet.addCell(new jxl.write.Label(4, p1 + 1,
							implObjList.get(i).getServiceName(), normalFormat1));

					workSheet.addCell(new jxl.write.Label(5, p1 + 1,
							implObjList.get(i).getPlannedServiceDate(), normalFormat1));

					workSheet.addCell(new jxl.write.Label(6, p1 + 1,
							String.valueOf(implObjList.get(i).getPlannedServiceHours()), normalFormat1));

					//	$F{dueOrOverDueHours}<0?("("+$F{dueOrOverDueHours}+")"):$F{dueOrOverDueHours}

					workSheet.addCell(new jxl.write.Label(7, p1 + 1,
							String.valueOf(implObjList.get(i).getDueOrOverDueHours()), normalFormat1));

					//$F{dueOrOverDueDays}<0?("("+$F{dueOrOverDueDays}+")"):$F{dueOrOverDueDays}

					workSheet.addCell(new jxl.write.Label(8, p1 + 1,
							String.valueOf(implObjList.get(i).getDueOrOverDueDays()), normalFormat1));

					workSheet.addCell(new jxl.write.Label(9, p1 + 1,
							implObjList.get(i).getCustomerName(), normalFormat1));

					workSheet.addCell(new jxl.write.Label(10, p1 + 1,
							implObjList.get(i).getCustomerContactNumber(), normalFormat1));

					workSheet.addCell(new jxl.write.Label(11, p1 + 1,
							implObjList.get(i).getDealerName(), normalFormat1));

					workSheet.addCell(new jxl.write.Label(12, p1 + 1,
							implObjList.get(i).getDealerContactNumber(), normalFormat1));

					workSheet.addCell(new jxl.write.Label(13, p1+ 1,
							implObjList.get(i).getOperatorName(), normalFormat1));

					workSheet.addCell(new jxl.write.Label(14, p1 + 1,
							implObjList.get(i).getOperatorContactNumber(), normalFormat1));



					p1++;
				}
				
			}



				else {


					workSheet.setColumnView(0, 15);
					workSheet.addCell(new jxl.write.Label(0, 0,
							"NO DATA FOUND", normalFormat1));
				}
				workbook.write();
				workbook.close();

              infoLogger.info("Service_Machine_OverdueDue_Report created");
			}
			catch (Exception e) {
				e.printStackTrace();
				infoLogger.info("Problem in creating Service_Machine_OverdueDue_Report");	

			}	
			
			
			
		}
		//due report
		if(!reqObj.isOverdueReport()){
			//Added by Roopa @DF20151104 for excel changes(Report will be sent as an email to your email id)	
			
			
		
			try {

			//String FileName="C:/Service_Machine_Due_Report"+jasperDate+".xls";
			File tempFile = new File(reportsDir+"Service_Machine_Due_Report_"+loginID+"_"+jasperDate+".xls");
	   	     String FileName = tempFile.getAbsolutePath();
			

				WorkbookSettings ws = new WorkbookSettings();
				WritableWorkbook workbook = Workbook.createWorkbook(new File(
						FileName), ws);

				WritableSheet workSheet = null;
				workSheet = workbook.createSheet("Service_Machine_Due_Report", 0);

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

				if(implObjList!=null && implObjList.size()>0){WritableCellFormat normalFormat = new WritableCellFormat(
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
				workSheet.addCell(new jxl.write.Label(0, 0, "Machine",
						normalFormat));
				workSheet.addCell(new jxl.write.Label(1, 0, "Model",
						normalFormat));
				workSheet.addCell(new jxl.write.Label(2, 0, "Total machine life hours",
						normalFormat));
				workSheet.addCell(new jxl.write.Label(3, 0, "Service Schedule",
						normalFormat));
				workSheet.addCell(new jxl.write.Label(4, 0, "Service name",
						normalFormat));
				workSheet.addCell(new jxl.write.Label(5, 0, "Planned service date",
						normalFormat));
				workSheet.addCell(new jxl.write.Label(6, 0, "Planned service hours",
						normalFormat));

				workSheet.addCell(new jxl.write.Label(7, 0, "Due by hours",
						normalFormat));

				workSheet.addCell(new jxl.write.Label(8, 0, "Due by days",
						normalFormat));

				workSheet.addCell(new jxl.write.Label(9, 0, "Customer name",
						normalFormat));

				workSheet.addCell(new jxl.write.Label(10, 0, "Customer contact",
						normalFormat));

				workSheet.addCell(new jxl.write.Label(11, 0, "Dealer name",
						normalFormat));

				workSheet.addCell(new jxl.write.Label(12, 0, "Dealer contact",
						normalFormat));

				workSheet.addCell(new jxl.write.Label(13, 0, "Operator name",
						normalFormat));

				workSheet.addCell(new jxl.write.Label(14, 0, "Operator contact",
						normalFormat));

				int p = 0;
				for(int i=0;i<implObjList.size();i++){
					workSheet.addCell(new jxl.write.Label(0, p + 1,
							implObjList.get(i).getSerialNumber(), normalFormat3));

					workSheet.addCell(new jxl.write.Label(1, p + 1,
							implObjList.get(i).getModelName(), normalFormat1));

					workSheet.addCell(new jxl.write.Label(2, p + 1,
							String.valueOf(implObjList.get(i).getTotalMachineHours()), normalFormat1));

					workSheet.addCell(new jxl.write.Label(3, p + 1,
							implObjList.get(i).getScheduleName(), normalFormat1));

					workSheet.addCell(new jxl.write.Label(4, p + 1,
							implObjList.get(i).getServiceName(), normalFormat1));

					workSheet.addCell(new jxl.write.Label(5, p + 1,
							implObjList.get(i).getPlannedServiceDate(), normalFormat1));

					workSheet.addCell(new jxl.write.Label(6, p + 1,
							String.valueOf(implObjList.get(i).getPlannedServiceHours()), normalFormat1));

					//	$F{dueOrOverDueHours}<0?("("+$F{dueOrOverDueHours}+")"):$F{dueOrOverDueHours}

					workSheet.addCell(new jxl.write.Label(7, p + 1,
							String.valueOf(implObjList.get(i).getDueOrOverDueHours()), normalFormat1));

					//$F{dueOrOverDueDays}<0?("("+$F{dueOrOverDueDays}+")"):$F{dueOrOverDueDays}

					workSheet.addCell(new jxl.write.Label(8, p + 1,
							String.valueOf(implObjList.get(i).getDueOrOverDueDays()), normalFormat1));

					workSheet.addCell(new jxl.write.Label(9, p + 1,
							implObjList.get(i).getCustomerName(), normalFormat1));

					workSheet.addCell(new jxl.write.Label(10, p + 1,
							implObjList.get(i).getCustomerContactNumber(), normalFormat1));

					workSheet.addCell(new jxl.write.Label(11, p + 1,
							implObjList.get(i).getDealerName(), normalFormat1));

					workSheet.addCell(new jxl.write.Label(12, p + 1,
							implObjList.get(i).getDealerContactNumber(), normalFormat1));

					workSheet.addCell(new jxl.write.Label(13, p + 1,
							implObjList.get(i).getOperatorName(), normalFormat1));

					workSheet.addCell(new jxl.write.Label(14, p + 1,
							implObjList.get(i).getOperatorContactNumber(), normalFormat1));



					p++;
				}
				dueOverdueWorkSheetFlag=true;
			}



				else {


					workSheet.setColumnView(0, 15);
					workSheet.addCell(new jxl.write.Label(0, 0,
							"NO DATA FOUND", normalFormat1));
				}
				workbook.write();
				workbook.close();
				 infoLogger.info("Service_Machine_Due_Report created");

			}
			catch (Exception e) {
				e.printStackTrace();
				 infoLogger.info("Problem in creating Service_Machine_Due_Report");
			}	
			
		}
		
		if(reqObj.isOverdueReport()){
			
		
		File sourceFile = new File(reportsDir+"Service_Machine_Due_Overdue_SummaryReport_"+loginID+"_"+jasperDate+".xls");
		
		infoLogger.info("Service_Machine_Due_Overdue_SummaryReport sourece File:"+sourceFile);
		
		File sourceFile2 = new File(reportsDir+"Service_Machine_Due_Report_"+loginID+"_"+jasperDate+".xls");
		
		infoLogger.info("Service_Machine_Due_Report sourece File:"+sourceFile2);
		
        File sourceFile1 = new File(reportsDir+"Service_Machine_OverDue_Report_"+loginID+"_"+jasperDate+".xls");
		
		infoLogger.info("Service_Machine_OverDue_Report sourece File:"+sourceFile1);
		
	SendEmail email = new SendEmail();
		
		String fileToBeAttcahed3 = reportsDir+"Service_Machine_Due_Overdue_SummaryReport_"+loginID+"_"+jasperDate+".xls";
		
		String fileToBeAttcahed2 = reportsDir+"Service_Machine_Due_Report_"+loginID+"_"+jasperDate+".xls";
		
		String fileToBeAttcahed1 = reportsDir+"Service_Machine_OverDue_Report_"+loginID+"_"+jasperDate+".xls";
		
		//String duefilesToBeAttcahed= fileToBeAttcahed2+","+fileToBeAttcahed3;
		String duefilesToBeAttcahed="";
		//String overduefilesToBeAttcahed= fileToBeAttcahed1+","+fileToBeAttcahed3;
		
		//infoLogger.info("fileToBeAttcahed :"+duefilesToBeAttcahed);
		
		//infoLogger.info("fileToBeAttcahed :"+overduefilesToBeAttcahed);
		

		infoLogger.info("Service_Machine_OverDue_Report loginID:"+loginID);
		String emailIDQuery = "select c.primary_email_id from ContactEntity c" +
				" where c.contact_id = '"+loginID+"'";
		infoLogger.info("Service_Machine_OverDue_Report Query:"+emailIDQuery);
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
		    }
		    infoLogger.info("Service_Machine_OverDue_Report Primary Email ID:"+ReceiverMail);
		    
		//String emailTo = "soorneedi.babu@wipro.com;deepthi.rao@wipro.com";
		    FileOutputStream fos;
			try {
				fos = new FileOutputStream(reportsDir+"ServiceDueOverDueReport_"+loginID+"_"+jasperDate+".zip");
				duefilesToBeAttcahed = reportsDir+"ServiceDueOverDueReport_"+loginID+"_"+jasperDate+".zip";
				infoLogger.info("fileToBeAttcahed :"+duefilesToBeAttcahed);
				ZipOutputStream zos = new ZipOutputStream(fos);
				addToZipFile(fileToBeAttcahed1,zos);
				addToZipFile(fileToBeAttcahed2,zos);
				addToZipFile(fileToBeAttcahed3,zos);
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
			
		String emailSubject = "Service_Machine_DueOverDue_Report";
		String emailBody = "Dear customer, \r\n Please find the attached Service_Machine_Due_Overdue_SummaryReport,Service_Machine_Due_Report.\r\n Thanks, \r\n Deepthi. ";
		String status = email.sendMailWithMultipleAttachment(ReceiverMail, emailSubject, emailBody, duefilesToBeAttcahed);
		infoLogger.info("Email Sending status of due report for "+ReceiverMail+":"+status);
		/*if(status.equals("SUCCESS"))
		{
			emailSubject = "Service_Machine_OverDue_Report";
			emailBody = "Dear customer, \r\n Please find the attcheched Service_Machine_Due_Overdue_SummaryReport, Service_Machine_OverDue_Report.\r\n Thanks, \r\n Deepthi. ";
			status = email.sendMailWithMultipleAttachment(ReceiverMail, emailSubject, emailBody, overduefilesToBeAttcahed);
			infoLogger.info("Email Sending status of Overdue report for "+ReceiverMail+":"+status);
		}
		else
			infoLogger.info("Email Sending status of due report for "+ReceiverMail+":"+status);*/
		if(status.equals("SUCCESS"))
		{
			File zipFile = new File(reportsDir+"ServiceDueOverDueReport_"+loginID+"_"+jasperDate+".zip");
			zipFile.delete();
			sourceFile.delete();
			sourceFile2.delete();
			sourceFile1.delete();
		}
		
		}
		
		}
		else{
		
		for(int i=0; i<implObjList.size(); i++)
		{
			DueOverDueReportRespContract response = new DueOverDueReportRespContract();
			response.setCustomerContactNumber(implObjList.get(i).getCustomerContactNumber());
			response.setCustomerName(implObjList.get(i).getCustomerName());
			response.setDealerContactNumber(implObjList.get(i).getDealerContactNumber());
			response.setDealerName(implObjList.get(i).getDealerName());
			
			response.setDueOrOverDueDays(implObjList.get(i).getDueOrOverDueDays());
			response.setDueOrOverDueHours(implObjList.get(i).getDueOrOverDueHours());
			
			response.setMachineGroupId(implObjList.get(i).getMachineGroupId());
			response.setMachineGroupName(implObjList.get(i).getMachineGroupName());
			response.setMachineName(implObjList.get(i).getMachineName());
			response.setMachineProfileId(implObjList.get(i).getMachineProfileId());
			response.setMachineProfileName(implObjList.get(i).getMachineProfileName());
			response.setModelId(implObjList.get(i).getModelId());
			response.setModelName(implObjList.get(i).getModelName());
			response.setOperatorContactNumber(implObjList.get(i).getOperatorContactNumber());
			response.setOperatorName(implObjList.get(i).getOperatorName());
			response.setPlannedServiceDate(implObjList.get(i).getPlannedServiceDate());
			response.setPlannedServiceHours(implObjList.get(i).getPlannedServiceHours());
			response.setSerialNumber(implObjList.get(i).getSerialNumber());
			response.setServiceName(implObjList.get(i).getServiceName());
			response.setTenancyId(implObjList.get(i).getTenancyId());
			response.setTenancyName(implObjList.get(i).getTenancyName());
			response.setScheduleName(implObjList.get(i).getScheduleName());
			
			if(implObjList.get(i).getTotalMachineHours()<0)
				response.setTotalMachineHours(0);
			else
				response.setTotalMachineHours(implObjList.get(i).getTotalMachineHours());
			
			responseList.add(response);
			
		}
		}
		return responseList;
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

	public int getServiceScheduleId() {
		return serviceScheduleId;
	}

	public void setServiceScheduleId(int serviceScheduleId) {
		this.serviceScheduleId = serviceScheduleId;
	}
	
}
