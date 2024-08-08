package remote.wise.service.implementation;

import java.util.HashMap;

import remote.wise.businessobject.UserDetailsBO;
import remote.wise.service.datacontract.ModelResponseContract;

public class ModelDetailsImpl {

	HashMap<Integer,String> modelMap;
	
	public void setModelMap(HashMap<Integer, String> modelMap) {
		this.modelMap = modelMap;
	}
	
	public ModelResponseContract getModelMap(){
		UserDetailsBO udBO= new UserDetailsBO();
		modelMap=udBO.getModelMap();
		ModelResponseContract respObj = new ModelResponseContract();
		respObj.setModelList(modelMap);
		return respObj;
	}
}
