package paw.project.calendarapp.model;

import lombok.Data;

@Data
public class Reminder {

    private String title;
    private String content;
    private String date;
    private String time;
    private Integer reminderId;

    public Reminder(String title, String content, String date, String time, Integer reminderId){
        this.title = title;
        this.content = content;
        this.date = date;
        this.time = time;
        this.reminderId = reminderId;
    }

}
