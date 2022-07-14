package blockChain.blockChainClasses;

import blockChainClasses.Block;
import blockChainClasses.Transaction;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BlockTest {

    @org.junit.jupiter.api.Test
    void getHash() {
    }

    @org.junit.jupiter.api.Test
    void toJson() {
        Block block = new Block();
        Transaction t1 = new Transaction("user1","user2", 100);
        block.addTransaction(t1);
        String res = block.ToJson();
        System.out.println();
    }
    @org.junit.jupiter.api.Test
    void mine(){
        int difficulty = 3;
        Block block = new Block("1");
        block.Mine(difficulty);
        assertEquals(block.getHash().substring(0, difficulty), "0".repeat(difficulty));
    }
}