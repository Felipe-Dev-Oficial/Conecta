import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SolicitationMgmtComponent } from './solicitation-mgmt.component';

describe('SolicitationMgmtComponent', () => {
  let component: SolicitationMgmtComponent;
  let fixture: ComponentFixture<SolicitationMgmtComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SolicitationMgmtComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(SolicitationMgmtComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
