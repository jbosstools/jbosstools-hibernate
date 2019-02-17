package entity;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;

public class ManyToMany1 implements Serializable {
	
	private int id1;	
	
	private JustData justData1;

	private Set<ManyToMany2> mtm2;
}