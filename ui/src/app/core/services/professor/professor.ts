import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DTORetornoNormal, PageResult } from '../../models/models';
import { environment } from '../../../../environments/environment';;

@Injectable({ providedIn: 'root' })
export class ProfessorService {
  private base = `${environment.apiUrl}/professores`;

  constructor(private http: HttpClient) {}

  /** GET /conecta/professores */
  listarAlunos(page = 0, size = 20): Observable<PageResult<DTORetornoNormal>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResult<DTORetornoNormal>>(this.base, { params });
  }

  /** GET /conecta/professores/aluno/id/{id} */
  buscarAlunoPorId(id: string): Observable<DTORetornoNormal> {
    return this.http.get<DTORetornoNormal>(`${this.base}/aluno/id/${id}`);
  }

  /** GET /conecta/professores/aluno/nome/{nome} */
  buscarAlunosPorNome(nome: string, page = 0, size = 20): Observable<PageResult<DTORetornoNormal>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResult<DTORetornoNormal>>(`${this.base}/aluno/nome/${nome}`, { params });
  }

  /** GET /conecta/professores/turma/{id} */
  listarAlunosPorTurma(id: string, page = 0, size = 20): Observable<PageResult<DTORetornoNormal>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResult<DTORetornoNormal>>(`${this.base}/turma/${id}`, { params });
  }
}