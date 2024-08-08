package remote.wise.businessobject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Blob;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessentity.ContactEntity;
import remote.wise.businessentity.FeedBackCountEntity;
import remote.wise.businessentity.TestimonialFeedBackEntity;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.service.implementation.TestimonialFeedBackImpl;
import remote.wise.util.HibernateUtil;

public class TestimonialFeedBackBO {
	private static final Set<String> categories = new HashSet<String>(Arrays.asList("Feedback","testimonials","Complaint","Suggestion"));
	public String setFeedBackForm(String contact_ID, byte[] image,
			String feedBack, int rating, String Edited_By, String organisation_Name, String Category_type) {
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

			SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Calendar cal = Calendar.getInstance();
			String date = sdf.format(cal.getTime());
			TestimonialFeedBackEntity entity = new TestimonialFeedBackEntity();
			if(Category_type!=null)
				entity.setCategory_Type(Category_type);
			//String name[] = null;
			if(Edited_By!=null)
			{
				//entity = (TestimonialFeedBackEntity)session.get(TestimonialFeedBackEntity.class, feedback_ID);
				entity.setEdited_By(Edited_By);
				//entity.setFeedBack(feedBack);
				/*name = contact_ID.split(" ");
				String query = "from ContactEntity where First_Name='"+name[0]+"'";
				
				if(name.length>1)
					query+=" and Last_Name='"+name[1]+"'";
				Query contactQuery = session.createQuery(query);
				Iterator contactItr = contactQuery.list().iterator();
				while(contactItr.hasNext())
				{
					ContactEntity idEntity = (ContactEntity)contactItr.next();
					contact_ID = idEntity.getContact_id();
				}*/
				
			}
				ContactEntity c= new ContactEntity();
				c.setContact_id(contact_ID);
				entity.setContact_ID(c);
				entity.setOrganisation_Name(organisation_Name);
				//System.out.println(entity.getContact_ID().getContact_id());
				if(image!=null){
					Blob blob = Hibernate.createBlob(image);
		
					entity.setImage(blob);
				}
				entity.setFeedBack(feedBack);
				entity.setCurrent_Date(date);
				entity.setRating(rating);
			session.saveOrUpdate(entity);
			if(! (session.isOpen() ))
            {
                        session = HibernateUtil.getSessionFactory().getCurrentSession();
                        session.getTransaction().begin();
            }
			FeedBackCountEntity countEntity = (FeedBackCountEntity) session.get(FeedBackCountEntity.class, rating); 
			countEntity.setCount(countEntity.getCount()+1);
			session.update(countEntity);
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
		return response;
	}

	public String setFeedBackApproval(int feedback_ID, boolean approved,
			String approved_By) {
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
			SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Calendar cal = Calendar.getInstance();
			String approved_date = sdf.format(cal.getTime());
			TestimonialFeedBackEntity dbEntity = (TestimonialFeedBackEntity)session.get(TestimonialFeedBackEntity.class, feedback_ID);
			dbEntity.setApproved(approved);
			dbEntity.setApproved_By(approved_By);
			dbEntity.setApproved_Date(approved_date);
			session.update(dbEntity);
			//System.out.println(dbEntity);
			
		}catch(Exception e)
		{
			if(session.getTransaction().isActive())
			session.getTransaction().rollback();
			return "FAILURE";
			//fLogger.fatal("Exception:"+e);
			
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
	return response;
	}

