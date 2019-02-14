package com.serical.funai;

import java.util.Scanner;

public class AI {

    public static void main(String[] args) {
        for (Scanner scanner = new Scanner(System.in); ; ) {
            System.out.println(scanner.nextLine()
                    .replaceAll("吗?[?？]", "!")
                    .replaceAll("你是", "我是"));
        }
    }
}
