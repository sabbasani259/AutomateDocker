package remote.wise.service.implementation;

import java.util.List;

import remote.wise.dao.MaAssetVinListServiceDao;



public class MaAssetVinListServiceImpl {
	
public List<String> getMaAssetVinList() {
		// TODO Auto-generated method stub
		 return MaAssetVinListServiceDao.AssetVinNumberOfMaAccountHolders();
	}

}
