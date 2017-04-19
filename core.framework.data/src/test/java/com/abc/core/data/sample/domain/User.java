package com.abc.core.data.sample.domain;
import javax.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="user")
public class User  implements java.io.Serializable{
    @Id
    @GeneratedValue
    private Long id;
    @Column(length = 64,nullable = false,unique = true)
    private String username;
    @Column(length = 64,nullable = false)
    private String password;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled=true;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_role")
    private Set<Role> roles= new HashSet<Role>();
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_authority")
    private Set<Authority> authorities = new HashSet<Authority>();
    
    private String address;
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }
    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }
    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }
    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }
    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    public Set<Role> getRoles() {
        return roles;
    }
    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
    public Set<Authority> getAuthorities() {
        return authorities;
    }
    public void setAuthorities(Set<Authority> authorities) {
        this.authorities = authorities;
    }
    @Override
    public boolean equals(Object o) {
        if (o instanceof User) {
            return username.equals(((User) o).username);
        }
        return false;
    }
    @Override
    public int hashCode() {
        return username.hashCode();
    }
	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", password=" + password + ", accountNonExpired="
				+ accountNonExpired + ", accountNonLocked=" + accountNonLocked + ", credentialsNonExpired="
				+ credentialsNonExpired + ", enabled=" + enabled + ", roles=" + roles + ", authorities=" + authorities
				+ "]";
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
}
