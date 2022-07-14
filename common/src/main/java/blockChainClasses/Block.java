package blockChainClasses;

import com.google.gson.Gson;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

public class Block {
    private String hash;
    private String prevHash;
    public ArrayList<Transaction> transactions;
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
        this.hash = this.GenerateHash();
        this.nonce = 0;
    }
    public Block(String prevHash, ArrayList<Transaction> transactions){
        timestamp = System.currentTimeMillis();
        this.prevHash = prevHash;
        this.transactions = transactions;
        this.hash = this.GenerateHash();
        this.nonce = 0;
    }
    public String getHash() {
        return hash;
    }

    public String getPrevHash() {
        return prevHash;
    }

    public Collection<Transaction> getTransactions() {
        return transactions;
    }

    public void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
        this.hash = this.GenerateHash();
    }

    public String TransactionsToString(){
        return this.transactions.stream()
                .map(Transaction::toString)
                .collect(Collectors.joining(","));
    }
    public String GenerateHash(){
        String toHash = this.prevHash
                + this.timestamp.toString()
                + this.TransactionsToString()
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

    public String ToJson(){
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

    public void Mine(int difficulty){
        while (!(this.hash.substring(0, difficulty).
                equals("0".repeat(difficulty)))){
            this.nonce++;
            this.hash = this.GenerateHash();
        }
    }
}