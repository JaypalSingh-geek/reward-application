package com.infy.rewards.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Customer Entity Class.
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "customers")
public class Customer implements Serializable {

	public Customer(String string) {
		// TODO Auto-generated constructor stub
	}

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "customer_id", unique = true, nullable = false)
	private String customerId;

	@NotBlank
	@Column(name = "name", nullable = false)
	private String name;

	@Email
	@Column(name = "email")
	private String email;

	public Customer(String customerId, String name, String email) {
		this.customerId = customerId;
		this.name = name;
		this.email = email;
	}

}
