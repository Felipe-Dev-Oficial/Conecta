import { Component, inject, signal, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { SolicitationService } from '../../core/services/http/solicitation/solicitation.service';
import { ToastService } from '../../core/services/toast/toast.service';
import { AuthService } from '../../core/services/http/auth/auth.service';
import { DTOReturnRequirement, TypeRequirement } from '../../core/models/models';

@Component({
  selector: 'app-solicitation',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './solicitation.component.html',
  styleUrl: './solicitation.component.css',
})
export class SolicitationComponent implements OnInit {
  svc = inject(SolicitationService);
  toast = inject(ToastService);
  auth = inject(AuthService);

  solicitations = signal<DTOReturnRequirement[]>([]);
  loading = signal(true);
  enviando = signal(false);
  showForm = signal(false);

  page = signal(0);
  totalPages = signal(1);

  // Form fields
  tipoSelecionado: TypeRequirement | '' = '';
  outroTexto = '';

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
    this.svc.getSolicitations(this.page()).subscribe({
      next: r => { this.solicitations.set(r.content); this.totalPages.set(r.totalPages); this.loading.set(false); },
      error: () => { this.toast.error('Erro ao carregar requerimentos.'); this.loading.set(false); },
    });
  }

  abrirForm() {
    this.tipoSelecionado = '';
    this.outroTexto = '';
    this.showForm.set(true);
  }

  fecharForm() {
    this.showForm.set(false);
  }

  enviar() {
    if (!this.tipoSelecionado) {
      this.toast.error('Selecione o tipo de requerimento.');
      return;
    }
    if (this.tipoSelecionado === 'OUTRO' && !this.outroTexto.trim()) {
      this.toast.error('Descreva o requerimento.');
      return;
    }
    this.enviando.set(true);
    this.svc.sendSolicitation({
      typeRequirement: this.tipoSelecionado as TypeRequirement,
      otherRequirement: this.tipoSelecionado === 'OUTRO' ? this.outroTexto.trim() : null,
    }).subscribe({
      next: () => {
        this.toast.success('Requerimento enviado com sucesso!');
        this.fecharForm();
        this.enviando.set(false);
        this.page.set(0);
        this.fetch();
      },
      error: () => { this.toast.error('Erro ao enviar requerimento.'); this.enviando.set(false); },
    });
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