import { HttpClient, HttpEvent, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CombinedData } from '../model/combined-data.model';
import { FolderContent } from '../model/folder-content-model';
import { Folder } from '../model/folder.model';
import { CloudUser } from '../model/cloud-user.model';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class HomeService {
  DRIVE_URL = "http://localhost:8080/drive";
  SHARE_URL = "http://localhost:8080/share";
  httpOptions: object;
  user: CloudUser;
  token: string;

  constructor(private httpClient: HttpClient, private auth: AuthService,) {
    this.token = auth.token!;
    this.httpOptions = { headers: new HttpHeaders({ "Authorization": "Bearer " + this.token }) }
    this.user = auth.cloudUser!;
  }

  update(folderId: number, existingFile: File): Observable<HttpEvent<any>> {
    const formData = new FormData();

    formData.append("file", existingFile, existingFile.name)
    formData.append("id", JSON.stringify(this.user.sub));
    formData.append("folder", JSON.stringify(folderId));

    return this.httpClient.post(this.DRIVE_URL + "/update", formData, {
      reportProgress: true,
      observe: 'events'
    })
  }

  upload(folderId: number, selectedFile: File): Observable<HttpEvent<any>> {
    console.log(selectedFile);

    const formData = new FormData();

    formData.append("file", selectedFile, selectedFile.name)
    formData.append("id", this.user.sub || "");
    formData.append("folder", JSON.stringify(folderId));

    return this.httpClient.post(this.DRIVE_URL + "/upload", formData, {
      reportProgress: true,
      observe: 'events',
      ...this.httpOptions
    })
  }

  newFolder(parentFolderId: number, name: string): Observable<Folder> {
    return this.httpClient.post(this.DRIVE_URL + "/new-folder",
      {
        "parentFolderId": parentFolderId,
        "folderName": name
      }, this.httpOptions);
  }

  loadRootFolder(): Observable<CombinedData> {
    return this.httpClient.post<CombinedData>(this.DRIVE_URL + "/get-root-folder", this.user.sub, this.httpOptions);
  }

  loadFolderContent(folderId: number): Observable<CombinedData> {
    return this.httpClient.post<CombinedData>(this.DRIVE_URL + "/folder-content", folderId, this.httpOptions);
  }


  delete(file: FolderContent) {
    return this.httpClient.post(this.DRIVE_URL + "/delete", {
      "id": file.id,
      "isFolder": (file.version! == -1)
    }, this.httpOptions);
  }

  restore(file: FolderContent) {
    return this.httpClient.post(this.DRIVE_URL + "/restore-deleted", {
      "id": file.id,
      "isFolder": (file.version! == -1)
    }, this.httpOptions);
  }

  loadTrashedFiles(): Observable<CombinedData> {
    return this.httpClient.post<CombinedData>(this.DRIVE_URL + "/trash", this.user.sub, this.httpOptions);
  }

  download(file: FolderContent): any {
    let params = new HttpParams();
    params = params.append("id", file.id!.toString());
    params = params.append("isFolder", (file.version! == -1) ? "true" : "false");

    return this.httpClient.get(this.DRIVE_URL + "/files",
      {
        observe: 'response',
        responseType: 'arraybuffer',
        params: params,
       ...this.httpOptions
      });
  }

  sharedWithMe(): Observable<FolderContent[]> {
    return this.httpClient.post<FolderContent[]>(this.SHARE_URL+"/shared-with-me",this.user.sub, this.httpOptions);
  }

  shareWithUsers(emails:string[], file: FolderContent): Observable<any> {
    return this.httpClient.post(this.SHARE_URL + "/content/users",
    {
      "userId": this.user.sub,
      "fileId": file.id!,
      "isFolder": (file.version! == -1),
      "emails": emails
    }, {
      responseType: 'text',
   ...this.httpOptions
  })
  }

  getShareLink(file: FolderContent): any {
    return this.httpClient.post(this.SHARE_URL + "/content",
      {
        "userId": this.user.sub,
        "fileId": file.id!,
        "isFolder": (file.version! == -1),
      }, {
        responseType: 'text',
     ...this.httpOptions
    })
  }
}
