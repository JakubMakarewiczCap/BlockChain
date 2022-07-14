package server;

import blockChainClasses.BlockChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

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
            blockChain = BlockChain.FromFile("blockchain.json");
        }
        catch(Exception e){
            blockChain = new BlockChain(6);
        }
        this.blockChain.addPropertyChangeListener(e->this.FireBlockChainUpdate());
        this.miner.runMiner(this.blockChain);
    }
    @MessageMapping("/get")
    @SendTo("/blockchain")
    public String BlockChainUpdate() {
        return this.blockChain.ToJson();
    }
    public void FireBlockChainUpdate() {
        this.template.convertAndSend("/blockchain", this.blockChain.ToJson());
    }

}


