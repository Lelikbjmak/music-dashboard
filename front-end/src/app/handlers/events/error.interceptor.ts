import { Injectable } from '@angular/core';
import { HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { catchError, Observable, of, throwError } from 'rxjs';
import { Router } from "@angular/router";
import { AuthenticationService } from 'src/app/services/authentication.service';
import { ErrorService } from 'src/app/services/error.service';

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {

    constructor(private authService: AuthenticationService,
        private router: Router, private errorService: ErrorService) {
    }

    intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
        return next.handle(request).pipe(
            catchError(response => {
                const error = response.error;
                let message = 'Error';
                switch (response.status) {
                    case 401:
                        this.authService.signOut();
                        this.router.navigate(["/sign-in"]);
                        break;
                    case 403:
                        alert("403 Forbidden!");
                        break;
                    case 404:
                        message = 'Not found';
                        console.error(response);
                        break;
                }

                if (error) {
                    if (error instanceof ProgressEvent) {
                        message = 'Connection refused';
                    } else {
                        message = error;
                    }
                    if (error.error) {
                        message = error.error;
                    }
                } else {
                    message = response.statusText;
                }

                this.errorService.handleError(message);

                return throwError(() => error);
            })
        );
    }
}