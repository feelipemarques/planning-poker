package com.agile.planning_poker.room;

import com.agile.planning_poker.participant.Participant;
import com.agile.planning_poker.userstory.UserStory;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class  Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<UserStory> userStories;

    @Enumerated(EnumType.STRING)
    private RoomStatus status;

    private LocalDateTime lastInteraction;
    private LocalDateTime createdAt;
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<Participant> participants;
    private String roomCode;

}
