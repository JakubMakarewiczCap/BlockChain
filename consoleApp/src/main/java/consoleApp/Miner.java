package consoleApp;

import blockChainClasses.Block;
import blockChainClasses.BlockChain;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component("Miner")
public class Miner{
    private boolean run = false;
    @Async
    public void runMiner(BlockChain blockChain, String userId){
        this.run = true;
        while (this.run){
            Block block = blockChain.GetNewBlockForMining(userId);
            block.Mine(blockChain.difficulty);
            blockChain.AddBlock(block,true);
        }
    }

    public void stopMiner(){
        this.run = false;
    }

    public boolean isRun() {
        return run;
    }
}
