import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { SecretariaService } from '../../../core/services/http/secretaria/secretaria.service';
import { ToastService } from '../../../core/services/toast/toast.service';
import { DTOReturnMessageSecretaria } from '../../../core/models/models';
import { MediaViewerComponent } from '../../../shared/components/media-viewer/media-viewer.component';

@Component({
  selector: 'app-mensagens-mgmt',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterLink, MediaViewerComponent],
  templateUrl: './mensagens-mgmt.component.html',
  styleUrl: './mensagens-mgmt.component.css',
})
export class MensagensMgmtComponent {
  svc = inject(SecretariaService);
  toast = inject(ToastService);

  mensagens = signal<DTOReturnMessageSecretaria[]>([]);
  loading = signal(false);
  page = signal(0);
  totalPages = signal(1);

  sender = '';
  receiver = '';

  buscar() {
    if (!this.sender.trim() || !this.receiver.trim()) {
      this.toast.error('Informe o ID do remetente e do destinatário.');
      return;
    }
    this.page.set(0);
    this.fetch();
  }

  private fetch() {
    this.loading.set(true);
    this.svc.listarMensagens(this.sender.trim(), this.receiver.trim(), this.page()).subscribe({
      next: r => { this.mensagens.set(r.content); this.totalPages.set(r.totalPages); this.loading.set(false); },
      error: () => { this.toast.error('Erro ao buscar mensagens.'); this.loading.set(false); },
    });
  }

  goTo(p: number) { this.page.set(p); this.fetch(); }
  pageRange() { return Array.from({ length: this.totalPages() }, (_, i) => i); }
  formatDate(iso: string) {
    return new Date(iso).toLocaleDateString('pt-BR', { day: '2-digit', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit' });
  }
}