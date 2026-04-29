package mn.csm311.lab12.task2;

public class NullLogger implements Logger {
    @Override
    public void log(String message) {
        // Юу ч хийхгүй (Хоосон орхино)
    }

    @Override
    public int logCount() {
        // Үргэлж 0 буцаана
        return 0;
    }
}