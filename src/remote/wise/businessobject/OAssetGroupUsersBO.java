/**
 * 
 */
package remote.wise.businessobject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.Arrays;

import java.util.List;

import org.apache.logging.log4j.Logger;



import java.util.ArrayList;
import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;

/**
 * @author roopn5
 *
 */
public class OAssetGroupUsersBO {
	
	//DF20160115 @Roopa for Set AssetGroupUers Details into OrientDB
	//DF20171211: KO369761 - Removing OrienDB from the Production Completely.
	/*public String setOAssetGroupUsers(int groupId, List<String> ContactId, List<String> serialNumberList) throws CustomFault
	{
		String response = "SUCCESS";

		Logger fLogger = FatalLoggerClass.logger;

		Logger iLogger = InfoLoggerClass.logger;

		iLogger.info("----- OrientDB setOAssetGroupUsers Input-----");

		iLogger.info("OgroupId :"+groupId+","+"OContactId :"+ContactId+","+"OserialNumberList :"+serialNumberList);



		OrientAppDbDatasource connObj = new OrientAppDbDatasource();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs=null;
		int updateCount = 0;

		String finalQuery=null;

		String tempUserGroupData="";

		String newVin="";

		try{
			iLogger.info("AssetGroupUsers"+"setAssetGroupUsersDetails to OrientDB- START");
			conn = connObj.getConnection();
			iLogger.info("AssetGroupUsers:"+"setAssetGroupUsers to OrientDB- Got Connection");



			stmt = conn.createStatement();

			for(int i=0;i<ContactId.size();i++){

				for(int j=0;j<serialNumberList.size();j++){

					iLogger.info("ContactId :"+ContactId.get(i)+","+"Serial Number :"+serialNumberList.get(j));

					String query = "select @RID,AssetGroupData."+serialNumberList.get(j)+" from TAssetGroupUsers where UserID='"+ContactId.get(i)+"'";

					iLogger.info("AssetGroupUsers:"+"query :"+query);
					rs = stmt.executeQuery(query);

					int rsCount=rs.getFetchSize();

					iLogger.info("AssetGroupUsers:"+"rsCount :"+rsCount);

					if(rsCount==0){
						tempUserGroupData= "{"+"'"+"@type"+"'"+":"+"d"+","+"'"+serialNumberList.get(j)+"'"+":"+groupId+"}";

						finalQuery="insert into TAssetGroupUsers(UserID,AssetGroupData) values('"+ContactId.get(i)+"',"+tempUserGroupData+")";		
					}
					else{


						Object entry = rs.getObject(1);

						String rID = entry.toString();

						iLogger.info("AssetGroupUsers:"+"rID :"+rID);

						String columnTxnDetails="AssetGroupData";

						String UserGroupData=null;

						ResultSetMetaData rsmd1 = rs.getMetaData();
						int columns1 = rsmd1.getColumnCount();
						for (int x = 1; x <= columns1; x++) {
							if (columnTxnDetails.equals(rsmd1.getColumnName(x))) {
								UserGroupData= String.valueOf(rs.getObject(2));
							}
						}
						
						iLogger.info("AssetGroupUsers:"+"UserGroupData :"+UserGroupData);

						String UserGroupData ;

							try{

								UserGroupData= String.valueOf(rs.getObject(2));

								iLogger.info("AssetGroupUsers:"+"UserGroupData :"+UserGroupData);
							}
							catch(ArrayIndexOutOfBoundsException e){
								//e.printStackTrace();
								iLogger.info("OrientDB-AssetGroupUsers: UserGroupData data not found for the Serial Number :"+serialNumberList.get(j)+" "+e);
								UserGroupData=null;
								iLogger.info("AssetGroupUsers:"+"UserGroupData :"+UserGroupData);
							}
							catch(Exception e){
								iLogger.info("OrientDB-AssetGroupUsers: Exception:"+e);
								UserGroupData=null;
								iLogger.info("AssetGroupUsers:"+"UserGroupData :"+UserGroupData);
							}



						if(rID!=null && UserGroupData!=null){

							String currentGroupId= String.valueOf(groupId);

							List<String> tempList=new ArrayList<String>();

							String[] tempUserGroupDataList=UserGroupData.split(",");

							tempList=Arrays.asList(tempUserGroupDataList);

							if(! tempList.contains(currentGroupId)){

								tempUserGroupData= UserGroupData+","+String.valueOf(groupId);

								finalQuery="update TAssetGroupUsers set AssetGroupData."+serialNumberList.get(j)+"='"+tempUserGroupData+"' where UserID='"+ContactId.get(i)+"'";

							}





						}
						else if(rID!=null  && UserGroupData==null){

							tempUserGroupData= String.valueOf(groupId);

							finalQuery="update TAssetGroupUsers set AssetGroupData."+serialNumberList.get(j)+"='"+tempUserGroupData+"' where UserID='"+ContactId.get(i)+"'";
							//newVin="true";
						}	




					}

					iLogger.info("TAssetGroupUsers finalQuery :"+finalQuery);
					//fix for issue while adding new key to the existing json : java.sql.SQLException: Connection is not associated with a managed connection.org.jboss.jca.adapters.jdbc.jdk6.WrappedConnectionJDK6@ebf5a1
					if(newVin.equalsIgnoreCase("true")){
						iLogger.info("TAssetGroupUsers newVin :"+newVin);

						try{

							conn.close();

							stmt.close();

							conn = connObj.getConnection();

							stmt = conn.createStatement();
						}
						catch(Exception e){
							fLogger.error("TAssetGroupUsers newVin connection Exception:"+e);
						}

					}
					if(finalQuery!=null){

						updateCount=stmt.executeUpdate(finalQuery);	
					}

					iLogger.info("TAssetGroupUsers updateCount :"+updateCount);


				}
			}







		}
		catch(Exception e)
		{
			//e.printStackTrace();
			fLogger.error("TAssetGroupUsers Exception:"+e);

			//Error in getting OrientDB session
			fLogger.error("TAssetGroupUsers:"+"Exception in getting OrientDB Connection:"+e.getMessage());
			response="Failure";
		}

		finally
		{
			iLogger.info("OrientDB-AssetGroupUsers:Close Connection - START");
			try
			{



				if(rs!=null)
					rs.close();

				if(stmt!=null)
					stmt.close();

				if(conn!=null)
					conn.close();



			}
			catch(Exception e)
			{
				iLogger.info("Problem in closing the connection");
				//e.printStackTrace();
				fLogger.error("Orient AssetGroupUsers:"+"Exception in closing OrientDB Connection:"+e.getMessage());
			}
			iLogger.info("OrientDB-AssetGroupUsers:Close Connection - END");

		}


		return response;

	}*/

}
