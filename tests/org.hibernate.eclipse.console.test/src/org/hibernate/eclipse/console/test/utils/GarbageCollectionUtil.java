/*******************************************************************************
 * Copyright (c) 2007-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.hibernate.eclipse.console.test.utils;

/**
 * @author Vitali Yemialyanchyk
 */
public class GarbageCollectionUtil {
	
	static public int forceCollectGarbage() {
		int numGCCall = 0;
		long freeMemory = 0;
		long freeMemoryAfter = freeMemory;
		do {
			freeMemory = Runtime.getRuntime().freeMemory();
			System.gc();
			numGCCall++;
			freeMemoryAfter = Runtime.getRuntime().freeMemory();
		} while (freeMemoryAfter - freeMemory != 0);
		return numGCCall;
	}

}
