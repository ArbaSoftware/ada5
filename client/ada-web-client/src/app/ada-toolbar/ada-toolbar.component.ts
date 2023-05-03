import { Component } from '@angular/core';
import { RestService } from '../rest.service';
import { Store } from '../model/store';
import { Input } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-ada-toolbar',
  templateUrl: './ada-toolbar.component.html',
  styleUrls: ['./ada-toolbar.component.css']
})
export class AdaToolbarComponent {
  @Input() title = '';
  stores: Store[] = [];

  constructor(private service: RestService, private router: Router) {
    service.getStores(this.applyStores.bind(this));
  }

  applyStores(stores: Store[]) {
    this.stores = stores;
  }

  showStore(storeid: string) : void {
    this.router.navigate(['/store', {id: storeid}]);
  }

}
