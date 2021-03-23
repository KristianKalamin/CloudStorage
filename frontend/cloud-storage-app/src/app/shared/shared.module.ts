import { HttpClientModule } from "@angular/common/http";
import { NgModule } from "@angular/core";
import { FormsModule } from "@angular/forms";

@NgModule({
  declarations: [],
  imports: [
    HttpClientModule,
    FormsModule
  ],
  exports: [
    HttpClientModule,
    FormsModule
  ]
})
export class SharedModule {}
