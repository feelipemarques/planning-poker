import { Component, OnInit } from '@angular/core';
import { RoomService } from '../../services/room.service';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-room',
  imports: [ FormsModule ],
  templateUrl: './room.component.html',
  styleUrl: './room.component.css',
})
export class RoomComponent implements OnInit{

  roomCode = '';
  nickname = '';
  isOwner = false;
  participants: string[] = [];
  votes: any[] = [];
  stories: any[] = [];
  storyName = '';
  storyDescription = '';
  storyPriority = '';
  selectedStoryId: number | null = null;
  selectedStoryName = '';
  isVoting = false;
  fibonacciDeck = [1, 2, 3, 5, 8, 13, 21];
  selectedCard: number | null = null;
  revealedVotes: string[] = [];
  average = '';

  constructor(
    private roomService: RoomService,
    private route: ActivatedRoute,
    private router: Router
  ){
    this.nickname = this.router.getCurrentNavigation()?.extras?.state?.['nickname'] ?? sessionStorage.getItem('nickname') ?? '';
  }

  ngOnInit(): void {
    this.roomCode = this.route.snapshot.paramMap.get('code') ?? '';
    this.roomService.joinRoom(this.roomCode, this.nickname);

    this.roomService.subscribeToRound(this.roomCode).subscribe(event => {
      this.isVoting = true;
      this.selectedStoryId = event.storyId;
      this.selectedStoryName = event.storyName;
    })

    this.roomService.subscribeToParticipants(this.roomCode).subscribe(event => {
      this.participants = event.participants;
      this.isOwner = event.ownerNickname === this.nickname;
    });

    this.roomService.subscribeToVotes(this.roomCode).subscribe(event => {
      if(event.type === 'VOTE_CAST'){
        this.votes.push(event.nickname);
      }else if(event.type === 'VOTES_REVEALED'){
        this.revealedVotes = event.votes;
        this.average = event.finalEstimate;
        this.isVoting = false;
      
      }
    });

    this.roomService.subscribeToStories(this.roomCode).subscribe(event => {
      this.stories.push(event);
    });

  }

  ngOnDestroy(): void {
    this.roomService.disconnect();
  }

  copyCode(): void {
    navigator.clipboard.writeText(this.roomCode);
  }

  createStory(): void {
    this.roomService.createStory(this.roomCode, this.storyName, this.storyDescription, this.storyPriority);
  }
  
  startVoting(): void {
    const story = this.stories.find(s => s.id == this.selectedStoryId);
    this.selectedStoryName = story.storyName;
    this.roomService.startVoting(this.roomCode, Number(this.selectedStoryId)!);
    this.isVoting = true;
  }

  selectCard(value: number): void{
    this.selectedCard = value;
  }

  registerVote(): void {
    if (!this.selectedStoryId || !this.selectedCard) return;
    this.roomService.castVote(this.roomCode, this.selectedStoryId, this.selectedCard.toString());
  }
  
  revealCards(): void {
      this.roomService.revealCards(this.roomCode, this.selectedStoryId!);
  }

}
