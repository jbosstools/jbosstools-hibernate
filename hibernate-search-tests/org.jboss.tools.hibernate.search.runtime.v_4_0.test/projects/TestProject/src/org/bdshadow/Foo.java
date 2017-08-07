package org.bdshadow;

import java.util.HashSet;
import java.util.Set;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.DocumentId;


@Indexed
public class Foo {
	
	@DocumentId
	public String id;
	public Set<String> bars = new HashSet<String>();

}
