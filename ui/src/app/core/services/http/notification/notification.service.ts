import { Injectable, inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private platformId = inject(PLATFORM_ID);
  private http = inject(HttpClient);

  async requestPermission(): Promise<void> {
    if (!isPlatformBrowser(this.platformId)) return;

    const permission = await Notification.requestPermission();
    if (permission !== 'granted') return;

    try {
      const registration = await navigator.serviceWorker.ready;
      const vapidPublicKey = await this.getVapidPublicKey();

      const subscription = await registration.pushManager.subscribe({
        userVisibleOnly: true,
        applicationServerKey: this.urlBase64ToUint8Array(vapidPublicKey),
      });

      const { endpoint, keys } = subscription.toJSON() as any;
      await this.http.post<void>(`${environment.apiUrl}/notification`, {
        endpoint,
        p256dh: keys.p256dh,
        auth: keys.auth,
      }).toPromise();

    } catch (err) {
      console.error('Erro ao registrar Web Push:', err);
    }
  }

  listenForMessages(): void {
  }

  private async getVapidPublicKey(): Promise<string> {
    return this.http
      .get(`${environment.apiUrl}/notification/vapid-public-key`, { responseType: 'text' })
      .toPromise() as Promise<string>;
  }

  private urlBase64ToUint8Array(base64String: string): Uint8Array<ArrayBuffer> {
    const padding = '='.repeat((4 - (base64String.length % 4)) % 4);
    const base64 = (base64String + padding).replace(/-/g, '+').replace(/_/g, '/');
    const rawData = atob(base64);
    const outputArray = new Uint8Array(rawData.length);
    for (let i = 0; i < rawData.length; ++i) {
      outputArray[i] = rawData.charCodeAt(i);
    }
    return outputArray;
  }
}