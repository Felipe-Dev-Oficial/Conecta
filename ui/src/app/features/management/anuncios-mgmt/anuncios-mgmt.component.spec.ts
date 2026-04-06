import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AnunciosMgmtComponent } from './anuncios-mgmt.component';

describe('AnunciosMgmtComponent', () => {
  let component: AnunciosMgmtComponent;
  let fixture: ComponentFixture<AnunciosMgmtComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AnunciosMgmtComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(AnunciosMgmtComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
