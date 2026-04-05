import { TestBed } from '@angular/core/testing';

import { Alterator } from './alterator';

describe('Alterator', () => {
  let service: Alterator;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Alterator);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
