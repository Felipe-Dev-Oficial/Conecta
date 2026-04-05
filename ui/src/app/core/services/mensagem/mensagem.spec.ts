import { TestBed } from '@angular/core/testing';

import { Mensagem } from './mensagem';

describe('Mensagem', () => {
  let service: Mensagem;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Mensagem);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
