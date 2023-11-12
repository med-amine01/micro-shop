package de.tekup.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Role extends AbstractEntity {
    
    @Column(unique = true)
    private String roleName;
}
