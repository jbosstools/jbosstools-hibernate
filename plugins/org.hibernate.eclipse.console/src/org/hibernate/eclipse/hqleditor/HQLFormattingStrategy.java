package org.hibernate.eclipse.hqleditor;

import org.eclipse.jface.text.formatter.IFormattingStrategy;
import org.hibernate.pretty.Formatter;

public class HQLFormattingStrategy implements IFormattingStrategy {

	public void formatterStarts(String initialIndentation) {
		
	}

	public String format(String content, boolean isLineStart,
			String indentation, int[] positions) {
		return new Formatter(content).setInitialString("").setIndentString(" ").format();		
	}

	public void formatterStops() {
		

	}

}
