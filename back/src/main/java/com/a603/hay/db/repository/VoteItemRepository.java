package com.a603.hay.db.repository;

import com.a603.hay.db.entity.VoteItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteItemRepository extends JpaRepository<VoteItem, Long> {

}
