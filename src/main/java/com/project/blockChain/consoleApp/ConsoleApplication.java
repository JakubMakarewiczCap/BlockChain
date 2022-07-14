package com.project.blockChain.consoleApp;

import com.project.blockChain.blockChainClasses.Block;
import com.project.blockChain.blockChainClasses.BlockChain;
import com.project.blockChain.consoleApp.Miner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
@SpringBootApplication

public class ConsoleApplication implements CommandLineRunner {
    @Autowired
    private Miner miner;
    private BlockChain blockChain;
    private String path;

    public static void main(String[] args) {
        SpringApplication.run(ConsoleApplication.class, args);
    }

    @Override
    public void run(String... args) throws IOException {
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
                case 4:
                    this.ShowBlockChain();
                    break;
                case 5:
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
        System.out.println("------------------------------------------------------");
        Integer action = null;
        while (action ==null) {
            action = ConsoleHelper.ReadInt("Choose action: " +
                    "\n1. mine new block" +
                    "\n2. get block info" +
                    "\n3. get user info" +
                    "\n4. show blockchain" +
                    "\n5. toggle miner" +
                    "\n0. close app");
            if (action != null && (action < 0 || action > 5)){
                System.out.println("invalid action: " + action);
                action = null;
            }
        }
        return action;
    }
    //region Action Windows
    private void Mine(){
        Block block = blockChain.GetNewBlock();
        block.Mine(blockChain.difficulty);
        boolean mined = blockChain.AddBlock(block);
        if (mined) {
            System.out.println("Mined block");
        } else {
            System.out.println("Could not mine block");
        }
    }
    private void BlockInfoScreen(){}
    private void UserInfoScreen(){}
    private void ToggleMiner(){
        if (this.miner.isRun()) {
            System.out.println("Stopping Miner..");
            this.miner.stopMiner();
        }
        else {
            System.out.println("Starting Miner..");
            this.miner.runMiner(this.blockChain);
        }
    }
    private void ShowBlockChain(){
        System.out.println(this.blockChain.ToJson());
    }
    //endregion
    //endregion
}
