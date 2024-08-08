package remote.wise.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

//Added by SU334449 - Time Conversion Utility for IST and LT Conversions
public class GmtLtTimeConversion {

	public String convertGmtToLocal(String timeZone, String transTimeStmp) throws ParseException{
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		String time = timeZone.replaceAll("[^\\d:]", "");
		int hours = Integer.parseInt(time.split("\\:")[0]);
		int minutes = Integer.parseInt(time.split("\\:")[1]);
		Date newDate1 = sdf.parse(transTimeStmp);
		cal.setTime(newDate1);
		cal.add(Calendar.HOUR, hours);
		cal.add(Calendar.MINUTE, minutes);
		transTimeStmp = sdf.format(cal.getTime());
		return transTimeStmp;
	}
	
	public String convertSAARCTime(String timeZone) throws ParseException{
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		String time = timeZone.replaceAll("[^\\d:]", "");
		int hours = Integer.parseInt(time.split("\\:")[0]);
		int minutes = Integer.parseInt(time.split("\\:")[1]);
		String totalHours = "24:00:00";
		Date newDate1 = sdf.parse(totalHours);
		cal.setTime(newDate1);
		cal.add(Calendar.HOUR, -hours);
		cal.add(Calendar.MINUTE, -minutes);
		String transTimeStamp = sdf.format(cal.getTime());
		return transTimeStamp;
	}
}
