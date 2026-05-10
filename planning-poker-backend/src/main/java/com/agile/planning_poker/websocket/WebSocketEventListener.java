package com.agile.planning_poker.websocket;

import com.agile.planning_poker.participant.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final ParticipantRepository participantRepository;

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event){
        String sessionId = event.getSessionId();
        participantRepository.findBySessionId(sessionId).ifPresent(participant -> {
            participant.setIsConnected(false);
            participantRepository.save(participant);
        });
    }


}
