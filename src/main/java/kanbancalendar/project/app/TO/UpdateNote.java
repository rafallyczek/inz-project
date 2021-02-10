package kanbancalendar.project.app.TO;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UpdateNote {

    private Long noteId;
    @NotBlank(message = "Tytuł nie może byc pusty.")
    private String title;
    @NotBlank(message = "Zawartość nie może być pusta.")
    private String content;
    private Integer userId;
    private String date;
    private String time;
    private boolean isTask;

    public void setIsTask(boolean isTask){
        this.isTask = isTask;
    }

    public boolean getIsTask(){
        return this.isTask;
    }

    //Zwróć dzień
    public String getDay(){
        String day = date.substring(date.length()-2);
        return day.startsWith("0") ? date.substring(date.length()-1) : day;
    }

    //Zwróć miesiąc
    public String getMonth(){
        String month = date.substring(date.length()-5,date.length()-3);
        return month.startsWith("0") ? month.substring(month.length()-1) : month;
    }

    //Zwróć rok
    public String getYear(){
        return date.substring(0,4);
    }

}
