import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, delay, map, retry } from 'rxjs';
import { environment } from '../../environments/environment';
import { AuthenticationInterface } from '../models/AuthenticationInterface';
import { SignInUserInterface } from '../models/SignInUserInterface';
import { ErrorService } from './error.service';
import { JwtService } from './jwt.service';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {

  private readonly authenticationServiceRoute = environment.authenticationServiceRoute;

  constructor(private httpClient: HttpClient, private jwtService: JwtService, private router: Router) {
  }

  signIn(user: SignInUserInterface): Observable<string> {
    return this.httpClient.post<AuthenticationInterface>(this.authenticationServiceRoute, user)
      .pipe(
        delay(1000),
        retry(2),
        map((response: AuthenticationInterface) => {
          const jwtToken = response.token;
          const username = response.username;
          this.jwtService.save(jwtToken);
          return username;
        })
      );
  }

  signOut() {
    this.router.navigate(['/sign-in']);
    this.jwtService.delete();
  }

  isAuthenticated(): boolean {
    return this.jwtService.isValid();
  }

  isAuthorized(requiredRoleList: string[]): boolean {
    const userRoleList = this.jwtService.extractRoles();
    return this.isAuthenticated() && userRoleList.some(role => requiredRoleList.includes(role));
  }
}
