import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ThemeService } from '../../core/services/theme/theme.service';

interface Card { icon: string; title: string; desc: string; route: string; }

@Component({
  selector: 'app-management',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './management.component.html',
  styleUrl: './management.component.css',
})
export class ManagementComponent {
  themeService = inject(ThemeService);

  cards: Card[] = [
    { icon: '👥', title: 'Usuários', desc: 'Gerenciar alunos e funcionários', route: '/management/usuarios' },
    { icon: '🏫', title: 'Turmas', desc: 'Criar e gerenciar turmas', route: '/management/turmas' },
    { icon: '📢', title: 'Anúncios', desc: 'Publicar e editar anúncios', route: '/management/anuncios' },
    { icon: '❓', title: 'FAQs', desc: 'Gerenciar perguntas frequentes', route: '/management/faqs' },
    { icon: '💬', title: 'Mensagens', desc: 'Visualizar conversas entre usuários', route: '/management/mensagens' },
  ];
}