package org.hibernate.eclipse.hqleditor;

import org.eclipse.jface.text.formatter.IFormattingStrategy;
import org.hibernate.eclipse.console.utils.QLFormatHelper;

public class HQLFormattingStrategy implements IFormattingStrategy {

	public void formatterStarts(String initialIndentation) {
		
	}

	public String format(String content, boolean isLineStart,
			String indentation, int[] positions) {
		return QLFormatHelper.formatForScreen(content);		
	}

	public void formatterStops() {
		

	}

}
