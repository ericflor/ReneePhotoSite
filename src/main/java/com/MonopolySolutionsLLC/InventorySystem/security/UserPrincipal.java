package com.MonopolySolutionsLLC.InventorySystem.security;

import com.MonopolySolutionsLLC.InventorySystem.model.Agency;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class UserPrincipal implements UserDetails {

    private final Long id;
    private final String username;
    private final String password;
    private final String role; // Store the role

    public UserPrincipal(Agency user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.role = String.valueOf(user.getRole()); // Initialize role
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Use the role field here
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + this.role));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    // Ensure these methods return true if the account is in good standing
    @Override
    public boolean isAccountNonExpired() {
        return true; // Update as necessary
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Update as necessary
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Update as necessary
    }

    @Override
    public boolean isEnabled() {
        return true; // Update as necessary
    }

    public Long getId() {
        return id;
    }
}
