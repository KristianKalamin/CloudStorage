import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, UrlSegment } from '@angular/router';
import { FolderContent } from 'src/app/shared/model/folder-content-model';
import { SharedContentService } from 'src/app/shared/services/shared-content.service';

@Component({
  selector: 'app-shared-content',
  templateUrl: './shared-content.component.html',
  styleUrls: ['./shared-content.component.css']
})
export class SharedContentComponent implements OnInit {
  content: FolderContent;

  constructor(private sharedHttpApi: SharedContentService, private route: ActivatedRoute) {
    this.content = {};
  }

  ngOnInit(): void {
    const link = this.route.snapshot.paramMap.get("link");
    this.sharedHttpApi.getSharedContent(link!).subscribe(
      res => {
        this.content = res;
      },
      error => console.log(error)
    )
  }

  download() {
    this.sharedHttpApi.download(this.content).subscribe(
      data => {
        const fileName = data.headers.get('File-Name');

        const blob = new Blob([data.body], { type: 'application/octet-stream' });

        const element = document.createElement('a');
        element.href = URL.createObjectURL(blob);
        element.download = fileName;
        document.body.appendChild(element);
        element.click();

      },
      error => console.log(error)
    )
  }
}
