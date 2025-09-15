package remote.wise.businessobject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import remote.wise.EAintegration.dataContract.CustomerInfoInputContract;
import remote.wise.businessentity.AccountEntity;
import remote.wise.businessentity.AccountEntityPOJO;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.HibernateUtil;

public class CustomerAccCreationBO 
{
	public AccountEntity createCustomer(String custCode,String dealerCode,String messageId, String serialNumber, String sellerCode)
	{
		AccountEntity custEntity = null;
		Logger fLogger = FatalLoggerClass.logger;
		Logger bLogger = BusinessErrorLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;  
		
		Connection con=null;
		Statement stmt=null;
		
		String custCreationStatus="FAILURE";
				
		try
		{
			long startTime = System.currentTimeMillis();
			
			if(custCode==null)
			{
				throw new CustomFault("Mandatory parameter Customer Code not received");
			}
			
			if(dealerCode==null)
			{
				throw new CustomFault("Mandatory parameter Dealer Code not received");
			}
			
			
			con = new ConnectMySQL().getConnection();
			stmt = con.createStatement();
			
			String query = "select Customer_Detail from customer_master where Customer_Code='"+custCode+"'";
			ResultSet rs = stmt.executeQuery(query);
			if(rs.next())
			{
				String customerDetails = rs.getString("Customer_Detail");
				
				if(customerDetails!=null)
				{
					String[] msgString = customerDetails.split("\\|");
					CustomerInfoInputContract inputObject = new CustomerInfoInputContract();
					
					if(msgString.length>0)
						inputObject.setCustomerCode(msgString[0]);

					if(msgString.length>1)
						inputObject.setCustomerName(msgString[1]);

					if(msgString.length>2)
						inputObject.setAddressLine1(msgString[2]);

					if(msgString.length>3)
						inputObject.setAddressLine2(msgString[3]);

					if(msgString.length>4)
						inputObject.setCity(msgString[4]);

					if(msgString.length>5)
						inputObject.setZipCode(msgString[5]);

					if(msgString.length>6)
						inputObject.setState(msgString[6]);

					if(msgString.length>7)
						inputObject.setZone(msgString[7]);

					if(msgString.length>8)
						inputObject.setCountry(msgString[8]);

					if(msgString.length>9)
						inputObject.setEmail(msgString[9]);

					if(msgString.length>10)
						inputObject.setContactNumber(msgString[10]);

					if(msgString.length>11)
						inputObject.setFax(msgString[11]);

					if(msgString.length>12)
						inputObject.setPrimaryDealerCode(msgString[12]);

					if(inputObject.getEmail().equalsIgnoreCase(""))
						inputObject.setEmail(null);
					
					inputObject.setPrimaryDealerCode(dealerCode);


					custCreationStatus = setNewCustomerAccount(inputObject.getCustomerCode(), 
							inputObject.getCustomerName(), inputObject.getAddressLine1(), inputObject.getAddressLine2(), 
							inputObject.getCity(), inputObject.getZipCode(), inputObject.getState(), inputObject.getZone(), 
							inputObject.getCountry(), inputObject.getEmail(), inputObject.getContactNumber(), inputObject.getFax(), 
							inputObject.getPrimaryDealerCode(),messageId,serialNumber, sellerCode);
				}
				
			}
			
			if(custCreationStatus!=null && custCreationStatus.contains("SUCCESS"))
			{
				String updateQuery ="update customer_master set Process_Flag=1 where Customer_Code='"+custCode+"'";
				stmt.executeUpdate(updateQuery);
			}
			
			if(custCreationStatus.split("-").length>1)
			{
				custCreationStatus = custCreationStatus.split("-")[0].trim();

			}
			
			if(!(custCreationStatus.equalsIgnoreCase("SUCCESS")))
			{
				throw new CustomFault("EA Processing: CustomerAccCreationBO:createCustomer:MessageID: "+messageId+": Exception in creating Customer account: "+custCode);
			}
			
			else
			{
				Session session = HibernateUtil.getSessionFactory().openSession();
				Transaction tx = null;
				//Get the customer account entity via a hibernate session
				try
				{
					tx=session.beginTransaction();
					Query custAccQ = session.createQuery("from AccountEntity where status=true and accountCode='"+custCode+"'");
					Iterator custAccIter = custAccQ.list().iterator();
					while(custAccIter.hasNext())
					{
						custEntity = (AccountEntity) custAccIter.next();
					}
				}
				catch(Exception e)
				{
					fLogger.fatal("EAProcessing:CustomerAccCreationBO:createCustomer:custCode:"+custCode+":Hibernate Exception :"+e.getMessage(),e);
				}
				finally
				{
					try
					{
						if(session.isOpen())
						{
							if(session.getTransaction().isActive())
							{
								session.flush();
								session.getTransaction().commit();
							}
						}
					}
		
					catch(Exception e)
					{
						fLogger.fatal("EAProcessing:CustomerAccCreationBO:createCustomer:custCode:"+custCode+":Exception in comitting hibernate session:"+e.getMessage(),e);
					}
		
					if(session.isOpen())
					{
						session.close();
					}
				}
			}
			
			long endTime = System.currentTimeMillis();
			iLogger.info("EAProcessing:CustomerAccCreationBO:createCustomer:custCode:"+custCode+":Customer creation Status:"+custCreationStatus+"; Total Execution time in ms:"+(endTime - startTime));
			
		}
		
		catch(CustomFault e)
		{
			bLogger.error("EAProcessing:CustomerAccCreationBO:createCustomer:custCode:"+custCode+":"+ e.getFaultInfo());
		}
		
		catch(Exception e)
		{
			fLogger.fatal("EAProcessing:CustomerAccCreationBO:createCustomer:custCode:"+custCode+": Exception :"+e.getMessage(),e);
		}
		finally
		{
			try
			{
				if(stmt!=null)
					stmt.close();
				if(con!=null)
					con.close();
				
			}
			catch(Exception e)
			{
				fLogger.fatal("EA EAProcessing:CustomerAccCreationBO:createCustomer:custCode:"+custCode+": Exception in closing the MySQL connection:"+e.getMessage(),e);
			}
		}
		return custEntity;
	}
	
	
	
