package remote.wise.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

// Added by Rajani Nagaraju - 20131022 - Time Conversion Utility for IST and GMT Conversions
public class IstGmtTimeConversion 
{
	//---------------------------- Convert the TimeStamp value from IST to GMT Format ----------------------------
	public Timestamp convertIstToGmt(String inputDate)
	{
		Timestamp timeInGmt = null;
		Date inputDateTime =null;
		
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf1.setTimeZone(TimeZone.getTimeZone("IST"));
		try 
		{
			inputDateTime = sdf1.parse(inputDate);
			
		} 
		catch (ParseException e) 
		{
			e.printStackTrace();
		}
		
		sdf1.setTimeZone(TimeZone.getTimeZone("GMT"));
		inputDate = sdf1.format(inputDateTime);
		timeInGmt = Timestamp.valueOf(inputDate);
		
		return timeInGmt;
	}
	
	
	public Timestamp convertIstToGmt(Timestamp inputDate)
	{
		Timestamp timeInGmt = null;
		Date inputDateTime = null;
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("IST"));
		String inputDateInString = sdf.format(inputDate.getTime());
			
		try
		{
			inputDateTime = sdf.parse(inputDateInString);
		}
		
		catch(ParseException e)
		{
			e.printStackTrace();
		}
		
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		String inputDateString = sdf.format(inputDateTime.getTime());
		timeInGmt = Timestamp.valueOf(inputDateString);
		
		return timeInGmt;
	}
	
	//---------------------------- Convert the TimeStamp value from GMT to IST Format ----------------------------
	
	public Timestamp convertGmtToIst(String inputDate)
	{
		Timestamp timeInGmt = null;
		Date inputDateTime =null;
		
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf1.setTimeZone(TimeZone.getTimeZone("GMT"));
		try 
		{
			inputDateTime = sdf1.parse(inputDate);
			
		} 
		catch (ParseException e) 
		{
			e.printStackTrace();
		}
		
		
		sdf1.setTimeZone(TimeZone.getTimeZone("IST"));
		inputDate = sdf1.format(inputDateTime);
		timeInGmt = Timestamp.valueOf(inputDate);
		
		return timeInGmt;
	}
	
	
	
	public Timestamp convertGmtToIst(Timestamp inputDate)
	{
		Timestamp timeInGmt = null;
		Date inputDateTime = null;
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String inputDateInString = sdf.format(inputDate.getTime());
		
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		
		try
		{
			inputDateTime = sdf.parse(inputDateInString);
		}
		
		catch(ParseException e)
		{
			e.printStackTrace();
		}
		
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf1.setTimeZone(TimeZone.getTimeZone("IST"));
		String inputDateString = sdf1.format(inputDateTime.getTime());
		timeInGmt = Timestamp.valueOf(inputDateString);
		
		return timeInGmt;
	}
	
	public static String stringGMTToIST(String date) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		//Added by roopn5 for time stamp defect
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		//Added by roopn5 for time stamp defect
		Date newDate;
		try {
			newDate = sdf.parse(date);
		} catch (Exception e) {
			return date;
		}

		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
		//Added by roopn5 for time stamp defect
		sdf1.setTimeZone(TimeZone.getTimeZone("IST"));

		return sdf1.format(newDate);
		//End
	}
}
