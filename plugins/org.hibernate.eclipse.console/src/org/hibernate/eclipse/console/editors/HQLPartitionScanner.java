package org.hibernate.eclipse.console.editors;

import org.eclipse.jface.text.rules.*;

public class HQLPartitionScanner extends RuleBasedPartitionScanner {
	public final static String HQL_DEFAULT = "__hql_default";
	public final static String HQL_COMMENT = "__xml_comment";
	public final static String HQL_TAG = "__hql_tag";

	public HQLPartitionScanner() {

		IToken xmlComment = new Token(HQL_COMMENT);
		IToken tag = new Token(HQL_TAG);

		IPredicateRule[] rules = new IPredicateRule[1];

		rules[0] = new MultiLineRule("/*", "*/", xmlComment);
		
		setPredicateRules(rules);
	}
}
