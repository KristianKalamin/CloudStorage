export interface FolderContent {
  id?: number;
  size?: number;
  name?: string;
  timeOfCreation?: Date;
  timeOfDeletion?: Date;
  version?: number; // if version is -1 then file is folder
}
