package server;

import blockChainClasses.BlockChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;

@Controller
public class BlockChainController {
    private BlockChain blockChain;
    @Autowired
    private Miner miner;
    @Autowired
    private SimpMessagingTemplate template;

    @PostConstruct
    public void initialize() {
        try {
            blockChain = BlockChain.deserializeFromFile("blockchain.json");
        }
        catch(Exception e){
            blockChain = new BlockChain(6);
        }
        this.blockChain.addPropertyChangeListener(e->this.fireBlockChainUpdate());
        this.miner.runMiner(this.blockChain, "serverMiner");
    }
    @MessageMapping("/get")
    @SendTo("/blockchain")
    public String blockChainUpdate() {
        return this.blockChain.toJson();
    }
    public void fireBlockChainUpdate() {
        this.template.convertAndSend("/blockchain", this.blockChain.toJson());
    }

}


