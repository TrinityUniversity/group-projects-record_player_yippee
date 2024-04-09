const songRoute = document.getElementById("song-route").value
const getSongRoute = document.getElementById("get-song-route").value
const csrfToken = document.getElementById("csrf-token").value

const addSong = () => {
    const file = document.getElementById("file-input").files[0]
    const name = document.getElementById('song-name').value
    const fd = new FormData()
    fd.append('file',file)
    fd.append('name',name)
    try{
        fetch(songRoute, {
            method: "POST",
            headers: {'Content-Type': 'multipart/form-data', 'Csrf-Token': csrfToken},
            body: fd
        })
        .then(res => res.json())
        .then(data => console.log(data))
    } catch (error){
        console.log('Caught:',error)
    }
}

const getSong = () => {
    const current_song = document.getElementById('current_song')
    if(current_song){
        document.body.removeChild(current_song)
    }
    const song = document.getElementById('song-name').value
    fetch(`${getSongRoute}${song}`)
    .then(res => res.blob())
    .then(blob => {
        const audio = new Audio(URL.createObjectURL(blob))
        audio.id = 'current_song'
        document.body.appendChild(audio)
    })
}