/**
 * 
 */
package remote.wise.service.implementation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import remote.wise.businessobject.UserDetailsBO;
import remote.wise.exception.CustomFault;
import remote.wise.service.datacontract.StateRespContract;

/**
 * @author roopn5
 *
 */
public class StateImpl {
	private int stateId;
	private String stateName;
	/**
	 * @return the stateId
	 */
	public int getStateId() {
		return stateId;
	}
	/**
	 * @param stateId the stateId to set
	 */
	public void setStateId(int stateId) {
		this.stateId = stateId;
	}
	/**
	 * @return the stateName
	 */
	public String getStateName() {
		return stateName;
	}
	/**
	 * @param stateName the stateName to set
	 */
	public void setStateName(String stateName) {
		this.stateName = stateName;
	}
	public List<StateRespContract> getStates()throws CustomFault
	{
		
		
		List<StateRespContract> respList = new ArrayList<StateRespContract>();
		UserDetailsBO userBO=new UserDetailsBO();
		List<StateImpl> StateImplList = userBO.getStates();	
		
		StateRespContract respObj = null;
		StateImpl implObj = null;
		Iterator<StateImpl> listIterator=StateImplList.iterator();
		while(listIterator.hasNext()){			
			implObj = listIterator.next();
			respObj = new StateRespContract();
			respObj.setStateId(implObj.getStateId());
			respObj.setStateName(implObj.getStateName());
			respList.add(respObj);
		}		
		return respList;		
	}

}
