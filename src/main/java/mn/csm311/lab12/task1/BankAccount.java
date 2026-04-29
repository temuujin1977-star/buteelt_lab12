package mn.csm311.lab12.task1;

/**
 * Энгийн банкны дансны анги.
 */
public class BankAccount {

    private final String owner;
    private long balance; // MNT

    public BankAccount(String owner, long initialBalance) {
    
        if (owner == null || owner.isBlank()) {
            throw new IllegalArgumentException("owner must not be blank");
        }
        
    
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

    public void withdraw(long amount) {
        
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }

    
        if (this.balance < amount) {
            throw new InsufficientFundsException("insufficient funds: balance=" + balance + ", requested=" + amount);
        }

        this.balance -= amount;

    
        assert this.balance >= 0 : "invariant violated: balance < 0";
    }

    public static void transfer(BankAccount from, BankAccount to, long amount) {
        
        if (from == null || to == null) {
            throw new IllegalArgumentException("account must not be null");
        }

    
        if (from == to) {
            throw new IllegalArgumentException("cannot transfer to the same account");
        }

        from.withdraw(amount); 
        to.deposit(amount);
    }

    public void deposit(long amount) {
        
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        
        this.balance += amount;
    }
}