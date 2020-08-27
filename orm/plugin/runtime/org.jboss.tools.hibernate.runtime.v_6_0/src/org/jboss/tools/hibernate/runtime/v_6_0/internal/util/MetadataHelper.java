package org.jboss.tools.hibernate.runtime.v_6_0.internal.util;

import java.lang.reflect.Field;

import org.hibernate.boot.MetadataSources;
import org.hibernate.cfg.Configuration;

public class MetadataHelper {
	
	public static MetadataSources getMetadataSources(Configuration configuration) {
		MetadataSources result = null;
		Field metadataSourcesField = getField("metadataSources", configuration);
		if (metadataSourcesField != null) {
			try {
				metadataSourcesField.setAccessible(true);
				result = 
						(MetadataSources)metadataSourcesField.get(configuration);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		if (result == null) {
			result = new MetadataSources();
		}
		return result;
	}
	
	private static Field getField(String fieldName, Object target) {
		Field result = null;
		Class<?> clazz = target.getClass();
		while (clazz != null) {
			try {
				result = clazz.getDeclaredField(fieldName);
				// if it exists, exit the while loop
				break;
			} catch (NoSuchFieldException e) {
				// if it doesn't exist, look in the superclass
				clazz = clazz.getSuperclass();
			} catch (SecurityException e) {
				throw new RuntimeException(e);
			}
		}
		return result;
	}
	
}
