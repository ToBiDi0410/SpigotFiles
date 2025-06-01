<template>
    <div class="Login h-full w-full flex flex-col justify-center align-middle items-center content-center">
        <div class="bg-gray-200 px-4 py-8 rounded-xl shadow-lg w-screen h-screen lg:max-w-sm lg:max-h-96 flex flex-col justify-center">
            <div class="text-xl font-bold text-center mb-4">Login</div>
            <div class="mb-6">
                <label for="default-input" class="block mb-2 text-sm font-medium text-gray-900 dark:text-white">Username</label>
                <input type="text" ref="username" class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500">
            </div>

            <div class="mb-6">
                <label for="default-input" class="block mb-2 text-sm font-medium text-gray-900 dark:text-white">Password</label>
                <input type="password" ref="password" class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500">
            </div>

            <div class="button rounded-lg px-4 py-2" style="width: fit-content;" @click="!loading ? handleLogin() : null" ref="button" :class="{ 'bg-green-300' : loading, 'bg-green-600 hover:bg-green-500' : !loading }">
                <a>{{ loading ? '...' : 'Login' }}</a>
            </div>

            <div class="error text-red-500 text-center pt-2 font-bold" v-if="error">
                {{ error }}
            </div>
        </div>
    </div>
</template>

<script>
export default {
    emits: ["loginCompleted"],
    data: () => ({ error: null, loading: false }),
    methods: {
        async handleLogin() {
            this.error = null;
            this.loading = true;
            const username = this.$refs.username.value;
            const password = this.$refs.password.value;

            const req = await fetch(window.apiURL + "login?username=" + encodeURIComponent(username) + "&password=" + encodeURIComponent(password), { credentials: 'same-origin' });
            const res = await req.text();
            this.loading = false;
            if(req.status != 200) {
                console.warn("Error during login", res);
                this.error = res.replace("INVALID_PASSWORD", "Invalid password!").replace("UNKNOWN_USER", "Invalid username!");
                return;
            }

            this.$emit("loginCompleted");

        }
    }
}
</script>