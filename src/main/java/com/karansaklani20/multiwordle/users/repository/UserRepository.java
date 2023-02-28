package com.karansaklani20.multiwordle.users.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.karansaklani20.multiwordle.users.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    @Query(value = "SELECT users.* FROM room_users LEFT JOIN users ON users.id=user_id WHERE room_id=:roomId", nativeQuery = true)
    List<User> getUsersForRoom(@Param("roomId") Long roomId);
}
