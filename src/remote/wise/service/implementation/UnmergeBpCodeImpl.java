package remote.wise.service.implementation;

import java.util.List;

import remote.wise.dao.UnmergeBpCodeDAO;

public class UnmergeBpCodeImpl {

	public String updateMappingCode(List<String> accountCodeList,String userID) {
		UnmergeBpCodeDAO daoObj = new UnmergeBpCodeDAO();
		String response = null;
		if(accountCodeList!=null && !accountCodeList.isEmpty()){
			
		for(String accountCode:accountCodeList){
			
		boolean isFound=daoObj.searchAccountCode(accountCode);
		if(isFound)
	    daoObj.updateMappingCodeForCorrespondingAccountCode(accountCode,userID);
		else{
				if(response==null)
					response=accountCode;
				else
					response+=" "+accountCode;
			}
		}
	}
		return response;
	}		
}
