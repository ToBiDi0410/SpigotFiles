<template>
    <div class="FileViewer h-screen w-screen absolute left-0 top-0 bg-white z-10 text-black flex flex-col overflow-hidden">
        <div class="h-12 bg-black w-full text-white z-20">
            <div class="flex flex-row px-6 items-center h-full w-full">
                <div>{{ file.PATH.replace(/^.*[\\\/]/, '') }}</div>
                <div class="flex-grow"></div>
                <div class="rounded-full bg-white w-5 h-5 text-black text-center align-middle" @click="this.$emit('close')">X</div>
            </div>
        </div>
        <div class="flex-grow bg-gray-200 text-black relative">
            <div v-if="!loaded && !error" class="z-40">
                <a>Loading...</a>
            </div>

            <div class="absolute top-0 left-0 bottom-0 right-0 m-auto text-center z-50" style="width: fit-content; height: fit-content;" v-if="error">
                <Icon class="text-8xl">report</Icon><br>
                <div class="text-xl font-bold">There was an error opening this file:</div>
                <div>{{ error }}</div>
            </div>


            <div class="w-full h-full text-base" ref="editor"></div>
        </div>
    </div>
</template>

<script>
import Icon from './Icon.vue';

export default {
    components: { Icon },
    data: () => ({ editor: null, loaded: false, error: null }),
    props: ["file"],
    emits: ["close"],
    async mounted() {        
        if(!window.user.permissions.includes('READ')) return this.error = "NO_PERMISSION";

        let mode = "ace/mode/plain_text";
        if(this.file.PATH.endsWith(".js")) mode = "ace/mode/javascript";
        else if(this.file.PATH.endsWith(".json")) mode = "ace/mode/json";
        else if(this.file.PATH.endsWith(".yml")) mode = "ace/mode/yaml";
        else if(this.file.PATH.endsWith(".yaml")) mode = "ace/mode/javascript";
        else if(this.file.mimeType.startsWith("text")) mode = mode;
        else return this.error = "UNSUPPORTED_FILE";

        const res = await fetch(window.apiURL + "download?path=" + encodeURIComponent(this.file.PATH));
        const text = await res.text();

        this.editor = ace.edit(this.$refs.editor);
        this.editor.setTheme("ace/theme/monokai");
        this.editor.session.setMode(mode);
        this.$refs.editor.innerHtml = "";
        this.editor.setValue(text, -1);
        this.editor.setReadOnly(true);
        this.loaded = true;
    },

    async unmounted() {
        if(this.editor) this.editor.destroy();
    }
}
</script>