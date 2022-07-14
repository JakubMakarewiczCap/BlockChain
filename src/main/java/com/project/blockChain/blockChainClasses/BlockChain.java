package com.project.blockChain.blockChainClasses;
import com.google.gson.Gson;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;

public class BlockChain {
    private transient final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private LinkedList<Block> blockChain;
    public int difficulty;
    public BlockChain(){
        this.blockChain = new LinkedList<>();
        this.blockChain.add(new Block("0", new ArrayList<>()));
        this.difficulty = 4;
    }
    public BlockChain(int difficulty){
        this.blockChain = new LinkedList<>();
        this.blockChain.add(new Block("0", new ArrayList<>()));
        this.difficulty = difficulty;
    }
    public BlockChain(LinkedList<Block> blockChain, int difficulty) {
        this.blockChain = blockChain;
        this.difficulty = difficulty;
    }

    public boolean AddBlock(){
        Block block = new Block(this.GetLast().getHash());
        return this.AddBlock(block);
    }
    public Block GetNewBlock(){
        return new Block(this.GetLast().getHash());
    }
    public boolean AddBlock(Block block){
        if (VerifyNewBlock(block)){
            this.blockChain.add(block);
            support.firePropertyChange("blockChain", new LinkedList<Block>(), this.blockChain); // WARNING: old value is useless
            return true;
        }
        return false;
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
        if (!Objects.equals(block.GenerateHash(), block.getHash()))
            return false;
        if (this.blockChain.size()<1)
            return true;
        if (!Objects.equals(this.GetLast().getHash(), block.getPrevHash()))
            return false;
        if (!(block.getHash().substring(0, difficulty).
                equals("0".repeat(difficulty))))
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
            prev = current;
        }
        return true;
    }

    public String ToJson(){
        return new Gson().toJson(this);
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
}
