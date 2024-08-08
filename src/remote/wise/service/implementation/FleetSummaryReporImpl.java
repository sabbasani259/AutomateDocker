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
import remote.wise.businessentity.RoleEntity;
import remote.wise.businessobject.ReportDetailsBO;
import remote.wise.exception.CustomFault;
import remote.wise.handler.SendEmail;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
import remote.wise.service.datacontract.FleetSummaryReportReqContract;
import remote.wise.service.datacontract.FleetSummaryReportRespContract;
//import remote.wise.util.WiseLogger;
import remote.wise.util.HibernateUtil;
import remote.wise.util.ListToStringConversion;

public class FleetSummaryReporImpl {
	//DF:2013:12:13 : Conversion from Long to Double for all the required fields : Suprava
	//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
	//public static WiseLogger businessError = WiseLogger.getLogger("FleetSummaryReporImpl:","businessError");
	
	private String serialNumber;
	private String machineName;//nickName,TABLE-asset
	
	private double averageFuelConsumption;
	private double fuelused;
	private double fuelUsedInIdle;	
		
	private double powerBandhigh;
	private double powerBandMedium;
	private double powerBandLow;	
	
	private double workingTime;
	private double idleTime;
	private double engineOff;
	
	private double machineHours;//(inPeriod);
	private double totalMachineLifeHours;
	
	private int machineProfile;//asset_Group_Id,TABLE-asset_class_dimension
	private String profile;//asset_Group_Name,TABLE-asset_class_dimension
	
	private int  machineGroupIdList;//CustomassetGroupIdList
	private String machineGroupName;//CustomAssetGroupName
	
	private int modelIdList;
	private String modelName;
	
	private int tenanctIdList;
	private String tenancyName;
	//DefectId:20150216 @ Suprava Dealer As a new parameter Added
	private String dealername;
	
	/**
	 * @return the dealername
	 */
	public String getDealername() {
		return dealername;
	}

	/**
	 * @param dealername the dealername to set
	 */
	public void setDealername(String dealername) {
		this.dealername = dealername;
	}

	public String getTenancyName() {
		return tenancyName;
	}

	public void setTenancyName(String tenancyName) {
		this.tenancyName = tenancyName;
	}
	
	
	public String getMachineGroupName() {
		return machineGroupName;
	}

	public void setMachineGroupName(String machineGroupName) {
		this.machineGroupName = machineGroupName;
	}

	public double getAverageFuelConsumption() {
		return averageFuelConsumption;
	}

	public void setAverageFuelConsumption(double averageFuelConsumption) {
		this.averageFuelConsumption = averageFuelConsumption;
	}

	public double getFuelused() {
		return fuelused;
	}

	public void setFuelused(double fuelused) {
		this.fuelused = fuelused;
	}

	public double getFuelUsedInIdle() {
		return fuelUsedInIdle;
	}

	public void setFuelUsedInIdle(double fuelUsedInIdle) {
		this.fuelUsedInIdle = fuelUsedInIdle;
	}

	public double getPowerBandhigh() {
		return powerBandhigh;
	}

	public void setPowerBandhigh(double powerBandhigh) {
		this.powerBandhigh = powerBandhigh;
	}

	public double getPowerBandMedium() {
		return powerBandMedium;
	}

	public void setPowerBandMedium(double powerBandMedium) {
		this.powerBandMedium = powerBandMedium;
	}

	public double getPowerBandLow() {
		return powerBandLow;
	}

