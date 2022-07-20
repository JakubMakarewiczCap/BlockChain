package blockChain.blockChainClasses;

import blockChainClasses.Block;
import blockChainClasses.Blockchain;
import blockChainClasses.Transaction;
import blockChainClasses.verificationResults.BlockchainVerificationResultEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class BlockchainTest {
    private Blockchain blockChain;
    @BeforeEach
    void initializeChain(){
        blockChain = new Blockchain(1);
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
        assertEquals(BlockchainVerificationResultEnum.VALID,
                blockChain.verifyBlockChain().verificationResult());
        Block b1 = blockChain.getNewBlock("");
        b1.mine(blockChain.difficulty);
        blockChain.addBlock(b1);
        assertEquals(BlockchainVerificationResultEnum.VALID,
                blockChain.verifyBlockChain().verificationResult());
        Block b2 = new Block("blockChain.GetLast().getHash()", new ArrayList<>());
        b2.mine(blockChain.difficulty);
        blockChain.getBlockChain().add(b2);
        assertEquals(BlockchainVerificationResultEnum.INVALID_PREV_HASH,
                blockChain.verifyBlockChain().verificationResult());

        blockChain.getBlockChain().removeLast();
        Block b3 = blockChain.getNewBlock("");
        blockChain.getBlockChain().add(b3);
        assertEquals(BlockchainVerificationResultEnum.INVALID_HASH,
                blockChain.verifyBlockChain().verificationResult());
        blockChain.getLastBlock().getTransactions().add(new Transaction("1", "2", 312));
        blockChain.getLastBlock().getTransactions().add(new Transaction(null, "1", 312313));
        blockChain.getLastBlock().mine(blockChain.getDifficulty());
        assertEquals(BlockchainVerificationResultEnum.INVALID_TRANSACTIONS,
                blockChain.verifyBlockChain().verificationResult());
    }

    @Test
    void serializationTest(){
        blockChain.serializeToFile("test.json");
        Blockchain blockchain2 = Blockchain.deserializeFromFile("test.json");
        assertEquals(blockChain, blockchain2);
        blockchain2.getLastBlock().transactions.add(new Transaction("","",1, System.currentTimeMillis()));
        assertNotEquals(blockChain, blockchain2);
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
            blockChain.addPendingTransaction(new Transaction("1", "123", 1,System.currentTimeMillis()));
        }

        @Test
        void getUserBalanceTest(){
            assertEquals(9, blockChain.getUserBalance("1"));
            assertEquals(11, blockChain.getUserBalance("2"));
            assertEquals(2, blockChain.getUserBalance("3"));
            assertEquals(3, blockChain.getUserBalance("4"));
            assertEquals(9, blockChain.getUserBalance("1", new Transaction("","",123)));
            assertEquals(4, blockChain.getUserBalance("1", blockChain.getBlockChain().get(3).getTransactions().get(3)));
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
            assertEquals(6,blockChain.getUserTransactionHistory("1").size());
            assertEquals(4,blockChain.getUserTransactionHistory("2").size());
            assertEquals(3,blockChain.getUserTransactionHistory("3").size());
            assertEquals(1,blockChain.getUserTransactionHistory("4").size());
            assertEquals(0,blockChain.getUserTransactionHistory("5").size());
        }
        @Test
        void transactionInBlockchainTest(){
            assertFalse(blockChain.transactionInBlockChain(new Transaction("","",2)));
            assertTrue(blockChain.transactionInBlockChain(blockChain.getUserTransactionHistory("1").get(0)));
            assertTrue(blockChain.transactionInBlockChain(blockChain.getPendingTransactions().get(0)));
        }

        @Test
        void transactionsUpToTest(){
            assertEquals(6,
                    blockChain.getAllTransactions(
                            blockChain.getBlockChain().get(3).getTransactions().get(4)).size());
            assertEquals(12,
                    blockChain.getAllTransactions(
                            new Transaction("","",123)).size());
            assertEquals(12, blockChain.getAllTransactions(true).size());
            assertEquals(11, blockChain.getAllTransactions(false).size());
        }
    }
}