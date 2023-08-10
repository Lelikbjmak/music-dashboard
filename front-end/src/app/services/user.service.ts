import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, delay, retry, throwError } from 'rxjs';
import { environment } from '../../environments/environment';
import { SignUpUserInterface } from '../models/SignUpUserInterface';
import { ErrorService } from './error.service';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private readonly userServiceRoute = environment.userServiceRoute;

  constructor(private httpClient: HttpClient) {
  }

  signUp(user: SignUpUserInterface): Observable<any> {
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    return this.httpClient.post<any>(this.userServiceRoute, user, { headers: headers })
      .pipe(
        delay(2000),
        retry(2)
      );
  }
}
