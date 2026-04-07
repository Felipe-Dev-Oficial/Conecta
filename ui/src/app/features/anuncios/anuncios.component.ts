import { Component, inject, signal, OnInit, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { CommonModule } from '@angular/common';
import { AnuncioService } from '../../core/services/http/anuncio/anuncio.service';
import { ToastService } from '../../core/services/toast/toast.service';
import { DTORetornoAnuncio } from '../../core/models/models';
import { MediaViewerComponent } from '../../shared/components/media-viewer/media-viewer.component';

@Component({
  selector: 'app-anuncios',
  standalone: true,
  imports: [CommonModule, MediaViewerComponent],
  templateUrl: './anuncios.component.html',
  styleUrl: './anuncios.component.css',
})
export class AnunciosComponent implements OnInit {
  svc = inject(AnuncioService);
  toast = inject(ToastService);
  platformId = inject(PLATFORM_ID);

  anuncios = signal<DTORetornoAnuncio[]>([]);
  loading = signal(true);
  page = signal(0);
  totalPages = signal(1);

  get isLogado(): boolean {
    return isPlatformBrowser(this.platformId) && !!localStorage.getItem('token');
  }

  ngOnInit() { this.load(); }

  load() {
    this.loading.set(true);
    const obs = this.isLogado
      ? this.svc.listarAnuncios(this.page())
      : this.svc.listarAnunciosDefault(this.page());

    obs.subscribe({
      next: r => { this.anuncios.set(r.content); this.totalPages.set(r.totalPages); this.loading.set(false); },
      error: () => { this.toast.error('Erro ao carregar anúncios.'); this.loading.set(false); },
    });
  }

  goTo(p: number) { this.page.set(p); this.load(); }
  pageRange() { return Array.from({ length: this.totalPages() }, (_, i) => i); }
  initial(name: string) { return name?.[0]?.toUpperCase() ?? '?'; }
  formatDate(iso: string) {
    return new Date(iso).toLocaleDateString('pt-BR', { day: '2-digit', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit' });
  }
}