import { FolderContent } from "./folder-content-model";
import { Folder } from "./folder.model";

export interface CombinedData {
  rootFolder?: Folder;
  combinedFiles?: FolderContent[];
}
