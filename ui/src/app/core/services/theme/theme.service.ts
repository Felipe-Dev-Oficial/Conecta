import { Injectable, signal, effect, PLATFORM_ID, inject } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';

export type Theme = 'light' | 'dark';

@Injectable({ providedIn: 'root' })
export class ThemeService {
  private platformId = inject(PLATFORM_ID);
  theme = signal<Theme>(this.initialTheme());

  constructor() {
    effect(() => {
      if (isPlatformBrowser(this.platformId)) {
        const t = this.theme();
        document.documentElement.setAttribute('data-theme', t);
        localStorage.setItem('theme', t);
      }
    });
  }

  toggle() {
    this.theme.update(t => (t === 'dark' ? 'light' : 'dark'));
  }

  private initialTheme(): Theme {
    if (!isPlatformBrowser(this.platformId)) return 'dark';
    const saved = localStorage.getItem('theme') as Theme | null;
    if (saved) return saved;
    return window.matchMedia('(prefers-color-scheme: light)').matches ? 'light' : 'dark';
  }
}