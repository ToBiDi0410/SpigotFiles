<template>
    <ContextMenu :position="bus.get('CONTEXTMENU.position')" v-if="bus.get('CONTEXTMENU.show') && bus.get('CONTEXTMENU.position')" @click="bus.set('CONTEXTMENU.show', false)">
        <div class="flex flex-col w-full">
            <div class="py-3 px-2 hover:bg-base-content/10" v-if="getFiles().length > 0" @click="handleCopy">
                <Icon>file_copy</Icon> Copy
            </div>

            <div class="py-3 px-2 hover:bg-base-content/10" v-if="getFiles().length > 0" @click="handleCut">
                <Icon>cut</Icon> Cut
            </div>

            <div class="py-3 px-2 hover:bg-base-content/10 text-error" v-if="getFiles().length > 0" @click="handleDelete">
                <Icon>delete</Icon> Delete
            </div>

            <div class="py-3 px-2 hover:bg-base-content/10" v-if="getFiles().length > 0" @click="handleDownload">
                <Icon>download</Icon> Download
            </div>

            <div class="py-3 px-2 hover:bg-base-content/10" @click="handleUpload">
                <Icon>upload</Icon> Upload
            </div>

            <div class="py-3 px-2 hover:bg-base-content/10" v-if="getActionFiles().length > 0 && !getActionFiles().includes(bus.get('DISPLAYED.file'))" @click="handlePaste">
                <Icon>content_paste</Icon> Paste
            </div>
        </div>
    </ContextMenu>
</template>

<script>
import ContextMenu from './ContextMenu.vue';
import Icon from './Icon.vue';

export default {
    components: { ContextMenu, Icon },
    props: ["bus"],
    methods: {
        getFiles() {
            return this.bus.get('SELECTION.files');
        },

        getActionFiles() {
            return this.bus.get('ACTION.files');
        },

        handleCopy() {
            this.bus.set("ACTION.type", "COPY");
            this.bus.set("ACTION.files", this.bus.get("SELECTION.files"));
            this.bus.set("SELECTION.files", []);
        },

        handleCut() {
            this.bus.set("ACTION.type", "CUT");
            this.bus.set("ACTION.files", this.bus.get("SELECTION.files"));
            this.bus.set("SELECTION.files", []);
        },

        handleDelete() {
            this.bus.emit("TRANSACTIONS.doDelete", { files: this.bus.get("SELECTION.files")});
            this.bus.set("SELECTION.files", []);
        },

        handlePaste() {
            if(this.bus.get("ACTION.type") == "COPY") {
                this.bus.emit("TRANSACTIONS.doCopy", { target: this.bus.get("DISPLAYED.file"), files: this.bus.get("ACTION.files")});
            }

            if(this.bus.get("ACTION.type") == "CUT") {
                this.bus.emit("TRANSACTIONS.doCut", { target: this.bus.get("DISPLAYED.file"), files: this.bus.get("ACTION.files")});
                this.bus.set("ACTION.files", []);
            }
        },

        handleDownload() {
            this.bus.emit("TRANSACTIONS.doDownload", { files: this.bus.get("SELECTION.files")});
            this.bus.set("SELECTION.files", []);
        },

        handleUpload() {
            this.bus.emit("TRANSACTIONS.doUpload");
        }
    }
}
</script>