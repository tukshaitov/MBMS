package com.bankofbaku.model;

import com.bankofbaku.record.IRecordItem;
import com.bankofbaku.record.annotation.RecordIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * The persistent class for the users database table.
 */
@Entity
@NamedQueries({
        @NamedQuery(name = "User.findAll", query = "SELECT u FROM User u WHERE u.status IN ('ACTIVE', 'LOCKED')"),
        @NamedQuery(name = "User.findByLogin", query = "SELECT u FROM User u WHERE u.login = :login AND u.status IN ('ACTIVE')")
})
@Table(name = "user")
public class User implements Serializable, IRecordItem {
    public static enum Status {
        ACTIVE, LOCKED, DELETED
    }

    private static final long serialVersionUID = 1L;
    private Integer id;
    private Date created;
    private Date expired;
    private String login;
    private String password;
    private Status status;
    private Date updated;
    private Integer version;
    private List<Role> roles;
    private List<Session> sessions;

    public User() {
    }

    @Id
    @Column(unique = true, nullable = false)
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    @RecordIgnore
    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getCreated() {
        return this.created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getExpired() {
        return this.expired;
    }

    public void setExpired(Date expired) {
        this.expired = expired;
    }

    @Column(length = 32)
    public String getLogin() {
        return this.login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @RecordIgnore
    @Column(length = 100)
    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Column(nullable = false, length = 7, columnDefinition = "enum('ACTIVE', 'LOCKED', 'DELETED')")
    @Enumerated(EnumType.STRING)
    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getUpdated() {
        return this.updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    @RecordIgnore
    @Version
    public Integer getVersion() {
        return this.version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    // bi-directional many-to-one association to Role
    @OneToMany(mappedBy = "user")
    @RecordIgnore
    public List<Role> getRoles() {
        return this.roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    // bi-directional many-to-one association to Session
    @RecordIgnore
    @OneToMany(mappedBy = "user")
    public List<Session> getSessions() {
        return this.sessions;
    }

    public void setSessions(List<Session> sessions) {
        this.sessions = sessions;
    }

    @Override
    public User clone() throws CloneNotSupportedException {
        return (User) super.clone();
    }
}
