package blockChainClasses;

import java.util.Objects;
import java.util.Random;

public class Transaction {
    private final String fromId;
    private final String toId;
    private final double amount;
    private final long timestamp;
    private final int index;
    private static int counter = new Random().nextInt();
    public String getFromId() {
        return fromId;
    }

    public String getToId() {
        return toId;
    }

    public double getAmount(){
        return amount;
    }

    public Transaction(String fromId, String toId, double amount, long timestamp) {
        this.fromId = fromId;
        this.toId = toId;
        this.amount = amount;
        this.timestamp = timestamp;
        this.index = ++counter;
    }
    public Transaction(String fromId, String toId, double amount) {
        this(fromId, toId, amount, System.currentTimeMillis());
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (other.getClass() != getClass()) return false;
        Transaction rhs = (Transaction) other;
        if (!Objects.equals(this.fromId, rhs.fromId) ||
                !Objects.equals(this.toId, rhs.toId) ||
                !Objects.equals(this.amount, rhs.amount) ||
                !Objects.equals(this.timestamp, rhs.timestamp))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "fromId='" + fromId + '\'' +
                ", toId='" + toId + '\'' +
                ", amount=" + amount +
                ", timestamp=" + timestamp +
                ", index="+index+
                '}';
    }

    /**
     * @param userId user
     * @return true if user is either the sender or the receiver*/
    public boolean involvesUser(String userId){
        return Objects.equals(this.fromId, userId) || Objects.equals(this.toId, userId);
    }

    public long getTimestamp() {
        return timestamp;
    }
}
