package org.gen;

// Generated Mar 5, 2015 8:03:38 AM by Hibernate Tools 4.3.1

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Customer generated by hbm2java
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "CUSTOMER", schema = "PUBLIC", catalog = "SAKILA")
public class Customer implements java.io.Serializable {

	private short customerId;
	private Address address;
	private Store store;
	private String firstName;
	private String lastName;
	private String email;
	private boolean active;
	private Date createDate;
	private Date lastUpdate;
	private Set<Payment> payments = new HashSet<Payment>(0);
	private Set<Payment> payments_1 = new HashSet<Payment>(0);
	private Set<Rental> rentals = new HashSet<Rental>(0);
	private Set<Rental> rentals_1 = new HashSet<Rental>(0);

	public Customer() {
	}

	public Customer(short customerId, Address address, Store store,
			String firstName, String lastName, boolean active, Date createDate,
			Date lastUpdate) {
		this.customerId = customerId;
		this.address = address;
		this.store = store;
		this.firstName = firstName;
		this.lastName = lastName;
		this.active = active;
		this.createDate = createDate;
		this.lastUpdate = lastUpdate;
	}

	public Customer(short customerId, Address address, Store store,
			String firstName, String lastName, String email, boolean active,
			Date createDate, Date lastUpdate, Set<Payment> payments,
			Set<Payment> payments_1, Set<Rental> rentals, Set<Rental> rentals_1) {
		this.customerId = customerId;
		this.address = address;
		this.store = store;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.active = active;
		this.createDate = createDate;
		this.lastUpdate = lastUpdate;
		this.payments = payments;
		this.payments_1 = payments_1;
		this.rentals = rentals;
		this.rentals_1 = rentals_1;
	}

	@Id
	@Column(name = "CUSTOMER_ID", unique = true, nullable = false)
	public short getCustomerId() {
		return this.customerId;
	}

	public void setCustomerId(short customerId) {
		this.customerId = customerId;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ADDRESS_ID", nullable = false)
	public Address getAddress() {
		return this.address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "STORE_ID", nullable = false)
	public Store getStore() {
		return this.store;
	}

	public void setStore(Store store) {
		this.store = store;
	}

	@Column(name = "FIRST_NAME", nullable = false, length = 45)
	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@Column(name = "LAST_NAME", nullable = false, length = 45)
	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Column(name = "EMAIL", length = 50)
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name = "ACTIVE", nullable = false)
	public boolean isActive() {
		return this.active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATE_DATE", nullable = false, length = 23)
	public Date getCreateDate() {
		return this.createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LAST_UPDATE", nullable = false, length = 23)
	public Date getLastUpdate() {
		return this.lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "customer")
	public Set<Payment> getPayments() {
		return this.payments;
	}

	public void setPayments(Set<Payment> payments) {
		this.payments = payments;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "customer")
	public Set<Payment> getPayments_1() {
		return this.payments_1;
	}

	public void setPayments_1(Set<Payment> payments_1) {
		this.payments_1 = payments_1;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "customer")
	public Set<Rental> getRentals() {
		return this.rentals;
	}

	public void setRentals(Set<Rental> rentals) {
		this.rentals = rentals;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "customer")
	public Set<Rental> getRentals_1() {
		return this.rentals_1;
	}

	public void setRentals_1(Set<Rental> rentals_1) {
		this.rentals_1 = rentals_1;
	}

}
