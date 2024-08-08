package com.wipro.MachineBlocking;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.json.simple.JSONObject;

import remote.wise.businessentity.AssetEntity;
import remote.wise.businessentity.AssetEventEntity;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.HibernateUtil;

@Path("/getMachineRenewalStatus")
public class GetMachineRenewalStatus 
{
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getMachineRenewalStatus(@QueryParam("vin")String vin)
	{
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		String status="";
		Session session=null;

		HashMap<String, String> machineRenewalDetails = new LinkedHashMap<String, String>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try
		{
			iLogger.info("getMachineRenewalStatus VIN : "+vin);
			session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();

			String addToQuery="";
			if(vin.length()==7) {addToQuery="like '%"+vin+"'";}
			else{addToQuery="='"+vin+"'";}

			Query getQuery = session.createQuery("SELECT renewal_flag,renewal_date FROM AssetEntity where serial_number "+addToQuery);

			iLogger.info("Executing query: "+getQuery.getQueryString());

			//			AssetEntity asset=null;
			@SuppressWarnings("rawtypes")
			Iterator itr = getQuery.list().iterator();
			Object[] resultQ = null;
			while(itr.hasNext())
			{
				resultQ = (Object[]) itr.next();
				//				AssetEntity assetEvent = (AssetEntity) itr.next();

				//				if(assetEvent.getRenewal_date()!=null)
				//				{
				//					machineRenewalDetails.put("renewal_date", sdf.format(assetEvent.getRenewal_date()));
				//				}
				//				else
				//				{
				//					machineRenewalDetails.put("renewal_date", "NA");
				//				}
				//
				//				machineRenewalDetails.put("renewal_flag",Integer.toString(assetEvent.getRenewal_flag()));

				//				iLogger.info("resultQ : "+resultQ[0].toString()+" :: "+resultQ[1].toString());
				String renewalDate = "";
				if(resultQ[1]==null)
				{

					machineRenewalDetails.put("renewal_date", "NA");
				}
				else
				{
					renewalDate = resultQ[1].toString();
					machineRenewalDetails.put("renewal_date", renewalDate);
				}

				String renewalFlag = resultQ[0].toString();
				machineRenewalDetails.put("renewal_flag",renewalFlag);

				iLogger.info("machineRenewalDetails : "+machineRenewalDetails);

			}

		}catch(Exception e)
		{
			e.printStackTrace();

			return "FAILURE";
		}
		finally
		{
			if(session.isOpen())
				if(session.getTransaction().isActive())
				{
					session.getTransaction().commit();
				}	
		}
		return new JSONObject(machineRenewalDetails).toJSONString();
	}

}
