package com.bankofbaku.model;

import com.bankofbaku.record.IRecordItem;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * The persistent class for the sessions database table.
 */
@Entity
@Table(name = "session")
public class Session implements Serializable, IRecordItem {
    private static final long serialVersionUID = 1L;
    private int id;
    private Date closed;
    private Date opened;
    private User user;

    public Session() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getClosed() {
        return this.closed;
    }

    public void setClosed(Date closed) {
        this.closed = closed;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getOpened() {
        return this.opened;
    }

    public void setOpened(Date opened) {
        this.opened = opened;
    }

    // bi-directional many-to-one association to User
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "uid", nullable = false)
    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public Session clone() throws CloneNotSupportedException {
        return (Session) super.clone();
    }

}