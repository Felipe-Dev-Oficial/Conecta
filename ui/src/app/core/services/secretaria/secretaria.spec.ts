import { TestBed } from '@angular/core/testing';

import { Secretaria } from './secretaria';

describe('Secretaria', () => {
  let service: Secretaria;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Secretaria);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
