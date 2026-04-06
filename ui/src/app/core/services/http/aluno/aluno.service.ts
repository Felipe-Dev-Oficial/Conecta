import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DTORetornoNormal, PageResult } from '../../../models/models';
import { environment } from '../../../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AlunoService {
  private base = `${environment.apiUrl}/alunos`;

  constructor(private http: HttpClient) {}

  /** GET /conecta/alunos/busca_professor */
  buscarProfessores(page = 0, size = 20): Observable<PageResult<DTORetornoNormal>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResult<DTORetornoNormal>>(`${this.base}/busca_professor`, { params });
  }

  /** GET /conecta/alunos/busca_professor/{id} */
  buscarProfessorPorId(id: string): Observable<DTORetornoNormal> {
    return this.http.get<DTORetornoNormal>(`${this.base}/busca_professor/${id}`);
  }
}