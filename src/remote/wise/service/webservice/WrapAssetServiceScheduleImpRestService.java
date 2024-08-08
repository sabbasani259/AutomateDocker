package remote.wise.service.webservice;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;
import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.AssestServiceHistoryGetRespContract;
import remote.wise.service.datacontract.AssetServiceScheduleGetReqContract;
import remote.wise.service.datacontract.AssetServiceScheduleGetRespContract;
import remote.wise.service.datacontract.AssetServiceScheduleGetRespContractApi;
import remote.wise.service.implementation.AssetServiceScheduleImpl;
import remote.wise.service.implementation.AssetServiceScheduleImplTwo;

//CR366 - 20221114 - Prasad - New Api for Service History
@Path("/WrapAssetServiceScheduleImp")
public class WrapAssetServiceScheduleImpRestService {

	@POST()
	@Path("wrapAssetServiceScheduleImp")
	@Produces(MediaType.APPLICATION_JSON)

	public List<Map<String, Object>> wrapAssetServiceScheduleImp(Map<String, Object> reqMap) {

		Logger iLogger = InfoLoggerClass.logger;
		List<String> serialNumberList = (List<String>) reqMap.get("serialNumberList");
		String startDate = (String) reqMap.get("startDate");
		String endDate = (String) reqMap.get("endDate");
		iLogger.info("wrapAssetServiceScheduleImp reqMap.get(serialNumberList): "
				+ (List<String>) reqMap.get("serialNumberList"));
		AssetServiceScheduleGetReqContract reqObj = new AssetServiceScheduleGetReqContract();
		List<Map<String, Object>> responseList = new ArrayList<>();

		iLogger.info("wrapAssetServiceScheduleImp Size of SerialNumberList: " + serialNumberList.size());
		System.out.println("SerialNumberList:--------->" + serialNumberList);
		System.out.println("wrapAssetServiceScheduleImp Size of SerialNumberList: " + serialNumberList.size());
		// iteration Serial number from serialNumberList
		Iterator<String> serialNumberIterator = serialNumberList.iterator();
		iLogger.info("wrapAssetServiceScheduleImp  serialNumberIterator: " + serialNumberIterator.hasNext());
		while (serialNumberIterator.hasNext()) {

			AssetServiceScheduleGetRespContractApi respobj = new AssetServiceScheduleGetRespContractApi();
			List<AssetServiceScheduleGetRespContract> respobjList = new ArrayList<>();
			List<AssestServiceHistoryGetRespContract> respobjList2 = new ArrayList<>();
			AssetServiceScheduleGetRespContract serviceScheduleObj = new AssetServiceScheduleGetRespContract();
			String currentCMH = null;
			String serialNumber = serialNumberIterator.next();
			reqObj.setSerialNumber(serialNumber);
			iLogger.info("get serialnumber from requst object: " + reqObj.getSerialNumber());

			// get CMH from com_rep_oem_enhanced
			currentCMH = new AssetServiceScheduleImplTwo().getCurrentCMH(serialNumber);

			try {

				Map<String, Object> map2 = new HashMap<>();

				// ServiceSchedule response
				respobjList = new AssetServiceScheduleImpl().getAssetserviceSchedule(reqObj);
				if (!respobjList.isEmpty() || respobjList.size() != 0) {
					serviceScheduleObj = respobjList.get(respobjList.size() - 1);
					iLogger.info("iteratorObj " + serviceScheduleObj);
					if (serviceScheduleObj.getEventId() == 3) {

						respobj.setDealerName(serviceScheduleObj.getDealerName());
						respobj.setEngineHoursSchedule(serviceScheduleObj.getEngineHoursSchedule());
						respobj.setExtendedWarrantyType(serviceScheduleObj.getExtendedWarrantyType());
						respobj.setHoursToNextService(serviceScheduleObj.getHoursToNextService());
						if (null != serviceScheduleObj.getScheduledDate())
							respobj.setScheduledDate(serviceScheduleObj.getScheduledDate().substring(0, 19));
						else
							respobj.setScheduledDate("NA");
						respobj.setScheduleName(serviceScheduleObj.getScheduleName());
						respobj.setServiceName(serviceScheduleObj.getServiceName());
						respobj.setCurrentCMH(currentCMH);
						respobj.setStatus("Red");

					}
					else if (serviceScheduleObj.getEventId() == 1 || serviceScheduleObj.getEventId() == 2) {

						respobj.setDealerName(serviceScheduleObj.getDealerName());
						respobj.setEngineHoursSchedule(serviceScheduleObj.getEngineHoursSchedule());
						respobj.setExtendedWarrantyType(serviceScheduleObj.getExtendedWarrantyType());
						respobj.setHoursToNextService(serviceScheduleObj.getHoursToNextService());
						if (null != serviceScheduleObj.getScheduledDate())
							respobj.setScheduledDate(serviceScheduleObj.getScheduledDate().substring(0, 19));
						else
							respobj.setScheduledDate("NA");
						respobj.setScheduleName(serviceScheduleObj.getScheduleName());
						respobj.setServiceName(serviceScheduleObj.getServiceName());
						respobj.setCurrentCMH(currentCMH);
						respobj.setStatus("Amber");

					}
					else {
						respobj.setDealerName(serviceScheduleObj.getDealerName());
						respobj.setEngineHoursSchedule(serviceScheduleObj.getEngineHoursSchedule());
						respobj.setExtendedWarrantyType(serviceScheduleObj.getExtendedWarrantyType());
						respobj.setHoursToNextService(serviceScheduleObj.getHoursToNextService());
						if (null != serviceScheduleObj.getScheduledDate())
							respobj.setScheduledDate(serviceScheduleObj.getScheduledDate().substring(0, 19));
						else
							respobj.setScheduledDate("NA");
						respobj.setScheduleName(serviceScheduleObj.getScheduleName());
						respobj.setServiceName(serviceScheduleObj.getServiceName());
						respobj.setCurrentCMH(currentCMH);
						respobj.setStatus("Green");
					}

				} else {
					respobj.setDealerName(serviceScheduleObj.getDealerName());
					respobj.setEngineHoursSchedule(serviceScheduleObj.getEngineHoursSchedule());
					respobj.setExtendedWarrantyType(serviceScheduleObj.getExtendedWarrantyType());
					respobj.setHoursToNextService(serviceScheduleObj.getHoursToNextService());
					if (null != serviceScheduleObj.getScheduledDate())
						respobj.setScheduledDate(serviceScheduleObj.getScheduledDate().substring(0, 19));
					else
						respobj.setScheduledDate("NA");
					respobj.setScheduleName(serviceScheduleObj.getScheduleName());
					respobj.setServiceName(serviceScheduleObj.getServiceName());
					respobj.setCurrentCMH(currentCMH);
					respobj.setStatus("Green");
				}

				// Service history response
				respobjList2 = new AssetServiceScheduleImplTwo().getAssetserviceScheduleTwo(serialNumber, startDate,
						endDate);
				System.out.println("print respObj in wrap --->" + respobj);
				System.out.println("print respObj2 in wrap --->" + respobjList2);
				map2.put("serialNumber", serialNumber);
				map2.put("ServiceSchedule", respobj);
				map2.put("ServiceHistory", respobjList2);
				//
				System.out.println("mappppppppp --->" + map2.containsKey(serialNumber));

				responseList.add(map2);
				System.out.println("print responseList in wrap --->" + responseList);
			} catch (NullPointerException e) {

				e.printStackTrace();
				System.out.println("NullPointerException ---->" + e.getMessage());
			} catch (CustomFault e) {
				e.printStackTrace();
				System.out.println(e.getMessage());
			}

		}

		iLogger.info("----- wrapAssetServiceScheduleImp Output-----");
		iLogger.info("wrapAssetServiceScheduleImp Output: " + responseList);

		return responseList;

	}

}
