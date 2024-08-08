//CR290-20220309-Balaji-Download7Day report for BHLBS4 Machine communicating with 050 Msg_id
package remote.wise.businessobject;

import java.io.File;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
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

import remote.wise.businessentity.AssetClassDimensionEntity;
import remote.wise.businessentity.AssetEntity;
import remote.wise.businessentity.ClientEntity;
import remote.wise.dal.DynamicAMH_DAL;
import remote.wise.dal.DynamicTAssetMonData_DAL;
import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.pojo.TAssetMonDataDAO;
import remote.wise.service.implementation.DownloadAempImpl;
import remote.wise.util.DateUtil;
import remote.wise.util.HibernateUtil;
import remote.wise.util.ListToStringConversion;
//import remote.wise.util.WiseLogger;

/**
 * DownloadAempBO will allow to Download details in Aemp Format for given Serial
 * Number and Period
 * 
 * @author jgupta41
 * 
 */

public class DownloadAempBO {
	// DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static
	// logger object all throughout the application
	/*public static WiseLogger businessError = WiseLogger.getLogger(
			"DownloadAempBO:", "businessError");
	public static WiseLogger fatalError = WiseLogger.getLogger(
			"DownloadAempBO:", "fatalError");
	public static WiseLogger infoLogger = WiseLogger.getLogger(
			"DownloadAempBO:", "info");
*/
	// ************************** Download details in Aemp Format for given
	// Serial Number and Period****************************************

