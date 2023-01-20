<template>
    <div class="AceEditor w-full h-full text-base" ref="editor">
        <slot></slot>
    </div>
</template>

<script>
export default {
    data: () => ({ editor: null }),
    mounted() {
        ace.config.set('basePath', 'https://cdn.jsdelivr.net/npm/ace-builds@1.14.0/src-noconflict/');
        this.editor = ace.edit(this.$refs.editor);
        this.editor.setTheme("ace/theme/monokai");
        this.editor.setReadOnly(true);
    },

    unmounted() {
        if(this.editor) this.editor.destroy();
    }
}
</script>