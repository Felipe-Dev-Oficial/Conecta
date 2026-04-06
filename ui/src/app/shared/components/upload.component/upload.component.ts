import { Component, Output, EventEmitter, signal, inject, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Midia, TipoMidia } from '../../../core/models/models';
import { ToastService } from '../../../core/services/toast/toast.service';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-media-upload',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './upload.component.html',
  styleUrl: './upload.component.css',
})
export class MediaUploadComponent {
  @Input() midiaAtual: Midia | null = null;

  @Output() midiaChange = new EventEmitter<Midia | null>();

  private http = inject(HttpClient);
  private toast = inject(ToastService);

  uploading = signal(false);
  preview = signal<string | null>(null);
  tipoAtual = signal<TipoMidia | null>(null);

  private readonly ACCEPT = 'image/*,video/*,audio/*';
  get accept() { return this.ACCEPT; }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;

    const tipo = this.detectarTipo(file);
    if (!tipo) {
      this.toast.error('Formato não suportado. Use imagem, vídeo ou áudio.');
      return;
    }

    // Preview local imediato
    const reader = new FileReader();
    reader.onload = e => this.preview.set(e.target?.result as string);
    reader.readAsDataURL(file);
    this.tipoAtual.set(tipo);

    // Upload para a API
    const form = new FormData();
    form.append('file', file);

    this.uploading.set(true);
    this.http.post<{ link: string }>(`${environment.apiUrl}/media/upload`, form).subscribe({
      next: ({ link }) => {
        this.uploading.set(false);
        this.midiaChange.emit({ tipoMidia: tipo, link });
      },
      error: () => {
        this.uploading.set(false);
        this.preview.set(null);
        this.tipoAtual.set(null);
        this.toast.error('Erro ao fazer upload da mídia.');
      },
    });

    // Limpa o input para permitir re-seleção do mesmo arquivo
    input.value = '';
  }

  remover() {
    this.preview.set(null);
    this.tipoAtual.set(null);
    this.midiaChange.emit(null);
  }

  private detectarTipo(file: File): TipoMidia | null {
    if (file.type.startsWith('image/')) return 'FOTO';
    if (file.type.startsWith('video/')) return 'VIDEO';
    if (file.type.startsWith('audio/')) return 'AUDIO';
    return null;
  }
}