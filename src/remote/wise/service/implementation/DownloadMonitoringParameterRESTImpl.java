package remote.wise.service.implementation;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

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

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.HibernateUtil;

public class DownloadMonitoringParameterRESTImpl {

	@SuppressWarnings("rawtypes")
	public String getMachineParameterDetails() {
		String finalResult = "SUCCESS";
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();

		try{
			Properties prop = new Properties();
			prop.load(getClass()
					.getClassLoader()
					.getResourceAsStream(
							"remote/wise/resource/properties/configuration.properties"));
			String downloadMPDetails="";

			if (prop.getProperty("deployenvironment").equalsIgnoreCase("SIT")) {
				downloadMPDetails = prop.getProperty("DownloadMPDetails_SIT");
			} 
			else if (prop.getProperty("deployenvironment").equalsIgnoreCase("DEV")) {
				downloadMPDetails = prop.getProperty("DownloadMPDetails_DEV");
			}
			else if (prop.getProperty("deployenvironment").equalsIgnoreCase("QA")) {
				downloadMPDetails = prop.getProperty("DownloadMPDetails_QA");
			} 
			else if (prop.getProperty("deployenvironment").equalsIgnoreCase("PROD")) {
				downloadMPDetails = prop.getProperty("DownloadMPDetails_PROD");
			}

			int  dtc_code = 0;
			String paramName, error_Code = null;
			String dtc_code1  = null;

			String FileName = downloadMPDetails+ "Monitoring_Data" + ".xls";
			String DIR = downloadMPDetails;

			// Delete previous existing files
			File file = new File(DIR);
			String[] myFiles;
			if (file.isDirectory()) {
				myFiles = file.list();
				for (int k = 0; k < myFiles.length; k++) {
					File myFile = new File(file, myFiles[k]);
					if (myFile.getName().equalsIgnoreCase(
							"Monitoring_Data" + ".xls")) {
						iLogger.info("file name getting deleted : "
								+ myFile.getName());
						myFile.delete();
					}
				}
			}
			session.beginTransaction();
			Query query = session.createQuery("select dtcCode, parameterName, errorCode from MonitoringParameters");
			List resultList = query.list();
			Iterator itr = resultList.iterator();
			Object [] result = null;
			WorkbookSettings ws = new WorkbookSettings();
			WritableWorkbook workbook = Workbook.createWorkbook(new File(
					FileName), ws);
			WritableSheet workSheet = null;
			workSheet = workbook.createSheet("Fleet Monitoring Parameters", 0);

			WritableFont normalFont = new WritableFont(
					WritableFont.createFont("Calibri"),
					WritableFont.DEFAULT_POINT_SIZE, WritableFont.BOLD,
					false, UnderlineStyle.NO_UNDERLINE);
			WritableCellFormat normalFormat1 = new WritableCellFormat(
					normalFont);
			normalFormat1.setAlignment(jxl.format.Alignment.CENTRE);
			normalFormat1.setVerticalAlignment(VerticalAlignment.CENTRE);
			normalFormat1.setBorder(jxl.format.Border.ALL,
					jxl.format.BorderLineStyle.THIN,
					jxl.format.Colour.BLACK);
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

				//Setting column Size for Parameter_Name and Parameter_Key columns.
				workSheet.setColumnView(1, 60);
				workSheet.setColumnView(2, 25);
				workSheet.addCell(new jxl.write.Label(0, 0, "DTC_code",
						normalFormat));
				workSheet.addCell(new jxl.write.Label(1, 0, "Parameter_Name",
						normalFormat));
				workSheet.addCell(new jxl.write.Label(2, 0, "Error_Code",
						normalFormat));
				int p = 0;
				while(itr.hasNext()){
					result = (Object[]) itr.next();
					if((Integer)result[0]>0){
						dtc_code = (Integer) result[0];
					}else{
						dtc_code = 0;
					}
					if((String)result[1] != null){
						paramName = (String) result[1];
					}else{
						paramName = "NULL";
					}
					if((String)result[2] != null){
						error_Code = (String) result[2];
					}else{
						error_Code = "NULL";
					}
					dtc_code1 = ""+dtc_code;
					workSheet.addCell(new jxl.write.Label(0, p + 1,
							dtc_code1, normalFormat1));
					workSheet.addCell(new jxl.write.Label(1, p + 1,
							paramName, normalFormat1));
					workSheet.addCell(new jxl.write.Label(2, p + 1,
							error_Code, normalFormat1));
					p++;
				}
			}else{
				iLogger.info("SORRY NO DATA FOUND TO DOWNLOAD");
				workSheet.setColumnView(0, 25);
				workSheet.addCell(new jxl.write.Label(0, 0,
						"SORRY NO DATA FOUND TO DOWNLOAD", normalFormat1));
			}
			workbook.write();
			workbook.close();
		}catch (Exception e) {
			fLogger.fatal("Hello this is an Fatal Error. Need immediate Action: "
					+ e.getMessage());
			finalResult = "FAILURE";
		} finally {
			if (session!=null && session.isOpen()) {
				session.close();
			}
		}
		return finalResult;
	}
}
