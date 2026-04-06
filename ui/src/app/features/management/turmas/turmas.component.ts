import { Component, inject, signal, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { SecretariaService } from '../../../core/services/http/secretaria/secretaria.service';
import { ToastService } from '../../../core/services/toast/toast.service';
import { Turma, DTOCadastroTurma, Cursos } from '../../../core/models/models';

const CURSOS: Cursos[] = [
  'ADMINISTRACAO', 'CONTABILIDADE', 'DESENVOLVIMENTO_DE_SISTEMAS', 'LOGISTICA',
  'SERVICOS_JURIDICOS', 'MATUTINO_ADMINISTRACAO_MTEC', 'MATUTINO_CONTABILIDADE_MTEC',
  'MATUTINO_DESENVOLVIMENTO_DE_SISTEMAS_MTEC', 'MATUTINO_LOGISTICA_MTEC',
  'MATUTINO_RECURSOS_HUMANOS_MTEC', 'VESPERTINO_ADMINISTRACAO_MTEC',
  'VESPERTINO_CONTABILIDADE_MTEC', 'VESPERTINO_DESENVOLVIMENTO_DE_SISTEMAS_MTEC',
  'VESPERTINO_LOGISTICA_MTEC', 'VESPERTINO_RECURSOS_HUMANOS_MTEC',
  'LOGISTICA_MTEC_N', 'DESENVOLVIMENTO_DE_SISTEMAS_AMS',
];

@Component({
  selector: 'app-turmas',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterLink],
  templateUrl: './turmas.component.html',
  styleUrl: './turmas.component.css',
})
export class TurmasComponent implements OnInit {
  svc = inject(SecretariaService);
  toast = inject(ToastService);

  turmas = signal<Turma[]>([]);
  loading = signal(true);
  saving = signal(false);
  showNova = signal(false);
  page = signal(0);
  totalPages = signal(1);

  apenasAtuais = true;
  filtroCurso = '';
  formCurso: Cursos = 'DESENVOLVIMENTO_DE_SISTEMAS';
  formModulos = 6;
  cursos = CURSOS;

  ngOnInit() { this.listar(); }

  listar() {
    this.loading.set(true);
    const obs = this.filtroCurso
      ? (this.apenasAtuais
          ? this.svc.listarTurmasPorCursoAtuais(this.filtroCurso as Cursos, this.page())
          : this.svc.listarTurmasPorCurso(this.filtroCurso as Cursos, this.page()))
      : (this.apenasAtuais
          ? this.svc.listarTurmasAtuais(this.page())
          : this.svc.listarTurmas(this.page()));

    obs.subscribe({
      next: r => { this.turmas.set(r.content); this.totalPages.set(r.totalPages); this.loading.set(false); },
      error: () => { this.toast.error('Erro ao listar turmas.'); this.loading.set(false); },
    });
  }

  criar() {
    this.saving.set(true);
    const dto: DTOCadastroTurma = { curso: this.formCurso, modulos: this.formModulos };
    this.svc.criarTurmas([dto]).subscribe({
      next: () => {
        this.toast.success('Turma criada!');
        this.showNova.set(false);
        this.saving.set(false);
        this.listar();
      },
      error: () => { this.toast.error('Erro ao criar turma.'); this.saving.set(false); },
    });
  }

  passarModulo() {
    if (!confirm('Passar todos os módulos ativos?')) return;
    this.svc.passarModulo().subscribe({
      next: () => { this.toast.success('Módulos avançados!'); this.listar(); },
      error: () => this.toast.error('Erro ao passar módulo.'),
    });
  }

  goTo(p: number) { this.page.set(p); this.listar(); }
  pageRange() { return Array.from({ length: this.totalPages() }, (_, i) => i); }
}