package remote.wise.service.implementation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.logging.log4j.Logger;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.RolledOffMachinesOnMapRespContract;
import remote.wise.util.ConnectMySQL;

public class RolledOffMachinesOnMapImpl {
	
	public List<RolledOffMachinesOnMapRespContract> getRolledOffMachinesData(String accountIdLlist,Date installedDate) {
	
	Logger fLogger = FatalLoggerClass.logger;
	Logger iLogger = InfoLoggerClass.logger;
	ConnectMySQL connMySql = new ConnectMySQL();
	Connection conn = null;
	Statement stmt = null;
	ResultSet rs = null;
	
	List<RolledOffMachinesOnMapRespContract> listOfRolledoffMachines=new ArrayList<RolledOffMachinesOnMapRespContract>();
	iLogger.info("RolledOffMachinesOnMapImpl::getRolledOffmachinesData:accountIdLlist :"+accountIdLlist+"  :installedDate:  "+installedDate);
	try{
		conn = connMySql.getConnection();
		stmt = conn.createStatement();
	java.sql.Date sqldate=new  java.sql.Date(installedDate.getTime());
		
	String queryString ="select a.serial_number,a.zone,a.dealerCode,a.dealer_name, "+
	" a.bp_code,a.CustomerName,a.Model,a.Profile,a.Roll_off_date,a.Installed_date,a.Lat,a.Lon,a.tmh, "+
	" a.version,a.City,a.State "+
	" FROM com_rep_oem_enhanced a, asset_owner_snapshot b "+
	" where a.serial_number=b.serial_number "+
	" and b.account_id in"+"("+accountIdLlist+") "+ " and a.Installed_date<"+"'"+sqldate+"'";
	
	rs= stmt.executeQuery(queryString);

	iLogger.info("RolledOffMachinesOnMapImpl::getRolledOffmachinesData: query ... "+queryString);
	while(rs.next())
	{
		RolledOffMachinesOnMapRespContract contract=new RolledOffMachinesOnMapRespContract();
		contract.setSerialNumber(rs.getString("Serial_Number"));
		contract.setZone(rs.getString("zone"));
		contract.setDealerCode(rs.getString("dealerCode"));
		contract.setDealerName(rs.getString("dealer_name"));
		contract.setCustomerBPCode(rs.getString("bp_code"));
		contract.setCustomerName(rs.getString("CustomerName"));
		contract.setModelCode(rs.getString("Model"));
		contract.setProfile(rs.getString("Profile"));
		//contract.setRollOffDate(new java.util.Date(rs.getDate("Roll_off_date").getTime()));
		//contract.setInstallationDate(new java.util.Date(rs.getDate("Installed_date").getTime()));
		contract.setRollOffDate(rs.getDate("Roll_off_date").toString());
		contract.setInstallationDate(rs.getDate("Installed_date").toString());
		contract.setLatitude(rs.getString("Lat"));
		contract.setLongitude(rs.getString("Lon"));
		contract.setMachineHours(rs.getString("tmh"));
		contract.setFwVersion(rs.getString("version"));
		contract.setCity(rs.getString("city"));
		contract.setState(rs.getString("State"));
		
		listOfRolledoffMachines.add(contract);
	}
	iLogger.info("RolledOffMachinesOnMapImpl::getRolledOffmachinesData: size ... "+listOfRolledoffMachines.size());
	}
	catch(Exception e)
	{
		System.out.println("error ..."+e.toString());
		fLogger.fatal(" Exception in RolledOffMachinesOnMapImpl:"+e);
		
	}finally{
		if(rs!=null)
			try {
				rs.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		
		if(stmt!=null)
			try {
				stmt.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	
	}
	return listOfRolledoffMachines;
}
	
}