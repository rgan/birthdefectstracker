package org.healthapps.birthdefects.model;

import com.google.appengine.repackaged.com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.healthapps.birthdefects.utils.Encryptor;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.userdetails.UserDetails;

import javax.jdo.annotations.*;
import java.util.List;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class User implements UserDetails {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    protected Long id;
    @Persistent
    protected String name;
    @Persistent
    private String password;
    @Persistent
    private String roles;
    @Persistent
    private boolean medicalProfessional;
    @Persistent
    private boolean enabled;
    @Persistent
    protected String email;

    public User() {
        this(StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, false, StringUtils.EMPTY);
    }

    public User(String name, String password, String roles, boolean medicalProfessional, String email) {
        this(null, name, password, roles, medicalProfessional, email);
    }

    public User(Long id, String name, String password, String roles, boolean medicalProfessional, String email) {
        this(id, name, password, roles, medicalProfessional, true, email);
    }

    public User(Long id, String name, String password, String roles, boolean medicalProfessional, boolean enabled, String email) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.roles = roles;
        this.medicalProfessional = medicalProfessional;
        this.enabled = enabled;
        this.email = email;
    }

    public void setRoles(String[] roles) {
        this.roles = StringUtils.join(roles, ",");
    }

    public boolean isInUserRole() {
        return roles != null && roles.contains(Role.USER.getName());
    }

    public boolean isInAdminRole() {
        return roles != null && roles.contains(Role.ADMIN.getName());
    }

    public void setMedicalProfessional(boolean medicalProfessional) {
        this.medicalProfessional = medicalProfessional;
    }

    public boolean isMedicalProfessional() {
        return medicalProfessional;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return name;
    }

    public String[] getRoles() {
        return (roles != null && roles.length() > 0) ? roles.split(",") : new String[]{};
    }

    public GrantedAuthority[] getAuthorities() {
        List<GrantedAuthority> authorities = Lists.newArrayList();
        if (roles != null) {
            String[] roleNames = roles.split(",");
            for (String role : roleNames) {
                authorities.add(new GrantedAuthorityImpl(Role.from(role).getName()));
            }
        }
        return authorities.toArray(new GrantedAuthority[authorities.size()]);
    }

    public boolean isAccountNonExpired() {
        return true;
    }

    public boolean isAccountNonLocked() {
        return true;
    }

    public boolean isCredentialsNonExpired() {
        return true;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Long getId() {
        return id;
    }

    public void updateFrom(User user) {
        this.name = user.name;
        this.password = user.password;
        this.roles = user.roles;
        this.medicalProfessional = user.medicalProfessional;
    }

    public boolean isNew() {
        return id == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (enabled != user.enabled) return false;
        if (medicalProfessional != user.medicalProfessional) return false;
        if (id != null ? !id.equals(user.id) : user.id != null) return false;
        if (name != null ? !name.equals(user.name) : user.name != null) return false;
        if (password != null ? !password.equals(user.password) : user.password != null) return false;
        if (roles != null ? !roles.equals(user.roles) : user.roles != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (roles != null ? roles.hashCode() : 0);
        result = 31 * result + (medicalProfessional ? 1 : 0);
        result = 31 * result + (enabled ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", roles='" + roles + '\'' +
                ", medicalProfessional=" + medicalProfessional +
                ", enabled=" + enabled +
                '}';
    }

    public String getEmail() {
        return email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
