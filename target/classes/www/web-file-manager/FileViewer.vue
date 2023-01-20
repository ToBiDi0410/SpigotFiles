<template>
    <div class="FileViewerFrame absolute top-0 left-0 w-full h-full bg-base-300/70 p-12" v-if="bus.get('VIEWER.show')">
        <div class="FileViewerContent w-full h-full bg-base-100 shadow-xl flex flex-col">
            <div class="FileViewerHeader py-3 w-full bg-base-content text-base-100 flex flex-row px-6 items-center">
                <div class="flex-grow text-left font-bold" style="flex-basis: 0;">{{ file.NAME }}</div>
                <div class="flex-grow text-center text-xs" style="flex-basis: 0;">© Tobias Dickes</div>
                <div class="flex-grow text-right" style="flex-basis: 0;">
                    <Icon @click="handleClose">close</Icon>
                </div>
            </div>
            <div class="flex-grow relative flex flex-col items-center justify-center">
                <div class="Loading" v-if="this.bus.get('VIEWER.refreshing')">
                    <div class="text-center py-2 font-bold text-sm">{{ bus.get('VIEWER.refreshText') }}</div>
                    <progress class="progress progress-primary w-full" :value="this.bus.get('VIEWER.progress') || null" max="100"></progress>
                </div>

                <div class="Error text-center" v-if="this.bus.get('VIEWER.refreshError')">
                    <Icon class="text-8xl" style="font-size: 5rem;">sentiment_dissatisfied</Icon>
                    <div class="text-error">This file could not be opened:</div>
                    <div class="text-center py-2 font-bold text-sm">{{ bus.get('VIEWER.refreshError') }}</div>
                </div>

                <div class="Content Text h-full w-full" v-if="shouldDisplay() && this.bus.get('VIEWER.contentType') == 'TEXT'">
                    <AceEditor>{{ this.bus.get('VIEWER.content') }}</AceEditor>
                </div>

                <div class="Content Image" v-if="shouldDisplay() && this.bus.get('VIEWER.contentType') == 'IMAGE'">
                    <img :src="this.bus.get('VIEWER.content')">
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import { FileBackendError } from './FileBackend.js';
import Icon from './Icon.vue';
import * as ProgressFetcher from './ProgressFetcher.js';
import AceEditor from './AceEditor.vue';

export default {
    components: { Icon, AceEditor },
    props: ["bus"],
    data: () => ({ file: null, fileMeta: null }),
    created() {
        this.bus.onValue("VIEWER.file", async (v) => {
            this.file = v;
            this.fileMeta = await this.file.getMeta();
            this.refresh();
        })
    },
    methods: {
        async refresh() {
            this.bus.set("VIEWER.refreshing", true);
            this.bus.remove("VIEWER.refreshError");
            const type = this.determineType();

            if(type == "UNKNOWN") {
                this.bus.set("VIEWER.refreshing", false);
                this.bus.set("VIEWER.refreshError", "Unsupported Filetype");
                return;
            }

            if(type.startsWith("TEXT")) {
                this.bus.set("VIEWER.contentType", "TEXT");
                this.bus.set("VIEWER.contentAddition", type.split("|")[1]);
            }

            if(type.startsWith("IMAGE")) {
                this.bus.set("VIEWER.contentType", "IMAGE");
                this.bus.remove("VIEWER.contentAddition");
            }

            try {
                this.bus.set("VIEWER.progress", 0);
                this.bus.set("VIEWER.refreshText", "Requesting file...");

                const fetcher = new ProgressFetcher.ProgressReportFetcher(({loaded, total}) => {
                    this.bus.set("VIEWER.progress", (loaded/total)*100);
                    this.bus.set("VIEWER.refreshText", "Downloading file (" + Math.round((loaded/total)*100) + "%)");
                });

                const url = await this.file.getDownloadURL();
                if(url instanceof FileBackendError) {
                    throw url.displayMessage;
                }

                const response = await fetcher.fetch(url, { cache: 'no-cache' });
                this.bus.set("VIEWER.progress", 0);
                this.bus.set("VIEWER.refreshText", "Opening file...");

                if(this.bus.get("VIEWER.contentType") == "TEXT") {
                    const text = await response.text();
                    this.bus.set("VIEWER.content", text);
                }

                if(this.bus.get("VIEWER.contentType") == "IMAGE") {
                    const blob = await response.blob();
                    this.bus.set("VIEWER.content", window.URL.createObjectURL(blob));
                }

                this.bus.remove("VIEWER.refreshError");

            } catch (err) {
                this.bus.set("VIEWER.refreshError", err);  
            }

            this.bus.set("VIEWER.refreshing", false);
        },

        handleClose() {
            this.bus.set("VIEWER.show", false);
        },

        determineType() {
            if(this.fileMeta.MIME.startsWith("application/json")) return "TEXT|JSON";
            if(this.fileMeta.MIME.startsWith("application/yml")) return "TEXT|YML";
            if(this.fileMeta.MIME.startsWith("application/yaml")) return "TEXT|YML";
            if(this.fileMeta.MIME.startsWith("application/html")) return "TEXT|HTML";
            if(this.fileMeta.MIME.startsWith("application/css")) return "TEXT|CSS";
            if(this.fileMeta.MIME.startsWith("text/")) return "TEXT";
            if(this.fileMeta.MIME.startsWith("image/")) return "IMAGE";
            return "UNKNOWN";
        },

        shouldDisplay() {
            return !this.bus.get('VIEWER.refreshing') && !this.bus.get('VIEWER.refreshError');
        }
    }
}
</script>