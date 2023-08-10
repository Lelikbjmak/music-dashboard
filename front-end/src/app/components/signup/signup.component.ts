import { Component, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, FormControl, FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { UserService } from 'src/app/services/user.service';
import { finalize } from 'rxjs';
import { SignUpUserInterface } from 'src/app/models/SignUpUserInterface';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignupComponent implements OnInit {

  signUpForm: FormGroup;

  isLoading: boolean = false;
  isErrorResponse = false;

  signUpMessage: string = '';

  emailServerErrorMessage: string = '';
  usernameServerErrorMessage: string = '';

  notValidServerEmail: string = '*';
  notValidServerUsername: string = '*';

  constructor(private formBuilder: FormBuilder, private userService: UserService) {
  }

  ngOnInit(): void {
    this.signUpForm = this.formBuilder.group({
      username: new FormControl('', [
        Validators.required,
        Validators.pattern(/^\w{5,25}$/),
        this.serverValidator(() => this.notValidServerUsername)
      ]),
      email: new FormControl('', [
        Validators.required,
        Validators.email,
        this.serverValidator(() => this.notValidServerEmail)
      ]),
      password: new FormControl('', [
        Validators.required,
        Validators.pattern(/^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,25}$/)
      ]),
      confirmedPassword: new FormControl('', [
      ])
    }, {
      validator: [
        this.passwordMatchValidator
      ]
    });
  }

  togglePassword: boolean = false;
  toggleConfirmedPassword: boolean = false;

  togglePasswordVisibility(): void {
    this.togglePassword = !this.togglePassword;
  }

  toggleConfirmedPasswordVisibility(): void {
    this.toggleConfirmedPassword = !this.toggleConfirmedPassword;
  }

  signUp() {
    if (this.signUpForm.valid) {
      const signUpUser: SignUpUserInterface = this.signUpForm.value;
      signUpUser.roleSet = ['ROLE_USER'];

      this.isLoading = true;
      this.userService.signUp(signUpUser)
        .pipe(finalize(() => this.isLoading = false))
        .subscribe(
          () => {
            this.signUpMessage = "Successfully";
            this.signUpForm.reset();
          },
          (error) => {
            this.isErrorResponse = true;
            if (error.error) {
              if (error.status === 400) {
                this.signUpMessage = "Registration failed";

                const errorResponse = error.error;

                const emailError = errorResponse.email;
                const usernameError = errorResponse.username;
                const passwordError = errorResponse.password;
                const confirmedPasswordError = errorResponse.registrationUserDto;

                if (emailError) {
                  this.email?.setErrors({ server: emailError });
                  this.notValidServerEmail = this.email?.value;
                  this.emailServerErrorMessage = emailError;
                }

                if (usernameError) {
                  this.username?.setErrors({ server: usernameError });
                  this.notValidServerUsername = this.username?.value;
                  this.usernameServerErrorMessage = usernameError;
                }

                if (passwordError) {
                  this.password?.setErrors({ pattern: passwordError });
                }

                if (confirmedPasswordError) {
                  this.confirmedPasword?.setErrors({ mismatch: confirmedPasswordError });
                }

              } else {
                this.signUpMessage = error.statusText;
              }
            }
          }
        );
    }
  }

  get username() {
    return this.signUpForm.get('username');
  }

  get email() {
    return this.signUpForm.get('email');
  }

  get password() {
    return this.signUpForm.get('password');
  }

  get confirmedPasword() {
    return this.signUpForm.get('confirmedPassword');
  }

  passwordMatchValidator(form: FormGroup): void {
    const password = form.get('password')?.value;
    const confirmedPassword = form.get('confirmedPassword')?.value;

    if (confirmedPassword !== password) {
      form.get('confirmedPassword')?.setErrors({ mismatch: true });
    }
  }

  serverValidator(notEqualValueFn: () => string): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const field = control.value;
      const notEqualValue = notEqualValueFn();
      if (field === notEqualValue) {
        return { server: true };
      }
      return null;
    };
  }

}