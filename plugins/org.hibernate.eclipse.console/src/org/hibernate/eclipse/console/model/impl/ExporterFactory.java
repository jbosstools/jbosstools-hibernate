package org.hibernate.eclipse.console.model.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.hibernate.cfg.Configuration;
import org.hibernate.console.HibernateConsoleRuntimeException;
import org.hibernate.eclipse.launch.HibernateLaunchConstants;
import org.hibernate.eclipse.launch.PathHelper;
import org.hibernate.tool.hbm2x.ArtifactCollector;
import org.hibernate.tool.hbm2x.Exporter;
import org.hibernate.tool.hbm2x.GenericExporter;
import org.hibernate.util.StringHelper;

/**
 * ExporterFactory is used in UI to hold additional configuration for Exporter definitions
 * @author max
 *
 */
public class ExporterFactory {

	private ExporterDefinition definition;

	final Map inputProperties;

	private boolean enabled = true;

	private final String exporterId;
	
	public ExporterFactory(ExporterDefinition definition, String exporterId) {
		this.definition = definition;
		this.exporterId = exporterId;
		inputProperties = new HashMap();
	}

	public Map getDefaultExporterProperties() {
		return definition.getExporterProperties();
	}
	

	public String setProperty(String key, String value) {
		return (String) inputProperties.put( key, value );		
	}

	public void removeProperty(String propertyName) {
		inputProperties.remove( propertyName );
	}

	public String getPropertyValue(String key) {
		if(inputProperties.containsKey( key )) {
			return (String) inputProperties.get( key );
		} else {
			ExporterProperty ep = (ExporterProperty) definition.getExporterProperties().get( key );
			if(ep!=null) {
				return ep.getDefaultValue();
			} else {
				return null;
			}
		} 
	}

	public boolean isEnabled() {
		return enabled ;
	}

	public void setEnabled(boolean b) {
		enabled = b;		
	}

	public ExporterDefinition getExporterDefinition() {
		return definition;
	}

	// todo: move to ExporterAttributes together with isEnabled functionallity...
	String getLaunchAttributePrefix(String exporterId) {
		return HibernateLaunchConstants.ATTR_EXPORTERS + "." + exporterId;
	}

	public boolean isEnabled(ILaunchConfiguration configuration) {
		boolean enabled = false;

		try {
		if(configuration.getAttribute(HibernateLaunchConstants.ATTR_EXPORTERS, (List)null)==null) {
				enabled = configuration.getAttribute( getId(), false );			
		} else {
			enabled = configuration.getAttribute( getLaunchAttributePrefix( getId() ), false );
		}
		} catch(CoreException ce) {
			// ignore; assume false
			enabled=false;
		}
		
		setEnabled( enabled );
		return isEnabled();
	}

	public void setEnabled(ILaunchConfigurationWorkingCopy configuration, boolean enabled, boolean oldSettings) {
		setEnabled( enabled );
		if(oldSettings) {
			configuration.setAttribute( getId(), isEnabled() );
		} else {
			configuration.setAttribute( getLaunchAttributePrefix( getId() ), isEnabled());
		}
	}
	
	public void setEnabled(ILaunchConfigurationWorkingCopy configuration, boolean enabled) {
		
		boolean oldSettings = true;
		try {
		if(configuration.getAttribute(HibernateLaunchConstants.ATTR_EXPORTERS, (List)null)==null) {
			oldSettings = true;
		} else {
			oldSettings = false;
		}
		} catch(CoreException ce) {
			// ignore and assume settings are old
		}

		setEnabled(configuration, enabled, oldSettings);
	}

	public Map getProperties() {
		return inputProperties;
	}

	public String getId() {
		return exporterId;
	}

	public void setProperties(Map props) {
		inputProperties.clear();
		inputProperties.putAll( props );				
	}

	public ExporterProperty getExporterProperty(String key) {
		return (ExporterProperty) definition.getExporterProperties().get( key );
	}

	public boolean hasLocalValueFor(String string) {
		return inputProperties.containsKey( string );
	}

	/** Method that resolves an expression through eclipses built-in variable manager.  
	 * @throws CoreException if expression could not be evaluated. */
	private String resolve(String expression) throws CoreException  {
		if(expression==null) return null;
		IStringVariableManager variableManager = VariablesPlugin.getDefault().getStringVariableManager();
		
		return variableManager.performStringSubstitution(expression, false); 		
	}
	
	/**
	 * Creates exporter with the specified settings; also resolves any relevant properties via Eclipse VariablesPlugin.
	 * @param collector 
	 * @throws CoreException in case of resolve variables issues.
	 */
	public Exporter createConfiguredExporter(Configuration cfg, String defaultOutputDirectory,
			String customTemplatePath, Properties globalProperties, Set outputDirectories, ArtifactCollector collector) throws CoreException {
		
		Exporter exporter = getExporterDefinition().createExporterInstance();
		
		
		Properties props = new Properties();
		props.putAll(globalProperties);
		props.putAll(getProperties());
				
		exporter.setProperties(props);
		exporter.setArtifactCollector(collector);
		
		String outputPath = defaultOutputDirectory;
		if(props.containsKey("outputdir")) {
			outputPath = props.getProperty("outputdir");
			props.remove("outputdir"); // done to avoid validation check in hibernate tools templates	
		}
		
		String resolvedOutputDir = resolve(outputPath);
		String loc = PathHelper.getLocationAsStringPath(resolvedOutputDir);
		if(outputPath != null && loc == null) {
			throw new HibernateConsoleRuntimeException("Output directory '" + resolvedOutputDir + "' in " + getExporterDefinition().getDescription() + " does not exist.");
		}
				
		if(StringHelper.isNotEmpty(loc)) { // only set if something valid found
			outputDirectories.add(loc);
			exporter.setOutputDirectory(new File(loc));
		}
		
		exporter.setConfiguration(cfg);
		
		List templatePathList = new ArrayList();
		if(props.containsKey("template_path")) {
			String resolveTemplatePath = resolve(props.getProperty("template_path"));
			String locationAsStringPath = PathHelper.getLocationAsStringPath(resolveTemplatePath);
			if(locationAsStringPath==null) {
				throw new HibernateConsoleRuntimeException("Template directory '" + resolveTemplatePath + "' in " + getExporterDefinition().getDescription() + " does not exist.");
			} else {
				templatePathList.add(locationAsStringPath);
			}					
			props.remove("template_path"); // done to avoid validation check in hibernate tools templates
		}
		if (StringHelper.isNotEmpty(customTemplatePath)){
			String resolvedCustomTemplatePath = resolve(customTemplatePath);
			String locationAsStringPath = PathHelper.getLocationAsStringPath(resolvedCustomTemplatePath);
			if(locationAsStringPath != null) {
				templatePathList.add(locationAsStringPath);
			} else {
				throw new HibernateConsoleRuntimeException("Template directory '" + resolvedCustomTemplatePath + "' in " + getExporterDefinition().getDescription() + " does not exist.");				
			}
		}
		
		exporter.setTemplatePath((String[]) templatePathList.toArray(new String[templatePathList.size()]));
		
	
		// special handling for GenericExporter (TODO: be delegated via plugin.xml)
		if(getExporterDefinition().getId().equals("org.hibernate.tools.hbmtemplate")) {
			GenericExporter ge = (GenericExporter) exporter;
			
			ge.setFilePattern(props.getProperty("file_pattern", null));
			props.remove("file_pattern");
			ge.setTemplateName(props.getProperty("template_name",null));
			props.remove("template_name");
			ge.setForEach(props.getProperty("for_each",null));
			props.remove("for_each");
			
		}
		return exporter;
	}

}
