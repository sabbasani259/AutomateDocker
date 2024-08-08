package remote.wise.service.implementation;


import java.util.HashMap;
import java.util.List;
import remote.wise.businessobject.MAServiceDueOverDueCountBO;

public class MAServiceDueOverDueCountImpl {
	public List<HashMap<String, String>> getServiceDueCount(String accountIdList, String filter, String dateFilter, String downloadFlag, String userId) {	
		List<HashMap<String, String>> result = null;
		result = new MAServiceDueOverDueCountBO().getServiceDueCount(accountIdList,filter,dateFilter, downloadFlag,userId);				
		return result;
	}

	public List<HashMap<String, String>> getServiceOverDueCount(String accountIdList, String filter, String dateFilter, String downloadFlag, String userId) {
		List<HashMap<String, String>> result = null;
		result = new MAServiceDueOverDueCountBO().getServiceOverDueCount(accountIdList, filter,dateFilter, downloadFlag,userId);				
		return result;
	}

}



