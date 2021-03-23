import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { UserRegister } from 'src/app/shared/model/user-register.model';
import { UserService } from 'src/app/shared/services/user.service';

@Component({
  selector: 'app-register-page',
  templateUrl: './register-page.component.html',
  styleUrls: ['./register-page.component.css']
})
export class RegisterPageComponent implements OnInit {

  user: UserRegister;
  errorMessage: string;

  constructor(private userHttpApi: UserService, private router: Router) {
    this.user = {};
    this.errorMessage = "";
  }

  ngOnInit(): void {
  }

  register() {
    console.log('Button is pressed:', this.user);
    if (this.user.password === this.user.passwordRepeat) {
      this.userHttpApi.register(this.user).subscribe(
        response => {
          console.log('response:', response);
          localStorage.setItem('currentUser', JSON.stringify(response));
          this.router.navigate(['/home']);
        }, error => {
          console.log(error)
          this.errorMessage = "Wrong username or password";
        })
    } else this.errorMessage = "Check password";
  }

}
