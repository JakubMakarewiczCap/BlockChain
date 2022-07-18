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
        Block b1 = new Block(blockChain.GetLast().getHash(), new ArrayList<>());
        b1.Mine(blockChain.difficulty);
        blockChain.AddBlock(b1);
        Block b2 = new Block(blockChain.GetLast().getHash(), new ArrayList<>());
        b2.Mine(blockChain.difficulty);
        blockChain.AddBlock(b2);
    }
    @Test
    void addBlock() {
        assertFalse(blockChain.AddBlock(new Block("1234", new ArrayList<>())));
        assertTrue(blockChain.AddBlock(new Block(blockChain.GetLast().getHash(), new ArrayList<>())));
    }

    @Test
    void verifyNewBlock() {
        assertFalse(blockChain.VerifyNewBlock(new Block("1234", new ArrayList<>())));
        assertTrue(blockChain.VerifyNewBlock(new Block(blockChain.GetLast().getHash(), new ArrayList<>())));
    }

    @Test
    void verifyBlockChain() {
        assertTrue(blockChain.VerifyBlockChain());
        Block b1 = new Block(blockChain.GetLast().getHash(), new ArrayList<>());
        b1.Mine(blockChain.difficulty);
        blockChain.AddBlock(b1);
        assertTrue(blockChain.VerifyBlockChain());
        Block b2 = new Block("blockChain.GetLast().getHash()", new ArrayList<>());
        b2.Mine(blockChain.difficulty);
        blockChain.getBlockChain().add(b2);
        assertFalse(blockChain.VerifyBlockChain());
    }

    @Test
    void serializationTest(){
        blockChain.ToFile("test.json");
        BlockChain blockChain2 = BlockChain.FromFile("test.json");
        assertEquals(blockChain, blockChain2);
        blockChain2.GetLast().transactions.add(new Transaction("","",1, System.currentTimeMillis()));
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
            Block b1 = blockChain.GetNewBlockForMining("1");
            b1.Mine(blockChain.difficulty);
            blockChain.AddBlock(b1, true);

            blockChain.addPendingTransaction(new Transaction("2", "1", 6,System.currentTimeMillis()));
            blockChain.addPendingTransaction(new Transaction("3", "4", 3,System.currentTimeMillis()));
            Block b2 = blockChain.GetNewBlockForMining("1");
            b2.Mine(blockChain.difficulty);
            blockChain.AddBlock(b2, true);
        }

        @Test
        void getUserBalanceTest(){
            assertEquals(10, blockChain.GetUserBalance("1"));
            assertEquals(11, blockChain.GetUserBalance("2"));
            assertEquals(2, blockChain.GetUserBalance("3"));
            assertEquals(3, blockChain.GetUserBalance("4"));
        }
        @Test
        void verifyTransactionTest(){
            assertTrue(blockChain.VerifyTransaction(new Transaction(null, "2", 0,System.currentTimeMillis())));
            assertTrue(blockChain.VerifyTransaction(new Transaction("1", "2", 3,System.currentTimeMillis())));
            assertFalse(blockChain.VerifyTransaction(new Transaction("2", "2", 3,System.currentTimeMillis())));
            assertFalse(blockChain.VerifyTransaction(new Transaction("3", "2", 23,System.currentTimeMillis())));
            assertFalse(blockChain.VerifyTransaction(new Transaction("3", "2", -1000,System.currentTimeMillis())));
        }

        @Test
        void getUserTransactionHistoryTest(){
            assertEquals(5,blockChain.GetUserTransactionHistory("1").size());
            assertEquals(4,blockChain.GetUserTransactionHistory("2").size());
            assertEquals(3,blockChain.GetUserTransactionHistory("3").size());
            assertEquals(1,blockChain.GetUserTransactionHistory("4").size());
            assertEquals(0,blockChain.GetUserTransactionHistory("5").size());
        }
    }
}