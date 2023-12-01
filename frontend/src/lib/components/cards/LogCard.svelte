<script lang="ts">
    export let modulo: string;

    const evtSource = new EventSource(`/api/modulo/${modulo}/logs`);

    let messages: object[] = [];

    evtSource.onmessage = (event) => {
        messages = [JSON.parse(event.data), ...messages];
    };
</script>

<div class="p-2 flex-1 flex flex-col border border-neutral-800 rounded-md">
    <h2 class="text-xl font-bold">{modulo}</h2>
    <div class="h-52 overflow-auto flex flex-col-reverse bg-neutral-800 rounded-sm">
        {#each messages as message}
            <code class="whitespace-nowrap">{JSON.stringify(message, null, 2)}</code>
        {/each}
    </div>
</div>