	/**
	 * This method Download details in Aemp Format for given Serial Number and
	 * Period
	 * 
	 * @param serialNumber
	 *            :serialNumber
	 * @param Period
	 *            :Period YYYY-MM-DD
	 * @return downloadImpl : Download details in Aemp Format for given Serial
	 *         Number and Period
	 * @throws CustomFault
	 */
	public List<DownloadAempImpl> getDownloadAemp(String serialNumber,
			String Period) throws CustomFault {
		
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		List<DownloadAempImpl> downloadImpl = new LinkedList<DownloadAempImpl>();
		ListToStringConversion conversionObj = new ListToStringConversion();
		try {

			Session session = HibernateUtil.getSessionFactory().openSession();
			
			
			AssetEntity asset=null;
			
			try{
				
				Query assetQ = session.createQuery(" from AssetEntity where serial_number='"+serialNumber+"'");
				Iterator assetItr = assetQ.list().iterator();
				if(assetItr.hasNext())
				{
					asset = (AssetEntity)assetItr.next();
				}
				
			}
			catch(Exception e)
			{
				fLogger.fatal("DownLoadAEMPService::getDownloadAemp::Exception::"+e.getMessage());
			}
			

			try {
				// get Client Details
				Properties prop1 = new Properties();
				String clientName = null;

				prop1.load(getClass()
						.getClassLoader()
						.getResourceAsStream(
								"remote/wise/resource/properties/configuration.properties"));
				clientName = prop1.getProperty("ClientName");

				IndustryBO industryBoObj = new IndustryBO();
				ClientEntity clientEntity = industryBoObj
						.getClientEntity(clientName);
				// END of get Client Details
				// Changes done by Juhi on 16-09-2013 for defect id:1259
				if (!(session.isOpen())) {
					session = HibernateUtil.getSessionFactory().openSession();
					
				}
				iLogger.info("get customername from given serial no.");
				String CustomerName = null;
				Query q = session
						.createQuery("select a.account_name from AccountEntity a, AssetEntity b where a.status=true and b.active_status=true and b.client_id="
								+ clientEntity.getClient_id()
								+ " and b.primary_owner_id=a.account_id and b.serial_number ='"
								+ serialNumber + "'");
				Iterator itr = q.list().iterator();
				Object result4[] = null;
				while (itr.hasNext()) {
					CustomerName = (String) itr.next();
				}
			
				//Rajani Nagaraju - 20140512 - Adding properties for DownloadAEMP
				String downloadAEMP="";
				if (prop1.getProperty("deployenvironment").equalsIgnoreCase("SIT")) {
					downloadAEMP = prop1.getProperty("DownloadAEMP_SIT");
					
				} 
				else if (prop1.getProperty("deployenvironment").equalsIgnoreCase("DEV")) {
					downloadAEMP = prop1.getProperty("DownloadAEMP_DEV");
					
				}
				else if (prop1.getProperty("deployenvironment").equalsIgnoreCase("QA")) {
					downloadAEMP = prop1.getProperty("DownloadAEMP_QA");
					
				} 
				else if (prop1.getProperty("deployenvironment").equalsIgnoreCase("PROD")) {
					downloadAEMP = prop1.getProperty("DownloadAEMP_PROD");
					
				}
				
				
				String FileName = downloadAEMP + serialNumber + ".xls";
				// String FileName="C:/Downloads/"+serialNumber+".xls";

			 String DIR = downloadAEMP;
			//String DIR="C:/Downloads/";

				// delete all existing files
				File file = new File(DIR);
				String[] myFiles;
				if (file.isDirectory()) {
					myFiles = file.list();
					for (int k = 0; k < myFiles.length; k++) {
						File myFile = new File(file, myFiles[k]);
						if (myFile.getName().equalsIgnoreCase(
								serialNumber + ".xls")) {
							iLogger.info("file name getting deleted : "
									+ myFile.getName());
							myFile.delete();
						}

					}
				}

				WorkbookSettings ws = new WorkbookSettings();
				WritableWorkbook workbook = Workbook.createWorkbook(new File(
						FileName), ws);

				WritableSheet workSheet = null;
				workSheet = workbook.createSheet(serialNumber, 0);

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
				iLogger
						.info("get Date,machinehours, Engine On hours,Idle hours, Working Hours , Model for given serial no.");
				double machineHours = 0L;
				double engineOnHours = 0L;
				double workingTime = 0L;
				double idleHours = 0L;
				DecimalFormat df2 = new DecimalFormat("###.##");
				String location = null;
				int assetClassDimensionId = 0;

				int i = 0;
				String result = null;
				String result1 = null;
				List<String> dateList = new LinkedList<String>();
				while (i < 7) {

					try {

						DateFormat dateFormat1 = new SimpleDateFormat(
								"yyyy-MM-dd hh:mm:ss");

						DateFormat dateFormat = new SimpleDateFormat(
								"yyyy-MM-dd");

						Date myDate = dateFormat.parse(Period);

						Calendar calendar = Calendar.getInstance();
						calendar.setTime(myDate);
						calendar.add(Calendar.DAY_OF_YEAR, -(i));

						Date previousDate = calendar.getTime();

						result = dateFormat.format(previousDate);
						result1 = dateFormat1.format(previousDate);
						// workSheet.addCell(new
						// jxl.write.Label(0,i+1,result,normalFormat1));
						iLogger.info("week dates are" + result);

					} catch (ParseException e) {
						fLogger.fatal("Invalid date string");
						e.printStackTrace();
					}

					dateList.add(result);
					i = i + 1;
				}

				iLogger.info("Number of days are" + dateList.size());
				String dateStringList = conversionObj.getStringList(dateList)
						.toString();

				List<Integer> assetClassDimensionIdList = new LinkedList<Integer>();
				String Model = null;
				Object[] res = null;
				/*
				 * Query q2 = session.createQuery(
				 * "select a.machineHours ,a.engineOffHours,a.workingTime,a.idleHours ,d.assetClassDimensionId,a.location "
				 * +
				 * "from AssetMonitoringFactDataDayAgg a JOIN a.tenancyId b JOIN a.assetClassDimensionId d "
				 * +
				 * "where a.serialNumber ='"+serialNumber+"' and a.timeKey in ("
				 * +dateStringList+")");
				 */
				Query q2 = session
						.createQuery("select a.machineHours ,a.engineOffHours,sum(a.EngineRunningBand3+a.EngineRunningBand4+a.EngineRunningBand5+a.EngineRunningBand6+a.EngineRunningBand7+a.EngineRunningBand8),sum(a.EngineRunningBand1+a.EngineRunningBand2) ,d.assetClassDimensionId,a.location,a.timeKey "
								+ "from AssetMonitoringFactDataDayAgg_json a JOIN a.tenancyId b JOIN a.assetClassDimensionId d "
								+ "where a.serialNumber ='"
								+ serialNumber
								+ "' and a.timeKey in ("
								+ dateStringList
								+ ") group by a.timeKey order by a.timeKey  desc");

				List resultList = q2.list();
				Iterator itr2 = resultList.iterator();
				if (resultList.size() > 0) {
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
					normalFormat3.setAlignment(jxl.format.Alignment.CENTRE);			normalFormat3
							.setVerticalAlignment(VerticalAlignment.CENTRE);
					normalFormat3.setWrap(true);
					normalFormat3.setBorder(jxl.format.Border.ALL,
							jxl.format.BorderLineStyle.THIN,
							jxl.format.Colour.BLACK);

					// Keerthi : 22/10/13 : changing column width
					// first two columns justified,rest centered
					workSheet.setColumnView(0, 10);
					workSheet.setColumnView(1, 20);
					workSheet.setColumnView(2, 15);
					workSheet.setColumnView(8, 40);
					workSheet.addCell(new jxl.write.Label(0, 0, " Date",
							normalFormat));
					workSheet.addCell(new jxl.write.Label(1, 0, "PIN",
							normalFormat));
					workSheet.addCell(new jxl.write.Label(2, 0, "Cust. Name",
							normalFormat));
					workSheet.addCell(new jxl.write.Label(3, 0, "Model",
							normalFormat));
					workSheet.addCell(new jxl.write.Label(4, 0, "Cum. HMR",
							normalFormat));
					workSheet.addCell(new jxl.write.Label(5, 0, "Idle Hours",
							normalFormat));
					workSheet.addCell(new jxl.write.Label(6, 0,
							"Engine On Duration", normalFormat));
					workSheet.addCell(new jxl.write.Label(7, 0,
							"Working Hours", normalFormat));
					workSheet.addCell(new jxl.write.Label(8, 0, "Location",
							normalFormat));
					workSheet.addCell(new jxl.write.Label(9, 0, "Low EOP",
							normalFormat));
					workSheet.addCell(new jxl.write.Label(10, 0,
							"HIGH_COOLANT_TEMPERATURE", normalFormat));
					workSheet.addCell(new jxl.write.Label(11, 0,
							"Water in Fuel", normalFormat));
					workSheet.addCell(new jxl.write.Label(12, 0,
							"Blocked Air Filter", normalFormat));
					workSheet.addCell(new jxl.write.Label(13, 0,
							"Trans Oil Temp", normalFormat));
					workSheet.addCell(new jxl.write.Label(14, 0,
							"Coolent Level", normalFormat));

					// workSheet.addCell(new
					// jxl.write.Label(0,1,Period,normalFormat1));
					List<String> dateListInOlap = new LinkedList<String>();

					int p = 0;
					while (itr2.hasNext()) {
						res = (Object[]) itr2.next();
						// Check condition for negative values done by Juhi on
						// 30-September-2013 for defect id:1366
						if ((Double) res[0] > 0) {
							//Integer to Double Conversion DF:20131217
							//machineHours = (Double) res[0];
							machineHours = (Double.valueOf(df2.format((Double)res[0])));
						} else {
							machineHours = 0;
						}
						if ((Double) res[1] > 0 && (Double) res[1] <= 24) {
							engineOnHours = 24 - (Double) res[1];
							engineOnHours = (Double.valueOf(df2.format(Double.valueOf(engineOnHours))));
						} else {
							engineOnHours = 24;
						}
						if ((Double) res[2] > 0) {
							//workingTime = (Double) res[2];
							workingTime = (Double.valueOf(df2.format((Double)res[2])));
						} else {
							workingTime = 0;
						}
						if ((Double) res[3] > 0) {
							//idleHours = (Double) res[3];
							idleHours = (Double.valueOf(df2.format((Double)res[3])));
						} else {
							idleHours = 0;
						}

						location = (String) res[5];
						Timestamp ts = (Timestamp) res[6];
						Date Date = new Date(ts.getTime());

						DateFormat dateFormat = new SimpleDateFormat(
								"yyyy-MM-dd");

						Calendar calendar = Calendar.getInstance();
						calendar.setTime(Date);
						String Date1 = dateFormat.format(Date);
						// Get date present in Olap only .Code Added by Juhi on
						// 25-10-2013
						dateListInOlap.add(Date1);

						String ExcelDate = "" + Date1;
						String machineHours1 = "" + machineHours;
						String engineOnHours1 = "" + engineOnHours;
						String workingTime1 = ""
								+ Math.round(workingTime * 100.0) / 100.0;
						String idleHours1 = "" + idleHours;
						workSheet.addCell(new jxl.write.Label(0, p + 1,
								ExcelDate, normalFormat1));
						workSheet.addCell(new jxl.write.Label(4, p + 1,
								machineHours1, normalFormat3));
						workSheet.addCell(new jxl.write.Label(5, p + 1,
								idleHours1, normalFormat3));
						workSheet.addCell(new jxl.write.Label(7, p + 1,
								workingTime1, normalFormat3));
						workSheet.addCell(new jxl.write.Label(6, p + 1,
								engineOnHours1, normalFormat3));
						workSheet.addCell(new jxl.write.Label(8, p + 1,
								location, normalFormat3));
						assetClassDimensionId = (Integer) res[4];
						assetClassDimensionIdList.add(assetClassDimensionId);

						p++;
					}
					// Get date present in Olap only .Code Added by Juhi on
					// 25-10-2013
					
					String assetClassDimensionIdStringList = conversionObj
							.getIntegerListString(assetClassDimensionIdList)
							.toString();
					int a = 0;
					Query q22 = session
							.createQuery("from AssetClassDimensionEntity where assetClassDimensionId in ("
									+ assetClassDimensionIdStringList + ")");
					Iterator itr22 = q22.list().iterator();
					while (itr22.hasNext()) {
						AssetClassDimensionEntity assetType = (AssetClassDimensionEntity) itr22
								.next();
						Model = assetType.getAssetTypeName();
					}
					for (int n = 0; n < assetClassDimensionIdList.size(); n++) {
						workSheet.addCell(new jxl.write.Label(1, n + 1,
								serialNumber, normalFormat1));
					}

					for (int m = 0; m < assetClassDimensionIdList.size(); m++) {
						workSheet.addCell(new jxl.write.Label(2, m + 1,
								CustomerName, normalFormat3));
					}
					for (int k = 0; k < assetClassDimensionIdList.size(); k++) {
						workSheet.addCell(new jxl.write.Label(3, k + 1, Model,
								normalFormat3));

					}

				
					
					//DF20170102 @Roopa changing AMH and AMD into single TAssetMOnData table and taking parameters from json column TxnData
					
					SimpleDateFormat dateTimeFormat= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					
					//String tAssetMonTable=null;
					int seg_ID=0;
					//List<DownloadAempImpl> downloadAempImplList=new LinkedList<DownloadAempImpl>();
					
					DynamicTAssetMonData_DAL amdDaoObj=new DynamicTAssetMonData_DAL();
					List<TAssetMonDataDAO> amdDaoList=new LinkedList<TAssetMonDataDAO>();
					
					int s = 8;
					int b = 1, b1 = 1, b2 = 1, b3 = 1, b4 = 1, b5 = 1;
					
					String EVT_LEOP="0";
					String EVT_HCT="0";
					String EVT_WIF="0";
					String EVT_BAF="0";
					String EVT_HTOT="0";
					String EVT_LCL="0";
					
					if(asset!=null)
						 seg_ID=asset.getSegmentId();
					
					for(int j=0;j<dateListInOlap.size();j++){
						
						/*String txnTimestamp= dateListInOlap.get(j) + " 00:00:00";
						
						


						Date txnDate = null;
						try {
							txnDate = dateTimeFormat.parse(txnTimestamp);
						} catch (ParseException e) {
							fLogger.fatal("DownloadAempService::GetDownloadAemp::Exception::"+e.getMessage());
						}

						
						tAssetMonTable=new DateUtil().getDynamicTable("DownloadAEMPService::getDownloadAEMPDetails::", txnDate);*/
						
						
						
					
					/*String query="select t.Serial_Number, t.TxnData "
							     + "from "+tAssetMonTable+" t where Transaction_Timestamp="
                                 +"(select max(Transaction_Timestamp) from "+tAssetMonTable+" tmon where tmon.Serial_Number='"+serialNumber+"' and tmon.Segment_ID_TxnDate="+seg_ID+" and Date(Transaction_Timestamp)='"+dateListInOlap.get(j)+"' group by Date(Transaction_Timestamp))"
                                 +"and Serial_Number='"+serialNumber+"' and Segment_ID_TxnDate="+seg_ID+"";
                                
					
					downloadAempImplList=new DynamicAMH_DAL().getAMHAMDListForDownloadAemp(query);
					
					     EVT_LEOP=downloadAempImplList.get(0).getTxnDataMap().get("EVT_LEOP");
						 EVT_HCT=downloadAempImplList.get(0).getTxnDataMap().get("EVT_HCT");
						 EVT_WIF=downloadAempImplList.get(0).getTxnDataMap().get("EVT_WIF");
						 EVT_BAF=downloadAempImplList.get(0).getTxnDataMap().get("EVT_BAF");
						 EVT_HTOT=downloadAempImplList.get(0).getTxnDataMap().get("EVT_HTOT");
						 EVT_LCL=downloadAempImplList.get(0).getTxnDataMap().get("EVT_LCL");
					*
					*/
						
						
						try{
							amdDaoList=amdDaoObj.getPrevTAssetMonData("DownLoad AEMP Service",serialNumber,Timestamp.valueOf(dateListInOlap.get(j)+" 18:30:00"), seg_ID,"EVT_LEOP");

							if(amdDaoList!=null && amdDaoList.size()>0){
								HashMap<String,String> txnDataMap1=amdDaoList.get(0).getTxnData();
								
								 EVT_LEOP=txnDataMap1.get("EVT_LEOP");
							}

						}

						catch(Exception e){
							e.printStackTrace();
						}
						
						try{
							amdDaoList=amdDaoObj.getPrevTAssetMonData("DownLoad AEMP Service",serialNumber,Timestamp.valueOf(dateListInOlap.get(j)+" 18:30:00"), seg_ID,"EVT_HCT");

							if(amdDaoList!=null && amdDaoList.size()>0){
								HashMap<String,String> txnDataMap1=amdDaoList.get(0).getTxnData();
								
								EVT_HCT=txnDataMap1.get("EVT_HCT");
							}

						}

						catch(Exception e){
							e.printStackTrace();
						}
						
						try{
							amdDaoList=amdDaoObj.getPrevTAssetMonData("DownLoad AEMP Service",serialNumber,Timestamp.valueOf(dateListInOlap.get(j)+" 18:30:00"), seg_ID,"EVT_WIF");

							if(amdDaoList!=null && amdDaoList.size()>0){
								HashMap<String,String> txnDataMap1=amdDaoList.get(0).getTxnData();
								
								EVT_WIF=txnDataMap1.get("EVT_WIF");
							}

						}

						catch(Exception e){
							e.printStackTrace();
						}
						
						try{
							amdDaoList=amdDaoObj.getPrevTAssetMonData("DownLoad AEMP Service",serialNumber,Timestamp.valueOf(dateListInOlap.get(j)+" 18:30:00"), seg_ID,"EVT_BAF");

							if(amdDaoList!=null && amdDaoList.size()>0){
								HashMap<String,String> txnDataMap1=amdDaoList.get(0).getTxnData();
								
								EVT_BAF=txnDataMap1.get("EVT_BAF");
							}

						}

						catch(Exception e){
							e.printStackTrace();
						}
						
						try{
							amdDaoList=amdDaoObj.getPrevTAssetMonData("DownLoad AEMP Service",serialNumber,Timestamp.valueOf(dateListInOlap.get(j)+" 18:30:00"), seg_ID,"EVT_HTOT");

							if(amdDaoList!=null && amdDaoList.size()>0){
								HashMap<String,String> txnDataMap1=amdDaoList.get(0).getTxnData();
								
								EVT_HTOT=txnDataMap1.get("EVT_HTOT");
							}

						}

						catch(Exception e){
							e.printStackTrace();
						}
					
						try{
							amdDaoList=amdDaoObj.getPrevTAssetMonData("DownLoad AEMP Service",serialNumber,Timestamp.valueOf(dateListInOlap.get(j)+" 18:30:00"), seg_ID,"EVT_LCL");

							if(amdDaoList!=null && amdDaoList.size()>0){
								HashMap<String,String> txnDataMap1=amdDaoList.get(0).getTxnData();
								
								EVT_LCL=txnDataMap1.get("EVT_LCL");
							}

						}

						catch(Exception e){
							e.printStackTrace();
						}

						s = s + 1;
						
						
						

						workSheet.addCell(new jxl.write.Label(s, b,
									EVT_LEOP, normalFormat3));

							b = b + 1;
						workSheet.addCell(new jxl.write.Label(s + 1, b1,
									EVT_HCT, normalFormat3));
							b1 = b1 + 1;
						workSheet.addCell(new jxl.write.Label(s + 2, b2,
									EVT_WIF, normalFormat3));
							b2 = b2 + 1;
						workSheet.addCell(new jxl.write.Label(s + 3, b3,
									EVT_BAF, normalFormat3));
							b3 = b3 + 1;
						workSheet.addCell(new jxl.write.Label(s + 4, b4,
									EVT_HTOT, normalFormat3));
							b4 = b4 + 1;
						workSheet.addCell(new jxl.write.Label(s + 5, b5,
									EVT_LCL, normalFormat3));
							b5 = b5 + 1;
						

						s = 8;

					
					
					}
					


				} else {
					// Keerthi : 28/10/13 : Empty file for PIN with no data
					iLogger.info("NO DATA FOUND TO DOWNLOAD for "
							+ serialNumber);
					workSheet.setColumnView(0, 15);
					workSheet.addCell(new jxl.write.Label(0, 0,
							"NO DATA FOUND", normalFormat1));
				}
				workbook.write();
				workbook.close();
			} catch (Exception e) {
				e.printStackTrace();
				fLogger
						.fatal("Hello this is an Fatal Error. Need immediate Action"
								+ e.getMessage());

			} finally {
	
				if (session!=null && session.isOpen()) {
					session.close();
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return downloadImpl;
	}

	// **************************End of Download Aemp for given Serial Number
	// and Period****************************************
	
	
	/**
	 * Using TAssetMonDataTables for machines hours instead of
	 * remote_monitoring_fact_data_dayagg_json_new.
	 * DF20180918 KO369761 - Created New Method with above modifications
	 **/
	public List<DownloadAempImpl> getDownloadAemp_New(String serialNumber,
			String Period) throws CustomFault {

		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		List<DownloadAempImpl> downloadImpl = new LinkedList<DownloadAempImpl>();
		try {

			Session session = HibernateUtil.getSessionFactory().openSession();
			AssetEntity asset=null;

			try{

				Query assetQ = session.createQuery(" from AssetEntity where serial_number='"+serialNumber+"'");
				Iterator assetItr = assetQ.list().iterator();
				if(assetItr.hasNext())
				{
					asset = (AssetEntity)assetItr.next();
				}

			}
			catch(Exception e)
			{
				fLogger.fatal("DownLoadAEMPService::getDownloadAemp::Exception::"+e.getMessage());
			}


			try {
				// get Client Details
				Properties prop1 = new Properties();
				String clientName = null;

				prop1.load(getClass()
						.getClassLoader()
						.getResourceAsStream(
								"remote/wise/resource/properties/configuration.properties"));
				clientName = prop1.getProperty("ClientName");

				IndustryBO industryBoObj = new IndustryBO();
				ClientEntity clientEntity = industryBoObj
						.getClientEntity(clientName);
				// END of get Client Details
				// Changes done by Juhi on 16-09-2013 for defect id:1259
				if (!(session.isOpen())) {
					session = HibernateUtil.getSessionFactory().openSession();

				}
				iLogger.info("get customername from given serial no.");
				String CustomerName = null;
				Query q = session
						.createQuery("select a.account_name from AccountEntity a, AssetEntity b where a.status=true and b.active_status=true and b.client_id="
								+ clientEntity.getClient_id()
								+ " and b.primary_owner_id=a.account_id and b.serial_number ='"
								+ serialNumber + "'");
				Iterator itr = q.list().iterator();
				while (itr.hasNext()) {
					CustomerName = (String) itr.next();
				}

				//Rajani Nagaraju - 20140512 - Adding properties for DownloadAEMP
				String downloadAEMP="";				
				if (prop1.getProperty("deployenvironment").equalsIgnoreCase("SIT")) {
					downloadAEMP = prop1.getProperty("DownloadAEMP_SIT");

				} 
				else if (prop1.getProperty("deployenvironment").equalsIgnoreCase("DEV")) {
					downloadAEMP = prop1.getProperty("DownloadAEMP_DEV");

				}
				else if (prop1.getProperty("deployenvironment").equalsIgnoreCase("QA")) {
					downloadAEMP = prop1.getProperty("DownloadAEMP_QA");

				} 
				else if (prop1.getProperty("deployenvironment").equalsIgnoreCase("PROD")) {
					downloadAEMP = prop1.getProperty("DownloadAEMP_PROD");

				}


				String FileName = downloadAEMP + serialNumber + ".xls";
				// String FileName="C:/Downloads/"+serialNumber+".xls";

				String DIR = downloadAEMP;
				 //String DIR="C:/Downloads/";

				// delete all existing files
				File file = new File(DIR);
				String[] myFiles;
				if (file.isDirectory()) {
					myFiles = file.list();
					for (int k = 0; k < myFiles.length; k++) {
						File myFile = new File(file, myFiles[k]);
						if (myFile.getName().equalsIgnoreCase(
								serialNumber + ".xls")) {
							iLogger.info("file name getting deleted : "
									+ myFile.getName());
							myFile.delete();
						}
					}
				}

				WorkbookSettings ws = new WorkbookSettings();
				WritableWorkbook workbook = Workbook.createWorkbook(new File(
						FileName), ws);

				WritableSheet workSheet = null;
				workSheet = workbook.createSheet(serialNumber, 0);
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

				iLogger.info("get Date,machinehours, Engine On hours,Idle hours, Working Hours , Model for given serial no.");

				double machineHours = 0L;
				double engineOnHours = 0L;
				double workingTime = 0L;
				double idleHours = 0L;
				double highIdle = 0L;
				double lowIdle = 0L;
				double loadJobHour = 0L;
				double roadJobHour = 0L;
				double exaStdModHour = 0L;
				double exaPlusModHour = 0L;
				double atchOpHour = 0L;		
				double exaEcoModHour = 0L;
				double EnginePowerband0 = 0L;
				double EnginePowerband1 = 0L;
				double EnginePowerband2 = 0L;
				double EnginePowerband3 = 0L;
				DecimalFormat df2 = new DecimalFormat("###.##");
				String location = null;
				String lat = null;
				String lng = null;
				String assetTypeName = null;

				int i = 1;
				String result = null;
				List<String> dateList = new LinkedList<String>();
				while (i <= 7) {

					try {

						DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

						Date myDate = dateFormat.parse(Period);

						Calendar calendar = Calendar.getInstance();
						calendar.setTime(myDate);
						calendar.add(Calendar.DAY_OF_YEAR, -(i));
						Date previousDate = calendar.getTime();
						result = dateFormat.format(previousDate);

					} catch (ParseException e) {
						fLogger.fatal("Invalid date string");
						e.printStackTrace();
					}

					dateList.add(result);
					i = i + 1;
				}

				iLogger.info("Number of days are" + dateList.size());

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
				normalFormat3.setAlignment(jxl.format.Alignment.CENTRE);			normalFormat3
				.setVerticalAlignment(VerticalAlignment.CENTRE);
				normalFormat3.setWrap(true);
				normalFormat3.setBorder(jxl.format.Border.ALL,
						jxl.format.BorderLineStyle.THIN,
						jxl.format.Colour.BLACK);

				//DF20170102 @Roopa changing AMH and AMD into single TAssetMOnData table and taking parameters from json column TxnData
				int seg_ID=0;
				DynamicTAssetMonData_DAL amdDaoObj=new DynamicTAssetMonData_DAL();
				List<TAssetMonDataDAO> amdDaoList=new LinkedList<TAssetMonDataDAO>();
				List<String> dateListInOlap =dateList;

				String EVT_LEOP="0";
				String EVT_HCT="0";
				String EVT_WIF="0";
				String EVT_BAF="0";
				String EVT_HTOT="0";
				String EVT_LCL="0";

				if(asset!=null){
					seg_ID=asset.getSegmentId();

					if(asset.getProductId() != null && asset.getProductId().getAssetTypeId() !=null)
						assetTypeName = asset.getProductId().getAssetTypeId().getAsset_type_name();
				}

				int p = 1;
				int dataFlag = 0;
				for(int j=0;j<dateListInOlap.size();j++){
					
					machineHours = 0L;
					engineOnHours = 0L;
					workingTime = 0L;
					idleHours = 0L;
					location = null;
					lat = null;
					lng = null;
					HashMap<String, String> txnData = null;

					try{
						amdDaoList=amdDaoObj.getPrevTAssetMonData("DownLoad AEMP Service",serialNumber,Timestamp.valueOf(dateListInOlap.get(j)+" 18:30:00"), seg_ID,"EVT_LEOP");

						if(amdDaoList!=null && amdDaoList.size()>0){
							HashMap<String,String> txnDataMap1=amdDaoList.get(0).getTxnData();

							EVT_LEOP=txnDataMap1.get("EVT_LEOP");
						}
					}
					catch(Exception e){
						e.printStackTrace();
					}

					try{
						amdDaoList=amdDaoObj.getPrevTAssetMonData("DownLoad AEMP Service",serialNumber,Timestamp.valueOf(dateListInOlap.get(j)+" 18:30:00"), seg_ID,"EVT_HCT");

						if(amdDaoList!=null && amdDaoList.size()>0){
							HashMap<String,String> txnDataMap1=amdDaoList.get(0).getTxnData();

							EVT_HCT=txnDataMap1.get("EVT_HCT");
						}
					}
					catch(Exception e){
						e.printStackTrace();
					}

					try{
						amdDaoList=amdDaoObj.getPrevTAssetMonData("DownLoad AEMP Service",serialNumber,Timestamp.valueOf(dateListInOlap.get(j)+" 18:30:00"), seg_ID,"EVT_WIF");

						if(amdDaoList!=null && amdDaoList.size()>0){
							HashMap<String,String> txnDataMap1=amdDaoList.get(0).getTxnData();

							EVT_WIF=txnDataMap1.get("EVT_WIF");
						}
					}
					catch(Exception e){
						e.printStackTrace();
					}

					try{
						amdDaoList=amdDaoObj.getPrevTAssetMonData("DownLoad AEMP Service",serialNumber,Timestamp.valueOf(dateListInOlap.get(j)+" 18:30:00"), seg_ID,"EVT_BAF");

						if(amdDaoList!=null && amdDaoList.size()>0){
							HashMap<String,String> txnDataMap1=amdDaoList.get(0).getTxnData();

							EVT_BAF=txnDataMap1.get("EVT_BAF");
						}
					}
					catch(Exception e){
						e.printStackTrace();
					}

					try{
						amdDaoList=amdDaoObj.getPrevTAssetMonData("DownLoad AEMP Service",serialNumber,Timestamp.valueOf(dateListInOlap.get(j)+" 18:30:00"), seg_ID,"EVT_HTOT");

						if(amdDaoList!=null && amdDaoList.size()>0){
							HashMap<String,String> txnDataMap1=amdDaoList.get(0).getTxnData();

							EVT_HTOT=txnDataMap1.get("EVT_HTOT");
						}
					}
					catch(Exception e){
						e.printStackTrace();
					}

					try{
						amdDaoList=amdDaoObj.getPrevTAssetMonData("DownLoad AEMP Service",serialNumber,Timestamp.valueOf(dateListInOlap.get(j)+" 18:30:00"), seg_ID,"EVT_LCL");

						if(amdDaoList!=null && amdDaoList.size()>0){
							HashMap<String,String> txnDataMap1=amdDaoList.get(0).getTxnData();

							EVT_LCL=txnDataMap1.get("EVT_LCL");
						}
					}
					catch(Exception e){
						e.printStackTrace();
					}
					//DF20190731:Abhishek::Changed logic for CAN machines.
					try{
						txnData = amdDaoObj.getMachinesHoursData("DownLoad AEMP Service", serialNumber, dateListInOlap.get(j), seg_ID, assetTypeName);
						if(txnData != null && txnData.size() > 0){
							machineHours = Double.valueOf(df2.format(Double.parseDouble(txnData.get("CMH"))));
							lat = txnData.get("LAT");
							lng = txnData.get("LONG");
							double ERB1 = 0.0,ERB2=0.0,IEPB=0.0,MEPB=0.0,HEPB=0.0,LEPB=0.0;

							if(lat != null && lng!=null)
								location = lat+","+lng;
							else
								location = "";
							
							//if(txnData.get("MSG_ID").equals("020") && !(assetTypeName.equalsIgnoreCase("VM116"))){
							//Deepthi:MEDefectID:100000174-20220119- The data filling to an excel should be considered based on either Log or Event. 
							//In the earlier case, the Log messages were only considered and hence the code was not going thru the loop. 							
							if((txnData.get("MSG_ID").equals("020") || (txnData.get("MSG_ID").equals("002")) ) && !("VM116".equalsIgnoreCase(assetTypeName))){
								
								if(txnData.get("IEPB") != null)
									IEPB = Double.parseDouble(txnData.get("IEPB"));
								idleHours = (Double.valueOf(df2.format(IEPB)));
								
								if(txnData.get("MEPB") != null)
									MEPB = Double.parseDouble(txnData.get("MEPB"));
								if(txnData.get("HEPB") != null)
									HEPB = Double.parseDouble(txnData.get("HEPB"));
								if(txnData.get("LEPB") != null)
									LEPB = Double.parseDouble(txnData.get("LEPB"));
								
								engineOnHours = IEPB+MEPB+HEPB+LEPB;
								engineOnHours = Double.valueOf(df2.format(engineOnHours));
								
								workingTime = MEPB+HEPB+LEPB;
								workingTime = Double.valueOf(df2.format(workingTime));
							}
							
							//CR290.sn							
							else if(txnData.get("MSG_ID").equals("050") || txnData.get("MSG_ID").equals("008")){
								idleHours = 0.0;
								highIdle = Double.parseDouble(txnData.get("HIGHIDLEHOUR"));
								lowIdle = Double.parseDouble(txnData.get("LOWIDLEHOUR"));
								workingTime = Double.parseDouble(txnData.get("WORKINGHOUR"));
								loadJobHour  = Double.parseDouble(txnData.get("LoadJobHour"));
								roadJobHour  = Double.parseDouble(txnData.get("RoadJobHour"));
								exaStdModHour = Double.parseDouble(txnData.get("ExaStdModHour"));
								exaPlusModHour = Double.parseDouble(txnData.get("ExaPlusModHour"));
								atchOpHour = Double.parseDouble(txnData.get("AtchOpHour"));
								exaEcoModHour = Double.parseDouble(txnData.get("ExaEcoModHour"));
								idleHours = highIdle+lowIdle;
								idleHours = Double.valueOf(df2.format(idleHours));
								workingTime = loadJobHour+roadJobHour+exaEcoModHour+atchOpHour;
								workingTime = Double.valueOf(df2.format(workingTime));
								engineOnHours = idleHours+workingTime;
								engineOnHours = Double.valueOf(df2.format(engineOnHours));														
							}
						    //CR290.en
							
							//DF20210812 JCB4498 Avinash Incorrect nested if loop corrected 
							else if(txnData.get("MSG_ID").equals("040") || txnData.get("MSG_ID").equals("004") ){


								if(txnData.get("PeriodHMR") != null){
									idleHours = 0.0;
									engineOnHours = Double.parseDouble(txnData.get("PeriodHMR"));
									engineOnHours = Double.valueOf(df2.format(engineOnHours));
									workingTime = Double.valueOf(df2.format(engineOnHours));
								}

							// Leela - 	
							}else if(txnData.get("MSG_ID").equals("062")) {
								idleHours = 0.0;
								highIdle = Double.parseDouble(txnData.get("HIGHIDLEHOUR"));
								lowIdle = Double.parseDouble(txnData.get("LOWIDLEHOUR"));
								//workingTime = Double.parseDouble(txnData.get("WORKINGHOUR"));
								loadJobHour  = Double.parseDouble(txnData.get("LoadJobHour"));
								roadJobHour  = Double.parseDouble(txnData.get("RoadJobHour"));
								exaStdModHour = Double.parseDouble(txnData.get("ExaStdModHour"));
								exaPlusModHour = Double.parseDouble(txnData.get("ExaPlusModHour"));
								atchOpHour = Double.parseDouble(txnData.get("AtchOpHour"));
								exaEcoModHour = Double.parseDouble(txnData.get("ExaEcoModHour"));
							    idleHours = highIdle+lowIdle;
								idleHours = Double.valueOf(df2.format(idleHours));
								workingTime = loadJobHour+roadJobHour+exaStdModHour+exaPlusModHour+exaEcoModHour+atchOpHour;
								workingTime = Double.valueOf(df2.format(workingTime));
								engineOnHours = idleHours+workingTime;
								engineOnHours = Double.valueOf(df2.format(engineOnHours));	
							}
							// Leela - 	
							else if(txnData.get("MSG_ID").equals("066")) {
								idleHours = 0.0;
								EnginePowerband0 = Double.parseDouble(txnData.get("EnginePowerbandHr0"));
								EnginePowerband1 = Double.parseDouble(txnData.get("EnginePowerbandHr1"));
								EnginePowerband2 = Double.parseDouble(txnData.get("EnginePowerbandHr2"));
								EnginePowerband3 = Double.parseDouble(txnData.get("EnginePowerbandHr3"));
								
								idleHours = EnginePowerband0;
								idleHours = (Double.valueOf(df2.format(idleHours)));
								workingTime = EnginePowerband1+EnginePowerband2+EnginePowerband3;
								workingTime = Double.valueOf(df2.format(workingTime));
								engineOnHours = idleHours+workingTime;
								engineOnHours = Double.valueOf(df2.format(engineOnHours));	
							}
							else{
									if(txnData.get("ERB1") != null)
									ERB1 = Double.parseDouble(txnData.get("ERB1"));
									if(txnData.get("ERB2") != null)
										ERB2 = Double.parseDouble(txnData.get("ERB2"));
	
									idleHours = (Double.valueOf(df2.format(ERB1)));
	
									engineOnHours = 0.0;
									for(int k = 1;k<=7;k++){
										if(txnData.get("ERB"+k) != null)
											engineOnHours += Double.parseDouble(txnData.get("ERB"+k));
										iLogger.info(" txnData.get(ERB+k) "+txnData.get("ERB"+k)+"engineOnHours :"+engineOnHours);	
									}
									engineOnHours = Double.valueOf(df2.format(engineOnHours));
									workingTime = engineOnHours-idleHours;
									workingTime = Double.valueOf(df2.format(workingTime));
									iLogger.info("idleHours :"+idleHours+"engineOnHours :"+engineOnHours+" workingTime :"+workingTime);
							}
						}
					}catch(Exception e){
						e.printStackTrace();
						fLogger.fatal("Exception caught :"+e.getMessage());
					}

					if(txnData != null && txnData.size() > 0){
						workSheet.addCell(new jxl.write.Label(0, p,
								dateListInOlap.get(j), normalFormat3));
						workSheet.addCell(new jxl.write.Label(1, p,
								serialNumber, normalFormat3));
						workSheet.addCell(new jxl.write.Label(2, p,
								CustomerName, normalFormat3));
						workSheet.addCell(new jxl.write.Label(3, p,
								assetTypeName, normalFormat3));
						workSheet.addCell(new jxl.write.Label(4, p,
								String.valueOf(machineHours), normalFormat3));
						workSheet.addCell(new jxl.write.Label(5, p,
								String.valueOf(idleHours), normalFormat3));
						workSheet.addCell(new jxl.write.Label(6, p,
								String.valueOf(engineOnHours), normalFormat3));
						workSheet.addCell(new jxl.write.Label(7, p,
								String.valueOf(workingTime), normalFormat3));
						workSheet.addCell(new jxl.write.Label(8, p,
								location, normalFormat3));
						workSheet.addCell(new jxl.write.Label(9, p,
								EVT_LEOP, normalFormat3));
						workSheet.addCell(new jxl.write.Label(10, p,
								EVT_HCT, normalFormat3));
						workSheet.addCell(new jxl.write.Label(11, p,
								EVT_WIF, normalFormat3));
						workSheet.addCell(new jxl.write.Label(12, p,
								EVT_BAF, normalFormat3));
						workSheet.addCell(new jxl.write.Label(13, p,
								EVT_HTOT, normalFormat3));
						workSheet.addCell(new jxl.write.Label(14, p,
								EVT_LCL, normalFormat3));
						iLogger.info("dateListInOlap.get(j) :"+dateListInOlap.get(j)+" serialNumber :"+serialNumber+" CustomerName :"+CustomerName+" assetTypeName :"+assetTypeName+" machineHours : "+String.valueOf(machineHours)+" idleHours :"+String.valueOf(idleHours)+" engineOnHours :"+String.valueOf(engineOnHours)+" workingTime : "+String.valueOf(workingTime)+" location :"+location+" EVT_LEOP :"+EVT_LEOP+" EVT_HCT :"+EVT_HCT+" EVT_WIF :"+EVT_WIF+" EVT_BAF :"+EVT_BAF+" EVT_HTOT :"+EVT_HTOT+" EVT_LCL : "+EVT_LCL);
						p++;
						dataFlag = 1;
					}
				}
				
				if(dataFlag == 1){
					// Keerthi : 22/10/13 : changing column width
					// first two columns justified,rest centered
					workSheet.setColumnView(0, 10);
					workSheet.setColumnView(1, 20);
					workSheet.setColumnView(2, 15);
					workSheet.setColumnView(8, 40);
					workSheet.addCell(new jxl.write.Label(0, 0, " Date",
							normalFormat));
					workSheet.addCell(new jxl.write.Label(1, 0, "PIN",
							normalFormat));
					workSheet.addCell(new jxl.write.Label(2, 0, "Cust. Name",
							normalFormat));
					workSheet.addCell(new jxl.write.Label(3, 0, "Model",
							normalFormat));
					workSheet.addCell(new jxl.write.Label(4, 0, "Cum. HMR",
							normalFormat));
					workSheet.addCell(new jxl.write.Label(5, 0, "Idle Hours",
							normalFormat));
					workSheet.addCell(new jxl.write.Label(6, 0,
							"Engine On Duration", normalFormat));
					workSheet.addCell(new jxl.write.Label(7, 0,
							"Working Hours", normalFormat));
					workSheet.addCell(new jxl.write.Label(8, 0, "Location",
							normalFormat));
					workSheet.addCell(new jxl.write.Label(9, 0, "Low EOP",
							normalFormat));
					workSheet.addCell(new jxl.write.Label(10, 0,
							"HIGH_COOLANT_TEMPERATURE", normalFormat));
					workSheet.addCell(new jxl.write.Label(11, 0,
							"Water in Fuel", normalFormat));
					workSheet.addCell(new jxl.write.Label(12, 0,
							"Blocked Air Filter", normalFormat));
					workSheet.addCell(new jxl.write.Label(13, 0,
							"Trans Oil Temp", normalFormat));
					workSheet.addCell(new jxl.write.Label(14, 0,
							"Coolent Level", normalFormat));
				}
				else{
					// Keerthi : 28/10/13 : Empty file for PIN with no data
					iLogger.info("NO DATA FOUND TO DOWNLOAD for "
							+ serialNumber);
					workSheet.setColumnView(0, 15);
					workSheet.addCell(new jxl.write.Label(0, 0,
							"NO DATA FOUND", normalFormat1));
				}

				workbook.write();
				workbook.close();

			} catch (Exception e) {
				e.printStackTrace();
				fLogger
				.fatal("Hello this is an Fatal Error. Need immediate Action"
						+ e.getMessage());

			} finally {

				if (session!=null && session.isOpen()) {
					session.close();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return downloadImpl;
	}
}
