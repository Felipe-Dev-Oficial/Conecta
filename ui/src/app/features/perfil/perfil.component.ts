import { Component, inject, signal, OnInit, computed } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/services/http/auth/auth.service';
import { ProfessorService } from '../../core/services/http/professor/professor.service';
import { AlunoService } from '../../core/services/http/aluno/aluno.service';
import { ToastService } from '../../core/services/toast/toast.service';
import { DTORetornoNormal } from '../../core/models/models';
import { SecretariaService } from '../../core/services/http/secretaria/secretaria.service';

@Component({
  selector: 'app-perfil',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './perfil.component.html',
  styleUrl: './perfil.component.css',
})
export class PerfilComponent implements OnInit {
  auth = inject(AuthService);
  profSvc = inject(ProfessorService);
  alunoSvc = inject(AlunoService);
  toast = inject(ToastService);

  lista = signal<DTORetornoNormal[]>([]);
  loading = signal(false);
  busca = ''; turmaId = ''; profId = '';

  secSvc = inject(SecretariaService);
  secretaria = signal<DTORetornoNormal[]>([]);
  loadingSecretaria = signal(false);

  private userData = computed(() => this.auth.getUserData());
  role = computed(() => this.userData()?.role ?? '');
  rolePretty = computed(() => {
    const m: Record<string, string> = { PROFESSOR: 'Professor', ALUNO: 'Aluno' };
    return m[this.role()] ?? '';
  });

  ngOnInit() {
    if (this.role() === 'PROFESSOR') this.listarTodos();
    this.carregarSecretaria();
  }

  listarTodos() {
    this.loading.set(true);
    this.profSvc.listarAlunos().subscribe({
      next: r => { this.lista.set(r.content); this.loading.set(false); },
      error: () => { this.toast.error('Erro ao listar alunos.'); this.loading.set(false); },
    });
  }

  buscarAlunos() {
    this.loading.set(true);
    const obs = this.turmaId
      ? this.profSvc.listarAlunosPorTurma(this.turmaId)
      : this.busca
        ? this.profSvc.buscarAlunosPorNome(this.busca)
        : this.profSvc.listarAlunos();

    obs.subscribe({
      next: r => {
        if ('content' in r) this.lista.set(r.content);
        else this.lista.set([r as unknown as DTORetornoNormal]);
        this.loading.set(false);
      },
      error: () => { this.toast.error('Erro na busca.'); this.loading.set(false); },
    });
  }

  verDetalhe(id: string) {
    this.loading.set(true);
    this.profSvc.buscarAlunoPorId(id).subscribe({
      next: r => { this.lista.set([r]); this.loading.set(false); },
      error: () => { this.toast.error('Aluno não encontrado.'); this.loading.set(false); },
    });
  }

  listarProfessores() {
    this.loading.set(true);
    this.alunoSvc.buscarProfessores().subscribe({
      next: r => { this.lista.set(r.content); this.loading.set(false); },
      error: () => { this.toast.error('Erro ao listar professores.'); this.loading.set(false); },
    });
  }

  buscarProfessor() {
    if (!this.profId) return;
    this.loading.set(true);
    this.alunoSvc.buscarProfessorPorId(this.profId).subscribe({
      next: r => { this.lista.set([r]); this.loading.set(false); },
      error: () => { this.toast.error('Professor não encontrado.'); this.loading.set(false); },
    });
  }
  carregarSecretaria() {
    this.loadingSecretaria.set(true);
    this.secSvc.listarSecretaria().subscribe({
      next: r => { this.secretaria.set(r.content); this.loadingSecretaria.set(false); },
      error: () => { this.toast.error('Erro ao listar secretaria.'); this.loadingSecretaria.set(false); },
    });
  }
}