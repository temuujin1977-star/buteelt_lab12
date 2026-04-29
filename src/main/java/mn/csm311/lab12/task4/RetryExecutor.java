package mn.csm311.lab12.task4;

import java.util.function.Supplier;

/**
 * ДААЛГАВАР 4: Exponential backoff + jitter-тэй retry хэрэгжүүлэгч.
 *
 * Зорилго: Түр зуурын (transient) алдааг автоматаар дахин оролдож,
 * дуудлагын хооронд хүлээх хугацааг exponential байдлаар нэмэгдүүлнэ.
 *
 * Алхамууд:
 *  - Эхний оролдлого: оролдоно
 *  - Алдаа гарсан бол: base * 2^(attempt-1) ms хүлээнэ + 0..base ms санамсаргүй
 *    (энэ нь "thundering herd"-оос сэргийлдэг jitter)
 *  - maxAttempts хүртэл давтана
 *  - NonRetryableException гарвал шууд throw — дахин оролдохгүй
 *  - Бүх оролдлого бүтэлгүйтвэл хамгийн сүүлийн exception-ыг throw
 */
public class RetryExecutor {

    private final int maxAttempts;
    private final long baseDelayMs;
    private final Sleeper sleeper;

    /**
     * Sleeper нь Thread.sleep-ыг шууд дуудахын оронд тест боломжтой болгосон.
     * Production-д RealSleeper, тестэд RecordingSleeper ашиглана.
     */
    public interface Sleeper {
        void sleep(long millis) throws InterruptedException;
    }

    public static class RealSleeper implements Sleeper {
        @Override
        public void sleep(long millis) throws InterruptedException {
            Thread.sleep(millis);
        }
    }

    public RetryExecutor(int maxAttempts, long baseDelayMs) {
        this(maxAttempts, baseDelayMs, new RealSleeper());
    }

    public RetryExecutor(int maxAttempts, long baseDelayMs, Sleeper sleeper) {
        if (maxAttempts < 1) {
            throw new IllegalArgumentException("maxAttempts must be >= 1");
        }
        if (baseDelayMs < 0) {
            throw new IllegalArgumentException("baseDelayMs must be >= 0");
        }
        this.maxAttempts = maxAttempts;
        this.baseDelayMs = baseDelayMs;
        this.sleeper = sleeper;
    }

   
    public <T> T execute(Supplier<T> op) {
        RuntimeException lastError = null;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return op.get();
            } catch (NonRetryableException e) {
                
                throw e;
            } catch (RuntimeException e) {
                lastError = e;

                // Хэрэв хамгийн сүүлийн оролдлого бол дахин хүлээхгүйгээр давталтаас гарна
                if (attempt == maxAttempts) {
                    break;
                }

                // Exponential backoff тооцоолох: base * 2^(attempt-1)
                long exponentialDelay = (long) (baseDelayMs * Math.pow(2, attempt - 1));
                
                // Jitter нэмэх: 0-ээс baseDelayMs хүртэлх санамсаргүй хугацаа
                long jitter = (long) (Math.random() * baseDelayMs);
                
                long totalDelay = exponentialDelay + jitter;

                try {
                    sleeper.sleep(totalDelay);
                } catch (InterruptedException ie) {
                    // Хэрэв thread тасалдвал (interrupt) логикийг зогсооно
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Retry interrupted", ie);
                }
            }
        }

        throw lastError;
    }
}
