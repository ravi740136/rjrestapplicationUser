package rj.training.rest.user.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import rj.training.rest.user.User;

public interface UserRepository extends JpaRepository<User, Long> {}
