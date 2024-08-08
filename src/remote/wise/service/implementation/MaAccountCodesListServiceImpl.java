package remote.wise.service.implementation;

import java.util.List;

import remote.wise.dao.MaAccountCodesListServiceDao;
import remote.wise.dao.MaAccountTenancyIdListServiceDao;

public class MaAccountCodesListServiceImpl {
	
	public List<String> getMaAccountCodesList() {
		return MaAccountCodesListServiceDao.accountCodesOfMaAccountHolders();
	}
}
