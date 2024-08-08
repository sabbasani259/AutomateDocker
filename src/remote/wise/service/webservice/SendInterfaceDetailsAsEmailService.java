package remote.wise.service.webservice;

/**
 * DF20190305: Added SendInterfaceDetailsAsEmail Rest Service to send interface details to users on daily bases, using shell scripts
 * @author Z1007653
 */

import java.nio.file.Files;
import java.nio.file.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.io.File;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.UnderlineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.apache.logging.log4j.Logger;

import remote.wise.handler.SendEmail;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.InterfaceDetailsPOJOForEmail;
import remote.wise.util.ConnectMySQL;

@Path("/SendInterfaceDetailsAsEmailService")
public class SendInterfaceDetailsAsEmailService {
	private List <InterfaceDetailsPOJOForEmail> dateResponseNewList=new ArrayList<InterfaceDetailsPOJOForEmail>();
	@GET
	public String sendEmails() {

		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;

		List<HashMap<String, String>> respObj = getListOfFailedInterface();
		String status = null;
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String today = sdf.format(cal.getTime());
		
		System.out.println("dateResponseNewList size"+dateResponseNewList.size());
		try{
			System.out.println("Came inside Writing Rejec record");
			
			//File tempFile = new File("C:\\Users\\Z1007653\\Desktop\\FileMove\\current\\InterfaceDetails_"+today+".xls"); //For Windows
			File tempFile = new File("/user/JCBLiveLink/Data/Daily_Interface_Data/current_data/InterfaceDetails_"+today+".xls"); //For Unix

			WorkbookSettings ws = new WorkbookSettings();
			WritableWorkbook workbook = Workbook.createWorkbook(tempFile, ws);

			WritableSheet workSheet = null;
			workSheet = workbook.createSheet("InterfaceFailures_" + today, 0);

			WritableFont normalFont = new WritableFont(
					WritableFont.createFont("Calibri"),
					WritableFont.DEFAULT_POINT_SIZE, WritableFont.BOLD, false,
					UnderlineStyle.NO_UNDERLINE);
			WritableCellFormat normalFormat1 = new WritableCellFormat(
					normalFont);

			normalFormat1.setAlignment(jxl.format.Alignment.JUSTIFY);
			normalFormat1.setVerticalAlignment(VerticalAlignment.CENTRE);

			normalFormat1.setBorder(jxl.format.Border.ALL,
					jxl.format.BorderLineStyle.THIN, jxl.format.Colour.BLACK);

			WritableCellFormat normalFormat = new WritableCellFormat(normalFont);
			normalFormat.setAlignment(jxl.format.Alignment.CENTRE);
			normalFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
			normalFormat.setBackground(jxl.format.Colour.GRAY_25);
			normalFormat.setWrap(true);
			normalFormat.setBorder(jxl.format.Border.ALL,
					jxl.format.BorderLineStyle.THIN, jxl.format.Colour.BLACK);
			
			if (!respObj.isEmpty() && respObj != null) {
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
            
			workSheet.addCell(new jxl.write.Label(0, 0, "Interface File", normalFormat));
			workSheet.addCell(new jxl.write.Label(1, 0, "Message String", normalFormat));
			workSheet.addCell(new jxl.write.Label(2, 0, "Failure Cause", normalFormat));
            
			int p = 0;
				for (int i = 0; i < respObj.size(); i++) {

					workSheet.addCell(new jxl.write.Label(0, p + 1, respObj
							.get(i).get("FileName"), normalFormat1));
					workSheet.addCell(new jxl.write.Label(1, p + 1, respObj
							.get(i).get("MessageString"), normalFormat3));
					workSheet.addCell(new jxl.write.Label(2, p + 1, respObj
							.get(i).get("FailureCause"), normalFormat3));
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
		} catch (Exception e) {
            e.printStackTrace();
      }
		
		 	try{
		 	String fileToBeAttached = "/user/JCBLiveLink/Data/Daily_Interface_Data/current_data/InterfaceDetails_"+today+".xls";
		 	//String fileToBeAttcahed = "C:\\Users\\Z1007653\\Desktop\\FileMove\\current\\InterfaceDetails_"+today+".xls";
			SendEmail email = new SendEmail();
			
		    String receiverMail = "Gunjan.CHOPRA@jcb.com,Satpreet.Kaur@jcb.com,deepthi.rao@wipro.com,zakir.37@wipro.com,abhishek.goyal@wipro.com";
		    String emailSubject = "Interface Details";
			String emailBody = "Dear Customer,\nPlease find the attached Interface Detailed Report.\r\nThanks,\r\n Team";
			status = email.sendMailWithAttachment(receiverMail, emailSubject, emailBody, fileToBeAttached);
			System.out.println("Status came from sendEmailWithAttachment: "+status);
		 	}
		 	catch(Exception e){
		 		System.out.println("Exception error: "+e);
		 	}
		 	
		 	//Moving the file from current to archieved.
		 	try {
		 		if(status.equalsIgnoreCase("SUCCESS"))
				moveFileToArchive(today);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return status;
	}

	public List<HashMap<String, String>> getListOfFailedInterface() {
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		
		iLogger.info(":: Inside getListOfFailedInterface method :: ");
		
		List<HashMap<String, String>> response = new LinkedList<HashMap<String, String>>();
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;
		
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String today = sdf.format(cal.getTime());
		System.out.println("Today's date: " + today);
		
		
		//#########################################
		try {

			ConnectMySQL connFactory = new ConnectMySQL();
			connection = connFactory.getConnection();
			String faultDetailsQuery = "select file_name, message_string, Failure_Cause from fault_details where file_name like '%" +today+"%'";
			System.out.println("faultDetailsQuery: "+ faultDetailsQuery);

			statement = connection.createStatement();
			rs = statement.executeQuery(faultDetailsQuery);
			HashMap<String, String> vinData = null;
			while(rs.next()) {
				//Retrieve the data
				vinData = new HashMap<String, String>();
				vinData.put("FileName", rs.getString("file_name"));
				vinData.put("MessageString", rs.getString("message_string"));
				vinData.put("FailureCause", rs.getString("Failure_Cause"));

				response.add(vinData);
				System.out.println(rs.getString("file_name"));
			}


		} catch (Exception e) {
			e.printStackTrace();
			fLogger.fatal( "::Exception Caught while geting failed interface lists ::"+e.getMessage());
		}
		finally{
			try{
				if(rs != null)
					rs.close();
				if(statement != null)
					statement.close();
				if(connection != null)
					connection.close();
			}catch (Exception e) {
				e.printStackTrace();
				fLogger.fatal("::Exception Caught while closing the connection::"+e.getMessage());
			}
		}
		//#########################################
		return response;
		
	}
	
	
	public void moveFileToArchive(String date) {
		
		Logger fLogger = FatalLoggerClass.logger;
		//For Windows
		//String currentPath = "C:\\Users\\Z1007653\\Desktop\\FileMove\\current\\";
		//String archivedPath = "C:\\Users\\Z1007653\\Desktop\\FileMove\\archieved\\";
		
		//For Unix
		String currentPath = "/user/JCBLiveLink/Data/Daily_Interface_Data/current_data/"; 
		String archivedPath = "/user/JCBLiveLink/Data/Daily_Interface_Data/archived_data/";
		
		
		System.out.println("currentPath: "+currentPath+"\narchivedPath"+archivedPath);
		
		String fileName = "InterfaceDetails_" + date + ".xls";
		System.out.println("file name: "+fileName);
		try {
			java.nio.file.Path temp = Files.move(Paths.get(currentPath + fileName), Paths.get(archivedPath + fileName), StandardCopyOption.REPLACE_EXISTING);
			if (temp != null) {
				System.out.println("File moved to archieved successfully.");
			} else {
				System.out.println("Failed to move the file to archieved.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			fLogger.fatal("::Exception Caught while moving Interface Details file to archieved :: "+e.getMessage());
		}
	}
	
}