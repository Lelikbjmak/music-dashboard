import { Injectable } from '@angular/core';
import * as jwt_decode from "jwt-decode";
import { throwError } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class JwtService {

  private readonly key: string = environment.jwtKey;

  constructor() { }

  save(token: string): void {
    localStorage.setItem(this.key, token);
  }

  get(): string | null {
    return localStorage.getItem(this.key);
  }

  delete(): void {
    localStorage.removeItem(this.key);
  }

  extractRoles(): string[] {
    const jwt = this.get();
    if (jwt) {
      const payload: any = jwt_decode.default(jwt);
      const roles: string[] = payload.roles;
      return roles;
    } else {
      return [];
    }
  }

  extractExpiration(): number {
    const jwt = this.get();
    if (jwt) {
      const payload: any = jwt_decode.default(jwt);
      const expiration: number = payload.exp;
      return expiration;
    } else {
      return 0;
    }
  }

  isValid(): boolean {
    const jwt = this.get();
    if (jwt) {
      const expirationTime = this.extractExpiration();
      const currentTime = Math.floor(Date.now() / 1000);
      return expirationTime > currentTime;
    }
    return false;
  }
}
