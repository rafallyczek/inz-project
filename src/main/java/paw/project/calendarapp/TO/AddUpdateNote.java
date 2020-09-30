package paw.project.calendarapp.TO;

import lombok.Data;
import paw.project.calendarapp.model.Note;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class AddUpdateNote {

    private Long id;
    private String title;
    private String content;
    private Integer userId;
    private String date;
    private String time;
    private Integer calendarId;

    //Zwróć instancję notki do dodania
    public Note toAddNote(){
        Note note = new Note();
        LocalDateTime dateTime = toLocalDateTime(date,time);
        note.setTitle(title);
        note.setContent(content);
        note.setUserId(userId);
        note.setDateTime(dateTime);
        note.setCalendarId(calendarId);
        return note;
    }

    //Zwróć instancję notki do aktualizacji
    public Note toUpdateNote(){
        Note note = new Note();
        LocalDateTime dateTime = toLocalDateTime(date,time);
        note.setId(id);
        note.setTitle(title);
        note.setContent(content);
        note.setUserId(userId);
        note.setDateTime(dateTime);
        note.setCalendarId(calendarId);
        return note;
    }

    //Konwersja daty i czasu na LocalDateTime
    public LocalDateTime toLocalDateTime(String date, String time){
        String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";
        String dateTime = date+" "+time+":00";
        return LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern(dateTimeFormat));
    }

}