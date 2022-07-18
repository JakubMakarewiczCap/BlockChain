package consoleApp;

import blockChainClasses.Block;
import blockChainClasses.BlockChain;
import blockChainClasses.Transaction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class ConsoleHelper {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");

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
        System.out.println(ConsoleHelper.BuildBlockChainString(blockChain.getBlockChain())
                + "\nBlockchain length: "+blockChain.getBlockChain().size());
    }
    public static void PrintBlockchain(BlockChain blockChain, int skip, int take){
        if (skip<0||take<=0) System.out.println("Skip and take cannot be <0");
        if (skip>=blockChain.getBlockChain().size())
            System.out.println("Skip: " + skip
                    + "is bigger or equal to the blockchain length: " + blockChain.getBlockChain().size());
        StringBuilder stringBuilder = new StringBuilder();
        if (skip>0)
            stringBuilder.append(String.format("%50s\n", ".").repeat(2))
                    .append(String.format("%50s", "V"));
        stringBuilder.append(ConsoleHelper.BuildBlockChainString(
                blockChain.getBlockChain()
                        .stream()
                        .skip(skip)
                        .limit(take)
                        .collect(Collectors.toCollection(LinkedList::new))));
        if (skip+take-1 < blockChain.getBlockChain().size()-1)
            stringBuilder.append(String.format("\n%50s\n", "V"))
                    .append(String.format("%50s\n", ".").repeat(2));
        System.out.println(stringBuilder);
    }

    public static void PrintTransactionHistory(ArrayList<Transaction> transactions){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("-".repeat(100)).append("\n");
        for (Transaction transaction : (ArrayList<Transaction>) transactions.clone()) {
            stringBuilder.append(String.format("| %-96s |\n",
                    String.format("from: %-15s to: %-15s amount: %-20f date: %s",
                            transaction.getFromId(), transaction.getToId(),
                            transaction.getAmount(), ConsoleHelper.dateFormat.format(new Date(transaction.getTimestamp())))));
        }
        stringBuilder.append("-".repeat(100));
        System.out.println(stringBuilder);
    }
    private static String BuildBlockChainString(LinkedList<Block> blockChain){
        StringBuilder stringBuilder = new StringBuilder();
        for(Iterator<Block> itr = ((LinkedList<Block>)blockChain.clone()).iterator();
            itr.hasNext();){
            Block block = itr.next();
            stringBuilder.append("\n");
            stringBuilder.append(ConsoleHelper.BuildBlockString(block));
        }
        return stringBuilder.substring(0,stringBuilder.length()-2);
    }
    private static String BuildBlockString(Block block){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("-".repeat(100));
        stringBuilder.append(String.format("\n| depth: %-89s |", block.getDepth()));
        if (Objects.equals(block.getPrevHash(), "0"))
            stringBuilder.append(String.format("\n| prevHash: %-86s |\n", "0"));
        else
            stringBuilder.append(String.format(
                    "\n| %-96s |\n",
                    String.format("prevHash: %4s...%4s",
                            block.getPrevHash().substring(0,4),
                            block.getPrevHash().substring(block.getPrevHash().length()-4)
                    ))
            );
        stringBuilder.append(String.format("| hash: %-90s |\n", block.getHash()));
        stringBuilder.append(String.format("| date: %-90s |\n",ConsoleHelper.dateFormat.format(new Date(block.getTimestamp()))));
        stringBuilder.append(String.format("| transactions: %-82s |\n", ""));
        for (Transaction transaction :
                block.getTransactions()) {
            stringBuilder.append(String.format("| %-96s |\n",
                    String.format(">from: %-15s to: %-15s amount: %-19f date: %s",
                            transaction.getFromId(), transaction.getToId(),
                            transaction.getAmount(),ConsoleHelper.dateFormat.format(new Date(transaction.getTimestamp())))));
        }
        stringBuilder.append("-".repeat(100));
        stringBuilder.append(String.format("\n %50s", "V"));
        return stringBuilder.toString();
    }
}
