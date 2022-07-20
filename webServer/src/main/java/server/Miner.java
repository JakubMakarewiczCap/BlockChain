package server;

import blockChainClasses.Block;
import blockChainClasses.Blockchain;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component("miner")
public class Miner{
    private boolean run = false;
    @Async
    public void runMiner(Blockchain blockChain, String userId){
        this.run = true;
        while (this.run){
            Block block = blockChain.getNewBlock(userId);
            block.mine(blockChain.difficulty);
            blockChain.addBlock(block);
            blockChain.serializeToFile("blockchain.json");
            System.out.println("mined");
        }
    }

    public void stopMiner(){
        this.run = false;
    }

}
