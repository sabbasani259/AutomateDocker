package remote.wise.businessentity;

import java.io.Serializable;

public class TenancyBridgeEntity extends BaseBusinessEntity implements Serializable
{
	int parentId;
	int childId;
	int level;
	String bottomFlag;
	String topFlag;
	
	
	public int getParentId() {
		return parentId;
	}
	public void setParentId(int parentId) {
		this.parentId = parentId;
	}
	public int getChildId() {
		return childId;
	}
	public void setChildId(int childId) {
		this.childId = childId;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public String getBottomFlag() {
		return bottomFlag;
	}
	public void setBottomFlag(String bottomFlag) {
		this.bottomFlag = bottomFlag;
	}
	public String getTopFlag() {
		return topFlag;
	}
	public void setTopFlag(String topFlag) {
		this.topFlag = topFlag;
	}
	
	
}
