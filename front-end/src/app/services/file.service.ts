import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ErrorService } from './error.service';
import { environment } from '../../environments/environment';
import { Observable, catchError, delay, map, retry, throwError } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class FileService {

  private readonly fileServiceRoute = environment.fileServiceRoute;

  constructor(private httpClient: HttpClient) {

  }

  upload(file: FormData): Observable<string> {
    return this.httpClient.post<any>(this.fileServiceRoute, file)
      .pipe(
        delay(2000),
        retry(2),
        map(() => {
          return 'File is successfully uploaded.';
        })
      );
  }
}
