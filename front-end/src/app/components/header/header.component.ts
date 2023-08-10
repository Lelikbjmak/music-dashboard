import { Component } from '@angular/core';
import { AuthenticationService } from 'src/app/services/authentication.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent {

  constructor(public authenticationService: AuthenticationService) {

  }

  a() {
    alert(this.authenticationService.isAuthenticated());
  }
}
