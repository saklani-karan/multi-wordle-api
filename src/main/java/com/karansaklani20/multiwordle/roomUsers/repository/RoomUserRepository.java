package com.karansaklani20.multiwordle.roomUsers.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.karansaklani20.multiwordle.roomUsers.models.RoomUser;
import com.karansaklani20.multiwordle.roomUsers.models.RoomUserStatus;
import com.karansaklani20.multiwordle.rooms.model.Room;
import com.karansaklani20.multiwordle.users.models.User;

public interface RoomUserRepository extends JpaRepository<RoomUser, Long> {
    RoomUser findByUserAndRoom(User user, Room room);

    List<RoomUser> findByRoom(Room room);

    @Query(value = "SELECT users.* FROM room_users LEFT JOIN users ON users.id=user_id WHERE room_id=:roomId", nativeQuery = true)
    List<User> getRoomUserDataForRoom(@Param("roomId") Long roomId);

    @Query(value = "SELECT COUNT(id) FROM room_users WHERE room_id=:roomId AND status=:#{#status.name()}", nativeQuery = true)
    long getUserStatusCount(@Param("roomId") Long roomId, @Param("status") RoomUserStatus status);

    @Query(value = "SELECT COUNT(id) FROM room_users WHERE room_id=:roomId", nativeQuery = true)
    long getUserCount(@Param("roomId") Long roomId);
}
