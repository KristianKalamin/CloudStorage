import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginPageComponent } from './pages/login-page/login-page.component';
import { HomeComponent } from './pages/home/home.component';
import { SharedModule } from './shared/shared.module';
import { RegisterPageComponent } from './pages/register-page/register-page.component';
import { AuthGuard } from './_guards/auth.guard';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { SharedContentComponent } from './pages/shared-content/shared-content.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { ToastrModule } from 'ngx-toastr';
import { CommonModule } from '@angular/common';
@NgModule({
  declarations: [
    AppComponent,
    LoginPageComponent,
    HomeComponent,
    RegisterPageComponent,
    SharedContentComponent
  ],
  imports: [
    CommonModule,
    BrowserModule,
    SharedModule,
    AppRoutingModule,
    NgbModule,
    BrowserAnimationsModule,
    ToastrModule.forRoot({
      disableTimeOut: true,
      tapToDismiss: true,
      enableHtml: true,
      closeButton: true,
      newestOnTop: true,
      positionClass: 'toast-bottom-right',
      preventDuplicates: true,
      iconClasses: {
        info: 'toast-info',
        success: 'toast-success'
      }
    })
  ],
  providers: [
    AuthGuard
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
