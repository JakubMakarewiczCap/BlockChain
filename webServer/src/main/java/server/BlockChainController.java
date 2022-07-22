package server;

import blockChainClasses.Blockchain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.beans.PropertyChangeEvent;
import java.util.Objects;

@Controller
public class BlockChainController {
    private Blockchain blockChain;
    @Autowired
    private Miner miner;
    @Autowired
    private SimpMessagingTemplate template;
    private final String userId = "serverMiner";

    @PostConstruct
    public void initialize() {
        try {
            blockChain = Blockchain.deserializeFromFile("blockchain.json").blockchain();
        }
        catch(Exception e){
            blockChain = new Blockchain(6);
        }
        this.blockChain.addPropertyChangeListener(this::fireBlockChainUpdate);
        this.miner.runMiner(this.blockChain, this.userId);
    }
    @MessageMapping("/get")
    @SendTo("/blockchain")
    public String blockChainUpdate() {
        return this.blockChain.toJson();
    }
    public void fireBlockChainUpdate(PropertyChangeEvent e) {
        try {
            if (Objects.equals(e.getPropertyName(), "blockChain")
                    && Objects.equals(this.blockChain.getLastBlock().getTransactions().get(0).getToId(),
                    this.userId))
                this.template.convertAndSend("/blockchain", this.blockChain.toJson());
        }
        catch (IndexOutOfBoundsException ignored){}
    }

}


