package paw.project.calendarapp.TO;

import lombok.Data;

@Data
public class UpdateNote {

    private Long noteId;
    private String title;
    private String content;
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
