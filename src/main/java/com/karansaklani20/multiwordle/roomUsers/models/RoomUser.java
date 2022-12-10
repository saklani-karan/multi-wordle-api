package com.karansaklani20.multiwordle.roomUsers.models;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.karansaklani20.multiwordle.rooms.model.Room;
import com.karansaklani20.multiwordle.users.models.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "room_users")
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Room room;

    @OneToOne
    private User user;

    @Enumerated(EnumType.STRING)
    private RoomUserStatus status;

    @Enumerated(EnumType.STRING)
    private RoomUserRole userRole;
}
