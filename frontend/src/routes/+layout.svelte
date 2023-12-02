<script lang="ts">
    import { browser } from '$app/environment';
    import { QueryClient, QueryClientProvider } from '@tanstack/svelte-query';

    const queryClient = new QueryClient({
        defaultOptions: {
            queries: {
                enabled: browser,
            },
        },
    });
</script>

<svelte:head>
    <title>Pilacoin Dashboard</title>
</svelte:head>

<QueryClientProvider client={queryClient}>
    <main class="grid flex-1 w-full h-full grid-rows-[30%_70%] gap-2 p-2 my-auto md:m-auto dark:text-white">
        <slot />
    </main>
</QueryClientProvider>
