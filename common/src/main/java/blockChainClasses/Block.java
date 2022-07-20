package blockChainClasses;

import com.google.gson.Gson;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

public class Block {
    public Long getTimestamp() {
        return timestamp;
    }
    private int depth;

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    private String hash;
    private String prevHash;
    public final ArrayList<Transaction> transactions;
    private final Long timestamp;
    private int nonce;

    public Block(){
        timestamp = System.currentTimeMillis();
        transactions = new ArrayList<>();
        this.nonce = 0;
    }
    public Block(String prevHash){
        timestamp = System.currentTimeMillis();
        this.prevHash = prevHash;
        this.transactions = new ArrayList<>();
        this.hash = this.generateHash();
        this.nonce = 0;
    }
    public Block(String prevHash, ArrayList<Transaction> transactions){
        timestamp = System.currentTimeMillis();
        this.prevHash = prevHash;
        this.transactions = transactions;
        this.hash = this.generateHash();
        this.nonce = 0;
    }
    public String getHash() {
        return hash;
    }

    public String getPrevHash() {
        return prevHash;
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    /**
     * This method adds a transaction to the block and recalculates its hash
     * @param transaction Transaction to be added*/
    public void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
        this.hash = this.generateHash();
    }

    public String serializeTransactionsToString(){
        return this.transactions.stream()
                .map(Transaction::toString)
                .collect(Collectors.joining(","));
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    /**
     * This method is used to generate the hash of a block.
     * @return String - the generated hash of this block.
     */
    public String generateHash(){
        String toHash = this.prevHash
                + this.timestamp.toString()
                + this.serializeTransactionsToString()
                + this.nonce;
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

    public String toJson(){
        return new Gson().toJson(this);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (other.getClass() != getClass()) return false;
        Block rhs = (Block) other;
        if (!Objects.equals(this.hash, rhs.hash) ||
                !Objects.equals(this.prevHash, rhs.prevHash) ||
                !Objects.equals(this.nonce, rhs.nonce) ||
                !Objects.equals(this.timestamp, rhs.timestamp))
            return false;
        if (!Objects.equals(this.transactions, rhs.transactions))
            return false;
        return true;
    }

    /**
     * This method is used for mining a block.
     * The block is mined when this method ends.
     * @param difficulty the difficulty of target blockchain
     * */
    public void mine(int difficulty){
        while (!(this.hash.substring(0, difficulty).
                equals("0".repeat(difficulty)))){
            this.nonce++;
            this.hash = this.generateHash();
        }
    }
}
