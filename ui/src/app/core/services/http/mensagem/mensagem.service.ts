import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  DTOContatos,
  DTOInfoMessage,
  DTOReturnMessage,
  PageResult,
  SliceResult,
} from '../../../models/models';
import { environment } from '../../../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class MensagensService {
  private base = `${environment.apiUrl}/mensagens`;

  constructor(private http: HttpClient) {}

  /** GET /conecta/mensagens/contatos */
  listarContatos(page = 0, size = 20): Observable<SliceResult<DTOContatos>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<SliceResult<DTOContatos>>(`${this.base}/contatos`, { params });
  }

  /** GET /conecta/mensagens/{id} */
  lerMensagens(id: string, page = 0, size = 20): Observable<PageResult<DTOReturnMessage>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResult<DTOReturnMessage>>(`${this.base}/${id}`, { params });
  }

  /** POST /conecta/mensagens/{id} */
  enviarMensagem(id: string, dto: DTOInfoMessage): Observable<void> {
    return this.http.post<void>(`${this.base}/${id}`, dto);
  }
}