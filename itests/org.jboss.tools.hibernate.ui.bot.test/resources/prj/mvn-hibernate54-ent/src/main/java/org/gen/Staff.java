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
 * Staff generated by hbm2java
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "STAFF", schema = "PUBLIC", catalog = "SAKILA")
public class Staff implements java.io.Serializable {

	private byte staffId;
	private Address address;
	private Store store;
	private String firstName;
	private String lastName;
	private byte[] picture;
	private String email;
	private boolean active;
	private String username;
	private String password;
	private Date lastUpdate;
	private Set<Payment> payments = new HashSet<Payment>(0);
	private Set<Store> stores = new HashSet<Store>(0);
	private Set<Rental> rentals = new HashSet<Rental>(0);
	private Set<Payment> payments_1 = new HashSet<Payment>(0);
	private Set<Store> stores_1 = new HashSet<Store>(0);
	private Set<Rental> rentals_1 = new HashSet<Rental>(0);

	public Staff() {
	}

	public Staff(byte staffId, Address address, Store store, String firstName,
			String lastName, boolean active, String username, Date lastUpdate) {
		this.staffId = staffId;
		this.address = address;
		this.store = store;
		this.firstName = firstName;
		this.lastName = lastName;
		this.active = active;
		this.username = username;
		this.lastUpdate = lastUpdate;
	}

	public Staff(byte staffId, Address address, Store store, String firstName,
			String lastName, byte[] picture, String email, boolean active,
			String username, String password, Date lastUpdate,
			Set<Payment> payments, Set<Store> stores, Set<Rental> rentals,
			Set<Payment> payments_1, Set<Store> stores_1, Set<Rental> rentals_1) {
		this.staffId = staffId;
		this.address = address;
		this.store = store;
		this.firstName = firstName;
		this.lastName = lastName;
		this.picture = picture;
		this.email = email;
		this.active = active;
		this.username = username;
		this.password = password;
		this.lastUpdate = lastUpdate;
		this.payments = payments;
		this.stores = stores;
		this.rentals = rentals;
		this.payments_1 = payments_1;
		this.stores_1 = stores_1;
		this.rentals_1 = rentals_1;
	}

	@Id
	@Column(name = "STAFF_ID", unique = true, nullable = false)
	public byte getStaffId() {
		return this.staffId;
	}

	public void setStaffId(byte staffId) {
		this.staffId = staffId;
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

	@Column(name = "PICTURE")
	public byte[] getPicture() {
		return this.picture;
	}

	public void setPicture(byte[] picture) {
		this.picture = picture;
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

	@Column(name = "USERNAME", nullable = false, length = 16)
	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Column(name = "PASSWORD", length = 40)
	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LAST_UPDATE", nullable = false, length = 23)
	public Date getLastUpdate() {
		return this.lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "staff")
	public Set<Payment> getPayments() {
		return this.payments;
	}

	public void setPayments(Set<Payment> payments) {
		this.payments = payments;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "staff")
	public Set<Store> getStores() {
		return this.stores;
	}

	public void setStores(Set<Store> stores) {
		this.stores = stores;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "staff")
	public Set<Rental> getRentals() {
		return this.rentals;
	}

	public void setRentals(Set<Rental> rentals) {
		this.rentals = rentals;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "staff")
	public Set<Payment> getPayments_1() {
		return this.payments_1;
	}

	public void setPayments_1(Set<Payment> payments_1) {
		this.payments_1 = payments_1;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "staff")
	public Set<Store> getStores_1() {
		return this.stores_1;
	}

	public void setStores_1(Set<Store> stores_1) {
		this.stores_1 = stores_1;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "staff")
	public Set<Rental> getRentals_1() {
		return this.rentals_1;
	}

	public void setRentals_1(Set<Rental> rentals_1) {
		this.rentals_1 = rentals_1;
	}

}
