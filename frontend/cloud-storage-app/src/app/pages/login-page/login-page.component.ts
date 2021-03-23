import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { User } from 'src/app/shared/model/user.model';
import { UserService } from 'src/app/shared/services/user.service';


@Component({
  selector: 'app-login-page',
  templateUrl: './login-page.component.html',
  styleUrls: ['./login-page.component.css']
})
export class LoginPageComponent implements OnInit {

  user: User;
  errorMessage: string;

  constructor(private userHttpApi: UserService, private router: Router) {
    this.user = {};
    this.errorMessage = "";
  }

  ngOnInit(): void {
  }

  login() {
    console.log('Button is pressed:', this.user);
    this.userHttpApi.login(this.user).subscribe(
      response => {
        console.log('response:', response);
        localStorage.setItem('currentUser', JSON.stringify(response));
        this.router.navigate(['/home']);
      }, error => {
        console.log(error)
        this.errorMessage = "Wrong username or password";
      }

    )
  }

}
