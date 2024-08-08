package remote.wise.service.implementation;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import org.apache.logging.log4j.Logger;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import remote.wise.handler.WhatsappHandler;
import remote.wise.handler.WhatsappTemplate;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.ConnectMySQL;

public class BreakfastReportSingleThread implements Callable
{
	int threadNumber;
	String userID;
	String mobileNumber;
	String mappingCode;
	String batchReportPath;
	CountDownLatch latch;
	
	@Override
	public Object call()
	{
		Logger iLogger = InfoLoggerClass.logger;
		long startTime = System.currentTimeMillis();
		String fileName= generateBreakfastReport(this.threadNumber, this.userID, this.mobileNumber, this.mappingCode, this.batchReportPath, this.latch);
		long endTime=System.currentTimeMillis();
		iLogger.info("BreakfastReport:BreakfastReportSingleThread:UserID"+this.userID+"; generated FileName:" +fileName+"; "+
				"Total Time in ms:"+(endTime-startTime));
		
		if(fileName!=null && !fileName.equalsIgnoreCase("No record Found"))
		{
			//Send the details to WhatsApp Queue
			WhatsappTemplate whatsappTemplate = new WhatsappTemplate();				
			whatsappTemplate.setTo(this.mobileNumber);
			whatsappTemplate.setMsgBody(fileName);
			whatsappTemplate.setMsgType("BreakfastReport");
			new WhatsappHandler().putToKafkaProducerQueue("WhatsAppQueue", whatsappTemplate);
			iLogger.info("BreakfastReport:BreakfastReportSingleThread:UserID"+this.userID+"; FileName:" +fileName+"; Details sent to WhatsApp Queue");
		}
		
		return fileName;
	}
	
	public String generateBreakfastReport(int threadNumber, String userID, String mobileNumber, String mappingCode,String batchReportPath,
			CountDownLatch latch)
	{
		String fileName=null;
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		Connection con=null;
		Statement stmt=null;
		ResultSet rs = null;
		//Write data to PDF
		Document document = new Document(PageSize.A4);
		
		
		try
		{
			//Get DB connection
			con = new ConnectMySQL().getConnection();
			stmt = con.createStatement();
			
			
			//Get the count of LL4 VINs tagged to the user
			String countQuery = "SELECT count(*) as count" +
					" from asset_monitoring_snapshot a, BreakfastReport_AggParameters b, asset c, account d" +
					" where a.serial_number=b.AssetID and a.serial_number=c.serial_number and c.Primary_Owner_ID=d.account_id" +
					" and d.mapping_code='"+mappingCode+"'"+
					" and JSON_EXTRACT(TxnData,'$.FW_VER') is not null " +
					" and ( CAST(SUBSTRING(JSON_EXTRACT(TxnData,'$.FW_VER'),2,2) AS UNSIGNED) >=30 " +
					" and CAST(SUBSTRING(JSON_EXTRACT(TxnData,'$.FW_VER'),2,2) AS UNSIGNED) < 50)";
			int count=0;
			rs = stmt.executeQuery(countQuery);
			
			System.out.println("countQuery:"+countQuery);
			while(rs.next())
			{
				count=rs.getInt("count");
				System.out.println("count:"+rs.getInt("count"));
			}
			if(count==0)
			{
				return "No record Found";
			}
			
			//Get the list of LL4 VINs tagged to the user with the usage details
			String query = "SELECT a.serial_number, JSON_EXTRACT(TxnData,'$.CMH') as CMH,JSON_EXTRACT(TxnData,'$.FUEL_PERCT') as FuelLevel," +
					" JSON_EXTRACT(TxnData,'$.LAT') as Latitude,JSON_EXTRACT(TxnData,'$.LONG') as Longitude," +
					" b.WorkingHrs,b.IdleHrs,b.EngineOffHrs, b.UtilNationalAvg, b.IdleTimeNationalAvg" +
					" from asset_monitoring_snapshot a, BreakfastReport_AggParameters b, asset c, account d" +
					" where a.serial_number=b.AssetID and a.serial_number=c.serial_number and c.Primary_Owner_ID=d.account_id" +
					" and d.mapping_code='"+mappingCode+"'"+
					" and JSON_EXTRACT(TxnData,'$.FW_VER') is not null " +
					" and ( CAST(SUBSTRING(JSON_EXTRACT(TxnData,'$.FW_VER'),2,2) AS UNSIGNED) >=30 " +
					" and CAST(SUBSTRING(JSON_EXTRACT(TxnData,'$.FW_VER'),2,2) AS UNSIGNED) < 50)";
			
			rs = stmt.executeQuery(query);
			
		
			String[] headers = new String[]{"Machine", "HMR", "Fuel %", "Latitude","Longitude","Engine Off Hrs",
											"Working Hrs","National Avg. Working Hrs","Idle Time","National Avg. Idle Time"};
			
			String[][] rows = new String[count][10];  
			int rowNum=0;
			while(rs.next())
			{
				rows[rowNum][0]=rs.getString("serial_number");
				
				if(rs.getString("CMH")!=null)
				{
					rows[rowNum][1]=String.valueOf(truncateDecimal(Double.valueOf(rs.getString("CMH").replaceAll("\"", "")),3));
				}
				
				if(rs.getString("FuelLevel")!=null)
				{
					rows[rowNum][2]=String.valueOf(Double.valueOf(rs.getString("FuelLevel").replaceAll("\"", "")));
				}
				
				if(rs.getString("Latitude")!=null)
				{
					rows[rowNum][3]=String.valueOf(truncateDecimal(Double.valueOf(rs.getString("Latitude").replaceAll("\"", "")),3));
				}
				
				if(rs.getString("Longitude")!=null)
				{
					rows[rowNum][4]=String.valueOf(truncateDecimal(Double.valueOf(rs.getString("Longitude").replaceAll("\"", "")),3));
				}
				
				rows[rowNum][5]=String.valueOf(truncateDecimal(rs.getDouble("EngineOffHrs"),3));
				rows[rowNum][6]=String.valueOf(truncateDecimal(rs.getDouble("WorkingHrs"),3));
				rows[rowNum][7]=String.valueOf(truncateDecimal(rs.getDouble("UtilNationalAvg"),3));
				rows[rowNum][8]=String.valueOf(truncateDecimal(rs.getDouble("IdleHrs"),3));
				rows[rowNum][9]=String.valueOf(truncateDecimal(rs.getDouble("IdleTimeNationalAvg"),3));
				
				rowNum++;
			}
			
			 // Get an instance of PdfWriter and create a .pdf file as an output.
			String currentDate=new SimpleDateFormat("yyyyMMdd").format(new Date());
			fileName=batchReportPath+"/"+"BreakfastReport_"+currentDate+"_"+userID;
            PdfWriter.getInstance(document, new FileOutputStream(fileName+".pdf"));
            document.open();
			
            Font fontHeader = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.BOLD);
            Font fontRow = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.NORMAL);
            
