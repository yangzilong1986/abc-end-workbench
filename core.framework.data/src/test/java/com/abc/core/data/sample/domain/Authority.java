package com.abc.core.data.sample.domain;
//import org.springframework.security.core.GrantedAuthority;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name="authority")
public class Authority  implements java.io.Serializable{
	@Id
	@GeneratedValue
	private Long id;
	@Column(length = 64,nullable = false)
	private String name;
	@Column(length = 64,nullable = false,unique = true)
	private String authority;
	@ManyToMany(fetch = FetchType.LAZY,mappedBy = "authorities")
	private Set<User> users=new HashSet<User>();
	@ManyToMany(fetch = FetchType.LAZY,mappedBy = "authorities")
	private Set<Role> roles=new HashSet<Role>();
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
	public Set<Role> getRoles() {
		return roles;
	}
	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
	public String getAuthority() {
		return authority;
	}
	public void setAuthority(String authority) {
		this.authority = authority;
	}
	@Override
	public boolean equals(Object o) {
		if(o instanceof Authority){
			return authority.equals(((Authority)o).authority);
		}
		return false;
	}
	@Override
	public int hashCode() {
		return authority.hashCode();
	}
}
