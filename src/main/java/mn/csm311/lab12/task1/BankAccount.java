package mn.csm311.lab12.task1;

/**
 * Энгийн банкны дансны анги.
 */
public class BankAccount {

    private final String owner;
    private long balance; // MNT

    public BankAccount(String owner, long initialBalance) {
        // TODO 1.1: owner null эсвэл хоосон (blank) бол IllegalArgumentException шид.
        if (owner == null || owner.isBlank()) {
            throw new IllegalArgumentException("owner must not be blank");
        }
        
        // TODO 1.2: initialBalance < 0 бол IllegalArgumentException шид.
        if (initialBalance < 0) {
            throw new IllegalArgumentException("initialBalance must be non-negative");
        }

        this.owner = owner;
        this.balance = initialBalance;
    }

    public String owner() {
        return owner;
    }

    public long balance() {
        return balance;
    }

    /**
     * Дансны үлдэгдлээс мөнгө гаргах.
     */
    public void withdraw(long amount) {
        // TODO 1.3: amount <= 0 бол IllegalArgumentException шид.
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }

        // TODO 1.4: balance < amount бол InsufficientFundsException шид.
        if (this.balance < amount) {
            throw new InsufficientFundsException("insufficient funds: balance=" + balance + ", requested=" + amount);
        }

        this.balance -= amount;

        // TODO 1.5: invariant шалгах assert ашиглах:
        assert this.balance >= 0 : "invariant violated: balance < 0";
    }

    /**
     * Нэг данснаас өөр данс руу мөнгө шилжүүлэх.
     */
    public static void transfer(BankAccount from, BankAccount to, long amount) {
        // TODO 1.6: from эсвэл to нь null бол IllegalArgumentException шид.
        if (from == null || to == null) {
            throw new IllegalArgumentException("account must not be null");
        }

        // TODO 1.7: from == to бол IllegalArgumentException шид
        if (from == to) {
            throw new IllegalArgumentException("cannot transfer to the same account");
        }

        // TODO 1.8: withdraw → дараа нь to.deposit хийх.
        // Эндээс withdraw() дуудагдахад доторх шалгалтууд (Preconditions) хийгдэнэ.
        from.withdraw(amount); 
        to.deposit(amount);
    }

    /**
     * Дансанд мөнгө нэмэх.
     */
    public void deposit(long amount) {
        // TODO 1.9: amount <= 0 бол IllegalArgumentException шид.
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        
        this.balance += amount;
    }
}