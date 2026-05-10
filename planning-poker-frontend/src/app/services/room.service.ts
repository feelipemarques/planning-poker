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

  subscribeToParticipants(code: string): Observable<any> {
    return this.webSocketService.subscribe(`/topic/room/${code}/participants`);
  }

  subscribeToVotes(code: string): Observable<any> {
    return this.webSocketService.subscribe(`/topic/room/${code}/votes`);
  }

  castVote(code: string, userStoryId: number, value: string): void {
    this.webSocketService.publish(`/app/room/${code}/vote`, { userStoryId, value });
  }

  revealCards(code: string, userStoryId: number): void {
    this.webSocketService.publish(`/app/room/${code}/reveal`, { userStoryId });
  }

  restartRound(code: string, userStoryId: number): void {
    this.webSocketService.publish(`/app/room/${code}/restart`, { userStoryId });
  }

  
}
