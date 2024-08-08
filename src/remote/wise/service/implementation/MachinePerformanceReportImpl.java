package remote.wise.service.implementation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
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
import remote.wise.service.datacontract.MachinePerformanceReportReqContract;
import remote.wise.service.datacontract.MachinePerformanceReportRespContract;
//import remote.wise.util.WiseLogger;
import remote.wise.util.HibernateUtil;
import remote.wise.util.ListToStringConversion;

public class MachinePerformanceReportImpl 
{
	//DF:2013:12:13 : Conversion from Long to Double for all the required fields : Suprava
	//public static WiseLogger businessError = WiseLogger.getLogger("MachinePerformanceReportImpl:","businessError");
	//Utilization
	private double engineOff;//Engine_Off_Hours
	private double engineOn;//Machine_Hours
	private double workingTime;//Working_Time
	private double idleTime;//Idle_Hours
	//Productivity
	private double powerBandLow;
	private double powerBandMedium;
	private double powerBandHigh;
	
	//Consumption
	private double startingEngineRunHours;
	private double finishEngineRunHours;
	private double fuelUsedLitres;//Fuel_Used
	private double fuelUsedIdleLitres;//Fuel_Used_In_Idle
	private String finishFuelLevel;
	private double overallFuelConsumptionLitres;
	
	private double startingEngineRunHoursLife;
	private double finishEngineRunHoursLife;
	private double fuelUsedLitresLife;
	private double fuelUsedIdleLitresLife;
	private String finishFuelLevelLife;
	
	//Returning parameters required for Report Grouping
	private String serialNumber;
	private String asset_group_name;
	private int assetGroupId;
	private String customMachineGroupName;
	private int customMachineGroupId;
	private String tenancyName;
	private int tenancyId;
	private String modelName;
	private int modelId;
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


	public double getEngineOff() {
		return engineOff;
	}


	public void setEngineOff(double engineOff) {
		this.engineOff = engineOff;
	}


	public double getEngineOn() {
		return engineOn;
	}


	public void setEngineOn(double engineOn) {
		this.engineOn = engineOn;
	}


	public double getWorkingTime() {
		return workingTime;
	}


	public void setWorkingTime(double workingTime) {
		this.workingTime = workingTime;
	}


	public double getIdleTime() {
		return idleTime;
	}


	public void setIdleTime(double idleTime) {
		this.idleTime = idleTime;
	}


	public void setIdleTime(int idleTime) {
		this.idleTime = idleTime;
	}
	
	
	public String getFinishFuelLevelLife() {
		return finishFuelLevelLife;
	}


	public void setFinishFuelLevelLife(String finishFuelLevelLife) {
		this.finishFuelLevelLife = finishFuelLevelLife;
	}


	public double getPowerBandLow() {
		return powerBandLow;
	}
	public void setPowerBandLow(double powerBandLow) {
		this.powerBandLow = powerBandLow;
	}
	public double getPowerBandMedium() {
		return powerBandMedium;
	}
	public void setPowerBandMedium(double powerBandMedium) {
		this.powerBandMedium = powerBandMedium;
	}
	public double getPowerBandHigh() {
		return powerBandHigh;
	}
	public void setPowerBandHigh(double powerBandHigh) {
		this.powerBandHigh = powerBandHigh;
	}	
	
	public double getFinishEngineRunHours() {
		return finishEngineRunHours;
	}
	public void setFinishEngineRunHours(double finishEngineRunHours) {
		this.finishEngineRunHours = finishEngineRunHours;
	}
	
	public double getFuelUsedLitres() {
		return fuelUsedLitres;
	}


	public void setFuelUsedLitres(double fuelUsedLitres) {
		this.fuelUsedLitres = fuelUsedLitres;
	}


	public double getFuelUsedIdleLitres() {
		return fuelUsedIdleLitres;
	}


	public void setFuelUsedIdleLitres(double fuelUsedIdleLitres) {
		this.fuelUsedIdleLitres = fuelUsedIdleLitres;
	}


	public String getFinishFuelLevel() {
		return finishFuelLevel;
	}
	public void setFinishFuelLevel(String finishFuelLevel) {
		this.finishFuelLevel = finishFuelLevel;
	}
		
	public double getOverallFuelConsumptionLitres() {
		return overallFuelConsumptionLitres;
	}


	public void setOverallFuelConsumptionLitres(double overallFuelConsumptionLitres) {
		this.overallFuelConsumptionLitres = overallFuelConsumptionLitres;
	}


	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public String getAsset_group_name() {
		return asset_group_name;
	}
	public void setAsset_group_name(String asset_group_name) {
		this.asset_group_name = asset_group_name;
	}

	
	
	public double getFuelUsedLitresLife() {
		return fuelUsedLitresLife;
	}


	public void setFuelUsedLitresLife(double fuelUsedLitresLife) {
		this.fuelUsedLitresLife = fuelUsedLitresLife;
	}


