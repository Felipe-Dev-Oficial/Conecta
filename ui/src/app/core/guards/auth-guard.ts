import { inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/http/auth/auth.service';

export const authGuard: CanActivateFn = () => {
  const router = inject(Router);
  const platformId = inject(PLATFORM_ID);

  if (isPlatformBrowser(platformId)) {
    if (localStorage.getItem('token')) return true;
    router.navigate(['/auth/login']);
  }
  return false;
};

export const secretariaGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const platformId = inject(PLATFORM_ID);

  if (!isPlatformBrowser(platformId)) return false;

  const user = auth.getUserData();
  if (user?.role === 'SECRETARIA') return true;

  router.navigate(['/dashboard']);
  return false;
};

export const guestGuard: CanActivateFn = () => {
  const router = inject(Router);
  const platformId = inject(PLATFORM_ID);

  if (isPlatformBrowser(platformId)) {
    if (!localStorage.getItem('token')) return true;
    router.navigate(['/dashboard']);
    return false;
  }
  return true;
};