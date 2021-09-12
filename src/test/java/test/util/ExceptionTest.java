package test.util;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ExceptionTest {
    public static void expectException(Class<?> e, Runnable runnable, String message) {
        try {
            runnable.run();
        } catch (Exception exception) {
            if(e.isInstance(exception)) {
                if(message == null || exception.getMessage().equals(message))
                    return;
                else
                    throw new AssertionError("Expected message '%s' from exception got message '%s' instead".formatted(message, exception.getMessage()));
            } else {
                throw new AssertionError("Expected exception '%s' got exception '%s' instead".formatted(e.getName(), exception.getClass().getName()));
            }
        }

        throw new AssertionError("Expected exception '%s' but no exception was thrown");
    }

    public static <T> void expectException(Class<?> e, Consumer<T> consumer, T argument, String message) {
        try {
            consumer.accept(argument);
        } catch (Exception exception) {
            if(e.isInstance(exception)) {
                if(message == null || exception.getMessage().equals(message))
                    return;
                else
                    throw new AssertionError("Expected message '%s' from exception got message '%s' instead".formatted(message, exception.getMessage()));
            } else {
                throw new AssertionError("Expected exception '%s' got exception '%s' instead".formatted(e.getName(), exception.getClass().getName()));
            }
        }

        throw new AssertionError("Expected exception '%s' but no exception was thrown");
    }

    public static <T> void expectException(Class<?> e, Supplier<T> supplier, String message) {
        try {
            supplier.get();
        } catch (Exception exception) {
            if(e.isInstance(exception)) {
                if((message == null && exception.getMessage() == null) || (message != null && exception.getMessage() != null && exception.getMessage().equals(message)))
                    return;
                else
                    throw new AssertionError("Expected message '%s' from exception got message '%s' instead".formatted(message, exception.getMessage()));
            } else {
                throw new AssertionError("Expected exception '%s' got exception '%s' instead".formatted(e.getName(), exception.getClass().getName()));
            }
        }

        throw new AssertionError("Expected exception '%s' but no exception was thrown");
    }

    public static <A, R> void expectException(Class<?> e, Function<A, R> function, A argument, String message) {
        try {
            function.apply(argument);
        } catch (Exception exception) {
            if(e.isInstance(exception)) {
                if(message == null || exception.getMessage().equals(message))
                    return;
                else
                    throw new AssertionError("Expected message '%s' from exception got message '%s' instead".formatted(message, exception.getMessage()));
            } else {
                throw new AssertionError("Expected exception '%s' got exception '%s' instead".formatted(e.getName(), exception.getClass().getName()));
            }
        }

        throw new AssertionError("Expected exception '%s' but no exception was thrown");
    }
}
