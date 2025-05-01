package io.github.akuniutka.user.entity;

import io.github.akuniutka.common.entity.BaseHibernateEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class User extends BaseHibernateEntity {

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    private String email;

    @Enumerated(EnumType.STRING)
    private State state;

    @Column(name = "registration_date")
    private Instant registrationDate;

    public User(final UUID id) {
        super(id);
    }

    public enum State {
        ACTIVE,
        BLOCKED,
        DELETED
    }
}
