import { Component, OnInit } from '@angular/core';
import { SignInUserInterface } from 'src/app/models/SignInUserInterface';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { AuthenticationService } from 'src/app/services/authentication.service';
import { finalize } from 'rxjs';
import { Router } from '@angular/router';

@Component({
  selector: 'app-signin',
  templateUrl: './signin.component.html',
  styleUrls: ['./signin.component.css']
})
export class SigninComponent implements OnInit {

  signInForm: FormGroup;

  togglePassword: boolean = false;
  isLoading: boolean = false;

  signInMessage: string = '';

  constructor(private formBuilder: FormBuilder, private authenticationService: AuthenticationService,
    private router: Router) {
  }

  ngOnInit(): void {
    this.signInForm = this.formBuilder.group({
      username: new FormControl('', [
        Validators.required
      ]),
      password: new FormControl('', [
        Validators.required
      ])
    });
  }

  togglePasswordVisibility(): void {
    this.togglePassword = !this.togglePassword;
  }

  signIn() {
    if (this.signInForm.valid) {
      const signInUser: SignInUserInterface = this.signInForm.value;

      this.isLoading = true;

      this.authenticationService.signIn(signInUser)
        .pipe(finalize(() => this.isLoading = false))
        .subscribe(
          username => {
            this.signInMessage = "Welcome " + username;
            this.signInForm.reset();
            setTimeout(() => {
              this.router.navigate(['/search-track']); // Replace '/destination' with the target route URL
            }, 1000); // 200
          }
        );
    }
  }

  get username() {
    return this.signInForm.get('username');
  }

  get password() {
    return this.signInForm.get('password');
  }

}
