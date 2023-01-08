<template>
    <div class="FileContextMenu fixed z-50 w-48 bg-gray-300 text-black rounded-sm p-2" :class="{'hidden' : !show}" :style="'top: ' + y + 'px; left: ' + x + 'px;'" ref="menu">
        <div class="option py-2 hover:bg-gray-200 text-red-600 font-bold" @click="handleDeleteClick" v-if="window.user.permissions.includes('WRITE')"><Icon>delete</Icon> Delete</div>
        <div class="option py-2 hover:bg-gray-200" @click="handleDownloadClick" v-if="window.user.permissions.includes('READ')"><Icon>download</Icon> Download</div>
        <div class="option py-2 hover:bg-gray-200" @click="handleUploadClick" v-if="window.user.permissions.includes('WRITE')"><Icon>upload</Icon> Upload</div>
    </div>
</template>

<script>
import Icon from './Icon.vue';

export default {
    components: { Icon },
    props: ["currentPath"],
    emits: ["updateList"],
    data: () => ({ x: 0, y: 0, show: false, fileTargeted: null }),
    mounted() {
        window.addEventListener('click', (event) => {
            if(!this.$refs.menu.contains(event.target) && this.show) {
                this.show = false;
                event.stopPropagation();
                event.preventDefault();
            }
        }, true);
    },
    methods: {
        handleClick(event, file) {
            this.show = true;
            this.x = event.clientX;
            this.y = event.clientY;
            this.fileTargeted = file;
        },

        handleDownloadClick() {
            var link = document.createElement("a");

            if(this.fileTargeted.mimeType == 'FOLDER') {
                link.href = window.apiURL + 'downloadDir?path=' + encodeURIComponent(this.fileTargeted.PATH);
            } else {
                link.href = window.apiURL + 'download?path=' + encodeURIComponent(this.fileTargeted.PATH);
            }
            link.download = this.fileTargeted.PATH.split("\\")[this.fileTargeted.PATH.split("\\").length-1];
            link.click();
            this.show = false;
        },

        async handleDeleteClick() {
            await fetch(window.apiURL + 'delete?path=' + encodeURIComponent(this.fileTargeted.PATH));
            this.show = false;
            this.$emit("updateList");
        },

        async handleUploadClick() {
            var fileSelector = document.createElement('input');
            fileSelector.setAttribute('type', 'file');
            fileSelector.click();

            await new Promise(resolve => fileSelector.onchange = resolve);
            var formData = new FormData();
            formData.append("file", fileSelector.files[0]);

            console.log(this.currentPath);
            const req = await fetch(window.apiURL + "upload?path=" + encodeURIComponent(this.currentPath), { method: 'POST', body: formData });
            this.$emit("updateList");
        }
    }
}
</script>