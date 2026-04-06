import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MediaViewerComponent } from './media-viewer.component';

describe('MediaViewerComponent', () => {
  let component: MediaViewerComponent;
  let fixture: ComponentFixture<MediaViewerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MediaViewerComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(MediaViewerComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
