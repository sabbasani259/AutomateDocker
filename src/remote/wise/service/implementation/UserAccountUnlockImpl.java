/**
 * 
 */
package remote.wise.service.implementation;

import org.apache.logging.log4j.Logger;

import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.CommonUtil;

/**
 * @author ROOPN5
 *
 */
public class UserAccountUnlockImpl {
	
	public String unLockAccountDetails(){
		
		Logger iLogger = InfoLoggerClass.logger;
		String result=null;
		
		try{
			//DF20180817-MA369757 -Updating the account unlock query. Unlock should be lockedTime>=12 hrs
			//String hourDiffQuery="UPDATE contact set errorLogCounter=0,lockedOutTime=null where TIMESTAMPDIFF(hour,lockedOutTime, CURRENT_TIMESTAMP)>=12 and errorLogCounter >0;";
			//Updating the account unlock query. Unlock should be lockedTime>=30 minute
			String hourDiffQuery="UPDATE contact set errorLogCounter=0,lockedOutTime=null where TIMESTAMPDIFF(minute,lockedOutTime, CURRENT_TIMESTAMP)>=30 and errorLogCounter >0;";
			//String updateQuery="UPDATE contact set errorLogCounter=0 where errorLogCounter >0";

			 result=new CommonUtil().insertData(hourDiffQuery);

			iLogger.info("Reset the errorCounter to 0 for all the users::status"+result);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return result;
	}

}
