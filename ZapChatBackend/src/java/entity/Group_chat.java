package entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "group_chat")
public class Group_chat implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "group_member_id")
    private Group_member group_member;

    @ManyToOne
    @JoinColumn(name = "message_status_id")
    private Message_status message_status;

    @Column(name = "datetime", nullable = false)
    private Date datetime;

    @ManyToOne
    @JoinColumn(name = "message_content_type_id")
    private Message_content_type message_content_type;

    public Group_chat() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Group_member getGroup_member() {
        return group_member;
    }

    public void setGroup_member(Group_member group_member) {
        this.group_member = group_member;
    }

    public Message_status getMessage_status() {
        return message_status;
    }

    public void setMessage_status(Message_status message_status) {
        this.message_status = message_status;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public Message_content_type getMessage_content_type() {
        return message_content_type;
    }

    public void setMessage_content_type(Message_content_type message_content_type) {
        this.message_content_type = message_content_type;
    }
    
}
