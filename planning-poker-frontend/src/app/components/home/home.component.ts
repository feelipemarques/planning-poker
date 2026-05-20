import { Component } from '@angular/core';
import { RoomService } from '../../services/room.service';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-home',
  imports: [FormsModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css',
})
export class HomeComponent {

  roomCode = '';
  nickname = '';

  constructor(
    private roomService: RoomService,
    private router: Router
  ){}
  
  createRoom(){
    this.roomService.createRoom().subscribe(code => {
      sessionStorage.setItem('nickname', this.nickname);
      this.router.navigate(['/room', code], {
        state: { nickname: this.nickname }
      });
    });
  }

  joinRoom(){
    sessionStorage.setItem('nickname', this.nickname);
    this.router.navigate(['/room', this.roomCode], {
      state: { nickname: this.nickname }
    });
  }

}
