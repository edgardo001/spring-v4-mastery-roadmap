package com.springroadmap.restclient.controller;

import com.springroadmap.restclient.dto.Todo;
import com.springroadmap.restclient.service.TodoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint que expone la integracion externa dentro de nuestra propia API.
 * Delega al TodoService, que a su vez consume https://jsonplaceholder.typicode.com/todos/{id}.
 */
@RestController
@RequestMapping("/api/todos")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping("/{id}")
    public Todo getById(@PathVariable long id) {
        return todoService.fetch(id);
    }
}
