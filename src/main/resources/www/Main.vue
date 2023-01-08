<template>
    <div class="Main h-full w-full bg-white text-black">
        <FileBrowser v-if="user"></FileBrowser>
        <Login v-if="!user" @loginCompleted="updateCurrentUser"></Login>
    </div>
</template>

<script>
import FileBrowser from './FileBrowser.vue';
import Login from './Login.vue';

export default {
    components: { FileBrowser, Login },
    data: () => ({ user: null }),
    async created() {
        await this.updateCurrentUser();
    },
    methods: {
        async updateCurrentUser() {
            const loginReq = await fetch(window.apiURL + "loginstate", { credentials: 'same-origin' });
            if(loginReq.status == 200) {
                const loginRes = await loginReq.text();
                this.user = { name: loginRes.split(";")[2], id: loginRes.split(";")[1], permissions: loginRes.split(";")[3].split(",") };
                window.user = this.user;
            }
        }
    }
}
</script>