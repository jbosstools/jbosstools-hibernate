package org.jboss.tools.hibernate.runtime.spi;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.osgi.service.prefs.Preferences;

public class RuntimeServiceManagerTest {
	
	private static String testPreferencesName = "org.jboss.tools.hibernate.runtime.spi.test.services";
	
	private RuntimeServiceManager runtimeServiceManager = null;
	
	@BeforeEach
	public void before() throws Exception {
		Constructor<RuntimeServiceManager> constructor = 
				RuntimeServiceManager.class.getDeclaredConstructor(new Class[] {});
		constructor.setAccessible(true);
		runtimeServiceManager = constructor.newInstance(new Object[] {});
	}
	
	@Test
	public void testConstruction() throws Exception {
		Field servicePreferencesField = RuntimeServiceManager.class.getDeclaredField("servicePreferences");
		servicePreferencesField.setAccessible(true);
		Preferences preferences = (Preferences)servicePreferencesField.get(runtimeServiceManager);
		assertEquals("org.jboss.tools.hibernate.runtime.spi.services", preferences.name());
		Field servicesMapField = RuntimeServiceManager.class.getDeclaredField("servicesMap");
		servicesMapField.setAccessible(true);
		assertNotNull(servicesMapField.get(runtimeServiceManager));
		Field allVersionsField = RuntimeServiceManager.class.getDeclaredField("allVersions");
		allVersionsField.setAccessible(true);
		assertNotNull(allVersionsField.get(runtimeServiceManager));
		Field enabledVersionsField = RuntimeServiceManager.class.getDeclaredField("enabledVersions");
		enabledVersionsField.setAccessible(true);
		assertNotNull(enabledVersionsField.get(runtimeServiceManager));
		Field initiallyEnabledVersionsField = RuntimeServiceManager.class.getDeclaredField("initiallyEnabledVersions");
		initiallyEnabledVersionsField.setAccessible(true);
		assertNotNull(initiallyEnabledVersionsField.get(runtimeServiceManager));
	}

	@Test
	public void testGetInstance() throws Exception {
		Field instanceField = RuntimeServiceManager.class.getDeclaredField("INSTANCE");
		instanceField.setAccessible(true);
		Object instance = instanceField.get(null);
		assertNotNull(instance);
		assertSame(instance, RuntimeServiceManager.getInstance());
	}
	
	@Test
	public void testGetDefaultService() throws Exception {
		Field servicesMapField = RuntimeServiceManager.class.getDeclaredField("servicesMap");
		servicesMapField.setAccessible(true);
		Map<String, IService> servicesMap = new HashMap<String, IService>();
		IService fooService = createService();
		servicesMap.put("foo", fooService);
		servicesMapField.set(runtimeServiceManager, servicesMap);
		Field enabledVersionsField = RuntimeServiceManager.class.getDeclaredField("enabledVersions");
		enabledVersionsField.setAccessible(true);
		enabledVersionsField.set(
				runtimeServiceManager, 
				new HashSet<String> (Arrays.asList("foo", "baz")));
		assertSame(fooService, runtimeServiceManager.getDefaultService());
	}
	
	@Test
	public void testGetAllVersions() throws Exception {
		Field allVersionsField = RuntimeServiceManager.class.getDeclaredField("allVersions");
		allVersionsField.setAccessible(true);
		String[] allVersions = new String[] { "foo", "bar" };
		allVersionsField.set(runtimeServiceManager, allVersions);
		assertArrayEquals(allVersions, runtimeServiceManager.getAllVersions());
		assertNotSame(allVersions, runtimeServiceManager.getAllVersions());
	}
	
	@Test
	public void testGetEnabledVersions() throws Exception {
		Field enabledVersionsField = RuntimeServiceManager.class.getDeclaredField("enabledVersions");
		enabledVersionsField.setAccessible(true);
		enabledVersionsField.set(
				runtimeServiceManager, 
				new HashSet<String> (Arrays.asList("foo", "bar")));
		assertArrayEquals(new String[] {"bar", "foo" }, runtimeServiceManager.getEnabledVersions());
	}
	
	@Test
	public void testSetDefaultVersion() throws Exception {
		Preferences preferences = InstanceScope.INSTANCE.getNode(testPreferencesName + ".testSetDefaultVersion");
		Field preferencesField = RuntimeServiceManager.class.getDeclaredField("servicePreferences");
		preferencesField.setAccessible(true);
		preferencesField.set(runtimeServiceManager, preferences);
		Field enabledVersionsField = RuntimeServiceManager.class.getDeclaredField("enabledVersions");
		enabledVersionsField.setAccessible(true);
		enabledVersionsField.set(
				runtimeServiceManager, 
				new HashSet<String> (Arrays.asList("foo", "bar")));
		assertNull(preferences.get("default", null));
		// first: trying to set a disabled runtime as the default should fail
		try {
			runtimeServiceManager.setDefaultVersion("baz");
			fail();
		} catch (Exception e) {
			assertEquals(
					"Setting a disabled Hibernate runtime as the default is not allowed", 
					e.getMessage());
		}
		// second: choosing an enabled runtime
		assertNull(preferences.get("default", null));
		runtimeServiceManager.setDefaultVersion("foo");
		assertEquals("foo", preferences.get("default", null));
		runtimeServiceManager.setDefaultVersion("bar");
		assertEquals("bar", preferences.get("default", null));
	}
	
