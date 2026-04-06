import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AlteradorComponent } from './alterador.component';

describe('AlteradorComponent', () => {
  let component: AlteradorComponent;
  let fixture: ComponentFixture<AlteradorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AlteradorComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(AlteradorComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
