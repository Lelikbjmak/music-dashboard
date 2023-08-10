import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AuthenticationService } from 'src/app/services/authentication.service';
import { JwtService } from 'src/app/services/jwt.service';

@Injectable()
export class JwtInterceptor implements HttpInterceptor {

    constructor(private jwtService: JwtService, private authenticationService: AuthenticationService) {
    }

    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        const jwt = this.jwtService.get();
        if (this.authenticationService.isAuthenticated()) {
            request = request.clone({
                setHeaders: {
                    Authorization: `Bearer ${jwt}`
                }
            });
        }

        return next.handle(request);
    }
}