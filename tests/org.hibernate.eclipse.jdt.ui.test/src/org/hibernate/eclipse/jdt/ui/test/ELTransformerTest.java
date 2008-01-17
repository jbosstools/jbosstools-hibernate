package org.hibernate.eclipse.jdt.ui.test;

import org.hibernate.eclipse.jdt.ui.internal.ELTransformer;

import junit.framework.TestCase;

public class ELTransformerTest extends TestCase {

	public void testTransformer() {
		
		assertEquals("from Test", ELTransformer.removeEL("from Test"));
		assertEquals("from Test where t.x = :_customer_id_", ELTransformer.removeEL("from Test where t.x = #{customer.id}"));
		assertEquals("from Test where t.x = #{customer.id", ELTransformer.removeEL("from Test where t.x = #{customer.id"));
		assertEquals("from Test where t.x = :_customer_id_ and x = :_id_ ", ELTransformer.removeEL("from Test where t.x = #{customer.id} and x = #{id} "));
		assertEquals("from Test where t.x = :_customer_id_and_x_____id_ ", ELTransformer.removeEL("from Test where t.x = #{customer.id and x = #{id} "));
		assertEquals("from Test where t.x = :_id_______", ELTransformer.removeEL("from Test where t.x = #{id+-&*()}"));
	}
}