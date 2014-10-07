package org.hibernate.util.xpl;

import java.util.List;

import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.io.DOMReader;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.jboss.tools.hibernate.spi.IService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

public final class XMLHelper {
	private static final Logger log = LoggerFactory.getLogger(XMLHelper.class);

	private DOMReader domReader;
	private SAXReader saxReader;

	/**
	 * Create a dom4j SAXReader which will append all validation errors
	 * to errorList
	 */
	public SAXReader createSAXReader(String file, List<SAXParseException> errorsList, EntityResolver entityResolver) {
		SAXReader saxReader = resolveSAXReader();
		saxReader.setEntityResolver(entityResolver);
		saxReader.setErrorHandler( new ErrorLogger(file, errorsList) );
		return saxReader;
	}

	private SAXReader resolveSAXReader() {
		if ( saxReader == null ) {
			saxReader = new SAXReader();
			saxReader.setMergeAdjacentText(true);
			saxReader.setValidation(true);
		}
		return saxReader;
	}

	/**
	 * Create a dom4j DOMReader
	 */
	public DOMReader createDOMReader() {
		if (domReader==null) domReader = new DOMReader();
		return domReader;
	}

	public static class ErrorLogger implements ErrorHandler {
		private String file;
		private List<SAXParseException> errors;

		private ErrorLogger(String file, List<SAXParseException> errors) {
			this.file=file;
			this.errors = errors;
		}
		public void error(SAXParseException error) {
			log.error( "Error parsing XML: " + file + '(' + error.getLineNumber() + ") " + error.getMessage() );  //$NON-NLS-1$//$NON-NLS-2$
			errors.add(error);
		}
		public void fatalError(SAXParseException error) {
			error(error);
		}
		public void warning(SAXParseException warn) {
			log.warn( "Warning parsing XML: " + file + '(' + warn.getLineNumber() + ") " + warn.getMessage() ); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	public static Element generateDom4jElement(String elementName) {
		return DocumentFactory.getInstance().createElement( elementName );
	}

	public static void dump(Element element) {
		try {
			// try to "pretty print" it
			OutputFormat outformat = OutputFormat.createPrettyPrint();
			XMLWriter writer = new XMLWriter( System.out, outformat );
			writer.write( element );
			writer.flush();
			System.out.println( "" ); //$NON-NLS-1$
		}
		catch( Throwable t ) {
			// otherwise, just dump it
			System.out.println( element.asXML() );
		}

	}
	
	protected IService service;
	public void setService(IService service) {
		this.service = service;
	}
}