	public double getFuelUsedIdleLitresLife() {
		return fuelUsedIdleLitresLife;
	}


	public void setFuelUsedIdleLitresLife(double fuelUsedIdleLitresLife) {
		this.fuelUsedIdleLitresLife = fuelUsedIdleLitresLife;
	}


	public double getStartingEngineRunHours() {
		return startingEngineRunHours;
	}


	public void setStartingEngineRunHours(double startingEngineRunHours) {
		this.startingEngineRunHours = startingEngineRunHours;
	}


	public double getStartingEngineRunHoursLife() {
		return startingEngineRunHoursLife;
	}


	public void setStartingEngineRunHoursLife(double startingEngineRunHoursLife) {
		this.startingEngineRunHoursLife = startingEngineRunHoursLife;
	}


	public double getFinishEngineRunHoursLife() {
		return finishEngineRunHoursLife;
	}


	public void setFinishEngineRunHoursLife(double finishEngineRunHoursLife) {
		this.finishEngineRunHoursLife = finishEngineRunHoursLife;
	}


	public int getAssetGroupId() {
		return assetGroupId;
	}


	public void setAssetGroupId(int assetGroupId) {
		this.assetGroupId = assetGroupId;
	}


	public String getCustomMachineGroupName() {
		return customMachineGroupName;
	}


	public void setCustomMachineGroupName(String customMachineGroupName) {
		this.customMachineGroupName = customMachineGroupName;
	}


	public int getCustomMachineGroupId() {
		return customMachineGroupId;
	}


	public void setCustomMachineGroupId(int customMachineGroupId) {
		this.customMachineGroupId = customMachineGroupId;
	}


	public String getTenancyName() {
		return tenancyName;
	}


	public void setTenancyName(String tenancyName) {
		this.tenancyName = tenancyName;
	}


	public int getTenancyId() {
		return tenancyId;
	}


	public void setTenancyId(int tenancyId) {
		this.tenancyId = tenancyId;
	}


	public String getModelName() {
		return modelName;
	}


	public void setModelName(String modelName) {
		this.modelName = modelName;
	}


	public int getModelId() {
		return modelId;
	}


	public void setModelId(int modelId) {
		this.modelId = modelId;
	}


