import { inject } from '@angular/core';
import { AuthenticationService } from '../services/authentication.service';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot } from '@angular/router';

export const authenticationGuard = (route: ActivatedRouteSnapshot, state: RouterStateSnapshot) => {

  const authenticationService: AuthenticationService = inject(AuthenticationService);
  const router: Router = inject(Router);

  if (authenticationService.isAuthenticated()) {
    return true;
  }

  router.navigate(['/sign-in']);

  return false;
}