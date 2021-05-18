import { HttpEvent, HttpEventType } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { CombinedData } from 'src/app/shared/model/combined-data.model';
import { HomeService } from 'src/app/shared/services/home.service';
import { ActiveToast, Toast, ToastrService } from 'ngx-toastr';
import { AuthService } from 'src/app/shared/services/auth.service';
import { UserService } from 'src/app/shared/services/user.service';
@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  allData: CombinedData;
  newFolderName = '';
  currentFolderName = '';
  isTrashShowed = false;
  sharedLink: string = "";
  currentFolderId: number;
  fileEvent: any;
  uploadProgress: number;
  i = 0;
  isEmailShare = false;
  emailsTextBox: string = "";
  shareContentId?: number;
  sharedMsg: string = "";
  isDriveContentShowed = true;
  userEmail: string = "";

  constructor(private auth: AuthService, private userService: UserService, private toast: ToastrService, private modalService: NgbModal, private homeHttpApi: HomeService, private router: Router) {
    this.allData = {};
    this.currentFolderId = 0;
    this.uploadProgress = 0;
  }

  ngOnInit(): void {
    this.userService.login(this.auth.cloudUser!)
      .subscribe(r => {
        this.userEmail = this.auth.cloudUser!.email!;
        this.loadData();
      });

  }

  sharedWithMe() {
    this.homeHttpApi.sharedWithMe().subscribe(
      sharedContent => {
        this.isDriveContentShowed = false;
        let cd: CombinedData = {
          rootFolder: {},
          combinedFiles: sharedContent
        };
        this.allData = cd;
      },
      error => console.log(error)
    )
  }

  emailShare() {
    let emails: string[] = this.emailsTextBox.split(",");
    this.homeHttpApi
      .shareWithUsers(emails, this.allData.combinedFiles![this.shareContentId!])
      .subscribe(_ => this.sharedMsg = "Link is send to emails",
        error => this.sharedMsg = "Error");
  }

  logout() {
    localStorage.removeItem('currentUser');
    localStorage.removeItem('token');
    this.auth.logout();
    this.router.navigate(['/']);
  }

  refreshCurrentFolder() {
    console.log("Refreshing current folder " + this.currentFolderId);

    this.homeHttpApi.loadFolderContent(this.currentFolderId).subscribe(
      res => {
        this.allData = res
      },
      error => console.log(error)
    );
  }

  up() {
    this.homeHttpApi.loadFolderContent(this.allData.rootFolder!.id!).subscribe(
      res => {
        this.allData = res;
        this.currentFolderName = res.rootFolder!.name!;
        console.log("Up " + this.currentFolderName);
        this.i--;

      },
      error => console.log(error)

    );
  }

  setFile(event) {
    this.fileEvent = event;
  }

  upload() {
    const files: FileList = this.fileEvent.target.files;
    if (!this.isTrashShowed) {
      for (let i = 0; i < files.length; i++) {
        let file = this.allData.combinedFiles!.find(v => v.name === files[i].name);
        if (file !== undefined) {
          this.homeHttpApi.update(this.currentFolderId, files[i]).subscribe((event: HttpEvent<any>) => {
            let activeToast: ActiveToast<Toast> = this.toast.info("Uploading");
            let toastInstance: Toast = activeToast.toastRef.componentInstance;
            switch (event.type) {
              case HttpEventType.Sent:
                break;
              case HttpEventType.ResponseHeader:
                break;
              case HttpEventType.UploadProgress:
                this.uploadProgress = Math.round(event.loaded / event.total! * 100);
                toastInstance.message = "Uploading " + file!.name! + " - " + this.uploadProgress + "%";
                break;
              case HttpEventType.Response:
                toastInstance.message = "Uploaded " + file!.name!;
                this.refreshCurrentFolder();
            }
          })
        } else {
          this.homeHttpApi.upload(this.currentFolderId, files[i]).subscribe((event: HttpEvent<any>) => {
            let activeToast: ActiveToast<Toast> = this.toast.info("Uploading");
            let toastInstance: Toast = activeToast.toastRef.componentInstance;

            switch (event.type) {
              case HttpEventType.Sent:
                break;
              case HttpEventType.ResponseHeader:
                break;
              case HttpEventType.UploadProgress:
                this.uploadProgress = Math.round(event.loaded / event.total! * 100);
                toastInstance.message = "Uploading " + files[i].name + " - " + this.uploadProgress + "%";
                break;
              case HttpEventType.Response:
                this.refreshCurrentFolder();
                toastInstance.message = "Uploaded " + files[i].name;
            }
          });
        }
      }
    }

  }

  trash() {
    this.isTrashShowed = true;
    this.homeHttpApi.loadTrashedFiles().subscribe(
      res => this.allData = res,
      error => console.log(error)
    );
  }

  restoreFile(id: number) {
    const file = this.allData.combinedFiles![id];
    this.homeHttpApi.restore(file).subscribe(
      _ => this.trash(),
      error => console.log(error)
    );
  }

  showFolderContent(id: number) {
    if (!this.isTrashShowed) {
      const content = this.allData.combinedFiles![id];
      if (content.version! == -1) {
        this.homeHttpApi.loadFolderContent(content.id!).subscribe(
          res => {
            this.allData = res;
            this.currentFolderName = content.name!;
            this.currentFolderId = content.id!;
            console.log(this.allData);
            this.i++;
          },
          error => console.log(error)

        );
      }
    }
  }

  download(id: number) {
    const file = this.allData.combinedFiles![id];
    this.homeHttpApi.download(file).subscribe(
      data => {
        const fileName = data.headers.get('File-Name');

        const blob = new Blob([data.body], { type: 'application/octet-stream' });
        //const url = window.URL.createObjectURL(blob);
        //let pwa = window.open(url);
        /*
                let anchor = document.createElement("a");
                anchor.download = "myfile.txt";
                anchor.href = url;
                let pwa = anchor.click();
        */
        const element = document.createElement('a');
        element.href = URL.createObjectURL(blob);
        element.download = fileName;
        document.body.appendChild(element);
        element.click();


        /* if (!pwa || pwa.closed || typeof pwa.closed == 'undefined') {
           alert('Please disable your Pop-up blocker and try again.');
         }*/
      },
      error => console.log(error)
    )
  }

  shareWithAnyone() {
    this.isEmailShare = false;
    this.homeHttpApi.getShareLink(this.allData.combinedFiles![this.shareContentId!]).subscribe(
      res => {
        console.log(res);
        this.sharedLink = res as string;
      },
      error => console.log(error)
    );
  }

  deleteFile(id: number) {
    console.log(id);

    this.homeHttpApi.delete(this.allData.combinedFiles![id]).subscribe(
      _ => this.loadData(),
      error => console.log(error)
    );
  }

  loadData() {
    this.isTrashShowed = false;
    this.homeHttpApi.loadRootFolder().subscribe(
      combinedData => {
        this.isDriveContentShowed = true;
        this.allData = combinedData;
        this.currentFolderName = combinedData.rootFolder!.name!;

        this.currentFolderId = combinedData.rootFolder!.id!;
        console.log(this.currentFolderName);

      },
      error => console.log(error)
    )
  }

  openNewFolderModal(newFolder) {
    this.modalService.open(newFolder, { ariaLabelledBy: 'folder' }).result.then((result) => {
      if (result === "Save") {
        this.homeHttpApi.newFolder(this.currentFolderId, this.newFolderName).subscribe(
          _ => {
            if (this.isTrashShowed) {
              this.loadData();
            } else {
              this.refreshCurrentFolder();
            }
          },
          error => console.log(error)
        );
      }
    }, null);
  }

  openUploadModal(upload) {
    this.modalService.open(upload, { ariaLabelledBy: 'upload' }).result.then((result) => {
      if (result === 'Upload') {
        this.upload();
      }
    }, null);
  }

  openShareContentModal(contentId, share) {
    console.log(contentId);

    let modal = this.modalService.open(share, { ariaLabelledBy: 'share' });

    modal.shown.subscribe(
      _ => {
        if (this.isTrashShowed) {
          this.loadData();
        } else {
          this.shareContentId = contentId;
        }
      },
      error => console.log(error)
    );

    modal.closed.subscribe(
      _ => {
        this.sharedMsg = "";
        this.sharedLink = "";
        this.isEmailShare = false;
        this.emailsTextBox = "";
      });
  }
}
