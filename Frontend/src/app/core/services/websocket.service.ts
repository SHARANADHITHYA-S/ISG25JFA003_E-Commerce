import { Injectable } from '@angular/core';
import { webSocket, WebSocketSubject } from 'rxjs/webSocket';
import { Observable, Subject, retry, share } from 'rxjs';
import { StorageService } from './storage.service';

@Injectable({
    providedIn: 'root'
})
export class WebsocketService {
    private socket$: WebSocketSubject<any> | null = null;
    private messagesSubject = new Subject<any>();
    public messages$ = this.messagesSubject.asObservable().pipe(share(), retry());

    constructor(private storageService: StorageService) {}

    connect(userId: string): void {
        if (!this.socket$ || this.socket$.closed) {
            const token = this.storageService.getItem<string>('token');
            this.socket$ = webSocket({
                url: `ws://localhost:8080/ws?userId=${userId}&token=${token}`,
                deserializer: msg => JSON.parse(msg.data)
            });

            this.socket$.subscribe({
                next: msg => this.messagesSubject.next(msg),
                error: err => console.error('WebSocket error:', err),
                complete: () => console.log('WebSocket connection closed')
            });
        }
    }

    sendMessage(message: any): void {
        if (this.socket$) {
            this.socket$.next(message);
        }
    }

    close(): void {
        if (this.socket$) {
            this.socket$.complete();
            this.socket$ = null;
        }
    }

    getOrderUpdates(orderId: number): Observable<any> {
        return new Observable(observer => {
            const subscription = this.messages$.subscribe(message => {
                if (message.type === 'ORDER_UPDATE' && message.orderId === orderId) {
                    observer.next(message);
                }
            });

            return () => subscription.unsubscribe();
        });
    }
}
