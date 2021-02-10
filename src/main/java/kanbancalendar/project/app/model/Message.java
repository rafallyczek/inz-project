package kanbancalendar.project.app.model;

import lombok.Data;

import java.util.HashMap;

@Data
public class Message {

    private String title;
    private String content;
    private HashMap<String, String> data;

}
