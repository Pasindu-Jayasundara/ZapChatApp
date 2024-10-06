package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "group_message")
public class Group_message implements Serializable{
    
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column(name = "message",nullable = false)
    private String message;
    
    @OneToOne
    @JoinColumn(name = "group_chat_id")
    private Group_chat group_chat;

    public Group_message() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Group_chat getGroup_chat() {
        return group_chat;
    }

    public void setGroup_chat(Group_chat group_chat) {
        this.group_chat = group_chat;
    }
    
}
