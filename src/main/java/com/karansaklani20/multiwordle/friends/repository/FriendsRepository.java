package com.karansaklani20.multiwordle.friends.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.karansaklani20.multiwordle.friends.models.Friend;

@Repository
public interface FriendsRepository extends JpaRepository<Friend, Long> {
    @Query(value = "SELECT * FROM friends WHERE (primary_user_id=:primaryId AND secondary_user_id=:secondaryId) OR (primary_user_id=:secondaryId AND secondary_user_id=:primaryId) ", nativeQuery = true)
    Friend findFriendByPrimaryAndSecondaryUserId(@Param("primaryId") Long primaryId,
            @Param("secondaryId") Long secondaryId);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO friends (primary_user_id,secondary_user_id) VALUES (:primaryId,:secondaryId)", nativeQuery = true)
    Object saveFriendByUserId(@Param("primaryId") Long primaryId, @Param("secondaryId") Long secondaryId);

}