	//************************************************************************************************************************************
	public String setNewCustomerAccount (String accountCode, String accountName, String addressLine1, String addressLine2, String city,
			 String zipCode, String state, String zone, String country, String email,String contactNumber, String fax, String parentAccountCode,
			 String messageId, String serialNumber, String sellerCode)
	{
		String status="SUCCESS";
		
		Logger fLogger = FatalLoggerClass.logger;
		Logger bLogger = BusinessErrorLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;  
		
		Connection con=null;
		Statement stmt=null;
		PreparedStatement pstmt=null;
		
		try
		{
			//Validity Checks 
			if(email!=null && email.contains("#"))
				email = null;
			if(contactNumber!=null && contactNumber.contains("#"))
				contactNumber=null;
			
			
			//DF20140723 - Rajani Nagaraju - Introducing sleep time for random seconds(between 1 and 5) to ensure no duplicate Customer acc/ten will be created
			//(Especially if sale details for different machines for the same customer under same dealer is received in same file - and customer acc/ten does not exists,
			//In that case, since each record will be processed as parallel thread from Q object there might be a possibility of duplicate acc/ten creation) 
			Random r = new Random();
			int delayPeriod = r.nextInt(5 - 1 + 1) + 1;
			int waitTimeInMilliSec = delayPeriod*1000;
			//iLogger.info("EAProcessing:CustomerAccCreationBO:setNewCustomerAccount:custCode:"+accountCode+" :"+"Sleep for "+delayPeriod+" sec");
    		Thread.sleep(waitTimeInMilliSec);
    		
    		
    		//------------------------ STEP1: Get the corresponding LL code for the received dealer code from ECC/CRM mapping table
    		String query = "select LL_Code from account_mapping where ECC_Code='"+parentAccountCode+"' OR CRM_Code='"+parentAccountCode+"'";
    		con = new ConnectMySQL().getConnection();
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			String llAccountCode = null;
			if(rs.next())
			{
				llAccountCode = rs.getString("LL_Code");
			}
			
			if(llAccountCode==null)
			{
				throw new CustomFault("Data not found in Mapping table for the Dealer AccountCode:"+accountCode);
			}
			else
			{
				parentAccountCode = llAccountCode;
			}
			
			//------------------------- STEP2: Validate parent account in LL
			int parentAccId=0;
			String parentAccName=null;
			query = "select Account_ID,Account_Name from account where Status=1 and account_code='"+parentAccountCode+"'";
			rs = stmt.executeQuery(query);
			while(rs.next())
			{
				parentAccId = rs.getInt("Account_ID");
				parentAccName=rs.getString("Account_Name");
			}
			if(parentAccName==null)
			{
				throw new CustomFault("Parent Account Doesnot Exists in LL: DealerCode:"+parentAccountCode);
			}
			
			
			//------------------------------ Validate the machine ownership with the seller
			int primaryOwnerId=0, sellerAccId=0;
			query = "select LL_Code from account_mapping where ecc_code='"+sellerCode+"' OR crm_code='"+sellerCode+"'" +
					" OR  LL_Code='"+sellerCode+"'";
			rs = stmt.executeQuery(query);
			while(rs.next())
			{
				if(rs.getString("LL_Code")!=null)
					sellerCode=rs.getString("LL_Code");
			}
			
			
			query = "select Account_ID from account where Status=1 and account_code='"+sellerCode+"'";
			rs = stmt.executeQuery(query);
			List<Integer> sellerAccountList=new LinkedList<>();//LLOPS - 182.n
			while(rs.next())
			{
				sellerAccId = rs.getInt("Account_ID");
				sellerAccountList.add(sellerAccId);//LLOPS - 182.n
			}
			//if(sellerAccId==0)
			//LLOPS - 182.sn
			if(sellerAccountList.isEmpty())
			{
				throw new CustomFault("Seller account doesn't exists in LL:"+sellerCode);
			}
			//LLOPS - 182.en

			query = "select primary_owner_id from asset where serial_number='"+serialNumber+"'";
			rs = stmt.executeQuery(query);
			while(rs.next())
			{
				primaryOwnerId=rs.getInt("primary_owner_id");
			}
			//if(primaryOwnerId!=sellerAccId)
			//LLOPS - 182.sn
			if(!sellerAccountList.contains(primaryOwnerId))
			{
				throw new CustomFault("Seller is not the current owner of the machine:"+sellerCode);
			}	
			//LLOPS - 182.en
			
			//---------------------------------------- STEP3: Get the address details
			int adressInput = 1, AddressID=0;
			if ( ( addressLine1 == null || addressLine1.equals("") || (!(addressLine1.replaceAll("\\s","").length()>0)) ) &&
					( addressLine2 == null || addressLine2.equals("") || (!(addressLine2.replaceAll("\\s","").length()>0)) ) &&
					( city == null || city.equals("") || (!(city.replaceAll("\\s","").length()>0)) ) &&
					( zipCode == null || zipCode.equals("") || (!(zipCode.replaceAll("\\s","").length()>0)) ) &&
					( state == null || state.equals("") || (!(state.replaceAll("\\s","").length()>0)) ) &&
					( zone == null || zone.equals("") || (!(zone.replaceAll("\\s","").length()>0)) ) &&
					( country == null || country.equals("") || (!(country.replaceAll("\\s","").length()>0)) ) 
					)
			{
				adressInput=0;
			}
			
			if(adressInput==1)
			{
				addressLine1 = addressLine1.replaceAll("'", "");
				addressLine2 = addressLine2.replaceAll("'", "");
				city = city.replaceAll("'", "");
				zipCode = zipCode.replaceAll("'", "");
				state = state.replaceAll("'", "");
				zone = zone.replaceAll("'", "");
				country =country.replaceAll("'", "");
				
				addressLine1 = addressLine1.replaceAll("\"", "");
				addressLine2 = addressLine2.replaceAll("\"", "");
				city = city.replaceAll("\"", "");
				zipCode = zipCode.replaceAll("\"", "");
				state = state.replaceAll("\"", "");
				zone = zone.replaceAll("\"", "");
				country =country.replaceAll("\"", "");
				
				int update =0;
				String addressQueryString = null;
				String whereQuery = "";

				if(addressLine1!=null && addressLine1!="" && (addressLine1.replaceAll("\\s","").length()>0) ) 
					whereQuery = whereQuery + " AddressLine1 like '"+addressLine1+"' ";
				else
					whereQuery = whereQuery + " AddressLine1 = null";

				if(addressLine2!=null && addressLine2!="" && (addressLine2.replaceAll("\\s","").length()>0) ) 
					whereQuery = whereQuery + " and AddressLine2 like '"+addressLine2+"' ";
				else
					whereQuery = whereQuery + " and AddressLine2 = null";

				if(city!=null && city!="" && (city.replaceAll("\\s","").length()>0) ) 
					whereQuery = whereQuery + " and City like '"+city+"' ";
				else
					whereQuery = whereQuery + " and City = null";

				if(zipCode!=null && zipCode!="" && (zipCode.replaceAll("\\s","").length()>0) ) 
					whereQuery = whereQuery + " and ZipCode like '"+zipCode+"' ";
				else
					whereQuery = whereQuery + " and ZipCode = null";

				if(state!=null && state!="" && (state.replaceAll("\\s","").length()>0) ) 
					whereQuery = whereQuery + " and State like '"+state+"' ";
				else
					whereQuery = whereQuery + " and State = null";

				if(zone!=null && zone!="" && (zone.replaceAll("\\s","").length()>0) ) 
					whereQuery = whereQuery + " and Zone like '"+zone+"' ";
				else
					whereQuery = whereQuery + " and Zone = null";

				if(country!=null && country!="" && (country.replaceAll("\\s","").length()>0) ) 
					whereQuery = whereQuery + " and Country like '"+country+"' ";
				else
					whereQuery = whereQuery + " and Country = null";

				addressQueryString = "select Address_ID from address where " + whereQuery;
				
				rs = stmt.executeQuery(addressQueryString);
				while(rs.next())
				{
					update=1;
					AddressID=rs.getInt("Address_ID");
				}
				
				if(update==0)//Insert the details as new Address
				{
					query = "INSERT INTO address(Country,Zone,State,City,ZipCode,AddressLine1,AddressLine2) values (" +
							"'"+country+"', '"+zone+"', '"+state+"', '"+city+"', '"+zipCode+"', '"+addressLine1+"', '"+addressLine2+"')";
					stmt.executeUpdate(query,Statement.RETURN_GENERATED_KEYS);
					rs = stmt.getGeneratedKeys();
					
					if (rs.next()) 
					{
						AddressID = rs.getInt(1);
					}
				}
			
			}
			
			
			//---------------------------------------- STEP4: Get the country code details
			String countryCode=null, timeZone=null; 
			query = "select country_code,TimeZones from country_codes where country_name='"+country+"'";
			rs = stmt.executeQuery(query);
			while(rs.next())
			{
				countryCode=rs.getString("country_code");
				timeZone = rs.getString("TimeZones");
			}
			
			
			//--------------------------------------- STEP5: Validate duplicate account
			int duplicateAccId=0;
			query = "select Account_ID from account where account_code='"+accountCode+"'";
			rs = stmt.executeQuery(query);
			while(rs.next())
			{
				duplicateAccId = rs.getInt("Account_ID");
			}
			if(duplicateAccId!=0)
			{
				throw new CustomFault("Customer Account for accountCode "+accountCode+" already exists with AccountID:"+duplicateAccId);
			}
			
			//---------------------------------------- STEP6: Create new Customer Account
			int newAccountId=0;
			Date currentDate = new Date();
			Timestamp currentTimeStamp = new Timestamp(currentDate.getTime());
			query = "INSERT INTO account (Account_Name,Status,Parent_ID,Mobile,Client_ID,Address_ID,Email_ID,Account_Code,Fax,timeZone," +
					"CountryCode,mapping_code,created_on,last_updated_on,MAFlag) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			pstmt = con.prepareStatement(query,Statement.RETURN_GENERATED_KEYS);
			pstmt.setString(1, accountName);
			pstmt.setBoolean(2, true);
			pstmt.setInt(3, parentAccId);
			pstmt.setString(4, contactNumber);
			pstmt.setInt(5, 1);
			pstmt.setInt(6, AddressID);
			pstmt.setString(7, email);
			pstmt.setString(8, accountCode);
			pstmt.setString(9, fax);
			pstmt.setString(10, timeZone);
			pstmt.setString(11, countryCode);
			pstmt.setString(12, accountCode);
			pstmt.setTimestamp(13, currentTimeStamp);
			pstmt.setTimestamp(14, currentTimeStamp);
			pstmt.setInt(15, 0);
			
			int rowAffected = pstmt.executeUpdate();
			if(rowAffected == 1)
	        {
	           // get candidate id
	            rs = pstmt.getGeneratedKeys();
	            if(rs.next())
	            	newAccountId = rs.getInt(1);
            }
			
			if(newAccountId==0)
			{
				throw new Exception("Customer Account Creation Failure - AccountID not returned");
			}
			
			iLogger.info("EAProcessing:CustomerAccCreationBO:setNewCustomerAccount:custCode:"+accountCode+"; Customer Account creation: SUCCESS");
			
			//---------------------------------------- STEP7: Update Partnership table
			query = "INSERT INTO partnership(Account_From_ID,Account_To_ID,Partner_ID) values ("+parentAccId+", "+newAccountId+", 3)";
			stmt.executeUpdate(query);
			iLogger.info("EAProcessing:CustomerAccCreationBO:setNewCustomerAccount:custCode:"+accountCode+"; Partnership update: SUCCESS");
			
			//----------------------------------------- STEP8: Update Primary Owner ID in asset ( This is required for setting NS3) 
			query = "update asset set Primary_Owner_ID="+newAccountId+" where Serial_Number='"+serialNumber+"'";
			stmt.executeUpdate(query);
			iLogger.info("EAProcessing:CustomerAccCreationBO:setNewCustomerAccount:custCode:"+accountCode+"; Asset table Primary_owner_id update: SUCCESS");
			
			//---------------------------------------- STEP9: Create Tenancy
			AccountEntityPOJO accountObj = new AccountEntityPOJO();
			accountObj.setAccount_id(newAccountId);
			accountObj.setAccount_name(accountName);
			accountObj.setStatus(true);
			accountObj.setParent_account_id(parentAccId);
			accountObj.setMobile_no(contactNumber);
			accountObj.setClient_id(1);
			accountObj.setEmailId(email);
			accountObj.setAccountCode(accountCode);
			accountObj.setTimeZone(timeZone);
			accountObj.setCountryCode(countryCode);
			accountObj.setMappingCode(accountCode);
			accountObj.setParent_account_name(parentAccName);
			
			status = new PendingTenancyBO().createPendingTenancy(accountObj);
			
		}
		
		catch(CustomFault e)
		{
			status = "FAILURE-"+e.getFaultInfo();
			bLogger.error("EAProcessing:CustomerAccCreationBO:setNewCustomerAccount:custCode:"+accountCode+": "+ e.getFaultInfo());
		}
		
		catch(Exception e)
		{
			status = "FAILURE-"+e.getMessage();
			fLogger.fatal("EAProcessing:CustomerAccCreationBO:setNewCustomerAccount:custCode:"+accountCode+": Exception :"+e);
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
				fLogger.fatal("EAProcessing:CustomerAccCreationBO:setNewCustomerAccount:custCode:"+accountCode+":Exception in closing the MySQL connection:"+e.getMessage(),e);
			}
		}
		
		return status;
	}
}
