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
@Table(name = "user")
public class User implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "mobile", length = 10, nullable = false)
    private String mobile;

    @Column(name = "first_name", length = 45, nullable = false)
    private String first_name;

    @Column(name = "last_name", length = 45, nullable = false)
    private String last_name;

    @Column(name = "password", length = 20, nullable = false)
    private String password;

    @Column(name = "registered_datetime", nullable = false)
    private Date registered_datetime;

    @ManyToOne
    @JoinColumn(name = "user_online_status_id")
    private User_online_status user_online_status;

    @ManyToOne
    @JoinColumn(name = "user_verified_status_id")
    private User_verified_status user_verified_status;

    @Column(name = "otp", length = 10, nullable = false)
    private int otp;
    
    @Column(name = "about", length = 45, nullable = true)
    private String about;
    
    @Column(name = "profile_image", nullable = true)
    private String profile_image;

    public User() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getRegistered_datetime() {
        return registered_datetime;
    }

    public void setRegistered_datetime(Date registered_datetime) {
        this.registered_datetime = registered_datetime;
    }

    public User_online_status getUser_online_status() {
        return user_online_status;
    }

    public void setUser_online_status(User_online_status user_online_status) {
        this.user_online_status = user_online_status;
    }

    public User_verified_status getUser_verified_status() {
        return user_verified_status;
    }

    public void setUser_verified_status(User_verified_status user_verified_status) {
        this.user_verified_status = user_verified_status;
    }

    public int getOtp() {
        return otp;
    }

    public void setOtp(int otp) {
        this.otp = otp;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

   
}
