package org.hibernate.ui.test.model;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class User {
   private Long id;
   @Id public Long getId() { return id; }
   public void setId(Long id) { this.id = id; }

   private Address address;
   @Embedded public Address getAddress() { return address; }
   public void setAddress(Address address) { this.address = address; }
}