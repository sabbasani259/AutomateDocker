package remote.wise.service.implementation;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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
import remote.wise.service.datacontract.MachineActivityReportRespContract;
import remote.wise.service.datacontract.MachineServiceDueOverDueReqContract;
import remote.wise.service.datacontract.MachineServiceDueOverDueRespContract;
//import remote.wise.util.WiseLogger;
import remote.wise.util.HibernateUtil;

public class MachineServiceDueOverDueImpl {
	
	private String dealerTenancyId;
	public String getDealerTenancyId() {
		return dealerTenancyId;
	}


	public void setDealerTenancyId(String dealerTenancyId) {
		this.dealerTenancyId = dealerTenancyId;
	}


	public String getDealerName() {
		return dealerName;
	}


	public void setDealerName(String dealerName) {
		this.dealerName = dealerName;
	}


	public long getMachineCountDueForService() {
		return machineCountDueForService;
	}


	public void setMachineCountDueForService(long machineCountDueForService) {
		this.machineCountDueForService = machineCountDueForService;
	}


	public long getMachineCountOverdueForService() {
		return machineCountOverdueForService;
	}


	public void setMachineCountOverdueForService(long machineCountOverdueForService) {
		this.machineCountOverdueForService = machineCountOverdueForService;
	}


	private String dealerName;
	private long machineCountDueForService;
	private long machineCountOverdueForService;
	
	
	//public static WiseLogger businessError = WiseLogger.getLogger("MachineServiceDueOverDueImpl:","businessError");

	
	HashMap<String, HashMap<Integer, Long>> dealerMachineCountData=null;
	

	/**
	 * @return the dealerMachineCountData
	 */
	public HashMap<String, HashMap<Integer, Long>> getDealerMachineCountData() {
		return dealerMachineCountData;
	}


	/**
	 * @param dealerMachineCountData the dealerMachineCountData to set
	 */
	public void setDealerMachineCountData(
			HashMap<String, HashMap<Integer, Long>> dealerMachineCountData) {
		this.dealerMachineCountData = dealerMachineCountData;
	}


