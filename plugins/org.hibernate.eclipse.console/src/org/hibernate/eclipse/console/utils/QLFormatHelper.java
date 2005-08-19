package org.hibernate.eclipse.console.utils;

import org.hibernate.pretty.Formatter;

public class QLFormatHelper {

	static public String formatForScreen(String query) {
		return new Formatter(query).setInitialString("").setIndentString(" ").format();
	}
}
