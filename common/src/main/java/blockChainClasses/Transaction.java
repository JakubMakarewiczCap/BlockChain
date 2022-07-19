package blockChainClasses;

import java.util.Objects;

public class Transaction {
    private final String fromId;
    private final String toId;
    private final double amount;
    private final long timestamp;
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
                '}';
    }

    public boolean involvesUser(String user){
        return Objects.equals(this.fromId, user) || Objects.equals(this.toId, user);
    }

    public long getTimestamp() {
        return timestamp;
    }
}
