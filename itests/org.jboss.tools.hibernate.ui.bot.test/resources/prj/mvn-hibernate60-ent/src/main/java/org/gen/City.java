package org.gen;

// Generated Mar 5, 2015 8:03:38 AM by Hibernate Tools 4.3.1

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

/**
 * City generated by hbm2java
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "CITY", schema = "PUBLIC", catalog = "SAKILA")
public class City implements java.io.Serializable {

	private short cityId;
	private Country country;
	private String city;
	private Date lastUpdate;
	private Set<Address> addresses = new HashSet<Address>(0);
	private Set<Address> addresses_1 = new HashSet<Address>(0);

	public City() {
	}

	public City(short cityId, Country country, String city, Date lastUpdate) {
		this.cityId = cityId;
		this.country = country;
		this.city = city;
		this.lastUpdate = lastUpdate;
	}

	public City(short cityId, Country country, String city, Date lastUpdate,
			Set<Address> addresses, Set<Address> addresses_1) {
		this.cityId = cityId;
		this.country = country;
		this.city = city;
		this.lastUpdate = lastUpdate;
		this.addresses = addresses;
		this.addresses_1 = addresses_1;
	}

	@Id
	@Column(name = "CITY_ID", unique = true, nullable = false)
	public short getCityId() {
		return this.cityId;
	}

	public void setCityId(short cityId) {
		this.cityId = cityId;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COUNTRY_ID", nullable = false)
	public Country getCountry() {
		return this.country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	@Column(name = "CITY", nullable = false, length = 50)
	public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LAST_UPDATE", nullable = false, length = 23)
	public Date getLastUpdate() {
		return this.lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "city")
	public Set<Address> getAddresses() {
		return this.addresses;
	}

	public void setAddresses(Set<Address> addresses) {
		this.addresses = addresses;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "city")
	public Set<Address> getAddresses_1() {
		return this.addresses_1;
	}

	public void setAddresses_1(Set<Address> addresses_1) {
		this.addresses_1 = addresses_1;
	}

}
