package thehatefulsix.carsharingapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import thehatefulsix.carsharingapp.model.user.Role;
import thehatefulsix.carsharingapp.model.user.RoleName;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(RoleName name);
}
