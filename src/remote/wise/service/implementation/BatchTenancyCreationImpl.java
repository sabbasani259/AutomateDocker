package remote.wise.service.implementation;

import java.util.Iterator;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessentity.AccountEntity;
import remote.wise.businessentity.AccountTenancyMapping;
import remote.wise.businessentity.PartnershipMapping;
import remote.wise.businessentity.RoleEntity;
import remote.wise.businessobject.TenancyBO;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.TenancyCreationReqContract;
import remote.wise.util.HibernateUtil;

public class BatchTenancyCreationImpl 
{
	public int createTenancy(String accountId)
	{
		int newTenancyCount=0;
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;

		try
		{
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			if(session.getTransaction().isActive() && session.isDirty())
			{
				iLogger.info("Opening a new session");
				session = HibernateUtil.getSessionFactory().openSession();
			}
			session.beginTransaction();

			try
			{
				Query accountQ = null;

				//If account Id is not specified, get all the accounts for which tenancy is not created
				if(accountId==null || accountId.trim().length()==0 )
				{
					accountQ = session.createQuery(" from AccountEntity where account_id not in (select account_id from " +
					" AccountTenancyMapping) and status=1 ");
				}

				else
				{
					accountQ = session.createQuery(" from AccountEntity where account_id="+accountId);
				}

				Iterator accountItr = accountQ.list().iterator();
				while(accountItr.hasNext())
				{
					AccountEntity account = (AccountEntity)accountItr.next();
					PartnershipMapping partner =null;

					//Get the account type from partnership table
					Query accountTypeQ = session.createQuery(" from PartnershipMapping where accountToId="+account.getAccount_id());
					Iterator accountTypeItr = accountTypeQ.list().iterator();
					while(accountTypeItr.hasNext())
					{
						partner= (PartnershipMapping)accountTypeItr.next();
					}

					if(partner!=null)
					{
						String accountType = partner.getPartnerId().getReversePartnerRole();
						String roleName=null;
						if(accountType.equalsIgnoreCase("Dealer"))
							roleName = "DealerAdmin";
						else if (accountType.equalsIgnoreCase("Customer"))
							roleName = "CustomerFleetManager";


						if(roleName!=null)
						{
							TenancyCreationReqContract tenancyReqContract  = new TenancyCreationReqContract();
							tenancyReqContract.setLoginId("batch");
							tenancyReqContract.setAccountId(account.getAccount_id());
							AccountTenancyMapping paccountTenancy = new TenancyBO().getTenancyEntity(account.getParent_account_id().getAccount_id());

							//DF20140328 - Rajani Nagaraju - IF Parent tenancy is not present then child tenancy cannot be created
							if(paccountTenancy!=null)
							{
								tenancyReqContract.setParentTenancyId(paccountTenancy.getTenancy_id().getTenancy_id());
								tenancyReqContract.setChildTenancyId(0);
								tenancyReqContract.setChildTenancyName(account.getAccount_name());
								RoleEntity roleEntity = new TenancyBO().getRoleEntity(roleName);
								if(roleEntity!=null)
									tenancyReqContract.setTenancyAdminRoleId(roleEntity.getRole_id());

								tenancyReqContract.setTenancyAdminFirstName(account.getAccount_name());
								tenancyReqContract.setTenancyAdminPhoneNumber(account.getMobile_no());
								tenancyReqContract.setTenancyAdminEmailId(account.getEmailId());
								tenancyReqContract.setCountryCode("91");

								//                          createTenancyForUser
								//Df20150611 - Rajani Nagaraju - Adding try catch block around each tenancy creation to continue with the next iteration
								try
								{
									iLogger.info("Batch Tenancy Creation: "+account.getAccount_id()+": INPUTS to automated tenancy creation :");
									iLogger.info("Batch Tenancy Creation: "+account.getAccount_id()+": Tenancy Name: "+ account.getAccount_name()+", " +
											"Account ID: "+account.getAccount_id()+", ParentTenancyId: "+paccountTenancy.getTenancy_id().getTenancy_id());
									String tenancyCreationStatus =new TenancyDetailsImpl().createTenancy(tenancyReqContract);
									iLogger.info("Batch Tenancy Creation: "+account.getAccount_id()+":TenancyCreationStatus  "+tenancyCreationStatus);

									if(tenancyCreationStatus!=null && tenancyCreationStatus.equalsIgnoreCase("SUCCESS"))
										newTenancyCount++;
								}

								catch(Exception e)
								{
									fLogger.fatal("Batch Tenancy Creation: "+account.getAccount_id()+":Exception: "+e);
								}
							}

						}
					}


					if(! (session.isOpen() ))
					{
						session = HibernateUtil.getSessionFactory().getCurrentSession();
						//DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
						if(session.getTransaction().isActive() && session.isDirty())
						{
							iLogger.info("Opening a new session");
							session = HibernateUtil.getSessionFactory().openSession();
						}
						session.getTransaction().begin();
					}
				}


			}

			catch(Exception e)
			{
				fLogger.fatal("Exception :"+e);
				e.printStackTrace();
			}

			finally
			{
				try
				{
					if(session.isOpen())
						if(session.getTransaction().isActive())
						{
							session.flush();
							session.getTransaction().commit();
						}
				}
				catch(Exception e)
				{
					fLogger.fatal("Exception in commiting the record:"+e);

				}

				if(session.isOpen())
				{

					session.close();
				}
			}
		}

		catch(Exception e)
		{
			fLogger.fatal("Exception in getting Hibernate Session:"+e);
			e.printStackTrace();
		}


		return newTenancyCount;
	}
}
