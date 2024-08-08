package remote.wise.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessentity.AssetControlUnitEntity;
import remote.wise.log.InfoLogging.InfoLoggerClass;

/**
 * @author Ajay Kumar
 *
 */
public class AssetUtil {

	static Session session = null;
	static Logger iLogger = InfoLoggerClass.logger;
	private static ConnectMySQL connFactory = new ConnectMySQL();

	
	//this method will get the serialNo/VinNo using the machine no
	public static String getVinNoUsingMachineNo(String machineNumber) {

		String serialNum=null;
		try
		{
		session = HibernateUtil.getSessionFactory().openSession();
		Query query = session
				.createQuery("select serial_number from AssetEntity where Machine_Number ='"
						+ machineNumber + "'");
		Iterator itr = query.list().iterator();

		while (itr.hasNext()) {
			 serialNum = ((AssetControlUnitEntity) itr.next()).getSerialNumber();
		}
		}catch(Exception e){
			iLogger.info("Issue : "+e.getMessage());
		}finally{
			session.close();
		}
		if (serialNum == null)
			return "No VINs found for the machine : "+machineNumber;
		else
			return serialNum;
	}
	
	//
	public static boolean checkIfVinExists(String vins) {

		List<String> resultVinList=new ArrayList<String>();
		String[] vinArr=vins.split(",");
		List<String> vinList = Arrays.asList(vinArr);
		boolean isAllVinPresent=true;
		
		
		
		String query="select Serial_Number from asset_monitoring_snapshot" +
				" where Serial_Number in("+vins+")";
		iLogger.info(query);
		
		try (Connection connection = connFactory.getConnection();
				PreparedStatement statement = connection.prepareStatement(query);
				ResultSet rs = statement.executeQuery(query);) {
		
			
			while (rs.next()) {
				 resultVinList.add(rs.getString("Serial_Number"));
			}
			
			if (resultVinList != null)
			{
				if(vinList!=null){
				if(resultVinList.size()!=vinList.size())
					isAllVinPresent=false;
				}
			}

			
		}catch(Exception e){
			e.printStackTrace();
		}
			return isAllVinPresent;
	}
	
	public static String getFormatedVinsToQuery(List<Integer> tenancyList) {
		// to add comma in the list after each element
		String ten = "";
		if (tenancyList.size() > 0) {
			StringBuilder sb = new StringBuilder();
			for (Integer tenancy : tenancyList)
				sb.append(tenancy).append(",");
			ten = sb.deleteCharAt(sb.length() - 1).toString();
		}
		return ten;
	}
	
	public static String getIDListAsCommaSeperated(List<Integer> idList) {
		ListToStringConversion conversion = new ListToStringConversion();
		String IdListAsString = conversion.getIntegerListString(idList).toString();
		return IdListAsString;
	}
}



