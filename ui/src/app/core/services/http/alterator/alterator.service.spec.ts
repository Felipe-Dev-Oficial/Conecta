import { TestBed } from '@angular/core/testing';

import { AlteradorService } from './alterator.service';

describe('Alterator', () => {
  let service: AlteradorService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AlteradorService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
