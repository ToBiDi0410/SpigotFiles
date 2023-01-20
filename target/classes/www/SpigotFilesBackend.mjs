import { FFile, FileBackend, FileHistoryEntry, FileMeta } from "./web-file-manager/FileBackend.js";

export class SpigotFilesBackend extends FileBackend {

    async getFilesInsideDirectory(path) {
        if (path == "/") path = "";
        const res = await fetch(window.apiURL + "list?path=" + encodeURIComponent(path));
        const resObj = await res.json();

        resObj.children.sort((a, b) => {
            if (a.mimeType == 'FOLDER' && b.mimeType == 'FOLDER') return 0;
            if (a.mimeType == 'FOLDER') return 1;
            if (b.mimeType == 'FOLDER') return -1;
        }).reverse();

        return resObj.children.map(a => responseFileToWebFile(a, this));
    }

    async getFile(path) {
        if (path == "/") path = "";
        const res = await fetch(window.apiURL + "fileinfo?path=" + encodeURIComponent(path));
        const resObj = await res.json();
        return responseFileToWebFile(resObj, this);
    }

    async deleteFile(file) {
        const req = await fetch(window.apiURL + "delete?path=" + encodeURIComponent(file.PATH));
        return req.status == 200;
    }

    async moveFile(file, target) {
        const res = await fetch(window.apiURL + "move?source=" + encodeURIComponent(file.PATH) + "&target=" + encodeURIComponent(target.PATH));
        return res.status == 200;
    }

    async copyFile(file, target) {
        const res = await fetch(window.apiURL + "copy?source=" + encodeURIComponent(file.PATH) + "&target=" + encodeURIComponent(target.PATH));
        return res.status == 200;
    }

    async getFileDownload(file) {
        if (file.DIRECTORY) {
            return window.apiURL + 'downloadDir?path=' + encodeURIComponent(file.PATH);
        } else {
            return window.apiURL + 'download?path=' + encodeURIComponent(file.PATH);
        }
    }

    async uploadFile(file, target) {
        const formData = new FormData();
        formData.append("file", file);

        const req = await fetch(window.apiURL + "upload?path=" + encodeURIComponent(target.PATH), { method: 'POST', body: formData });
        return req.status == 200;
    }

    async getRootPath() {
        const info = await this.getFile("/");
        return info.PATH;
    }
}

function responseFileToWebFile(obj, backend) {
    let newFile = new FFile();
    newFile.DIRECTORY = obj.mimeType == 'FOLDER';
    newFile.PATH = obj.PATH;
    const normalizedPath = obj.PATH.replaceAll("\\", "/").replaceAll("//", "/");
    newFile.NAME = normalizedPath.split("/")[normalizedPath.split("/").length - 1];
    newFile.meta = getMetaOfReponseFile(obj);
    newFile.filebackend = backend;
    return newFile;
}

function getMetaOfReponseFile(obj) {
    let newMeta = new FileMeta();
    newMeta.SIZE = obj.size;
    newMeta.MIME = obj.mimeType;
    newMeta.LAST_CHANGED = obj.lastUpdated;
    newMeta.VISIBILITY = obj.exists;
    newMeta.HISTORY = getHistoryOfReponseFile(obj);
    return newMeta;
}

function getHistoryOfReponseFile(obj) {
    let list = [];
    for (const transactions of obj.transactions) {
        list.push(convertTransactionToHistoryEntry(transactions));
    }
    list.sort((a, b) => a.TIME - b.TIME).reverse();
    return list;
}

function convertTransactionToHistoryEntry(obj) {
    let newHistory = new FileHistoryEntry();

    if (obj.type == "CREATE") {
        newHistory.COLOR = "success";
        newHistory.FIELDS = {
            "Type": obj.additionalData
        };
        newHistory.TIME = obj.date;
        newHistory.TEXT = "The file was indexed";
    } else if (obj.type == "DELETE") {
        newHistory.COLOR = "error";
        newHistory.FIELDS = {}
        newHistory.TEXT = "The file has been deleted";
    } else if (obj.type == "RENAME") {
        newHistory.COLOR = "warning";
        newHistory.FIELDS = {
            "From": obj.additionalData.split(";;;")[0],
            "To": obj.additionalData.split(";;;")[1]
        };
        newHistory.TEXT = "The file was renamed to '" + obj.additionalData.split(";;;")[1] + "'";
    } else if (obj.type == "MOVE") {
        newHistory.COLOR = "warning";
        newHistory.FIELDS = {
            "From": obj.additionalData.split(";;;")[0],
            "To": obj.additionalData.split(";;;")[1]
        };
        newHistory.TEXT = "The file was moved";
    } else if (obj.type == "COPY_TO") {
        newHistory.TEXT = "The file has been copied to another location";
        newHistory.COLOR = "info";
        newHistory.FIELDS = { "Target": obj.additionalData };
    } else if (obj.type == "COPY_FROM") {
        newHistory.TEXT = "The file has been created as a copy";
        newHistory.COLOR = "info";
        newHistory.FIELDS = { "Source": obj.additionalData };
    } else if (obj.type == "ADD") {
        newHistory.TEXT = "The file has been uploaded";
        newHistory.COLOR = "primary";
        newHistory.FIELDS = {};
    } else {
        newHistory.TEXT = "unknown type: " + obj.type;
        newHistory.COLOR = "error";
        newHistory.FIELDS = { "Additional Data: ": obj.additionalData };
    }

    newHistory.TIME = obj.date;
    newHistory.FIELDS = { "User": obj.user, ...newHistory.FIELDS };
    return newHistory;
}