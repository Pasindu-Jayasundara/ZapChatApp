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
@Table(name = "message")
public class Message implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "message", nullable = false)
    private String message;

    @OneToOne
    @JoinColumn(name = "single_chat_id")
    private Single_chat single_chat;

    public Message() {
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

    public Single_chat getSingle_chat() {
        return single_chat;
    }

    public void setSingle_chat(Single_chat single_chat) {
        this.single_chat = single_chat;
    }
    
    

}
