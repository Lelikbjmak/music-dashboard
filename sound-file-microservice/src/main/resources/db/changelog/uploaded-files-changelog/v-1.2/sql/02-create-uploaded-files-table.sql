CREATE TABLE uploaded_audio_files (
  id BIGINT AUTO_INCREMENT NOT NULL,
   file_name VARCHAR(255) NOT NULL,
   storage VARCHAR(255) NOT NULL,
   `path` VARCHAR(255) NOT NULL,
   uploaded_date datetime NULL,
   updated_date datetime NULL,
   CONSTRAINT pk_uploaded_audio_files PRIMARY KEY (id),
   CONSTRAINT uc_uploaded_audio_files_filename UNIQUE (file_name)
)
GO