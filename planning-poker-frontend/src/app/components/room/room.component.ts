import { Component, OnInit } from '@angular/core';
import { RoomService } from '../../services/room.service';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-room',
  imports: [],
  templateUrl: './room.component.html',
  styleUrl: './room.component.css',
})
export class RoomComponent implements OnInit{

  roomCode = '';
  nickname = '';
  participants: string[] = [];
  votes: any[] = [];
  stories: any[] = [];

  constructor(
    private roomService: RoomService,
    private route: ActivatedRoute,
    private router: Router
  ){
    this.nickname = this.router.getCurrentNavigation()?.extras?.state?.['nickname'] ?? '';
  }

  ngOnInit(): void {
    this.roomCode = this.route.snapshot.paramMap.get('code') ?? '';
    this.roomService.joinRoom(this.roomCode, this.nickname);

    this.roomService.subscribeToParticipants(this.roomCode).subscribe(event => {
      this.participants = event.participants;
    });

    this.roomService.subscribeToVotes(this.roomCode).subscribe(event => {
      this.votes = event.votes;
    });
  }

  ngOnDestroy(): void {
    this.roomService.disconnect();
  }

  copyCode(): void {
    navigator.clipboard.writeText(this.roomCode);
  }
}
