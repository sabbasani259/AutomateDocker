/*DF-100000266: 20220307: DH20313904: Alert Preference Update in table MUserAlertPref. 
 *Event Type Id(Integer) getting updated in table instead of event type code(String).
 *Changes added to update table with correct data type.
 *DF-100000266: 20220714: DH20313904: Alert Preference Update in table MUserAlertPref. 
 */

package remote.wise.businessobject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;

import com.wipro.mcoreapp.businessobject.UserPreferenceBO;
import remote.wise.businessentity.AccountEntityPOJO;
import remote.wise.handler.SmsHandler;
import remote.wise.handler.SmsTemplate;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.UserAlertPreferenceRespContract;
import remote.wise.util.CommonUtil;
import remote.wise.util.ConnectMySQL;

public class PendingTenancyBO 
{
	public String createPendingTenancy(AccountEntityPOJO accountObj)
	{
		String status = "SUCCESS";
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;

		Connection con=null;
		Statement stmt=null;
		PreparedStatement pstmt=null;
		ResultSet rs = null;
		//Deepthi: Added LastUpdatedTime to Contact Table : 20210927:AwakeId : 5292
		String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

		try
		{
			if(accountObj.getAccount_name().contains("'"))
			{
				String accountName = accountObj.getAccount_name().replaceAll("'", "");
				accountObj.setAccount_name(accountName);
			}
			if(accountObj.getAccount_name().contains("\""))
			{
				String accountName = accountObj.getAccount_name().replaceAll("\"", "");
				accountObj.setAccount_name(accountName);
			}

			String currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			con = new ConnectMySQL().getConnection();
			stmt = con.createStatement();

			//----------------------- STEP1: Check if this is a customer account
			String query= "select * from customer_master where Customer_code='"+accountObj.getAccountCode()+"'";
			rs = stmt.executeQuery(query);
			int customerAcc=0;
			while(rs.next())
			{
				customerAcc=1;
			}
			if(customerAcc==0)
			{
				fLogger.fatal("PendingTenancyBatchService:PendingTenancyBO:AccountID:"+accountObj.getAccount_id()+"; This is not a customer account and hence not proceeding further");
				return "FAILURE";
			}

			//----------------------- STEP2: Get Parent tenancy Id
			int parentTenancyID=0;
			String parentTenancyName=null;
			query = "select a.Tenancy_ID,b.Tenancy_Name from account_tenancy a, tenancy b where a.Account_ID="+accountObj.getParent_account_id()+" " +
					" and a.Tenancy_ID=b.Tenancy_ID ";
			rs = stmt.executeQuery(query);
			while(rs.next())
			{
				parentTenancyID=rs.getInt("Tenancy_ID");
				parentTenancyName=rs.getString("Tenancy_Name");
			}
			if(parentTenancyID==0)
			{
				fLogger.fatal("PendingTenancyBatchService:PendingTenancyBO:AccountID:"+accountObj.getAccount_id()+"; No parent tenancy mapping found for parent account:"+accountObj.getParent_account_id());
				return "FAILURE";
			}

			//-------------------------- STEP3: Check for Duplicate Tenancy
			int newTenancyId=0;
			query = "select Tenancy_ID from tenancy where Tenancy_Code='"+accountObj.getAccountCode()+"' and Client_ID=1 and Tenancy_Type_ID=4" +
					" and Parent_Tenancy_ID="+parentTenancyID;
			rs = stmt.executeQuery(query);
			while(rs.next())
			{
				newTenancyId=rs.getInt("Tenancy_ID");
			}

			//----------------------- STEP4: Create a new Tenancy
			if(newTenancyId==0)
			{
				/*query = "INSERT INTO tenancy(Tenancy_Name,Parent_Tenancy_Name,Parent_Tenancy_ID,Client_ID,Tenancy_Type_ID,CreatedBy," +
						"CreatedDate,Tenancy_Code,mapping_code) values ('"+accountObj.getAccount_name()+"-"+accountObj.getAccountCode()+"', " +
						"'"+parentTenancyName+"', "+parentTenancyID+", 1, 4, 'PTBatch', '"+currentDateTime+"', '"+accountObj.getAccountCode()+"', " +
						"'"+accountObj.getMappingCode()+"')";*/
				query = "INSERT INTO tenancy(Tenancy_Name,Parent_Tenancy_Name,Parent_Tenancy_ID,Client_ID,Tenancy_Type_ID,CreatedBy," +
						"CreatedDate,Tenancy_Code,mapping_code) values (?,?,?,?,?,?,?,?,?) ";
				pstmt = con.prepareStatement(query,Statement.RETURN_GENERATED_KEYS);
				pstmt.setString(1, accountObj.getAccount_name()+"-"+accountObj.getAccountCode());
				pstmt.setString(2, parentTenancyName);
				pstmt.setInt(3, parentTenancyID);
				pstmt.setInt(4, 1);
				pstmt.setInt(5, 4);
				pstmt.setString(6, "PTBatch");
				Timestamp currentTimeStamp = new Timestamp(new Date().getTime());
				pstmt.setTimestamp(7, currentTimeStamp);
				pstmt.setString(8, accountObj.getAccountCode());
				pstmt.setString(9, accountObj.getMappingCode());

				int rowAffected = pstmt.executeUpdate();
				if(rowAffected == 1)
				{
					rs = pstmt.getGeneratedKeys();
				}
				if (rs.next()) 
				{
					newTenancyId = rs.getInt(1);
				}
				else
				{
					//Get the tenancy ID of newly created tenancy
					query = "select Tenancy_ID from tenancy where Tenancy_Code='"+accountObj.getAccountCode()+"' and Client_ID=1 and Tenancy_Type_ID=4" +
							" and Parent_Tenancy_ID="+parentTenancyID;
					rs = stmt.executeQuery(query);
					while(rs.next())
					{
						newTenancyId=rs.getInt("Tenancy_ID");
					}
				}
				iLogger.info("PendingTenancyBatchService:PendingTenancyBO:AccountID:"+accountObj.getAccount_id()+"; Tenancy creation:SUCCESS");
			}

			//------------------------ STEP5: Account Tenancy Mapping
			if(newTenancyId==0)
			{
				fLogger.fatal("PendingTenancyBatchService:PendingTenancyBO:AccountID:"+accountObj.getAccount_id()+"; Error in creating new Tenancy");
				return "FAILURE";
			}
			query = "INSERT INTO account_tenancy(Account_ID,Tenancy_ID) values ("+accountObj.getAccount_id()+", "+newTenancyId+")";
			stmt.executeUpdate(query);
			iLogger.info("PendingTenancyBatchService:PendingTenancyBO:AccountID:"+accountObj.getAccount_id()+"; Account Tenancy Mapping:SUCCESS");

			//------------------------ STEP6: Validate mobile number for user creation
			if(accountObj.getMobile_no()==null)
			{
				fLogger.fatal("PendingTenancyBatchService:PendingTenancyBO:AccountID:"+accountObj.getAccount_id()+"; " +
						"Contact cannot be created as Mobile number is NULL");
				return status;
			}

			query = "select Contact_ID from contact where Status=1 and Primary_Moblie_Number='"+accountObj.getMobile_no()+"'";
			rs = stmt.executeQuery(query);
			String existingContactId=null;
			if(rs.next())
			{
				existingContactId = rs.getString("Contact_ID");
			}

			if(existingContactId!=null)
			{
				String faultDetailsQuery = null;
				String lastUpdatedTime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

				//Insert / Update ContactFaultDetails for future reference
				query = "select * from ContactFaultDetails where AccountID='"+accountObj.getAccount_id()+"' and MobileNumber='"+accountObj.getMobile_no()+"'";
				rs = stmt.executeQuery(query);
				if(rs.next())
				{
					faultDetailsQuery = "update ContactFaultDetails set LastUpdatedTime='"+lastUpdatedTime+"',ErrorSource='PendingTenancyCreation'" +
							" where AccountID='"+accountObj.getAccount_id()+"' and MobileNumber='"+accountObj.getMobile_no()+"'";
				}
				else
				{
					faultDetailsQuery =" INSERT INTO ContactFaultDetails(AccountID,MobileNumber,ExistingContactID,CreatedTime,LastUpdatedTime,ErrorSource) values" +
							" ("+accountObj.getAccount_id()+", '"+accountObj.getMobile_no()+"', '"+existingContactId+"', '"+lastUpdatedTime+"', " +
							"'"+lastUpdatedTime+"', 'PendingTenancyCreation')";
				}

				stmt.executeUpdate(faultDetailsQuery);

				fLogger.fatal("PendingTenancyBatchService:PendingTenancyBO:AccountID:"+accountObj.getAccount_id()+"; " +
						"Contact cannot be created as Mobile number already exists; ContactID:"+existingContactId+"; Mobile no:"+accountObj.getMobile_no());
				return status;

			}

			//------------------------ STEP7: Create new Tenancy Admin and send email notification
			//------------ Generate loginID
			if(accountObj.getCountryCode()==null)
				accountObj.setCountryCode("+91");

			String accountName = accountObj.getAccount_name();
			if(accountName==null)
			{
				fLogger.fatal("PendingTenancyBatchService:PendingTenancyBO:AccountID:"+accountObj.getAccount_id()+"; " +
						"Contact cannot be created as Account name is NULL");
				return status;
			}

			String userName = accountName.replaceAll("[-+/.^:,$&!#@*]","");
			userName = StringUtils.deleteWhitespace(userName);
			userName=userName.toLowerCase();
			if(userName==null || userName.length()<4)
			{
				userName=userName+userName+userName;
			}

			String firstName = StringUtils.deleteWhitespace(accountName);
			firstName=firstName.toLowerCase();
			String uname = userName.substring(0, 4);
			String uid = null;
			String newContactID = null;
			int loopVar = 0;

			for(loopVar = 0;loopVar<3;loopVar++)
			{
				Random r = new Random();
				long nextLong = Math.abs(r.nextLong());
				uid = String.valueOf(nextLong).substring(0, 6);
				newContactID = uname+uid;

				//Check if this contactId already exists
				query = "select * from contact where contact_id='"+newContactID+"'";
				rs = stmt.executeQuery(query);
				int present=0;
				if(rs.next())
				{
					present=1;
				}

				if(present==0)
				{
					break;
				}
			}

			//After 3 attempts, if there is no unique id generation, throwing exception
			if(loopVar==3)
			{
				fLogger.fatal("PendingTenancyBatchService:PendingTenancyBO:AccountID:"+accountObj.getAccount_id()+"; " +
						"Contact cannot be created - Failed in getting unique contactId even after 3 attempts");
				return status;
			}

			//------------ Generate password
			String validChars = "abcdefghijklmnopqrstuvwxyz";
			String validCapital = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
			String password = "";
			String validSpecial = "*#@!";
			String numeric = "1234567890";
			Random generator = new Random();

			for (int i = 0; i < 7; i++) 
			{
				password += validChars.charAt(generator.nextInt(validChars.length()));
			}

			password += validSpecial.charAt(generator.nextInt(validSpecial.length()));

			password += numeric.charAt(generator.nextInt(numeric.length()));
			password += validCapital.charAt(generator.nextInt(validCapital.length()));

			for (int i = 0; i < 2; i++) 
			{
				password += validChars.charAt(generator.nextInt(validChars.length()));
			}
			newContactID = newContactID.toLowerCase();

			String insertQuery="INSERT INTO contact(First_Name,Is_Tenancy_Admin,Contact_ID,Role_ID,Password,sys_gen_password," +
					"Primary_Moblie_Number,countrycode,Primary_Email_ID,TimeZone,Language,Status,Client_ID,LastUpdatedTime) VALUES " +
					"('"+firstName+"',1,'"+newContactID+"',7,AES_ENCRYPT('"+password+"','"+accountObj.getMobile_no()+"')," +
					"1,'"+accountObj.getMobile_no()+"','"+accountObj.getCountryCode()+"','"+accountObj.getEmailId()+"'," +
					"'"+accountObj.getTimeZone()+"','English',1,1,'"+currentDate+"')";

			System.out.println("insertQuery" + insertQuery);
			iLogger.info("insertQuery in Customercreation" + insertQuery);
			stmt.executeUpdate(insertQuery);
			iLogger.info("PendingTenancyBatchService:PendingTenancyBO:AccountID:"+accountObj.getAccount_id()+"; Contact Creation:SUCCESS: ContactID:"+newContactID);


			if(accountObj.getEmailId()!=null && accountObj.getEmailId().trim().length()>0)
			{
				new UserDetailsBO().sendMailToUser(accountObj.getEmailId(), newContactID, password);
			}

			if(accountObj.getMobile_no()!=null && accountObj.getMobile_no().trim().length()>0 ){

				if(accountObj.getCountryCode()!=null && !accountObj.getCountryCode().contains("+"))
				{
					accountObj.setCountryCode("+"+accountObj.getCountryCode());
				}

				List<String> toList = new ArrayList<String>();
				toList.add(accountObj.getCountryCode()+accountObj.getMobile_no());

				String body = "Thanks for registering with JCB. Pls login to jcblivelink.in with Login ID: "+newContactID+" Password: "+password;
				body = body + " . JCB LiveLink Team.";
				List<String> msgBody = new ArrayList<String>();
				msgBody.add(body);

				SmsTemplate smsTemplate = new SmsTemplate();			
				smsTemplate.setTo(toList);
				smsTemplate.setMsgBody(msgBody);

				new SmsHandler().handleSmsInKafka("SMSQueue", smsTemplate,0);
			}


			//------------------------ STEP5: Account contact mapping
			query = "INSERT INTO account_contact(Account_ID,Contact_ID) values ("+accountObj.getAccount_id()+", '"+newContactID+"' )";
			stmt.executeUpdate(query);
			iLogger.info("PendingTenancyBatchService:PendingTenancyBO:AccountID:"+accountObj.getAccount_id()+"; Account contact mapping - SUCCESS");

			//----------------------- STEP6: Set alert preference for tenancy admin
			query = "select * from event_type";
			rs = stmt.executeQuery(query);
			LinkedList<UserAlertPreferenceRespContract> userAlertPrefList = new LinkedList<UserAlertPreferenceRespContract>();
			while(rs.next())
			{
				UserAlertPreferenceRespContract response= new UserAlertPreferenceRespContract();
				response.setEmailEvent(true);

				//DF-100000266.o
				//response.setEventTypeId(rs.getInt("Event_Type_Code"));
				//DF-100000266.n

				//DF-100000266.sn
				response.setEventTypeCode(rs.getString("Event_Type_Code"));
				//DF-100000266.en

				response.setEventTypeName(rs.getString("Event_Type_Name"));
				response.setLoginId(newContactID);
				response.setRoleName("Customer Fleet Manager");
				//DF-100000266.sn
				// For utilization and landmark alert sms alert not required
				if (rs.getString("Event_Type_Code").equals("003") || rs.getString("Event_Type_Code").equals("005"))
					response.setSMSEvent(false);
				else
					response.setSMSEvent(true);
				//DF-100000266.en

				userAlertPrefList.add(response);
			}
			//DF-100000266.sn
			UserDetailsBO userDetBo = new UserDetailsBO();
			iLogger.info("UserAlertPreferenceImpl: Set Details in MySQL - Start");
			String flag = userDetBo.setUserAlertPreference(userAlertPrefList);	
			iLogger.info("UserAlertPreferenceImpl: Set Details in MySQL - End");
			//DF-100000266.en

			HashMap<Boolean,String> boolToStringMap = new HashMap<Boolean,String>();
			boolToStringMap.put(true, "1");
			boolToStringMap.put(false, "0");

			HashMap<String,String> smsAlertPrefMap = new HashMap<String,String>();
			HashMap<String,String> emailAlertPrefMap = new HashMap<String,String>();
			for(int i=0; i<userAlertPrefList.size(); i++)
			{
				//DF-100000266.o
				//smsAlertPrefMap.put(String.valueOf(userAlertPrefList.get(i).getEventTypeId()), boolToStringMap.get(userAlertPrefList.get(i).isSMSEvent()));
				//emailAlertPrefMap.put(String.valueOf(userAlertPrefList.get(i).getEventTypeId()), boolToStringMap.get(userAlertPrefList.get(i).isEmailEvent()));
				//DF-100000266.e

				//DF-100000266.sn
				smsAlertPrefMap.put(userAlertPrefList.get(i).getEventTypeCode(), boolToStringMap.get(userAlertPrefList.get(i).isSMSEvent()));
				emailAlertPrefMap.put(userAlertPrefList.get(i).getEventTypeCode(), boolToStringMap.get(userAlertPrefList.get(i).isEmailEvent()));
				//DF-100000266.en
			}

			//DF-100000266.o
			/*new UserPreferenceBO().setUserAlertPrefToMySql(newContactID, "SMS", "Alert Type", smsAlertPrefMap);
			new UserPreferenceBO().setUserAlertPrefToMySql(newContactID, "Email", "Alert Type", emailAlertPrefMap);*/
			//DF-100000266.sn
			new UserPreferenceBO().setUserAlertPrefToMySql(newContactID, "SMS", "AlertType", smsAlertPrefMap);
			new UserPreferenceBO().setUserAlertPrefToMySql(newContactID, "Email", "AlertType", emailAlertPrefMap);
			//DF100000266.en

			iLogger.info("PendingTenancyBatchService:PendingTenancyBO:AccountID:"+accountObj.getAccount_id()+"; Set User Alert Pref - SUCCESS");

			//----------------------- STEP7: Set subscriber3 in notification subscribers
			//Get the list of VINs tagged to this account
			query = "select Serial_Number from asset where Primary_Owner_ID="+accountObj.getAccount_id();
			rs = stmt.executeQuery(query);
			LinkedList<String> assetList = new LinkedList<String>();
			while(rs.next())
			{
				assetList.add(rs.getString("Serial_Number"));
			}

			if(assetList.size()==0)
			{
				fLogger.fatal("PendingTenancyBatchService:PendingTenancyBO:AccountID:"+accountObj.getAccount_id()+"; No Machines tagged to this account." +
						"Hence Subscriber3 not set");
				//return "FAILURE";
			}

			for(int i=0; i<assetList.size(); i++)
			{

				//Set Subscriber3 in MAlertSubscribers
				query = "update MAlertSubscribers SET Details = json_set(Details,'$.\"3\"', \""+newContactID+"\" ) where AssetID='"+assetList.get(i)+"'";
				stmt.executeUpdate(query);
				iLogger.info("PendingTenancyBatchService:PendingTenancyBO:AccountID:"+accountObj.getAccount_id()+"; Set Subscriber3 in MAlertSubscribers - SUCCESS");

				//Set Subscriber3 in MAlertSubsriberGroup
				query= "update MAlertSubsriberGroup set SubscriberGroup = json_set(SubscriberGroup,'$.\"Subscriber3\"'," +
						"CAST('{\"SMS1\":\""+newContactID+"\",\"EMAIL1\":\""+newContactID+"\" }' as json)) where AssetID='"+assetList.get(i)+"' ";
				stmt.executeUpdate(query);
				iLogger.info("PendingTenancyBatchService:PendingTenancyBO:AccountID:"+accountObj.getAccount_id()+"; Set Subscriber3 in MAlertSubsriberGroup - SUCCESS");
			}
		}
		catch(Exception e)
		{
			status="FAILURE";
			e.printStackTrace();
			fLogger.fatal("PendingTenancyBatchService:PendingTenancyBO:Exception:"+e.getMessage());
		}
		finally
		{
			try
			{
				if(stmt!=null)
					stmt.close();
				if(pstmt!=null)
					pstmt.close();
				if(con!=null)
					con.close();
			}
			catch(Exception e)
			{
				fLogger.fatal("PendingTenancyBatchService:PendingTenancyBO:Exception in closing the connection:"+e.getMessage(),e);
			}
		}
		return status;
	}
}
