package paw.project.calendarapp.model;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Entity
@Data
@Table(name = "Notes")
public class Note {

    //Pola
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String content;
    private Integer userId;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateTime;
    private Integer calendarId;
    private boolean isTask;
    private String status;

    @Transient
    private LocalDate date;
    @Transient
    private String time;

    //Ustaw datę
    public void setNoteDate(String localTimezone, String ownerTimezone){
        ZonedDateTime ownerDateTime = this.dateTime.atZone(ZoneId.of(ownerTimezone));
        ZonedDateTime localDateTime = ownerDateTime.withZoneSameInstant(ZoneId.of(localTimezone));
        this.date = localDateTime.toLocalDate();
    }

    //Ustaw czas
    public void setNoteTime(String localTimezone, String ownerTimezone){
        ZonedDateTime ownerDateTime = this.dateTime.atZone(ZoneId.of(ownerTimezone));
        ZonedDateTime localDateTime = ownerDateTime.withZoneSameInstant(ZoneId.of(localTimezone));
        this.time = localDateTime.toLocalTime().toString();
    }

    //Zwróć dzień
    public String getDay(){
        String date = dateTime.toLocalDate().toString();
        String day = date.substring(date.length()-2);
        return day.startsWith("0") ? date.substring(date.length()-1) : day;
    }

    //Zwróć miesiąc
    public String getMonth(){
        String date = dateTime.toLocalDate().toString();
        String month = date.substring(date.length()-5,date.length()-3);
        return month.startsWith("0") ? month.substring(month.length()-1) : month;
    }

    //Zwróć rok
    public String getYear(){
        String date = dateTime.toLocalDate().toString();
        return date.substring(0,4);
    }

}
