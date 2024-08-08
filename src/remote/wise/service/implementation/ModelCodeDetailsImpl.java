/**
 * 
 */
package remote.wise.service.implementation;

import java.util.HashMap;

import remote.wise.businessobject.UserDetailsBO;
import remote.wise.service.datacontract.ModelCodeResponseContract;

/**
 * @author roopn5
 *
 */
public class ModelCodeDetailsImpl {
	
HashMap<String,String> modelCodeMap;
	
	


	/**
 * @return the modelCodeMap
 */
public HashMap<String, String> getModelCodeMap() {
	return modelCodeMap;
}




/**
 * @param modelCodeMap the modelCodeMap to set
 */
public void setModelCodeMap(HashMap<String, String> modelCodeMap) {
	this.modelCodeMap = modelCodeMap;
}




	public ModelCodeResponseContract getModelMap(){
		UserDetailsBO udBO= new UserDetailsBO();
		ModelCodeResponseContract respObj = new ModelCodeResponseContract();
		respObj=udBO.getModelCodeMap();
		
		//respObj.setModelCodeMap(modelCodeMap);
		return respObj;
	}

}
