public class Customer extends User {
    private double balance;

    public Customer(String username, String password, double balance) {
        super(username, password);
        this.balance = balance;
    }

    public double getBalance() { return balance; }

    public void deductBalance(double amount) {
        this.balance -= amount;
    }
}
