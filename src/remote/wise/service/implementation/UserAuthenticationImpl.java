package remote.wise.service.implementation;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import remote.wise.util.CommonUtil;
import remote.wise.util.ConnectMySQL;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
////import org.apache.log4j.Logger;
import remote.wise.businessentity.AccountEntity;
import remote.wise.businessobject.TenancyBO;
import remote.wise.businessobject.UserDetailsBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.UserAuthenticationReqContract;
import remote.wise.service.datacontract.UserAuthenticationRespContract;
//import remote.wise.util.WiseLogger;
import remote.wise.util.CommonUtil;
import remote.wise.util.HibernateUtil;

public class UserAuthenticationImpl 
{	
	//Defect Id 1337 - Logger changes
	//public static WiseLogger businessError = WiseLogger.getLogger("UserAuthenticationImpl:","businessError");
	//static Logger businessError = Logger.getLogger("businessErrorLogger");

	String user_name;
	String loginId;
	String role_name;
	int role_id;
	int account_id;
	int client_id;
	String last_login_date;
	int isTenancyAdmin;
	boolean isSMS;
	boolean isMap;
	HashMap<String, HashMap<Integer,String>> tenancyNameIDProxyUser = new HashMap<String, HashMap<Integer,String>>();
	

	/**
	 * method to authenticate the user
	 * @param reqContract
	 * @return UserAuthenticationRespContract
	 * @throws CustomFault
	 * @throws IOException 
	 */

