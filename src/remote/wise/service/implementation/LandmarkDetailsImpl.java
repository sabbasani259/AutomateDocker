package remote.wise.service.implementation;

import java.util.LinkedList;
import java.util.List;

import remote.wise.businessobject.LandmarkCategoryBO;

import remote.wise.exception.CustomFault;
import remote.wise.service.datacontract.LandmarkDetailsReqContract;
import remote.wise.service.datacontract.LandmarkDetailsRespContract;
import remote.wise.util.CommonUtil;
/**
 * LandmarkDetailsImpl will allow to get and set Landmark Details
 * @author jgupta41
 *
 */
public class LandmarkDetailsImpl {
	//********************************************** get Landmark Details for given Tenancy and Login_Id *****************************************
	/**
	 * *This method gets all landmark details that belongs specified Tenancy ID and Login Id
	 * @param landmarkDetailsReq:Get all landmark details for a given Tenancy ID
	 * @return respList :Returns list of landmark details attached for the Tenancy ID  defined. 
	 * @throws CustomFault:custom exception is thrown when the login_id is not specified or invalid,Tenancy ID is invalid when specified
	 */
	public List< LandmarkDetailsRespContract> getLandmarkDetails( LandmarkDetailsReqContract landmarkDetailsReq)throws CustomFault
	{
		List<LandmarkDetailsRespContract> respList = new LinkedList<LandmarkDetailsRespContract>();
		LandmarkCategoryBO landmarkCategoryBO= new LandmarkCategoryBO();
		List<LandmarkCategoryBO> landmarkCategoryBOList =  landmarkCategoryBO.getLandmarkDetailsObjList(landmarkDetailsReq.getLogin_id(),landmarkDetailsReq.getTenancy_ID());
		
	
		for(int i=0;i<landmarkCategoryBOList.size();i++)
		{	LandmarkDetailsRespContract respContractObj=new LandmarkDetailsRespContract();
		
			respContractObj.setAddress(landmarkCategoryBOList.get(i).getAddress());
			respContractObj.setIsArrival(landmarkCategoryBOList.get(i).getIsArrival());
			respContractObj.setIsDeparture(landmarkCategoryBOList.get(i).getIsDeparture());
			respContractObj.setLandmark_Category_ID(landmarkCategoryBOList.get(i).getLandmark_Category_ID());
			respContractObj.setLandmark_Category_Name(landmarkCategoryBOList.get(i).getLandmark_Category_Name());
			respContractObj.setLandmark_id(landmarkCategoryBOList.get(i).getLandmark_id());
			respContractObj.setLandmark_Name(landmarkCategoryBOList.get(i).getLandmark_Name());
			respContractObj.setLatitude(landmarkCategoryBOList.get(i).getLatitude());
			respContractObj.setLongitude(landmarkCategoryBOList.get(i).getLongitude());
			respContractObj.setRadius(landmarkCategoryBOList.get(i).getRadius());
			//Added by Juhi on 24 july 2013 DefectId- 1043
			respContractObj.setLandmark_Category_Color_Code(landmarkCategoryBOList.get(i).getLandmark_Category_Color_Code());
			respList.add(respContractObj);
		}
		return respList;
	}
	//*************************************************END of get Landmark Details for given Tenancy and Login_Id ************************************
	//********************************************** set Landmark Details for given Landmark_Category_ID  and Landmark_Name*****************************************
	/**
	 * This method sets Landmark Details that belongs specified Landmark Category Name and Landmark_Name
	 * @param landmarkDetailsResp:Landmark Details to be setted.
	 * @return flag:Return Landmark_Id
	 * @throws CustomFault:custom exception is thrown when input like Landmark_Category_Name,Landmark_Name,Landmark_Category_ID is not specified or invalid
	 */
	public LandmarkDetailsReqContract setLandmarkDetails(	LandmarkDetailsRespContract landmarkDetailsResp)throws CustomFault
	{
		//DF20171011: KO369761 - Security Check added for input text fields.
		CommonUtil util = new CommonUtil();
		String isValidinput=null;
		// DF20180920 ::MANI ::sending the name for validation since countrycode has ex:+91 which is not allowed in validations
		isValidinput = util.inputFieldValidation(landmarkDetailsResp.getLandmark_Name().split("\\|")[0]);
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		
		isValidinput = util.inputFieldValidation(landmarkDetailsResp.getAddress());
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		
		isValidinput = util.inputFieldValidation(landmarkDetailsResp.getLatitude());
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		
		isValidinput = util.inputFieldValidation(landmarkDetailsResp.getLongitude());
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		
		isValidinput = util.inputFieldValidation(String.valueOf(landmarkDetailsResp.getRadius()));
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		
		LandmarkCategoryBO landmarkCategoryBO=new 	LandmarkCategoryBO();
		int flag=landmarkCategoryBO.setLandmarkDetails(landmarkDetailsResp.getLogin_id(),landmarkDetailsResp.getLandmark_id(), landmarkDetailsResp.getLandmark_Category_ID(),landmarkDetailsResp.getLandmark_Name(),landmarkDetailsResp.getLandmark_Category_Name(),landmarkDetailsResp.getLatitude(),landmarkDetailsResp.getLongitude(), landmarkDetailsResp.getRadius(), landmarkDetailsResp.getAddress(), landmarkDetailsResp.getIsArrival(), landmarkDetailsResp.getIsDeparture());
		LandmarkDetailsReqContract landmarkDetailsReqContract=new LandmarkDetailsReqContract();
		landmarkDetailsReqContract.setLandmark_id(flag);
		return landmarkDetailsReqContract;
	}
	//*************************************************END of set Landmark Details for given Landmark_Category_ID  and Landmark_Name ************************************
}

