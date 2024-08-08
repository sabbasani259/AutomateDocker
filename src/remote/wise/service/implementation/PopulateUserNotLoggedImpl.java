/*
 * ME10008770 : Dhiraj Kumar : 20230809 : Not logged in report Issue - Query modifications
 */

package remote.wise.service.implementation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.logging.log4j.Logger;

import remote.wise.dao.PopulateUserNotLoggedDao;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;

public class PopulateUserNotLoggedImpl {

	Logger fLogger = FatalLoggerClass.logger;
	Logger iLogger = InfoLoggerClass.logger;
	
	public String getUserNotLoggedData() {
		//ME10008770.so
		/*String result="failure";
		PopulateUserNotLoggedDao daoObj = new PopulateUserNotLoggedDao();
		daoObj.deleteDataFromTable();
		List<LinkedHashMap<String, Object>> resultList=daoObj.getDataFromTable();
		List<LinkedHashMap<String, Object>> partialResultList= new ArrayList<LinkedHashMap<String, Object>>();
		
		if(resultList!=null){
		for(int i=0;i<resultList.size();i=i+1){
			partialResultList.add(resultList.get(i));
			daoObj.insertDataIntoTable(partialResultList);
			partialResultList.clear();
			
			}
		}
		result="Success";
		return result;
		*/
		//ME10008770.eo
		//ME10008770.sn
		String result = "FAILURE";
		PopulateUserNotLoggedDao daoObj = new PopulateUserNotLoggedDao();

		// 1. Truncate tmp table.
		result = daoObj.truncateDataFromTable();
		// daoObj.deleteDataFromTable();

		// 2. Get data for Insertion
		List<LinkedHashMap<String, Object>> resultList = daoObj.getDataFromTable();
		List<LinkedHashMap<String, Object>> partialResultList = new ArrayList<LinkedHashMap<String, Object>>();

		// 3. Insert data to tmp table
		if (resultList != null && resultList.size() != 0) {
			for (int i = 0; i < resultList.size(); i = i + 1) {
				partialResultList.add(resultList.get(i));
				result = daoObj.insertDataIntoTable(partialResultList);
				partialResultList.clear();
			}
		} else {
			iLogger.info("Null or Empty data returned from Select query");
			return "FAILURE";
		}

		// 4. If data insertion is success in tmp table swap the data to original table.
		if (result.equalsIgnoreCase("SUCCESS")) {
			result = daoObj.swapDataFromTable();
		}
		return result;
		//ME10008770.en
	}
}
