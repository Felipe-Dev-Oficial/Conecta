import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FaqsMgmtComponent } from './faqs-mgmt.component';

describe('FaqsMgmtComponent', () => {
  let component: FaqsMgmtComponent;
  let fixture: ComponentFixture<FaqsMgmtComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FaqsMgmtComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(FaqsMgmtComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
