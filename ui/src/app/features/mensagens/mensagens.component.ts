import { Component, inject, signal, OnInit, computed } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MensagensService } from '../../core/services/http/mensagem/mensagem.service';
import { AuthService } from '../../core/services/http/auth/auth.service';
import { ToastService } from '../../core/services/toast/toast.service';
import { DTOContatos, DTOReturnMessage, DTOInfoMessage, Midia } from '../../core/models/models';
import { MediaViewerComponent } from '../../shared/components/media-viewer/media-viewer.component';
import { MediaUploadComponent } from '../../shared/components/upload/upload.component';

@Component({
  selector: 'app-mensagens',
  standalone: true,
  imports: [FormsModule, CommonModule, MediaViewerComponent, MediaUploadComponent],
  templateUrl: './mensagens.component.html',
  styleUrl: './mensagens.component.css',
})
export class MensagensComponent implements OnInit {
  svc = inject(MensagensService);
  auth = inject(AuthService);
  toast = inject(ToastService);
  router = inject(Router);

  contatos = signal<DTOContatos[]>([]);
  mensagens = signal<DTOReturnMessage[]>([]);
  contatoAtivo = signal<DTOContatos | null>(null);
  loadingContatos = signal(true);
  loadingMsgs = signal(false);
  enviando = signal(false);

  // ── Chat ativo ────────────────────────────────────────────────────────────
  texto = '';
  midiaAnexada: Midia | null = null;
  showUpload = signal(false);

  // ── Modal nova conversa ───────────────────────────────────────────────────
  showNova = signal(false);
  novoDestinatarioId = '';
  novaMensagem = '';
  novaMidia: Midia | null = null;
  showNovaUpload = signal(false);

  meuNome = computed(() => this.auth.getUserData()?.nome ?? '');

  ngOnInit() {
  this.svc.listarContatos().subscribe({
    next: r => {
      this.contatos.set(r.content);
      this.loadingContatos.set(false);
 
      // Se vier de "Responder" em Solicitações, o Router state traz destinatarioId
      const destinatarioId = (history.state as { destinatarioId?: string })?.destinatarioId;
 
      if (destinatarioId) {
        const existente = r.content.find(c => c.id === destinatarioId);
        if (existente) {
          // Contato já existe na lista — abre o chat direto
          this.selecionarContato(existente);
        } else {
          // Nunca conversaram — abre modal nova conversa com RM já preenchido
          this.abrirNova();
          this.novoDestinatarioId = destinatarioId;
        }
      }
    },
      error: () => { this.toast.error('Erro ao carregar contatos.'); this.loadingContatos.set(false); },
    });
  }

  selecionarContato(c: DTOContatos) {
    this.contatoAtivo.set(c);
    this.loadingMsgs.set(true);
    this.midiaAnexada = null;
    this.showUpload.set(false);
    this.svc.lerMensagens(c.id).subscribe({
      next: r => { this.mensagens.set(r.content); this.loadingMsgs.set(false); },
      error: () => { this.toast.error('Erro ao carregar mensagens.'); this.loadingMsgs.set(false); },
    });
  }

  // ── Chat ativo ────────────────────────────────────────────────────────────

  toggleUpload() {
    this.showUpload.update(v => !v);
    if (!this.showUpload()) this.midiaAnexada = null;
  }

  onMidiaChange(midia: Midia | null) {
    this.midiaAnexada = midia;
    if (!midia) this.showUpload.set(false);
  }

  enviar() {
  const t = this.texto.trim();
  if ((!t && !this.midiaAnexada) || !this.contatoAtivo()) return;

  this.enviando.set(true);

    const payload: DTOInfoMessage = {
      content: t || null, 
      midia: this.midiaAnexada,
    };

    this.svc.enviarMensagem(this.contatoAtivo()!.id, payload).subscribe({
      next: () => {
        this.texto = '';
        this.midiaAnexada = null;
        this.showUpload.set(false);
        this.enviando.set(false);
        this.selecionarContato(this.contatoAtivo()!);
      },
      error: () => { 
        this.toast.error('Erro ao enviar mensagem.'); 
        this.enviando.set(false); 
      },
    });
  }

  // ── Nova conversa ─────────────────────────────────────────────────────────

  abrirNova() {
    this.novoDestinatarioId = '';
    this.novaMensagem = '';
    this.novaMidia = null;
    this.showNovaUpload.set(false);
    this.showNova.set(true);
  }

  fecharNova() {
    this.showNova.set(false);
  }

  toggleNovaUpload() {
    this.showNovaUpload.update(v => !v);
    if (!this.showNovaUpload()) this.novaMidia = null;
  }

  onNovaMidiaChange(midia: Midia | null) {
    this.novaMidia = midia;
    if (!midia) this.showNovaUpload.set(false);
  }

  iniciarConversa() {
  const id = this.novoDestinatarioId.trim();
  const msg = this.novaMensagem.trim();

  if (!id || (!msg && !this.novaMidia)) {
    this.toast.error('Informe o ID e ao menos uma mensagem ou mídia.');
    return;
  }

  this.enviando.set(true);

  // Mesmo ajuste aqui
  const payload: DTOInfoMessage = {
      content: msg || null,
      midia: this.novaMidia,
    };

    this.svc.enviarMensagem(id, payload).subscribe({
      next: () => {
        this.toast.success('Mensagem enviada!');
        this.fecharNova();
        this.enviando.set(false);

        const existente = this.contatos().find(c => c.id === id);
        if (existente) {
          this.selecionarContato(existente);
        } else {
          this.svc.listarContatos().subscribe({
            next: r => {
              this.contatos.set(r.content);
              const novo = r.content.find(c => c.id === id);
              if (novo) this.selecionarContato(novo);
            }
          });
        }
      },
      error: () => { 
        this.toast.error('Erro ao enviar. Verifique o ID.'); 
        this.enviando.set(false); 
      },
    });
  }
}