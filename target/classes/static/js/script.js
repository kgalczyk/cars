let fetchManager = {
    fetchAsync: async function (bodyObject, url){
        const options = {
            method: "POST",
            headers: { "Content-Type": "application/json"},
            body: JSON.stringify(bodyObject)
        }
        let response = await fetch(url, options);
        if (!response.ok) return response.status
        else return await response.json() // response.json
    },
}