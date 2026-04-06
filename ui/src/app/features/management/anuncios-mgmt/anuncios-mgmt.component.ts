import { Component, inject, signal, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AnuncioService } from '../../../core/services/http/anuncio/anuncio.service';
import { ToastService } from '../../../core/services/toast/toast.service';
import { Statement, DTOAnuncio, DTOAlteraAnuncio, Prioridade, TargetType } from '../../../core/models/models';
import { MediaViewerComponent } from '../../../shared/components/media-viewer/media-viewer.component';

@Component({
  selector: 'app-anuncios-mgmt',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterLink, MediaViewerComponent],
  templateUrl: './anuncios-mgmt.component.html',
  styleUrl: './anuncios-mgmt.component.css',
})
export class AnunciosMgmtComponent implements OnInit {
  svc = inject(AnuncioService);
  toast = inject(ToastService);

  anuncios = signal<Statement[]>([]);
  loading = signal(true);
  saving = signal(false);
  showNovo = signal(false);
  showEditar = signal(false);
  editId = '';
  page = signal(0);
  totalPages = signal(1);

  fTitulo = '';
  fConteudo = '';
  fPrioridade: Prioridade = 'MEDIA';
  fTarget: TargetType = 'GERAL';

  ngOnInit() { this.load(); }

  load() {
    this.loading.set(true);
    this.svc.listarAnunciosSecretaria(this.page()).subscribe({
      next: r => { this.anuncios.set(r.content); this.totalPages.set(r.totalPages); this.loading.set(false); },
      error: () => { this.toast.error('Erro ao carregar.'); this.loading.set(false); },
    });
  }

  criar() {
    this.saving.set(true);
    const dto: DTOAnuncio = {
      title: { content: this.fTitulo },
      content: { content: this.fConteudo },
      midia: null,
      priority: this.fPrioridade,
      targetType: this.fTarget,
      targetsId: [],
    };
    this.svc.gerarAnuncio(dto).subscribe({
      next: () => { this.toast.success('Anúncio publicado!'); this.showNovo.set(false); this.saving.set(false); this.load(); },
      error: () => { this.toast.error('Erro.'); this.saving.set(false); },
    });
  }

  abrirEditar(a: Statement) {
    this.editId = a.id;
    this.fTitulo = a.title.content;
    this.fConteudo = a.content.content;
    this.fPrioridade = a.priority;
    this.showEditar.set(true);
  }

  editar() {
    this.saving.set(true);
    const dto: DTOAlteraAnuncio = {
      title: { content: this.fTitulo },
      content: { content: this.fConteudo },
      midia: null,
      priority: this.fPrioridade,
    };
    this.svc.alterarAnuncio(this.editId, dto).subscribe({
      next: () => { this.toast.success('Anúncio editado!'); this.showEditar.set(false); this.saving.set(false); this.load(); },
      error: () => { this.toast.error('Erro.'); this.saving.set(false); },
    });
  }

  elevar(id: string) {
    this.svc.elevarPrioridade(id).subscribe({ next: () => this.load(), error: () => this.toast.error('Erro.') });
  }

  reduzir(id: string) {
    this.svc.reduzirPrioridade(id).subscribe({ next: () => this.load(), error: () => this.toast.error('Erro.') });
  }

  apagar(id: string) {
    if (!confirm('Apagar este anúncio?')) return;
    this.svc.apagarAnuncio(id).subscribe({
      next: () => { this.toast.success('Apagado.'); this.load(); },
      error: () => this.toast.error('Erro.'),
    });
  }

  prioridadeBadge(p: Prioridade) {
    const m: Record<string, string> = { URGENTE: 'badge badge-red', ALTA: 'badge badge-amber', MEDIA: 'badge badge-blue', BAIXA: 'badge badge-gray' };
    return m[p] ?? 'badge badge-gray';
  }

  formatDate(iso: string) {
    return new Date(iso).toLocaleDateString('pt-BR', { day: '2-digit', month: 'short', year: 'numeric' });
  }

  goTo(p: number) { this.page.set(p); this.load(); }
  pageRange() { return Array.from({ length: this.totalPages() }, (_, i) => i); }
}