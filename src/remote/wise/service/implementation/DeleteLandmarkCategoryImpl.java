package remote.wise.service.implementation;

import remote.wise.businessobject.LandmarkCategoryBO;
import remote.wise.exception.CustomFault;
import remote.wise.service.datacontract.DeleteLandmarkCategoryRespContract;


public class DeleteLandmarkCategoryImpl {
	public String setDeleteLandmarkCategory(DeleteLandmarkCategoryRespContract deleteLandmarkCategoryResp)throws CustomFault
	{
		LandmarkCategoryBO landmarkCategoryBO=new 	LandmarkCategoryBO();
		String flag=landmarkCategoryBO.setDeleteLandmarkCategory(deleteLandmarkCategoryResp.getLandmark_Category_id() );
		return flag;
	}
}
