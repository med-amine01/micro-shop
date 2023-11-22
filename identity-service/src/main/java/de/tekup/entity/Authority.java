package de.tekup.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.Collection;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "authorities")
public class Authority extends AbstractEntity {
    
    private String name;
    
    @ManyToMany(mappedBy = "authorities")
    private Collection<Role> roles;
}
