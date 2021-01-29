package paw.project.calendarapp.TO;

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

}
