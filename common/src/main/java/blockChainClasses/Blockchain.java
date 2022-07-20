package blockChainClasses;

import blockChainClasses.verificationResults.BlockchainVerificationResult;
import blockChainClasses.verificationResults.BlockchainVerificationResultEnum;
import com.google.gson.Gson;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Blockchain {
    private transient final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private final LinkedList<Block> blockChain;
    private final ArrayList<Transaction> pendingTransactions;

    public boolean addPendingTransaction(Transaction pendingTransaction) {
        if (!this.verifyTransaction(pendingTransaction, true))
            return false;
        this.pendingTransactions.add(pendingTransaction);
        support.firePropertyChange("pendingTransactions", new ArrayList<Transaction>(), this.pendingTransactions); // WARNING: old value is useless

        return true;
    }

    public int difficulty;

    public int getDifficulty() {
        return difficulty;
    }

    private double miningReward;
    public LinkedList<Block> getBlockChain() {
        return blockChain;
    }

    public Blockchain(){
        this.blockChain = new LinkedList<>();
        this.blockChain.add(new Block("0", new ArrayList<>()));
        this.difficulty = 4;
        this.miningReward = 2;
        this.pendingTransactions = new ArrayList<>();
    }
    public Blockchain(int difficulty){
        this();
        this.difficulty = difficulty;
    }
    public Blockchain(int difficulty, double miningReward){
        this(difficulty);
        this.miningReward = miningReward;
    }
    /**
     * Adds a mined block to the blockchain
     * @param block Block to be added
     * @return true if block is valid, otherwise false (try using block.mine before using this method)*/
    public boolean addBlock(Block block){
        if (verifyNewBlock(block)){
            block.setDepth(this.blockChain.size());
            this.blockChain.add(block);
            support.firePropertyChange("blockChain", new LinkedList<Block>(), this.blockChain); // WARNING: old value is useless
            this.pendingTransactions.clear();
            return true;
        }
        return false;
    }
    /**
     * This method is used to retrieve a block suitable for mining
     * @param minerId The id of the miner, they will receive the mining reward
     * @return Block prepared for mining*/
    public Block getNewBlock(String minerId){
        var block = new Block(this.getLastBlock().getHash());
        block.transactions.add(new Transaction(null, minerId, this.miningReward,System.currentTimeMillis()));
        block.transactions.addAll(this.pendingTransactions);
        block.setHash(block.generateHash());
        return block;
    }
    public Block getLastBlock(){
        if (this.blockChain.size() == 0)
            return null;
        return this.blockChain.getLast();
    }

    /**
     * Checks if new block is valid
     * @param block Block to verify
     * @return true if block is valid, otherwise false*/
    public boolean verifyNewBlock(Block block){
        if (!Objects.equals(block.generateHash(), block.getHash()))
            return false;
        if (this.blockChain.size()<1)
            return true;
        if (!Objects.equals(this.getLastBlock().getHash(), block.getPrevHash()))
            return false;
        if (!(block.getHash().substring(0, difficulty).equals("0".repeat(difficulty)))
                || !(block.transactions.get(0).getAmount() == this.miningReward))
            return false;
        if (block.getTransactions().size()- 1 != this.pendingTransactions.size())
            return false;
        for (int i = 1; i<block.getTransactions().size();i++)
            if (!Objects.equals(block.getTransactions().get(i), this.pendingTransactions.get(i -1)))
                return false;
        if (block.getTransactions().get(0).getFromId()!=null
                || block.getTransactions().get(0).getAmount() != this.miningReward)
            return false;
        return true;
    }

    /**
     * This method verifies the blockChain.
     * If someone tinkers with the blockchain, this method will catch it
     * @return true if blockchain is valid, otherwise false*/ // TODO: return the invalid block
    public BlockchainVerificationResult verifyBlockChain(){
        if (this.blockChain.size() == 0)
            return new BlockchainVerificationResult(BlockchainVerificationResultEnum.VALID, null);
        if (this.blockChain.size() == 1)
            return Objects.equals(this.blockChain.getFirst().generateHash(),
                    this.blockChain.getFirst().getHash()) &&
                    this.blockChain.getFirst().getTransactions().size() == 0?
                    new BlockchainVerificationResult(BlockchainVerificationResultEnum.VALID, null) :
                    new BlockchainVerificationResult(BlockchainVerificationResultEnum.INVALID_GENESIS_BLOCK, this.blockChain.getFirst());
        Iterator<Block> i = this.blockChain.iterator();
        Block prev = i.next();
        Block current;
        while (i.hasNext()) {
            current = i.next();
            if (!Objects.equals(current.getHash(), current.generateHash()))
                return new BlockchainVerificationResult(BlockchainVerificationResultEnum.INVALID_HASH, current);
            if (!Objects.equals(prev.getHash(), current.getPrevHash()))
                return new BlockchainVerificationResult(BlockchainVerificationResultEnum.INVALID_PREV_HASH, current);
            if (!(current.getHash().substring(0, difficulty).
                    equals("0".repeat(difficulty))))
                return new BlockchainVerificationResult(BlockchainVerificationResultEnum.INVALID_HASH, current);
            if (current.getTransactions().stream().anyMatch(t -> !this.verifyTransaction(t)))
                return new BlockchainVerificationResult(BlockchainVerificationResultEnum.INVALID_TRANSACTIONS, current);
            prev = current;
        }
        return new BlockchainVerificationResult(BlockchainVerificationResultEnum.VALID, null);
    }

    public String toJson(){
        return new Gson().toJson(this);
    }

    public double getMiningReward() {
        return miningReward;
    }

    public void setMiningReward(double miningReward) {
        this.miningReward = miningReward;
    }

    public void serializeToFile(String filepath){
        String json = new Gson().toJson(this);
        try {
            FileWriter writer = new FileWriter(filepath);
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Blockchain deserializeFromFile(String filepath) throws RuntimeException{
        try {
            BufferedReader reader =  new BufferedReader(new FileReader(filepath));
            Blockchain result = new Gson().fromJson(reader, Blockchain.class);
            reader.close();
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<Transaction> getPendingTransactions() {
        return pendingTransactions;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (other.getClass() != getClass()) return false;
        Blockchain rhs = (Blockchain) other;
        if (this.blockChain.size() != rhs.blockChain.size())
            return false;
        if (!Objects.equals(this.blockChain, rhs.blockChain))
            return false;
        return true;
    }
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }
    public static boolean isDifficultyValid(int difficulty){
        return difficulty > 0;
    }

    /**
     * This method returns the user's balance including pending transactions
     * @param userId user's id
     * @return Double - user's balance*/
    public double getUserBalance(String userId){
        return this.getUserBalance(userId, true);
    }
    /**
     * This method returns the user's balance
     * @param userId user's id
     * @param includePending true if include pending transactions, false if include only mined transactions
     * @return Double - user's balance*/
    public double getUserBalance(String userId, boolean includePending){
        double balance=0;
        for (Transaction transaction :
                this.getAllTransactions(includePending)) {
            if (Objects.equals(userId, transaction.getFromId()))
                balance -= transaction.getAmount();
            if (Objects.equals(transaction.getToId(), userId))
                balance += transaction.getAmount();
        }
        return balance;
    }
    public double getUserBalance(String userId, Transaction limitingTransaction){
        double balance=0;
        for (Transaction transaction :
                this.getAllTransactions(limitingTransaction)) {
            if (Objects.equals(userId, transaction.getFromId()))
                balance -= transaction.getAmount();
            if (Objects.equals(transaction.getToId(), userId))
                balance += transaction.getAmount();
        }
        return balance;
    }

    public boolean transactionInBlockChain(Transaction transaction){
        var allTransactions = Stream.concat(
                this.blockChain.stream().flatMap(block -> block.getTransactions().stream()),
                this.pendingTransactions.stream());
        return allTransactions
                .filter(t -> Objects.equals(t, transaction))
                .findFirst().orElse(null) != null;
    }
    public ArrayList<Transaction> getAllTransactions(boolean includePending){
        return includePending?
                Stream.concat(
                    this.blockChain.stream().flatMap(block -> block.getTransactions().stream()),
                    this.pendingTransactions.stream())
                .collect(Collectors.toCollection(ArrayList::new))
                    :
                this.blockChain.stream().flatMap(block -> block.getTransactions().stream())
                        .collect(Collectors.toCollection(ArrayList::new));
    }
    public ArrayList<Transaction> getAllTransactions(Transaction limitingTransaction){
        return Stream.concat(
                this.blockChain.stream().flatMap(block -> block.getTransactions().stream()),
                this.pendingTransactions.stream())
                .takeWhile(t -> !Objects.equals(t, limitingTransaction))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Checks if transaction is valid, for new transactions use verifyTransaction(transaction, true)
     * @param transaction Transaction to verify
     * @return true if transaction is valid, otherwise false*/
    public boolean verifyTransaction(Transaction transaction){
        return this.verifyTransaction(transaction, false);
    }
    /**
     * Checks if transaction is valid
     * @param transaction Transaction to verify
     * @param isNewTransaction true if this is the most recently added transaction
     * @return true if transaction is valid, otherwise false*/
    public boolean verifyTransaction(Transaction transaction, boolean isNewTransaction){
        if (isNewTransaction && transactionInBlockChain(transaction))
            return false;
        if (transaction.getFromId() != null) {
            if (transaction.getAmount() <= 0)
                return false;
            if (Objects.equals(transaction.getFromId(), transaction.getToId()))
                return false;
            if (isNewTransaction) {
                if (getUserBalance(transaction.getFromId(), true) < transaction.getAmount())
                    return false;
            }
            else {
                if (getUserBalance(transaction.getFromId(), transaction) < transaction.getAmount())
                    return false;
            }
        }
        return true;
    }

    /**
     * Use this method to obtain user's transaction history
     * @param userId user's id
     * @return List of all transactions in which user is either sender or receiver*/
    public ArrayList<Transaction> getUserTransactionHistory(String userId){
        return Stream.concat(
                        this.blockChain.stream().flatMap(block -> block.getTransactions().stream()),
                        this.pendingTransactions.stream())
                .filter(transaction -> transaction.involvesUser(userId))
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
