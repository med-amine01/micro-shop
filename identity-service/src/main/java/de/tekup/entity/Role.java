package de.tekup.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "roles")
public class Role extends AbstractEntity {
    
    @Column(unique = true)
    private String roleName;
    
    @ManyToMany(mappedBy = "roles")
    private Collection<User> users;
    
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "roles_authorities",
            joinColumns = {@JoinColumn(name = "roles_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "authorities_id", referencedColumnName = "id")}
    )
    private Collection<Authority> authorities;
}
