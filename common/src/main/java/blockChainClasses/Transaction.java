package blockChainClasses;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.Random;

public class Transaction {
    private final String fromId;
    private final String toId;
    private final double amount;
    private final long timestamp;
    private final int index;
    private static int counter = new Random().nextInt();

    public String getHash() {
        return hash;
    }

    private final String hash;

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
        counter = (++counter) % Integer.MAX_VALUE;
        this.index = counter;
        this.hash = this.generateHash();
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
        if (!Objects.equals(this.hash, rhs.hash) ||
                !Objects.equals(this.fromId, rhs.fromId) ||
                !Objects.equals(this.toId, rhs.toId) ||
                !Objects.equals(this.amount, rhs.amount) ||
                !Objects.equals(this.timestamp, rhs.timestamp) ||
                !Objects.equals(this.index, rhs.index))
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

    public String generateHash(){
        String toHash = this.fromId
                + this.toId
                + this.index
                + this.timestamp
                + this.amount;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hash = messageDigest.digest(toHash.getBytes());
            StringBuilder hexString = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
