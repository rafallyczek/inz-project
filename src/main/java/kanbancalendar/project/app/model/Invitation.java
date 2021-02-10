package kanbancalendar.project.app.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "invitations")
public class Invitation {

    //Pola
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer calendarId;
    private Integer senderId;
    private Integer receiverId;

    @Transient
    private String calendarTitle;
    @Transient
    private String senderName;

}
