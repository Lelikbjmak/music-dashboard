import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ErrorService {

  error$ = new Subject<String>();

  constructor() { }

  handleError(message: String) {
    this.error$.next(message);
  }

  clearError() {
    this.error$.next('');
  }

}
