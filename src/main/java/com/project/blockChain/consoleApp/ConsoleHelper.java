package com.project.blockChain.consoleApp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
}
