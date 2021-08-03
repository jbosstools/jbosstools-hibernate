package org.hibernate.ui.test.model;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;



@Entity
public class Order {
   @Id private Long id;
   public Long getId() { return id; }
   public void setId(Long id) { this.id = id; }

   @Embedded private Address address;
   public Address getAddress() { return address; }
   public void setAddress(Address address) { this.address = address; }
}