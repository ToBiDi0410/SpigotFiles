<template>
    <div class="FileBrowser w-full h-full flex flex-col overflow-hidden">
        <div class="h-12 w-full bg-black text-white z-20">
            <a>HI!!!!</a>
        </div>

        <div class="w-full flex flex-row items-stretch flex-grow relative h-0">
            <div class="flex-grow h-full overflow-auto">
                <div class="FileEntryRenderer p-2 hover:bg-gray-100" @click="handleBack" v-if="!isAtRoot()">
                    <div class="flex flex-row items-center">
                        <div class="w-16"><Icon>arrow_left</Icon></div>
                        <div class="flex-grow">Back</div>
                        <div></div>
                        <div class="pl-8 w-1/12"></div>
                    </div>
                </div>
            
                <FileEntryRenderer v-for="file of filesToDisplay" v-bind:key="file.ID" :data="file" @click="handleClick(file)" @nameClicked="handleNameClick(file)" @contextmenu.prevent="handleContextMenuClick($event, file)"></FileEntryRenderer>
            </div>

            <FileContextMenu ref="contextMenu" @updateList="updateList"></FileContextMenu>
            <FileViewer :file="displayedFile" v-if="displayedFile" @close="displayedFile = null"></FileViewer>

            <Transition name="slide">
                <div class="rightBar w-80 bg-white-200 shadow-lg h-full overflow-auto" v-if="currentFile">
                    <FileSidebarRenderer :data="currentFile"></FileSidebarRenderer>
                </div>
            </Transition>
        </div>
    </div>
</template>

<script>
import FileEntryRenderer from './FileEntryRenderer.vue';
import FileSidebarRenderer from './FileSidebarRenderer.vue';
import FileContextMenu from './FileContextMenu.vue';
import FileViewer from './FileViewer.vue';
import Icon from './Icon.vue';

export default {
    components: { FileEntryRenderer, FileSidebarRenderer, Icon, FileContextMenu, FileViewer },
    data: () => ({ currentPath: "", filesToDisplay: [], ownFile: null, currentFile: null, displayedFile: null }),
    async created() {
        await this.updateForPath();
    },
    methods: {
        async updateForPath() {
            this.currentFile = null;
            this.filesToDisplay = [];
            const res = await fetch(window.apiURL + "list?path=" + encodeURIComponent(this.currentPath));
            const resObj = await res.json();

            this.filesToDisplay = resObj.children.sort((a, b) => {
                if(a.mimeType == 'FOLDER' && b.mimeType == 'FOLDER') return 0;
                if(a.mimeType == 'FOLDER') return 1;
                if(b.mimeType == 'FOLDER') return -1;
            }).reverse();
            this.ownFile = resObj.self;
        },

        handleClick(file) {
            if(this.currentFile == file) return this.currentFile = null;
            this.currentFile = file;
        },

        handleNameClick(file) {
            if(file.mimeType == 'FOLDER') {
                this.currentPath += "/" + file.PATH.replace(/^.*[\\\/]/, '');
                this.updateForPath();
            } else {
                this.displayedFile = file;
            }
        },

        handleBack() {
            if(!this.isAtRoot()) {
                const parts = this.currentPath.split("/");
                parts.pop();
                this.currentPath = parts.join("/");
                this.updateForPath();
                return;
            }
        },

        isAtRoot() {
            return !this.currentPath.includes("/");
        },

        handleContextMenuClick(event, file) {
            this.$refs.contextMenu.handleClick(event, file);
        },

        updateList() {
            this.updateForPath();
        }
    }
}
</script>

<style scoped>
.slide-enter-active {
  animation: bounce-in 0.3s;
}

.slide-leave-active {
  animation: bounce-in 0.3s reverse;
}

@keyframes bounce-in {
  0% {
    transform: translateX(20rem);
    width: 0;
  }
  100% {
    transform: translateX(0);
    width: 20rem;
  }
}
</style>