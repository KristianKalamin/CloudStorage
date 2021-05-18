import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from './auth.guard';
import { CallbackComponent } from './callback/callback.component';
import { HomeComponent } from './pages/home/home.component';
import { SharedContentComponent } from './pages/shared-content/shared-content.component';


const routes: Routes = [
  { path: '', component: HomeComponent, canActivate: [AuthGuard], pathMatch: 'full' },
  { path: 'share/:link', component: SharedContentComponent, pathMatch: 'full' },
  { path: 'share/users/:link', component: SharedContentComponent, canActivate: [AuthGuard], pathMatch: 'full'},
  { path: 'callback', component: CallbackComponent },
  { path: '**', redirectTo: '' }
];


@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
