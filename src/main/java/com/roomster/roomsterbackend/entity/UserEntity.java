package com.roomster.roomsterbackend.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.util.*;

@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = "phone_number"))
@Getter
@Setter
@Data
public class UserEntity implements UserDetails {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @CreatedDate
    @Column(
            nullable = false,
            updatable = false
    )
    private Date createdDate;
    @LastModifiedDate
    @Column(
            insertable = false
    )
    private Date modifiedDate;
    @CreatedBy
    @Column(
            nullable = false,
            updatable = false
    )
    private Long createdBy;
    @LastModifiedBy
    @Column(
            insertable = false
    )
    private Long modifiedBy;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "email")
    private String email;

    @Column(name = "images")
    private String images;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "phone_number_confirmed")
    private boolean phoneNumberConfirmed;

    @Column(name = "two_factor_enable")
    private boolean twoFactorEnable;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @Column(name = "date_of_birth")
    private Date dateOfBirth;

    @Column(name = "address")
    private String address;

    @Column(name = "balance")
    private BigDecimal balance;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<RoleEntity> roles = new HashSet<>();

    @OneToMany(mappedBy = "userToken")
    private List<TokenEntity> tokens;

    @OneToMany(mappedBy = "authorId")
    private List<PostEntity> posts;

    @OneToMany(mappedBy = "userWishList")
    @JsonManagedReference
    private List<WishlistEntity> wishlists;

    @OneToMany(mappedBy = "userPayment")
    @JsonManagedReference
    private List<PaymentEntity> payments;

    @OneToMany(mappedBy = "userTransaction")
    @JsonManagedReference
    private List<TransactionEntity> transactionEntities;

    public UserEntity() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void addRole(RoleEntity role){
        this.roles.add(role);
        role.getUsers().add(this);
    }

    public void removeRole(RoleEntity role){
        this.roles.remove(role);
        role.getUsers().remove(this);
    }

    @JsonManagedReference
    public Set<RoleEntity> getRoles() {
        return roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (RoleEntity role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.passwordHash;
    }

    @Override
    public String getUsername() {
        return this.phoneNumber;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
