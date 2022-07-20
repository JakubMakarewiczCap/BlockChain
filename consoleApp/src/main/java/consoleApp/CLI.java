package consoleApp;

import blockChainClasses.Block;
import blockChainClasses.BlockChain;
import blockChainClasses.Transaction;
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
            int action = this.createGetActionWindow();
            switch (action) {
                case 0 -> continueRunning = false;
                case 1 -> this.mine();
                case 2 -> this.walletScreen();
                case 3 -> this.blockChainOptionsScreen();
                case 4 -> this.toggleMiner();
                case 5 -> this.createTransactionScreen();
            }
        }
        this.onExit();
    }

    private void Init() throws IOException {
        this.path = ConsoleHelper.readString("Enter path to json file: ");
        try{
            this.blockChain = BlockChain.deserializeFromFile(this.path);
        }catch (Exception e){
            System.out.println(e.getMessage());
            System.out.println("Initializing Blockchain");
            Integer difficulty = null;
            while (difficulty ==null){
                difficulty = ConsoleHelper.readInt("Enter blockchain mining difficulty: ");
                if (difficulty != null
                        && !BlockChain.isDifficultyValid(difficulty)){
                    System.out.println("invalid difficulty: " + difficulty);
                    difficulty = null;
                }
            }
            this.blockChain = new BlockChain(difficulty);
        }
    }
    private void onExit(){
        this.blockChain.serializeToFile(this.path);
    }
    //region Windows
    private int createGetActionWindow() throws IOException {
        System.out.println("=".repeat(148));
        Integer action = null;
        while (action ==null) {
            action = ConsoleHelper.readInt("""
                    Choose action:\s
                    1. mine new block
                    2. get user info
                    3. show blockchain options
                    4. toggle miner
                    5. create new transaction
                    0. close app
                    """); // TODO: create logic for 5.
            if (action != null && (action < 0 || action > 5)){
                System.out.println("invalid action: " + action);
                action = null;
            }
        }
        return action;
    }
    //region Action Windows
    private void mine(){
        Block block = blockChain.getNewBlock("ConsoleUser");
        block.mine(blockChain.difficulty);
        boolean mined = blockChain.addBlock(block);
        if (mined) {
            System.out.println("Mined block");
        } else {
            System.out.println("Could not mine block");
        }
    }
    private void blockInfoScreen(){}
    private void walletScreen() throws IOException {
        System.out.println("-".repeat(70)+" Wallet "+"-".repeat(69));
        var userId = ConsoleHelper.readString("Provide UserID: ");
        Integer action = null;
        while (action ==null) {
            action = ConsoleHelper.readInt("""
                    Choose action:\s
                    1. get balance
                    2. get history
                    0. return
                    """);
            if (action != null && (action < 0 || action > 2)){
                System.out.println("invalid action: " + action);
                action = null;
            }
        }
        switch (action) {
            case 0:
                return;
            case 1:
                System.out.printf("User: %s balance: %f%n", userId, blockChain.getUserBalance(userId));
                break;
            case 2:
                System.out.printf("User: %s transaction history:%n", userId);
                ConsoleHelper.printTransactionHistory(blockChain.getUserTransactionHistory(userId));
                break;
        }
    }

    private void blockChainOptionsScreen() throws IOException {
        System.out.println("-".repeat(68)+" BlockChain "+"-".repeat(68));
        Integer action = null;
        while (action ==null) {
            action = ConsoleHelper.readInt("""
                    Choose action:\s
                    1. show whole
                    2. show range
                    3. show last
                    4. find by hash
                    0. return
                    """);
            if (action != null && (action < 0 || action > 4)){
                System.out.println("invalid action: " + action);
                action = null;
            }
        }
        switch (action){
            case 0:
                return;
            case 1:
                ConsoleHelper.printBlockchain(this.blockChain);
                break;
            case 2:
                Integer skip = ConsoleHelper.readInt("Skip: ");
                Integer take = ConsoleHelper.readInt("Take: ");
                if (skip == null || take == null)
                    break;
                ConsoleHelper.printBlockchain(this.blockChain, skip, take);
                break;
            case 3:
                ConsoleHelper.printBlockchain(this.blockChain, this.blockChain.getBlockChain().size()-1, 1);
                break;
            case 4:
                ConsoleHelper.printBlock(this.blockChain, ConsoleHelper.readString("Hash: "));
        }
    }
    private void toggleMiner(){
        if (this.miner.isRun()) {
            System.out.println("Stopping Miner..");
            this.miner.stopMiner();
        }
        else {
            System.out.println("Starting Miner..");
            this.miner.runMiner(this.blockChain, "ConsoleUser");
        }
    }
    private void showBlockChain(){
        ConsoleHelper.printBlockchain(blockChain);
    }
    private void createTransactionScreen() throws IOException {
        String sender = ConsoleHelper.readString("Sender: ");
        String recipient = ConsoleHelper.readString("Recipient: ");
        Integer amount = null;
        while (amount ==null) {
            amount = ConsoleHelper.readInt("Amount: ");
            if (amount != null && (amount <= 0)){
                System.out.println("Invalid amount (amount has to be >= 0): " + amount);
                amount = null;
            }
        }
        double senderBalance = this.blockChain.getUserBalance(sender);
        if (senderBalance < amount)
            System.out.println("Sender's balance: " + senderBalance
                    + " is not enough for amount: " + amount);
        if (!this.blockChain.addPendingTransaction(new Transaction(sender, recipient, amount)))
            System.out.println("Couldn't create transaction");
        else
            System.out.println("Transaction successfully created");
    }
    //endregion
    //endregion
}
