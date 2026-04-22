import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  DTOCadastro,
  DTORetornoSecretaria,
  DTOReturnMessageSecretaria,
  DTORetornoNormal,
  Turma,
  Tipo,
  Cursos,
  PageResult,
} from '../../../models/models';
import { environment } from '../../../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class SecretariaService {
  private base = `${environment.apiUrl}/management`;

  constructor(private http: HttpClient) {}

  // ── Alunos ──────────────────────────────────────────────────────────────────

  /** GET /conecta/management/alunos */
  listarAlunos(page = 0, size = 20): Observable<PageResult<DTORetornoSecretaria>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResult<DTORetornoSecretaria>>(`${this.base}/alunos`, { params });
  }

  /** GET /conecta/management/alunos/nome/{nome} */
  listarAlunosPorNome(nome: string, page = 0, size = 20): Observable<PageResult<DTORetornoSecretaria>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResult<DTORetornoSecretaria>>(`${this.base}/alunos/nome/${nome}`, { params });
  }

  /** GET /conecta/management/alunos/turma/{idTurma} */
  listarAlunosPorTurma(idTurma: string, page = 0, size = 20): Observable<PageResult<DTORetornoSecretaria>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResult<DTORetornoSecretaria>>(`${this.base}/alunos/turma/${idTurma}`, { params });
  }

  // ── Funcionários ─────────────────────────────────────────────────────────────

  /** GET /conecta/management/funcionarios */
  listarFuncionarios(page = 0, size = 20): Observable<PageResult<DTORetornoSecretaria>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResult<DTORetornoSecretaria>>(`${this.base}/funcionarios`, { params });
  }

  // ── Usuários ─────────────────────────────────────────────────────────────────

  /** GET /conecta/management/usuarios/{id} */
  buscarUsuario(id: string): Observable<DTORetornoSecretaria> {
    return this.http.get<DTORetornoSecretaria>(`${this.base}/usuarios/${id}`);
  }

  /** POST /conecta/management/usuarios */
  salvarUsuario(dto: DTOCadastro): Observable<void> {
    return this.http.post<void>(`${this.base}/usuarios`, dto);
  }

  /** POST /conecta/management/usuarios/lote */
  salvarUsuariosLote(dtos: DTOCadastro[]): Observable<void> {
    return this.http.post<void>(`${this.base}/usuarios/lote`, dtos);
  }

  /** DELETE /conecta/management/usuarios/{id} */
  deletarUsuario(id: string): Observable<void> {
    return this.http.delete<void>(`${this.base}/usuarios/${id}`);
  }

  /** PATCH /conecta/management/usuarios/{id}/tipo */
  alterarTipo(id: string, tipo: Tipo): Observable<void> {
    const params = new HttpParams().set('tipo', tipo);
    return this.http.patch<void>(`${this.base}/usuarios/${id}/tipo`, null, { params });
  }

  /** POST /conecta/management/usuarios/{id}/solicitar-senha */
  solicitarSenha(id: string): Observable<void> {
    return this.http.post<void>(`${this.base}/usuarios/${id}/solicitar-senha`, null);
  }

  /** POST /conecta/management/usuarios/{id}/solicitar-email */
  solicitarEmail(id: string): Observable<void> {
    return this.http.post<void>(`${this.base}/usuarios/${id}/solicitar-email`, null);
  }

  // ── Turmas ───────────────────────────────────────────────────────────────────

  /** GET /conecta/management/turmas */
  listarTurmas(page = 0, size = 20): Observable<PageResult<Turma>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResult<Turma>>(`${this.base}/turmas`, { params });
  }

  /** GET /conecta/management/turmas/atuais */
  listarTurmasAtuais(page = 0, size = 20): Observable<PageResult<Turma>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResult<Turma>>(`${this.base}/turmas/atuais`, { params });
  }

  /** GET /conecta/management/turmas/{id} */
  buscarTurma(id: string): Observable<Turma> {
    return this.http.get<Turma>(`${this.base}/turmas/${id}`);
  }

  /** GET /conecta/management/turmas/curso */
  listarTurmasPorCurso(curso: Cursos, page = 0, size = 20): Observable<PageResult<Turma>> {
    const params = new HttpParams().set('curso', curso).set('page', page).set('size', size);
    return this.http.get<PageResult<Turma>>(`${this.base}/turmas/curso`, { params });
  }

  /** GET /conecta/management/turmas/curso/{curso}/atuais */
  listarTurmasPorCursoAtuais(curso: Cursos, page = 0, size = 20): Observable<PageResult<Turma>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResult<Turma>>(`${this.base}/turmas/curso/${curso}/atuais`, { params });
  }

  /** POST /conecta/management/turmas */
  criarTurmas(cursos: Cursos[]): Observable<void> {
    return this.http.post<void>(`${this.base}/turmas`, cursos);
  }

  /** POST /conecta/management/turmas/passar-modulo */
  passarModulo(): Observable<void> {
    return this.http.post<void>(`${this.base}/turmas/passar-modulo`, null);
  }

  // ── Mensagens ────────────────────────────────────────────────────────────────

  /** GET /conecta/management/mensagens/sender/{sender}/receiver/{receiver} */
  listarMensagens(sender: string, receiver: string, page = 0, size = 20): Observable<PageResult<DTOReturnMessageSecretaria>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResult<DTOReturnMessageSecretaria>>(
      `${this.base}/mensagens/sender/${sender}/receiver/${receiver}`,
      { params }
    );
  }
  /** GET /conecta/secretaria */
  listarSecretaria(page = 0, size = 20): Observable<PageResult<DTORetornoNormal>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResult<DTORetornoNormal>>(`${environment.apiUrl}/secretaria`, { params });
  }
}