            PdfPTable table = new PdfPTable(headers.length);
            table.setWidthPercentage(100); //Width 100%
            float[] columnWidths = new float[]{25f, 11f, 10f, 12f, 12f, 10f, 10f, 10f, 10f, 10f};
            table.setWidths(columnWidths);
                       
            for (String header : headers) {
                PdfPCell cell = new PdfPCell();
                cell.setGrayFill(0.9f);
                cell.setPhrase(new Phrase(header, fontHeader));
                table.addCell(cell);
            }
            
            table.completeRow();

            for (String[] row : rows) {
                for (String data : row) {
                    Phrase phrase = new Phrase(data, fontRow);
                    PdfPCell cell = new PdfPCell(phrase);
                    cell.setFixedHeight(35f);
                    table.addCell(cell);
                }
                table.completeRow();
            }

            String todayDate=new SimpleDateFormat("dd/MM/yyyy").format(new Date());
            Font f = new Font(FontFamily.TIMES_ROMAN, 18.0f, Font.BOLD, BaseColor.BLACK);
            Chunk c = new Chunk("JCB India Livelink", f);
            //c.setBackground(BaseColor.YELLOW);
            Paragraph p = new Paragraph(c);
            document.add(p);
            
           // document.add( Chunk.NEWLINE );
            //document.add( Chunk.NEWLINE );
            
            Font f1 = new Font(FontFamily.TIMES_ROMAN, 14.0f, Font.BOLD, BaseColor.BLACK);
            Chunk c1 = new Chunk("Breakfast Report :" + todayDate, f1);
           // c1.setBackground(BaseColor.YELLOW);
            Paragraph p1 = new Paragraph(c1);
            document.add(p1);
            
            document.add( Chunk.NEWLINE );
           // document.add( Chunk.NEWLINE );
            
            document.setMargins(0,0,0,0);
            document.addTitle("Breakfast Report: "+currentDate);
            document.add(table);
            
		}
		catch(Exception e)
		{
			fLogger.fatal("BreakfastReport:BreakfastReportSingleThread:Exception:"+e.getMessage(),e);
		}
		finally
		{
			try
			{
				if(stmt!=null)
					stmt.close();
				if(con!=null)
					con.close();
			}
			catch(Exception e)
			{
				fLogger.fatal("BreakfastReport:BreakfastReportSingleThread:Exception in closing the connection:"+e.getMessage(),e);
			}	
			
			if(latch!=null)
				latch.countDown();
			
			 document.close();
		}
		
		return fileName;
		
	}
	
	private static BigDecimal truncateDecimal(final double x, final int numberofDecimals) {
	    return new BigDecimal(String.valueOf(x)).setScale(numberofDecimals, BigDecimal.ROUND_DOWN);
	}
}
