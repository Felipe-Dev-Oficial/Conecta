import { Routes } from '@angular/router';
import { authGuard, guestGuard, secretariaGuard } from '../app/core/guards/auth-guard';

export const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },

  {
    path: 'auth',
    canActivate: [guestGuard],
    children: [
      {
        path: 'login',
        loadComponent: () =>
          import('./features/auth/login/login.component').then(m => m.LoginComponent),
      },
      {
        path: 'alterador',
        loadComponent: () =>
          import('./features/auth/alterador/alterador.component').then(m => m.AlteradorComponent),
      },
      { path: '', redirectTo: 'login', pathMatch: 'full' },
    ],
  },

  // ── Rotas públicas (sem authGuard, com shell) ──────────────────────────
  {
    path: '',
    loadComponent: () =>
      import('./shared/components/shell/shell.component').then(m => m.ShellComponent),
    children: [
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./features/anuncios/anuncios.component').then(m => m.AnunciosComponent),
      },
      {
        path: 'faqs',
        loadComponent: () =>
          import('./features/faqs/faqs.component').then(m => m.FaqsComponent),
      },
    ],
  },

  // ── Rotas privadas (com authGuard, com shell) ──────────────────────────
  {
    path: '',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./shared/components/shell/shell.component').then(m => m.ShellComponent),
    children: [
      {
        path: 'mensagens',
        loadComponent: () =>
          import('./features/mensagens/mensagens.component').then(m => m.MensagensComponent),
      },
      {
        path: 'perfil',
        loadComponent: () =>
          import('./features/perfil/perfil.component').then(m => m.PerfilComponent),
      },
      {
        path: 'solicitacoes',
        loadComponent: () =>
          import('./features/solicitation/solicitation.component').then(m => m.SolicitationComponent),
      },
      {
        path: 'management',
        canActivate: [secretariaGuard],
        children: [
          {
            path: '',
            loadComponent: () =>
              import('./features/management/management.component').then(m => m.ManagementComponent),
          },
          {
            path: 'usuarios',
            loadComponent: () =>
              import('./features/management/usuarios/usuarios.component').then(m => m.UsuariosComponent),
          },
          {
            path: 'turmas',
            loadComponent: () =>
              import('./features/management/turmas/turmas.component').then(m => m.TurmasComponent),
          },
          {
            path: 'anuncios',
            loadComponent: () =>
              import('./features/management/anuncios-mgmt/anuncios-mgmt.component').then(
                m => m.AnunciosMgmtComponent,
              ),
          },
          {
            path: 'faqs',
            loadComponent: () =>
              import('./features/management/faqs-mgmt/faqs-mgmt.component').then(
                m => m.FaqsMgmtComponent,
              ),
          },
          {
            path: 'mensagens',
            loadComponent: () =>
              import('./features/management/mensagens-mgmt/mensagens-mgmt.component').then(
                m => m.MensagensMgmtComponent,
              ),
          },
          {
            path: 'solicitacoes',
            loadComponent: () =>
              import('./features/management/solicitation-mgmt/solicitation-mgmt.component').then(
                m => m.SolicitationMgmtComponent,
              ),
          },
        ],
      },
    ],
  },

  { path: '**', redirectTo: 'dashboard' },
];