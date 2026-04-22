import { Component, signal, OnInit, inject, PLATFORM_ID } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { isPlatformBrowser } from '@angular/common';
import { NotificationService } from '../app/core/services/http/notification/notification.service'; // ajusta o caminho


@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  protected readonly title = signal('ui');

  private platformId = inject(PLATFORM_ID);
  private notificationService = inject(NotificationService);

  ngOnInit() {
    if (isPlatformBrowser(this.platformId)) {
      this.notificationService.listenForMessages();
    }
  }
}