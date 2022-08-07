package com.restapi.study;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class TaskHttpHandler implements HttpHandler {



    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        InputStream inputStream = exchange.getRequestBody();
        String body = new BufferedReader(new InputStreamReader(inputStream))
                .lines()
                .collect(Collectors.joining("\n"));

        String content = "Hello world!";

        System.out.println(method);
        System.out.println(path);
        System.out.println(body);

        // TODO
        // http://localhost:8888 Create a response message
        //   - output stream
        // implement Get method

        
        exchange.sendResponseHeaders(200, content.getBytes(StandardCharsets.UTF_8).length);

        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(content.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
        outputStream.close();



//        if (method.equals("GET") && path.equals("/tasks")) {
//            System.out.println("GET Check");
//        }

    }
}
