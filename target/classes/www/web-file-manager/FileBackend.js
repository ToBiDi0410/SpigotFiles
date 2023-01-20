export class FileBackend {

    /*
        Returns a list of Files inside the provided directory
    */
    async getFilesInsideDirectory(path) {
        return FileBackendError.UNSUPPORTED;
    }

    /*
        Returns a File by path
    */
    async getFile(path) {
        return FileBackendError.UNSUPPORTED;
    }

    /*
        Returns a FileMeta responding to a File
    */
    async getFileMeta(file) {
        return FileBackendError.UNSUPPORTED;
    }


    /*
        Returns true if the file was moved
    */
    async moveFile(file, targetFile) {
        return FileBackendError.UNSUPPORTED;
    }

    /*
        Returns true if the file was moved
    */
    async moveFiles(files, targetFile) {
        let res;
        for (const file of files) {
            res = await this.moveFile(file, targetFile);
            if (res != true) {
                res = new FileBackendError().withOtherError(res, file.PATH);
                break;
            }
        }
        return res;
    }

    /*
        Returns true if the file was moved
    */
    async deleteFile(file) {
        return FileBackendError.UNSUPPORTED;
    }

    /*
        Returns true if the file was moved
    */
    async deleteFiles(files) {
        let res;
        for (const file of files) {
            res = await this.deleteFile(file);
            if (res != true) {
                res = new FileBackendError().withOtherError(res, file.PATH);
                break;
            }
        }
        return res;
    }


    /*
         Return true if the file was copied
    */
    async copyFile(file, target) {
        return FileBackendError.UNSUPPORTED;
    }

    /*
        Returns true if the file was moved
    */
    async copyFiles(files, target) {
        let res;
        for (const file of files) {
            res = await this.copyFile(file, target);
            if (res != true) {
                res = new FileBackendError().withOtherError(res, file.PATH);
                break;
            }
        }
        return res;
    }

    /*
        Generates an URL for download of single file
    */
    async getFileDownload(file) {
        return FileBackendError.UNSUPPORTED;
    }

    /*
        Generate an bundled URL for download of multiple files
    */
    async getFilesDownload(files) {
        return FileBackendError.UNSUPPORTED;
    }

    /*
        Uploads the File provided from fileselector into target
    */
    async uploadFile(file, target) {
        return FileBackendError.UNSUPPORTED;
    }

    async getRootPath() {
        return "/";
    }
}

export class FFile {
    NAME;
    PATH;
    DIRECTORY;

    filebackend;
    meta;

    async getMeta() {
        if (this.meta) return this.meta;
        if (!this.filebackend) throw new Error("Filebackend is null but 'getMeta' called");
        return (await this.filebackend.getFileMeta(this));
    }

    async getDownloadURL() {
        if (!this.filebackend) throw new Error("Filebackend is null but 'getDownloadURL' called");
        return (await this.filebackend.getFileDownload(this));
    }

    clone() {
        const n = new FFile();
        n.NAME = this.NAME;
        n.PATH = this.PATH;
        n.DIRECTORY = this.DIRECTORY;
        n.filebackend = this.filebackend;
        n.meta = meta;
        return n;
    }
}

export class FileMeta {
    SIZE;
    MIME;
    LAST_CHANGED;
    VISIBILITY;
    HISTORY;
}

export class FileHistoryEntry {
    TIME;
    TEXT;
    FIELDS;
    COLOR;
}

export class FileBackendError {

    id;
    displayMessage;
    isCritical;

    constructor(i, dm, crt) {
        this.id = i;
        this.displayMessage = dm;
        this.isCritical = crt;
    }

    static UNKNOWN = new FileBackendError("ERR_UNK", "There was an unknown error while performing this action");
    static UNSUPPORTED = new FileBackendError("ERR_UNSUPPORTED", "The current implementation does not support this feature");
    static DIR_NOT_FOUND = new FileBackendError("ERR_DIR_NOT_FOUND", "The directory does not exist or has been deleted");
    static FILE_NOT_FOUND = new FileBackendError("ERR_FILE_NOT_FOUND", "The file does not exist or has been deleted");
    static FILE_PERM = new FileBackendError("ERR_PERM", "This file cannot be accesed due to lacking permissions");
    static FILE_CHANGED = new FileBackendError("ERR_PERM", "The file has been changed during the action");
    static FILE_INDEX_CORRUPT = new FileBackendError("ERR_INDEX_CORRUPT", "The index of this file seems to be corrupt");

    clone() {
        const newError = new FileBackendError(this.id, this.displayMessage, this.isCritical);
        return newError;
    }

    withOtherError(err, childID) {
        const COPY = this.clone();
        COPY.id = "ERR_CHILD";
        if (err instanceof FileBackendError) {
            COPY.displayMessage = "There was an error with '" + childID + "': " + (err.id + " | " + err.displayMessage);
        } else {
            COPY.displayMessage = "There was an error with '" + childID + "': " + err;
        }
        console.log(COPY);
        return COPY;
    }

}