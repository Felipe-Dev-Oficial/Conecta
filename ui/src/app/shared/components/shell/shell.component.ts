import { Component, inject, computed, signal, effect } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../core/services/http/auth/auth.service';
import { ThemeService } from '../../../core/services/theme/theme.service';
import { ToastService } from '../../../core/services/toast/toast.service';
import { isPlatformBrowser } from '@angular/common';
import { PLATFORM_ID } from '@angular/core';

@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, CommonModule],
  templateUrl: './shell.component.html',
  styleUrl: './shell.component.css',
})
export class ShellComponent {
  auth = inject(AuthService);
  themeService = inject(ThemeService);
  toastService = inject(ToastService);
  router = inject(Router);
  mobileOpen = signal(false);
  platformId = inject(PLATFORM_ID);
  collapsed = signal(false);

  isLogado = computed(() => {
    return isPlatformBrowser(this.platformId) && !!localStorage.getItem('token');
  });

  private userData = computed(() => this.auth.getUserData());
  nome = computed(() => this.userData()?.nome ?? '');
  role = computed(() => this.userData()?.role ?? '');
  initials = computed(() => {
    const n = this.nome();
    return n ? n.split(' ').slice(0, 2).map(w => w[0]).join('').toUpperCase() : '?';
  });
  rolePretty = computed(() => {
    const map: Record<string, string> = {
      ALUNO: 'Aluno',
      PROFESSOR: 'Professor',
      SECRETARIA: 'Secretaria',
    };
    return map[this.role()] ?? this.role();
  });

  constructor() {
    // Propaga o estado collapsed para o body — qualquer componente filho
    // (mensagens, etc.) pode reagir via CSS: body.sidebar-collapsed ...
    effect(() => {
      if (isPlatformBrowser(this.platformId)) {
        document.body.classList.toggle('sidebar-collapsed', this.collapsed());
      }
    });
  }

  async logout() { await this.auth.logout(); }
}