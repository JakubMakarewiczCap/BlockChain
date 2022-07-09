package com.project.blockChain.blockChainClasses;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

class BlockChainTest {
    private BlockChain blockChain;
    @BeforeEach
    void initializeChain(){
        blockChain = new BlockChain();
        blockChain.AddBlock(new Block(blockChain.GetLast().getHash(), new ArrayList<>()));
        blockChain.AddBlock(new Block(blockChain.GetLast().getHash(), new ArrayList<>()));
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
    }

    @Test
    void serializationTest(){
        blockChain.ToFile("test.json");
        BlockChain blockChain2 = BlockChain.FromFile("test.json");
        assertEquals(blockChain, blockChain2);
        blockChain2.GetLast().transactions.add(new Transaction("","",1));
        assertNotEquals(blockChain, blockChain2);
    }
}