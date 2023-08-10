import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { finalize } from 'rxjs';
import { FileService } from 'src/app/services/file.service';

@Component({
  selector: 'app-upload-track',
  templateUrl: './upload-track.component.html',
  styleUrls: ['./upload-track.component.css']
})
export class UploadTrackComponent implements OnInit {

  uploadFileForm: FormGroup;

  uploadFileServerMessage: string = '';

  selectedFile: File;

  isLoading: boolean = false;

  uploadMessage: string = '';

  constructor(private formBuilder: FormBuilder, private fileService: FileService) {

  }

  ngOnInit(): void {
    this.uploadFileForm = this.formBuilder.group({
      file: new FormControl('', [Validators.required])
    });
  }

  onFileChange(event: any): void {
    this.selectedFile = event.target.files[0] as File;
  }

  submit() {
    if (this.uploadFileForm.valid) {
      this.isLoading = true;

      const formData: FormData = new FormData();
      formData.append('file', this.selectedFile);

      this.fileService.upload(formData)
        .pipe(finalize(() => this.isLoading = false))
        .subscribe(
          message => {
            this.uploadMessage = message;
            this.uploadFileForm.reset();
          }
        );
    }
  }
}