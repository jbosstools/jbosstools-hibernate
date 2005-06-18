package org.hibernate.eclipse.console.editors;

import org.eclipse.jface.text.*;
import org.eclipse.jface.text.rules.*;

public class HQLTagScanner extends RuleBasedScanner {

	public HQLTagScanner(ColorManager manager) {
		IToken string =
			new Token(
				new TextAttribute(manager.getColor(IHQLColorConstants.STRING) ) );

		IRule[] rules = new IRule[3];

		// Add rule for double quotes
		rules[0] = new SingleLineRule("\"", "\"", string, '\\');
		// Add a rule for single quotes
		rules[1] = new SingleLineRule("'", "'", string, '\\');
		// Add generic whitespace rule.
		rules[2] = new WhitespaceRule(new HQLWhitespaceDetector() );

		setRules(rules);
	}
}
