import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { User } from '../model/user.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private httpClient: HttpClient) { }

  login(user: User) {
    return this.httpClient.post("http://localhost:8080/login", user);
  }

  register(user: User) {
    return this.httpClient.post("http://localhost:8080/register", user);
  }
}
