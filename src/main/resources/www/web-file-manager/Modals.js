import Swal from "https://cdn.jsdelivr.net/npm/sweetalert2@11";

export async function moveFilesWithModal(backend, files, target) {
    await new Promise(resolve => {
        Swal.fire({
            title: 'Confirm move?',
            html: "<div class='text-base'>Do you really want to move <b>" + files.length + " files</b> into <b>'" + target.PATH + "'</b>?</div>",
            icon: 'question',
            showConfirmButton: true,
            showDenyButton: true,
            confirmButtonText: 'Yes',
            denyButtonText: 'No, abort',
            denyButtonColor: 'hsl(var(--su))',
            confirmButtonColor: 'hsl(var(--er))',
            showCloseButton: true,
            toast: true,
            preConfirm: (value) => {
                resolve(this);
                return false;
            }
        });
    });

    Swal.update({
        title: 'Please wait',
        icon: 'warning',
        html: "<div class='text-base'>Moving the files into <b>'" + target.PATH + "'</b>...</div>",
        showConfirmButton: false,
        showDenyButton: false,
    });

    let res = await backend.moveFiles(files, target);

    if (res == true) {
        Swal.update({
            title: 'Success',
            icon: 'success',
            html: "<div class='text-base'>The files have been moved successfully</div>",
        });
        setTimeout(() => Swal.close(), 2000);
    } else {
        Swal.update({
            title: 'Action failed',
            icon: 'error',
            html: "<div class='text-base'>There was an error while performing this operation!<br><br>ERROR: " + res.displayMessage || res + "</div>",
        });
    }
}

export async function deleteFilesWithModal(backend, files) {
    await new Promise(resolve => {
        Swal.fire({
            title: 'Confirm deleted?',
            html: "<div class='text-base'>Do you really want to delete <b>" + files.length + " files</b>?</div>",
            icon: 'warning',
            showConfirmButton: true,
            showDenyButton: true,
            confirmButtonText: 'YES',
            denyButtonText: 'No, abort',
            denyButtonColor: 'hsl(var(--su))',
            confirmButtonColor: 'hsl(var(--er))',
            showCloseButton: true,
            toast: true,
            preConfirm: (value) => {
                resolve(this);
                return false;
            }
        });
    });

    Swal.update({
        title: 'Please wait',
        icon: 'warning',
        html: "<div class='text-base'>Deleting the files...</div>",
        showConfirmButton: false,
        showDenyButton: false,
    });

    let res = await backend.deleteFiles(files);

    if (res == true) {
        Swal.update({
            title: 'Success',
            icon: 'success',
            html: "<div class='text-base'>The files have been deleted successfully</div>",
        });
        setTimeout(() => Swal.close(), 2000);
    } else {
        Swal.update({
            title: 'Action failed',
            icon: 'error',
            html: "<div class='text-base'>There was an error while performing this operation!<br><br>ERROR: " + res.displayMessage || res + "</div>",
        });
    }
}

export async function copyFilesWithModal(backend, files, target) {
    Swal.fire({
        title: 'Please wait',
        icon: 'warning',
        html: "<div class='text-base'>Copying the files...</div>",
        showConfirmButton: false,
        showDenyButton: false,
        showCloseButton: true,
        toast: true,
    });

    let res = await backend.copyFiles(files, target);

    if (res == true) {
        Swal.update({
            title: 'Success',
            icon: 'success',
            html: "<div class='text-base'>The files have been copied successfully</div>",
        });
        setTimeout(() => Swal.close(), 2000);
    } else {
        Swal.update({
            title: 'Action failed',
            icon: 'error',
            html: "<div class='text-base'>There was an error while performing this operation!<br><br>ERROR: " + res.displayMessage || res + "</div>",
        });
    }
}

export async function downloadFilesWithModal(backend, files) {
    Swal.fire({
        title: 'Please wait',
        icon: 'info',
        html: "<div class='text-base'>Starting download of the files...</div>",
        showConfirmButton: false,
        showDenyButton: false,
        showCloseButton: true,
        toast: true
    });

    let urls = [];

    let res;
    if (files.length > 1) res = await backend.getFilesDownload(files);
    if (res && res.startsWith) urls.push([res, "bundle.zip"]);
    else {
        for (const file of files) {
            res = await backend.getFileDownload(file);
            if (!(res && res.startsWith)) break;
            urls.push([res, file.NAME]);
        }
    }

    if (res && res.startsWith) {
        for (const url of urls) {
            startDownload(url[0], url[1]);
        }

        Swal.update({
            title: 'Success',
            icon: 'success',
            html: "<div class='text-base'>All downloads should have started</div>",
        });
        setTimeout(() => Swal.close(), 2000);
    } else {
        Swal.update({
            title: 'Action failed',
            icon: 'error',
            html: "<div class='text-base'>There was an error while performing this operation!<br><br>ERROR: " + res.displayMessage || res + "</div>",
        });
    }
}

export async function uploadFilesWithModal(backend, files, target) {
    Swal.fire({
        title: 'Please wait',
        icon: 'info',
        html: "<div class='text-base'>Starting upload...</div>",
        showConfirmButton: false,
        showDenyButton: false,
        showCloseButton: true,
        toast: true
    });

    let res;
    var i = 0;
    for (const file of files) {
        i++;
        Swal.update({
            title: 'Uploading ' + i + ' of ' + files.length,
            html: "<div>The upload is running...<br>Do not abort</div>",
        });
        res = await backend.uploadFile(file, target);
        if (res != true) break;
    }


    if (res == true) {
        Swal.update({
            title: 'Success',
            icon: 'success',
            html: "<div class='text-base'>All files have been uploaded successfully</div>",
        });
        setTimeout(() => Swal.close(), 2000);
    } else {
        Swal.update({
            title: 'Action failed',
            icon: 'error',
            html: "<div class='text-base'>There was an error while performing this operation!<br><br>ERROR: " + res.displayMessage || res + "</div>",
        });
    }
}

function startDownload(url, name) {
    const a = document.createElement('a');
    a.style.display = 'none';
    a.href = url;
    a.download = name;
    a.target = '_blank';
    document.body.appendChild(a);
    a.click();
}

export function getPosition(e) {

    var posx = 0;
    var posy = 0;

    if (!e) var e = window.event;

    if (e.pageX || e.pageY) {
        posx = e.pageX;
        posy = e.pageY;
    } else if (e.clientX || e.clientY) {
        posx = e.clientX + document.body.scrollLeft +
            document.documentElement.scrollLeft;
        posy = e.clientY + document.body.scrollTop +
            document.documentElement.scrollTop;
    }

    posx = Math.min(posx, document.body.clientWidth - 250);
    posy = Math.min(posy, document.body.clientHeight - 350);

    return {
        x: posx,
        y: posy,
    }
}