<template>
    <div class="FileManager relative bg-base-100 text-base-content" v-if="initDone">
        <div class="absolute bottom-0 z-50 pointer-events-none" v-if="debug">
            <div class="w-full" v-for="Key in Object.keys(bus.busCache)" v-bind:key="Key">{{ Key }} --> {{ bus.getText(Key) }}</div>
        </div>
        <div class="MainWindow w-full h-full flex flex-row overflow-auto" @contextmenu.prevent="handleContextMenu" @click.self="handleClick" @click.left="handleLeftClick">
            <table class="FileList relative flex-grow" border="1" style="height: fit-content;">
                <thead>
                    <tr class="text-left py-10" style="border-bottom: 1px solid hsl(var(--b3));">
                        <th class="px-2 pb-8" style="width: 1%;"></th>
                        <th class="px-2" style="width: 1%;"></th>
                        <th class="px-2" >
                            <div>Name</div>
                        </th>
                        <th class="px-2" style="width: 10%;">
                            <div>Size</div>
                        </th>
                        <th class="px-2" style="width: 20%;">
                            <div>Changed</div>
                        </th>
                    </tr>
                </thead>
                
                <div v-if="bus.get('DISPLAYED.error')" class="w-full text-center absolute">
                    <div class="text-base-content font-bold">There was an error accesing the requested directory:</div>
                    <div class="text-error">Code/ID: {{ bus.get('DISPLAYED.error') && bus.get('DISPLAYED.error').id ? bus.get('DISPLAYED.error').id : '?' }}</div>
                    <div class="text-error text-sm">{{ bus.get('DISPLAYED.error') ? bus.get('DISPLAYED.error').displayMessage || bus.get('DISPLAYED.error') : '?' }}</div>
                </div>

                <tbody ref="fileListEntries" v-if="!bus.get('DISPLAYED.error')">
                    <FileRenderer v-for="File of bus.get('DISPLAYED.content')" :bus="bus" :key="File.PATH" :File="File"></FileRenderer>
                    <tr class="hover:bg-base-300/70" @click="handleBack" v-if="!isAtRoot()">
                        <td class="px-2 py-4"></td>
                        <td class="px-2"><Icon>west</Icon></td>
                        <td class="px-2">Back</td>
                        <td class="px-2"></td>
                        <td class="px-2"></td>
                    </tr>
                </tbody>
            </table>

            <SidebarRenderer :bus="bus" v-if="!bus.get('DISPLAYED.error')"></SidebarRenderer>
            <FileManagerContextMenu :bus="bus" v-if="!bus.get('DISPLAYED.error')"></FileManagerContextMenu>
        </div>     

        <FileViewer :bus="bus"></FileViewer>  
    </div>
</template>

<script>
import * as Modals from './Modals.js';
import ContextMenu from './ContextMenu.vue';
import FileRenderer from './FileRenderer.vue';
import SidebarRenderer from './SidebarRenderer.vue';
import FileManagerContextMenu from './FileManagerContextMenu.vue';
import FileViewer from './FileViewer.vue';
import Icon from './Icon.vue';
import { FileBackendError } from './FileBackend.js';
import GlobalEventBus from './GlobalEventsBus.vue';

import 'https://cdn.jsdelivr.net/npm/ace-builds@1.14.0/src-noconflict/ace.min.js';

