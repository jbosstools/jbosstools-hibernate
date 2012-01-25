import mapping.compositeelement.*;

import org.hibernate.Session;

import java.util.*;

	public class PeopleManager {

	    public static void main(String[] args) {
	    	PeopleManager mgr = new PeopleManager();
	    	mgr.createAndStoreParent();
	        HibernateUtil.getSessionFactory().close();
	    }

	    private void createAndStoreParent() {
	    	HibernateUtil.getSessionFactory().openSession();
	        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
	        session.beginTransaction();

	        Parent theParent = new Parent("Parent2");
	        theParent.getChildren().add(new Child("Child1"));
	        theParent.getChildren().add(new Child("Child2"));
	        session.save(theParent);
	        session.getTransaction().commit();
	    }

	    
	}