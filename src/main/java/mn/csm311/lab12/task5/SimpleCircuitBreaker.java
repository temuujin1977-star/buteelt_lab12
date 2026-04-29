package mn.csm311.lab12.task5;

import java.util.function.Supplier;

/**
 * ДААЛГАВАР 5: Энгийн Circuit Breaker хэрэгжүүлэлт.
 */
public class SimpleCircuitBreaker {

    public enum State { CLOSED, OPEN, HALF_OPEN }

    public interface Clock {
        long now();
    }

    public static class SystemClock implements Clock {
        @Override
        public long now() { return System.currentTimeMillis(); }
    }

    private final int failureThreshold;
    private final long resetTimeoutMs;
    private final Clock clock;

    private State state = State.CLOSED;
    private int failureCount = 0;
    private long openedAt = 0L;

    public SimpleCircuitBreaker(int failureThreshold, long resetTimeoutMs) {
        this(failureThreshold, resetTimeoutMs, new SystemClock());
    }

    public SimpleCircuitBreaker(int failureThreshold, long resetTimeoutMs, Clock clock) {
        if (failureThreshold < 1) throw new IllegalArgumentException("failureThreshold >= 1");
        if (resetTimeoutMs < 0) throw new IllegalArgumentException("resetTimeoutMs >= 0");
        this.failureThreshold = failureThreshold;
        this.resetTimeoutMs = resetTimeoutMs;
        this.clock = clock;
    }

    public State state() {
        if (state == State.OPEN && clock.now() - openedAt >= resetTimeoutMs) {
            state = State.HALF_OPEN;
        }
        return state;
    }

    public <T> T execute(Supplier<T> op) {
        State current = state();

        if (current == State.OPEN) {
            throw new CircuitBreakerOpenException("circuit is open");
        }

        try {
            T result = op.get();
            onSuccess();
            return result;
        } catch (RuntimeException e) {
            onFailure();
            throw e;
        }
    }

    private void onSuccess() {
        this.state = State.CLOSED;
        this.failureCount = 0;
    }

    private void onFailure() {
        if (state == State.HALF_OPEN) {
            this.state = State.OPEN;
            this.openedAt = clock.now();
            this.failureCount = 0;
        } else if (state == State.CLOSED) {
            this.failureCount++;
            if (this.failureCount >= failureThreshold) {
                this.state = State.OPEN;
                this.openedAt = clock.now();
            }
        }
    }
}