import { inject } from '@angular/core';
import { AuthenticationService } from '../services/authentication.service';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot } from '@angular/router';

export const authorizationGuard = (route: ActivatedRouteSnapshot, state: RouterStateSnapshot) => {

    const requiredRoles = route.data.roles as string[];
    const authenticationService: AuthenticationService = inject(AuthenticationService);
    const router: Router = inject(Router);

    if (authenticationService.isAuthorized(requiredRoles)) {
        return true;
    }

    router.navigate(['/sign-in']);

    return false;
}