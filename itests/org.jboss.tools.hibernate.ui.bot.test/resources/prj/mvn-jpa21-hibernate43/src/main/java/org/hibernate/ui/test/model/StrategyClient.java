package org.hibernate.ui.test.model;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

@Entity
public class StrategyClient {
		
	@Id
	@GenericGenerator(name = "user_id_generator", strategy="org.hibernate.ui.test.model.UserIdGenerator" )
	private long id;
}
