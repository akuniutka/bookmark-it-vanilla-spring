package io.github.akuniutka.util;

import org.hamcrest.Matchers;
import org.mockito.ArgumentMatchers;
import org.mockito.hamcrest.MockitoHamcrest;

import java.util.List;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.samePropertyValuesAs;

public final class TestUtils {

    private TestUtils() {
    }

    public static <T> T deepEqual(final T object) {
        return MockitoHamcrest.argThat(Matchers.samePropertyValuesAs(object));
    }

    public static <T> List<T> refContains(final T element) {
        return ArgumentMatchers.argThat(argument -> contains(samePropertyValuesAs(element)).matches(argument));
    }
}
