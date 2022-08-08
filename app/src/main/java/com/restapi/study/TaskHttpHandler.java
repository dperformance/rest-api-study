package com.restapi.study;

import com.restapi.study.models.Task;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class TaskHttpHandler implements HttpHandler {
    private final Map<Long, String> tasks = new HashMap<>();

    public TaskHttpHandler() {

        tasks.put(1L, "Dyson");
        tasks.put(2L, "qwer");
        tasks.put(3L, "asdf");
        tasks.put(4L, "asdcv");
    }

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
        // implement GET Method
        //  - /tasks (getTasks)
        //  - /tasks/{id} (getTask)
        // implement POST Method
        //  - 조회 하기 위해 POST도 구현을 해야 한다.

        if (method.equals("GET")) {
            if (path.equals("/tasks")) {
                System.out.println("get all");
                System.out.println(tasks);
            }
            if (path.startsWith("/tasks/")) {
                Long id = Long.parseLong(path.substring("/tasks/".length()));
                System.out.println(tasks.get(id));
            }
        }



    }
    // response format
    private void responseSend(String content, HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(200, content.getBytes(StandardCharsets.UTF_8).length);

        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(content.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
        outputStream.close();
    }
}
