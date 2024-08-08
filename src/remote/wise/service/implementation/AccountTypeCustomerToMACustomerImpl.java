/**
 * 
 */
package remote.wise.service.implementation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.CommonUtil;
import remote.wise.util.HibernateUtil;
import remote.wise.util.ListToStringConversion;

/**
 * @author KI270523
 * 
 */
public class AccountTypeCustomerToMACustomerImpl {

	// Mamatha: To fetch account ID ,account name & account code
	public HashMap<Integer, String> getAccountTypeCustomer(int pageNumber,
			String searchParam, String searchValue) throws CustomFault {

		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		String Selectquery = "";
		HashMap<Integer, String> AccountMap = new HashMap<Integer, String>();

		int pge;
		if ((pageNumber % 5) == 0) {
			pge = pageNumber / 5;
		}

		else if (pageNumber == 1) {
			pge = 1;
		}

		else {
			while (((pageNumber) % 5) != 0) {
				pageNumber = pageNumber - 1;
			}

			pge = ((pageNumber) / 5) + 1;
		}
		int startLimit = (pge - 1) * 50;

		int endLimit = 50;

		String searchQuery = "";
		if (searchParam.equalsIgnoreCase("accountName") && searchValue != null
				&& !"null".equalsIgnoreCase(searchValue)
				&& !"NULL".equalsIgnoreCase(searchValue)
				&& !searchValue.isEmpty()) {
			searchQuery = " and a.account_name like '%" + searchValue + "%' ";
		} else if (searchParam.equalsIgnoreCase("accountCode")
				&& searchValue != null && !"null".equalsIgnoreCase(searchValue)
				&& !"NULL".equalsIgnoreCase(searchValue)
				&& !searchValue.isEmpty()) {
			searchQuery = " and a.accountCode like '%" + searchValue + "%' ";
		} else if (searchParam.equalsIgnoreCase("serialNumber")
				&& searchValue != null && !"null".equalsIgnoreCase(searchValue)
				&& !"NULL".equalsIgnoreCase(searchValue)
				&& !searchValue.isEmpty()) {
			searchQuery = " and a.accountCode IN (select a.mappingCode "
					+ " from AssetOwnerSnapshotEntity aos, AccountEntity a"
					+ " where aos.accountId = a.account_id and aos.accountType = 'customer'"
					+ " and (a.MAFlag = 0 or a.MAFlag IS NULL))"
					+ " and aos.serialNumber like '%" + searchValue + "%'";
		} else if (searchParam.equalsIgnoreCase("mobileNumber")
				&& searchValue != null && !"null".equalsIgnoreCase(searchValue)
				&& !"NULL".equalsIgnoreCase(searchValue)
				&& !searchValue.isEmpty()) {
			searchQuery = " and a.mobile_no ='" + searchValue + "' ";
		}

		try {

			Selectquery = "select distinct a.account_id,a.accountCode,a.account_name "
					+ " from AssetOwnerSnapshotEntity aos,"
					+ " AccountEntity a"
					+ " where aos.accountId = a.account_id and aos.accountType = 'customer'"
					+ " and (a.MAFlag = 0 or a.MAFlag IS NULL) " + searchQuery;

			System.out.println("Selectquery---------" + Selectquery);

			Query query = session.createQuery(Selectquery)
					.setFirstResult(startLimit).setMaxResults(endLimit);

			Iterator iterator = query.list().iterator();
			Object[] resultQ = null;
			while (iterator.hasNext()) {
				resultQ = (Object[]) iterator.next();
				int accountId = (Integer) resultQ[0];
				String accountcode = (String) resultQ[1];
				String account = (String) resultQ[2];
				AccountMap.put(accountId, account + "-" + accountcode);
			}

		} catch (Exception e) {
			fLogger.fatal("Exception :" + e);
			e.printStackTrace();
		}

		finally {
			if (session.getTransaction().isActive()) {
				session.getTransaction().commit();
			}

			if (session.isOpen()) {
				session.flush();
				session.close();
			}

		}
		return AccountMap;
	}

	public String updateCustomerAccount(List<String> accountIdList)
			throws CustomFault {

		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		String Selectquery = "";
		CommonUtil util = new CommonUtil();
		ListToStringConversion convert = new ListToStringConversion();
		String accountListString = convert.getStringList(accountIdList)
				.toString();

		/*
		 * String isValidinput = util.inputFieldValidation(accountListString);
		 * if (!isValidinput.equals("SUCCESS")) { throw new
		 * CustomFault(isValidinput); }
		 */

		String accountIDsQ = "";
		if (!accountListString.isEmpty()) {
			accountIDsQ = " and a.account_id IN (" + accountListString + ")";
		}

		try {
			Selectquery = "select distinct a.account_id,a.accountCode, a.account_name from AssetOwnerSnapshotEntity aos,"
					+ " AccountEntity a "
					+ " where aos.accountId = a.account_id and aos.accountType = 'customer'"
					+ " and (a.MAFlag = 0 or a.MAFlag IS NULL)" + accountIDsQ ;

			// if (!loginTenacnyId.isEmpty()) {
			// Selectquery +=
			// " and a.account_id in (select at.account_id from AccountTenancyMapping at where at.tenancy_id in ("
			// + loginTenacnyId + "))";
			// }
			System.out.println("Selectquery---------" + Selectquery);

			Query query = session.createQuery(Selectquery);
			Iterator iterator = query.list().iterator();
			List<Integer> accountIDList = new LinkedList<Integer>();
			Object[] resultQ = null;
			while (iterator.hasNext()) {
				resultQ = (Object[]) iterator.next();
				int accountId = (Integer) resultQ[0];
				accountIDList.add(accountId);
			}

			String accountIdListString = convert.getIntegerListString(
					accountIDList).toString();

			if (!accountIdListString.isEmpty()) {
				Query updateQuery = session
						.createQuery("UPDATE AccountEntity SET MAFlag = 1 where account_id IN ("
								+ accountIdListString + ")");
				System.out.println("Selectquery---------" + updateQuery);
				updateQuery.executeUpdate();
			}

		} catch (Exception e) {
			fLogger.fatal("Exception :" + e);
			e.printStackTrace();
			return "FAILURE";
		}

		finally {
			if (session.getTransaction().isActive()) {
				session.getTransaction().commit();
			}

			if (session.isOpen()) {
				session.flush();
				session.close();
			}

		}
		return "SUCCESS";
	}
}
