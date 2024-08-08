package remote.wise.service.implementation;

import remote.wise.businessobject.LandmarkCategoryBO;
import remote.wise.exception.CustomFault;
import remote.wise.service.datacontract.DeleteLandmarkRespContract;

/**
 *Implementation class will allow to delete LandmarkCategory.
 *  @author jgupta41
 */
public class DeleteLandmarkImpl {
	//********************************************** Delete Landmark for a given Landmark_id****************************
	/**
	 * Delete Landmark for a given Landmark_id
	 * @param deleteLandmarkResp Landmark to be deleted
	 * @return flag  Returns the status String as either SUCCESS/FAILURE for setting activeStatus for given Landmark
	 * @throws CustomFault custom exception is thrown when the Landmark_id is not specified or invalid,Landmark_id is invalid when specified
	 */
	public String setDeleteLandmark(DeleteLandmarkRespContract deleteLandmarkResp)throws CustomFault
	{		LandmarkCategoryBO landmarkCategoryBO=new 	LandmarkCategoryBO();
		String flag=landmarkCategoryBO.setDeleteLandmark(deleteLandmarkResp.getLandmark_id());
		return flag;
	}
	//*************************************************END of  Delete Landmark for a given Landmark_id************************************
	
}
