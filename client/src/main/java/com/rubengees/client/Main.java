package com.rubengees.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * TODO: Describe class
 *
 * @author Ruben Gees
 */
public class Main {

    public static void main(String[] args) throws IOException {
        Socket test = new Socket("127.0.0.1", 8999);

        PrintWriter out = new PrintWriter(test.getOutputStream());
        BufferedReader in = new BufferedReader(new InputStreamReader(test.getInputStream()));
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("Send something: ");
            String toSend = sc.nextLine();

            out.println(toSend);
            out.flush();

            System.out.println(in.readLine());
        }
    }

}
