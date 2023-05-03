import { Component } from '@angular/core';
import { RestService } from './rest.service';
import { Store } from './model/store';
import { AdaToolbarComponent } from './ada-toolbar/ada-toolbar.component';
import {MatTreeModule} from '@angular/material/tree';
@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'ada-web-client';
}
