package consoleApp;

import blockChainClasses.Block;
import blockChainClasses.BlockChain;
import blockChainClasses.Transaction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class ConsoleHelper {
    public static String ReadString(String message) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print(message);
        return br.readLine();
    }

    public static Integer ReadInt(String message) throws IOException{
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print(message);
        try {
            return Integer.parseInt(br.readLine());
        }catch(NumberFormatException nfe){
            System.out.println("Could not parse int " + nfe.getMessage());
            return null;
        }
    }

    public static void PrintBlockchain(BlockChain blockChain){
        StringBuilder stringBuilder = new StringBuilder();
        for(Iterator<Block> itr = ((LinkedList<Block>)blockChain.getBlockChain().clone()).iterator();
            itr.hasNext();){
            Block block = itr.next();
            stringBuilder.append("\n");
            stringBuilder.append("-".repeat(74));
            stringBuilder.append(String.format("\n| depth: %-63s |", block.getDepth()));
            if (Objects.equals(block.getPrevHash(), "0"))
                stringBuilder.append(String.format("\n| prevHash: %-60s |\n", "0"));
            else
                stringBuilder.append(String.format(
                        "\n| %-70s |\n",
                        String.format("prevHash: %4s...%4s",
                            block.getPrevHash().substring(0,4),
                            block.getPrevHash().substring(block.getPrevHash().length()-4)
                        ))
                );
            stringBuilder.append(String.format("| hash: %-64s |\n", block.getHash()));
            stringBuilder.append(String.format("| date: %-64s |\n", new Date(block.getTimestamp())));
            stringBuilder.append("-".repeat(74));
            stringBuilder.append(String.format("\n %36s", "V"));

        }
        System.out.println(stringBuilder.substring(0,stringBuilder.length()-2));
    }
    public static void PrintTransactionHistory(ArrayList<Transaction> transactions){
        StringBuilder stringBuilder = new StringBuilder();
        for(Iterator<Transaction> itr = ((ArrayList<Transaction>)transactions.clone()).iterator();
            itr.hasNext();){
            Transaction t = itr.next();
            stringBuilder.append(String.format("\nfrom: %-25s to: %-25s amount: %-25f", t.getFromId(), t.getToId(), t.getAmount()));
        }
        System.out.println(stringBuilder);
    }
}
