import { Component, inject, signal, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FaqService } from '../../../core/services/http/faq/faq.service';
import { ToastService } from '../../../core/services/toast/toast.service';
import { FAQ, Prioridade } from '../../../core/models/models';

@Component({
  selector: 'app-faqs-mgmt',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterLink],
  templateUrl: './faqs-mgmt.component.html',
  styleUrl: './faqs-mgmt.component.css',
})
export class FaqsMgmtComponent implements OnInit {
  svc = inject(FaqService);
  toast = inject(ToastService);

  faqs = signal<FAQ[]>([]);
  loading = signal(true);
  saving = signal(false);
  showModal = signal(false);
  editId = '';
  page = signal(0);
  totalPages = signal(1);

  fPergunta = '';
  fResposta = '';
  fRelevancia: Prioridade = 'MEDIA';

  ngOnInit() { this.load(); }

  load() {
    this.loading.set(true);
    this.svc.listarFaqsSecretaria(this.page()).subscribe({
      next: r => { this.faqs.set(r.content); this.totalPages.set(r.totalPages); this.loading.set(false); },
      error: () => { this.toast.error('Erro ao carregar.'); this.loading.set(false); },
    });
  }

  abrirNova() {
    this.editId = '';
    this.fPergunta = '';
    this.fResposta = '';
    this.fRelevancia = 'MEDIA';
    this.showModal.set(true);
  }

  abrirEditar(f: FAQ) {
    this.editId = f.id;
    this.fPergunta = f.question;
    this.fResposta = f.answer;
    this.showModal.set(true);
  }

  salvar() {
    this.saving.set(true);
    const obs = this.editId
      ? this.svc.alterarFaq(this.editId, { pergunta: this.fPergunta, resposta: this.fResposta })
      : this.svc.escreverFaq({ question: this.fPergunta, answer: this.fResposta, relevance: this.fRelevancia });

    obs.subscribe({
      next: () => {
        this.toast.success(this.editId ? 'FAQ editada!' : 'FAQ criada!');
        this.showModal.set(false);
        this.saving.set(false);
        this.load();
      },
      error: () => { this.toast.error('Erro.'); this.saving.set(false); },
    });
  }

  publicar(id: string) {
    this.svc.publicarFaq(id).subscribe({
      next: () => { this.toast.success('FAQ publicada!'); this.load(); },
      error: () => this.toast.error('Erro.'),
    });
  }

  elevar(id: string) {
    this.svc.elevarRelevancia(id).subscribe({ next: () => this.load(), error: () => this.toast.error('Erro.') });
  }

  reduzir(id: string) {
    this.svc.reduzirRelevancia(id).subscribe({ next: () => this.load(), error: () => this.toast.error('Erro.') });
  }

  apagar(id: string) {
    if (!confirm('Apagar esta FAQ?')) return;
    this.svc.apagarFaq(id).subscribe({
      next: () => { this.toast.success('Apagada.'); this.load(); },
      error: () => this.toast.error('Erro.'),
    });
  }

  statusBadge(s: string) {
    const m: Record<string, string> = { RASCUNHO: 'badge badge-gray', PUBLICADO: 'badge badge-green', APAGADO: 'badge badge-red' };
    return m[s] ?? 'badge badge-gray';
  }

  relevBadge(r: Prioridade) {
    const m: Record<string, string> = { URGENTE: 'badge badge-red', ALTA: 'badge badge-amber', MEDIA: 'badge badge-blue', BAIXA: 'badge badge-gray' };
    return m[r] ?? 'badge badge-gray';
  }

  formatDate(iso: string) { return new Date(iso).toLocaleDateString('pt-BR'); }
  goTo(p: number) { this.page.set(p); this.load(); }
  pageRange() { return Array.from({ length: this.totalPages() }, (_, i) => i); }
}