package com.bankofbaku.model;

import com.bankofbaku.record.IRecordItem;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * The persistent class for the roles database table.
 */
@Entity
@NamedQueries({
        @NamedQuery(name = "Role.deleteByUserAndRoleDefinition", query = "DELETE FROM Role r WHERE r.user = :user AND r.roleDefinition = :roleDefinition")
})
@Table(name = "role")
public class Role implements Serializable, IRecordItem {
    private static final long serialVersionUID = 1L;
    private int id;
    private Date created;
    private RoleDefinition roleDefinition;
    private User user;

    public Role() {
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
    public Date getCreated() {
        return this.created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    // bi-directional many-to-one association to RoleDefinition
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "rdid", nullable = false)
    public RoleDefinition getRoleDefinition() {
        return this.roleDefinition;
    }

    public void setRoleDefinition(RoleDefinition roleDefinition) {
        this.roleDefinition = roleDefinition;
    }

    // bi-directional many-to-one association to User
    @ManyToOne
    @JoinColumn(name = "uid")
    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public Role clone() throws CloneNotSupportedException {
        return (Role) super.clone();
    }

}