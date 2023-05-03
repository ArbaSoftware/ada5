import { Injectable } from '@angular/core';
import { Store } from './model/store';
import { HttpClient } from '@angular/common/http';
import { HttpHeaders } from '@angular/common/http';
import {AdaClass} from './model/ada-class';

@Injectable({
  providedIn: 'root'
})
export class RestService {
  private user: string = "dev@arjanbas.nl";
  private password: string = "hemertje";

  constructor(private http: HttpClient) { 
  }

  public getStores(callback: any) : void {
    this.http
      .get<any>("http://192.168.2.74:9601/ada/store", {
        headers: new HttpHeaders( {
          'Authorization': 'Basic ' + btoa(this.user + ':' + this.password)
        })
      })
      .subscribe(data => {
        var results : Store[] = [];
        for (var index = 0; index < data.length; index++) {
          results.push(Store.fromJson(data[index]));
        }
        callback(results);
      });
  }

  public getClasses(storeid: string, callback: any) : void {
    this.http
      .get<any>("http://192.168.2.74:9601/ada/store/" + storeid + "/class", {
        headers: new HttpHeaders( {
          'Authorization': 'Basic ' + btoa(this.user + ':' + this.password)
        })
      })
      .subscribe(data => {
        var result : AdaClass[] = [];
        for (var index = 0; index < data.length; index++)
          result.push(AdaClass.fromJson(data[index]));
        callback(result);
      })
  }

  public getClass(storeid: string, classid: string, callback: any) : void {
    this.http
      .get<any>("http://192.168.2.74:9601/ada/store/" + storeid + "/class/" + classid, {
        headers: new HttpHeaders( {
          'Authorization': 'Basic ' + btoa(this.user + ':' + this.password)
        })
      })
      .subscribe(data => {
        callback(AdaClass.fromJson(data));
      })
  }
}
