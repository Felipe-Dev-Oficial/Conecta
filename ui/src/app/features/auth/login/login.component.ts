import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/http/auth/auth.service';
import { ThemeService } from '../../../core/services/theme/theme.service';
import { NotificationService } from '../../../core/services/http/notification/notification.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent {
  auth = inject(AuthService);
  router = inject(Router);
  theme = inject(ThemeService);
  notification = inject(NotificationService); // ✅ adiciona

  rm = '';
  senha = '';
  loading = signal(false);
  error = signal('');
  showPwd = signal(false);

  submit() {
    if (!this.rm || !this.senha) {
      this.error.set('Preencha todos os campos.');
      return;
    }
    this.loading.set(true);
    this.error.set('');

    this.auth.login({ id: this.rm, senha: { password: this.senha } }).subscribe({
      next: token => {
        localStorage.setItem('token', token);
        this.router.navigate(['/dashboard']);
        this.notification.requestPermission();
      },
      error: err => {
        this.loading.set(false);
        this.error.set(
          err.status === 401 ? 'RM ou senha inválidos.' : 'Erro ao conectar. Tente novamente.',
        );
      },
    });
  }
}