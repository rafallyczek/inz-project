package paw.project.calendarapp.TO;

import lombok.Data;

@Data
public class UpdateNote {

    private Long noteId;
    private String title;
    private String content;
    private String date;
    private String time;

}
