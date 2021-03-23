import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { FolderContent } from '../model/folder-content-model';

@Injectable({
  providedIn: 'root'
})
export class SharedContentService {
  SHARE_URL = "http://localhost:8080/share";
  DRIVE_URL = "http://localhost:8080/drive";

  constructor(private httpClient: HttpClient) { }

  getSharedContent(url: string): Observable<FolderContent> {
    return this.httpClient.get<FolderContent>(this.SHARE_URL + "/" + encodeURIComponent(url));
  }

  download(content: FolderContent): any {
    let params = new HttpParams();
    params = params.append("id", content.id!.toString());
    params = params.append("isFolder", (content.version! == -1) ? "true" : "false");

    return this.httpClient.get(this.DRIVE_URL + "/files",
      { observe: 'response', responseType: 'arraybuffer', params: params });
  }
}
