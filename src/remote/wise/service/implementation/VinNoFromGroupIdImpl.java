package remote.wise.service.implementation;

import java.util.List;

import remote.wise.dao.VinNoFromGroupIdDAO;

public class VinNoFromGroupIdImpl {
	public List<String> getVins(List<Integer> grpIdList) {
		VinNoFromGroupIdDAO daoObj = new VinNoFromGroupIdDAO();
		List<String> vinList= daoObj
				.getVinAgainstGrpIdFromDB(grpIdList);
		return vinList;
}
}
