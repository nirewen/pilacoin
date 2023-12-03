<script lang="ts">
    import { queryClient } from '$lib';
    import { QueryClientProvider } from '@tanstack/svelte-query';
</script>

<svelte:head>
    <title>Pilacoin Dashboard</title>
</svelte:head>

<QueryClientProvider client={queryClient}>
    <main class="grid flex-1 w-full h-full grid-rows-[30%_70%] gap-2 p-2 my-auto md:m-auto dark:text-white">
        <slot />
    </main>
</QueryClientProvider>
