package com.serical.funai;

public class TestCode {

    public String nextLine(String result) {

        if (result.contains("傻逼")) {
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    System.out.println("说了你是个傻逼吧，还不信？");
                    System.exit(666);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
            return "你才是傻逼";
        }
        return result;
    }
}
