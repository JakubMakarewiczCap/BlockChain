package com.project.blockChain.server;

import com.project.blockChain.blockChainClasses.Block;
import com.project.blockChain.blockChainClasses.BlockChain;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component("miner")
public class Miner{
    @Async
    public void runMiner(BlockChain blockChain){
        while (true){
            blockChain.AddBlock(new Block(blockChain.GetLast().getHash()));
            blockChain.ToFile("blockchain.json");
            System.out.println("mined");
        }
    }
}
