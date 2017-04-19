package com.abc.core.data.sample.domain;

import javax.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="role")
public class Role  implements java.io.Serializable {
	@Id
	@GeneratedValue
	private Long id;
	@Column(length = 64,nullable = false)
	private String name;
	@ManyToMany(fetch = FetchType.LAZY,mappedBy = "roles")
	private Set<User> users=new HashSet<User>();
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "role_authority")
	private Set<Authority> authorities = new HashSet<Authority>();
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Set<User> getUsers() {
		return users;
	}
	public void setUsers(Set<User> users) {
		this.users = users;
	}
	public Set<Authority> getAuthorities() {
		return authorities;
	}
	public void setAuthorities(Set<Authority> authorities) {
		this.authorities = authorities;
	}
	@Override
	public boolean equals(Object o) {
		if (o instanceof Role){
			return name.equals(((Role)o).getName());
		}
		return false;
	}
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	@Override
	public String toString() {
		return "Role [id=" + id + ", name=" + name + ", users=" + users + ", authorities=" + authorities + "]";
	}
}
