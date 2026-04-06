import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FaqService } from '../../core/services/http/faq/faq.service';
import { ToastService } from '../../core/services/toast/toast.service';
import { DTOReturnFAQ } from '../../core/models/models';

@Component({
  selector: 'app-faqs',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './faqs.component.html',
  styleUrl: './faqs.component.css',
})
export class FaqsComponent implements OnInit {
  svc = inject(FaqService);
  toast = inject(ToastService);

  faqs = signal<DTOReturnFAQ[]>([]);
  loading = signal(true);
  page = signal(0);
  totalPages = signal(1);
  openIdx = signal<number | null>(null);

  ngOnInit() { this.load(); }

  load() {
    this.loading.set(true);
    this.svc.listarFaqs(this.page()).subscribe({
      next: r => { this.faqs.set(r.content); this.totalPages.set(r.totalPages); this.loading.set(false); },
      error: () => { this.toast.error('Erro ao carregar FAQs.'); this.loading.set(false); },
    });
  }

  toggle(i: number) { this.openIdx.update(v => v === i ? null : i); }
  goTo(p: number) { this.page.set(p); this.load(); }
  pageRange() { return Array.from({ length: this.totalPages() }, (_, i) => i); }
}