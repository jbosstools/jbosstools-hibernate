package org.hibernate.util.xpl;

import java.io.InputStream;
import java.io.Serializable;

import org.jboss.tools.hibernate.runtime.spi.HibernateException;
import org.jboss.tools.hibernate.runtime.spi.IService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

public class DTDEntityResolver implements EntityResolver, Serializable {

	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger( DTDEntityResolver.class );

	private static final String HIBERNATE_NAMESPACE = "http://www.hibernate.org/dtd/"; //$NON-NLS-1$
	private static final String OLD_HIBERNATE_NAMESPACE = "http://hibernate.sourceforge.net/"; //$NON-NLS-1$
	private static final String USER_NAMESPACE = "classpath://"; //$NON-NLS-1$
	
	private IService service;

	public DTDEntityResolver(IService service) {
		this.service = service;
	}

	public InputSource resolveEntity(String publicId, String systemId) {
		InputSource source = null; // returning null triggers default behavior
		if ( systemId != null ) {
			log.debug( "trying to resolve system-id [" + systemId + "]" );  //$NON-NLS-1$//$NON-NLS-2$
			if ( systemId.startsWith( HIBERNATE_NAMESPACE ) ) {
				log.debug( "recognized hibernate namespace; attempting to resolve on classpath under org/hibernate/" ); //$NON-NLS-1$
				source = resolveOnClassPath( publicId, systemId, HIBERNATE_NAMESPACE );
			}
			else if ( systemId.startsWith( OLD_HIBERNATE_NAMESPACE ) ) {
				log.warn(
						"recognized obsolete hibernate namespace " + OLD_HIBERNATE_NAMESPACE + ". Use namespace " //$NON-NLS-1$ //$NON-NLS-2$
								+ HIBERNATE_NAMESPACE + " instead. Refer to Hibernate 3.6 Migration Guide!" //$NON-NLS-1$
				);
				log.debug( "attempting to resolve on classpath under org/hibernate/" ); //$NON-NLS-1$
				source = resolveOnClassPath( publicId, systemId, OLD_HIBERNATE_NAMESPACE );
			}
			else if ( systemId.startsWith( USER_NAMESPACE ) ) {
				log.debug( "recognized local namespace; attempting to resolve on classpath" ); //$NON-NLS-1$
				String path = systemId.substring( USER_NAMESPACE.length() );
				InputStream stream = resolveInLocalNamespace( path );
				if ( stream == null ) {
					log.debug( "unable to locate [" + systemId + "] on classpath" );  //$NON-NLS-1$//$NON-NLS-2$
				}
				else {
					log.debug( "located [" + systemId + "] in classpath" );  //$NON-NLS-1$//$NON-NLS-2$
					source = new InputSource( stream );
					source.setPublicId( publicId );
					source.setSystemId( systemId );
				}
			}
		}
		return source;
	}

	private InputSource resolveOnClassPath(String publicId, String systemId, String namespace) {
		InputSource source = null;
		String path = "org/hibernate/" + systemId.substring( namespace.length() ); //$NON-NLS-1$
		InputStream dtdStream = resolveInHibernateNamespace( path );
		if ( dtdStream == null ) {
			log.debug( "unable to locate [" + systemId + "] on classpath" );  //$NON-NLS-1$//$NON-NLS-2$
			if ( systemId.substring( namespace.length() ).indexOf( "2.0" ) > -1 ) { //$NON-NLS-1$
				log.error( "Don't use old DTDs, read the Hibernate 3.x Migration Guide!" ); //$NON-NLS-1$
			}
		}
		else {
			log.debug( "located [" + systemId + "] in classpath" );  //$NON-NLS-1$//$NON-NLS-2$
			source = new InputSource( dtdStream );
			source.setPublicId( publicId );
			source.setSystemId( systemId );
		}
		return source;
	}

	protected InputStream resolveInHibernateNamespace(String path) {
		return this.getClass().getClassLoader().getResourceAsStream( path );
	}

	protected InputStream resolveInLocalNamespace(String path) {
		try {
			return getUserResourceAsStream( path );
		}
		catch ( Throwable t ) {
			return null;
		}
	}
	
	private InputStream getUserResourceAsStream(String resource) {
		boolean hasLeadingSlash = resource.startsWith( "/" ); //$NON-NLS-1$
		String stripped = hasLeadingSlash ? resource.substring(1) : resource;

		InputStream stream = null;

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		if ( classLoader != null ) {
			stream = classLoader.getResourceAsStream( resource );
			if ( stream == null && hasLeadingSlash ) {
				stream = classLoader.getResourceAsStream( stripped );
			}
		}

		if ( stream == null && service != null) {
			stream = service.getClass().getClassLoader().getResourceAsStream( resource );
		}
		if ( stream == null && hasLeadingSlash && service != null) {
			stream = service.getClass().getClassLoader().getResourceAsStream( stripped );
		}

		if ( stream == null ) {
			throw new HibernateException( resource + " not found" ); //$NON-NLS-1$
		}

		return stream;
	}

	
}