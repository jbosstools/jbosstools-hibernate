package org.hibernate.eclipse.console.editors;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.WhitespaceRule;

public class HQLScanner extends RuleBasedScanner {

	public HQLScanner(ColorManager manager) {
		/*IToken procInstr =
			new Token(
				new TextAttribute(
					manager.getColor(IHQLColorConstants.PROC_INSTR)));*/

		IRule[] rules = new IRule[1];
		//Add rule for processing instructions
		//rules[0] = new SingleLineRule("<?", "?>", procInstr);
		// Add generic whitespace rule.
		rules[0] = new WhitespaceRule(new HQLWhitespaceDetector());

		setRules(rules);
	}
}
