package server;

import blockChainClasses.Blockchain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;

@Controller
public class BlockChainController {
    private Blockchain blockChain;
    @Autowired
    private Miner miner;
    @Autowired
    private SimpMessagingTemplate template;

    @PostConstruct
    public void initialize() {
        try {
            blockChain = Blockchain.deserializeFromFile("blockchain.json");
        }
        catch(Exception e){
            blockChain = new Blockchain(6);
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


