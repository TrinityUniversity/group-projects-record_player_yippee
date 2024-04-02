const songRoute = document.getElementById("song-route").value
const csrfToken = document.getElementById("csrf-token")

const addSong = () => {
    console.log(songRoute)
    const file = document.getElementById("file-input").files[0]
    const name = document.getElementById('song-name').value
    const fd = new FormData()
    fd.append('file',file)
    console.log(fd)
    try{
        fetch(songRoute, {
            method: "POST",
            // headers: {'Content-Type': 'multipart/form-data', 'Csrf-Token': csrfToken},
            body: fd
        })
        // .then(res => res.json())
        // .then(data => console.log(data))
    } catch (error){
        console.log('Caught:',error)
    }
    
}