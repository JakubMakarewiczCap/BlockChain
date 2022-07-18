package consoleApp;

import blockChainClasses.Block;
import blockChainClasses.BlockChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component("CLI")
public class CLI {
    @Autowired
    private Miner miner;
    private BlockChain blockChain;
    private String path;

    public void run() throws IOException {
        this.Init();

        boolean continueRunning = true;
        while (continueRunning){
            int action = this.CreateGetActionWindow();
            switch (action){
                case 0:
                    continueRunning = false;
                    break;
                case 1:
                    this.Mine();
                    break;
                case 2:
                    this.WalletScreen();
                    break;
                case 3:
                    this.BlockChainOptionsScreen();
                    break;
                case 4:
                    this.ToggleMiner();
                    break;
            }
        }
        this.OnExit();
    }

    private void Init() throws IOException {
        this.path = ConsoleHelper.ReadString("Enter path to json file: ");
        try{
            this.blockChain = BlockChain.FromFile(this.path);
        }catch (Exception e){
            System.out.println(e.getMessage());
            System.out.println("Initializing Blockchain");
            Integer difficulty = null;
            while (difficulty ==null){
                difficulty = ConsoleHelper.ReadInt("Enter blockchain mining difficulty: ");
                if (difficulty != null
                        && !BlockChain.IsDifficultyValid(difficulty)){
                    System.out.println("invalid difficulty: " + difficulty);
                    difficulty = null;
                }
            }
            this.blockChain = new BlockChain(difficulty);
        }
    }
    private void OnExit(){
        this.blockChain.ToFile(this.path);
    }
    //region Windows
    private int CreateGetActionWindow() throws IOException {
        System.out.println("=".repeat(148));
        Integer action = null;
        while (action ==null) {
            action = ConsoleHelper.ReadInt("""
                    Choose action:\s
                    1. mine new block
                    2. get user info
                    3. show blockchain options
                    4. toggle miner
                    0. close app""");
            if (action != null && (action < 0 || action > 5)){
                System.out.println("invalid action: " + action);
                action = null;
            }
        }
        return action;
    }
    //region Action Windows
    private void Mine(){
        Block block = blockChain.GetNewBlockForMining("ConsoleUser");
        block.Mine(blockChain.difficulty);
        boolean mined = blockChain.AddBlock(block, true);
        if (mined) {
            System.out.println("Mined block");
        } else {
            System.out.println("Could not mine block");
        }
    }
    private void BlockInfoScreen(){}
    private void WalletScreen() throws IOException {
        System.out.println("-".repeat(70)+" Wallet "+"-".repeat(69));
        var userId = ConsoleHelper.ReadString("Provide UserID: ");
        Integer action = null;
        while (action ==null) {
            action = ConsoleHelper.ReadInt("""
                    Choose action:\s
                    1. get balance
                    2. get history
                    0. return""");
            if (action != null && (action < 0 || action > 2)){
                System.out.println("invalid action: " + action);
                action = null;
            }
        }
        switch (action){
            case 0:
                return;
            case 1:
                System.out.printf("User: %s balance: %f%n", userId, blockChain.GetUserBalance(userId));
                break;
            case 2:
                System.out.printf("User: %s transaction history:%n", userId);
                ConsoleHelper.PrintTransactionHistory(blockChain.GetUserTransactionHistory(userId));
                break;
        }
    }

    private void BlockChainOptionsScreen() throws IOException {
        System.out.println("-".repeat(68)+" BlockChain "+"-".repeat(68));
        Integer action = null;
        while (action ==null) {
            action = ConsoleHelper.ReadInt("""
                    Choose action:\s
                    1. show whole
                    2. show range
                    3. show last
                    0. return""");
            if (action != null && (action < 0 || action > 3)){
                System.out.println("invalid action: " + action);
                action = null;
            }
        }
        switch (action){
            case 0:
                return;
            case 1:
                ConsoleHelper.PrintBlockchain(this.blockChain);
                break;
            case 2:
                Integer skip = ConsoleHelper.ReadInt("Skip: ");
                Integer take = ConsoleHelper.ReadInt("Take: ");
                if (skip == null || take == null)
                    break;
                ConsoleHelper.PrintBlockchain(this.blockChain, skip, take);
                break;
            case 3:
                ConsoleHelper.PrintBlockchain(this.blockChain, this.blockChain.getBlockChain().size()-1, 1);
        }
    }
    private void ToggleMiner(){
        if (this.miner.isRun()) {
            System.out.println("Stopping Miner..");
            this.miner.stopMiner();
        }
        else {
            System.out.println("Starting Miner..");
            this.miner.runMiner(this.blockChain, "ConsoleUser");
        }
    }
    private void ShowBlockChain(){
        ConsoleHelper.PrintBlockchain(blockChain);
    }
    //endregion
    //endregion
}
