import { TestBed } from '@angular/core/testing';

import { AnuncioService } from './anuncio.service';

describe('Anuncio', () => {
  let service: AnuncioService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AnuncioService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
