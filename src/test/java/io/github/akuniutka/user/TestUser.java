package io.github.akuniutka.user;

import io.github.akuniutka.config.ApplicationTestConfig;
import io.github.akuniutka.user.entity.User;
import org.springframework.test.util.ReflectionTestUtils;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

public final class TestUser {

    public static final UUID ID = UUID.fromString("92f08b0a-4302-40ff-823d-b9ce18522552");
    public static final String FIRST_NAME = "John";
    public static final String LAST_NAME = "Doe";
    public static final String EMAIL = "john@mail.com";
    public static final String UPPERCASE_EMAIL = EMAIL.toUpperCase();
    public static final User.State STATE = User.State.ACTIVE;
    public static final Instant REGISTRATION_DATE = ApplicationTestConfig.FIXED_TIME;
    public static final Timestamp MODIFIED = Timestamp.from(Instant.parse("2002-03-04T05:06:07.890123Z"));
    public static final String OTHER_FIRST_NAME = "Jack";
    public static final String OTHER_LAST_NAME = "Sparrow";
    public static final String OTHER_EMAIL = "jack@mail.com";
    public static final String OTHER_EMAIL_UPPERCASE = OTHER_EMAIL.toUpperCase();
    public static final User.State OTHER_STATE = User.State.BLOCKED;
    public static final String NON_EXISTING_EMAIL = "zui@mail.com";

    private TestUser() {
        throw new AssertionError();
    }

    public static User fresh() {
        final User user = new User(ID);
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        user.setEmail(EMAIL);
        return user;
    }

    public static User base() {
        final User user = new User(ID);
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        user.setEmail(EMAIL);
        user.setState(STATE);
        user.setRegistrationDate(REGISTRATION_DATE);
        return user;
    }

    public static User persisted() {
        final User user = base();
        ReflectionTestUtils.setField(user, "modified", MODIFIED);
        return user;
    }

    public static User patched() {
        final User user = persisted();
        user.setFirstName(OTHER_FIRST_NAME);
        user.setLastName(OTHER_LAST_NAME);
        user.setEmail(OTHER_EMAIL);
        user.setState(OTHER_STATE);
        return user;
    }

    public static User patchedWithUppercaseEmail() {
        final User user = patched();
        user.setEmail(UPPERCASE_EMAIL);
        return user;
    }

    public static User patchedWithFirstNameOnly() {
        final User user = persisted();
        user.setFirstName(OTHER_FIRST_NAME);
        return user;
    }

    public static User patchedWithLastNameOnly() {
        final User user = persisted();
        user.setLastName(OTHER_LAST_NAME);
        return user;
    }

    public static User patchedWithNewEmailOnly() {
        final User user = persisted();
        user.setEmail(OTHER_EMAIL);
        return user;
    }

    public static User patchedWithNewEmailUppercaseOnly() {
        final User user = persisted();
        user.setEmail(OTHER_EMAIL_UPPERCASE);
        return user;
    }

    public static User patchedWithUppercaseEmailOnly() {
        final User user = persisted();
        user.setEmail(UPPERCASE_EMAIL);
        return user;
    }

    public static User patchedWithStateOnly() {
        final User user = persisted();
        user.setState(OTHER_STATE);
        return user;
    }

    public static User deleted() {
        final User user = persisted();
        user.setState(User.State.DELETED);
        return user;
    }

    public static User patch() {
        final User user = new User(ID);
        user.setFirstName(OTHER_FIRST_NAME);
        user.setLastName(OTHER_LAST_NAME);
        user.setEmail(OTHER_EMAIL);
        user.setState(OTHER_STATE);
        return user;
    }

    public static User patchWithUppercaseEmail() {
        final User patch = patch();
        patch.setEmail(UPPERCASE_EMAIL);
        return patch;
    }

    public static User patchWithFirstNameOnly() {
        final User patch = new User(ID);
        patch.setFirstName(OTHER_FIRST_NAME);
        return patch;
    }

    public static User patchWithLastNameOnly() {
        final User patch = new User(ID);
        patch.setLastName(OTHER_LAST_NAME);
        return patch;
    }

    public static User patchWithNewEmailOnly() {
        final User patch = new User(ID);
        patch.setEmail(OTHER_EMAIL);
        return patch;
    }

    public static User patchWithUppercaseEmailOnly() {
        final User patch = new User(ID);
        patch.setEmail(UPPERCASE_EMAIL);
        return patch;
    }

    public static User patchWithStateOnly() {
        final User patch = new User(ID);
        patch.setState(OTHER_STATE);
        return patch;
    }

    public static User patchWithEmptyFields() {
        return new User(ID);
    }
}
