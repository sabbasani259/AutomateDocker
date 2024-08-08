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

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;

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

import remote.wise.businessentity.AccountContactMapping;
import remote.wise.businessentity.AccountEntity;
import remote.wise.businessentity.ContactEntity;
import remote.wise.businessobject.ReportDetailsBO;
import remote.wise.exception.CustomFault;
import remote.wise.handler.SendEmail;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.UtilizationSummaryReportReqContract;
import remote.wise.service.datacontract.UtilizationSummaryReportRespContract;
//import remote.wise.util.WiseLogger;
import remote.wise.util.HibernateUtil;

/** Implementation class that renders the Machine Utilization Summary Report
* @author Rajani Nagaraju
*
*/
public class UtilizationSummaryReportImpl 
{
                
                //DF:2013:12:13 : Conversion from Long to Double for all the required fields : Suprava
                //Defect Id 1337 - Logger changes
                //public static WiseLogger businessError = WiseLogger.getLogger("UtilizationSummaryReportImpl:","businessError");
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
                double engineWorkingDurationInMin;
                double engineRunDurationInMin;
                double engineOffDurationInMin;
                Double machineUtilizationPercentage;
                //DefectI/user/Downloads20150220 @ Suprava-- Dealer As a new parameter Added
                String dealerName;
                private HttpServletRequest request;

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
                * @return the tenancyId
                */
                public int getTenancyId() {
                                return tenancyId;
                }
                /**
                * @param tenancyId the tenancyId to set as Integer input
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
                * @param tenancyName the tenancyName to set as String input
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
                * @param machineGroupId the machineGroupId to set as Integer input
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
                * @param machineGroupName the machineGroupName to set as String input
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
                * @param machineProfileId the machineProfileId to set as Integer input
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
                * @param machineProfileName the machineProfileName to set as String input
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
                * @param modelId the modelId to set as Integer input
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
                * @param modelName the modelName to set  as String input
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
                * @param machineName the machineName to set as String input
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
                * @param serialNumber the serialNumber to set as String input
                */
                public void setSerialNumber(String serialNumber) {
                                this.serialNumber = serialNumber;
                }
                
                
                /**
                * @return the engineWorkingDurationInMin
                */
                public double getEngineWorkingDurationInMin() {
                                return engineWorkingDurationInMin;
                }
                /**
                * @param engineWorkingDurationInMin the engineWorkingDurationInMin to set as Long
                */
                public void setEngineWorkingDurationInMin(double engineWorkingDurationInMin) {
                                this.engineWorkingDurationInMin = engineWorkingDurationInMin;
                }
                
                
                /**
                * @return the engineRunDurationInMin
                */
                public Double getEngineRunDurationInMin() {
                                return engineRunDurationInMin;
                }
                /**
                * @param engineRunDurationInMin the engineRunDurationInMin to set as Long
                */
                public void setEngineRunDurationInMin(Double engineRunDurationInMin) {
                                this.engineRunDurationInMin = engineRunDurationInMin;
                }
                
                
                /**
                * @return the engineOffDurationInMin
                */
                public double getEngineOffDurationInMin() {
                                return engineOffDurationInMin;
                }
                /**
                * @param engineOffDurationInMin the engineOffDurationInMin to set as Long
                */
                public void setEngineOffDurationInMin(double engineOffDurationInMin) {
                                this.engineOffDurationInMin = engineOffDurationInMin;
                }
                
                
                /**
                * @return the machineUtilizationPercentage
                */
                public Double getMachineUtilizationPercentage() {
                                return machineUtilizationPercentage;
                }
                /**
                * @param machineUtilizationPercentage the machineUtilizationPercentage to set as double
                */
                public void setMachineUtilizationPercentage(Double machineUtilizationPercentage) {
                                this.machineUtilizationPercentage = machineUtilizationPercentage;
                }
                
                
                
