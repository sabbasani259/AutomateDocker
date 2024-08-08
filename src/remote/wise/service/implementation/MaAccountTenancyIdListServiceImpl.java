package remote.wise.service.implementation;

import java.util.List;

import remote.wise.dao.MaAccountTenancyIdListServiceDao;

public class MaAccountTenancyIdListServiceImpl {

	public List<Integer> getMaAccountTenancyList() {
		return MaAccountTenancyIdListServiceDao.tenancyOfMaAccountHolders();
	}
}
