import { Component, inject, signal, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterLink, Router } from '@angular/router';
import { SolicitationService } from '../../../core/services/http/solicitation/solicitation.service';
import { ToastService } from '../../../core/services/toast/toast.service';
import { Solicitation, TypeRequirement } from '../../../core/models/models';

@Component({
  selector: 'app-solicitation-mgmt',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterLink],
  templateUrl: './solicitation-mgmt.component.html',
  styleUrl: './solicitation-mgmt.component.css',
})
export class SolicitationMgmtComponent implements OnInit {
  svc = inject(SolicitationService);
  toast = inject(ToastService);
  router = inject(Router);

  solicitations = signal<Solicitation[]>([]);
  loading = signal(true);
  resolvendo = signal<string | null>(null);

  page = signal(0);
  totalPages = signal(1);
  search = '';
  searchAtivo = '';

  readonly tiposDisponiveis: { value: TypeRequirement; label: string }[] = [
    { value: 'DECLARAÇÃO_DE_MATRICULA', label: 'Declaração de Matrícula' },
    { value: 'CERTIFICADO_ATUAL',       label: 'Certificado Atual' },
    { value: 'OUTRO',                   label: 'Outro' },
  ];

  ngOnInit() {
    this.fetch();
  }

  private fetch() {
    this.loading.set(true);
    this.svc.getSolicitationsSecretaria(this.searchAtivo, this.page()).subscribe({
      next: r => {
        this.solicitations.set(r.content);
        this.totalPages.set(r.totalPages);
        this.loading.set(false);
      },
      error: () => { this.toast.error('Erro ao carregar requerimentos.'); this.loading.set(false); },
    });
  }

  buscar() {
    this.searchAtivo = this.search.trim();
    this.page.set(0);
    this.fetch();
  }

  resolver(id: string) {
    this.resolvendo.set(id);
    this.svc.solveSolicitation(id).subscribe({
      next: () => {
        this.toast.success('Requerimento marcado como resolvido!');
        this.resolvendo.set(null);
        this.fetch();
      },
      error: () => { this.toast.error('Erro ao resolver requerimento.'); this.resolvendo.set(null); },
    });
  }

  // Navega para /mensagens passando o ID do solicitante via Router state.
  // O MensagensComponent lê o state em ngOnInit e pré-seleciona o contato.
  responder(idSoliciter: string) {
    this.router.navigate(['/mensagens'], { state: { destinatarioId: idSoliciter } });
  }

  goTo(p: number) { this.page.set(p); this.fetch(); }
  pageRange() { return Array.from({ length: this.totalPages() }, (_, i) => i); }

  labelTipo(tipo: TypeRequirement): string {
    return this.tiposDisponiveis.find(t => t.value === tipo)?.label ?? tipo;
  }

  formatDate(iso: string) {
    return new Date(iso).toLocaleDateString('pt-BR', {
      day: '2-digit', month: 'short', year: 'numeric',
      hour: '2-digit', minute: '2-digit',
    });
  }
}