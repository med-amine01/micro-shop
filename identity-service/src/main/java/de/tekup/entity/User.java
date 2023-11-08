package de.tekup.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Set;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class User extends AbstractEntity {
    
    private String name;
    
    @Column(unique = true)
    private String email;
    
    private String password;
    
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    //this will create third table called USER_ROLE with two columns =>  USER_ID ; ROLE_ID
    @JoinTable(
            name = "USER_ROLE",
            joinColumns = {@JoinColumn(name = "USER_ID")},
            inverseJoinColumns = {@JoinColumn(name = "ROLE_ID")}
    )
    private Set<Role> roles;
}
