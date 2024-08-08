package remote.wise.service.implementation;
/**
 * @author Z1007653
 * Class DealerOutletImpl contains the business logic of DealerOutlet Service 
 */
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.DealerOutletOutputContract;
import remote.wise.util.ConnectMySQL;

public class DealerOutletImpl {
	
	/**
	 * @method getDealerOutletLocationData provide dealer outlet location details from table: dealer_outlet_mapping
	 * @param category: holds outlet type codes
	 * @return List<DealerOutletOutputContract>
	 */
	public List<DealerOutletOutputContract> getDealerOutletLocationData(List<String> category) {
		
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		
		List<DealerOutletOutputContract> result = new ArrayList<DealerOutletOutputContract>();
		DealerOutletOutputContract data = new DealerOutletOutputContract();
		String inQuery = category.toString().replace("[", "(").replace("]", ")");
		
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;
		
		try {
			ConnectMySQL connMySql = new ConnectMySQL();
			connection = connMySql.getConnection();
			statement = connection.createStatement();
			
			String selectQuery = "SELECT dealer, outlet_type, outlet_type_code, address, location, lat, lon  FROM dealer_outlet_mapping ";
			if(category.size() > 0) selectQuery += "WHERE outlet_type_code in " + inQuery;
			
			rs = statement.executeQuery(selectQuery);
			while(rs.next())
			{
				data = new DealerOutletOutputContract();
				data.setDealerName(rs.getString("dealer"));
				data.setOutletType(rs.getString("outlet_type"));
				data.setOutletTypeCode(rs.getString("outlet_type_code"));
				data.setAddress(rs.getString("address").replace("–", ""));
				data.setLocation(rs.getString("location"));
				data.setLat(rs.getString("lat"));
				data.setLon(rs.getString("lon"));
				
				result.add(data);
			}
		} catch (Exception e) {
			Writer writer = new StringWriter();
			PrintWriter ps = new PrintWriter(writer);
			e.printStackTrace(ps);
			fLogger.fatal("DealerOutletImpl : getDealerOutletLocationData : Exception caught\n" + ps.toString());
		} finally {
			try{
				if(rs != null)
					rs.close();
				if(statement != null)
					statement.close();
				if(connection != null)
					connection.close();
			}catch (Exception e) {
				fLogger.fatal("DealerOutletImpl : getDealerOutletLocationData : Exception caught while closing connections - " + e.getMessage());
			}
		}
		
		iLogger.info("DealerOutletImpl : getDealerOutletLocationData : returning data of size : " + result.size());
		return result;
	}
}
