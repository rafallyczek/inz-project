package paw.project.calendarapp.TO;

import lombok.Data;

@Data
public class AddNote {

    private String title;
    private String content;
    private Integer userId;
    private String date;
    private String time;
    private Integer calendarId;
    private boolean isTask;

    public void setIsTask(boolean isTask){
        this.isTask = isTask;
    }

    public boolean getIsTask(){
        return  this.isTask;
    }

}