	public List<TestimonialFeedBackImpl> getApprovedFeedBacks(String dealer_Id) {
		// TODO Auto-generated method stub
		TestimonialFeedBackImpl dbFeedBackObj = null;
		Logger fLogger = FatalLoggerClass.logger;
		List<TestimonialFeedBackImpl> responseList = new LinkedList<TestimonialFeedBackImpl>();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();		
		String contact_id=null;
		int total_Feedbacks = 0;
		int rating_count = 0;
		boolean rating_flag = false;//check to see whether request is for count for feedback counts
		try{
			if(! (session.isOpen() ))
            {
                        session = HibernateUtil.getSessionFactory().getCurrentSession();
                        session.getTransaction().begin();
            }
			String FeedBackQuery = null;
			if(dealer_Id==null)
				FeedBackQuery = "from TestimonialFeedBackEntity where Approved=true order by Rating desc";
			else if(dealer_Id.equalsIgnoreCase("rating_count"))
			{
				FeedBackQuery = "from FeedBackCountEntity";
				rating_flag = true;
			}
			else if(categories.contains(dealer_Id))
				FeedBackQuery = "from TestimonialFeedBackEntity where Category_Type='"+dealer_Id+"' order by Rating desc";
			else if(dealer_Id.equalsIgnoreCase("admin"))
				FeedBackQuery = "from TestimonialFeedBackEntity order by Current_Date desc,Rating desc";
			else if(dealer_Id.matches("\\d+"))
				FeedBackQuery = "from TestimonialFeedBackEntity where Feedback_ID="+Integer.parseInt(dealer_Id);
			else
				FeedBackQuery = "from TestimonialFeedBackEntity where Contact_ID='"+dealer_Id+"' order by Current_Date desc";
			Query selectedFBQuery = session.createQuery(FeedBackQuery);
			//System.out.println(selectedFBQuery);
			Iterator selectedFBitr = selectedFBQuery.list().iterator();
			while(selectedFBitr.hasNext())
			{
				//System.out.println("Hiiiiiii");
				dbFeedBackObj = new TestimonialFeedBackImpl();
				
				if(!rating_flag)
				{
					TestimonialFeedBackEntity dbFBEntity = (TestimonialFeedBackEntity)selectedFBitr.next();
					dbFeedBackObj.setFeedback_ID(dbFBEntity.getFeedback_ID());
				//System.out.println("feedback ID"+dbFBEntity.getFeedback_ID());
					dbFeedBackObj.setCurrent_Date(dbFBEntity.getCurrent_Date());
					contact_id = dbFBEntity.getContact_ID().getContact_id();
					dbFeedBackObj.setFeedBack(dbFBEntity.getFeedBack());
					if(dbFBEntity.getOrganisation_Name()!=null)
						dbFeedBackObj.setOrganisation_Name(dbFBEntity.getOrganisation_Name());
					if(dbFBEntity.getEdited_By()!=null)
						dbFeedBackObj.setEdited_By(dbFBEntity.getEdited_By());
					if(dbFBEntity.getCategory_Type()!=null)
						dbFeedBackObj.setCategory_Type(dbFBEntity.getCategory_Type());
					//System.out.println(dbFBEntity.getCategory_Type());
				//System.out.println("select r.role_name from ContactEntity c ,c.role cr,RoleEntity r where c.contact_id='"+contact_id+"' and cr.role_id=r.role_id");
					dbFeedBackObj.setRole(contact_id);
					Query roleQuery = session.createQuery("from ContactEntity c where c.contact_id='"+contact_id+"'");
				//System.out.println(roleQuery);
					Iterator roleItr = roleQuery.list().iterator();
					while(roleItr.hasNext())
					{
					//resultObj = (Object[]) roleItr.next();
						ContactEntity e = (ContactEntity)roleItr.next();
						contact_id = e.getFirst_name()+ " ";
						if(e.getLast_name()!=null)
							contact_id+=e.getLast_name();
						dbFeedBackObj.setContact_ID(contact_id);
						
					}
					Blob b1 = dbFBEntity.getImage();
					if(b1!=null)
					{
						int length = (int)b1.length();
						byte[] bytes = new byte[length];
						ByteArrayOutputStream bao = new ByteArrayOutputStream();
						InputStream in = b1.getBinaryStream();
						int n=0;
						while((n=in.read(bytes))>=0)
						{
							bao.write(bytes, 0, n);
						}
						byte[] bFile = bao.toByteArray(); 
						dbFeedBackObj.setImage(bFile);
					}
					
					dbFeedBackObj.setRating(dbFBEntity.getRating());
					
				}
				else
				{
					FeedBackCountEntity dbFBEntity = (FeedBackCountEntity)selectedFBitr.next();
					dbFeedBackObj.setRating(dbFBEntity.getRating());
					dbFeedBackObj.setCount(dbFBEntity.getCount());
					rating_count = rating_count+(dbFBEntity.getRating()*dbFBEntity.getCount()); 
					total_Feedbacks+=dbFBEntity.getCount();
				}
				responseList.add(dbFeedBackObj);
			}
			DecimalFormat df = new DecimalFormat("0.0");
			
			 if(rating_flag)
			 {
				 dbFeedBackObj = new TestimonialFeedBackImpl();
				 dbFeedBackObj.setRating(6);
				 dbFeedBackObj.setCount(Double.parseDouble(df.format((double)rating_count/total_Feedbacks)));
				 responseList.add(dbFeedBackObj);
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
		return responseList;
	}	
}
