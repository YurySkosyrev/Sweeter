package com.example.sweeter.domain;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Collection;
import java.util.Set;

/**
 * Сущность User для хранения user в БД.
 * Имя таблицы не user, т.к. Postgresql не любит, когда названия таблиц
 * совпадают с названиями внутренних переменных.
 *
 * -> @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
 * позволяет избавиться от головной боли по созданию дополнительной таблицы для хранения Enum.
 * Fetch - параметр влияет нужно ли подгружать вспомогательную значения при загрузке основной сущности.
 *
 * -> @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
 * показывает, что данное поле будет храниться в отдельной таблицы,
 * для которой мы не описывали мэппинг.
 *
 * -> @JoinColumn - таблица будет соединяться с текущей через поле user id.
 *
 * -> @Enumerated(EnumType.STRING) - показываем, что это enum и храниться он будет в виде строки.
 */

@Entity
@Table(name="usr")
@Data
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotBlank(message = "User name can`t be empty")
    private String username;
    @NotBlank(message = "Password name can`t be empty")
    private String password;
    private boolean active;

//    e-mail пользователя и код активации, чтобы подтвердить, что user владеет этим email

    @Email(message = "Email is`t correct")
    @NotBlank(message = "Email name can`t be empty")
    private String email;
    private String activationCode;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    public boolean isAdmin() {
        return roles.contains(Role.ADMIN);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles();
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
        return isActive();
    }
}
