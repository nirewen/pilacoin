<script lang="ts">
    export let modulo: string;

    const evtSource = new EventSource(`/api/modulo/${modulo}/logs`);

    let messages: object[] = [];

    evtSource.onmessage = (event) => {
        messages = [JSON.parse(event.data), ...messages];
    };
</script>

<blockquote class="max-h-40 overflow-auto flex flex-col-reverse">
    {#each messages as message}
        <pre>
            {JSON.stringify(message, null, 2)}
        </pre>
    {/each}
</blockquote>
