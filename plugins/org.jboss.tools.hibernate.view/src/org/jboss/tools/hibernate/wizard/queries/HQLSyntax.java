/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.hibernate.wizard.queries;

/**
 * @author Konstantin Mishin
 *
 */
public class HQLSyntax {
	
	public static final String HQL_SEPARATORS = " \n\r\f\t,()=<>&|+-=/*'^![]#~\\";
	
	public static final String WORDS[] = {"ACCESS",
		"ADD",
		"ALL",
		"ALTER",
		"AND",
		"ANY",
		"AS",
		"ASC",
		"AUDIT",
		
		"BETWEEN",
		"BY",
		
		"CHAR",
		"CHECK",
		"CLASS",
		"CLUSTER",
		"COLUMN",
		"COMMENT",
		"COMPRESS",
		"CONNECT",
		"CREATE",
		"CURRENT",
		"CURSOR",
		
		"DATE",
		"DECIMAL",
		"DECLARE",
		"DEFAULT",
		"DELETE",
		"DESC",
		"DISTINCT",
		"DROP",
		
		"ELSE",
		"EXCLUSIVE",
		"EXISTS",
		
		"FETCH",
		"FILE",
		"FLOAT",
		"FOR",
		"FROM",
		"FULL",
		
		"GRANT",
		"GROUP",
		
		"HAVING",
		
		"IDENTIFIED",
		"ILIKE",
		"IMMEDIATE",
		"IN",
		"INCREMENT",
		"INDEX",
		"INITIAL",
		"INNER",
		"INSERT",
		"IS",
		
		"JOIN",
		
		"LEVEL",
		"LEFT",
		"LIKE",
		"LOCK",
		"LONG",
		
		"MAXEXTENTS",
		"MINUS",
		"MODE",
		"MODIFY",
		
		"NOAUDIT",
		"NEW",
		"NOCOMPRESS",
		"NOT",
		"NOWAIT",
		"NULL",
		"NUMBER",
		
		"OF",
		"OFFLINE",
		"ON",
		"ONLINE",
		"ORDER",
		"OR",
		"OUTER",
		
		"PRIOR",
		"PRIVILEGES",
		"PUBLIC",
		"RAW",
		"REGEXP",
		"RENAME",
		"RESOURCE",
		"REVOKE",
		"RIGHT",
		"RLIKE",
		"ROW",
		"ROWID",
		"ROWLABEL",
		"ROWNUM",
		"ROWS",
		
		"SELECT",
		"SESSION",
		"SET",
		"SHARE",
		"SIZE",
		"SMALLINT",
		"SOME",
		"START",
		"SUCCESSFUL",
		"SYNONYM",
		"SYSDATE",
		
		"TABLE",
		"THEN",
		"THIS",
		"TO",
		"TRIGGER",
		
		"UID",
		"UNION",
		"UNIQUE",
		"UPDATE",
		"USER",
		
		"VALIDATE",
		"VALUES",
		"VARCHAR",
		"VARCHAR2",
		"VIEW",
		
		"WHENEVER",
		"WHERE",
		"WITH"};
}
