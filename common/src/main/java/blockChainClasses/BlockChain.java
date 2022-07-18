package blockChainClasses;

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

public class BlockChain {
    private transient final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private LinkedList<Block> blockChain;
    private ArrayList<Transaction> pendingTransactions;

    public boolean addPendingTransaction(Transaction pendingTransaction) {
        if (!this.VerifyTransaction(pendingTransaction, true))
            return false;
        this.pendingTransactions.add(pendingTransaction);
        support.firePropertyChange("pendingTransactions", new ArrayList<Transaction>(), this.pendingTransactions); // WARNING: old value is useless

        return true;
    }

    public int difficulty;
    private double miningReward;
    public LinkedList<Block> getBlockChain() {
        return blockChain;
    }

    public BlockChain(){
        this.blockChain = new LinkedList<>();
        this.blockChain.add(new Block("0", new ArrayList<>()));
        this.difficulty = 4;
        this.miningReward = 2;
        this.pendingTransactions = new ArrayList<>();
    }
    public BlockChain(int difficulty){
        this();
        this.difficulty = difficulty;
    }
    public BlockChain(int difficulty, double miningReward){
        this(difficulty);
        this.miningReward = miningReward;
    }
    public BlockChain(LinkedList<Block> blockChain, int difficulty, double miningReward) {
        this(difficulty, miningReward);
        this.blockChain = blockChain;
    }

    public boolean AddBlock(){
        Block block = new Block(this.GetLast().getHash());
        return this.AddBlock(block);
    }
    public boolean AddBlock(Block block){
        return this.AddBlock(block, false);
    }
    public boolean AddBlock(Block block, boolean mined){
        if (VerifyNewBlock(block, mined)){
            block.setDepth(this.blockChain.size());
            this.blockChain.add(block);
            support.firePropertyChange("blockChain", new LinkedList<Block>(), this.blockChain); // WARNING: old value is useless
            this.pendingTransactions.clear();
            return true;
        }
        return false;
    }
    public Block GetNewBlock(){
        return new Block(this.GetLast().getHash(), this.pendingTransactions);
    }
    public Block GetNewBlockForMining(String minerId){
        var block = new Block(this.GetLast().getHash());
        block.transactions.add(new Transaction(null, minerId, this.miningReward,System.currentTimeMillis()));
        block.transactions.addAll(this.pendingTransactions);
        return block;
    }
    public Block GetLast(){
        if (this.blockChain.size() == 0)
            return null;
        return this.blockChain.getLast();
    }
    public void Clear(){
        this.blockChain.clear();
    }
    public boolean VerifyNewBlock(Block block){
        return this.VerifyNewBlock(block, false);
    }
    public boolean VerifyNewBlock(Block block, boolean mined){
        if (!Objects.equals(block.GenerateHash(), block.getHash()))
            return false;
        if (this.blockChain.size()<1)
            return true;
        if (!Objects.equals(this.GetLast().getHash(), block.getPrevHash()))
            return false;
        if (mined &&
                (!(block.getHash().substring(0, difficulty).equals("0".repeat(difficulty)))
                || !(block.transactions.get(0).getAmount() == this.miningReward)))
            return false;
        if (block.getTransactions().size()- (mined? 1: 0) != this.pendingTransactions.size())
            return false;
        for (int i = (mined?1:0); i<block.getTransactions().size();i++)
            if (!Objects.equals(block.getTransactions().get(i), this.pendingTransactions.get(i -(mined?1:0))))
                return false;
        if (mined && (block.getTransactions().get(0).getFromId()!=null || block.getTransactions().get(0).getAmount() != this.miningReward))
            return false;
        return true;
    }

    public boolean VerifyBlockChain(){
        if (this.blockChain.size() == 0)
            return true;
        if (this.blockChain.size() == 1)
            return Objects.equals(this.blockChain.getFirst().GenerateHash(),
                    this.blockChain.getFirst().getHash());
        Iterator<Block> i = this.blockChain.iterator();
        Block prev = i.next();
        Block current;
        while (i.hasNext()) {
            current = i.next();
            if (!Objects.equals(prev.getHash(), prev.GenerateHash()))
                return false;
            if (!Objects.equals(prev.getHash(), current.getPrevHash()))
                return false;
            if (!(current.getHash().substring(0, difficulty).
                    equals("0".repeat(difficulty))))
                return false;
            if (current.getTransactions().stream().anyMatch(t -> !this.VerifyTransaction(t)))
                return false;
            prev = current;
        }
        return true;
    }

    public String ToJson(){
        return new Gson().toJson(this);
    }

    public double getMiningReward() {
        return miningReward;
    }

    public void setMiningReward(double miningReward) {
        this.miningReward = miningReward;
    }

    public void ToFile(String filepath){
        String json = new Gson().toJson(this);
        try {
            FileWriter writer = new FileWriter(filepath);
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static BlockChain FromFile(String filepath) throws RuntimeException{
        try {
            BufferedReader reader =  new BufferedReader(new FileReader(filepath));
            BlockChain result = new Gson().fromJson(reader,BlockChain.class);
            reader.close();
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (other.getClass() != getClass()) return false;
        BlockChain rhs = (BlockChain) other;
        if (this.blockChain.size() != rhs.blockChain.size())
            return false;
        if (!Objects.equals(this.blockChain, rhs.blockChain))
            return false;
        return true;
    }
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    public static boolean IsDifficultyValid(int difficulty){
        return difficulty > 0;
    }

    public double GetUserBalance(String userId){
        return this.GetUserBalance(userId, true);
    }
    public double GetUserBalance(String userId, boolean includePending){
        double balance=0;
        for (Block block :
                this.blockChain) {
            for (Transaction transaction :
                    block.getTransactions()) {
                if (Objects.equals(userId, transaction.getFromId()))
                    balance -= transaction.getAmount();
                if (Objects.equals(transaction.getToId(), userId))
                    balance += transaction.getAmount();
            }
        }
        if (includePending) {
            for (Transaction transaction :
                    this.pendingTransactions) {
                if (Objects.equals(userId, transaction.getFromId()))
                    balance -= transaction.getAmount();
                if (Objects.equals(transaction.getToId(), userId))
                    balance += transaction.getAmount();
            }
        }
        return balance;
    }

    public boolean VerifyTransaction(Transaction t){
        return this.VerifyTransaction(t, false);
    }
    public boolean VerifyTransaction(Transaction t, boolean isNewTransaction){
        if (t.getFromId() != null) {
            if (t.getAmount() <= 0)
                return false;
            if (Objects.equals(t.getFromId(), t.getToId()))
                return false;
            if (GetUserBalance(t.getFromId(), isNewTransaction) < t.getAmount())
                return false;
        }
        return true;
    }

    public ArrayList<Transaction> GetUserTransactionHistory(String userId){
        return this.blockChain.stream()
                .flatMap(block -> block.getTransactions()
                        .stream().filter(transaction -> transaction.InvolvesUser(userId)))
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
