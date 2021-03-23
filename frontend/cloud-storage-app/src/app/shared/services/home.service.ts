import { HttpClient, HttpEvent, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CombinedData } from '../model/combined-data.model';
import { FolderContent } from '../model/folder-content-model';
import { Folder } from '../model/folder.model';
import { User } from '../model/user.model';

@Injectable({
  providedIn: 'root'
})
export class HomeService {
  user: User
  DRIVE_URL = "http://localhost:8080/drive";
  SHARE_URL = "http://localhost:8080/share";

  constructor(private httpClient: HttpClient) {
    this.user = JSON.parse(localStorage.getItem('currentUser') || "");
  }

  update(folderId: number, existingFile: File): Observable<HttpEvent<any>> {
    const formData = new FormData();

    formData.append("file", existingFile, existingFile.name)
    formData.append("id", JSON.stringify(this.user.id));
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
    formData.append("id", JSON.stringify(this.user.id));
    formData.append("folder", JSON.stringify(folderId));

    return this.httpClient.post(this.DRIVE_URL + "/upload", formData, {
      reportProgress: true,
      observe: 'events'
    })
  }

  newFolder(parentFolderId: number, name: string): Observable<Folder> {
    return this.httpClient.post(this.DRIVE_URL + "/new-folder",
      {
        "parentFolderId": parentFolderId,
        "folderName": name
      });
  }

  loadRootFolder(): Observable<CombinedData> {
    return this.httpClient.post<CombinedData>(this.DRIVE_URL + "/get-root-folder", this.user.id)
  }

  loadFolderContent(folderId: number): Observable<CombinedData> {
    return this.httpClient.post<CombinedData>(this.DRIVE_URL + "/folder-content", folderId)
  }

  delete(file: FolderContent) {
    return this.httpClient.post(this.DRIVE_URL + "/delete", {
      "id": file.id,
      "isFolder": (file.version! == -1)
    });
  }

  restore(file: FolderContent) {
    return this.httpClient.post(this.DRIVE_URL + "/restore-deleted", {
      "id": file.id,
      "isFolder": (file.version! == -1)
    });
  }

  loadTrashedFiles(): Observable<CombinedData> {
    return this.httpClient.post<CombinedData>(this.DRIVE_URL + "/trash", this.user.id);
  }

  download(file: FolderContent): any {
    let params = new HttpParams();
    params = params.append("id", file.id!.toString());
    params = params.append("isFolder", (file.version! == -1) ? "true" : "false");

    return this.httpClient.get(this.DRIVE_URL + "/files",
      { observe: 'response', responseType: 'arraybuffer', params: params });
  }

  getShareLink(file: FolderContent): any {
    return this.httpClient.post(this.SHARE_URL + "/content",
      {
        "userId": this.user.id,
        "fileId": file.id!,
        "isFolder": (file.version! == -1)
      }, { responseType: 'text' })
  }
}