export default {
    components: { FileRenderer, SidebarRenderer, ContextMenu, Icon, FileManagerContextMenu, FileViewer },
    props: ["FileBackend", "debug"],
    data: () => ({ bus: null, initDone: false }),
    async created() {
        this.bus = GlobalEventBus;

        this.bus.set("DISPLAYED.rootPath", await this.FileBackend.getRootPath());
        this.bus.set("DISPLAYED.file", await this.FileBackend.getFile(this.bus.get("DISPLAYED.rootPath")));
        this.bus.set("DISPLAYED.content", []);
        this.bus.set("DISPLAYED.error", null);

        this.bus.set("SELECTION.files", []);
        this.bus.set("ACTION.files", []);

        this.bus.onValue("DISPLAYED.file", () => {
            this.refreshEntries();
        });

        this.bus.on("TRANSACTIONS.doCopy", async (payload) => {
            await Modals.copyFilesWithModal(this.FileBackend, payload.files, payload.target);
            this.refreshEntries();
        });

        this.bus.on("TRANSACTIONS.doCut", async (payload) => {
            await Modals.moveFilesWithModal(this.FileBackend, payload.files, payload.target);
            this.refreshEntries();
        });

        this.bus.on("TRANSACTIONS.doDelete", async (payload) => {
            await Modals.deleteFilesWithModal(this.FileBackend, payload.files);
            this.refreshEntries();
        });

        this.bus.on("TRANSACTIONS.doDownload", async (payload) => {
            await Modals.downloadFilesWithModal(this.FileBackend, payload.files);
        });

        this.bus.on("TRANSACTIONS.doUpload", async () => {
            const fileSelector = document.createElement('input');
            fileSelector.type = 'file';
            fileSelector.multiple = true;
            fileSelector.click();
            await new Promise(resolve => fileSelector.onchange = resolve);
            await Modals.uploadFilesWithModal(this.FileBackend, fileSelector.files, this.bus.get('DISPLAYED.file'));
        });

        this.bus.on("DISPLAYED.navigate", async (payload) => {
            if(payload.DIRECTORY) {
                console.log("[Navigate] Opening Path:", payload.PATH);
                this.bus.set("DISPLAYED.file", payload);
                this.bus.set("SELECTION.files", []);
            } else {
                console.log("Should open file: ", payload.PATH);
                this.bus.set("VIEWER.file", payload);
                this.bus.set("VIEWER.show", true);
            }
        });

        this.bus.on("DRAGDROP.dropped", async () => {
            const target = this.bus.get("DRAGDROP.target");

            let files = [this.bus.get("DRAGDROP.origin")];
            if(this.bus.get("DRAGDROP.isPartOfSelection")) {
                files = this.bus.get("SELECTION.files");
                this.bus.set("SELECTION.files", []);
            }
            if(files.includes(this.bus.get("DRAGDROP.target"))) return;
            this.bus.emit("TRANSACTIONS.doCut", {files, target});
        });

        this.refreshEntries();
        this.initDone = true;
    },
    methods: {
        async refreshEntries() {
            console.log("[List] Refreshing entries...");
            this.bus.set("DISPLAYED.content", []);
            const entries = await this.FileBackend.getFilesInsideDirectory(this.bus.get("DISPLAYED.file").PATH);
            if(entries instanceof FileBackendError) {
                console.warn("[List] Error while updating list:", entries);
                this.bus.set("DISPLAYED.error", entries);
            } else {
                this.bus.set("DISPLAYED.content", entries);
                this.bus.remove("DISPLAYED.error");
            }
        },

        handleClick() {
            this.bus.set("CONTEXTMENU.show", false);
            this.bus.set("SELECTION.files", []);
        },

        handleLeftClick() {
            this.bus.set("CONTEXTMENU.show", false);
        },

        async handleBack() {
            let current = this.bus.get('DISPLAYED.file').PATH;
            if(!this.isAtRoot()) {
                const parts = current.split("\\");
                console.log(current, parts);
                parts.pop();
                const updated = parts.join("\\");
                this.bus.emit("DISPLAYED.navigate", await this.FileBackend.getFile(updated));
            }
        },

        isAtRoot() {
            return (this.bus.get('DISPLAYED.file') && this.bus.get('DISPLAYED.file').PATH == this.bus.get('DISPLAYED.rootPath'));
        },

        handleContextMenu(e) {
            this.bus.set("CONTEXTMENU.position", Modals.getPosition(e));
            this.bus.set("CONTEXTMENU.show", true);
        },

        handleFileDropped(file) {
            if(this.currentDragged != null && file != null && file.DIRECTORY) {
                console.log("[Drop] Perform move:", file, this.currentDragged);
                this.performMove(this.currentDragged, file);
            }
        },

        async performMove(file, target) {
            await Modals.moveFileWithModal(this.FileBackend, file, target);
            await this.refreshEntries();
        },
    }
}
</script>

<style>
@import url("https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200");
@import url("https://cdn.jsdelivr.net/npm/tailwindcss@2.2/dist/tailwind.min.css");
@import url("https://cdn.jsdelivr.net/npm/daisyui@2.46.1/dist/full.css");
</style>