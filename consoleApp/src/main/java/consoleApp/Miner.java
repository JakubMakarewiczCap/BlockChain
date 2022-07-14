package consoleApp;

import blockChainClasses.Block;
import blockChainClasses.BlockChain;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component("miner")
public class Miner{
    private boolean run = false;
    @Async
    public void runMiner(BlockChain blockChain){
        this.run = true;
        while (this.run){
            Block block = blockChain.GetNewBlock();
            block.Mine(blockChain.difficulty);
            blockChain.AddBlock(block);
        }
    }

    public void stopMiner(){
        this.run = false;
    }

    public boolean isRun() {
        return run;
    }
}