	/**
	 * method to get machine service due/over due count for all tenancies under logged-in-tenancy-id.
	 * @param reqObj
	 * @return List<MachineServiceDueOverDueRespContract>
	 * @throws CustomFault
	 */
	public List<MachineServiceDueOverDueRespContract> getMachineServiceDueOverDueCount(MachineServiceDueOverDueReqContract reqObj)
	{//Re-Imeplemented whole Logic by Juhi on 27-09-2013 for Defect Id:1372
	//	Logger businessError = Logger.getLogger("businessErrorLogger");
	//	Logger bLogger = BusinessErrorLoggerClass.logger;
		
		 Logger infoLogger = InfoLoggerClass.logger;
			Logger fatalError = FatalLoggerClass.logger;
			Logger bLogger = BusinessErrorLoggerClass.logger;
		
		try
		{
			if(reqObj.getLoggedInTenancyId()==null || reqObj.getLoggedInTenancyId().equals(""))
			{
	    		throw new CustomFault("Logged-in tenancy ID is not provided!!");
	    	}	
		}
		
		catch(CustomFault e)
		{
			bLogger.error("Custom Fault: "+ e.getFaultInfo());
		}
		
		ReportDetailsBO detailsBO = new ReportDetailsBO();
		
		MachineServiceDueOverDueRespContract resp= null;
		List<MachineServiceDueOverDueRespContract> respList= new ArrayList<MachineServiceDueOverDueRespContract>();
		
		List<MachineServiceDueOverDueImpl> implObj = new LinkedList<MachineServiceDueOverDueImpl>();
		//added by smitha on oct 14th 2013[grouping in summary report Internal Defect 20131014 request contract change]
		implObj= detailsBO.getMachineServiceDueOverDueCount(reqObj.getLoggedInTenancyId(),reqObj.getMachineGroupIdList(),reqObj.getMachineProfileIdList(),reqObj.getModelIdList());
		MachineServiceDueOverDueRespContract  respContractObj=null;
		
		String loginId= reqObj.getPeriod();
		ContactEntity contact;String userRole = null;
		DomainServiceImpl domainService = new DomainServiceImpl();
		contact = domainService.getContactDetails(loginId);
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
		
		//String period= "deve0011";
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

		//String FileName="C:/Service_Machine_Due_Overdue_SummaryReport"+period+".xls";
		 File tempFile = new File(reportsDir+ "Service_Machine_Due_Overdue_SummaryReport_"+loginId+"_"+jasperDate+".xls");
   	     String FileName = tempFile.getAbsolutePath();
		

			WorkbookSettings ws = new WorkbookSettings();
			WritableWorkbook workbook = Workbook.createWorkbook(new File(
					FileName), ws);

			WritableSheet workSheet = null;
			workSheet = workbook.createSheet("Service_Machine_Due_Overdue_SummaryReport", 0);

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

			if(implObj!=null && implObj.size()>0){

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
				workSheet.addCell(new jxl.write.Label(0, 0, "Dealer Name",
						normalFormat));
				workSheet.addCell(new jxl.write.Label(1, 0, "No.of machines due for",
						normalFormat));
				workSheet.addCell(new jxl.write.Label(2, 0, "No.of machine under service overdue",
						normalFormat));

				int p = 0;
				for(int i=0;i<implObj.size();i++){
					workSheet.addCell(new jxl.write.Label(0, p + 1,
							implObj.get(i).getDealerName(), normalFormat1));

					workSheet.addCell(new jxl.write.Label(1, p + 1,
							String.valueOf(implObj.get(i).getMachineCountDueForService()), normalFormat3));

					workSheet.addCell(new jxl.write.Label(2, p + 1,
							String.valueOf(implObj.get(i).getMachineCountOverdueForService()), normalFormat3));


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
		
		}
		else{
		
		
		for(int i=0;i<implObj.size();i++)
		{
			respContractObj=new MachineServiceDueOverDueRespContract();
			respContractObj.setDealerName(implObj.get(i).getDealerName());
			respContractObj.setMachineCountDueForService(implObj.get(i).getMachineCountDueForService());
			respContractObj.setMachineCountOverdueForService(implObj.get(i).getMachineCountOverdueForService());
			respList.add(respContractObj);
		}
		
		//HashMap<String, HashMap<Integer, Long>> dealerMachineCountDataMap =implObj.getDealerMachineCountData();
		//HashMap<Integer, Long> notificationMap = null;
		
		
		//if(dealerMachineCountDataMap!=null)
		/*{
			for(String dealerName : dealerMachineCountDataMap.keySet())
			{
				resp = new MachineServiceDueOverDueRespContract();
			//	System.out.println("dealerName   "+dealerName);
				resp.setDealerName(dealerName);
				notificationMap = dealerMachineCountDataMap.get(dealerName);
				//--------------------
				Iterator it = notificationMap.entrySet().iterator();
				
				while (it.hasNext()) {
	
				Map.Entry entry = (Map.Entry) it.next();
	
				Integer key = (Integer)entry.getKey();
	
				long val = (Long)entry.getValue();
	
			//	System.out.println("key,val: " + key + "," + val);
	
				}
				//--------------------
				if(notificationMap!=null)
				{
					if(notificationMap.containsKey(1))
					{
						resp.setMachineCountDueForService(resp.getMachineCountDueForService()+notificationMap.get(1));
					}
					
					if(notificationMap.containsKey(2))
					{
						resp.setMachineCountDueForService(resp.getMachineCountDueForService()+notificationMap.get(2));
					}
					
					if(notificationMap.containsKey(3))
					{
						resp.setMachineCountOverdueForService(notificationMap.get(3));
					}
					
				}
				
				respList.add(resp);
			}
		}*/
			
		}
		
		return respList;
	}

}
