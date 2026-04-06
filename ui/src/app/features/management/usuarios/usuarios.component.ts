import { Component, inject, signal, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { SecretariaService } from '../../../core/services/http/secretaria/secretaria.service';
import { ToastService } from '../../../core/services/toast/toast.service';
import { DTORetornoSecretaria, DTOCadastro, Tipo } from '../../../core/models/models';

interface FormUsuario {
  id: string;
  nome: string;
  email: string;
  numero: string;
  senha: string;
  tipo: Tipo;
  turmas: string[];
}

function formVazio(): FormUsuario {
  return { id: '', nome: '', email: '', numero: '', senha: '', tipo: 'ALUNO', turmas: [''] };
}

@Component({
  selector: 'app-usuarios',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterLink],
  templateUrl: './usuarios.component.html',
  styleUrl: './usuarios.component.css',
})
export class UsuariosComponent implements OnInit {
  svc = inject(SecretariaService);
  toast = inject(ToastService);

  usuarios = signal<DTORetornoSecretaria[]>([]);
  loading = signal(true);
  saving = signal(false);
  showCadastro = signal(false);
  showTipo = signal(false);
  showLote = signal(false);
  usuarioSel = signal<DTORetornoSecretaria | null>(null);
  page = signal(0);
  totalPages = signal(1);

  filtroTipo = 'alunos';
  busca = '';
  turmaId = '';
  novoTipo: Tipo = 'ALUNO';

  form: FormUsuario = formVazio();
  lote: FormUsuario[] = [formVazio()];

  ngOnInit() { this.listar(); }

  // ── Listagem ─────────────────────────────────────────────────────────────────

  listar() {
    this.loading.set(true);
    const obs = this.filtroTipo === 'funcionarios'
      ? this.svc.listarFuncionarios(this.page())
      : this.svc.listarAlunos(this.page());

    obs.subscribe({
      next: r => { this.usuarios.set(r.content); this.totalPages.set(r.totalPages); this.loading.set(false); },
      error: () => { this.toast.error('Erro ao listar usuários.'); this.loading.set(false); },
    });
  }

  buscar() {
    this.loading.set(true);
    const obs = this.turmaId
      ? this.svc.listarAlunosPorTurma(this.turmaId, this.page())
      : this.busca
        ? this.svc.listarAlunosPorNome(this.busca, this.page())
        : null;

    if (!obs) { this.listar(); return; }
    obs.subscribe({
      next: r => { this.usuarios.set(r.content); this.totalPages.set(r.totalPages); this.loading.set(false); },
      error: () => { this.toast.error('Erro na busca.'); this.loading.set(false); },
    });
  }

  // ── Cadastro único ───────────────────────────────────────────────────────────

  abrirCadastro() {
    this.form = formVazio();
    this.showCadastro.set(true);
  }

  onTipoChange() {
    if (this.form.tipo === 'SECRETARIA') {
      this.form.turmas = [];
    } else if (this.form.turmas.length === 0) {
      this.form.turmas = [''];
    }
  }

  addTurma() { this.form.turmas.push(''); }

  removeTurma(i: number) {
    if (this.form.turmas.length > 1) this.form.turmas.splice(i, 1);
  }

  salvar() {
    this.saving.set(true);
    const dto: DTOCadastro = {
      id: this.form.id,
      nome: { name: this.form.nome },
      email: { email: this.form.email },
      numero: { number: this.form.numero },
      senha: { password: this.form.senha },
      tipo: this.form.tipo,
      turmas: this.form.tipo === 'SECRETARIA' ? [] : this.form.turmas.filter(t => t.trim()),
    };
    this.svc.salvarUsuario(dto).subscribe({
      next: () => {
        this.toast.success('Usuário criado!');
        this.showCadastro.set(false);
        this.saving.set(false);
        this.listar();
      },
      error: () => { this.toast.error('Erro ao criar usuário.'); this.saving.set(false); },
    });
  }

  // ── Cadastro em lote ─────────────────────────────────────────────────────────

  abrirLote() {
    this.lote = [formVazio()];
    this.showLote.set(true);
  }

  addLoteItem() { this.lote.push(formVazio()); }

  removeLoteItem(i: number) {
    if (this.lote.length > 1) this.lote.splice(i, 1);
  }

  onLoteTipoChange(i: number) {
    const u = this.lote[i];
    if (u.tipo === 'SECRETARIA') {
      u.turmas = [];
    } else if (u.turmas.length === 0) {
      u.turmas = [''];
    }
  }

  addLoteTurma(i: number) { this.lote[i].turmas.push(''); }

  removeLoteTurma(i: number, j: number) {
    if (this.lote[i].turmas.length > 1) this.lote[i].turmas.splice(j, 1);
  }

  salvarLote() {
    this.saving.set(true);
    const dtos: DTOCadastro[] = this.lote.map(u => ({
      id: u.id,
      nome: { name: u.nome },
      email: { email: u.email },
      numero: { number: u.numero },
      senha: { password: u.senha },
      tipo: u.tipo,
      turmas: u.tipo === 'SECRETARIA' ? [] : u.turmas.filter(t => t.trim()),
    }));

    this.svc.salvarUsuariosLote(dtos).subscribe({
      next: () => {
        this.toast.success(`${dtos.length} usuário(s) cadastrado(s)!`);
        this.showLote.set(false);
        this.saving.set(false);
        this.listar();
      },
      error: () => { this.toast.error('Erro ao cadastrar lote.'); this.saving.set(false); },
    });
  }

  // ── Ações ────────────────────────────────────────────────────────────────────

  deletar(id: string) {
    if (!confirm('Deletar este usuário?')) return;
    this.svc.deletarUsuario(id).subscribe({
      next: () => { this.toast.success('Usuário deletado.'); this.listar(); },
      error: () => this.toast.error('Erro ao deletar.'),
    });
  }

  solicitarSenha(id: string) {
    this.svc.solicitarSenha(id).subscribe({
      next: () => this.toast.success('Solicitação de senha enviada!'),
      error: () => this.toast.error('Erro ao solicitar.'),
    });
  }

  solicitarEmail(id: string) {
    this.svc.solicitarEmail(id).subscribe({
      next: () => this.toast.success('Solicitação de e-mail enviada!'),
      error: () => this.toast.error('Erro ao solicitar.'),
    });
  }

  abrirAlterarTipo(u: DTORetornoSecretaria) {
    this.usuarioSel.set(u);
    this.novoTipo = u.tipo;
    this.showTipo.set(true);
  }

  alterarTipo() {
    const u = this.usuarioSel();
    if (!u) return;
    this.saving.set(true);
    this.svc.alterarTipo(u.id, this.novoTipo).subscribe({
      next: () => {
        this.toast.success('Tipo alterado!');
        this.showTipo.set(false);
        this.saving.set(false);
        this.listar();
      },
      error: () => { this.toast.error('Erro ao alterar tipo.'); this.saving.set(false); },
    });
  }

  // ── Paginação / utils ────────────────────────────────────────────────────────

  goTo(p: number) { this.page.set(p); this.listar(); }
  pageRange() { return Array.from({ length: this.totalPages() }, (_, i) => i); }

  badgeClass(tipo: Tipo) {
    const m: Record<string, string> = {
      ALUNO: 'badge badge-blue',
      PROFESSOR: 'badge badge-amber',
      SECRETARIA: 'badge badge-green',
      DESATIVADO: 'badge badge-gray',
    };
    return m[tipo] ?? 'badge badge-gray';
  }
}