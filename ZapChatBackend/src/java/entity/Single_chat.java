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
@Table(name = "single_chat")
public class Single_chat implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "from_user_id")
    private User from_user;

    @ManyToOne
    @JoinColumn(name = "to_user_id")
    private User to_user;

    @Column(name = "datetime", nullable = false)
    private Date datetime;

    @ManyToOne
    @JoinColumn(name = "message_status_id")
    private Message_status message_status;

    @ManyToOne
    @JoinColumn(name = "message_content_type_id")
    private Message_content_type message_content_type;

    public Single_chat() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getFrom_user() {
        return from_user;
    }

    public void setFrom_user(User from_user) {
        this.from_user = from_user;
    }

    public User getTo_user() {
        return to_user;
    }

    public void setTo_user(User to_user) {
        this.to_user = to_user;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public Message_status getMessage_status() {
        return message_status;
    }

    public void setMessage_status(Message_status message_status) {
        this.message_status = message_status;
    }

    public Message_content_type getMessage_content_type() {
        return message_content_type;
    }

    public void setMessage_content_type(Message_content_type message_content_type) {
        this.message_content_type = message_content_type;
    }
    
    

}
