import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MensagensComponent } from './mensagens.component';

describe('MensagensComponent', () => {
  let component: MensagensComponent;
  let fixture: ComponentFixture<MensagensComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MensagensComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(MensagensComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
