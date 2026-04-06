import { Component, inject, signal, OnInit, computed } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MensagensService } from '../../core/services/http/mensagem/mensagem.service';
import { AuthService } from '../../core/services/http/auth/auth.service';
import { ToastService } from '../../core/services/toast/toast.service';
import { DTOContatos, DTOReturnMessage } from '../../core/models/models';
import { MediaViewerComponent } from '../../shared/components/media-viewer/media-viewer.component';

@Component({
  selector: 'app-mensagens',
  standalone: true,
  imports: [FormsModule, CommonModule, MediaViewerComponent],
  templateUrl: './mensagens.component.html',
  styleUrl: './mensagens.component.css',
})
export class MensagensComponent implements OnInit {
  svc = inject(MensagensService);
  auth = inject(AuthService);
  toast = inject(ToastService);

  contatos = signal<DTOContatos[]>([]);
  mensagens = signal<DTOReturnMessage[]>([]);
  contatoAtivo = signal<DTOContatos | null>(null);
  loadingContatos = signal(true);
  loadingMsgs = signal(false);
  enviando = signal(false);
  texto = '';
  meuNome = computed(() => this.auth.getUserData()?.nome ?? '');

  ngOnInit() {
    this.svc.listarContatos().subscribe({
      next: r => { this.contatos.set(r.content); this.loadingContatos.set(false); },
      error: () => { this.toast.error('Erro ao carregar contatos.'); this.loadingContatos.set(false); },
    });
  }

  selecionarContato(c: DTOContatos) {
    this.contatoAtivo.set(c);
    this.loadingMsgs.set(true);
    this.svc.lerMensagens(c.id).subscribe({
      next: r => { this.mensagens.set(r.content); this.loadingMsgs.set(false); },
      error: () => { this.toast.error('Erro ao carregar mensagens.'); this.loadingMsgs.set(false); },
    });
  }

  enviar() {
    const t = this.texto.trim();
    if (!t || !this.contatoAtivo()) return;
    this.enviando.set(true);
    this.svc.enviarMensagem(this.contatoAtivo()!.id, { content: { content: t }, midia: null }).subscribe({
      next: () => {
        this.texto = '';
        this.enviando.set(false);
        this.selecionarContato(this.contatoAtivo()!);
      },
      error: () => { this.toast.error('Erro ao enviar mensagem.'); this.enviando.set(false); },
    });
  }
}