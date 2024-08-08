package remote.wise.service.implementation;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import org.apache.logging.log4j.Logger;

import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.CommonUtil;

public class CmhRefreshOffsetImpl {
	public String cmhRefreshOffset(){
		
		Logger iLogger = InfoLoggerClass.logger;
		String result=null;
		try{
			//DF20181213-MA369757 -updating the offset to NULL in asset_profile when offset value is 20 minutes old.
			Date todayDate = new Date();
			Calendar c1 = Calendar.getInstance();
			c1.setTime(todayDate);
			Timestamp cmhUpdatedTime = new Timestamp(c1.getTime().getTime());
			String MinDiffQuery = "UPDATE asset_profile set offset=NULL,CmhUpdatedTime='"
					+ cmhUpdatedTime
					+ "' where TIMESTAMPDIFF(MINUTE,CmhUpdatedTime,'"+cmhUpdatedTime+"')>=20;";
			iLogger.info("CmhRefreshOffsetService Query :"+MinDiffQuery);
			 result=new CommonUtil().insertData(MinDiffQuery);

			iLogger.info("updating the offset to NULL in asset_profile when offset value is 20 minutes old::status"+result);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}

}
