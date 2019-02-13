package org.hibernate.eclipse.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Properties;

import org.w3c.tidy.Tidy;

public final class XMLPrettyPrinter {

	private XMLPrettyPrinter() {
		// noop
	}

	public static void prettyPrint(InputStream in, OutputStream writer)
			throws IOException {
		Tidy tidy = getDefaultTidy();

		tidy.parse( in, writer );

	}

	static Tidy getDefaultTidy() throws IOException {
		Tidy tidy = new Tidy();

		// no output please!
		tidy.setErrout( new PrintWriter( new Writer() {
			public void close() throws IOException {
			}

			public void flush() throws IOException {
			}

			public void write(char[] cbuf, int off, int len) throws IOException {
				
			}
		} ) );

		Properties properties = new Properties();

		properties.load( XMLPrettyPrinter.class
				.getResourceAsStream( "jtidy.properties" ) ); //$NON-NLS-1$

		tidy.setConfigurationFromProps( properties );

		return tidy;
	}

}