package blockChain.blockChainClasses;

import blockChainClasses.Block;
import blockChainClasses.BlockChain;
import blockChainClasses.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class BlockChainTest {
    private BlockChain blockChain;
    @BeforeEach
    void initializeChain(){
        blockChain = new BlockChain(1);
        Block b1 = blockChain.getNewBlock("");
        b1.mine(blockChain.difficulty);
        blockChain.addBlock(b1);
        Block b2 = blockChain.getNewBlock("");
        b2.mine(blockChain.difficulty);
        blockChain.addBlock(b2);
    }
    @Test
    void addBlock() {
        assertFalse(blockChain.addBlock(new Block("1234", new ArrayList<>())));
        Block b1 = blockChain.getNewBlock("");
        b1.mine(blockChain.difficulty);
        assertTrue(blockChain.addBlock(b1));
    }

    @Test
    void verifyNewBlock() {
        assertFalse(blockChain.verifyNewBlock(new Block("1234", new ArrayList<>())));
        Block b1 = blockChain.getNewBlock("");
        b1.mine(blockChain.difficulty);
        assertTrue(blockChain.verifyNewBlock(b1));
    }

    @Test
    void verifyBlockChain() {
        assertTrue(blockChain.verifyBlockChain());
        Block b1 = blockChain.getNewBlock("");
        b1.mine(blockChain.difficulty);
        blockChain.addBlock(b1);
        assertTrue(blockChain.verifyBlockChain());
        Block b2 = new Block("blockChain.GetLast().getHash()", new ArrayList<>());
        b2.mine(blockChain.difficulty);
        blockChain.getBlockChain().add(b2);
        assertFalse(blockChain.verifyBlockChain());
    }

    @Test
    void serializationTest(){
        blockChain.serializeToFile("test.json");
        BlockChain blockChain2 = BlockChain.deserializeFromFile("test.json");
        assertEquals(blockChain, blockChain2);
        blockChain2.getLastBlock().transactions.add(new Transaction("","",1, System.currentTimeMillis()));
        assertNotEquals(blockChain, blockChain2);
    }

    @Nested
    class TransactionTestsNested{
        @BeforeEach
        void setup(){
            blockChain.addPendingTransaction(new Transaction(null, "1", 2, System.currentTimeMillis()));
            blockChain.addPendingTransaction(new Transaction(null, "2", 10,System.currentTimeMillis()));
            blockChain.addPendingTransaction(new Transaction(null, "3", 10,System.currentTimeMillis()));
            blockChain.addPendingTransaction(new Transaction("1", "2", 2,System.currentTimeMillis()));
            blockChain.addPendingTransaction(new Transaction("3", "2", 5,System.currentTimeMillis()));
            Block b1 = blockChain.getNewBlock("1");
            b1.mine(blockChain.difficulty);
            blockChain.addBlock(b1);

            blockChain.addPendingTransaction(new Transaction("2", "1", 6,System.currentTimeMillis()));
            blockChain.addPendingTransaction(new Transaction("3", "4", 3,System.currentTimeMillis()));
            Block b2 = blockChain.getNewBlock("1");
            b2.mine(blockChain.difficulty);
            blockChain.addBlock(b2);
        }

        @Test
        void getUserBalanceTest(){
            assertEquals(10, blockChain.getUserBalance("1"));
            assertEquals(11, blockChain.getUserBalance("2"));
            assertEquals(2, blockChain.getUserBalance("3"));
            assertEquals(3, blockChain.getUserBalance("4"));
        }
        @Test
        void verifyTransactionTest(){
            assertTrue(blockChain.verifyTransaction(new Transaction(null, "2", 0,System.currentTimeMillis())));
            assertTrue(blockChain.verifyTransaction(new Transaction("1", "2", 3,System.currentTimeMillis())));
            assertFalse(blockChain.verifyTransaction(new Transaction("2", "2", 3,System.currentTimeMillis())));
            assertFalse(blockChain.verifyTransaction(new Transaction("3", "2", 23,System.currentTimeMillis())));
            assertFalse(blockChain.verifyTransaction(new Transaction("3", "2", -1000,System.currentTimeMillis())));
        }

        @Test
        void getUserTransactionHistoryTest(){
            assertEquals(5,blockChain.getUserTransactionHistory("1").size());
            assertEquals(4,blockChain.getUserTransactionHistory("2").size());
            assertEquals(3,blockChain.getUserTransactionHistory("3").size());
            assertEquals(1,blockChain.getUserTransactionHistory("4").size());
            assertEquals(0,blockChain.getUserTransactionHistory("5").size());
        }
    }
}