package com.bankofbaku.model;

import com.bankofbaku.record.IRecordItem;
import com.bankofbaku.record.annotation.FieldRename;
import com.bankofbaku.record.annotation.RecordIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * The persistent class for the role_definitions database table.
 */

@Entity
@NamedQueries({
        @NamedQuery(name = "RoleDefinition.findById", query = "SELECT rd FROM RoleDefinition rd WHERE rd.id = :id"),
        @NamedQuery(name = "RoleDefinition.findAll", query = "SELECT rd FROM RoleDefinition rd ORDER BY rd.id ASC"),
        @NamedQuery(name = "RoleDefinition.findByUser", query = "SELECT r.roleDefinition FROM Role r WHERE r.user = :user ORDER BY r.roleDefinition.id ASC")
})
@Table(name = "role_definition")
public class RoleDefinition implements Serializable, IRecordItem {
    private static final long serialVersionUID = 1L;
    private int id;
    private String description;
    private String name;
    private List<Role> roles;

    public RoleDefinition() {
    }

    @FieldRename(value = "rdid", rename = true, ignorePrefix = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(length = 255)
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(length = 32)
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // bi-directional many-to-one association to Role
    @RecordIgnore
    @OneToMany(mappedBy = "roleDefinition")
    public List<Role> getRoles() {
        return this.roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    @Override
    public RoleDefinition clone() throws CloneNotSupportedException {
        return (RoleDefinition) super.clone();
    }

}