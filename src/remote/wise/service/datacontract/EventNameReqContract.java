/**
 * 
 */
package remote.wise.service.datacontract;

import java.util.List;

/**
 * @author roopn5
 *
 */
public class EventNameReqContract {
	
	private List<Integer> eventTypeId;

	/**
	 * @return the eventTypeId
	 */
	public List<Integer> getEventTypeId() {
		return eventTypeId;
	}

	/**
	 * @param eventTypeId the eventTypeId to set
	 */
	public void setEventTypeId(List<Integer> eventTypeId) {
		this.eventTypeId = eventTypeId;
	}

	
}
