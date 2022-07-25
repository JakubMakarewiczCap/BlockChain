package consoleApp;

import blockChainClasses.Block;
import blockChainClasses.Blockchain;
import blockChainClasses.Transaction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class ConsoleHelper {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");

    public static String readString(String message) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print(message);
        return br.readLine();
    }

    public static Integer readInt(String message) throws IOException{
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print(message);
        try {
            return Integer.parseInt(br.readLine());
        }catch(NumberFormatException nfe){
            System.out.println("Could not parse int " + nfe.getMessage());
            return null;
        }
    }

    public static void printBlockchain(Blockchain blockChain){
        System.out.println(ConsoleHelper.buildBlockChainString(
                blockChain, 0, blockChain.getBlockChain().size()));
    }
    public static void printBlockchain(Blockchain blockChain, int skip, int take){
        if (skip<0||take<=0) System.out.println("Skip and take cannot be <0");
        if (skip>=blockChain.getBlockChain().size())
            System.out.println("Skip: " + skip
                    + "is bigger or equal to the blockchain length: " + blockChain.getBlockChain().size());
        System.out.println(ConsoleHelper.buildBlockChainString(
                blockChain, skip, take));
    }

    public static void printTransactionHistory(ArrayList<Transaction> transactions){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("-".repeat(100)).append("\n");
        double sum=0;
        for (Transaction transaction : (ArrayList<Transaction>) transactions.clone()) {
            stringBuilder.append(String.format("| %-96s |\n",
                    String.format("from: %-15s to: %-15s amount: %-20f date: %s",
                            transaction.getFromId(), transaction.getToId(),
                            transaction.getAmount(), ConsoleHelper.dateFormat.format(new Date(transaction.getTimestamp())))));
            sum+=transaction.getAmount();
        }
        stringBuilder.append("-".repeat(100));
        stringBuilder.append(String.format("\nTotal: %.6f",  sum));
        System.out.println(stringBuilder);
    }
    private static String buildBlockChainString(Blockchain blockChain,
                                                int skip, int take){
        LinkedList<Block> blockChainList = blockChain.getBlockChain()
                .stream()
                .skip(skip)
                .limit(take)
                .collect(Collectors.toCollection(LinkedList::new));
        StringBuilder stringBuilder = new StringBuilder();

        // Building block list
        if (skip>0)
            stringBuilder.append(String.format("%50s\n", ".").repeat(2))
                    .append(String.format("%50s", "V"));
        for (Block block : (LinkedList<Block>) blockChainList.clone()) {
            stringBuilder.append("\n");
            stringBuilder.append(ConsoleHelper.buildBlockString(block));
            stringBuilder.append(String.format("\n %50s", "V"));
        }
        stringBuilder.setLength(stringBuilder.length()-2);
        if (skip+take-1 < blockChain.getBlockChain().size()-1)
            stringBuilder.append(String.format("\n%50s\n", "V"))
                    .append(String.format("%50s\n", ".").repeat(2));

        // Building blockchain info
        stringBuilder.append(String.format("\ndifficulty: %s\n", blockChain.getDifficulty()));
        stringBuilder.append(String.format("mining reward: %s\n", blockChain.getMiningReward()));

        // Building pending transactions
        stringBuilder.append("Pending Transactions: \n");
        for (Transaction transaction :
                blockChain.getPendingTransactions()) {
            stringBuilder.append(String.format("%-96s\n",
                    String.format(">from: %-15s to: %-15s amount: %-19f date: %s",
                            transaction.getFromId(), transaction.getToId(),
                            transaction.getAmount(), ConsoleHelper.dateFormat.format(new Date(transaction.getTimestamp())))));
        }
        return stringBuilder.toString();
    }
    private static String buildBlockString(Block block){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("-".repeat(100));
        stringBuilder.append(String.format("\n| depth: %-89s |", block.getDepth()));
        if (Objects.equals(block.getPrevHash(), "0"))
            stringBuilder.append(String.format("\n| prevHash: %-86s |\n", "0"));
        else
//            stringBuilder.append(String.format(
//                    "\n| %-96s |\n",
//                    String.format("prevHash: %4s...%4s",
//                            block.getPrevHash().substring(0,4),
//                            block.getPrevHash().substring(block.getPrevHash().length()-4)
//                    ))
//            );
            stringBuilder.append(String.format("\n| prevHash: %-86s |\n", block.getPrevHash()));
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
        return stringBuilder.toString();
    }

    public static void printBlock(Blockchain blockChain, String hash, boolean printPrev) {
        Block block = blockChain.getBlockChain().stream()
                .filter(b -> Objects.equals(b.getHash(), hash))
                .findFirst()
                .orElse(null);
        if (block == null)
            System.out.println("Block not found for hash: "+hash);
        else {
            StringBuilder stringBuilder = new StringBuilder();
            if (block.getDepth() > 0){
                if (block.getDepth() > 1){
                    stringBuilder.append(String.format("%50s\n", ".").repeat(2))
                            .append(String.format("%50s\n", "V"));
                }
                if (printPrev) {
                    Block blockPrev = blockChain.getBlockChain().stream()
                            .filter(b -> Objects.equals(b.getDepth(), block.getDepth() - 1))
                            .findFirst()
                            .orElse(null);
                    if (blockPrev != null) {
                        stringBuilder.append(ConsoleHelper.buildBlockString(blockPrev));
                        stringBuilder.append(String.format("\n %50s\n", "V"));
                    }
                }
            }
            stringBuilder.append(ConsoleHelper.buildBlockString(block));
            if (block.getDepth() < blockChain.getLastBlock().getDepth())
                stringBuilder.append(String.format("\n%50s", ".").repeat(2))
                        .append(String.format("\n%50s", "V"));
            System.out.println(stringBuilder);
        }
    }
}
