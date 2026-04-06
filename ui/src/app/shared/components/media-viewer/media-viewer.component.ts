import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Midia } from '../../../core/models/models';

@Component({
  selector: 'app-media-viewer',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './media-viewer.component.html',
  styleUrl: './media-viewer.component.css',
})
export class MediaViewerComponent {
  @Input() midia: Midia | null = null;
}