	@Test
	public void testGetDefaultVersion() throws Exception {
		Preferences preferences = InstanceScope.INSTANCE.getNode(testPreferencesName + ".testGetDefaultVersion");
		Field preferencesField = RuntimeServiceManager.class.getDeclaredField("servicePreferences");
		preferencesField.setAccessible(true);
		preferencesField.set(runtimeServiceManager, preferences);
		Field enabledVersionsField = RuntimeServiceManager.class.getDeclaredField("enabledVersions");
		enabledVersionsField.setAccessible(true);
		enabledVersionsField.set(
				runtimeServiceManager, 
				new HashSet<String> (Arrays.asList("foo", "bar", "baz")));
		// first: if a preference is found, take that one 
		preferences.put("default", "baz");
		assertEquals("baz", runtimeServiceManager.getDefaultVersion());
		// second: if there is no preference, take the alphabetically highest enabled version
		preferences.remove("default");
		assertEquals("foo", runtimeServiceManager.getDefaultVersion());
		// third: throw exception if no version is enabled
		enabledVersionsField.set(runtimeServiceManager, new HashSet<String> ());
		try {
			runtimeServiceManager.getDefaultVersion();
			fail();
		} catch (Exception e) {
			assertEquals("No Hibernate runtimes are enabled.", e.getMessage());
		}
	}
	
	@Test
	public void testFindService() throws Exception {
		Field servicesMapField = RuntimeServiceManager.class.getDeclaredField("servicesMap");
		servicesMapField.setAccessible(true);
		Map<String, IService> servicesMap = new HashMap<String, IService>();
		IService fooService = createService();
		IService barService = createService();
		servicesMap.put("foo", fooService);
		servicesMap.put("bar", barService);
		servicesMapField.set(runtimeServiceManager, servicesMap);
		assertSame(fooService, runtimeServiceManager.findService("foo"));
		assertSame(barService, runtimeServiceManager.findService("bar"));
	}
	
	@Test
	public void testIsServiceEnabled() throws Exception {
		assertFalse(runtimeServiceManager.isServiceEnabled("foobar"));
		Field enabledVersionsField = RuntimeServiceManager.class.getDeclaredField("enabledVersions");
		enabledVersionsField.setAccessible(true);
		Set<String> enabledVersions = new HashSet<String>();
		enabledVersions.add("foobar");
		enabledVersionsField.set(runtimeServiceManager, enabledVersions);
		assertTrue(runtimeServiceManager.isServiceEnabled("foobar"));
	}
	
	@Test
	public void testEnableService() throws Exception {
		Field enabledVersionsField = RuntimeServiceManager.class.getDeclaredField("enabledVersions");
		enabledVersionsField.setAccessible(true);	
		Set<String> enabledVersions = new HashSet<String>();
		enabledVersions.add("zanzibar");
		enabledVersionsField.set(runtimeServiceManager, enabledVersions);
		Preferences preferences = InstanceScope.INSTANCE.getNode(testPreferencesName + ".testEnableService");
		Field preferencesField = RuntimeServiceManager.class.getDeclaredField("servicePreferences");
		preferencesField.setAccessible(true);
		preferencesField.set(runtimeServiceManager, preferences);
		assertFalse(preferences.getBoolean("foobar", false));
		assertFalse(enabledVersions.contains("foobar"));
		runtimeServiceManager.enableService("foobar", true);
		assertTrue(preferences.getBoolean("foobar", false));
		assertTrue(enabledVersions.contains("foobar"));
		runtimeServiceManager.enableService("foobar", false);
		assertFalse(preferences.getBoolean("foobar", false));
		assertFalse(enabledVersions.contains("foobar"));
		try {
			runtimeServiceManager.enableService("zanzibar", false);
			fail();
		} catch (Exception e) {
			assertEquals(
					"Disabling the default Hibernate runtime is not allowed", 
					e.getMessage());
		}
	}
	
	private IService createService() {
		return (IService)Proxy.newProxyInstance(
				getClass().getClassLoader(), 
				new Class[] { IService.class }, 
				new InvocationHandler() {				
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						return null;
					}
				});
	}
	
}