	/** This method returns the performance details of the machine for the given period
	 * @param reqObj Specifies the filters according to which performance details will be displayed for those machines
	 * @return Returns the performance details
	 */
	public List<MachinePerformanceReportRespContract> getMachPerformanceReport(MachinePerformanceReportReqContract reqObj)
	{
		ReportDetailsBO reportDetObj=new ReportDetailsBO();		
		List<MachinePerformanceReportRespContract> listResponse=new LinkedList<MachinePerformanceReportRespContract>();
		Logger bLogger = BusinessErrorLoggerClass.logger;
		Logger infoLogger = InfoLoggerClass.logger;
		//Check for Mandatory Parameters
		
	//	Logger businessError = Logger.getLogger("businessErrorLogger");
		
    	Logger fLogger = FatalLoggerClass.logger;
	//	Logger businessError = Logger.getLogger("businessErrorLogger");
    	
   // 	System.out.println("tenancy id -----"+reqObj.getTenancyIdList().size());
    //	System.out.println("tenancy id 1 -----"+reqObj.getTenancyIdList());
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
			
			//Custom Dates (fromDate,toDate) added by Juhi on 14-August-2013 
			///DefectId:  DF20131014 - Custom Date Implementation - - && changed to || here - since for custom dates, both fromDate and todate is required
			if(((reqObj.getPeriod()==null))&&( ((reqObj.getFromDate()==null) ||(reqObj.getToDate()==null))))
			{
				throw new CustomFault("Either Period or custom dates are not specified");
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
			
			if(reqObj.getTenancyIdList()==null || reqObj.getTenancyIdList().isEmpty())
			{
				throw new CustomFault("Tenancy Id List should be specified");
			}
			
			//defectId:20160106 - login id sent as the last element in the tenancy id list added by S Suresh
			int loginTenancyId = reqObj.getTenancyIdList().get(reqObj.getTenancyIdList().size()-1);
			reqObj.getTenancyIdList().remove(reqObj.getTenancyIdList().get(reqObj.getTenancyIdList().size()-1));
			reqObj.getTenancyIdList().removeAll(Collections.singleton(null));
			infoLogger.info("start of Machine performance report service----");
			//Custom Dates (fromDate,toDate) added by Juhi on 14-August-2013 
			List<MachinePerformanceReportImpl> implResponse=reportDetObj.getMachPerformanceReport(reqObj.getFromDate(),reqObj.getToDate(),reqObj.getPeriod(),
					reqObj.getMachineGroupIdList(),reqObj.getMachineProfileIdList(),reqObj.getTenancyIdList(),reqObj.getModelIdList(),
					reqObj.isGroupingOnAssetGroup());
			ListToStringConversion conversionObj = new ListToStringConversion();
			List<Integer> loginTenancyIdList = new LinkedList<Integer>();
			loginTenancyIdList.add(loginTenancyId);
			String tenancyIDList = conversionObj.getIntegerListString(loginTenancyIdList).toString();
	 	/*String contactQuery = "select c from AccountTenancyMapping a_t,AccountContactMapping a_c,ContactEntity c" +
					" where a_t.tenancy_id in ("+tenancyIDList+") and a_c.account_id = a_t.account_id and c.contact_id = a_c.contact_id ";*/
			
			// DefectID 20160318 @Kishore code changes for sending report over mail to the login id
			String contactQuery = "from ContactEntity c" +
		                " where c.contact_id = '"+loginId+"'"; 
			Session session= null;
			
			infoLogger.info("start of Machine performance report service contactQuery----"+contactQuery);
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
			    infoLogger.info("loginID-------:"+loginID);
			    userRole = contact.getRole().getRole_name();
			    infoLogger.info("userRole-------:"+userRole);
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
			//String jasperDate = null;
	    	 Date d=new Date();
	    	 infoLogger.info("Machine performance report service userRole----" );
	    	 	SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
	    	 	try {
	                  jasperDate = sdf.format(d);
	            }
	            catch(Exception e){
	                  e.printStackTrace();
	            }
	    	 	try {

	    	 		  infoLogger.info("Machine performance report service excel file generation----" );
	                  File FileName=new File(reportsDir+"Machine_Performance_Report_"+jasperDate+".xls");
	               //  File FileName=new File("D:/"+"Machine_Performance_Report_"+jasperDate+".xls");


	                  

	                  WorkbookSettings ws = new WorkbookSettings();
	                  WritableWorkbook workbook = Workbook.createWorkbook(
	                              FileName, ws);

	                  WritableSheet workSheet = null;
	                  workSheet = workbook.createSheet("Machine_Performance_Report", 0);

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

	                  if(implResponse.size()>0){
	                	  	
	                	  
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
	                        workSheet.addCell(new jxl.write.Label(0, 0, "VIN",
	                                    normalFormat));
	                        workSheet.addCell(new jxl.write.Label(1, 0, "Profile",
	                                    normalFormat));
	                        workSheet.addCell(new jxl.write.Label(2, 0, "Model",
	                                    normalFormat));

	                        workSheet.addCell(new jxl.write.Label(3, 0, "Engine Off(Period in hrs)",
	                                    normalFormat));
	                        workSheet.addCell(new jxl.write.Label(4, 0, "Engine Off(% of period)",
	                                    normalFormat));

	                        workSheet.addCell(new jxl.write.Label(5, 0, "Engine On(Period in hrs)",
	                                    normalFormat));
	                        workSheet.addCell(new jxl.write.Label(6, 0, "Engine On(% of period)",
	                                    normalFormat));

	                        workSheet.addCell(new jxl.write.Label(7, 0, "Working Time(Period in hrs)",
	                                    normalFormat));
	                        workSheet.addCell(new jxl.write.Label(8, 0, "Working Time(% of period)",
	                                    normalFormat));

	                        workSheet.addCell(new jxl.write.Label(9, 0, "Idle Time(Period in hrs)",
	                                    normalFormat));
	                        workSheet.addCell(new jxl.write.Label(10, 0, "Idle Time(% of period)",
	                                    normalFormat));

	                        workSheet.addCell(new jxl.write.Label(11, 0, "Power Band low(Period in hrs)",
	                                    normalFormat));
	                        workSheet.addCell(new jxl.write.Label(12, 0, "Power Band low(% of Working time)",
	                                    normalFormat));

	                        workSheet.addCell(new jxl.write.Label(13, 0, "Power Band medium(Period in hrs)",
	                                    normalFormat));
	                        workSheet.addCell(new jxl.write.Label(14, 0, "Power Band medium(% of Working time)",
	                                    normalFormat));

	                        workSheet.addCell(new jxl.write.Label(15, 0, "Power Band high(Period in hrs)",
	                                    normalFormat));
	                        workSheet.addCell(new jxl.write.Label(16, 0, "Power Band high(% of Working time)",
	                                    normalFormat));


	                        workSheet.addCell(new jxl.write.Label(17, 0, "Starting Engine Run Hrs(Value)",
	                                    normalFormat));
	                        workSheet.addCell(new jxl.write.Label(18, 0, "Starting Engine Run Hrs(Life)",
	                                    normalFormat));


	                        workSheet.addCell(new jxl.write.Label(19, 0, "Finish Engine Run Hrs(Value)",
	                                    normalFormat));
	                        workSheet.addCell(new jxl.write.Label(20, 0, "Finish Engine Run Hrs(Life)",
	                                    normalFormat));

	                        workSheet.addCell(new jxl.write.Label(21, 0, "Fuel Used ltr(Value)",
	                                    normalFormat));
	                        workSheet.addCell(new jxl.write.Label(22, 0, "Fuel Used ltr(Life)",
	                                    normalFormat));

	                        workSheet.addCell(new jxl.write.Label(23, 0, "Fuel Used idle ltr(Value)",
	                                    normalFormat));
	                        workSheet.addCell(new jxl.write.Label(24, 0, "Fuel Used idle ltr(Life)",
	                                    normalFormat));

	                        workSheet.addCell(new jxl.write.Label(25, 0, "Finish fuel level %(Value)",
	                                    normalFormat));
	                        workSheet.addCell(new jxl.write.Label(26, 0, "Finish fuel level %(Life)",
	                                    normalFormat));

	                        workSheet.addCell(new jxl.write.Label(27, 0, "Overall fuel consumption ltr/hr(Value)",
	                                    normalFormat));
	                        workSheet.addCell(new jxl.write.Label(28, 0, "Overall fuel consumption ltr/hr(Life)",
	                                    normalFormat));

	                        workSheet.addCell(new jxl.write.Label(29, 0, "Dealer Name",
	                                    normalFormat));

	                        int p = 0;
	                        /*double number = 0.9999999999999;
	                  DecimalFormat numberFormat = new DecimalFormat("#.00");
	                  System.out.println(numberFormat.format(number));*/
	                        infoLogger.info("Machine performance report service excel file ----" );
	                        for(int i=0;i<implResponse.size();i++){
	                        	 double OnOffsum=implResponse.get(i).getEngineOff()+implResponse.get(i).getEngineOn();
	                             //double OnOffsum1=machinePerformanceReportRespContractList.get(i).getIdleTime()+machinePerformanceReportRespContractList.get(i).getWorkingTime();
	                             double tempEngineoff=implResponse.get(i).getEngineOff();
	                             double tempEngineon=implResponse.get(i).getEngineOn();
	                             double tempWorkingTime=implResponse.get(i).getWorkingTime();
	                             double tempIdleTime=implResponse.get(i).getIdleTime();
	                             double tempEngineOffPeriodPerc=(tempEngineoff/OnOffsum)*100;;
	                             double tempEngineOnPeriodPerc=(tempEngineon/OnOffsum)*100;
	                             double tempWorkingTimePeriodPerc=(tempWorkingTime/OnOffsum)*100;
	                             double tempIdleTimePeriodPerc=(tempIdleTime/OnOffsum)*100;
	                             double PowerBandHighTime = 0;
	                             double PowerBandMediumTime = 0;
	                             double PowerBandLowTime = 0;
	                             double PowerBandSum=implResponse.get(i).getPowerBandHigh()+
	                            		 implResponse.get(i).getPowerBandLow()+
	                                     implResponse.get(i).getPowerBandMedium();
	                             
	                             if(implResponse.get(i).getPowerBandHigh()!=0){
	                            	 PowerBandHighTime = (implResponse.get(i).getPowerBandHigh()/PowerBandSum)*100;    
	                           }
	                        
	                           if(implResponse.get(i).getPowerBandLow()!=0){
	                        	   PowerBandLowTime = (implResponse.get(i).getPowerBandLow()/PowerBandSum)*100;   
	                           }
	                           
	                           if(implResponse.get(i).getPowerBandMedium()!=0){
	                        	   PowerBandMediumTime = (implResponse.get(i).getPowerBandMedium()/PowerBandSum)*100; 
	                           }
	                           
	                              workSheet.addCell(new jxl.write.Label(0, p + 1,
	                                          implResponse.get(i).getSerialNumber(), normalFormat1));

	                              workSheet.addCell(new jxl.write.Label(1, p + 1,
	                                          implResponse.get(i).getAsset_group_name(), normalFormat1));

	                              workSheet.addCell(new jxl.write.Label(2, p + 1,
	                                          implResponse.get(i).getModelName(), normalFormat1));

	                              workSheet.addCell(new jxl.write.Label(3, p + 1,
	                                          implResponse.get(i).getEngineOff()+"", normalFormat1));

	                              workSheet.addCell(new jxl.write.Label(4, p + 1,
	                                          String.valueOf(tempEngineOffPeriodPerc), normalFormat1));

	                              workSheet.addCell(new jxl.write.Label(5, p + 1,
	                                          implResponse.get(i).getEngineOn()+"", normalFormat1));

	                              workSheet.addCell(new jxl.write.Label(6, p + 1,
	                                          String.valueOf(tempEngineOnPeriodPerc), normalFormat1));

	                              workSheet.addCell(new jxl.write.Label(7, p + 1,
	                                          String.valueOf(implResponse.get(i).getWorkingTime()), normalFormat1));

	                              workSheet.addCell(new jxl.write.Label(8, p + 1,
	                                          String.valueOf(tempWorkingTimePeriodPerc), normalFormat1));

	                              workSheet.addCell(new jxl.write.Label(9, p + 1,
	                                          String.valueOf(implResponse.get(i).getIdleTime()), normalFormat1));

	                              workSheet.addCell(new jxl.write.Label(10, p + 1,
	                                          String.valueOf(tempIdleTimePeriodPerc), normalFormat1));

	                              workSheet.addCell(new jxl.write.Label(11, p + 1,
	                                          String.valueOf(implResponse.get(i).getPowerBandLow()), normalFormat1));

	                              workSheet.addCell(new jxl.write.Label(12, p + 1,
	                                          String.valueOf(PowerBandLowTime), normalFormat1));


	                              workSheet.addCell(new jxl.write.Label(13, p + 1,
	                                          String.valueOf(implResponse.get(i).getPowerBandMedium()), normalFormat1));

	                              workSheet.addCell(new jxl.write.Label(14, p + 1,
	                                          String.valueOf(PowerBandMediumTime), normalFormat1));


	                              workSheet.addCell(new jxl.write.Label(15, p + 1,
	                                          String.valueOf(implResponse.get(i).getPowerBandHigh()), normalFormat1));

	                              workSheet.addCell(new jxl.write.Label(16, p + 1,
	                                          String.valueOf(PowerBandHighTime), normalFormat1));

	                              //(double) Math.round(value * 100) / 100;

	                              workSheet.addCell(new jxl.write.Label(17, p + 1,
	                                          String.valueOf((double) Math.round(implResponse.get(i).getStartingEngineRunHours()*100)/100), normalFormat1));

	                              workSheet.addCell(new jxl.write.Label(18, p + 1,
	                                          String.valueOf((double) Math.round(implResponse.get(i).getStartingEngineRunHoursLife()*100)/100), normalFormat1));


	                              workSheet.addCell(new jxl.write.Label(19, p + 1,
	                                          String.valueOf((double) Math.round(implResponse.get(i).getFinishEngineRunHours()*100)/100), normalFormat1));

	                              workSheet.addCell(new jxl.write.Label(20, p + 1,
	                                          String.valueOf((double) Math.round(implResponse.get(i).getFinishEngineRunHoursLife()*100)/100), normalFormat1));

	                              //($F{assetGroupName}=="Backhoe Loaders" || $F{assetGroupName}=="Backhoe") && ($F{fuelUsedLitres}==0.0)

	                              //($F{assetGroupName}=="Compact wheel Loaders" || $F{assetGroupName}=="Wheel Laoders" || $F{assetGroupName}=="Wheel Loaders" ) || ($F{assetGroupName}=="Excavators" && $F{modelName}!="JS 81")


	                              String fuelusedltr;
	                              if((implResponse.get(i).getAsset_group_name().equalsIgnoreCase("Backhoe Loaders") || implResponse.get(i).getAsset_group_name().equalsIgnoreCase("Backhoe")) && implResponse.get(i).getFuelUsedLitres()==0.0){
	                                    fuelusedltr="N/A";
	                              }
	                              else if((implResponse.get(i).getAsset_group_name().equalsIgnoreCase("Compact wheel Loaders") || implResponse.get(i).getAsset_group_name().equalsIgnoreCase("Wheel Laoders") || implResponse.get(i).getAsset_group_name().equalsIgnoreCase("Wheel Loaders")) || (implResponse.get(i).getAsset_group_name().equalsIgnoreCase("Excavators") && !(implResponse.get(i).getModelName().equalsIgnoreCase("JS 81")))){
	                                    fuelusedltr="N/A";      
	                              }
	                              else{
	                                    fuelusedltr=String.valueOf((double) Math.round(implResponse.get(i).getFuelUsedLitres()*100)/100);
	                              }


	                              workSheet.addCell(new jxl.write.Label(21, p + 1,
	                                          fuelusedltr, normalFormat1));

	                              String fuelusedltrlife;

	                              if((implResponse.get(i).getAsset_group_name().equalsIgnoreCase("Compact wheel Loaders") || implResponse.get(i).getAsset_group_name().equalsIgnoreCase("Wheel Laoders") || implResponse.get(i).getAsset_group_name().equalsIgnoreCase("Wheel Loaders")) || (implResponse.get(i).getAsset_group_name().equalsIgnoreCase("Excavators") && !(implResponse.get(i).getModelName().equalsIgnoreCase("JS 81")))){
	                                    fuelusedltrlife="N/A";  
	                              }
	                              else{
	                                    fuelusedltrlife=String.valueOf((double) Math.round(implResponse.get(i).getFuelUsedLitresLife()*100)/100);
	                              }

	                              workSheet.addCell(new jxl.write.Label(22, p + 1,
	                                          fuelusedltrlife, normalFormat1));


	                              String fuelusedidleltr;
	                              if((implResponse.get(i).getAsset_group_name().equalsIgnoreCase("Backhoe Loaders") || implResponse.get(i).getAsset_group_name().equalsIgnoreCase("Backhoe")) && implResponse.get(i).getFuelUsedIdleLitres()==0.0){
	                                    fuelusedidleltr="N/A";
	                              }
	                              else if((implResponse.get(i).getAsset_group_name().equalsIgnoreCase("Compact wheel Loaders") || implResponse.get(i).getAsset_group_name().equalsIgnoreCase("Wheel Laoders") || implResponse.get(i).getAsset_group_name().equalsIgnoreCase("Wheel Loaders")) || (implResponse.get(i).getAsset_group_name().equalsIgnoreCase("Excavators") && !(implResponse.get(i).getModelName().equalsIgnoreCase("JS 81")))){
	                                    fuelusedidleltr="N/A";  
	                              }
	                              else{
	                                    fuelusedidleltr=String.valueOf((double) Math.round(implResponse.get(i).getFuelUsedIdleLitres()*100)/100);
	                              }


	                              workSheet.addCell(new jxl.write.Label(23, p + 1,
	                                          fuelusedidleltr, normalFormat1));


	                              String fuelusedidleltrlife;

	                              if((implResponse.get(i).getAsset_group_name().equalsIgnoreCase("Compact wheel Loaders") || implResponse.get(i).getAsset_group_name().equalsIgnoreCase("Wheel Laoders") || implResponse.get(i).getAsset_group_name().equalsIgnoreCase("Wheel Loaders")) || (implResponse.get(i).getAsset_group_name().equalsIgnoreCase("Excavators") && !(implResponse.get(i).getModelName().equalsIgnoreCase("JS 220")))){
	                                    fuelusedidleltrlife="N/A";    
	                              }
	                              else{
	                                    fuelusedidleltrlife=String.valueOf((double) Math.round(implResponse.get(i).getFuelUsedIdleLitresLife()*100)/100);
	                              }

	                              workSheet.addCell(new jxl.write.Label(24, p + 1,
	                                          fuelusedidleltrlife, normalFormat1));



	                              String finishfuellevel;
	                              if((implResponse.get(i).getAsset_group_name().equalsIgnoreCase("Backhoe Loaders") || implResponse.get(i).getAsset_group_name().equalsIgnoreCase("Backhoe")) && implResponse.get(i).getFinishFuelLevel()=="0.0"){
	                                    finishfuellevel="N/A";
	                              }
	                              else if((implResponse.get(i).getAsset_group_name().equalsIgnoreCase("Compact wheel Loaders") || implResponse.get(i).getAsset_group_name().equalsIgnoreCase("Wheel Laoders") || implResponse.get(i).getAsset_group_name().equalsIgnoreCase("Wheel Loaders")) || (implResponse.get(i).getAsset_group_name().equalsIgnoreCase("Excavators") && !(implResponse.get(i).getModelName().equalsIgnoreCase("JS 220")))){
	                                    finishfuellevel="N/A";  
	                              }
	                              else{
	                                    finishfuellevel=implResponse.get(i).getFinishFuelLevel();
	                              }

	                              workSheet.addCell(new jxl.write.Label(25, p + 1,
	                                          finishfuellevel, normalFormat1));


	                              String finifhfuellevellife;

	                              if((implResponse.get(i).getAsset_group_name().equalsIgnoreCase("Compact wheel Loaders") || implResponse.get(i).getAsset_group_name().equalsIgnoreCase("Wheel Laoders") || implResponse.get(i).getAsset_group_name().equalsIgnoreCase("Wheel Loaders")) || (implResponse.get(i).getAsset_group_name().equalsIgnoreCase("Excavators") && !(implResponse.get(i).getModelName().equalsIgnoreCase("JS 220")))){
	                                    finifhfuellevellife="N/A";    
	                              }
	                              else{
	                                    finifhfuellevellife=implResponse.get(i).getFinishFuelLevelLife();
	                              }

	                              workSheet.addCell(new jxl.write.Label(26, p + 1,
	                                          finifhfuellevellife, normalFormat1));

	                              double tempFinishEngineRunHours=implResponse.get(i).getFinishEngineRunHours();
	                              double OverallFuelConsumptionLitresLife = 0.0;
	                              if(implResponse.get(i).getFinishEngineRunHours()!=0){
	                            	  OverallFuelConsumptionLitresLife = (implResponse.get(i).getFuelUsedLitresLife()/tempFinishEngineRunHours);
	                              }
	                              
	                              String overallfuelconsumption;
	                              if((implResponse.get(i).getAsset_group_name().equalsIgnoreCase("Backhoe Loaders") || implResponse.get(i).getAsset_group_name().equalsIgnoreCase("Backhoe")) && implResponse.get(i).getOverallFuelConsumptionLitres()==0.0){
	                                    overallfuelconsumption="N/A";
	                              }
	                              else if((implResponse.get(i).getAsset_group_name().equalsIgnoreCase("Compact wheel Loaders") || implResponse.get(i).getAsset_group_name().equalsIgnoreCase("Wheel Laoders") || implResponse.get(i).getAsset_group_name().equalsIgnoreCase("Wheel Loaders")) || (implResponse.get(i).getAsset_group_name().equalsIgnoreCase("Excavators") && !(implResponse.get(i).getModelName().equalsIgnoreCase("JS 220")))){
	                                    overallfuelconsumption="N/A"; 
	                              }
	                              else{
	                                    overallfuelconsumption=OverallFuelConsumptionLitresLife+"";
	                              }

	                              workSheet.addCell(new jxl.write.Label(27, p + 1,
	                                          overallfuelconsumption, normalFormat1));


	                              String overallfuelconsumptionlife;

	                              if((implResponse.get(i).getAsset_group_name().equalsIgnoreCase("Compact wheel Loaders") || implResponse.get(i).getAsset_group_name().equalsIgnoreCase("Wheel Laoders") || implResponse.get(i).getAsset_group_name().equalsIgnoreCase("Wheel Loaders")) || (implResponse.get(i).getAsset_group_name().equalsIgnoreCase("Excavators") && !(implResponse.get(i).getModelName().equalsIgnoreCase("JS 220")))){
	                                    overallfuelconsumptionlife="N/A";   
	                              }
	                              else{
	                                    overallfuelconsumptionlife=OverallFuelConsumptionLitresLife+"";
	                              }

	                              workSheet.addCell(new jxl.write.Label(28, p + 1,
	                                          overallfuelconsumptionlife, normalFormat1));

	                              workSheet.addCell(new jxl.write.Label(29, p + 1,
	                                          implResponse.get(i).getDealerName(), normalFormat1));

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
	                 fLogger.fatal(e);

	            }
	    	    infoLogger.info("Mailing Machine_Performance_Report_: start "+jasperDate);
	            String duefilesToBeAttcahed=null;
	            File sourceFile = new File(reportsDir+"Machine_Performance_Report_"+loginID+"_"+jasperDate+".xls");
	    		infoLogger.info("Machine_Performance_Report_ sourece File:"+sourceFile);
	    		SendEmail email = new SendEmail();
	    		String fileToBeAttcahed =reportsDir+"Machine_Performance_Report_"+loginID+"_"+jasperDate+".xls";
	    		infoLogger.info("Machine_Performance_Report_ loginID:"+loginID);
	    		
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
	    		    infoLogger.info("Machine_Performance_Report_ Primary Email ID:"+receiverMail);
	    		    FileOutputStream fos;
	    		   
	    			try {
	    				fos = new FileOutputStream(reportsDir+"Machine_Performance_Report_"+loginID+"_"+jasperDate+".zip");
	    				duefilesToBeAttcahed =reportsDir+"Machine_Performance_Report_"+loginID+"_"+jasperDate+".zip";
	    				infoLogger.info("fileToBeAttcahed :"+duefilesToBeAttcahed);
	    				ZipOutputStream zos = new ZipOutputStream(fos);
	    				addToZipFile(fileToBeAttcahed,zos);
	    				zos.close();
	    				fos.close();
	    			} catch (FileNotFoundException e) {
	    				// TODO Auto-generated catch block
	    				 fLogger.fatal(e);
	    			}
	    			//String fileToBeAttcahed= fileToBeAttcahed1;
	    			catch (IOException e) {
	    				// TODO Auto-generated catch block
	    				 fLogger.fatal(e);
	    			}
	    			 infoLogger.info("Machine performance report service excel file sending across mail ----" );
	    		//String emailTo = "soorneedi.babu@wipro.com;deepthi.rao@wipro.com";
	    		String emailSubject = "Usage machine performance report";
	    		String emailBody = "Dear Customer \r\n Please find the attached Machine_Performance_Report_.\r\n Thanks, \r\n Deepthi. ";
	    		String result = email.sendMailWithAttachment(receiverMail, emailSubject, emailBody, duefilesToBeAttcahed);
	    		 infoLogger.info("Machine performance report service excel file after sending ----"+result );
	    		File zipFile = new File(reportsDir+"Machine_Performance_Report_"+loginID+"_"+jasperDate+".zip");
	    		zipFile.delete();
	    		sourceFile.delete();
	     	}
	    	else{
			for(int i=0;i<implResponse.size();i++)
			{
				MachinePerformanceReportRespContract respObj=new MachinePerformanceReportRespContract();
				
				//Changes Done by Juhi On 7 May2013
				if (implResponse.get(i).getEngineOff()<0) 
				{
					respObj.setEngineOff(0);

				}
				else 
				{
					respObj.setEngineOff(implResponse.get(i).getEngineOff());

				}
				if (implResponse.get(i).getEngineOn()<0) {
					respObj.setEngineOn(0);

				} else {
					respObj.setEngineOn(implResponse.get(i).getEngineOn());

				}
				if (implResponse.get(i).getWorkingTime()<0) {
					respObj.setWorkingTime(0);

				} else {
					respObj.setWorkingTime(implResponse.get(i).getWorkingTime());

				}
				if (implResponse.get(i).getIdleTime()<0) {
					respObj.setIdleTime(0);

				} else {
					respObj.setIdleTime(implResponse.get(i).getIdleTime());

				}
				if (implResponse.get(i).getPowerBandLow()<0) {
					respObj.setPowerBandLow(0);

				} else {
					respObj.setPowerBandLow(implResponse.get(i).getPowerBandLow());

				}
				
				if(implResponse.get(i).getPowerBandMedium()<0)
					respObj.setPowerBandMedium(0);
				else
					respObj.setPowerBandMedium(implResponse.get(i).getPowerBandMedium());
				
				
				if(implResponse.get(i).getPowerBandHigh()<0)
					respObj.setPowerBandHigh(0);
				else
					respObj.setPowerBandHigh(implResponse.get(i).getPowerBandHigh());			
				
				if(implResponse.get(i).getStartingEngineRunHours()<0)
					respObj.setStartingEngineRunHours(0);
				else
					respObj.setStartingEngineRunHours(implResponse.get(i).getStartingEngineRunHours());
				
				if(implResponse.get(i).getFinishEngineRunHours()<0)
					respObj.setFinishEngineRunHours(0);
				else
					respObj.setFinishEngineRunHours(implResponse.get(i).getFinishEngineRunHours());
				
				if((implResponse.get(i).getFuelUsedLitres()<0))
					respObj.setFuelUsedLitres(0);
				else
					respObj.setFuelUsedLitres(implResponse.get(i).getFuelUsedLitres());
				
				if((implResponse.get(i).getFuelUsedIdleLitres()<0))
					respObj.setFuelUsedIdleLitres(0);
				else
					respObj.setFuelUsedIdleLitres(implResponse.get(i).getFuelUsedIdleLitres());
//				Keerthi : 30/05/14 : null check for fuel level added : no entries for PIN 356555059988215 in AMH
				if(implResponse.get(i).getFinishFuelLevel()!=null){
					if( ((Double.parseDouble(implResponse.get(i).getFinishFuelLevel()))<0))
						respObj.setFinishFuelLevel("0");
					else
						respObj.setFinishFuelLevel(implResponse.get(i).getFinishFuelLevel());
				}
				
				
				if((implResponse.get(i).getOverallFuelConsumptionLitres()<0))
					respObj.setOverallFuelConsumptionLitres(0);
				else
					respObj.setOverallFuelConsumptionLitres(implResponse.get(i).getOverallFuelConsumptionLitres());
				
				respObj.setSerialNumber(implResponse.get(i).getSerialNumber());
				respObj.setAsset_group_name(implResponse.get(i).getAsset_group_name());
				
				if((implResponse.get(i).getFinishEngineRunHoursLife()<0))
					respObj.setFinishEngineRunHoursLife(0);
				else
					respObj.setFinishEngineRunHoursLife(implResponse.get(i).getFinishEngineRunHoursLife());
				
				if((implResponse.get(i).getStartingEngineRunHoursLife()<0))
					respObj.setStartingEngineRunHoursLife(0);
				else
					respObj.setStartingEngineRunHoursLife(implResponse.get(i).getStartingEngineRunHoursLife());
				
				if((implResponse.get(i).getFuelUsedLitresLife()<0))
					respObj.setFuelUsedLitresLife(0);
				else
					respObj.setFuelUsedLitresLife(implResponse.get(i).getFuelUsedLitresLife());
				
				if((implResponse.get(i).getFuelUsedIdleLitresLife()<0))
					respObj.setFuelUsedIdleLitresLife(0);
				else
					respObj.setFuelUsedIdleLitresLife(implResponse.get(i).getFuelUsedIdleLitresLife());
				//added by smitha on Nov 6th 2013...Defect ID:20131106
				if(implResponse.get(i).getFinishFuelLevelLife()!=null){
				if( ((Double.parseDouble(implResponse.get(i).getFinishFuelLevelLife()))<0))
					respObj.setFinishFuelLevelLife("0");
				else
					respObj.setFinishFuelLevelLife(implResponse.get(i).getFinishFuelLevelLife());
				}else{
					respObj.setFinishFuelLevelLife("0");
				}
				//ended on Nov 6th 2013...Defect ID:20131106
				respObj.setAssetGroupId(implResponse.get(i).getAssetGroupId());
				respObj.setCustomMachineGroupId(implResponse.get(i).getCustomMachineGroupId());
				respObj.setCustomMachineGroupName(implResponse.get(i).getCustomMachineGroupName());
				respObj.setTenancyName(implResponse.get(i).getTenancyName());
				respObj.setTenancyId(implResponse.get(i).getTenancyId());
				respObj.setModelId(implResponse.get(i).getModelId());
				respObj.setModelName(implResponse.get(i).getModelName());
				//DefectID:20150223 @Suprava Added DealerName as additional Parameter
				respObj.setDealerName(implResponse.get(i).getDealerName());
				
				listResponse.add(respObj);
			}
	     }
		}
		
		catch(CustomFault e)
		{
			bLogger.error("Custom Fault: "+ e.getFaultInfo());
			return listResponse;
		}
		
		infoLogger.info("Machine performance report service listResponse----"+listResponse.size() );
		return listResponse;
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