	public UserAuthenticationRespContract authenticateUser(UserAuthenticationReqContract reqContract) throws CustomFault, IOException
	{
		UserAuthenticationRespContract response = null;
		Logger bLogger = BusinessErrorLoggerClass.logger;

		Logger iLogger = InfoLoggerClass.logger;

		int counter=0;

		int loggedInUserTenId=0;
		//get details from InputContract
		String login_id = reqContract.getLogin_id();
		String password = reqContract.getPassword();
		// defetc Id1200: QA Server issue**

		//validating login_id text
		//DF20171011: KO369761 - Security Check added for input text fields.
		CommonUtil util = new CommonUtil();
		String isUserValid = util.inputFieldValidation(login_id);

		if(!isUserValid.equals("SUCCESS")){
			throw new CustomFault(isUserValid);
		}

		isUserValid = util.inputFieldValidation(password);
		if(!isUserValid.equals("SUCCESS")){
			throw new CustomFault(isUserValid);
		}
		//---------------Get User Details from UserDetailsBO----------------------
		UserDetailsBO userDetails = new UserDetailsBO();
		//Df20171010 @Roopa checking if the error counter > 5 returning the msg
		counter=new CommonUtil().getInvalidCredCounter(login_id);

		if(counter>5){
			//DF20180817-MA369757 -Introducing lockedOutTime for a user for unlocking a locked account.
			Calendar cal1 = Calendar.getInstance();
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String timeStamp = sdf1.format(cal1.getTime());
			String lockTimeQuery="update contact set lockedOutTime=\'"+timeStamp+"\' where contact_id=\'"+login_id+"\'";
			iLogger.info("lockTimeQuery "+lockTimeQuery);
			String result=new CommonUtil().insertData(lockTimeQuery);
			iLogger.info("Status of updating the lockedOutTime for the user "+login_id+" :: "+result);

			//throw new CustomFault("Your Account has been locked after 5 invalid attempts. Please try again tomorrow.");

			//DF20180713-KO369761 - Changing the custom message for security purpose.
			throw new CustomFault("Please enter a valid Username/Password.");
		}

		//Df20170921 @Roopa  AES password encryption changes

		String storedPwd=new CommonUtil().getDecryptedPassword(login_id);


		if(storedPwd!=null){
			if(storedPwd.equals(password)){

				password=new CommonUtil().getEncryptedPassword(login_id);

				userDetails=userDetails.getUserDetails(login_id, password);
				
				// custom fault has given on 9/17/13 by shrini
				//Validate for incorrect loginID or password
				if(userDetails.getError_msg()!=null)
				{
					bLogger.error(userDetails.getError_msg());




					//Df20171009 @Roopa updating the errorCounter in case of wrong credentials
					try{
						String query="update contact set errorLogCounter=errorLogCounter+1 where contact_id='"+login_id+"'";

						String result=new CommonUtil().insertData(query);

						iLogger.info("Incrementing the errorCounter since invalid password:: '"+login_id+"'"+result);
					}
					catch(Exception e){
						e.printStackTrace();
					}

					counter=new CommonUtil().getInvalidCredCounter(login_id);

					if(counter>5){
						//DF20180817-MA369757 -Introducing lockedOutTime for a user for unlocking a locked account.
						Calendar cal1 = Calendar.getInstance();
						SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						String timeStamp = sdf1.format(cal1.getTime());
						String lockTimeQuery="update contact set lockedOutTime=\'"+timeStamp+"\' where contact_id=\'"+login_id+"\'";
						iLogger.info("lockTimeQuery "+lockTimeQuery);
						String result=new CommonUtil().insertData(lockTimeQuery);
						iLogger.info("Status of updating the lockedOutTime for the user "+login_id+" :: "+result);
						//throw new CustomFault("Your Account has been locked after 5 invalid attempts. Please try again tomorrow.");

						//DF20180713-KO369761 - Changing the custom message for security purpose.
						throw new CustomFault("Please enter a valid Username/Password.");
					}
					else{
						throw new CustomFault("The Credentials is incorrect.");
					}
				}

				//get details
				else
				{
					//To set the activity log for user login details
					//DefectId:1200 - Modified by Rajani Nagaraju - QA server going down
					userDetails.setActivityLog(login_id);

					//get tenancy details
					TenancyBO tenancy = new TenancyBO();
					tenancy= tenancy.getUserTenancy(login_id, userDetails.getAccount_id());	

					//set user data into Outputcontract object
					response=new UserAuthenticationRespContract();
					//Keerthi : 05/03/2014 : sending last name as empty, if its null : ID : 2102
					if(userDetails.getContact().getLast_name()==null){
						response.setUser_name(userDetails.getContact().getFirst_name());
					}
					else{
						response.setUser_name(userDetails.getContact().getFirst_name()+" "+userDetails.getContact().getLast_name());
					}

					//*************** Start of Business Logic ***************
					//DF20170616 - SU334449 - Sending ECC Dealer Code for Non-DBMS customers concatenate with last login date, with the response.
					Session session = HibernateUtil.getSessionFactory()
							.getCurrentSession();
					session.beginTransaction();
					String accountquery = ("select account_id from AccountContactMapping where contact_id= '"
							+ login_id + "'");
					@SuppressWarnings("rawtypes")
					Iterator accountlist = session.createQuery(accountquery).list()
					.iterator();
					AccountEntity account = (AccountEntity) accountlist.next();
					String account_code = null;

					//DF20171128: KO369761 - Sending Country name to UI in user name field for SAARC Map Changes. 
					String countryName = "|";
					Query countryQuery = session.createQuery("select countryName from CountryCodesEntity where countryCode = '"+account.getCountryCode()+"'");
					Iterator countryItr = countryQuery.list().iterator();

					if(countryItr.hasNext()){
						countryName = countryName+(String)countryItr.next();
					}
					response.setUser_name(response.getUser_name()+countryName);

					account_id = account.getAccount_id();
					account_code = account.getAccountCode();
					TenancyBO tenancyBoObj = new TenancyBO();
					String llAccountCode = tenancyBoObj.getLLAccountCode(account_code);
					if(llAccountCode==null && userDetails.getLast_login_date() != null){
						response.setLast_login_date(userDetails.getLast_login_date()+"|NA");
					}
					else if(llAccountCode!=null && userDetails.getLast_login_date() == null){
						response.setLast_login_date("|"+llAccountCode);
					}
					else if(llAccountCode!=null && userDetails.getLast_login_date() != null){
						response.setLast_login_date(userDetails.getLast_login_date()+"|"+llAccountCode);
					}
					else{
						response.setLast_login_date("|NA");
					}
					//*************** End of Business Logic ***************




					//response.setLoginId(userDetails.getContact().getContact_id());

					response.setRole_name(userDetails.getRole_name());
					response.setRoleId(userDetails.getRole_id());
					//response.setLast_login_date(userDetails.getLast_login_date());
					response.setIsTenancyAdmin(userDetails.getContact().getIs_tenancy_admin());
					response.setSysGeneratedPassword(userDetails.getContact().getSysGeneratedPassword());
					//added by smitha on july 1st 2013...DefectId: 790
					response.setSMS(userDetails.isSMS());
					response.setMap(userDetails.isMap());
					
					//ended on july 1st 2013...DefectId: 790
					List<String> nameIdProxyUserList = new LinkedList<String>();
					TreeMap<String,TreeMap<Integer,String>> nameIdProxyUser = tenancy.getTenancyNameIDProxyUser();
					for(int j=0; j< nameIdProxyUser.size(); j++)
					{
						String finalNameIdProxyUserString = null;
						String name = (String)nameIdProxyUser.keySet().toArray()[j];
						finalNameIdProxyUserString = name;
						TreeMap<Integer,String> idProxyUser = (TreeMap<Integer,String>)nameIdProxyUser.values().toArray()[j];
						for(int k=0; k<idProxyUser.size(); k++)
						{
							loggedInUserTenId=(Integer) idProxyUser.keySet().toArray()[k];
							int tenancyId = (Integer) idProxyUser.keySet().toArray()[k];
							String proxyUser = (String)idProxyUser.values().toArray()[k];
							finalNameIdProxyUserString = finalNameIdProxyUserString+","+tenancyId+","+proxyUser;

						}
						nameIdProxyUserList.add(finalNameIdProxyUserString);
					}			
					response.setTenancyNameIDProxyUser(nameIdProxyUserList);

					//DF20180731 - KO369761 - Deleting existing token ids for the user.
					new CommonUtil().deleteUserTokenIds(userDetails.getContact().getContact_id());

					//DF20170918 @Roopa Handling security issues Sending base64 encoded loginID 

					Timestamp currentTime = new Timestamp(new Date().getTime());
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

					String uniqueTime=sdf.format(currentTime).toString().replaceAll("-","").replaceAll(":","").replaceAll("\\s","");

					/*String userID=userDetails.getContact().getContact_id()+uniqueTime;

				String orgID=loggedInUserTenId+uniqueTime;

				//encoded userId creation

				byte[] encodedBytes = Base64.encodeBase64(userID.getBytes());
				String encodedUserId= new String(encodedBytes);

				response.setLoginId(encodedUserId);

				//encoded orgId creation

				byte[] encodedBytes1 = Base64.encodeBase64(orgID.getBytes());
				String encodedOrgId= new String(encodedBytes1);

				try{
					String query="Insert into idMaster(Id,orgId,timeStamp,status) values('"+encodedUserId+"','"+encodedOrgId+"','"+uniqueTime+"',1)";

					String result=new CommonUtil().insertData(query);

					iLogger.info("Insert record into IdMaster table status::"+result);
				}
				catch(Exception e){
					e.printStackTrace();
				}*/

					//DF20180720- Changing the login token id encryption logic for security purposes.
					Calendar cal= Calendar.getInstance();
					String userID=userDetails.getContact().getContact_id();
					String orgID=loggedInUserTenId+uniqueTime;

					byte[] encodedBytes = Base64.encodeBase64(userID.getBytes());
					String encodedUserId= new String(encodedBytes);

					byte[] encodedBytes1 = Base64.encodeBase64(orgID.getBytes());
					String encodedOrgId= new String(encodedBytes1);

					String tokenId = UUID.randomUUID().toString()+encodedUserId+ cal.getTimeInMillis();
					response.setLoginId(tokenId);

					try{
						String query="Insert into idMaster(Id,orgId,timeStamp,status,userId) values('"+tokenId+"','"+encodedOrgId+"','"+uniqueTime+"',1,'"+encodedUserId+"')";

						String result=new CommonUtil().insertData(query);

						iLogger.info("Insert record into IdMaster table status::"+result);

					}catch(Exception e){
						e.printStackTrace();
					}

					//Df20171009 @Roopa updating the errorCounter to 0 in case of valid credentials.
					try{
						String query="update contact set errorLogCounter=0 where contact_id='"+login_id+"'";

						String result=new CommonUtil().insertData(query);

						iLogger.info("Updating errorCounter if any, since valid password:: for user:: '"+login_id+"'"+result);
					}
					catch(Exception e){
						e.printStackTrace();
					}

				}
			}
			else{
				bLogger.error(userDetails.getError_msg());


				//Df20171009 @Roopa updating the errorCounter in case of wrong credentials
				try{
					String query="update contact set errorLogCounter=errorLogCounter+1 where contact_id='"+login_id+"'";

					String result=new CommonUtil().insertData(query);

					iLogger.info("Incrementing the errorCounter since invalid password for user:: '"+login_id+"'"+result);
				}
				catch(Exception e){
					e.printStackTrace();
				}
				counter=new CommonUtil().getInvalidCredCounter(login_id);
				try {
				if(counter>5){
					//DF20180817-MA369757 -Introducing lockedOutTime for a user for unlocking a locked account.
					Calendar cal1 = Calendar.getInstance();
					SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String timeStamp = sdf1.format(cal1.getTime());
					String lockTimeQuery="update contact set lockedOutTime=\'"+timeStamp+"\' where contact_id=\'"+login_id+"\'";
					iLogger.info("lockTimeQuery "+lockTimeQuery);
					String result=new CommonUtil().insertData(lockTimeQuery);
					iLogger.info("Status of updating the lockedOutTime for the user "+login_id+" :: "+result);
					//throw new CustomFault("Your Account has been locked after 5 invalid attempts. Please try again tomorrow.");
					//DF20180713-KO369761 - Changing the custom message for security purpose.
					throw new CustomFault("Please enter a valid Username/Password.");
				}
				
				else{
					throw new CustomFault("The Credentials is incorrect.");
				}
				}
				catch (CustomFault customFault) {
				    // Handle CustomFault exception here
				    iLogger.error("CustomFault occurred: " + customFault.getMessage());
				}
			}
		}
		else{
			throw new CustomFault("The Credentials is incorrect.");	
		}
		
		//DF20181023-KO369761-Sending original login id along with role_name for tracing errors.
		if(response.getRole_name() != null){
			response.setRole_name(response.getRole_name()+"|"+login_id);
		}

		return response;

	}

}
