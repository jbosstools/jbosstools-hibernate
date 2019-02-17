package entity;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

public class JustData {
	
	private int id3;	

	Set<ManyToMany1> mtm1;

}
