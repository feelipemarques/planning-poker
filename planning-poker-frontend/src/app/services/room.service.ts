import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { WebsocketService } from './websocket.service';
import { Observable, take } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class RoomService {

  private apiUrl = 'http://localhost:8080';

  constructor(
    private http: HttpClient,
    private webSocketService: WebsocketService
  ) {}

  createRoom(): Observable<String> {
    return this.http.post<String>(`${this.apiUrl}/room`, {}, { responseType: 'text' as 'json'});
  }

  joinRoom(code: string, nickname: string): void {
    this.webSocketService.connect();
    this.webSocketService.onConnected().pipe(take(1)).subscribe(() => {
      this.webSocketService.publish(`/app/room/${code}/join`, { nickname });
    })
  }

  disconnect(): void {
    this.webSocketService.disconnect();
  }

  createStory(roomCode: string, storyName: string, storyDescription: string, storyPriority: string): void{
    this.webSocketService.publish(`/app/room/${roomCode}/create`, {storyName, storyDescription, storyPriority});
  }

  subscribeToParticipants(roomCode: string): Observable<any> {
    return this.webSocketService.subscribe(`/topic/room/${roomCode}/participants`);
  }

  subscribeToRound(roomCode: string): Observable<any> {
    return this.webSocketService.subscribe(`/topic/room/${roomCode}/round`);
  }

  subscribeToVotes(roomCode: string): Observable<any> {
    return this.webSocketService.subscribe(`/topic/room/${roomCode}/votes`);
  }

  subscribeToStories(roomCode: string): Observable<any> {
    return this.webSocketService.subscribe(`/topic/room/${roomCode}/stories`);
  }

  castVote(roomCode: string, userStoryId: number, value: string): void {
    this.webSocketService.publish(`/app/room/${roomCode}/vote`, { userStoryId, value });
  }

  revealCards(roomCode: string, userStoryId: number): void {
    this.webSocketService.publish(`/app/room/${roomCode}/reveal`, { userStoryId });
  }

  restartRound(roomCode: string, userStoryId: number): void {
    this.webSocketService.publish(`/app/room/${roomCode}/restart`, { userStoryId });
  }

  startVoting(roomCode: string, storyId: number): void {
    this.webSocketService.publish(`/app/room/${roomCode}/start`, { storyId });
  }
  
}
