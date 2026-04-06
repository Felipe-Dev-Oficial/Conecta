import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MensagensMgmtComponent } from './mensagens-mgmt.component';

describe('MensagensMgmtComponent', () => {
  let component: MensagensMgmtComponent;
  let fixture: ComponentFixture<MensagensMgmtComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MensagensMgmtComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(MensagensMgmtComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
