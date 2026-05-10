import { Injectable } from '@angular/core';
import { RxStomp, RxStompConfig } from '@stomp/rx-stomp';
import { map, Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class WebsocketService {
  private rxStomp = new RxStomp();

  connect(): void{
    const config: RxStompConfig = {
      brokerURL: 'ws://localhost:8080/ws/websocket',
      reconnectDelay: 5000
    };
    this.rxStomp.configure(config);
    this.rxStomp.activate();
  }

  onConnected(): Observable<void> {
    return this.rxStomp.connected$.pipe(map(() => void 0));
  }

  disconnect(): void{
    this.rxStomp.deactivate();
  }

  subscribe<T>(topic: string): Observable<T>{
    return this.rxStomp.watch(topic).pipe(
      map(message => JSON.parse(message.body) as T)
    );
  }

  publish(destination: string, body: object = {}): void {
    this.rxStomp.publish({
      destination,
      body: JSON.stringify(body)
    });
  }

}
