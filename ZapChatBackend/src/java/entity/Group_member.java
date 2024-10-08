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
@Table(name = "group_member")
public class Group_member implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "group_table_id")
    private Group_table group_table;

    @ManyToOne
    @JoinColumn(name = "group_member_role_id")
    private Group_member_role group_member_role;

    public Group_member() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Group_member_role getGroup_member_role() {
        return group_member_role;
    }

    public void setGroup_member_role(Group_member_role group_member_role) {
        this.group_member_role = group_member_role;
    }

    public Group_table getGroup_table() {
        return group_table;
    }

    public void setGroup_table(Group_table group_table) {
        this.group_table = group_table;
    }

}