                /** This method returns the Machine Utilization summary for the user accessible list of serial Numbers
                * @param reqobj LoginId, period and other filters are specified through this reqObj
                * @return Returns the utilization summary details for the user accessible list of serial Numbers and for the given filter criteria
                * @throws CustomFault
                */
                public List<UtilizationSummaryReportRespContract> getMachineUtilizationSummary (UtilizationSummaryReportReqContract reqobj) throws CustomFault
                {
                                List<UtilizationSummaryReportRespContract> response = new LinkedList<UtilizationSummaryReportRespContract>();
                                
                                //Logger businessError = Logger.getLogger("businessErrorLogger");
                                ReportDetailsBO reportsBO = new ReportDetailsBO();
                                Logger infoLogger = InfoLoggerClass.logger;
                                                Logger fatalError = FatalLoggerClass.logger;
                                                Logger businessError = BusinessErrorLoggerClass.logger;
                                try
                                {
                                                //Custom Dates (fromDate,toDate) added by Juhi on 14-August-2013 
                                                if(((reqobj.getPeriod()==null))&&((reqobj.getFromDate()==null) &&(reqobj.getToDate()==null)))
                                                {
                                                                
                                                                businessError.error("Please pass either Period or custom dates");
                                                                throw new CustomFault("Either Period or custom dates are not specified");
                                                }
                                                
                                                if(reqobj.getTenancyIdList()==null)
                                                {
                                                                throw new CustomFault("Tenancy Id List should be specified");
                                                }
                                                
                                                
                                                if(reqobj.getPeriod()!=null)
            {
                  if( !(reqobj.getPeriod().equalsIgnoreCase("Week") || reqobj.getPeriod().equalsIgnoreCase("Month") ||
                                                  reqobj.getPeriod().equalsIgnoreCase("Quarter") || reqobj.getPeriod().equalsIgnoreCase("Year") ||
                                                  reqobj.getPeriod().equalsIgnoreCase("Last Week") ||      reqobj.getPeriod().equalsIgnoreCase("Last Month") ||
                              reqobj.getPeriod().equalsIgnoreCase("Last Quarter") || reqobj.getPeriod().equalsIgnoreCase("Last Year") ) )
                  {
                        throw new CustomFault("Invalid Time Period");
                        
                  }
      }
                                }
                                
                                catch(CustomFault e)
                                {
                                                businessError.error("Custom Fault: "+ e.getFaultInfo());
                                }
                                
                                
                                List<UtilizationSummaryReportImpl> responseList = new LinkedList<UtilizationSummaryReportImpl>();
                                //Custom Dates (fromDate,toDate) added by Juhi on 14-August-2013 
                                infoLogger.info("stmt1-------");
                                responseList = reportsBO.machineUtilizationSummaryReport(reqobj.getFromDate(),reqobj.getToDate(),reqobj.getLoginId(), reqobj.getPeriod(), reqobj.getTenancyIdList(), 
                                                                reqobj.getMachineGroupIdList(),/*reqobj.getMachineProfileIdList,()*/reqobj.getAssetGroupIdList(),reqobj.getModelIdList(),reqobj.isGroupingOnAssetGroup(),reqobj.getLoginTenancyIdList());
                                
          
                                infoLogger.info("stmt8-------");
                                
                                String loginID = reqobj.getLoginId();
                                
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
                        		//CR469:20408644:Sai Divya  roleName Changed from JCB Admin to JCB Account 
                                if((userRole!=null) && (userRole.equalsIgnoreCase("JCB Account") || userRole.equalsIgnoreCase("Customer Care") || userRole.equalsIgnoreCase("JCB HO"))){
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

                                
                                                File tempFile = new File(reportsDir+"Utilization_Summary_Report_"+loginID+"_"+jasperDate+".xls");
                     String FileName = tempFile.getAbsolutePath();

                                                WorkbookSettings ws = new WorkbookSettings();
                                                WritableWorkbook workbook = Workbook.createWorkbook(new File(
                                                                                FileName), ws);

                                                WritableSheet workSheet = null;
                                                workSheet = workbook.createSheet("Utilization_Summary_Report", 0);

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

                                                if(responseList!=null && responseList.size()>0){

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
                                                                workSheet.addCell(new jxl.write.Label(1, 0, "Engine Run Duration",
                                                                                                normalFormat));
                                                                workSheet.addCell(new jxl.write.Label(2, 0, "Engine Off Duration",
                                                                                                normalFormat));
                                                                workSheet.addCell(new jxl.write.Label(3, 0,"Machine Utilization (%)", normalFormat));
                                                                workSheet.addCell(new jxl.write.Label(4, 0,"Dealer Name", normalFormat));



                                                                int p = 0;
                                                                for(int i=0;i<responseList.size();i++){


                                                                                workSheet.addCell(new jxl.write.Label(0, p + 1,
                                                                                                                responseList.get(i).getSerialNumber(), normalFormat1));
                                                                                
                                                                                //Added by Roopa @DF12022015 for converting hours to mins change
                                                                                
                                                                                double tempengineRunDuration= responseList.get(i).getEngineRunDurationInMin()*60;
                                                                                
                                                                                double tempengineOffDuration= responseList.get(i).getEngineOffDurationInMin()*60;
                                                                                

                                                                                long V_R_D_1=(long)((tempengineRunDuration)/(24*60));

                                                                                long V_R_D_2=(long)(((tempengineRunDuration)%(24*60))/60);

                                                                                long temp= (long)(((tempengineRunDuration)%(24*60))%60);

                                                                                String onduration;
                                                                                if(V_R_D_1==0 && V_R_D_2==0 && temp==0){

                                                                                                onduration="0";
                                                                                }

                                                                                else{
                                                                                                String temp1;

                                                                                                String temp2;

                                                                                                String temp3;

                                                                                                if(V_R_D_1==0){
                                                                                                                temp1="";
                                                                                                }
                                                                                                else{
                                                                                                                temp1=V_R_D_1+"d";

                                                                                                }

                                                                                                if(V_R_D_2==0){
                                                                                                                temp2="";
                                                                                                }

                                                                                                else{
                                                                                                                temp2=V_R_D_2+"h";
                                                                                                }

                                                                                                if(temp==0){
                                                                                                                temp3="";           
                                                                                                }
                                                                                                else{
                                                                                                                temp3=temp+"m";
                                                                                                }

                                                                                                onduration=temp1+temp2+temp3;

                                                                                }

                                                                                workSheet.addCell(new jxl.write.Label(1, p + 1,
                                                                                                                onduration, normalFormat3));

                                                                                long V_O_D_1=(long)((tempengineOffDuration)/(24*60));

                                                                                long V_O_D_2=(long)(((tempengineOffDuration)%(24*60))/60);

                                                                                long temp_off= (long)(((tempengineOffDuration)%(24*60))%60);

                                                                                String offduration;
                                                                                if(V_O_D_1==0 && V_O_D_2==0 && temp_off==0){

                                                                                                offduration="0";
                                                                                }

                                                                                else{

                                                                                                String temp1;

                                                                                                String temp2;

                                                                                                String temp3;

                                                                                                if(V_O_D_1==0){
                                                                                                                temp1="";
                                                                                                }
                                                                                                else{
                                                                                                                temp1=V_O_D_1+"d";

                                                                                                }


                                                                                                if(V_O_D_2==0){
                                                                                                                temp2="";
                                                                                                }

                                                                                                else{
                                                                                                                temp2=V_O_D_2+"h";
                                                                                                }

                                                                                                if(temp_off==0){
                                                                                                                temp3="";           
                                                                                                }
                                                                                                else{
                                                                                                                temp3=temp+"m";
                                                                                                }

                                                                                                offduration=temp1+temp2+temp3;

                                                                                }

                                                                                workSheet.addCell(new jxl.write.Label(2, p + 1,
                                                                                                                offduration, normalFormat3));

                                                                                workSheet.addCell(new jxl.write.Label(3, p + 1,
                                                                                                                String.valueOf(responseList.get(i).getMachineUtilizationPercentage()), normalFormat3));

                                                                                workSheet.addCell(new jxl.write.Label(4, p + 1,
                                                                                                                responseList.get(i).getDealerName(), normalFormat3));


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
                                infoLogger.info("stmt9-------");
                                
                               // File sourceFile = new File("D:/" + "Utilization_Summary_Report_"+loginID+"_"+jasperDate+".xls");
                                File sourceFile = new File(reportsDir+"Utilization_Summary_Report_"+loginID+"_"+jasperDate+".xls");
                                infoLogger.info("Utilization summary report sourece File:"+sourceFile);
                                SendEmail email = new SendEmail();
                                String fileToBeAttcahed =reportsDir+"Utilization_Summary_Report_"+loginID+"_"+jasperDate+".xls";
                                String loginTenancyID = reqobj.getLoginId();
                                infoLogger.info("Utilization summary report loginID-"+loginTenancyID);
                                String emailIDQuery = "select c.primary_email_id from ContactEntity c" +
                                                                " where c.contact_id = '"+loginTenancyID+"'";
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
                                    }
                                    infoLogger.info("Utilization summary report Primary Email Id-"+ReceiverMail);
                                    FileOutputStream fos;
                                    String duefilesToBeAttcahed = null;
                         		   
                        			try {
                        				 infoLogger.info("stmt10-------");
                        				fos = new FileOutputStream(reportsDir+"Utilization_Summary_Report_"+loginID+"_"+jasperDate+".zip");
                        				duefilesToBeAttcahed =reportsDir+"Utilization_Summary_Report_"+loginID+"_"+jasperDate+".zip";
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
                        			infoLogger.info("stmt11-------");   
                                String emailTo = "soorneedi.babu@wipro.com;deepthi.rao@wipro.com";
                                String emailSubject = "Utilization summary report";
                                String emailBody = "Dear Customer \r\n Please find the attcheched utilization report.\r\n Thanks, \r\n Deepthi. ";
                                email.sendMailWithAttachment(ReceiverMail, emailSubject, emailBody, duefilesToBeAttcahed);
                                File zipFile = new File(reportsDir+"Utilization_Summary_Report_"+loginID+"_"+jasperDate+".zip");
                                zipFile.delete();
                                sourceFile.delete();
                                
//end
                                
                                }
                                else{
                                	infoLogger.info("stmt12-------");
                                for(int i=0; i<responseList.size(); i++)
                                {
                                                UtilizationSummaryReportRespContract resp = new UtilizationSummaryReportRespContract();
                                                resp.setTenancyId(responseList.get(i).getTenancyId());
                                                resp.setTenancyName(responseList.get(i).getTenancyName());
                                                resp.setMachineGroupId(responseList.get(i).getMachineGroupId());
                                                resp.setMachineGroupName(responseList.get(i).getMachineGroupName());
                                                resp.setMachineProfileId(responseList.get(i).getMachineProfileId());
                                                resp.setMachineProfileName(responseList.get(i).getMachineProfileName());
                                                resp.setModelId(responseList.get(i).getModelId());
                                                resp.setModelName(responseList.get(i).getModelName());
                                                resp.setMachineName(responseList.get(i).getMachineName());
                                                resp.setSerialNumber(responseList.get(i).getSerialNumber());
                                                //DefectI/user/Downloads20150216 @ Suprava Dealer As a new parameter Added
                                                resp.setDealerName(responseList.get(i).getDealerName());
                                                //Changes Done by Juhi On 6 May 2013
                                                if(responseList.get(i).getEngineWorkingDurationInMin()<0)
                                                {
                                                                resp.setEngineWorkingDurationInMin(new Long(0));
                                                }
                                                else
                                                {
                                                                resp.setEngineWorkingDurationInMin(responseList.get(i).getEngineWorkingDurationInMin());
                                                }
                                                if(responseList.get(i).getEngineRunDurationInMin()<0)
                                                {
                                                                resp.setEngineRunDurationInMin(0.0);
                                                }
                                                else
                                                {
                                                                resp.setEngineRunDurationInMin(responseList.get(i).getEngineRunDurationInMin());
                                                }
                                                if(responseList.get(i).getEngineOffDurationInMin()<0)
                                                {
                                                                resp.setEngineOffDurationInMin(new Long(0));
                                                }else
                                                {
                                                                resp.setEngineOffDurationInMin(responseList.get(i).getEngineOffDurationInMin());
                                                }
                                                if(responseList.get(i).getMachineUtilizationPercentage()<0 || responseList.get(i).getMachineUtilizationPercentage()>100)
                                                {
                                                                resp.setMachineUtilizationPercentage(0.0);
                                                }
                                                else
                                                {
                                                                resp.setMachineUtilizationPercentage(responseList.get(i).getMachineUtilizationPercentage());
                                                }
                                                
                                                //Changes Done by Juhi On 6 May 2013
                                                response.add(resp);
                                }
                                }
                                infoLogger.info("stmt13-------"+response.size());
                                return response;
                }
                public HttpServletRequest getRequest() {
                                return request;
                }
                public void setRequest(HttpServletRequest request) {
                                this.request = request;
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
