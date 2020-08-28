package org.jboss.tools.hibernate.runtime.v_6_0.internal.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import java.io.ByteArrayInputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
import org.junit.Test;

public class MetadataHelperTest {
	
	@Test
	public void testGetMetadataSources() {
		MetadataSources mds1 = new MetadataSources();
		Configuration configuration = new Configuration();
		MetadataSources mds2 = MetadataHelper.getMetadataSources(configuration);
		assertNotNull(mds2);
		assertNotSame(mds1, mds2);
		configuration = new Configuration(mds1);
		mds2 = MetadataHelper.getMetadataSources(configuration);
		assertNotNull(mds2);
		assertSame(mds1, mds2);
	}
	
	@Test
	public void testGetMetadata() {
		 Configuration methodConfiguration = new MetadataMethodConfiguration();
	     assertSame(
	    		 MetadataMethodConfiguration.METADATA, 
	    		 MetadataHelper.getMetadata(methodConfiguration));
	     Configuration fieldConfiguration = new MetadataFieldConfiguration();
	     assertSame(
	    		 MetadataFieldConfiguration.METADATA, 
	    		 MetadataHelper.getMetadata(fieldConfiguration));
	     MetadataSources metadataSources = new MetadataSources();
	     metadataSources.addInputStream(new ByteArrayInputStream(TEST_HBM_XML_STRING.getBytes()));
	     Configuration configuration = new Configuration(metadataSources);
	     configuration.setProperty(
	    		 "hibernate.dialect", 
	    		 TestDialect.class.getName());
	     Metadata metadata = MetadataHelper.getMetadata(configuration);
	     assertNotNull(metadata.getEntityBinding("org.jboss.tools.hibernate.runtime.v_6_0.internal.util.MetadataHelperTest$Foo"));
	}
	
	private static class MetadataMethodConfiguration extends Configuration {
		static Metadata METADATA = createMetadata();
		@SuppressWarnings("unused")
		public Metadata getMetadata() {
			return METADATA;
		}
	}
	
	private static class MetadataFieldConfiguration extends Configuration {
		static Metadata METADATA = createMetadata();
		@SuppressWarnings("unused")
		private Metadata metadata = METADATA;
	}
	
	private static Metadata createMetadata() {
		Metadata result = null;
		result = (Metadata) Proxy.newProxyInstance(
				MetadataHelperTest.class.getClassLoader(), 
				new Class[] { Metadata.class },  
				new InvocationHandler() {				
					@Override
					public Object invoke(
							Object proxy, 
							Method method, 
							Object[] args) throws Throwable {
						return null;
					}
				});
		return result;
	}
	
	public static class TestDialect extends Dialect {}
	
	@SuppressWarnings("unused")
	private static class Foo {
		public String id;
	}
	
	private static final String TEST_HBM_XML_STRING =
			"<!DOCTYPE hibernate-mapping PUBLIC" +
			"		'-//Hibernate/Hibernate Mapping DTD 3.0//EN'" +
			"		'http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd'>" +
			"<hibernate-mapping package='org.jboss.tools.hibernate.runtime.v_6_0.internal.util'>" +
			"  <class name='MetadataHelperTest$Foo'>" + 
			"    <id name='id'/>" +
			"  </class>" +
			"</hibernate-mapping>";

}
