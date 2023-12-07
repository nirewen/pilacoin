<script lang="ts">
    import { queryClient } from '$lib';
    import { QueryClientProvider } from '@tanstack/svelte-query';
</script>

<svelte:head>
    <title>Pilacoin Dashboard</title>
</svelte:head>

<QueryClientProvider client={queryClient}>
    <main class="flex flex-col flex-1 w-full h-full max-h-screen gap-2 p-2 my-auto md:m-auto dark:text-white">
        <slot />
    </main>
</QueryClientProvider>
