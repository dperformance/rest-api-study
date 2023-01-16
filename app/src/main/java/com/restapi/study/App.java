package com.restapi.study;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.net.InetSocketAddress;

@SpringBootApplication
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
//    private final static int PORT = 8888;
//
//    public String getGreeting() {
//        return "Hello World!";
//    }
//
//    public static void main(String[] args) {
//        System.out.println(new App().getGreeting());
//
//        try {
//            InetSocketAddress address = new InetSocketAddress(App.PORT);
//            HttpServer httpServer = HttpServer.create(address, 0);
//
//            HttpHandler handler = new TaskHttpHandler();
//            httpServer.createContext("/", handler);
//            httpServer.start();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
