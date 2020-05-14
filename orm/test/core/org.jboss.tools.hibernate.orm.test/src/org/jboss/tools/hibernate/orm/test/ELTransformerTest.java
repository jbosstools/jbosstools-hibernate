package org.jboss.tools.hibernate.orm.test;

import static org.junit.Assert.assertEquals;

import org.hibernate.eclipse.jdt.ui.internal.ELTransformer;
import org.junit.Test;

public class ELTransformerTest {

	@Test
	public void testTransformer() {
		
		assertEquals("from Test", ELTransformer.removeEL("from Test"));  //$NON-NLS-1$//$NON-NLS-2$
		assertEquals("from Test where t.x = :_customer_id_", ELTransformer.removeEL("from Test where t.x = #{customer.id}"));  //$NON-NLS-1$//$NON-NLS-2$
		assertEquals("from Test where t.x = #{customer.id", ELTransformer.removeEL("from Test where t.x = #{customer.id"));  //$NON-NLS-1$//$NON-NLS-2$
		assertEquals("from Test where t.x = :_customer_id_ and x = :_id_ ", ELTransformer.removeEL("from Test where t.x = #{customer.id} and x = #{id} ")); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals("from Test where t.x = :_customer_id_and_x_____id_ ", ELTransformer.removeEL("from Test where t.x = #{customer.id and x = #{id} ")); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals("from Test where t.x = :_id_______", ELTransformer.removeEL("from Test where t.x = #{id+-&*()}"));  //$NON-NLS-1$//$NON-NLS-2$
	}
}