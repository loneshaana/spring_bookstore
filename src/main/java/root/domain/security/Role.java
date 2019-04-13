package root.domain.security;

import javax.persistence.*;

import root.domain.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class Role extends BaseEntity{
    @OneToMany(mappedBy = "role",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private Set<UserRoles> userRoles = new HashSet<>();
}
