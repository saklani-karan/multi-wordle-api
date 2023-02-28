package com.karansaklani20.multiwordle.rooms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.karansaklani20.multiwordle.rooms.model.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

}
