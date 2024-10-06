package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "group_chat_read")
public class Group_chat_read implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "group_member_id")
    private Group_member group_member;

    @ManyToOne
    @JoinColumn(name = "group_chat_id")
    private Group_chat group_chat;

    public Group_chat_read() {
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

    public Group_chat getGroup_chat() {
        return group_chat;
    }

    public void setGroup_chat(Group_chat group_chat) {
        this.group_chat = group_chat;
    }
    
}
