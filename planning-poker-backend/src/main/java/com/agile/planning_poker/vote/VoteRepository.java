package com.agile.planning_poker.vote;

import com.agile.planning_poker.userstory.UserStory;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    public List<Vote> findByUserStory(UserStory userStory);

    @Transactional
    public void deleteByUserStory(UserStory userStory);
}
