package remote.wise.businessobject;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessentity.ContactEntity;
import remote.wise.businessentity.JcbNewsEntity;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.service.implementation.JcbNewsImpl;
import remote.wise.util.HibernateUtil;

public class JcbNewsBo {

	public String setJcbNews(String contact_ID, String headlines, String url) {
		// TODO Auto-generated method stub
		String response ="SUCCESS";
		Logger fLogger = FatalLoggerClass.logger;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();		
		try{
			if(! (session.isOpen() ))
            {
                        session = HibernateUtil.getSessionFactory().getCurrentSession();
                        session.getTransaction().begin();
            }
			JcbNewsEntity newEntity = new JcbNewsEntity();
			newEntity.setHeadlines(headlines);
			ContactEntity contact = new ContactEntity();
			contact.setContact_id(contact_ID);
			newEntity.setContact_ID(contact);
			newEntity.setUrl(url);
			session.save(newEntity);
	}catch(Exception e)
	{
		if(session.getTransaction().isActive())
			session.getTransaction().rollback();
		fLogger.fatal("Exception:"+e);
		return "FAILURE";
		}

		finally
		{
			
			if(session.isOpen())
			{
				if(session.getTransaction().isActive())
					session.getTransaction().commit();
			}
			if(session.isOpen())
			{
				session.flush();
				session.close();
			}
		}
		return "SUCCESS";
	}

	public List<JcbNewsImpl> getJcbNews() {
		// TODO Auto-generated method stub
		Logger fLogger = FatalLoggerClass.logger;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();	
		List<JcbNewsImpl> resObjList = new LinkedList<JcbNewsImpl>();
		
		Query getNewsQuery = session.createQuery("from JcbNewsEntity");
		Iterator newsItr = getNewsQuery.list().iterator();
		while(newsItr.hasNext())
		{
			JcbNewsImpl responseImpl = new JcbNewsImpl();
			JcbNewsEntity dbEntity = (JcbNewsEntity)newsItr.next();
			responseImpl.setContact_ID(dbEntity.getContact_ID().getContact_id());
			responseImpl.setHeadlines(dbEntity.getHeadlines());
			responseImpl.setNews_id(dbEntity.getNews_id());
			responseImpl.setUrl(dbEntity.getUrl());
			resObjList.add(responseImpl);
		}
		try{
			if(! (session.isOpen() ))
            {
                        session = HibernateUtil.getSessionFactory().getCurrentSession();
                        session.getTransaction().begin();
            }
		}catch(Exception e)
		{
			fLogger.fatal("Exception:"+e);
		}
		finally
		{
			if(session.getTransaction().isActive())
				session.getTransaction().commit();
			if(session.isOpen())
			{
				session.flush();
				session.close();
			}
		}
		return null;
	}
}
