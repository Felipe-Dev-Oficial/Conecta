import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AlteradorService } from '../../../core/services/http/alterator/alterator.service';
import { ThemeService } from '../../../core/services/theme/theme.service';

type Step = 'escolha' | 'formulario';
type Tipo = 'senha' | 'email';

@Component({
  selector: 'app-alterador',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './alterador.component.html',
  styleUrl: './alterador.component.css',
})
export class AlteradorComponent {
  alterador = inject(AlteradorService);
  theme = inject(ThemeService);

  step = signal<Step>('escolha');
  tipo = signal<Tipo>('senha');
  rm = ''; token = ''; valor = '';
  loading = signal(false);
  error = signal('');
  success = signal('');

  escolher(t: Tipo) { this.tipo.set(t); this.step.set('formulario'); }

  submit() {
    if (!this.rm || !this.token || !this.valor) {
      this.error.set('Preencha todos os campos.');
      return;
    }
    this.loading.set(true);
    this.error.set('');
    this.success.set('');

    const obs =
      this.tipo() === 'senha'
        ? this.alterador.alterarSenha(this.rm, this.token, this.valor)
        : this.alterador.alterarEmail(this.rm, this.token, this.valor);

    obs.subscribe({
      next: () => {
        this.loading.set(false);
        this.success.set(this.tipo() === 'senha' ? 'Senha alterada com sucesso!' : 'E-mail alterado com sucesso!');
        this.rm = ''; this.token = ''; this.valor = '';
      },
      error: e => {
        this.loading.set(false);
        this.error.set(e.status === 400 ? 'Token inválido ou expirado.' : 'Erro ao processar. Tente novamente.');
      },
    });
  }
}