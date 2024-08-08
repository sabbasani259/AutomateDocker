package remote.wise.service.implementation;

import java.util.LinkedList;
import java.util.List;

import remote.wise.businessobject.LandmarkCategoryBO;
import remote.wise.exception.CustomFault;
import remote.wise.service.datacontract.LandmarkCategoryReqContract;
import remote.wise.service.datacontract.LandmarkCategoryRespContract;
import remote.wise.util.CommonUtil;
/**
 * LandmarkCategoryImpl will allow to get and set Landmark Category
 * @author jgupta41
 *
 */
public class LandmarkCategoryImpl {
	//********************************************** get Landmark Category for given Tenancy *****************************************
	/**
	 * This method gets all landmark category that belongs specified Tenancy ID and Login Id
	 * @param landmarkCategoryReq: Get all landmark category for a given Tenancy ID
	 * @return respList :Returns list of landmark categories attached for the Tenancy ID  defined. 
	 * @throws CustomFault:custom exception is thrown when the login_id is not specified or invalid,Tenancy ID is invalid when specified
	 */
	public List<LandmarkCategoryRespContract> getLandmarkCategory(LandmarkCategoryReqContract landmarkCategoryReq)throws CustomFault
	{
		List<LandmarkCategoryRespContract> respList = new LinkedList<LandmarkCategoryRespContract>();
		LandmarkCategoryBO landmarkBO= new LandmarkCategoryBO();
		List<LandmarkCategoryBO> landmarkCategoryBO =  landmarkBO.getLandmarkCategoryBOList(landmarkCategoryReq.getLogin_id(),landmarkCategoryReq.getTenancy_ID());
		
		for(int i=0;i<landmarkCategoryBO.size();i++)
		{
			LandmarkCategoryRespContract  respContractObj=new LandmarkCategoryRespContract();
			
			respContractObj.setLandmark_Category_ID(landmarkCategoryBO.get(i).getLandmark_Category_ID());
			respContractObj.setLandmark_Category_Name(landmarkCategoryBO.get(i).getLandmark_Category_Name());
			respContractObj.setColor_Code(landmarkCategoryBO.get(i).getColor_Code());
			respContractObj.setLandmark_Category_Color_Code(landmarkCategoryBO.get(i).getLandmark_Category_Color_Code());
			respList.add(respContractObj);
		}
		return respList;
	}

	//*************************************************END of get Landmark Category for given Tenancy ************************************
	//********************************************** set Landmark Category for given Tenancy  *****************************************
	/**
	 * This method sets Color Code that belongs specified Landmark Category Name and Tenancy ID
	 * @param landmarkCategoryResp:Tenancy ID,Landmark Category Name,Color Code to be setted.
	 * @return flag:Return the status String as either Success/Failure.
	 * @throws CustomFault:custom exception is thrown when the Landmark_Category_Name,Tenancy ID is not specified or invalid
	 */
	public String setLandmarkCategory(	LandmarkCategoryRespContract landmarkCategoryResp)throws CustomFault
	{
		//DF20171011: KO369761 - Security Check added for input text fields.
		CommonUtil util = new CommonUtil();
		String isValidinput=null;
		
		isValidinput = util.inputFieldValidation(landmarkCategoryResp.getLandmark_Category_Name());
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		
		isValidinput = util.inputFieldValidation(landmarkCategoryResp.getColor_Code());
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		
		LandmarkCategoryBO landmarkCategoryBO=new 	LandmarkCategoryBO();
		String flag=landmarkCategoryBO.setLandmarkCategory(landmarkCategoryResp.getLandmark_Category_ID(),landmarkCategoryResp.getLandmark_Category_Name(),landmarkCategoryResp.getColor_Code(), landmarkCategoryResp.getTenancy_ID(),landmarkCategoryResp.getLandmark_Category_Color_Code());
		return flag;
	}
	//********************************************** End of set Landmark Category for given Tenancy  *****************************************
}
