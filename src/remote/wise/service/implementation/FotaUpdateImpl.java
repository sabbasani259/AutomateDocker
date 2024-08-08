package remote.wise.service.implementation;

import java.sql.Timestamp;

import org.apache.logging.log4j.Logger;

////import org.apache.log4j.Logger;

import remote.wise.businessobject.FotaUpdateBO2;
import remote.wise.businessobject.FotaUpdateNewBONEW;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;

import remote.wise.service.datacontract.FotaUpdateReqContract;
import remote.wise.service.datacontract.FotaUpdateRespContract;
//import remote.wise.util.WiseLogger;

public class FotaUpdateImpl {
	
	//public static WiseLogger infoLogger = WiseLogger.getLogger("FotaUpdateImpl:","info");
	
	private String imeiNo;
	
	private String versionId;
	private String sessionId;
	private Timestamp updateDate;
	private String status;
	private String file_path;
	public String getImeiNo() {
		return imeiNo;
	}
	public void setImeiNo(String imeiNo) {
		this.imeiNo = imeiNo;
	}
	public String getVersionId() {
		return versionId;
	}
	public void setVersionId(String versionId) {
		this.versionId = versionId;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public Timestamp getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Timestamp updateDate) {
		this.updateDate = updateDate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getFile_path() {
		return file_path;
	}
	public void setFile_path(String file_path) {
		this.file_path = file_path;
	}
	
	public FotaUpdateRespContract FotaUpdate(FotaUpdateReqContract reqObj ) throws CustomFault
	{
		//Logger bLogger = BusinessErrorLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
    	
		if(reqObj.getFotaimeiNo()==null || reqObj.getFotaimeiNo().equals("")){
			iLogger.info("Please provide IMEI no. !!!");
			throw new CustomFault("Please provide IMEI no. !!!");
		}
		if(reqObj.getFotaSessionId()==null || reqObj.getFotaSessionId().equals("")){
			iLogger.info("Please provide session id. !!!");
			throw new CustomFault("Please provide session id. !!!");
		}
		if(reqObj.getFotaSessionId()==null || reqObj.getFotaSessionId().equals("")){
			iLogger.info("Please provide session id. !!!");
			throw new CustomFault("Please provide session id. !!!");
		}
		if(reqObj.getFotaVersionId()==null || reqObj.getFotaVersionId().equals("")){
			iLogger.info("Please provide version id. !!!");
			throw new CustomFault("Please provide version id. !!!");
		}
		FotaUpdateRespContract respObj = new FotaUpdateRespContract();
		//FotaUpdateBO2  fotaUpdateBO2Obj = new FotaUpdateBO2();
		FotaUpdateNewBONEW fotaUpdateBO2Obj = new FotaUpdateNewBONEW();
		FotaUpdateImpl fotaUpdateImpl = fotaUpdateBO2Obj.getFotaUpdate(reqObj);
		respObj.setFile_path(fotaUpdateImpl.getFile_path());
		respObj.setSessionId(fotaUpdateImpl.getSessionId());
		respObj.setVersionId(fotaUpdateImpl.getVersionId());
		respObj.setStatus(fotaUpdateImpl.getStatus());
		return respObj;
	}

}