	public void setPowerBandLow(double powerBandLow) {
		this.powerBandLow = powerBandLow;
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

	public double getEngineOff() {
		return engineOff;
	}

	public void setEngineOff(double engineOff) {
		this.engineOff = engineOff;
	}

	public double getMachineHours() {
		return machineHours;
	}

	public void setMachineHours(double machineHours) {
		this.machineHours = machineHours;
	}

	public double getTotalMachineLifeHours() {
		return totalMachineLifeHours;
	}

	public void setTotalMachineLifeHours(double totalMachineLifeHours) {
		this.totalMachineLifeHours = totalMachineLifeHours;
	}

	public int getMachineProfile() {
		return machineProfile;
	}

	public void setMachineProfile(int machineProfile) {
		this.machineProfile = machineProfile;
	}

	public String getProfile() {
		return profile;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

	public String getMachineName() {
		return machineName;
	}

	public void setMachineName(String machineName) {
		this.machineName = machineName;
	}

	public int getMachineGroupIdList() {
		return machineGroupIdList;
	}

	public void setMachineGroupIdList(int machineGroupIdList) {
		this.machineGroupIdList = machineGroupIdList;
	}

	public int getTenanctIdList() {
		return tenanctIdList;
	}

	public void setTenanctIdList(int tenanctIdList) {
		this.tenanctIdList = tenanctIdList;
	}

	public int getModelIdList() {
		return modelIdList;
	}

	public void setModelIdList(int modelIdList) {
		this.modelIdList = modelIdList;
	}

		public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
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
 * 
 * @param reqObj, can get list/summary on fleets based on reqObj 
 * @return listResponse, list of response as fleetSummary based on the input reqObj 
 */
	public List<FleetSummaryReportRespContract> getFleetSummaryReportService(FleetSummaryReportReqContract reqObj)
	{
		FleetSummaryReportRespContract response=null;
		List<FleetSummaryReportRespContract> listResponse=new LinkedList<FleetSummaryReportRespContract>();
		Logger infoLogger = InfoLoggerClass.logger;
		//Check for Mandatory Parameters
		
	//	Logger businessError = Logger.getLogger("businessErrorLogger");
		
    	Logger fLogger = FatalLoggerClass.logger;
    	Logger bLogger = BusinessErrorLoggerClass.logger;
		
    	infoLogger.info("start of Fleet summary report impl");
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
			
			//Custom Dates (fromDate,toDate) added by Juhi on 19-August-2013 
			if(((reqObj.getPeriod()==null))&&((reqObj.getFromDate()==null) &&(reqObj.getToDate()==null)))
			{
				
				bLogger.error("Please pass either Period or custom dates");
				throw new CustomFault("Either Period or custom dates are not specified");
			}
			
			if(reqObj.getTenanctIdList()==null || reqObj.getTenanctIdList().isEmpty())
			{
				throw new CustomFault("TenancyId has to be passed");
			}
			
			//Check for the valid String input for period
			if(reqObj.getPeriod()!=null)
            {
                  if( !(reqObj.getPeriod().equalsIgnoreCase("Week") || reqObj.getPeriod().equalsIgnoreCase("Month") ||
                              reqObj.getPeriod().equalsIgnoreCase("Quarter") || reqObj.getPeriod().equalsIgnoreCase("Year") ||
                              reqObj.getPeriod().equalsIgnoreCase("Last Week") ||      reqObj.getPeriod().equalsIgnoreCase("Last Month") ||
                              reqObj.getPeriod().equalsIgnoreCase("Last Quarter") || reqObj.getPeriod().equalsIgnoreCase("Last Year") ) )
                  {
                        throw new CustomFault("Invalid Time Period");
                        
                  }
            }

		}

		catch(CustomFault e)
		{
			bLogger.error("Custom Fault: "+ e.getFaultInfo());
		}
		
		
		//Call the BO to get the details
		ReportDetailsBO reportDetailsBO=new ReportDetailsBO();
		
		//Custom Dates (fromDate,toDate) added by Juhi on 19-August-2013 
		List<FleetSummaryReporImpl> implObj= reportDetailsBO.getFleetSummaryReportService(reqObj.getFromDate(),reqObj.getToDate(),reqObj.getPeriod(),reqObj.getMachineGroupIdList(),reqObj.getMachineProfileIdList(),reqObj.getTenanctIdList(),reqObj.getModelIdList(),reqObj.isGroupingOnAssetGroup(),reqObj.getLoginTenancyIdList());	
		
        
        
        ListToStringConversion conversionObj = new ListToStringConversion();
		String tenancyIDList = conversionObj.getIntegerListString(reqObj.getLoginTenancyIdList()).toString();
    	/*String contactQuery = "select c from AccountTenancyMapping a_t,AccountContactMapping a_c,ContactEntity c" +
				" where a_t.tenancy_id in ("+tenancyIDList+") and a_c.account_id = a_t.account_id and c.contact_id = a_c.contact_id ";*/
		// DefectID 20160318 @Kishore code changes for sending report over mail to the login id
		
		String contactQuery = "from ContactEntity c" +
	                " where c.contact_id = '"+loginId+"'"; 
		infoLogger.info("Fleet summary report Query:"+contactQuery);
		Session session= null;
		
			session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();		    
		    Query query = session.createQuery(contactQuery);
		    Iterator itr = query.list().iterator();
		    String userRole = null;
		    ContactEntity contact = null;
		    while(itr.hasNext())
		    {
		    	contact = (ContactEntity)itr.next();
		    }
		    String loginID = contact.getContact_id();
		    infoLogger.info("loginID-------:"+loginID);
		    userRole = contact.getRole().getRole_name();
		    infoLogger.info("Fleet summary report userRole:"+userRole);
		   // System.out.println(userRole);
		    String jasperDate = null;
		    
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


        if((userRole!=null) && (userRole.equalsIgnoreCase("JCB Account") || userRole.equalsIgnoreCase("Customer Care") || userRole.equalsIgnoreCase("JCB HO"))){
        try {
        	
        		Date d=new Date();
        		
        		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        		jasperDate = sdf.format(d);
              //String FileName=request.getRealPath("/excelReport") + "/"+"Fleet_Summary_Report_"+jasperDate+".xls";
              File tempFile = new File(reportsDir+"Fleet_Summary_Report_"+loginID+"_"+jasperDate+".xls");
           //   File tempFile=new File("D:/"+"Fleet_Summary_Report_"+jasperDate+".xls");
              WorkbookSettings ws = new WorkbookSettings();
              WritableWorkbook workbook = Workbook.createWorkbook(tempFile, ws);

              WritableSheet workSheet = null;
              workSheet = workbook.createSheet("Fleet_Summary_Report", 0);

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
            	  infoLogger.info("Fleet summary report inside excel sheet process");
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
                    workSheet.addCell(new jxl.write.Label(1, 0, "Profile",
                                normalFormat));
                    workSheet.addCell(new jxl.write.Label(2, 0, "Total Machine Life Hours",
                                normalFormat));
                    workSheet.addCell(new jxl.write.Label(3, 0, "Machine Hours (In Period)",
                                normalFormat));
                    workSheet.addCell(new jxl.write.Label(4, 0, "Engine Off (%)",
                                normalFormat));
                    workSheet.addCell(new jxl.write.Label(5, 0, "Idle Time (%)",
                                normalFormat));
                    workSheet.addCell(new jxl.write.Label(6, 0,
                                "Working Time (%)", normalFormat));
                    workSheet.addCell(new jxl.write.Label(7, 0,
                                "Power Band Low (%)", normalFormat));
                    workSheet.addCell(new jxl.write.Label(8, 0, "Power Band Medium (%)",
                                normalFormat));
                    workSheet.addCell(new jxl.write.Label(9, 0, "Power Band High (%)",
                                normalFormat));
                    workSheet.addCell(new jxl.write.Label(10, 0, "Fuel Used In Idle (Litres)",
                                normalFormat));

                    workSheet.addCell(new jxl.write.Label(11, 0, "Fuel Used (Litres)",
                                normalFormat));

                    workSheet.addCell(new jxl.write.Label(12, 0, "Average Fuel Consumption (Litres/h)",
                                normalFormat));
                    workSheet.addCell(new jxl.write.Label(13, 0, "Dealer Name",
                                normalFormat));


                    int p = 0;
                    for(int i=0;i<implObj.size();i++){
                    	if( implObj.get(i).getSerialNumber().equalsIgnoreCase("Summary"))
                    		continue;
                          workSheet.addCell(new jxl.write.Label(0, p + 1,
                        		  implObj.get(i).getSerialNumber(), normalFormat1));

                          workSheet.addCell(new jxl.write.Label(1, p + 1,
                                      implObj.get(i).getProfile(), normalFormat3));

                          workSheet.addCell(new jxl.write.Label(2, p + 1,
                                      String.valueOf(implObj.get(i).getTotalMachineLifeHours()), normalFormat3));

                          workSheet.addCell(new jxl.write.Label(3, p + 1,
                                      String.valueOf(implObj.get(i).getMachineHours()), normalFormat3));

                          //$F{engineOff}==0.0?0.0:(($F{engineOff}*100.0)/($F{engineOff}+$F{idleTime}+$F{workingTime}))
                          Double value;
                          if(implObj.get(i).getEngineOff()==0.0){
                                value=0.0;
                          }
                          else{
                                value=(implObj.get(i).getEngineOff()*100.0)/(implObj.get(i).getEngineOff()+implObj.get(i).getIdleTime()+implObj.get(i).getWorkingTime());      
                          }

                          String engineoffperc=String.valueOf(value);

                          workSheet.addCell(new jxl.write.Label(4, p + 1,
                                      engineoffperc, normalFormat3));

                          //$F{idleTime}==0.0?0.0:(($F{idleTime}*100.0)/($F{engineOff}+$F{idleTime}+$F{workingTime}))

                          Double idle;
                          if(implObj.get(i).getIdleTime()==0.0){
                                idle=0.0;
                          }
                          else{
                                idle=(implObj.get(i).getIdleTime()*100.0)/(implObj.get(i).getEngineOff()+implObj.get(i).getIdleTime()+implObj.get(i).getWorkingTime());
                          }

                          String idleperc=String.valueOf(idle);

                          workSheet.addCell(new jxl.write.Label(5, p + 1,
                                      idleperc, normalFormat3));

                          //$F{workingTime}==0.0?0.0:(($F{workingTime}*100.0)/($F{engineOff}+$F{idleTime}+$F{workingTime}))

                          Double working;
                          if(implObj.get(i).getWorkingTime()==0.0){
                                working=0.0;
                          }
                          else{
                                working=(implObj.get(i).getWorkingTime()*100.0)/(implObj.get(i).getEngineOff()+implObj.get(i).getIdleTime()+implObj.get(i).getWorkingTime());
                          }

                          String workingperc=String.valueOf(working);

                          workSheet.addCell(new jxl.write.Label(6, p + 1,
                                      workingperc, normalFormat3));

                          //$F{powerBandLow}==0.0?0.0:($F{powerBandLow}*100.0)/($F{powerBandLow}+$F{powerBandMedium}+$F{powerBandhigh})
                          Double bandlow;
                          if(implObj.get(i).getPowerBandLow()==0.0){
                                bandlow=0.0;
                          }
                          else{
                                bandlow=(implObj.get(i).getPowerBandLow()*100.0)/(implObj.get(i).getPowerBandLow()+implObj.get(i).getPowerBandhigh()+implObj.get(i).getPowerBandMedium());
                          }

                          String bandlowperc=String.valueOf(bandlow);

                          workSheet.addCell(new jxl.write.Label(7, p + 1,
                                      bandlowperc, normalFormat3));
                          //$F{powerBandMedium}==0.0?0.0:(($F{powerBandMedium}*100.0)/($F{powerBandLow}+$F{powerBandMedium}+$F{powerBandhigh}))
                          Double bandmed;
                          if(implObj.get(i).getPowerBandMedium()==0.0){
                                bandmed=0.0;
                          }
                          else{
                                bandmed=(implObj.get(i).getPowerBandMedium()*100.0)/(implObj.get(i).getPowerBandLow()+implObj.get(i).getPowerBandhigh()+implObj.get(i).getPowerBandMedium());
                          }

                          String bandmedperc=String.valueOf(bandmed);

                          workSheet.addCell(new jxl.write.Label(8, p + 1,
                                      bandmedperc, normalFormat3));

                          //$F{powerBandhigh}==0.0?0.0:(($F{powerBandhigh}*100.0)/($F{powerBandLow}+$F{powerBandMedium}+$F{powerBandhigh}))

                          Double bandhigh;
                          if(implObj.get(i).getPowerBandhigh()==0.0){
                                bandhigh=0.0;
                          }
                          else{
                                bandhigh=(implObj.get(i).getPowerBandhigh()*100.0)/(implObj.get(i).getPowerBandLow()+implObj.get(i).getPowerBandhigh()+implObj.get(i).getPowerBandMedium());
                          }

                          String bandhighperc=String.valueOf(bandhigh);

                          workSheet.addCell(new jxl.write.Label(9, p + 1,
                                      bandhighperc, normalFormat3));



                          //($F{profile}=="Compact wheel Loaders" || $F{profile}=="Wheel Laoders" || $F{profile}=="Wheel Loaders")|| ($F{profile}=="Excavators" && $F{modelName}!="JS 220")
                          String fuelusedinidle;
                          if(implObj.get(i).getProfile().equalsIgnoreCase("Compact wheel Loaders") || implObj.get(i).getProfile().equalsIgnoreCase("Wheel Laoders") || implObj.get(i).getProfile().equalsIgnoreCase("Wheel Loaders")
                                      || (implObj.get(i).getProfile().equalsIgnoreCase("Excavators") && implObj.get(i).getModelName()!="JS 220")
                                      ){
                                fuelusedinidle="N/A";
                          }

                          //($F{profile}=="Backhoe Loaders " || $F{profile}=="Backhoe") && ($F{fuelUsedInIdle}==0.0)

                          else if((implObj.get(i).getProfile().equalsIgnoreCase("Backhoe Loaders") || implObj.get(i).getProfile().equalsIgnoreCase("Backhoe")) && implObj.get(i).getFuelUsedInIdle()==0.0){

                                fuelusedinidle="N/A";
                          }

                          else{
                                fuelusedinidle=String.valueOf(implObj.get(i).getFuelUsedInIdle());
                          }

                          workSheet.addCell(new jxl.write.Label(10, p + 1,
                                      fuelusedinidle, normalFormat3));


                          //($F{profile}=="Compact wheel Loaders" || $F{profile}=="Wheel Laoders" || $F{profile}=="Wheel Loaders")|| ($F{profile}=="Excavators" && $F{modelName}!="JS 220")

                          String fuelusedinlitres;
                          if(implObj.get(i).getProfile().equalsIgnoreCase("Compact wheel Loaders") || implObj.get(i).getProfile().equalsIgnoreCase("Wheel Laoders") || implObj.get(i).getProfile().equalsIgnoreCase("Wheel Loaders")
                                      || (implObj.get(i).getProfile().equalsIgnoreCase("Excavators") && implObj.get(i).getModelName()!="JS 220")
                                      ){
                                fuelusedinlitres="N/A";
                          }

                          //($F{profile}=="Backhoe Loaders " || $F{profile}=="Backhoe") && ($F{fuelUsedInIdle}==0.0)

                          else if((implObj.get(i).getProfile().equalsIgnoreCase("Backhoe Loaders") || implObj.get(i).getProfile().equalsIgnoreCase("Backhoe")) && implObj.get(i).getFuelused()==0.0){

                                fuelusedinlitres="N/A";
                          }

                          else{
                                fuelusedinlitres=String.valueOf(implObj.get(i).getFuelused());

                          }

                          workSheet.addCell(new jxl.write.Label(11, p + 1,
                                      fuelusedinlitres, normalFormat3));


                          //($F{profile}=="Compact wheel Loaders" || $F{profile}=="Wheel Laoders" || $F{profile}=="Wheel Loaders")|| ($F{profile}=="Excavators" && $F{modelName}!="JS 220")

                          String avgfuelusedinlitres;
                          if(implObj.get(i).getProfile().equalsIgnoreCase("Compact wheel Loaders") || implObj.get(i).getProfile().equalsIgnoreCase("Wheel Laoders") || implObj.get(i).getProfile().equalsIgnoreCase("Wheel Loaders")
                                      || (implObj.get(i).getProfile().equalsIgnoreCase("Excavators") && implObj.get(i).getModelName()!="JS 220")
                                      ){
                                avgfuelusedinlitres="N/A";
                          }

                          //($F{profile}=="Backhoe Loaders " || $F{profile}=="Backhoe") && ($F{fuelUsedInIdle}==0.0)

                          else if((implObj.get(i).getProfile().equalsIgnoreCase("Backhoe Loaders") || implObj.get(i).getProfile().equalsIgnoreCase("Backhoe")) && implObj.get(i).getAverageFuelConsumption()==0.0){

                                avgfuelusedinlitres="N/A";
                          }

                          else{
                                avgfuelusedinlitres=String.valueOf(implObj.get(i).getAverageFuelConsumption());

                          }

                          workSheet.addCell(new jxl.write.Label(12, p + 1,
                                      avgfuelusedinlitres, normalFormat3));

                          workSheet.addCell(new jxl.write.Label(13, p + 1,
                                      implObj.get(i).getDealername(), normalFormat3));



                          p++;
                    }
                    infoLogger.info("fleet summary report excel generated successfully");
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
      
        infoLogger.info("Mailing Fleet summary report: start "+jasperDate);
        String duefilesToBeAttcahed=null;
        File sourceFile = new File(reportsDir+"Fleet_Summary_Report_"+loginID+"_"+jasperDate+".xls");
		infoLogger.info("Fleet summary report sourece File:"+sourceFile);
		SendEmail email = new SendEmail();
		String fileToBeAttcahed = reportsDir+"Fleet_Summary_Report_"+loginID+"_"+jasperDate+".xls";
		infoLogger.info("Fleet summary report loginID:"+loginID);
		
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
		    infoLogger.info("Fleet summary report Primary Email ID:"+receiverMail);
		    FileOutputStream fos;
		   
			try {
				fos = new FileOutputStream(reportsDir+"Fleet_Summary_Report_"+loginID+"_"+jasperDate+".zip");
				duefilesToBeAttcahed = reportsDir+"Fleet_Summary_Report_"+loginID+"_"+jasperDate+".zip";
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
		String emailSubject = "Fleet summary report";
		String emailBody = "Dear Customer \r\n Please find the attached Fleet Summary report.\r\n Thanks, \r\n Deepthi. ";
		String result = email.sendMailWithAttachment(receiverMail, emailSubject, emailBody, duefilesToBeAttcahed);
		
		infoLogger.info("Email Sending status of Fleet Summary report for "+receiverMail+":"+result);
		
		File zipFile = new File(reportsDir+"Fleet_Summary_Report_"+loginID+"_"+jasperDate+".zip");
		zipFile.delete();
		sourceFile.delete();
		//end of sending fleet report as an attachment in email
        }
        else
        {
		for(int i=0;i<implObj.size();i++)
		{
			
			response=new FleetSummaryReportRespContract();
			
			response.setSerialNumber(implObj.get(i).getSerialNumber());
			response.setMachineName(implObj.get(i).getMachineName());
			
			if(implObj.get(i).getAverageFuelConsumption()<0 || implObj.get(i).getAverageFuelConsumption()== -0)
			{
				response.setAverageFuelConsumption(0);
			}
			else
			{
			response.setAverageFuelConsumption(implObj.get(i).getAverageFuelConsumption());
			}
			
			if(implObj.get(i).getFuelused()<0)
			{
				response.setFuelused(0);
			}
			else
			{
			response.setFuelused(implObj.get(i).getFuelused());
			}
			
			if (implObj.get(i).getFuelUsedInIdle()<0)
			{
				response.setFuelUsedInIdle(0);
				
			} 
			else {
				 response.setFuelUsedInIdle(implObj.get(i).getFuelUsedInIdle());
			}
			
			if(implObj.get(i).getPowerBandhigh()<0)
			{
			   response.setPowerBandhigh(0);
			}
			else
			{
			   response.setPowerBandhigh(implObj.get(i).getPowerBandhigh()); 
			}
			if(implObj.get(i).getPowerBandMedium()<0)
			{
				response.setPowerBandMedium(0);
			}
			else
			{
				response.setPowerBandMedium(implObj.get(i).getPowerBandMedium());
			}
				
			if(implObj.get(i).getPowerBandLow()<0)
			{
				response.setPowerBandLow(0);
			}
			else
			{
				response.setPowerBandLow(implObj.get(i).getPowerBandLow());
			}
			
			if (implObj.get(i).getWorkingTime()<0) 
			{
				response.setWorkingTime(0);
			} 
			else 
			{
				response.setWorkingTime(implObj.get(i).getWorkingTime());

			}
			if (implObj.get(i).getIdleTime()<0)
			{
				response.setIdleTime(0);
			} 
			else 
			{
				response.setIdleTime(implObj.get(i).getIdleTime());
			}
			
			if(implObj.get(i).getEngineOff()<0)
			{
				response.setEngineOff(0);
			}
			else 
			{
				response.setEngineOff(implObj.get(i).getEngineOff());
			}
			if(implObj.get(i).getMachineHours()<0)
			{
				response.setMachineHours(0);
			}
			else
			{
				response.setMachineHours(implObj.get(i).getMachineHours());
			}
			
			if(implObj.get(i).getTotalMachineLifeHours()<0)
			{
				response.setTotalMachineLifeHours(0);

			}
			else
			{
				response.setTotalMachineLifeHours(implObj.get(i).getTotalMachineLifeHours());

			}
			
			response.setMachineProfile(implObj.get(i).getMachineProfile());
			response.setProfile(implObj.get(i).getProfile());
			
			
			response.setMachineGroupIdList(implObj.get(i).getMachineGroupIdList());
			response.setMachineGroupName(implObj.get(i).getMachineGroupName());
			
			response.setModelIdList(implObj.get(i).getModelIdList());
			response.setModelName(implObj.get(i).getModelName());
			
			response.setTenanctIdList(implObj.get(i).getTenanctIdList());
			response.setTenancyName(implObj.get(i).getTenancyName());
			response.setDealername(implObj.get(i).getDealername());
			listResponse.add(response);
		}
        }
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
